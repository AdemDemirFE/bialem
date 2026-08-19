# Database Post-Generation (Manual SQL)

JHipster/JDL may **not** generate these PostgreSQL-specific objects. Apply via Liquibase **custom changesets** after JHipster import.

## Partial indexes

```sql
-- Published city events radar
CREATE INDEX idx_city_events_city_starts ON city_events (city, starts_at)
  WHERE status = 'published';

-- Event discovery
CREATE INDEX idx_events_discovery_category_starts ON events (category_id, starts_at)
  WHERE status = 'published' AND published_to_discovery = true;
```

## Composite / filtered uniques (verify JDL generated)

| Table | Constraint |
|-------|------------|
| `event_participants` | UNIQUE `(event_id, user_id)` |
| `event_ratings` | UNIQUE `(event_id, user_id)` |
| `follows` | UNIQUE `(follower_id, followed_id)` + CHECK follower <> followed |
| `blocks` | UNIQUE `(blocker_id, blocked_user_id)` |
| `city_events` | UNIQUE `(provider_code, external_id)` WHERE external_id IS NOT NULL |
| `city_event_ticket_offers` | UNIQUE `(city_event_id, provider_code, external_offer_id)` |
| `partner_offer_redemptions` | UNIQUE `token`, UNIQUE `redemption_code` |
| `push_tokens` | UNIQUE `expo_push_token` (or `fcm_token`) |
| `user_honor_badges` | UNIQUE `(user_id, badge_id)` |

## Lookup indexes

```sql
CREATE INDEX idx_profiles_username ON profiles (username);
CREATE INDEX idx_events_community_starts ON events (community_id, starts_at);
CREATE INDEX idx_events_status_starts ON events (status, starts_at);
CREATE INDEX idx_notifications_user_read ON notifications (user_id, is_read);
CREATE INDEX idx_reports_status_created ON reports (status, created_at);
CREATE INDEX idx_comments_target ON comments (target_type, target_id);
CREATE INDEX idx_push_tokens_user_active ON push_tokens (user_id, is_active);
CREATE INDEX idx_partner_redemptions_user_offer ON partner_offer_redemptions (user_id, offer_id);
CREATE INDEX idx_community_members_community_user ON community_members (community_id, user_id);
CREATE INDEX idx_follow_requests_target_created ON follow_requests (target_user_id, created_at DESC);
CREATE INDEX idx_ai_usage_user_created ON ai_usage_logs (user_id, created_at DESC);
```

## CHECK constraints (if JDL enum insufficient)

Replicate from migrations `0001`–`0054`:

- `profiles.status` IN (`active`, `pending_verification`, `suspended`, `deleted`)
- `events.status` IN (`draft`, `pending_approval`, `published`, `rejected`, `cancelled`, `completed`)
- `event_participants.status` IN (`pending`, `waitlisted`, `approved`, `rejected`, `cancelled`, `checked_in`, `no_show`)
- `communities.community_type` IN (`category_hub`, `partner_hub`, `group`)
- `partner_offer_redemptions.status` + `redeemed_at` consistency

## POST-JHIPSTER MANUAL ACTION

| Item | Reason |
|------|--------|
| `profiles.id` @MapsId with User UUID | JDL OneToOne may not set shared PK |
| `story_community_targets` composite PK | JDL composite key limited |
| `comments.target_id` polymorphic (no FK) | Service-layer validation |
| `reports.target_id` polymorphic | Same |
| `notifications.payload` JSONB | Use `@JdbcTypeCode(SqlTypes.JSON)` |
| `city_events.raw_payload` JSONB | Same |
| Trigger: `send_notification_push` | Move to Java `NotificationListener` |
| Trigger: `refresh_badges_after_check_in` | `HonorBadgeService.onCheckIn` |
| Trigger: `create_profile_for_auth_user` | `UserRegistrationService` |
| Extension `pg_net` | **Remove** — push from Spring |
| Realtime publication | Not needed on self-hosted PG unless logical replication for analytics |

## Rating / check-in uniqueness

Enforce in DB **and** service:

- One rating per `(event_id, user_id)` — requires `checked_in` or attended rule via `can_rate_event` logic
- One check-in per participant row — status transition to `checked_in` once

## Advantage redemption

- Partial unique: one `issued` token per user/offer within validity window (business rule in `issue_partner_offer_redemption` RPC — replicate in service + transaction isolation SERIALIZABLE or row lock)
