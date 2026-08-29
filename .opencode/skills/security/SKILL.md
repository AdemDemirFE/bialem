# Bialem Security

Authentication and authorization rules for Bialem.

## Scope

- JWT
- Spring Security
- Roles
- Authorities
- Permissions
- RBAC
- `@PreAuthorize`
- Endpoint security
- CORS
- 401 / 403 problems

## Rules

- Use existing JWT and Spring Security configuration.
- Roles and authorities are stored in DB and mapped via Spring Security.
- Protect endpoints with `@PreAuthorize` or Security DSL.
- Do not hide 403/401 errors with frontend workarounds.

## 401 / 403 Debug Chain

For any authentication or authorization issue, trace the full chain before proposing a fix:

```text
Frontend Request
 ↓
Authorization Header
 ↓
JWT
 ↓
JWT Claims
 ↓
Authentication
 ↓
Authorities
 ↓
Security Rules
 ↓
Endpoint
 ↓
Database User/Role
```

Use PostgreSQL MCP to inspect user/role relationships when needed.

## JWT

- Validate token format, expiry, and signature configuration.
- Check that the frontend sends the token in the expected header.

## Roles / Authorities

- Distinguish roles from authorities.
- Verify role assignment in the database.
- Use existing `Role` / `UserRole` / `Authority` entities.

## Endpoint Security

- Apply the principle of least privilege.
- Test both authenticated and unauthenticated access.
- Test access with insufficient roles.

## CORS

- Do not weaken CORS to fix local issues.
- Follow existing CORS configuration patterns.

## Frontend Workarounds

- Redirecting on 403 or hiding the error is not a fix.
- Fix the root cause in auth/security configuration or role assignment.
