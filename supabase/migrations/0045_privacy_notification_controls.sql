create table if not exists public.account_preferences (
  user_id uuid primary key references public.profiles(id) on delete cascade,
  discoverable boolean not null default true,
  show_city boolean not null default true,
  show_follow_connections boolean not null default true,
  allow_follows boolean not null default true,
  allow_messages_from text not null default 'following'
    check (allow_messages_from in ('everyone', 'following', 'no_one')),
  notify_events boolean not null default true,
  notify_communities boolean not null default true,
  notify_social boolean not null default true,
  notify_advantages boolean not null default true,
  notify_system boolean not null default true,
  updated_at timestamptz not null default now()
);

alter table public.account_preferences enable row level security;

drop policy if exists account_preferences_read_own on public.account_preferences;
create policy account_preferences_read_own
on public.account_preferences for select to authenticated
using (user_id = auth.uid() or public.is_admin());

drop policy if exists account_preferences_insert_own on public.account_preferences;
create policy account_preferences_insert_own
on public.account_preferences for insert to authenticated
with check (user_id = auth.uid());

drop policy if exists account_preferences_update_own on public.account_preferences;
create policy account_preferences_update_own
on public.account_preferences for update to authenticated
using (user_id = auth.uid())
with check (user_id = auth.uid());

drop trigger if exists trg_account_preferences_updated_at on public.account_preferences;
create trigger trg_account_preferences_updated_at
before update on public.account_preferences
for each row execute function public.set_updated_at();

insert into public.account_preferences (user_id)
select profile.id
from public.profiles profile
where profile.status <> 'deleted'
on conflict (user_id) do nothing;

create or replace function public.create_account_preferences_for_profile()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  insert into public.account_preferences (user_id)
  values (new.id)
  on conflict (user_id) do nothing;
  return new;
end;
$$;

drop trigger if exists trg_profiles_create_account_preferences on public.profiles;
create trigger trg_profiles_create_account_preferences
after insert on public.profiles
for each row execute function public.create_account_preferences_for_profile();

create or replace function public.search_public_profiles(
  target_query text default null,
  result_limit integer default 30
)
returns table (
  user_id uuid,
  display_name text,
  username text,
  avatar_url text,
  bio text,
  city text,
  is_verified boolean,
  follower_count bigint,
  following_count bigint,
  is_following boolean
)
language sql
stable
security definer
set search_path = public
as $$
  with input as (
    select
      lower(trim(coalesce(target_query, ''))) as query,
      least(greatest(coalesce(result_limit, 30), 1), 50) as row_limit
  )
  select
    profile.id,
    profile.display_name,
    profile.username,
    profile.avatar_url,
    profile.bio,
    case when coalesce(preference.show_city, true) then profile.city else null end,
    profile.is_verified,
    (
      select count(*)
      from public.follows follower
      join public.profiles follower_profile on follower_profile.id = follower.follower_id
      where follower.followed_id = profile.id
        and follower_profile.status = 'active'
    ),
    (
      select count(*)
      from public.follows followed
      join public.profiles followed_profile on followed_profile.id = followed.followed_id
      where followed.follower_id = profile.id
        and followed_profile.status = 'active'
    ),
    exists (
      select 1
      from public.follows mine
      where mine.follower_id = auth.uid()
        and mine.followed_id = profile.id
    )
  from public.profiles profile
  left join public.account_preferences preference on preference.user_id = profile.id
  cross join input
  where auth.uid() is not null
    and profile.id <> auth.uid()
    and profile.status = 'active'
    and coalesce(preference.discoverable, true)
    and not exists (
      select 1
      from public.blocks block
      where
        (block.blocker_id = auth.uid() and block.blocked_user_id = profile.id)
        or (block.blocker_id = profile.id and block.blocked_user_id = auth.uid())
    )
    and (
      input.query = ''
      or strpos(lower(profile.username), input.query) > 0
      or strpos(lower(profile.display_name), input.query) > 0
      or (
        coalesce(preference.show_city, true)
        and strpos(lower(coalesce(profile.city, '')), input.query) > 0
      )
    )
  order by
    case when lower(profile.username) = input.query then 0 else 1 end,
    case when lower(profile.username) like input.query || '%' then 0 else 1 end,
    8 desc,
    profile.display_name
  limit (select row_limit from input);
$$;

create or replace function public.get_public_profile_card(target_user_id uuid)
returns table (
  id uuid,
  display_name text,
  username text,
  avatar_url text,
  bio text,
  city text,
  status text,
  is_verified boolean,
  created_at timestamptz
)
language sql
stable
security definer
set search_path = public
as $$
  select
    profile.id,
    profile.display_name,
    profile.username,
    profile.avatar_url,
    profile.bio,
    case
      when profile.id = auth.uid() or coalesce(preference.show_city, true) then profile.city
      else null
    end,
    profile.status,
    profile.is_verified,
    profile.created_at
  from public.profiles profile
  left join public.account_preferences preference on preference.user_id = profile.id
  where profile.id = target_user_id
    and profile.status = 'active'
    and not exists (
      select 1
      from public.blocks block
      where
        (block.blocker_id = auth.uid() and block.blocked_user_id = profile.id)
        or (block.blocker_id = profile.id and block.blocked_user_id = auth.uid())
    );
$$;

create or replace function public.get_public_follow_summary(target_user_id uuid)
returns table (
  follower_count bigint,
  following_count bigint,
  is_following boolean
)
language sql
stable
security definer
set search_path = public
as $$
  select
    case
      when target_user_id = auth.uid() or coalesce(preference.show_follow_connections, true)
      then (
        select count(*)
        from public.follows follower
        join public.profiles profile on profile.id = follower.follower_id
        where follower.followed_id = target_user_id and profile.status = 'active'
      )
      else 0
    end,
    case
      when target_user_id = auth.uid() or coalesce(preference.show_follow_connections, true)
      then (
        select count(*)
        from public.follows followed
        join public.profiles profile on profile.id = followed.followed_id
        where followed.follower_id = target_user_id and profile.status = 'active'
      )
      else 0
    end,
    exists (
      select 1
      from public.follows mine
      where mine.follower_id = auth.uid()
        and mine.followed_id = target_user_id
    )
  from public.profiles target
  left join public.account_preferences preference on preference.user_id = target.id
  where auth.uid() is not null
    and target.id = target_user_id
    and target.status = 'active'
    and not exists (
      select 1
      from public.blocks block
      where
        (block.blocker_id = auth.uid() and block.blocked_user_id = target_user_id)
        or (block.blocker_id = target_user_id and block.blocked_user_id = auth.uid())
    );
$$;

create or replace function public.get_public_follow_connections(
  target_user_id uuid,
  target_kind text,
  result_limit integer default 100,
  result_offset integer default 0
)
returns table (
  user_id uuid,
  display_name text,
  username text,
  avatar_url text,
  bio text,
  city text,
  is_verified boolean,
  follower_count bigint,
  following_count bigint,
  is_following boolean,
  followed_at timestamptz
)
language sql
stable
security definer
set search_path = public
as $$
  with connections as (
    select
      case when target_kind = 'followers' then follow.follower_id else follow.followed_id end as connected_user_id,
      follow.created_at
    from public.follows follow
    where
      (target_kind = 'followers' and follow.followed_id = target_user_id)
      or (target_kind = 'following' and follow.follower_id = target_user_id)
  )
  select
    profile.id,
    profile.display_name,
    profile.username,
    profile.avatar_url,
    profile.bio,
    case when coalesce(profile_preference.show_city, true) then profile.city else null end,
    profile.is_verified,
    (
      select count(*) from public.follows follower
      join public.profiles follower_profile on follower_profile.id = follower.follower_id
      where follower.followed_id = profile.id and follower_profile.status = 'active'
    ),
    (
      select count(*) from public.follows followed
      join public.profiles followed_profile on followed_profile.id = followed.followed_id
      where followed.follower_id = profile.id and followed_profile.status = 'active'
    ),
    exists (
      select 1 from public.follows mine
      where mine.follower_id = auth.uid() and mine.followed_id = profile.id
    ),
    connection.created_at
  from connections connection
  join public.profiles profile on profile.id = connection.connected_user_id
  left join public.account_preferences target_preference on target_preference.user_id = target_user_id
  left join public.account_preferences profile_preference on profile_preference.user_id = profile.id
  where auth.uid() is not null
    and target_kind in ('followers', 'following')
    and (target_user_id = auth.uid() or coalesce(target_preference.show_follow_connections, true))
    and profile.status = 'active'
    and not exists (
      select 1 from public.blocks block
      where
        (block.blocker_id = auth.uid() and block.blocked_user_id in (target_user_id, profile.id))
        or (block.blocker_id in (target_user_id, profile.id) and block.blocked_user_id = auth.uid())
    )
  order by connection.created_at desc
  limit least(greatest(coalesce(result_limit, 100), 1), 100)
  offset greatest(coalesce(result_offset, 0), 0);
$$;

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
  if current_user_id is null then raise exception 'Authentication required'; end if;
  if target_user_id is null then raise exception 'Target profile is required'; end if;
  if target_user_id = current_user_id then raise exception 'Users cannot follow themselves'; end if;

  if not target_should_follow then
    delete from public.follows
    where follower_id = current_user_id and followed_id = target_user_id;
    return false;
  end if;

  if not exists (
    select 1
    from public.profiles target
    left join public.account_preferences preference on preference.user_id = target.id
    where target.id = target_user_id
      and target.status = 'active'
      and coalesce(preference.allow_follows, true)
  ) then
    raise exception 'Following is not available for this profile';
  end if;

  if exists (
    select 1 from public.blocks block
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

create or replace function public.get_my_blocked_profiles()
returns table (
  user_id uuid,
  display_name text,
  username text,
  avatar_url text,
  blocked_at timestamptz
)
language sql
stable
security definer
set search_path = public
as $$
  select profile.id, profile.display_name, profile.username, profile.avatar_url, block.created_at
  from public.blocks block
  join public.profiles profile on profile.id = block.blocked_user_id
  where block.blocker_id = auth.uid()
  order by block.created_at desc;
$$;

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
  if target_user_id is null or target_user_id = current_user_id then
    raise exception 'Invalid profile';
  end if;

  if target_should_block then
    insert into public.blocks (blocker_id, blocked_user_id)
    values (current_user_id, target_user_id)
    on conflict (blocker_id, blocked_user_id) do nothing;

    delete from public.follows
    where
      (follower_id = current_user_id and followed_id = target_user_id)
      or (follower_id = target_user_id and followed_id = current_user_id);
    return true;
  end if;

  delete from public.blocks
  where blocker_id = current_user_id and blocked_user_id = target_user_id;
  return false;
end;
$$;

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
    when target_type in ('new_follower', 'new_comment', 'user_review', 'story_reply')
      then 'social'
    else 'system'
  end;
$$;

create or replace function public.should_deliver_push_notification(target_user_id uuid, target_type text)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select case public.notification_category(target_type)
    when 'events' then coalesce(preference.notify_events, true)
    when 'communities' then coalesce(preference.notify_communities, true)
    when 'social' then coalesce(preference.notify_social, true)
    when 'advantages' then coalesce(preference.notify_advantages, true)
    else coalesce(preference.notify_system, true)
  end
  from (select 1) input
  left join public.account_preferences preference on preference.user_id = target_user_id;
$$;

create or replace function public.send_notification_push()
returns trigger
language plpgsql
security definer
set search_path = public, extensions
as $$
declare
  token_record record;
begin
  if not public.should_deliver_push_notification(new.user_id, new.type) then
    return new;
  end if;

  for token_record in
    select expo_push_token
    from public.push_tokens
    where user_id = new.user_id and is_active = true
  loop
    perform net.http_post(
      url := 'https://exp.host/--/api/v2/push/send',
      headers := jsonb_build_object('Accept', 'application/json', 'Content-Type', 'application/json'),
      body := jsonb_build_object(
        'to', token_record.expo_push_token,
        'sound', 'default',
        'title', new.title,
        'body', coalesce(new.body, ''),
        'data', new.payload
      )
    );
  end loop;
  return new;
end;
$$;

create or replace function public.get_my_profile_plans()
returns table (
  event_id uuid,
  title text,
  starts_at timestamptz,
  ends_at timestamptz,
  location_name text,
  cover_image_url text,
  event_status text,
  participation_status text,
  community_name text
)
language sql
stable
security definer
set search_path = public
as $$
  select
    event.id,
    event.title,
    event.starts_at,
    event.ends_at,
    event.location_name,
    event.cover_image_url,
    event.status,
    participant.status,
    community.name
  from public.event_participants participant
  join public.events event on event.id = participant.event_id
  join public.communities community on community.id = event.community_id
  where participant.user_id = auth.uid()
    and participant.status in ('pending', 'waitlisted', 'approved', 'checked_in')
    and event.status in ('published', 'completed', 'cancelled')
  order by event.starts_at desc;
$$;

revoke all on function public.create_account_preferences_for_profile() from public;
revoke all on function public.get_my_blocked_profiles() from public;
revoke all on function public.set_profile_block(uuid, boolean) from public;
revoke all on function public.get_my_profile_plans() from public;
revoke all on function public.notification_category(text) from public;
revoke all on function public.should_deliver_push_notification(uuid, text) from public;
revoke all on function public.send_notification_push() from public;

grant execute on function public.get_my_blocked_profiles() to authenticated;
grant execute on function public.set_profile_block(uuid, boolean) to authenticated;
grant execute on function public.get_my_profile_plans() to authenticated;

notify pgrst, 'reload schema';
