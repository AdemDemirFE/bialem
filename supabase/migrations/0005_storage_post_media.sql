insert into storage.buckets (id, name, public)
values ('post-media', 'post-media', true)
on conflict (id) do update
set public = excluded.public;

drop policy if exists "Post media public read" on storage.objects;
create policy "Post media public read"
on storage.objects
for select
using (bucket_id = 'post-media');

drop policy if exists "Post media upload own folder" on storage.objects;
create policy "Post media upload own folder"
on storage.objects
for insert
to authenticated
with check (
  bucket_id = 'post-media'
  and auth.uid() is not null
  and (storage.foldername(name))[1] = auth.uid()::text
);

drop policy if exists "Post media update own folder" on storage.objects;
create policy "Post media update own folder"
on storage.objects
for update
to authenticated
using (
  bucket_id = 'post-media'
  and auth.uid() is not null
  and (storage.foldername(name))[1] = auth.uid()::text
)
with check (
  bucket_id = 'post-media'
  and auth.uid() is not null
  and (storage.foldername(name))[1] = auth.uid()::text
);

drop policy if exists "Post media delete own folder" on storage.objects;
create policy "Post media delete own folder"
on storage.objects
for delete
to authenticated
using (
  bucket_id = 'post-media'
  and auth.uid() is not null
  and (storage.foldername(name))[1] = auth.uid()::text
);
