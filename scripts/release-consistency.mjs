/**
 * Bialem release consistency check (Capacitor/Vite era).
 * Replaces the legacy Expo-only release-check.mjs assertions with checks that
 * match the current Vite + React Native Web + Capacitor + Spring architecture.
 * Exits non-zero on any failure, so it is safe to wire into `npm run check:release`.
 */
import { existsSync, readFileSync, statSync } from "node:fs";
import { execSync } from "node:child_process";
import { join } from "node:path";

const root = process.cwd();
const failures = [];
const checks = [];

function check(name, ok, detail = "") {
  checks.push({ name, ok, detail });
  if (!ok) failures.push(`${name}${detail ? `: ${detail}` : ""}`);
}

function read(relativePath) {
  return readFileSync(join(root, relativePath), "utf8");
}

function find(path, pattern) {
  return new RegExp(pattern).test(read(path));
}

// -- Mobile app identity ------------------------------------------------
const capacitor = JSON.parse(read("mobile/capacitor.config.json"));
check("capacitor appName (BiAlem)", capacitor.appName === "BiAlem", String(capacitor.appName));
check(
  "capacitor appId stable",
  typeof capacitor.appId === "string" && capacitor.appId.startsWith("com.bialem."),
  String(capacitor.appId)
);

// -- Android ------------------------------------------------------------
const manifest = read("mobile/android/app/src/main/AndroidManifest.xml");
check("Android allowBackup disabled", /allowBackup="false"/.test(manifest), "production backups must be off");
check("Android label uses @string", /android:label="@string\/app_name"/.test(manifest));

const strings = read("mobile/android/app/src/main/res/values/strings.xml");
check("Android app_name (BiAlem)", /<string name="app_name">BiAlem<\/string>/.test(strings));

// -- iOS ----------------------------------------------------------------
const infoPlist = read("mobile/ios/App/App/Info.plist");
check("iOS CFBundleDisplayName (BiAlem)", /<key>CFBundleDisplayName<\/key>\s*<string>BiAlem<\/string>/.test(infoPlist));
check(
  "iOS ATS not blanket arbitrary-loads",
  !/<key>NSAllowsArbitraryLoads<\/key>\s*<true\/>/.test(infoPlist),
  "store-safe ATS (domain-scoped) required"
);
check("iOS camera usage description", infoPlist.includes("NSCameraUsageDescription"));
check("iOS photo library usage description", infoPlist.includes("NSPhotoLibraryUsageDescription"));
check("iOS location usage description", infoPlist.includes("NSLocationWhenInUseUsageDescription"));
check("iOS notification usage description", infoPlist.includes("NSUserNotificationUsageDescription"));
check("iOS privacy manifest exists", existsSync(join(root, "mobile/ios/App/App/PrivacyInfo.xcprivacy")));

// -- PWA / web ----------------------------------------------------------
check("PWA manifest exists", existsSync(join(root, "mobile/public/manifest.json")));
const indexHtml = existsSync(join(root, "mobile/index.html")) ? read("mobile/index.html") : "";
check("index.html links manifest", /rel="manifest"/.test(indexHtml));
check("index.html has theme-color", /name="theme-color"/.test(indexHtml));

// -- Runnable scripts ---------------------------------------------------
const mobilePkg = JSON.parse(read("mobile/package.json"));
check("mobile typecheck script", Boolean(mobilePkg.scripts?.typecheck));
check("mobile prod build script", Boolean(mobilePkg.scripts?.["2:prod"]));
const adminPkg = JSON.parse(read("admin/package.json"));
check("admin prod build script", Boolean(adminPkg.scripts?.["2:prod"]));

// -- Secrets must not be tracked ----------------------------------------
// Tracked .env.* files are allowed only when they are pure URL/fallback
// config (Vite injects values from the canonical matrix). Any high-signal
// secret value in tracked files is a hard failure.
try {
  const tracked = execSync("git ls-files", { cwd: root, encoding: "utf8" }).split("\n").filter(Boolean);
  const secretPattern =
    /sb_secret_[A-Za-z0-9_]+|BEGIN (RSA |EC |OPENSSH )?PRIVATE KEY|sk_[A-Za-z0-9]{16,}|pk_live_[A-Za-z0-9]+|AKIA[0-9A-Z]{16}|aws_secret_access_key\s*[:=]|(.+\s*=\s*)(eyJ[A-Za-z0-9_-]{20,}\.[A-Za-z0-9_-]{20,})/;
  const BINARY_EXT = /\.(png|jpe?g|webp|gif|ico|zst|ttf|woff2?|eot|otf|svg|pdf|heic|gz|zip|db|so|dll|exe|apk|aab)$/i;
  const leaks = tracked.filter((file) => {
    if (BINARY_EXT.test(file)) return false;
    try {
      const { size } = statSync(join(root, file));
      if (size > 512 * 1024) return false;
      const content = readFileSync(join(root, file), "utf8");
      return secretPattern.test(content);
    } catch {
      return false;
    }
  });
  check("no high-signal secrets in tracked files", leaks.length === 0, leaks.slice(0, 5).join(", "));
} catch {
  check("git availability", false, "git ls-files failed");
}

const summary = {
  total: checks.length,
  failed: failures.length,
  failures
};
console.log(`\nBialem release consistency — ${checks.filter((c) => c.ok).length}/${checks.length} passed`);
for (const failure of failures) console.log(`  ⚠ ${failure}`);
process.exit(failures.length === 0 ? 0 : 1);