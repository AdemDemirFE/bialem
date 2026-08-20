import { api } from "./api";

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

export async function registerPushToken(token: string, platform = "ANDROID") {
  await api.rest.post("/api/push-device-tokens", { token, platform });
}

export async function getNotifications(): Promise<AppNotification[]> {
  const data = await api.rest.get<AppNotification[]>("/api/notifications/inbox");
  return Array.isArray(data) ? data : [];
}

export async function getUnreadNotificationCount(): Promise<number> {
  const data = await api.rest.get<{ count: number }>("/api/notifications/unread-count");
  return data?.count ?? 0;
}

export async function markNotificationAsRead(id: number) {
  return api.rest.put<AppNotification>(`/api/notifications/${id}/read`);
}

export async function markAllNotificationsAsRead() {
  await api.rest.put("/api/notifications/read-all");
}

export async function sendTestNotification() {
  return api.rest.post<AppNotification>("/api/notifications/test");
}
