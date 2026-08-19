# Target Project Structure

Not created in this phase. Reference layout for monorepo after backend generation.

```text
bialem/
├── mobile/                 # Expo app (unchanged in analysis phase)
├── admin/                  # Next.js admin + public pages
├── backend/                # JHipster Spring Boot (YOU create manually)
│   ├── src/main/java/com/bialem/backend/
│   ├── src/main/resources/config/liquibase/
│   └── pom.xml
├── docs/
│   └── backend-migration/  # This analysis pack
├── infra/                  # Future: Docker Compose, K8s, MinIO, PostgreSQL
│   ├── docker-compose.yml
│   └── README.md
└── legacy/                 # Optional: Supabase export snapshots, read-only
    └── supabase-export/
```

## backend/ (future JHipster output)

```text
backend/
├── src/main/java/com/bialem/backend/
│   ├── BialemApp.java
│   ├── config/
│   ├── security/
│   ├── web/rest/
│   ├── service/
│   ├── repository/
│   └── domain/
├── src/main/resources/
│   ├── config/application.yml
│   └── config/liquibase/changelog/
└── src/test/
```

## infra/ (recommended)

```text
infra/
├── docker-compose.dev.yml    # PostgreSQL + MinIO + backend
├── docker-compose.prod.yml
└── env.example
```

## legacy/

Store Supabase schema dumps and anonymized data exports during migration window. Do not commit secrets.

## Spring domain packages (post-generation refactor target)

```text
com.bialem.backend
├── config
├── security
├── common
├── identity          # User registration, activation
├── profile
├── community
├── partner
├── event
├── cityradar
├── social
├── chat
├── notification
├── moderation
├── advantage
├── gamification
├── media
├── ai
└── integration
```

Move generated entities/services into these packages incrementally after JHipster bootstrap.
