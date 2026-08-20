import { spawnSync } from "node:child_process";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const mobileDir = join(dirname(fileURLToPath(import.meta.url)), "..", "mobile");
const androidTest = process.argv.includes("--android-test");

const env = { ...process.env };
if (androidTest) {
  env.BIALEM_CAP_ENV = "android-test";
  console.log("[bialem] cap sync android (android-test: http://localhost + cleartext)");
} else {
  delete env.BIALEM_CAP_ENV;
  console.log("[bialem] cap sync android (production defaults: https://localhost)");
}

const result = spawnSync(
  process.platform === "win32" ? "npx.cmd" : "npx",
  ["cap", "sync", "android"],
  { cwd: mobileDir, stdio: "inherit", env, shell: true }
);

process.exit(result.status ?? 1);
