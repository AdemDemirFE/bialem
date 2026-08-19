create table if not exists public.stories (
  id uuid primary key default gen_random_uuid(),
  author_id uuid not null references public.profiles(id) on delete cascade,
  community_id uuid references public.communities(id) on delete cascade,
  content_type text not null check (content_type in ('text', 'image')),
  body text check (body is null or char_length(body) <= 500),
  media_url text,
  created_at timestamptz not null default now(),
  expires_at timestamptz not null default (now() + interval '24 hours')
);

create table if not exists public.story_views (
  id uuid primary key default gen_random_uuid(),
  story_id uuid not null references public.stories(id) on delete cascade,
  viewer_id uuid not null references public.profiles(id) on delete cascade,
  viewed_at timestamptz not null default now(),
  unique (story_id, viewer_id)
);

create index if not exists idx_stories_expires_created on public.stories(expires_at, created_at desc);
create index if not exists idx_stories_author_created on public.stories(author_id, created_at desc);
create index if not exists idx_story_views_viewer on public.story_views(viewer_id, viewed_at desc);

alter table public.stories enable row level security;
alter table public.story_views enable row level security;

drop policy if exists stories_read_related on public.stories;
create policy stories_read_related
on public.stories
for select
to authenticated
using (
  expires_at > now()
  and (
    author_id = auth.uid()
    or exists (
      select 1 from public.follows f
      where f.follower_id = auth.uid() and f.followed_id = stories.author_id
    )
    or (community_id is not null and public.is_approved_community_member(community_id, auth.uid()))
  )
);

drop policy if exists stories_create_own on public.stories;
create policy stories_create_own
on public.stories
for insert
to authenticated
with check (
  author_id = auth.uid()
  and (community_id is null or public.is_approved_community_member(community_id, auth.uid()))
);

drop policy if exists stories_update_own on public.stories;
create policy stories_update_own
on public.stories
for update
to authenticated
using (author_id = auth.uid())
with check (author_id = auth.uid());

drop policy if exists stories_delete_own on public.stories;
create policy stories_delete_own
on public.stories
for delete
to authenticated
using (author_id = auth.uid());

drop policy if exists story_views_read_related on public.story_views;
create policy story_views_read_related
on public.story_views
for select
to authenticated
using (
  viewer_id = auth.uid()
  or exists (
    select 1 from public.stories s
    where s.id = story_views.story_id and s.author_id = auth.uid()
  )
);

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
    s.id,
    s.author_id,
    p.display_name,
    p.avatar_url,
    s.community_id,
    c.name,
    s.content_type,
    s.body,
    s.media_url,
    s.created_at,
    s.expires_at,
    exists (
      select 1 from public.story_views sv
      where sv.story_id = s.id and sv.viewer_id = auth.uid()
    ) as is_viewed
  from public.stories s
  join public.profiles p on p.id = s.author_id
  left join public.communities c on c.id = s.community_id
  where auth.uid() is not null
    and s.expires_at > now()
    and (
      s.author_id = auth.uid()
      or exists (
        select 1 from public.follows f
        where f.follower_id = auth.uid() and f.followed_id = s.author_id
      )
      or (s.community_id is not null and public.is_approved_community_member(s.community_id, auth.uid()))
    )
  order by is_viewed asc, s.created_at desc;
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
  select * from public.get_story_feed() sf where sf.story_id = target_story_id;
$$;

create or replace function public.mark_story_viewed(target_story_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
  if not exists (select 1 from public.get_story_detail(target_story_id)) then
    raise exception 'Story is not available';
  end if;

  insert into public.story_views (story_id, viewer_id)
  values (target_story_id, auth.uid())
  on conflict (story_id, viewer_id)
  do update set viewed_at = now();
end;
$$;

revoke all on function public.get_story_feed() from public;
revoke all on function public.get_story_detail(uuid) from public;
revoke all on function public.mark_story_viewed(uuid) from public;
grant execute on function public.get_story_feed() to authenticated;
grant execute on function public.get_story_detail(uuid) to authenticated;
grant execute on function public.mark_story_viewed(uuid) to authenticated;

insert into storage.buckets (id, name, public)
values ('stories', 'stories', true)
on conflict (id) do update set public = excluded.public;

drop policy if exists "Stories public read" on storage.objects;
create policy "Stories public read"
on storage.objects for select
using (bucket_id = 'stories');

drop policy if exists "Stories upload own folder" on storage.objects;
create policy "Stories upload own folder"
on storage.objects for insert to authenticated
with check (
  bucket_id = 'stories'
  and (storage.foldername(name))[1] = auth.uid()::text
);

drop policy if exists "Stories delete own folder" on storage.objects;
create policy "Stories delete own folder"
on storage.objects for delete to authenticated
using (
  bucket_id = 'stories'
  and (storage.foldername(name))[1] = auth.uid()::text
);
