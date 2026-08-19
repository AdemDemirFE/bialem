-- Role checks are used from RLS policies, so they must not be evaluated through
-- the same user_roles policies they protect.
create or replace function public.user_has_role(target_user_id uuid, target_role text)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.user_roles user_role
    join public.roles role on role.id = user_role.role_id
    where user_role.user_id = target_user_id
      and role.code = target_role
  );
$$;

create or replace function public.is_admin()
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select public.user_has_role(auth.uid(), 'admin');
$$;

create or replace function public.is_moderator_or_admin()
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select public.user_has_role(auth.uid(), 'moderator')
      or public.user_has_role(auth.uid(), 'admin');
$$;

notify pgrst, 'reload schema';
