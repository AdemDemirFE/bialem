# Bilet Karşılaştırma Sistemi

Bu sistem şehir etkinliklerinin doğrulanmış bilet satıcılarını, fiyat aralıklarını ve satın alma bağlantılarını aynı etkinlik detayında gösterir.

## Kurulum

Supabase SQL Editor'de `supabase/migrations/0023_city_event_ticket_offers.sql` dosyasını çalıştırın. Migration mevcut Ticketmaster kayıtlarından Biletix tekliflerini otomatik oluşturur. Sonraki şehir senkronizasyonlarında teklifler tetikleyiciyle güncellenir.

## Fiyat doğruluğu

- Sayısal fiyat yalnızca resmî sağlayıcı verisinde bulunduğunda gösterilir.
- Fiyat yoksa kullanıcıya `Güncel fiyatı satıcıda gör` denir; tahmin üretilmez.
- `En ucuz` rozeti yalnızca sayısal fiyatı bulunan teklifler arasında hesaplanır.
- Hizmet bedelinin dahil olduğu doğrulanmadıkça toplam ücret iddiasında bulunulmaz.
- Satın alma Bialem'da değil, satıcının resmî sayfasında tamamlanır.

## Yeni satıcı eklemek

Biletinial, Bubilet, Passo veya başka bir firma için resmî API/partner JSON akışı alındığında her teklif `public.city_event_ticket_offers` tablosuna yazılır. Aynı etkinliğin teklifleri aynı `city_event_id` altında tutulmalıdır. Mobil ekran yeni teklifleri kod değişikliği gerektirmeden en düşük sayısal fiyata göre sıralar.

Etkinlik sayfalarının HTML içeriği izinsiz kazınmamalıdır. Fiyat, stok ve bağlantı verileri yalnızca resmî API, affiliate feed veya yazılı partner entegrasyonundan alınmalıdır.
