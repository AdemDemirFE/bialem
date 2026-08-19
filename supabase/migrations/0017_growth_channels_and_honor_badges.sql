create extension if not exists pg_net with schema extensions;

create table if not exists public.push_tokens (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  expo_push_token text not null,
  platform text not null check (platform in ('ios', 'android')),
  device_name text,
  is_active boolean not null default true,
  last_seen_at timestamptz not null default now(),
  created_at timestamptz not null default now(),
  unique (user_id, expo_push_token)
);

create index if not exists idx_push_tokens_user_active
on public.push_tokens(user_id, is_active);

alter table public.push_tokens enable row level security;

drop policy if exists push_tokens_read_own on public.push_tokens;
create policy push_tokens_read_own
on public.push_tokens for select to authenticated
using (user_id = auth.uid());

drop policy if exists push_tokens_create_own on public.push_tokens;
create policy push_tokens_create_own
on public.push_tokens for insert to authenticated
with check (user_id = auth.uid());

drop policy if exists push_tokens_update_own on public.push_tokens;
create policy push_tokens_update_own
on public.push_tokens for update to authenticated
using (user_id = auth.uid())
with check (user_id = auth.uid());

drop policy if exists push_tokens_delete_own on public.push_tokens;
create policy push_tokens_delete_own
on public.push_tokens for delete to authenticated
using (user_id = auth.uid());

create or replace function public.send_notification_push()
returns trigger
language plpgsql
security definer
set search_path = public, extensions
as $$
declare
  token_record record;
begin
  for token_record in
    select expo_push_token
    from public.push_tokens
    where user_id = new.user_id and is_active = true
  loop
    perform net.http_post(
      url := 'https://exp.host/--/api/v2/push/send',
      headers := jsonb_build_object(
        'Accept', 'application/json',
        'Content-Type', 'application/json'
      ),
      body := jsonb_build_object(
        'to', token_record.expo_push_token,
        'sound', 'default',
        'title', new.title,
        'body', coalesce(new.body, ''),
        'data', new.payload
      )
    );
  end loop;

  return new;
end;
$$;

drop trigger if exists trg_notifications_send_push on public.notifications;
create trigger trg_notifications_send_push
after insert on public.notifications
for each row execute function public.send_notification_push();

create or replace function public.get_public_event_share(target_event_id uuid)
returns table (
  event_id uuid,
  title text,
  description text,
  starts_at timestamptz,
  ends_at timestamptz,
  location_name text,
  address_text text,
  cover_image_url text,
  capacity integer,
  community_id uuid,
  community_name text,
  organizer_display_name text,
  approved_count bigint
)
language sql
stable
security definer
set search_path = public
as $$
  select
    e.id,
    e.title,
    e.description,
    e.starts_at,
    e.ends_at,
    e.location_name,
    e.address_text,
    e.cover_image_url,
    e.capacity,
    e.community_id,
    c.name,
    p.display_name,
    count(ep.id) filter (where ep.status in ('approved', 'checked_in'))
  from public.events e
  join public.communities c on c.id = e.community_id
  join public.profiles p on p.id = e.created_by
  left join public.event_participants ep on ep.event_id = e.id
  where e.id = target_event_id and e.status = 'published'
  group by e.id, c.id, p.id;
$$;

revoke all on function public.get_public_event_share(uuid) from public;
grant execute on function public.get_public_event_share(uuid) to anon, authenticated;

create table if not exists public.honor_badges (
  id uuid primary key default gen_random_uuid(),
  code text not null unique,
  name_template text not null,
  description text not null,
  badge_type text not null check (badge_type in ('community', 'city', 'city_community', 'special')),
  community_id uuid references public.communities(id) on delete cascade,
  minimum_check_ins integer not null default 3 check (minimum_check_ins > 0),
  is_active boolean not null default true,
  created_at timestamptz not null default now()
);

create table if not exists public.user_honor_badges (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  badge_id uuid not null references public.honor_badges(id) on delete cascade,
  awarded_by uuid references public.profiles(id) on delete set null,
  reason text,
  awarded_at timestamptz not null default now(),
  unique (user_id, badge_id)
);

create index if not exists idx_user_honor_badges_user_awarded
on public.user_honor_badges(user_id, awarded_at desc);

alter table public.honor_badges enable row level security;
alter table public.user_honor_badges enable row level security;

drop policy if exists honor_badges_read_active on public.honor_badges;
create policy honor_badges_read_active
on public.honor_badges for select
using (is_active = true or public.is_admin());

drop policy if exists user_honor_badges_read_all on public.user_honor_badges;
create policy user_honor_badges_read_all
on public.user_honor_badges for select
using (true);

create or replace function public.refresh_user_honor_badges(target_user_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  badge_record record;
  target_city text;
  qualified_count integer;
begin
  select nullif(trim(city), '') into target_city
  from public.profiles where id = target_user_id;

  for badge_record in
    select * from public.honor_badges
    where is_active = true and badge_type <> 'special'
  loop
    select count(*)::integer into qualified_count
    from public.event_participants ep
    join public.events e on e.id = ep.event_id
    join public.communities event_group on event_group.id = e.community_id
    where ep.user_id = target_user_id
      and ep.status = 'checked_in'
      and (
        badge_record.badge_type = 'city'
        or badge_record.community_id is null
        or event_group.id = badge_record.community_id
        or event_group.parent_id = badge_record.community_id
      )
      and (
        badge_record.badge_type = 'community'
        or (
          target_city is not null
          and concat_ws(' ', e.location_name, e.address_text) ilike '%' || target_city || '%'
        )
      );

    if qualified_count >= badge_record.minimum_check_ins then
      insert into public.user_honor_badges (user_id, badge_id, reason)
      values (
        target_user_id,
        badge_record.id,
        qualified_count || ' doğrulanmış etkinlik katılımı'
      )
      on conflict (user_id, badge_id) do nothing;
    end if;
  end loop;
end;
$$;

create or replace function public.get_user_honor_badges(target_user_id uuid)
returns table (
  badge_code text,
  badge_name text,
  description text,
  reason text,
  awarded_at timestamptz
)
language sql
stable
security definer
set search_path = public
as $$
  select
    hb.code,
    replace(hb.name_template, '{city}', coalesce(nullif(trim(p.city), ''), 'Şehir')),
    hb.description,
    uhb.reason,
    uhb.awarded_at
  from public.user_honor_badges uhb
  join public.honor_badges hb on hb.id = uhb.badge_id
  join public.profiles p on p.id = uhb.user_id
  where uhb.user_id = target_user_id and hb.is_active = true
  order by uhb.awarded_at desc;
$$;

create or replace function public.award_honor_badge(
  target_user_id uuid,
  target_badge_code text,
  target_reason text default null
)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  target_badge_id uuid;
  target_community_id uuid;
begin
  select id, community_id into target_badge_id, target_community_id
  from public.honor_badges
  where code = target_badge_code and is_active = true;

  if target_badge_id is null then
    raise exception 'Badge not found';
  end if;

  if not public.is_admin()
    and not (
      target_community_id is not null
      and public.is_community_manager(target_community_id, auth.uid())
    ) then
    raise exception 'Not authorized to award this badge';
  end if;

  insert into public.user_honor_badges (user_id, badge_id, awarded_by, reason)
  values (target_user_id, target_badge_id, auth.uid(), nullif(trim(target_reason), ''))
  on conflict (user_id, badge_id) do update
  set awarded_by = excluded.awarded_by,
      reason = excluded.reason,
      awarded_at = now();
end;
$$;

create or replace function public.refresh_badges_after_check_in()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  if new.status = 'checked_in' and old.status is distinct from new.status then
    perform public.refresh_user_honor_badges(new.user_id);
  end if;
  return new;
end;
$$;

create or replace function public.notify_honor_badge_awarded()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
  awarded_badge_name text;
  target_city text;
begin
  select hb.name_template, p.city
  into awarded_badge_name, target_city
  from public.honor_badges hb
  join public.profiles p on p.id = new.user_id
  where hb.id = new.badge_id;

  insert into public.notifications (user_id, type, title, body, payload)
  values (
    new.user_id,
    'honor_badge_awarded',
    'Yeni bir onur madalyası kazandın',
    replace(awarded_badge_name, '{city}', coalesce(nullif(trim(target_city), ''), 'Şehir')),
    jsonb_build_object('user_id', new.user_id, 'badge_id', new.badge_id)
  );

  return new;
end;
$$;

drop trigger if exists trg_user_honor_badges_notify on public.user_honor_badges;
create trigger trg_user_honor_badges_notify
after insert on public.user_honor_badges
for each row execute function public.notify_honor_badge_awarded();

drop trigger if exists trg_event_participants_refresh_badges on public.event_participants;
create trigger trg_event_participants_refresh_badges
after update of status on public.event_participants
for each row execute function public.refresh_badges_after_check_in();

revoke all on function public.refresh_user_honor_badges(uuid) from public;
revoke all on function public.get_user_honor_badges(uuid) from public;
revoke all on function public.award_honor_badge(uuid, text, text) from public;
grant execute on function public.get_user_honor_badges(uuid) to authenticated;
grant execute on function public.award_honor_badge(uuid, text, text) to authenticated;

insert into public.honor_badges (code, name_template, description, badge_type, community_id, minimum_check_ins)
select seed.code, seed.name_template, seed.description, seed.badge_type, c.id, seed.minimum_check_ins
from (
  values
    ('doganin-koruyucusu', 'Doğanın Koruyucusu', 'Doğa etkinliklerinde düzenli ve doğrulanmış katılım gösterir.', 'community', 'doga-acik-hava', 3),
    ('sanatin-sesi', 'Sanatın Sesi', 'Kültür ve sanat buluşmalarını canlı tutan katılımcıdır.', 'community', 'kultur-sanat', 3),
    ('takimin-kalbi', 'Takımın Kalbi', 'Spor etkinliklerinde takım ruhunu büyütür.', 'community', 'spor-rekabet', 3),
    ('101-ustasi', '101 Ustası', 'Masa ve zeka oyunlarında deneyimli, güvenilir katılımcıdır.', 'community', 'masa-zeka-oyunlari', 3),
    ('gecenin-rehberi', 'Gecenin Rehberi', 'Güvenli ve keyifli akşam buluşmalarının müdavimidir.', 'community', 'aksam-eglencesi', 3),
    ('lezzet-kasifi', 'Lezzet Kaşifi', 'Yeni tatlar ve gastronomi deneyimlerini keşfeder.', 'community', 'gastronomi', 3),
    ('sehrin-gurmesi', '{city} Gurmesi', 'Kendi şehrindeki gastronomi etkinliklerine düzenli katılır.', 'city_community', 'gastronomi', 3)
) as seed(code, name_template, description, badge_type, community_slug, minimum_check_ins)
join public.communities c on c.slug = seed.community_slug
on conflict (code) do update
set name_template = excluded.name_template,
    description = excluded.description,
    badge_type = excluded.badge_type,
    community_id = excluded.community_id,
    minimum_check_ins = excluded.minimum_check_ins,
    is_active = true;

insert into public.honor_badges (code, name_template, description, badge_type, community_id, minimum_check_ins)
values (
  'sehir-elcisi',
  '{city} Elçisi',
  'Şehrindeki farklı etkinliklere güvenilir katılım göstererek topluluğu büyütür.',
  'city',
  null,
  5
)
on conflict (code) do update
set name_template = excluded.name_template,
    description = excluded.description,
    badge_type = excluded.badge_type,
    community_id = null,
    minimum_check_ins = excluded.minimum_check_ins,
    is_active = true;

do $$
declare
  profile_record record;
begin
  for profile_record in select id from public.profiles loop
    perform public.refresh_user_honor_badges(profile_record.id);
  end loop;
end;
$$;
