export function createURL(path: string) {
  return path;
}

export async function openURL(url: string) {
  if (typeof window !== "undefined") window.open(url, "_blank");
}

export function useURL() {
  return typeof window === "undefined" ? null : window.location.href;
}

export default { createURL, openURL };
