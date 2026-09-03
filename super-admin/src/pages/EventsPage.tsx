import { useEffect, useState, useCallback, type FormEvent } from "react";
import { getPage, request, type EventDTO } from "../api";

const STATUSES = ["DRAFT", "PUBLISHED", "CANCELLED"];
const MOD_STATUSES = ["PENDING", "APPROVED", "REJECTED"];
const STATUS_LABELS: Record<string, string> = {
  DRAFT: "Taslak", PUBLISHED: "Yayında", CANCELLED: "İptal", COMPLETED: "Tamamlandı",
  PENDING_APPROVAL: "Onay Bekliyor",
  PENDING: "Beklemede", APPROVED: "Onaylandı", REJECTED: "Reddedildi",
  NOT_REQUIRED: "Gerekli Değil",
};
const STATUS_BADGE: Record<string, string> = {
  DRAFT: "badge-warning", PUBLISHED: "badge-success", CANCELLED: "badge-danger", COMPLETED: "badge-info",
  PENDING_APPROVAL: "badge-warning",
  PENDING: "badge-warning", APPROVED: "badge-success", REJECTED: "badge-danger",
  NOT_REQUIRED: "badge-info",
};

export default function EventsPage() {
  const [events, setEvents] = useState<EventDTO[]>([]);
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [modal, setModal] = useState<"create" | "edit" | null>(null);
  const [form, setForm] = useState<Partial<EventDTO>>({});
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);
  const [detail, setDetail] = useState<EventDTO | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const pageSize = 20;

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      // Backend returns List<EventDTO> with pagination headers (X-Total-Count)
      const data = await request<EventDTO[]>(`/events?page=${page}&size=${pageSize}&sort=id,desc`);
      setEvents(Array.isArray(data) ? data : []);
      // Get total from headers or fallback
      const countRes = await request<number>("/events/count");
      setTotal(countRes || 0);
    } catch (err: any) {
      setError(err.message || "Etkinlikler yüklenemedi");
      setEvents([]);
    }
    setLoading(false);
  }, [page]);

  useEffect(() => { load(); }, [load]);

  const openDetail = async (id: number) => {
    setDetailLoading(true);
    try {
      setDetail(await request<EventDTO>(`/events/${id}`));
    } catch (err: any) {
      setError(err.message);
    }
    setDetailLoading(false);
  };

  const badge = (s: string) => (
    <span className={`badge ${STATUS_BADGE[s] || "badge-info"}`}>{STATUS_LABELS[s] || s}</span>
  );

  const validate = (): boolean => {
    const e: Record<string, string> = {};
    if (!form.title?.trim()) e.title = "Başlık zorunludur";
    if (!form.startsAt) e.startsAt = "Başlangıç tarihi zorunludur";
    if (!form.status) e.status = "Durum zorunludur";
    setFormErrors(e);
    return Object.keys(e).length === 0;
  };

  const toISOString = (v: string) => v ? new Date(v).toISOString() : "";

  const openCreate = () => {
    const now = new Date().toISOString();
    setForm({
      status: "DRAFT",
      publishedToDiscovery: false,
      groupModerationStatus: "PENDING",
      platformModerationStatus: "PENDING",
      createdAt: now,
      updatedAt: now,
    });
    setFormErrors({});
    setModal("create");
  };

  const openEdit = (ev: EventDTO) => {
    setForm({ ...ev });
    setFormErrors({});
    setModal("edit");
  };

  const handleSave = async (e: FormEvent) => {
    e.preventDefault();
    if (!validate()) return;
    setSaving(true);
    setError("");
    setSuccess("");
    try {
      const now = new Date().toISOString();
      // Build clean payload — don't send nested objects (community/category/createdBy)
      // as MapStruct can't convert DTO back to entity for these fields
      const payload: Record<string, unknown> = {
        title: form.title,
        description: form.description || null,
        startsAt: toISOString(form.startsAt || ""),
        endsAt: form.endsAt ? toISOString(form.endsAt) : null,
        locationName: form.locationName || null,
        addressText: form.addressText || null,
        latitude: form.latitude || null,
        longitude: form.longitude || null,
        coverImageUrl: form.coverImageUrl || null,
        capacity: form.capacity || null,
        status: form.status || "DRAFT",
        rejectionReason: form.rejectionReason || null,
        publishedAt: form.publishedAt || null,
        publishedToDiscovery: form.publishedToDiscovery ?? false,
        groupModerationStatus: form.groupModerationStatus || "PENDING",
        platformModerationStatus: form.platformModerationStatus || "PENDING",
        cancelledAt: form.cancelledAt || null,
        cancellationReason: form.cancellationReason || null,
        createdAt: form.createdAt || now,
        updatedAt: now,
      };
      if (modal === "edit" && form.id) {
        await request(`/events/${form.id}`, { method: "PUT", json: payload });
        setSuccess("Etkinlik güncellendi");
      } else {
        await request("/events", { method: "POST", json: payload });
        setSuccess("Etkinlik oluşturuldu");
      }
      setModal(null);
      load();
    } catch (err: any) {
      setError(err.message);
    }
    setSaving(false);
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Bu etkinliği silmek istediğinize emin misiniz?")) return;
    setError("");
    try {
      await request(`/events/${id}`, { method: "DELETE" });
      setSuccess("Etkinlik silindi");
      load();
    } catch (err: any) {
      setError(err.message);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1>Etkinlikler ({total})</h1>
        <button className="btn btn-primary" onClick={openCreate}>+ Yeni Etkinlik</button>
      </div>

      {error && <div className="login-error" style={{ marginBottom: 12 }}>{error}</div>}
      {success && <div style={{ background: "rgba(34,197,94,0.1)", border: "1px solid var(--success)", color: "var(--success)", padding: "8px 12px", borderRadius: "var(--radius)", marginBottom: 12 }}>{success}</div>}

      {loading ? <div className="loading">Yükleniyor...</div> : (
        <>
          <table className="data-table">
            <thead><tr>
              <th>Başlık</th><th>Başlangıç</th><th>Bitiş</th><th>Konum</th><th>Kapasite</th><th>Durum</th><th>Grup Mod.</th><th>Platform Mod.</th><th>İşlemler</th>
            </tr></thead>
            <tbody>
              {events.map(ev => (
                <tr key={ev.id} style={{ cursor: "pointer" }} onClick={() => openDetail(ev.id)}>
                  <td style={{ fontWeight: 500 }}>
                    <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                      {ev.coverImageUrl && (
                        <img src={ev.coverImageUrl} alt="" style={{ width: 36, height: 36, objectFit: "cover", borderRadius: 4 }} onError={e => { (e.target as HTMLImageElement).style.display = "none"; }} />
                      )}
                      {ev.title}
                    </div>
                  </td>
                  <td>{ev.startsAt ? new Date(ev.startsAt).toLocaleString("tr-TR") : "—"}</td>
                  <td>{ev.endsAt ? new Date(ev.endsAt).toLocaleString("tr-TR") : "—"}</td>
                  <td>{ev.locationName || "—"}</td>
                  <td>{ev.capacity ?? "—"}</td>
                  <td>{badge(ev.status)}</td>
                  <td>{badge(ev.groupModerationStatus)}</td>
                  <td>{badge(ev.platformModerationStatus)}</td>
                  <td onClick={e => e.stopPropagation()} style={{ display: "flex", gap: 4 }}>
                    <button className="btn btn-ghost btn-sm" onClick={() => openEdit(ev)}>Düzenle</button>
                    <button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => handleDelete(ev.id)}>Sil</button>
                  </td>
                </tr>
              ))}
              {events.length === 0 && <tr><td colSpan={9} className="empty">Kayıt bulunamadı</td></tr>}
            </tbody>
          </table>
          <div className="pagination">
            <button className="btn btn-ghost btn-sm" disabled={page === 0} onClick={() => setPage(p => p - 1)}>← Önceki</button>
            <span>Sayfa {page + 1} / {Math.ceil(total / pageSize) || 1}</span>
            <button className="btn btn-ghost btn-sm" disabled={(page + 1) * pageSize >= total} onClick={() => setPage(p => p + 1)}>Sonraki →</button>
          </div>
        </>
      )}

      {/* ── Detail Drawer ── */}
      {(detail || detailLoading) && (
        <div className="modal-overlay" onClick={() => setDetail(null)}>
          <div className="modal" onClick={e => e.stopPropagation()} style={{ width: 640 }}>
            {detailLoading ? <div className="loading">Yükleniyor...</div> : detail && (
              <>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "start", marginBottom: 16 }}>
                  <div>
                    <h2 style={{ margin: 0 }}>{detail.title}</h2>
                    <div style={{ color: "var(--text2)", fontSize: 12, marginTop: 4 }}>ID: {detail.id}</div>
                  </div>
                  <button className="btn btn-ghost btn-sm" onClick={() => setDetail(null)}>✕</button>
                </div>

                {detail.coverImageUrl && (
                  <img src={detail.coverImageUrl} alt="" style={{ width: "100%", maxHeight: 200, objectFit: "cover", borderRadius: 8, marginBottom: 16 }} onError={e => { (e.target as HTMLImageElement).style.display = "none"; }} />
                )}

                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12, marginBottom: 16, fontSize: 13 }}>
                  <div><strong>Durum:</strong> {badge(detail.status)}</div>
                  <div><strong>Kapasite:</strong> {detail.capacity ?? "—"}</div>
                  <div><strong>Başlangıç:</strong> {detail.startsAt ? new Date(detail.startsAt).toLocaleString("tr-TR") : "—"}</div>
                  <div><strong>Bitiş:</strong> {detail.endsAt ? new Date(detail.endsAt).toLocaleString("tr-TR") : "—"}</div>
                  <div><strong>Konum:</strong> {detail.locationName || "—"}</div>
                  <div><strong>Adres:</strong> {detail.addressText || "—"}</div>
                  <div><strong>Grup Mod.:</strong> {badge(detail.groupModerationStatus)}</div>
                  <div><strong>Platform Mod.:</strong> {badge(detail.platformModerationStatus)}</div>
                  <div><strong>Keşifte Yayın:</strong> {detail.publishedToDiscovery ? "✅" : "❌"}</div>
                  {detail.publishedAt && <div><strong>Yayın Tarihi:</strong> {new Date(detail.publishedAt).toLocaleString("tr-TR")}</div>}
                  {detail.community && <div><strong>Topluluk:</strong> {detail.community.name || `#${detail.community.id}`}</div>}
                  {detail.createdBy && <div><strong>Oluşturan:</strong> {detail.createdBy.displayName || `#${detail.createdBy.id}`}</div>}
                  {detail.cancelledAt && <div><strong>İptal Tarihi:</strong> {new Date(detail.cancelledAt).toLocaleString("tr-TR")}</div>}
                  {detail.cancellationReason && <div><strong>İptal Nedeni:</strong> {detail.cancellationReason}</div>}
                  {detail.rejectionReason && <div><strong>Red Nedeni:</strong> {detail.rejectionReason}</div>}
                  <div><strong>Oluşturulma:</strong> {new Date(detail.createdAt).toLocaleString("tr-TR")}</div>
                  {detail.updatedAt && <div><strong>Güncellenme:</strong> {new Date(detail.updatedAt).toLocaleString("tr-TR")}</div>}
                </div>

                {detail.description && (
                  <div style={{ marginBottom: 16, padding: 12, background: "var(--bg)", borderRadius: 8, fontSize: 13, lineHeight: 1.6 }}>{detail.description}</div>
                )}

                <div className="modal-actions">
                  <button className="btn btn-ghost" onClick={() => { setDetail(null); openEdit(detail); }}>Düzenle</button>
                </div>
              </>
            )}
          </div>
        </div>
      )}

      {/* ── Create / Edit Modal ── */}
      {modal && (
        <div className="modal-overlay" onClick={() => setModal(null)}>
          <div className="modal" onClick={e => e.stopPropagation()} style={{ width: 600 }}>
            <h2>{modal === "edit" ? "Etkinlik Düzenle" : "Yeni Etkinlik"}</h2>
            <form onSubmit={handleSave}>
              <div className="form-group">
                <label>Başlık * {formErrors.title && <span style={{ color: "var(--danger)", fontSize: 11 }}>({formErrors.title})</span>}</label>
                <input className="form-input" value={form.title || ""} onChange={e => setForm({ ...form, title: e.target.value })} required style={formErrors.title ? { borderColor: "var(--danger)" } : undefined} />
              </div>
              <div className="form-group">
                <label>Açıklama</label>
                <textarea className="form-textarea" value={form.description || ""} onChange={e => setForm({ ...form, description: e.target.value })} />
              </div>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" }}>
                <div className="form-group">
                  <label>Başlangıç * {formErrors.startsAt && <span style={{ color: "var(--danger)", fontSize: 11 }}>({formErrors.startsAt})</span>}</label>
                  <input className="form-input" type="datetime-local" value={form.startsAt ? form.startsAt.slice(0, 16) : ""} onChange={e => setForm({ ...form, startsAt: e.target.value })} required />
                </div>
                <div className="form-group">
                  <label>Bitiş</label>
                  <input className="form-input" type="datetime-local" value={form.endsAt ? form.endsAt.slice(0, 16) : ""} onChange={e => setForm({ ...form, endsAt: e.target.value || null })} />
                </div>
                <div className="form-group">
                  <label>Konum</label>
                  <input className="form-input" value={form.locationName || ""} onChange={e => setForm({ ...form, locationName: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>Adres</label>
                  <input className="form-input" value={form.addressText || ""} onChange={e => setForm({ ...form, addressText: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>Kapasite</label>
                  <input className="form-input" type="number" min={1} value={form.capacity ?? ""} onChange={e => setForm({ ...form, capacity: e.target.value ? Number(e.target.value) : null })} />
                </div>
                <div className="form-group">
                  <label>Durum *</label>
                  <select className="form-select" value={form.status || "DRAFT"} onChange={e => setForm({ ...form, status: e.target.value })}>
                    {STATUSES.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label>Görsel URL</label>
                <input className="form-input" value={form.coverImageUrl || ""} onChange={e => setForm({ ...form, coverImageUrl: e.target.value })} placeholder="https://..." />
              </div>
              {form.coverImageUrl && (
                <div style={{ marginBottom: 16 }}>
                  <img src={form.coverImageUrl} alt="Önizleme" style={{ maxWidth: 300, maxHeight: 150, borderRadius: 8, objectFit: "cover" }} onError={e => { (e.target as HTMLImageElement).style.display = "none"; }} />
                </div>
              )}
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" }}>
                <div className="form-group">
                  <label>Grup Moderasyon</label>
                  <select className="form-select" value={form.groupModerationStatus || "PENDING"} onChange={e => setForm({ ...form, groupModerationStatus: e.target.value })}>
                    {MOD_STATUSES.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label>Platform Moderasyon</label>
                  <select className="form-select" value={form.platformModerationStatus || "PENDING"} onChange={e => setForm({ ...form, platformModerationStatus: e.target.value })}>
                    {MOD_STATUSES.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
                  </select>
                </div>
              </div>
              <label style={{ display: "flex", alignItems: "center", gap: 6, cursor: "pointer", marginBottom: 16 }}>
                <input type="checkbox" checked={form.publishedToDiscovery || false} onChange={e => setForm({ ...form, publishedToDiscovery: e.target.checked })} />
                Keşifte Yayınla
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
