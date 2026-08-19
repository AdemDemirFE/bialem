create or replace function public.get_public_profile_card(target_user_id uuid)
returns table (
  id uuid,
  display_name text,
  username text,
  avatar_url text,
  bio text,
  city text,
  status text,
  is_verified boolean,
  created_at timestamptz
)
language sql
stable
security definer
set search_path = public
as $$
  select
    profile.id,
    profile.display_name,
    profile.username,
    profile.avatar_url,
    profile.bio,
    profile.city,
    profile.status,
    profile.is_verified,
    profile.created_at
  from public.profiles profile
  where profile.id = target_user_id
    and profile.status = 'active';
$$;

revoke all on function public.get_public_profile_card(uuid) from public;
grant execute on function public.get_public_profile_card(uuid) to authenticated;

notify pgrst, 'reload schema';
