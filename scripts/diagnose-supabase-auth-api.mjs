import process from "node:process";
import { createClient } from "@supabase/supabase-js";

const targetProjectRef = process.env.TARGET_SUPABASE_PROJECT_REF;
const targetUrl = process.env.TARGET_SUPABASE_URL;
const secretKey = process.env.TARGET_SUPABASE_SECRET_KEY;
const email = process.env.TARGET_TEST_EMAIL;
const productionProjectRef = "tvaatpmlqlcnyjsvzlcy";

if (!targetProjectRef || !targetUrl || !secretKey || !email) {
  throw new Error("Staging Auth API tani bilgileri bulunamadi.");
}

if (targetProjectRef === productionProjectRef || targetUrl.includes(productionProjectRef)) {
  throw new Error("Guvenlik engeli: production Auth hedefinde tani calistirilamaz.");
}

const expectedUrl = `https://${targetProjectRef}.supabase.co`;
if (targetUrl !== expectedUrl) {
  throw new Error(`Guvenlik engeli: hedef URL ${expectedUrl} ile eslesmiyor.`);
}

const client = createClient(targetUrl, secretKey, {
  auth: {
    autoRefreshToken: false,
    persistSession: false,
    detectSessionInUrl: false,
  },
});

try {
  const settingsResponse = await fetch(`${targetUrl}/auth/v1/settings`, {
    headers: { apikey: secretKey },
  });

  if (!settingsResponse.ok) {
    throw new Error(`Auth ayarlari okunamadi: HTTP ${settingsResponse.status}`);
  }

  const settings = await settingsResponse.json();
  const emailProviderEnabled = settings?.external?.email === true;

  let page = 1;
  let userFound = false;
  const normalizedEmail = email.trim().toLowerCase();

  while (!userFound) {
    const { data, error } = await client.auth.admin.listUsers({ page, perPage: 1000 });
    if (error) {
      throw new Error(`Auth Admin API kullanici listesi okunamadi: ${error.message}`);
    }

    const users = data?.users ?? [];
    userFound = users.some((user) => user.email?.toLowerCase() === normalizedEmail);

    if (users.length < 1000) break;
    page += 1;
  }

  console.log(`email_provider_enabled = ${emailProviderEnabled}`);
  console.log(`admin_api_user_found = ${userFound}`);

  if (!emailProviderEnabled || !userFound) {
    process.exitCode = 2;
  }
} catch (error) {
  console.error(error instanceof Error ? error.message : String(error));
  process.exitCode = 1;
}
