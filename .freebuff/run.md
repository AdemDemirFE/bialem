# Super Admin Preview Run Doc

## How to reproduce the artifacts

```bash
cd super-admin
npm install   # install dependencies
```

## How to run the server

```bash
cd super-admin
npx vite --port 5199 --host
```

The server proxies `/api` requests to `http://localhost:8080` (the Spring Boot backend).

## Prerequisites

- Backend running on port 8080 (`cd backend && ./mvnw -Pdev spring-boot:run`)
- Docker PostgreSQL on port 15432 (`cd backend && docker compose -f src/main/docker/services.yml up -d`)

## Login

- Username: `admin`
- Password: `admin`

## Backend CORS

Port 5199 is already in the allowed-origins list in `application-dev.yml`.
