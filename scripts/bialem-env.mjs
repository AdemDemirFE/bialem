/**
 * Shared Bialem environment loader.
 * Single source of truth: root package.json → "version" + "bialem"
 */
import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");

/** @typedef {'dev' | 'prod' | 'android-test'} BialemEnvName */

/**
 * @returns {{
 *   version: string,
 *   versionCode: number,
 *   environments: Record<BialemEnvName, { apiBaseUrl: string, publicWebUrl: string }>
 * }}
 */
export function loadBialemConfig() {
  const pkg = JSON.parse(readFileSync(join(root, "package.json"), "utf8"));
  const bialem = pkg.bialem;
  if (!bialem?.environments?.dev || !bialem?.environments?.prod) {
    throw new Error("package.json missing bialem.environments.dev/prod");
  }
  return {
    version: String(pkg.version || "0.0.0"),
    versionCode: Number(bialem.versionCode || 1),
    environments: bialem.environments
  };
}

/**
 * Map Vite/Next mode → bialem env name
 * @param {string} mode
 * @returns {BialemEnvName}
 */
export function resolveEnvName(mode) {
  if (mode === "production" || mode === "prod") return "prod";
  if (mode === "android-test") return "android-test";
  return "dev";
}

function nonEmpty(value) {
  if (value == null) return undefined;
  const s = String(value).trim();
  return s.length ? s : undefined;
}

/**
 * @param {string} mode
 */
export function resolveEnvironment(mode) {
  const config = loadBialemConfig();
  const name = resolveEnvName(mode);
  const env = config.environments[name];
  if (!env?.apiBaseUrl) {
    throw new Error(`Unknown or incomplete bialem environment: ${name}`);
  }
  const apiBaseUrl = String(env.apiBaseUrl);
  const publicWebUrl = String(env.publicWebUrl || "");
  // Repeated values are accepted, but a mode-specific env file/build arg may
  // never silently select a different backend than the canonical matrix.
  for (const [key, value] of [
    ["VITE_API_BASE_URL", nonEmpty(process.env.VITE_API_BASE_URL)],
    ["NEXT_PUBLIC_API_BASE_URL", nonEmpty(process.env.NEXT_PUBLIC_API_BASE_URL)]
  ]) {
    if (value && value.replace(/\/+$/, "") !== apiBaseUrl.replace(/\/+$/, "")) {
      throw new Error(`${key} conflicts with canonical ${name} API: ${apiBaseUrl}`);
    }
  }
  const configuredPublicUrl = nonEmpty(process.env.VITE_PUBLIC_WEB_URL);
  if (configuredPublicUrl && configuredPublicUrl.replace(/\/+$/, "") !== publicWebUrl.replace(/\/+$/, "")) {
    throw new Error(`VITE_PUBLIC_WEB_URL conflicts with canonical ${name} web URL: ${publicWebUrl}`);
  }
  return {
    name,
    version: config.version,
    versionCode: config.versionCode,
    apiBaseUrl: apiBaseUrl.replace(/\/+$/, ""),
    publicWebUrl: publicWebUrl.replace(/\/+$/, "")
  };
}

export function getRootDir() {
  return root;
}