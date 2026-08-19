alter table public.stories
add column if not exists is_public boolean not null default false;

alter table public.stories
add column if not exists share_with_followers boolean not null default true;

create table if not exists public.story_community_targets (
  story_id uuid not null references public.stories(id) on delete cascade,
  community_id uuid not null references public.communities(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key (story_id, community_id)
);

create index if not exists idx_story_community_targets_community
on public.story_community_targets(community_id, story_id);

insert into public.story_community_targets (story_id, community_id)
select story.id, story.community_id
from public.stories story
where story.community_id is not null
on conflict (story_id, community_id) do nothing;

alter table public.story_community_targets enable row level security;
revoke all on table public.story_community_targets from anon, authenticated;

create or replace function public.sync_legacy_story_community_target()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  if new.community_id is not null then
    insert into public.story_community_targets (story_id, community_id)
    values (new.id, new.community_id)
    on conflict (story_id, community_id) do nothing;
  end if;

  return new;
end;
$$;

drop trigger if exists trg_stories_sync_legacy_community_target on public.stories;
create trigger trg_stories_sync_legacy_community_target
after insert on public.stories
for each row execute function public.sync_legacy_story_community_target();

create or replace function public.can_view_story(target_story_id uuid, target_user_id uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select target_user_id is not null and exists (
    select 1
    from public.stories story
    where story.id = target_story_id
      and story.expires_at > now()
      and (
        story.author_id = target_user_id
        or story.is_public
        or (
          story.share_with_followers
          and exists (
            select 1
            from public.follows follow
            where follow.follower_id = target_user_id
              and follow.followed_id = story.author_id
          )
        )
        or exists (
          select 1
          from public.story_community_targets target
          where target.story_id = story.id
            and public.is_approved_community_member(target.community_id, target_user_id)
        )
      )
  );
$$;

drop policy if exists stories_read_related on public.stories;
create policy stories_read_related
on public.stories
for select
to authenticated
using (public.can_view_story(id, auth.uid()));

drop policy if exists stories_create_own on public.stories;
create policy stories_create_own
on public.stories
for insert
to authenticated
with check (
  author_id = auth.uid()
  and (community_id is null or public.is_approved_community_member(community_id, auth.uid()))
);

create or replace function public.create_story_with_audience(
  target_content_type text,
  target_body text,
  target_is_public boolean,
  target_share_with_followers boolean,
  target_community_ids uuid[]
)
returns uuid
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  normalized_community_ids uuid[];
  created_story_id uuid;
begin
  if current_user_id is null then
    raise exception 'Authentication required';
  end if;

  if target_content_type not in ('text', 'image') then
    raise exception 'Invalid story content type';
  end if;

  if target_content_type = 'text' and nullif(trim(target_body), '') is null then
    raise exception 'Story text is required';
  end if;

  if char_length(coalesce(target_body, '')) > 500 then
    raise exception 'Story text is too long';
  end if;

  select coalesce(array_agg(distinct input.community_id), '{}'::uuid[])
  into normalized_community_ids
  from unnest(coalesce(target_community_ids, '{}'::uuid[])) as input(community_id)
  where input.community_id is not null;

  if not target_is_public
    and not target_share_with_followers
    and cardinality(normalized_community_ids) = 0 then
    raise exception 'Select at least one story audience';
  end if;

  if not target_is_public and exists (
    select 1
    from unnest(normalized_community_ids) as selected(community_id)
    where not public.is_approved_community_member(selected.community_id, current_user_id)
  ) then
    raise exception 'Story can only be shared with joined communities';
  end if;

  insert into public.stories (
    author_id,
    community_id,
    content_type,
    body,
    is_public,
    share_with_followers
  )
  values (
    current_user_id,
    case when target_is_public then null else normalized_community_ids[1] end,
    target_content_type,
    nullif(trim(target_body), ''),
    target_is_public,
    case when target_is_public then false else target_share_with_followers end
  )
  returning id into created_story_id;

  if not target_is_public then
    insert into public.story_community_targets (story_id, community_id)
    select created_story_id, selected.community_id
    from unnest(normalized_community_ids) as selected(community_id)
    on conflict (story_id, community_id) do nothing;
  end if;

  return created_story_id;
end;
$$;

create or replace function public.get_story_feed()
returns table (
  story_id uuid,
  author_id uuid,
  display_name text,
  avatar_url text,
  community_id uuid,
  community_name text,
  content_type text,
  body text,
  media_url text,
  created_at timestamptz,
  expires_at timestamptz,
  is_viewed boolean
)
language sql
stable
security definer
set search_path = public
as $$
  select
    story.id,
    story.author_id,
    profile.display_name,
    profile.avatar_url,
    story.community_id,
    case
      when story.is_public then 'Herkesle paylaştı'
      when story.share_with_followers and target_names.names is not null
        then 'Takipçiler + ' || target_names.names
      when target_names.names is not null then target_names.names
      else null
    end,
    story.content_type,
    story.body,
    story.media_url,
    story.created_at,
    story.expires_at,
    exists (
      select 1
      from public.story_views story_view
      where story_view.story_id = story.id
        and story_view.viewer_id = auth.uid()
    ) as is_viewed
  from public.stories story
  join public.profiles profile on profile.id = story.author_id
  left join lateral (
    select string_agg(community.name, ', ' order by community.name) as names
    from public.story_community_targets target
    join public.communities community on community.id = target.community_id
    where target.story_id = story.id
  ) target_names on true
  where public.can_view_story(story.id, auth.uid())
  order by is_viewed asc, story.created_at desc;
$$;

create or replace function public.get_story_detail(target_story_id uuid)
returns table (
  story_id uuid,
  author_id uuid,
  display_name text,
  avatar_url text,
  community_id uuid,
  community_name text,
  content_type text,
  body text,
  media_url text,
  created_at timestamptz,
  expires_at timestamptz,
  is_viewed boolean
)
language sql
stable
security definer
set search_path = public
as $$
  select *
  from public.get_story_feed() story_feed
  where story_feed.story_id = target_story_id;
$$;

revoke all on function public.can_view_story(uuid, uuid) from public;
revoke all on function public.sync_legacy_story_community_target() from public;
revoke all on function public.create_story_with_audience(text, text, boolean, boolean, uuid[]) from public;
revoke all on function public.get_story_feed() from public;
revoke all on function public.get_story_detail(uuid) from public;
grant execute on function public.can_view_story(uuid, uuid) to authenticated;
grant execute on function public.create_story_with_audience(text, text, boolean, boolean, uuid[]) to authenticated;
grant execute on function public.get_story_feed() to authenticated;
grant execute on function public.get_story_detail(uuid) to authenticated;

notify pgrst, 'reload schema';
