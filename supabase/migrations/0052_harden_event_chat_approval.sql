create or replace function public.can_manage_event_participants(
  target_event_id uuid,
  target_user_id uuid
)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.events event
    where event.id = target_event_id
      and target_user_id is not null
      and (
        (target_user_id = auth.uid() and public.is_admin())
        or public.is_community_manager(event.community_id, target_user_id)
        or public.has_community_assistant_permission(
          event.community_id,
          target_user_id,
          'manage_participants'
        )
      )
  );
$$;

create or replace function public.can_access_event_chat(
  target_event_id uuid,
  target_user_id uuid
)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select
    public.can_manage_event_participants(target_event_id, target_user_id)
    or exists (
      select 1
      from public.event_participants participant
      where participant.event_id = target_event_id
        and participant.user_id = target_user_id
        and participant.status in ('approved', 'checked_in')
    );
$$;

drop policy if exists event_messages_read_participants on public.event_messages;
create policy event_messages_read_participants
on public.event_messages
for select
to authenticated
using (
  public.can_access_event_chat(event_id, auth.uid())
  and (
    moderation_status = 'visible'
    or author_id = auth.uid()
    or public.is_moderator_or_admin()
  )
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
using (
  (author_id = auth.uid() and public.can_access_event_chat(event_id, auth.uid()))
  or public.is_moderator_or_admin()
)
with check (
  (author_id = auth.uid() and public.can_access_event_chat(event_id, auth.uid()))
  or public.is_moderator_or_admin()
);

create or replace function public.review_event_participant(
  target_participant_id uuid,
  target_status text
)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  target_event_id uuid;
  target_user_id uuid;
  event_title text;
begin
  if target_status not in ('approved', 'rejected') then
    raise exception 'Invalid participant status';
  end if;

  select participant.event_id, participant.user_id, event.title
  into target_event_id, target_user_id, event_title
  from public.event_participants participant
  join public.events event on event.id = participant.event_id
  where participant.id = target_participant_id
    and participant.status = 'pending';

  if target_event_id is null then
    raise exception 'Pending participant not found';
  end if;

  if not public.can_manage_event_participants(target_event_id, current_user_id) then
    raise exception 'Not authorized to review participants';
  end if;

  update public.event_participants
  set status = target_status, updated_at = now()
  where id = target_participant_id;

  insert into public.notifications (user_id, type, title, body, payload)
  values (
    target_user_id,
    'event_participation_' || target_status,
    case
      when target_status = 'approved' then 'Katılımın onaylandı'
      else 'Katılım talebin reddedildi'
    end,
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
    count(*) filter (where participant.status = 'pending'),
    count(*) filter (where participant.status = 'approved'),
    count(*) filter (where participant.status = 'waitlisted'),
    count(*) filter (where participant.status = 'checked_in'),
    max(participant.status) filter (where participant.user_id = auth.uid()),
    (
      select count(*)
      from public.event_participants queue
      where queue.event_id = target_event_id
        and queue.status = 'waitlisted'
        and queue.created_at <= coalesce((
          select mine.created_at
          from public.event_participants mine
          where mine.event_id = target_event_id
            and mine.user_id = auth.uid()
            and mine.status = 'waitlisted'
        ), '-infinity'::timestamptz)
    ),
    public.can_manage_event_participants(target_event_id, auth.uid())
  from public.event_participants participant
  where participant.event_id = target_event_id;
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
  if not public.can_manage_event_participants(target_event_id, auth.uid()) then
    raise exception 'Not authorized to view roster';
  end if;

  return query
  select
    participant.id,
    participant.user_id,
    profile.display_name,
    profile.avatar_url,
    participant.status,
    participant.created_at
  from public.event_participants participant
  join public.profiles profile on profile.id = participant.user_id
  where participant.event_id = target_event_id
  order by
    case participant.status
      when 'pending' then 0
      when 'waitlisted' then 1
      when 'approved' then 2
      else 3
    end,
    participant.created_at;
end;
$$;

create or replace function public.check_in_event_participant(
  target_event_id uuid,
  target_user_id uuid
)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
  if not exists (select 1 from public.events where id = target_event_id) then
    raise exception 'Event not found';
  end if;

  if not public.can_manage_event_participants(target_event_id, auth.uid()) then
    raise exception 'Not authorized to check in participants';
  end if;

  update public.event_participants
  set status = 'checked_in', updated_at = now()
  where event_id = target_event_id
    and user_id = target_user_id
    and status in ('approved', 'checked_in');

  if not found then
    raise exception 'Approved participant not found';
  end if;
end;
$$;

create or replace function public.mark_event_participant_no_show(
  target_event_id uuid,
  target_user_id uuid
)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
  if not exists (select 1 from public.events where id = target_event_id) then
    raise exception 'Event not found';
  end if;

  if not public.can_manage_event_participants(target_event_id, auth.uid()) then
    raise exception 'Not authorized to update attendance';
  end if;

  update public.event_participants
  set status = 'no_show', updated_at = now()
  where event_id = target_event_id
    and user_id = target_user_id
    and status = 'approved';
end;
$$;

revoke all on function public.can_manage_event_participants(uuid, uuid) from public;
revoke all on function public.can_access_event_chat(uuid, uuid) from public;
grant execute on function public.can_access_event_chat(uuid, uuid) to authenticated;

notify pgrst, 'reload schema';
