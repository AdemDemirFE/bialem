export function profileStatusLabel(status?: string | null) {
  switch (status) {
    case "active":
      return "Aktif";
    case "pending_verification":
      return "Doğrulama bekliyor";
    case "suspended":
      return "Askıya alındı";
    case "deleted":
      return "Silindi";
    default:
      return "Bilinmiyor";
  }
}
