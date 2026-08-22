# Bialem Kurulum Rehberi

Bu doküman güncel mimari içindir. Uygulama Supabase veya Expo kullanmaz.

## Gereksinimler

- Java 17 veya 21
- Node.js LTS ve npm
- Docker Desktop (yerel PostgreSQL için)
- Android Studio ve/veya Xcode (Capacitor native build için)

## Mimari

- `backend/`: Spring Boot, Spring Security JWT, JPA, Liquibase ve PostgreSQL
- `mobile/`: Vite, React 19, React Native Web ve Capacitor
- `admin/`: Next.js yönetim paneli; Spring API kullanır
- `deploy/`: Docker Compose ve Nginx production yapılandırması

## Yerel ortam

Repo kökünde `.env.dev.example` dosyasını `.env.dev` olarak kopyalayın. Frontend istemcilerinde yalnızca backend adresi bulunur:

```env
VITE_API_BASE_URL=http://localhost:8080
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

Supabase URL/anahtarı veya Expo public değişkeni tanımlamayın.

Windows'ta bütün servisleri menüden başlatmak için `bialem.bat` çalıştırın. Manuel çalıştırma:

```powershell
cd backend
.\create-db.cmd
.\mvnw.cmd
```

```powershell
cd mobile
npm install
npm run dev
```

```powershell
cd admin
npm install
npm run dev
```

Yerel adresler:

- Mobil web: `http://localhost:5173`
- Admin: `http://localhost:3000`
- Backend: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Capacitor

Android production senkronizasyonu:

```powershell
cd mobile
npm run android:2:prod
npx cap open android
```

iOS production senkronizasyonu macOS üzerinde:

```bash
cd mobile
npm run ios:2:prod
npx cap open ios
```

## Production

Production yalnızca Spring `prod` profili, `deploy/docker-compose.prod.yml` ve `deploy/nginx/bialem.conf` ile çalıştırılır. Gizli değerler git dışındaki `.env.prod` dosyasında tutulur. Ayrıntılı kurulum ve güncelleme adımları için kökteki `DEPLOYMENT.md` dosyasını kullanın.

## Doğrulama

```powershell
cd backend
.\mvnw.cmd -Dmodernizer.skip=true -Dmaven.test.skip=true package

cd ..\mobile
npm run typecheck
npm run build
```

Production deploy betiği health, Swagger UI ve OpenAPI endpointlerini otomatik denetler.
