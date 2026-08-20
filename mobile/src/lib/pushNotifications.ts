import { Capacitor } from "@capacitor/core";
import { PushNotifications } from "@capacitor/push-notifications";
import { registerPushToken as registerPushTokenApi } from "./notificationApi";
import { router } from "./router";

let listenersRegistered = false;
let initInProgress = false;

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
    registerPushListenersOnce();

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

function registerPushListenersOnce() {
  if (listenersRegistered) {
    console.log("[PUSH] listeners already registered");
    return;
  }
  listenersRegistered = true;

  void PushNotifications.addListener("registration", async (token) => {
    const tokenPreview = token.value ? `${token.value.slice(0, 8)}...` : "(empty)";
    console.log("[PUSH] registration token received", { tokenPreview });
    try {
      await registerPushTokenApi(token.value, Capacitor.getPlatform().toUpperCase());
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
  });

  void PushNotifications.addListener("pushNotificationActionPerformed", (action) => {
    const route = action.notification.data?.route;
    if (typeof route === "string" && route.length > 0) {
      console.log("[PUSH] notification tapped, navigating to", { route });
      router.push(route);
    }
  });

  console.log("[PUSH] listeners registered");
}

export function hasPushListenersRegistered(): boolean {
  return listenersRegistered;
}
