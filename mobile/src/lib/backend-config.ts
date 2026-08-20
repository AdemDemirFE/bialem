function env(
  name: "VITE_API_BASE_URL" | "VITE_PUBLIC_WEB_URL" | "VITE_APP_VERSION" | "VITE_APP_ENV" | "VITE_APP_VERSION_CODE" | "VITE_DEV_API_PROXY"
) {
  const value = import.meta.env[name];
  return value && !String(value).includes("${") ? String(value) : undefined;
}

export function getApiBaseUrl() {
  if (env("VITE_DEV_API_PROXY") === "true") return "";
  return (env("VITE_API_BASE_URL") || "http://localhost:8080").replace(/\/+$/, "");
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
  return env("VITE_APP_ENV") || "dev";
}

export function usesSpringBackend() {
  return true;
}
