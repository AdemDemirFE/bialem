import { useEffect, useSyncExternalStore } from "react";
import { getUnreadNotificationCount } from "./notificationApi";
import { addForegroundNotificationListener } from "./pushNotifications";

let count = 0;
let request: Promise<void> | null = null;
let pushBound = false;
const listeners = new Set<() => void>();
function emit() { listeners.forEach(listener => listener()); }
export function setUnreadCount(value: number) { count = Math.max(0, value); emit(); }
export function decrementUnreadCount() { setUnreadCount(count - 1); }
export async function refreshUnreadCount() {
  if (request) return request;
  request = getUnreadNotificationCount().then(setUnreadCount).finally(() => { request = null; });
  return request;
}
export function useUnreadNotificationCount() {
  useEffect(() => {
    void refreshUnreadCount();
    if (!pushBound) { pushBound = true; addForegroundNotificationListener(() => void refreshUnreadCount()); }
  }, []);
  return useSyncExternalStore(callback => { listeners.add(callback); return () => listeners.delete(callback); }, () => count, () => count);
}
