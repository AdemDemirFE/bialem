import { spawnSync } from "node:child_process";
import { existsSync, writeFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const mobileDir = join(dirname(fileURLToPath(import.meta.url)), "..", "mobile");
const flagPath = join(mobileDir, ".capacitor-android-test");
const configPath = join(mobileDir, "capacitor.config.json");

const androidTest =
  process.argv.includes("--android-test") ||
  process.env.BIALEM_CAP_ENV === "android-test" ||
  existsSync(flagPath);

/** @type {Record<string, unknown>} */
const config = {
  appId: "com.bialem.mobile",
  appName: "BiAlem",
  webDir: "dist"
};

if (androidTest) {
  config.server = {
    hostname: "localhost",
    androidScheme: "http",
    cleartext: true
  };
  config.android = {
    allowMixedContent: true
  };
  console.log("[bialem] cap sync android (android-test: http://localhost + cleartext)");
} else {
  console.log("[bialem] cap sync android (production defaults: https://localhost)");
}

// Capacitor CLI cannot load ESM .ts/.js under "type":"module" (exports is not defined).
// Always emit JSON so `npx cap sync` works.
writeFileSync(configPath, `${JSON.stringify(config, null, 2)}\n`);

const env = { ...process.env };
if (androidTest) {
  env.BIALEM_CAP_ENV = "android-test";
} else {
  delete env.BIALEM_CAP_ENV;
}

const result = spawnSync(
  process.platform === "win32" ? "npx.cmd" : "npx",
  ["cap", "sync", "android"],
  { cwd: mobileDir, stdio: "inherit", env, shell: true }
);

process.exit(result.status ?? 1);
