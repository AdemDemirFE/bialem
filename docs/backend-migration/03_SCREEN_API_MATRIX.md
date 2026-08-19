# Screen → API Migration Matrix

Migration difficulty: **L** Low (direct table) | **M** Medium (RPC) | **H** High (realtime/auth/edge)

## Mobile screens

| Screen | Current data source | Target endpoint(s) | Auth | Difficulty |
|--------|---------------------|-------------------|------|------------|
| `/` index (auth) | `auth.*` | `/api/v1/auth/*` | Guest | H |
| `/(tabs)/feed` | `events`, `posts`, RPC stories/radar | `/events/feed`, `/posts/feed`, `/stories/feed`, `/city-events/radar` | JWT | M |
| `/(tabs)/profile` | profiles, RPCs, storage | `/me`, `/me/*`, `/media` | JWT | M |
| `/(tabs)/communities` | communities, RPC join | `/communities` | JWT | M |
| `/(tabs)/notifications` | notifications | `/notifications` | JWT | L |
| `/(tabs)/assistant` | Edge bialem-assistant | POST `/ai/chat` | JWT | M |
| `/profile/edit` | profiles, storage | PUT `/me` | JWT | L |
| `/settings` | account_preferences | `/me/preferences` | JWT | L |
| `/account` | Edge delete-account | DELETE `/me` | JWT | H |
| `/reset-password` | auth recovery | `/auth/account/reset-password/*` | Guest | H |
| `/blocked-users` | RPC block list | `/profiles/blocked` | JWT | M |
| `/my-plans` | RPC plans | GET `/me/plans` | JWT | M |
| `/people/*` | RPC search/follow | `/profiles/search`, `/follows/*` | JWT | M |
| `/user/[id]` | RPC profile card | `/profiles/{id}/*` | JWT | M |
| `/post/[id]` | posts, comments | `/posts/{id}`, comments | JWT | L |
| `/story/*` | stories RPC, storage | `/stories/*`, `/media` | JWT | M |
| `/event/[id]` | events, RPC participation | `/events/{id}/*` | JWT | M |
| `/event/[id]/chat` | RPC + **Realtime** | REST + **WebSocket** | JWT | **H** |
| `/event/[id]/check-in` | RPC roster/check-in | `/events/{id}/roster`, check-in | Mod | M |
| `/event/[id]/poster` | events (read) | GET `/events/{id}` | JWT | L |
| `/event-share/[id]` | RPC public share | GET `/events/share/{id}` | Public | M |
| `/community/[id]/*` | communities, RPC | `/communities/{id}/*` | JWT | M |
| `/group/[id]` | group events/members | `/communities/{id}/*` | JWT | M |
| `/organizer-request` | RPC create event, storage | POST `/events`, `/media` | JWT | M |
| `/city-radar`, `/city-event/[id]` | RPC radar/tickets | `/city-events/*` | JWT | M |
| `/advantages/*` | partner tables, RPC | `/advantages/*` | JWT | M |
| `/legal/[document]` | Static | — | — | — |

**Mobile screens analyzed: 37 routes**

## Admin screens

| Screen | Current data source | Target endpoint(s) | Auth | Difficulty |
|--------|---------------------|-------------------|------|------------|
| `/admin` dashboard | service role tables | `/admin/dashboard`, CRUD | Admin+MFA | H |
| `/admin/login` | auth | `/auth/login` | Guest | H |
| `/admin/mfa` | Supabase MFA | Spring MFA | Admin | H |
| `/admin/team` | platform_team_members | `/admin/team` | Admin | M |
| `/admin/advantages` | partner_*, RPC redeem | `/admin/advantages/*` | Admin | M |
| Public legal pages | Static | — | — | — |
| `/event-share/[id]` | Bridge only | Public GET share | Public | L |
| `/reset-password` | auth bridge | `/auth/reset-password/*` | Guest | M |
| `/api/health` | fetch health | `/management/health` | Public | L |

**Admin routes analyzed: 15**

## Shared lib modules

| Module | Current | Target client |
|--------|---------|---------------|
| `supabase.ts` | Supabase client | `apiClient.ts` (axios/fetch + JWT) |
| `auth.tsx` | Supabase Auth | Auth context + `/auth/login` |
| `storage.ts` | Supabase Storage | `/media` multipart |
| `notifications.ts` | RPC + Expo push | `/push-tokens` + FCM native |

## Realtime migration

| Screen | Current | Target |
|--------|---------|--------|
| Event chat | Supabase channel | STOMP `/topic/event.{id}` or SSE |

## Edge function migration

| Screen | Current | Target |
|--------|---------|--------|
| Assistant | `functions.invoke` | POST `/ai/chat` |
| Account delete | `functions.invoke` | DELETE `/me` |
