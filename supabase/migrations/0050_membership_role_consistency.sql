-- Root communities have a single authoritative moderator in lead_moderator_id.
-- Normalize legacy manager rows so ordinary members can safely leave.
update public.community_members member
set role = 'member'
from public.communities community
where member.community_id = community.id
  and community.parent_id is null
  and member.role in ('manager', 'owner')
  and member.user_id is distinct from community.lead_moderator_id;

create or replace function public.cancel_community_membership_request(target_community_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
  if auth.uid() is null then
    raise exception 'Oturum acman gerekiyor';
  end if;

  delete from public.community_members member
  where member.community_id = target_community_id
    and member.user_id = auth.uid()
    and member.status = 'pending';

  if not found then
    raise exception 'Geri cekilebilecek bekleyen katilim istegi bulunamadi';
  end if;
end;
$$;

create or replace function public.leave_community(target_community_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  target_parent_id uuid;
  current_status text;
begin
  if auth.uid() is null then
    raise exception 'Oturum acman gerekiyor';
  end if;

  select community.parent_id, member.status
  into target_parent_id, current_status
  from public.community_members member
  join public.communities community on community.id = member.community_id
  where member.community_id = target_community_id
    and member.user_id = auth.uid()
  for update of member;

  if current_status is distinct from 'approved' then
    raise exception 'Onayli topluluk uyeligi bulunamadi';
  end if;

  if public.is_community_manager(target_community_id, auth.uid()) then
    raise exception 'Moderatorler ayrilmadan once rollerini devretmelidir';
  end if;

  if target_parent_id is null and exists (
    select 1
    from public.community_moderator_assistants assistant
    where assistant.community_id = target_community_id
      and assistant.user_id = auth.uid()
  ) then
    raise exception 'Moderator yardimciligi kaldirilmadan topluluktan ayrilamazsin';
  end if;

  if target_parent_id is null then
    -- Remove the root membership even if it carries a stale legacy role.
    delete from public.community_members member
    where member.community_id = target_community_id
      and member.user_id = auth.uid();

    -- Child moderator roles are never removed implicitly.
    delete from public.community_members member
    using public.communities community
    where member.community_id = community.id
      and community.parent_id = target_community_id
      and member.user_id = auth.uid()
      and member.role = 'member';
  else
    delete from public.community_members member
    where member.community_id = target_community_id
      and member.user_id = auth.uid();
  end if;
end;
$$;

revoke all on function public.cancel_community_membership_request(uuid) from public;
revoke all on function public.leave_community(uuid) from public;
grant execute on function public.cancel_community_membership_request(uuid) to authenticated;
grant execute on function public.leave_community(uuid) to authenticated;

notify pgrst, 'reload schema';
