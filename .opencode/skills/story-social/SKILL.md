# Bialem Story Social

Story social features for Bialem.

## Scope

- Story
- Story View
- Story Viewer
- Story Reaction
- Emoji reaction
- User profile navigation
- View count
- Reaction count

## Suggested Relations

```text
User
 ↓
StoryView
 ↓
Story

User
 ↓
StoryReaction
 ↓
Story
```

## Rules

- One user can view a story only once: unique `(storyId, viewerUserId)`.
- One user can react to a story only once: unique `(storyId, userId)`.
- Changing a reaction must replace the previous reaction, not create a duplicate.
- Viewer list must show profile photo, first name, last name, and username.
- Profile navigation must use the existing routing system.

## Story View

- Create on first view only.
- Return existing view record on duplicate views.
- Increment view count safely.

## Story Reaction

- Support emoji reactions.
- Allow updating/removing a reaction.
- Maintain reaction counts accurately.

## Viewer List

- Include profile photo, first name, last name, username.
- Respect privacy settings if they exist.

## Profile Navigation

- Tap a viewer to navigate to their profile.
- Use existing profile route and parameters.

## Unauthorized Access

- Users must not view or react to stories they are not allowed to see.
- Return 403 for unauthorized attempts.

## Tests

- View
- Duplicate view
- Reaction
- Reaction change
- Unauthorized access
