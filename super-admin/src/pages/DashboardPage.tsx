import { useEffect, useState } from "react";
import { request, type DashboardDTO } from "../api";

export default function DashboardPage() {
  const [dash, setDash] = useState<DashboardDTO | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    request<DashboardDTO>("/admin/dashboard")
      .then(setDash)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading">Dashboard yükleniyor...</div>;
  if (!dash) return <div className="empty">Dashboard verisi alınamadı</div>;

  const cards = [
    { label: "Kullanıcılar", value: dash.totalUsers, color: "var(--primary)" },
    { label: "Profiller", value: dash.totalProfiles, color: "var(--primary)" },
    { label: "Etkinlikler", value: dash.totalEvents, color: "var(--success)" },
    { label: "Topluluklar", value: dash.totalCommunities, color: "var(--success)" },
    { label: "Gönderiler", value: dash.totalPosts, color: "var(--warning)" },
    { label: "Siparişler", value: dash.totalOrders, color: "var(--primary)" },
    { label: "Gelir", value: `₺${(dash.totalRevenue || 0).toLocaleString("tr-TR")}`, color: "var(--success)" },
    { label: "Bekleyen Raporlar", value: dash.pendingReports, color: "var(--danger)" },
    { label: "Aktif Ürünler", value: dash.activeProducts, color: "var(--primary)" },
  ];

  return (
    <div>
      <div className="page-header">
        <h1>Dashboard</h1>
      </div>
      <div className="stats-grid">
        {cards.map((c) => (
          <div className="stat-card" key={c.label}>
            <div className="label">{c.label}</div>
            <div className="value" style={{ color: c.color }}>
              {c.value}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
