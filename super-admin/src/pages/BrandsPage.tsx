import { useEffect, useState, useCallback, useMemo, type FormEvent } from "react";
import { request, type StoreBrandDTO } from "../api";
import { Alert, EmptyState, TableSkeleton } from "../components/Feedback";

export default function BrandsPage() {
  const [items, setItems] = useState<StoreBrandDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [modal, setModal] = useState<"create" | "edit" | null>(null);
  const [form, setForm] = useState<Partial<StoreBrandDTO>>({});
  const [saving, setSaving] = useState(false);
  const [query, setQuery] = useState("");

  const visible = useMemo(() => {
    const q = query.trim().toLocaleLowerCase("tr-TR");
    if (!q) return items;
    return items.filter((b) =>
      [b.name, b.slug, b.description ?? ""].some((v) => v.toLocaleLowerCase("tr-TR").includes(q))
    );
  }, [items, query]);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try { setItems(await request<StoreBrandDTO[]>("/store/brands")); }
    catch (err: any) { setError(err.message); setItems([]); }
    setLoading(false);
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleSave = async (e: FormEvent) => {
    e.preventDefault();
    if (!form.name?.trim() || !form.slug?.trim()) {
      setError("Ad ve slug zorunludur");
      return;
    }
    setSaving(true);
    setError("");
    try {
      if (modal === "edit" && form.id) {
        await request(`/store/admin/brands/${form.id}`, { method: "PUT", json: form });
        setSuccess("Marka güncellendi");
      } else {
        await request("/store/admin/brands", { method: "POST", json: form });
        setSuccess("Marka oluşturuldu");
      }
      setModal(null);
      load();
    } catch (err: any) {
      setError(err.message);
    }
    setSaving(false);
  };

  const handleDelete = async (b: StoreBrandDTO) => {
    if (!confirm(`"${b.name}" markasını silmek istediğinize emin misiniz?`)) return;
    setError("");
    try {
      await request(`/store/admin/brands/${b.id}`, { method: "DELETE" });
      setSuccess("Marka silindi");
      load();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const toggleActive = async (b: StoreBrandDTO) => {
    try {
      await request(`/store/admin/brands/${b.id}`, {
        method: "PUT",
        json: { ...b, isActive: !b.isActive },
      });
      setSuccess(b.isActive ? "Marka pasifleştirildi" : "Marka aktifleştirildi");
      load();
    } catch (err: any) {
      setError(err.message);
    }
  };

  return (
    <div className="page-enter">
      <div className="page-header">
        <h1 className="t-title t-rise">Markalar ({items.length})</h1>
        <div className="header-actions">
          <input
            className="table-search"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Ad, slug veya açıklama ara..."
          />
          <button className="btn btn-primary" onClick={() => { setForm({ isActive: true }); setModal("create"); }}>+ Yeni Marka</button>
        </div>
      </div>

      {error && <Alert kind="error">{error}</Alert>}
      {success && <Alert kind="success">{success}</Alert>}

      {loading ? <TableSkeleton rows={6} /> : visible.length === 0 ? (
        <EmptyState
          title={query.trim() ? "Aramaya uygun marka yok" : "Henüz marka yok"}
          hint={query.trim() ? "Farklı bir kelimeyle aramayı deneyin." : "Yeni Marka düğmesiyle ilk markayı oluşturun."}
        />
      ) : (
        <table className="data-table">
          <thead><tr><th>Logo</th><th>Ad</th><th>Slug</th><th>Açıklama</th><th>Durum</th><th>İşlem</th></tr></thead>
          <tbody>
            {visible.map(b => (
              <tr key={b.id}>
                <td>
                  {b.logoUrl ? (
                    <img src={b.logoUrl} alt="" style={{ width: 40, height: 40, objectFit: "contain", borderRadius: 4 }} onError={e => { (e.target as HTMLImageElement).style.display = "none"; }} />
                  ) : <span style={{ color: "var(--text2)" }}>—</span>}
                </td>
                <td style={{ fontWeight: 500 }}>{b.name}</td>
                <td>{b.slug}</td>
                <td style={{ maxWidth: 200, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{b.description || "—"}</td>
                <td>
                  <span
                    className={`badge badge-clickable ${b.isActive ? "badge-success" : "badge-danger"}`}
                    title="Durumu değiştirmek için tıklayın"
                    onClick={() => toggleActive(b)}
                  >
                    {b.isActive ? "Aktif" : "Pasif"}
                  </span>
                </td>
                <td style={{ display: "flex", gap: 4 }}>
                  <button className="btn btn-ghost btn-sm" onClick={() => { setForm(b); setModal("edit"); }}>Düzenle</button>
                  <button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => handleDelete(b)}>Sil</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {modal && (
        <div className="modal-overlay" onClick={() => setModal(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h2>{modal === "edit" ? "Marka Düzenle" : "Yeni Marka"}</h2>
            <form onSubmit={handleSave}>
              <div className="form-group">
                <label>Ad *</label>
                <input className="form-input" value={form.name || ""} onChange={e => setForm({ ...form, name: e.target.value })} required />
              </div>
              <div className="form-group">
                <label>Slug *</label>
                <input className="form-input" value={form.slug || ""} onChange={e => setForm({ ...form, slug: e.target.value })} required />
              </div>
              <div className="form-group">
                <label>Açıklama</label>
                <textarea className="form-textarea" value={form.description || ""} onChange={e => setForm({ ...form, description: e.target.value })} />
              </div>
              <div className="form-group">
                <label>Logo URL</label>
                <input className="form-input" value={form.logoUrl || ""} onChange={e => setForm({ ...form, logoUrl: e.target.value })} placeholder="https://..." />
              </div>
              {form.logoUrl && (
                <div style={{ marginBottom: 16 }}>
                  <img src={form.logoUrl} alt="Logo önizleme" style={{ maxWidth: 120, maxHeight: 80, objectFit: "contain", borderRadius: 8, background: "var(--bg)", padding: 8 }} onError={e => { (e.target as HTMLImageElement).style.display = "none"; }} />
                </div>
              )}
              <label style={{ display: "flex", alignItems: "center", gap: 6, cursor: "pointer", marginBottom: 16 }}>
                <input type="checkbox" checked={form.isActive !== false} onChange={e => setForm({ ...form, isActive: e.target.checked })} />
                Aktif
              </label>
              <div className="modal-actions">
                <button type="button" className="btn btn-ghost" onClick={() => setModal(null)}>İptal</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? "Kaydediliyor..." : "Kaydet"}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
