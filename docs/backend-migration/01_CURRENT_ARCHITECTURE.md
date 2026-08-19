# Bialem — Current Architecture

**Date:** 2026-08-15  
**Scope:** Read-only repository analysis for Supabase → JHipster/Spring Boot migration planning.

## Repository layout

```text
bialem/  (repo root: bidunya)
├── mobile/          Expo SDK 54 + React Native 0.81 + expo-router
├── admin/           Next.js 15 admin + public legal/deep-link pages
├── supabase/        54 SQL migrations + 3–4 edge functions
├── scripts/         Local dev, release, rebrand helpers
├── docs/            TR setup/runbook guides
├── package.json     npm workspaces (mobile, admin)
└── bialem.bat       Windows launcher menu
```

**No `backend/` folder exists yet.** Target structure documented in migration plan.

## Runtime stack (today)

| Layer | Technology |
|-------|------------|
| Mobile | Expo managed workflow, expo-router, React 19 |
| Admin | Next.js App Router, server actions, middleware |
| API / DB | Supabase (PostgreSQL + Auth + Storage + Edge Functions) |
| Auth | Supabase Auth (email/password, email verification, recovery) |
| Authorization | PostgreSQL RLS + ~80 SECURITY DEFINER RPCs |
| Realtime | Supabase Realtime (event chat only) |
| Push | Expo Push API → FCM; tokens in `push_tokens`; DB trigger via `pg_net` |
| AI | Supabase Edge Function `bialem-assistant` + OpenAI |
| City sync | Edge Function `sync-city-events` + external cron |
| Media | Supabase Storage (5 buckets) |
| Deploy | EAS Build (mobile), Vercel-style hosting (admin), Supabase cloud |

## Data flow

```text
Mobile / Admin (publishable key or service role)
        ↓
Supabase PostgREST / RPC / Auth / Storage / Functions
        ↓
PostgreSQL (public schema, 33 tables)
        ↓
Triggers → notifications, push (pg_net), honor badges, Turkish normalization
```

## Business logic location

Most write paths are **not** direct table INSERT from clients:

- **54+ RPC functions** enforce membership, moderation, participation, follow privacy, redemption, etc.
- **RLS** on all public tables (74+ policies)
- **Triggers** for notifications, badges, validation, profile lifecycle

Mobile uses **54 distinct RPC names**; admin uses service role + 3 RPCs (`is_admin`, `set_community_lead_moderator`, `redeem_partner_offer`).

## Auth model (current)

| Feature | Implementation |
|---------|----------------|
| Register | `auth.signUp` + profile trigger on `auth.users` INSERT |
| Login | `auth.signInWithPassword` |
| Email verification | Supabase Auth + profile `is_verified` sync trigger |
| Password reset | Email → `bialem.app/reset-password` → `bialem://reset-password` |
| Session | JWT in AsyncStorage (mobile), cookies (admin) |
| Admin gate | `is_admin` RPC + Supabase MFA (TOTP, AAL2) |
| Account deletion | Edge function `delete-account` + `cleanup_current_user_account` RPC |
| Google / Apple login | **Not implemented** in mobile or admin code |

## Branding

- Product name: **Bialem** (`Bi'Alem` in app.json)
- Android package: `com.bialem.app`
- Deep link scheme: `bialem://`
- Web: `https://bialem.app`
- Residual Bidünya references documented in `12_BIDUNYA_TO_BIALEM_AUDIT.md`

## Target architecture (planned, not built)

```text
Mobile / Admin
        ↓ REST /api/v1
Spring Boot 3.5 + JHipster (Java 21, JWT, Liquibase)
        ↓
PostgreSQL (self-hosted)
        ↓
MinIO/S3 (media), Firebase Admin SDK (push), OpenAI (AI)
```

See `07_SUPABASE_TO_SPRING_MATRIX.md` for component mapping.
