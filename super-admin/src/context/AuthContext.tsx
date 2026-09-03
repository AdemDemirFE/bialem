import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import {
  authenticate,
  getAccount,
  getAdminContext,
  setToken,
  getToken,
  type AccountInfo,
  type AdminContext,
} from "../api";

interface AuthState {
  user: AccountInfo | null;
  ctx: AdminContext | null;
  loading: boolean;
  isSuperAdmin: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthState>(null!);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AccountInfo | null>(null);
  const [ctx, setCtx] = useState<AdminContext | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!getToken()) {
      setLoading(false);
      return;
    }
    Promise.all([getAccount(), getAdminContext()])
      .then(([a, c]) => {
        setUser(a);
        setCtx(c);
      })
      .catch(() => {
        setToken(null);
      })
      .finally(() => setLoading(false));
  }, []);

  const login = async (username: string, password: string) => {
    await authenticate(username, password);
    const [a, c] = await Promise.all([getAccount(), getAdminContext()]);
    setUser(a);
    setCtx(c);
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    setCtx(null);
  };

  const isSuperAdmin = ctx?.superAdmin ?? user?.authorities?.includes("ROLE_SUPER_ADMIN") ?? false;

  return (
    <AuthContext.Provider value={{ user, ctx, loading, isSuperAdmin, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
