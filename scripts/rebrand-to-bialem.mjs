import { copyFileSync, existsSync, readFileSync, readdirSync, renameSync, statSync, writeFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");

const skipDirs = new Set(["node_modules", ".git", ".expo", ".next", "dist", "build"]);
const skipFiles = new Set(["package-lock.json"]);
const textExtensions = new Set([
  ".ts", ".tsx", ".js", ".mjs", ".json", ".md", ".bat", ".cmd", ".ps1", ".sql", ".template", ".example", ".xml", ".html", ".css"
]);

const replacements = [
  ["BİDÜNYA", "BİALEM"],
  ["Bidünya", "Bi'Alem"],
  ["BIDUNYA", "BIALEM"],
  ["Bidunya", "Bialem"],
  ["bidunya-logo", "bialem-logo"],
  ["withBidunyaAndroidTheme", "withBialemAndroidTheme"],
  ["bidunya_colors.xml", "bialem_colors.xml"],
  ["bidunya-assistant", "bialem-assistant"],
  ["bidunya-checkin", "bialem-checkin"],
  ["bidunya://", "bialem://"],
  ["bidunya.", "bialem."],
  ["bidunya:", "bialem:"],
  ["bidunya_", "bialem_"],
  ["--bidunya-", "--bialem-"],
  ["@color/bidunya_", "@color/bialem_"],
  ["com.bidunya.app", "com.bialem.app"],
  ["bidunya.app", "bialem.app"],
  ["destek@bialem.app", "destek@bialem.app"],
  ["noreply@bialem.app", "noreply@bialem.app"],
  ["bidunya-26b91", "bialem-app"],
  ["\"bidunya\"", "\"bialem\""],
  ["'bidunya'", "'bialem'"],
  ["projects/bidunya/", "projects/bialem/"],
  ["Bidunya Admin", "Bialem Admin"],
  ["Bidunya Mobile", "Bialem Mobile"],
  ["BIDUNYA -", "BIALEM -"],
  ["# Bidünya", "# Bi'Alem"],
  ["# Bidunya", "# Bialem"],
  ["name\": \"bidunya\"", "name\": \"bialem\""],
  ["bidunya-admin", "bialem-admin"],
  ["bidunya-mobile", "bialem-mobile"],
  ["BIDUNYA_STAGING", "BIALEM_STAGING"],
  ["generate-bidunya-", "generate-bialem-"],
  ["render-bidunya-", "render-bialem-"],
  ["BIDUNYA_ADVANTAGE", "BIALEM_ADVANTAGE"],
  ["0032_bidunya_advantage", "0032_bialem_advantage"]
];

function walk(dir, files = []) {
  for (const entry of readdirSync(dir)) {
    if (skipDirs.has(entry)) continue;
    const full = join(dir, entry);
    const stat = statSync(full);
    if (stat.isDirectory()) walk(full, files);
    else files.push(full);
  }
  return files;
}

function shouldProcess(file) {
  const base = file.split(/[/\\]/).pop() ?? "";
  if (skipFiles.has(base)) return false;
  const ext = base.includes(".") ? base.slice(base.lastIndexOf(".")) : "";
  if (!textExtensions.has(ext)) return false;
  if (base.endsWith(".png") || base.endsWith(".jpg")) return false;
  return true;
}

let changed = 0;
for (const file of walk(root)) {
  if (!shouldProcess(file)) continue;
  const original = readFileSync(file, "utf8");
  let next = original;
  for (const [from, to] of replacements) {
    next = next.split(from).join(to);
  }
  if (next !== original) {
    writeFileSync(file, next, "utf8");
    changed += 1;
    console.log("updated:", file.replace(root + "\\", "").replace(root + "/", ""));
  }
}

const renames = [
  ["bidunya.bat", "bialem.bat"],
  ["mobile/plugins/withBidunyaAndroidTheme.js", "mobile/plugins/withBialemAndroidTheme.js"],
  ["docs/BIDUNYA_ADVANTAGE_TR.md", "docs/BIALEM_ADVANTAGE_TR.md"],
  ["scripts/generate-bidunya-narration.ps1", "scripts/generate-bialem-narration.ps1"],
  ["scripts/render-bidunya-real-film.mjs", "scripts/render-bialem-real-film.mjs"],
  ["supabase/functions/bidunya-assistant", "supabase/functions/bialem-assistant"],
  ["supabase/migrations/0032_bidunya_advantage.sql", "supabase/migrations/0032_bialem_advantage.sql"],
  ["mobile/assets/bidunya-logo.png", "mobile/assets/bialem-logo.png"]
];

for (const [from, to] of renames) {
  const fromPath = join(root, from);
  const toPath = join(root, to);
  if (existsSync(fromPath) && !existsSync(toPath)) {
    renameSync(fromPath, toPath);
    console.log("renamed:", from, "->", to);
  } else if (existsSync(fromPath) && existsSync(toPath) && from.endsWith(".png")) {
    console.log("asset exists:", to);
  }
}

if (existsSync(join(root, "mobile/assets/bidunya-logo.png")) && !existsSync(join(root, "mobile/assets/bialem-logo.png"))) {
  copyFileSync(join(root, "mobile/assets/bidunya-logo.png"), join(root, "mobile/assets/bialem-logo.png"));
  console.log("copied logo to bialem-logo.png");
}

console.log(`\nDone. ${changed} files updated.`);
