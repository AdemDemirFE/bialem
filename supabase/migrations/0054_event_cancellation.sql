alter table public.events
add column if not exists cancelled_at timestamptz,
add column if not exists cancelled_by uuid references public.profiles(id) on delete set null,
add column if not exists cancellation_reason text;

create or replace function public.cancel_event(
  target_event_id uuid,
  target_reason text default null
)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  target_community_id uuid;
  target_creator_id uuid;
  target_title text;
  current_status text;
  normalized_reason text;
begin
  if current_user_id is null then raise exception 'Authentication required'; end if;

  select event.community_id, event.created_by, event.title, event.status
  into target_community_id, target_creator_id, target_title, current_status
  from public.events event
  where event.id = target_event_id
  for update;

  if target_community_id is null then raise exception 'Event not found'; end if;

  if current_user_id <> target_creator_id
    and not public.is_admin()
    and not public.is_community_manager(target_community_id, current_user_id)
    and not public.has_community_assistant_permission(target_community_id, current_user_id, 'review_events') then
    raise exception 'Not authorized to cancel this event';
  end if;

  if current_status = 'cancelled' then raise exception 'Event is already cancelled'; end if;
  if current_status not in ('pending_approval', 'published') then
    raise exception 'Only pending or published events can be cancelled';
  end if;

  normalized_reason := coalesce(
    nullif(trim(target_reason), ''),
    'Organizatör tarafından iptal edildi.'
  );

  insert into public.notifications (user_id, type, title, body, payload)
  select distinct
    participant.user_id,
    'event_cancelled',
    'Etkinlik iptal edildi',
    target_title || ': ' || normalized_reason,
    jsonb_build_object(
      'event_id', target_event_id,
      'reason', normalized_reason
    )
  from public.event_participants participant
  where participant.event_id = target_event_id
    and participant.status in ('pending', 'waitlisted', 'approved')
    and participant.user_id <> current_user_id;

  update public.event_participants
  set status = 'cancelled', updated_at = now()
  where event_id = target_event_id
    and status in ('pending', 'waitlisted', 'approved');

  update public.events
  set
    status = 'cancelled',
    cancelled_at = now(),
    cancelled_by = current_user_id,
    cancellation_reason = normalized_reason,
    published_at = null
  where id = target_event_id;
end;
$$;

revoke all on function public.cancel_event(uuid, text) from public;
grant execute on function public.cancel_event(uuid, text) to authenticated;

notify pgrst, 'reload schema';
