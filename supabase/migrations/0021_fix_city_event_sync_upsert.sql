-- PostgREST upsert requires a non-partial unique index matching onConflict.
-- The first 0020 version used a partial index, so recreate it safely.
drop index if exists public.idx_city_events_provider_external;

create unique index idx_city_events_provider_external
on public.city_events(provider_code, external_id);

notify pgrst, 'reload schema';
