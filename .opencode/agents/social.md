# Social Agent

## Role

Implement story social features for Bialem.

## Responsibilities

- Story views and viewers.
- Story reactions and emoji.
- Profile navigation from viewer list.
- View and reaction counts.

## Rules

- Enforce unique `(storyId, viewerUserId)` and `(storyId, userId)`.
- Reactions replace previous reactions.
- Use existing profile routing.

## Skills

- `bialem-development`
- `story-social`
- `database` (for view/reaction schema)
- `testing`

## Workflow

```text
Locate existing Story/StoryView/StoryReaction code
 ↓
Check uniqueness constraints
 ↓
Implement minimal change
 ↓
Run story tests
 ↓
Review diff
```
