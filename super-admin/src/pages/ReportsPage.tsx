import { useEffect, useState, useCallback } from "react";
import { request, type ReportDTO } from "../api";
import { TableSkeleton } from "../components/Feedback";

export default function ReportsPage() {
  const [items, setItems] = useState<ReportDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const params: Record<string, string> = {};
      if (statusFilter) params.status = statusFilter;
      const qs = new URLSearchParams(params).toString();
      setItems(await request<ReportDTO[]>(`/reports${qs ? "?" + qs : ""}`));
    } catch { setItems([]); }
    setLoading(false);
  }, [statusFilter]);

  useEffect(() => { load(); }, [load]);

  const resolveReport = async (id: number) => {
    await request(`/reports/${id}`, { method: "PATCH", json: { status: "RESOLVED", resolvedAt: new Date().toISOString() } });
    load();
  };

  const deleteReport = async (id: number) => {
    if (!confirm("Bu raporu silmek istediğinize emin misiniz?")) return;
    await request(`/reports/${id}`, { method: "DELETE" });
    load();
  };

  const statusBadge = (s: string) => {
    const map: Record<string, string> = { PENDING: "badge-warning", RESOLVED: "badge-success", DISMISSED: "badge-info" };
    return <span className={`badge ${map[s] || "badge-info"}`}>{s}</span>;
  };

  return (
    <div>
      <div className="page-header">
        <h1>Raporlar ({items.length})</h1>
        <select className="form-select" style={{ width: 160 }} value={statusFilter} onChange={e => { setStatusFilter(e.target.value); }}>
          <option value="">Tüm Durumlar</option>
          <option value="PENDING">PENDING</option><option value="RESOLVED">RESOLVED</option><option value="DISMISSED">DISMISSED</option>
        </select>
      </div>
      {loading ? <TableSkeleton rows={6} /> : (
        <table className="data-table">
          <thead><tr><th>ID</th><th>Hedef</th><th>Hedef ID</th><th>Sebep</th><th>Durum</th><th>Tarih</th><th>İşlemler</th></tr></thead>
          <tbody>
            {items.map(r => (
              <tr key={r.id}>
                <td>{r.id}</td><td>{r.targetType}</td><td>{r.targetId}</td>
                <td style={{ maxWidth: 300, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{r.reason}</td>
                <td>{statusBadge(r.status)}</td>
                <td>{new Date(r.createdAt).toLocaleDateString("tr-TR")}</td>
                <td style={{ display: "flex", gap: 4 }}>
                  {r.status === "PENDING" && <button className="btn btn-ghost btn-sm" onClick={() => resolveReport(r.id)}>Çözüldü</button>}
                  <button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => deleteReport(r.id)}>Sil</button>
                </td>
              </tr>
            ))}
            {items.length === 0 && <tr><td colSpan={7} className="empty">Kayıt bulunamadı</td></tr>}
          </tbody>
        </table>
      )}
    </div>
  );
}
