alter table public.communities
add column if not exists community_type text;

alter table public.communities
add column if not exists category_id uuid references public.communities(id) on delete set null;

alter table public.communities
add column if not exists partner_trust_level text;

alter table public.communities
add column if not exists is_verified_partner boolean not null default false;

alter table public.communities
add column if not exists is_discoverable boolean not null default true;

update public.communities
set community_type = case when parent_id is null then 'category_hub' else 'group' end
where community_type is null;

update public.communities child
set category_id = parent.id
from public.communities parent
where child.parent_id = parent.id
  and child.category_id is null
  and parent.community_type = 'category_hub';

update public.communities
set partner_trust_level = case when community_type = 'partner_hub' then 'new' else 'trusted' end
where partner_trust_level is null;

alter table public.communities alter column community_type set default 'group';
alter table public.communities alter column community_type set not null;
alter table public.communities alter column partner_trust_level set default 'trusted';
alter table public.communities alter column partner_trust_level set not null;

alter table public.communities drop constraint if exists communities_community_type_check;
alter table public.communities add constraint communities_community_type_check
check (community_type in ('category_hub', 'partner_hub', 'group'));

alter table public.communities drop constraint if exists communities_partner_trust_level_check;
alter table public.communities add constraint communities_partner_trust_level_check
check (partner_trust_level in ('new', 'verified', 'trusted'));

create index if not exists idx_communities_type_parent
on public.communities(community_type, parent_id, created_at desc);

create index if not exists idx_communities_category
on public.communities(category_id)
where category_id is not null;

alter table public.events
add column if not exists category_id uuid references public.communities(id) on delete set null;

alter table public.events
add column if not exists published_to_discovery boolean not null default true;

alter table public.events
add column if not exists group_moderation_status text;

alter table public.events
add column if not exists platform_moderation_status text;

update public.events event
set category_id = coalesce(source.category_id, category_parent.id)
from public.communities source
left join public.communities category_parent
  on category_parent.id = source.parent_id
 and category_parent.community_type = 'category_hub'
where event.community_id = source.id
  and event.category_id is null;

update public.events
set
  group_moderation_status = case
    when status = 'rejected' then 'rejected'
    when status in ('published', 'completed', 'cancelled') then 'approved'
    else 'pending'
  end,
  platform_moderation_status = case
    when status = 'rejected' then 'rejected'
    when status in ('published', 'completed', 'cancelled') then 'approved'
    else 'not_required'
  end
where group_moderation_status is null or platform_moderation_status is null;

alter table public.events alter column group_moderation_status set default 'pending';
alter table public.events alter column group_moderation_status set not null;
alter table public.events alter column platform_moderation_status set default 'not_required';
alter table public.events alter column platform_moderation_status set not null;

alter table public.events drop constraint if exists events_group_moderation_status_check;
alter table public.events add constraint events_group_moderation_status_check
check (group_moderation_status in ('pending', 'approved', 'rejected'));

alter table public.events drop constraint if exists events_platform_moderation_status_check;
alter table public.events add constraint events_platform_moderation_status_check
check (platform_moderation_status in ('not_required', 'pending', 'approved', 'rejected'));

create index if not exists idx_events_discovery_category_starts
on public.events(category_id, starts_at)
where status = 'published' and published_to_discovery = true;

-- Exact group managers keep their authority. A partner hub manager also manages
-- that partner's child groups, but category moderators never inherit this power.
create or replace function public.is_community_manager(target_community_id uuid, target_user_id uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.community_members cm
    where cm.community_id = target_community_id
      and cm.user_id = target_user_id
      and cm.status = 'approved'
      and cm.role in ('manager', 'owner')
  ) or exists (
    select 1
    from public.communities child
    join public.communities partner
      on partner.id = child.parent_id
     and partner.community_type = 'partner_hub'
    join public.community_members cm on cm.community_id = partner.id
    where child.id = target_community_id
      and child.community_type = 'group'
      and cm.user_id = target_user_id
      and cm.status = 'approved'
      and cm.role in ('manager', 'owner')
  );
$$;

create or replace function public.set_event_federation_fields()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
  source_group public.communities%rowtype;
  source_parent public.communities%rowtype;
begin
  select * into source_group from public.communities where id = new.community_id;
  if source_group.id is null or source_group.community_type <> 'group' then
    raise exception 'Events can only be created inside a group';
  end if;

  select * into source_parent from public.communities where id = source_group.parent_id;
  new.category_id := coalesce(
    source_group.category_id,
    case when source_parent.community_type = 'category_hub' then source_parent.id else null end
  );

  if tg_op = 'INSERT' then
    new.group_moderation_status := 'pending';
    new.platform_moderation_status := 'not_required';
  end if;

  return new;
end;
$$;

drop trigger if exists trg_events_set_federation_fields on public.events;
create trigger trg_events_set_federation_fields
before insert or update of community_id on public.events
for each row execute function public.set_event_federation_fields();

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
begin
  if current_user_id is null then raise exception 'Authentication required'; end if;

  if not exists (
    select 1 from public.communities c
    where c.id = target_parent_id
      and c.parent_id is null
      and c.community_type = 'category_hub'
  ) then
    raise exception 'Category community not found';
  end if;

  if not public.is_admin() and not public.is_community_manager(target_parent_id, current_user_id) then
    raise exception 'Only an assigned category moderator can create groups';
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

  insert into public.community_members (community_id, user_id, role, status)
  values (created_group_id, current_user_id, 'manager', 'approved');

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
begin
  if current_user_id is null then raise exception 'Authentication required'; end if;

  if not exists (
    select 1 from public.communities c
    where c.id = target_partner_id and c.parent_id is null and c.community_type = 'partner_hub'
  ) then
    raise exception 'Partner community not found';
  end if;

  if not exists (
    select 1 from public.communities c
    where c.id = target_category_id and c.parent_id is null and c.community_type = 'category_hub'
  ) then
    raise exception 'Discovery category not found';
  end if;

  if not public.is_admin() and not public.is_community_manager(target_partner_id, current_user_id) then
    raise exception 'Only the partner manager can create groups';
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

  insert into public.community_members (community_id, user_id, role, status)
  values (created_group_id, current_user_id, 'manager', 'approved');

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

  select e.community_id, e.created_by, parent.community_type, parent.partner_trust_level
  into target_group_id, event_owner_id, parent_type, partner_trust
  from public.events e
  join public.communities child on child.id = e.community_id
  left join public.communities parent on parent.id = child.parent_id
  where e.id = target_event_id and e.status = 'pending_approval';

  if target_group_id is null then raise exception 'Pending event not found'; end if;
  if not public.is_admin() and not public.is_community_manager(target_group_id, current_user_id) then
    raise exception 'Only the source group moderator can review this event';
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
    rejection_reason = case when target_status = 'rejected' then coalesce(nullif(trim(target_rejection_reason), ''), 'Grup moderatoru tarafindan reddedildi.') else null end,
    published_at = case when final_status = 'published' then now() else null end
  where id = target_event_id;

  if final_status = 'pending_approval' then
    insert into public.notifications (user_id, type, title, body, payload)
    values (
      event_owner_id,
      'event_platform_review',
      'Etkinliğin son kontrolde',
      'Grup moderatörü etkinliğini onayladı. Yeni partner güven kontrolünden sonra yayınlanacak.',
      jsonb_build_object('event_id', target_event_id)
    );
  end if;
end;
$$;

revoke all on function public.create_partner_group(uuid, uuid, text, text, text, text) from public;
grant execute on function public.create_partner_group(uuid, uuid, text, text, text, text) to authenticated;

drop policy if exists events_read_group_visible on public.events;
create policy events_read_group_visible
on public.events
for select
using (
  created_by = auth.uid()
  or public.is_admin()
  or public.is_community_manager(community_id, auth.uid())
  or (
    status = 'published'
    and (
      published_to_discovery = true
      or public.is_approved_community_member(community_id, auth.uid())
    )
  )
);

notify pgrst, 'reload schema';
