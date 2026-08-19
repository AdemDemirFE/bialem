create extension if not exists pgcrypto;

create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

create table if not exists public.profiles (
  id uuid primary key,
  email text not null unique,
  display_name text not null,
  username text not null unique,
  avatar_url text,
  bio text,
  city text,
  status text not null default 'pending_verification' check (status in ('active', 'pending_verification', 'suspended', 'deleted')),
  is_verified boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.roles (
  id uuid primary key default gen_random_uuid(),
  code text not null unique,
  name text not null,
  created_at timestamptz not null default now()
);

create table if not exists public.user_roles (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  role_id uuid not null references public.roles(id) on delete cascade,
  created_at timestamptz not null default now(),
  unique (user_id, role_id)
);

create table if not exists public.communities (
  id uuid primary key default gen_random_uuid(),
  parent_id uuid references public.communities(id) on delete set null,
  name text not null,
  slug text not null unique,
  description text,
  visibility text not null default 'public' check (visibility in ('public', 'private', 'invite_only')),
  cover_image_url text,
  created_by uuid not null references public.profiles(id) on delete restrict,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.community_members (
  id uuid primary key default gen_random_uuid(),
  community_id uuid not null references public.communities(id) on delete cascade,
  user_id uuid not null references public.profiles(id) on delete cascade,
  role text not null default 'member' check (role in ('member', 'manager', 'owner')),
  status text not null default 'approved' check (status in ('pending', 'approved', 'rejected', 'blocked')),
  created_at timestamptz not null default now(),
  unique (community_id, user_id)
);

create table if not exists public.events (
  id uuid primary key default gen_random_uuid(),
  community_id uuid not null references public.communities(id) on delete cascade,
  created_by uuid not null references public.profiles(id) on delete restrict,
  title text not null,
  description text,
  starts_at timestamptz not null,
  ends_at timestamptz,
  location_name text,
  address_text text,
  latitude numeric(9,6),
  longitude numeric(9,6),
  cover_image_url text,
  capacity integer check (capacity is null or capacity > 0),
  status text not null default 'pending_approval' check (status in ('draft', 'pending_approval', 'published', 'rejected', 'cancelled', 'completed')),
  rejection_reason text,
  published_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.event_participants (
  id uuid primary key default gen_random_uuid(),
  event_id uuid not null references public.events(id) on delete cascade,
  user_id uuid not null references public.profiles(id) on delete cascade,
  status text not null default 'pending' check (status in ('pending', 'approved', 'rejected', 'cancelled', 'checked_in')),
  note text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (event_id, user_id)
);

create table if not exists public.posts (
  id uuid primary key default gen_random_uuid(),
  community_id uuid references public.communities(id) on delete cascade,
  event_id uuid references public.events(id) on delete cascade,
  author_id uuid not null references public.profiles(id) on delete cascade,
  body text,
  visibility text not null default 'public' check (visibility in ('public', 'community_only', 'event_only')),
  moderation_status text not null default 'visible' check (moderation_status in ('visible', 'hidden', 'flagged')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  check (community_id is not null or event_id is not null)
);

create table if not exists public.post_media (
  id uuid primary key default gen_random_uuid(),
  post_id uuid not null references public.posts(id) on delete cascade,
  media_type text not null check (media_type in ('image', 'video')),
  storage_path text not null,
  sort_order integer not null default 0,
  created_at timestamptz not null default now()
);

create table if not exists public.comments (
  id uuid primary key default gen_random_uuid(),
  target_type text not null check (target_type in ('event', 'post', 'user_review')),
  target_id uuid not null,
  author_id uuid not null references public.profiles(id) on delete cascade,
  body text not null,
  moderation_status text not null default 'visible' check (moderation_status in ('visible', 'hidden', 'flagged')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.event_ratings (
  id uuid primary key default gen_random_uuid(),
  event_id uuid not null references public.events(id) on delete cascade,
  user_id uuid not null references public.profiles(id) on delete cascade,
  rating smallint not null check (rating between 1 and 5),
  review_text text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (event_id, user_id)
);

create table if not exists public.user_reviews (
  id uuid primary key default gen_random_uuid(),
  reviewer_id uuid not null references public.profiles(id) on delete cascade,
  reviewed_user_id uuid not null references public.profiles(id) on delete cascade,
  event_id uuid references public.events(id) on delete set null,
  rating smallint not null check (rating between 1 and 5),
  review_text text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (reviewer_id, reviewed_user_id, event_id)
);

create table if not exists public.notifications (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  type text not null,
  title text not null,
  body text,
  payload jsonb not null default '{}'::jsonb,
  is_read boolean not null default false,
  created_at timestamptz not null default now()
);

create table if not exists public.reports (
  id uuid primary key default gen_random_uuid(),
  reporter_id uuid not null references public.profiles(id) on delete cascade,
  target_type text not null check (target_type in ('post', 'comment', 'event', 'user')),
  target_id uuid not null,
  reason text not null,
  details text,
  status text not null default 'open' check (status in ('open', 'under_review', 'resolved', 'dismissed')),
  resolved_by uuid references public.profiles(id) on delete set null,
  resolved_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.blocks (
  id uuid primary key default gen_random_uuid(),
  blocker_id uuid not null references public.profiles(id) on delete cascade,
  blocked_user_id uuid not null references public.profiles(id) on delete cascade,
  created_at timestamptz not null default now(),
  unique (blocker_id, blocked_user_id),
  check (blocker_id <> blocked_user_id)
);

create index if not exists idx_profiles_username on public.profiles(username);
create index if not exists idx_communities_parent_id on public.communities(parent_id);
create index if not exists idx_events_community_id_starts_at on public.events(community_id, starts_at);
create index if not exists idx_events_status_starts_at on public.events(status, starts_at);
create index if not exists idx_event_participants_event_id_status on public.event_participants(event_id, status);
create index if not exists idx_posts_community_created_at on public.posts(community_id, created_at desc);
create index if not exists idx_posts_event_created_at on public.posts(event_id, created_at desc);
create index if not exists idx_comments_target on public.comments(target_type, target_id);
create index if not exists idx_notifications_user_id_is_read on public.notifications(user_id, is_read);
create index if not exists idx_reports_status_created_at on public.reports(status, created_at desc);

create or replace function public.user_has_role(target_user_id uuid, target_role text)
returns boolean
language sql
stable
as $$
  select exists (
    select 1
    from public.user_roles ur
    join public.roles r on r.id = ur.role_id
    where ur.user_id = target_user_id
      and r.code = target_role
  );
$$;

create or replace function public.can_rate_event(target_event_id uuid, target_user_id uuid)
returns boolean
language sql
stable
as $$
  select exists (
    select 1
    from public.event_participants ep
    where ep.event_id = target_event_id
      and ep.user_id = target_user_id
      and ep.status in ('approved', 'checked_in')
  );
$$;

create or replace function public.prevent_invalid_event_rating()
returns trigger
language plpgsql
as $$
begin
  if not public.can_rate_event(new.event_id, new.user_id) then
    raise exception 'User is not eligible to rate this event';
  end if;

  return new;
end;
$$;

drop trigger if exists trg_event_ratings_validate on public.event_ratings;
create trigger trg_event_ratings_validate
before insert or update on public.event_ratings
for each row
execute function public.prevent_invalid_event_rating();

drop trigger if exists trg_profiles_updated_at on public.profiles;
create trigger trg_profiles_updated_at
before update on public.profiles
for each row
execute function public.set_updated_at();

drop trigger if exists trg_communities_updated_at on public.communities;
create trigger trg_communities_updated_at
before update on public.communities
for each row
execute function public.set_updated_at();

drop trigger if exists trg_events_updated_at on public.events;
create trigger trg_events_updated_at
before update on public.events
for each row
execute function public.set_updated_at();

drop trigger if exists trg_event_participants_updated_at on public.event_participants;
create trigger trg_event_participants_updated_at
before update on public.event_participants
for each row
execute function public.set_updated_at();

drop trigger if exists trg_posts_updated_at on public.posts;
create trigger trg_posts_updated_at
before update on public.posts
for each row
execute function public.set_updated_at();

drop trigger if exists trg_comments_updated_at on public.comments;
create trigger trg_comments_updated_at
before update on public.comments
for each row
execute function public.set_updated_at();

drop trigger if exists trg_event_ratings_updated_at on public.event_ratings;
create trigger trg_event_ratings_updated_at
before update on public.event_ratings
for each row
execute function public.set_updated_at();

drop trigger if exists trg_user_reviews_updated_at on public.user_reviews;
create trigger trg_user_reviews_updated_at
before update on public.user_reviews
for each row
execute function public.set_updated_at();

drop trigger if exists trg_reports_updated_at on public.reports;
create trigger trg_reports_updated_at
before update on public.reports
for each row
execute function public.set_updated_at();

insert into public.roles (code, name)
values
  ('member', 'Member'),
  ('organizer', 'Organizer'),
  ('moderator', 'Moderator'),
  ('admin', 'Admin')
on conflict (code) do nothing;
