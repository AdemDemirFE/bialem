# Bialem production deployment

This stack is **production only**. Local development stays on Spring `dev` + host Postgres `localhost:15432`. Do not mix the two.

## Environment split

| | Development | Production |
|---|---|---|
| Env file | `.env.dev.example` → `mobile/.env`, `admin/.env.local` | `.env.prod.example` → `.env.prod` |
| Spring profile | `dev` (`application-dev.yml`) | `prod` (`application-prod.yml`) |
| Database | Docker `postgresql.yml` on **127.0.0.1:15432** | Container `bialem-db:5432` (**no host port**) |
| API URL | `http://localhost:8080` | `https://api.bialem.app` |
| Compose | `backend/src/main/docker/*` (local) | `deploy/docker-compose.prod.yml` (`-p bialem`) |
| JWT / DB password | local defaults | only in `.env.prod` (gitignored) |

`.env.prod` must contain `BIALEM_ENV=prod`. Deploy scripts refuse files that still point at `15432` or `localhost:8080`.

Admin Next.js is not in this Compose file. This frontend is the Vite web app in `mobile/`.

Supabase is not used in production. Do not import `supabase/migrations`.

## First install on Ubuntu VPS

Other Docker projects on the same host are left alone. Only names starting with `bialem` are used: `bialem-backend`, `bialem-frontend`, `bialem-db`, `bialem-network`, `bialem-postgres-data`.

```bash
git clone <repo-url> /opt/bialem
cd /opt/bialem

cp .env.prod.example .env.prod
nano .env.prod
```

Set at least:

```bash
openssl rand -base64 24   # POSTGRES_PASSWORD — same value for SPRING_*_PASSWORD
openssl rand -base64 64   # JWT_SECRET and JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET
```

```bash
chmod +x deploy.sh update.sh deploy/scripts/*.sh
./deploy.sh
```

Host bindings (loopback only):

- Frontend: `127.0.0.1:4174`
- Backend: `127.0.0.1:8184` → container `8080`
- PostgreSQL: not published

## Nginx (host)

```bash
sudo cp deploy/nginx/bialem.conf /etc/nginx/sites-available/bialem.conf
sudo ln -sf /etc/nginx/sites-available/bialem.conf /etc/nginx/sites-enabled/bialem.conf
sudo nginx -t
sudo systemctl reload nginx
```

HTTP works before certificates. After DNS for `bialem.app`, `www.bialem.app`, and `api.bialem.app`:

```bash
sudo certbot --nginx \
  -d bialem.app \
  -d www.bialem.app \
  -d api.bialem.app
```

## Day-to-day

```bash
./update.sh                      # git pull --ff-only, rebuild, keep DB volume
./deploy/scripts/status.sh
./deploy/scripts/logs.sh backend
./deploy/scripts/restart.sh
./deploy/scripts/stop.sh         # does not delete volumes
./deploy/scripts/backup-db.sh
./deploy/scripts/restore-db.sh backups/bialem-YYYYMMDD-HHMMSS.sql.gz
```

`update.sh` stops if the git working tree is dirty. It never runs `git reset --hard`, `docker system prune`, or `docker compose down -v`.

## Local development (do not use deploy.sh)

```bash
cd backend && create-db.cmd   # or docker compose -f src/main/docker/postgresql.yml up -d
cd backend && ./mvnw          # profile dev, DB localhost:15432
cd mobile && npm run dev      # VITE_API_BASE_URL=http://localhost:8080
cd admin && npm run dev
```
