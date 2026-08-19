# EAS Build ve OTA Guncelleme Rehberi

Bu proje uc ayri EAS Update kanali kullanir:

- `development`: Gelistirme istemcisi.
- `preview`: Gercek cihaz kabul testi.
- `production`: Magazadaki kullanicilar.

## Ne Zaman Yeni Build Gerekir?

Asagidaki degisikliklerde yeni APK/AAB veya iOS build gerekir:

- Yeni native paket ekleme veya paket surumu degistirme.
- Android/iOS izinleri, ikon, splash, bundle/package kimligi degisikligi.
- Google Maps, Firebase, bildirim veya signing yapilandirmasi.
- Expo SDK ya da React Native surumu degisikligi.

Yalnizca JavaScript/TypeScript, metin, stil ve mevcut asset degisiklikleri OTA ile yayinlanabilir. Proje `appVersion` runtime politikasini kullanir. Native kodu veya native yapilandirmayi etkileyen her degisiklikte `mobile/app.json` icindeki `version` artirilmali ve yeni magaza build'i alinmalidir. Aksi halde eski build'lere uyumsuz OTA gonderilebilir.

## Guvenli OTA Akisi

1. Tum yerel kontrolleri calistirin:

```powershell
npm.cmd run check:release
```

2. Degisikligi once preview kanalina yayinlayin:

```powershell
cd mobile
npx eas-cli update --channel preview --message "Degisiklik ozeti"
```

3. Preview build kurulu gercek cihazda kayit, topluluk, etkinlik ve geri acilis testlerini yapin.

4. Ayni kaynak surumunu production kanalina yayinlayin:

```powershell
cd mobile
npx eas-cli update --channel production --message "Degisiklik ozeti"
```

## Yeni Magaza Build Akisi

Tum native degisiklikler bittikten ve release kontrolu gectikten sonra:

```powershell
cd mobile
npx eas-cli build --platform android --profile preview
```

Preview APK gercek cihazda onaylandiktan sonra Play Store icin:

```powershell
npx eas-cli build --platform android --profile production
```

iOS build, Apple Developer hesabi ve signing yapilandirmasi tamamlandiktan sonra ayni `production` profiliyle alinmalidir.

## Geri Donus

Sorunlu bir OTA yayininda EAS Dashboard uzerinden son saglam update grubunu yeniden production kanalina baglayin. Yeni bir duzeltme yayinlamadan once sorunlu surumu preview kanalinda tekrar uretip dogrulayin.

Production OTA komutu yalnizca gercek cihaz preview testi tamamlandiktan sonra calistirilmalidir.
