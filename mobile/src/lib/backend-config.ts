function env(
  name: "VITE_API_BASE_URL" | "VITE_PUBLIC_WEB_URL" | "VITE_APP_VERSION" | "VITE_APP_ENV" | "VITE_APP_VERSION_CODE" | "VITE_DEV_API_PROXY"
) {
  const value = import.meta.env[name];
  return value && !String(value).includes("${") ? String(value) : undefined;
}

export function getApiBaseUrl() {
  if (env("VITE_DEV_API_PROXY") === "true") return "";
  const value = env("VITE_API_BASE_URL");
  if (!value) throw new Error("VITE_API_BASE_URL is missing from this build");
  return value.replace(/\/+$/, "");
}

export function getPublicWebUrl() {
  return (env("VITE_PUBLIC_WEB_URL") || "https://bialem.app").replace(/\/+$/, "");
}

export function getAppVersion() {
  return env("VITE_APP_VERSION") || "0.0.0";
}

export function getAppVersionCode() {
  const raw = env("VITE_APP_VERSION_CODE");
  const n = raw ? Number(raw) : NaN;
  return Number.isFinite(n) ? n : 0;
}

export function getAppEnv() {
  const value = env("VITE_APP_ENV");
  if (!value) throw new Error("VITE_APP_ENV is missing from this build");
  return value;
}

export function usesSpringBackend() {
  return true;
}
