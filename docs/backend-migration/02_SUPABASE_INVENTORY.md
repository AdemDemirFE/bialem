# Supabase Inventory

**54 migrations** (`0001_init.sql` … `0054_event_cancellation.sql`)

## Summary counts

| Component | Count |
|-----------|-------|
| Public tables | 33 |
| RPC/user functions | 80+ |
| Triggers | 35+ |
| RLS policies | 74+ |
| Storage buckets | 5 |
| Edge functions | 3 active (`bialem-assistant` and related) |
| pg_cron in migrations | 0 (external scheduler documented) |
| PostgreSQL ENUM types | 0 (text + CHECK) |

## Extensions

- `pgcrypto` (0001)
- `pg_net` (0017 — push HTTP; **remove in Spring target**)

## Tables (alphabetical)

| Table | PK | Notes |
|-------|-----|-------|
| `account_preferences` | user_id UUID | 1:1 profile |
| `ai_usage_logs` | id UUID | AI rate limit |
| `blocks` | id UUID | |
| `city_event_interests` | id UUID | unique (city_event_id, user_id) |
| `city_event_sync_logs` | id UUID | |
| `city_event_ticket_offers` | id UUID | |
| `city_events` | id UUID | provider_code + external_id unique |
| `comments` | id UUID | polymorphic target |
| `communities` | id UUID | self-ref parent, category |
| `community_members` | id UUID | unique (community_id, user_id) |
| `community_moderator_assistants` | id UUID | |
| `event_messages` | id UUID | realtime |
| `event_participants` | id UUID | unique (event_id, user_id) |
| `event_ratings` | id UUID | unique (event_id, user_id) |
| `events` | id UUID | |
| `follow_requests` | id UUID | RPC-only writes |
| `follows` | id UUID | RPC-only writes |
| `honor_badges` | id UUID | |
| `notifications` | id UUID | jsonb payload |
| `partner_offer_redemptions` | id UUID | token UUID unique |
| `partner_offers` | id UUID | |
| `partner_venue_staff` | id UUID | |
| `partner_venues` | id UUID | slug unique |
| `platform_team_members` | id UUID | user_id unique |
| `post_media` | id UUID | |
| `posts` | id UUID | |
| `profiles` | id UUID | = auth.users.id, no default |
| `push_tokens` | id UUID | expo_push_token unique |
| `reports` | id UUID | polymorphic target |
| `roles` | id UUID | seed: member, organizer, moderator, admin |
| `stories` | id UUID | expires_at |
| `story_community_targets` | composite PK | |
| `story_views` | id UUID | unique (story_id, viewer_id) |
| `user_honor_badges` | id UUID | unique (user_id, badge_id) |
| `user_reviews` | id UUID | unique (reviewer, reviewed, event) |
| `user_roles` | id UUID | unique (user_id, role_id) |

Full column lists: see migration files or subagent export in repo analysis session.

## Edge functions

| Function | Auth | Spring target |
|----------|------|---------------|
| `bialem-assistant` | User JWT | `AiAssistantResource` |
| `delete-account` | User JWT | `AccountDeletionResource` |
| `sync-city-events` | `x-sync-secret` header | `CityEventSyncJob` |

## Storage buckets

| Bucket | Public | Limit |
|--------|--------|-------|
| post-media | yes | 10MB |
| stories | yes | 10MB |
| profile-avatars | yes | 5MB |
| community-covers | yes | 5MB |
| event-covers | yes | 5MB |

## Key RPC groups (54 called from mobile)

**Profile/social:** `get_public_profile_card`, `search_public_profiles`, `set_profile_follow_state`, `get_my_follow_requests`, `review_follow_request`, `set_profile_block`, …

**Community:** `join_community`, `leave_community`, `review_community_membership`, `create_community_group`, `create_partner_group`, `get_community_member_directory`, …

**Event:** `create_group_event`, `request_event_participation`, `check_in_event_participant`, `get_event_chat_messages`, `cancel_event`, …

**City:** `get_city_radar`, `set_city_event_interest`, `get_city_event_ticket_offers`

**Advantage:** `issue_partner_offer_redemption`, `redeem_partner_offer`

**AI:** `claim_ai_request`

**Push:** `register_current_device_push_token`, `deactivate_current_device_push_token`

**Admin RPC:** `is_admin`, `set_community_lead_moderator`

## Triggers (business-critical)

| Trigger | Purpose | Spring |
|---------|---------|--------|
| `trg_auth_user_create_profile` | Signup profile | RegistrationService |
| `trg_notifications_send_push` | Push on notify | NotificationService |
| `trg_event_participants_refresh_badges` | Honor badges on check-in | HonorBadgeService |
| `trg_event_ratings_validate` | Rating eligibility | RatingService |
| `prevent_invalid_user_review` | Review rules | ReviewService |

## RLS pattern

- All public tables: RLS enabled
- Many writes blocked → RPC SECURITY DEFINER
- Admin bypass: service role (not RLS) — replace with admin API authority

## auth schema dependencies

- Triggers on `auth.users` for profile creation and email verification sync
- **Must replicate** in Spring user registration flow

## Scheduled jobs

| Job | Current | Target |
|-----|---------|--------|
| City event sync | Supabase Dashboard cron POST to edge fn | `@Scheduled` every 6h |

Documented in `docs/AUTOMATIC_CITY_EVENTS_SETUP_TR.md`.
