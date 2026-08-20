# syntax=docker/dockerfile:1
# Production web image only (Vite + nginx).
# Expo/Supabase/Capacitor native toolchain NOT used.
#
# Important:
# mobile/tsconfig.json extends ../tsconfig.base.json,
# so the build context must be repo root (tsconfig.base.json must exist in /app).
FROM node:22-alpine AS build
WORKDIR /app

COPY tsconfig.base.json ./tsconfig.base.json
COPY mobile/ ./mobile/

# Override the mobile/package.json with a "web-only" dependency set.
COPY mobile/package.web.json ./mobile/package.json
COPY mobile/.npmrc ./mobile/.npmrc

WORKDIR /app/mobile

ARG VITE_API_BASE_URL=https://api.bialem.app
ARG VITE_PUBLIC_WEB_URL=https://bialem.app

ENV VITE_API_BASE_URL=$VITE_API_BASE_URL \
    VITE_PUBLIC_WEB_URL=$VITE_PUBLIC_WEB_URL

RUN npm install --legacy-peer-deps --no-audit --no-fund
RUN npm run build

FROM nginx:1.27-alpine
COPY --from=build /app/mobile/dist /usr/share/nginx/html
COPY mobile/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
HEALTHCHECK --interval=15s --timeout=5s --start-period=10s --retries=5 \
    CMD wget -qO- http://127.0.0.1/ >/dev/null || exit 1
