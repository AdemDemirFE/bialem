alter table public.city_events
add column if not exists provider_code text not null default 'manual',
add column if not exists external_id text,
add column if not exists last_synced_at timestamptz,
add column if not exists raw_payload jsonb;

create unique index if not exists idx_city_events_provider_external
on public.city_events(provider_code, external_id);

create table if not exists public.city_event_sync_logs (
  id uuid primary key default gen_random_uuid(),
  provider_code text not null,
  status text not null check (status in ('success', 'partial', 'failed')),
  imported_count integer not null default 0,
  error_message text,
  started_at timestamptz not null default now(),
  finished_at timestamptz not null default now()
);

create index if not exists idx_city_event_sync_logs_started
on public.city_event_sync_logs(started_at desc);

alter table public.city_event_sync_logs enable row level security;

drop policy if exists city_event_sync_logs_admin_read on public.city_event_sync_logs;
create policy city_event_sync_logs_admin_read
on public.city_event_sync_logs
for select
to authenticated
using (public.is_admin());

-- Edge Function uses the service role. Mobile and anonymous clients cannot write sync logs.
revoke all on public.city_event_sync_logs from anon, authenticated;

comment on column public.city_events.provider_code is
  'manual, ticketmaster or an approved partner feed identifier';

comment on column public.city_events.external_id is
  'Stable event identifier supplied by the external provider';
