create table if not exists public.partner_venues (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  slug text not null unique,
  description text,
  category text not null check (category in (
    'cafe', 'restaurant', 'sports', 'education', 'entertainment',
    'beauty', 'health', 'shopping', 'other'
  )),
  logo_url text,
  cover_image_url text,
  address text not null,
  city text not null default 'Ankara',
  latitude numeric(9, 6),
  longitude numeric(9, 6),
  phone text,
  website_url text,
  instagram_url text,
  is_featured boolean not null default false,
  is_active boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  check (
    (latitude is null and longitude is null)
    or (latitude between -90 and 90 and longitude between -180 and 180)
  )
);

create table if not exists public.partner_offers (
  id uuid primary key default gen_random_uuid(),
  venue_id uuid not null references public.partner_venues(id) on delete cascade,
  title text not null,
  description text,
  discount_percent numeric(5, 2) not null check (discount_percent > 0 and discount_percent <= 100),
  minimum_spend numeric(12, 2) check (minimum_spend is null or minimum_spend >= 0),
  maximum_discount numeric(12, 2) check (maximum_discount is null or maximum_discount > 0),
  valid_from timestamptz not null default now(),
  valid_until timestamptz,
  valid_days smallint[] not null default array[1, 2, 3, 4, 5, 6, 7]::smallint[],
  daily_start_time time,
  daily_end_time time,
  per_user_limit integer check (per_user_limit is null or per_user_limit > 0),
  terms text,
  is_active boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  check (valid_until is null or valid_until > valid_from),
  check (
    valid_days <@ array[1, 2, 3, 4, 5, 6, 7]::smallint[]
    and cardinality(valid_days) > 0
  )
);

create table if not exists public.partner_venue_staff (
  id uuid primary key default gen_random_uuid(),
  venue_id uuid not null references public.partner_venues(id) on delete cascade,
  user_id uuid not null references public.profiles(id) on delete cascade,
  assigned_by uuid references public.profiles(id) on delete set null,
  is_active boolean not null default true,
  created_at timestamptz not null default now(),
  unique (venue_id, user_id)
);

create table if not exists public.partner_offer_redemptions (
  id uuid primary key default gen_random_uuid(),
  offer_id uuid not null references public.partner_offers(id) on delete cascade,
  venue_id uuid not null references public.partner_venues(id) on delete cascade,
  user_id uuid not null references public.profiles(id) on delete cascade,
  token uuid not null default gen_random_uuid() unique,
  redemption_code text not null unique,
  status text not null default 'issued' check (status in ('issued', 'redeemed', 'expired', 'cancelled')),
  issued_at timestamptz not null default now(),
  expires_at timestamptz not null,
  redeemed_at timestamptz,
  redeemed_by uuid references public.profiles(id) on delete set null,
  order_amount numeric(12, 2) check (order_amount is null or order_amount >= 0),
  discount_amount numeric(12, 2) check (discount_amount is null or discount_amount >= 0),
  check (
    (status = 'redeemed' and redeemed_at is not null)
    or (status <> 'redeemed' and redeemed_at is null)
  )
);

create index if not exists idx_partner_venues_active_city
on public.partner_venues(city, is_featured desc, name)
where is_active = true;

create index if not exists idx_partner_offers_active_venue
on public.partner_offers(venue_id, valid_until)
where is_active = true;

create index if not exists idx_partner_redemptions_user_offer
on public.partner_offer_redemptions(user_id, offer_id, issued_at desc);

create index if not exists idx_partner_redemptions_venue_status
on public.partner_offer_redemptions(venue_id, status, issued_at desc);

drop trigger if exists trg_partner_venues_updated_at on public.partner_venues;
create trigger trg_partner_venues_updated_at
before update on public.partner_venues
for each row execute function public.set_updated_at();

drop trigger if exists trg_partner_offers_updated_at on public.partner_offers;
create trigger trg_partner_offers_updated_at
before update on public.partner_offers
for each row execute function public.set_updated_at();

alter table public.partner_venues enable row level security;
alter table public.partner_offers enable row level security;
alter table public.partner_venue_staff enable row level security;
alter table public.partner_offer_redemptions enable row level security;

create or replace function public.is_partner_venue_staff(target_venue_id uuid, target_user_id uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.partner_venue_staff staff
    where staff.venue_id = target_venue_id
      and staff.user_id = target_user_id
      and staff.is_active = true
  );
$$;

drop policy if exists partner_venues_read_active on public.partner_venues;
create policy partner_venues_read_active
on public.partner_venues
for select
to authenticated
using (is_active = true or public.is_admin());

drop policy if exists partner_offers_read_active on public.partner_offers;
create policy partner_offers_read_active
on public.partner_offers
for select
to authenticated
using (
  public.is_admin()
  or (
    is_active = true
    and valid_from <= now()
    and (valid_until is null or valid_until > now())
    and exists (
      select 1
      from public.partner_venues venue
      where venue.id = partner_offers.venue_id and venue.is_active = true
    )
  )
);

drop policy if exists partner_venue_staff_read_related on public.partner_venue_staff;
create policy partner_venue_staff_read_related
on public.partner_venue_staff
for select
to authenticated
using (user_id = auth.uid() or public.is_admin());

drop policy if exists partner_redemptions_read_related on public.partner_offer_redemptions;
create policy partner_redemptions_read_related
on public.partner_offer_redemptions
for select
to authenticated
using (
  user_id = auth.uid()
  or public.is_admin()
  or public.is_partner_venue_staff(venue_id, auth.uid())
);

create or replace function public.issue_partner_offer_redemption(target_offer_id uuid)
returns table (
  redemption_id uuid,
  redemption_token uuid,
  redemption_code text,
  expires_at timestamptz
)
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  target_offer public.partner_offers%rowtype;
  created_redemption public.partner_offer_redemptions%rowtype;
  current_time time := (now() at time zone 'Europe/Istanbul')::time;
  current_day smallint := extract(isodow from (now() at time zone 'Europe/Istanbul'))::smallint;
  previous_usage_count integer;
  generated_code text;
begin
  if current_user_id is null then
    raise exception 'Oturum açmanız gerekiyor';
  end if;

  select offer.* into target_offer
  from public.partner_offers offer
  join public.partner_venues venue on venue.id = offer.venue_id
  where offer.id = target_offer_id
    and offer.is_active = true
    and venue.is_active = true
    and offer.valid_from <= now()
    and (offer.valid_until is null or offer.valid_until > now())
  for update of offer;

  if not found then
    raise exception 'Bu avantaj şu anda kullanılamıyor';
  end if;

  if not current_day = any(target_offer.valid_days) then
    raise exception 'Bu avantaj bugün geçerli değil';
  end if;

  if target_offer.daily_start_time is not null
    and target_offer.daily_end_time is not null
    and (
      (target_offer.daily_start_time <= target_offer.daily_end_time
        and current_time not between target_offer.daily_start_time and target_offer.daily_end_time)
      or
      (target_offer.daily_start_time > target_offer.daily_end_time
        and current_time < target_offer.daily_start_time
        and current_time > target_offer.daily_end_time)
    ) then
    raise exception 'Bu avantaj şu anda geçerli değil';
  end if;

  update public.partner_offer_redemptions
  set status = 'expired'
  where user_id = current_user_id
    and offer_id = target_offer_id
    and status = 'issued'
    and expires_at <= now();

  if exists (
    select 1
    from public.partner_offer_redemptions redemption
    where redemption.user_id = current_user_id
      and redemption.offer_id = target_offer_id
      and redemption.status = 'issued'
      and redemption.expires_at > now()
  ) then
    return query
    select redemption.id, redemption.token, redemption.redemption_code, redemption.expires_at
    from public.partner_offer_redemptions redemption
    where redemption.user_id = current_user_id
      and redemption.offer_id = target_offer_id
      and redemption.status = 'issued'
      and redemption.expires_at > now()
    order by redemption.issued_at desc
    limit 1;
    return;
  end if;

  if target_offer.per_user_limit is not null then
    select count(*) into previous_usage_count
    from public.partner_offer_redemptions redemption
    where redemption.user_id = current_user_id
      and redemption.offer_id = target_offer_id
      and redemption.status = 'redeemed';

    if previous_usage_count >= target_offer.per_user_limit then
      raise exception 'Bu avantaj için kullanım hakkınız kalmadı';
    end if;
  end if;

  loop
    generated_code := upper(left(replace(gen_random_uuid()::text, '-', ''), 8));
    exit when not exists (
      select 1 from public.partner_offer_redemptions redemption
      where redemption.redemption_code = generated_code
    );
  end loop;

  insert into public.partner_offer_redemptions (
    offer_id, venue_id, user_id, redemption_code, expires_at
  )
  values (
    target_offer.id, target_offer.venue_id, current_user_id, generated_code, now() + interval '60 seconds'
  )
  returning * into created_redemption;

  return query select
    created_redemption.id,
    created_redemption.token,
    created_redemption.redemption_code,
    created_redemption.expires_at;
end;
$$;

create or replace function public.redeem_partner_offer(
  target_token_or_code text,
  target_order_amount numeric default null
)
returns table (
  redemption_id uuid,
  venue_name text,
  offer_title text,
  member_name text,
  discount_percent numeric,
  discount_amount numeric
)
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  target_redemption public.partner_offer_redemptions%rowtype;
  calculated_discount numeric(12, 2);
begin
  select redemption.* into target_redemption
  from public.partner_offer_redemptions redemption
  where (
      redemption.redemption_code = upper(trim(target_token_or_code))
      or redemption.token::text = lower(trim(target_token_or_code))
    )
  for update;

  if not found then
    raise exception 'Avantaj kodu bulunamadı';
  end if;

  if auth.role() <> 'service_role'
    and not public.is_admin()
    and not public.is_partner_venue_staff(target_redemption.venue_id, current_user_id) then
    raise exception 'Bu kodu doğrulama yetkiniz yok';
  end if;

  if target_redemption.status <> 'issued' then
    raise exception 'Bu avantaj kodu daha önce kullanılmış veya iptal edilmiş';
  end if;

  if target_redemption.expires_at <= now() then
    update public.partner_offer_redemptions
    set status = 'expired'
    where id = target_redemption.id;
    raise exception 'Avantaj kodunun süresi dolmuş';
  end if;

  if target_order_amount is not null and exists (
    select 1
    from public.partner_offers offer
    where offer.id = target_redemption.offer_id
      and offer.minimum_spend is not null
      and target_order_amount < offer.minimum_spend
  ) then
    raise exception 'Sepet tutarı kampanya alt limitini karşılamıyor';
  end if;

  select case
    when target_order_amount is null then null
    else least(
      round(target_order_amount * offer.discount_percent / 100, 2),
      coalesce(offer.maximum_discount, round(target_order_amount * offer.discount_percent / 100, 2))
    )
  end into calculated_discount
  from public.partner_offers offer
  where offer.id = target_redemption.offer_id;

  update public.partner_offer_redemptions
  set
    status = 'redeemed',
    redeemed_at = now(),
    redeemed_by = current_user_id,
    order_amount = target_order_amount,
    discount_amount = calculated_discount
  where id = target_redemption.id;

  return query
  select
    target_redemption.id,
    venue.name,
    offer.title,
    profile.display_name,
    offer.discount_percent,
    calculated_discount
  from public.partner_offers offer
  join public.partner_venues venue on venue.id = offer.venue_id
  join public.profiles profile on profile.id = target_redemption.user_id
  where offer.id = target_redemption.offer_id;
end;
$$;

revoke all on function public.is_partner_venue_staff(uuid, uuid) from public;
revoke all on function public.issue_partner_offer_redemption(uuid) from public;
revoke all on function public.redeem_partner_offer(text, numeric) from public;

grant execute on function public.is_partner_venue_staff(uuid, uuid) to authenticated;
grant execute on function public.issue_partner_offer_redemption(uuid) to authenticated;
grant execute on function public.redeem_partner_offer(text, numeric) to authenticated;
grant execute on function public.redeem_partner_offer(text, numeric) to service_role;

notify pgrst, 'reload schema';
