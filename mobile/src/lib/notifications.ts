export const Notifications = {
  addNotificationResponseReceivedListener() {
    return { remove() {} };
  },
  async getLastNotificationResponseAsync() {
    return null;
  },
  async clearLastNotificationResponseAsync() {}
};

export async function registerPushToken() {
  return null;
}

export async function deactivateCurrentDevicePushToken() {}

export function getNotificationTarget(data: Record<string, unknown>) {
  if (typeof data.route === "string" && data.route.length > 0) {
    return data.route;
  }
  if (typeof data.follow_request_id === "string") return "/people/requests";
  for (const [key, route] of [
    ["event_id", "event"],
    ["post_id", "post"],
    ["community_id", "community"],
    ["user_id", "user"]
  ] as const) {
    if (typeof data[key] === "string" || typeof data[key] === "number") {
      return `/${route}/${data[key]}`;
    }
  }
  return null;
}
