import { existsSync, readFileSync, readdirSync } from "node:fs";
import { join } from "node:path";

const root = process.cwd();
const failures = [];
const warnings = [];

function requireFile(relativePath) {
  if (!existsSync(join(root, relativePath))) failures.push(`Eksik dosya: ${relativePath}`);
}

function requireValue(condition, message) {
  if (!condition) failures.push(message);
}

const appConfig = JSON.parse(readFileSync(join(root, "mobile/app.json"), "utf8")).expo;
const easConfig = JSON.parse(readFileSync(join(root, "mobile/eas.json"), "utf8"));
const mobilePackage = JSON.parse(readFileSync(join(root, "mobile/package.json"), "utf8"));
requireValue(appConfig.ios?.bundleIdentifier === "com.bialem.app", "iOS bundle identifier beklenen degerde degil.");
requireValue(appConfig.android?.package === "com.bialem.app", "Android package name beklenen degerde degil.");
requireValue(appConfig.scheme === "bialem", "Deep-link scheme 'bialem' olmali.");
requireValue(appConfig.extra?.eas?.projectId, "EAS projectId app config'e bagli degil.");
requireValue(Boolean(appConfig.icon), "Magaza ikonu app.json icinde tanimli degil.");
requireValue(Boolean(appConfig.android?.adaptiveIcon?.foregroundImage), "Android adaptive icon tanimli degil.");
requireValue(
  appConfig.plugins?.some((plugin) => Array.isArray(plugin) && plugin[0] === "expo-splash-screen"),
  "expo-splash-screen plugin'i tanimli degil."
);
requireValue(appConfig.ios?.supportsTablet === false, "iPad destegi kapali beta icin false olmali veya gercek iPad testleri tamamlanmali.");
requireValue(appConfig.android?.allowBackup === false, "Android uygulama veri yedegi production icin kapali olmali.");
requireValue(
  ["fingerprint", "appVersion"].includes(appConfig.runtimeVersion?.policy),
  "EAS Update icin runtimeVersion policy 'fingerprint' veya 'appVersion' olmali."
);
requireValue(
  appConfig.updates?.url === `https://u.expo.dev/${appConfig.extra?.eas?.projectId}`,
  "EAS Update URL, EAS projectId ile eslesmiyor."
);
requireValue(Boolean(mobilePackage.dependencies?.["expo-updates"]), "Mobil pakette expo-updates eksik.");
for (const profile of ["development", "preview", "production"]) {
  requireValue(
    easConfig.build?.[profile]?.channel === profile,
    `EAS '${profile}' profili '${profile}' kanalina bagli olmali.`
  );
}

for (const path of [
  "scripts/production-smoke.mjs",
  "scripts/backup-supabase.ps1",
  "scripts/fix-turkish-ui.mjs",
  "scripts/test-supabase-restore.ps1",
  "admin/app/privacy/page.tsx",
  "admin/app/account-deletion/page.tsx",
  "admin/app/reset-password/page.tsx",
  "admin/app/api/health/route.ts",
  "admin/app/event-share/[id]/page.tsx",
  "admin/app/terms/page.tsx",
  "admin/app/community-guidelines/page.tsx",
  "admin/app/kvkk/page.tsx",
  "mobile/eas.json",
  "mobile/plugins/withBlockedAndroidPermissions.js",
  "docs/LEGAL_CHECKLIST_TR.md",
  "docs/FIREBASE_PUSH_SETUP_TR.md",
  "docs/EAS_UPDATE_RUNBOOK_TR.md",
  "docs/SUPABASE_BACKUP_RESTORE_TR.md",
  "docs/OPERATIONS_RUNBOOK_TR.md"
]) requireFile(path);

if (!existsSync(join(root, "admin/public/.well-known/apple-app-site-association"))) {
  warnings.push("Apple AASA dosyasi EAS/Apple Team ID alindiktan sonra eklenmeli.");
}
if (!existsSync(join(root, "admin/public/.well-known/assetlinks.json"))) {
  warnings.push("Android assetlinks.json EAS imza SHA-256 degeri alindiktan sonra eklenmeli.");
}
if (!appConfig.android?.googleServicesFile) {
  warnings.push("Android FCM icin googleServicesFile ve EAS FCM v1 service account henuz yapilandirilmadi.");
}
requireValue(
  !appConfig.android?.permissions?.includes("android.permission.RECORD_AUDIO"),
  "Android RECORD_AUDIO izni acikca eklenmemeli."
);
for (const permission of [
  "android.permission.RECORD_AUDIO",
  "android.permission.READ_EXTERNAL_STORAGE",
  "android.permission.WRITE_EXTERNAL_STORAGE",
  "android.permission.SYSTEM_ALERT_WINDOW"
]) {
  requireValue(
    appConfig.android?.blockedPermissions?.includes(permission),
    `Android izni blockedPermissions icinde olmali: ${permission}`
  );
}

const mobileFiles = [
  "mobile/app.json",
  "mobile/.env.example",
  "mobile/.env.template",
  ...readdirSync(join(root, "mobile/app"), { recursive: true })
    .filter((file) => typeof file === "string" && /\.(ts|tsx|json)$/.test(file))
    .map((file) => join("mobile/app", file)),
  ...readdirSync(join(root, "mobile/src"), { recursive: true })
    .filter((file) => typeof file === "string" && /\.(ts|tsx|json)$/.test(file))
    .map((file) => join("mobile/src", file))
];

const forbiddenAsciiTurkishWords = [
  "Acik",
  "Bitis",
  "Cikis",
  "Dogrulama",
  "Etkinligi",
  "Gecerli",
  "Giris",
  "Gonder",
  "Gorunum",
  "Gunduz",
  "Hatali",
  "Henuz",
  "Kadikoy",
  "Katilim",
  "Kullanici",
  "Moderator",
  "Once",
  "Ornek",
  "Sifre",
  "Su",
  "Topluluga",
  "Yazilan",
  "Yardimcinin",
  "Yardimcilari",
  "BIALEM",
  "ETKINLIKLER",
  "ILGI",
  "Puani",
  "Akisa",
  "acilamadi",
  "acik",
  "aciklanacak",
  "alinamadi",
  "alani",
  "alanlari",
  "altina",
  "ayarlari",
  "baslangic",
  "bitis",
  "bulunamadi",
  "birakabilir",
  "cikis",
  "eklenmemis",
  "eklememis",
  "e-postasini",
  "fotograf",
  "giris",
  "gonder",
  "hatali",
  "hakkinda",
  "henuz",
  "katilmalisin",
  "kullanim",
  "kisa",
  "kapsami",
  "olmali",
  "olmalidir",
  "onaylanmis",
  "organizator",
  "puani",
  "puanlari",
  "sifre",
  "taslaklari",
  "tanitimi",
  "turleri",
  "dusunceni",
  "sec",
  "su",
  "surukleyin",
  "sonrasi",
  "yapin",
  "yazin",
  "yapip",
  "yapacaklarini",
  "yuruyusu"
];
const forbiddenAsciiTurkishPattern = new RegExp(
  `\\b(${forbiddenAsciiTurkishWords.join("|")})\\b`
);
const machineIdentifierFiles = new Set([
  "mobile\\src\\theme\\communityCovers.ts"
]);

for (const relativePath of mobileFiles) {
  const content = readFileSync(join(root, relativePath), "utf8");
  if (/sb_secret_|SUPABASE_SERVICE_ROLE_KEY|service_role/i.test(content)) {
    failures.push(`Mobil pakette sunucu anahtari izi bulundu: ${relativePath}`);
  }
  if (/En az 6 karakter/.test(content)) {
    failures.push(`Mobil kaynakta eski 6 karakter parola aciklamasi bulundu: ${relativePath}`);
  }
  if (/Ã|Ä|Å|Â|â€™|â€œ|â€|�/.test(content)) {
    failures.push(`Mobil kaynakta bozuk UTF-8 metni bulundu: ${relativePath}`);
  }
  if (!machineIdentifierFiles.has(relativePath) && forbiddenAsciiTurkishPattern.test(content)) {
    failures.push(`Mobil kaynakta Turkce karaktersiz kullanici metni bulundu: ${relativePath}`);
  }
}

const migrations = readdirSync(join(root, "supabase/migrations"))
  .filter((name) => /^\d{4}_.+\.sql$/.test(name))
  .sort();
requireValue(migrations.length >= 27, "Beklenen migration dosyalari eksik.");

for (const warning of warnings) console.warn(`UYARI: ${warning}`);
if (failures.length) {
  for (const failure of failures) console.error(`HATA: ${failure}`);
  process.exit(1);
}

console.log("Statik release kontrolleri basarili.");
