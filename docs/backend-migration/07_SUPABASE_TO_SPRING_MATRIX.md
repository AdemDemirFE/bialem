# Supabase → Spring Migration Matrix

| Supabase component | Current usage | Spring replacement | Migration risk |
|--------------------|---------------|-------------------|----------------|
| **Auth (email/password)** | `signUp`, `signInWithPassword`, `signOut`, `resetPasswordForEmail`, `verifyOtp`, session refresh | Spring Security + JWT + `User`/`Profile` + email service | **HIGH** — password hash import unreliable |
| **Auth (MFA admin)** | Supabase TOTP, AAL2 in middleware | Spring Security MFA / WebAuthn | **MEDIUM** |
| **Auth (Google/Apple)** | Not implemented | Optional OAuth2 login adapters | **LOW** (greenfield) |
| **PostgREST `.from()`** | Direct SELECT/INSERT/UPDATE on ~20 tables | JPA repositories + REST controllers | **MEDIUM** — must replicate RLS in services |
| **RPC (~54 functions)** | All business writes | `@Service` methods + REST endpoints | **HIGH** — core logic |
| **RLS (74+ policies)** | Row-level security | `@PreAuthorize` + service ownership checks + query filters | **HIGH** |
| **Triggers (~35)** | Notifications, badges, validation, Turkish normalize | `@EntityListeners`, domain events, `@Scheduled`, DB constraints | **HIGH** |
| **Realtime** | Event chat `postgres_changes` | WebSocket/STOMP or SSE + polling fallback | **MEDIUM** |
| **Storage (5 buckets)** | Public object storage, path `{userId}/...` | `MediaStorageService` → MinIO/S3 | **MEDIUM** — blob migration |
| **Edge: bialem-assistant** | OpenAI proxy + rate limit | `AiAssistantService` + `POST /api/v1/ai/chat` | **LOW** |
| **Edge: delete-account** | Service role delete user + storage cleanup | `AccountDeletionService` + admin API | **MEDIUM** |
| **Edge: sync-city-events** | Ticketmaster + partner feeds | `CityEventSyncScheduler` + providers | **MEDIUM** |
| **Cron** | Supabase Dashboard HTTP cron → sync function | `@Scheduled` or external cron → REST | **LOW** |
| **Push (pg_net → Expo)** | Trigger on `notifications` INSERT | `NotificationService` → Firebase Admin SDK | **HIGH** — mobile token format change |
| **pg_net** | HTTP from PostgreSQL | Remove; push from Java | **LOW** |
| **service_role key (admin)** | Bypass RLS in Next.js server actions | Admin JWT + elevated authorities | **MEDIUM** |
| **anon/publishable key** | Mobile + admin client | Public REST + JWT | **LOW** |
| **auth.users** | Source of truth for credentials | JHipster `jhi_user` | **HIGH** |
| **ai_usage_logs + claim_ai_request** | Rate limit 20/h | `AiUsageService` + DB table | **LOW** |
| **Supabase JWT in Edge Functions** | Validate caller | Spring JWT validation | **LOW** |

## Realtime detail

| Channel | Tables | Spring approach |
|---------|--------|-----------------|
| `event-chat-{eventId}` | `event_messages`, `event_participants` | WebSocket topic per event; authorize via `can_access_event_chat` rules |

## Storage buckets

| Bucket | Spring |
|--------|--------|
| `post-media` | S3 prefix `post-media/` |
| `stories` | `stories/` |
| `profile-avatars` | `profile-avatars/` |
| `community-covers` | `community-covers/` |
| `event-covers` | `event-covers/` |

## RPC → Service mapping (sample)

| RPC | Spring service |
|-----|----------------|
| `join_community` | `CommunityMembershipService.join` |
| `create_group_event` | `EventService.createGroupEvent` |
| `request_event_participation` | `EventParticipationService.request` |
| `check_in_event_participant` | `EventCheckInService.checkIn` |
| `issue_partner_offer_redemption` | `AdvantageRedemptionService.issue` |
| `redeem_partner_offer` | `AdvantageRedemptionService.redeem` |
| `claim_ai_request` | `AiUsageService.claim` |
| `is_admin` | `SecurityUtils.hasAuthority(ADMIN)` |

Full RPC list: `02_SUPABASE_INVENTORY.md` § Functions.
