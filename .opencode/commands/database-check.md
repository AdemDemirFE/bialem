# /database-check

Check database schema and migrations.

## Steps

1. Inspect existing entities/JDL files.
2. Check Liquibase changelogs.
3. Verify relationships, indexes, and constraints.
4. Run migrations locally if changed.
5. Use PostgreSQL MCP to inspect schema if needed.

## Output

- Schema status.
- Migration status.
- Issues found.
- Recommended fix.
