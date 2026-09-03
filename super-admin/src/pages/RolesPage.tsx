import { useEffect, useState, useCallback, type FormEvent } from "react";
import { request, type RoleDTO, type AuthorityDTO } from "../api";

export default function RolesPage() {
  const [roles, setRoles] = useState<RoleDTO[]>([]);
  const [authorities, setAuthorities] = useState<AuthorityDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [modal, setModal] = useState<"createRole" | null>(null);
  const [form, setForm] = useState<Partial<RoleDTO>>({});

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [r, a] = await Promise.all([
        request<RoleDTO[]>("/roles"),
        request<AuthorityDTO[]>("/authorities"),
      ]);
      setRoles(Array.isArray(r) ? r : []);
      setAuthorities(Array.isArray(a) ? a : []);
    } catch (err: any) {
      setError(err.message || "Roller yüklenemedi");
    }
    setLoading(false);
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleSaveRole = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    try {
      await request("/roles", { method: "POST", json: form });
      setSuccess("Rol oluşturuldu");
      setModal(null);
      load();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const deleteRole = async (id: number) => {
    if (!confirm("Bu rolü silmek istediğinize emin misiniz?")) return;
    setError("");
    try {
      await request(`/roles/${id}`, { method: "DELETE" });
      setSuccess("Rol silindi");
      load();
    } catch (err: any) {
      setError(err.message);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1>Roller & Yetkiler</h1>
        <button className="btn btn-primary" onClick={() => { setForm({}); setModal("createRole"); }}>+ Yeni Rol</button>
      </div>

      {error && <div className="login-error" style={{ marginBottom: 12 }}>{error}</div>}
      {success && <div style={{ background: "rgba(34,197,94,0.1)", border: "1px solid var(--success)", color: "var(--success)", padding: "8px 12px", borderRadius: "var(--radius)", marginBottom: 12 }}>{success}</div>}

      {loading ? <div className="loading">Yükleniyor...</div> : (
        <>
          {/* Roles */}
          <h2 style={{ fontSize: 16, marginBottom: 12 }}>Roller ({roles.length})</h2>
          <table className="data-table" style={{ marginBottom: 32 }}>
            <thead><tr><th>ID</th><th>Kod</th><th>Ad</th><th>Oluşturulma</th><th>İşlem</th></tr></thead>
            <tbody>
              {roles.map(r => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td><span className="badge badge-info">{r.code}</span></td>
                  <td style={{ fontWeight: 500 }}>{r.name}</td>
                  <td>{r.createdAt ? new Date(r.createdAt).toLocaleDateString("tr-TR") : "—"}</td>
                  <td>
                    <button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => deleteRole(r.id)}>Sil</button>
                  </td>
                </tr>
              ))}
              {roles.length === 0 && <tr><td colSpan={5} className="empty">Kayıt bulunamadı</td></tr>}
            </tbody>
          </table>

          {/* Authorities */}
          <h2 style={{ fontSize: 16, marginBottom: 12 }}>Yetkiler ({authorities.length})</h2>
          <table className="data-table">
            <thead><tr><th>Yetki Adı</th></tr></thead>
            <tbody>
              {authorities.map(a => (
                <tr key={a.name}>
                  <td><code style={{ padding: "2px 8px", borderRadius: 4, background: "var(--surface2)", fontSize: 13 }}>{a.name}</code></td>
                </tr>
              ))}
              {authorities.length === 0 && <tr><td className="empty">Kayıt bulunamadı</td></tr>}
            </tbody>
          </table>
        </>
      )}

      {modal && (
        <div className="modal-overlay" onClick={() => setModal(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h2>Yeni Rol</h2>
            <form onSubmit={handleSaveRole}>
              <div className="form-group">
                <label>Kod *</label>
                <input className="form-input" value={form.code || ""} onChange={e => setForm({ ...form, code: e.target.value })} required placeholder="Örn: ROLE_MANAGER" />
              </div>
              <div className="form-group">
                <label>Ad *</label>
                <input className="form-input" value={form.name || ""} onChange={e => setForm({ ...form, name: e.target.value })} required placeholder="Örn: Mağaza Yöneticisi" />
              </div>
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
