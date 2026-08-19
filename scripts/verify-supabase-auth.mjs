import process from "node:process";
import { createClient } from "@supabase/supabase-js";

const targetProjectRef = process.env.TARGET_SUPABASE_PROJECT_REF;
const targetUrl = process.env.TARGET_SUPABASE_URL;
const publishableKey = process.env.TARGET_SUPABASE_PUBLISHABLE_KEY;
const email = process.env.TARGET_TEST_EMAIL;
const password = process.env.TARGET_TEST_PASSWORD;
const productionProjectRef = "tvaatpmlqlcnyjsvzlcy";

if (!targetProjectRef || !targetUrl || !publishableKey || !email || !password) {
  throw new Error("Staging Auth test bilgileri bulunamadi.");
}

if (targetProjectRef === productionProjectRef || targetUrl.includes(productionProjectRef)) {
  throw new Error("Guvenlik engeli: production Auth hedefinde test yapilamaz.");
}

const expectedUrl = `https://${targetProjectRef}.supabase.co`;
if (targetUrl !== expectedUrl) {
  throw new Error(`Guvenlik engeli: hedef URL ${expectedUrl} ile eslesmiyor.`);
}

const client = createClient(targetUrl, publishableKey, {
  auth: { autoRefreshToken: false, persistSession: false },
});

try {
  const { data: authData, error: authError } = await client.auth.signInWithPassword({
    email: email.trim().toLowerCase(),
    password,
  });

  if (authError || !authData.user || !authData.session) {
    throw new Error(`Staging kullanici girisi basarisiz: ${authError?.message ?? "oturum olusmadi"}`);
  }

  const { data: profile, error: profileError } = await client
    .from("profiles")
    .select("id, status, is_verified")
    .eq("id", authData.user.id)
    .single();

  if (profileError || !profile) {
    throw new Error(`Giris basarili ancak profil RLS dogrulamasi basarisiz: ${profileError?.message ?? "profil bulunamadi"}`);
  }

  console.log(`Staging girisi dogrulandi: profil durumu ${profile.status}, dogrulama ${profile.is_verified}.`);

  const { error: signOutError } = await client.auth.signOut();
  if (signOutError) {
    throw new Error(`Test oturumu kapatilamadi: ${signOutError.message}`);
  }

  console.log("Test oturumu guvenli sekilde kapatildi.");
} catch (error) {
  console.error(error instanceof Error ? error.message : String(error));
  process.exitCode = 1;
}
