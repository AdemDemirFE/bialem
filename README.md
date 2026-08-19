# Bi'Alem

Topluluk, etkinlik ve yönetim paneli. Ürün arayüzü **Expo ve Supabase kullanmaz**; kendi Spring Boot backend'ine bağlanır.

## Proje Yapısı

- `mobile/`: Vite + React Native Web + Capacitor (web ve native)
- `admin/`: Next.js yönetim paneli
- `backend/`: JHipster / Spring Boot + PostgreSQL
- `shared/`: Ortak Spring API istemcisi
- `docs/`: ürün ve geçiş dokümanları

## Hızlı Başlangıç (Windows)

PostgreSQL `localhost:15432` (Docker) üzerinde `bialem` veritabanı. JDL şeması Liquibase ile uygulanır (`jhipster jdl` tekrar çalıştırılmaz):

```bat
cd backend
create-db.cmd
```

veya `bialem.bat` → **B**.

```bat
bialem.bat
```

| Menü | Ne yapar |
|------|----------|
| **1** | Kurulum |
| **2** | Backend + Admin + Mobil web |
| **3** | Sadece admin (`http://localhost:3000`) |
| **4** | Sadece mobil web (`http://localhost:5173`) |
| **5** | Capacitor canlı geliştirme |
| **6** | Android (Capacitor) |
| **7** | Temiz kurulum |
| **8** | Env düzenle |
| **9** | Durdur |

Manuel çalıştırma:

```text
cd backend && mvnw
cd mobile && npm install && npm run dev
cd admin && npm install && npm run dev
```

- Uygulama kullanıcısı: kayıt ol, sonra giriş yap.
- Admin: JHipster varsayılanı `admin` / `admin` (`ROLE_ADMIN`).
- Env: `mobile/.env` içinde `VITE_API_BASE_URL=http://localhost:8080`, `admin/.env.local` içinde `NEXT_PUBLIC_API_BASE_URL=http://localhost:8080`.
