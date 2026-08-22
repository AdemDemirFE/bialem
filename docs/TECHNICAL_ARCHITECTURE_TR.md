# Bialem Teknik Mimari ve Yetkilendirme

## Güncel sistem

Bialem tek kaynak kodundan web, Android ve iOS istemcileri üreten; kendi Spring Boot API'si ve PostgreSQL veritabanı üzerinde çalışan bir uygulamadır.

```text
Vite/React + Capacitor ─┐
                       ├─ HTTPS/JWT ─ Spring Boot ─ JPA/Liquibase ─ PostgreSQL
Next.js Admin ─────────┘                    │
                                           ├─ Firebase Cloud Messaging
                                           ├─ yerel/S3 uyumlu medya katmanı
                                           └─ OpenAI API
```

Supabase Auth, RLS, Storage, Edge Functions, Realtime ve Expo runtime güncel sistemin parçası değildir.

## İstemciler

### Mobil-web

- Vite ve React 19
- React Native Web uyumlu ortak componentler
- Android/iOS paketleme ve native yetenekler için Capacitor
- JWT oturumu ve `/api/app/**` uygulama endpointleri
- Push için Capacitor Push Notifications ve FCM

### Admin

- Next.js
- HTTP-only oturum cookie'si
- Spring JWT doğrulaması
- `ROLE_ADMIN` korumalı yönetim işlemleri

## Backend

- Spring Boot ve JHipster altyapısı
- Stateless Bearer JWT
- Spring Security method ve endpoint yetkilendirmesi
- JPA repository/service katmanı
- Liquibase ile ileri yönlü şema değişiklikleri
- RFC 7807/JHipster problem response'ları
- Springdoc OpenAPI ve Swagger UI

Frontendler iş akışları için `/api/app/**` kullanır. JHipster tarafından üretilmiş ham entity CRUD endpointleri yönetim yüzeyidir ve `ROLE_ADMIN` gerektirir.

## Yetkilendirme ilkeleri

- Guest yalnızca kayıt, aktivasyon, parola sıfırlama, public medya, health ve API dokümantasyonuna erişir.
- Member yalnızca kendisine ve görünür/üye olduğu kaynaklara ilişkin uygulama servislerini kullanır.
- Moderator yetkileri topluluk veya etkinlik kapsamıyla servis katmanında doğrulanır.
- Admin ham entity yönetimi ve `/api/admin/**` işlemlerine erişebilir.
- İstemciden gelen `userId`, sahiplik veya rol bilgisine güvenilmez; aktif JWT ve veritabanı ilişkileri esas alınır.
- Medya yolu, boyutu, MIME türü ve sahipliği backend tarafından doğrulanır.

## Veri modeli

Ana domainler profil, tercih, takip/engelleme, topluluk/üyelik, etkinlik/katılımcı/mesaj, gönderi/yorum/hikâye, değerlendirme, rapor, bildirim, şehir etkinliği ve avantaj modülleridir. Kaynak şema `backend/bialem.jdl`; uygulanan gerçek şema ise `backend/src/main/resources/config/liquibase/master.xml` ve bağlı changelog dosyalarıdır.

Şema değişikliği sırası:

1. Entity ve DTO değişikliği
2. Yeni Liquibase changelog
3. `master.xml` include kaydı
4. Repository/service/resource güncellemesi
5. Yetki ve entegrasyon testi

Production'da `ddl-auto` kullanılmaz; Liquibase tek şema otoritesidir.

## Ortamlar

- Local: `dev`, PostgreSQL `localhost:15432`, API `localhost:8080`
- Production: `prod`, Docker ağı içindeki `bialem-db:5432`, public API `https://api.bialem.app`
- Nginx forwarded headers Spring tarafından işlenir.
- CORS yalnızca yapılandırılmış frontend originlerine izin verir.

## Operasyon

- `/management/health` public probe'dur; diğer management endpointleri admin gerektirir.
- `/swagger-ui/index.html` ve `/v3/api-docs` local ve production ortamında açıktır.
- Production deploy kontrolü frontend, backend, PostgreSQL, Swagger UI ve OpenAPI JSON'u doğrular.
- Backup, restore ve deployment işlemlerinin güncel kaynağı `DEPLOYMENT.md` ve `docs/OPERATIONS_RUNBOOK_TR.md` dosyalarıdır.
