create table if not exists public.ai_usage_logs (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  created_at timestamptz not null default now()
);

create index if not exists idx_ai_usage_logs_user_created
on public.ai_usage_logs(user_id, created_at desc);

alter table public.ai_usage_logs enable row level security;

drop policy if exists ai_usage_logs_admin_read on public.ai_usage_logs;
create policy ai_usage_logs_admin_read
on public.ai_usage_logs
for select
using (public.is_admin());

create or replace function public.claim_ai_request()
returns boolean
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  recent_request_count integer;
begin
  if current_user_id is null then
    return false;
  end if;

  select count(*)
  into recent_request_count
  from public.ai_usage_logs
  where user_id = current_user_id
    and created_at > now() - interval '1 hour';

  if recent_request_count >= 20 then
    return false;
  end if;

  insert into public.ai_usage_logs (user_id)
  values (current_user_id);

  return true;
end;
$$;

revoke all on function public.claim_ai_request() from public;
grant execute on function public.claim_ai_request() to authenticated;
