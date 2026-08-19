# Bialem - Teknik Mimari ve Yetkilendirme

## 1. Önerilen Mimari

İlk sürüm için en dengeli yapı:

- Mobil uygulama: React Native + Expo
- Backend platformu: Supabase
- Veritabanı: PostgreSQL
- Yönetim paneli: Next.js
- Bildirim: Firebase Cloud Messaging
- Medya: Supabase Storage

Bu yapı tek seferde hem `iOS` hem `Android` çıkışı için yeterlidir ve MVP maliyetini düşük tutar.

## 2. Katmanlar

### Mobil Uygulama
- Kullanıcı kayıt/giriş
- Profil yönetimi
- Topluluk ve alt alan görüntüleme
- Etkinlik oluşturma isteği
- Etkinlik katılımı
- Yorum ve puanlama
- Fotoğraf/paylaşım akışı
- Bildirim ekranı

### Admin Panel
- Etkinlik onay/red
- Organizatör yetkisi verme
- Topluluk oluşturma ve düzenleme
- Kullanıcı şikayet inceleme
- İçerik moderasyonu
- Rapor ekranları

### Backend / Supabase
- Auth
- PostgreSQL veri katmanı
- Row Level Security
- Storage
- Realtime event/presence desteği
- Edge Functions ile özel iş kuralları

## 3. Uygulama Domain Yapısı

Temel domain'ler:

- `identity`
- `profiles`
- `communities`
- `events`
- `posts`
- `reviews`
- `notifications`
- `moderation`

Bu ayrım ileride `NestJS` gibi özel backend'e geçişi de kolaylaştırır.

## 4. Rol Modeli

### guest
- Kayıt olmamış kullanıcı

### member
- Profil açabilir
- Topluluğa katılabilir
- Etkinliklere başvurabilir
- Yorum yapabilir
- Puan verebilir
- Paylaşım yapabilir

### organizer
- Admin onayıyla etkinlik talebi oluşturabilir
- Kendi etkinlik taslaklarını güncelleyebilir
- Katılımcı listesini görebilir

### moderator
- İçerik raporlarını yönetebilir
- Yorum ve gönderileri gizleyebilir
- Kullanıcıyı geçici kısıtlayabilir

### admin
- Tüm toplulukları yönetir
- Tüm etkinlikleri onaylar/reddeder
- Rolleri yönetir
- Moderasyon politikalarını belirler

## 5. Yetki Mantığı

### Etkinlik Açma
- Her kullanıcı doğrudan yayınlanmış etkinlik oluşturamaz
- `member` veya `organizer` rolündeki kullanıcı etkinlik talebi oluşturur
- Kayıt önce `pending_approval` durumunda tutulur
- `admin` onayı sonrası `published` olur

### Etkinlik Puanlama
- Yalnızca etkinliğe katılımı `approved` veya `checked_in` olan kullanıcı puan verebilir
- Aynı kullanıcı aynı etkinliği yalnızca bir kez puanlayabilir

### Kullanıcı Yıldızlama
- Kullanıcılar birbirine etkinlik bağlantılı değerlendirme bırakabilir
- Değerlendirme, katılım sonrası açılırsa güven artar
- Sahte hesap etkisini azaltmak için minimum hesap yaşı veya katılım şartı eklenebilir

### Topluluk ve Alt Alanlar
- Bir topluluğun `parent_id` alanı varsa bu kayıt alt alandır
- Topluluk tipi:
  - `public`
  - `private`
  - `invite_only`

## 6. Temel Durum Alanları

### Kullanıcı Durumu
- `active`
- `pending_verification`
- `suspended`
- `deleted`

### Etkinlik Durumu
- `draft`
- `pending_approval`
- `published`
- `rejected`
- `cancelled`
- `completed`

### Katılım Durumu
- `pending`
- `approved`
- `rejected`
- `cancelled`
- `checked_in`

### Rapor Durumu
- `open`
- `under_review`
- `resolved`
- `dismissed`

## 7. Önerilen Klasör Yapısı

### Mobil
```txt
mobile/
  app/
  src/
    features/
      auth/
      profile/
      communities/
      events/
      posts/
      reviews/
      notifications/
    lib/
    components/
    hooks/
    services/
```

### Admin
```txt
admin/
  app/
  src/
    modules/
      users/
      communities/
      events/
      moderation/
      analytics/
```

### Backend Altyapısı
```txt
supabase/
  migrations/
  seed/
  functions/
```

## 8. Güvenlik Yaklaşımı

İlk günden zorunlu:

- Tüm iş kurallarını sadece istemciye bırakmamak
- Veritabanında `RLS` kullanmak
- Her kayıt için `created_by` veya eşdeğeri iz alanı tutmak
- Soft delete veya görünürlük alanları eklemek
- Medya yüklemelerinde boyut ve mime-type kontrolü yapmak
- Şikayet ve engelleme tablolarını baştan tasarlamak

## 9. Supabase Tarafında Kullanılacak Başlıca Özellikler

### Auth
- E-posta/şifre
- Google login
- Apple login

### Database
- PostgreSQL ana veri kaynağı
- Trigger ve function desteği

### Storage
- Profil fotoğrafları
- Etkinlik görselleri
- Gönderi medyaları

### Realtime
- Yorum akışı
- Bildirim badge güncellemeleri

### Edge Functions
- Push notification tetikleme
- İçerik moderasyon akışı
- Özel admin işlemleri

## 10. API Seviyesinde Ana Akışlar

### Auth
- kayıt ol
- giriş yap
- çıkış yap
- şifre sıfırla

### Communities
- topluluk listele
- topluluk detay
- topluluğa katıl
- alt alan oluştur

### Events
- etkinlik talebi oluştur
- etkinlik onayla/reddet
- etkinlik listele
- etkinliğe katıl
- etkinliği puanla

### Posts
- gönderi oluştur
- yorum yap
- medya yükle

### Reviews
- kullanıcıya yıldız ver
- kullanıcı yorumu bırak

### Moderation
- rapor oluştur
- rapor incele
- içeriği gizle

## 11. MVP İçin Net Teknik Kararlar

İlk sürümde şunları sabit kabul edelim:

- Native yerine cross-platform geliştirme yapılacak
- Backend olarak Supabase kullanılacak
- Admin paneli mobil uygulamadan ayrı web proje olacak
- Ödemeler ve canlı mesajlaşma ilk sürüm dışında kalacak
- İlk sürümde çoklu dil opsiyonel olacak

## 12. Sonraki Teknik Adım

Bu dokümandan sonra hemen uygulanması gereken sıradaki işler:

1. Veritabanı migration dosyasını oluşturmak
2. RLS kurallarını tanımlamak
3. Mobil ve admin proje iskeletini kurmak
4. Auth ve profil akışını ayağa kaldırmak
