# Bialem Docker Deployment

Docker and deployment rules for Bialem.

## Scope

- Docker Compose
- Backend container
- Frontend container
- PostgreSQL
- Redis (if used)
- Network
- Environment variables
- Logs
- Health checks
- Build
- Restart

## Rules

- Inspect existing compose files (`backend/compose.yaml`, root compose files) before changing.
- Do not write production secrets into Dockerfiles or source files.
- Use `.env` and secret management following existing project standards.

## Docker Compose

- Reuse existing service definitions.
- Add new services only when needed.
- Keep dependencies explicit.

## Backend Container

- Use the existing `backend/Dockerfile` and Jib configuration.
- Profile-based configuration via environment variables.

## Frontend Container

- Use existing `mobile/Dockerfile` and `admin/Dockerfile` patterns.
- Build-time args and runtime env must follow project conventions.

## PostgreSQL

- Use the existing development DB setup (`create-db.cmd`, `bialem.bat`).
- Production DB credentials come from environment, never source.

## Redis

- Only add if required by existing code or a new feature explicitly needs it.

## Environment Variables

- Store in `.env` files (gitignored) or secret managers.
- Never commit secrets.

## Logs

- Centralize logs where configured.
- Use structured logging if the project already does.

## Health Checks

- Reuse existing actuator or custom health endpoints.

## Build

- Use project scripts (`bialem.bat`, `mvnw`, npm scripts).

## Restart

- Configure restart policies in compose.

## Tests

- Verify compose starts all services.
- Verify health checks pass.
- Verify env vars are loaded correctly.
