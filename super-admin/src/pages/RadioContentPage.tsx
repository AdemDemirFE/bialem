import { useState, useEffect, useCallback } from "react";
import { request, type RadioContentDTO } from "../api";

const CONTENT_TYPES = [
  { value: "MUSIC", label: "Müzik" },
  { value: "PODCAST", label: "Podcast" },
  { value: "ANNOUNCEMENT", label: "Duyuru" },
  { value: "PROGRAM", label: "Program" },
  { value: "JINGLE", label: "Jingle" },
  { value: "AD", label: "Reklam/Sponsor" },
  { value: "AUDIO_BOOK", label: "Sesli Kitap" },
  { value: "INTERVIEW", label: "Röportaj" },
  { value: "NEWS", label: "Haber" },
  { value: "OTHER", label: "Diğer" },
];

const SOURCE_TYPES = [
  { value: "YOUTUBE", label: "YouTube" },
  { value: "SPOTIFY", label: "Spotify" },
  { value: "AUDIO_FILE", label: "Ses Dosyası" },
  { value: "STREAM_URL", label: "Canlı Yayın URL" },
  { value: "EXTERNAL_LINK", label: "Harici Link" },
  { value: "OTHER", label: "Diğer" },
];

const CONTENT_BADGE: Record<string, string> = {
  MUSIC: "badge-info",
  PODCAST: "badge-success",
  ANNOUNCEMENT: "badge-warning",
  PROGRAM: "badge-primary",
  JINGLE: "badge-secondary",
  AD: "badge-danger",
  AUDIO_BOOK: "badge-info",
  INTERVIEW: "badge-success",
  NEWS: "badge-warning",
  OTHER: "badge-secondary",
};

const EMPTY: Partial<RadioContentDTO> = {
  title: "",
  description: "",
  contentType: "MUSIC",
  sourceType: "YOUTUBE",
  sourceUrl: "",
  audioFile: "",
  thumbnail: "",
  artist: "",
  album: "",
  duration: undefined,
  category: "",
  programName: "",
  presenter: "",
  isActive: true,
  isFeatured: false,
  sortOrder: 0,
};

export default function RadioContentPage() {
  const [items, setItems] = useState<RadioContentDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [search, setSearch] = useState("");
  const [filterType, setFilterType] = useState("");
  const [form, setForm] = useState<Partial<RadioContentDTO>>(EMPTY);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [detailItem, setDetailItem] = useState<RadioContentDTO | null>(null);
  const [deleteId, setDeleteId] = useState<number | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await request<RadioContentDTO[]>("/radio-contents");
      setItems(data);
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "Yükleme hatası");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  useEffect(() => {
    if (success) {
      const t = setTimeout(() => setSuccess(null), 3000);
      return () => clearTimeout(t);
    }
  }, [success]);

  const filtered = items.filter((i) => {
    const matchSearch = !search ||
      i.title.toLowerCase().includes(search.toLowerCase()) ||
      i.artist?.toLowerCase().includes(search.toLowerCase()) ||
      i.category?.toLowerCase().includes(search.toLowerCase());
    const matchType = !filterType || i.contentType === filterType;
    return matchSearch && matchType;
  });

  const openCreate = () => {
    setForm({ ...EMPTY });
    setEditingId(null);
    setShowForm(true);
  };

  const openEdit = (item: RadioContentDTO) => {
    setForm({ ...item });
    setEditingId(item.id);
    setShowForm(true);
  };

  const handleSave = async () => {
    try {
      const now = new Date().toISOString();
      const body = {
        ...form,
        createdAt: form.createdAt || now,
        updatedAt: now,
      };
      if (editingId) {
        await request<RadioContentDTO>(`/radio-contents/${editingId}`, { method: "PUT", json: body });
        setSuccess("İçerik güncellendi");
      } else {
        await request<RadioContentDTO>("/radio-contents", { method: "POST", json: body });
        setSuccess("İçerik oluşturuldu");
      }
      setShowForm(false);
      load();
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "Kaydetme hatası");
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await request(`/radio-contents/${id}`, { method: "DELETE" });
      setSuccess("İçerik silindi");
      setDeleteId(null);
      load();
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "Silme hatası");
    }
  };

  const formatDuration = (s: number | null) => {
    if (!s) return "-";
    const h = Math.floor(s / 3600);
    const m = Math.floor((s % 3600) / 60);
    const sec = s % 60;
    if (h > 0) return `${h}sa ${m}dk`;
    return `${m}dk ${sec}sn`;
  };

  const formatDate = (d: string | null) => {
    if (!d) return "-";
    return new Date(d).toLocaleDateString("tr-TR", { day: "2-digit", month: "2-digit", year: "numeric" });
  };

  return (
    <div>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
        <h1>📻 Radyo İçerikleri</h1>
        <button className="btn btn-primary" onClick={openCreate}>+ Yeni İçerik</button>
      </div>

      {success && <div className="alert alert-success">{success}</div>}
      {error && <div className="alert alert-danger">{error}</div>}

      <div style={{ display: "flex", gap: 12, marginBottom: 16 }}>
        <input
          type="text"
          placeholder="İçerik ara..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="form-input"
          style={{ flex: 1 }}
        />
        <select value={filterType} onChange={(e) => setFilterType(e.target.value)} className="form-input" style={{ width: 180 }}>
          <option value="">Tüm Türler</option>
          {CONTENT_TYPES.map(ct => <option key={ct.value} value={ct.value}>{ct.label}</option>)}
        </select>
      </div>

      {loading ? (
        <div className="loading">Yükleniyor...</div>
      ) : (
        <div className="table-wrapper">
          <table className="table">
            <thead>
              <tr>
                <th>Başlık</th>
                <th>Tür</th>
                <th>Kaynak</th>
                <th>Sunucu</th>
                <th>Kategori</th>
                <th>Süre</th>
                <th>Oynatma</th>
                <th>Durum</th>
                <th>Öne Çıkan</th>
                <th>Sıra</th>
                <th>İşlemler</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((item) => (
                <tr key={item.id}>
                  <td>
                    <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                      {item.thumbnail && <img src={item.thumbnail} alt="" style={{ width: 32, height: 32, borderRadius: 4, objectFit: "cover" }} />}
                      <div>
                        <strong>{item.title}</strong>
                        {item.artist && <div style={{ fontSize: 12, color: "#999" }}>{item.artist}</div>}
                      </div>
                    </div>
                  </td>
                  <td><span className={`badge ${CONTENT_BADGE[item.contentType] || "badge-secondary"}`}>{CONTENT_TYPES.find(ct => ct.value === item.contentType)?.label || item.contentType}</span></td>
                  <td>{SOURCE_TYPES.find(st => st.value === item.sourceType)?.label || item.sourceType}</td>
                  <td>{item.presenter || "-"}</td>
                  <td>{item.category || "-"}</td>
                  <td>{formatDuration(item.duration)}</td>
                  <td>{item.playCount || 0}</td>
                  <td>
                    <span className={`badge ${item.isActive ? "badge-success" : "badge-danger"}`}>
                      {item.isActive ? "Aktif" : "Pasif"}
                    </span>
                  </td>
                  <td>{item.isFeatured ? "⭐" : ""}</td>
                  <td>{item.sortOrder ?? "-"}</td>
                  <td>
                    <div style={{ display: "flex", gap: 4 }}>
                      <button className="btn btn-sm" onClick={() => setDetailItem(item)}>👁️</button>
                      <button className="btn btn-sm" onClick={() => openEdit(item)}>✏️</button>
                      <button className="btn btn-sm btn-danger" onClick={() => setDeleteId(item.id)}>🗑️</button>
                    </div>
                  </td>
                </tr>
              ))}
              {filtered.length === 0 && (
                <tr><td colSpan={11} style={{ textAlign: "center", padding: 24, color: "#888" }}>İçerik bulunamadı</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Detail Modal */}
      {detailItem && (
        <div className="modal-overlay" onClick={() => setDetailItem(null)}>
          <div className="modal" style={{ maxWidth: 600 }} onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>İçerik Detay</h2>
              <button onClick={() => setDetailItem(null)}>✕</button>
            </div>
            <div className="modal-body">
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
                <div><strong>Başlık:</strong> {detailItem.title}</div>
                <div><strong>Tür:</strong> {CONTENT_TYPES.find(ct => ct.value === detailItem.contentType)?.label}</div>
                <div><strong>Kaynak:</strong> {SOURCE_TYPES.find(st => st.value === detailItem.sourceType)?.label}</div>
                <div><strong>Süre:</strong> {formatDuration(detailItem.duration)}</div>
                <div><strong>Sanatçı:</strong> {detailItem.artist || "-"}</div>
                <div><strong>Albüm:</strong> {detailItem.album || "-"}</div>
                <div><strong>Kategori:</strong> {detailItem.category || "-"}</div>
                <div><strong>Program:</strong> {detailItem.programName || "-"}</div>
                <div><strong>Sunucu:</strong> {detailItem.presenter || "-"}</div>
                <div><strong>Oynatma:</strong> {detailItem.playCount || 0}</div>
                <div><strong>Sıra:</strong> {detailItem.sortOrder ?? "-"}</div>
                <div><strong>Durum:</strong> {detailItem.isActive ? "✅ Aktif" : "❌ Pasif"}</div>
                <div style={{ gridColumn: "1 / -1" }}><strong>Açıklama:</strong> {detailItem.description || "-"}</div>
                <div style={{ gridColumn: "1 / -1" }}><strong>Kaynak URL:</strong> {detailItem.sourceUrl ? <a href={detailItem.sourceUrl} target="_blank" rel="noreferrer">{detailItem.sourceUrl}</a> : "-"}</div>
                <div style={{ gridColumn: "1 / -1" }}><strong>Ses Dosyası:</strong> {detailItem.audioFile || "-"}</div>
                <div><strong>Kapak:</strong> {detailItem.thumbnail ? <img src={detailItem.thumbnail} alt="" style={{ width: 60, borderRadius: 4 }} /> : "-"}</div>
                <div><strong>Oluşturulma:</strong> {formatDate(detailItem.createdAt)}</div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Create/Edit Modal */}
      {showForm && (
        <div className="modal-overlay" onClick={() => setShowForm(false)}>
          <div className="modal" style={{ maxWidth: 700, maxHeight: "85vh", overflow: "auto" }} onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editingId ? "İçerik Düzenle" : "Yeni İçerik"}</h2>
              <button onClick={() => setShowForm(false)}>✕</button>
            </div>
            <div className="modal-body">
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
                <div className="form-group" style={{ gridColumn: "1 / -1" }}>
                  <label>Başlık *</label>
                  <input className="form-input" value={form.title || ""} onChange={(e) => setForm({ ...form, title: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>İçerik Türü *</label>
                  <select className="form-input" value={form.contentType || "MUSIC"} onChange={(e) => setForm({ ...form, contentType: e.target.value as RadioContentDTO["contentType"] })}>
                    {CONTENT_TYPES.map(ct => <option key={ct.value} value={ct.value}>{ct.label}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label>Kaynak Türü *</label>
                  <select className="form-input" value={form.sourceType || "YOUTUBE"} onChange={(e) => setForm({ ...form, sourceType: e.target.value as RadioContentDTO["sourceType"] })}>
                    {SOURCE_TYPES.map(st => <option key={st.value} value={st.value}>{st.label}</option>)}
                  </select>
                </div>
                <div className="form-group" style={{ gridColumn: "1 / -1" }}>
                  <label>Kaynak URL</label>
                  <input className="form-input" value={form.sourceUrl || ""} onChange={(e) => setForm({ ...form, sourceUrl: e.target.value })} placeholder="https://..." />
                </div>
                <div className="form-group" style={{ gridColumn: "1 / -1" }}>
                  <label>Ses Dosyası URL</label>
                  <input className="form-input" value={form.audioFile || ""} onChange={(e) => setForm({ ...form, audioFile: e.target.value })} />
                </div>
                <div className="form-group" style={{ gridColumn: "1 / -1" }}>
                  <label>Kapak Resmi URL</label>
                  <input className="form-input" value={form.thumbnail || ""} onChange={(e) => setForm({ ...form, thumbnail: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>Sanatçı</label>
                  <input className="form-input" value={form.artist || ""} onChange={(e) => setForm({ ...form, artist: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>Albüm</label>
                  <input className="form-input" value={form.album || ""} onChange={(e) => setForm({ ...form, album: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>Süre (saniye)</label>
                  <input type="number" className="form-input" value={form.duration || ""} onChange={(e) => setForm({ ...form, duration: e.target.value ? Number(e.target.value) : undefined })} />
                </div>
                <div className="form-group">
                  <label>Kategori</label>
                  <input className="form-input" value={form.category || ""} onChange={(e) => setForm({ ...form, category: e.target.value })} placeholder="Haberler, Müzik, vb." />
                </div>
                <div className="form-group">
                  <label>Program Adı</label>
                  <input className="form-input" value={form.programName || ""} onChange={(e) => setForm({ ...form, programName: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>Sunucu</label>
                  <input className="form-input" value={form.presenter || ""} onChange={(e) => setForm({ ...form, presenter: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>Sıra</label>
                  <input type="number" className="form-input" value={form.sortOrder ?? 0} onChange={(e) => setForm({ ...form, sortOrder: Number(e.target.value) })} />
                </div>
                <div className="form-group">
                  <label style={{ display: "flex", alignItems: "center", gap: 8 }}>
                    <input type="checkbox" checked={form.isActive ?? true} onChange={(e) => setForm({ ...form, isActive: e.target.checked })} />
                    Aktif
                  </label>
                </div>
                <div className="form-group">
                  <label style={{ display: "flex", alignItems: "center", gap: 8 }}>
                    <input type="checkbox" checked={form.isFeatured ?? false} onChange={(e) => setForm({ ...form, isFeatured: e.target.checked })} />
                    Öne Çıkan
                  </label>
                </div>
                <div className="form-group" style={{ gridColumn: "1 / -1" }}>
                  <label>Açıklama</label>
                  <textarea className="form-input" rows={3} value={form.description || ""} onChange={(e) => setForm({ ...form, description: e.target.value })} />
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn" onClick={() => setShowForm(false)}>İptal</button>
              <button className="btn btn-primary" onClick={handleSave}>{editingId ? "Güncelle" : "Oluştur"}</button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirmation */}
      {deleteId && (
        <div className="modal-overlay" onClick={() => setDeleteId(null)}>
          <div className="modal" style={{ maxWidth: 400 }} onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Silme Onayı</h2>
              <button onClick={() => setDeleteId(null)}>✕</button>
            </div>
            <div className="modal-body">
              <p>Bu içeriği silmek istediğinizden emin misiniz?</p>
            </div>
            <div className="modal-footer">
              <button className="btn" onClick={() => setDeleteId(null)}>İptal</button>
              <button className="btn btn-danger" onClick={() => handleDelete(deleteId)}>Sil</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
