export function normalizeImageUrl(value: string) {
  const normalized = value.trim();
  if (/^https?:\/\//i.test(normalized)) return normalized.replace(/^http:/i, "https:");
  return normalized;
}
