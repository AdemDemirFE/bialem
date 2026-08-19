create table if not exists public.city_event_ticket_offers (
  id uuid primary key default gen_random_uuid(),
  city_event_id uuid not null references public.city_events(id) on delete cascade,
  provider_code text not null,
  external_offer_id text not null,
  seller_name text not null,
  purchase_url text not null,
  currency text,
  min_price numeric(12,2) check (min_price is null or min_price >= 0),
  max_price numeric(12,2) check (max_price is null or max_price >= min_price),
  price_label text,
  availability text not null default 'available'
    check (availability in ('available', 'limited', 'sold_out', 'unknown')),
  fees_included boolean,
  is_official boolean not null default true,
  last_checked_at timestamptz not null default now(),
  raw_payload jsonb not null default '{}'::jsonb,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (city_event_id, provider_code, external_offer_id)
);

create index if not exists idx_city_event_ticket_offers_event_price
on public.city_event_ticket_offers(city_event_id, min_price, seller_name)
where availability in ('available', 'limited', 'unknown');

alter table public.city_event_ticket_offers enable row level security;

drop policy if exists city_event_ticket_offers_read_published on public.city_event_ticket_offers;
create policy city_event_ticket_offers_read_published
on public.city_event_ticket_offers
for select
to authenticated
using (
  exists (
    select 1
    from public.city_events ce
    where ce.id = city_event_id and (ce.status = 'published' or public.is_admin())
  )
);

drop policy if exists city_event_ticket_offers_admin_manage on public.city_event_ticket_offers;
create policy city_event_ticket_offers_admin_manage
on public.city_event_ticket_offers
for all
to authenticated
using (public.is_admin())
with check (public.is_admin());

drop trigger if exists trg_city_event_ticket_offers_updated_at on public.city_event_ticket_offers;
create trigger trg_city_event_ticket_offers_updated_at
before update on public.city_event_ticket_offers
for each row execute function public.set_updated_at();

create or replace function public.sync_city_event_primary_ticket_offer()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
  seller text;
  offer_min numeric;
  offer_max numeric;
  offer_currency text;
begin
  if new.ticket_url is null or trim(new.ticket_url) = '' then
    delete from public.city_event_ticket_offers
    where city_event_id = new.id and provider_code = new.provider_code;
    return new;
  end if;

  seller := case
    when lower(new.ticket_url) like '%biletix.com%' then 'Biletix'
    when lower(new.ticket_url) like '%bubilet.com%' then 'Bubilet'
    when lower(new.ticket_url) like '%biletinial.com%' then 'Biletinial'
    when lower(new.ticket_url) like '%passo.com.tr%' then 'Passo'
    else coalesce(nullif(trim(new.source_name), ''), initcap(new.provider_code))
  end;

  if jsonb_typeof(new.raw_payload #> '{priceRanges,0,min}') = 'number' then
    offer_min := (new.raw_payload #>> '{priceRanges,0,min}')::numeric;
  end if;
  if jsonb_typeof(new.raw_payload #> '{priceRanges,0,max}') = 'number' then
    offer_max := (new.raw_payload #>> '{priceRanges,0,max}')::numeric;
  end if;
  offer_currency := nullif(new.raw_payload #>> '{priceRanges,0,currency}', '');

  insert into public.city_event_ticket_offers (
    city_event_id,
    provider_code,
    external_offer_id,
    seller_name,
    purchase_url,
    currency,
    min_price,
    max_price,
    price_label,
    availability,
    is_official,
    last_checked_at,
    raw_payload
  )
  values (
    new.id,
    new.provider_code,
    coalesce(new.external_id, new.id::text),
    seller,
    new.ticket_url,
    offer_currency,
    offer_min,
    offer_max,
    new.price_label,
    case when new.status = 'cancelled' then 'sold_out' else 'unknown' end,
    true,
    coalesce(new.last_synced_at, now()),
    jsonb_build_object('source', new.source_name)
  )
  on conflict (city_event_id, provider_code, external_offer_id) do update
  set
    seller_name = excluded.seller_name,
    purchase_url = excluded.purchase_url,
    currency = excluded.currency,
    min_price = excluded.min_price,
    max_price = excluded.max_price,
    price_label = excluded.price_label,
    availability = excluded.availability,
    is_official = excluded.is_official,
    last_checked_at = excluded.last_checked_at,
    raw_payload = excluded.raw_payload,
    updated_at = now();

  return new;
end;
$$;

drop trigger if exists trg_city_events_sync_primary_ticket_offer on public.city_events;
create trigger trg_city_events_sync_primary_ticket_offer
after insert or update of ticket_url, price_label, status, raw_payload, last_synced_at
on public.city_events
for each row execute function public.sync_city_event_primary_ticket_offer();

-- Create an initial official offer for events imported before this migration.
insert into public.city_event_ticket_offers (
  city_event_id,
  provider_code,
  external_offer_id,
  seller_name,
  purchase_url,
  currency,
  min_price,
  max_price,
  price_label,
  availability,
  is_official,
  last_checked_at,
  raw_payload
)
select
  ce.id,
  ce.provider_code,
  coalesce(ce.external_id, ce.id::text),
  case
    when lower(ce.ticket_url) like '%biletix.com%' then 'Biletix'
    when lower(ce.ticket_url) like '%bubilet.com%' then 'Bubilet'
    when lower(ce.ticket_url) like '%biletinial.com%' then 'Biletinial'
    when lower(ce.ticket_url) like '%passo.com.tr%' then 'Passo'
    else coalesce(nullif(trim(ce.source_name), ''), initcap(ce.provider_code))
  end,
  ce.ticket_url,
  nullif(ce.raw_payload #>> '{priceRanges,0,currency}', ''),
  case when jsonb_typeof(ce.raw_payload #> '{priceRanges,0,min}') = 'number'
    then (ce.raw_payload #>> '{priceRanges,0,min}')::numeric else null end,
  case when jsonb_typeof(ce.raw_payload #> '{priceRanges,0,max}') = 'number'
    then (ce.raw_payload #>> '{priceRanges,0,max}')::numeric else null end,
  ce.price_label,
  case when ce.status = 'cancelled' then 'sold_out' else 'unknown' end,
  true,
  coalesce(ce.last_synced_at, ce.updated_at),
  jsonb_build_object('source', ce.source_name)
from public.city_events ce
where ce.ticket_url is not null and trim(ce.ticket_url) <> ''
on conflict (city_event_id, provider_code, external_offer_id) do update
set
  seller_name = excluded.seller_name,
  purchase_url = excluded.purchase_url,
  currency = excluded.currency,
  min_price = excluded.min_price,
  max_price = excluded.max_price,
  price_label = excluded.price_label,
  availability = excluded.availability,
  last_checked_at = excluded.last_checked_at,
  updated_at = now();

create or replace function public.get_city_event_ticket_offers(target_event_id uuid)
returns table (
  offer_id uuid,
  seller_name text,
  purchase_url text,
  currency text,
  min_price numeric,
  max_price numeric,
  price_label text,
  availability text,
  fees_included boolean,
  is_official boolean,
  last_checked_at timestamptz,
  is_cheapest boolean
)
language sql
stable
security definer
set search_path = public
as $$
  select
    offer.id,
    offer.seller_name,
    offer.purchase_url,
    offer.currency,
    offer.min_price,
    offer.max_price,
    offer.price_label,
    offer.availability,
    offer.fees_included,
    offer.is_official,
    offer.last_checked_at,
    offer.min_price is not null
      and offer.min_price = (
        select min(candidate.min_price)
        from public.city_event_ticket_offers candidate
        where candidate.city_event_id = target_event_id
          and candidate.availability in ('available', 'limited', 'unknown')
      ) as is_cheapest
  from public.city_event_ticket_offers offer
  join public.city_events event on event.id = offer.city_event_id
  where auth.uid() is not null
    and offer.city_event_id = target_event_id
    and event.status = 'published'
    and offer.availability <> 'sold_out'
  order by offer.min_price asc nulls last, offer.seller_name asc;
$$;

revoke all on function public.get_city_event_ticket_offers(uuid) from public;
grant execute on function public.get_city_event_ticket_offers(uuid) to authenticated;

notify pgrst, 'reload schema';
