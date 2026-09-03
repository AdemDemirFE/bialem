import { useEffect, useState, useCallback } from "react";
import { getPage, request, type ReportDTO } from "../api";

interface CommentDTO {
  id: number;
  targetType: string;
  targetId: string;
  body: string;
  moderationStatus: string;
  createdAt: string;
  authorId: number | null;
}

export default function CommentsPage() {
  const [items, setItems] = useState<CommentDTO[]>([]);
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const pageSize = 30;

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getPage<CommentDTO>("/comments", { page, size: pageSize, sort: "id,desc" });
      setItems(res.content);
      setTotal(res.totalElements);
    } catch { setItems([]); }
    setLoading(false);
  }, [page]);

  useEffect(() => { load(); }, [load]);

  const moderate = async (id: number, status: string) => {
    await request(`/comments/${id}`, { method: "PATCH", json: { moderationStatus: status } });
    load();
  };

  const deleteComment = async (id: number) => {
    if (!confirm("Bu yorumu silmek istediğinize emin misiniz?")) return;
    await request(`/comments/${id}`, { method: "DELETE" });
    load();
  };

  const statusBadge = (s: string) => {
    const map: Record<string, string> = { APPROVED: "badge-success", PENDING: "badge-warning", REJECTED: "badge-danger" };
    return <span className={`badge ${map[s] || "badge-info"}`}>{s}</span>;
  };

  return (
    <div>
      <div className="page-header"><h1>Yorumlar ({total})</h1></div>
      {loading ? <div className="loading">Yükleniyor...</div> : (
        <>
          <table className="data-table">
            <thead><tr><th>ID</th><th>Hedef</th><th>İçerik</th><th>Durum</th><th>Tarih</th><th>İşlemler</th></tr></thead>
            <tbody>
              {items.map(c => (
                <tr key={c.id}>
                  <td>{c.id}</td><td>{c.targetType}</td>
                  <td style={{ maxWidth: 350, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{c.body}</td>
                  <td>{statusBadge(c.moderationStatus)}</td>
                  <td>{new Date(c.createdAt).toLocaleDateString("tr-TR")}</td>
                  <td style={{ display: "flex", gap: 4 }}>
                    {c.moderationStatus !== "APPROVED" && <button className="btn btn-ghost btn-sm" onClick={() => moderate(c.id, "APPROVED")}>Onayla</button>}
                    {c.moderationStatus !== "REJECTED" && <button className="btn btn-ghost btn-sm" style={{ color: "var(--warning)" }} onClick={() => moderate(c.id, "REJECTED")}>Reddet</button>}
                    <button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => deleteComment(c.id)}>Sil</button>
                  </td>
                </tr>
              ))}
              {items.length === 0 && <tr><td colSpan={6} className="empty">Kayıt bulunamadı</td></tr>}
            </tbody>
          </table>
          <div className="pagination">
            <button className="btn btn-ghost btn-sm" disabled={page === 0} onClick={() => setPage(p => p - 1)}>← Önceki</button>
            <span>Sayfa {page + 1} / {Math.ceil(total / pageSize) || 1}</span>
            <button className="btn btn-ghost btn-sm" disabled={(page + 1) * pageSize >= total} onClick={() => setPage(p => p + 1)}>Sonraki →</button>
          </div>
        </>
      )}
    </div>
  );
}
