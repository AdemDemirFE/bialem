# Feature Parity Checklist

Legend: **CURRENT** = works on Supabase today | **TARGET** = Spring REST | **MIGRATION** = work required

| Feature | CURRENT | TARGET | MIGRATION REQUIRED |
|---------|---------|--------|-------------------|
| Auth (email/password) | Supabase Auth | JWT + User | YES |
| Email verification | Supabase + SMTP | Spring mail + activation token | YES |
| Password reset | Supabase + web bridge | Spring reset flow | YES |
| Google / Apple login | **Not in repo** | OAuth2 optional | NO (v1) |
| Profile CRUD | `profiles` + RPC | `/api/v1/me`, `/profiles` | YES |
| Private profile | `account_preferences` + RPC follow | Profile privacy service | YES |
| Follow (public) | RPC `set_profile_follow_state` | `/api/v1/follows` | YES |
| Follow request (private) | `follow_requests` + RPC | Same | YES |
| Block users | RPC `set_profile_block` | `/api/v1/blocks` | YES |
| Community (hub/group) | `communities` tree | `/api/v1/communities` | YES |
| Community membership approval | RPC join/review | Membership service | YES |
| Group create | RPC `create_community_group` | POST communities | YES |
| Partner community | RPC `create_partner_group` | Partner federation | YES |
| Partner trust level | Admin UPDATE | Admin API | YES |
| Moderator assistants | `community_moderator_assistants` | Assistant service | YES |
| Event create | RPC `create_group_event` | POST events | YES |
| Event approval (group) | RPC `moderate_group_event` | Moderator API | YES |
| Event approval (platform) | Admin UPDATE events | Admin API | YES |
| Event cancel | RPC `cancel_event` | PATCH events | YES |
| Event participation | RPC request/cancel | Participation API | YES |
| Waiting list | RPC promote waitlist | Service logic | YES |
| Event chat | `event_messages` + Realtime | WebSocket + REST | YES |
| Event QR check-in | RPC check-in | Check-in API | YES |
| Event rating | `event_ratings` + validation trigger | Rating service | YES |
| User review (post-event) | `user_reviews` | Reviews API | YES |
| Posts & feed | `posts`, `post_media` | `/api/v1/posts` | YES |
| Comments | `comments` | `/api/v1/comments` | YES |
| Stories | `stories`, RPC audience | Stories API | YES |
| Media upload | Supabase Storage | `/api/v1/media` + S3 | YES |
| Notifications inbox | `notifications` | `/api/v1/notifications` | YES |
| Push delivery | pg_net → Expo | Firebase Admin | YES |
| Moderation reports | `reports` | `/api/v1/reports` | YES |
| Account deletion | Edge `delete-account` | DELETE account API | YES |
| City Radar | RPC `get_city_radar` | `/api/v1/city-events` | YES |
| Automatic city events | Edge sync + cron | `@Scheduled` sync | YES |
| Ticket offers | `city_event_ticket_offers` | Nested resource | YES |
| Companion / Birlikte Git | `city_event_interests.looking_for_company` | Same field | YES |
| Bialem Advantage venues | `partner_venues` | `/api/v1/advantages` | YES |
| Advantage QR issue/redeem | RPC issue/redeem | Redemption API | YES |
| Honor badges | RPC + triggers | Gamification service | YES |
| AI Assistant | Edge `bialem-assistant` | POST `/api/v1/ai/chat` | YES |
| Admin dashboard | Next.js + service role | `/api/v1/admin` + same admin UI | YES |
| Admin MFA | Supabase TOTP | Spring MFA | YES |
| Deep links | `bialem://`, `bialem.app` | Unchanged URLs | PARTIAL (client only) |
| Public event share | RPC anon OK | Public GET | YES |
| Legal pages | Static admin pages | Static (no backend) | NO |
| Organizer request flow | Mobile screen | Same UI, new API | YES |
| Team identities badge | `platform_team_members` | Platform team API | YES |
| My plans | RPC `get_my_profile_plans` | GET `/me/plans` | YES |
| Calendar export | Client-only expo-calendar | Client-only | NO |

## Not implemented in mobile UI

| Feature | Note |
|---------|------|
| Post image upload UI | `uploadPostImage` in storage.ts unused |
| Google/Apple OAuth | Not in auth.tsx |

## Parity gate for cutover

All **YES** rows must pass E2E on staging before Supabase decommission.

Reference: `docs/ANDROID_DEVICE_E2E_TR.md`, `docs/RELEASE_READINESS_TR.md`.
