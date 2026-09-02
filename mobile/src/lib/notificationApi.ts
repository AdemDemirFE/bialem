import { api } from "./api";
import { getApiBaseUrl } from "./backend-config";

export type AppNotification = {
  id: number;
  title: string;
  body: string | null;
  notificationType?: string;
  type?: string;
  message?: string | null;
  recipientUserId?: number | null;
  actorUserId?: number | null;
  referenceType?: string | null;
  metadata?: string | null;
  referenceId?: string | null;
  route?: string | null;
  read: boolean;
  createdAt: string;
  readAt?: string | null;
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

type NotificationsResponse = PagedNotifications | AppNotification[];

function normalizeNotificationsResponse(
  data: NotificationsResponse | null | undefined,
  page: number,
  size: number
): PagedNotifications {
  if (Array.isArray(data)) {
    const hasNextPage = data.length === size;
    return {
      content: data,
      totalPages: hasNextPage ? page + 2 : page + 1,
      totalElements: page * size + data.length,
      size,
      number: page
    };
  }

  return {
    content: Array.isArray(data?.content) ? data.content : [],
    totalPages: typeof data?.totalPages === "number" ? data.totalPages : 0,
    totalElements: typeof data?.totalElements === "number" ? data.totalElements : 0,
    size: typeof data?.size === "number" ? data.size : size,
    number: typeof data?.number === "number" ? data.number : page
  };
}

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
    if (__DEV__) {
      const tokenPreview = token ? `${token.slice(0, 8)}...` : "(empty)";
      console.log("[PUSH] sending token to backend", { url: buildUrl(path), tokenPreview });
    }
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
  const token = typeof localStorage !== "undefined" ? localStorage.getItem("bialem.push.token") : null;
  const deviceUuid = typeof localStorage !== "undefined" ? localStorage.getItem("bialem.push.deviceUuid") : null;
  const params = new URLSearchParams();
  if (token) params.set("token", token);
  if (deviceUuid) params.set("deviceUuid", deviceUuid);
  const path = `/api/push-device-tokens/current?${params.toString()}`;
  try {
    await api.rest.delete(path);
    console.log("[PUSH] current device deactivated");
    if (typeof localStorage !== "undefined") localStorage.removeItem("bialem.push.token");
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
    const data = await api.rest.get<NotificationsResponse>(path);
    return normalizeNotificationsResponse(data, page, size);
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

export async function getNotificationDetail(id: number): Promise<AppNotification> {
  const path = `/api/app/notifications/${id}`;
  try {
    return await api.rest.get<AppNotification>(path);
  } catch (error) {
    await logAndRethrow("notification detail", path, error);
    throw error;
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
