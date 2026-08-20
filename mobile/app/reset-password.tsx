import { Ionicons } from "@expo/vector-icons";
import * as Linking from "expo-linking";
import { Stack, useLocalSearchParams, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import {
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";
import { api } from "../src/lib/api";
import { colors } from "../src/theme/colors";

const PASSWORD_HINT = "En az 8 karakter, 1 büyük harf, 1 küçük harf ve 1 rakam";

function isPasswordValid(password: string) {
  return password.length >= 8 && /[A-Z]/.test(password) && /[a-z]/.test(password) && /\d/.test(password);
}

function firstParam(value?: string | string[]) {
  return Array.isArray(value) ? value[0] : value;
}

function readKeyFromUrl(url: string | null | undefined) {
  if (!url) return null;
  try {
    const query = url.includes("?") ? url.split("?")[1].split("#")[0] : "";
    const hash = url.includes("#") ? url.split("#")[1] : "";
    const params = new URLSearchParams([query, hash].filter(Boolean).join("&"));
    return params.get("key") || params.get("code");
  } catch {
    return null;
  }
}

export default function ResetPasswordScreen() {
  const router = useRouter();
  const incomingUrl = Linking.useURL();
  const routeParams = useLocalSearchParams<{
    key?: string | string[];
    token?: string | string[];
    code?: string | string[];
    email?: string | string[];
  }>();

  const [code, setCode] = useState("");
  const [password, setPassword] = useState("");
  const [passwordAgain, setPasswordAgain] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [showPasswordAgain, setShowPasswordAgain] = useState(false);
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    const resolve = async () => {
      const fromRoute =
        firstParam(routeParams.key) || firstParam(routeParams.token) || firstParam(routeParams.code);
      const fromUrl = readKeyFromUrl(incomingUrl) || readKeyFromUrl(await Linking.getInitialURL());
      const initial = fromRoute || fromUrl;
      if (active && initial) {
        setCode(initial.replace(/\s/g, ""));
      }
    };
    void resolve();
    return () => {
      active = false;
    };
  }, [incomingUrl, routeParams.code, routeParams.key, routeParams.token]);

  const savePassword = async () => {
    const normalizedCode = code.replace(/\s/g, "").trim();
    if (!normalizedCode || normalizedCode.length < 6) {
      setError("E-postanıza gelen 8 haneli sıfırlama kodunu girin.");
      return;
    }
    if (!isPasswordValid(password)) {
      setError(PASSWORD_HINT);
      return;
    }
    if (password !== passwordAgain) {
      setError("Şifreler birbiriyle eşleşmiyor.");
      return;
    }

    setSaving(true);
    setError(null);
    const { error: updateError } = await api.auth.updateUser({ password, key: normalizedCode });
    setSaving(false);

    if (updateError) {
      const message = updateError.message || "";
      if (/reset|token|key|kod|süresi|geçersiz|expired|invalid/i.test(message)) {
        setError(
          "Şifre sıfırlama kodunun süresi dolmuş veya kod geçersiz.\n\nYeni bir kod talep edebilirsiniz."
        );
      } else {
        setError(message);
      }
      return;
    }

    setSuccess(true);
    setTimeout(() => router.replace("/"), 1600);
  };

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: "Yeni Şifre" }} />
      <KeyboardAvoidingView
        style={styles.keyboardPage}
        behavior={Platform.OS === "ios" ? "padding" : undefined}
      >
        <ScrollView contentContainerStyle={styles.page} keyboardShouldPersistTaps="handled">
          <View style={styles.icon}>
            <Ionicons name="key" size={30} color={colors.accent} />
          </View>
          <Text style={styles.title}>{success ? "Şifreniz başarıyla güncellendi." : "Kodu gir, şifreni yenile"}</Text>
          <Text style={styles.description}>
            {success
              ? "Giriş ekranına yönlendiriliyorsunuz..."
              : "E-postadaki 8 haneli kodu yazın, ardından yeni şifrenizi belirleyin."}
          </Text>

          {!success ? (
            <View style={styles.form}>
              {error ? <Text style={styles.error}>{error}</Text> : null}

              <Text style={styles.label}>Sıfırlama kodu</Text>
              <TextInput
                value={code}
                onChangeText={(value) => setCode(value.replace(/[^\d\s]/g, ""))}
                placeholder="12345678"
                placeholderTextColor={colors.muted}
                keyboardType="number-pad"
                maxLength={12}
                autoCapitalize="none"
                style={[styles.input, styles.codeInput]}
              />

              <Text style={styles.label}>Yeni Şifre</Text>
              <PasswordField
                value={password}
                onChangeText={setPassword}
                placeholder="Yeni Şifre"
                visible={showPassword}
                onToggle={() => setShowPassword((value) => !value)}
              />
              <Text style={styles.label}>Yeni Şifre Tekrar</Text>
              <PasswordField
                value={passwordAgain}
                onChangeText={setPasswordAgain}
                placeholder="Yeni Şifre Tekrar"
                visible={showPasswordAgain}
                onToggle={() => setShowPasswordAgain((value) => !value)}
              />

              <Pressable
                disabled={saving}
                style={[styles.button, saving && styles.buttonDisabled]}
                onPress={() => void savePassword()}
              >
                <Text style={styles.buttonText}>{saving ? "Kaydediliyor..." : "Şifremi Güncelle"}</Text>
              </Pressable>

              <Pressable style={styles.secondaryLink} onPress={() => router.replace("/forgot-password")}>
                <Text style={styles.secondaryLinkText}>Yeni Kod Gönder</Text>
              </Pressable>
              <Pressable style={styles.secondaryLink} onPress={() => router.replace("/")}>
                <Text style={styles.secondaryLinkText}>Giriş ekranına dön</Text>
              </Pressable>
            </View>
          ) : null}
        </ScrollView>
      </KeyboardAvoidingView>
    </>
  );
}

function PasswordField({
  value,
  onChangeText,
  placeholder,
  visible,
  onToggle
}: {
  value: string;
  onChangeText: (value: string) => void;
  placeholder: string;
  visible: boolean;
  onToggle: () => void;
}) {
  return (
    <View style={styles.passwordWrap}>
      <TextInput
        value={value}
        onChangeText={onChangeText}
        placeholder={placeholder}
        placeholderTextColor={colors.muted}
        secureTextEntry={!visible}
        autoCapitalize="none"
        style={styles.input}
      />
      <Pressable style={styles.eyeButton} onPress={onToggle} accessibilityLabel={visible ? "Şifreyi gizle" : "Şifreyi göster"}>
        <Ionicons name={visible ? "eye-outline" : "eye-off-outline"} size={22} color={colors.muted} />
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  keyboardPage: { flex: 1, backgroundColor: colors.page },
  page: { flexGrow: 1, justifyContent: "center", gap: 14, padding: 24, backgroundColor: colors.page },
  icon: {
    width: 58,
    height: 58,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 20,
    backgroundColor: colors.accentSoft
  },
  title: { color: colors.ink, fontSize: 28, fontWeight: "900" },
  description: { color: colors.muted, fontSize: 15, lineHeight: 22 },
  form: { gap: 12, marginTop: 8 },
  label: { color: colors.ink, fontSize: 14, fontWeight: "700", marginTop: 4 },
  passwordWrap: { position: "relative", justifyContent: "center" },
  input: {
    minHeight: 54,
    paddingHorizontal: 16,
    paddingRight: 48,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 18,
    color: colors.ink,
    backgroundColor: colors.surface,
    fontSize: 16
  },
  codeInput: {
    paddingRight: 16,
    letterSpacing: 4,
    fontSize: 22,
    fontWeight: "800",
    textAlign: "center"
  },
  eyeButton: {
    position: "absolute",
    right: 12,
    height: 54,
    width: 36,
    alignItems: "center",
    justifyContent: "center"
  },
  button: { alignItems: "center", padding: 16, borderRadius: 999, backgroundColor: colors.accent, marginTop: 8 },
  buttonDisabled: { opacity: 0.55 },
  buttonText: { color: colors.onBrand, fontSize: 14, fontWeight: "900" },
  secondaryLink: { alignSelf: "center", paddingVertical: 6 },
  secondaryLinkText: { color: colors.accent, fontSize: 14, fontWeight: "800" },
  error: { color: colors.danger, fontSize: 13, lineHeight: 19, fontWeight: "700" }
});
