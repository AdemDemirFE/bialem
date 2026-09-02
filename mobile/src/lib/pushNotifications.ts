import { Capacitor } from "@capacitor/core";
import { registerPushToken as registerPushTokenApi } from "./notificationApi";
import { router } from "./router";

let listenersRegistered = false;
let initInProgress = false;
let pendingRoute: string | null = null;
const foregroundListeners = new Set<() => void>();

export function addForegroundNotificationListener(callback: () => void) {
  foregroundListeners.add(callback);
  return () => {
    foregroundListeners.delete(callback);
  };
}

export async function initializePushNotifications() {
  if (!Capacitor.isNativePlatform()) {
    console.log("[PUSH] initialization skipped: not a native platform");
    return;
  }

  if (initInProgress) {
    console.log("[PUSH] initialization already in progress");
    return;
  }
  initInProgress = true;

  try {
    console.log("[PUSH] initialization started");
    const { PushNotifications } = await import("@capacitor/push-notifications");
    if (Capacitor.getPlatform() === "android") {
      await PushNotifications.createChannel({
        id: "bialem_notifications",
        name: "Bialem bildirimleri",
        description: "Etkinlik, topluluk ve hesap bildirimleri",
        importance: 5,
        visibility: 1,
        sound: "default",
        vibration: true
      });
    }
    registerPushListenersOnce(PushNotifications);

    const result = await PushNotifications.requestPermissions();
    console.log("[PUSH] permission result", { receive: result.receive });
    if (result.receive !== "granted") {
      console.warn("[PUSH] permission denied");
      return;
    }

    console.log("[PUSH] calling register");
    await PushNotifications.register();
  } catch (error) {
    console.warn("[PUSH] initialization failed", error);
  } finally {
    initInProgress = false;
  }
}

function registerPushListenersOnce(
  PushNotifications: typeof import("@capacitor/push-notifications").PushNotifications
) {
  if (listenersRegistered) {
    console.log("[PUSH] listeners already registered");
    return;
  }
  listenersRegistered = true;

  void PushNotifications.addListener("registration", async (token) => {
    if (__DEV__) {
      const tokenPreview = token.value ? `${token.value.slice(0, 8)}...` : "(empty)";
      console.log("[PUSH] registration token received", { tokenPreview });
    }
    try {
      if (typeof localStorage !== "undefined") localStorage.setItem("bialem.push.token", token.value);
      const platform = Capacitor.getPlatform().toUpperCase();
      await registerPushTokenApi(token.value, platform, {
        deviceUuid: getDeviceUuid(),
        appVersion: getAppVersion()
      });
      console.log("[PUSH] token registered successfully");
    } catch (error) {
      console.warn("[PUSH] token registration failed", error);
    }
  });

  void PushNotifications.addListener("registrationError", (error) => {
    console.warn("[PUSH] registration error", error);
  });

  void PushNotifications.addListener("pushNotificationReceived", (notification) => {
    console.log("[PUSH] notification received in foreground", { title: notification.title });
    foregroundListeners.forEach((cb) => {
      try {
        cb();
      } catch (e) {
        console.warn("[PUSH] foreground listener error", e);
      }
    });
  });

  void PushNotifications.addListener("pushNotificationActionPerformed", (action) => {
    const route = action.notification.data?.route;
    if (typeof route === "string" && route.length > 0) {
      console.log("[PUSH] notification tapped, navigating to", { route });
      navigateToRoute(route);
    }
  });

  console.log("[PUSH] listeners registered");
}

function getDeviceUuid(): string | undefined {
  try {
    if (typeof globalThis !== "undefined" && (globalThis as { deviceUuid?: string }).deviceUuid) {
      const value = (globalThis as { deviceUuid?: string }).deviceUuid;
      if (value && typeof localStorage !== "undefined") localStorage.setItem("bialem.push.deviceUuid", value);
      return value;
    }
  } catch {
    // ignore
  }
  return undefined;
}

function getAppVersion(): string | undefined {
  try {
    if (typeof globalThis !== "undefined" && (globalThis as { appVersion?: string }).appVersion) {
      return (globalThis as { appVersion?: string }).appVersion;
    }
  } catch {
    // ignore
  }
  return undefined;
}

function navigateToRoute(route: string) {
  if (!route || route.includes("://") || route.toLowerCase().startsWith("javascript:")) {
    console.warn("[PUSH] rejected unsafe route", route);
    return;
  }
  try {
    if (pendingRoute === route) return;
    pendingRoute = route;
    router.push(route);
    setTimeout(() => {
      pendingRoute = null;
    }, 500);
  } catch (error) {
    console.warn("[PUSH] navigation failed, storing route for later", error);
    pendingRoute = route;
  }
}

export function consumePendingNotificationRoute(): string | null {
  const route = pendingRoute;
  pendingRoute = null;
  return route;
}

export function hasPushListenersRegistered(): boolean {
  return listenersRegistered;
}
