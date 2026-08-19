drop table if exists pg_temp.legacy_communities_to_remove;

create temporary table legacy_communities_to_remove (
  id uuid primary key,
  name text not null,
  slug text not null
);

with recursive legacy_community_tree as (
  select c.id, c.name, c.slug
  from public.communities c
  where c.parent_id is null
    and (
      lower(trim(c.name)) in (
        'kitap kulübü',
        'kitap kulubu',
        'ankara kitap kulübü',
        'ankara kitap kulubu',
        'ankara litap kulübü',
        'ankara litap kulubu',
        'kamp ve doğa severler',
        'kamp ve doga severler',
        'kamp ve doğa sevreler',
        'kamp ve doga sevreler'
      )
      or lower(trim(c.slug)) in (
        'kitap-kulubu',
        'ankara-kitap-kulubu',
        'ankara-litap-kulubu',
        'kamp-ve-doga-severler',
        'kamp-doga-severler',
        'kamp-ve-doga-sevreler',
        'kamp-doga-sevreler'
      )
    )

  union all

  select child.id, child.name, child.slug
  from public.communities child
  join legacy_community_tree parent on parent.id = child.parent_id
)
insert into legacy_communities_to_remove (id, name, slug)
select distinct id, name, slug
from legacy_community_tree
on conflict (id) do nothing;

-- Polymorphic targets do not have foreign keys, so remove them before their content.
delete from public.comments comment
where (
  comment.target_type = 'post'
  and comment.target_id in (
    select post.id
    from public.posts post
    where post.community_id in (select id from legacy_communities_to_remove)
  )
)
or (
  comment.target_type = 'event'
  and comment.target_id in (
    select event.id
    from public.events event
    where event.community_id in (select id from legacy_communities_to_remove)
  )
);

delete from public.reports report
where (
  report.target_type = 'post'
  and report.target_id in (
    select post.id
    from public.posts post
    where post.community_id in (select id from legacy_communities_to_remove)
  )
)
or (
  report.target_type = 'event'
  and report.target_id in (
    select event.id
    from public.events event
    where event.community_id in (select id from legacy_communities_to_remove)
  )
);

delete from public.notifications notification
where notification.payload ->> 'community_id' in (
  select id::text from legacy_communities_to_remove
)
or notification.payload ->> 'post_id' in (
  select post.id::text
  from public.posts post
  where post.community_id in (select id from legacy_communities_to_remove)
)
or notification.payload ->> 'event_id' in (
  select event.id::text
  from public.events event
  where event.community_id in (select id from legacy_communities_to_remove)
);

-- Related memberships, events, posts and stories are removed by foreign-key cascades.
delete from public.communities community
where community.id in (select id from legacy_communities_to_remove);

select
  name as silinen_topluluk,
  slug,
  'Silindi' as durum
from legacy_communities_to_remove
order by name;
