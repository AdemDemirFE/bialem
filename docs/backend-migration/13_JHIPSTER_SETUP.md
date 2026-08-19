# JHipster Setup Guide (Manual — Do Not Run in This Phase)

You will create the backend **manually** after reviewing this analysis. **Do not run JHipster during the analysis phase.**

## Recommended generator options

| Wizard field | Value |
|--------------|-------|
| Application type | **Monolithic application** |
| Base name | `bialem` |
| Package name | `com.bialem.backend` |
| Java version | **21** |
| Build tool | **Maven** |
| Authentication | **JWT** (stateless API) |
| Database | **PostgreSQL** |
| Production database | **PostgreSQL** |
| Database migration | **Liquibase** |
| Client framework | **No** (no generated Angular/React client) |
| Other languages | No |
| Internationalization | Optional (API error messages TR can be app-layer) |

## JHipster feature flags (when available)

| Option | Recommendation |
|--------|----------------|
| DTO mapping | **MapStruct** |
| Service layer | **Service classes** (not serviceImpl only) |
| Pagination | **Spring Data** (Pageable) |
| Cache | Second-level cache optional; Redis later if needed |
| Search | Elasticsearch **No** for v1 (PostgreSQL full-text sufficient) |
| WebSocket | **Enable** if event chat uses STOMP; else add post-generation |
| API docs | **SpringDoc OpenAPI** (recommended) |

## Version note

JHipster release line evolves independently of this repo. Before generation:

1. Check https://www.jhipster.tech/releases/
2. Confirm **UUID** entity ID support for PostgreSQL
3. Confirm **Java 21** + **Spring Boot 3.5.x** compatibility for chosen JHipster version

If Spring Boot 3.5 is not yet supported by stable JHipster, use latest supported 3.4.x and document delta in `15_OPEN_QUESTIONS.md`.

## Post-generation checklist

1. Import or merge `backend/bialem.jdl` (canonical Spring/JHipster JDL; do not use a blank `cityPlatform` dump)
2. Apply `09_DATABASE_POST_GENERATION.md` SQL (indexes, partial indexes, checks JDL missed)
3. Implement `POST-JHIPSTER MANUAL ACTION` items from JDL comments
4. Configure `application-*.yml`: MinIO/S3, Firebase, OpenAI, Ticketmaster secrets
5. **Do not** expose service role keys to mobile/admin

## Suggested first commands (after you decide to generate)

```bash
# Example only — run manually when ready
npm install -g generator-jhipster
mkdir backend && cd backend
jhipster
# Follow wizard using table above
jhipster import-jdl bialem.jdl
```

## Package layout target

See `01_CURRENT_ARCHITECTURE.md` and domain package plan in analysis summary:

```text
com.bialem.backend
├── config
├── security
├── common
├── identity / profile / community / event / ...
```

## What JHipster provides vs custom work

| Provided | Custom |
|----------|--------|
| User, Authority, JWT filter | Profile @MapsId to User UUID |
| Liquibase baseline | Import 54 migrations worth of rules into services |
| REST CRUD scaffolding | 54 RPC equivalents as services |
| MapStruct DTOs | Authorization matrix from RLS |

## Related files

- `bialem.jdl` — domain model (`backend/bialem.jdl`)
- `09_DATABASE_POST_GENERATION.md` — indexes & constraints
- `14_DATA_MIGRATION_STRATEGY.md` — data cutover
- `07_SUPABASE_TO_SPRING_MATRIX.md` — Supabase replacement map
