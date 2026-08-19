# API Contract (Target REST)

**Base path:** `/api/v1`  
**Format:** JSON  
**Auth:** Bearer JWT unless noted  
**Pagination:** Spring `Pageable` (`page`, `size`, `sort`) where lists are large

Frontend not changed in this phase — this contract guides backend generation and future client migration.

---

## Auth `/api/v1/auth`

| Method | Path | Auth | Request | Response | Supabase equivalent |
|--------|------|------|---------|----------|---------------------|
| POST | `/register` | Public | `{ email, password, displayName, username }` | `{ userId, activationRequired }` | `auth.signUp` + profile trigger |
| POST | `/login` | Public | `{ email, password }` | `{ id_token, refresh_token }` | `signInWithPassword` |
| POST | `/logout` | JWT | — | 204 | `signOut` + deactivate push |
| POST | `/activate` | Public | `{ token }` | 200 | email confirm |
| POST | `/account/reset-password/init` | Public | `{ email }` | 202 | `resetPasswordForEmail` |
| POST | `/account/reset-password/finish` | Public | `{ token, newPassword }` | 200 | recovery flow |

---

## Me `/api/v1/me`

| Method | Path | Auth | Supabase equivalent |
|--------|------|------|---------------------|
| GET | `/me` | JWT | `profiles` SELECT |
| PUT | `/me` | JWT | `profiles` UPDATE |
| PUT | `/me/avatar` | JWT | storage + profile |
| GET | `/me/preferences` | JWT | `account_preferences` |
| PUT | `/me/preferences` | JWT | upsert preferences |
| GET | `/me/plans` | JWT | RPC `get_my_profile_plans` |
| DELETE | `/me` | JWT | `delete-account` function |

---

## Profiles `/api/v1/profiles`

| Method | Path | Auth | Supabase equivalent |
|--------|------|------|---------------------|
| GET | `/profiles/search?q=` | JWT | RPC `search_public_profiles` |
| GET | `/profiles/{id}/card` | JWT/Guest | RPC `get_public_profile_card` |
| GET | `/profiles/{id}/follow-summary` | JWT | RPC `get_public_follow_summary` |
| GET | `/profiles/{id}/reliability` | JWT | RPC `get_user_reliability` |
| GET | `/profiles/{id}/badges` | JWT | RPC `get_user_honor_badges` |
| GET | `/profiles/{id}/reviewable-events` | JWT | RPC `get_reviewable_events` |
| POST | `/profiles/{id}/reviews` | JWT | `user_reviews` INSERT |
| POST | `/profiles/{id}/block` | JWT | RPC `set_profile_block` |

---

## Follows `/api/v1/follows`

| Method | Path | Auth | Supabase equivalent |
|--------|------|------|---------------------|
| PUT | `/follows/{userId}` | JWT | RPC `set_profile_follow_state` |
| GET | `/follows/requests` | JWT | RPC `get_my_follow_requests` |
| POST | `/follows/requests/{id}/review` | JWT | RPC `review_follow_request` |
| GET | `/follows/connections` | JWT | RPC `get_public_follow_connections` |

---

## Communities `/api/v1/communities`

| Method | Path | Auth | Supabase equivalent |
|--------|------|------|---------------------|
| GET | `/communities` | JWT | list + membership |
| GET | `/communities/{id}` | JWT | `communities` + members |
| POST | `/communities/{id}/join` | JWT | RPC `join_community` |
| DELETE | `/communities/{id}/join` | JWT | RPC cancel/leave |
| POST | `/communities/{id}/members/{memberId}/review` | Mod | RPC `review_community_membership` |
| GET | `/communities/{id}/members` | JWT | RPC member directory |
| POST | `/communities/{hubId}/groups` | Mod | RPC `create_community_group` |
| POST | `/communities/{hubId}/partner-groups` | Mod | RPC `create_partner_group` |
| GET | `/communities/{id}/assistants` | JWT | RPC get assistants |
| PUT | `/communities/{id}/assistants` | Mod | RPC set/remove assistant |

---

## Events `/api/v1/events`

| Method | Path | Auth | Supabase equivalent |
|--------|------|------|---------------------|
| GET | `/events/feed` | JWT | `events` + follows |
| GET | `/events/{id}` | JWT | event detail |
| GET | `/events/share/{id}` | **Public** | RPC `get_public_event_share` |
| POST | `/events` | JWT | RPC `create_group_event` |
| POST | `/events/{id}/participation` | JWT | RPC `request_event_participation` |
| DELETE | `/events/{id}/participation` | JWT | RPC `cancel_event_participation` |
| GET | `/events/{id}/participation-summary` | JWT | RPC |
| POST | `/events/{id}/moderate` | Mod | RPC `moderate_group_event` |
| POST | `/events/{id}/cancel` | Mod | RPC `cancel_event` |
| GET | `/events/{id}/roster` | Mod | RPC roster |
| POST | `/events/{id}/participants/{pid}/check-in` | Mod | RPC check-in |
| POST | `/events/{id}/ratings` | JWT | `event_ratings` UPSERT |
| GET | `/events/{id}/messages` | JWT | RPC chat messages |
| POST | `/events/{id}/messages` | JWT | INSERT message |
| WS | `/events/{id}/chat` | JWT | Realtime channel |

---

## City events `/api/v1/city-events`

| Method | Path | Auth | Supabase equivalent |
|--------|------|------|---------------------|
| GET | `/city-events/radar?city=` | JWT | RPC `get_city_radar` |
| GET | `/city-events/{id}` | JWT | radar item |
| GET | `/city-events/{id}/ticket-offers` | JWT | RPC ticket offers |
| PUT | `/city-events/{id}/interest` | JWT | RPC set/clear interest |

---

## Posts, comments, stories

| Method | Path | Auth | Supabase equivalent |
|--------|------|------|---------------------|
| GET | `/posts/feed` | JWT | `posts` |
| GET | `/posts/{id}` | JWT | post detail |
| POST | `/posts/{id}/comments` | JWT | `comments` |
| GET | `/stories/feed` | JWT | RPC `get_story_feed` |
| POST | `/stories` | JWT | RPC `create_story_with_audience` |
| GET | `/stories/{id}` | JWT | RPC detail + mark viewed |

---

## Notifications `/api/v1/notifications`

| Method | Path | Auth | Supabase equivalent |
|--------|------|------|---------------------|
| GET | `/notifications` | JWT | `notifications` list |
| PUT | `/notifications/read-all` | JWT | bulk update |
| PUT | `/notifications/{id}/read` | JWT | single update |
| POST | `/push-tokens` | JWT | RPC register |
| DELETE | `/push-tokens/current` | JWT | RPC deactivate |

---

## Advantages `/api/v1/advantages`

| Method | Path | Auth | Supabase equivalent |
|--------|------|------|---------------------|
| GET | `/advantages/venues` | JWT | `partner_venues` |
| GET | `/advantages/venues/{id}` | JWT | venue + offers |
| POST | `/advantages/offers/{id}/issue` | JWT | RPC issue redemption |
| POST | `/advantages/redeem` | Staff | RPC `redeem_partner_offer` |

---

## AI `/api/v1/ai`

| Method | Path | Auth | Request | Supabase equivalent |
|--------|------|------|---------|---------------------|
| POST | `/ai/chat` | JWT | `{ messages: [{role, content}] }` | Edge `bialem-assistant` |

Rate limit: 20/hour via `claim_ai_request` logic.

---

## Admin `/api/v1/admin`

Requires `ROLE_ADMIN` + MFA session.

| Method | Path | Supabase equivalent |
|--------|------|---------------------|
| GET | `/admin/dashboard` | aggregates |
| PUT | `/admin/events/{id}/approve` | events UPDATE |
| PUT | `/admin/events/{id}/reject` | events UPDATE |
| PUT | `/admin/reports/{id}` | reports UPDATE |
| PUT | `/admin/profiles/{id}/status` | profiles UPDATE |
| POST | `/admin/communities/category-hub` | INSERT community |
| POST | `/admin/communities/partner-hub` | INSERT partner hub |
| PUT | `/admin/communities/{id}/lead-moderator` | RPC set lead |
| CRUD | `/admin/advantages/**` | partner tables |
| CRUD | `/admin/team/**` | platform_team_members |

---

## Media `/api/v1/media`

| Method | Path | Auth | Supabase equivalent |
|--------|------|------|---------------------|
| POST | `/media/{bucket}` | JWT | storage upload |
| DELETE | `/media/{bucket}/{path}` | JWT | storage remove |

Buckets: `profile-avatars`, `stories`, `event-covers`, `community-covers`, `post-media`

---

## Reports `/api/v1/reports`

| Method | Path | Auth | Supabase equivalent |
|--------|------|------|---------------------|
| POST | `/reports` | JWT | `reports` INSERT |

---

## DTO guidelines

- Use MapStruct entity ↔ DTO
- Never expose `service_role` fields
- UUID as string in JSON
- Timestamps ISO-8601 UTC
- Error format: JHipster Problem Details or RFC 7807

See `03_SCREEN_API_MATRIX.md` for screen-level mapping.
