do $$
declare
  culture_community_id uuid;
  group_owner_id uuid;
  book_club_id uuid;
begin
  select c.id, c.created_by
  into culture_community_id, group_owner_id
  from public.communities c
  where c.slug = 'kultur-sanat'
    and c.parent_id is null
  limit 1;

  if culture_community_id is null then
    raise exception 'Önce Kültür & Sanat ana topluluğu oluşturulmalıdır.';
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
  values (
    culture_community_id,
    'Kitap Kulübü',
    'kultur-kitap-kulubu',
    'Birlikte kitap okumak, eserleri değerlendirmek ve yazar buluşmaları düzenlemek isteyenler için.',
    'private',
    null,
    group_owner_id
  )
  on conflict (slug) do update
  set
    parent_id = excluded.parent_id,
    name = excluded.name,
    description = excluded.description,
    visibility = 'private',
    updated_at = now()
  returning id into book_club_id;

  insert into public.community_members (community_id, user_id, role, status)
  values (book_club_id, group_owner_id, 'manager', 'approved')
  on conflict (community_id, user_id) do update
  set
    role = 'manager',
    status = 'approved';

  raise notice 'Kitap Kulübü, Kültür & Sanat topluluğunun altına eklendi.';
end;
$$;

select
  parent.name as ana_topluluk,
  child.name as alt_grup,
  child.slug
from public.communities child
join public.communities parent on parent.id = child.parent_id
where child.slug = 'kultur-kitap-kulubu';
