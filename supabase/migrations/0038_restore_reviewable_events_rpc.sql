create or replace function public.get_reviewable_events(target_user_id uuid)
returns table (
  event_id uuid,
  title text,
  starts_at timestamptz
)
language sql
stable
security definer
set search_path = public
as $$
  select distinct
    event.id as event_id,
    event.title,
    event.starts_at
  from public.events event
  join public.event_participants current_participant
    on current_participant.event_id = event.id
  join public.event_participants target_participant
    on target_participant.event_id = event.id
  where current_participant.user_id = auth.uid()
    and target_participant.user_id = target_user_id
    and current_participant.status in ('approved', 'checked_in')
    and target_participant.status in ('approved', 'checked_in')
  order by event.starts_at desc;
$$;

revoke all on function public.get_reviewable_events(uuid) from public;
grant execute on function public.get_reviewable_events(uuid) to authenticated;

notify pgrst, 'reload schema';
