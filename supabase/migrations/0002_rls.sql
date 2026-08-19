alter table public.profiles enable row level security;
alter table public.user_roles enable row level security;
alter table public.communities enable row level security;
alter table public.community_members enable row level security;
alter table public.events enable row level security;
alter table public.event_participants enable row level security;
alter table public.posts enable row level security;
alter table public.post_media enable row level security;
alter table public.comments enable row level security;
alter table public.event_ratings enable row level security;
alter table public.user_reviews enable row level security;
alter table public.notifications enable row level security;
alter table public.reports enable row level security;
alter table public.blocks enable row level security;

create or replace function public.is_admin()
returns boolean
language sql
stable
as $$
  select public.user_has_role(auth.uid(), 'admin');
$$;

create or replace function public.is_moderator_or_admin()
returns boolean
language sql
stable
as $$
  select public.user_has_role(auth.uid(), 'moderator')
      or public.user_has_role(auth.uid(), 'admin');
$$;

create or replace function public.is_approved_community_member(target_community_id uuid, target_user_id uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.community_members cm
    where cm.community_id = target_community_id
      and cm.user_id = target_user_id
      and cm.status = 'approved'
  );
$$;

create or replace function public.can_view_community_basic(target_community_id uuid, target_user_id uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.communities c
    where c.id = target_community_id
      and (
        c.visibility = 'public'
        or c.created_by = target_user_id
        or public.user_has_role(target_user_id, 'admin')
        or public.is_approved_community_member(c.id, target_user_id)
      )
  );
$$;

drop policy if exists profiles_select_self_or_admin on public.profiles;
create policy profiles_select_self_or_admin
on public.profiles
for select
using (auth.uid() = id or public.is_admin());

drop policy if exists profiles_update_self_or_admin on public.profiles;
create policy profiles_update_self_or_admin
on public.profiles
for update
using (auth.uid() = id or public.is_admin())
with check (auth.uid() = id or public.is_admin());

drop policy if exists profiles_insert_self on public.profiles;
create policy profiles_insert_self
on public.profiles
for insert
with check (auth.uid() = id);

drop policy if exists user_roles_select_self_or_admin on public.user_roles;
create policy user_roles_select_self_or_admin
on public.user_roles
for select
using (user_id = auth.uid() or public.is_admin());

drop policy if exists user_roles_manage_admin on public.user_roles;
create policy user_roles_manage_admin
on public.user_roles
for all
using (public.is_admin())
with check (public.is_admin());

drop policy if exists communities_public_read on public.communities;
create policy communities_public_read
on public.communities
for select
using (
  visibility = 'public'
  or created_by = auth.uid()
  or public.is_admin()
  or public.is_approved_community_member(id, auth.uid())
);

drop policy if exists communities_create_authenticated on public.communities;
create policy communities_create_authenticated
on public.communities
for insert
with check (auth.uid() = created_by);

drop policy if exists communities_update_owner_or_admin on public.communities;
create policy communities_update_owner_or_admin
on public.communities
for update
using (created_by = auth.uid() or public.is_admin())
with check (created_by = auth.uid() or public.is_admin());

drop policy if exists community_members_read_related_or_admin on public.community_members;
create policy community_members_read_related_or_admin
on public.community_members
for select
using (
  user_id = auth.uid()
  or public.is_admin()
  or public.can_view_community_basic(community_id, auth.uid())
);

drop policy if exists community_members_join_self on public.community_members;
create policy community_members_join_self
on public.community_members
for insert
with check (user_id = auth.uid() or public.is_admin());

drop policy if exists community_members_update_self_or_admin on public.community_members;
create policy community_members_update_self_or_admin
on public.community_members
for update
using (user_id = auth.uid() or public.is_admin())
with check (user_id = auth.uid() or public.is_admin());

drop policy if exists events_read_visible on public.events;
create policy events_read_visible
on public.events
for select
using (
  status = 'published'
  or created_by = auth.uid()
  or public.is_admin()
  or public.is_moderator_or_admin()
);

drop policy if exists events_create_own on public.events;
create policy events_create_own
on public.events
for insert
with check (created_by = auth.uid());

drop policy if exists events_update_owner_or_admin on public.events;
create policy events_update_owner_or_admin
on public.events
for update
using (created_by = auth.uid() or public.is_admin())
with check (
  (created_by = auth.uid() and status in ('draft', 'pending_approval', 'cancelled'))
  or public.is_admin()
);

drop policy if exists event_participants_read_self_owner_admin on public.event_participants;
create policy event_participants_read_self_owner_admin
on public.event_participants
for select
using (
  user_id = auth.uid()
  or public.is_admin()
  or exists (
    select 1
    from public.events e
    where e.id = event_participants.event_id
      and e.created_by = auth.uid()
  )
);

drop policy if exists event_participants_insert_self on public.event_participants;
create policy event_participants_insert_self
on public.event_participants
for insert
with check (user_id = auth.uid());

drop policy if exists event_participants_update_self_owner_admin on public.event_participants;
create policy event_participants_update_self_owner_admin
on public.event_participants
for update
using (
  user_id = auth.uid()
  or public.is_admin()
  or exists (
    select 1
    from public.events e
    where e.id = event_participants.event_id
      and e.created_by = auth.uid()
  )
)
with check (
  user_id = auth.uid()
  or public.is_admin()
  or exists (
    select 1
    from public.events e
    where e.id = event_participants.event_id
      and e.created_by = auth.uid()
  )
);

drop policy if exists posts_read_related_visibility on public.posts;
create policy posts_read_related_visibility
on public.posts
for select
using (
  moderation_status = 'visible'
  and (
    visibility = 'public'
    or author_id = auth.uid()
    or public.is_admin()
  )
);

drop policy if exists posts_create_own on public.posts;
create policy posts_create_own
on public.posts
for insert
with check (author_id = auth.uid());

drop policy if exists posts_update_own_or_admin on public.posts;
create policy posts_update_own_or_admin
on public.posts
for update
using (author_id = auth.uid() or public.is_moderator_or_admin())
with check (author_id = auth.uid() or public.is_moderator_or_admin());

drop policy if exists post_media_read_via_post_access on public.post_media;
create policy post_media_read_via_post_access
on public.post_media
for select
using (
  exists (
    select 1
    from public.posts p
    where p.id = post_media.post_id
      and (
        p.author_id = auth.uid()
        or p.visibility = 'public'
        or public.is_admin()
      )
  )
);

drop policy if exists post_media_create_via_post_owner on public.post_media;
create policy post_media_create_via_post_owner
on public.post_media
for insert
with check (
  exists (
    select 1
    from public.posts p
    where p.id = post_media.post_id
      and p.author_id = auth.uid()
  )
);

drop policy if exists comments_read_visible on public.comments;
create policy comments_read_visible
on public.comments
for select
using (moderation_status = 'visible' or public.is_moderator_or_admin() or author_id = auth.uid());

drop policy if exists comments_create_own on public.comments;
create policy comments_create_own
on public.comments
for insert
with check (author_id = auth.uid());

drop policy if exists comments_update_own_or_mod on public.comments;
create policy comments_update_own_or_mod
on public.comments
for update
using (author_id = auth.uid() or public.is_moderator_or_admin())
with check (author_id = auth.uid() or public.is_moderator_or_admin());

drop policy if exists event_ratings_read_all on public.event_ratings;
create policy event_ratings_read_all
on public.event_ratings
for select
using (true);

drop policy if exists event_ratings_create_self on public.event_ratings;
create policy event_ratings_create_self
on public.event_ratings
for insert
with check (user_id = auth.uid());

drop policy if exists event_ratings_update_self_or_admin on public.event_ratings;
create policy event_ratings_update_self_or_admin
on public.event_ratings
for update
using (user_id = auth.uid() or public.is_admin())
with check (user_id = auth.uid() or public.is_admin());

drop policy if exists user_reviews_read_all on public.user_reviews;
create policy user_reviews_read_all
on public.user_reviews
for select
using (true);

drop policy if exists user_reviews_create_self on public.user_reviews;
create policy user_reviews_create_self
on public.user_reviews
for insert
with check (reviewer_id = auth.uid());

drop policy if exists user_reviews_update_self_or_admin on public.user_reviews;
create policy user_reviews_update_self_or_admin
on public.user_reviews
for update
using (reviewer_id = auth.uid() or public.is_admin())
with check (reviewer_id = auth.uid() or public.is_admin());

drop policy if exists notifications_read_own on public.notifications;
create policy notifications_read_own
on public.notifications
for select
using (user_id = auth.uid() or public.is_admin());

drop policy if exists notifications_update_own on public.notifications;
create policy notifications_update_own
on public.notifications
for update
using (user_id = auth.uid() or public.is_admin())
with check (user_id = auth.uid() or public.is_admin());

drop policy if exists reports_create_self on public.reports;
create policy reports_create_self
on public.reports
for insert
with check (reporter_id = auth.uid());

drop policy if exists reports_read_self_or_mod on public.reports;
create policy reports_read_self_or_mod
on public.reports
for select
using (reporter_id = auth.uid() or public.is_moderator_or_admin());

drop policy if exists reports_update_mod on public.reports;
create policy reports_update_mod
on public.reports
for update
using (public.is_moderator_or_admin())
with check (public.is_moderator_or_admin());

drop policy if exists blocks_read_own on public.blocks;
create policy blocks_read_own
on public.blocks
for select
using (blocker_id = auth.uid() or public.is_admin());

drop policy if exists blocks_create_own on public.blocks;
create policy blocks_create_own
on public.blocks
for insert
with check (blocker_id = auth.uid());

drop policy if exists blocks_delete_own on public.blocks;
create policy blocks_delete_own
on public.blocks
for delete
using (blocker_id = auth.uid() or public.is_admin());
