# Bialem Avantaj

Bialem Avantaj, üyelerin anlaşmalı kurumlarda kısa süreli ve tek kullanımlık QR kodla indirim kazanmasını sağlar. İlk pilot şehir Ankara'dır.

## Kurulum

1. Supabase SQL Editor'da sırasıyla uygulanmamış migration dosyalarını çalıştırın.
2. Bu özellik için son dosya `supabase/migrations/0032_bialem_advantage.sql` dosyasıdır.
3. Admin projesini yeniden Vercel'e yayınlayın.
4. Mobil uygulamanın yeni preview APK'sını ancak diğer değişiklikler de tamamlandığında oluşturun.

## Admin Akışı

Admin ekranı: `https://bialem.app/admin/advantages`

Yerel geliştirme: `http://localhost:3000/admin/advantages`

## Üye Akışı

1. Üye Keşfet ekranındaki `Bialem Avantaj` kartına girer.
2. Kasadayken QR kod üretir ve personel doğrular.

## Yayın Notu

Yeni EAS preview APK alınmalı ve gerçek cihazda test edilmelidir.
