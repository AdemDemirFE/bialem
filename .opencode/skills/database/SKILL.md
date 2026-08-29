# Bialem Database

Database rules for Bialem.

## Stack

- PostgreSQL (source of truth)
- JHipster JDL
- Liquibase

## Rules

- Never manually modify production database schema.
- Check existing entities in `backend/.jhipster/` before adding new ones.
- Define relationships, foreign keys, indexes, and unique constraints explicitly.
- Prefer migrations over ad-hoc schema changes.

## New Entity Flow

```text
Entity
 ↓
Relationship
 ↓
JDL
 ↓
Liquibase
 ↓
Repository
 ↓
Service
 ↓
API
```

## PostgreSQL

- Use PostgreSQL MCP for schema inspection and user/role checks.
- Use parameterized queries/JPQL; avoid raw SQL concatenation.
- Do not run destructive SQL on production unless explicitly instructed.

## Entity Relationships

- One-to-many / many-to-one: define join columns and fetch types.
- Many-to-many: prefer join tables with explicit names.
- Avoid duplicate relationships.

## JDL

- Add or update JDL files in `backend/.jhipster/`.
- Regenerate Liquibase changelogs via JHipster tooling.

## Liquibase

- Each change set must be small and reversible where possible.
- Add indexes and constraints in separate change sets.
- Test migrations locally before committing.

## Foreign Keys

- Name them clearly.
- Consider `on delete` behavior.

## Indexes

- Add indexes for query columns and foreign keys.
- Do not over-index.

## Unique Constraints

- Enforce uniqueness at the database level, not only in code.
- Examples: `storyId + viewerUserId`, `storyId + userId`.

## Enum / Status

- Prefer database enums or application-level enums with clear values.
- Document state transitions.

## Data Integrity

- Use transactions for multi-step writes.
- Validate referential integrity in code and schema.
