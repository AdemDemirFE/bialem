# Bialem Magaza Yayin Engelleri

Guncelleme: 3 Agustos 2026

Bu liste yalnizca Google Play veya App Store gonderimini durduran maddeleri icerir. Izleme, pazarlama ve sonraki surum iyilestirmeleri bu listeye dahil degildir.

## Ortak Durum

- `[TAMAM]` Yerel release kontrolu, mobil ve admin typecheck, admin production build.
- `[TAMAM]` Expo Doctor: 18/18.
- `[TAMAM]` Android hedef API: 36.
- `[TAMAM]` Gizlilik politikasi, kullanim sartlari ve hesap silme sayfalari canli.
- `[TAMAM]` Uygulama icinde hesap silme, raporlama, engelleme ve moderasyon akislari mevcut.
- `[TAMAM]` Google Play ikon ve feature graphic hazir.

## Google Play Engelleri

1. `[BEKLIYOR]` Google Play Console gelistirici hesabinin turu ve acilis tarihi dogrulanmali.
2. `[BEKLIYOR]` Production imzali Android App Bundle (`.aab`) olusturulmali.
3. `[BEKLIYOR]` Play Console uygulama kaydi, magaza metinleri, ekran goruntuleri ve iletisim bilgileri tamamlanmali.
4. `[BEKLIYOR]` Data Safety, Content Rating, Target Audience, Ads, App Access ve Account Deletion formlari doldurulmali.
5. `[KOSULLU]` 13 Kasim 2023 sonrasinda acilan kisisel hesaplarda 12 tester ile kesintisiz 14 gun kapali test tamamlanmali ve production erisimi istenmeli.
6. `[BEKLIYOR]` Inceleme/demo hesabi ve moderator akisi icin inceleme notlari eklenmeli.
7. `[BEKLIYOR]` Son AAB ile kritik gercek cihaz testleri tamamlanmali.

## App Store Engelleri

1. `[BEKLIYOR]` Aktif Apple Developer Program hesabi ve App Store Connect erisimi dogrulanmali.
2. `[BEKLIYOR]` Apple Distribution signing, provisioning ve APNs anahtari tamamlanmali.
3. `[BEKLIYOR]` Apple Team ID ile `apple-app-site-association` dosyasi yayinlanmali.
4. `[BEKLIYOR]` Xcode 26 / iOS 26 SDK ile production iOS build olusturulmali.
5. `[BEKLIYOR]` TestFlight gercek cihaz testi tamamlanmali.
6. `[BEKLIYOR]` App Store kaydi, iPhone ekran goruntuleri, copyright ve metadata tamamlanmali.
7. `[BEKLIYOR]` App Privacy, yeni Age Rating, Export Compliance ve App Review formlari doldurulmali.
8. `[BEKLIYOR]` Inceleme/demo hesabi ile topluluk, moderator, etkinlik, raporlama ve hesap silme notlari eklenmeli.

## Kritik Son Cihaz Testleri

- Etkinlik katilim, bekleme sirasi, kendi katilimini iptal etme ve etkinlik iptali.
- Yalnizca onayli katilimcinin etkinlik sohbetine erisebilmesi.
- QR giris ve Bialem Avantaj QR kodunun tek kullanim davranisi.
- Push bildirimin on plan, arka plan ve kapali uygulamada acilmasi.
- Raporlama, kullanici engelleme ve hesap silme.
- Takvim, App Link/Universal Link, tarih-saat secici ve harita konum secimi.

## Uygulanacak Sira

1. Play Console hesap kosulunu dogrula.
2. Android production AAB olustur ve internal/closed testing kanalina yukle.
3. Play Console formlarini ve gorsellerini tamamla.
4. Apple Developer/App Store Connect hesabini dogrula.
5. Apple signing, AASA ve iOS production build islemlerini tamamla.
6. TestFlight ve son cihaz testlerinden sonra iki magazada incelemeye gonder.
