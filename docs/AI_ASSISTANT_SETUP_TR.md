# Bialem Yapay Zeka Asistanı Kurulumu

Yapay zeka anahtarı mobil `.env` dosyasına kesinlikle eklenmez. Anahtar yalnızca Supabase Edge Function secret olarak saklanır.

## 1. Veritabanı migration'ını çalıştırın

Supabase SQL Editor içinde `supabase/migrations/0008_ai_assistant.sql` dosyasını çalıştırın. Bu migration kullanıcı başına saatte 20 mesaj sınırı oluşturur.

## 2. OpenAI API anahtarını oluşturun

OpenAI Platform API Keys sayfasından bir API anahtarı oluşturun. Anahtarı kaynak koduna, GitHub'a veya mobil `.env` dosyasına yazmayın.

## 3. Supabase secret ekleyin

Supabase Dashboard içinde Edge Functions > Secrets bölümünü açın ve şu değerleri ekleyin:

```text
OPENAI_API_KEY=oluşturduğunuz OpenAI API anahtarı
OPENAI_MODEL=gpt-5.4-mini
```

`SUPABASE_URL` ve `SUPABASE_ANON_KEY` Supabase Edge Functions ortamında otomatik sağlanır.

## 4. Edge Function'ı yayınlayın

Supabase CLI ile proje kökünde çalıştırın:

```powershell
npx supabase login
npx supabase link --project-ref YOUR_PROJECT_ID
npx supabase functions deploy bialem-assistant
```

`YOUR_PROJECT_ID`, Supabase Project URL içindeki `.supabase.co` öncesinde bulunan proje kimliğidir.

## 5. Test edin

Mobil uygulamayı yeniden başlatın, Asistan sekmesini açın ve örnek sorulardan birine dokunun. Function yayınlanmadıysa ekranda function bulunamadı hatası görülür.
