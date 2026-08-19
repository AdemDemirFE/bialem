export function getApiBaseUrl(): string | undefined {
  const raw = process.env.NEXT_PUBLIC_API_BASE_URL;
  if (!raw) {
    return undefined;
  }

  return raw.replace(/\/+$/, "");
}

export function usesSpringBackend(): boolean {
  return Boolean(getApiBaseUrl());
}
