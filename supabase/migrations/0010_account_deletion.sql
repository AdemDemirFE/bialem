create or replace function public.cleanup_current_user_account()
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
begin
  if current_user_id is null then
    raise exception 'Authentication required';
  end if;

  delete from public.comments
  where (target_type = 'post' and target_id in (
    select p.id
    from public.posts p
    left join public.communities c on c.id = p.community_id
    where p.author_id = current_user_id or c.created_by = current_user_id
  ))
  or (target_type = 'event' and target_id in (
    select e.id
    from public.events e
    join public.communities c on c.id = e.community_id
    where e.created_by = current_user_id or c.created_by = current_user_id
  ))
  or (target_type = 'user_review' and target_id in (
    select ur.id
    from public.user_reviews ur
    where ur.reviewer_id = current_user_id or ur.reviewed_user_id = current_user_id
  ));

  delete from public.reports
  where (target_type = 'post' and target_id in (
    select p.id
    from public.posts p
    left join public.communities c on c.id = p.community_id
    where p.author_id = current_user_id or c.created_by = current_user_id
  ))
  or (target_type = 'event' and target_id in (
    select e.id
    from public.events e
    join public.communities c on c.id = e.community_id
    where e.created_by = current_user_id or c.created_by = current_user_id
  ))
  or (target_type = 'user' and target_id = current_user_id);

  -- Owned communities prevent profile deletion and are removed after explicit confirmation.
  delete from public.communities where created_by = current_user_id;
  delete from public.events where created_by = current_user_id;
  delete from public.profiles where id = current_user_id;
end;
$$;

revoke all on function public.cleanup_current_user_account() from public;
grant execute on function public.cleanup_current_user_account() to authenticated;
