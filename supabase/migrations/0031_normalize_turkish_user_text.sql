create or replace function public.normalize_notification_turkish_text(target_text text)
returns text
language sql
immutable
set search_path = public
as $$
  select case
    when target_text is null then null
    else replace(
      replace(
        replace(
          replace(
            replace(
              replace(
                replace(
                  replace(target_text,
                    'Yeni topluluk katilim istegi',
                    'Yeni topluluk katılım isteği'
                  ),
                  ' toplulugunda yeni bir uye basvurusu var.',
                  ' topluluğunda yeni bir üye başvurusu var.'
                ),
                'Topluluk katilimin onaylandi',
                'Topluluk katılımın onaylandı'
              ),
              'Topluluk katilim istegin reddedildi',
              'Topluluk katılım isteğin reddedildi'
            ),
            'Etkinligin son kontrolde',
            'Etkinliğin son kontrolde'
          ),
          'Grup onayi tamamlandi. Yeni partner guven kontrolunden sonra yayinlanacak.',
          'Grup onayı tamamlandı. Yeni partner güven kontrolünden sonra yayınlanacak.'
        ),
        'Katilimin onaylandi',
        'Katılımın onaylandı'
      ),
      'Katilim talebin reddedildi',
      'Katılım talebin reddedildi'
    )
  end;
$$;

create or replace function public.normalize_notification_turkish()
returns trigger
language plpgsql
set search_path = public
as $$
begin
  new.title := public.normalize_notification_turkish_text(new.title);
  new.body := public.normalize_notification_turkish_text(new.body);
  return new;
end;
$$;

drop trigger if exists trg_notifications_normalize_turkish on public.notifications;
create trigger trg_notifications_normalize_turkish
before insert or update of title, body on public.notifications
for each row execute function public.normalize_notification_turkish();

update public.notifications
set
  title = public.normalize_notification_turkish_text(title),
  body = public.normalize_notification_turkish_text(body)
where
  title is distinct from public.normalize_notification_turkish_text(title)
  or body is distinct from public.normalize_notification_turkish_text(body);

create or replace function public.normalize_event_rejection_reason_turkish()
returns trigger
language plpgsql
set search_path = public
as $$
begin
  if new.rejection_reason = 'Grup moderatoru tarafindan reddedildi.' then
    new.rejection_reason := 'Grup moderatörü tarafından reddedildi.';
  end if;

  return new;
end;
$$;

drop trigger if exists trg_events_normalize_rejection_reason_turkish on public.events;
create trigger trg_events_normalize_rejection_reason_turkish
before insert or update of rejection_reason on public.events
for each row execute function public.normalize_event_rejection_reason_turkish();

update public.events
set rejection_reason = 'Grup moderatörü tarafından reddedildi.'
where rejection_reason = 'Grup moderatoru tarafindan reddedildi.';

revoke all on function public.normalize_notification_turkish_text(text) from public;
revoke all on function public.normalize_notification_turkish() from public;
revoke all on function public.normalize_event_rejection_reason_turkish() from public;

notify pgrst, 'reload schema';
