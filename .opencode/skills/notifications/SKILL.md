# Bialem Notifications

Notification rules for Bialem.

## Scope

- Push notification
- In-app notification
- Read / unread
- Notification center
- Event notification
- Ticket notification
- Payment notification
- Story reaction notification

## Rules

- Reuse the existing Notification entity, service, and push system.
- Do not create a new notification infrastructure unless explicitly required.
- Use Codebase Memory to locate `Notification`, `AppNotificationService`, `PushToken`, and related code.

## Existing System

- Search for `Notification`, `AppNotificationService`, `PushToken`, and Firebase push logic.
- Extend existing notification types instead of inventing new ones.

## Push Notification

- Use existing Firebase push integration.
- Handle token registration and invalidation.

## In-App Notification

- Reuse notification center UI if it exists.
- Support mark-as-read and unread counts.

## Notification Types

- Event
- Ticket
- Payment
- Story reaction
- Follow / follow request (if applicable)

## Read / Unread

- Track read timestamp per user.
- Provide unread count endpoint if not already present.

## Tests

- Notification creation
- Read/unread state
- Push delivery success and failure
- Unauthorized access to notifications
