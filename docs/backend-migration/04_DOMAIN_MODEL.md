# Domain Model

Domains extracted from migrations + mobile/admin usage.

## Domain map

```text
identity          → JHipster User, Authority (member/organizer/moderator/admin)
profile           → Profile, AccountPreferences
community         → Community, CommunityMember, CommunityModeratorAssistant
partner           → Partner hub communities (Community.communityType=partner_hub)
event             → Event, EventParticipant, EventMessage, EventRating
cityradar         → CityEvent, CityEventInterest, CityEventTicketOffer, CityEventSyncLog
social            → Post, PostMedia, Comment, Follow, FollowRequest, Block, UserReview
chat              → EventMessage (subset of event domain)
notification      → Notification, PushToken
moderation        → Report, profile/event/post/message moderation fields
advantage         → PartnerVenue, PartnerOffer, PartnerVenueStaff, PartnerOfferRedemption
gamification      → HonorBadge, UserHonorBadge
media             → Storage metadata (paths on entities + MediaStorageService)
ai                → AiUsageLog, AiAssistantService (no chat persistence)
integration       → TicketmasterClient, PartnerFeedClient, CityEventSync
platform          → PlatformTeamMember
```

## Entity count (public schema)

**33 tables** in Supabase `public` (see `02_SUPABASE_INVENTORY.md`).

JHipster adds: `jhi_user`, `jhi_authority`, `jhi_user_authority`, Liquibase tables.

## Key relationships

```text
User 1──1 Profile
Profile 1──1 AccountPreferences
Community *──1 Community (parent, category)
Community 1──* CommunityMember *──1 Profile
Community 1──* CommunityModeratorAssistant
Community 1──* Event
Event 1──* EventParticipant
Event 1──* EventMessage
Event 1──* EventRating
Profile *──* Profile (Follow, FollowRequest, Block)
Community 1──* Post
Event 1──* Post (optional)
Post 1──* PostMedia
Profile 1──* Story
Story *──* Community (StoryCommunityTarget)
CityEvent 1──* CityEventInterest
CityEvent 1──* CityEventTicketOffer
PartnerVenue 1──* PartnerOffer
PartnerOffer 1──* PartnerOfferRedemption
HonorBadge 1──* UserHonorBadge
```

## Profile ↔ JHipster User

```text
User (JHipster built-in)
  id: UUID  ← preserve from auth.users
  login: email
  activated: email verified

Profile
  id: UUID  ← same as User.id (@MapsId)  [POST-JHIPSTER MANUAL ACTION]
  displayName, username (unique), avatarUrl, bio, city, status, isVerified
```

Do **not** duplicate `User`/`Role`/`UserRole` in JDL beyond JHipster defaults.

Map Supabase `roles` + `user_roles` → JHipster `Authority`:

| Supabase `roles.code` | JHipster Authority |
|-----------------------|-------------------|
| member | ROLE_MEMBER |
| organizer | ROLE_ORGANIZER |
| moderator | ROLE_MODERATOR |
| admin | ROLE_ADMIN |

`platform_team_members.role_code` (founder/team/support/editor) is **separate** from RBAC.

## Enums (from CHECK constraints, no PG enums)

See `bialem.jdl` for JDL enum definitions (~25 enums).

## ID strategy summary

| Area | Current | Target | Migration risk |
|------|---------|--------|----------------|
| All entity PKs | UUID | UUID | LOW if preserved |
| Profile PK | = auth user id | = User id | **HIGH** if sequential |
| Routes `/event/[id]` | UUID string | Same | HIGH if changed |
| Notification payload | UUID ids in JSON | Same | HIGH |
| Storage paths | `{userId}/file` | Same | HIGH |
| QR advantage token | UUID | Same | MEDIUM |
| Composite story targets | (storyId, communityId) | Same | MEDIUM |

**Recommendation:** Keep UUID everywhere. **POST-JHIPSTER MANUAL ACTION** for Profile @MapsId.

## Business rules (critical)

### Community
- Tree: category_hub → partner_hub / group
- visibility: public | private | invite_only
- membership: pending → approved/rejected/blocked
- lead_moderator required on root communities
- partner moderator scoped to partner hub — not global admin

### Event
- Status flow: draft → pending_approval → published | rejected; cancelled | completed
- Group moderation + optional platform moderation
- Only approved participants access chat (0052)

### Event participant
- Unique (event, user)
- Status: pending → approved | waitlisted | rejected; checked_in once; no_show

### Rating
- Unique (event, user); trigger validates attendance/eligibility

### Check-in
- Organizer scans participant; single transition to checked_in

### Advantage
- Issue: short-lived token + redemption_code
- Redeem: single use, staff RPC with transaction lock

### Notification
- Insert trigger → push; 0045 privacy prefs filter by category
- Push token: one active token per device install (0027)

## Spring package plan (target)

```text
com.bialem.backend
├── config
├── security
├── common
├── identity
├── profile
├── community
├── partner
├── event
├── cityradar
├── social
├── chat
├── notification
├── moderation
├── advantage
├── gamification
├── media
├── ai
└── integration
```

Generated code stays in default JHipster layout until refactor phase.
