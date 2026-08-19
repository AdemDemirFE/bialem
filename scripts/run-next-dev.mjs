import { existsSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import { spawnSync } from "node:child_process";

const adminDir = join(dirname(fileURLToPath(import.meta.url)), "..", "admin");
const rootDir = join(adminDir, "..");
const nextBin = [
  join(adminDir, "node_modules/next/dist/bin/next"),
  join(rootDir, "node_modules/next/dist/bin/next")
].find(existsSync);

if (!nextBin) {
  console.error("HATA: next bulunamadi. bialem.bat > 1 veya 7 calistirin.");
  process.exit(1);
}

const args = process.argv.slice(2);
if (!args.length) args.push("dev");

const result = spawnSync(process.execPath, [nextBin, ...args], {
  cwd: adminDir,
  stdio: "inherit"
});

process.exit(result.status ?? 1);
