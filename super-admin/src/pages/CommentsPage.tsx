import { useEffect, useState, useCallback } from "react";
import { request } from "../api";
import { TableSkeleton } from "../components/Feedback";

interface CommentAuthor {
  id: number;
  displayName: string | null;
  username: string | null;
  avatarUrl: string | null;
}

interface CommentDTO {
  id: number;
  targetType: string;
  targetId: string;
  body: string;
  moderationStatus: string;
  createdAt: string;
  updatedAt: string;
  author: CommentAuthor | null;
}

export default function CommentsPage() {
  const [all, setAll] = useState<CommentDTO[]>([]);
  const [items, setItems] = useState<CommentDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await request<CommentDTO[]>("/comments");
      const list = Array.isArray(data) ? data : [];
      setAll(list);
      setItems(list);
    } catch {
      setAll([]);
      setItems([]);
      setError("Yorumlar yüklenirken hata oluştu");
    }
    setLoading(false);
  }, []);

  useEffect(() => { load(); }, [load]);

  useEffect(() => {
    let filtered = all;
    if (statusFilter !== "ALL") {
      filtered = filtered.filter(c => c.moderationStatus === statusFilter);
    }
    if (search.trim()) {
      const q = search.toLowerCase();
      filtered = filtered.filter(c =>
        c.body?.toLowerCase().includes(q) ||
        c.targetType?.toLowerCase().includes(q) ||
        c.author?.displayName?.toLowerCase().includes(q) ||
        c.author?.username?.toLowerCase().includes(q)
      );
    }
    setItems(filtered);
  }, [search, statusFilter, all]);

  const moderate = async (id: number, status: string) => {
    try {
      await request(`/comments/${id}`, { method: "PATCH", json: { moderationStatus: status } });
      load();
    } catch { alert("Moderasyon işlemi başarısız oldu"); }
  };

  const deleteComment = async (id: number) => {
    if (!confirm("Bu yorumu silmek istediğinize emin misiniz?")) return;
    try {
      await request(`/comments/${id}`, { method: "DELETE" });
      load();
    } catch { alert("Silme işlemi başarısız oldu"); }
  };

  const statusBadge = (s: string) => {
    const map: Record<string, { cls: string; label: string }> = {
      VISIBLE: { cls: "badge-success", label: "Görünür" },
      HIDDEN: { cls: "badge-danger", label: "Gizli" },
      DELETED: { cls: "badge-danger", label: "Silindi" },
      PENDING: { cls: "badge-warning", label: "Bekliyor" },
      APPROVED: { cls: "badge-success", label: "Onaylandı" },
      REJECTED: { cls: "badge-danger", label: "Reddedildi" },
    };
    const info = map[s] || { cls: "badge-info", label: s };
    return <span className={`badge ${info.cls}`}>{info.label}</span>;
  };

  const authorName = (author: CommentAuthor | null) => {
    if (!author) return "—";
    if (author.displayName) return author.displayName;
    if (author.username) return author.username;
    return `Kullanıcı #${author.id}`;
  };

  return (
    <div>
      <div className="page-header">
        <h1>Yorumlar ({items.length}{search || statusFilter !== "ALL" ? ` / ${all.length}` : ""})</h1>
        <div style={{ display: "flex", gap: 8 }}>
          <select className="search-input" value={statusFilter} onChange={e => setStatusFilter(e.target.value)}>
            <option value="ALL">Tüm Durumlar</option>
            <option value="VISIBLE">Görünür</option>
            <option value="HIDDEN">Gizli</option>
            <option value="PENDING">Bekliyor</option>
          </select>
          <input
            className="search-input"
            placeholder="Yorum ara..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
      </div>
      {error && <div className="alert alert-danger">{error}</div>}
      {loading ? <TableSkeleton rows={6} /> : (
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th><th>Yazar</th><th>Hedef</th><th>İçerik</th><th>Durum</th><th>Tarih</th><th>İşlemler</th>
            </tr>
          </thead>
          <tbody>
            {items.map(c => (
              <tr key={c.id}>
                <td>{c.id}</td>
                <td style={{ fontWeight: 600 }}>{authorName(c.author)}</td>
                <td>
                  <span className="badge badge-info">{c.targetType}</span>
                  <span style={{ marginLeft: 4, fontSize: 11, color: "var(--text-muted)" }}>#{c.targetId}</span>
                </td>
                <td style={{ maxWidth: 350, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                  {c.body}
                </td>
                <td>{statusBadge(c.moderationStatus)}</td>
                <td>{c.createdAt ? new Date(c.createdAt).toLocaleDateString("tr-TR") : "-"}</td>
                <td style={{ display: "flex", gap: 4 }}>
                  {c.moderationStatus !== "VISIBLE" && c.moderationStatus !== "APPROVED" && (
                    <button className="btn btn-ghost btn-sm" onClick={() => moderate(c.id, "VISIBLE")}>Göster</button>
                  )}
                  {c.moderationStatus !== "HIDDEN" && c.moderationStatus !== "REJECTED" && (
                    <button className="btn btn-ghost btn-sm" style={{ color: "var(--warning)" }} onClick={() => moderate(c.id, "HIDDEN")}>Gizle</button>
                  )}
                  <button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => deleteComment(c.id)}>Sil</button>
                </td>
              </tr>
            ))}
            {items.length === 0 && (
              <tr><td colSpan={7} className="empty">{search || statusFilter !== "ALL" ? "Filtreye uygun yorum bulunamadı" : "Kayıt bulunamadı"}</td></tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  );
}
