# Bialem Yayin Hazirlik Durumu

Guncelleme: 3 Agustos 2026

## 13 Maddelik Kontrol Listesi

1. `[TAMAM]` Kaynak kontrolu ve rollback: Yerel `main` dali private `acbaldirlioglu-coder/bialem` deposuna baglandi ve ilk guvenli snapshot push edildi. `.env` dosyalari ignore ediliyor; commit adaylarinda `sb_secret_...`, private key, keystore veya credential dosyasi bulunmadi.
2. `[TAMAM]` Gizli anahtar ayrimi: Mobilde yalnizca publishable key var; service-role yalnizca sunucu ortamlarinda tutuluyor. Sizdirilan eski secret key iptal edilmis olmali.
3. `[TAMAM]` Supabase sema ve migration: Yerel migrationlar `0001-0054` arasinda tutuluyor. `0048-0050` ile grup uyelik moderasyonu ve guvenli ayrilma; `0051` ile topluluk uye dizini; `0052` ile etkinlik sohbetinin yalnizca onayli katilimcilara acilmasi; `0053` ile bekleme sirasi ve katilim iptali; `0054` ile yetkili etkinlik iptali production SQL Editor'da uygulandi. Onceki migration history ve production schema lint sonucu temiz.
4. `[TEST GEREKIYOR]` RLS ve rol matrisi: Normal uye etkinlik onerir; grup yoneticisi, etkinlik inceleme yetkili asistani ve admin dogrudan etkinlik olusturur. Bu akislar iki uye, moderator ve admin hesaplariyla gercek cihazda pozitif/negatif test edilmelidir.
5. `[TAMAM]` Yedekleme ve geri donus: 2 Agustos 2026 tarihinde bagli production projesinden DB + Storage yedegi alindi, AES-256-GCM ile sifrelendi ve SHA-256 ile dogrulandi. Public uygulama semasi ile 35 tablonun verisi production'dan bagimsiz gecici Docker PostgreSQL ortaminda basariyla geri yuklendi. Ayni yedek gecici Supabase staging projesine de tek transaction ile uygulandi; `auth.users` dahil 11 kritik tablo sayimi, 35/35 public tabloda RLS, 74 politika ve 90 public fonksiyon dogrulandi. Storage'daki 4 bucket ve 7 nesne staging'e yuklendi, yeniden indirilip SHA-256 ile dogrulandi. Geri yuklenen kullanicinin mevcut parolasiyla staging Auth girisi, authenticated RLS profil erisimi ve guvenli cikisi basariyla test edildi. Sifreli arsiv ile manifest fiziksel olarak ayri flash diske kopyalanip SHA-256 ile dogrulandi; acik yedek klasoru kaldirildi. Ayrintilar `docs/BACKUP_DRILL_2026-08-02_TR.md` dosyasindadir.
6. `[TAMAM]` Alan adi, HTTPS ve web sayfalari: `bialem.app`, gizlilik, hesap silme ve sifre sifirlama sayfalari canli.
7. `[TAMAM]` Uretim e-postasi: Resend DKIM/SPF/DMARC ve Supabase SMTP kurulu; teslimat testi yapildi.
8. `[DEVAM EDIYOR]` EAS ve imzalama: `@canexpo1/bialem` projesi, projectId ve Production public env'leri hazir. Google Maps anahtari Android uygulama ve API kisitlamalariyla EAS Production ortaminda Sensitive olarak tanimlandi. Firebase Android uygulamasi, `google-services.json` ve EAS FCM V1 hizmet hesabi eklendi. EAS Update; `development`, `preview` ve `production` kanallari ile `appVersion` runtime politikasina baglandi. Native acik/koyu tema kaynaklarini ve FCM yapilandirmasini iceren `versionCode 7` preview APK olusturuldu. Etkinlik turune ve kapak gorseline gore zenginlesen paylasim afisini ve Android alt guvenli alan duzeltmelerini iceren `e52db2c1-5304-42da-b2ff-c7894ae6fb8d` update grubu 3 Agustos 2026 tarihinde `preview` kanalinda yayinlandi. Expo SDK 54 Android API 36 hedefliyor. Apple signing, APNs, iOS 26 SDK ile production build ve TestFlight testi hala gerekli.
9. `[DEVAM EDIYOR]` Magaza gorselleri: Google Play icin 512 x 512 alfa kanalsiz ikon ve 1024 x 500 alfa kanalsiz feature graphic `store-assets/` altinda hazirlandi. Android adaptive icon ve splash yapilandirildi; Expo Doctor 18/18 ve yerel release kontrolu basarili. Telefon ekran goruntuleri ve istege bagli Android monochrome icon tamamlanmali.
10. `[DEVAM EDIYOR]` Universal/App Links: Android `assetlinks.json` production alan adinda dogru paket ve signing SHA-256 ile `200 application/json` olarak dogrulandi. Apple Team ID alindiktan sonra AASA yayinlanmali.
11. `[YAYIN ENGELI]` Gercek cihaz E2E: Kayit, e-posta dogrulama, giris/cikis, parola sifirlama, profil duzenleme, kisi arama, acik/gizli hesap takip, kamera izinleri ve coklu anlik hedefleme akislari gecti. Tarih/saat secici, uygulama ici harita, rol bazli etkinlik olusturma, etkinlik/grup kapak yukleme, topluluk onayi, QR, sohbet, bildirim, takvim, rapor ve hesap silme protokolu `docs/ANDROID_DEVICE_E2E_TR.md` dosyasinda izleniyor. Kalan adimlar `versionCode 7` preview APK ve guncel `preview` OTA ile tamamlanmali.
12. `[DEVAM EDIYOR]` Izleme ve operasyon: Public health endpoint ve ilk hafta runbook'u hazir; production smoke testi public sayfalar, health endpoint, admin giris yonlendirmesi ve admin login sayfasi icin basarili. Harici uptime alarmi, crash reporting ve OpenAI/Supabase harcama alarmlari kurulmalidir. Admin Next.js `15.5.21` maintenance LTS seviyesinde. Kalan Expo arac zinciri bildirimleri `--force` kullanmadan izlenmeli; yeni kararli duzeltmeler ciktiginda planli surum yukseltilmelidir.
13. `[DEVAM EDIYOR]` Hukuk ve magaza sureci: Play ve App Store metinleri, gorsel sirasi ve veri beyan taslagi `docs/STORE_SUBMISSION_TR.md` dosyasinda hazirlandi. Metinler hukuk uzmanindan gecmeli; resmi veri formlari, demo hesap ve kapali test sureci tamamlanmali.

## Otomatik Kontrol

```powershell
npm.cmd run check:release
```

Bu komut tip kontrollerini, admin production build'ini, secret sizintisi kontrolunu ve temel release dosyalarini denetler. Son denetimde Expo Doctor 18/18, Supabase schema lint ve production smoke kontrolleri de basarili oldu.

## Siradaki Zorunlu Sira

1. `docs/ANDROID_DEVICE_E2E_TR.md` icindeki kalan tum adimlari iki uye, moderator ve admin hesaplariyla bitir.
2. Harici uptime, crash reporting ve maliyet alarmlarini etkinlestir.
3. Android kapali test kosulu hesaba uygulaniyorsa tester surecini tamamla; ardindan production AAB olustur.
4. Apple Developer hesabi, signing/APNs, AASA ve TestFlight akisini tamamla.

Build ve OTA yayin sirasi icin `docs/EAS_UPDATE_RUNBOOK_TR.md` kullanilmalidir.

Yalnizca Google Play ve App Store gonderimini durduran maddeler `docs/STORE_RELEASE_BLOCKERS_TR.md` dosyasinda izlenir.
