create or replace function public.can_view_community_member_directory(
  target_community_id uuid,
  target_user_id uuid
)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select target_user_id is not null
    and exists (
      select 1
      from public.communities community
      where community.id = target_community_id
        and community.parent_id is null
    )
    and (
      public.is_admin()
      or public.is_lead_community_moderator(target_community_id, target_user_id)
      or exists (
        select 1
        from public.community_members member
        where member.community_id = target_community_id
          and member.user_id = target_user_id
          and member.status = 'approved'
      )
      or public.has_community_assistant_permission(target_community_id, target_user_id, 'manage_groups')
      or public.has_community_assistant_permission(target_community_id, target_user_id, 'review_events')
      or public.has_community_assistant_permission(target_community_id, target_user_id, 'manage_participants')
    );
$$;

create or replace function public.get_community_member_directory(
  target_community_id uuid,
  search_query text default null,
  result_limit integer default 30,
  result_offset integer default 0
)
returns table (
  user_id uuid,
  display_name text,
  username text,
  avatar_url text,
  city text,
  is_verified boolean,
  member_role text,
  joined_at timestamptz
)
language plpgsql
stable
security definer
set search_path = public
as $$
begin
  if not public.can_view_community_member_directory(target_community_id, auth.uid()) then
    raise exception 'Community membership is required';
  end if;

  return query
  select
    profile.id,
    profile.display_name,
    profile.username,
    profile.avatar_url,
    case when coalesce(preference.show_city, true) then profile.city else null end,
    profile.is_verified,
    member.role,
    member.created_at
  from public.community_members member
  join public.profiles profile on profile.id = member.user_id
  left join public.account_preferences preference on preference.user_id = profile.id
  where member.community_id = target_community_id
    and member.status = 'approved'
    and profile.status = 'active'
    and not exists (
      select 1
      from public.blocks block
      where
        (block.blocker_id = auth.uid() and block.blocked_user_id = profile.id)
        or (block.blocker_id = profile.id and block.blocked_user_id = auth.uid())
    )
    and (
      nullif(trim(search_query), '') is null
      or profile.display_name ilike '%' || trim(search_query) || '%'
      or profile.username ilike '%' || trim(search_query) || '%'
      or (
        coalesce(preference.show_city, true)
        and profile.city ilike '%' || trim(search_query) || '%'
      )
    )
  order by
    case member.role when 'owner' then 0 when 'manager' then 1 else 2 end,
    profile.display_name,
    member.created_at
  limit least(greatest(coalesce(result_limit, 30), 1), 100)
  offset greatest(coalesce(result_offset, 0), 0);
end;
$$;

create or replace function public.get_community_member_directory_count(
  target_community_id uuid,
  search_query text default null
)
returns bigint
language plpgsql
stable
security definer
set search_path = public
as $$
declare
  visible_member_count bigint;
begin
  if not public.can_view_community_member_directory(target_community_id, auth.uid()) then
    raise exception 'Community membership is required';
  end if;

  select count(*)
  into visible_member_count
  from public.community_members member
  join public.profiles profile on profile.id = member.user_id
  left join public.account_preferences preference on preference.user_id = profile.id
  where member.community_id = target_community_id
    and member.status = 'approved'
    and profile.status = 'active'
    and not exists (
      select 1
      from public.blocks block
      where
        (block.blocker_id = auth.uid() and block.blocked_user_id = profile.id)
        or (block.blocker_id = profile.id and block.blocked_user_id = auth.uid())
    )
    and (
      nullif(trim(search_query), '') is null
      or profile.display_name ilike '%' || trim(search_query) || '%'
      or profile.username ilike '%' || trim(search_query) || '%'
      or (
        coalesce(preference.show_city, true)
        and profile.city ilike '%' || trim(search_query) || '%'
      )
    );

  return visible_member_count;
end;
$$;

revoke all on function public.can_view_community_member_directory(uuid, uuid) from public;
revoke all on function public.get_community_member_directory(uuid, text, integer, integer) from public;
revoke all on function public.get_community_member_directory_count(uuid, text) from public;
grant execute on function public.get_community_member_directory(uuid, text, integer, integer) to authenticated;
grant execute on function public.get_community_member_directory_count(uuid, text) to authenticated;

notify pgrst, 'reload schema';
