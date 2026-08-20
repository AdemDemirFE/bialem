# syntax=docker/dockerfile:1
# Production web image only (Vite + nginx).
# Expo/Supabase/Capacitor native toolchain NOT used.
#
# Important:
# mobile/tsconfig.json extends ../tsconfig.base.json,
# so the build context must be repo root (tsconfig.base.json must exist in /app).
FROM node:22-alpine AS build
WORKDIR /app

# Vite build sırasında hata aldığın durumda tsconfig.json "/app/tsconfig.json" bekleniyor.
# Bu yüzden mobile/ içeriğini /app'e alıyoruz:
#   /app/tsconfig.json -> extends ../tsconfig.base.json -> /tsconfig.base.json
COPY mobile/ ./

# Keep web-only dependency set.
COPY mobile/package.web.json ./package.json
COPY mobile/.npmrc ./.npmrc

# Root env/version source used by vite.config.ts → scripts/bialem-env.mjs
COPY package.json /package.json
COPY scripts/bialem-env.mjs /scripts/bialem-env.mjs

# Ensure the extended base tsconfig resolves from "/app/tsconfig.json".
COPY tsconfig.base.json /tsconfig.base.json

# Build-time guard: fail fast if tsconfig.base.json isn't where tsconfig.json expects it.
RUN test -f /tsconfig.base.json \
  && test -f /app/tsconfig.json \
  && test -f /package.json \
  && test -f /scripts/bialem-env.mjs \
  && echo "tsconfig + bialem-env resolution OK"

# Optional overrides (defaults come from /package.json bialem.environments.prod)
ARG VITE_API_BASE_URL=
ARG VITE_PUBLIC_WEB_URL=

ENV VITE_API_BASE_URL=$VITE_API_BASE_URL \
    VITE_PUBLIC_WEB_URL=$VITE_PUBLIC_WEB_URL

RUN npm install --legacy-peer-deps --no-audit --no-fund
RUN npm run build

FROM nginx:1.27-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY --from=build /app/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
HEALTHCHECK --interval=15s --timeout=5s --start-period=10s --retries=5 \
    CMD wget -qO- http://127.0.0.1/ >/dev/null || exit 1
