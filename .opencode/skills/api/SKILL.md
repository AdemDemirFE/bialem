# Bialem API

API development rules for Bialem.

## Conventions

- Base path: `/api/`
- Naming: plural nouns, kebab-case
- HTTP verbs for actions

## Every Endpoint Checklist

Before creating or modifying an endpoint, verify:

- Authentication
- Authorization
- Validation
- Error response
- Pagination (if listing)
- Filtering (if listing)
- HTTP status codes
- DTO usage

## Rules

- Reuse existing endpoint patterns from `backend/src/main/java/com/bialem/backend/web/rest/`.
- Follow JHipster-generated resource patterns.
- Return `ResponseEntity` with DTOs.
- Use `@Valid` for request bodies.
- Use `@PreAuthorize` for authorization.

## Authentication

- Ensure the endpoint is included or excluded from security filters as intended.
- Test unauthenticated access returns 401 when required.

## Authorization

- Use roles/authorities consistently.
- Test insufficient roles return 403.

## Validation

- Validate input DTOs.
- Return 400 with problem details on validation failure.

## Error Response

- Use existing `BadRequestAlertException` and problem-detail patterns.
- Do not leak internal details in production error messages.

## Pagination

- Use `Pageable` for list endpoints.
- Return `Page<DTO>`.

## Filtering

- Reuse criteria/filter patterns.
- Do not reinvent query languages.

## HTTP Status

- 200 OK
- 201 Created
- 204 No Content
- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found
- 409 Conflict (when appropriate)

## DTO

- Map entities to DTOs before returning.
- Use MapStruct mappers.
