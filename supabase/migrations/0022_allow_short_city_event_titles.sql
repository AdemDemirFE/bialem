alter table public.city_events
drop constraint if exists city_events_title_check;

alter table public.city_events
add constraint city_events_title_check
check (char_length(trim(title)) between 1 and 160);

notify pgrst, 'reload schema';
