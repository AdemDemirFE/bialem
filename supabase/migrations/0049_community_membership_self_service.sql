create or replace function public.cancel_community_membership_request(target_community_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
  if auth.uid() is null then
    raise exception 'Authentication required';
  end if;

  delete from public.community_members member
  where member.community_id = target_community_id
    and member.user_id = auth.uid()
    and member.role = 'member'
    and member.status = 'pending';

  if not found then
    raise exception 'Pending membership request not found';
  end if;
end;
$$;

create or replace function public.leave_community(target_community_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  target_parent_id uuid;
  current_role text;
  current_status text;
begin
  if auth.uid() is null then
    raise exception 'Authentication required';
  end if;

  select community.parent_id, member.role, member.status
  into target_parent_id, current_role, current_status
  from public.community_members member
  join public.communities community on community.id = member.community_id
  where member.community_id = target_community_id
    and member.user_id = auth.uid()
  for update of member;

  if current_status is distinct from 'approved' then
    raise exception 'Approved membership not found';
  end if;

  if current_role <> 'member' then
    raise exception 'Moderators must transfer their role before leaving';
  end if;

  if target_parent_id is null and exists (
    select 1
    from public.community_moderator_assistants assistant
    where assistant.community_id = target_community_id
      and assistant.user_id = auth.uid()
  ) then
    raise exception 'Moderator assistants must be removed before leaving';
  end if;

  if target_parent_id is null then
    delete from public.community_members member
    using public.communities community
    where member.community_id = community.id
      and member.user_id = auth.uid()
      and member.role = 'member'
      and (
        community.id = target_community_id
        or community.parent_id = target_community_id
      );
  else
    delete from public.community_members member
    where member.community_id = target_community_id
      and member.user_id = auth.uid()
      and member.role = 'member';
  end if;
end;
$$;

create or replace function public.get_managed_community_members(target_community_id uuid)
returns table (
  membership_id uuid,
  user_id uuid,
  display_name text,
  username text,
  avatar_url text,
  created_at timestamptz
)
language plpgsql
stable
security definer
set search_path = public
as $$
begin
  if auth.uid() is null then
    raise exception 'Authentication required';
  end if;

  if not public.is_admin()
    and not public.is_community_manager(target_community_id, auth.uid()) then
    raise exception 'Only the community moderator can view members';
  end if;

  return query
  select
    member.id,
    member.user_id,
    profile.display_name,
    profile.username,
    profile.avatar_url,
    member.created_at
  from public.community_members member
  join public.profiles profile on profile.id = member.user_id
  where member.community_id = target_community_id
    and member.status = 'approved'
    and member.role = 'member'
  order by profile.display_name, member.created_at;
end;
$$;

create or replace function public.remove_community_member(target_membership_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  target_community_id uuid;
  target_parent_id uuid;
  target_user_id uuid;
  target_role text;
  target_status text;
  target_community_name text;
begin
  if auth.uid() is null then
    raise exception 'Authentication required';
  end if;

  select
    member.community_id,
    community.parent_id,
    member.user_id,
    member.role,
    member.status,
    community.name
  into
    target_community_id,
    target_parent_id,
    target_user_id,
    target_role,
    target_status,
    target_community_name
  from public.community_members member
  join public.communities community on community.id = member.community_id
  where member.id = target_membership_id
  for update of member;

  if target_community_id is null or target_status <> 'approved' then
    raise exception 'Approved membership not found';
  end if;

  if target_role <> 'member' then
    raise exception 'Moderator memberships cannot be removed here';
  end if;

  if not public.is_admin()
    and not public.is_community_manager(target_community_id, auth.uid()) then
    raise exception 'Only the community moderator can remove members';
  end if;

  if target_parent_id is null then
    delete from public.community_members member
    using public.communities community
    where member.community_id = community.id
      and member.user_id = target_user_id
      and member.role = 'member'
      and (
        community.id = target_community_id
        or community.parent_id = target_community_id
      );
  else
    delete from public.community_members member
    where member.id = target_membership_id;
  end if;

  insert into public.notifications (user_id, type, title, body, payload)
  values (
    target_user_id,
    'community_membership_removed',
    'Üyeliğin sonlandırıldı',
    target_community_name || ' üyeliğin moderatör tarafından sonlandırıldı.',
    jsonb_build_object('community_id', target_community_id)
  );
end;
$$;

revoke all on function public.cancel_community_membership_request(uuid) from public;
revoke all on function public.leave_community(uuid) from public;
revoke all on function public.get_managed_community_members(uuid) from public;
revoke all on function public.remove_community_member(uuid) from public;
grant execute on function public.cancel_community_membership_request(uuid) to authenticated;
grant execute on function public.leave_community(uuid) to authenticated;
grant execute on function public.get_managed_community_members(uuid) to authenticated;
grant execute on function public.remove_community_member(uuid) to authenticated;

notify pgrst, 'reload schema';
