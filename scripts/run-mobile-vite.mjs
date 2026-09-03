import { spawnSync } from "node:child_process";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const mobileDir = join(dirname(fileURLToPath(import.meta.url)), "..", "mobile");
const args = process.argv.slice(2);
const proxy = args.includes("--proxy");
const modeArg = args.find((a) => a.startsWith("--mode="));
const mode = modeArg ? modeArg.slice("--mode=".length) : "development";

const env = { ...process.env };
if (proxy) env.BIALEM_DEV_PROXY = "1";

const result = spawnSync(
  process.platform === "win32" ? "npx.cmd" : "npx",
  ["vite", "--mode", mode],
  { cwd: mobileDir, stdio: "inherit", env, shell: true }
);

process.exit(result.status ?? 1);