# Bialem Development

Main development skill for Bialem. Applies to every task.

## Core Principle

> Find existing code first, then change it.

## Workflow

```text
User Task
 ↓
Codebase Memory Search
 ↓
Existing Implementation Detection
 ↓
Dependency / Relationship Check
 ↓
Minimal Change Plan
 ↓
Implementation
 ↓
Compile / Test
 ↓
Diff Review
 ↓
Final Result
```

## Rules

- Do not scan the whole repository without reason.
- Start with Codebase Memory (`search_graph`, `trace_path`, `get_architecture`).
- Locate relevant files, then read only what must change.
- Reuse existing implementations: entities, services, repositories, components, endpoints, utilities.
- Do not create duplicates.
- Do not invent new patterns if an existing one works.
- Do not add unnecessary abstractions or dependencies.
- Do not refactor unrelated code.
- Prefer minimal, focused changes.
- Keep explanations short; implement first, explain afterward.

## Skill Selection

Pick additional skills based on the task domain:

- Backend code → `bialem-backend`
- Frontend code → `bialem-frontend`
- Database change → `database`
- Auth / 401 / 403 → `security`
- Payment or ticket → `payments`
- Story / viewer / reaction → `story-social`
- Notification → `notifications`
- New endpoint → `api`
- Test work → `testing`
- Docker / compose → `docker-deployment`
- Bug investigation → `debugging`

Use only the skills required by the task.
