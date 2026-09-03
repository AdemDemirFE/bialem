# Bialem Super Admin

Bialem platformu için bağımsız, web tabanlı super admin yönetim paneli.

## Özellikler

- **JWT Kimlik Doğrulama**: Spring Boot backend ile entegre
- **SUPER_ADMIN Rol Kontrolü**: Sadece `ROLE_SUPER_ADMIN` yetkisine sahip kullanıcılar erişebilir
- **Gerçek API Bağlantısı**: Mock veri kullanmaz, tüm CRUD işlemleri backend üzerinden
- **Yönetim Sayfaları**: Kullanıcılar, Profiller, Etkinlikler, Topluluklar, Ürünler, Kategoriler, Markalar, Siparişler, Raporlar, Bildirimler, Şablonlar, Roller, Yorumlar

## Mimari

```
super-admin/
├── src/
│   ├── api.ts              # API client (JWT auth, pagination, entity types)
│   ├── context/
│   │   └── AuthContext.tsx  # Kimlik durumu + role guard
│   ├── components/
│   │   └── Layout.tsx       # Sidebar + main layout
│   ├── pages/
│   │   ├── LoginPage.tsx
│   │   ├── DashboardPage.tsx
│   │   ├── UsersPage.tsx
│   │   ├── ProfilesPage.tsx
│   │   ├── EventsPage.tsx
│   │   ├── CommunitiesPage.tsx
│   │   ├── ProductsPage.tsx
│   │   ├── CategoriesPage.tsx
│   │   ├── BrandsPage.tsx
│   │   ├── OrdersPage.tsx
│   │   ├── ReportsPage.tsx
│   │   ├── NotificationsPage.tsx
│   │   ├── TemplatesPage.tsx
│   │   ├── RolesPage.tsx
│   │   └── CommentsPage.tsx
│   ├── App.tsx              # Router + route guard
│   ├── main.tsx             # Entry point
│   └── styles.css           # Dark theme admin panel CSS
├── package.json
├── vite.config.ts           # Port 5199, proxy /api → localhost:8080
└── tsconfig.json
```

## Kurulum

```bash
cd super-admin
npm install
```

## Çalıştırma

Backend'in çalıştığından emin olun (`localhost:8080`), ardından:

```bash
npm run dev
```

Tarayıcıda açın: **http://localhost:5199**

## Production Build

```bash
npm run build
npm run preview
```

## Backend Bağımlılıkları

Super Admin, Spring Boot backend'in aşağıdaki endpoint'lerini kullanır:

| Endpoint | Yetki | Açıklama |
|---|---|---|
| `POST /api/authenticate` | Açık | JWT token alımı |
| `GET /api/account` | AUTH | Mevcut kullanıcı bilgisi |
| `GET /api/admin/context` | ADMIN | Rol/izin bilgisi |
| `GET /api/admin/dashboard` | ADMIN | Dashboard istatistikleri |
| `/api/admin/users/**` | ADMIN | Kullanıcı CRUD |
| `/api/profiles/**` | ADMIN | Profil listeleme/silme |
| `/api/events/**` | ADMIN | Etkinlik CRUD |
| `/api/communities/**` | ADMIN | Topluluk CRUD |
| `/api/store/admin/**` | ADMIN+ | Mağaza ürün/kategori/marka CRUD |
| `/api/store/orders/admin/**` | ADMIN+ | Sipariş yönetimi |
| `/api/admin/notifications/**` | ADMIN | Bildirim yönetimi |
| `/api/admin/notification-templates/**` | ADMIN | Şablon CRUD |
| `/api/roles/**` | ADMIN | Rol CRUD |
| `/api/authorities/**` | ADMIN | Yetki listesi |
| `/api/reports/**` | ADMIN | Rapor CRUD |
| `/api/comments/**` | ADMIN | Yorum moderasyonu |

## Yetki Modeli

```
ROLE_SUPER_ADMIN > ROLE_ADMIN > ROLE_USER
```

- Tüm admin endpoint'leri `ROLE_ADMIN` veya `ROLE_SUPER_ADMIN` gerektirir
- Super Admin paneli giriş sonrası `GET /api/admin/context` ile `superAdmin` durumunu kontrol eder
- `superAdmin: false` olan kullanıcılar erişemez

## Portlar

| Uygulama | Port |
|---|---|
| Backend (Spring Boot) | 8080 |
| Super Admin (Vite dev) | 5199 |
| Admin (Next.js) | 5173 |
| Mobile (Vite) | 5174 |
