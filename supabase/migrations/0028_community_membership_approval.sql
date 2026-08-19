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
  existing_status text;
begin
  if current_user_id is null then
    raise exception 'Authentication required';
  end if;

  select community.parent_id, community.visibility, community.name, community.lead_moderator_id
  into target_parent_id, target_visibility, target_name, target_lead_moderator_id
  from public.communities community
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

  if target_parent_id is null
    and target_lead_moderator_id is not null
    and target_lead_moderator_id <> current_user_id then
    insert into public.notifications (user_id, type, title, body, payload)
    values (
      target_lead_moderator_id,
      'community_membership_request',
      'Yeni topluluk katilim istegi',
      target_name || ' toplulugunda yeni bir uye basvurusu var.',
      jsonb_build_object('community_id', target_community_id, 'applicant_id', current_user_id)
    );
  end if;
end;
$$;

create or replace function public.get_pending_community_memberships(target_community_id uuid)
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
    raise exception 'Only the community moderator can view membership requests';
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
    and member.status = 'pending'
  order by member.created_at;
end;
$$;

create or replace function public.review_community_membership(
  target_membership_id uuid,
  target_status text
)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  target_community_id uuid;
  target_user_id uuid;
  target_community_name text;
begin
  if auth.uid() is null then
    raise exception 'Authentication required';
  end if;

  if target_status not in ('approved', 'rejected') then
    raise exception 'Invalid membership status';
  end if;

  select member.community_id, member.user_id, community.name
  into target_community_id, target_user_id, target_community_name
  from public.community_members member
  join public.communities community on community.id = member.community_id
  where member.id = target_membership_id
    and member.status = 'pending'
  for update of member;

  if target_community_id is null then
    raise exception 'Pending membership request not found';
  end if;

  if not public.is_admin()
    and not public.is_community_manager(target_community_id, auth.uid()) then
    raise exception 'Only the community moderator can review membership requests';
  end if;

  update public.community_members
  set status = target_status
  where id = target_membership_id;

  insert into public.notifications (user_id, type, title, body, payload)
  values (
    target_user_id,
    'community_membership_' || target_status,
    case
      when target_status = 'approved' then 'Topluluk katilimin onaylandi'
      else 'Topluluk katilim istegin reddedildi'
    end,
    target_community_name,
    jsonb_build_object('community_id', target_community_id)
  );
end;
$$;

drop policy if exists community_members_read_related_or_admin on public.community_members;
create policy community_members_read_self_or_manager
on public.community_members
for select
to authenticated
using (
  user_id = auth.uid()
  or public.is_admin()
  or public.is_community_manager(community_id, auth.uid())
);

revoke all on function public.join_community(uuid) from public;
revoke all on function public.get_pending_community_memberships(uuid) from public;
revoke all on function public.review_community_membership(uuid, text) from public;
grant execute on function public.join_community(uuid) to authenticated;
grant execute on function public.get_pending_community_memberships(uuid) to authenticated;
grant execute on function public.review_community_membership(uuid, text) to authenticated;

notify pgrst, 'reload schema';
