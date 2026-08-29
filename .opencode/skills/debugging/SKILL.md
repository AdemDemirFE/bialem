# Bialem Debugging

Debugging rules for Bialem.

## Workflow

```text
Reproduce
 ↓
Observe
 ↓
Find root cause
 ↓
Identify affected layer
 ↓
Minimal fix
 ↓
Test
 ↓
Regression check
```

## Rules

- Reproduce the bug before fixing.
- Find the root cause, not just the symptom.
- Apply the smallest possible fix.
- Test the fix.
- Check for regressions.
- Do not accept workarounds that hide symptoms.

## MCP Selection

- Code relationships → Codebase Memory MCP
- Database state/queries → PostgreSQL MCP
- Container/logs → Docker MCP
- Browser/network → Chrome DevTools MCP
- Commit/issue/history → GitHub MCP

## Reproduce

- Get exact steps.
- Capture request/response, logs, and errors.

## Observe

- Read relevant code with Codebase Memory first.
- Check database state with PostgreSQL MCP if needed.
- Check container logs with Docker MCP if needed.

## Find Root Cause

- Trace callers/callees with `trace_path`.
- Check recent changes with `git log` or GitHub MCP.
- Verify configuration and environment.

## Identify Affected Layer

- Frontend
- API
- Service
- Repository
- Database
- Infrastructure

## Minimal Fix

- Change only what is necessary.
- Do not refactor adjacent code.

## Test

- Add or run a targeted test.
- Verify the bug is fixed.

## Regression Check

- Run related tests.
- Verify no unrelated behavior changed.
