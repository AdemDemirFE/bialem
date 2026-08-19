alter table public.account_preferences
add column if not exists require_follow_approval boolean not null default false;

create table if not exists public.follow_requests (
  id uuid primary key default gen_random_uuid(),
  requester_id uuid not null references public.profiles(id) on delete cascade,
  target_user_id uuid not null references public.profiles(id) on delete cascade,
  created_at timestamptz not null default now(),
  unique (requester_id, target_user_id),
  check (requester_id <> target_user_id)
);

create index if not exists idx_follow_requests_target_created
on public.follow_requests(target_user_id, created_at desc);

alter table public.follow_requests enable row level security;

revoke all on table public.follow_requests from anon;
grant select on table public.follow_requests to authenticated;

drop policy if exists follow_requests_read_related on public.follow_requests;
create policy follow_requests_read_related
on public.follow_requests for select to authenticated
using (requester_id = auth.uid() or target_user_id = auth.uid());

-- Follow creation and removal must pass through the permission-aware RPCs below.
drop policy if exists follows_create_own on public.follows;
drop policy if exists follows_delete_own on public.follows;
revoke insert, delete on table public.follows from authenticated;
revoke insert, update, delete on table public.follow_requests from authenticated;

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
    where follower_id = current_user_id and followed_id = target_user_id;

    delete from public.follow_requests
    where requester_id = current_user_id and follow_requests.target_user_id = set_profile_follow_state.target_user_id;
    return 'none';
  end if;

  select coalesce(preference.require_follow_approval, false)
  into target_requires_approval
  from public.profiles target
  left join public.account_preferences preference on preference.user_id = target.id
  where target.id = target_user_id
    and target.status = 'active'
    and coalesce(preference.allow_follows, true);

  if not found then raise exception 'Following is not available for this profile'; end if;

  if exists (
    select 1 from public.blocks block
    where
      (block.blocker_id = current_user_id and block.blocked_user_id = target_user_id)
      or (block.blocker_id = target_user_id and block.blocked_user_id = current_user_id)
  ) then
    raise exception 'Following is not available for this profile';
  end if;

  if exists (
    select 1 from public.follows follow
    where follow.follower_id = current_user_id and follow.followed_id = target_user_id
  ) then
    return 'following';
  end if;

  if target_requires_approval then
    insert into public.follow_requests (requester_id, target_user_id)
    values (current_user_id, target_user_id)
    on conflict (requester_id, target_user_id) do nothing
    returning id into inserted_request_id;

    if inserted_request_id is not null then
      select display_name into requester_name from public.profiles where id = current_user_id;
      insert into public.notifications (user_id, type, title, body, payload)
      values (
        target_user_id,
        'follow_request',
        'Yeni takip isteği',
        coalesce(requester_name, 'Bir kullanıcı') || ' seni takip etmek istiyor.',
        jsonb_build_object('user_id', current_user_id, 'follow_request_id', inserted_request_id)
      );
    end if;

    return 'requested';
  end if;

  delete from public.follow_requests
  where requester_id = current_user_id and follow_requests.target_user_id = set_profile_follow_state.target_user_id;

  insert into public.follows (follower_id, followed_id)
  values (current_user_id, target_user_id)
  on conflict (follower_id, followed_id) do nothing;
  return 'following';
end;
$$;

-- Compatibility for installed versions that still expect a boolean response.
create or replace function public.set_profile_follow(
  target_user_id uuid,
  target_should_follow boolean
)
returns boolean
language plpgsql
security definer
set search_path = public
as $$
begin
  return public.set_profile_follow_state(target_user_id, target_should_follow) = 'following';
end;
$$;

create or replace function public.get_my_follow_relation(target_user_id uuid)
returns text
language sql
stable
security definer
set search_path = public
as $$
  select case
    when exists (
      select 1 from public.follows follow
      where follow.follower_id = auth.uid() and follow.followed_id = target_user_id
    ) then 'following'
    when exists (
      select 1 from public.follow_requests request
      where request.requester_id = auth.uid() and request.target_user_id = get_my_follow_relation.target_user_id
    ) then 'requested'
    else 'none'
  end;
$$;

create or replace function public.get_my_follow_requests()
returns table (
  request_id uuid,
  user_id uuid,
  display_name text,
  username text,
  avatar_url text,
  bio text,
  city text,
  is_verified boolean,
  follower_count bigint,
  following_count bigint,
  requested_at timestamptz
)
language sql
stable
security definer
set search_path = public
as $$
  select
    request.id,
    profile.id,
    profile.display_name,
    profile.username,
    profile.avatar_url,
    profile.bio,
    case when coalesce(preference.show_city, true) then profile.city else null end,
    profile.is_verified,
    (select count(*) from public.follows follower where follower.followed_id = profile.id),
    (select count(*) from public.follows followed where followed.follower_id = profile.id),
    request.created_at
  from public.follow_requests request
  join public.profiles profile on profile.id = request.requester_id
  left join public.account_preferences preference on preference.user_id = profile.id
  where request.target_user_id = auth.uid()
    and profile.status = 'active'
    and not exists (
      select 1 from public.blocks block
      where
        (block.blocker_id = auth.uid() and block.blocked_user_id = profile.id)
        or (block.blocker_id = profile.id and block.blocked_user_id = auth.uid())
    )
  order by request.created_at desc;
$$;

create or replace function public.review_follow_request(
  target_request_id uuid,
  target_accept boolean
)
returns text
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  request_record record;
  current_user_name text;
begin
  if current_user_id is null then raise exception 'Authentication required'; end if;

  select request.id, request.requester_id
  into request_record
  from public.follow_requests request
  where request.id = target_request_id and request.target_user_id = current_user_id
  for update;

  if not found then raise exception 'Follow request not found'; end if;

  delete from public.follow_requests where id = request_record.id;

  select display_name into current_user_name
  from public.profiles
  where id = current_user_id;

  if not target_accept then
    insert into public.notifications (user_id, type, title, body, payload)
    values (
      request_record.requester_id,
      'follow_request_rejected',
      'Takip isteğin sonuçlandı',
      coalesce(current_user_name, 'Kullanıcı') || ' takip isteğini kabul etmedi.',
      jsonb_build_object('user_id', current_user_id)
    );
    return 'rejected';
  end if;

  if exists (
    select 1 from public.blocks block
    where
      (block.blocker_id = current_user_id and block.blocked_user_id = request_record.requester_id)
      or (block.blocker_id = request_record.requester_id and block.blocked_user_id = current_user_id)
  ) then
    raise exception 'Following is not available for this profile';
  end if;

  if not coalesce((
    select preference.allow_follows
    from public.account_preferences preference
    where preference.user_id = current_user_id
  ), true) then
    raise exception 'Following is not available for this profile';
  end if;

  insert into public.follows (follower_id, followed_id)
  values (request_record.requester_id, current_user_id)
  on conflict (follower_id, followed_id) do nothing;

  insert into public.notifications (user_id, type, title, body, payload)
  values (
    request_record.requester_id,
    'follow_request_accepted',
    'Takip isteğin kabul edildi',
    coalesce(current_user_name, 'Kullanıcı') || ' takip isteğini kabul etti.',
    jsonb_build_object('user_id', current_user_id)
  );
  return 'accepted';
end;
$$;

create or replace function public.cleanup_disabled_follow_requests()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  if old.allow_follows and not new.allow_follows then
    delete from public.follow_requests where target_user_id = new.user_id;
  end if;
  return new;
end;
$$;

drop trigger if exists trg_account_preferences_cleanup_follow_requests on public.account_preferences;
create trigger trg_account_preferences_cleanup_follow_requests
after update of allow_follows on public.account_preferences
for each row execute function public.cleanup_disabled_follow_requests();

create or replace function public.set_profile_block(
  target_user_id uuid,
  target_should_block boolean
)
returns boolean
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
begin
  if current_user_id is null then raise exception 'Authentication required'; end if;
  if target_user_id is null or target_user_id = current_user_id then raise exception 'Invalid profile'; end if;

  if target_should_block then
    insert into public.blocks (blocker_id, blocked_user_id)
    values (current_user_id, target_user_id)
    on conflict (blocker_id, blocked_user_id) do nothing;

    delete from public.follows
    where
      (follower_id = current_user_id and followed_id = target_user_id)
      or (follower_id = target_user_id and followed_id = current_user_id);

    delete from public.follow_requests
    where
      (requester_id = current_user_id and follow_requests.target_user_id = set_profile_block.target_user_id)
      or (requester_id = set_profile_block.target_user_id and follow_requests.target_user_id = current_user_id);
    return true;
  end if;

  delete from public.blocks
  where blocker_id = current_user_id and blocked_user_id = target_user_id;
  return false;
end;
$$;

revoke all on function public.set_profile_follow_state(uuid, boolean) from public;
revoke all on function public.get_my_follow_relation(uuid) from public;
revoke all on function public.get_my_follow_requests() from public;
revoke all on function public.review_follow_request(uuid, boolean) from public;
revoke all on function public.cleanup_disabled_follow_requests() from public;
grant execute on function public.set_profile_follow_state(uuid, boolean) to authenticated;
grant execute on function public.get_my_follow_relation(uuid) to authenticated;
grant execute on function public.get_my_follow_requests() to authenticated;
grant execute on function public.review_follow_request(uuid, boolean) to authenticated;

create or replace function public.notification_category(target_type text)
returns text
language sql
immutable
set search_path = public
as $$
  select case
    when target_type like 'event_%'
      or target_type like 'participation_%'
      or target_type like 'waitlist_%'
      then 'events'
    when target_type like 'community_%'
      or target_type like 'group_%'
      then 'communities'
    when target_type like 'advantage_%'
      or target_type like 'offer_%'
      then 'advantages'
    when target_type in (
      'new_follower', 'follow_request', 'follow_request_accepted',
      'follow_request_rejected', 'new_comment', 'user_review', 'story_reply'
    ) then 'social'
    else 'system'
  end;
$$;

notify pgrst, 'reload schema';
