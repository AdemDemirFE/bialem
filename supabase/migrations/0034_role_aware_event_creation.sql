create or replace function public.get_my_event_creation_groups()
returns table (
  id uuid,
  name text,
  slug text,
  creation_mode text
)
language sql
stable
security definer
set search_path = public
as $$
  select
    community.id,
    community.name,
    community.slug,
    case
      when public.is_admin()
        or public.is_community_manager(community.id, auth.uid())
        or public.has_community_assistant_permission(community.id, auth.uid(), 'review_events')
      then 'direct'
      else 'proposal'
    end
  from public.communities community
  where auth.uid() is not null
    and community.community_type = 'group'
    and community.parent_id is not null
    and (
      public.is_admin()
      or public.is_community_manager(community.id, auth.uid())
      or public.has_community_assistant_permission(community.id, auth.uid(), 'review_events')
      or public.is_approved_community_member(community.id, auth.uid())
    )
  order by community.created_at desc;
$$;

create or replace function public.create_group_event(
  target_community_id uuid,
  target_title text,
  target_description text,
  target_starts_at timestamptz,
  target_ends_at timestamptz,
  target_location_name text,
  target_address_text text,
  target_latitude numeric,
  target_longitude numeric,
  target_capacity integer
)
returns table (
  event_id uuid,
  event_status text,
  creation_mode text
)
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  target_group public.communities%rowtype;
  target_parent public.communities%rowtype;
  can_create_directly boolean;
  created_event_id uuid;
  final_status text;
  final_platform_status text;
begin
  if current_user_id is null then
    raise exception 'Etkinlik oluşturmak için oturum açmalısınız';
  end if;

  select * into target_group
  from public.communities
  where id = target_community_id
    and community_type = 'group'
    and parent_id is not null;

  if not found then raise exception 'Grup bulunamadı'; end if;

  if not (
    public.is_admin()
    or public.is_community_manager(target_group.id, current_user_id)
    or public.has_community_assistant_permission(target_group.id, current_user_id, 'review_events')
    or public.is_approved_community_member(target_group.id, current_user_id)
  ) then
    raise exception 'Bu grupta etkinlik oluşturma yetkiniz yok';
  end if;

  if char_length(trim(coalesce(target_title, ''))) < 3 then
    raise exception 'Etkinlik başlığı en az 3 karakter olmalıdır';
  end if;
  if target_starts_at is null or target_starts_at <= now() then
    raise exception 'Başlangıç tarihi gelecekte olmalıdır';
  end if;
  if target_ends_at is not null and target_ends_at <= target_starts_at then
    raise exception 'Bitiş tarihi başlangıç tarihinden sonra olmalıdır';
  end if;
  if target_capacity is not null and target_capacity <= 0 then
    raise exception 'Kapasite sıfırdan büyük olmalıdır';
  end if;
  if target_latitude is not null and (target_latitude < -90 or target_latitude > 90) then
    raise exception 'Geçersiz enlem değeri';
  end if;
  if target_longitude is not null and (target_longitude < -180 or target_longitude > 180) then
    raise exception 'Geçersiz boylam değeri';
  end if;

  can_create_directly :=
    public.is_admin()
    or public.is_community_manager(target_group.id, current_user_id)
    or public.has_community_assistant_permission(target_group.id, current_user_id, 'review_events');

  select * into target_parent
  from public.communities
  where id = target_group.parent_id;

  final_status := case
    when not can_create_directly then 'pending_approval'
    when public.is_admin() then 'published'
    when target_parent.community_type = 'partner_hub'
      and target_parent.partner_trust_level = 'new'
    then 'pending_approval'
    else 'published'
  end;

  final_platform_status := case
    when not can_create_directly then 'not_required'
    when final_status = 'pending_approval' then 'pending'
    else 'approved'
  end;

  insert into public.events (
    community_id,
    created_by,
    title,
    description,
    starts_at,
    ends_at,
    location_name,
    address_text,
    latitude,
    longitude,
    capacity,
    status,
    published_at
  )
  values (
    target_group.id,
    current_user_id,
    trim(target_title),
    nullif(trim(coalesce(target_description, '')), ''),
    target_starts_at,
    target_ends_at,
    nullif(trim(coalesce(target_location_name, '')), ''),
    nullif(trim(coalesce(target_address_text, '')), ''),
    target_latitude,
    target_longitude,
    target_capacity,
    final_status,
    case when final_status = 'published' then now() else null end
  )
  returning id into created_event_id;

  -- The federation trigger initializes moderation fields conservatively.
  update public.events
  set
    group_moderation_status = case when can_create_directly then 'approved' else 'pending' end,
    platform_moderation_status = final_platform_status
  where id = created_event_id;

  if can_create_directly and final_status = 'pending_approval' then
    insert into public.notifications (user_id, type, title, body, payload)
    values (
      current_user_id,
      'event_platform_review',
      'Etkinliğin son kontrolde',
      'Grup onayı gerekmiyor. Etkinlik, yeni partner güven kontrolünden sonra yayınlanacak.',
      jsonb_build_object('event_id', created_event_id)
    );
  end if;

  return query
  select
    created_event_id,
    final_status,
    case when can_create_directly then 'direct' else 'proposal' end;
end;
$$;

revoke all on function public.get_my_event_creation_groups() from public;
revoke all on function public.create_group_event(uuid, text, text, timestamptz, timestamptz, text, text, numeric, numeric, integer) from public;
grant execute on function public.get_my_event_creation_groups() to authenticated;
grant execute on function public.create_group_event(uuid, text, text, timestamptz, timestamptz, text, text, numeric, numeric, integer) to authenticated;

notify pgrst, 'reload schema';
