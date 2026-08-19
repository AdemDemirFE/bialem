do $$
declare
  founder_user_id uuid;
  founder_badge_id uuid;
begin
  select profile.id
  into founder_user_id
  from public.profiles profile
  where lower(profile.email) = 'acbaldirlioglu@gmail.com'
  limit 1;

  if founder_user_id is null then
    raise exception 'Founder profile not found';
  end if;

  insert into public.honor_badges (
    code,
    name_template,
    description,
    badge_type,
    community_id,
    minimum_check_ins,
    is_active
  )
  values (
    'bidunya-kurucusu',
    'Bialem Kurucusu',
    'Bialem platformunu hayata geçiren kurucu hesabıdır.',
    'special',
    null,
    1,
    true
  )
  on conflict (code) do update
  set
    name_template = excluded.name_template,
    description = excluded.description,
    badge_type = excluded.badge_type,
    community_id = null,
    minimum_check_ins = excluded.minimum_check_ins,
    is_active = true
  returning id into founder_badge_id;

  insert into public.user_honor_badges (
    user_id,
    badge_id,
    awarded_by,
    reason
  )
  values (
    founder_user_id,
    founder_badge_id,
    null,
    'Bialem kurucu hesabı'
  )
  on conflict (user_id, badge_id) do update
  set reason = excluded.reason;
end;
$$;

notify pgrst, 'reload schema';
