import { useEffect, useState, useCallback } from "react";
import { request, type AdminNotificationDTO } from "../api";
import { Alert, TableSkeleton } from "../components/Feedback";

const STATUS_LABELS: Record<string, string> = {
  SENT: "Gönderildi", SKIPPED: "Atlandı", FAILED: "Başarısız", PENDING: "Beklemede",
};
const STATUS_BADGE: Record<string, string> = {
  SENT: "badge-success", SKIPPED: "badge-info", FAILED: "badge-danger", PENDING: "badge-warning",
};

export default function NotificationsPage() {
  const [items, setItems] = useState<AdminNotificationDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [stats, setStats] = useState<Record<string, number> | null>(null);
  const [filter, setFilter] = useState("");
  const [detail, setDetail] = useState<AdminNotificationDTO | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      let url = "/admin/notifications";
      if (filter) url += `?status=${filter}`;
      const data = await request<AdminNotificationDTO[]>(url);
      setItems(Array.isArray(data) ? data : []);
    } catch (err: any) {
      setError(err.message || "Bildirimler yüklenemedi");
      setItems([]);
    }
    setLoading(false);
  }, [filter]);

  const loadStats = useCallback(async () => {
    try {
      setStats(await request<Record<string, number>>("/admin/notifications/stats"));
    } catch { /* ignore */ }
  }, []);

  useEffect(() => { load(); loadStats(); }, [load, loadStats]);

  const retry = async (id: number) => {
    try {
      await request(`/admin/notifications/${id}/retry`, { method: "POST" });
      load();
      loadStats();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const badge = (s: string | null) => {
    if (!s) return <span className="badge badge-info">—</span>;
    return <span className={`badge ${STATUS_BADGE[s] || "badge-info"}`}>{STATUS_LABELS[s] || s}</span>;
  };

  return (
    <div>
      <div className="page-header">
        <h1>Bildirimler ({items.length})</h1>
        <select className="form-select" style={{ width: 200 }} value={filter} onChange={e => setFilter(e.target.value)}>
          <option value="">Tüm Durumlar</option>
          <option value="SENT">Gönderildi</option>
          <option value="PENDING">Beklemede</option>
          <option value="FAILED">Başarısız</option>
          <option value="SKIPPED">Atlandı</option>
        </select>
      </div>

      {/* Stats bar */}
      {stats && (
        <div style={{ display: "flex", gap: 12, marginBottom: 16, flexWrap: "wrap" }}>
          {[
            { key: "pending", label: "Bekleyen", color: "var(--warning)" },
            { key: "sent", label: "Gönderilen", color: "var(--success)" },
            { key: "failed", label: "Başarısız", color: "var(--danger)" },
            { key: "partial", label: "Kısmi", color: "var(--text2)" },
            { key: "activeDevices", label: "Aktif Cihaz", color: "var(--primary)" },
          ].map(({ key, label, color }) => (
            <div key={key} className="stat-card" style={{ minWidth: 120, flex: "1 1 0" }}>
              <div className="label">{label}</div>
              <div style={{ fontSize: 20, fontWeight: 700, color }}>{stats[key] ?? 0}</div>
            </div>
          ))}
        </div>
      )}

      {error && <Alert kind="error">{error}</Alert>}

      {loading ? <TableSkeleton rows={6} /> : (
        <table className="data-table">
          <thead><tr>
            <th>ID</th><th>Tür</th><th>Başlık</th><th>Alıcı</th><th>Durum</th><th>Push</th><th>Tarih</th><th>İşlem</th>
          </tr></thead>
          <tbody>
            {items.map(n => (
              <tr key={n.id} style={{ cursor: "pointer" }} onClick={() => setDetail(n)}>
                <td>{n.id}</td>
                <td><span className="badge badge-info">{n.notificationType}</span></td>
                <td style={{ maxWidth: 250, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{n.title}</td>
                <td>#{n.recipientUserId}</td>
                <td>{badge(n.firebaseStatus)}</td>
                <td>
                  <span style={{ color: "var(--success)", fontWeight: 500 }}>{n.pushSuccessful}</span>
                  {" / "}
                  <span style={{ color: "var(--danger)", fontWeight: 500 }}>{n.pushFailed}</span>
                </td>
                <td>{new Date(n.createdAt).toLocaleDateString("tr-TR")}</td>
                <td onClick={e => e.stopPropagation()}>
                  {n.firebaseStatus === "FAILED" && (
                    <button className="btn btn-ghost btn-sm" style={{ color: "var(--primary)" }} onClick={() => retry(n.id)}>
                      Tekrar Dene
                    </button>
                  )}
                </td>
              </tr>
            ))}
            {items.length === 0 && <tr><td colSpan={8} className="empty">Kayıt bulunamadı</td></tr>}
          </tbody>
        </table>
      )}

      {/* Detail Modal */}
      {detail && (
        <div className="modal-overlay" onClick={() => setDetail(null)}>
          <div className="modal" onClick={e => e.stopPropagation()} style={{ width: 600 }}>
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 16 }}>
              <h2 style={{ margin: 0 }}>Bildirim Detayı</h2>
              <button className="btn btn-ghost btn-sm" onClick={() => setDetail(null)}>✕</button>
            </div>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12, fontSize: 13 }}>
              <div><strong>ID:</strong> {detail.id}</div>
              <div><strong>Bildirim ID:</strong> {detail.notificationId}</div>
              <div style={{ gridColumn: "1 / -1" }}><strong>Başlık:</strong> {detail.title}</div>
              {detail.body && <div style={{ gridColumn: "1 / -1" }}><strong>İçerik:</strong> {detail.body}</div>}
              <div><strong>Tür:</strong> <span className="badge badge-info">{detail.notificationType}</span></div>
              <div><strong>Kaynak:</strong> {detail.source || "—"}</div>
              <div><strong>Firebase Durumu:</strong> {badge(detail.firebaseStatus)}</div>
              <div><strong>Alıcı Kullanıcı:</strong> #{detail.recipientUserId}</div>
              <div><strong>Push Başarılı:</strong> <span style={{ color: "var(--success)" }}>{detail.pushSuccessful}</span></div>
              <div><strong>Push Başarısız:</strong> <span style={{ color: "var(--danger)" }}>{detail.pushFailed}</span></div>
              <div><strong>Deneme Sayısı:</strong> {detail.attemptCount}</div>
              {detail.firebaseMessageId && <div style={{ gridColumn: "1 / -1" }}><strong>FCM Message ID:</strong> {detail.firebaseMessageId}</div>}
              {detail.referenceType && <div><strong>Referans Türü:</strong> {detail.referenceType}</div>}
              {detail.referenceId && <div><strong>Referans ID:</strong> {detail.referenceId}</div>}
              {detail.lastError && <div style={{ gridColumn: "1 / -1", color: "var(--danger)" }}><strong>Hata:</strong> {detail.lastError}</div>}
              {detail.firebaseErrors && Object.keys(detail.firebaseErrors).length > 0 && (
                <div style={{ gridColumn: "1 / -1" }}><strong>Hata Kodları:</strong> {JSON.stringify(detail.firebaseErrors)}</div>
              )}
              <div><strong>Oluşturulma:</strong> {new Date(detail.createdAt).toLocaleString("tr-TR")}</div>
              {detail.sentAt && <div><strong>Gönderim:</strong> {new Date(detail.sentAt).toLocaleString("tr-TR")}</div>}
            </div>
            <div className="modal-actions" style={{ marginTop: 16 }}>
              {detail.firebaseStatus === "FAILED" && (
                <button className="btn btn-primary" onClick={() => { retry(detail.id); setDetail(null); }}>Tekrar Dene</button>
              )}
              <button className="btn btn-ghost" onClick={() => setDetail(null)}>Kapat</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
