import { useEffect, useState, useCallback, type FormEvent } from "react";
import { request, type AdminUserDTO } from "../api";
import { Alert, TableSkeleton } from "../components/Feedback";

export default function UsersPage() {
  const [users, setUsers] = useState<AdminUserDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [modal, setModal] = useState<"create" | "edit" | null>(null);
  const [form, setForm] = useState<Partial<AdminUserDTO>>({});

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await request<AdminUserDTO[]>("/admin/users");
      setUsers(Array.isArray(data) ? data : []);
    } catch (err: any) {
      setError(err.message || "Kullanıcılar yüklenemedi");
      setUsers([]);
    }
    setLoading(false);
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleSave = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    try {
      if (modal === "edit" && form.id) {
        await request(`/admin/users`, { method: "PUT", json: form });
        setSuccess("Kullanıcı güncellendi");
      } else {
        await request("/admin/users", { method: "POST", json: form });
        setSuccess("Kullanıcı oluşturuldu");
      }
      setModal(null);
      load();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const handleDelete = async (login: string) => {
    if (!confirm(`"${login}" kullanıcısını silmek istediğinize emin misiniz?`)) return;
    setError("");
    try {
      await request(`/admin/users/by-login/${login}`, { method: "DELETE" });
      setSuccess("Kullanıcı silindi");
      load();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const toggleActive = async (u: AdminUserDTO) => {
    setError("");
    try {
      const endpoint = u.activated ? "deactivate" : "activate";
      await request(`/admin/users/${u.id}/${endpoint}`, { method: "POST" });
      setSuccess(u.activated ? "Kullanıcı pasifleştirildi" : "Kullanıcı aktifleştirildi");
      load();
    } catch (err: any) {
      setError(err.message);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1>Kullanıcılar ({users.length})</h1>
        <button className="btn btn-primary" onClick={() => { setForm({}); setModal("create"); }}>+ Yeni Kullanıcı</button>
      </div>

      {error && <Alert kind="error">{error}</Alert>}
      {success && <Alert kind="success">{success}</Alert>}

      {loading ? <TableSkeleton rows={6} /> : (
        <table className="data-table">
          <thead><tr>
            <th>ID</th><th>Login</th><th>Ad Soyad</th><th>E-posta</th><th>Durum</th><th>Roller</th><th>İşlemler</th>
          </tr></thead>
          <tbody>
            {users.map(u => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td style={{ fontWeight: 500 }}>{u.login}</td>
                <td>{u.firstName} {u.lastName}</td>
                <td>{u.email}</td>
                <td>
                  <span className={`badge ${u.activated ? "badge-success" : "badge-danger"}`}>
                    {u.activated ? "Aktif" : "Pasif"}
                  </span>
                </td>
                <td>
                  {u.authorities?.map(a => (
                    <span key={a} className="badge badge-info" style={{ marginRight: 4, fontSize: 11 }}>{a.replace("ROLE_", "")}</span>
                  ))}
                </td>
                <td style={{ display: "flex", gap: 4, flexWrap: "wrap" }}>
                  <button className="btn btn-ghost btn-sm" onClick={() => { setForm(u); setModal("edit"); }}>Düzenle</button>
                  <button className="btn btn-ghost btn-sm" onClick={() => toggleActive(u)}>
                    {u.activated ? "Pasifleştir" : "Aktifleştir"}
                  </button>
                  <button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => handleDelete(u.login)}>Sil</button>
                </td>
              </tr>
            ))}
            {users.length === 0 && <tr><td colSpan={7} className="empty">Kayıt bulunamadı</td></tr>}
          </tbody>
        </table>
      )}

      {modal && (
        <div className="modal-overlay" onClick={() => setModal(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h2>{modal === "edit" ? "Kullanıcı Düzenle" : "Yeni Kullanıcı"}</h2>
            <form onSubmit={handleSave}>
              <div className="form-group">
                <label>Login *</label>
                <input className="form-input" value={form.login || ""} onChange={e => setForm({ ...form, login: e.target.value })} required disabled={modal === "edit"} />
              </div>
              <div className="form-group">
                <label>E-posta *</label>
                <input className="form-input" type="email" value={form.email || ""} onChange={e => setForm({ ...form, email: e.target.value })} required />
              </div>
              {modal === "create" && (
                <div className="form-group">
                  <label>Şifre *</label>
                  <input className="form-input" type="password" value={(form as any).password || ""} onChange={e => setForm({ ...form, password: e.target.value } as any)} required />
                </div>
              )}
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" }}>
                <div className="form-group">
                  <label>Ad</label>
                  <input className="form-input" value={form.firstName || ""} onChange={e => setForm({ ...form, firstName: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>Soyad</label>
                  <input className="form-input" value={form.lastName || ""} onChange={e => setForm({ ...form, lastName: e.target.value })} />
                </div>
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
