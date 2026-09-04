import { Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "./context/AuthContext";
import Layout from "./components/Layout";
import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import UsersPage from "./pages/UsersPage";
import ProfilesPage from "./pages/ProfilesPage";
import EventsPage from "./pages/EventsPage";
import CommunitiesPage from "./pages/CommunitiesPage";
import ProductsPage from "./pages/ProductsPage";
import CategoriesPage from "./pages/CategoriesPage";
import BrandsPage from "./pages/BrandsPage";
import OrdersPage from "./pages/OrdersPage";
import ShipmentsPage from "./pages/ShipmentsPage";
import ReportsPage from "./pages/ReportsPage";
import NotificationsPage from "./pages/NotificationsPage";
import TemplatesPage from "./pages/TemplatesPage";
import RolesPage from "./pages/RolesPage";
import CommentsPage from "./pages/CommentsPage";
import RadioContentPage from "./pages/RadioContentPage";
import RadioConfigPage from "./pages/RadioConfigPage";

function ProtectedRoute({ children, requireSuperAdmin = false }: {
  children: React.ReactNode;
  requireSuperAdmin?: boolean;
}) {
  const { user, loading, isSuperAdmin } = useAuth();
  if (loading) return <div className="loading">Yükleniyor...</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (requireSuperAdmin && !isSuperAdmin) {
    return <div className="main-content"><h1>403 — Erişim Engellendi</h1><p>Sadece SUPER_ADMIN kullanıcıları bu alana erişebilir.</p></div>;
  }
  return <Layout>{children}</Layout>;
}

export default function App() {
  const { user, loading } = useAuth();

  if (loading) return <div className="loading" style={{ display: "flex", alignItems: "center", justifyContent: "center", height: "100vh" }}>Yükleniyor...</div>;

  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to="/" replace /> : <LoginPage />} />
      <Route path="/" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
      <Route path="/users" element={<ProtectedRoute><UsersPage /></ProtectedRoute>} />
      <Route path="/profiles" element={<ProtectedRoute><ProfilesPage /></ProtectedRoute>} />
      <Route path="/events" element={<ProtectedRoute><EventsPage /></ProtectedRoute>} />
      <Route path="/communities" element={<ProtectedRoute><CommunitiesPage /></ProtectedRoute>} />
      <Route path="/products" element={<ProtectedRoute><ProductsPage /></ProtectedRoute>} />
      <Route path="/categories" element={<ProtectedRoute><CategoriesPage /></ProtectedRoute>} />
      <Route path="/brands" element={<ProtectedRoute><BrandsPage /></ProtectedRoute>} />
      <Route path="/orders" element={<ProtectedRoute><OrdersPage /></ProtectedRoute>} />
      <Route path="/shipments" element={<ProtectedRoute><ShipmentsPage /></ProtectedRoute>} />
      <Route path="/reports" element={<ProtectedRoute><ReportsPage /></ProtectedRoute>} />
      <Route path="/notifications" element={<ProtectedRoute><NotificationsPage /></ProtectedRoute>} />
      <Route path="/templates" element={<ProtectedRoute><TemplatesPage /></ProtectedRoute>} />
      <Route path="/roles" element={<ProtectedRoute><RolesPage /></ProtectedRoute>} />
      <Route path="/comments" element={<ProtectedRoute><CommentsPage /></ProtectedRoute>} />
      <Route path="/radio-contents" element={<ProtectedRoute><RadioContentPage /></ProtectedRoute>} />
      <Route path="/radio-config" element={<ProtectedRoute><RadioConfigPage /></ProtectedRoute>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
