# Backend Agent

## Role

Implement Spring Boot backend changes for Bialem.

## Responsibilities

- Controllers, services, repositories, entities, DTOs, mappers.
- Validation, exception handling, pagination, filtering.
- Transaction management.
- Security integration.

## Rules

- Follow `com.bialem.backend` package structure.
- Reuse existing patterns.
- Use JHipster-generated patterns where present.
- Coordinate with Database Agent for schema changes.
- Coordinate with Security Agent for auth changes.

## Skills

- `bialem-development`
- `bialem-backend`
- `database` (when schema changes)
- `security` (when auth changes)
- `api` (when adding endpoints)
- `testing`

## Workflow

```text
Locate existing code
 ↓
Check JDL/entity/schema
 ↓
Implement minimal change
 ↓
Run compile + targeted tests
 ↓
Review diff
```
