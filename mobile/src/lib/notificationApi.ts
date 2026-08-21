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
  pushStatus?: string | null;
  pushSentAt?: string | null;
};

export type NotificationFilter = "ALL" | "READ" | "UNREAD";

export type PagedNotifications = {
  content: AppNotification[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
};

export type NotificationPreference = {
  id?: number;
  notificationType: string;
  inAppEnabled: boolean;
  pushEnabled: boolean;
  emailEnabled?: boolean;
  mandatory?: boolean;
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

export async function registerPushToken(
  token: string,
  platform = "ANDROID",
  extras?: { firebaseInstallationId?: string; deviceUuid?: string; appVersion?: string; notificationsEnabled?: boolean }
) {
  const path = "/api/push-device-tokens";
  try {
    const tokenPreview = token ? `${token.slice(0, 8)}...` : "(empty)";
    console.log("[PUSH] sending token to backend", { url: buildUrl(path), tokenPreview });
    await api.rest.post(path, {
      token,
      platform,
      firebaseInstallationId: extras?.firebaseInstallationId,
      deviceUuid: extras?.deviceUuid,
      appVersion: extras?.appVersion,
      notificationsEnabled: extras?.notificationsEnabled ?? true
    });
    console.log("[PUSH] token registered successfully");
  } catch (error) {
    await logAndRethrow("token registration", path, error);
  }
}

export async function deactivateCurrentPushDevice() {
  const path = "/api/push-device-tokens/current";
  try {
    await api.rest.delete(path);
    console.log("[PUSH] current device deactivated");
  } catch (error) {
    await logAndRethrow("device deactivation", path, error);
  }
}

export async function getNotifications(
  filter: NotificationFilter = "ALL",
  page = 0,
  size = 20
): Promise<PagedNotifications> {
  const path = `/api/app/notifications?filter=${encodeURIComponent(filter)}&page=${page}&size=${size}&sort=createdAt,desc`;
  try {
    const data = await api.rest.get<PagedNotifications>(path);
    return data ?? { content: [], totalPages: 0, totalElements: 0, size, number: page };
  } catch (error) {
    await logAndRethrow("get notifications", path, error);
    return { content: [], totalPages: 0, totalElements: 0, size, number: page };
  }
}

export async function getUnreadNotificationCount(): Promise<number> {
  const path = "/api/app/notifications/unread-count";
  try {
    const data = await api.rest.get<{ count: number }>(path);
    return data?.count ?? 0;
  } catch (error) {
    await logAndRethrow("unread count", path, error);
    return 0;
  }
}

export async function markNotificationAsRead(id: number) {
  const path = `/api/app/notifications/${id}/read`;
  try {
    return await api.rest.put<AppNotification>(path);
  } catch (error) {
    await logAndRethrow("mark read", path, error);
    throw error;
  }
}

export async function markAllNotificationsAsRead() {
  const path = "/api/app/notifications/read-all";
  try {
    await api.rest.put(path);
  } catch (error) {
    await logAndRethrow("mark all read", path, error);
  }
}

export async function sendTestNotification() {
  const path = "/api/app/notifications/test";
  try {
    return await api.rest.post<AppNotification>(path);
  } catch (error) {
    await logAndRethrow("test notification", path, error);
    throw error;
  }
}

export async function getNotificationPreferences(): Promise<NotificationPreference[]> {
  const path = "/api/app/notifications/preferences";
  try {
    const data = await api.rest.get<NotificationPreference[]>(path);
    return Array.isArray(data) ? data : [];
  } catch (error) {
    await logAndRethrow("get preferences", path, error);
    return [];
  }
}

export async function updateNotificationPreferences(preferences: NotificationPreference[]): Promise<NotificationPreference[]> {
  const path = "/api/app/notifications/preferences";
  try {
    const data = await api.rest.put<NotificationPreference[]>(path, preferences);
    return Array.isArray(data) ? data : [];
  } catch (error) {
    await logAndRethrow("update preferences", path, error);
    return [];
  }
}
