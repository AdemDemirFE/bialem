# Bialem — QA Tasks

## Project
Bialem: community, event, and admin platform.

## Goal
Comprehensive QA: discover, run, test, record issues, fix, verify.

## Task States
- TODO
- IN_PROGRESS
- DONE
- BLOCKED

## Bugs / Issues

### BUG-003
Status: TODO
Severity: MEDIUM
Feature: Profile
Screen: User profile
Route: /user/:id
Problem: User profile screen displays "Kayıt tarihi: Invalid Date" and conflicting status labels.
Expected: Valid registration date and consistent verification/status labels.
Actual: Invalid Date and conflicting status text.
Evidence: Snapshot of /user/1.
Frontend: mobile/app/user/[id].tsx
Backend: -
API: GET /api/profiles/1

### BUG-002
Status: TODO
Severity: HIGH
Feature: Mobile navigation
Screen: Tab screens
Route: /communities, /calendar, /management, /store
Problem: Direct URL navigation to mobile tab routes redirects to /feed.
Expected: Each tab route should render its own screen when accessed directly.
Actual: All tested tab routes redirect to /feed.
Evidence: Chrome DevTools navigation to /communities, /calendar, /management, /store all ended at /feed.
Frontend: mobile/app/(tabs)/_layout.tsx auth guard, mobile/src/lib/auth.tsx
Backend: -
API: -

### BUG-001
Status: TODO
Severity: HIGH
Feature: Store / Admin Orders
Screen: Admin Store Orders
Route: /admin/store/orders
Problem: Admin store orders list renders "Henüz siparebulunmuyor." even though backend /api/store/orders/admin/all returns 3 orders when called with a valid admin Bearer token.
Expected: The 3 existing orders should appear in the admin orders table.
Actual: Empty table.
Evidence: curl to /api/store/orders/admin/all with admin JWT returns 3 orders; UI shows none.
Frontend: admin/app/admin/store/orders/page.tsx uses getAdminApi().rest.get
Backend: StoreOrderResource.getAdminOrders returns data correctly
API: GET /api/store/orders/admin/all

### BUG-000 (Template)
Status: TODO
Severity: -
Feature: -
Screen: -
Route: -
Problem: -
Expected: -
Actual: -
Evidence: -
Frontend: -
Backend: -
API: -

## Test Tasks

- [ ] PHASE 0 — Project discovery
- [ ] PHASE 1 — Environment startup
- [ ] PHASE 2 — Route / screen smoke tests
- [ ] PHASE 3 — Form tests
- [ ] PHASE 4 — CRUD tests
- [ ] PHASE 5 — API tests
- [ ] PHASE 6 — Auth tests
- [ ] PHASE 7 — Responsive / UI tests
- [ ] PHASE 8 — E2E scenarios
- [ ] PHASE 9 — Bug fixes
- [ ] PHASE 10 — Regression tests
- [ ] PHASE 11 — Final QA report
