create table if not exists public.platform_team_members (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null unique references public.profiles(id) on delete cascade,
  role_code text not null check (role_code in ('founder', 'team', 'support', 'editor')),
  assigned_by uuid references public.profiles(id) on delete set null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

alter table public.platform_team_members enable row level security;

drop policy if exists platform_team_members_read_authenticated on public.platform_team_members;
create policy platform_team_members_read_authenticated
on public.platform_team_members
for select
to authenticated
using (true);

create or replace function public.validate_platform_team_member()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
  target_email text;
begin
  select lower(profile.email)
  into target_email
  from public.profiles profile
  where profile.id = new.user_id;

  if target_email is null then
    raise exception 'Registered user not found';
  end if;

  if new.role_code = 'founder'
    and target_email not in ('acbaldirlioglu@gmail.com', 'mehmetas58@gmail.com') then
    raise exception 'Founder role is reserved for the two registered founder accounts';
  end if;

  if target_email in ('acbaldirlioglu@gmail.com', 'mehmetas58@gmail.com')
    and new.role_code <> 'founder' then
    raise exception 'Founder accounts cannot be assigned another platform role';
  end if;

  return new;
end;
$$;

drop trigger if exists trg_platform_team_members_validate on public.platform_team_members;
create trigger trg_platform_team_members_validate
before insert or update of user_id, role_code on public.platform_team_members
for each row execute function public.validate_platform_team_member();

create or replace function public.protect_platform_founder_identity()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  if exists (
    select 1
    from public.profiles profile
    where profile.id = old.user_id
      and lower(profile.email) in ('acbaldirlioglu@gmail.com', 'mehmetas58@gmail.com')
  ) then
    raise exception 'Founder identity cannot be removed';
  end if;

  return old;
end;
$$;

drop trigger if exists trg_platform_team_members_protect_founder on public.platform_team_members;
create trigger trg_platform_team_members_protect_founder
before delete on public.platform_team_members
for each row execute function public.protect_platform_founder_identity();

drop trigger if exists trg_platform_team_members_updated_at on public.platform_team_members;
create trigger trg_platform_team_members_updated_at
before update on public.platform_team_members
for each row execute function public.set_updated_at();

create or replace function public.sync_reserved_platform_founder()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  if lower(new.email) in ('acbaldirlioglu@gmail.com', 'mehmetas58@gmail.com') then
    insert into public.platform_team_members (user_id, role_code)
    values (new.id, 'founder')
    on conflict (user_id) do update
    set role_code = 'founder', updated_at = now();
  end if;

  return new;
end;
$$;

drop trigger if exists trg_profiles_sync_reserved_platform_founder on public.profiles;
create trigger trg_profiles_sync_reserved_platform_founder
after insert or update of email on public.profiles
for each row execute function public.sync_reserved_platform_founder();

insert into public.platform_team_members (user_id, role_code)
select profile.id, 'founder'
from public.profiles profile
where lower(profile.email) in ('acbaldirlioglu@gmail.com', 'mehmetas58@gmail.com')
on conflict (user_id) do update
set role_code = 'founder', updated_at = now();

-- Founder is a platform identity, not an achievement earned by user activity.
delete from public.user_honor_badges assignment
using public.honor_badges badge
where assignment.badge_id = badge.id
  and badge.code = 'bidunya-kurucusu';

update public.honor_badges
set is_active = false
where code = 'bidunya-kurucusu';

revoke all on table public.platform_team_members from anon;
revoke insert, update, delete on table public.platform_team_members from authenticated;
grant select on table public.platform_team_members to authenticated;

revoke all on function public.validate_platform_team_member() from public;
revoke all on function public.protect_platform_founder_identity() from public;
revoke all on function public.sync_reserved_platform_founder() from public;

notify pgrst, 'reload schema';
