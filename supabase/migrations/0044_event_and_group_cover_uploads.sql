insert into storage.buckets (
  id,
  name,
  public,
  file_size_limit,
  allowed_mime_types
)
values (
  'event-covers',
  'event-covers',
  true,
  5242880,
  array['image/jpeg', 'image/png', 'image/webp']
)
on conflict (id) do update
set
  public = true,
  file_size_limit = excluded.file_size_limit,
  allowed_mime_types = excluded.allowed_mime_types;

drop policy if exists event_covers_insert_own on storage.objects;
create policy event_covers_insert_own
on storage.objects
for insert
to authenticated
with check (
  bucket_id = 'event-covers'
  and (storage.foldername(name))[1] = auth.uid()::text
);

drop policy if exists event_covers_delete_own on storage.objects;
create policy event_covers_delete_own
on storage.objects
for delete
to authenticated
using (
  bucket_id = 'event-covers'
  and (storage.foldername(name))[1] = auth.uid()::text
);

drop policy if exists community_covers_insert_own on storage.objects;
create policy community_covers_insert_own
on storage.objects
for insert
to authenticated
with check (
  bucket_id = 'community-covers'
  and (storage.foldername(name))[1] = auth.uid()::text
);

drop policy if exists community_covers_delete_own on storage.objects;
create policy community_covers_delete_own
on storage.objects
for delete
to authenticated
using (
  bucket_id = 'community-covers'
  and (storage.foldername(name))[1] = auth.uid()::text
);

-- Keep the previous signature available for installed builds while adding cover
-- support to new builds.
create or replace function public.create_group_event(
  target_community_id uuid,
  target_title text,
  target_description text,
  target_starts_at timestamptz,
  target_ends_at timestamptz,
  target_location_name text,
  target_address_text text,
  target_latitude numeric,
  target_longitude numeric,
  target_capacity integer,
  target_cover_image_url text
)
returns table (
  event_id uuid,
  event_status text,
  creation_mode text
)
language plpgsql
security definer
set search_path = public
as $$
declare
  created_record record;
begin
  if target_cover_image_url is not null
    and target_cover_image_url not like '%/storage/v1/object/public/event-covers/%' then
    raise exception 'Geçersiz etkinlik kapak görseli';
  end if;

  select *
  into created_record
  from public.create_group_event(
    target_community_id,
    target_title,
    target_description,
    target_starts_at,
    target_ends_at,
    target_location_name,
    target_address_text,
    target_latitude,
    target_longitude,
    target_capacity
  );

  update public.events
  set cover_image_url = target_cover_image_url
  where id = created_record.event_id
    and created_by = auth.uid();

  return query
  select
    created_record.event_id::uuid,
    created_record.event_status::text,
    created_record.creation_mode::text;
end;
$$;

revoke all on function public.create_group_event(
  uuid,
  text,
  text,
  timestamptz,
  timestamptz,
  text,
  text,
  numeric,
  numeric,
  integer,
  text
) from public;

grant execute on function public.create_group_event(
  uuid,
  text,
  text,
  timestamptz,
  timestamptz,
  text,
  text,
  numeric,
  numeric,
  integer,
  text
) to authenticated;

notify pgrst, 'reload schema';
