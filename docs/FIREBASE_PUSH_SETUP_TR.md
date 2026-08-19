# Android FCM v1 Kurulumu

Android uzak bildirimleri icin Firebase uygulama yapilandirmasi ile EAS FCM v1 gonderim anahtari birlikte gerekir. Hassas service-account JSON dosyasini GitHub'a, e-postaya veya sohbete yuklemeyin.

## 1. Firebase Android Uygulamasi

1. Firebase Console'da `bialem` projesini olusturun veya mevcut projeyi acin.
2. Android uygulamasi ekleyin ve paket adini tam olarak `com.bialem.app` yazin.
3. `google-services.json` dosyasini indirin. Bu dosya service-account private key degildir.
4. Dosyayi gecici olarak guvenli bir klasorde tutun. Sonraki adimda uygulama yapilandirmasina baglanacak.

## 2. FCM v1 Service Account

1. Firebase Project settings > Service accounts ekranini acin.
2. Yeni private key olusturun ve indirilen JSON dosyasini kimseyle paylasmayin.
3. `mobile` klasorunde su komutu calistirin:

```powershell
npx.cmd eas-cli credentials --platform android
```

4. Android production credentials icinde `Google Service Account` / `FCM V1` secenegini acin ve JSON dosyasini dogrudan EAS'e yukleyin.
5. Yukleme tamamlaninca yerel service-account JSON dosyasini guvenli parola kasasina alin veya silin.

## 3. Uygulamaya Baglama

`google-services.json` hazir oldugunda `mobile/app.json` icindeki Android bolumune dosya yolu eklenir ve yeni preview build alinir. Build APK'sinda `google_app_id` ile `gcm_defaultSenderId` kaynaklari gorulmeden FCM tamamlanmis sayilmaz.

## 4. Gercek Cihaz Kabul Testi

1. Yeni APK temiz kurulur ve bildirim izni verilir.
2. Giris sonrasi `push_tokens` tablosunda bu cihaza ait tek aktif Expo tokeni olusur.
3. Test bildirimi foreground, background ve uygulama kapaliyken teslim edilir.
4. Bildirime dokunmak dogru etkinlik, gonderi, topluluk veya kullanici ekranini acar.
5. Cikis yapinca token pasif olur; ayni telefonda ikinci hesap eski hesabin bildirimini almaz.
6. Expo push receipt sonucu `DeviceNotRegistered` verirse ilgili token pasiflestirilir.

Resmi Expo rehberleri:

- https://docs.expo.dev/push-notifications/fcm-credentials/
- https://docs.expo.dev/push-notifications/push-notifications-setup/
- https://docs.expo.dev/push-notifications/sending-notifications/
