# Bialem Ilk Hafta Operasyon Runbook'u

Bu belge production yayinindan once sorumlular ve alarm kanallariyla doldurulur. Parola, API key veya service-role anahtari bu dosyaya yazilmaz.

## Izleme Noktalari

- Web ve backend sagligi: `https://bialem.app/api/health`
- Vercel deployment ve function loglari
- Supabase Auth, Database, Storage ve Edge Function loglari
- Resend teslimat/bounce kayitlari
- Expo build ve push ticket/receipt kayitlari
- OpenAI, Supabase, Vercel ve Resend kullanim/maliyet ekranlari

Her deployment sonrasinda public smoke testi calistirin:

```powershell
npm.cmd run check:production
```

## Alarm Esikleri

- Health endpoint iki ardısık kontrolde `503` verirse yuksek oncelikli alarm.
- Kayit, giris, sifre sifirlama veya hesap silme tamamen durursa `SEV-1`.
- Yetki asimi, veri sizintisi veya yanlis hesaba push bildirimi `SEV-1`.
- Medya yukleme, etkinlik onayi veya bildirim teslimi kismen bozulursa `SEV-2`.
- Tek kullaniciyi etkileyen ve guvenlik riski olmayan hata `SEV-3`.

## Ilk Mudahale

1. Olay saatini, etkilenen surumu ve ilk belirtileri kaydedin.
2. Health endpoint, Vercel deployment ve Supabase status/loglarini kontrol edin.
3. Guvenlik olayiysa yeni deployment ve veri degisikligini durdurun; ilgili anahtari rotate edin.
4. Son saglam Git commit ve migration numarasini belirleyin; kanit olmadan veritabani rollback'i yapmayin.
5. Kullanici etkisini ve gecici cozum yolunu destek kanalinda yayinlayin.
6. Duzeltme sonrasi ilgili E2E akisini bastan sona tekrarlayin.

## Gunluk Kontrol

- `[ ]` Health endpoint ve alan adi TLS kontrolu
- `[ ]` Yeni auth/database/function hatalari
- `[ ]` E-posta bounce ve spam orani
- `[ ]` Push ticket/receipt hatalari ve pasiflestirilen tokenlar
- `[ ]` Acik raporlar ve hesap silme talepleri
- `[ ]` Supabase Storage/DB, OpenAI ve Vercel kullanim artisi
- `[ ]` Son deployment ile gercek cihaz smoke testi

## Yayin Geri Alma

- Web: Vercel'de son saglam deployment'i production'a promote edin.
- Mobil: Store rollout'u durdurun; onceki build'i kullananlar icin backend uyumlulugunu koruyun.
- Veritabani: Yalniz test edilmis forward-fix migration veya dogrulanmis backup restore plani kullanin.
- Anahtarlar: Sizinti durumunda once rotate edin, sonra Vercel/EAS/Supabase secretlarini guncelleyip yeniden deploy edin.
