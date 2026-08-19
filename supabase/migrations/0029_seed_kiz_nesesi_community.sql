do $$
declare
  founder_id uuid;
  root_community_id uuid;
begin
  select profile.id
  into founder_id
  from public.profiles profile
  where lower(profile.email) = 'acbaldirlioglu@gmail.com'
    and profile.status = 'active'
  limit 1;

  if founder_id is null then
    raise exception 'Kiz Nesesi founder profile was not found or is not active';
  end if;

  insert into public.communities (
    parent_id,
    category_id,
    community_type,
    name,
    slug,
    description,
    visibility,
    cover_image_url,
    created_by,
    lead_moderator_id,
    partner_trust_level,
    is_verified_partner,
    is_discoverable
  )
  values (
    null,
    null,
    'category_hub',
    'Kız Neşesi',
    'kiz-nesesi',
    'Kadınların güvenli, samimi ve eğlenceli buluşmalarda yeni arkadaşlıklar kurduğu; oyun, sohbet ve üretim odaklı topluluk.',
    'public',
    null,
    founder_id,
    founder_id,
    'trusted',
    false,
    true
  )
  on conflict (slug) do update
  set
    parent_id = null,
    category_id = null,
    community_type = 'category_hub',
    name = excluded.name,
    description = excluded.description,
    visibility = 'public',
    created_by = founder_id,
    lead_moderator_id = founder_id,
    partner_trust_level = 'trusted',
    is_verified_partner = false,
    is_discoverable = true,
    updated_at = now()
  returning id into root_community_id;

  insert into public.communities (
    parent_id,
    category_id,
    community_type,
    name,
    slug,
    description,
    visibility,
    cover_image_url,
    created_by,
    partner_trust_level,
    is_verified_partner,
    is_discoverable
  )
  select
    root_community_id,
    root_community_id,
    'group',
    group_record.name,
    group_record.slug,
    group_record.description,
    'private',
    null,
    founder_id,
    'trusted',
    false,
    true
  from (
    values
      (
        'Kız Kıza Masa Oyunları Grubu',
        'kiz-kiza-masa-oyunlari',
        'Kutu oyunları, kart oyunları ve keyifli rekabet etrafında güvenle buluşmak isteyen kadınlar için.'
      ),
      (
        'Kız Kıza Eğlence Grubu',
        'kiz-kiza-eglence',
        'Konser, dans, sahne ve şehir eğlencelerini birlikte keşfetmek isteyen kadınlar için.'
      ),
      (
        'Kız Kıza Workshop Grubu',
        'kiz-kiza-workshop',
        'Yeni beceriler öğrenmek, üretmek ve yaratıcı atölyelerde bir araya gelmek isteyen kadınlar için.'
      ),
      (
        'Kız Kıza Sohbet Grubu',
        'kiz-kiza-sohbet',
        'Samimi sohbetler, dayanışma ve yeni arkadaşlıklar için güvenli buluşma grubu.'
      )
  ) as group_record(name, slug, description)
  on conflict (slug) do update
  set
    parent_id = root_community_id,
    category_id = root_community_id,
    community_type = 'group',
    name = excluded.name,
    description = excluded.description,
    visibility = 'private',
    created_by = founder_id,
    partner_trust_level = 'trusted',
    is_verified_partner = false,
    is_discoverable = true,
    updated_at = now();

  insert into public.community_members (community_id, user_id, role, status)
  select community.id, founder_id, 'manager', 'approved'
  from public.communities community
  where community.slug in (
    'kiz-nesesi',
    'kiz-kiza-masa-oyunlari',
    'kiz-kiza-eglence',
    'kiz-kiza-workshop',
    'kiz-kiza-sohbet'
  )
  on conflict (community_id, user_id) do update
  set
    role = 'manager',
    status = 'approved';
end;
$$;

notify pgrst, 'reload schema';
