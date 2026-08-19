alter table public.event_participants
drop constraint if exists event_participants_status_check;

alter table public.event_participants
add constraint event_participants_status_check
check (status in ('pending', 'waitlisted', 'approved', 'rejected', 'cancelled', 'checked_in', 'no_show'));

alter table public.reports
drop constraint if exists reports_target_type_check;

alter table public.reports
add constraint reports_target_type_check
check (target_type in ('post', 'comment', 'event', 'user', 'event_message'));

create table if not exists public.event_messages (
  id uuid primary key default gen_random_uuid(),
  event_id uuid not null references public.events(id) on delete cascade,
  author_id uuid not null references public.profiles(id) on delete cascade,
  body text not null check (char_length(trim(body)) between 1 and 1000),
  moderation_status text not null default 'visible' check (moderation_status in ('visible', 'hidden', 'flagged')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index if not exists idx_event_messages_event_created
on public.event_messages(event_id, created_at);

alter table public.event_messages enable row level security;

create or replace function public.can_access_event_chat(target_event_id uuid, target_user_id uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.events e
    where e.id = target_event_id
      and (
        e.created_by = target_user_id
        or public.is_admin()
        or public.is_community_manager(e.community_id, target_user_id)
        or exists (
          select 1
          from public.event_participants ep
          where ep.event_id = e.id
            and ep.user_id = target_user_id
            and ep.status in ('approved', 'checked_in')
        )
      )
  );
$$;

drop policy if exists event_messages_read_participants on public.event_messages;
create policy event_messages_read_participants
on public.event_messages
for select
to authenticated
using (
  public.can_access_event_chat(event_id, auth.uid())
  and (moderation_status = 'visible' or author_id = auth.uid() or public.is_moderator_or_admin())
);

drop policy if exists event_messages_create_participants on public.event_messages;
create policy event_messages_create_participants
on public.event_messages
for insert
to authenticated
with check (
  author_id = auth.uid()
  and public.can_access_event_chat(event_id, auth.uid())
);

drop policy if exists event_messages_update_own_or_mod on public.event_messages;
create policy event_messages_update_own_or_mod
on public.event_messages
for update
to authenticated
using (author_id = auth.uid() or public.is_moderator_or_admin())
with check (author_id = auth.uid() or public.is_moderator_or_admin());

drop trigger if exists trg_event_messages_updated_at on public.event_messages;
create trigger trg_event_messages_updated_at
before update on public.event_messages
for each row execute function public.set_updated_at();

create or replace function public.request_event_participation(target_event_id uuid)
returns text
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  event_capacity integer;
  target_community_id uuid;
  reserved_count integer;
  next_status text;
  existing_status text;
begin
  if current_user_id is null then
    raise exception 'Authentication required';
  end if;

  select e.capacity, e.community_id into event_capacity, target_community_id
  from public.events e
  where e.id = target_event_id and e.status = 'published'
  for update;

  if not found then
    raise exception 'Published event not found';
  end if;

  if not public.is_approved_community_member(target_community_id, current_user_id) then
    raise exception 'Join the event group first';
  end if;

  select ep.status into existing_status
  from public.event_participants ep
  where ep.event_id = target_event_id and ep.user_id = current_user_id;

  if existing_status in ('pending', 'waitlisted', 'approved', 'checked_in') then
    return existing_status;
  end if;

  select count(*) into reserved_count
  from public.event_participants ep
  where ep.event_id = target_event_id
    and ep.status in ('pending', 'approved', 'checked_in');

  next_status := case
    when event_capacity is not null and reserved_count >= event_capacity then 'waitlisted'
    else 'pending'
  end;

  insert into public.event_participants (event_id, user_id, status)
  values (target_event_id, current_user_id, next_status)
  on conflict (event_id, user_id) do update
  set status = excluded.status, updated_at = now();

  return next_status;
end;
$$;

create or replace function public.promote_event_waitlist(target_event_id uuid)
returns uuid
language plpgsql
security definer
set search_path = public
as $$
declare
  promoted_participant_id uuid;
  promoted_user_id uuid;
  event_title text;
begin
  select ep.id, ep.user_id
  into promoted_participant_id, promoted_user_id
  from public.event_participants ep
  where ep.event_id = target_event_id and ep.status = 'waitlisted'
  order by ep.created_at asc
  limit 1
  for update skip locked;

  if promoted_participant_id is null then
    return null;
  end if;

  update public.event_participants
  set status = 'pending', updated_at = now()
  where id = promoted_participant_id;

  select title into event_title from public.events where id = target_event_id;
  insert into public.notifications (user_id, type, title, body, payload)
  values (
    promoted_user_id,
    'event_waitlist_promoted',
    'Etkinlik sırası sana geldi',
    coalesce(event_title, 'Etkinlik') || ' için katılım talebin değerlendirmeye alındı.',
    jsonb_build_object('event_id', target_event_id)
  );

  return promoted_participant_id;
end;
$$;

create or replace function public.cancel_event_participation(target_event_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  previous_status text;
begin
  select ep.status into previous_status
  from public.event_participants ep
  where ep.event_id = target_event_id and ep.user_id = current_user_id;

  update public.event_participants
  set status = 'cancelled', updated_at = now()
  where event_id = target_event_id and user_id = current_user_id
    and status in ('pending', 'waitlisted', 'approved');

  if not found then
    raise exception 'Participation not found';
  end if;

  if previous_status in ('pending', 'approved') then
    perform public.promote_event_waitlist(target_event_id);
  end if;
end;
$$;

create or replace function public.review_event_participant(target_participant_id uuid, target_status text)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  target_event_id uuid;
  target_user_id uuid;
  target_community_id uuid;
  event_title text;
begin
  if target_status not in ('approved', 'rejected') then
    raise exception 'Invalid participant status';
  end if;

  select ep.event_id, ep.user_id, e.community_id, e.title
  into target_event_id, target_user_id, target_community_id, event_title
  from public.event_participants ep
  join public.events e on e.id = ep.event_id
  where ep.id = target_participant_id and ep.status = 'pending';

  if target_event_id is null then
    raise exception 'Pending participant not found';
  end if;

  if not exists (select 1 from public.events e where e.id = target_event_id and e.created_by = current_user_id)
    and not public.is_admin()
    and not public.is_community_manager(target_community_id, current_user_id) then
    raise exception 'Not authorized to review participants';
  end if;

  update public.event_participants
  set status = target_status, updated_at = now()
  where id = target_participant_id;

  insert into public.notifications (user_id, type, title, body, payload)
  values (
    target_user_id,
    'event_participation_' || target_status,
    case when target_status = 'approved' then 'Katılımın onaylandı' else 'Katılım talebin reddedildi' end,
    event_title,
    jsonb_build_object('event_id', target_event_id)
  );

  if target_status = 'rejected' then
    perform public.promote_event_waitlist(target_event_id);
  end if;
end;
$$;

create or replace function public.get_event_participation_summary(target_event_id uuid)
returns table (
  pending_count bigint,
  approved_count bigint,
  waitlisted_count bigint,
  checked_in_count bigint,
  my_status text,
  my_waitlist_position bigint,
  can_manage boolean
)
language sql
stable
security definer
set search_path = public
as $$
  select
    count(*) filter (where ep.status = 'pending'),
    count(*) filter (where ep.status = 'approved'),
    count(*) filter (where ep.status = 'waitlisted'),
    count(*) filter (where ep.status = 'checked_in'),
    max(ep.status) filter (where ep.user_id = auth.uid()),
    (
      select count(*)
      from public.event_participants queue
      where queue.event_id = target_event_id
        and queue.status = 'waitlisted'
        and queue.created_at <= coalesce((
          select mine.created_at from public.event_participants mine
          where mine.event_id = target_event_id and mine.user_id = auth.uid() and mine.status = 'waitlisted'
        ), '-infinity'::timestamptz)
    ),
    exists (
      select 1 from public.events e
      where e.id = target_event_id
        and (e.created_by = auth.uid() or public.is_admin() or public.is_community_manager(e.community_id, auth.uid()))
    )
  from public.event_participants ep
  where ep.event_id = target_event_id;
$$;

create or replace function public.get_event_participant_roster(target_event_id uuid)
returns table (
  participant_id uuid,
  user_id uuid,
  display_name text,
  avatar_url text,
  status text,
  created_at timestamptz
)
language plpgsql
stable
security definer
set search_path = public
as $$
begin
  if not exists (
    select 1 from public.events e
    where e.id = target_event_id
      and (e.created_by = auth.uid() or public.is_admin() or public.is_community_manager(e.community_id, auth.uid()))
  ) then
    raise exception 'Not authorized to view roster';
  end if;

  return query
  select ep.id, ep.user_id, p.display_name, p.avatar_url, ep.status, ep.created_at
  from public.event_participants ep
  join public.profiles p on p.id = ep.user_id
  where ep.event_id = target_event_id
  order by
    case ep.status when 'pending' then 0 when 'waitlisted' then 1 when 'approved' then 2 else 3 end,
    ep.created_at;
end;
$$;

create or replace function public.check_in_event_participant(target_event_id uuid, target_user_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  target_community_id uuid;
begin
  select community_id into target_community_id from public.events where id = target_event_id;
  if target_community_id is null then raise exception 'Event not found'; end if;

  if not exists (select 1 from public.events e where e.id = target_event_id and e.created_by = auth.uid())
    and not public.is_admin()
    and not public.is_community_manager(target_community_id, auth.uid()) then
    raise exception 'Not authorized to check in participants';
  end if;

  update public.event_participants
  set status = 'checked_in', updated_at = now()
  where event_id = target_event_id and user_id = target_user_id and status in ('approved', 'checked_in');

  if not found then raise exception 'Approved participant not found'; end if;
end;
$$;

create or replace function public.mark_event_participant_no_show(target_event_id uuid, target_user_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  target_community_id uuid;
begin
  select community_id into target_community_id from public.events where id = target_event_id;
  if not exists (select 1 from public.events e where e.id = target_event_id and e.created_by = auth.uid())
    and not public.is_admin()
    and not public.is_community_manager(target_community_id, auth.uid()) then
    raise exception 'Not authorized to update attendance';
  end if;

  update public.event_participants
  set status = 'no_show', updated_at = now()
  where event_id = target_event_id and user_id = target_user_id and status = 'approved';
end;
$$;

create or replace function public.get_user_reliability(target_user_id uuid)
returns table (checked_in_count bigint, no_show_count bigint, reliability_score integer)
language sql
stable
security definer
set search_path = public
as $$
  select
    count(*) filter (where status = 'checked_in'),
    count(*) filter (where status = 'no_show'),
    case
      when count(*) filter (where status in ('checked_in', 'no_show')) = 0 then null
      else round(
        100.0 * count(*) filter (where status = 'checked_in')
        / count(*) filter (where status in ('checked_in', 'no_show'))
      )::integer
    end
  from public.event_participants
  where user_id = target_user_id;
$$;

create or replace function public.get_event_chat_messages(target_event_id uuid)
returns table (
  message_id uuid,
  author_id uuid,
  display_name text,
  avatar_url text,
  body text,
  created_at timestamptz
)
language plpgsql
stable
security definer
set search_path = public
as $$
begin
  if not public.can_access_event_chat(target_event_id, auth.uid()) then
    raise exception 'Event chat access denied';
  end if;

  return query
  select em.id, em.author_id, p.display_name, p.avatar_url, em.body, em.created_at
  from public.event_messages em
  join public.profiles p on p.id = em.author_id
  where em.event_id = target_event_id
    and (em.moderation_status = 'visible' or em.author_id = auth.uid() or public.is_moderator_or_admin())
  order by em.created_at asc
  limit 250;
end;
$$;

revoke all on function public.request_event_participation(uuid) from public;
revoke all on function public.promote_event_waitlist(uuid) from public;
revoke all on function public.can_access_event_chat(uuid, uuid) from public;
revoke all on function public.cancel_event_participation(uuid) from public;
revoke all on function public.review_event_participant(uuid, text) from public;
revoke all on function public.get_event_participation_summary(uuid) from public;
revoke all on function public.get_event_participant_roster(uuid) from public;
revoke all on function public.check_in_event_participant(uuid, uuid) from public;
revoke all on function public.mark_event_participant_no_show(uuid, uuid) from public;
revoke all on function public.get_user_reliability(uuid) from public;
revoke all on function public.get_event_chat_messages(uuid) from public;
grant execute on function public.request_event_participation(uuid) to authenticated;
grant execute on function public.can_access_event_chat(uuid, uuid) to authenticated;
grant execute on function public.cancel_event_participation(uuid) to authenticated;
grant execute on function public.review_event_participant(uuid, text) to authenticated;
grant execute on function public.get_event_participation_summary(uuid) to authenticated;
grant execute on function public.get_event_participant_roster(uuid) to authenticated;
grant execute on function public.check_in_event_participant(uuid, uuid) to authenticated;
grant execute on function public.mark_event_participant_no_show(uuid, uuid) to authenticated;
grant execute on function public.get_user_reliability(uuid) to authenticated;
grant execute on function public.get_event_chat_messages(uuid) to authenticated;

do $$
begin
  if not exists (
    select 1 from pg_publication_tables
    where pubname = 'supabase_realtime' and schemaname = 'public' and tablename = 'event_messages'
  ) then
    alter publication supabase_realtime add table public.event_messages;
  end if;
end;
$$;
