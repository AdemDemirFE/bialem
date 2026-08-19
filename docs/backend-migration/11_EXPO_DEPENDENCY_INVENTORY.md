# Expo Dependency Inventory

**Rule:** Do not change `package.json` or remove Expo in this phase.

Source: `mobile/package.json` + codebase grep.

## Summary

| Difficulty | Count | Examples |
|------------|-------|----------|
| HIGH | 4 | expo, expo-router, expo-notifications, expo-updates |
| MEDIUM | 6 | expo-camera, expo-location, expo-image-picker, expo-calendar, expo-constants, react-native-maps |
| LOW | 15+ | vector-icons, linking, sharing, async-storage, etc. |

Backend migration **does not require** removing Expo. Native build independence is a **separate** future track.

## Dependency table

| Expo / RN package | Used for | Key files | Bare RN alternative | Native config | Migration difficulty |
|-------------------|----------|-----------|---------------------|---------------|----------------------|
| `expo` | Core SDK | All | RN CLI + config plugins | app.json → native projects | HIGH |
| `expo-router` | File-based navigation | `mobile/app/**` | React Navigation | Route structure rewrite | HIGH |
| `expo-notifications` | Push registration, permissions | `notifications.ts`, `auth.tsx`, `_layout.tsx` | `@react-native-firebase/messaging` | FCM, APNs, channels | HIGH |
| `expo-updates` | OTA reload on theme change | `theme.tsx` | CodePush or remove | EAS Update channels | MEDIUM |
| `expo-camera` | QR check-in, advantage redeem | `check-in.tsx`, `redeem.tsx` | `react-native-vision-camera` | Camera permission | MEDIUM |
| `expo-location` | Event location picker | `organizer-request.tsx` | `@react-native-community/geolocation` | Location permission | MEDIUM |
| `expo-image-picker` | Avatars, covers, stories | `storage.ts` | `react-native-image-picker` | Photo library permission | MEDIUM |
| `expo-calendar` | Add event to calendar | `calendar.ts` | `react-native-calendar-events` | Calendar permission | MEDIUM |
| `expo-constants` | EAS projectId, env | `notifications.ts`, `supabase.ts` | `react-native-config` | Build-time env | MEDIUM |
| `expo-device` | Physical device check | `notifications.ts` | `react-native-device-info` | — | LOW |
| `expo-linking` | Deep links, reset password | `auth.tsx`, `reset-password.tsx` | RN `Linking` | URL schemes | LOW |
| `expo-sharing` | Share event poster | `poster.tsx` | RN Share API | — | LOW |
| `expo-splash-screen` | Splash | `app.json` plugin | Native splash | — | LOW |
| `expo-font` | Fonts | `app.json` plugin | Asset linking | — | LOW |
| `expo-status-bar` | Status bar | `_layout.tsx` | RN StatusBar | — | LOW |
| `expo-system-ui` | — | **Unused in code** | — | — | LOW |
| `@expo/vector-icons` | Icons | Many screens | `@expo/vector-icons` or vector-icons | — | LOW |
| `react-native-maps` | Event map | `EventLocationMap.native.tsx` | Same | Google Maps API key | MEDIUM |
| `react-native-qrcode-svg` | QR display | `event/[id].tsx`, advantages | Same | — | LOW |
| `react-native-view-shot` | Poster capture | `poster.tsx` | Same | — | LOW |
| `@supabase/supabase-js` | Backend | All data layers | **Replace with REST client** | API base URL | LOW (API swap) |

## Feature → Expo coupling

| Feature | Expo-specific? | Backend migration note |
|---------|----------------|------------------------|
| Push | Yes (Expo token + Expo Push API) | Target: FCM token → Spring → Firebase Admin |
| Camera / QR | Yes (expo-camera) | Unrelated to Supabase migration |
| Location | Yes | Unrelated |
| Calendar | Yes | Unrelated |
| Deep links | Partial (expo-linking) | Keep schemes `bialem://` |
| OTA updates | Yes (expo-updates) | Optional after self-hosted APK pipeline |
| Build | EAS | Can move to local `expo prebuild` + Gradle |

## Custom native plugins

| File | Purpose |
|------|---------|
| `plugins/withBialemAndroidTheme.js` | Android theme colors |
| `plugins/withBlockedAndroidPermissions` | Permission stripping |

## Recommendation for backend migration project

1. **Phase 1:** Swap `@supabase/supabase-js` for REST client; **keep Expo**.
2. **Phase 2:** Replace Expo Push with FCM-native tokens when Spring push is live.
3. **Phase 3 (optional):** Bare workflow / local Gradle if EAS dependency must go.

See `07_SUPABASE_TO_SPRING_MATRIX.md`.
