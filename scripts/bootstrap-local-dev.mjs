import { copyFileSync, existsSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import { spawnSync } from "node:child_process";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");

function ensureAssets() {
  const required = [
    "mobile/assets/app-icon.png",
    "mobile/assets/bialem-logo.png",
    "mobile/assets/onboarding-worlds.png"
  ];
  if (required.every((file) => existsSync(join(root, file)))) return;

  if (process.platform === "win32") {
    const result = spawnSync("cmd.exe", ["/c", join(root, "scripts/bootstrap-mobile-assets.cmd")], {
      stdio: "inherit",
      cwd: root
    });
    if (result.status !== 0) throw new Error("Asset bootstrap basarisiz.");
  }
}

const envPairs = [
  ["mobile/.env.template", "mobile/.env"],
  ["admin/.env.local.template", "admin/.env.local"]
];

ensureAssets();

for (const [template, target] of envPairs) {
  const templatePath = join(root, template);
  const targetPath = join(root, target);
  if (existsSync(targetPath)) continue;
  if (!existsSync(templatePath)) {
    console.warn(`Sablon yok: ${template}`);
    continue;
  }
  copyFileSync(templatePath, targetPath);
  console.log(`Olusturuldu: ${target}`);
}

console.log("Bootstrap tamam.");