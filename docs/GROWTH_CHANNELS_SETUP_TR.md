# Büyüme Kanalları Kurulum Rehberi

Bu sürüm etkinlik afişi, web etkinlik sayfası, universal link, push bildirim, takvime ekleme, "Şimdi Ne Yapabilirim?" filtresi ve onur madalyalarını içerir.

## 1. Veritabanı

Supabase SQL Editor içinde `supabase/migrations/0017_growth_channels_and_honor_badges.sql` dosyasını çalıştırın. Migration şunları kurar:

- `push_tokens`: Kullanıcı cihazlarının Expo push tokenları.
- `get_public_event_share`: Giriş yapmadan görülebilen güvenli etkinlik özeti.
- `honor_badges` ve `user_honor_badges`: Otomatik kazanılan profil madalyaları.
- Yeni uygulama bildirimi oluştuğunda Expo Push API çağrısı.

## 2. Web Alan Adı

`mobile/.env` dosyasına üretim adresini ekleyin:

```env
EXPO_PUBLIC_WEB_URL=https://bialem.app
```

Farklı bir alan adı kullanacaksanız bu değeri ve `mobile/app.json` içindeki `associatedDomains` ile Android `host` değerini birlikte değiştirin.

## 3. Universal Link

EAS projesi oluşturulduktan sonra:

1. `mobile/public/.well-known/apple-app-site-association.template` içindeki `YOUR_APPLE_TEAM_ID` değerini Apple Developer Team ID ile değiştirin.
2. Dosya adındaki `.template` uzantısını kaldırın.
3. `mobile/public/.well-known/assetlinks.json.template` içindeki SHA-256 değerini EAS Android imza sertifikasıyla değiştirin.
4. Dosya adındaki `.template` uzantısını kaldırın.
5. Bu iki dosyayı `https://bialem.app/.well-known/` altında yönlendirmesiz ve `200` yanıtıyla yayınlayın.

## 4. Push Bildirim

Push tokenı için uygulamanın gerçek bir EAS `projectId` değerine ihtiyacı vardır:

```powershell
cd "C:\Users\Administrator\Desktop\bialem\mobile"
npx eas-cli init
```

EAS bu değeri uygulama yapılandırmasına ekler. Android'de uzak push bildirimi Expo Go ile çalışmaz; development build veya mağaza derlemesi kullanılmalıdır.

## 5. Madalya Kuralları

- Topluluk madalyaları aynı ana topluluk veya alt gruplarında 3 doğrulanmış katılımla kazanılır.
- `{city} Gurmesi`, kullanıcının profil şehrindeki 3 Gastronomi katılımıyla kazanılır.
- `{city} Elçisi`, kullanıcının profil şehrindeki 5 doğrulanmış katılımla kazanılır.
- Katılım, organizatör QR kontrolüyle `checked_in` olduğunda otomatik hesaplanır.
- Admin veya ilgili topluluk moderatörü `award_honor_badge` RPC'siyle gerekçeli ödül verebilir.

## 6. Instagram Afişi

Etkinlik detayındaki `Instagram` düğmesi 9:16 afiş stüdyosunu açar. Oluşturulan görsel QR koduyla halka açık etkinlik sayfasına gider. Telefon paylaşım menüsünden Instagram Hikayeleri veya gönderi seçilebilir.
