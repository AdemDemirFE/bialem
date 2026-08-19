# Bi Alem rebrand audit

Canonical product name: **Bi Alem**  
Identifiers: **`bialem`**, **`BIALEM`**, **`BİALEM`**  
Canonical Android package: **`com.bialem.app`** (verified in `mobile/app.json`)

## Brand mapping

| Old form | New form |
|----------|----------|
| bi dünya | bi alem |
| bidünya / bidunya | bialem |
| Bidünya / Bidunya | Bialem |
| BİDÜNYA / BIDUNYA | BİALEM / BIALEM |

## Status

| Area | Status |
|------|--------|
| `mobile/app.json` name/slug/scheme | `Bi Alem` / `bialem` / `com.bialem.app` |
| Deep links | `bialem://`, `bialem.app` |
| Edge function invoke | `bialem-assistant` |
| npm package names | `bialem`, `bialem-mobile`, `bialem-admin` |
| UI strings | BİALEM branding |
| Founder badge code | `bialem-kurucusu` |
| Docs | `BIALEM_ADVANTAGE_TR.md`, etc. |

## Notes

- Push still needs a real Firebase project whose `project_id` matches `mobile/google-services.json`.
- Public URLs use `bialem.app`.
- Backend package is `com.bialem.backend`.
- If `supabase/migrations/0032_bidunya_advantage.sql` is still on disk, rename it to `0032_bialem_advantage.sql` (`git mv`). Contents have no old brand strings.
