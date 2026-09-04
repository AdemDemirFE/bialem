import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Alert } from "../components/Feedback";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await login(username.trim(), password);
      navigate("/");
    } catch (err: any) {
      setError(err.message || "Giriş başarısız");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <form className="login-box" onSubmit={handleSubmit}>
        <div style={{ textAlign: "center", marginBottom: 32 }}>
          <h1 className="t-title t-rise" style={{ marginBottom: 8 }}>BiAlem</h1>
          <p className="t-rise t-rise-1" style={{ margin: 0 }}>Yönetim paneline giriş yapın</p>
        </div>

        {error && <Alert kind="error">{error}</Alert>}

        <div className="form-group">
          <label>Kullanıcı Adı veya E-posta</label>
          <input
            className="form-input"
            type="text"
            placeholder="admin"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoFocus
            required
          />
        </div>

        <div className="form-group">
          <label>Şifre</label>
          <input
            className="form-input"
            type="password"
            placeholder="••••••••"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <button
          className="btn btn-primary"
          style={{ width: "100%", height: 44, fontSize: 14 }}
          disabled={loading}
        >
          {loading ? (
            <span style={{ display: "flex", alignItems: "center", gap: 8 }}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" style={{ animation: "spin 1s linear infinite" }}>
                <path d="M21 12a9 9 0 1 1-6.219-8.56" />
              </svg>
              Giriş yapılıyor…
            </span>
          ) : (
            "Giriş Yap"
          )}
        </button>

        <div style={{ textAlign: "center", marginTop: 20 }}>
          <span style={{ fontSize: 12, color: "var(--text-muted)" }}>
            BiAlem Super Admin Panel
          </span>
        </div>
      </form>

      <style>{`
        @keyframes spin { to { transform: rotate(360deg); } }
      `}</style>
    </div>
  );
}
