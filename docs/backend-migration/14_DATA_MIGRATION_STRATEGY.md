# Data Migration Strategy

**No data migration in this phase.** Design only.

## Source & target

| | |
|-|-|
| **Source** | Supabase PostgreSQL (`auth` + `public` schemas) |
| **Target** | Self-hosted PostgreSQL (JHipster/Liquibase) |

## UUID preservation

**Recommendation: preserve all UUIDs.**

| Entity | Current PK | Target | Risk if changed |
|--------|------------|--------|-----------------|
| All `public.*` tables | UUID `gen_random_uuid()` | Same UUID | **HIGH** — breaks mobile deep links, notifications payload, QR, storage paths |
| `profiles.id` | UUID = `auth.users.id` | `profile.id` = `jhi_user.id` | **HIGH** — must 1:1 map |
| `story_community_targets` | Composite PK | Same | MEDIUM |

## auth.users → JHipster User

| Field | Supabase | JHipster |
|-------|----------|----------|
| id | uuid | `jhi_user.id` (UUID if configured) |
| email | text | `login` / `email` |
| encrypted_password | bcrypt | `password_hash` — **format may differ** |
| email_confirmed_at | timestamp | `activated` |
| raw_user_meta_data | jsonb | `Profile` fields |

**Password hashes:** Do **not** assume Supabase bcrypt imports cleanly into JHipster without verification.

**Recommended alternatives:**

1. **Force password reset** on first login after cutover (email campaign).
2. **Parallel run:** read-only old auth for 30 days (complex — avoid).
3. **Proof of concept:** migrate 1 test user hash in staging lab.

## Migration order (tables)

1. `jhi_user` + `jhi_authority` + `jhi_user_authority`
2. `profiles` (+ trigger-equivalent defaults → `account_preferences`)
3. `roles` / map to `Authority` OR migrate `user_roles` → authorities
4. `communities` → `community_members` → assistants
5. `events` → participants → messages → ratings
6. Social: follows, blocks, follow_requests, posts, comments, stories
7. `notifications`, `push_tokens`
8. City: `city_events`, interests, ticket_offers, sync_logs
9. Partner: venues, offers, staff, redemptions
10. Gamification: honor_badges, user_honor_badges
11. Moderation: reports, ai_usage_logs, platform_team_members

## Foreign keys

Export with `pg_dump --data-only` preserving UUID FKs. Validate counts per table after import.

## Storage migration

1. List all objects in Supabase Storage buckets.
2. Copy to MinIO/S3 preserving path `{userId}/{filename}`.
3. Update `storage_path` / `*_url` columns if CDN base URL changes.

## Soft delete / status

| Table | Field | Migrate |
|-------|-------|---------|
| `profiles` | `status = deleted` | Include or exclude per policy |
| `push_tokens` | `is_active` | Migrate active only |

## Timestamps

Preserve `created_at`, `updated_at`, `published_at`, etc. — used in UI relative dates.

## Rollback

Keep Supabase read-only snapshot until parity checklist passes (`10_FEATURE_PARITY_CHECKLIST.md`).

## Validation queries (post-migrate)

- Row counts per table vs source
- Orphan FK check
- Sample login + event join + redemption flow on staging

See `08_DATABASE_MIGRATION_MAP.md` for table-level mapping.
