# Yedekleme Tatbikati - 2 Agustos 2026

## Kapsam

- Kaynak: bagli production Supabase projesi
- PostgreSQL: 17.6
- Yedek klasoru: `backups/20260802-165133`
- Sifreli arsiv: `backups/20260802-165133.zip.enc`
- Tatbikat hedefleri:
  - production'dan bagimsiz gecici yerel Docker PostgreSQL
  - production disinda gecici Supabase staging projesi (`ttetagksomglkfuxpzjm`)

## Basarili Kontroller

- `roles.sql`, `schema.sql`, `data.sql` ve manifest olusturuldu.
- 4 Storage bucket ve 7 nesne indirildi; tum nesne SHA-256 degerleri dogrulandi.
- Arsiv AES-256-GCM ile sifrelendi, tekrar acildi ve SHA-256 degeri manifest ile birebir eslesti.
- Public sema geri yuklendi: 35 tablo, 90 fonksiyon, 74 RLS politikasi ve 45 tetikleyici.
- Public veri geri yuklendi: 35 `COPY` blogu hatasiz tamamlandi.
- Kritik tablo sayilari yedek ile geri yukleme arasinda birebir eslesti.
- Turkce profil metinleri UTF-8 byte seviyesinde dogrulandi.
- Tatbikat konteyneri kontrol sonrasi kaldirildi.
- Ayni yedek 2 Agustos 2026 tarihinde gecici Supabase staging projesine tek transaction ile geri yuklendi.
- Staging projesinde `auth.users` dahil 11 kritik tablo sayimi yedekle birebir eslesti.
- Staging projesinde 35/35 public tabloda RLS, 74 RLS politikasi ve 90 public fonksiyon dogrulandi.
- Storage'daki 4 bucket ve 7 nesne staging projesine yuklendi; her nesne yeniden indirilerek boyut ve SHA-256 degeriyle dogrulandi.
- Geri yuklenen test kullanicisi staging Auth servisine mevcut parolasiyla giris yapti; `profiles` tablosuna authenticated RLS uzerinden erisim ve guvenli cikis dogrulandi.
- Sifreli arsiv ile manifest fiziksel olarak ayri bir flash diske kopyalandi; iki kopyanin boyutlari ve SHA-256 degerleri kaynaklarla birebir eslesti.
- Ikinci kopya dogrulandiktan sonra `backups/20260802-165133` acik yedek klasoru kaldirildi; yalnizca sifreli arsiv ve manifest korundu.

## Kritik Sayimlar

| Tablo | Yedek | Geri yukleme |
| --- | ---: | ---: |
| profiles | 8 | 8 |
| communities | 27 | 27 |
| community_members | 52 | 52 |
| events | 1 | 1 |
| event_participants | 1 | 1 |
| follows | 9 | 9 |
| notifications | 34 | 34 |
| push_tokens | 7 | 7 |
| partner_venues | 1 | 1 |
| partner_offers | 1 | 1 |

## Bulgu

Tam veri dosyasi yerel Supabase PostgreSQL imajina uygulanirken `auth.audit_log_entries.ip_address` sutunu yerel Auth semasinda bulunmadigi icin Auth bolumu durdu. Bu, yedegin bozuk oldugunu degil, yerel Auth servis semasi ile canli projenin Auth sema surumlerinin farkli oldugunu gosterir. Public uygulama semasi ve verisi ayri olarak basariyla dogrulandi.

## Sonuc

Veritabani, Auth, authenticated RLS ve Storage geri yukleme tatbikati tamamlandi. Sifreli yedegin yerel ve fiziksel olarak ayri iki kopyasi dogrulandi; acik yedek verisi kaldirildi.

Production veritabanina tatbikat boyunca hicbir yazma veya restore islemi yapilmadi.
