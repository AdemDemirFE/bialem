function env(name: string, fallback?: string) {
  const meta = (import.meta as { env?: Record<string, string> }).env ?? {};
  const value = meta[name] || (typeof process !== "undefined" ? process.env[name] : undefined);
  return value && !value.includes("${") ? value : fallback;
}

export function getApiBaseUrl() {
  return (env("VITE_API_BASE_URL") || "http://localhost:8080").replace(/\/+$/, "");
}

export function usesSpringBackend() {
  return true;
}
