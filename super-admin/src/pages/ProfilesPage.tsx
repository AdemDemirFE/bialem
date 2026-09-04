import { useEffect, useState, useCallback } from "react";
import { request, type ProfileDTO } from "../api";
import { Alert, TableSkeleton } from "../components/Feedback";

export default function ProfilesPage() {
  const [profiles, setProfiles] = useState<ProfileDTO[]>([]);
  const [filtered, setFiltered] = useState<ProfileDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [search, setSearch] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await request<ProfileDTO[]>("/profiles");
      setProfiles(Array.isArray(data) ? data : []);
      setFiltered(Array.isArray(data) ? data : []);
    } catch (err: any) {
      setError(err.message || "Profiller yüklenemedi");
      setProfiles([]);
      setFiltered([]);
    }
    setLoading(false);
  }, []);

  useEffect(() => { load(); }, [load]);

  useEffect(() => {
    if (!search.trim()) {
      setFiltered(profiles);
    } else {
      const q = search.toLowerCase();
      setFiltered(profiles.filter(p =>
        p.username?.toLowerCase().includes(q) ||
        p.displayName?.toLowerCase().includes(q) ||
        p.city?.toLowerCase().includes(q)
      ));
    }
  }, [search, profiles]);

  const handleDelete = async (id: number) => {
    if (!confirm("Bu profili silmek istediğinize emin misiniz?")) return;
    setError("");
    try {
      await request(`/profiles/${id}`, { method: "DELETE" });
      setSuccess("Profil silindi");
      load();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const STATUS_LABELS: Record<string, string> = {
    ACTIVE: "Aktif", PENDING_VERIFICATION: "Doğrulama Bekliyor", SUSPENDED: "Askıya Alınmış",
  };

  return (
    <div>
      <div className="page-header">
        <h1>Profiller ({profiles.length})</h1>
        <input className="form-input" style={{ width: 250 }} placeholder="Kullanıcı adı, görünen ad veya şehir ara..." value={search} onChange={e => setSearch(e.target.value)} />
      </div>

      {error && <Alert kind="error">{error}</Alert>}
      {success && <Alert kind="success">{success}</Alert>}

      {loading ? <TableSkeleton rows={6} /> : (
        <table className="data-table">
          <thead><tr>
            <th>ID</th><th>Kullanıcı Adı</th><th>Görünen Ad</th><th>Şehir</th><th>Durum</th><th>Doğrulanmış</th><th>Oluşturulma</th><th>İşlem</th>
          </tr></thead>
          <tbody>
            {filtered.map(p => (
              <tr key={p.id}>
                <td>{p.id}</td>
                <td style={{ fontWeight: 500 }}>{p.username}</td>
                <td>{p.displayName}</td>
                <td>{p.city || "—"}</td>
                <td>
                  <span className={`badge ${p.status === "ACTIVE" ? "badge-success" : p.status === "SUSPENDED" ? "badge-danger" : "badge-warning"}`}>
                    {STATUS_LABELS[p.status] || p.status}
                  </span>
                </td>
                <td>{p.isVerified ? "✅" : "❌"}</td>
                <td>{p.createdAt ? new Date(p.createdAt).toLocaleDateString("tr-TR") : "—"}</td>
                <td>
                  <button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => handleDelete(p.id)}>Sil</button>
                </td>
              </tr>
            ))}
            {filtered.length === 0 && <tr><td colSpan={8} className="empty">{search ? "Arama sonucu bulunamadı" : "Kayıt bulunamadı"}</td></tr>}
          </tbody>
        </table>
      )}
    </div>
  );
}
