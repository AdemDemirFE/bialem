# Bialem Uygulaması - Ürün ve Teknik Yol Haritası

## 1. Uygulama Özeti

Bu uygulama, topluluk ve etkinlik odaklı bir mobil sosyal ağdır.

Kullanıcılar:
- Üye olabilir
- Profil oluşturabilir
- Topluluklara/alt alanlara katılabilir
- Etkinliklere yorum yapabilir
- Birbirine yıldız ve puan verebilir
- Etkinlik içinden fotoğraf, not ve deneyim paylaşabilir

Yönetici olarak siz:
- Etkinlik açma yetkisini kontrol edersiniz
- Kullanıcıları onaylayabilir veya rol verebilirsiniz
- Alt topluluklar (grup/alan/network) oluşturabilirsiniz
- İçerik moderasyonu yapabilirsiniz

## 2. Ana Roller

### Ziyaretçi
- Uygulamayı inceler
- Kayıt olur / giriş yapar

### Üye
- Profil düzenler
- Topluluklara katılır
- Etkinlikleri görüntüler
- Yorum yapar
- Yıldız verir
- Etkinlik puanlar
- Gönderi/fotoğraf paylaşır

### Yetkili Organizatör
- Sizin onayınızla etkinlik oluşturur
- Kendi etkinliklerini düzenler
- Etkinlik katılımcılarını yönetir

### Moderatör
- Şikayetleri inceler
- Uygunsuz yorum/gönderi kaldırır

### Süper Admin
- Tüm kullanıcı, rol, topluluk ve etkinlikleri yönetir
- Onay akışlarını belirler

## 3. Ana Modüller

### Kimlik ve Üyelik
- E-posta ile kayıt/giriş
- Telefon doğrulama (opsiyonel)
- Apple ile giriş
- Google ile giriş
- Şifre sıfırlama
- KVKK/GDPR onayları

### Profil Sistemi
- Profil fotoğrafı
- Biyografi
- İlgi alanları
- Şehir/lokasyon
- Kullanıcı puanı
- Kullanıcının açtığı etkinlikler
- Kullanıcının aldığı yorum ve yıldızlar

### Topluluk / Alt Alan Sistemi
- Ana topluluk yapısı
- Alt topluluklar / alt alanlar
- Açık, kapalı ve davetli topluluk seçenekleri
- Topluluk yöneticileri
- Topluluk akışı

Bu bölüm, WordPress grup/topluluk mantığına en yakın sosyal yapı olur.

### Etkinlik Sistemi
- Etkinlik oluşturma talebi
- Admin onayı sonrası yayın
- Tarih, saat, konum
- Katılım limiti
- Ücretli / ücretsiz etkinlik
- Etkinlik kategorileri
- Harita entegrasyonu
- Katılım isteği / direkt katılım
- QR ile check-in (faz 2)

### Yorum, Yıldız ve Puanlama
- Kullanıcılar etkinliğe yorum yapar
- Kullanıcılar birbirine yıldız verebilir
- Etkinlik sonrası puanlama yapılır
- Sahte puanlamayı azaltmak için yalnızca katılanlar puan verebilir
- Kullanıcı güven skoru üretilebilir

### Paylaşım Akışı
- Etkinlikten fotoğraf/video paylaşımı
- Metin gönderileri
- Beğeni
- Yorum
- Kaydetme
- Şikayet etme

### Bildirim Sistemi
- Yeni yorum
- Yeni takipçi / topluluk daveti
- Etkinlik onayı
- Etkinlik hatırlatması
- Admin duyuruları
- Push notification

### Moderasyon ve Güvenlik
- İçerik şikayet sistemi
- Kullanıcı engelleme
- Kullanıcı raporlama
- Admin paneli
- Yasaklı kelime filtresi
- Medya moderasyonu

## 4. MVP'de Olması Gerekenler

İlk sürüm için en doğru yaklaşım, fazla büyük başlamadan güçlü bir temel kurmaktır.

### MVP Özellikleri
- Kayıt / giriş
- Profil oluşturma
- Topluluk ve alt alan oluşturma
- Admin onaylı etkinlik oluşturma
- Etkinlik listeleme ve detay sayfası
- Katılım isteği / katılım
- Yorum sistemi
- Etkinlik puanlama
- Kullanıcıya yıldız verme
- Fotoğraf ve metin paylaşımı
- Bildirimler
- Admin paneli

### Faz 2 Özellikleri
- Canlı mesajlaşma
- Takip sistemi
- QR check-in
- Premium üyelik
- Bilet satışı / ödeme entegrasyonu
- Rozet ve seviye sistemi
- Gelişmiş öneri algoritması

## 5. Teknik Öneri

App Store ve Play Store için tek kod tabanı kullanmak en mantıklı çözümdür.

### Mobil
- React Native + Expo

Neden:
- iOS ve Android için tek ekip/tek kod tabanı
- Daha hızlı MVP
- Push notification, kamera, galeri, konum gibi ihtiyaçlara uygun

Alternatif:
- Flutter

Eğer görsel kalite ve animasyon çok kritikse Flutter da güçlü seçenektir. Ancak hızlı ekip kurma ve web-admin entegrasyonu açısından React Native daha esnek olur.

### Backend
- Supabase veya NestJS + PostgreSQL

En iyi başlangıç önerim:
- Supabase

Neden:
- Authentication hazır gelir
- PostgreSQL hazırdır
- Storage hazırdır
- Realtime özellikleri vardır
- MVP maliyeti düşer

Daha kurumsal ikinci aşama:
- NestJS backend
- PostgreSQL
- Redis
- S3 uyumlu storage

### Admin Panel
- Next.js tabanlı web panel

Buradan:
- Kullanıcı yönetimi
- Etkinlik onayı
- İçerik moderasyonu
- Topluluk yönetimi
- Rapor ekranları

### Bildirim
- Firebase Cloud Messaging
- iOS için APNs bağlantısı

### Medya
- Supabase Storage veya Cloudinary

## 6. Önerilen Veri Modeli

### Tablolar
- users
- profiles
- roles
- communities
- community_members
- events
- event_requests
- event_participants
- posts
- comments
- ratings
- user_reviews
- media
- notifications
- reports
- bans

### Önemli İlişkiler
- Bir kullanıcının birden çok topluluğu olabilir
- Bir topluluğun birden çok etkinliği olabilir
- Bir etkinliğin birden çok yorumu olabilir
- Bir kullanıcı başka bir kullanıcıyı değerlendirebilir
- Yalnızca etkinliğe katılan kullanıcılar etkinliği puanlayabilir

## 7. Uygulama Ekranları

### Mobil Taraf
- Splash
- Onboarding
- Kayıt / giriş
- Ana akış
- Topluluklar
- Alt alan detay
- Etkinlik listesi
- Etkinlik detay
- Etkinlik oluşturma talebi
- Paylaşım oluşturma
- Profil
- Bildirimler
- Ayarlar

### Admin Web
- Dashboard
- Kullanıcı listesi
- Etkinlik onay ekranı
- Topluluk yönetimi
- İçerik şikayetleri
- Raporlama ekranı

## 8. Güvenlik ve Mağaza Yayını İçin Zorunlu Konular

### Hukuki
- Gizlilik politikası
- Kullanım şartları
- Açık rıza ve veri işleme metinleri
- İçerik kaldırma ve şikayet mekanizması

### Teknik
- Rol bazlı yetkilendirme
- Rate limiting
- Medya boyut kontrolü
- Spam koruması
- Şifreli token yapısı
- Sunucu tarafında izin kontrolleri

### App Store / Play Store
- Apple Sign In gerekliliği değerlendirilmeli
- Uygunsuz kullanıcı içeriği için raporlama sistemi şart
- Hesap silme özelliği olmalı
- Gizlilik linkleri hazırlanmalı

## 9. Geliştirme Planı

### Faz 1 - Analiz ve Tasarım
- Ürün kapsamını netleştir
- Kullanıcı akışlarını çıkar
- Wireframe hazırla
- Veri modelini belirle

### Faz 2 - MVP Geliştirme
- Mobil uygulama
- Backend
- Admin panel
- Test verisi

### Faz 3 - Test
- Beta kullanıcı testi
- Hata düzeltmeleri
- Güvenlik kontrolü
- Performans optimizasyonu

### Faz 4 - Yayın
- Store görselleri
- Gizlilik ve kullanım metinleri
- App Store Connect ve Google Play Console hazırlığı

## 10. Yaklaşık Efor

Küçük ama sağlam bir MVP için:
- 1 ürün tasarımcısı
- 1 mobil geliştirici
- 1 backend geliştirici
- 1 part-time QA / test desteği

Tek kişiyle yapılırsa:
- 3 ila 5 ay MVP

Küçük ekiple yapılırsa:
- 8 ila 12 hafta MVP

## 11. En Doğru Başlangıç Önerim

İlk sürüm için şu mimari en mantıklı seçim olur:

- Mobil: React Native + Expo
- Backend: Supabase
- Admin panel: Next.js
- Tasarım: Figma
- Bildirim: Firebase

Bu kombinasyon:
- hızlı çıkar
- maliyeti düşürür
- App Store ve Play Store için uygundur
- daha sonra büyütülebilir

## 12. Sonraki Adım

Bu proje için bundan sonra 3 farklı yoldan ilerlenebilir:

1. Sadece detaylı ürün dokümanı hazırlamak
2. Teknik mimariyi ve veritabanını çıkarıp yazılıma başlamak
3. Doğrudan React Native + Supabase tabanlı MVP projesini kurmak

En doğru pratik başlangıç genelde 2. adımdır, ardından hemen 3. adıma geçilir.
