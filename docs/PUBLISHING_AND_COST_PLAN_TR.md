# Bialem Yayın ve Maliyet Planı

Güncelleme tarihi: 15 Temmuz 2026

> TL karşılıkları bütçe planlaması için `1 USD = 50 TL` varsayımıyla yuvarlanmıştır. Ödeme günündeki banka kuru, vergi ve kart komisyonu farklı olabilir.

## 1. Yayına Kadar İzlenecek Sıra

### Aşama 1 - Özellikleri Dondurma ve Veritabanı

- Yeni özellik eklemeyi geçici olarak durdurun ve yayın adayını belirleyin.
- `0017_growth_channels_and_honor_badges.sql` ve `0018_profile_avatars.sql` migration dosyalarını Supabase üzerinde çalıştırın.
- RLS politikalarını iki normal üye, bir moderatör ve bir admin hesabıyla sınayın.
- Veritabanı ve Storage için yedek alın.
- Demo topluluk, grup, etkinlik, hikâye, yorum, rapor ve hesap silme senaryolarını test edin.

Tahmini süre: 3-5 gün.

### Aşama 2 - Üretim Servisleri

- Supabase projesini Pro plana alın ve harcama limitini açık tutun.
- Özel SMTP sağlayıcısını bağlayın; Supabase'in yerleşik e-posta servisi üretim için uygun değildir.
- `bialem.app` alan adını alın ve `EXPO_PUBLIC_WEB_URL` değerini tanımlayın.
- Apple Universal Link ve Android App Link doğrulama dosyalarını alan adına yayınlayın.
- EAS projesini oluşturun, `projectId`, Android FCM ve Apple APNs bilgilerini tamamlayın.
- OpenAI kullanım limiti ve aylık bütçe alarmı koyun.

Tahmini süre: 2-4 gün.

### Aşama 3 - Hukuk ve Mağaza Politikaları

- KVKK aydınlatma metni, gizlilik politikası, kullanım şartları ve topluluk kurallarını hukuk uzmanına kontrol ettirin.
- Herkese açık bir gizlilik politikası URL'si ve destek e-posta adresi hazırlayın.
- Uygulama içi hesap silme, kullanıcı engelleme, içerik raporlama ve moderasyon akışlarını gerçek cihazda doğrulayın.
- App Store gizlilik beyanında e-posta, profil, fotoğraf, şehir, kullanıcı içeriği, cihaz bildirimi ve yapay zekâya gönderilen verileri doğru beyan edin.
- İnceleme ekibi için çalışan demo hesap ve örnek QR kod hazırlayın.

Tahmini süre: 3-7 gün. Hukuk danışmanlığı bu belgedeki yazılım bütçesine dahil değildir.

### Aşama 4 - Gerçek Cihaz Testi

- En az iki iPhone ve üç farklı Android cihaz boyutunda test yapın.
- Kayıt, e-posta doğrulama, fotoğraf yükleme, topluluğa katılma, etkinlik başvurusu, moderatör onayı, QR giriş, sohbet, bildirim, takvim, raporlama ve hesap silmeyi uçtan uca deneyin.
- İnternet kesintisi, düşük bağlantı, izin reddi ve boş veri durumlarını sınayın.
- Çökme takibi ve temel ürün analitiğini etkinleştirin.

Tahmini süre: 7-14 gün.

### Aşama 5 - Mağaza Hazırlığı

- Uygulama ikonu, açılış ekranı, telefon ekran görüntüleri, kısa açıklama, uzun açıklama, anahtar kelimeler ve yaş derecelendirmesini hazırlayın.
- Android `AAB` ve iOS `IPA` üretim derlemelerini EAS ile oluşturun.
- TestFlight iç testini başlatın.
- Google Play iç testini başlatın.
- Yeni kişisel Google Play hesabı kullanılıyorsa en az 12 test kullanıcısını 14 gün kesintisiz kapalı testte tutun.

Tahmini süre: Google zorunlu kapalı test dahil en az 14-21 gün.

### Aşama 6 - Yayın ve Kontrollü Açılış

- Önce Ankara gibi tek şehirde sınırlı topluluk ve etkinliklerle başlayın.
- Android'de kademeli yayın, iOS'ta manuel yayın seçeneğini kullanın.
- İlk hafta raporları, kayıt hatalarını, bildirim teslimini, yapay zekâ maliyetini ve Storage kullanımını her gün kontrol edin.
- İlk 30 günde yeni özellik yerine hata düzeltme, moderasyon ve kullanıcı geri bildirimine öncelik verin.

## 2. Zorunlu ve Tavsiye Edilen Maliyetler

| Kalem | Ücret | Sıklık | Not |
|---|---:|---|---|
| Apple Developer Program | 99 USD | Yıllık | App Store yayını için zorunlu |
| Google Play Console | 25 USD | Tek sefer | Android yayını için zorunlu |
| Alan adı | Yaklaşık 10-25 USD | Yıllık | Kayıt firmasına ve uzantıya göre değişir |
| Supabase Free | 0 USD | Aylık | Yalnızca geliştirme ve küçük beta için |
| Supabase Pro | 25 USD'den başlar | Aylık | Canlı uygulama için tavsiye edilir |
| Expo EAS Free | 0 USD | Aylık | İlk yayın için yeterli olabilir |
| Expo EAS Starter | 19 USD + kullanım | Aylık | Hızlı derleme ve düzenli güncellemelerde tercih edilir |
| Expo Push Service | 0 USD | Kullanıma bağlı | Proje başına saniyede 600 bildirim sınırı vardır |
| SMTP | 0 USD'den başlar | Aylık | Resend Free: günde 100, ayda 3.000 e-posta |
| OpenAI API | Kullanıma bağlı | Aylık | Mevcut varsayılan model `gpt-5.4-mini` |
| Hukuk danışmanlığı | Teklif alınmalı | İhtiyaca bağlı | KVKK ve sözleşme kontrolü için tavsiye edilir |
| Reklam ve içerik üretimi | İsteğe bağlı | Aylık | Teknik işletme bütçesinden ayrılmalıdır |

### İlk Yıl Mağazaya Çıkış Tabanı

Apple `99 USD` + Google Play `25 USD` + alan adı `10-25 USD`:

- Toplam: yaklaşık `134-149 USD`
- Planlama kuru ile yaklaşık `6.700-7.450 TL`
- Supabase, yapay zekâ, hukuk, reklam ve vergiler bu toplama dahil değildir.

## 3. Aylık İşletme Senaryoları

### Kapalı Beta

- Supabase Free: `0 USD`
- Expo EAS Free: `0 USD`
- SMTP Free: `0 USD`
- OpenAI deneme/kısıtlı kullanım: `5-15 USD`
- Alan adı aylık karşılığı: `1-2 USD`

Toplam: yaklaşık `6-17 USD/ay`, planlama kuru ile `300-850 TL/ay`.

Free Supabase projeleri hareketsizlikte duraklayabildiği ve otomatik yedek sunmadığı için bu seçenek canlı yayın için önerilmez.

### Tavsiye Edilen İlk Canlı Sürüm - Yaklaşık 1.000 Aktif Kullanıcı

- Supabase Pro: `25 USD`
- Expo EAS Free: `0 USD`
- OpenAI: yaklaşık `15-35 USD`
- SMTP: ilk 3.000 e-posta içinde `0 USD`
- Alan adı aylık karşılığı: `1-2 USD`

Toplam: yaklaşık `41-62 USD/ay`, planlama kuru ile `2.050-3.100 TL/ay`.

Expo Starter seçilirse toplam yaklaşık `60-81 USD/ay`, yani `3.000-4.050 TL/ay` olur.

### Büyüme - Yaklaşık 10.000 Aktif Kullanıcı

- Supabase Pro ve olası aşım: `25-75 USD`
- Expo Starter: `19 USD + olası aşım`
- OpenAI: yaklaşık `150-350 USD`
- SMTP ve izleme servisleri: `0-50 USD`
- Alan adı: `1-2 USD`

Toplam: yaklaşık `195-496 USD/ay`, planlama kuru ile `9.750-24.800 TL/ay`.

Bu senaryoda en değişken kalem yapay zekâdır. Asistan için kullanıcı başına günlük limit, kısa yanıt sınırı, önbellek ve aylık harcama tavanı kullanılmalıdır.

## 4. Yapay Zekâ Hesaplama Yöntemi

Mevcut Edge Function varsayılan olarak `gpt-5.4-mini` kullanır. Resmî fiyatı 1 milyon input token için `0,75 USD`, 1 milyon output token için `4,50 USD` düzeyindedir.

Ortalama bir isteğin 500-1.500 input ve 250-500 output token tükettiği varsayılırsa:

- 10.000 asistan isteği: yaklaşık `15-35 USD`
- 100.000 asistan isteği: yaklaşık `150-350 USD`

Uzun sohbet geçmişi her istekte tekrar gönderilirse maliyet yükselir. Bu nedenle yalnızca son mesajların gönderilmesi ve kullanıcı başına aylık kota uygulanması gerekir.

## 5. Kaynaklar

- Apple Developer üyeliği: https://developer.apple.com/programs/whats-included/
- Google Play kayıt: https://support.google.com/googleplay/android-developer/answer/6112435
- Google kapalı test şartı: https://support.google.com/googleplay/android-developer/answer/14151465
- Expo fiyatları: https://expo.dev/pricing
- Expo push maliyeti: https://docs.expo.dev/push-notifications/faq/
- Supabase fiyatları: https://supabase.com/pricing
- Supabase SMTP: https://supabase.com/docs/guides/auth/auth-smtp
- OpenAI `gpt-5.4-mini`: https://developers.openai.com/api/docs/models/gpt-5.4-mini
- Apple gizlilik beyanı: https://developer.apple.com/app-store/app-privacy-details/
- Apple hesap silme: https://developer.apple.com/support/offering-account-deletion-in-your-app
