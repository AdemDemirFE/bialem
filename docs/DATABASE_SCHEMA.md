# Bialem Database Schema

> Canlı PostgreSQL şeması (public schema) — dev ortamı `localhost:15432/bialem` (container: `bialem-postgresql-1`).
> Bu doküman `information_schema` sorgusuyla üretilmiştir, kaynak: PostgreSQL MCP / docker exec.

Oluşturulma: 2026-09-03T15:30:58.248Z

## Tablo Sayısı

- Toplam tablo: **82** (Liquibase `databasechangelog*` tabloları hariç içerilmez)

## İçindekiler

- [account_preferences](#account\_preferences)
- [ai_usage_log](#ai\_usage\_log)
- [app_notification](#app\_notification)
- [app_role](#app\_role)
- [audit_log](#audit\_log)
- [block](#block)
- [city_event](#city\_event)
- [city_event_interest](#city\_event\_interest)
- [city_event_sync_log](#city\_event\_sync\_log)
- [city_event_ticket_offer](#city\_event\_ticket\_offer)
- [comment](#comment)
- [community](#community)
- [community_member](#community\_member)
- [community_moderator_assistant](#community\_moderator\_assistant)
- [direct_conversation](#direct\_conversation)
- [direct_message](#direct\_message)
- [event](#event)
- [event_message](#event\_message)
- [event_participant](#event\_participant)
- [event_rating](#event\_rating)
- [event_ticket](#event\_ticket)
- [follow](#follow)
- [follow_request](#follow\_request)
- [hashtag](#hashtag)
- [honor_badge](#honor\_badge)
- [jhi_authority](#jhi\_authority)
- [jhi_user](#jhi\_user)
- [jhi_user_authority](#jhi\_user\_authority)
- [media_asset](#media\_asset)
- [notification](#notification)
- [notification_delivery_log](#notification\_delivery\_log)
- [notification_outbox](#notification\_outbox)
- [notification_template](#notification\_template)
- [order_item](#order\_item)
- [partner_offer](#partner\_offer)
- [partner_offer_redemption](#partner\_offer\_redemption)
- [partner_venue](#partner\_venue)
- [partner_venue_staff](#partner\_venue\_staff)
- [payment](#payment)
- [platform_team_member](#platform\_team\_member)
- [post](#post)
- [post_media](#post\_media)
- [profile](#profile)
- [promotion](#promotion)
- [push_device_token](#push\_device\_token)
- [push_token](#push\_token)
- [report](#report)
- [store_address](#store\_address)
- [store_bank_transfer](#store\_bank\_transfer)
- [store_brand](#store\_brand)
- [store_cart_item](#store\_cart\_item)
- [store_category](#store\_category)
- [store_coupon](#store\_coupon)
- [store_order](#store\_order)
- [store_order_item](#store\_order\_item)
- [store_order_status_history](#store\_order\_status\_history)
- [store_payment](#store\_payment)
- [store_payment_refund](#store\_payment\_refund)
- [store_payment_transaction](#store\_payment\_transaction)
- [store_payment_webhook](#store\_payment\_webhook)
- [store_product](#store\_product)
- [store_product_attribute](#store\_product\_attribute)
- [store_product_image](#store\_product\_image)
- [store_product_variant](#store\_product\_variant)
- [store_review](#store\_review)
- [store_review_image](#store\_review\_image)
- [store_shipping](#store\_shipping)
- [store_wishlist](#store\_wishlist)
- [story](#story)
- [story_community_target](#story\_community\_target)
- [story_element](#story\_element)
- [story_group](#story\_group)
- [story_hashtag](#story\_hashtag)
- [story_reaction](#story\_reaction)
- [story_view](#story\_view)
- [ticket](#ticket)
- [ticket_order](#ticket\_order)
- [user_honor_badge](#user\_honor\_badge)
- [user_notification_preference](#user\_notification\_preference)
- [user_review](#user\_review)
- [user_role](#user\_role)
- [﻿account_preferences](#﻿account\_preferences)

## account_preferences

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `discoverable` | boolean | NO | `` |
| `show_city` | boolean | NO | `` |
| `show_follow_connections` | boolean | NO | `` |
| `allow_follows` | boolean | NO | `` |
| `require_follow_approval` | boolean | NO | `` |
| `allow_messages_from` | varchar(255) | NO | `` |
| `notify_events` | boolean | NO | `` |
| `notify_communities` | boolean | NO | `` |
| `notify_social` | boolean | NO | `` |
| `notify_advantages` | boolean | NO | `` |
| `notify_system` | boolean | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `profile_id` | bigint | NO | `` |

## ai_usage_log

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `created_at` | timestamp | NO | `` |
| `user_id` | bigint | YES | `` |

**Foreign Keys:**

- `user_id → profile(id)`

## app_notification

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `user_id` | bigint | NO | `` |
| `title` | varchar(200) | NO | `` |
| `body` | varchar(2000) | YES | `` |
| `notification_type` | varchar(80) | NO | `` |
| `reference_id` | varchar(120) | YES | `` |
| `route` | varchar(500) | YES | `` |
| `is_read` | boolean | NO | `false` |
| `created_at` | timestamp | NO | `` |
| `read_at` | timestamp | YES | `` |
| `template_id` | bigint | YES | `` |
| `event_id` | varchar(120) | YES | `` |
| `payload` | text | YES | `` |
| `scheduled_at` | timestamp | YES | `` |
| `push_status` | varchar(40) | YES | `` |
| `push_sent_at` | timestamp | YES | `` |
| `correlation_id` | varchar(120) | YES | `` |
| `idempotency_key` | varchar(500) | YES | `` |
| `actor_user_id` | bigint | YES | `` |
| `reference_type` | varchar(80) | YES | `` |

**Foreign Keys:**

- `actor_user_id → jhi_user(id)`
- `template_id → notification_template(id)`
- `user_id → jhi_user(id)`

## app_role

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `code` | varchar(80) | NO | `` |
| `name` | varchar(160) | NO | `` |
| `created_at` | timestamp | NO | `` |

## audit_log

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `actor_id` | bigint | YES | `` |
| `actor_login` | varchar(80) | YES | `` |
| `action` | varchar(80) | NO | `` |
| `resource_type` | varchar(80) | NO | `` |
| `resource_id` | varchar(120) | YES | `` |
| `ip_address` | varchar(64) | YES | `` |
| `metadata` | jsonb | YES | `` |
| `created_at` | timestamp | NO | `` |

**Foreign Keys:**

- `actor_id → jhi_user(id)`

## block

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `created_at` | timestamp | NO | `` |
| `blocker_id` | bigint | YES | `` |
| `blocked_user_id` | bigint | YES | `` |

**Foreign Keys:**

- `blocked_user_id → profile(id)`
- `blocker_id → profile(id)`

## city_event

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `title` | varchar(160) | NO | `` |
| `description` | text | YES | `` |
| `category` | varchar(80) | NO | `` |
| `city` | varchar(80) | NO | `` |
| `venue_name` | varchar(200) | YES | `` |
| `address_text` | varchar(500) | YES | `` |
| `starts_at` | timestamp | NO | `` |
| `ends_at` | timestamp | YES | `` |
| `cover_image_url` | varchar(2048) | YES | `` |
| `price_label` | varchar(120) | YES | `` |
| `source_name` | varchar(120) | NO | `` |
| `source_url` | varchar(2048) | YES | `` |
| `ticket_url` | varchar(2048) | YES | `` |
| `status` | varchar(255) | NO | `` |
| `provider_code` | varchar(50) | NO | `` |
| `external_id` | varchar(120) | YES | `` |
| `last_synced_at` | timestamp | YES | `` |
| `raw_payload` | text | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |

## city_event_interest

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `looking_for_company` | boolean | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `city_event_id` | bigint | YES | `` |
| `user_id` | bigint | YES | `` |

**Foreign Keys:**

- `city_event_id → city_event(id)`
- `user_id → profile(id)`

## city_event_sync_log

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `provider_code` | varchar(50) | NO | `` |
| `status` | varchar(20) | NO | `` |
| `imported_count` | integer | NO | `` |
| `error_message` | text | YES | `` |
| `started_at` | timestamp | NO | `` |
| `finished_at` | timestamp | NO | `` |

## city_event_ticket_offer

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `provider_code` | varchar(50) | NO | `` |
| `external_offer_id` | varchar(120) | NO | `` |
| `seller_name` | varchar(160) | NO | `` |
| `purchase_url` | varchar(2048) | NO | `` |
| `currency` | varchar(8) | YES | `` |
| `min_price` | numeric | YES | `` |
| `max_price` | numeric | YES | `` |
| `price_label` | varchar(120) | YES | `` |
| `availability` | varchar(255) | NO | `` |
| `fees_included` | boolean | YES | `` |
| `is_official` | boolean | NO | `` |
| `last_checked_at` | timestamp | NO | `` |
| `raw_payload` | text | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `city_event_id` | bigint | YES | `` |

**Foreign Keys:**

- `city_event_id → city_event(id)`

## comment

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `target_type` | varchar(255) | NO | `` |
| `target_id` | varchar(64) | NO | `` |
| `body` | varchar(4000) | NO | `` |
| `moderation_status` | varchar(255) | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `author_id` | bigint | YES | `` |

**Foreign Keys:**

- `author_id → profile(id)`

## community

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `name` | varchar(160) | NO | `` |
| `slug` | varchar(80) | NO | `` |
| `description` | text | YES | `` |
| `visibility` | varchar(255) | NO | `` |
| `cover_image_url` | varchar(2048) | YES | `` |
| `community_type` | varchar(255) | NO | `` |
| `partner_trust_level` | varchar(255) | NO | `` |
| `is_verified_partner` | boolean | NO | `` |
| `is_discoverable` | boolean | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `parent_id` | bigint | YES | `` |
| `category_hub_id` | bigint | YES | `` |
| `created_by_id` | bigint | YES | `` |
| `lead_moderator_id` | bigint | YES | `` |
| `cover_image_id` | bigint | YES | `` |

**Foreign Keys:**

- `category_hub_id → community(id)`
- `cover_image_id → media_asset(id)`
- `created_by_id → profile(id)`
- `lead_moderator_id → profile(id)`
- `parent_id → community(id)`

## community_member

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `role` | varchar(255) | NO | `` |
| `status` | varchar(255) | NO | `` |
| `created_at` | timestamp | NO | `` |
| `community_id` | bigint | YES | `` |
| `user_id` | bigint | YES | `` |

**Foreign Keys:**

- `community_id → community(id)`
- `user_id → profile(id)`

## community_moderator_assistant

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `can_manage_groups` | boolean | NO | `` |
| `can_review_events` | boolean | NO | `` |
| `can_manage_participants` | boolean | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `community_id` | bigint | YES | `` |
| `user_id` | bigint | YES | `` |

**Foreign Keys:**

- `community_id → community(id)`
- `user_id → profile(id)`

## direct_conversation

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `participant_one_id` | bigint | NO | `` |
| `participant_two_id` | bigint | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |

**Foreign Keys:**

- `participant_one_id → profile(id)`
- `participant_two_id → profile(id)`

## direct_message

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `conversation_id` | bigint | NO | `` |
| `sender_id` | bigint | NO | `` |
| `body` | varchar(2000) | NO | `` |
| `created_at` | timestamp | NO | `` |
| `read_at` | timestamp | YES | `` |

**Foreign Keys:**

- `conversation_id → direct_conversation(id)`
- `sender_id → profile(id)`

## event

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `title` | varchar(200) | NO | `` |
| `description` | text | YES | `` |
| `starts_at` | timestamp | NO | `` |
| `ends_at` | timestamp | YES | `` |
| `location_name` | varchar(200) | YES | `` |
| `address_text` | varchar(500) | YES | `` |
| `latitude` | numeric | YES | `` |
| `longitude` | numeric | YES | `` |
| `cover_image_url` | varchar(2048) | YES | `` |
| `capacity` | integer | YES | `` |
| `status` | varchar(255) | NO | `` |
| `rejection_reason` | varchar(1000) | YES | `` |
| `published_at` | timestamp | YES | `` |
| `published_to_discovery` | boolean | NO | `` |
| `group_moderation_status` | varchar(255) | NO | `` |
| `platform_moderation_status` | varchar(255) | NO | `` |
| `cancelled_at` | timestamp | YES | `` |
| `cancellation_reason` | varchar(1000) | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `community_id` | bigint | YES | `` |
| `category_id` | bigint | YES | `` |
| `created_by_id` | bigint | YES | `` |
| `cancelled_by_id` | bigint | YES | `` |
| `cover_image_id` | bigint | YES | `` |

**Foreign Keys:**

- `cancelled_by_id → profile(id)`
- `category_id → community(id)`
- `community_id → community(id)`
- `cover_image_id → media_asset(id)`
- `created_by_id → profile(id)`

## event_message

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `body` | varchar(1000) | NO | `` |
| `moderation_status` | varchar(255) | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `event_id` | bigint | YES | `` |
| `author_id` | bigint | YES | `` |

**Foreign Keys:**

- `author_id → profile(id)`
- `event_id → event(id)`

## event_participant

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `status` | varchar(255) | NO | `` |
| `note` | varchar(500) | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `event_id` | bigint | YES | `` |
| `user_id` | bigint | YES | `` |

**Foreign Keys:**

- `event_id → event(id)`
- `user_id → profile(id)`

## event_rating

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `rating` | integer | NO | `` |
| `review_text` | varchar(2000) | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `event_id` | bigint | YES | `` |
| `user_id` | bigint | YES | `` |

**Foreign Keys:**

- `event_id → event(id)`
- `user_id → profile(id)`

## event_ticket

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `name` | varchar(160) | NO | `` |
| `description` | varchar(2000) | YES | `` |
| `price` | numeric | NO | `` |
| `currency` | varchar(8) | NO | `` |
| `quantity` | integer | NO | `` |
| `sold_quantity` | integer | YES | `0` |
| `sale_start_date` | timestamp | YES | `` |
| `sale_end_date` | timestamp | YES | `` |
| `status` | varchar(255) | NO | `` |
| `event_id` | bigint | YES | `` |

**Foreign Keys:**

- `event_id → event(id)`

## follow

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `created_at` | timestamp | NO | `` |
| `follower_id` | bigint | YES | `` |
| `followed_id` | bigint | YES | `` |

**Foreign Keys:**

- `followed_id → profile(id)`
- `follower_id → profile(id)`

## follow_request

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `created_at` | timestamp | NO | `` |
| `requester_id` | bigint | YES | `` |
| `target_user_id` | bigint | YES | `` |

**Foreign Keys:**

- `requester_id → profile(id)`
- `target_user_id → profile(id)`

## hashtag

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `name` | varchar(100) | NO | `` |
| `normalized_name` | varchar(100) | NO | `` |
| `usage_count` | bigint | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `is_active` | boolean | NO | `` |

## honor_badge

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `code` | varchar(80) | NO | `` |
| `name_template` | varchar(160) | NO | `` |
| `description` | varchar(500) | NO | `` |
| `badge_type` | varchar(255) | NO | `` |
| `minimum_check_ins` | integer | NO | `` |
| `is_active` | boolean | NO | `` |
| `created_at` | timestamp | NO | `` |
| `community_id` | bigint | YES | `` |

**Foreign Keys:**

- `community_id → community(id)`

## jhi_authority

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `name` | varchar(50) | NO | `` |

## jhi_user

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `login` | varchar(50) | NO | `` |
| `password_hash` | varchar(60) | NO | `` |
| `first_name` | varchar(50) | YES | `` |
| `last_name` | varchar(50) | YES | `` |
| `email` | varchar(191) | YES | `` |
| `image_url` | varchar(256) | YES | `` |
| `activated` | boolean | NO | `` |
| `lang_key` | varchar(10) | YES | `` |
| `activation_key` | varchar(20) | YES | `` |
| `reset_key` | varchar(64) | YES | `` |
| `created_by` | varchar(50) | NO | `` |
| `created_date` | timestamp | YES | `` |
| `reset_date` | timestamp | YES | `` |
| `last_modified_by` | varchar(50) | YES | `` |
| `last_modified_date` | timestamp | YES | `` |

## jhi_user_authority

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `user_id` | bigint | NO | `` |
| `authority_name` | varchar(50) | NO | `` |

**Foreign Keys:**

- `authority_name → jhi_authority(name)`
- `user_id → jhi_user(id)`

## media_asset

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `original_file_name` | varchar(255) | NO | `` |
| `content_type` | varchar(100) | NO | `` |
| `file_size` | bigint | NO | `` |
| `checksum` | varchar(64) | NO | `` |
| `width` | integer | YES | `` |
| `height` | integer | YES | `` |
| `binary_data` | bytea | NO | `` |
| `created_by_id` | bigint | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |

**Foreign Keys:**

- `created_by_id → jhi_user(id)`

## notification

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `type` | varchar(80) | NO | `` |
| `title` | varchar(200) | NO | `` |
| `body` | varchar(2000) | YES | `` |
| `payload` | text | YES | `` |
| `is_read` | boolean | NO | `` |
| `created_at` | timestamp | NO | `` |
| `user_id` | bigint | YES | `` |
| `notification_type` | varchar(80) | YES | `` |
| `reference_id` | varchar(120) | YES | `` |
| `route` | varchar(500) | YES | `` |
| `read_at` | timestamp | YES | `` |

**Foreign Keys:**

- `user_id → profile(id)`

## notification_delivery_log

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `notification_id` | bigint | NO | `` |
| `push_device_id` | bigint | YES | `` |
| `provider` | varchar(50) | NO | `` |
| `status` | varchar(40) | NO | `` |
| `provider_message_id` | varchar(200) | YES | `` |
| `error_code` | varchar(120) | YES | `` |
| `error_message` | varchar(2000) | YES | `` |
| `attempt_number` | integer | YES | `` |
| `sent_at` | timestamp | YES | `` |
| `created_at` | timestamp | NO | `` |

**Foreign Keys:**

- `notification_id → app_notification(id)`
- `push_device_id → push_device_token(id)`

## notification_outbox

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `notification_id` | bigint | NO | `` |
| `user_id` | bigint | NO | `` |
| `status` | varchar(40) | NO | `` |
| `scheduled_at` | timestamp | YES | `` |
| `attempt_count` | integer | NO | `0` |
| `max_attempts` | integer | NO | `5` |
| `next_attempt_at` | timestamp | YES | `` |
| `last_error` | varchar(2000) | YES | `` |
| `created_at` | timestamp | NO | `` |
| `sent_at` | timestamp | YES | `` |
| `processed_at` | timestamp | YES | `` |
| `idempotency_key` | varchar(500) | YES | `` |

**Foreign Keys:**

- `notification_id → app_notification(id)`
- `user_id → jhi_user(id)`

## notification_template

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `code` | varchar(80) | NO | `` |
| `event_type` | varchar(80) | NO | `` |
| `name` | varchar(120) | NO | `` |
| `title_template` | varchar(200) | NO | `` |
| `body_template` | varchar(2000) | YES | `` |
| `route_template` | varchar(500) | YES | `` |
| `enabled` | boolean | NO | `true` |
| `in_app_enabled` | boolean | NO | `true` |
| `push_enabled` | boolean | NO | `true` |
| `priority` | varchar(20) | NO | `'NORMAL'::character varying` |
| `target_strategy` | varchar(80) | YES | `` |
| `schedule_type` | varchar(40) | NO | `'IMMEDIATE'::character varying` |
| `delay_minutes` | integer | YES | `` |
| `preferred_send_time` | varchar(10) | YES | `` |
| `timezone` | varchar(80) | YES | `'Europe/Istanbul'::character varying` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |
| `created_by` | varchar(80) | YES | `` |
| `updated_by` | varchar(80) | YES | `` |

## order_item

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `quantity` | integer | NO | `` |
| `unit_price` | numeric | NO | `` |
| `total_price` | numeric | NO | `` |
| `order_id` | bigint | YES | `` |
| `ticket_id` | bigint | YES | `` |

**Foreign Keys:**

- `order_id → ticket_order(id)`
- `ticket_id → event_ticket(id)`

## partner_offer

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `title` | varchar(160) | NO | `` |
| `description` | text | YES | `` |
| `discount_percent` | numeric | NO | `` |
| `minimum_spend` | numeric | YES | `` |
| `maximum_discount` | numeric | YES | `` |
| `valid_from` | timestamp | NO | `` |
| `valid_until` | timestamp | YES | `` |
| `per_user_limit` | integer | YES | `` |
| `terms` | text | YES | `` |
| `is_active` | boolean | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `venue_id` | bigint | YES | `` |
| `valid_days` | varchar(32) | YES | `` |
| `daily_start_time` | time | YES | `` |
| `daily_end_time` | time | YES | `` |

**Foreign Keys:**

- `venue_id → partner_venue(id)`

## partner_offer_redemption

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `token` | uuid | NO | `` |
| `redemption_code` | varchar(32) | NO | `` |
| `status` | varchar(255) | NO | `` |
| `issued_at` | timestamp | NO | `` |
| `expires_at` | timestamp | NO | `` |
| `redeemed_at` | timestamp | YES | `` |
| `order_amount` | numeric | YES | `` |
| `discount_amount` | numeric | YES | `` |
| `offer_id` | bigint | YES | `` |
| `venue_id` | bigint | YES | `` |
| `user_id` | bigint | YES | `` |
| `redeemed_by_id` | bigint | YES | `` |

**Foreign Keys:**

- `offer_id → partner_offer(id)`
- `redeemed_by_id → profile(id)`
- `user_id → profile(id)`
- `venue_id → partner_venue(id)`

## partner_venue

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `name` | varchar(160) | NO | `` |
| `slug` | varchar(80) | NO | `` |
| `description` | text | YES | `` |
| `category` | varchar(255) | NO | `` |
| `logo_url` | varchar(2048) | YES | `` |
| `cover_image_url` | varchar(2048) | YES | `` |
| `address` | varchar(500) | NO | `` |
| `city` | varchar(80) | NO | `` |
| `latitude` | numeric | YES | `` |
| `longitude` | numeric | YES | `` |
| `phone` | varchar(40) | YES | `` |
| `website_url` | varchar(2048) | YES | `` |
| `instagram_url` | varchar(2048) | YES | `` |
| `is_featured` | boolean | NO | `` |
| `is_active` | boolean | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `logo_image_id` | bigint | YES | `` |
| `cover_image_id` | bigint | YES | `` |

**Foreign Keys:**

- `cover_image_id → media_asset(id)`
- `logo_image_id → media_asset(id)`

## partner_venue_staff

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `is_active` | boolean | NO | `` |
| `created_at` | timestamp | NO | `` |
| `venue_id` | bigint | YES | `` |
| `user_id` | bigint | YES | `` |

**Foreign Keys:**

- `user_id → profile(id)`
- `venue_id → partner_venue(id)`

## payment

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `provider` | varchar(255) | NO | `` |
| `provider_transaction_id` | varchar(255) | YES | `` |
| `amount` | numeric | NO | `` |
| `currency` | varchar(8) | NO | `` |
| `status` | varchar(255) | NO | `` |
| `payment_date` | timestamp | YES | `` |
| `failure_reason` | varchar(1000) | YES | `` |
| `provider_response` | varchar(4000) | YES | `` |
| `idempotency_key` | varchar(255) | NO | `` |
| `callback_payload` | varchar(4000) | YES | `` |
| `order_id` | bigint | YES | `` |

**Foreign Keys:**

- `order_id → ticket_order(id)`

## platform_team_member

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `role_code` | varchar(255) | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `user_id` | bigint | YES | `` |
| `assigned_by_id` | bigint | YES | `` |

**Foreign Keys:**

- `assigned_by_id → profile(id)`
- `user_id → profile(id)`

## post

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `body` | text | YES | `` |
| `visibility` | varchar(255) | NO | `` |
| `moderation_status` | varchar(255) | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `community_id` | bigint | YES | `` |
| `event_id` | bigint | YES | `` |
| `author_id` | bigint | YES | `` |

**Foreign Keys:**

- `author_id → profile(id)`
- `community_id → community(id)`
- `event_id → event(id)`

## post_media

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `media_type` | varchar(255) | NO | `` |
| `storage_path` | varchar(512) | NO | `` |
| `sort_order` | integer | NO | `` |
| `created_at` | timestamp | NO | `` |
| `post_id` | bigint | YES | `` |

**Foreign Keys:**

- `post_id → post(id)`

## profile

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `display_name` | varchar(120) | NO | `` |
| `username` | varchar(50) | NO | `` |
| `avatar_url` | varchar(2048) | YES | `` |
| `bio` | varchar(2000) | YES | `` |
| `city` | varchar(100) | YES | `` |
| `status` | varchar(255) | NO | `` |
| `is_verified` | boolean | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `user_id` | bigint | NO | `` |
| `avatar_image_id` | bigint | YES | `` |
| `birth_date` | date | YES | `` |

**Foreign Keys:**

- `avatar_image_id → media_asset(id)`
- `user_id → jhi_user(id)`

## promotion

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `title` | varchar(200) | NO | `` |
| `description` | varchar(1000) | YES | `` |
| `image_id` | bigint | YES | `` |
| `placement` | varchar(40) | NO | `` |
| `action_type` | varchar(40) | NO | `` |
| `action_value` | varchar(500) | YES | `` |
| `start_at` | timestamp | NO | `` |
| `end_at` | timestamp | NO | `` |
| `active` | boolean | NO | `true` |
| `priority` | integer | NO | `0` |
| `display_order` | integer | NO | `0` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |
| `created_by_id` | bigint | YES | `` |

**Foreign Keys:**

- `created_by_id → jhi_user(id)`
- `image_id → media_asset(id)`

## push_device_token

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `user_id` | bigint | NO | `` |
| `token` | varchar(512) | NO | `` |
| `platform` | varchar(20) | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `active` | boolean | YES | `true` |
| `notifications_enabled` | boolean | YES | `true` |
| `last_seen_at` | timestamp | YES | `` |
| `last_success_at` | timestamp | YES | `` |
| `last_failure_at` | timestamp | YES | `` |
| `firebase_installation_id` | varchar(255) | YES | `` |
| `device_uuid` | varchar(255) | YES | `` |
| `app_version` | varchar(80) | YES | `` |

**Foreign Keys:**

- `user_id → jhi_user(id)`

## push_token

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `device_token` | varchar(512) | NO | `` |
| `platform` | varchar(255) | NO | `` |
| `device_name` | varchar(120) | YES | `` |
| `is_active` | boolean | NO | `` |
| `last_seen_at` | timestamp | NO | `` |
| `created_at` | timestamp | NO | `` |
| `user_id` | bigint | YES | `` |
| `updated_at` | timestamp | YES | `` |

**Foreign Keys:**

- `user_id → profile(id)`

## report

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `target_type` | varchar(255) | NO | `` |
| `target_id` | varchar(64) | NO | `` |
| `reason` | varchar(500) | NO | `` |
| `details` | text | YES | `` |
| `status` | varchar(255) | NO | `` |
| `resolved_at` | timestamp | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `reporter_id` | bigint | YES | `` |
| `resolved_by_id` | bigint | YES | `` |

**Foreign Keys:**

- `reporter_id → profile(id)`
- `resolved_by_id → profile(id)`

## store_address

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `title` | varchar(120) | NO | `` |
| `first_name` | varchar(100) | NO | `` |
| `last_name` | varchar(100) | NO | `` |
| `phone` | varchar(30) | YES | `` |
| `country` | varchar(100) | YES | `'T├╝rkiye'::character varying` |
| `city` | varchar(100) | NO | `` |
| `district` | varchar(100) | NO | `` |
| `neighborhood` | varchar(100) | YES | `` |
| `address_line` | varchar(500) | NO | `` |
| `postal_code` | varchar(20) | YES | `` |
| `note` | varchar(1000) | YES | `` |
| `is_default` | boolean | NO | `false` |
| `user_id` | bigint | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |

**Foreign Keys:**

- `user_id → profile(id)`

## store_bank_transfer

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `reference_code` | varchar(120) | NO | `` |
| `amount` | numeric | NO | `` |
| `currency` | varchar(8) | YES | `'TRY'::character varying` |
| `iban` | varchar(64) | YES | `` |
| `account_holder` | varchar(255) | YES | `` |
| `bank_name` | varchar(255) | YES | `` |
| `receipt_url` | varchar(2048) | YES | `` |
| `status` | varchar(255) | NO | `` |
| `admin_note` | varchar(1000) | YES | `` |
| `order_id` | bigint | NO | `` |
| `approved_by` | bigint | YES | `` |
| `approved_at` | timestamp | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |

**Foreign Keys:**

- `approved_by → profile(id)`
- `order_id → store_order(id)`

## store_brand

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `name` | varchar(200) | NO | `` |
| `slug` | varchar(200) | NO | `` |
| `description` | varchar(2000) | YES | `` |
| `logo_url` | varchar(2048) | YES | `` |
| `is_active` | boolean | NO | `true` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |
| `created_by` | varchar(80) | YES | `` |
| `updated_by` | varchar(80) | YES | `` |
| `deleted_at` | timestamp | YES | `` |

## store_cart_item

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `quantity` | integer | NO | `1` |
| `unit_price` | numeric | NO | `` |
| `discount_amount` | numeric | YES | `0` |
| `user_id` | bigint | YES | `` |
| `product_id` | bigint | YES | `` |
| `variant_id` | bigint | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |

**Foreign Keys:**

- `product_id → store_product(id)`
- `user_id → profile(id)`
- `variant_id → store_product_variant(id)`

## store_category

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `name` | varchar(200) | NO | `` |
| `slug` | varchar(200) | NO | `` |
| `description` | varchar(2000) | YES | `` |
| `image_url` | varchar(2048) | YES | `` |
| `sort_order` | integer | YES | `0` |
| `is_active` | boolean | NO | `true` |
| `parent_id` | bigint | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |
| `created_by` | varchar(80) | YES | `` |
| `updated_by` | varchar(80) | YES | `` |
| `deleted_at` | timestamp | YES | `` |

**Foreign Keys:**

- `parent_id → store_category(id)`

## store_coupon

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `code` | varchar(80) | NO | `` |
| `discount_type` | varchar(255) | NO | `` |
| `discount_value` | numeric | NO | `` |
| `minimum_cart_amount` | numeric | YES | `` |
| `maximum_discount` | numeric | YES | `` |
| `start_date` | timestamp | YES | `` |
| `end_date` | timestamp | YES | `` |
| `usage_limit` | integer | YES | `` |
| `per_user_limit` | integer | YES | `` |
| `is_active` | boolean | NO | `true` |
| `usage_count` | integer | YES | `0` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |

## store_order

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `order_number` | varchar(120) | NO | `` |
| `user_id` | bigint | YES | `` |
| `shipping_address_snapshot` | text | YES | `` |
| `billing_address_snapshot` | text | YES | `` |
| `subtotal` | numeric | NO | `` |
| `discount_amount` | numeric | YES | `0` |
| `shipping_amount` | numeric | YES | `0` |
| `total_amount` | numeric | NO | `` |
| `currency` | varchar(8) | NO | `'TRY'::character varying` |
| `payment_status` | varchar(255) | NO | `` |
| `order_status` | varchar(255) | NO | `` |
| `shipping_status` | varchar(255) | NO | `` |
| `customer_note` | varchar(2000) | YES | `` |
| `coupon_code` | varchar(120) | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |

**Foreign Keys:**

- `user_id → profile(id)`

## store_order_item

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `order_id` | bigint | YES | `` |
| `product_id` | bigint | YES | `` |
| `product_name_snapshot` | varchar(300) | NO | `` |
| `product_sku_snapshot` | varchar(120) | YES | `` |
| `product_image_snapshot` | varchar(2048) | YES | `` |
| `quantity` | integer | NO | `` |
| `unit_price` | numeric | NO | `` |
| `discount` | numeric | YES | `0` |
| `total_price` | numeric | NO | `` |
| `variant_snapshot` | varchar(1000) | YES | `` |
| `created_at` | timestamp | NO | `` |

**Foreign Keys:**

- `order_id → store_order(id)`

## store_order_status_history

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `order_id` | bigint | YES | `` |
| `old_status` | varchar(255) | YES | `` |
| `new_status` | varchar(255) | NO | `` |
| `changed_by` | varchar(80) | YES | `` |
| `note` | varchar(2000) | YES | `` |
| `created_at` | timestamp | NO | `` |

**Foreign Keys:**

- `order_id → store_order(id)`

## store_payment

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `order_id` | bigint | YES | `` |
| `provider` | varchar(255) | NO | `` |
| `transaction_id` | varchar(255) | YES | `` |
| `amount` | numeric | NO | `` |
| `currency` | varchar(8) | NO | `'TRY'::character varying` |
| `status` | varchar(255) | NO | `` |
| `payment_method` | varchar(80) | YES | `` |
| `paid_at` | timestamp | YES | `` |
| `failure_reason` | varchar(1000) | YES | `` |
| `provider_response` | varchar(4000) | YES | `` |
| `idempotency_key` | varchar(255) | NO | `` |
| `callback_payload` | text | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |
| `refunded_amount` | numeric | YES | `0` |

**Foreign Keys:**

- `order_id → store_order(id)`

## store_payment_refund

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `refund_reference` | varchar(120) | NO | `` |
| `status` | varchar(255) | NO | `` |
| `amount` | numeric | NO | `` |
| `reason` | varchar(1000) | YES | `` |
| `provider_reference` | varchar(255) | YES | `` |
| `provider_response` | text | YES | `` |
| `payment_id` | bigint | NO | `` |
| `approved_by` | bigint | YES | `` |
| `approved_at` | timestamp | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |

**Foreign Keys:**

- `approved_by → profile(id)`
- `payment_id → store_payment(id)`

## store_payment_transaction

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `transaction_reference` | varchar(120) | NO | `` |
| `status` | varchar(255) | NO | `` |
| `amount` | numeric | NO | `` |
| `currency` | varchar(8) | YES | `'TRY'::character varying` |
| `provider_request` | varchar(4000) | YES | `` |
| `provider_response` | text | YES | `` |
| `failure_reason` | varchar(1000) | YES | `` |
| `processed_at` | timestamp | YES | `` |
| `payment_id` | bigint | NO | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |

**Foreign Keys:**

- `payment_id → store_payment(id)`

## store_payment_webhook

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `provider` | varchar(120) | NO | `` |
| `event_type` | varchar(255) | NO | `` |
| `provider_reference` | varchar(255) | YES | `` |
| `payload` | text | YES | `` |
| `signature` | varchar(1000) | YES | `` |
| `signature_valid` | boolean | NO | `false` |
| `processed` | boolean | NO | `false` |
| `processing_error` | varchar(1000) | YES | `` |
| `payment_id` | bigint | YES | `` |
| `received_at` | timestamp | NO | `` |
| `processed_at` | timestamp | YES | `` |

**Foreign Keys:**

- `payment_id → store_payment(id)`

## store_product

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `name` | varchar(300) | NO | `` |
| `slug` | varchar(300) | NO | `` |
| `short_description` | varchar(500) | YES | `` |
| `description` | text | YES | `` |
| `sku` | varchar(120) | YES | `` |
| `barcode` | varchar(120) | YES | `` |
| `price` | numeric | NO | `` |
| `discounted_price` | numeric | YES | `` |
| `currency` | varchar(8) | NO | `'TRY'::character varying` |
| `stock_quantity` | integer | YES | `0` |
| `low_stock_threshold` | integer | YES | `5` |
| `status` | varchar(255) | NO | `` |
| `is_featured` | boolean | NO | `false` |
| `is_active` | boolean | NO | `true` |
| `weight` | numeric | YES | `` |
| `width` | numeric | YES | `` |
| `height` | numeric | YES | `` |
| `length` | numeric | YES | `` |
| `rating_average` | numeric | YES | `0` |
| `review_count` | integer | YES | `0` |
| `sales_count` | integer | YES | `0` |
| `category_id` | bigint | YES | `` |
| `brand_id` | bigint | YES | `` |
| `seller_id` | bigint | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |
| `created_by` | varchar(80) | YES | `` |
| `updated_by` | varchar(80) | YES | `` |
| `deleted_at` | timestamp | YES | `` |

**Foreign Keys:**

- `brand_id → store_brand(id)`
- `category_id → store_category(id)`
- `seller_id → profile(id)`

## store_product_attribute

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `attribute_key` | varchar(200) | NO | `` |
| `attribute_value` | varchar(1000) | NO | `` |
| `sort_order` | integer | YES | `0` |
| `product_id` | bigint | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |

**Foreign Keys:**

- `product_id → store_product(id)`

## store_product_image

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `image_url` | varchar(2048) | NO | `` |
| `thumbnail_url` | varchar(2048) | YES | `` |
| `sort_order` | integer | YES | `0` |
| `is_primary` | boolean | NO | `false` |
| `alt_text` | varchar(300) | YES | `` |
| `product_id` | bigint | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |

**Foreign Keys:**

- `product_id → store_product(id)`

## store_product_variant

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `variant_name` | varchar(300) | NO | `` |
| `sku` | varchar(120) | YES | `` |
| `price` | numeric | YES | `` |
| `discounted_price` | numeric | YES | `` |
| `stock_quantity` | integer | NO | `0` |
| `image_url` | varchar(2048) | YES | `` |
| `is_active` | boolean | NO | `true` |
| `product_id` | bigint | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |

**Foreign Keys:**

- `product_id → store_product(id)`

## store_review

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `product_id` | bigint | YES | `` |
| `user_id` | bigint | YES | `` |
| `order_id` | bigint | YES | `` |
| `order_item_id` | bigint | YES | `` |
| `rating` | integer | NO | `` |
| `title` | varchar(200) | YES | `` |
| `comment` | varchar(2000) | YES | `` |
| `status` | varchar(255) | NO | `` |
| `helpful_count` | integer | YES | `0` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |

**Foreign Keys:**

- `product_id → store_product(id)`
- `user_id → profile(id)`

## store_review_image

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `review_id` | bigint | YES | `` |
| `image_url` | varchar(2048) | NO | `` |
| `thumbnail_url` | varchar(2048) | YES | `` |
| `sort_order` | integer | YES | `0` |
| `created_at` | timestamp | NO | `` |

**Foreign Keys:**

- `review_id → store_review(id)`

## store_shipping

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `order_id` | bigint | YES | `` |
| `carrier` | varchar(120) | NO | `` |
| `tracking_number` | varchar(200) | NO | `` |
| `shipping_status` | varchar(255) | NO | `` |
| `shipped_at` | timestamp | YES | `` |
| `estimated_delivery_date` | date | YES | `` |
| `delivered_at` | timestamp | YES | `` |
| `carrier_response` | varchar(2000) | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |

**Foreign Keys:**

- `order_id → store_order(id)`

## store_wishlist

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `user_id` | bigint | YES | `` |
| `product_id` | bigint | YES | `` |
| `created_at` | timestamp | NO | `` |

**Foreign Keys:**

- `product_id → store_product(id)`
- `user_id → profile(id)`

## story

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `content_type` | varchar(255) | NO | `` |
| `body` | varchar(500) | YES | `` |
| `media_url` | varchar(2048) | YES | `` |
| `is_public` | boolean | NO | `` |
| `share_with_followers` | boolean | NO | `` |
| `created_at` | timestamp | NO | `` |
| `expires_at` | timestamp | NO | `` |
| `author_id` | bigint | YES | `` |
| `story_group_id` | bigint | YES | `` |
| `event_id` | bigint | YES | `` |
| `location_name` | varchar(200) | YES | `` |
| `latitude` | numeric | YES | `` |
| `longitude` | numeric | YES | `` |

**Foreign Keys:**

- `author_id → profile(id)`
- `event_id → event(id)`
- `story_group_id → story_group(id)`

## story_community_target

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `created_at` | timestamp | NO | `` |
| `story_id` | bigint | YES | `` |
| `community_id` | bigint | YES | `` |

**Foreign Keys:**

- `community_id → community(id)`
- `story_id → story(id)`

## story_element

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `element_type` | varchar(255) | NO | `` |
| `content` | varchar(2000) | YES | `` |
| `position_x` | double | YES | `` |
| `position_y` | double | YES | `` |
| `scale` | double | YES | `` |
| `rotation` | double | YES | `` |
| `color` | varchar(50) | YES | `` |
| `background_color` | varchar(50) | YES | `` |
| `font_size` | integer | YES | `` |
| `width` | double | YES | `` |
| `height` | double | YES | `` |
| `metadata_json` | varchar(4000) | YES | `` |
| `sort_order` | integer | NO | `` |
| `created_at` | timestamp | NO | `` |
| `story_id` | bigint | YES | `` |

**Foreign Keys:**

- `story_id → story(id)`

## story_group

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `location_name` | varchar(200) | YES | `` |
| `latitude` | numeric | YES | `` |
| `longitude` | numeric | YES | `` |
| `created_at` | timestamp | NO | `` |
| `expires_at` | timestamp | NO | `` |
| `author_id` | bigint | YES | `` |
| `community_id` | bigint | YES | `` |
| `event_id` | bigint | YES | `` |

**Foreign Keys:**

- `author_id → profile(id)`
- `community_id → community(id)`
- `event_id → event(id)`

## story_hashtag

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `created_at` | timestamp | NO | `` |
| `story_id` | bigint | YES | `` |
| `hashtag_id` | bigint | YES | `` |

**Foreign Keys:**

- `hashtag_id → hashtag(id)`
- `story_id → story(id)`

## story_reaction

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `reaction_type` | varchar(255) | NO | `` |
| `created_at` | timestamp | NO | `` |
| `story_id` | bigint | YES | `` |
| `user_id` | bigint | YES | `` |

**Foreign Keys:**

- `story_id → story(id)`
- `user_id → profile(id)`

## story_view

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `viewed_at` | timestamp | NO | `` |
| `story_id` | bigint | YES | `` |
| `viewer_id` | bigint | YES | `` |

**Foreign Keys:**

- `story_id → story(id)`
- `viewer_id → profile(id)`

## ticket

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `ticket_code` | varchar(120) | NO | `` |
| `qr_code` | varchar(2048) | YES | `` |
| `status` | varchar(255) | NO | `` |
| `used_at` | timestamp | YES | `` |
| `order_item_id` | bigint | YES | `` |
| `user_id` | bigint | YES | `` |
| `event_id` | bigint | YES | `` |

**Foreign Keys:**

- `event_id → event(id)`
- `order_item_id → order_item(id)`
- `user_id → profile(id)`

## ticket_order

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `order_number` | varchar(120) | NO | `` |
| `total_amount` | numeric | NO | `` |
| `currency` | varchar(8) | NO | `` |
| `status` | varchar(255) | NO | `` |
| `created_at` | timestamp | NO | `` |
| `paid_at` | timestamp | YES | `` |
| `user_id` | bigint | YES | `` |

**Foreign Keys:**

- `user_id → profile(id)`

## user_honor_badge

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `reason` | varchar(500) | YES | `` |
| `awarded_at` | timestamp | NO | `` |
| `user_id` | bigint | YES | `` |
| `badge_id` | bigint | YES | `` |
| `awarded_by_id` | bigint | YES | `` |

**Foreign Keys:**

- `awarded_by_id → profile(id)`
- `badge_id → honor_badge(id)`
- `user_id → profile(id)`

## user_notification_preference

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `user_id` | bigint | NO | `` |
| `notification_type` | varchar(80) | NO | `` |
| `in_app_enabled` | boolean | NO | `true` |
| `push_enabled` | boolean | NO | `true` |
| `email_enabled` | boolean | YES | `false` |
| `muted_until` | timestamp | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | YES | `` |
| `mandatory` | boolean | YES | `false` |

**Foreign Keys:**

- `user_id → jhi_user(id)`

## user_review

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `rating` | integer | NO | `` |
| `review_text` | varchar(2000) | YES | `` |
| `created_at` | timestamp | NO | `` |
| `updated_at` | timestamp | NO | `` |
| `reviewer_id` | bigint | YES | `` |
| `reviewed_user_id` | bigint | YES | `` |
| `event_id` | bigint | YES | `` |

**Foreign Keys:**

- `event_id → event(id)`
- `reviewed_user_id → profile(id)`
- `reviewer_id → profile(id)`

## user_role

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |
| `created_at` | timestamp | NO | `` |
| `user_id` | bigint | YES | `` |
| `role_id` | bigint | YES | `` |

**Foreign Keys:**

- `role_id → app_role(id)`
- `user_id → profile(id)`

## ﻿account_preferences

| Kolon | Tip | Null | Varsayılan |
| --- | --- | --- | --- |
| `id` | bigint | NO | `` |

**Foreign Keys:**

- `profile_id → profile(id)`
