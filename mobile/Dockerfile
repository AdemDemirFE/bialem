# syntax=docker/dockerfile:1
# Production web image only (Vite + nginx). No Expo, no Supabase, no Capacitor native toolchain.
FROM node:22-alpine AS build
WORKDIR /app

COPY package.web.json ./package.json
COPY .npmrc ./
RUN npm install --legacy-peer-deps --no-audit --no-fund

COPY . .
ARG VITE_API_BASE_URL=https://api.bialem.app
ARG VITE_PUBLIC_WEB_URL=https://bialem.app
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL \
    VITE_PUBLIC_WEB_URL=$VITE_PUBLIC_WEB_URL
RUN npm run build

FROM nginx:1.27-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
HEALTHCHECK --interval=15s --timeout=5s --start-period=10s --retries=5 \
    CMD wget -qO- http://127.0.0.1/ >/dev/null || exit 1
