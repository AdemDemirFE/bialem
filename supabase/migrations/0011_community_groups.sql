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
  );
$$;

create or replace function public.join_community(target_community_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  target_parent_id uuid;
  target_visibility text;
begin
  if current_user_id is null then
    raise exception 'Authentication required';
  end if;

  select parent_id, visibility
  into target_parent_id, target_visibility
  from public.communities
  where id = target_community_id;

  if not found then
    raise exception 'Community not found';
  end if;

  if target_parent_id is null and target_visibility <> 'public' then
    raise exception 'This community does not accept direct membership';
  end if;

  if target_parent_id is not null
    and not public.is_approved_community_member(target_parent_id, current_user_id) then
    raise exception 'Join the main community first';
  end if;

  insert into public.community_members (community_id, user_id, role, status)
  values (target_community_id, current_user_id, 'member', 'approved')
  on conflict (community_id, user_id)
  do update set role = 'member', status = 'approved';
end;
$$;

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
  if current_user_id is null then
    raise exception 'Authentication required';
  end if;

  if not exists (
    select 1 from public.communities c
    where c.id = target_parent_id and c.parent_id is null
  ) then
    raise exception 'Main community not found';
  end if;

  if not public.is_admin()
    and not public.is_community_manager(target_parent_id, current_user_id) then
    raise exception 'Only an assigned moderator can create groups';
  end if;

  if char_length(trim(target_name)) < 3 or char_length(trim(target_slug)) < 3 then
    raise exception 'Group name and slug are required';
  end if;

  insert into public.communities (
    parent_id,
    name,
    slug,
    description,
    visibility,
    cover_image_url,
    created_by
  )
  values (
    target_parent_id,
    trim(target_name),
    lower(regexp_replace(trim(target_slug), '[^a-zA-Z0-9-]+', '-', 'g')),
    nullif(trim(target_description), ''),
    'private',
    nullif(trim(target_cover_image_url), ''),
    current_user_id
  )
  returning id into created_group_id;

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
begin
  if current_user_id is null then
    raise exception 'Authentication required';
  end if;

  if target_status not in ('published', 'rejected') then
    raise exception 'Invalid moderation status';
  end if;

  select community_id into target_group_id
  from public.events
  where id = target_event_id and status = 'pending_approval';

  if target_group_id is null then
    raise exception 'Pending event not found';
  end if;

  if not public.is_admin()
    and not public.is_community_manager(target_group_id, current_user_id) then
    raise exception 'Only the group moderator can review this event';
  end if;

  update public.events
  set
    status = target_status,
    rejection_reason = case when target_status = 'rejected' then nullif(trim(target_rejection_reason), '') else null end,
    published_at = case when target_status = 'published' then now() else null end
  where id = target_event_id;
end;
$$;

revoke all on function public.join_community(uuid) from public;
revoke all on function public.create_community_group(uuid, text, text, text, text) from public;
revoke all on function public.moderate_group_event(uuid, text, text) from public;
grant execute on function public.join_community(uuid) to authenticated;
grant execute on function public.create_community_group(uuid, text, text, text, text) to authenticated;
grant execute on function public.moderate_group_event(uuid, text, text) to authenticated;

drop policy if exists communities_public_read on public.communities;
create policy communities_hierarchical_read
on public.communities
for select
using (
  (parent_id is null and visibility = 'public')
  or created_by = auth.uid()
  or public.is_admin()
  or public.is_approved_community_member(id, auth.uid())
  or (parent_id is not null and public.is_approved_community_member(parent_id, auth.uid()))
);

drop policy if exists communities_create_authenticated on public.communities;
create policy communities_create_admin_only
on public.communities
for insert
with check (public.is_admin() and parent_id is null and created_by = auth.uid());

drop policy if exists communities_update_owner_or_admin on public.communities;
create policy communities_update_admin_or_group_manager
on public.communities
for update
using (public.is_admin() or (parent_id is not null and public.is_community_manager(id, auth.uid())))
with check (public.is_admin() or (parent_id is not null and public.is_community_manager(id, auth.uid())));

drop policy if exists community_members_join_self on public.community_members;
create policy community_members_insert_admin_only
on public.community_members
for insert
with check (public.is_admin());

drop policy if exists community_members_update_self_or_admin on public.community_members;
create policy community_members_update_manager_or_admin
on public.community_members
for update
using (public.is_admin() or public.is_community_manager(community_id, auth.uid()))
with check (public.is_admin() or public.is_community_manager(community_id, auth.uid()));

drop policy if exists events_read_visible on public.events;
create policy events_read_group_visible
on public.events
for select
using (
  created_by = auth.uid()
  or public.is_admin()
  or public.is_community_manager(community_id, auth.uid())
  or (status = 'published' and public.is_approved_community_member(community_id, auth.uid()))
);

drop policy if exists events_create_own on public.events;
create policy events_create_in_joined_group
on public.events
for insert
with check (
  created_by = auth.uid()
  and status = 'pending_approval'
  and public.is_approved_community_member(community_id, auth.uid())
  and exists (
    select 1 from public.communities c
    where c.id = community_id and c.parent_id is not null
  )
);

drop policy if exists events_update_owner_or_admin on public.events;
create policy events_update_owner_draft_or_admin
on public.events
for update
using (created_by = auth.uid() or public.is_admin())
with check (
  public.is_admin()
  or (created_by = auth.uid() and status in ('draft', 'pending_approval', 'cancelled'))
);

drop policy if exists event_participants_insert_self on public.event_participants;
create policy event_participants_join_published_group_event
on public.event_participants
for insert
with check (
  user_id = auth.uid()
  and status = 'pending'
  and exists (
    select 1 from public.events e
    where e.id = event_id
      and e.status = 'published'
      and public.is_approved_community_member(e.community_id, auth.uid())
  )
);

drop policy if exists event_participants_read_self_owner_admin on public.event_participants;
create policy event_participants_read_related
on public.event_participants
for select
using (
  user_id = auth.uid()
  or public.is_admin()
  or exists (
    select 1 from public.events e
    where e.id = event_id
      and (e.created_by = auth.uid() or public.is_community_manager(e.community_id, auth.uid()))
  )
);

drop policy if exists event_participants_update_self_owner_admin on public.event_participants;
create policy event_participants_update_related
on public.event_participants
for update
using (
  user_id = auth.uid()
  or public.is_admin()
  or exists (
    select 1 from public.events e
    where e.id = event_id
      and (e.created_by = auth.uid() or public.is_community_manager(e.community_id, auth.uid()))
  )
)
with check (
  (user_id = auth.uid() and status in ('pending', 'cancelled'))
  or public.is_admin()
  or exists (
    select 1 from public.events e
    where e.id = event_id
      and (e.created_by = auth.uid() or public.is_community_manager(e.community_id, auth.uid()))
  )
);
