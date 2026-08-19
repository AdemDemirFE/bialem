create or replace function public.create_event_status_notification()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  if old.status is distinct from new.status and new.status in ('published', 'rejected', 'cancelled') then
    insert into public.notifications (user_id, type, title, body, payload)
    values (
      new.created_by,
      'event_' || new.status,
      case new.status
        when 'published' then 'Etkinliğin yayınlandı'
        when 'rejected' then 'Etkinlik talebin reddedildi'
        else 'Etkinliğin iptal edildi'
      end,
      case
        when new.status = 'rejected' and new.rejection_reason is not null then new.title || ': ' || new.rejection_reason
        else new.title
      end,
      jsonb_build_object('event_id', new.id)
    );
  end if;

  return new;
end;
$$;

drop trigger if exists trg_events_notify_status on public.events;
create trigger trg_events_notify_status
after update of status on public.events
for each row
execute function public.create_event_status_notification();

create or replace function public.create_comment_notification()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
  recipient_id uuid;
begin
  if new.target_type = 'post' then
    select author_id into recipient_id from public.posts where id = new.target_id;
  elsif new.target_type = 'event' then
    select created_by into recipient_id from public.events where id = new.target_id;
  end if;

  if recipient_id is not null and recipient_id <> new.author_id then
    insert into public.notifications (user_id, type, title, body, payload)
    values (
      recipient_id,
      'new_comment',
      'Yeni bir yorum aldın',
      left(new.body, 140),
      case
        when new.target_type = 'post' then jsonb_build_object('post_id', new.target_id)
        else jsonb_build_object('event_id', new.target_id)
      end
    );
  end if;

  return new;
end;
$$;

drop trigger if exists trg_comments_notify_owner on public.comments;
create trigger trg_comments_notify_owner
after insert on public.comments
for each row
execute function public.create_comment_notification();

create or replace function public.create_user_review_notification()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  if new.reviewer_id <> new.reviewed_user_id then
    insert into public.notifications (user_id, type, title, body, payload)
    values (
      new.reviewed_user_id,
      'user_review',
      'Yeni bir değerlendirme aldın',
      new.rating || ' yıldız' || case when new.review_text is not null then ': ' || left(new.review_text, 120) else '' end,
      jsonb_build_object('user_id', new.reviewer_id)
    );
  end if;

  return new;
end;
$$;

drop trigger if exists trg_user_reviews_notify_user on public.user_reviews;
create trigger trg_user_reviews_notify_user
after insert on public.user_reviews
for each row
execute function public.create_user_review_notification();
