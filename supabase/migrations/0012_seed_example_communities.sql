do $$
declare
  seed_owner_id uuid;
begin
  select ur.user_id
  into seed_owner_id
  from public.user_roles ur
  join public.roles r on r.id = ur.role_id
  join public.profiles p on p.id = ur.user_id
  where r.code = 'admin'
    and p.status <> 'deleted'
  order by p.created_at asc
  limit 1;

  if seed_owner_id is null then
    select p.id
    into seed_owner_id
    from public.profiles p
    where p.status <> 'deleted'
    order by p.created_at asc
    limit 1;
  end if;

  if seed_owner_id is null then
    raise exception 'Örnek toplulukları oluşturmak için önce en az bir kullanıcı hesabı açılmalıdır.';
  end if;

  insert into public.communities (
    parent_id,
    name,
    slug,
    description,
    visibility,
    cover_image_url,
    created_by
  )
  select
    null,
    category.name,
    category.slug,
    category.description,
    'public',
    null,
    seed_owner_id
  from (
    values
      (
        'Doğa & Açık Hava',
        'doga-acik-hava',
        'Piknikten doğa sporlarına, açık havada birlikte keşfetmek ve hareket etmek isteyenlerin topluluğu.'
      ),
      (
        'Kültür & Sanat',
        'kultur-sanat',
        'Sahne, görsel sanatlar, müzik ve üretim odaklı buluşmaları aynı çatı altında birleştiren topluluk.'
      ),
      (
        'Spor & Rekabet',
        'spor-rekabet',
        'Takım ruhunu, bireysel gelişimi ve eğlenceli rekabeti bir araya getiren spor topluluğu.'
      ),
      (
        'Masa & Zeka Oyunları',
        'masa-zeka-oyunlari',
        'Geleneksel oyunlardan yeni nesil kutu oyunlarına kadar strateji ve sosyalleşme topluluğu.'
      ),
      (
        'Akşam Eğlencesi',
        'aksam-eglencesi',
        'Sosyalleşme, canlı müzik ve gece etkinliklerini güvenli buluşmalarda bir araya getiren topluluk.'
      ),
      (
        'Gastronomi',
        'gastronomi',
        'Yeni lezzetler, tadım buluşmaları ve mutfak atölyeleri çevresinde bir araya gelenlerin topluluğu.'
      )
  ) as category(name, slug, description)
  on conflict (slug) do update
  set
    parent_id = null,
    name = excluded.name,
    description = excluded.description,
    visibility = 'public',
    updated_at = now();

  insert into public.communities (
    parent_id,
    name,
    slug,
    description,
    visibility,
    cover_image_url,
    created_by
  )
  select
    parent.id,
    group_data.name,
    group_data.slug,
    group_data.description,
    'private',
    null,
    seed_owner_id
  from (
    values
      (
        'doga-acik-hava',
        'Günübirlik Piknik & Sosyal',
        'doga-gunubirlik-piknik-sosyal',
        'Şehirden kısa süreliğine uzaklaşmak, piknik yapmak ve açık havada sosyalleşmek isteyenler için.'
      ),
      (
        'doga-acik-hava',
        'Macera & Keşif',
        'doga-macera-kesif',
        'Yeni rotalar, doğa yürüyüşleri ve keşif odaklı güvenli grup etkinlikleri için.'
      ),
      (
        'doga-acik-hava',
        'Aktif Outdoor Sporları',
        'doga-aktif-outdoor-sporlari',
        'Kamp, trekking, bisiklet ve hareket odaklı açık hava sporlarını birlikte yapmak isteyenler için.'
      ),
      (
        'kultur-sanat',
        'Sahne Sanatları',
        'kultur-sahne-sanatlari',
        'Tiyatro, dans, performans ve canlı sahne etkinliklerini takip edenler için.'
      ),
      (
        'kultur-sanat',
        'Görsel & İşitsel Sanatlar',
        'kultur-gorsel-isitsel-sanatlar',
        'Sinema, fotoğraf, müzik ve dijital sanat buluşmaları için.'
      ),
      (
        'kultur-sanat',
        'Atölye & Üretim',
        'kultur-atolye-uretim',
        'Birlikte öğrenmek, el işi yapmak ve yaratıcı üretim deneyimleri paylaşmak isteyenler için.'
      ),
      (
        'spor-rekabet',
        'Takım Sporları',
        'spor-takim-sporlari',
        'Futbol, basketbol, voleybol ve takım halinde oynanan diğer sporlar için.'
      ),
      (
        'spor-rekabet',
        'Bireysel & Eğlence Sporları',
        'spor-bireysel-eglence',
        'Bireysel gelişim, amatör turnuvalar ve eğlence odaklı spor buluşmaları için.'
      ),
      (
        'masa-zeka-oyunlari',
        'Geleneksel Kafe & Kıraathane Oyunları',
        'oyun-geleneksel-kafe-kiraathane',
        'Tavla, okey, iskambil ve geleneksel masa oyunları etrafında sosyalleşmek isteyenler için.'
      ),
      (
        'masa-zeka-oyunlari',
        'Strateji & Masaüstü Oyunları',
        'oyun-strateji-masaustu',
        'Satranç, strateji ve masaüstü oyunlarında yeni rakipler ve ekip arkadaşları bulmak için.'
      ),
      (
        'masa-zeka-oyunlari',
        'Yeni Nesil Kutu Oyunları',
        'oyun-yeni-nesil-kutu-oyunlari',
        'Modern board game deneyimlerini öğrenmek, öğretmek ve birlikte oynamak isteyenler için.'
      ),
      (
        'aksam-eglencesi',
        'Geleneksel Sosyalleşme',
        'aksam-geleneksel-sosyallesme',
        'Sakin sohbetler, arkadaş buluşmaları ve geleneksel sosyal etkinlikler için.'
      ),
      (
        'aksam-eglencesi',
        'Modern Gece Hayatı',
        'aksam-modern-gece-hayati',
        'Konser, canlı müzik ve güvenli gece etkinliklerini birlikte deneyimlemek isteyenler için.'
      ),
      (
        'gastronomi',
        'Gurme & Atölye',
        'aksam-gurme-atolye',
        'Yeni lezzetler keşfetmek, tadım yapmak ve gastronomi atölyelerine katılmak isteyenler için.'
      )
  ) as group_data(parent_slug, name, slug, description)
  join public.communities parent
    on parent.slug = group_data.parent_slug
   and parent.parent_id is null
  on conflict (slug) do update
  set
    parent_id = excluded.parent_id,
    name = excluded.name,
    description = excluded.description,
    visibility = 'private',
    updated_at = now();

  insert into public.community_members (community_id, user_id, role, status)
  select
    c.id,
    seed_owner_id,
    case when c.parent_id is null then 'owner' else 'manager' end,
    'approved'
  from public.communities c
  where c.slug in (
    'doga-acik-hava',
    'kultur-sanat',
    'spor-rekabet',
    'masa-zeka-oyunlari',
    'aksam-eglencesi',
    'gastronomi',
    'doga-gunubirlik-piknik-sosyal',
    'doga-macera-kesif',
    'doga-aktif-outdoor-sporlari',
    'kultur-sahne-sanatlari',
    'kultur-gorsel-isitsel-sanatlar',
    'kultur-atolye-uretim',
    'spor-takim-sporlari',
    'spor-bireysel-eglence',
    'oyun-geleneksel-kafe-kiraathane',
    'oyun-strateji-masaustu',
    'oyun-yeni-nesil-kutu-oyunlari',
    'aksam-geleneksel-sosyallesme',
    'aksam-modern-gece-hayati',
    'aksam-gurme-atolye'
  )
  on conflict (community_id, user_id) do update
  set
    role = excluded.role,
    status = 'approved';

  raise notice '6 ana topluluk ve 14 alt grup örnek veri olarak oluşturuldu.';
end;
$$;

select
  parent.name as ana_topluluk,
  child.name as alt_grup
from public.communities parent
left join public.communities child on child.parent_id = parent.id
where parent.parent_id is null
  and parent.slug in (
    'doga-acik-hava',
    'kultur-sanat',
    'spor-rekabet',
    'masa-zeka-oyunlari',
    'aksam-eglencesi',
    'gastronomi'
  )
order by parent.name, child.name;
