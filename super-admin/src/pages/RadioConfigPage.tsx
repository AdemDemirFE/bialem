import { useState, useEffect, useCallback } from "react";
import { request, type RadioConfigDTO } from "../api";

export default function RadioConfigPage() {
  const [config, setConfig] = useState<RadioConfigDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [form, setForm] = useState<Partial<RadioConfigDTO>>({});

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await request<RadioConfigDTO>("/radio-configs/latest");
      setConfig(data);
      setForm({ ...data });
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

  const handleSave = async () => {
    if (!config) return;
    try {
      const now = new Date().toISOString();
      const body = { ...form, updatedAt: now };
      const updated = await request<RadioConfigDTO>(`/radio-configs/${config.id}`, { method: "PUT", json: body });
      setConfig(updated);
      setForm({ ...updated });
      setSuccess("Radyo ayarları güncellendi");
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "Kaydetme hatası");
    }
  };

  if (loading) return <div className="loading">Yükleniyor...</div>;

  return (
    <div>
      <h1>📻 Radyo Ayarları — {config?.radioName || "Karasu Belediye Radyo"}</h1>

      {success && <div className="alert alert-success">{success}</div>}
      {error && <div className="alert alert-danger">{error}</div>}

      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16, maxWidth: 900 }}>
        {/* Station Info */}
        <div className="card" style={{ gridColumn: "1 / -1" }}>
          <h3>İstasyon Bilgileri</h3>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
            <div className="form-group">
              <label>Radyo Adı</label>
              <input className="form-input" value={form.radioName || ""} onChange={(e) => setForm({ ...form, radioName: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Slogan</label>
              <input className="form-input" value={form.slogan || ""} onChange={(e) => setForm({ ...form, slogan: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Logo URL</label>
              <input className="form-input" value={form.logo || ""} onChange={(e) => setForm({ ...form, logo: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Kapak Resmi URL</label>
              <input className="form-input" value={form.cover || ""} onChange={(e) => setForm({ ...form, cover: e.target.value })} />
            </div>
          </div>
          {config?.logo && (
            <div style={{ marginTop: 8 }}>
              <img src={config.logo} alt="Logo" style={{ height: 48, borderRadius: 8 }} />
            </div>
          )}
        </div>

        {/* Live Stream */}
        <div className="card" style={{ gridColumn: "1 / -1" }}>
          <h3>Canlı Yayın</h3>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
            <div className="form-group">
              <label>Canlı Yayın URL</label>
              <input className="form-input" value={form.liveStreamUrl || ""} onChange={(e) => setForm({ ...form, liveStreamUrl: e.target.value })} placeholder="https://stream.karasu.bel.tr/live" />
            </div>
            <div className="form-group">
              <label style={{ display: "flex", alignItems: "center", gap: 8, marginTop: 24 }}>
                <input type="checkbox" checked={form.isLive ?? false} onChange={(e) => setForm({ ...form, isLive: e.target.checked })} />
                Canlı Yayın Aktif
              </label>
            </div>
            <div className="form-group">
              <label>Mevcut Program</label>
              <input className="form-input" value={form.currentProgram || ""} onChange={(e) => setForm({ ...form, currentProgram: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Mevcut Şarkı/İçerik</label>
              <input className="form-input" value={form.currentTrack || ""} onChange={(e) => setForm({ ...form, currentTrack: e.target.value })} />
            </div>
          </div>
        </div>

        {/* Social Links */}
        <div className="card" style={{ gridColumn: "1 / -1" }}>
          <h3>Sosyal Medya & İletişim</h3>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
            <div className="form-group">
              <label>🌐 Web Sitesi</label>
              <input className="form-input" value={form.websiteUrl || ""} onChange={(e) => setForm({ ...form, websiteUrl: e.target.value })} />
            </div>
            <div className="form-group">
              <label>📘 Facebook</label>
              <input className="form-input" value={form.facebookUrl || ""} onChange={(e) => setForm({ ...form, facebookUrl: e.target.value })} />
            </div>
            <div className="form-group">
              <label>🐦 Twitter</label>
              <input className="form-input" value={form.twitterUrl || ""} onChange={(e) => setForm({ ...form, twitterUrl: e.target.value })} />
            </div>
            <div className="form-group">
              <label>📷 Instagram</label>
              <input className="form-input" value={form.instagramUrl || ""} onChange={(e) => setForm({ ...form, instagramUrl: e.target.value })} />
            </div>
            <div className="form-group" style={{ gridColumn: "1 / -1" }}>
              <label>▶️ YouTube</label>
              <input className="form-input" value={form.youtubeUrl || ""} onChange={(e) => setForm({ ...form, youtubeUrl: e.target.value })} />
            </div>
          </div>
        </div>

        {/* Metadata */}
        <div className="card" style={{ gridColumn: "1 / -1" }}>
          <h3>Meta Veriler</h3>
          <div className="form-group">
            <label>JSON Metadata</label>
            <textarea
              className="form-input"
              rows={4}
              value={form.metadataJson || ""}
              onChange={(e) => setForm({ ...form, metadataJson: e.target.value })}
              placeholder='{"frequency": "89.5 FM", "city": "Karasu"}'
            />
          </div>
        </div>
      </div>

      <div style={{ marginTop: 16, display: "flex", gap: 8 }}>
        <button className="btn" onClick={load}>🔄 Sıfırla</button>
        <button className="btn btn-primary" onClick={handleSave}>💾 Kaydet</button>
      </div>

      {/* Status display */}
      {config && (
        <div className="card" style={{ marginTop: 16, maxWidth: 900 }}>
          <h3>Mevcut Durum</h3>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: 16 }}>
            <div className="stat-card">
              <div className="stat-value">{config.radioName}</div>
              <div className="stat-label">İsim</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{config.isLive ? "🟢 Canlı" : "⚫ Çevrimdışı"}</div>
              <div className="stat-label">Yayın Durumu</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{config.currentProgram || "-"}</div>
              <div className="stat-label">Program</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{config.currentTrack || "-"}</div>
              <div className="stat-label">İçerik</div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
