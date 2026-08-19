# Supabase doğrulama e-postası kurulumu

## Mevcut durum

Projede yeni kullanıcı kaydı açık ve e-posta doğrulaması zorunludur. Mobil uygulamadaki `signUp` çağrısı da doğrudur.

Supabase'in varsayılan e-posta servisi üretim amaçlı değildir. Bu servis yalnızca Supabase projesinin ekip üyelerine ait, önceden yetkilendirilmiş adreslere e-posta gönderebilir. Normal test kullanıcılarına doğrulama e-postası ulaştırmak için özel SMTP kurulmalıdır.

## Yapılacaklar

1. Bir e-posta sağlayıcısında hesap açın. Resend, Brevo, Postmark, SendGrid veya Amazon SES kullanılabilir.
2. Gönderici alan adını sağlayıcıda doğrulayın. Önerilen adres: `noreply@bialem.app`.
3. Supabase Dashboard'da `Project Settings > Authentication > SMTP Settings` bölümünü açın.
4. `Enable Custom SMTP` seçeneğini açın.
5. Sağlayıcının verdiği host, port, kullanıcı adı ve şifreyi girin.
6. Sender name alanına `Bialem`, sender email alanına doğrulanmış e-posta adresini yazın.
7. `Authentication > Email Templates > Confirm signup` şablonunu kontrol edin.
8. `Authentication > URL Configuration` bölümünde geçerli Site URL ve yönlendirme adreslerini tanımlayın.
9. Yeni ve daha önce kullanılmamış bir e-posta adresiyle kayıt testi yapın.

Şifre yenileme bağlantısının uygulamaya dönebilmesi için Redirect URLs listesine şu adresleri de ekleyin:

- Üretim e-posta yönlendirmesi: `https://bialem.app/reset-password`
- Uygulamaya son geçiş için özel şema: `bialem://reset-password`
- Yerel web testi: `http://localhost:8081/reset-password`

Production EAS ortamında `EXPO_PUBLIC_AUTH_REDIRECT_URL` değeri
`https://bialem.app/reset-password` olmalıdır. E-posta uygulamalarının doğrudan
özel şemaları engellememesi için sıfırlama e-postası önce bu HTTPS köprüsünü açar;
köprü daha sonra kullanıcıyı `bialem://reset-password` ile mobil uygulamaya taşır.

Supabase `Reset password` e-posta şablonunda bağlantı doğrudan uygulamanın güvenli
web köprüsüne verilmelidir:

```html
<a href="https://bialem.app/reset-password?token_hash={{ .TokenHash }}&type=recovery">
  Şifremi yenile
</a>
```

Mobil uygulama `token_hash`, PKCE `code` ve eski erişim/yenileme token biçimlerini
destekler. Böylece bağlantı Supabase doğrulama alan adında tarayıcı uyarısı
oluşturmadan açılır.

## Hata kontrolü

Supabase Dashboard'da `Logs > Auth` bölümünü açın ve kayıt denemesinin saatindeki kaydı inceleyin.

- `Email address not authorized`: Özel SMTP kurulmamış ve alıcı proje ekibinde değil.
- `Email rate limit exceeded`: Gönderim limiti aşılmış; bekleyip yeniden deneyin.
- SMTP/connection hatası: Host, port, kullanıcı adı, şifre veya gönderici doğrulaması hatalı.
- Kayıt başarılı fakat e-posta yok: Spam klasörünü ve sağlayıcının gönderim kayıtlarını kontrol edin.

Uygulamadaki “Doğrulama e-postasını tekrar gönder” düğmesi ancak SMTP kurulumu doğruysa teslimat sağlayabilir.
