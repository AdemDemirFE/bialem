# Database Migration Map

Supabase table → JHipster entity → notes

| Supabase table | JHipster entity | ID | Notes |
|----------------|-----------------|-----|-------|
| `auth.users` | `User` (built-in) | UUID | Password hash migration TBD |
| `profiles` | `Profile` | UUID = User.id | @MapsId POST-JHIPSTER |
| `roles` | `Authority` or map codes | — | member→ROLE_MEMBER, etc. |
| `user_roles` | `User`↔`Authority` join | UUID | |
| `account_preferences` | `AccountPreferences` | userId PK | |
| `communities` | `Community` | UUID | self-ref parent, category |
| `community_members` | `CommunityMember` | UUID | |
| `community_moderator_assistants` | `CommunityModeratorAssistant` | UUID | |
| `events` | `Event` | UUID | |
| `event_participants` | `EventParticipant` | UUID | |
| `event_messages` | `EventMessage` | UUID | |
| `event_ratings` | `EventRating` | UUID | |
| `posts` | `Post` | UUID | |
| `post_media` | `PostMedia` | UUID | |
| `comments` | `Comment` | UUID | polymorphic target |
| `stories` | `Story` | UUID | |
| `story_views` | `StoryView` | UUID | |
| `story_community_targets` | `StoryCommunityTarget` | composite | POST-JHIPSTER |
| `follows` | `Follow` | UUID | |
| `follow_requests` | `FollowRequest` | UUID | |
| `blocks` | `Block` | UUID | |
| `user_reviews` | `UserReview` | UUID | |
| `reports` | `Report` | UUID | |
| `notifications` | `Notification` | UUID | JSON payload |
| `push_tokens` | `PushToken` | UUID | rename expo→fcm optional |
| `city_events` | `CityEvent` | UUID | |
| `city_event_interests` | `CityEventInterest` | UUID | |
| `city_event_ticket_offers` | `CityEventTicketOffer` | UUID | |
| `city_event_sync_logs` | `CityEventSyncLog` | UUID | |
| `partner_venues` | `PartnerVenue` | UUID | |
| `partner_offers` | `PartnerOffer` | UUID | |
| `partner_venue_staff` | `PartnerVenueStaff` | UUID | |
| `partner_offer_redemptions` | `PartnerOfferRedemption` | UUID | |
| `honor_badges` | `HonorBadge` | UUID | |
| `user_honor_badges` | `UserHonorBadge` | UUID | |
| `ai_usage_logs` | `AiUsageLog` | UUID | |
| `platform_team_members` | `PlatformTeamMember` | UUID | |

## Not migrated as tables

| Supabase | Target |
|----------|--------|
| `auth.*` sessions | JWT stateless |
| `storage.objects` | S3 objects (no PG row unless MediaAsset added) |
| `pg_net` queue | Removed |

## Column rename considerations

Keep DB column names snake_case in Liquibase for easier data import, use JPA `@Column(name=)` if needed.

## Seed data migrations

| Source migration | Content |
|------------------|---------|
| 0001 | roles seed |
| 0012, 0015, 0029 | example communities |
| 0017 | honor badge definitions |
| 0040 | founder badge (code `bialem-kurucusu`) |

## auth schema triggers → Java

| Trigger | Java service |
|---------|--------------|
| create_profile_for_auth_user | UserRegisteredEvent → create Profile + AccountPreferences |
| sync_profile_email_verification | UserActivatedEvent → profile.isVerified |

See `14_DATA_MIGRATION_STRATEGY.md`.
