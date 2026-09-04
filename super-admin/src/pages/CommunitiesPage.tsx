import { useEffect, useState, useCallback } from "react";
import { request, type CommunityDTO } from "../api";
import { TableSkeleton } from "../components/Feedback";

export default function CommunitiesPage() {
  const [all, setAll] = useState<CommunityDTO[]>([]);
  const [items, setItems] = useState<CommunityDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await request<CommunityDTO[]>("/communities");
      const list = Array.isArray(data) ? data : [];
      setAll(list);
      setItems(list);
    } catch {
      setAll([]);
      setItems([]);
      setError("Topluluklar yüklenirken hata oluştu");
    }
    setLoading(false);
  }, []);

  useEffect(() => { load(); }, [load]);

  useEffect(() => {
    if (!search.trim()) { setItems(all); return; }
    const q = search.toLowerCase();
    setItems(all.filter(c =>
      c.name.toLowerCase().includes(q) ||
      c.slug?.toLowerCase().includes(q) ||
      c.visibility?.toLowerCase().includes(q) ||
      c.communityType?.toLowerCase().includes(q)
    ));
  }, [search, all]);

  const handleDelete = async (id: number) => {
    if (!confirm("Bu topluluğu silmek istediğinize emin misiniz?")) return;
    try {
      await request(`/communities/${id}`, { method: "DELETE" });
      load();
    } catch { alert("Silme işlemi başarısız oldu"); }
  };

  const visBadge = (v: string) => {
    const map: Record<string, string> = {
      PUBLIC: "badge-success", PRIVATE: "badge-warning", HIDDEN: "badge-danger"
    };
    return <span className={`badge ${map[v] || "badge-info"}`}>{v === "PUBLIC" ? "Açık" : v === "PRIVATE" ? "Özel" : v === "HIDDEN" ? "Gizli" : v}</span>;
  };

  const typeBadge = (t: string) => {
    const map: Record<string, { cls: string; label: string }> = {
      GROUP: { cls: "badge-info", label: "Grup" },
      CATEGORY_HUB: { cls: "badge-success", label: "Kategori" },
      PARTNER_HUB: { cls: "badge-warning", label: "Ortak" },
      INTEREST: { cls: "badge-info", label: "İlgi" },
      LOCATION: { cls: "badge-success", label: "Konum" },
    };
    const info = map[t] || { cls: "badge-info", label: t };
    return <span className={`badge ${info.cls}`}>{info.label}</span>;
  };

  return (
    <div>
      <div className="page-header">
        <h1>Topluluklar ({items.length}{search ? ` / ${all.length}` : ""})</h1>
        <input
          className="search-input"
          placeholder="Topluluk ara..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
      </div>
      {error && <div className="alert alert-danger">{error}</div>}
      {loading ? <TableSkeleton rows={6} /> : (
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th><th>Ad</th><th>Slug</th><th>Görünürlük</th><th>Tür</th><th>Doğrulanmış</th><th>Oluşturulma</th><th>İşlem</th>
            </tr>
          </thead>
          <tbody>
            {items.map(c => (
              <tr key={c.id}>
                <td>{c.id}</td>
                <td style={{ fontWeight: 600 }}>{c.name}</td>
                <td><code>{c.slug}</code></td>
                <td>{visBadge(c.visibility)}</td>
                <td>{typeBadge(c.communityType)}</td>
                <td>{c.isVerifiedPartner ? "✅" : "❌"}</td>
                <td>{c.createdAt ? new Date(c.createdAt).toLocaleDateString("tr-TR") : "-"}</td>
                <td>
                  <button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => handleDelete(c.id)}>
                    Sil
                  </button>
                </td>
              </tr>
            ))}
            {items.length === 0 && (
              <tr><td colSpan={8} className="empty">{search ? "Aramaya uygun topluluk bulunamadı" : "Kayıt bulunamadı"}</td></tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  );
}
