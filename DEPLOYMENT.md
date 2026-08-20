# Bialem production deployment

This stack is **production only**. Local development stays on Spring `dev` + host Postgres `localhost:15432`. Do not mix the two.

Production does **not** use Expo or Supabase. The live path is Nginx → Vite web (`bialem.app`) and Spring Boot JWT API (`api.bialem.app`) → PostgreSQL (`bialem-db:5432`).

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
bash deploy/scripts/deploy.sh
```

(`./deploy.sh` also works if the file is executable; on a fresh clone use `bash` to avoid `Permission denied`.)

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
bash deploy/scripts/update.sh       # git pull --ff-only, rebuild, keep DB volume
bash deploy/scripts/status.sh
bash deploy/scripts/logs.sh backend
bash deploy/scripts/restart.sh
bash deploy/scripts/stop.sh         # does not delete volumes
bash deploy/scripts/backup-db.sh
bash deploy/scripts/restore-db.sh backups/bialem-YYYYMMDD-HHMMSS.sql.gz
```

Shorthand from repo root (same scripts):

```bash
bash update.sh
bash deploy.sh
```

`update.sh` stops if the git working tree is dirty. It never runs `git reset --hard`, `docker system prune`, or `docker compose down -v`.

## Troubleshooting: "Local changes detected"

On the VPS, running `chmod +x deploy/scripts/*.sh` marks many files as **modified** in git (file mode only). `git pull` then fails. Prefer `bash deploy/scripts/update.sh` instead of `./update.sh` — no chmod needed.

Reset tracked files to match GitHub (`.env.prod` is **not** touched):

```bash
cd /opt/bialem
git status
git diff --stat
git checkout -- .
git pull --ff-only
bash deploy/scripts/update.sh
```

Do **not** use `git reset --hard` unless you understand what it discards. Do **not** use `git clean -fd` (can remove untracked files).

### Permission denied on `./update.sh`

Scripts are not always executable after clone. Use bash:

```bash
bash update.sh
# or
bash deploy/scripts/update.sh
```

Do **not** run `chmod +x` on the VPS if you plan to `git pull` later (Git will show those files as modified).

If you edited a file on purpose, back it up first:

```bash
cp deploy/scripts/update.sh /root/update.sh.vps.bak
git checkout -- .
git pull --ff-only
./update.sh
```

## Local development (do not use deploy.sh)

```bash
cd backend && create-db.cmd   # or docker compose -f src/main/docker/postgresql.yml up -d
cd backend && ./mvnw          # profile dev, DB localhost:15432
cd mobile && npm run dev      # VITE_API_BASE_URL=http://localhost:8080
cd admin && npm run dev
```
