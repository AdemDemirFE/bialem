import { Share } from "@capacitor/share";
import { Capacitor } from "@capacitor/core";
import { showAppError, showAppSuccess } from "../components/AppAlert";

export type SharePayload = {
  title: string;
  text: string;
  url: string;
  dialogTitle?: string;
};

function isUserCancellation(error: unknown): boolean {
  if (error instanceof Error) {
    const message = error.message.toLowerCase();
    return (
      message.includes("abort") ||
      message.includes("cancel") ||
      message.includes("dismiss") ||
      message.includes("canceled") ||
      message.includes("user canceled")
    );
  }
  return false;
}

async function copyToClipboard(text: string): Promise<boolean> {
  try {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      await navigator.clipboard.writeText(text);
      return true;
    }
    // Fallback for older browsers / non-secure contexts.
    const textarea = document.createElement("textarea");
    textarea.value = text;
    textarea.style.position = "fixed";
    textarea.style.left = "-9999px";
    textarea.style.opacity = "0";
    document.body.appendChild(textarea);
    textarea.focus();
    textarea.select();
    const result = document.execCommand("copy");
    document.body.removeChild(textarea);
    return result;
  } catch {
    return false;
  }
}

/**
 * Cross-platform share helper.
 *
 * - Native (Android/iOS): uses the official Capacitor Share plugin.
 * - Web: uses navigator.share when available, otherwise copies the URL to the
 *   clipboard and shows a success toast.
 *
 * User cancellations are silently ignored; real errors show an alert.
 */
export async function shareContent({ title, text, url, dialogTitle }: SharePayload): Promise<void> {
  if (Capacitor.isNativePlatform()) {
    try {
      await Share.share({
        title,
        text,
        url,
        dialogTitle: dialogTitle || title
      });
    } catch (error) {
      if (isUserCancellation(error)) return;
      void showAppError("Paylaşım yapılamadı. Lütfen tekrar deneyin.");
    }
    return;
  }

  if (typeof navigator !== "undefined" && navigator.share) {
    try {
      await navigator.share({ title, text, url });
    } catch (error) {
      if (isUserCancellation(error)) return;
      const copied = await copyToClipboard(url);
      if (copied) {
        void showAppSuccess("Bağlantı kopyalandı.");
      } else {
        void showAppError("Paylaşım yapılamadı. Lütfen tekrar deneyin.");
      }
    }
    return;
  }

  const copied = await copyToClipboard(url);
  if (copied) {
    void showAppSuccess("Bağlantı kopyalandı.");
  } else {
    void showAppError("Paylaşım yapılamadı. Lütfen tekrar deneyin.");
  }
}
