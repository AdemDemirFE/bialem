# Bialem AI Development Rules

## Project Overview

Bialem is a community, event, and admin platform.

- `mobile/`: Vite + React Native Web + Capacitor (web and native)
- `admin/`: Next.js admin panel
- `backend/`: JHipster / Spring Boot + PostgreSQL
- `shared/`: Shared Spring API client
- `docs/`: Product and migration docs

The product UI connects to its own Spring Boot backend, not Expo/Supabase.

## Core Principle

> **Find existing code first, then change it.**

Never create a new entity, service, controller, component, endpoint, or dependency without checking the existing implementation first. If a feature already exists, reuse it. Do not duplicate. Do not refactor unnecessarily. Do not touch unrelated files.

## Architecture

- Backend: Spring Boot 3.4.x, Java 17, JHipster 8.11
- Frontend mobile: Vite + React Native Web + Capacitor + Ionic-style patterns
- Frontend admin: Next.js
- Database: PostgreSQL (source of truth)
- Migrations: JHipster JDL + Liquibase
- Search/code graph: Codebase Memory MCP

## Backend Rules

- Follow the existing package structure under `com.bialem.backend`.
- Reuse existing DTO, mapper, service, and repository patterns.
- Use JHipster-generated patterns where present.
- Add entities via JDL, then regenerate Liquibase changelogs.
- Never manually modify production database schema.
- Use MapStruct for DTO/entity mapping.
- Use Spring Data JPA repositories with criteria/specification filters.
- Validate inputs with Bean Validation (`@Valid`, `@NotNull`, etc.).
- Return proper HTTP status codes and problem-detail errors.
- Use `@Transactional` for multi-step DB operations.
- Integrate security via Spring Security / JWT / roles / authorities.

## Frontend Rules

- Follow existing Ionic/React component architecture in `mobile/`.
- Reuse existing API client (`shared/spring-client.ts` and copies in `mobile/`, `admin/`).
- Preserve existing mobile navigation, routing, and safe-area patterns.
- Use existing loading, error, and empty-state patterns.
- Do not introduce a new UI library unless explicitly required.
- Build mobile-first; web admin uses Next.js patterns.

## Database Rules

- PostgreSQL is the source of truth.
- Check existing entities and JDL files in `backend/.jhipster/` before adding new ones.
- Define relationships, foreign keys, indexes, and unique constraints explicitly.
- Every new entity flow: Entity → Relationship → JDL → Liquibase → Repository → Service → API.
- Never run destructive SQL directly on production.

## Authentication & Authorization

- JWT-based authentication.
- Roles and authorities stored in DB; mapped via Spring Security.
- Use `@PreAuthorize` or Security DSL to protect endpoints.
- For 401/403 issues, trace: Frontend Request → Authorization Header → JWT → Claims → Authentication → Authorities → Security Rules → Endpoint → DB User/Role.
- Do not hide 403/401 with frontend workarounds.

## API Rules

- REST conventions under `/api/`.
- Naming: plural nouns, kebab-case, HTTP verbs for actions.
- Every endpoint must consider: authentication, authorization, validation, error response, pagination, filtering, DTO.
- Reuse existing endpoint patterns before creating new ones.

## UI/UX Rules

- Mobile-first design.
- Consistent loading, error, and empty states.
- Reusable components only.
- Preserve navigation hierarchy and back-button behavior.
- Respect safe areas and platform differences (iOS/Android).

## Testing Rules

- Backend: compile, unit tests, integration tests (`./mvnw test`).
- API tests must cover authentication, authorization, 401, 403, validation.
- Database tests must cover migration, relationships, constraints.
- Frontend: build, routes, API calls, error states.
- Payment tests: success, failure, duplicate callback, refund.
- Social tests: view, duplicate view, reaction, reaction change, unauthorized access.
- Run the smallest relevant test scope, not the whole suite every time.

## MCP Usage

Use MCPs task-appropriately. Do not call them unnecessarily.

- **Codebase Memory MCP**: code relationships, symbol search, callers/callees, architecture.
- **PostgreSQL MCP**: database queries, schema inspection, user/role checks.
- **Docker MCP**: containers, logs, stats, compose.
- **Chrome DevTools MCP**: browser/network debugging.
- **GitHub MCP**: commits, issues, history.
- **Context7 MCP**: library/framework/API documentation lookup.

## Codebase Memory Usage

- Always search Codebase Memory before scanning the repository.
- Use `search_graph` for symbols/functions/classes/routes.
- Use `trace_path` for callers, callees, and data flow.
- Use `get_code_snippet` for exact source after locating a symbol.
- Use `query_graph` for complex multi-hop patterns.
- Use `get_architecture` for orientation in unfamiliar areas.
- Avoid full-repository scans and repeated reads of unchanged files.

## Token Efficiency

- Do not scan the entire repository for every task.
- Do not open large files without a reason.
- Do not analyze the same file repeatedly.
- Do not produce long plans unless requested.
- Keep explanations short; implement first, explain afterward.
- Preferred flow: Search → Locate → Modify → Test.

## Security

- Never commit secrets, API keys, or payment credentials.
- Never store card data in the database.
- Validate and sanitize all inputs.
- Use parameterized queries/JPQL; avoid raw SQL concatenation.
- Keep dependencies up to date; do not add unnecessary libraries.
- Follow OWASP top 10 basics: auth, injection, XSS, CSRF, misconfiguration.

## Production Safety

- Do not run destructive operations on production databases unless explicitly instructed.
- Prefer migrations over manual schema changes.
- Keep credentials in environment variables / secret managers, never in source.
- Verify changes in local/test before staging/production.

## Git Rules

- Do not run `git commit`, `git push`, `git reset`, `git rebase`, or other mutations unless explicitly asked.
- Before any commit, review `git status`, `git diff`, and recent `git log`.
- Stage only intended files; never commit secrets.
- Write concise commit messages matching repo style.

## Definition of Done

- Existing code checked and reused where possible.
- Minimal, focused change set.
- No duplicate entities/services/components.
- Build compiles and targeted tests pass.
- No unrelated files modified.
- No secrets or credentials added.
- Final result reported briefly as:

```text
Yapıldı:
- ...

Değişen:
- ...

Test:
- ...

Kalan:
- ...
```
