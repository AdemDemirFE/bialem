import { getApiBaseUrl } from "./backend-config";

const PROXY_HOSTS = ["ticketm.net", "ticketmaster.com", "ticketmaster.com.tr", "ticketmaster.eu", "tmol.io"];

export function normalizeImageUrl(value: string) {
  const httpsUrl = value.trim().replace(/^http:/i, "https:");
  try {
    const host = new URL(httpsUrl).hostname.toLowerCase();
    if (PROXY_HOSTS.some((allowed) => host === allowed || host.endsWith(`.${allowed}`))) {
      return `${getApiBaseUrl()}/api/app/media-proxy?url=${encodeURIComponent(httpsUrl)}`;
    }
  } catch {
    return httpsUrl;
  }
  return httpsUrl;
}
