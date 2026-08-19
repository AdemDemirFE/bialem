# Bialem - Veritabanı Tasarımı

## 1. Temel Yaklaşım

Veritabanı tasarımı `Supabase PostgreSQL` üzerinde çalışacak şekilde planlandı. Amaç:

- topluluk ilişkilerini düzgün kurmak
- admin onaylı etkinlik akışını desteklemek
- kullanıcı yorum ve puanlarını kayıt altına almak
- moderasyon ve raporlama altyapısını baştan kurmak

## 2. Ana Tablolar

### `profiles`
Uygulamadaki kullanıcı profili.

Temel alanlar:
- `id`
- `email`
- `display_name`
- `username`
- `avatar_url`
- `bio`
- `city`
- `status`
- `is_verified`

### `roles`
Sistem rollerinin tanımı.

Örnek roller:
- `member`
- `organizer`
- `moderator`
- `admin`

### `user_roles`
Bir kullanıcıya bir veya birden fazla rol atanmasını sağlar.

### `communities`
Ana topluluklar ve alt alanlar.

Temel alanlar:
- `id`
- `parent_id`
- `name`
- `slug`
- `description`
- `visibility`
- `created_by`

### `community_members`
Kullanıcının hangi toplulukta hangi statü ile bulunduğunu tutar.

### `events`
Etkinlik ana tablosu.

Temel alanlar:
- `community_id`
- `created_by`
- `title`
- `description`
- `starts_at`
- `ends_at`
- `location_name`
- `latitude`
- `longitude`
- `status`
- `capacity`

### `event_participants`
Etkinlik katılım başvuruları ve durumları.

### `posts`
Topluluk veya etkinlik içi gönderiler.

### `post_media`
Gönderi fotoğraf/video kayıtları.

### `comments`
Gönderi veya etkinlik yorumları.

### `event_ratings`
Etkinlike verilen puan ve değerlendirme.

### `user_reviews`
Kullanıcının başka kullanıcıya verdiği yıldız ve yorum.

### `notifications`
Uygulama içi bildirimler.

### `reports`
Şikayetler ve moderasyon kayıtları.

### `blocks`
Kullanıcı engelleme ilişkileri.

## 3. Kritik Kurallar

### Etkinlik Puanlama Kuralı
- Sadece ilgili etkinliğe gerçekten katılan kişi puan verebilir
- Bir kullanıcı aynı etkinliğe bir kez puan verebilir

### Kullanıcı Yorumlama Kuralı
- Kullanıcıya verilecek yorum opsiyonel olarak etkinlik bazlı bağlanabilir
- İleride güven skoru üretimi için yıldız ortalamaları kullanılabilir

### Alt Topluluk Kuralı
- `communities.parent_id` doluysa kayıt alt alan kabul edilir

### Moderasyon Kuralı
- İçerik doğrudan silinmek yerine önce `hidden` veya `moderated` statüsüne alınabilir

## 4. İndeksleme Önerileri

İlk günden açılması gereken başlıca indeksler:

- `profiles(username)`
- `communities(slug)`
- `events(community_id, starts_at)`
- `events(status, starts_at)`
- `posts(community_id, created_at desc)`
- `comments(target_type, target_id)`
- `notifications(user_id, is_read)`
- `reports(status, created_at)`

## 5. RLS Temel Yaklaşımı

### Kullanıcı kendisini yönetebilir
- Kendi profilini okuyabilir/güncelleyebilir

### Herkes yalnızca görünür içeriği görür
- `public` topluluk verileri açık
- `private` topluluk için üyelik kontrolü gerekir

### Etkinlik oluşturma akışı kontrollü olur
- Kullanıcı kendi etkinlik kaydını oluşturabilir
- Ama `published` durumuna sadece admin çekebilir

### Moderasyon yetkileri ayrılır
- Moderatör ve admin dışı kullanıcı rapor sonuçlarını yönetemez

## 6. Faz 2 İçin Sonradan Eklenebilecek Tablolar

- `follows`
- `badges`
- `tickets`
- `payments`
- `chat_threads`
- `chat_messages`
- `event_checkins`

## 7. Topluluk ve Grup Hiyerarsisi

- `communities.parent_id` bos ise kayit ana topluluktur ve yalnizca admin panelinden olusturulur.
- `communities.parent_id` dolu ise kayit gruptur ve topluluga atanmis moderator tarafindan olusturulur.
- Normal uye once ana topluluga, sonra istedigi gruba katilir.
- Etkinlik yalnizca uye olunan grup icinde onerilir.
- Etkinlik, ilgili grup moderatoru onayladiktan sonra `published` olur.
