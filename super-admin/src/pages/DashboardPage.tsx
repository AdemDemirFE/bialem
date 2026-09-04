import { useEffect, useState } from "react";
import { request } from "../api";
import { TableSkeleton } from "../components/Feedback";

/* ─── Real backend response shape ────────────────────────────────────── */
interface DashboardData {
  users: { total: number; active: number; inactive: number; suspended: number; newToday: number; newThisWeek: number };
  communities: { total: number; active: number; pendingRequests: number };
  events: { total: number; upcoming: number; pendingApproval: number };
  moderation: { openReports: number; flaggedPosts: number; flaggedComments: number };
  communications: { notificationsSent: number };
}

const cardIcons: Record<string, string> = {
  "Toplam Kullanıcı": "M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2 M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z",
  "Aktif Kullanıcı": "M22 11.08V12a10 10 0 1 1-5.93-9.14 M22 4L12 14.01l-3-3",
  "Toplam Topluluk": "M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2 M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z M23 21v-2a4 4 0 0 0-3-3.87 M16 3.13a4 4 0 0 1 0 7.75 M12 14a6 6 0 0 0 6 6",
  "Toplam Etkinlik": "M19 4H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2z M16 2v4 M8 2v4 M3 10h18",
  "Yaklaşan Etkinlik": "M8 2v4 M16 2v4 M3 10h18 M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z",
  "Bekleyen Rapor": "M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z M12 9v4 M12 17h.01",
  "Bekleyen Onay": "M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z M9 12l2 2 4-4",
  "Gönderilen Bildirim": "M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9 M13.73 21a2 2 0 0 1-3.46 0",
  "Yeni Bu Hafta": "M12 5v14 M5 12h14",
};

export default function DashboardPage() {
  const [dash, setDash] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    request<DashboardData>("/admin/dashboard")
      .then(setDash)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <TableSkeleton rows={4} />;
  if (!dash) return <div className="empty">Dashboard verisi alınamadı</div>;

  const cards = [
    { label: "Toplam Kullanıcı", value: dash.users.total, color: "#6366f1" },
    { label: "Aktif Kullanıcı", value: dash.users.active, color: "#22c55e" },
    { label: "Toplam Topluluk", value: dash.communities.total, color: "#14b8a6" },
    { label: "Toplam Etkinlik", value: dash.events.total, color: "#a78bfa" },
    { label: "Yaklaşan Etkinlik", value: dash.events.upcoming, color: "#3b82f6" },
    { label: "Bekleyen Onay", value: dash.events.pendingApproval, color: "#f59e0b" },
    { label: "Bekleyen Rapor", value: dash.moderation.openReports, color: "#ef4444" },
    { label: "Gönderilen Bildirim", value: dash.communications.notificationsSent, color: "#a78bfa" },
    { label: "Yeni Bu Hafta", value: dash.users.newThisWeek, color: "#22c55e" },
  ];

  return (
    <div>
      <div className="page-header">
        <h1>Dashboard</h1>
        <span style={{ fontSize: "var(--fs-sm)", color: "var(--text-muted)" }}>
          Genel bakış ve istatistikler
        </span>
      </div>
      <div className="stats-grid">
        {cards.map((c) => (
          <div className="stat-card" key={c.label}>
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 12 }}>
              <div className="label">{c.label}</div>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke={c.color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" style={{ opacity: 0.5 }}>
                <path d={cardIcons[c.label] || "M12 2L2 7l10 5 10-5-10-5z"} />
              </svg>
            </div>
            <div style={{ color: c.color }}>
              {c.value}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
