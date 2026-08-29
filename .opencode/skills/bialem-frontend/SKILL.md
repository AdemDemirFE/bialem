# Bialem Frontend

Frontend development rules for Bialem.

## Stack

- Mobile: Vite + React Native Web + Capacitor + Ionic-style patterns
- Admin: Next.js
- Shared API client: `shared/spring-client.ts` (copied into `mobile/` and `admin/`)

## Rules

- Follow existing component architecture in `mobile/` and `admin/`.
- Reuse existing API client and hooks.
- Preserve navigation, routing, and safe-area patterns.
- Use existing loading, error, and empty-state components.
- Do not introduce a new UI library unless explicitly required.
- Build mobile-first.

## Component Architecture

- Keep components small and focused.
- Reuse shared components from `mobile/src/components/` or `admin/src/components/`.
- Prefer function components and hooks.

## Routing

- Preserve mobile navigation hierarchy and back-button behavior.
- Use existing route definitions and route guards.
- Profile navigation must follow the existing routing system.

## API Services

- Use the Spring API client (`spring-client.ts`).
- Reuse existing query/mutation hooks.
- Centralize error handling.

## State Management

- Prefer local state and existing hooks.
- Avoid adding global state libraries unless required.

## Forms

- Reuse existing form components and validation patterns.
- Validate inputs before sending to backend.

## Loading / Error / Empty States

- Use existing patterns for loaders, error messages, and empty placeholders.
- Do not hide 401/403 errors with UI workarounds.

## Mobile Responsive & Safe Area

- Respect safe areas and platform differences (iOS/Android).
- Use platform-aware spacing and sizing.

## Reusable Components

- Before creating a new component, search for an existing one with `search_graph` or `grep`.
- Extend existing components rather than duplicating them.
