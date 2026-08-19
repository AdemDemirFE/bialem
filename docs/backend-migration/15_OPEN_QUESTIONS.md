# Open Questions — Backend Migration

Decisions required before or during JHipster generation.

## Identity & auth

1. **Password hash migration:** Supabase Auth uses bcrypt; can JHipster import hashes or force reset? (See `14_DATA_MIGRATION_STRATEGY.md` — recommend phased reset.)
2. **Admin MFA:** Replace Supabase TOTP with Spring Security WebAuthn/TOTP? Same UX on `/admin/mfa`?
3. **Google / Apple login:** Not in codebase today — include in v1 target or defer?

## IDs

4. **UUID preservation:** All public tables use UUID. Confirm JHipster `uuid` type + Liquibase custom changes for `profiles.id = users.id` (@MapsId). **POST-JHIPSTER MANUAL ACTION** likely required.
5. **Deep links / QR / share URLs:** Keep UUID strings in URLs unchanged — confirm no sequential ID plan.

## Realtime

6. **Event chat:** Replace Supabase Realtime with WebSocket (STOMP/Spring), SSE, or polling? Mobile currently subscribes to `event_messages` + `event_participants`.

## Push

7. **Expo Push → FCM direct:** Mobile must switch from `getExpoPushTokenAsync` to native FCM token — separate from backend migration but coupled.
8. **Token table:** Rename `expo_push_token` → `fcm_token` or generic `device_token`?

## Media

9. **Storage path convention:** Keep `{userId}/filename` prefix for migration compatibility?
10. **Public vs signed URLs:** Buckets are public read today; keep or move to signed URLs?

## City events

11. **Cron host:** Spring `@Scheduled` vs external scheduler calling REST endpoint?
12. **Ticketmaster / partner feeds:** Keep provider abstraction as designed in `integration` package?

## AI

13. **OpenAI model:** Edge function default `gpt-5.4-mini` — confirm production model name.
14. **Rate limit:** Keep 20 requests/hour/user in `ai_usage_logs`?

## Admin

15. **Service role pattern:** Admin today bypasses RLS via service role. Target: admin JWT with `ROLE_ADMIN` + same REST API or separate `/api/v1/admin`?

## Data migration

16. **Downtime window:** Big-bang cutover vs dual-write vs read-from-Spring-write-to-both?
17. **Staging validation:** Use existing backup/restore drill (`docs/SUPABASE_BACKUP_RESTORE_TR.md`)?

## JHipster

18. **JHipster version:** Pin 8.x vs latest at generation time — verify UUID + MapStruct + serviceClass support.
19. **Monolith vs modular monolith:** Single JHipster app recommended; confirm no microservices split for v1.

## Legal / compliance

20. **KVKK / account deletion:** `prepare_profile_deletion` trigger logic — replicate in `AccountDeletionService`?

---

**Blockers for SAFE TO CREATE JHIPSTER BACKEND:** None critical for **skeleton** generation. UUID/Profile mapping and RLS→Spring authorization design should be reviewed before first Liquibase prod deploy.
