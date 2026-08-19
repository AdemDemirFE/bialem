import { Ionicons } from "@expo/vector-icons";
import * as Linking from "expo-linking";
import { Stack, useLocalSearchParams, useRouter } from "expo-router";
import { useEffect, useRef, useState } from "react";
import { ActivityIndicator, Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { api } from "../src/lib/api";
import { colors } from "../src/theme/colors";

export default function ResetPasswordScreen() {
  const router = useRouter();
  const incomingUrl = Linking.useURL();
  const routeParams = useLocalSearchParams<{
    access_token?: string | string[];
    refresh_token?: string | string[];
    token_hash?: string | string[];
    code?: string | string[];
    error_description?: string | string[];
  }>();
  const routeAccessToken = firstParam(routeParams.access_token);
  const routeRefreshToken = firstParam(routeParams.refresh_token);
  const routeTokenHash = firstParam(routeParams.token_hash);
  const routeCode = firstParam(routeParams.code);
  const routeErrorDescription = firstParam(routeParams.error_description);
  const [password, setPassword] = useState("");
  const [passwordAgain, setPasswordAgain] = useState("");
  const [loading, setLoading] = useState(true);
  const [sessionReady, setSessionReady] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const handledUrlRef = useRef<string | null>(null);

  useEffect(() => {
    let active = true;

    const initializeRecovery = async () => {
      const recoveryUrl = incomingUrl || (await Linking.getInitialURL());
      const urlRecovery = recoveryUrl ? readRecoveryParameters(recoveryUrl) : emptyRecoveryParameters;
      const recovery = {
        accessToken: routeAccessToken ?? urlRecovery.accessToken,
        refreshToken: routeRefreshToken ?? urlRecovery.refreshToken,
        tokenHash: routeTokenHash ?? urlRecovery.tokenHash,
        code: routeCode ?? urlRecovery.code,
        errorDescription: routeErrorDescription ?? urlRecovery.errorDescription
      };
      const recoveryKey = JSON.stringify(recovery);

      if (hasRecoveryParameters(recovery) && handledUrlRef.current !== recoveryKey) {
        handledUrlRef.current = recoveryKey;
        let sessionError: Error | null = null;

        if (recovery.errorDescription) {
          sessionError = new Error(recovery.errorDescription);
        } else if (recovery.accessToken && recovery.refreshToken) {
          const result = await api.auth.setSession({
            access_token: recovery.accessToken,
            refresh_token: recovery.refreshToken
          });
          sessionError = result.error;
        } else if (recovery.tokenHash) {
          const result = await api.auth.verifyOtp({
            token_hash: recovery.tokenHash,
            type: "recovery"
          });
          sessionError = result.error;
          if (result.data.session) {
            const persisted = await api.auth.setSession({
              access_token: result.data.session.access_token,
              refresh_token: result.data.session.refresh_token
            });
            sessionError ??= persisted.error;
          }
        } else if (recovery.code) {
          const result = await api.auth.exchangeCodeForSession(recovery.code);
          sessionError = result.error;
        }

        if (sessionError && active) {
          setError("Şifre yenileme bağlantısı geçersiz veya süresi dolmuş.");
        }
      }

      const { data } = await api.auth.getSession();
      if (active) {
        setSessionReady(Boolean(data.session));
        if (!data.session) {
          setError("Şifre yenileme bağlantısı geçersiz veya süresi dolmuş. Lütfen yeni bağlantı iste.");
        } else {
          setError(null);
        }
        setLoading(false);
      }
    };

    void initializeRecovery();
    return () => {
      active = false;
    };
  }, [
    incomingUrl,
    routeAccessToken,
    routeCode,
    routeErrorDescription,
    routeRefreshToken,
    routeTokenHash
  ]);

  const savePassword = async () => {
    if (password.length < 8) {
      setError("Yeni şifre en az 8 karakter olmalı.");
      return;
    }

    if (password !== passwordAgain) {
      setError("Şifreler birbiriyle eşleşmiyor.");
      return;
    }

    const { data: sessionData } = await api.auth.getSession();
    if (!sessionData.session) {
      setSessionReady(false);
      setError("Şifre yenileme oturumu bulunamadı. Lütfen yeni bir sıfırlama bağlantısı iste.");
      return;
    }

    setSaving(true);
    setError(null);
    const { error: updateError } = await api.auth.updateUser({ password });

    if (updateError) {
      setError(updateError.message);
      setSaving(false);
      return;
    }

    setSaving(false);
    router.replace("/(tabs)/profile");
  };

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: "Yeni Şifre" }} />
      <View style={styles.page}>
        <View style={styles.icon}><Ionicons name="key" size={30} color={colors.accent} /></View>
        <Text style={styles.title}>Yeni şifreni belirle</Text>
        <Text style={styles.description}>Hesabın için daha önce kullanmadığın, en az 8 karakterli bir şifre oluştur.</Text>

        {loading ? (
          <ActivityIndicator color={colors.accent} />
        ) : sessionReady ? (
          <View style={styles.form}>
            {error ? <Text style={styles.error}>{error}</Text> : null}
            <TextInput
              value={password}
              onChangeText={setPassword}
              placeholder="Yeni şifre"
              placeholderTextColor={colors.muted}
              secureTextEntry
              style={styles.input}
            />
            <TextInput
              value={passwordAgain}
              onChangeText={setPasswordAgain}
              placeholder="Yeni şifre tekrar"
              placeholderTextColor={colors.muted}
              secureTextEntry
              style={styles.input}
            />
            <Pressable disabled={saving} style={[styles.button, saving && styles.buttonDisabled]} onPress={() => void savePassword()}>
              <Text style={styles.buttonText}>{saving ? "Kaydediliyor..." : "Şifreyi güncelle"}</Text>
            </Pressable>
          </View>
        ) : (
          <Text style={styles.error}>{error}</Text>
        )}
      </View>
    </>
  );
}

function readRecoveryParameters(url: string) {
  const hash = url.includes("#") ? url.split("#")[1] : "";
  const query = url.includes("?") ? url.split("?")[1].split("#")[0] : "";
  const params = new URLSearchParams([query, hash].filter(Boolean).join("&"));

  return {
    accessToken: params.get("access_token"),
    refreshToken: params.get("refresh_token"),
    tokenHash: params.get("token_hash"),
    code: params.get("code"),
    errorDescription: params.get("error_description")
  };
}

type RecoveryParameters = {
  accessToken: string | null;
  refreshToken: string | null;
  tokenHash: string | null;
  code: string | null;
  errorDescription: string | null;
};

const emptyRecoveryParameters: RecoveryParameters = {
  accessToken: null,
  refreshToken: null,
  tokenHash: null,
  code: null,
  errorDescription: null
};

function hasRecoveryParameters(recovery: RecoveryParameters) {
  return Boolean(
    recovery.accessToken
    || recovery.refreshToken
    || recovery.tokenHash
    || recovery.code
    || recovery.errorDescription
  );
}

function firstParam(value?: string | string[]) {
  return Array.isArray(value) ? value[0] : value;
}

const styles = StyleSheet.create({
  page: { flex: 1, justifyContent: "center", gap: 14, padding: 24, backgroundColor: colors.page },
  icon: { width: 58, height: 58, alignItems: "center", justifyContent: "center", borderRadius: 20, backgroundColor: colors.accentSoft },
  title: { color: colors.ink, fontSize: 30, fontWeight: "900" },
  description: { color: colors.muted, fontSize: 15, lineHeight: 22 },
  form: { gap: 12, marginTop: 8 },
  input: { minHeight: 54, paddingHorizontal: 16, borderWidth: 1, borderColor: colors.border, borderRadius: 18, color: colors.ink, backgroundColor: colors.surface, fontSize: 16 },
  button: { alignItems: "center", padding: 16, borderRadius: 999, backgroundColor: colors.accent },
  buttonDisabled: { opacity: 0.55 },
  buttonText: { color: colors.onBrand, fontSize: 14, fontWeight: "900" },
  error: { color: colors.danger, fontSize: 13, lineHeight: 19, fontWeight: "700" }
});
