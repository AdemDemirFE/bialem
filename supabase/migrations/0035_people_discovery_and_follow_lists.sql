drop policy if exists follows_read_authenticated on public.follows;
drop policy if exists follows_read_related on public.follows;
create policy follows_read_related
on public.follows
for select
to authenticated
using (
  follower_id = auth.uid()
  or followed_id = auth.uid()
  or public.is_admin()
);

drop policy if exists follows_create_own on public.follows;
create policy follows_create_own
on public.follows
for insert
to authenticated
with check (
  follower_id = auth.uid()
  and exists (
    select 1
    from public.profiles target
    where target.id = followed_id
      and target.status = 'active'
  )
  and not exists (
    select 1
    from public.blocks block
    where
      (block.blocker_id = auth.uid() and block.blocked_user_id = followed_id)
      or (block.blocker_id = followed_id and block.blocked_user_id = auth.uid())
  )
);

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
    profile.city,
    profile.is_verified,
    (
      select count(*)
      from public.follows follower
      join public.profiles follower_profile on follower_profile.id = follower.follower_id
      where follower.followed_id = profile.id
        and follower_profile.status = 'active'
    ) as follower_count,
    (
      select count(*)
      from public.follows followed
      join public.profiles followed_profile on followed_profile.id = followed.followed_id
      where followed.follower_id = profile.id
        and followed_profile.status = 'active'
    ) as following_count,
    exists (
      select 1
      from public.follows mine
      where mine.follower_id = auth.uid()
        and mine.followed_id = profile.id
    ) as is_following
  from public.profiles profile
  cross join input
  where auth.uid() is not null
    and profile.id <> auth.uid()
    and profile.status = 'active'
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
      or strpos(lower(coalesce(profile.city, '')), input.query) > 0
    )
  order by
    case when lower(profile.username) = input.query then 0 else 1 end,
    case when lower(profile.username) like input.query || '%' then 0 else 1 end,
    8 desc,
    profile.display_name
  limit (select row_limit from input);
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
    (
      select count(*)
      from public.follows follower
      join public.profiles profile on profile.id = follower.follower_id
      where follower.followed_id = target_user_id
        and profile.status = 'active'
    ),
    (
      select count(*)
      from public.follows followed
      join public.profiles profile on profile.id = followed.followed_id
      where followed.follower_id = target_user_id
        and profile.status = 'active'
    ),
    exists (
      select 1
      from public.follows mine
      where mine.follower_id = auth.uid()
        and mine.followed_id = target_user_id
    )
  where auth.uid() is not null
    and exists (
      select 1
      from public.profiles target
      where target.id = target_user_id
        and target.status <> 'deleted'
    )
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
      case
        when target_kind = 'followers' then follow.follower_id
        else follow.followed_id
      end as connected_user_id,
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
    profile.city,
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
    ),
    connection.created_at
  from connections connection
  join public.profiles profile on profile.id = connection.connected_user_id
  where auth.uid() is not null
    and target_kind in ('followers', 'following')
    and not exists (
      select 1
      from public.blocks target_block
      where
        (target_block.blocker_id = auth.uid() and target_block.blocked_user_id = target_user_id)
        or (target_block.blocker_id = target_user_id and target_block.blocked_user_id = auth.uid())
    )
    and profile.status = 'active'
    and not exists (
      select 1
      from public.blocks block
      where
        (block.blocker_id = auth.uid() and block.blocked_user_id = profile.id)
        or (block.blocker_id = profile.id and block.blocked_user_id = auth.uid())
    )
  order by connection.created_at desc
  limit least(greatest(coalesce(result_limit, 100), 1), 100)
  offset greatest(coalesce(result_offset, 0), 0);
$$;

revoke all on function public.search_public_profiles(text, integer) from public;
revoke all on function public.get_public_follow_summary(uuid) from public;
revoke all on function public.get_public_follow_connections(uuid, text, integer, integer) from public;
grant execute on function public.search_public_profiles(text, integer) to authenticated;
grant execute on function public.get_public_follow_summary(uuid) to authenticated;
grant execute on function public.get_public_follow_connections(uuid, text, integer, integer) to authenticated;

notify pgrst, 'reload schema';
