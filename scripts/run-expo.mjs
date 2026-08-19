import { spawnSync } from "node:child_process";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const mobileDir = join(dirname(fileURLToPath(import.meta.url)), "..", "mobile");
const args = process.argv.slice(2);
const npmArgs = args.includes("--web") || args[0] === "start" || args.length === 0 ? ["run", "dev"] : args;

const result = spawnSync("npm", npmArgs, {
  cwd: mobileDir,
  stdio: "inherit",
  shell: true
});

process.exit(result.status ?? 1);
