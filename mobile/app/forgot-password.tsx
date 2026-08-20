import { Ionicons } from "@expo/vector-icons";
import { Stack, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";
import { useAuth } from "../src/lib/auth";
import { colors } from "../src/theme/colors";
import { useTheme } from "../src/theme/theme";

const authPalettes = {
  light: {
    page: "#f6f8ff",
    surface: "#ffffff",
    ink: "#081a44",
    muted: "#42527d",
    border: "#cfddfb",
    accent: "#6f2cff",
    accentSoft: "#eee4ff"
  },
  dark: {
    page: "#070b18",
    surface: "#11182a",
    ink: "#ffffff",
    muted: "#c7d2e8",
    border: "#35415f",
    accent: "#b28cff",
    accentSoft: "#2b1d48"
  }
} as const;

export default function ForgotPasswordScreen() {
  const router = useRouter();
  const { resolvedTheme } = useTheme();
  const palette = authPalettes[resolvedTheme];
  const { requestPasswordReset, clearError, clearNotice, error, notice } = useAuth();
  const [email, setEmail] = useState("");
  const [sending, setSending] = useState(false);
  const [cooldown, setCooldown] = useState(0);
  const [localNotice, setLocalNotice] = useState<string | null>(null);

  useEffect(() => {
    if (cooldown <= 0) return;
    const timer = setTimeout(() => setCooldown((seconds) => Math.max(0, seconds - 1)), 1000);
    return () => clearTimeout(timer);
  }, [cooldown]);

  const submit = async () => {
    if (sending || cooldown > 0) return;
    clearError();
    clearNotice();
    setLocalNotice(null);
    setSending(true);
    const ok = await requestPasswordReset(email);
    setSending(false);
    if (ok) {
      setLocalNotice(
        "Eğer bu e-posta adresi sistemimizde kayıtlıysa şifre sıfırlama kodu gönderildi."
      );
      setCooldown(60);
      router.push(`/reset-password?email=${encodeURIComponent(email.trim())}`);
    }
  };

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: "Şifremi Unuttum" }} />
      <KeyboardAvoidingView
        style={[styles.keyboardPage, { backgroundColor: palette.page }]}
        behavior={Platform.OS === "ios" ? "padding" : undefined}
      >
        <ScrollView
          contentContainerStyle={[styles.page, { backgroundColor: palette.page }]}
          keyboardShouldPersistTaps="handled"
        >
          <View style={[styles.icon, { backgroundColor: palette.accentSoft }]}>
            <Ionicons name="mail-outline" size={28} color={palette.accent} />
          </View>
          <Text style={[styles.title, { color: palette.ink }]}>Şifre sıfırlama</Text>
          <Text style={[styles.description, { color: palette.muted }]}>
            Kayıtlı e-posta adresinizi yazın. Geçerli bir hesap varsa 8 haneli sıfırlama kodu gönderilir.
          </Text>

          <View style={[styles.card, { backgroundColor: palette.surface, borderColor: palette.border }]}>
            {error ? <Text style={styles.errorText}>{error}</Text> : null}
            {localNotice || notice ? (
              <Text style={[styles.noticeText, { backgroundColor: palette.accentSoft, color: palette.accent }]}>
                {localNotice || notice}
              </Text>
            ) : null}

            <Text style={[styles.fieldLabel, { color: palette.ink }]}>E-posta Adresi</Text>
            <TextInput
              value={email}
              onChangeText={setEmail}
              placeholder="ornek@eposta.com"
              placeholderTextColor={palette.muted}
              autoCapitalize="none"
              keyboardType="email-address"
              style={[
                styles.input,
                { backgroundColor: palette.surface, borderColor: palette.border, color: palette.ink }
              ]}
            />

            <Pressable
              style={[styles.primaryButton, (sending || cooldown > 0) && styles.primaryButtonDisabled]}
              disabled={sending || cooldown > 0}
              onPress={() => void submit()}
            >
              {sending ? (
                <ActivityIndicator color={colors.actionText} />
              ) : (
                <Text style={styles.primaryButtonText}>
                  {cooldown > 0 ? `Tekrar deneyin (${cooldown} sn)` : "Sıfırlama Kodu Gönder"}
                </Text>
              )}
            </Pressable>

            <Pressable style={styles.linkButton} onPress={() => router.replace("/")}>
              <Text style={[styles.linkText, { color: palette.accent }]}>Giriş ekranına dön</Text>
            </Pressable>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </>
  );
}

const styles = StyleSheet.create({
  keyboardPage: { flex: 1 },
  page: { flexGrow: 1, padding: 24, gap: 14 },
  icon: {
    width: 58,
    height: 58,
    borderRadius: 20,
    alignItems: "center",
    justifyContent: "center",
    marginTop: 12
  },
  title: { fontSize: 30, fontWeight: "900" },
  description: { fontSize: 15, lineHeight: 22 },
  card: {
    borderRadius: 30,
    padding: 22,
    gap: 14,
    borderWidth: 1
  },
  fieldLabel: { fontSize: 14, fontWeight: "700" },
  input: {
    minHeight: 52,
    borderRadius: 18,
    borderWidth: 1,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 16
  },
  primaryButton: {
    marginTop: 4,
    backgroundColor: colors.action,
    borderRadius: 999,
    paddingVertical: 15,
    paddingHorizontal: 18,
    alignItems: "center"
  },
  primaryButtonDisabled: { opacity: 0.55 },
  primaryButtonText: {
    color: colors.actionText,
    textAlign: "center",
    fontSize: 16,
    fontWeight: "900"
  },
  linkButton: { alignSelf: "center", paddingVertical: 8 },
  linkText: { fontSize: 14, fontWeight: "800" },
  errorText: { color: colors.danger, fontSize: 14, lineHeight: 20, fontWeight: "600" },
  noticeText: {
    fontSize: 14,
    lineHeight: 20,
    fontWeight: "600",
    borderRadius: 14,
    paddingHorizontal: 12,
    paddingVertical: 10
  }
});
