# Bialem - Hizli Baslangic Verisi

Uygulamada topluluk ve etkinlik akisinin bos gelmemesi icin asagidaki test adimlarini kullanabilirsiniz.

## 1. Mobil Uygulamada Ilk Toplulugu Olusturun

Giris sonrasi ana ekranda:

- Topluluk adi girin
- Topluluk slug girin
- Aciklama yazin
- `Toplulugu Olustur` butonuna basin

Bu adimdan sonra topluluk listesi dolmaya baslar.

## 2. Etkinlik Talebi Gonderin

Ana ekrandan `Etkinlik Acma Talebi` ekranina gidin.

Burada:

- topluluk secin
- etkinlik basligi girin
- tarih ve konum ekleyin
- talebi gonderin

Kayit `pending_approval` durumunda tutulur.

## 3. Supabase Tarafinda Etkinligi Yayina Almak

Test icin Supabase Table Editor uzerinden:

- `events` tablosunu acin
- ilgili kaydi bulun
- `status` alanini `published` yapin

Boylece etkinlik mobil ana ekrandaki yaklasan etkinlikler listesine yayinda olarak duser.

## 4. Admin Panel Sonraki Adim

Gercek urun akisi icin sonraki is:

- `admin` panelinde bekleyen etkinlikler listesi
- `Onayla / Reddet` butonlari
- moderasyon aciklamasi alanlari
# Örnek Topluluk Verileri

`0011_community_groups.sql` çalıştırıldıktan sonra `0012_seed_example_communities.sql` çalıştırılır.
Bu seed dosyası 5 ana topluluk ve 14 alt grup oluşturur. Tekrar çalıştırılması aynı kayıtları çoğaltmaz.
