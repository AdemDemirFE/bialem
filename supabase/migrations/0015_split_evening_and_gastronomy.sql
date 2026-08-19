do $$
declare
  evening_community_id uuid;
  gastronomy_community_id uuid;
  community_owner_id uuid;
begin
  select c.id, c.created_by
  into evening_community_id, community_owner_id
  from public.communities c
  where c.parent_id is null
    and c.slug in ('aksam-eglencesi-gastronomi', 'aksam-eglencesi')
  order by case when c.slug = 'aksam-eglencesi-gastronomi' then 0 else 1 end
  limit 1;

  if evening_community_id is null then
    raise exception 'Akşam Eğlencesi & Gastronomi ana topluluğu bulunamadı.';
  end if;

  update public.communities
  set
    name = 'Akşam Eğlencesi',
    slug = 'aksam-eglencesi',
    description = 'Sosyalleşme, canlı müzik ve gece etkinliklerini güvenli buluşmalarda bir araya getiren topluluk.',
    visibility = 'public',
    updated_at = now()
  where id = evening_community_id;

  insert into public.communities (
    parent_id,
    name,
    slug,
    description,
    visibility,
    cover_image_url,
    created_by
  )
  values (
    null,
    'Gastronomi',
    'gastronomi',
    'Yeni lezzetler, tadım buluşmaları ve mutfak atölyeleri çevresinde bir araya gelenlerin topluluğu.',
    'public',
    null,
    community_owner_id
  )
  on conflict (slug) do update
  set
    parent_id = null,
    name = excluded.name,
    description = excluded.description,
    visibility = 'public',
    updated_at = now()
  returning id into gastronomy_community_id;

  update public.communities
  set parent_id = evening_community_id, updated_at = now()
  where slug in ('aksam-geleneksel-sosyallesme', 'aksam-modern-gece-hayati');

  update public.communities
  set parent_id = gastronomy_community_id, updated_at = now()
  where slug = 'aksam-gurme-atolye';

  -- Birleşik topluluğun mevcut üyeleri bölünme sonrasında iki başlığa da erişebilsin.
  insert into public.community_members (community_id, user_id, role, status)
  select gastronomy_community_id, cm.user_id, cm.role, cm.status
  from public.community_members cm
  where cm.community_id = evening_community_id
  on conflict (community_id, user_id) do update
  set
    role = excluded.role,
    status = excluded.status;

  raise notice 'Akşam Eğlencesi ve Gastronomi iki ayrı ana topluluk olarak düzenlendi.';
end;
$$;

select
  parent.name as ana_topluluk,
  child.name as alt_grup
from public.communities parent
left join public.communities child on child.parent_id = parent.id
where parent.parent_id is null
  and parent.slug in ('aksam-eglencesi', 'gastronomi')
order by parent.name, child.name;
