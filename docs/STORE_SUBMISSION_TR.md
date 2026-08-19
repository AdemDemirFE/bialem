# Bialem Magaza Gonderim Paketi

Guncelleme: 25 Temmuz 2026

Bu dosya Play Console ve App Store Connect alanlarini doldurmak icin calisma taslagidir. Veri guvenligi, App Privacy ve yas derecelendirmesi beyanlari son build ve hukuk kontrolu ile tekrar dogrulanmalidir.

## Ortak Bilgiler

- Uygulama adi: `Bialem`
- Paket / Bundle ID: `com.bialem.app`
- Birincil dil: Turkce
- Destek e-postasi: `destek@bialem.app`
- Destek URL: `https://bialem.app`
- Gizlilik politikasi: `https://bialem.app/privacy`
- Kullanim sartlari: `https://bialem.app/terms`
- Hesap silme: `https://bialem.app/account-deletion`
- Iletisim kategorisi: Sosyal / Social Networking
- Ikincil kategori adayi: Lifestyle
- Fiyat: Ucretsiz
- Reklam: Yok
- Uygulama ici satin alma: Yok

## Google Play Metinleri

### Kisa Aciklama

Toplulukları, etkinlikleri ve yeni insanları güvenle keşfet.

### Tam Aciklama

Bialem, aynı şehirde ortak ilgi alanlarına sahip insanları topluluklar ve etkinlikler etrafında bir araya getirir.

Ankara'daki pilot deneyimde şehir etkinliklerini keşfedebilir, ilgi alanına uygun topluluklara katılım isteği gönderebilir ve moderatör onayından sonra topluluk içeriğine erişebilirsin.

Bialem ile:

- Şehrindeki konser, atölye, sahne ve sosyal etkinlikleri keşfet.
- İlgi alanına uygun topluluk ve gruplara katıl.
- Yeni etkinlik önerileri gönder ve moderasyon sürecini takip et.
- Etkinlik katılımı, bekleme listesi ve QR giriş özelliklerini kullan.
- Aynı etkinliğe gitmek isteyen üyeleri gör.
- Onaylı katılımcılarla etkinlik sohbetine katıl.
- Anlaşmalı kurumların Bialem Avantaj kampanyalarını keşfet.
- Kısa süreli, tek kullanımlık QR kodla kurum avantajından yararlan.
- Uygunsuz içerikleri raporla ve hesabını uygulama içinden yönet.

Topluluk başvuruları ve içerikler, daha güvenli bir deneyim için yetkili moderatörler ve platform yöneticileri tarafından incelenebilir.

Bialem ilk olarak Ankara'da topluluk, etkinlik ve yerel iş birliklerini tek bir güvenli sosyal keşif deneyiminde buluşturur.

## App Store Metinleri

- Alt baslik: `Topluluk ve etkinlik keşfi`
- Promosyon metni: `Ankara'daki toplulukları, etkinlikleri ve Bialem Avantaj noktalarını keşfet; güvenli katılım ve QR deneyimiyle şehre karış.`
- Anahtar kelimeler: `topluluk,etkinlik,ankara,sosyal,arkadaşlık,atölye,konser,grup`
- Aciklama: Google Play tam aciklamasi kullanilabilir.
- Copyright: Sirket veya marka sahibi kesinlesince doldurulacak.
- Review notes: Moderator onayi, etkinlik katilimi, QR check-in ve Bialem Avantaj test adimlari ile demo hesap bilgileri eklenecek.

## Magaza Gorselleri

### Google Play

- 512 x 512 PNG uygulama ikonu, en fazla 1024 KB.
- 1024 x 500 JPEG veya alfa kanalsiz PNG feature graphic.
- Hazir ikon: `store-assets/google-play-icon-512.png` (512 x 512, alfa kanali yok).
- Hazir feature graphic: `store-assets/google-play-feature-graphic-1024x500.png` (1024 x 500, alfa kanali yok).
- Bu iki dosya `node scripts/generate-store-assets.mjs` komutuyla marka kaynaklarindan yeniden uretilebilir.
- Telefon icin en az 2, tercihen 5-8 gercek uygulama ekran goruntusu.
- Onerilen portre orani 9:16; ayni dildeki gorsellerde tutarli boyut kullanilmali.

### App Store

- iPhone icin 1-10 ekran goruntusu.
- En yuksek hedef boyut olarak 6.9 inc kabul edilen boyutlardan biri kullanilmali.
- iPad destegi kapali oldugu icin iPad ekran goruntusu gerekmiyor.
- Ekran goruntulerinde gercek uygulama arayuzu bulunmali; test e-postasi, telefon, QR kodu veya baska kisisel veri gorunmemeli.

### Ekran Sirasi

1. Kesfet: Sehir Radari ve Ankara etkinlikleri.
2. Topluluk: Moderator onayli topluluk ve grup deneyimi.
3. Etkinlik: Tarih, mekan, bilet ve katilim bilgileri.
4. Birlikte Git: Ayni etkinlige gitmek isteyen uye sayisi.
5. Bialem Avantaj: Anlasmali kurum ve kampanya detayi.
6. Profil: Kisisel veri icermeyen ornek hesap ozeti.

## Google Play Data Safety Taslagi

### Toplanan Veriler

| Veri grubu | Ornek | Amac | Kullaniciya bagli |
| --- | --- | --- | --- |
| Kisisel bilgiler | E-posta, ad, kullanici adi, sehir, bio | Hesap ve profil yonetimi | Evet |
| Kullanici kimlikleri | Supabase kullanici kimligi | Kimlik dogrulama ve guvenlik | Evet |
| Fotograf ve videolar | Avatar, gonderi ve hikaye medyasi | Kullanici icerigi | Evet |
| Kullanici icerigi | Gonderi, yorum, rapor, mesaj | Sosyal ve moderasyon ozellikleri | Evet |
| Konum | Izin verilirse mevcut konum; secilen etkinlik koordinati | Haritadan mekan secimi | Evet |
| Uygulama etkinligi | Topluluk, takip, etkinlik ve katilim kayitlari | Uygulama islevi ve guvenlik | Evet |
| Cihaz veya diger kimlikler | Expo push tokeni ve cihaz adi | Bildirim teslimi ve oturum guvenligi | Evet |

### Beyan Adaylari

- Veriler aktarim sirasinda sifrelenir: Evet, HTTPS/TLS.
- Kullanici veri silme talebi: Evet, uygulama ici ve web uzerinden.
- Reklam veya kullanicilar arasi takip: Hayir.
- Veri satisi: Hayir.
- Ucuncu taraf hizmetleri: Supabase, Expo/EAS, Firebase Cloud Messaging, Google Maps, Vercel ve Resend.
- "Paylasilan veri" secimleri, Google Play'in hizmet saglayici istisnasi ve imzalanan sozlesmeler kontrol edilerek kesinlestirilmelidir.

## Apple App Privacy Taslagi

Toplanabilecek ve kullaniciya baglanabilecek veri turleri:

- Contact Info: Email Address.
- User Content: Photos or Videos, Other User Content.
- Identifiers: User ID, Device ID veya push token karsiligi.
- Location: Precise Location, yalnizca kullanici mekan seciminde izin verirse.
- Usage Data: Product Interaction.
- Other Data: Moderasyon raporlari, etkinlik katilimi ve topluluk uyeligi.

Tracking: Uygulamada reklam veya capraz uygulama/site takibi yapan SDK bulunmadigi icin `No` adayi. Son build SDK listesiyle tekrar dogrulanmalidir.

## Test ve Gonderim Sirasi

1. Preview APK ile Android gercek cihaz E2E ve FCM testi.
2. Play Store icin production AAB.
3. Play Console uygulama kaydi, store listing ve App Content formlari.
4. Once internal test, sonra hesap turu gerektiriyorsa 12 test kullanicisi ile 14 gun kapali test.
5. Apple Developer uyeligi, signing, APNs ve Associated Domains.
6. iOS production build ve TestFlight.
7. App Store Connect metadata, App Privacy, review demo hesabi ve inceleme notlari.

## Resmi Kaynaklar

- Google Play uygulama kurulumu: https://support.google.com/googleplay/android-developer/answer/9859152
- Google Play test gereksinimi: https://support.google.com/googleplay/android-developer/answer/14151465
- Google Play Data Safety: https://support.google.com/googleplay/android-developer/answer/10787469
- Google Play gorselleri: https://support.google.com/googleplay/android-developer/answer/9866151
- Apple App Privacy: https://developer.apple.com/app-store/app-privacy-details/
- Apple ekran goruntuleri: https://developer.apple.com/help/app-store-connect/reference/app-information/screenshot-specifications/
- Apple gonderim akisi: https://developer.apple.com/help/app-store-connect/manage-submissions-to-app-review/submit-an-app/
