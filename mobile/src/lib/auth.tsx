import { type PropsWithChildren, createContext, useContext, useEffect, useState } from "react";
import { api } from "./api";
import { deactivateCurrentPushDevice } from "./notificationApi";
import { noPermissions, type AccountPermissions } from "./permissions";
import { initializePushNotifications } from "./pushNotifications";

type Profile = {
  id: string;
  email: string;
  display_name: string;
  username: string;
  avatar_url: string | null;
  bio: string | null;
  city: string | null;
  birth_date: string | null;
  status: string;
  is_verified: boolean;
};

type SignUpInput = {
  email: string;
  password: string;
  displayName: string;
  username: string;
};

type ProfileInput = {
  displayName: string;
  username: string;
  city: string;
  bio: string;
  birthDate: string;
};

type AuthUser = { id: string; email: string };

type AuthContextValue = {
  session: { access_token: string; user: AuthUser } | null;
  user: AuthUser | null;
  profile: Profile | null;
  permissions: AccountPermissions;
  loading: boolean;
  error: string | null;
  notice: string | null;
  signIn: (email: string, password: string) => Promise<boolean>;
  signUp: (input: SignUpInput) => Promise<boolean>;
  requestPasswordReset: (email: string) => Promise<boolean>;
  resendSignUpEmail: (email: string) => Promise<boolean>;
  saveProfile: (input: ProfileInput) => Promise<boolean>;
  updateAvatar: (avatarUrl: string) => Promise<boolean>;
  changePassword: (currentPassword: string, newPassword: string) => Promise<boolean>;
  signOut: () => Promise<void>;
  clearError: () => void;
  clearNotice: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

function sanitizeUsername(username: string) {
  return username.trim().toLowerCase().replace(/[^a-z0-9_]/g, "");
}

function parseTurkishDate(value: string): string | null {
  const trimmed = value.trim();
  if (!trimmed) return null;
  const match = trimmed.match(/^(\d{1,2})[./-](\d{1,2})[./-](\d{4})$/);
  if (!match) return null;
  const [_, day, month, year] = match;
  const d = Number(day);
  const m = Number(month);
  const y = Number(year);
  if (d < 1 || d > 31 || m < 1 || m > 12 || y < 1900 || y > 2100) return null;
  return `${year}-${month.padStart(2, "0")}-${day.padStart(2, "0")}`;
}

function mapErrorMessage(message: string) {
  const normalized = message.toLowerCase();
  if (
    normalized.includes("failed to fetch") ||
    normalized.includes("network request failed") ||
    normalized.includes("network error") ||
    normalized.includes("load failed") ||
    normalized.includes("connection") ||
    normalized.includes("time aşımı") ||
    normalized.includes("bağlantı kurulamadı")
  ) {
    return "Bağlantı kurulamadı. İnternet bağlantını kontrol edip tekrar dene.";
  }
  if (normalized.includes("unauthorized") || normalized.includes("401") || normalized.includes("bad credentials")) {
    return "E-posta veya şifre hatalı.";
  }
  if (normalized.includes("forbidden") || normalized.includes("403") || normalized.includes("yetki")) {
    return "Bu işlem için yetkiniz yok.";
  }
  if (normalized.includes("already used") || normalized.includes("already registered")) {
    return "Bu e-posta veya kullanıcı adı zaten kayıtlı.";
  }
  if (normalized.includes("too many") || normalized.includes("çok fazla") || normalized.includes("rate limit")) {
    return "Çok fazla şifre sıfırlama isteği gönderildi. Lütfen bir süre sonra tekrar deneyin.";
  }
  if (normalized.includes("passwordpolicy") || (normalized.includes("büyük harf") && normalized.includes("şifre"))) {
    return "Şifre en az 8 karakter olmalı ve en az bir büyük harf, bir küçük harf ve bir rakam içermelidir.";
  }
  return message;
}

export function AuthProvider({ children }: PropsWithChildren) {
  const [session, setSession] = useState<AuthContextValue["session"]>(null);
  const [user, setUser] = useState<AuthUser | null>(null);
  const [profile, setProfile] = useState<Profile | null>(null);
  const [permissions, setPermissions] = useState<AccountPermissions>(noPermissions);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  useEffect(() => {
    let mounted = true;
    void api.auth.getSession().then(({ data }) => {
      if (!mounted) return;
      setSession(data.session);
      setUser(data.session?.user ?? null);
      // Keep loading true when user exists so the profile effect can finish;
      // only mark ready here when there is definitely no authenticated user.
      if (!data.session?.user) {
        setLoading(false);
      }
    });
    const { data } = api.auth.onAuthStateChange((_event, next) => {
      if (!mounted) return;
      setSession(next);
      setUser(next?.user ?? null);
      // Re-enter loading state when a session appears mid-render so we wait
      // for the profile/permissions fetch before rendering protected routes.
      if (next?.user) {
        setLoading(true);
      } else {
        setLoading(false);
      }
    });
    return () => {
      mounted = false;
      data.subscription.unsubscribe();
    };
  }, []);

  useEffect(() => {
    if (!user) {
      setProfile(null);
      setPermissions(noPermissions);
      return;
    }
    let mounted = true;
    setLoading(true);
    void Promise.all([api.profiles.getById(user.id), api.rest.get<{ permissions?: AccountPermissions }>("/api/account")])
      .then(([{ data, error: profileError }, account]) => {
        if (!mounted) return;
        if (profileError) {
          setError("Profil bilgileri alınamadı: " + mapErrorMessage(profileError.message));
        } else {
          setProfile(data ?? null);
        }
        setPermissions(account.permissions ?? noPermissions);
        setLoading(false);
      });
    return () => {
      mounted = false;
    };
  }, [user]);

  const signIn = async (email: string, password: string) => {
    if (!email.trim() || !password) {
      setError("E-posta ve şifre alanları zorunludur.");
      return false;
    }
    setLoading(true);
    setError(null);
    const { error: signInError } = await api.auth.signInWithPassword({ email: email.trim(), password });
    if (signInError) {
      setError(mapErrorMessage(signInError.message));
      setLoading(false);
      return false;
    }
    setLoading(false);
    await initializePushNotificationsAfterLogin();
    return true;
  };

  const initializePushNotificationsAfterLogin = async () => {
    try {
      await initializePushNotifications();
    } catch (error) {
      console.warn("Push notification init after login failed", error);
    }
  };

  const signUp = async ({ email, password, displayName, username }: SignUpInput) => {
    const normalizedUsername = sanitizeUsername(username);
    const passwordOk =
      password.length >= 8 && /[A-Z]/.test(password) && /[a-z]/.test(password) && /\d/.test(password);
    if (!email.includes("@") || !passwordOk || displayName.trim().length < 2 || normalizedUsername.length < 3) {
      setError(
        !passwordOk
          ? "Şifre en az 8 karakter olmalı ve en az bir büyük harf, bir küçük harf ve bir rakam içermelidir."
          : "Geçerli e-posta, en az 8 karakter şifre, ad ve kullanıcı adı girin."
      );
      return false;
    }
    setLoading(true);
    setError(null);
    const { error: signUpError } = await api.auth.signUp({
      email: email.trim(),
      password,
      options: { data: { display_name: displayName.trim(), username: normalizedUsername } }
    });
    if (signUpError) {
      setError(mapErrorMessage(signUpError.message));
      setLoading(false);
      return false;
    }
    setNotice("Kayıt tamamlandı. Şimdi giriş yapabilirsiniz.");
    setLoading(false);
    return true;
  };

  const requestPasswordReset = async (email: string) => {
    if (!email.trim()) {
      setError("E-posta adresinizi yazın.");
      return false;
    }
    const { error: resetError } = await api.auth.resetPasswordForEmail(email.trim());
    if (resetError) {
      setError(mapErrorMessage(resetError.message));
      return false;
    }
    setNotice("Eğer bu e-posta adresi sistemimizde kayıtlıysa şifre sıfırlama kodu gönderildi.");
    return true;
  };

  const resendSignUpEmail = async () => {
    setNotice("Hesap doğrudan aktif. Giriş yapabilirsiniz.");
    return true;
  };

  const saveProfile = async ({ displayName, username, city, bio, birthDate }: ProfileInput) => {
    if (!user) return false;
    const normalizedUsername = sanitizeUsername(username);
    const parsedBirthDate = parseTurkishDate(birthDate);
    const { data, error: profileError } = await api.profiles.update(user.id, {
      display_name: displayName.trim(),
      username: normalizedUsername,
      city: city.trim() || null,
      bio: bio.trim() || null,
      birth_date: parsedBirthDate
    });
    if (profileError) {
      setError(mapErrorMessage(profileError.message));
      return false;
    }
    setProfile(data);
    return true;
  };

  const updateAvatar = async (avatarUrl: string) => {
    if (!user) return false;
    const { data, error: avatarError } = await api.profiles.update(user.id, { avatar_url: avatarUrl });
    if (avatarError) {
      setError(mapErrorMessage(avatarError.message));
      return false;
    }
    setProfile(data);
    return true;
  };

  const changePassword = async (currentPassword: string, newPassword: string) => {
    try {
      await api.rest.post("/api/account/change-password", { currentPassword, newPassword });
      return true;
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      setError(mapErrorMessage(message));
      return false;
    }
  };

  const signOut = async () => {
    try {
      await deactivateCurrentPushDevice();
    } catch (error) {
      console.warn("Push device cleanup during logout failed", error);
    }
    await api.auth.signOut();
    setSession(null);
    setUser(null);
      setProfile(null);
    setPermissions(noPermissions);
  };

  return (
    <AuthContext.Provider
      value={{
        session,
        user,
        profile,
        permissions,
        loading,
        error,
        notice,
        signIn,
        signUp,
        requestPasswordReset,
        resendSignUpEmail,
        saveProfile,
        updateAvatar,
        changePassword,
        signOut,
        clearError: () => setError(null),
        clearNotice: () => setNotice(null)
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used inside AuthProvider");
  return context;
}
