import { useEffect, useState, useCallback, type FormEvent } from "react";
import { request, type StoreCategoryDTO, ApiError } from "../api";
import { Alert, TableSkeleton } from "../components/Feedback";

function CategoryTree({ items, depth = 0, onEdit, onDelete, onToggleActive }: {
  items: StoreCategoryDTO[];
  depth?: number;
  onEdit: (c: StoreCategoryDTO) => void;
  onDelete: (c: StoreCategoryDTO) => void;
  onToggleActive: (c: StoreCategoryDTO) => void;
}) {
  const [expanded, setExpanded] = useState<Set<number>>(new Set(items.map(i => i.id)));

  const toggle = (id: number) => {
    setExpanded(prev => {
      const next = new Set(prev);
      next.has(id) ? next.delete(id) : next.add(id);
      return next;
    });
  };

  return (
    <>
      {items.map(c => (
        <tr key={c.id}>
          <td style={{ paddingLeft: 16 + depth * 24 }}>
            {c.children?.length ? (
              <span style={{ cursor: "pointer", marginRight: 6, userSelect: "none" }} onClick={() => toggle(c.id)}>
                {expanded.has(c.id) ? "▼" : "▶"}
              </span>
            ) : <span style={{ marginRight: 18 }} />}
            {c.name}
            {c.children?.length ? <span style={{ color: "var(--text2)", fontSize: 11, marginLeft: 6 }}>({c.children.length})</span> : null}
          </td>
          <td>{c.slug}</td>
          <td>{c.parentName || "—"}</td>
          <td>{c.sortOrder}</td>
          <td>
            <span
              className={`badge ${c.isActive ? "badge-success" : "badge-danger"}`}
              style={{ cursor: "pointer" }}
              onClick={() => onToggleActive(c)}
            >
              {c.isActive ? "Aktif" : "Pasif"}
            </span>
          </td>
          <td>
            <button className="btn btn-ghost btn-sm" onClick={() => onEdit(c)}>Düzenle</button>
            <button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => onDelete(c)}>Sil</button>
          </td>
        </tr>
      ))}
      {expanded.has(items[0]?.id) && items.map(c =>
        c.children?.length ? (
          <CategoryTree key={`children-${c.id}`} items={c.children} depth={depth + 1} onEdit={onEdit} onDelete={onDelete} onToggleActive={onToggleActive} />
        ) : null
      )}
    </>
  );
}

export default function CategoriesPage() {
  const [tree, setTree] = useState<StoreCategoryDTO[]>([]);
  const [flat, setFlat] = useState<StoreCategoryDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [modal, setModal] = useState<"create" | "edit" | null>(null);
  const [form, setForm] = useState<Partial<StoreCategoryDTO>>({});
  const [saving, setSaving] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [treeData, flatData] = await Promise.all([
        request<StoreCategoryDTO[]>("/store/categories/tree"),
        request<StoreCategoryDTO[]>("/store/categories"),
      ]);
      setTree(treeData);
      setFlat(flatData);
    } catch (err: any) {
      setError(err.message);
      setTree([]);
      setFlat([]);
    }
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
        await request(`/store/admin/categories/${form.id}`, { method: "PUT", json: form });
        setSuccess("Kategori güncellendi");
      } else {
        await request("/store/admin/categories", { method: "POST", json: form });
        setSuccess("Kategori oluşturuldu");
      }
      setModal(null);
      load();
    } catch (err: any) {
      setError(err.message);
    }
    setSaving(false);
  };

  const handleDelete = async (c: StoreCategoryDTO) => {
    if (!confirm(`"${c.name}" kategorisini silmek istediğinize emin misiniz?`)) return;
    setError("");
    try {
      await request(`/store/admin/categories/${c.id}`, { method: "DELETE" });
      setSuccess("Kategori silindi");
      load();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const toggleActive = async (c: StoreCategoryDTO) => {
    try {
      await request(`/store/admin/categories/${c.id}`, {
        method: "PUT",
        json: { ...c, isActive: !c.isActive },
      });
      setSuccess(c.isActive ? "Kategori pasifleştirildi" : "Kategori aktifleştirildi");
      load();
    } catch (err: any) {
      setError(err.message);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1>Kategoriler ({flat.length})</h1>
        <button className="btn btn-primary" onClick={() => { setForm({ isActive: true, sortOrder: 0 }); setModal("create"); }}>+ Yeni Kategori</button>
      </div>

      {error && <Alert kind="error">{error}</Alert>}
      {success && <Alert kind="success">{success}</Alert>}

      {loading ? <TableSkeleton rows={6} /> : (
        <table className="data-table">
          <thead><tr><th>Ad</th><th>Slug</th><th>Üst Kategori</th><th>Sıra</th><th>Durum</th><th>İşlem</th></tr></thead>
          <tbody>
            {tree.length > 0 ? (
              <CategoryTree items={tree} onEdit={c => { setForm(c); setModal("edit"); }} onDelete={handleDelete} onToggleActive={toggleActive} />
            ) : (
              <tr><td colSpan={6} className="empty">Kayıt bulunamadı</td></tr>
            )}
          </tbody>
        </table>
      )}

      {modal && (
        <div className="modal-overlay" onClick={() => setModal(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h2>{modal === "edit" ? "Kategori Düzenle" : "Yeni Kategori"}</h2>
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
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" }}>
                <div className="form-group">
                  <label>Üst Kategori</label>
                  <select className="form-select" value={form.parentId || ""} onChange={e => setForm({ ...form, parentId: e.target.value ? Number(e.target.value) : null })}>
                    <option value="">— Kök Kategori —</option>
                    {flat.filter(c => c.id !== form.id).map(c => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>Sıra</label>
                  <input className="form-input" type="number" value={form.sortOrder || 0} onChange={e => setForm({ ...form, sortOrder: Number(e.target.value) })} />
                </div>
              </div>
              <div className="form-group">
                <label>Görsel URL</label>
                <input className="form-input" value={form.imageUrl || ""} onChange={e => setForm({ ...form, imageUrl: e.target.value })} placeholder="https://..." />
              </div>
              {form.imageUrl && (
                <div style={{ marginBottom: 16 }}>
                  <img src={form.imageUrl} alt="Önizleme" style={{ maxWidth: 200, maxHeight: 120, borderRadius: 8, objectFit: "cover" }} onError={e => { (e.target as HTMLImageElement).style.display = "none"; }} />
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
