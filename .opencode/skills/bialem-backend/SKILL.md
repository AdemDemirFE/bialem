# Bialem Backend

Backend development rules for Bialem Spring Boot.

## Stack

- Spring Boot 3.4.x
- Java 17
- JHipster 8.11
- MapStruct
- Spring Data JPA
- Spring Security / JWT
- Liquibase

## Rules

- Follow the existing package structure under `com.bialem.backend`.
- Reuse existing DTO, mapper, service, and repository patterns.
- Use JHipster-generated patterns where present.
- Add entities via JDL, then regenerate Liquibase changelogs.
- Use MapStruct for DTO/entity mapping (`EntityMapper`).
- Use Spring Data JPA repositories with criteria/specification filters.
- Validate inputs with Bean Validation (`@Valid`, `@NotNull`, etc.).
- Return proper HTTP status codes and problem-detail errors.
- Use `@Transactional` for multi-step DB operations.
- Integrate security via `@PreAuthorize` or Security DSL.

## Controller

- REST conventions under `/api/`.
- Naming: plural nouns, kebab-case.
- Use existing `*Resource` patterns.
- Return `ResponseEntity` with DTOs.
- Handle `BadRequestAlertException` and validation errors consistently.

## Service

- Business logic lives in services, not controllers.
- Use constructor injection.
- Mark read-only transactions with `@Transactional(readOnly = true)`.
- Mark write transactions with `@Transactional`.

## Repository

- Extend `JpaRepository` or `JpaSpecificationExecutor`.
- Reuse existing criteria/filter patterns.

## Entity

- Add via JDL in `backend/.jhipster/`.
- Define relationships, foreign keys, indexes, and unique constraints.
- Use JHipster-generated equals/hashCode helpers.

## DTO & Mapper

- Use MapStruct mappers extending `EntityMapper`.
- Keep DTOs flat and focused on API contracts.

## Exception Handling

- Use existing problem-detail and `BadRequestAlertException` patterns.
- Do not swallow exceptions silently.

## Pagination & Filtering

- Reuse existing `Pageable` and criteria patterns.
- Support search/filter where existing endpoints already do.

## Security Integration

- Protect endpoints with roles/authorities.
- For 401/403 trace the full chain; see `security` skill.
