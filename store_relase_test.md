# BiAlem Mobile — Store Release Test Raporu

> Tarih: 2026-08-31
> Proje: `C:\ADEM\GITHUB\bialem\mobile`
> Derleme: `npm run build` başarılı (`vite build --mode production`)
> Type check: `npm run typecheck` başarısız (19 hata)
> Chrome DevTools MCP: bu oturumda kullanılabilir araç listesi boş; otomatik browser/runtime testi yapılamadı. Aşağıdaki bulgular statik kod/build incelemesine dayanır.

---

## Özet

Mağazaya göndermeden önce çözülmesi gereken **P0 (release blocker)** bulgu sayısı: **7**
- Bundle/package ID tutarsızluğu
- iOS production HTTP güvenlik açığı
- Android cleartext + HTTP production yapılandırması
- TypeScript derleme hataları
- Eksik PWA manifest / service worker
- iOS Privacy Manifest eksik
- iOS AppIcon seti eksik boyutlarla

P1 (release öncesi) bulgu sayısı: **11**
P2 (release sonrası) bulgu sayısı: **5**

---

## 1. APP STORE / PLAY STORE META VERİLERİ

| # | Kontrol | Durum | Dosya:Sıra | Kanıt | Risk | Öneri |
|---|---|---|---|---|---|---|
| 1.1 | Bundle Identifier tutarlılığı | **açık** | `capacitor.config.json:3`, `app.json:15,21`, `android/app/build.gradle:37`, `google-services.json:12` | iOS: `com.bialem.app`, Android: `com.bialem.mobile`, Expo slug `bialem` | **KRİTİK** | Tek bir bundle ID seç: hem iOS hem Android `com.bialem.app` veya `com.bialem.mobile`. Provisioning, app links, Firebase, deeplink hepsi aynı ID'ye bağlanmalı. |
| 1.2 | Uygulama adı tutarlılığı | **kısmen** | `capacitor.config.json:3`, `app.json:3`, `index.html:6`, `android/app/src/main/res/values/strings.xml:3` | `BiAlem` vs `Bi Alem` | DÜŞÜK | Mağaza görünümü ve uygulama ikonu altındaki adı tek bir forma sabitle: `BiAlem` veya `Bi Alem`. |
| 1.3 | Versiyon / build numarası | **kısmen** | `package.json:3`, `app.json:8,14,22`, `android/app/build.gradle:6-9` | `1.0.2` + buildNumber `1` + versionCode `9`; Android `versionCode` root `package.json`'dan `bialem.versionCode` okuyor | ORTA | Android `versionCode` kaynağı net değil; `rootPackageJson.bialem.versionCode` tanımlı mı? Build öncesi doğrula, her release'de artır. |
| 1.4 | Uygulama açıklaması | **var** | `app.json:6` | Türkçe açıklama mevcut | DÜŞÜK | App Store/Play Store için İngilizce versiyon da hazırla. |
| 1.5 | Ekran görüntüleri | **emin değilim** | — | Rapor kapsamında üretilmedi | ORTA | iPhone 6.7", 6.5", iPad, Android telefon/tablet ekran görüntüleri hazırlanmalı. |
| 1.6 | Yaş derecelendirmesi / content rating | **emin değilim** | — | Veri yok | ORTA | App Store ve Play Console'da yaş derecelendirmesi doldurulmalı. |
| 1.7 | Gizlilik politikası / KVKK / kullanım şartları | **var** | `app/account.tsx:13-16` | `privacy`, `terms`, `kvkk`, `community` linkleri var | DÜŞÜK | Linklerin canlı ve mağaza metadata alanlarına eklenmiş olduğundan emin ol. |
| 1.8 | Hesap silme | **var** | `app/account.tsx:67-83` | Kullanıcı "HESABIMI SİL" yazarak silebiliyor, backend `DELETE /api/app/me` çağrısı | DÜŞÜK | Apple / Google hesap silme gereksinimini karşılar. Sildirip sonra aynı oturumla sunucuya tekrar istek atıp atmadığını test et. |

---

## 2. GÜVENLİK

| # | Kontrol | Durum | Dosya:Sıra | Kanıt | Risk | Öneri |
|---|---|---|---|---|---|---|
| 2.1 | iOS NSAllowsArbitraryLoads | **açık** | `ios/App/App/Info.plist:36-40` | `<true/>` tüm domainler için arbitrary load açık | **KRİTİK** | Production'da kaldır. Sadece `api.bialem.app` için `NSExceptionDomains` kullan veya tamamen kaldır (ATS default HTTPS). |
| 2.2 | Android cleartext production | **açık** | `capacitor.config.json:8`, `android/app/src/main/res/xml/network_security_config.xml:12-17` | `cleartext: true`, `allowMixedContent: true`; `191.215.36.29` IP'sine cleartext izni var | **KRİTİK** | `capacitor.config.json`'dan `cleartext` ve `allowMixedContent` kaldır. `network_security_config.xml`'de `191.215.36.29` domain config'ini production'dan çıkar. `androidScheme` HTTPS yap. |
| 2.3 | Token depolama | **açık** | `src/lib/api.ts:5,17-20` | `AsyncStorage` üzerinde `bialem_api_token` | **YÜKSEK** | iOS Keychain / Android EncryptedSharedPreferences (Keystore) kullan. `@capacitor/preferences` + encrypted storage veya `capacitor-secure-storage-plugin`. |
| 2.4 | Push token localStorage | **açık** | `src/lib/pushNotifications.ts:74,116`, `src/lib/notificationApi.ts:113-114` | `bialem.push.token`, `bialem.push.deviceUuid` `localStorage`'a yazılıyor | **YÜKSEK** | Native platformda `localStorage` web view izole alanıdır ama yine de güvenli storage taşı. Web'de de IndexedDB/secure cookie tercih et. |
| 2.5 | API base URL production | **var** | `vite.config.ts:117`, build çıktısı | `env=prod api=https://api.bialem.app` | DÜŞÜK | Dev/test IP'leri production build'e girmiyor; ancak `capacitor.config.json` localhost/cleartext production'da risk oluşturuyor. |
| 2.6 | Secret/hassas veri logları | **açık** | `src/lib/pushNotifications.ts:72`, `src/lib/notificationApi.ts:96-97` | Push token önizlemesi loglanıyor (`token.slice(0,8)`), hata loglarında response body gösteriliyor | **YÜKSEK** | Production'da push token loglarını tamamen kapat. `__DEV__` veya `diagnostics` flag ile sınırla. |
| 2.7 | Debug console.log'ları production | **açık** | `src/lib/pushNotifications.ts`, `src/lib/api.ts`, `src/lib/notificationApi.ts`, `src/lib/auth.tsx`, `src/lib/calendar.ts` | 20+ `console.log/warn/error/info` çağrısı | ORTA | `__DEV__` veya `diagnostics` flag altına al; production build'lerde console metodlarını noop'la. |
| 2.8 | Deep link şeması tutarlılığı | **kısmen** | `AndroidManifest.xml:32`, `app.json:7`, `app.json:36-52` | Android `bialem://reset-password`, Expo scheme `bialem`, HTTPS `bialem.app/event-share` | ORTA | iOS `Info.plist` / associated domains ve entitlements dosyasını kontrol et; `applinks:bialem.app` tanımlı mı? |
| 2.9 | App links / assetlinks.json | **açık** | `public/.well-known/assetlinks.json.template` | `YOUR_ANDROID_SHA256_FINGERPRINT` placeholder, `package_name: com.bialem.app` | **YÜKSEK** | Gerçek release keystore SHA-256 fingerprint ile doldur, `package_name` nihai ID ile uyumlu yap, sunucu `https://bialem.app/.well-known/assetlinks.json` olarak yayınla. |
| 2.10 | Apple app site association | **açık** | `public/.well-known/apple-app-site-association.template` | `YOUR_APPLE_TEAM_ID.com.bialem.app` placeholder | **YÜKSEK** | Gerçek Apple Team ID ile doldur, `https://bialem.app/.well-known/apple-app-site-association` olarak yayınla. MIME type `application/json` olmalı. |
| 2.11 | Android manifest allowBackup çelişkisi | **açık** | `AndroidManifest.xml:5` = `true`, `app.json:25` = `false` | Çelişkili değerler | ORTA | `app.json` ile `AndroidManifest.xml` arasında tutarlılık sağla; production'da `allowBackup="false"` önerilir. |
| 2.12 | Android `usesCleartextTraffic` | **kapalı** | `android/app/src/main/AndroidManifest.xml` | `android:usesCleartextTraffic` yok; `network_security_config` ile yönetiliyor | DÜŞÜK | Güvenlik config sadece `network_security_config.xml` üzerinden; production öncesi bu dosyayı temizle. |

---

## 3. BUILD / DERLEME

| # | Kontrol | Durum | Dosya:Sıra | Kanık | Risk | Öneri |
|---|---|---|---|---|---|---|
| 3.1 | Production build | **var** | Build çıktısı | `vite build --mode production` başarılı | DÜŞÜK | — |
| 3.2 | TypeScript type check | **açık** | `npm run typecheck` | 19 hata; DTO/UI model tip uyumsuzlukları | **KRİTİK** | `tsc --noEmit` hatalarını çöz. Örnek: `PostDto[]` → `PostItem[]`, `CommunityDto` → `CommunityRecord`, `EventDto` → `EventRecord` dönüşümleri. |
| 3.3 | Lint | **emin değilim** | — | `eslint` script'i `package.json`'da yok | ORTA | Lint komutu ekle ve çalıştır; biçimsel sorunları çöz. |
| 3.4 | Bundle boyutu | **kısmen** | Build çıktısı | JS chunk `1,530 kB` (gzipped 425 kB); resimler `evening-entertainment` 2.2 MB, `onboarding-worlds` 2.6 MB, `gastronomy` 2.7 MB | ORTA | 200 MB sınırına uzak ama açılış performansı için: onboarding PNG'leri WebP/AVIF yap, lazy load ekle, JS chunk'ı split et. |
| 3.5 | Proguard / code shrinking | **açık** | `android/app/build.gradle:50-53` | `minifyEnabled false` | ORTA | Release için `minifyEnabled true` ve ProGuard/R8 kurallarını test et; React Native Web/Capacitor ile uyumluluğunu doğrula. |
| 3.6 | iOS build signing | **emin değilim** | `ios/App/App.xcodeproj/project.pbxproj` | Signing/team alanları rapor kapsamında görülmedi | **YÜKSEK** | Xcode'da release signing, provisioning profili, App Store Connect team ayarlarını doğrula. |
| 3.7 | Android release signing | **emin değilim** | — | Keystore bilgisi rapor kapsamında yok | **YÜKSEK** | Release keystore oluştur, `android/app/build.gradle` signingConfigs ekle, Play App Signing ile uyumlu hale getir. |

---

## 4. İZİNLER ve GİZLİLİK

| # | Kontrol | Durum | Dosfa:Sıra | Kanıt | Risk | Öneri |
|---|---|---|---|---|---|---|
| 4.1 | iOS izin açıklamaları | **açık** | `ios/App/App/Info.plist` | `NSCameraUsageDescription`, `NSLocationWhenInUseUsageDescription`, `NSPhotoLibraryUsageDescription`, `NSUserNotificationUsageDescription` yok | **YÜKSEK** | `expo-camera`, `expo-image-picker`, `expo-location`, push notification kullanımı var; Info.plist'e ilgili `NS*UsageDescription` stringleri ekle. |
| 4.2 | iOS Privacy Manifest | **açık** | — | `PrivacyInfo.xcprivacy` dosyası yok | **KRİTİK** | App Store, 2024 sonrası uygulamalarda Privacy Manifest zorunlu. `NSPrivacyAccessedAPITypes` (disk space, file timestamp vb.) ve `NSPrivacyCollectedDataTypes` oluştur. |
| 4.3 | Android izinleri | **var** | `android/app/src/main/AndroidManifest.xml:59-60` | Sadece `INTERNET`, `POST_NOTIFICATIONS` | DÜŞÜK | Kamera/fotoğraf/konum için runtime izinleri gerektiğinde isteniyor; just-in-time yaklaşımı uygun. `blockedPermissions` ile gereksiz izinler kapatılmış. |
| 4.4 | Push notification izin zamanlaması | **kısmen** | `src/lib/pushNotifications.ts:45` | Giriş sonrası `initializePushNotificationsAfterLogin` çağrılıyor | DÜŞÜK | İlk açılışta değil, giriş sonrası isteniyor; iyi. Değer açıklaması eklenebilir. |

---

## 5. UI / ARAYÜZ ve ERİŞİLEBİLİRLİK

> Not: Chrome DevTools MCP bu oturumda kullanılabilir değildi; aşağıdakiler statik inceleme ve build çıktısına dayanır.

| # | Kontrol | Durum | Dosya:Sıra | Kanıt | Risk | Öneri |
|---|---|---|---|---|---|---|
| 5.1 | PWA manifest | **açık** | `index.html`, `dist/index.html` | `<link rel="manifest" ...>` yok; `manifest.json` dosyası yok | **KRİTİK** | Web/PWA için `manifest.json` ekle: `name`, `short_name`, `start_url`, `display`, `icons`, `theme_color`, `background_color`. Play Store TWAs ve Lighthouse audit için gerekli. |
| 5.2 | Meta theme-color | **açık** | `index.html:6` | `<meta name="theme-color">` yok | DÜŞÜK | `theme-color` meta tag'i ekle. |
| 5.3 | Viewport / safe area | **var** | `index.html:5` | `viewport-fit=cover` mevcut | DÜŞÜK | — |
| 5.4 | Font scaling / accessibility | **emin değilim** | — | Test edilmedi | ORTA | iOS Dinamik Type ve Android font boyutu artışlarında layout bozulmalarını test et. |
| 5.5 | Touch target boyutu | **emin değilim** | — | Test edilmedi | ORTA | Küçük icon/button'lara minimum 44x44 dp (iOS) / 48x48 dp (Android) dokunma alanı sağla. |
| 5.6 | Dark mode | **var** | `app.json:11`, `src/theme/theme.tsx` | `userInterfaceStyle: "automatic"` ve theme provider var | DÜŞÜK | Tüm ekranlarda hardcoded renkler olmadığını test et. |
| 5.7 | Splash screen | **var** | `resources/splash.png`, `resources/splash-dark.png`, iOS/Android splash asset'leri | Asset'ler mevcut | DÜŞÜK | Brand asset'leri yeniden üret; Capacitor 7 splash API'sini kullan. |
| 5.8 | Loading / empty / error states | **kısmen** | `src/components/ui/FeedbackState.tsx`, `SkeletonList.tsx` | Paylaşılan feedback componentleri var | ORTA | Her ekranın bu componentleri kullandığını ve boş/başarısız durumları ele aldığını doğrula. |
| 5.9 | Keyboard davranışı | **kısmen** | `AndroidManifest.xml:19`, `app.json:23` | `adjustResize` ve `resize` ayarları var | DÜŞÜK | iOS'ta keyboard avoiding view ve focus scroll testi yap. |
| 5.10 | Back navigation | **var** | `src/App.tsx:84-120` | Capacitor back button listener mevcut, `canGoBack` kontrolü var | DÜŞÜK | Modal/sheet/keyboard üzerinde back davranışını fiziksel cihazda test et. |

---

## 6. İÇERİK / LOCALIZATION

| # | Kontrol | Durum | Dosya:Sıra | Kanıt | Risk | Öneri |
|---|---|---|---|---|---|---|
| 6.1 | Inline string'ler | **açık** | `app/account.tsx:90-92`, `app/reset-password.tsx:18` vb. | Türkçe string'ler component içinde | ORTA | Çoklu dil desteği planlanıyorsa `i18n` kütüphanesi ekle, tüm kullanıcıya dönük metinleri translation key'lere taşı. |
| 6.2 | HTML lang attribute | **var** | `index.html:2` | `lang="tr"` | DÜŞÜK | — |
| 6.3 | Uzun metin uyumu | **emin değilim** | — | Test edilmedi | DÜŞÜK | İngilizce çevirilerde uzun metinlerin taşma yapıp yapmadığını kontrol et. |

---

## 7. BACKEND / API

| # | Kontrol | Durum | Dosya:Sıra | Kanıt | Risk | Öneri |
|---|---|---|---|---|---|---|
| 7.1 | API base URL fallback | **var** | `src/lib/backend-config.ts:11` | Eksikse hata fırlatıyor | DÜŞÜK | Build zamanı hatası alınmaması için CI/CD'de env değişkenlerini doğrula. |
| 7.2 | API timeout / retry | **emin değilim** | `src/lib/spring-client.ts:197` | Doğrudan `fetch` kullanılıyor, timeout yok | ORTA | `AbortController` ile timeout ekle; ağ hatalarında retry stratejisi tanımla. |
| 7.3 | Offline davranış | **kısmen** | `src/lib/spring-client.ts` | Özel offline cache yok | ORTA | Network hatalarında kullanıcıya anlamlı mesaj göster; kritik veriler için cache stratejisi düşün. |

---

## 8. ABONELİK / ÖDEME

| # | Kontrol | Durum | Dosya:Sıra | Kanıt | Risk | Öneri |
|---|---|---|---|---|---|---|
| 8.1 | In-app purchase altyapısı | **yok** | — | `iap`, `StoreKit`, `BillingClient`, `RevenueCat` vb. referans yok | DÜŞÜK | Uygulama şu an ücretsiz görünüyor. Eğer gelecekte premium/abonelik planlanıyorsa StoreKit 2 / Google Play Billing entegrasyonu ekle. |
| 8.2 | Paywall | **yok** | — | Paywall component'i yok | DÜŞÜK | Gerekli olacaksa store fiyat metadata'sından çekilen, restore purchase içeren paywall tasarla. |

---

## 9. ANALYTICS / CRASH REPORTING

| # | Kontrol | Durum | Dosya:Sıra | Kanıt | Risk | Öneri |
|---|---|---|---|---|---|---|
| 9.1 | Analytics entegrasyonu | **emin değilim** | — | Rapor kapsamında görülmedi | DÜŞÜK | Firebase Analytics, Mixpanel vb. entegre edilecekse PII ve token göndermeme kontrolü yap. |
| 9.2 | Crash reporting | **emin değilim** | — | Rapor kapsamında görülmedi | ORTA | Sentry/Firebase Crashlytics entegre et; token/PII maskele. |

---

## 10. TEST SONUÇLARI

| Test | Komut | Sonuç |
|---|---|---|
| Production build | `npm run build` | ✅ PASS (15.08s) |
| Type check | `npm run typecheck` | ❌ FAIL (19 hata) |
| Lint | yok | ⚠️ EKSİK |
| Unit test | yok | ⚠️ EKSİK |
| Android release build | yapılmadı | ⚠️ EKSİK |
| iOS release build | yapılmadı | ⚠️ EKSİK |
| Chrome DevTools runtime test | MCP kullanılamadı | ⚠️ EKSİK |

---

## 11. ÖNERİLEN SIRALI EYLEM PLANI

### P0 — Release blocker (çözülmeden mağazaya gönderme)

1. **Bundle ID birleştir**: Tüm konfigürasyonlarda (`capacitor.config.json`, `app.json`, `android/app/build.gradle`, `google-services.json`, iOS provisioning, app links) aynı ID'yi kullan.
2. **iOS `NSAllowsArbitraryLoads` kaldır**; ATS production kurallarına uy.
3. **Android cleartext kapat**: `capacitor.config.json`'dan `cleartext`/`allowMixedContent` kaldır, `network_security_config.xml` production IP'sini temizle.
4. **TypeScript hatalarını çöz** (`npm run typecheck` PASS olmalı).
5. **iOS Privacy Manifest (`PrivacyInfo.xcprivacy`) ekle**.
6. **PWA manifest.json ekle** (`index.html` ve `dist/`'e).
7. **iOS AppIcon setini tam boyutlu yap** (20+ boyut, iPad desteği isteğe bağlı ama telefon için gerekli setler).

### P1 — Release öncesi

8. Token depolamayı `AsyncStorage`'dan güvenli storage'a taşı.
9. `assetlinks.json` ve `apple-app-site-association`'ı gerçek değerlerle doldur, sunucuya koy.
10. Production loglarını `__DEV__` flag'i altına al veya kapat.
11. iOS `NS*UsageDescription` izin açıklamalarını ekle.
12. Release signing/keystore yapılandırmasını tamamla (Android + iOS).
13. Ekran görüntüleri, yaş derecelendirmesi, App Store/Play Store metadata alanlarını doldur.
14. Lint ve test komutlarını ekle; CI/CD pipeline'ında çalıştır.
15. Bundle boyutunu optimize et (onboarding PNG'leri, JS chunk splitting).
16. Proguard/R8'i release için test et.
17. `allowBackup` çelişkisini çöz.

### P2 — Release sonrası

18. i18n altyapısı kur.
19. Offline cache / retry stratejisi ekle.
20. Accessibility (font scaling, screen reader labels) testleri yap.
21. Analytics ve crash reporting entegrasyonu yap.
22. In-app purchase/paywall altyapısını planla (varsa).

---

## 12. EK: KRİTİK KOD PARÇALARI

### capacitor.config.json
```json
{
  "appId": "com.bialem.mobile",
  "appName": "BiAlem",
  "webDir": "dist",
  "server": {
    "hostname": "localhost",
    "androidScheme": "http",
    "cleartext": true
  },
  "android": {
    "allowMixedContent": true
  }
}
```

### iOS Info.plist ATS
```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
</dict>
```

### Android network_security_config.xml
```xml
<domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="false">localhost</domain>
    <domain includeSubdomains="false">191.215.36.29</domain>
</domain-config>
```

### AsyncStorage token
```ts
const TOKEN_KEY = "bialem_api_token";
getToken: () => AsyncStorage.getItem(TOKEN_KEY),
setToken: async (token) => {
  if (!token) await AsyncStorage.removeItem(TOKEN_KEY);
  else await AsyncStorage.setItem(TOKEN_KEY, token);
}
```

---

Raporu hazırlayan: `mobile-engineer` agent  
Yöntem: statik kod/build incelemesi (Chrome DevTools MCP kullanılamadı)
