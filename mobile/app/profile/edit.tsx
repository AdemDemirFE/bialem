import { Ionicons } from "@expo/vector-icons";
import { router, Stack } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Image, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { useAuth } from "../../src/lib/auth";
import { pickImageFromLibrary, requestMediaLibraryPermission, uploadProfileAvatar } from "../../src/lib/storage";
import { colors } from "../../src/theme/colors";
import { ImageViewerModal } from "../../src/components/ImageViewerModal";

export default function EditProfileScreen() {
  const { user, profile, loading, error, clearError, saveProfile, updateAvatar } = useAuth();
  const [displayName, setDisplayName] = useState("");
  const [username, setUsername] = useState("");
  const [city, setCity] = useState("");
  const [bio, setBio] = useState("");
  const [uploadingAvatar, setUploadingAvatar] = useState(false);
  const [saved, setSaved] = useState(false);
  const [viewerVisible, setViewerVisible] = useState(false);

  useEffect(() => {
    setDisplayName(profile?.display_name ?? "");
    setUsername(profile?.username ?? "");
    setCity(profile?.city ?? "");
    setBio(profile?.bio ?? "");
  }, [profile?.id]);

  useEffect(() => {
    clearError();
    return clearError;
  }, []);

  const changePhoto = async () => {
    if (!user || uploadingAvatar) return;
    setUploadingAvatar(true);
    setSaved(false);
    clearError();

    try {
      if (!(await requestMediaLibraryPermission())) return;
      const image = await pickImageFromLibrary({ square: true });
      if (!image) return;
      const avatarUrl = await uploadProfileAvatar({ userId: user.id, image });
      if (await updateAvatar(avatarUrl)) {
        setSaved(true);
      }
    } finally {
      setUploadingAvatar(false);
    }
  };

  const submit = async () => {
    setSaved(false);
    const successful = await saveProfile({ displayName, username, city, bio });
    if (successful) setSaved(true);
  };

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: "Profili Düzenle" }} />
      <ImageViewerModal
        visible={viewerVisible}
        uri={profile?.avatar_url ?? null}
        onClose={() => setViewerVisible(false)}
        onEdit={() => void changePhoto()}
      />

      <ScrollView style={styles.screen} contentContainerStyle={styles.page} keyboardShouldPersistTaps="handled">
        <View style={styles.hero}>
          <Pressable accessibilityLabel="Profil fotoğrafını görüntüle" style={styles.avatarButton} onPress={() => setViewerVisible(true)}>
            {profile?.avatar_url ? (
              <Image source={{ uri: profile.avatar_url }} style={styles.avatar} />
            ) : (
              <Text style={styles.avatarInitial}>{(displayName || "Ü").slice(0, 1).toLocaleUpperCase("tr-TR")}</Text>
            )}
            <View style={styles.cameraBadge}>
              {uploadingAvatar ? (
                <ActivityIndicator size="small" color={colors.actionText} />
              ) : (
                <Ionicons name="camera" size={18} color={colors.actionText} />
              )}
            </View>
          </Pressable>
          <View style={styles.heroCopy}>
            <Text style={styles.kicker}>PROFİLİN SENİ ANLATSIN</Text>
            <Text style={styles.title}>Bilgilerini güncel tut.</Text>
            <Text style={styles.subtitle}>E-posta ve doğrulama durumu güvenlik nedeniyle bu ekrandan değiştirilemez.</Text>
          </View>
        </View>

        <View style={styles.panel}>
          {error ? <Text style={styles.error}>{error}</Text> : null}
          {saved ? <Text style={styles.success}>Profil bilgilerin güncellendi.</Text> : null}
          <Field label="Görünen ad" value={displayName} onChangeText={setDisplayName} placeholder="Adınız Soyadınız" />
          <Field
            label="Kullanıcı adı"
            value={username}
            onChangeText={setUsername}
            autoCapitalize="none"
            placeholder="örnek_kullanıcı"
            hint="Yalnızca küçük harf, rakam ve alt çizgi kullanabilirsin."
          />
          <Field label="Şehir" value={city} onChangeText={setCity} placeholder="Ankara" />
          <Field
            label="Kısa biyografi"
            value={bio}
            onChangeText={setBio}
            placeholder="İlgi alanlarını ve toplulukta neler yapmak istediğini anlat."
            multiline
          />

          <Pressable disabled={loading} style={[styles.saveButton, loading && styles.disabled]} onPress={() => void submit()}>
            {loading ? <ActivityIndicator color={colors.actionText} /> : <Ionicons name="checkmark-circle" size={20} color={colors.actionText} />}
            <Text style={styles.saveText}>{loading ? "Kaydediliyor..." : "Değişiklikleri kaydet"}</Text>
          </Pressable>
          {saved ? (
            <Pressable style={styles.doneButton} onPress={() => router.back()}>
              <Text style={styles.doneText}>Profile dön</Text>
            </Pressable>
          ) : null}
        </View>
      </ScrollView>
    </>
  );
}

function Field({
  label,
  hint,
  multiline = false,
  ...props
}: {
  label: string;
  hint?: string;
  multiline?: boolean;
  value: string;
  onChangeText: (value: string) => void;
  placeholder: string;
  autoCapitalize?: "none" | "sentences" | "words" | "characters";
}) {
  return (
    <View style={styles.field}>
      <Text style={styles.label}>{label}</Text>
      <TextInput
        {...props}
        multiline={multiline}
        placeholderTextColor={colors.muted}
        style={[styles.input, multiline && styles.textarea]}
      />
      {hint ? <Text style={styles.hint}>{hint}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  page: { gap: 18, padding: 20, paddingBottom: 46 },
  hero: { flexDirection: "row", alignItems: "center", gap: 17, padding: 22, borderRadius: 30, backgroundColor: colors.brandInk },
  avatarButton: { width: 92, height: 92, alignItems: "center", justifyContent: "center", borderRadius: 32, backgroundColor: colors.accentSoft },
  avatar: { width: "100%", height: "100%", borderRadius: 32 },
  avatarInitial: { color: colors.accent, fontSize: 38, fontWeight: "900" },
  cameraBadge: { position: "absolute", right: -6, bottom: -6, width: 38, height: 38, alignItems: "center", justifyContent: "center", borderRadius: 15, backgroundColor: colors.action },
  heroCopy: { flex: 1, gap: 6 },
  kicker: { color: colors.action, fontSize: 10, fontWeight: "900", letterSpacing: 1.3 },
  title: { color: "#fff", fontSize: 25, lineHeight: 29, fontWeight: "900" },
  subtitle: { color: "#cbd5ef", fontSize: 12, lineHeight: 18 },
  panel: { gap: 17, padding: 20, borderRadius: 28, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  field: { gap: 7 },
  label: { color: colors.ink, fontSize: 13, fontWeight: "900" },
  input: { minHeight: 50, paddingHorizontal: 15, borderRadius: 16, borderWidth: 1, borderColor: colors.border, color: colors.ink, backgroundColor: colors.surfaceStrong, fontSize: 15 },
  textarea: { minHeight: 110, paddingTop: 14, textAlignVertical: "top" },
  hint: { color: colors.muted, fontSize: 11, lineHeight: 16 },
  error: { padding: 13, borderRadius: 15, color: colors.danger, backgroundColor: "#ffe8ef", fontWeight: "800" },
  success: { padding: 13, borderRadius: 15, color: colors.success, backgroundColor: colors.accentSoft, fontWeight: "800" },
  saveButton: { minHeight: 53, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 8, borderRadius: 18, backgroundColor: colors.action },
  saveText: { color: colors.actionText, fontSize: 15, fontWeight: "900" },
  doneButton: { alignItems: "center", padding: 13, borderRadius: 18, borderWidth: 1, borderColor: colors.border },
  doneText: { color: colors.ink, fontWeight: "900" },
  disabled: { opacity: 0.55 }
});
