const fallbackWebUrl = "https://bialem.app";

export const publicWebUrl = (process.env.EXPO_PUBLIC_WEB_URL || fallbackWebUrl).replace(/\/$/, "");

export function eventPublicUrl(eventId: string) {
  return `${publicWebUrl}/event-share/${eventId}`;
}

export function eventDeepLink(eventId: string) {
  return `bialem://event/${eventId}`;
}
