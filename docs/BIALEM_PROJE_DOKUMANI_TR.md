# Bialem — Tam Proje Dokümanı

> Amaç: Backend'in hedeflediği ürün kapsamını, veri modelini, API yüzeyini, iş akışlarını,
> istemci ekranlarını ve modernizasyon yol haritasını tek belgede toplamak.
> Kaynak: `backend/` (Spring Boot + JPA/Liquibase), `mobile/` (Vite + React 19 + Capacitor),
> `super-admin/` (Vite + React 18), `admin/` (Next.js 15), `docs/`.
> Oluşturulma: 2026-09-04. Kod değiştikçe bu belge güncellenmelidir.

## İçindekiler

1. [Ürün Vizyonu ve Amaç](#1-ürün-vizyonu-ve-amaç)
2. [Mimari Genel Bakış](#2-mimari-genel-bakış)
3. [Backend Domain Modeli](#3-backend-domain-modeli)
4. [API Yüzeyi](#4-api-yüzeyi)
5. [Yetkilendirme Modeli](#5-yetkilendirme-modeli)
6. [Kritik İş Akışları](#6-kritik-iş-akışları)
7. [Mobil Uygulama Ekran Haritası](#7-mobil-uygulama-ekran-haritası)
8. [Super-Admin (16 Sayfa)](#8-super-admin-16-sayfa)
9. [Admin Paneli (Next.js)](#9-admin-paneli-nextjs)
10. [Mevcut Tasarım Sistemi](#10-mevcut-tasarım-sistemi)
11. [Ortamlar, Deploy ve Operasyon](#11-ortamlar-deploy-ve-operasyon)
12. [Bilinen Riskler ve TODO'lar](#12-bilinen-riskler-ve-todolar)
13. [Arayüz Modernizasyon Yol Haritası](#13-arayüz-modernizasyon-yol-haritası)

---

## 1. Ürün Vizyonu ve Amaç

Bialem; **topluluk + etkinlik + sosyal akış + şehir keşfi + e-ticaret (store) + biletleme** işlevlerini
tek hesap, tek backend ve tek mobil kabuk altında birleştiren bir **yerel sosyal yaşam platformudur**.

Backend'in hedeflediği ürün amaçları:

| # | Amaç | Karşılığı |
|---|------|-----------|
| 1 | Kullanıcı kimliği ve profili | JWT auth, `profile`, hesap tercihleri |
| 2 | Topluluk kurma ve yönetme | `community`, üyelik, moderatör asistanları |
| 3 | Etkinlik oluşturma, keşfetme, katılma | `event`, katılımcı, mesaj, puanlama, bilet |
| 4 | Sosyal akış (gönderi, yorum, hikâye) | `post`, `comment`, `story` ailesi, takip/engelle |
| 5 | Şehirde ne oluyor sorusuna cevap | `city_event` senkronizasyonu + radar/keşif |
| 6 | Avantaj/partner ekosistemi | `partner_venue`, `partner_offer`, redemption akışı |
| 7 | Mağaza: ürün → sepet → sipariş → ödeme → kargo | `store_*` (20 tablo) + iyzico/mock ödeme |
| 8 | Biletli etkinlik satışı | `event_ticket` → `ticket_order` → `payment` → `ticket` |
| 9 | Bildirim altyapısı | template + outbox + FCM push + uygulama içi kutu |
| 10 | Güven ve moderasyon | rapor, blok, takip isteği, rol bazlı yönetim |
| 11 | Yapay zekâ destekli deneyim | `ai_usage_log`, `AppAiChat`, asistan butonu |

Supabase / Expo mimarisi **artık yoktur**; tek doğruluk kaynağı Spring Boot + PostgreSQL'dir
(bkz. `docs/TECHNICAL_ARCHITECTURE_TR.md`).

---

## 2. Mimari Genel Bakış

```text
┌──────────────────────── mobile/ ────────────────────────┐
│ Vite + React 19 + React Native Web  │  Capacitor (Android/iOS)  │
│ expo-router Stack + react-router web │  Push (FCM), SafeArea     │
└──────────────┬──────────────────────────────────────────┘
               │ HTTPS + Bearer JWT
┌──────────────▼──────────────────────────────────────────┐
│ super-admin/ (Vite+React 18)  │  admin/ (Next.js 15)     │
└──────────────┬────────────────────────┬─────────────────┘
               └────────────┬───────────┘
                    JWT / cookie-session
┌───────────────────▼─────────────────────────────────────┐
│ backend/ Spring Boot + JHipster                          │
│ Controller → Service → Repository (JPA) → PostgreSQL     │
│ Liquibase = tek şema otoritesi (ddl-auto prod'da kapalı) │
└───┬──────────┬──────────────┬───────────────────────────┘
    │          │              │
   FCM      OpenAI      Yerel/S3 uyumlu
  (push)   (asistan)     medya katmanı
```

**Katman kuralı:** Controller iş mantığı içermez → Service doğrular ve yönetir →
Repository + Liquibase şeması. Frontend ham tabloyu değil `/api/app/**` ve
`/api/store/**` servislerini tüketir. Ham JHipster CRUD (`/api/**`) yönetim yüzeyidir.

---

## 3. Backend Domain Modeli

Toplam ~75 entity → ~82 tablo (`docs/DATABASE_SCHEMA.md` gerçek şemadır).
Aşağıdaki tablo **mevcut kodun gerektirdiği** nihai durumdur.

### 3.1 Kimlik ve profil

| Entity | Tablo | Not |
|---|---|---|
| User | `jhi_user` | login unique, JWT'nin öznesi |
| Authority | `jhi_authority` | rol adları (`ROLE_*`) |
| Role / UserRole | `app_role`, `user_role` | uygulama seviyesi rol atama |
| Profile | `profile` | görünen kimlik (`user_id → jhi_user`) |
| AccountPreferences | `account_preferences` | keşfedilebilirlik, bildirim tercihleri |
| PushToken / PushDeviceToken | `push_token`, `push_device_token` | FCM cihaz eşleşmesi |
| UserNotificationPreference | `user_notification_preference` | kanal tercihleri |
| PlatformTeamMember | `platform_team_member` | platform ekibi üyeliği |

### 3.2 Sosyal grafik

| Entity | Tablo | Not |
|---|---|---|
| Follow / FollowRequest | `follow`, `follow_request` | onaylı takip akışı |
| Block | `block` | engelleme |
| UserReview | `user_review` | kullanıcı değerlendirmesi |
| Report | `report` | şikâyet + çözümleme |

### 3.3 Topluluk

| Entity | Tablo | Not |
|---|---|---|
| Community | `community` | slug unique, görünürlük: PUBLIC/PRIVATE/HIDDEN |
| CommunityMember | `community_member` | unique(community, user) |
| CommunityModeratorAssistant | `community_moderator_assistant` | kapsamlı mod yetkisi |

### 3.4 Etkinlik ve biletleme

| Entity | Tablo | Not |
|---|---|---|
| Event | `event` | moderasyon statüleri, keşif yayını |
| EventParticipant | `event_participant` | katılım durumu |
| EventMessage | `event_message` | etkinlik sohbeti |
| EventRating | `event_rating` | puan + yorum |
| EventTicket | `event_ticket` | bilet tipi, kontenjan |
| Order (ticket) | `ticket_order` | `order` SQL rezerve sözcüğü olduğu için bu ad |
| OrderItem | `order_item` | sipariş ↔ bilet eşleşmesi |
| Payment (ticket) | `payment` | unique idempotency_key |
| Ticket | `ticket` | unique ticket_code, giriş bileti |

### 3.5 İçerik: gönderi, yorum, hikâye

| Entity | Tablo | Not |
|---|---|---|
| Post / PostMedia | `post`, `post_media` | topluluk/etkinlik bağlantılı |
| Comment | `comment` | hedef tip + hedef id ile polimorfik |
| Story (+ Element, Group, Hashtag, Reaction, View, CommunityTarget) | `story`, `story_element`, `story_group`, `story_hashtag`, `story_reaction`, `story_view`, `story_community_target` | `story_reaction` unique(story, user); `story_element.metadata_json` = text (LONG32VARCHAR) |
| Hashtag | `hashtag` | unique normalized_name |

### 3.6 Şehir etkinlikleri (otomatik senkron)

| Entity | Tablo | Not |
|---|---|---|
| CityEvent | `city_event` | harici sağlayıcıdan toplanır |
| CityEventInterest | `city_event_interest` | "birlikte gitme" ilgisi |
| CityEventTicketOffer | `city_event_ticket_offer` | bilet teklifleri |
| CityEventSyncLog | `city_event_sync_log` | senkron koşu kayıtları |

### 3.7 Partner / avantaj

| Entity | Tablo | Not |
|---|---|---|
| PartnerVenue / PartnerVenueStaff | `partner_venue`, `partner_venue_staff` | mekân + personel |
| PartnerOffer / PartnerOfferRedemption | `partner_offer`, `partner_offer_redemption` | kampanya + token ile kullanım |
| HonorBadge / UserHonorBadge | `honor_badge`, `user_honor_badge` | rozet kazanımı |

### 3.8 Bildirim altyapısı

| Entity | Tablo | Not |
|---|---|---|
| NotificationTemplate | `notification_template` | başlık/gövde/route şablonları |
| AppNotification / Notification | `app_notification`, `notification` | kuyruk + kullanıcı kutusu |
| NotificationOutbox | `notification_outbox` | zamanlanmış gönderim, retry sayacı |
| NotificationDeliveryLog | `notification_delivery_log` | sağlayıcı bazlı teslim kaydı |

### 3.9 Mağaza (store — 20 tablo, `store_` önekli)

Katalog: `store_category` (ağaç), `store_brand`, `store_product`, `store_product_variant`,
`store_product_image`, `store_product_attribute`, `store_coupon`.
Sepet/sipariş: `store_cart_item`, `store_address`, `store_order`, `store_order_item`,
`store_order_status_history`, `store_shipping`, `store_wishlist` (unique user+product).
Ödeme: `store_payment` (unique idempotency_key), `store_payment_transaction`,
`store_payment_webhook`, `store_payment_refund`, `store_bank_transfer`.
Değerlendirme: `store_review`, `store_review_image`.

### 3.10 Diğer

`ai_usage_log` (AI kullanım kotası), `direct_conversation` + `direct_message` (birebir mesajlaşma),
`image` (merkezi görsel kaydı: `source_type` UPLOAD/URL, checksum tekilleme, `/api/app/images`
üzerinden kayıt + `/content` yönlendirmesi; gösterim her zaman `displayUrl` ile yapılır).

> Not: Dev veritabanındaki `audit_log`, `media_asset`, `promotion`, `jhi_date_time_wrapper`
> tabloları artık kodda referanslanmayan legacy/test kalıntılarıdır; yeni kurulumlarda oluşmaz.

---

## 4. API Yüzeyi

### 4.1 Uygulama API'si (üye girişi yeterli)

| Base path | Sorumlu controller | Kapsam |
|---|---|---|
| `/api/authenticate`, `/api/register`, `/api/activate`, `/api/account`, reset-password | `AuthenticateController`, `AccountResource` | kayıt, giriş, aktivasyon, parola |
| `/api/app` | `AppFacadeResource` | `/me`, `/query`, `/rpc/{name}`, `/ai/chat`, medya upload/silme/proxy |
| `/api/app/calendar` | `AppCalendarResource` | tarih aralıklı takvim |
| `/api/app/notifications` | `AppNotificationResource` | bildirim kutusu |
| `/api/app/messages` | `DirectMessagingResource` | birebir mesajlaşma |
| `/api/store`, `/api/store/cart`, `/api/store/checkout`, `/api/store/orders`, `/api/store/payments`, `/api/store/addresses`, `/api/store/wishlist`, `/api/store/products/{id}/reviews` | `Store*Resource` (10 controller) | katalog, sepet, sipariş, ödeme, adres, favori, yorum, kargo |

### 4.2 Yönetim API'si (ADMIN / SUPER_ADMIN)

`/api/admin` (`ManagementResource`, `AdminNotificationResource`, `AdminCommunityMemberResource`,
`UserResource`, `NotificationTemplateResource`) + ham entity CRUD (`/api/communities`,
`/api/events`, `/api/stories`, …) yönetim yüzeyidir.

### 4.3 Sözleşme kuralları

- REST, `/api/` önekli, çoğul isim, kebab-case; hata gövdesi RFC 7807 / problem-detail.
- Sayfalama + filtre her liste endpoint'inde beklenir; DTO + Bean Validation zorunlu.
- İstemciden gelen `userId`/rol bilgisine güvenilmez; JWT + DB ilişkisi esastır.

---

## 5. Yetkilendirme Modeli

- **JWT:** HS256 (Nimbus), `base64-secret` en az 32 bayt, eksikse fail-fast. Stateless Bearer.
- **Roller** (`AuthoritiesConstants`): `ROLE_SUPER_ADMIN` > `ROLE_ADMIN` > `ROLE_USER`;
  kapsam rolleri: `ROLE_COMMUNITY_MANAGER`, `ROLE_EVENT_MANAGER`, `ROLE_MODERATOR`, `ROLE_ANONYMOUS`.
- **URL matrisi** (`SecurityConfiguration`):
  - Public: kayıt/aktivasyon/parola sıfırlama, public medya, swagger, `/management/health-info`.
  - Authenticated: `/api/app/**`, `/api/store/**`, takip/üyelik/push-token/hesap.
  - ADMIN/SUPER_ADMIN: `/api/admin/**`, ham `/api/**` CRUD, management endpointleri.

---

## 6. Kritik İş Akışları

### 6.1 Üyelik
Kayıt → aktivasyon → JWT (`/authenticate`, rememberMe) → `profile` otomatik provizyon
(`ProfileProvisioning`) → tercih varsayılanları.

### 6.2 Mağaza siparişi
Sepet (`StoreCartService`) → özet (`getCheckoutSummary`) → `checkout()` (sipariş + ödeme kaydı,
idempotency_key) → ödeme başlatma (`/initiate`) → callback/webhook ile statü →
admin akışı: approve → preparing → ready-for-shipping → kargo (`StoreShippingService`) →
statü geçmişi (`store_order_status_history`). İade: `StoreAdminPaymentResource /refund`.
Havale/EFT: `store_bank_transfer` + onay/red.

### 6.3 Ödeme sağlayıcı
`PaymentProviderFactory` → iyzico (sandbox varsayılan, credentials yoksa mock fallback) /
mock. Bilet tarafı: `PaymentService.initiate/handleCallback`. **İmza doğrulama (iyzico
`x-iyzico-signature`) ve gerçek 3DS çağrıları TODO durumundadır.**

### 6.4 Bildirim
Olay → template çözümleme → `notification_outbox` (zamanlama + retry) →
`NotificationOutboxScheduler` polling → FCM (`FirebasePushService`) + uygulama içi kutu →
`notification_delivery_log`. Credential yoksa push sessizce devre dışı kalır.

### 6.5 Biletli etkinlik
`event_ticket` (kontenjan) → `ticket_order` + `order_item` → `payment` →
onayda `ticket` (ticket_code) üretimi → `my-tickets` ekranı + QR.

### 6.6 Moderasyon
Rapor (`report`) → inceleme → içerik statüsü / blok / rol işlemi. Topluluk/etkinlik
kapsamlı yetkiler servis katmanında doğrulanır (`CommunityAuthorization`,
`ManagementAuthorization`).

### 6.7 AI asistan
`/api/app/ai/chat` → `AppAiChat` → OpenAI; her çağrı `ai_usage_log`'a yazılır (kota/izleme).

---

## 7. Mobil Uygulama Ekran Haritası

`mobile/app` (~82 route). Kabuk: `app/_layout.tsx` (Theme + Auth + expo-router Stack),
sekmeler: **Keşfet (feed) · Mağaza (store) · Topluluk (communities) · Takvim (calendar) ·
Profil / Yönetim** (yetkiye göre biri gizlenir). Web tarafı `mobile/src/App.tsx`
(BrowserRouter + ~60 stack route) ile birebir eşleşir.

| Alan | Ekranlar |
|---|---|
| Giriş/onboarding | `index`, `account`, `settings`, `forgot-password`, `reset-password` |
| Akış | `(tabs)/feed`, `post/[id]`, `story/[id]`, `story/create` |
| Topluluk | `(tabs)/communities`, `community/[id]` (+members, +assistants), `group/[id]` |
| Etkinlik | `event/[id]` (+poster, +chat, +check-in), `event/tickets/[id]`, `event-share/[id]`, `city-event/[id]`, `city-radar`, `my-plans` |
| Mesaj | `messages`, `messages/[id]` |
| Kişiler | `people`, `people/requests`, `people/connections`, `user/[id]`, `profile/edit`, `blocked-users` |
| Mağaza | `(tabs)/store`, `store/search`, `store/product/[slug]`, `store/category/[slug]`, `store/cart`, `store/checkout`, `store/payment`, `store/addresses`, `store/orders`, `store/orders/[id]` |
| Ödeme sonucu | `payment/success`, `payment/failure`, `payment/pending`, `payment/callback` |
| Bilet/sipariş | `my-tickets`, `ticket/[id]`, `order/[id]` |
| Avantaj | `advantages`, `advantages/[id]`, `advantages/redeem`, `organizer-request` |
| Bildirim | `notifications` (tabs dışı), `notification-settings` |
| Hukuk | `legal/[document]` (gizlilik, KVKK, şartlar) |
| Yönetim (mobil) | `management/users`, `management/communities`, `management/events`, `management/notifications`, `management/roles`, `management/moderation`, `management/data`, `management/store/*` |

Paylaşılan yapı: `mobile/src/components` (24; `ui/AppButton`, `ui/AppHeader`,
`ui/FeedbackState`, `SkeletonList`, `ImageViewerModal` …), `mobile/src/lib`
(api, auth, store-api, messaging, notification, permissions, router …),
`mobile/src/experiences` (hero/maskot sahne motoru + `FloatingAssistantButton`).

---

## 8. Super-Admin (16 Sayfa)

Vite + React 18 + React Router (`super-admin/src`). `api.ts` + `AuthContext` ile JWT'li yönetim.

| Sayfa | Amaç |
|---|---|
| Dashboard | kullanıcı/profil/etkinlik/sipariş/gelir özet kartları |
| Users | admin kullanıcı listesi + oluştur/düzenle modalı |
| Roles | rol + authority eşleşmesiyle rol oluşturma |
| Profiles | kullanıcı profilleri + arama |
| Communities | topluluk listesi (PUBLIC/PRIVATE/HIDDEN rozeti) + silme |
| Events | etkinlik CRUD + statü/moderasyon rozetleri |
| Comments | yorum listesi + moderasyon takibi |
| Reports | raporları statüden geçirip çözümleme/silme |
| Products | ürün listesi + detay çekmecesi + ürün formu |
| Categories | kategori ağacı genişlet/daralt + düzenleme/aktiflik |
| Brands | marka listesi + ad/slug zorunlu formu (**açık olan dosya**) |
| Orders | çok statülü sipariş listesi + detay ve statü yönetimi |
| Shipments | kargo firması/takip no atama + gönderi statüleri |
| Notifications | gönderimler + SENT/SKIPPED/FAILED filtresi |
| Templates | bildirim şablon CRUD |
| Login | username/password giriş formu |

---

## 9. Admin Paneli (Next.js)

`admin/` — Next.js 15 App Router (18 sayfa): genel `page/layout/globals.css`, hukuk
(`privacy`, `terms`, `kvkk`, `community-guidelines`, `account-deletion`),
`reset-password`, `event-share/[id]`, `api/session`, `api/health`, `middleware.ts`,
`src/lib` (spring-client, admin-api, admin-auth, permissions); panel:
`admin/page` + `login`, `mfa`, `unauthorized`, `team`, `tickets`, `advantages`,
`store`, `store/orders`, `store/orders/[id]`.

---

## 10. Mevcut Tasarım Sistemi

- **Renk** (`mobile/src/theme/colors.ts`): light/dark iki palet
  (page/surface/ink/muted/accent/accentSoft/aqua/border/action/success/warning/danger);
  web'de `--bialem-*` CSS değişkenleri, iOS'ta DynamicColorIOS; sabit marka renkleri.
- **Token** (`mobile/src/theme/tokens.ts`): spacing (xxs→section), radius, tipografi
  (display→caption), boyutlar (touch/avatar…), **motion (160/220/260 ms)**, katmanlar.
- **Tema** (`theme.tsx` + `global.css`): light/dark/system, AsyncStorage tercihi, Inter fontu,
  `--ui-duration/ease/shadow`, safe-area, focus halkaları.
- **Animasyon bugünü:** özel `animations/` klasörü **yok**; hareket `tokens.motion` +
  `global.css` + `experiences/` sahne motoruna dağılmış durumda → modernizasyonun ilk işi
  bunu tek altyapıda toplamaktır (bkz. §13).

---

## 11. Ortamlar, Deploy ve Operasyon

| Ortam | API | DB | Profil |
|---|---|---|---|
| dev | `localhost:8080` | `localhost:15432/bialem` (container `bialem-postgresql-1`) | `dev` |
| android-test | `191.215.36.29:8080` | aynı VPS | test |
| prod | `https://api.bialem.app` | `bialem-db:5432` (compose ağı, host portu yok) | `prod` |

- Deploy kaynağı: `deploy/` (`docker-compose.prod.yml`, `scripts/deploy.sh`, backup/restore,
  health-check) + `DEPLOYMENT.md` + `docs/OPERATIONS_RUNBOOK_TR.md`.
- Secret'lar `.env.prod` (VPS'te, repoda yok); örnek: `.env.prod.example`.
- Backend prod'da `SPRING_PROFILES_ACTIVE=prod` ile çalışır; Liquibase startup'ta eksik
  migrationları otomatik uygular; `DATABASECHANGELOG`/`LOCK` ile izlenir.
- Health: `/management/health` public probe; diğer management endpointleri admin ister.
- Medya: yerel/S3 uyumlu katman; yol/boyut/MIME/sahiplik backend'de doğrulanır.

---

## 12. Bilinen Riskler ve TODO'lar

1. **Ödeme sağlayıcılar stub ağırlıklı:** iyzico/Stripe gerçek API + 3DS + webhook imza
   doğrulaması tamamlanmadan gerçek tahsilat açılmamalı.
2. **FCM credential yoksa push sessizce kapalı:** prod `.env`'de `FIREBASE_*` zorunlu kontrolü yok.
3. **Legacy tablo kalıntıları:** `audit_log`, `media_asset`, `promotion` kodda yok; yeni
   kurulumlarda oluşmaz. Mevcut DB'lerden DROP ile temizlenmemeli (veri kaybı riski).
4. **`store_module_recreate_schema` (dropTable) prod chain'den çıkarıldı** (2026-09-04);
   dev'de kayıtlı olduğu için checksum sorunu yaratmaz.
5. **Arayüz borcu:** ortak animasyon altyapısı yok, liste ekranlarında skeleton/empty/error
   kullanımı dağınık, mobil yönetim ekranları ile super-admin arasında çift bakım maliyeti.

---

## 13. Arayüz Modernizasyon Yol Haritası

Kullanıcı onayı alındı: **önce tasarım sistemi + animasyon altyapısı, sonra 2-3 pilot ekran,
kalan ekranlar partiler halinde.** Yeni bağımlılık kararı serbest bırakıldı
(tercih: önce CSS-only, gerekirse hafif lib).

### Adım 0 — Kılavuz (bu belge + §10 envanteri)
- [x] Ekran envanteri (§7–§9), tema envanteri (§10)

### Adım 1 — Tasarım sistemi çekirdeği (`mobile/src/theme` + yeni `mobile/src/animations`)
- [x] `motion` tokenlarını genişlet (`slow:320`, `stagger:45`; reduced-motion `global.css`'te mevcut)
- [x] Ortak bileşenler: `Reveal` (kademeli giriş), `Skeleton` (nabız iskelet), `usePressAnimation`
      (basma yaylanması), `usePrefersReducedMotion` — `mobile/src/animations/` (CSS-only + RN Animated,
      yeni bağımlılık yok, typecheck temiz)
- [ ] `FeedbackState`/`SkeletonList` birleştirme, `EmptyState`/`ErrorState` standardı, `BottomSheet` geçişi
- [ ] Düşük donanım kısayolu (ağır gölge/blur kapatma)

### Adım 2 — Pilot ekranlar (2-3)
- [x] `store` katalog (`mobile/app/store/index.tsx`): `Reveal` ile kademeli giriş (arama → kategoriler
      → 4 bölüm), `usePressAnimation` ile kart basma geri bildirimi, yükleme hatasında `FeedbackState`
      + tekrar dene (typecheck temiz)
- [x] Sekme paketi: `feed` (blok girişleri + liste stagger + kart basma), `communities` (hero/sekmeler
      + kart stagger + `FeedbackState` hata + retry), `profile` (8 blok kademeli giriş), `management`
      (hero + menü girişi), `assistant` (hero + öneri stagger + mesaj girişleri); `calendar` mevcut
      motion sistemiyle yeterli bulundu, `cart` store akış partisine bırakıldı (typecheck temiz)
- [ ] `BrandsPage` (super-admin; tablo → modern veri tablosu + çekmece formu) — açık dosya olduğu için aday

### Adım 3 — Partiler (sırayla, her parti build + typecheck ile kapanır)
1. Mağaza akışı (search → product → cart → checkout → payment → orders)
2. Sosyal akış (feed → post → story → people → messages → notifications)
3. Etkinlik/şehir (communities → event → city-radar → calendar → my-plans/my-tickets)
4. Yönetim (mobil management + super-admin kalan 15 sayfa + Next.js admin)

Her parti sonunda: `npm run typecheck:mobile`, ilgili `vite build`, ekran görüntüsü notu.
Commit/push kullanıcı tarafından sabah yapılır; ajan commit atmaz.

### İlerleme günlüğü (loop)
- Tur 1: `store` katalog + animasyon altyapısı (`mobile/src/animations`)
- Tur 2: sekmeler — `feed`, `communities`, `profile`, `management`, `assistant` (`calendar` yeterli bulundu)
- Tur 3: mağaza akışı — `search`, `product/[slug]` (sonsuz-spinner hatası düzeltildi + iskelet),
  `CartScreenContent` (iskelet + stagger), `checkout`, `payment`, `orders`, `orders/[id]`
  (sonsuz-spinner hatası düzeltildi), `category/[slug]`, `addresses` — tamamı typecheck temiz
- Tur 5: etkinlik/şehir — `event/[id]` (6 blok giriş + iskelet + retry), `event` chat (başlık girişi),
  check-in (hero/panel/roster stagger + QR tara basma), `event/tickets` (iskelet + stagger + adet
  butonları), `city-radar` (hero/filtre/sonuç girişi + kart basma + `FeedbackState`), `my-tickets`
  (iskelet + stagger + `FeedbackState`), `advantages` (hero/personal/filtre girişi + kart basma +
  iskelet + `FeedbackState`), `advantages/[id]` (iskelet + giriş + QR butonu), `redeem`
  (izin/sonuç butonları); `my-plans`, `poster` mevcut sistemleriyle yeterli bulundu
- Tur 4: sosyal parti — `post/[id]` (iskelet + stagger + JSX düzeltmesi), `story/[id]`
  (reaksiyon/ikon basma), `people` (arama/sonuç stagger + `FeedbackState`), `people/requests`
  (onay/red basma + iskelet), `people/connections` (sekme basma + stagger), `messages`
  (liste stagger + basma), `messages/[id]` (başlık girişi), `(tabs)/notifications`
  (kart stagger + basma + `FeedbackState`); `story/create` zengin editör, dokunulmadı
- Tur 6: kalan mobil — auth (`index`, `forgot/reset-password`), `account`, `settings`, `legal`,
  `profile/edit`, `notification-settings`, `blocked-users`, `user/[id]`, `community/[id]` (+members,
  +assistants), `group/[id]`, `order/[id]`, `ticket/[id]`, `payment/*`, `city-event/[id]`,
  `event-share/[id]`, `organizer-request`, mobil `management/*` (liste/form ortak bileşenler dahil)
- Tur 7: super-admin — `styles.css` motion katmanı (sayfa/satır/modal girişleri, shimmer iskelet,
  buton basma, focus halkaları, responsive, reduced-motion), ortak `Feedback` bileşenleri
  (Alert/EmptyState/TableSkeleton), `BrandsPage` pilotu (arama filtresi + iskelet + rozet),
  kalan 14 sayfaya standart tedavi — typecheck + build PASS
- Tur 8: admin paneli global motion katmanı + MCP test fazı — mobile/admin/super-admin
  typecheck + build PASS; tarayıcı smoke: super-admin login + guard, mobil landing/auth/guard,
  forgot-password validasyonu, legal, event-share hata yolu, payment sonuçları, 5 eksik web
  rotası bulunup eklendi (`my-tickets`, `order/:id`, `ticket/:id`, `payment/callback`,
  `event/tickets/:id`), admin landing PASS
- Tur 9: super-admin son parçalar — `LoginPage` (Alert + giriş animasyonu + gölge, ekran görüntüsü
  doğrulandı), Products/Events/Orders/Shipments detay çekmeceleri iskelet — typecheck + build PASS
