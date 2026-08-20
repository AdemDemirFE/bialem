import { existsSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import { spawnSync } from "node:child_process";
import { resolveEnvironment } from "./bialem-env.mjs";

const adminDir = join(dirname(fileURLToPath(import.meta.url)), "..", "admin");
const rootDir = join(adminDir, "..");
const nextBin = [
  join(adminDir, "node_modules/next/dist/bin/next"),
  join(rootDir, "node_modules/next/dist/bin/next")
].find(existsSync);

if (!nextBin) {
  console.error("HATA: next bulunamadi. Once root'ta npm install calistirin.");
  process.exit(1);
}

const args = process.argv.slice(2);
const modeFlagIndex = args.findIndex((a) => a === "--bialem-env");
let bialemMode = "dev";
if (modeFlagIndex >= 0) {
  bialemMode = args[modeFlagIndex + 1] || "dev";
  args.splice(modeFlagIndex, 2);
}
if (!args.length) args.push("dev");

const env = resolveEnvironment(bialemMode);
const childEnv = {
  ...process.env,
  NEXT_PUBLIC_API_BASE_URL: env.apiBaseUrl,
  NEXT_PUBLIC_APP_VERSION: env.version,
  NEXT_PUBLIC_APP_ENV: env.name
};

console.log(`[bialem-admin] env=${env.name} api=${env.apiBaseUrl} version=${env.version}`);

const result = spawnSync(process.execPath, [nextBin, ...args], {
  cwd: adminDir,
  stdio: "inherit",
  env: childEnv
});

process.exit(result.status ?? 1);
