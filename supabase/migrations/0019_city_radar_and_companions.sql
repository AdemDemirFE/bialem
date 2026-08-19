create table if not exists public.city_events (
  id uuid primary key default gen_random_uuid(),
  title text not null check (char_length(trim(title)) between 3 and 160),
  description text,
  category text not null,
  city text not null,
  venue_name text,
  address_text text,
  starts_at timestamptz not null,
  ends_at timestamptz,
  cover_image_url text,
  price_label text,
  source_name text not null,
  source_url text,
  ticket_url text,
  status text not null default 'draft' check (status in ('draft', 'published', 'cancelled')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  check (ends_at is null or ends_at >= starts_at)
);

create table if not exists public.city_event_interests (
  id uuid primary key default gen_random_uuid(),
  city_event_id uuid not null references public.city_events(id) on delete cascade,
  user_id uuid not null references public.profiles(id) on delete cascade,
  looking_for_company boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (city_event_id, user_id)
);

create index if not exists idx_city_events_city_starts
on public.city_events(city, starts_at)
where status = 'published';

create index if not exists idx_city_event_interests_event
on public.city_event_interests(city_event_id, looking_for_company);

alter table public.city_events enable row level security;
alter table public.city_event_interests enable row level security;

drop policy if exists city_events_read_published on public.city_events;
create policy city_events_read_published
on public.city_events
for select
to authenticated
using (status = 'published' or public.is_admin());

drop policy if exists city_events_admin_manage on public.city_events;
create policy city_events_admin_manage
on public.city_events
for all
to authenticated
using (public.is_admin())
with check (public.is_admin());

-- Interest rows stay private; aggregate counts are exposed by get_city_radar.
drop policy if exists city_event_interests_read_own on public.city_event_interests;
create policy city_event_interests_read_own
on public.city_event_interests
for select
to authenticated
using (user_id = auth.uid());

drop policy if exists city_event_interests_create_own on public.city_event_interests;
create policy city_event_interests_create_own
on public.city_event_interests
for insert
to authenticated
with check (user_id = auth.uid());

drop policy if exists city_event_interests_update_own on public.city_event_interests;
create policy city_event_interests_update_own
on public.city_event_interests
for update
to authenticated
using (user_id = auth.uid())
with check (user_id = auth.uid());

drop policy if exists city_event_interests_delete_own on public.city_event_interests;
create policy city_event_interests_delete_own
on public.city_event_interests
for delete
to authenticated
using (user_id = auth.uid());

drop trigger if exists trg_city_events_updated_at on public.city_events;
create trigger trg_city_events_updated_at
before update on public.city_events
for each row execute function public.set_updated_at();

drop trigger if exists trg_city_event_interests_updated_at on public.city_event_interests;
create trigger trg_city_event_interests_updated_at
before update on public.city_event_interests
for each row execute function public.set_updated_at();

create or replace function public.get_city_radar(target_city text default null)
returns table (
  event_id uuid,
  title text,
  description text,
  category text,
  city text,
  venue_name text,
  address_text text,
  starts_at timestamptz,
  ends_at timestamptz,
  cover_image_url text,
  price_label text,
  source_name text,
  source_url text,
  ticket_url text,
  interested_count bigint,
  companion_count bigint,
  is_interested boolean,
  is_looking_for_company boolean
)
language sql
stable
security definer
set search_path = public
as $$
  select
    ce.id,
    ce.title,
    ce.description,
    ce.category,
    ce.city,
    ce.venue_name,
    ce.address_text,
    ce.starts_at,
    ce.ends_at,
    ce.cover_image_url,
    ce.price_label,
    ce.source_name,
    ce.source_url,
    ce.ticket_url,
    count(cei.id),
    count(cei.id) filter (where cei.looking_for_company),
    coalesce(bool_or(cei.user_id = auth.uid()), false),
    coalesce(bool_or(cei.user_id = auth.uid() and cei.looking_for_company), false)
  from public.city_events ce
  left join public.city_event_interests cei on cei.city_event_id = ce.id
  where auth.uid() is not null
    and ce.status = 'published'
    and ce.starts_at >= now() - interval '2 hours'
    and (
      nullif(trim(target_city), '') is null
      or lower(ce.city) = lower(trim(target_city))
    )
  group by ce.id
  order by ce.starts_at asc;
$$;

create or replace function public.set_city_event_interest(
  target_event_id uuid,
  target_looking_for_company boolean default false
)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
  if auth.uid() is null then
    raise exception 'Authentication required';
  end if;

  if not exists (
    select 1 from public.city_events ce
    where ce.id = target_event_id
      and ce.status = 'published'
      and ce.starts_at >= now() - interval '2 hours'
  ) then
    raise exception 'City event is not available';
  end if;

  insert into public.city_event_interests (city_event_id, user_id, looking_for_company)
  values (target_event_id, auth.uid(), target_looking_for_company)
  on conflict (city_event_id, user_id) do update
  set
    looking_for_company = excluded.looking_for_company,
    updated_at = now();
end;
$$;

create or replace function public.clear_city_event_interest(target_event_id uuid)
returns void
language sql
security definer
set search_path = public
as $$
  delete from public.city_event_interests
  where city_event_id = target_event_id and user_id = auth.uid();
$$;

revoke all on function public.get_city_radar(text) from public;
revoke all on function public.set_city_event_interest(uuid, boolean) from public;
revoke all on function public.clear_city_event_interest(uuid) from public;
grant execute on function public.get_city_radar(text) to authenticated;
grant execute on function public.set_city_event_interest(uuid, boolean) to authenticated;
grant execute on function public.clear_city_event_interest(uuid) to authenticated;
