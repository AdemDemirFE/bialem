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
  current_local_time time := (now() at time zone 'Europe/Istanbul')::time;
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
        and current_local_time not between target_offer.daily_start_time and target_offer.daily_end_time)
      or
      (target_offer.daily_start_time > target_offer.daily_end_time
        and current_local_time < target_offer.daily_start_time
        and current_local_time > target_offer.daily_end_time)
    ) then
    raise exception 'Bu avantaj şu anda geçerli değil';
  end if;

  update public.partner_offer_redemptions as redemption
  set status = 'expired'
  where redemption.user_id = current_user_id
    and redemption.offer_id = target_offer_id
    and redemption.status = 'issued'
    and redemption.expires_at <= now();

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
      select 1
      from public.partner_offer_redemptions redemption
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

  return query
  select
    created_redemption.id,
    created_redemption.token,
    created_redemption.redemption_code,
    created_redemption.expires_at;
end;
$$;

revoke all on function public.issue_partner_offer_redemption(uuid) from public;
grant execute on function public.issue_partner_offer_redemption(uuid) to authenticated;

notify pgrst, 'reload schema';
