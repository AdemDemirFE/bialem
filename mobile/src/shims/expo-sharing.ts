export async function isAvailableAsync() {
  return typeof navigator !== "undefined" && typeof navigator.share === "function";
}

export async function shareAsync(uri?: string, options?: { dialogTitle?: string; mimeType?: string; UTI?: string }) {
  if (await isAvailableAsync()) {
    await navigator.share({ title: options?.dialogTitle, url: uri });
    return { action: "shared" };
  }
  return { action: "dismissed" };
}

export default { isAvailableAsync, shareAsync };
