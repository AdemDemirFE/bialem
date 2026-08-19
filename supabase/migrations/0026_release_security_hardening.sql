create or replace function public.create_profile_for_auth_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
  desired_username text;
  generated_username text := 'uye_' || left(replace(new.id::text, '-', ''), 12);
  profile_status text := case when new.email_confirmed_at is null then 'pending_verification' else 'active' end;
begin
  if new.email is null then return new; end if;

  desired_username := lower(regexp_replace(coalesce(new.raw_user_meta_data ->> 'username', ''), '[^a-zA-Z0-9_]+', '', 'g'));
  if char_length(desired_username) < 3 then desired_username := generated_username; end if;

  begin
    insert into public.profiles (id, email, display_name, username, status, is_verified)
    values (
      new.id,
      lower(new.email),
      coalesce(nullif(trim(new.raw_user_meta_data ->> 'display_name'), ''), split_part(new.email, '@', 1)),
      desired_username,
      profile_status,
      new.email_confirmed_at is not null
    )
    on conflict (id) do nothing;
  exception when unique_violation then
    insert into public.profiles (id, email, display_name, username, status, is_verified)
    values (
      new.id,
      lower(new.email),
      coalesce(nullif(trim(new.raw_user_meta_data ->> 'display_name'), ''), split_part(new.email, '@', 1)),
      generated_username,
      profile_status,
      new.email_confirmed_at is not null
    )
    on conflict (id) do nothing;
  end;

  return new;
end;
$$;

drop trigger if exists trg_auth_user_create_profile on auth.users;
create trigger trg_auth_user_create_profile
after insert on auth.users
for each row execute function public.create_profile_for_auth_user();

create or replace function public.sync_profile_email_verification()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  update public.profiles
  set
    email = lower(new.email),
    status = case
      when status = 'pending_verification' and new.email_confirmed_at is not null then 'active'
      else status
    end,
    is_verified = new.email_confirmed_at is not null
  where id = new.id;

  return new;
end;
$$;

drop trigger if exists trg_auth_user_sync_profile_verification on auth.users;
create trigger trg_auth_user_sync_profile_verification
after update of email, email_confirmed_at on auth.users
for each row execute function public.sync_profile_email_verification();

insert into public.profiles (id, email, display_name, username, status, is_verified)
select
  user_record.id,
  lower(user_record.email),
  coalesce(nullif(trim(user_record.raw_user_meta_data ->> 'display_name'), ''), split_part(user_record.email, '@', 1)),
  'uye_' || left(replace(user_record.id::text, '-', ''), 12),
  case when user_record.email_confirmed_at is null then 'pending_verification' else 'active' end,
  user_record.email_confirmed_at is not null
from auth.users user_record
left join public.profiles profile on profile.id = user_record.id
where profile.id is null and user_record.email is not null
on conflict do nothing;

create or replace function public.protect_profile_system_fields()
returns trigger
language plpgsql
set search_path = public
as $$
begin
  if auth.uid() = old.id and not public.is_admin() then
    new.id := old.id;
    new.email := old.email;
    new.status := old.status;
    new.is_verified := old.is_verified;
    new.created_at := old.created_at;
  end if;

  return new;
end;
$$;

drop trigger if exists trg_profiles_protect_system_fields on public.profiles;
create trigger trg_profiles_protect_system_fields
before update on public.profiles
for each row execute function public.protect_profile_system_fields();

-- A deleted hub must not leave child groups behind as invalid root communities.
alter table public.communities drop constraint if exists communities_parent_id_fkey;
alter table public.communities add constraint communities_parent_id_fkey
foreign key (parent_id) references public.communities(id) on delete cascade;

-- Keep assignment history when its assigning user deletes their account.
alter table public.community_moderator_assistants alter column assigned_by drop not null;
alter table public.community_moderator_assistants
drop constraint if exists community_moderator_assistants_assigned_by_fkey;
alter table public.community_moderator_assistants
add constraint community_moderator_assistants_assigned_by_fkey
foreign key (assigned_by) references public.profiles(id) on delete set null;

create or replace function public.normalize_new_report()
returns trigger
language plpgsql
set search_path = public
as $$
begin
  if auth.uid() = new.reporter_id and not public.is_admin() then
    new.status := 'open';
    new.resolved_by := null;
    new.resolved_at := null;
  end if;

  return new;
end;
$$;

drop trigger if exists trg_reports_normalize_new on public.reports;
create trigger trg_reports_normalize_new
before insert on public.reports
for each row execute function public.normalize_new_report();

create or replace function public.prepare_profile_deletion()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  -- If the departing lead did not create the hub, return leadership to its creator.
  update public.communities
  set lead_moderator_id = created_by
  where parent_id is null
    and lead_moderator_id = old.id
    and created_by <> old.id;

  return old;
end;
$$;

drop trigger if exists trg_profiles_prepare_deletion on public.profiles;
create trigger trg_profiles_prepare_deletion
before delete on public.profiles
for each row execute function public.prepare_profile_deletion();

drop policy if exists profiles_insert_self on public.profiles;
revoke insert on table public.profiles from authenticated;
revoke update on table public.profiles from authenticated;
grant update (display_name, username, avatar_url, bio, city) on table public.profiles to authenticated;

update storage.buckets
set
  file_size_limit = 10485760,
  allowed_mime_types = array['image/jpeg', 'image/png', 'image/webp', 'image/heic', 'image/heif']
where id in ('post-media', 'stories');

revoke all on function public.create_profile_for_auth_user() from public;
revoke all on function public.sync_profile_email_verification() from public;
revoke all on function public.protect_profile_system_fields() from public;
revoke all on function public.normalize_new_report() from public;
revoke all on function public.prepare_profile_deletion() from public;

notify pgrst, 'reload schema';
