-- Founder accounts are the initial break-glass administrators.
insert into public.user_roles (user_id, role_id)
select profile.id, role.id
from public.profiles profile
cross join public.roles role
where lower(profile.email) in (
  'acbaldirlioglu@gmail.com',
  'mehmetas58@gmail.com'
)
  and role.code = 'admin'
on conflict (user_id, role_id) do nothing;

notify pgrst, 'reload schema';
