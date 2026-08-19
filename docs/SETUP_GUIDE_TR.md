# Bialem - Kurulum Rehberi

## 1. Gereksinimler

- Node.js `24.x`
- npm `11.x`
- Expo Go veya iOS/Android simulator
- Supabase projesi

## 2. Ortam Değişkenleri

Bu bölüm, uygulamanın çalışırken ihtiyaç duyduğu ama kodun içine sabit yazılmaması gereken değerleri tanımlar.

Örnek:
- Supabase proje adresi
- Supabase public anon key
- İleride eklenecek harita, bildirim veya analiz servis anahtarları

Bu değerleri doğrudan kod içine yazmıyoruz. Bunun yerine `.env` dosyalarında tutuyoruz. Böylece:
- farklı ortamlar için farklı değer kullanılabilir
- gizli bilgiler repoya yanlışlıkla eklenmez
- yerel geliştirme ve canlı ortam ayrılır

### Bu projede neden iki ayrı env dosyası var?

Çünkü projede iki ayrı uygulama bulunuyor:
- `mobile/`: React Native + Expo mobil uygulaması
- `admin/`: Next.js yönetim paneli

Bu iki uygulama kendi çalışma mantığına göre farklı env dosyası okur.

### Mobil

`mobile/.env.example` dosyasını `mobile/.env` olarak oluşturun.

İçeriği:

```env
EXPO_PUBLIC_SUPABASE_URL=https://your-project.supabase.co
EXPO_PUBLIC_SUPABASE_ANON_KEY=your-anon-key
```

Bu alanların anlamı:
- `EXPO_PUBLIC_SUPABASE_URL`: Supabase projenizin ana URL adresi
- `EXPO_PUBLIC_SUPABASE_ANON_KEY`: Mobil uygulamanın Supabase'e bağlanırken kullanacağı public anahtar

Neden `EXPO_PUBLIC_` ile başlıyor:
- Expo, istemci tarafında kullanılmasına izin verilen değişkenleri bu önekle tanır
- Bu isimlendirme olmazsa uygulama içinde değeri okuyamazsınız

Bu değerler şu dosyada kullanılıyor:
- [mobile/src/lib/supabase.ts](../mobile/src/lib/supabase.ts)

Ayrıca şu dosyada `extra` alanına da bağlandı:
- [mobile/app.json](../mobile/app.json)

### Admin

`admin/.env.example` dosyasını `admin/.env.local` olarak oluşturun.

İçeriği:

```env
NEXT_PUBLIC_SUPABASE_URL=https://your-project.supabase.co
NEXT_PUBLIC_SUPABASE_ANON_KEY=your-anon-key
```

Bu alanların anlamı:
- `NEXT_PUBLIC_SUPABASE_URL`: Admin panelinin bağlanacağı Supabase proje adresi
- `NEXT_PUBLIC_SUPABASE_ANON_KEY`: Admin panelinin istemci tarafında kullanacağı public anahtar

Neden `NEXT_PUBLIC_` ile başlıyor:
- Next.js tarayıcı tarafında kullanılacak env değişkenlerini bu önekle dışarı açar
- Bu önek yoksa değişken sadece server tarafında kalır

Bu değerler şu dosyada kullanılıyor:
- [admin/src/lib/supabase.ts](../admin/src/lib/supabase.ts)

## 2.1 Bu değerler nereden alınır?

Supabase projesi oluşturduktan sonra:

1. Supabase dashboard'a girin
2. `Project Settings`
3. `API` bölümünü açın
4. Buradan şu bilgileri alın:

- `Project URL`
- `anon public key`

Bu iki değeri mobil ve admin env dosyalarına yerleştirirsiniz.

## 2.2 Hangi anahtar güvenli, hangisi değil?

Buradaki en önemli ayrım:

- `anon key`: istemci tarafında kullanılabilir
- `service_role key`: istemci tarafında kullanılmaz

### Güvenli olan
`anon key` mobil uygulama ve admin panelinde kullanılabilir.

Bu anahtar public kullanım içindir ama yine de tek başına sınırsız yetki vermez. Güvenliği sağlayan asıl katman:
- `RLS`
- kullanıcı oturumu
- tablo bazlı izinler

### Asla frontend'e koyulmaması gereken
`service_role key` kesinlikle şu alanlara yazılmaz:
- `mobile/.env`
- `admin/.env.local`

Çünkü bu anahtar çok yüksek yetkilidir. Yanlış yerde kullanılırsa veritabanında tam erişim riski doğurur.

Bu anahtar sadece ileride:
- güvenli backend servislerinde
- cron işlerinde
- admin-only server işlemlerinde
kullanılmalıdır.

## 2.3 `.env.example` neden var?

`.env.example` dosyaları gerçek gizli bilgi içermez.

Amaçları:
- projeyi açan kişiye hangi değişkenlerin gerektiğini göstermek
- ekip içinde standart sağlamak
- gerçek anahtarları repoya koymadan yapılandırmayı belgelemek

Yani:
- `mobile/.env.example` örnek şablondur
- `mobile/.env` gerçek yerel dosyadır
- `admin/.env.example` örnek şablondur
- `admin/.env.local` gerçek yerel dosyadır

Gerçek env dosyaları `.gitignore` içinde tutulduğu için git'e yanlışlıkla eklenmez.

## 2.4 Env dosyası oluşturma adımı

### Mobil için

1. [mobile/.env.example](../mobile/.env.example) dosyasını açın
2. Aynı klasörde `mobile/.env` dosyası oluşturun
3. Örnek değerleri gerçek Supabase bilgilerinizle değiştirin
4. Hızlı başlamak isterseniz [mobile/.env.template](../mobile/.env.template) dosyasını kopyalayabilirsiniz

Örnek:

```env
EXPO_PUBLIC_SUPABASE_URL=https://abc123.supabase.co
EXPO_PUBLIC_SUPABASE_ANON_KEY=eyJhbGciOi...
```

### Admin için

1. [admin/.env.example](../admin/.env.example) dosyasını açın
2. Aynı klasörde `admin/.env.local` dosyası oluşturun
3. Örnek değerleri gerçek Supabase bilgilerinizle değiştirin
4. Hızlı başlamak isterseniz [admin/.env.local.template](../admin/.env.local.template) dosyasını kopyalayabilirsiniz

Örnek:

```env
NEXT_PUBLIC_SUPABASE_URL=https://abc123.supabase.co
NEXT_PUBLIC_SUPABASE_ANON_KEY=eyJhbGciOi...
```

## 2.5 Sık yapılan hatalar

- `service_role key` kullanmak
- `EXPO_PUBLIC_` veya `NEXT_PUBLIC_` önekini kaldırmak
- `.env.example` dosyasını doldurup gerçek dosyayı oluşturmamak
- Env dosyasını oluşturduktan sonra geliştirme sunucusunu yeniden başlatmamak
- URL ile key değerlerini karıştırmak

## 2.6 Sonradan eklenecek env alanları

İleride şu değişkenler de eklenebilir:
- Firebase push notification ayarları
- harita servisi anahtarı
- analytics servis anahtarı
- dosya upload CDN ayarları

Bu durumda aynı mantık korunur:
- public istemci değişkenleri uygun önekle tanımlanır
- hassas server anahtarları frontend dosyalarına konmaz

## 2.7 Hazır kopyala-yapıştır şablonları

Supabase bilgilerinizi aldıktan sonra aşağıdaki iki dosyayı temel alabilirsiniz:

- [mobile/.env.template](../mobile/.env.template)
- [admin/.env.local.template](../admin/.env.local.template)

Yapmanız gereken tek şey:
- `YOUR_PROJECT_ID` kısmını kendi Supabase proje adresinizle değiştirmek
- `YOUR_SUPABASE_ANON_KEY` kısmını kendi `anon public key` değerinizle değiştirmek

Ardından dosyaları şu isimlerle kullanın:
- `mobile/.env`
- `admin/.env.local`

## 3. Bağımlılık Kurulumu

Kök dizinde:

```powershell
npm.cmd install
```

## 4. Mobil Uygulamayı Çalıştırma

```powershell
npm.cmd run dev:mobile
```

## 5. Admin Panelini Çalıştırma

```powershell
npm.cmd run dev:admin
```

## 6. Supabase Migration Mantığı

İlk migration dosyaları:

- `supabase/migrations/0001_init.sql`
- `supabase/migrations/0002_rls.sql`

Supabase CLI eklendiğinde sıradaki akış:

```powershell
supabase start
supabase db reset
```

## 7. Önerilen Sıradaki Geliştirme

1. Auth akışını bağla
2. Profil oluşturma ekranını tamamla
3. Topluluk listeleme ve detay ekranlarını ekle
4. Admin panelinde etkinlik onay ekranını aç
