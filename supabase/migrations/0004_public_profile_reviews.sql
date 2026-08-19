create or replace function public.get_public_profile_card(target_user_id uuid)
returns table (
  id uuid,
  display_name text,
  username text,
  avatar_url text,
  bio text,
  city text,
  status text,
  is_verified boolean,
  created_at timestamptz
)
language sql
stable
security definer
set search_path = public
as $$
  select
    p.id,
    p.display_name,
    p.username,
    p.avatar_url,
    p.bio,
    p.city,
    p.status,
    p.is_verified,
    p.created_at
  from public.profiles p
  where p.id = target_user_id
    and p.status <> 'deleted';
$$;

create or replace function public.can_review_user(target_reviewer_id uuid, target_reviewed_user_id uuid, target_event_id uuid default null)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select
    target_reviewer_id is not null
    and target_reviewed_user_id is not null
    and target_reviewer_id <> target_reviewed_user_id
    and exists (
      select 1
      from public.event_participants reviewer_ep
      join public.event_participants reviewed_ep
        on reviewed_ep.event_id = reviewer_ep.event_id
      where reviewer_ep.user_id = target_reviewer_id
        and reviewed_ep.user_id = target_reviewed_user_id
        and reviewer_ep.status in ('approved', 'checked_in')
        and reviewed_ep.status in ('approved', 'checked_in')
        and (target_event_id is null or reviewer_ep.event_id = target_event_id)
    );
$$;

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
    e.id as event_id,
    e.title,
    e.starts_at
  from public.events e
  join public.event_participants my_ep
    on my_ep.event_id = e.id
  join public.event_participants other_ep
    on other_ep.event_id = e.id
  where my_ep.user_id = auth.uid()
    and other_ep.user_id = target_user_id
    and my_ep.status in ('approved', 'checked_in')
    and other_ep.status in ('approved', 'checked_in')
  order by e.starts_at desc;
$$;

create or replace function public.prevent_invalid_user_review()
returns trigger
language plpgsql
as $$
begin
  if not public.can_review_user(new.reviewer_id, new.reviewed_user_id, new.event_id) then
    raise exception 'User is not eligible to review this user';
  end if;

  return new;
end;
$$;

drop trigger if exists trg_user_reviews_validate on public.user_reviews;
create trigger trg_user_reviews_validate
before insert or update on public.user_reviews
for each row
execute function public.prevent_invalid_user_review();
