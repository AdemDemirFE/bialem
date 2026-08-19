create or replace function public.set_profile_follow(
  target_user_id uuid,
  target_should_follow boolean
)
returns boolean
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
begin
  if current_user_id is null then
    raise exception 'Authentication required';
  end if;

  if target_user_id is null then
    raise exception 'Target profile is required';
  end if;

  if target_user_id = current_user_id then
    raise exception 'Users cannot follow themselves';
  end if;

  if not target_should_follow then
    delete from public.follows
    where follower_id = current_user_id
      and followed_id = target_user_id;

    return false;
  end if;

  if not exists (
    select 1
    from public.profiles target
    where target.id = target_user_id
      and target.status = 'active'
  ) then
    raise exception 'Active profile not found';
  end if;

  if exists (
    select 1
    from public.blocks block
    where
      (block.blocker_id = current_user_id and block.blocked_user_id = target_user_id)
      or (block.blocker_id = target_user_id and block.blocked_user_id = current_user_id)
  ) then
    raise exception 'Following is not available for this profile';
  end if;

  insert into public.follows (follower_id, followed_id)
  values (current_user_id, target_user_id)
  on conflict (follower_id, followed_id) do nothing;

  return true;
end;
$$;

create or replace function public.create_follow_notification()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
  follower_name text;
begin
  select display_name
  into follower_name
  from public.profiles
  where id = new.follower_id;

  insert into public.notifications (user_id, type, title, body, payload)
  values (
    new.followed_id,
    'new_follower',
    'Yeni bir takipçin var',
    coalesce(follower_name, 'Bir kullanıcı') || ' seni takip etmeye başladı.',
    jsonb_build_object('user_id', new.follower_id)
  );

  return new;
end;
$$;

revoke all on function public.set_profile_follow(uuid, boolean) from public;
grant execute on function public.set_profile_follow(uuid, boolean) to authenticated;

revoke all on function public.create_follow_notification() from public;

notify pgrst, 'reload schema';
