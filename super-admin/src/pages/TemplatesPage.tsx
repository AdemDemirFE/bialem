import { useEffect, useState, useCallback, type FormEvent } from "react";
import { request, type NotificationTemplateDTO } from "../api";

export default function TemplatesPage() {
  const [items, setItems] = useState<NotificationTemplateDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState<"create" | "edit" | null>(null);
  const [form, setForm] = useState<Partial<NotificationTemplateDTO>>({});

  const load = useCallback(async () => {
    setLoading(true);
    try { setItems(await request<NotificationTemplateDTO[]>("/admin/notification-templates")); }
    catch { setItems([]); }
    setLoading(false);
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleSave = async (e: FormEvent) => {
    e.preventDefault();
    try {
      if (modal === "edit" && form.id) {
        await request(`/admin/notification-templates/${form.id}`, { method: "PUT", json: form });
      } else {
        await request("/admin/notification-templates", { method: "POST", json: form });
      }
      setModal(null); load();
    } catch (err: any) { alert(err.message); }
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Bu şablonu silmek istediğinize emin misiniz?")) return;
    await request(`/admin/notification-templates/${id}`, { method: "DELETE" });
    load();
  };

  return (
    <div>
      <div className="page-header">
        <h1>Bildirim Şablonları ({items.length})</h1>
        <button className="btn btn-primary" onClick={() => { setForm({ enabled: true, inAppEnabled: true, pushEnabled: true, priority: "NORMAL", scheduleType: "IMMEDIATE", timezone: "Europe/Istanbul" }); setModal("create"); }}>+ Yeni Şablon</button>
      </div>
      {loading ? <div className="loading">Yükleniyor...</div> : (
        <table className="data-table">
          <thead><tr><th>ID</th><th>Kod</th><th>Tür</th><th>Ad</th><th>Öncelik</th><th>Aktif</th><th>İşlemler</th></tr></thead>
          <tbody>
            {items.map(t => (
              <tr key={t.id}>
                <td>{t.id}</td><td>{t.code}</td><td>{t.eventType}</td><td>{t.name}</td>
                <td><span className={`badge ${t.priority === "HIGH" ? "badge-danger" : "badge-info"}`}>{t.priority}</span></td>
                <td>{t.enabled ? "✅" : "❌"}</td>
                <td style={{ display: "flex", gap: 4 }}>
                  <button className="btn btn-ghost btn-sm" onClick={() => { setForm(t); setModal("edit"); }}>Düzenle</button>
                  <button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => handleDelete(t.id)}>Sil</button>
                </td>
              </tr>
            ))}
            {items.length === 0 && <tr><td colSpan={7} className="empty">Kayıt bulunamadı</td></tr>}
          </tbody>
        </table>
      )}

      {modal && (
        <div className="modal-overlay" onClick={() => setModal(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h2>{modal === "edit" ? "Şablon Düzenle" : "Yeni Şablon"}</h2>
            <form onSubmit={handleSave}>
              <div className="form-group"><label>Kod</label><input className="form-input" value={form.code || ""} onChange={e => setForm({ ...form, code: e.target.value })} required /></div>
              <div className="form-group"><label>Olay Türü</label><input className="form-input" value={form.eventType || ""} onChange={e => setForm({ ...form, eventType: e.target.value })} required /></div>
              <div className="form-group"><label>Ad</label><input className="form-input" value={form.name || ""} onChange={e => setForm({ ...form, name: e.target.value })} required /></div>
              <div className="form-group"><label>Başlık Şablonu</label><input className="form-input" value={form.titleTemplate || ""} onChange={e => setForm({ ...form, titleTemplate: e.target.value })} required /></div>
              <div className="form-group"><label>Gövde Şablonu</label><textarea className="form-textarea" value={form.bodyTemplate || ""} onChange={e => setForm({ ...form, bodyTemplate: e.target.value })} /></div>
              <div className="modal-actions">
                <button type="button" className="btn btn-ghost" onClick={() => setModal(null)}>İptal</button>
                <button type="submit" className="btn btn-primary">Kaydet</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
