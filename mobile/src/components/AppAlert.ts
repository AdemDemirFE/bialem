import { Alert, Platform } from "react-native";
import Swal, { type SweetAlertIcon, type SweetAlertResult } from "sweetalert2";

type AlertOptions = {
  title: string;
  text?: string;
  icon?: SweetAlertIcon;
  confirmText?: string;
  timer?: number;
};

type ConfirmOptions = AlertOptions & {
  cancelText?: string;
  confirmDanger?: boolean;
};

type FireOptions = ConfirmOptions & {
  showCancel?: boolean;
};

const brand = {
  accent: "#7047d7",
  ink: "#0a1833",
  muted: "#44516f",
  danger: "#c94568",
  surface: "#ffffff"
};

function isWeb() {
  return Platform.OS === "web" && typeof document !== "undefined";
}

function nativeAlert({ title, text, icon = "info" }: AlertOptions) {
  Alert.alert(title, text, [{ text: "Tamam" }], { cancelable: true });
  return Promise.resolve({ isConfirmed: true, isDismissed: false } as SweetAlertResult);
}

function nativeConfirm({
  title,
  text,
  confirmText = "Evet",
  cancelText = "Hayır"
}: ConfirmOptions): Promise<boolean> {
  return new Promise((resolve) => {
    Alert.alert(
      title,
      text,
      [
        { text: cancelText, onPress: () => resolve(false), style: "cancel" },
        { text: confirmText, onPress: () => resolve(true) }
      ],
      { cancelable: true, onDismiss: () => resolve(false) }
    );
  });
}

function fire(options: FireOptions): Promise<SweetAlertResult> {
  if (!isWeb()) {
    if (options.showCancel) {
      return nativeConfirm(options).then((confirmed) =>
        ({ isConfirmed: confirmed, isDismissed: !confirmed } as SweetAlertResult)
      );
    }
    return nativeAlert(options);
  }

  const swalOptions: Record<string, unknown> = {
    title: options.title,
    text: options.text,
    icon: options.icon ?? "info",
    confirmButtonText: options.confirmText ?? "Tamam",
    cancelButtonText: options.cancelText ?? "Vazgeç",
    showCancelButton: Boolean(options.showCancel),
    reverseButtons: true,
    buttonsStyling: false,
    timerProgressBar: Boolean(options.timer),
    customClass: {
      popup: "bialem-alert",
      title: "bialem-alert-title",
      htmlContainer: "bialem-alert-text",
      confirmButton: options.confirmDanger ? "bialem-alert-btn bialem-alert-btn-danger" : "bialem-alert-btn",
      cancelButton: "bialem-alert-btn bialem-alert-btn-ghost",
      icon: "bialem-alert-icon"
    },
    color: brand.ink,
    background: brand.surface
  };

  if (options.timer !== undefined) {
    swalOptions.timer = options.timer;
  }

  return Swal.fire(swalOptions);
}

export function showAppAlert({ title, text, icon = "info", confirmText, timer }: AlertOptions) {
  return fire({ title, text, icon, confirmText, timer });
}

export function showAppSuccess(message: string, title = "Başarılı") {
  return showAppAlert({ title, text: message, icon: "success", timer: 3000 });
}

export function showAppInfo(message: string, title = "Bilgilendirme") {
  return showAppAlert({ title, text: message, icon: "info", timer: 4000 });
}

export function showAppWarning(message: string, title = "Uyarı") {
  return showAppAlert({ title, text: message, icon: "warning", timer: 4000 });
}

export function showAppError(message: string, title = "Hata") {
  return showAppAlert({ title, text: message, icon: "error" });
}

export async function showAppConfirm({
  title,
  text,
  icon = "question",
  confirmText = "Evet",
  cancelText = "Hayır",
  confirmDanger
}: ConfirmOptions) {
  const result = await fire({
    title,
    text,
    icon,
    confirmText,
    cancelText,
    showCancel: true,
    confirmDanger
  });
  return result.isConfirmed;
}

export async function showAppConfirmDelete(message?: string) {
  return showAppConfirm({
    title: "Emin misiniz?",
    text: message ?? "Bu kaydı silmek istediğinize emin misiniz?",
    icon: "warning",
    confirmText: "Evet, Sil",
    cancelText: "Vazgeç",
    confirmDanger: true
  });
}

function statusOf(data: unknown) {
  return String(data ?? "").trim().toLowerCase();
}

export function showJoinCommunityResult(data: unknown, error?: { message: string } | null, kind: "community" | "group" = "community") {
  if (error) return showAppError(error.message);

  const target = kind === "group" ? "gruba" : "topluluğa";
  const status = statusOf(data);

  if (status === "approved") {
    return showAppAlert({
      title: kind === "group" ? "Gruba katıldın" : "Topluluğa katıldın",
      text: "Artık içerikleri görebilir, etkinliklere katılabilirsin.",
      icon: "success",
      timer: 3000
    });
  }

  if (status === "pending") {
    return showAppAlert({
      title: "Katılım isteğin gönderildi",
      text: `Moderatör onayından sonra ${target} üye olacaksın.`,
      icon: "info",
      timer: 4000
    });
  }

  if (status === "rejected") {
    return showAppAlert({
      title: "İstek henüz açılamıyor",
      text: "Önceki katılım isteğin reddedilmiş. Yeni bir istek için moderatörle iletişime geç.",
      icon: "warning",
      timer: 4000
    });
  }

  if (status === "blocked") {
    return showAppAlert({
      title: "Katılım engellendi",
      text: `Bu ${kind === "group" ? "gruba" : "topluluğa"} şu anda katılamazsın.`,
      icon: "error"
    });
  }

  return showAppAlert({
    title: "Katılım kaydedildi",
    text: "Durumun güncellendi.",
    icon: "success",
    timer: 3000
  });
}

export function showEventJoinResult(data: unknown, error?: { message: string } | null) {
  if (error) {
    if (error.message.includes("Join the event group first")) {
      return showAppError("Katılım talebi göndermek için önce etkinliğin grubuna katılmalısın.", "Önce gruba katıl");
    }
    return showAppError(error.message);
  }

  const status = statusOf(data);
  if (status === "waitlisted") {
    return showAppAlert({
      title: "Bekleme listesine eklendin",
      text: "Kontenjan dolu. Yer açılırsa sıran gelince haberdar edilirsin.",
      icon: "warning",
      timer: 4000
    });
  }
  if (status === "approved" || status === "checked_in") {
    return showAppAlert({
      title: "Katılımın onaylandı",
      text: "Etkinlik günü yerin ayrıldı.",
      icon: "success",
      timer: 3000
    });
  }
  return showAppAlert({
    title: "Katılım isteğin gönderildi",
    text: "Moderatör onayından sonra etkinliğe katılmış olacaksın.",
    icon: "info",
    timer: 4000
  });
}
