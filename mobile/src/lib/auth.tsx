import { type PropsWithChildren, createContext, useContext, useEffect, useState } from "react";
import { api } from "./api";

type Profile = {
  id: string;
  email: string;
  display_name: string;
  username: string;
  avatar_url: string | null;
  bio: string | null;
  city: string | null;
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
};

type AuthUser = { id: string; email: string };

type AuthContextValue = {
  session: { access_token: string; user: AuthUser } | null;
  user: AuthUser | null;
  profile: Profile | null;
  loading: boolean;
  error: string | null;
  notice: string | null;
  signIn: (email: string, password: string) => Promise<boolean>;
  signUp: (input: SignUpInput) => Promise<boolean>;
  requestPasswordReset: (email: string) => Promise<boolean>;
  resendSignUpEmail: (email: string) => Promise<boolean>;
  saveProfile: (input: ProfileInput) => Promise<boolean>;
  updateAvatar: (avatarUrl: string) => Promise<boolean>;
  signOut: () => Promise<void>;
  clearError: () => void;
  clearNotice: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

function sanitizeUsername(username: string) {
  return username.trim().toLowerCase().replace(/[^a-z0-9_]/g, "");
}

function mapErrorMessage(message: string) {
  const normalized = message.toLowerCase();
  if (normalized.includes("unauthorized") || normalized.includes("401") || normalized.includes("bad credentials")) {
    return "E-posta veya şifre hatalı.";
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
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  useEffect(() => {
    let mounted = true;
    void api.auth.getSession().then(({ data }) => {
      if (!mounted) return;
      setSession(data.session);
      setUser(data.session?.user ?? null);
      setLoading(false);
    });
    const { data } = api.auth.onAuthStateChange((_event, next) => {
      if (!mounted) return;
      setSession(next);
      setUser(next?.user ?? null);
    });
    return () => {
      mounted = false;
      data.subscription.unsubscribe();
    };
  }, []);

  useEffect(() => {
    if (!user) {
      setProfile(null);
      return;
    }
    let mounted = true;
    setLoading(true);
    void api
      .from("profiles")
      .select("*")
      .eq("id", user.id)
      .maybeSingle()
      .then(({ data, error: profileError }) => {
        if (!mounted) return;
        if (profileError) setError(mapErrorMessage(profileError.message));
        else setProfile((data as Profile) ?? null);
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
    initializePushNotificationsAfterLogin();
    return true;
  };

  const initializePushNotificationsAfterLogin = async () => {
    try {
      const { initializePushNotifications } = await import("./pushNotifications");
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

  const saveProfile = async ({ displayName, username, city, bio }: ProfileInput) => {
    if (!user) return false;
    const normalizedUsername = sanitizeUsername(username);
    const { data, error: profileError } = await api
      .from("profiles")
      .update({
        display_name: displayName.trim(),
        username: normalizedUsername,
        city: city.trim() || null,
        bio: bio.trim() || null
      })
      .eq("id", user.id)
      .select("*")
      .single();
    if (profileError) {
      setError(mapErrorMessage(profileError.message));
      return false;
    }
    setProfile(data as Profile);
    return true;
  };

  const updateAvatar = async (avatarUrl: string) => {
    if (!user) return false;
    const { data, error: avatarError } = await api.from("profiles").update({ avatar_url: avatarUrl }).eq("id", user.id).select("*").single();
    if (avatarError) {
      setError(mapErrorMessage(avatarError.message));
      return false;
    }
    setProfile(data as Profile);
    return true;
  };

  const signOut = async () => {
    await api.auth.signOut();
    setSession(null);
    setUser(null);
    setProfile(null);
  };

  return (
    <AuthContext.Provider
      value={{
        session,
        user,
        profile,
        loading,
        error,
        notice,
        signIn,
        signUp,
        requestPasswordReset,
        resendSignUpEmail,
        saveProfile,
        updateAvatar,
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
