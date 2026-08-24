import Swal, { type SweetAlertIcon, type SweetAlertResult } from "sweetalert2";

type AlertOptions = {
  title: string;
  text?: string;
  icon?: SweetAlertIcon;
  confirmText?: string;
};

type ConfirmOptions = AlertOptions & {
  cancelText?: string;
  confirmDanger?: boolean;
};

const brand = {
  accent: "#7047d7",
  ink: "#0a1833",
  muted: "#44516f",
  danger: "#c94568",
  surface: "#ffffff"
};

async function fire(options: {
  title: string;
  text?: string;
  icon?: SweetAlertIcon;
  confirmText?: string;
  showCancel?: boolean;
  cancelText?: string;
  confirmDanger?: boolean;
  timer?: number;
}): Promise<SweetAlertResult> {
  Swal.close();
  return Swal.fire({
    title: options.title,
    text: options.text,
    icon: options.icon ?? "info",
    confirmButtonText: options.confirmText ?? "Tamam",
    cancelButtonText: options.cancelText ?? "Vazgeç",
    showCancelButton: Boolean(options.showCancel),
    timer: options.timer,
    timerProgressBar: Boolean(options.timer),
    reverseButtons: true,
    buttonsStyling: false,
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
  });
}

export function showAppAlert({ title, text, icon = "info", confirmText }: AlertOptions) {
  return fire({ title, text, icon, confirmText, timer: 2000 });
}

export async function showAppConfirm({
  title,
  text,
  icon = "question",
  confirmText = "Evet",
  cancelText = "Vazgeç",
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

export function showAppError(message: string, title = "İşlem tamamlanamadı") {
  return showAppAlert({ title, text: message, icon: "error" });
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
      icon: "success"
    });
  }

  if (status === "pending") {
    return showAppAlert({
      title: "Katılım isteğin gönderildi",
      text: `Moderatör onayından sonra ${target} üye olacaksın.`,
      icon: "info"
    });
  }

  if (status === "rejected") {
    return showAppAlert({
      title: "İstek henüz açılamıyor",
      text: "Önceki katılım isteğin reddedilmiş. Yeni bir istek için moderatörle iletişime geç.",
      icon: "warning"
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
    icon: "success"
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
      icon: "warning"
    });
  }
  if (status === "approved" || status === "checked_in") {
    return showAppAlert({
      title: "Katılımın onaylandı",
      text: "Etkinlik günü yerin ayrıldı.",
      icon: "success"
    });
  }
  return showAppAlert({
    title: "Katılım isteğin gönderildi",
    text: "Moderatör onayından sonra etkinliğe katılmış olacaksın.",
    icon: "info"
  });
}
