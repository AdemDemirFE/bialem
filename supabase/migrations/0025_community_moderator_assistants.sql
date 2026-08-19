alter table public.communities
add column if not exists lead_moderator_id uuid references public.profiles(id) on delete set null;

update public.communities community
set lead_moderator_id = coalesce(
  (
    select member.user_id
    from public.community_members member
    where member.community_id = community.id
      and member.status = 'approved'
      and member.role in ('manager', 'owner')
    order by case member.role when 'manager' then 0 else 1 end, member.created_at
    limit 1
  ),
  community.created_by
)
where community.parent_id is null
  and community.lead_moderator_id is null;

insert into public.community_members (community_id, user_id, role, status)
select community.id, community.lead_moderator_id, 'manager', 'approved'
from public.communities community
where community.parent_id is null
  and community.lead_moderator_id is not null
on conflict (community_id, user_id) do update
set role = 'manager', status = 'approved';

alter table public.communities drop constraint if exists communities_root_lead_moderator_check;
alter table public.communities add constraint communities_root_lead_moderator_check
check (parent_id is not null or lead_moderator_id is not null);

create index if not exists idx_communities_lead_moderator
on public.communities(lead_moderator_id)
where lead_moderator_id is not null;

create table if not exists public.community_moderator_assistants (
  id uuid primary key default gen_random_uuid(),
  community_id uuid not null references public.communities(id) on delete cascade,
  user_id uuid not null references public.profiles(id) on delete cascade,
  assigned_by uuid not null references public.profiles(id) on delete restrict,
  can_manage_groups boolean not null default false,
  can_review_events boolean not null default false,
  can_manage_participants boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (community_id, user_id),
  check (can_manage_groups or can_review_events or can_manage_participants)
);

create index if not exists idx_community_moderator_assistants_user
on public.community_moderator_assistants(user_id, community_id);

alter table public.community_moderator_assistants enable row level security;

create or replace function public.is_lead_community_moderator(target_community_id uuid, target_user_id uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.communities community
    where community.id = target_community_id
      and community.parent_id is null
      and community.lead_moderator_id = target_user_id
  );
$$;

create or replace function public.has_community_assistant_permission(
  target_community_id uuid,
  target_user_id uuid,
  target_permission text
)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.communities target
    join public.community_moderator_assistants assistant
      on assistant.community_id = case
        when target.parent_id is null then target.id
        else target.parent_id
      end
    where target.id = target_community_id
      and assistant.user_id = target_user_id
      and case target_permission
        when 'manage_groups' then assistant.can_manage_groups
        when 'review_events' then assistant.can_review_events
        when 'manage_participants' then assistant.can_manage_participants
        else false
      end
  );
$$;

create or replace function public.is_community_manager(target_community_id uuid, target_user_id uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.communities target
    where target.id = target_community_id
      and (
        (target.parent_id is null and target.lead_moderator_id = target_user_id)
        or (
          target.parent_id is not null
          and exists (
            select 1
            from public.community_members member
            where member.community_id = target.id
              and member.user_id = target_user_id
              and member.status = 'approved'
              and member.role in ('manager', 'owner')
          )
        )
      )
  ) or exists (
    select 1
    from public.communities child
    join public.communities partner
      on partner.id = child.parent_id
     and partner.community_type = 'partner_hub'
    where child.id = target_community_id
      and child.community_type = 'group'
      and partner.lead_moderator_id = target_user_id
  );
$$;

drop policy if exists community_moderator_assistants_read_related on public.community_moderator_assistants;
create policy community_moderator_assistants_read_related
on public.community_moderator_assistants
for select
to authenticated
using (
  user_id = auth.uid()
  or public.is_admin()
  or public.is_lead_community_moderator(community_id, auth.uid())
);

drop trigger if exists trg_community_moderator_assistants_updated_at on public.community_moderator_assistants;
create trigger trg_community_moderator_assistants_updated_at
before update on public.community_moderator_assistants
for each row execute function public.set_updated_at();

create or replace function public.set_community_moderator_assistant(
  target_community_id uuid,
  target_user_email text,
  target_can_manage_groups boolean default false,
  target_can_review_events boolean default false,
  target_can_manage_participants boolean default false
)
returns uuid
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  target_user_id uuid;
  assignment_id uuid;
  assistant_count integer;
begin
  if current_user_id is null then raise exception 'Authentication required'; end if;
  if not (target_can_manage_groups or target_can_review_events or target_can_manage_participants) then
    raise exception 'Select at least one assistant permission';
  end if;

  perform 1
  from public.communities community
  where community.id = target_community_id and community.parent_id is null
  for update;
  if not found then raise exception 'Main community not found'; end if;

  if not public.is_admin()
    and not public.is_lead_community_moderator(target_community_id, current_user_id) then
    raise exception 'Only the lead moderator can manage assistants';
  end if;

  select profile.id into target_user_id
  from public.profiles profile
  where lower(profile.email) = lower(trim(target_user_email));
  if target_user_id is null then raise exception 'Registered user not found'; end if;

  if exists (
    select 1 from public.communities community
    where community.id = target_community_id and community.lead_moderator_id = target_user_id
  ) then
    raise exception 'Lead moderator cannot also be an assistant';
  end if;

  if not exists (
    select 1 from public.community_moderator_assistants assistant
    where assistant.community_id = target_community_id and assistant.user_id = target_user_id
  ) then
    select count(*) into assistant_count
    from public.community_moderator_assistants assistant
    where assistant.community_id = target_community_id;
    if assistant_count >= 2 then raise exception 'A community can have at most two assistants'; end if;
  end if;

  insert into public.community_members (community_id, user_id, role, status)
  values (target_community_id, target_user_id, 'member', 'approved')
  on conflict (community_id, user_id) do update
  set status = 'approved';

  insert into public.community_moderator_assistants (
    community_id,
    user_id,
    assigned_by,
    can_manage_groups,
    can_review_events,
    can_manage_participants
  ) values (
    target_community_id,
    target_user_id,
    current_user_id,
    target_can_manage_groups,
    target_can_review_events,
    target_can_manage_participants
  )
  on conflict (community_id, user_id) do update
  set
    assigned_by = excluded.assigned_by,
    can_manage_groups = excluded.can_manage_groups,
    can_review_events = excluded.can_review_events,
    can_manage_participants = excluded.can_manage_participants,
    updated_at = now()
  returning id into assignment_id;

  return assignment_id;
end;
$$;

create or replace function public.remove_community_moderator_assistant(target_assignment_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  target_community_id uuid;
begin
  select assistant.community_id into target_community_id
  from public.community_moderator_assistants assistant
  where assistant.id = target_assignment_id;
  if target_community_id is null then raise exception 'Assistant assignment not found'; end if;

  if not public.is_admin()
    and not public.is_lead_community_moderator(target_community_id, current_user_id) then
    raise exception 'Only the lead moderator can remove assistants';
  end if;

  delete from public.community_moderator_assistants where id = target_assignment_id;
end;
$$;

create or replace function public.get_community_moderator_assistants(target_community_id uuid)
returns table (
  assignment_id uuid,
  user_id uuid,
  display_name text,
  email text,
  avatar_url text,
  can_manage_groups boolean,
  can_review_events boolean,
  can_manage_participants boolean,
  created_at timestamptz
)
language plpgsql
stable
security definer
set search_path = public
as $$
begin
  if not public.is_admin()
    and not public.is_lead_community_moderator(target_community_id, auth.uid()) then
    raise exception 'Only the lead moderator can view assistants';
  end if;

  return query
  select
    assistant.id,
    assistant.user_id,
    profile.display_name,
    profile.email,
    profile.avatar_url,
    assistant.can_manage_groups,
    assistant.can_review_events,
    assistant.can_manage_participants,
    assistant.created_at
  from public.community_moderator_assistants assistant
  join public.profiles profile on profile.id = assistant.user_id
  where assistant.community_id = target_community_id
  order by assistant.created_at;
end;
$$;

create or replace function public.get_my_community_assistant_permissions(target_community_id uuid)
returns table (
  can_manage_groups boolean,
  can_review_events boolean,
  can_manage_participants boolean
)
language sql
stable
security definer
set search_path = public
as $$
  select
    public.has_community_assistant_permission(target_community_id, auth.uid(), 'manage_groups'),
    public.has_community_assistant_permission(target_community_id, auth.uid(), 'review_events'),
    public.has_community_assistant_permission(target_community_id, auth.uid(), 'manage_participants');
$$;

create or replace function public.set_community_lead_moderator(
  target_community_id uuid,
  target_user_email text
)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  target_user_id uuid;
begin
  perform 1
  from public.communities community
  where community.id = target_community_id
    and community.parent_id is null
  for update;
  if not found then raise exception 'Main community not found'; end if;

  select profile.id into target_user_id
  from public.profiles profile
  where lower(profile.email) = lower(trim(target_user_email));
  if target_user_id is null then raise exception 'Registered user not found'; end if;

  update public.community_members
  set role = 'member'
  where community_id = target_community_id
    and user_id <> target_user_id
    and role in ('manager', 'owner');

  insert into public.community_members (community_id, user_id, role, status)
  values (target_community_id, target_user_id, 'manager', 'approved')
  on conflict (community_id, user_id) do update
  set role = 'manager', status = 'approved';

  delete from public.community_moderator_assistants
  where community_id = target_community_id and user_id = target_user_id;

  update public.communities
  set lead_moderator_id = target_user_id
  where id = target_community_id;
end;
$$;

revoke all on function public.is_lead_community_moderator(uuid, uuid) from public;
revoke all on function public.has_community_assistant_permission(uuid, uuid, text) from public;
revoke all on function public.set_community_moderator_assistant(uuid, text, boolean, boolean, boolean) from public;
revoke all on function public.remove_community_moderator_assistant(uuid) from public;
revoke all on function public.get_community_moderator_assistants(uuid) from public;
revoke all on function public.get_my_community_assistant_permissions(uuid) from public;
revoke all on function public.set_community_lead_moderator(uuid, text) from public;
grant execute on function public.is_lead_community_moderator(uuid, uuid) to authenticated;
grant execute on function public.has_community_assistant_permission(uuid, uuid, text) to authenticated;
grant execute on function public.set_community_moderator_assistant(uuid, text, boolean, boolean, boolean) to authenticated;
grant execute on function public.remove_community_moderator_assistant(uuid) to authenticated;
grant execute on function public.get_community_moderator_assistants(uuid) to authenticated;
grant execute on function public.get_my_community_assistant_permissions(uuid) to authenticated;
grant execute on function public.set_community_lead_moderator(uuid, text) to service_role;

create or replace function public.create_community_group(
  target_parent_id uuid,
  target_name text,
  target_slug text,
  target_description text default null,
  target_cover_image_url text default null
)
returns uuid
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  created_group_id uuid;
  creator_role text;
begin
  if current_user_id is null then raise exception 'Authentication required'; end if;

  if not exists (
    select 1 from public.communities community
    where community.id = target_parent_id
      and community.parent_id is null
      and community.community_type = 'category_hub'
  ) then
    raise exception 'Category community not found';
  end if;

  if not public.is_admin()
    and not public.is_community_manager(target_parent_id, current_user_id)
    and not public.has_community_assistant_permission(target_parent_id, current_user_id, 'manage_groups') then
    raise exception 'Not authorized to create groups';
  end if;

  if char_length(trim(target_name)) < 3 or char_length(trim(target_slug)) < 3 then
    raise exception 'Group name and slug are required';
  end if;

  insert into public.communities (
    parent_id, category_id, community_type, name, slug, description,
    visibility, cover_image_url, created_by, partner_trust_level
  ) values (
    target_parent_id, target_parent_id, 'group', trim(target_name),
    lower(regexp_replace(trim(target_slug), '[^a-zA-Z0-9-]+', '-', 'g')),
    nullif(trim(target_description), ''), 'private',
    nullif(trim(target_cover_image_url), ''), current_user_id, 'trusted'
  ) returning id into created_group_id;

  creator_role := case
    when public.is_community_manager(target_parent_id, current_user_id) then 'manager'
    else 'member'
  end;

  insert into public.community_members (community_id, user_id, role, status)
  values (created_group_id, current_user_id, creator_role, 'approved');

  return created_group_id;
end;
$$;

create or replace function public.create_partner_group(
  target_partner_id uuid,
  target_category_id uuid,
  target_name text,
  target_slug text,
  target_description text default null,
  target_cover_image_url text default null
)
returns uuid
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  created_group_id uuid;
  creator_role text;
begin
  if current_user_id is null then raise exception 'Authentication required'; end if;

  if not exists (
    select 1 from public.communities community
    where community.id = target_partner_id
      and community.parent_id is null
      and community.community_type = 'partner_hub'
  ) then
    raise exception 'Partner community not found';
  end if;

  if not exists (
    select 1 from public.communities community
    where community.id = target_category_id
      and community.parent_id is null
      and community.community_type = 'category_hub'
  ) then
    raise exception 'Discovery category not found';
  end if;

  if not public.is_admin()
    and not public.is_community_manager(target_partner_id, current_user_id)
    and not public.has_community_assistant_permission(target_partner_id, current_user_id, 'manage_groups') then
    raise exception 'Not authorized to create partner groups';
  end if;

  if char_length(trim(target_name)) < 3 or char_length(trim(target_slug)) < 3 then
    raise exception 'Group name and slug are required';
  end if;

  insert into public.communities (
    parent_id, category_id, community_type, name, slug, description,
    visibility, cover_image_url, created_by, partner_trust_level
  ) values (
    target_partner_id, target_category_id, 'group', trim(target_name),
    lower(regexp_replace(trim(target_slug), '[^a-zA-Z0-9-]+', '-', 'g')),
    nullif(trim(target_description), ''), 'private',
    nullif(trim(target_cover_image_url), ''), current_user_id, 'trusted'
  ) returning id into created_group_id;

  creator_role := case
    when public.is_community_manager(target_partner_id, current_user_id) then 'manager'
    else 'member'
  end;

  insert into public.community_members (community_id, user_id, role, status)
  values (created_group_id, current_user_id, creator_role, 'approved');

  return created_group_id;
end;
$$;

create or replace function public.moderate_group_event(
  target_event_id uuid,
  target_status text,
  target_rejection_reason text default null
)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  target_group_id uuid;
  event_owner_id uuid;
  parent_type text;
  partner_trust text;
  final_status text;
begin
  if current_user_id is null then raise exception 'Authentication required'; end if;
  if target_status not in ('published', 'rejected') then raise exception 'Invalid moderation status'; end if;

  select event.community_id, event.created_by, parent.community_type, parent.partner_trust_level
  into target_group_id, event_owner_id, parent_type, partner_trust
  from public.events event
  join public.communities child on child.id = event.community_id
  left join public.communities parent on parent.id = child.parent_id
  where event.id = target_event_id and event.status = 'pending_approval';

  if target_group_id is null then raise exception 'Pending event not found'; end if;
  if not public.is_admin()
    and not public.is_community_manager(target_group_id, current_user_id)
    and not public.has_community_assistant_permission(target_group_id, current_user_id, 'review_events') then
    raise exception 'Not authorized to review this event';
  end if;

  final_status := case
    when target_status = 'rejected' then 'rejected'
    when parent_type = 'partner_hub' and partner_trust = 'new' and not public.is_admin() then 'pending_approval'
    else 'published'
  end;

  update public.events
  set
    status = final_status,
    group_moderation_status = case when target_status = 'rejected' then 'rejected' else 'approved' end,
    platform_moderation_status = case
      when target_status = 'rejected' then 'rejected'
      when final_status = 'pending_approval' then 'pending'
      else 'approved'
    end,
    rejection_reason = case
      when target_status = 'rejected' then coalesce(nullif(trim(target_rejection_reason), ''), 'Grup moderatoru tarafindan reddedildi.')
      else null
    end,
    published_at = case when final_status = 'published' then now() else null end
  where id = target_event_id;

  if final_status = 'pending_approval' then
    insert into public.notifications (user_id, type, title, body, payload)
    values (
      event_owner_id,
      'event_platform_review',
      'Etkinligin son kontrolde',
      'Grup onayi tamamlandi. Yeni partner guven kontrolunden sonra yayinlanacak.',
      jsonb_build_object('event_id', target_event_id)
    );
  end if;
end;
$$;

drop policy if exists communities_update_admin_or_group_manager on public.communities;
create policy communities_update_admin_or_group_manager
on public.communities
for update
using (
  public.is_admin()
  or (
    parent_id is not null
    and (
      public.is_community_manager(id, auth.uid())
      or public.has_community_assistant_permission(id, auth.uid(), 'manage_groups')
    )
  )
)
with check (
  public.is_admin()
  or (
    parent_id is not null
    and (
      public.is_community_manager(id, auth.uid())
      or public.has_community_assistant_permission(id, auth.uid(), 'manage_groups')
    )
  )
);

drop policy if exists events_read_group_visible on public.events;
create policy events_read_group_visible
on public.events
for select
using (
  created_by = auth.uid()
  or public.is_admin()
  or public.is_community_manager(community_id, auth.uid())
  or public.has_community_assistant_permission(community_id, auth.uid(), 'review_events')
  or public.has_community_assistant_permission(community_id, auth.uid(), 'manage_participants')
  or (
    status = 'published'
    and (
      published_to_discovery = true
      or public.is_approved_community_member(community_id, auth.uid())
    )
  )
);

create or replace function public.review_event_participant(target_participant_id uuid, target_status text)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  target_event_id uuid;
  target_user_id uuid;
  target_community_id uuid;
  event_title text;
begin
  if target_status not in ('approved', 'rejected') then raise exception 'Invalid participant status'; end if;

  select participant.event_id, participant.user_id, event.community_id, event.title
  into target_event_id, target_user_id, target_community_id, event_title
  from public.event_participants participant
  join public.events event on event.id = participant.event_id
  where participant.id = target_participant_id and participant.status = 'pending';

  if target_event_id is null then raise exception 'Pending participant not found'; end if;

  if not exists (select 1 from public.events event where event.id = target_event_id and event.created_by = current_user_id)
    and not public.is_admin()
    and not public.is_community_manager(target_community_id, current_user_id)
    and not public.has_community_assistant_permission(target_community_id, current_user_id, 'manage_participants') then
    raise exception 'Not authorized to review participants';
  end if;

  update public.event_participants
  set status = target_status, updated_at = now()
  where id = target_participant_id;

  insert into public.notifications (user_id, type, title, body, payload)
  values (
    target_user_id,
    'event_participation_' || target_status,
    case when target_status = 'approved' then 'Katilimin onaylandi' else 'Katilim talebin reddedildi' end,
    event_title,
    jsonb_build_object('event_id', target_event_id)
  );

  if target_status = 'rejected' then perform public.promote_event_waitlist(target_event_id); end if;
end;
$$;

create or replace function public.get_event_participation_summary(target_event_id uuid)
returns table (
  pending_count bigint,
  approved_count bigint,
  waitlisted_count bigint,
  checked_in_count bigint,
  my_status text,
  my_waitlist_position bigint,
  can_manage boolean
)
language sql
stable
security definer
set search_path = public
as $$
  select
    count(*) filter (where participant.status = 'pending'),
    count(*) filter (where participant.status = 'approved'),
    count(*) filter (where participant.status = 'waitlisted'),
    count(*) filter (where participant.status = 'checked_in'),
    max(participant.status) filter (where participant.user_id = auth.uid()),
    (
      select count(*)
      from public.event_participants queue
      where queue.event_id = target_event_id
        and queue.status = 'waitlisted'
        and queue.created_at <= coalesce((
          select mine.created_at
          from public.event_participants mine
          where mine.event_id = target_event_id
            and mine.user_id = auth.uid()
            and mine.status = 'waitlisted'
        ), '-infinity'::timestamptz)
    ),
    exists (
      select 1
      from public.events event
      where event.id = target_event_id
        and (
          event.created_by = auth.uid()
          or public.is_admin()
          or public.is_community_manager(event.community_id, auth.uid())
          or public.has_community_assistant_permission(event.community_id, auth.uid(), 'manage_participants')
        )
    )
  from public.event_participants participant
  where participant.event_id = target_event_id;
$$;

create or replace function public.get_event_participant_roster(target_event_id uuid)
returns table (
  participant_id uuid,
  user_id uuid,
  display_name text,
  avatar_url text,
  status text,
  created_at timestamptz
)
language plpgsql
stable
security definer
set search_path = public
as $$
begin
  if not exists (
    select 1
    from public.events event
    where event.id = target_event_id
      and (
        event.created_by = auth.uid()
        or public.is_admin()
        or public.is_community_manager(event.community_id, auth.uid())
        or public.has_community_assistant_permission(event.community_id, auth.uid(), 'manage_participants')
      )
  ) then
    raise exception 'Not authorized to view roster';
  end if;

  return query
  select participant.id, participant.user_id, profile.display_name, profile.avatar_url, participant.status, participant.created_at
  from public.event_participants participant
  join public.profiles profile on profile.id = participant.user_id
  where participant.event_id = target_event_id
  order by
    case participant.status when 'pending' then 0 when 'waitlisted' then 1 when 'approved' then 2 else 3 end,
    participant.created_at;
end;
$$;

create or replace function public.check_in_event_participant(target_event_id uuid, target_user_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  target_community_id uuid;
begin
  select community_id into target_community_id from public.events where id = target_event_id;
  if target_community_id is null then raise exception 'Event not found'; end if;

  if not exists (select 1 from public.events event where event.id = target_event_id and event.created_by = auth.uid())
    and not public.is_admin()
    and not public.is_community_manager(target_community_id, auth.uid())
    and not public.has_community_assistant_permission(target_community_id, auth.uid(), 'manage_participants') then
    raise exception 'Not authorized to check in participants';
  end if;

  update public.event_participants
  set status = 'checked_in', updated_at = now()
  where event_id = target_event_id
    and user_id = target_user_id
    and status in ('approved', 'checked_in');

  if not found then raise exception 'Approved participant not found'; end if;
end;
$$;

create or replace function public.mark_event_participant_no_show(target_event_id uuid, target_user_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  target_community_id uuid;
begin
  select community_id into target_community_id from public.events where id = target_event_id;
  if target_community_id is null then raise exception 'Event not found'; end if;

  if not exists (select 1 from public.events event where event.id = target_event_id and event.created_by = auth.uid())
    and not public.is_admin()
    and not public.is_community_manager(target_community_id, auth.uid())
    and not public.has_community_assistant_permission(target_community_id, auth.uid(), 'manage_participants') then
    raise exception 'Not authorized to update attendance';
  end if;

  update public.event_participants
  set status = 'no_show', updated_at = now()
  where event_id = target_event_id and user_id = target_user_id and status = 'approved';
end;
$$;

notify pgrst, 'reload schema';
