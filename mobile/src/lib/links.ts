const fallbackWebUrl = "https://bialem.app";

function vitePublicWebUrl() {
  const meta = (import.meta as { env?: Record<string, string> }).env ?? {};
  return meta.VITE_PUBLIC_WEB_URL;
}

export const publicWebUrl = (vitePublicWebUrl() || fallbackWebUrl).replace(/\/$/, "");

export function eventPublicUrl(eventId: string) {
  return `${publicWebUrl}/event-share/${eventId}`;
}

export function eventDeepLink(eventId: string) {
  return `bialem://event/${eventId}`;
}
