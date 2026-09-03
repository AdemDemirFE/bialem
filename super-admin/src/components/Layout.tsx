import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const NAV = [
  { section: "Genel" },
  { to: "/", label: "Dashboard", icon: "📊" },
  { section: "Kullanıcılar" },
  { to: "/users", label: "Kullanıcılar", icon: "👤" },
  { to: "/profiles", label: "Profiller", icon: "🧑" },
  { to: "/roles", label: "Roller & Yetkiler", icon: "🔐" },
  { section: "Mağaza" },
  { to: "/products", label: "Ürünler", icon: "📦" },
  { to: "/categories", label: "Kategoriler", icon: "📂" },
  { to: "/brands", label: "Markalar", icon: "🏷️" },
  { to: "/orders", label: "Siparişler", icon: "🛒" },
  { to: "/shipments", label: "Kargolar", icon: "🚚" },
  { section: "Topluluk" },
  { to: "/events", label: "Etkinlikler", icon: "🎉" },
  { to: "/communities", label: "Topluluklar", icon: "👥" },
  { to: "/comments", label: "Yorumlar", icon: "💬" },
  { section: "Yönetim" },
  { to: "/reports", label: "Raporlar", icon: "⚠️" },
  { to: "/notifications", label: "Bildirimler", icon: "🔔" },
  { to: "/templates", label: "Bildirim Şablonları", icon: "📝" },
];

export default function Layout({ children }: { children: React.ReactNode }) {
  const { user, isSuperAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-header">
          <h2>Bialem Admin</h2>
          <small>{isSuperAdmin ? "🔑 SUPER_ADMIN" : "👤 ADMIN"}</small>
        </div>
        <nav className="sidebar-nav">
          {NAV.map((item, i) =>
            item.section ? (
              <div key={i} className="sidebar-section">{item.section}</div>
            ) : (
              <NavLink
                key={item.to}
                to={item.to!}
                end={item.to === "/"}
                className={({ isActive }) =>
                  `sidebar-link${isActive ? " active" : ""}`
                }
              >
                {item.icon} {item.label}
              </NavLink>
            )
          )}
        </nav>
        <div className="sidebar-footer">
          <div>{user?.firstName} {user?.lastName}</div>
          <div style={{ fontSize: 11, marginTop: 2 }}>{user?.email}</div>
          <button onClick={handleLogout}>Çıkış Yap</button>
        </div>
      </aside>
      <main className="main-content">{children}</main>
    </div>
  );
}
