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
  select participant.id, participant.user_id
  into promoted_participant_id, promoted_user_id
  from public.event_participants participant
  where participant.event_id = target_event_id
    and participant.status = 'waitlisted'
  order by participant.created_at, participant.id
  limit 1
  for update skip locked;

  if promoted_participant_id is null then
    return null;
  end if;

  update public.event_participants
  set status = 'pending', updated_at = now()
  where id = promoted_participant_id;

  select event.title into event_title
  from public.events event
  where event.id = target_event_id;

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
  if current_user_id is null then
    raise exception 'Authentication required';
  end if;

  -- Serialize capacity changes with new participation requests.
  perform 1
  from public.events event
  where event.id = target_event_id
  for update;

  if not found then
    raise exception 'Event not found';
  end if;

  select participant.status
  into previous_status
  from public.event_participants participant
  where participant.event_id = target_event_id
    and participant.user_id = current_user_id
  for update;

  if previous_status is null
    or previous_status not in ('pending', 'waitlisted', 'approved') then
    raise exception 'Active participation not found';
  end if;

  update public.event_participants
  set status = 'cancelled', updated_at = now()
  where event_id = target_event_id
    and user_id = current_user_id;

  if previous_status in ('pending', 'approved') then
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
      join public.event_participants mine
        on mine.event_id = queue.event_id
       and mine.user_id = auth.uid()
       and mine.status = 'waitlisted'
      where queue.event_id = target_event_id
        and queue.status = 'waitlisted'
        and (
          queue.created_at < mine.created_at
          or (queue.created_at = mine.created_at and queue.id <= mine.id)
        )
    ),
    public.can_manage_event_participants(target_event_id, auth.uid())
  from public.event_participants participant
  where participant.event_id = target_event_id;
$$;

revoke all on function public.promote_event_waitlist(uuid) from public;
revoke all on function public.cancel_event_participation(uuid) from public;
revoke all on function public.get_event_participation_summary(uuid) from public;
grant execute on function public.cancel_event_participation(uuid) to authenticated;
grant execute on function public.get_event_participation_summary(uuid) to authenticated;

notify pgrst, 'reload schema';
