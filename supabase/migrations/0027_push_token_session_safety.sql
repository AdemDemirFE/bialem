-- An Expo push token identifies one app installation and must not belong to
-- multiple accounts after users switch accounts on the same device.
with ranked_tokens as (
  select
    id,
    row_number() over (
      partition by expo_push_token
      order by last_seen_at desc, created_at desc, id
    ) as token_rank
  from public.push_tokens
)
delete from public.push_tokens token
using ranked_tokens ranked
where token.id = ranked.id
  and ranked.token_rank > 1;

alter table public.push_tokens
drop constraint if exists push_tokens_user_id_expo_push_token_key;

alter table public.push_tokens
drop constraint if exists push_tokens_expo_push_token_key;

alter table public.push_tokens
add constraint push_tokens_expo_push_token_key unique (expo_push_token);

create or replace function public.register_current_device_push_token(
  target_expo_push_token text,
  target_platform text,
  target_device_name text default null
)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  normalized_token text := trim(target_expo_push_token);
begin
  if current_user_id is null then raise exception 'Authentication required'; end if;
  if target_platform not in ('ios', 'android') then raise exception 'Invalid push platform'; end if;
  if normalized_token !~ '^Expo(nent)?PushToken\[[A-Za-z0-9_-]+\]$' then
    raise exception 'Invalid Expo push token';
  end if;

  insert into public.push_tokens (
    user_id,
    expo_push_token,
    platform,
    device_name,
    is_active,
    last_seen_at
  ) values (
    current_user_id,
    normalized_token,
    target_platform,
    nullif(left(trim(target_device_name), 200), ''),
    true,
    now()
  )
  on conflict (expo_push_token) do update
  set
    user_id = excluded.user_id,
    platform = excluded.platform,
    device_name = excluded.device_name,
    is_active = true,
    last_seen_at = now();
end;
$$;

create or replace function public.deactivate_current_device_push_token(target_expo_push_token text)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
  if auth.uid() is null then raise exception 'Authentication required'; end if;

  update public.push_tokens
  set is_active = false, last_seen_at = now()
  where user_id = auth.uid()
    and expo_push_token = trim(target_expo_push_token);
end;
$$;

revoke insert, update, delete on table public.push_tokens from public, anon, authenticated;
revoke all on function public.register_current_device_push_token(text, text, text) from public;
revoke all on function public.deactivate_current_device_push_token(text) from public;
grant execute on function public.register_current_device_push_token(text, text, text) to authenticated;
grant execute on function public.deactivate_current_device_push_token(text) to authenticated;

notify pgrst, 'reload schema';
