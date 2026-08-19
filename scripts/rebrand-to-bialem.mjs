import { existsSync, readFileSync, readdirSync, renameSync, statSync, writeFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");

const skipDirs = new Set(["node_modules", ".git", ".expo", ".next", "dist", "build", "target"]);
const skipFiles = new Set(["package-lock.json", "rebrand-to-bialem.mjs"]);
const textExtensions = new Set([
  ".ts", ".tsx", ".js", ".mjs", ".json", ".md", ".bat", ".cmd", ".ps1", ".sql", ".template", ".example", ".xml", ".html", ".css", ".java", ".yml", ".yaml", ".properties", ".gradle"
]);

const replacements = [
  ["BİDÜNYA", "BİALEM"],
  ["Bi Dünya", "Bi Alem"],
  ["bi dünya", "bi alem"],
  ["Bidünya", "Bialem"],
  ["bidünya", "bialem"],
  ["BIDUNYA", "BIALEM"],
  ["Bidunya", "Bialem"],
  ["bidunya", "bialem"],
  ["Bi’Dünya", "Bi Alem"],
  ["Bi'Dünya", "Bi Alem"],
  ["Bi'Alem", "Bi Alem"],
  ["BiAlem", "Bi Alem"]
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
  ["scripts/generate-bidunya-narration.ps1", "scripts/generate-bialem-narration.ps1"],
  ["scripts/render-bidunya-real-film.mjs", "scripts/render-bialem-real-film.mjs"],
  ["supabase/migrations/0032_bidunya_advantage.sql", "supabase/migrations/0032_bialem_advantage.sql"],
  ["docs/backend-migration/12_BIDUNYA_TO_BIALEM_AUDIT.md", "docs/backend-migration/12_BIALEM_REBRAND_AUDIT.md"],
  [".idea/bidunya.iml", ".idea/bialem.iml"]
];

for (const [from, to] of renames) {
  const fromPath = join(root, from);
  const toPath = join(root, to);
  if (existsSync(fromPath) && !existsSync(toPath)) {
    renameSync(fromPath, toPath);
    console.log("renamed:", from, "->", to);
  }
}

console.log(`\nDone. ${changed} files updated.`);
