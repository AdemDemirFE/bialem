#!/usr/bin/env node
/**
 * One-time maintainer task: mark deploy shell scripts executable in the Git index
 * so `./update.sh` works on Linux after clone (mode 100755).
 *
 * Run from repo root:  node scripts/mark-deploy-executable.mjs
 * Then commit the mode change:  git commit -am "chore: mark deploy scripts executable"
 */
import { spawnSync } from "node:child_process";
import { existsSync } from "node:fs";
import { join } from "node:path";

const root = join(import.meta.dirname, "..");
const files = [
  "deploy.sh",
  "update.sh",
  ...[
    "backup-db.sh",
    "build-backend-jar.sh",
    "common.sh",
    "deploy.sh",
    "free-disk.sh",
    "health-check.sh",
    "import-initial-data.sh",
    "logs.sh",
    "restart.sh",
    "restore-db.sh",
    "status.sh",
    "stop.sh",
    "update.sh"
  ].map((name) => join("deploy", "scripts", name))
];

for (const rel of files) {
  const path = join(root, rel);
  if (!existsSync(path)) {
    console.warn("skip (missing):", rel);
    continue;
  }
  const r = spawnSync("git", ["update-index", "--chmod=+x", rel], { cwd: root, stdio: "inherit" });
  if (r.status !== 0) process.exit(r.status ?? 1);
}

const ls = spawnSync("git", ["ls-files", "-s", "deploy.sh", "update.sh", "deploy/scripts/update.sh"], {
  cwd: root,
  encoding: "utf8"
});
console.log(ls.stdout);
console.log("Done. Commit with: git commit -am \"chore: mark deploy scripts executable\"");
