# Android Gercek Cihaz E2E Kontrolu

Bu kontrol, EAS preview APK gercek bir Android telefona kurulduktan sonra sirayla uygulanir. Her adim icin tarih, cihaz/Android surumu, test hesabi ve ekran goruntusu veya hata notu kaydedilir.

## Hazirlik

- Temiz kurulum yapin; eski Bialem uygulama verisini silin.
- Iki normal uye, bir topluluk moderatoru ve bir admin test hesabi kullanin.
- Wi-Fi ve mobil veriyle en az birer acilis testi yapin.
- Gercek kullanici verisi yerine kolay silinebilen test verisi olusturun.

## Kritik Akislar

1. `[x]` Uygulama acilir, splash sonrasinda bos/beyaz ekran veya crash olmaz. (25.07.2026 gercek Android cihaz testi gecti.)
2. `[x]` Yeni uye kaydolur, dogrulama e-postasi `noreply@bialem.app` adresinden gelir ve dogru uygulama/web rotasini acar. (25.07.2026 gercek Android cihaz testi gecti.)
3. `[x]` Cikis ve tekrar giris calisir; yanlis parola guvenli ve anlasilir hata verir. (26.07.2026 gercek Android cihaz testi gecti.)
4. `[x]` Parolami unuttum e-postasi gelir; `bialem.app/reset-password` acilir ve yeni parolayla giris yapilir. (26.07.2026 gercek Android cihaz testi gecti.)
5. `[x]` Profil adi, bio, sehir ve avatar guncellenir; baska kullanicinin profil sistem alanlari degistirilemez. Kisi arama, acik hesabi dogrudan takip etme, gizli hesaba istek gonderme, istegi kabul/reddetme ve takipten cikma akislari dogru calisir. (Profil ve RLS testi 26.07.2026; gizli hesap takip akisi 02.08.2026 gercek Android cihazda gecti.)
6. `[x]` Gonderi ve anlik gorseli yuklenir; izin reddedildiginde uygulama crash olmaz. Anlik hedefinde `Herkes`, yalniz `Takipcilerim`, yalniz birden fazla topluluk ve `Takipcilerim + birden fazla topluluk` secimleri ayri hesaplarla dogru gorunurlugu saglar. (2 Agustos 2026 gercek Android cihaz testi gecti.)
7. `[x]` Uye topluluga katilim istegi gonderir; moderator onayindan once gruplari ve topluluk icerigini goremez. Onaydan sonra erisim acilir.
8. `[x]` Moderator topluluk basvurusunu, grup ve etkinlik onay akisini tamamlar; normal uye ayni yetkili islemleri yapamaz. (3 Agustos 2026: normal uye etkinlik talebi olusturdu, moderator onayladi ve etkinlik yayinlandi.)
9. `[ ]` Etkinlik katilim talebi, onay, bekleme listesi ve iptal akislari dogru sayilari gosterir.
10. `[ ]` Onayli katilimci sohbeti acar; katilimci olmayan hesap sohbeti okuyamaz veya mesaj yazamaz.
11. `[ ]` Katilimci QR kodu olusur; moderator kamera izniyle tarar ve check-in yalniz bir kez uygulanir.
12. `[ ]` Etkinligi takvime ekleme ve `https://bialem.app/event-share/...` App Link'i uygulamadaki dogru etkinligi acar.
13. `[ ]` Bildirim listesi okunur/isaretlenir; push kurulunca bildirime dokunmak dogru ekrana yonlendirir.
14. `[ ]` Gonderi, yorum, etkinlik ve sohbet raporlari admin panelinde gorunur; raporlayan uye durum alanlarini degistiremez.
15. `[ ]` Hesap silme iki asamali onaydan sonra oturumu kapatir; silinen hesap tekrar erisemez ve bagli veriler beklenen sekilde temizlenir.
16. `[ ]` Etkinlik olusturma ve talep ekraninda tarih/saat yerel seciciyle belirlenir; mekan haritadan secilir ve kaydedilen konum etkinlik detayinda dogru gorunur.
17. `[ ]` Bialem Avantaj kampanyasi acilir; 60 saniyelik uye QR'i yetkili personel hesabiyla taranir, indirim kaydi olusur ve ayni kod ikinci kez kullanilamaz.
18. `[ ]` Push bildirimi uygulama acikken, arka plandayken ve tamamen kapaliyken gelir; dokunuldugunda ilgili ekrana yonlendirir.

## Cikis Kriteri

- Kritik adimlarin tamami gecmeden store build'i alinmaz.
- Crash, veri sizintisi, yetki asimi, hatali deep link veya hesap silme sorunu release blocker'dir.
- Duzeltme sonrasi yalniz hatali adim degil, ilgili akisin basindan sonuna regresyon testi tekrarlanir.
