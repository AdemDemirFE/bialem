import { api } from "./api";
import { getApiBaseUrl } from "./backend-config";

export type AppNotification = {
  id: number;
  title: string;
  body: string | null;
  notificationType?: string;
  referenceId?: string | null;
  route?: string | null;
  read: boolean;
  createdAt: string;
};

function buildUrl(path: string): string {
  const base = getApiBaseUrl();
  if (path.startsWith("http")) return path;
  return base ? `${base}${path}` : path;
}

async function logAndRethrow(label: string, path: string, error: unknown) {
  const status = error && typeof error === "object" && "status" in error ? (error as { status?: number }).status : undefined;
  const body = error && typeof error === "object" && "body" in error ? (error as { body?: unknown }).body : undefined;
  const message = error instanceof Error ? error.message : String(error);
  console.warn(`[PUSH] ${label} failed`, {
    status,
    url: buildUrl(path),
    message,
    response: body
  });
  throw error;
}

export async function registerPushToken(token: string, platform = "ANDROID") {
  const path = "/api/push-device-tokens";
  try {
    console.log("[PUSH] sending token to backend", { url: buildUrl(path), tokenPreview: `${token.slice(0, 8)}...` });
    await api.rest.post(path, { token, platform });
    console.log("[PUSH] token registered successfully");
  } catch (error) {
    await logAndRethrow("token registration", path, error);
  }
}

export async function getNotifications(): Promise<AppNotification[]> {
  const path = "/api/notifications/inbox";
  try {
    const data = await api.rest.get<AppNotification[]>(path);
    return Array.isArray(data) ? data : [];
  } catch (error) {
    await logAndRethrow("get notifications", path, error);
    return [];
  }
}

export async function getUnreadNotificationCount(): Promise<number> {
  const path = "/api/notifications/unread-count";
  try {
    const data = await api.rest.get<{ count: number }>(path);
    return data?.count ?? 0;
  } catch (error) {
    await logAndRethrow("unread count", path, error);
    return 0;
  }
}

export async function markNotificationAsRead(id: number) {
  const path = `/api/notifications/${id}/read`;
  try {
    return await api.rest.put<AppNotification>(path);
  } catch (error) {
    await logAndRethrow("mark read", path, error);
    throw error;
  }
}

export async function markAllNotificationsAsRead() {
  const path = "/api/notifications/read-all";
  try {
    await api.rest.put(path);
  } catch (error) {
    await logAndRethrow("mark all read", path, error);
  }
}

export async function sendTestNotification() {
  const path = "/api/notifications/test";
  try {
    return await api.rest.post<AppNotification>(path);
  } catch (error) {
    await logAndRethrow("test notification", path, error);
    throw error;
  }
}
