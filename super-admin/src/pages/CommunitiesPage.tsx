import { useEffect, useState, useCallback } from "react";
import { getPage, request, type CommunityDTO } from "../api";

export default function CommunitiesPage() {
  const [items, setItems] = useState<CommunityDTO[]>([]);
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const pageSize = 20;

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getPage<CommunityDTO>("/communities", { page, size: pageSize, sort: "id,desc" });
      setItems(res.content);
      setTotal(res.totalElements);
    } catch { setItems([]); }
    setLoading(false);
  }, [page]);

  useEffect(() => { load(); }, [load]);

  const handleDelete = async (id: number) => {
    if (!confirm("Bu topluluğu silmek istediğinize emin misiniz?")) return;
    await request(`/communities/${id}`, { method: "DELETE" });
    load();
  };

  const visBadge = (v: string) => {
    const map: Record<string, string> = { PUBLIC: "badge-success", PRIVATE: "badge-warning", HIDDEN: "badge-danger" };
    return <span className={`badge ${map[v] || "badge-info"}`}>{v}</span>;
  };

  return (
    <div>
      <div className="page-header"><h1>Topluluklar ({total})</h1></div>
      {loading ? <div className="loading">Yükleniyor...</div> : (
        <>
          <table className="data-table">
            <thead><tr><th>ID</th><th>Ad</th><th>Slug</th><th>Görünürlük</th><th>Tür</th><th>Doğrulanmış</th><th>Oluşturulma</th><th>İşlem</th></tr></thead>
            <tbody>
              {items.map(c => (
                <tr key={c.id}>
                  <td>{c.id}</td><td>{c.name}</td><td>{c.slug}</td>
                  <td>{visBadge(c.visibility)}</td><td>{c.communityType}</td>
                  <td>{c.isVerifiedPartner ? "✅" : "❌"}</td>
                  <td>{new Date(c.createdAt).toLocaleDateString("tr-TR")}</td>
                  <td><button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => handleDelete(c.id)}>Sil</button></td>
                </tr>
              ))}
              {items.length === 0 && <tr><td colSpan={8} className="empty">Kayıt bulunamadı</td></tr>}
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
