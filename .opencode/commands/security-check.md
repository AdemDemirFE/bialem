# /security-check

Check authentication and authorization for an endpoint or flow.

## Steps

1. Identify the endpoint or flow.
2. Trace the auth chain:
   Frontend Request → Authorization Header → JWT → Claims → Authentication → Authorities → Security Rules → Endpoint → DB User/Role.
3. Verify roles/authorities in the database with PostgreSQL MCP if needed.
4. Test authenticated, unauthenticated, and insufficient-role access.
5. Report findings.

## Output

- Security configuration status.
- Missing or incorrect roles/authorities.
- Recommended fix.
