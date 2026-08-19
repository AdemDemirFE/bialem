create or replace function public.is_approved_community_member(target_community_id uuid, target_user_id uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.community_members cm
    where cm.community_id = target_community_id
      and cm.user_id = target_user_id
      and cm.status = 'approved'
  );
$$;

create or replace function public.can_view_community_basic(target_community_id uuid, target_user_id uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.communities c
    where c.id = target_community_id
      and (
        c.visibility = 'public'
        or c.created_by = target_user_id
        or public.user_has_role(target_user_id, 'admin')
        or public.is_approved_community_member(c.id, target_user_id)
      )
  );
$$;

drop policy if exists communities_public_read on public.communities;
create policy communities_public_read
on public.communities
for select
using (
  visibility = 'public'
  or created_by = auth.uid()
  or public.is_admin()
  or public.is_approved_community_member(id, auth.uid())
);

drop policy if exists community_members_read_related_or_admin on public.community_members;
create policy community_members_read_related_or_admin
on public.community_members
for select
using (
  user_id = auth.uid()
  or public.is_admin()
  or public.can_view_community_basic(community_id, auth.uid())
);
