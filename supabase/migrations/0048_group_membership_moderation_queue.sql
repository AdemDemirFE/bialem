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
  target_name text;
  target_lead_moderator_id uuid;
  target_parent_type text;
  target_parent_lead_moderator_id uuid;
  existing_status text;
begin
  if current_user_id is null then
    raise exception 'Authentication required';
  end if;

  select
    community.parent_id,
    community.visibility,
    community.name,
    community.lead_moderator_id,
    parent.community_type,
    parent.lead_moderator_id
  into
    target_parent_id,
    target_visibility,
    target_name,
    target_lead_moderator_id,
    target_parent_type,
    target_parent_lead_moderator_id
  from public.communities community
  left join public.communities parent on parent.id = community.parent_id
  where community.id = target_community_id;

  if not found then
    raise exception 'Community not found';
  end if;

  if target_parent_id is null and target_visibility <> 'public' then
    raise exception 'This community does not accept membership requests';
  end if;

  if target_parent_id is not null
    and not public.is_approved_community_member(target_parent_id, current_user_id) then
    raise exception 'Join the main community first';
  end if;

  select member.status
  into existing_status
  from public.community_members member
  where member.community_id = target_community_id
    and member.user_id = current_user_id
  for update;

  if existing_status = 'blocked' then
    raise exception 'Membership requests are blocked for this community';
  end if;

  if existing_status = 'approved' then
    return;
  end if;

  insert into public.community_members (community_id, user_id, role, status)
  values (target_community_id, current_user_id, 'member', 'pending')
  on conflict (community_id, user_id)
  do update
  set
    role = 'member',
    status = 'pending',
    created_at = now();

  insert into public.notifications (user_id, type, title, body, payload)
  select
    recipient.user_id,
    'community_membership_request',
    case
      when target_parent_id is null then 'Yeni topluluk katılım isteği'
      else 'Yeni grup katılım isteği'
    end,
    target_name || case
      when target_parent_id is null then ' topluluğunda yeni bir üye başvurusu var.'
      else ' grubunda yeni bir üye başvurusu var.'
    end,
    jsonb_build_object(
      'community_id', target_community_id,
      'parent_community_id', target_parent_id,
      'applicant_id', current_user_id
    )
  from (
    select target_lead_moderator_id as user_id
    where target_parent_id is null
      and target_lead_moderator_id is not null

    union

    select member.user_id
    from public.community_members member
    where target_parent_id is not null
      and member.community_id = target_community_id
      and member.status = 'approved'
      and member.role in ('manager', 'owner')

    union

    select target_parent_lead_moderator_id
    where target_parent_id is not null
      and target_parent_type = 'partner_hub'
      and target_parent_lead_moderator_id is not null
  ) recipient
  where recipient.user_id <> current_user_id;
end;
$$;

create or replace function public.get_pending_managed_community_memberships(target_root_community_id uuid)
returns table (
  membership_id uuid,
  community_id uuid,
  community_name text,
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

  if not exists (
    select 1
    from public.communities root
    where root.id = target_root_community_id
      and root.parent_id is null
  ) then
    raise exception 'Main community not found';
  end if;

  return query
  select
    member.id,
    community.id,
    community.name,
    member.user_id,
    profile.display_name,
    profile.username,
    profile.avatar_url,
    member.created_at
  from public.community_members member
  join public.communities community on community.id = member.community_id
  join public.profiles profile on profile.id = member.user_id
  where member.status = 'pending'
    and (
      community.id = target_root_community_id
      or community.parent_id = target_root_community_id
    )
    and (
      public.is_admin()
      or public.is_community_manager(community.id, auth.uid())
    )
  order by member.created_at;
end;
$$;

revoke all on function public.join_community(uuid) from public;
revoke all on function public.get_pending_managed_community_memberships(uuid) from public;
grant execute on function public.join_community(uuid) to authenticated;
grant execute on function public.get_pending_managed_community_memberships(uuid) to authenticated;

notify pgrst, 'reload schema';
