# Supabase Yedekleme ve Geri Yukleme Plani

## Hedef

- Veritabani icin en fazla 24 saatlik veri kaybi toleransi.
- Her ay en az bir staging geri yukleme testi.
- Production projesine dogrudan deneme restore'u yapilmamasi.
- Yedeklerin Git deposuna veya herkese acik depolamaya konulmamasi.

## Platform Yedegi

Supabase Pro, Team ve Enterprise projelerinde gunluk platform yedekleri bulunur. Free planda duzenli CLI yedegi zorunludur. Dashboard'da `Database > Backups` ekranindan mevcut saklama suresi kontrol edilmelidir.

PITR saniye seviyesinde geri donus saglar fakat ek ucretlidir. Pilot donemde gunluk yedek yeterli gorulurse once Pro gunluk yedekleri ve haftalik harici mantiksal yedek kullanilabilir.

## Haftalik Mantiksal Veritabani Yedegi

1. Proje Supabase CLI ile bagliysa kalici veritabani parolasi kullanmadan yedegi alin:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/backup-supabase.ps1 -Linked
```

2. Bagli CLI kullanilamiyorsa Supabase Dashboard > Connect ekranindan Session pooler baglanti URL'sini alin.
3. Parolayi URL icinde yuzde kodlamasi gerektiren karakterler varsa encode edin ve yalnizca acik terminal oturumu icin ayarlayin:

```powershell
$env:SUPABASE_DB_URL = "postgresql://..."
powershell -ExecutionPolicy Bypass -File scripts/backup-supabase.ps1
```

4. Supabase CLI dump islemi icin Docker Desktop'in calisiyor olmasi gerekir.

Komut `backups/<tarih-saat>/` altinda `roles.sql`, `schema.sql`, `data.sql` ve SHA-256 manifesti olusturur. `backups/` Git tarafindan yok sayilir.

5. Storage nesnelerini ve bucket ayarlarini ayni yedek klasorune alin:

```powershell
node scripts/backup-supabase-storage.mjs backups/<tarih-saat>
```

6. En az 20 karakterlik benzersiz parolayi yalnizca terminal oturumuna tanimlayin. Parolayi Git'e, mesaja veya yedek klasorune yazmayin:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/protect-supabase-backup-interactive.ps1 `
  -BackupDirectory "backups/<tarih-saat>"
```

Komut AES-256-GCM sifreli `.zip.enc` arsivi olusturur, arsivi tekrar acar ve SHA-256 ile dogrular. Sifreli kopya erisim kontrollu ikinci bir konuma tasindiktan sonra acik yedek klasoru guvenli sekilde silinmelidir.

## Storage Yedegi

Supabase veritabani yedekleri Storage tablosundaki metadata'yi kapsar, ancak gercek resim ve video dosyalarini kapsamaz. `post-media`, `stories` ve diger kullanici dosyasi bucket'lari ayrica nesne depolamaya kopyalanmalidir.

Ilk yayin icin haftalik Storage arsivi yeterlidir. `backup-supabase-storage.mjs` tum bucket'lari sayfali ve klasorleri yinelemeli olarak indirir; her nesnenin SHA-256 degerini `storage-manifest.json` icine yazar. Medya hacmi buyudugunde S3 uyumlu ikinci depoya zamanlanmis kopyalama kurulmalidir. Storage yedegi alinmadan yedek gorevi tamamlanmis sayilmaz.

## Aylik Geri Yukleme Tatbikati

1. Bos ve production disinda bir Supabase staging projesi olusturun.
2. Staging projesinde gerekli eklentileri ve Database Webhooks ozelligini etkinlestirin.
3. PostgreSQL `psql` istemcisini kurun.
4. Staging baglanti URL'sini kullanarak:

```powershell
$env:RESTORE_TEST_CONFIRM = "BIALEM_STAGING_RESTORE"
powershell -ExecutionPolicy Bypass -File scripts/test-supabase-restore.ps1 `
  -BackupDirectory "backups/20260723-120000" `
  -TargetDbUrl "postgresql://STAGING..."
```

Script production proje referansina restore etmeyi reddeder.

Yerel Docker testi public uygulama semasi ve verisi icin hizli bir ilk kanittir; ancak Supabase Auth servis semasi surumleri farkli olabilir. `auth` veya `storage` sistem semalarinda sutun uyumsuzlugu gorulurse tabloyu elle degistirerek sonucu zorlamayin. Tam tatbikati bos bir Supabase staging projesinde, canli projeyle uyumlu servis semasi uzerinde tamamlayin.

## Restore Sonrasi Kontrol

- `auth.users`, `profiles`, `communities`, `events`, `notifications` ve `push_tokens` sayilari karsilastirilir.
- RLS politikalarinin aktif oldugu dogrulanir.
- Kayit, giris, topluluk basvurusu ve etkinlik goruntuleme staging ortaminda test edilir.
- Realtime publication ve Database Webhooks yeniden kontrol edilir.
- Storage dosyalari ayri arsivden staging bucket'larina yuklenerek ornek medya URL'leri acilir.
- Tatbikat tarihi, sure, hata ve duzeltmeler operasyon kaydina yazilir.

## Production Geri Donus Karari

Production restore kesinti olusturur. Once olay saati ve hedef geri donus noktasi belirlenir, yeni yazma islemleri durdurulur ve en yakin saglam yedek secilir. Mumkunse once staging restore ile yedek dogrulanir. Kanit olmadan production restore baslatilmaz.
