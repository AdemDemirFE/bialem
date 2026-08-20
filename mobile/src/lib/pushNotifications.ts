import { Capacitor } from "@capacitor/core";
import { PushNotifications } from "@capacitor/push-notifications";
import { registerPushToken as registerPushTokenApi } from "./notificationApi";
import { router } from "./router";

const listenersRegisteredKey = "bialem:push-listeners-registered";
let listenersRegistered = false;

export async function initializePushNotifications() {
  if (!Capacitor.isNativePlatform()) {
    return;
  }

  try {
    const result = await PushNotifications.requestPermissions();
    if (result.receive !== "granted") {
      return;
    }

    await PushNotifications.register();
    registerPushListenersOnce();
  } catch (error) {
    console.warn("Push notification initialization failed", error);
  }
}

function registerPushListenersOnce() {
  if (listenersRegistered) {
    return;
  }
  listenersRegistered = true;

  void PushNotifications.addListener("registration", async (token) => {
    try {
      await registerPushTokenApi(token.value, Capacitor.getPlatform().toUpperCase());
    } catch (error) {
      console.warn("Failed to register push token", error);
    }
  });

  void PushNotifications.addListener("registrationError", (error) => {
    console.warn("Push registration error", error);
  });

  void PushNotifications.addListener("pushNotificationReceived", (notification) => {
    console.log("Push notification received in foreground", notification);
  });

  void PushNotifications.addListener("pushNotificationActionPerformed", (action) => {
    const route = action.notification.data?.route;
    if (typeof route === "string" && route.length > 0) {
      router.push(route);
    }
  });
}

export function hasPushListenersRegistered(): boolean {
  return listenersRegistered;
}

export async function resetPushListenersFlag() {
  listenersRegistered = false;
}
