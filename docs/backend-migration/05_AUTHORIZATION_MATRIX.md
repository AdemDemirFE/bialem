# Authorization Matrix

Derived from RLS policies, RPC guards, and UI rules. Target: Spring Security + service-layer checks (not database RLS).

Legend: ✓ allowed | ✗ denied | ◐ conditional (ownership/membership/moderator)

## Global roles

| Role | Source |
|------|--------|
| Guest | Unauthenticated |
| Member | Authenticated + profile complete |
| Organizer | `user_roles` / event creator patterns |
| Moderator | Community lead/assistant or platform team |
| Admin | `user_roles.admin` + MFA (admin panel) |

## Resource matrix (summary)

| Resource | Guest | Member | Organizer | Moderator | Admin | Owner check |
|----------|-------|--------|-----------|-----------|-------|-------------|
| Public event share | ✓ | ✓ | ✓ | ✓ | ✓ | — |
| Published event read | ◐ | ✓ | ✓ | ✓ | ✓ | group visibility |
| Event create | ✗ | ◐ | ✓ | ✓ | ✓ | must be group member/moderator |
| Event publish (platform) | ✗ | ✗ | ✗ | ◐ | ✓ | federation rules |
| Event participate | ✗ | ✓ | ✓ | ✓ | ✓ | not blocked |
| Event chat | ✗ | ◐ | ◐ | ◐ | ✓ | approved participant only |
| Event check-in scan | ✗ | ✗ | ◐ | ✓ | ✓ | event manager |
| Community read | ◐ | ◐ | ✓ | ✓ | ✓ | visibility + membership |
| Community join | ✗ | ✓ | ✓ | ✓ | ✓ | approval if private |
| Group create | ✗ | ✗ | ◐ | ✓ | ✓ | hub moderator |
| Partner group create | ✗ | ✗ | ◐ | ✓ | ✓ | partner hub mod |
| Post read | ◐ | ◐ | ✓ | ✓ | ✓ | visibility enum |
| Post create | ✗ | ✓ | ✓ | ✓ | ✓ | author = self |
| Comment create | ✗ | ✓ | ✓ | ✓ | ✓ | author = self |
| Report create | ✗ | ✓ | ✓ | ✓ | ✓ | reporter = self |
| Report resolve | ✗ | ✗ | ◐ | ✓ | ✓ | moderator |
| Profile read | ◐ | ◐ | ✓ | ✓ | ✓ | discoverable / connection |
| Follow | ✗ | ◐ | ✓ | ✓ | ✓ | not blocked; private → request |
| Block | ✗ | ✓ | ✓ | ✓ | ✓ | blocker = self |
| Notification read | ✗ | ✓ | ✓ | ✓ | ✓ | user_id = self |
| Push token register | ✗ | ✓ | ✓ | ✓ | ✓ | self only |
| City events read | ✓ | ✓ | ✓ | ✓ | ✓ | published only |
| Advantage venue read | ✓ | ✓ | ✓ | ✓ | ✓ | active venues |
| Issue redemption QR | ✗ | ✓ | ✓ | ✓ | ✓ | self |
| Redeem offer (staff) | ✗ | ◐ | ✗ | ◐ | ✓ | partner_venue_staff |
| AI chat | ✗ | ✓ | ✓ | ✓ | ✓ | rate limit |
| Admin dashboard | ✗ | ✗ | ✗ | ✗ | ✓ | is_admin + MFA |
| Honor badge award | ✗ | ✗ | ◐ | ◐ | ✓ | moderator RPC |

## Non-role checks (implement in services)

| Check | Used for |
|-------|----------|
| `is_approved_community_member` | Event visibility, posts |
| `has_community_assistant_permission` | Assistant-scoped actions |
| `is_lead_community_moderator` | Hub management |
| `can_access_event_chat` | Chat read/write |
| `can_manage_event_participants` | Roster, check-in |
| `is_partner_venue_staff` | Advantage redeem |
| `should_deliver_push_notification` | Push prefs by type |
| Block graph | Follow, profile view, notifications |
| Private profile + follow request | People discovery |
| `checked_in` status | Ratings, badges |
| Partner vs global moderator | Partner hub scope |

## Admin panel (current)

```
Session → is_admin RPC → MFA AAL2 → service_role client (bypass RLS)
```

## Target Spring pattern

```text
@PreAuthorize("hasAuthority('ROLE_ADMIN')")  // coarse
+ @communityModerator(#communityId)          // custom
+ ownership validation on entity
+ query filters (no cross-tenant leaks)
```

## RLS → Spring mapping examples

| RLS policy | Spring |
|------------|--------|
| `events_read_group_visible` | `EventQueryService.findVisibleForUser(userId)` |
| `community_members_insert_admin_only` | Removed — join via `MembershipService` |
| `follows` INSERT revoked | `FollowService.follow()` only |
| `push_tokens` DML revoked | `PushTokenService.register()` only |

Full policy list: migration `0002_rls.sql` + updates through `0054`.
