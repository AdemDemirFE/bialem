create or replace function public.set_profile_follow_state(
  target_user_id uuid,
  target_should_follow boolean
)
returns text
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  target_requires_approval boolean;
  inserted_request_id uuid;
  requester_name text;
begin
  if current_user_id is null then raise exception 'Authentication required'; end if;
  if target_user_id is null then raise exception 'Target profile is required'; end if;
  if target_user_id = current_user_id then raise exception 'Users cannot follow themselves'; end if;

  if not target_should_follow then
    delete from public.follows
    where follower_id = current_user_id
      and followed_id = set_profile_follow_state.target_user_id;

    delete from public.follow_requests
    where requester_id = current_user_id
      and follow_requests.target_user_id = set_profile_follow_state.target_user_id;

    return 'none';
  end if;

  select coalesce(preference.require_follow_approval, false)
  into target_requires_approval
  from public.profiles target
  left join public.account_preferences preference on preference.user_id = target.id
  where target.id = set_profile_follow_state.target_user_id
    and target.status = 'active'
    and coalesce(preference.allow_follows, true);

  if not found then raise exception 'Following is not available for this profile'; end if;

  if exists (
    select 1
    from public.blocks block
    where
      (block.blocker_id = current_user_id and block.blocked_user_id = set_profile_follow_state.target_user_id)
      or (block.blocker_id = set_profile_follow_state.target_user_id and block.blocked_user_id = current_user_id)
  ) then
    raise exception 'Following is not available for this profile';
  end if;

  if exists (
    select 1
    from public.follows follow_record
    where follow_record.follower_id = current_user_id
      and follow_record.followed_id = set_profile_follow_state.target_user_id
  ) then
    return 'following';
  end if;

  if target_requires_approval then
    insert into public.follow_requests (requester_id, target_user_id)
    values (current_user_id, set_profile_follow_state.target_user_id)
    on conflict on constraint follow_requests_requester_id_target_user_id_key do nothing
    returning id into inserted_request_id;

    if inserted_request_id is not null then
      select profile.display_name
      into requester_name
      from public.profiles profile
      where profile.id = current_user_id;

      insert into public.notifications (user_id, type, title, body, payload)
      values (
        set_profile_follow_state.target_user_id,
        'follow_request',
        'Yeni takip isteği',
        coalesce(requester_name, 'Bir kullanıcı') || ' seni takip etmek istiyor.',
        jsonb_build_object('user_id', current_user_id, 'follow_request_id', inserted_request_id)
      );
    end if;

    return 'requested';
  end if;

  delete from public.follow_requests
  where requester_id = current_user_id
    and follow_requests.target_user_id = set_profile_follow_state.target_user_id;

  insert into public.follows (follower_id, followed_id)
  values (current_user_id, set_profile_follow_state.target_user_id)
  on conflict (follower_id, followed_id) do nothing;

  return 'following';
end;
$$;

revoke all on function public.set_profile_follow_state(uuid, boolean) from public;
grant execute on function public.set_profile_follow_state(uuid, boolean) to authenticated;

notify pgrst, 'reload schema';
