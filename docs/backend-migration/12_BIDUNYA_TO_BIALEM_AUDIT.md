# Bialem → Bialem Audit

**Rule:** Do not rename in this phase. Document only.

Canonical product name: **Bialem**  
Canonical Android package: **`com.bialem.app`** (verified in `mobile/app.json`)

## Residual references (grep, excl. node_modules)

| Path | Current | Target | Risk | Rename phase |
|------|---------|--------|------|--------------|
| `mobile/google-services.json` | Firebase `project_id`: `bidunya-26b91` | New Firebase project `bialem-*` + new JSON | **HIGH** — FCM/push | Pre-prod push migration |
| `bialem.bat` L106 | Expo URL `.../projects/bidunya/builds` | `.../projects/bialem/builds` | **LOW** — stale link | Docs/script fix |
| `supabase/migrations/0040_award_founder_badge.sql` | Badge code `bidunya-kurucusu` | `bialem-kurucusu` (forward migration) | **MEDIUM** — DB data | Data migration |
| `supabase/migrations/0041_platform_team_identities.sql` | SQL refs `bidunya-kurucusu` | Update badge code in data | **MEDIUM** | Data migration |
| `supabase/functions/bidunya-assistant/` | Legacy folder name | Removed; use `bialem-assistant` | **LOW** if deleted | Cleanup |
| `mobile/plugins/withBidunyaAndroidTheme.js` | Internal color keys `bidunya_*` | Optional rename to `bialem_*` | **LOW** — cosmetic | Native theme refactor |
| `scripts/rebrand-to-bialem.mjs` | Mapping rules | Keep as migration tool | **NONE** | N/A |

## Already migrated

| Area | Status |
|------|--------|
| `mobile/app.json` name/slug/scheme | `Bi'Alem` / `bialem` / `com.bialem.app` |
| Deep links | `bialem://`, `bialem.app` |
| Edge function invoke | `bialem-assistant` |
| npm package names | `bialem`, `bialem-mobile`, `bialem-admin` |
| UI strings | BİALEM branding in mobile screens |
| Docs | `BIALEM_ADVANTAGE_TR.md`, etc. |

## Not found in repo (good)

- No `bidunya.app` in active config
- No spaced `bi alem` in source (except rebrand script)
- Mobile scheme is not `bidunya://`

## Migration impact on backend rename

| Concern | Action |
|---------|--------|
| Storage paths | User UUID folders — **no bidunya prefix** |
| Public URLs | Use `bialem.app` in Spring config |
| JWT issuer | New domain, not Supabase project ref |
| Package name | `com.bialem.backend` for JHipster |

## Firebase / Expo legacy

Even after UI rebrand, **push may still hit old Firebase project** until `google-services.json` and EAS FCM credentials are replaced.

See `11_EXPO_DEPENDENCY_INVENTORY.md` and `FIREBASE_PUSH_SETUP_TR.md`.
