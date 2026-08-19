# Otomatik Şehir Etkinlikleri Kurulumu

Bu sistem konser, tiyatro, gösteri ve API'de bulunan diğer şehir etkinliklerini belirli aralıklarla alır. Aynı etkinliği tekrar eklemek yerine mevcut kaydı günceller.

## Kullanılan kaynak

İlk adaptör Ticketmaster Discovery API'dir. Türkiye (`TR`) ve şehir filtresi desteklenir. Türkiye kapsamı sağlayıcıya göre değişebilir; özellikle bağımsız sinemalar ve yerel tiyatrolar için ileride resmî partner veri akışları eklenmelidir.

Etkinlik sitelerinin HTML sayfaları izinsiz kazınmaz. Biletinial, Biletix veya Bubilet için yalnızca resmî API ya da yazılı iş ortaklığı sağlandığında yeni adaptör eklenmelidir.

## 1. Veritabanını hazırlayın

Supabase SQL Editor'de sırasıyla çalıştırın:

1. `supabase/migrations/0019_city_radar_and_companions.sql`
2. `supabase/migrations/0020_automatic_city_event_sync.sql`
3. `supabase/migrations/0021_fix_city_event_sync_upsert.sql`
4. `supabase/migrations/0022_allow_short_city_event_titles.sql`

## 2. Ticketmaster anahtarı alın

1. `https://developer.ticketmaster.com/` adresinde geliştirici hesabı açın.
2. Bir uygulama oluşturun.
3. Uygulamanın `Consumer Key` değerini alın.

Bu anahtarı `mobile/.env` veya `admin/.env.local` içine yazmayın.

## 3. Supabase secrets ekleyin

Proje klasöründe PowerShell açın ve Supabase projenize giriş yaptıktan sonra çalıştırın:

```powershell
npx.cmd supabase login
npx.cmd supabase link --project-ref YOUR_PROJECT_ID
npx.cmd supabase secrets set TICKETMASTER_API_KEY="YOUR_TICKETMASTER_KEY"
npx.cmd supabase secrets set TICKETMASTER_CITIES="Ankara,İstanbul,İzmir"

$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$syncSecret = ($bytes | ForEach-Object { $_.ToString("x2") }) -join ""
$rng.Dispose()

npx.cmd supabase secrets set "EVENT_SYNC_SECRET=$syncSecret"
```

Bu komutlar güvenli bir `EVENT_SYNC_SECRET` üretir ve değeri aynı PowerShell oturumunda `$syncSecret` değişkeninde tutar. Test tamamlanana kadar bu pencereyi kapatmayın ve secret değerini ekran görüntüsüyle paylaşmayın.

## 4. Fonksiyonu yayınlayın

```powershell
npx.cmd supabase functions deploy sync-city-events --no-verify-jwt
```

JWT kontrolü kapalıdır çünkü fonksiyonu kullanıcı değil zamanlayıcı çağırır. Fonksiyon yine de `x-sync-secret` başlığını doğruladığı için herkese açık çalışmaz.

## 5. İlk testi yapın

PowerShell'de aşağıdaki isteği gönderin:

```powershell
$headers = @{ "x-sync-secret" = $syncSecret }
Invoke-RestMethod -Method Post -Uri "https://tvaatpmlqlcnyjsvzlcy.supabase.co/functions/v1/sync-city-events" -Headers $headers
```

`401 Onaylanmadı` alınırsa `$syncSecret` boşalmış veya Supabase'e farklı bir değer kaydedilmiştir. Yukarıdaki secret üretme ve `secrets set` adımlarını aynı PowerShell penceresinde yeniden çalıştırın.

Başarılı yanıtta `imported` sayısı görülür. Ticketmaster'da seçilen şehir için etkinlik yoksa bu sayı `0` olabilir; bu bir yazılım hatası değildir.

## 6. Altı saatte bir otomatik çalıştırın

Supabase Dashboard içinde:

1. `Integrations` bölümünden `Cron` ekranını açın.
2. Yeni HTTP job oluşturun.
3. Çalışma düzeni olarak `0 */6 * * *` girin.
4. URL olarak `https://YOUR_PROJECT_ID.supabase.co/functions/v1/sync-city-events` yazın.
5. Header ekleyin: `x-sync-secret: UZUN_RASTGELE_BIR_DEGER`.
6. Method olarak `POST` seçin ve kaydedin.

Supabase, Edge Function'ları `pg_cron` ve `pg_net` ile zamanlayabilir. Secret değerini mümkünse Supabase Vault/Cron güvenli header alanında tutun.

## Partner veri akışı biçimi

Yerel sinema, belediye veya organizatörden resmî JSON feed alındığında URL'leri `PARTNER_EVENT_FEED_URLS` secret değerine virgülle ayırarak ekleyebilirsiniz. Beklenen biçim:

```json
{
  "events": [
    {
      "id": "kurum-icin-benzersiz-id",
      "title": "Etkinlik adı",
      "description": "Açıklama",
      "category": "Sinema",
      "city": "Ankara",
      "venue_name": "Salon adı",
      "address_text": "Adres",
      "starts_at": "2026-08-01T20:00:00+03:00",
      "ends_at": "2026-08-01T22:00:00+03:00",
      "cover_image_url": "https://...",
      "price_label": "250 TL",
      "source_name": "Kurum adı",
      "source_url": "https://...",
      "ticket_url": "https://...",
      "status": "published"
    }
  ]
}
```

## Kontrol ve hata takibi

- Gelen kayıtlar `public.city_events` tablosuna yazılır.
- Her çalışma `public.city_event_sync_logs` tablosunda kaydedilir.
- API anahtarları ve ham hata ayrıntıları mobil kullanıcıya gösterilmez.
- Geçmiş etkinlikler Şehir Radarı RPC'si tarafından otomatik olarak görünmez hale gelir.
