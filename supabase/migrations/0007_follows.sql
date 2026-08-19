create table if not exists public.follows (
  id uuid primary key default gen_random_uuid(),
  follower_id uuid not null references public.profiles(id) on delete cascade,
  followed_id uuid not null references public.profiles(id) on delete cascade,
  created_at timestamptz not null default now(),
  unique (follower_id, followed_id),
  check (follower_id <> followed_id)
);

create index if not exists idx_follows_follower on public.follows(follower_id, created_at desc);
create index if not exists idx_follows_followed on public.follows(followed_id, created_at desc);

alter table public.follows enable row level security;

drop policy if exists follows_read_authenticated on public.follows;
create policy follows_read_authenticated
on public.follows
for select
to authenticated
using (true);

drop policy if exists follows_create_own on public.follows;
create policy follows_create_own
on public.follows
for insert
to authenticated
with check (follower_id = auth.uid());

drop policy if exists follows_delete_own on public.follows;
create policy follows_delete_own
on public.follows
for delete
to authenticated
using (follower_id = auth.uid());

create or replace function public.create_follow_notification()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
  follower_name text;
begin
  select display_name into follower_name from public.profiles where id = new.follower_id;

  insert into public.notifications (user_id, type, title, body, payload)
  values (
    new.followed_id,
    'new_follower',
    'Yeni bir takipçin var',
    coalesce(follower_name, 'Bir kullanıcı') || ' seni takip etmeye başladı.',
    jsonb_build_object('user_id', new.follower_id)
  );

  return new;
end;
$$;

drop trigger if exists trg_follows_notify_user on public.follows;
create trigger trg_follows_notify_user
after insert on public.follows
for each row
execute function public.create_follow_notification();
