import { getPublicWebUrl } from "./backend-config";

export const publicWebUrl = getPublicWebUrl();

export function eventPublicUrl(eventId: string) {
  return `${publicWebUrl}/event-share/${eventId}`;
}

export function eventDeepLink(eventId: string) {
  return `bialem://event/${eventId}`;
}
