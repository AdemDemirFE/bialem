import { Ionicons } from "@expo/vector-icons";
import { Link, router, Stack } from "expo-router";
import { useState } from "react";
import { ActivityIndicator, Linking, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { api } from "../src/lib/api";
import { colors } from "../src/theme/colors";

const SUPPORT_EMAIL = process.env.EXPO_PUBLIC_SUPPORT_EMAIL;
const CONFIRMATION_TEXT = "HESABIMI SİL";

const legalLinks = [
  { key: "privacy", title: "Gizlilik Politikası", icon: "shield-checkmark-outline" },
  { key: "terms", title: "Kullanım Şartları", icon: "document-text-outline" },
  { key: "kvkk", title: "KVKK Aydınlatma Metni", icon: "lock-closed-outline" },
  { key: "community", title: "Topluluk Kurallari", icon: "people-outline" }
] as const;

export default function AccountScreen() {
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [confirmation, setConfirmation] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const deleteAccount = async () => {
    if (confirmation.trim().toLocaleUpperCase("tr-TR") !== CONFIRMATION_TEXT || busy) return;

    setBusy(true);
    setError(null);
    const { data, error: functionError } = await api.functions.invoke("delete-account", { body: {} });

    if (functionError || !data?.deleted) {
      setError(data?.error || functionError?.message || "Hesap silinemedi. Lütfen tekrar deneyin.");
      setBusy(false);
      return;
    }

    await api.auth.signOut({ scope: "local" });
    router.replace("/");
  };

  return (
    <ScrollView contentContainerStyle={styles.page} keyboardShouldPersistTaps="handled">
      <Stack.Screen options={{ headerShown: true, title: "Hesap ve yasal" }} />
      <View style={styles.hero}>
        <View style={styles.iconBadge}><Ionicons name="shield-checkmark" size={28} color={colors.accent} /></View>
        <Text style={styles.kicker}>GÜVEN MERKEZI</Text>
        <Text style={styles.title}>Hesabın ve haklarin senin kontrolünde.</Text>
        <Text style={styles.description}>Hukuki metinleri inceleyebilir, destek isteyebilir veya hesabını kalıcı olarak silebilirsin.</Text>
      </View>

      <View style={styles.panel}>
        <Text style={styles.panelTitle}>Gizlilik ve güvenlik</Text>
        <Link href="/settings" asChild>
          <Pressable style={styles.linkRow}>
            <Ionicons name="options-outline" size={21} color={colors.accent} />
            <View style={styles.linkCopy}>
              <Text style={styles.linkText}>Gizlilik ve bildirimler</Text>
              <Text style={styles.linkHint}>Görünürlük, takip ve cihaz bildirimlerini yönet.</Text>
            </View>
            <Ionicons name="chevron-forward" size={18} color={colors.muted} />
          </Pressable>
        </Link>
        <Link href="/blocked-users" asChild>
          <Pressable style={styles.linkRow}>
            <Ionicons name="person-remove-outline" size={21} color={colors.accent} />
            <View style={styles.linkCopy}>
              <Text style={styles.linkText}>Engellenen kullanıcılar</Text>
              <Text style={styles.linkHint}>Engellediğin kişileri görüntüle ve yönet.</Text>
            </View>
            <Ionicons name="chevron-forward" size={18} color={colors.muted} />
          </Pressable>
        </Link>
      </View>

      <View style={styles.panel}>
        <Text style={styles.panelTitle}>Hukuki metinler</Text>
        {legalLinks.map((item) => (
          <Pressable
            key={item.key}
            style={styles.linkRow}
            onPress={() => router.push({ pathname: "/legal/[document]", params: { document: item.key } })}
          >
            <Ionicons name={item.icon} size={21} color={colors.accent} />
            <Text style={styles.linkText}>{item.title}</Text>
            <Ionicons name="chevron-forward" size={18} color={colors.muted} />
          </Pressable>
        ))}
      </View>

      <View style={styles.panel}>
        <Text style={styles.panelTitle}>Destek ve KVKK başvurusu</Text>
        <Text style={styles.description}>Hesabın, bir şikâyet veya kişisel verilerin hakkında destek ekibine e-posta gönderebilirsin.</Text>
        <Pressable
          disabled={!SUPPORT_EMAIL}
          style={[styles.secondaryButton, !SUPPORT_EMAIL && styles.disabledButton]}
          onPress={() => SUPPORT_EMAIL && void Linking.openURL(`mailto:${SUPPORT_EMAIL}?subject=Bialem%20Destek%20Talebi`)}
        >
          <Text style={styles.secondaryButtonText}>{SUPPORT_EMAIL ? `Destek: ${SUPPORT_EMAIL}` : "Destek e-postası yayın öncesi tanımlanacak"}</Text>
        </Pressable>
      </View>

      <View style={styles.dangerPanel}>
        <Text style={styles.dangerTitle}>Hesabı kalıcı olarak sil</Text>
        <Text style={styles.description}>
          Hesabın, profilin, paylaşımların, hikâyelerin, yorumların ve puanların silinir. Sahibi olduğun topluluk ve etkinlikler de kaldırılır. Bu işlem geri alınamaz.
        </Text>

        {!deleteOpen ? (
          <Pressable style={styles.dangerOutlineButton} onPress={() => setDeleteOpen(true)}>
            <Text style={styles.dangerOutlineText}>Hesap silme adımını aç</Text>
          </Pressable>
        ) : (
          <View style={styles.deleteBox}>
            <Text style={styles.confirmLabel}>Onaylamak için aşağıdaki alana “{CONFIRMATION_TEXT}” yaz:</Text>
            <TextInput
              value={confirmation}
              onChangeText={setConfirmation}
              editable={!busy}
              autoCapitalize="characters"
              placeholder={CONFIRMATION_TEXT}
              placeholderTextColor={colors.muted}
              style={styles.input}
            />
            {error ? <Text style={styles.errorText}>{error}</Text> : null}
            <Pressable
              disabled={confirmation.trim().toLocaleUpperCase("tr-TR") !== CONFIRMATION_TEXT || busy}
              style={[styles.dangerButton, (confirmation.trim().toLocaleUpperCase("tr-TR") !== CONFIRMATION_TEXT || busy) && styles.disabledButton]}
              onPress={() => void deleteAccount()}
            >
              {busy ? <ActivityIndicator color="#ffffff" /> : <Text style={styles.dangerButtonText}>Hesabımı kalıcı olarak sil</Text>}
            </Pressable>
            <Pressable disabled={busy} onPress={() => { setDeleteOpen(false); setConfirmation(""); setError(null); }}>
              <Text style={styles.cancelText}>Vazgeç</Text>
            </Pressable>
          </View>
        )}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 24, paddingBottom: 48, gap: 18, backgroundColor: colors.page },
  hero: { gap: 9 },
  iconBadge: { width: 54, height: 54, borderRadius: 18, alignItems: "center", justifyContent: "center", backgroundColor: colors.accentSoft },
  kicker: { color: colors.accent, fontSize: 12, fontWeight: "800", letterSpacing: 1.2 },
  title: { color: colors.ink, fontSize: 30, lineHeight: 37, fontWeight: "900" },
  description: { color: colors.muted, fontSize: 15, lineHeight: 22 },
  panel: { backgroundColor: colors.surface, borderRadius: 26, borderWidth: 1, borderColor: colors.border, padding: 18, gap: 13 },
  panelTitle: { color: colors.ink, fontSize: 20, fontWeight: "800" },
  linkRow: { minHeight: 52, flexDirection: "row", alignItems: "center", gap: 12, borderBottomWidth: 1, borderBottomColor: colors.border },
  linkText: { flex: 1, color: colors.ink, fontSize: 15, fontWeight: "700" },
  linkCopy: { flex: 1, gap: 2 },
  linkHint: { color: colors.muted, fontSize: 11, lineHeight: 16 },
  secondaryButton: { borderRadius: 18, borderWidth: 1, borderColor: colors.border, padding: 14 },
  secondaryButtonText: { color: colors.ink, textAlign: "center", fontSize: 14, fontWeight: "700" },
  dangerPanel: { backgroundColor: colors.surfaceStrong, borderRadius: 26, borderWidth: 1, borderColor: colors.danger, padding: 18, gap: 13 },
  dangerTitle: { color: colors.danger, fontSize: 20, fontWeight: "900" },
  dangerOutlineButton: { borderRadius: 18, borderWidth: 1, borderColor: colors.danger, padding: 14 },
  dangerOutlineText: { color: colors.danger, textAlign: "center", fontSize: 14, fontWeight: "800" },
  deleteBox: { gap: 12 },
  confirmLabel: { color: colors.ink, fontSize: 14, lineHeight: 20, fontWeight: "700" },
  input: { backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, borderRadius: 17, paddingHorizontal: 15, paddingVertical: 13, color: colors.ink, fontSize: 15, fontWeight: "700" },
  dangerButton: { minHeight: 50, alignItems: "center", justifyContent: "center", borderRadius: 18, padding: 14, backgroundColor: colors.danger },
  dangerButtonText: { color: "#ffffff", fontSize: 14, fontWeight: "800" },
  disabledButton: { opacity: 0.45 },
  cancelText: { color: colors.muted, textAlign: "center", fontSize: 14, fontWeight: "700" },
  errorText: { color: colors.danger, fontSize: 13, lineHeight: 19, fontWeight: "700" }
});
