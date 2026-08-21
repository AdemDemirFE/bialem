import { Link, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Image, Linking, Platform, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { useAuth } from "../../src/lib/auth";
import { showAppAlert, showAppConfirm } from "../../src/components/AppAlert";
import {
  pickImageFromLibrary,
  requestCameraPermission,
  requestMediaLibraryPermission,
  takePhotoWithCamera,
  type PickedImage,
  uploadStoryImage
} from "../../src/lib/storage";
import { api } from "../../src/lib/api";
import { useScreenInsets } from "../../src/lib/safeArea";
import { colors } from "../../src/theme/colors";

type CommunityOption = {
  community_id: string;
  communities: { name: string } | null;
};

export default function CreateStoryScreen() {
  const router = useRouter();
  const { user } = useAuth();
  const insets = useScreenInsets();
  const [mode, setMode] = useState<"text" | "image">("text");
  const [body, setBody] = useState("");
  const [image, setImage] = useState<PickedImage | null>(null);
  const [communities, setCommunities] = useState<CommunityOption[]>([]);
  const [shareWithEveryone, setShareWithEveryone] = useState(false);
  const [shareWithFollowers, setShareWithFollowers] = useState(true);
  const [selectedCommunityIds, setSelectedCommunityIds] = useState<string[]>([]);
  const [loadingCommunities, setLoadingCommunities] = useState(true);
  const [sharing, setSharing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadCommunities = async () => {
      if (!user) return;
      const result = await api
        .from("community_members")
        .select("community_id, communities(name)")
        .eq("user_id", user.id)
        .eq("status", "approved");

      if (!result.error) setCommunities((result.data ?? []) as unknown as CommunityOption[]);
      setLoadingCommunities(false);
    };

    void loadCommunities();
  }, [user?.id]);

  const setSelectedImage = (selected: PickedImage | null) => {
    if (!selected) return;
    setImage(selected);
    setMode("image");
    setError(null);
  };

  const chooseImageFromLibrary = async () => {
    if (!(await requestMediaLibraryPermission())) {
      setError("Fotoğraf seçmek için galeri izni vermen gerekiyor.");
      return;
    }

    const selected = await pickImageFromLibrary();
    setSelectedImage(selected);
  };

  const takePhoto = async () => {
    const permission = await requestCameraPermission();

    if (!permission.granted) {
      setError("Fotoğraf çekmek için kamera izni vermen gerekiyor.");

      if (permission.canAskAgain) {
        const confirmed = await showAppConfirm({
          title: "Kamera izni gerekli",
          text: "Kamerayı kullanabilmek için izin vermelisin. İstersen tekrar deneyebilir veya galeriden fotoğraf seçebilirsin.",
          confirmText: "Tekrar dene",
          cancelText: "Galeriden seç"
        });
        if (confirmed) {
          void takePhoto();
        } else {
          void chooseImageFromLibrary();
        }
      } else {
        const confirmed = await showAppConfirm({
          title: "Kamera izni kapalı",
          text: "Kamera izni kalıcı olarak kapatılmış. Telefon ayarlarından Bialem için kamera iznini açabilir veya galeriden fotoğraf seçebilirsin.",
          confirmText: "Ayarları aç",
          cancelText: "Galeriden seç"
        });
        if (confirmed) {
          void Linking.openSettings();
        } else {
          void chooseImageFromLibrary();
        }
      }
      return;
    }

    const selected = await takePhotoWithCamera();
    setSelectedImage(selected);
  };

  const chooseImageSource = async () => {
    setMode("image");
    setError(null);

    if (Platform.OS === "web") {
      void chooseImageFromLibrary();
      return;
    }

    const choice = await showAppAlert({
      title: "Anını fotoğrafla paylaş",
      text: "Yeni bir fotoğraf çekebilir veya galeriden seçebilirsin.",
      confirmText: "Kamerayı aç"
    });
    if (choice.isConfirmed) {
      void takePhoto();
    } else {
      void chooseImageFromLibrary();
    }
  };

  const shareStory = async () => {
    if (!user) return;
    if (mode === "text" && !body.trim()) {
      setError("Metin anlığı boş olamaz.");
      return;
    }
    if (mode === "image" && !image) {
      setError("Bir fotoğraf seçmelisin.");
      return;
    }
    if (!shareWithEveryone && !shareWithFollowers && selectedCommunityIds.length === 0) {
      setError("Anlığı görecek en az bir hedef seçmelisin.");
      return;
    }

    setSharing(true);
    setError(null);

    const { data: storyId, error: storyError } = await api.rpc("create_story_with_audience", {
      target_content_type: mode,
      target_body: body.trim(),
      target_is_public: shareWithEveryone,
      target_share_with_followers: shareWithFollowers,
      target_community_ids: selectedCommunityIds
    });

    if (storyError) {
      setError(storyError.message);
      setSharing(false);
      return;
    }

    if (mode === "image" && image) {
      try {
        const uploaded = await uploadStoryImage({ userId: user.id, storyId, image });
        const updateResult = await api.from("stories").update({ media_url: uploaded.storagePath }).eq("id", storyId);
        if (updateResult.error) throw updateResult.error;
      } catch (uploadError) {
        await api.from("stories").delete().eq("id", storyId);
        setError(uploadError instanceof Error ? uploadError.message : "Fotoğraf yüklenemedi.");
        setSharing(false);
        return;
      }
    }

    router.replace("/(tabs)/feed");
  };

  const selectEveryone = () => {
    setShareWithEveryone(true);
    setError(null);
  };

  const toggleFollowers = () => {
    setShareWithEveryone(false);
    setShareWithFollowers((current) => !current);
    setError(null);
  };

  const toggleCommunity = (communityId: string) => {
    setShareWithEveryone(false);
    setSelectedCommunityIds((current) => (
      current.includes(communityId)
        ? current.filter((id) => id !== communityId)
        : [...current, communityId]
    ));
    setError(null);
  };

  return (
    <ScrollView contentContainerStyle={[styles.page, { paddingTop: insets.top + 16, paddingBottom: insets.bottom + 24 }]} keyboardShouldPersistTaps="handled">
      <Link href="/(tabs)/feed" asChild>
        <Pressable style={styles.backButton}><Text style={styles.backText}>Keşfet'e dön</Text></Pressable>
      </Link>

      <View style={styles.hero}>
        <Text style={styles.kicker}>YENİ ANLIK</Text>
        <Text style={styles.title}>Şu anda dünyanda ne oluyor?</Text>
        <Text style={styles.description}>Fotoğrafını veya kısa notunu paylaş. Anlığın 24 saat sonra otomatik kaybolur.</Text>
      </View>

      <View style={styles.modeRow}>
        <Pressable style={[styles.modeButton, mode === "text" && styles.modeButtonActive]} onPress={() => setMode("text")}>
          <Text style={[styles.modeText, mode === "text" && styles.modeTextActive]}>Yazı</Text>
        </Pressable>
        <Pressable style={[styles.modeButton, mode === "image" && styles.modeButtonActive]} onPress={chooseImageSource}>
          <Text style={[styles.modeText, mode === "image" && styles.modeTextActive]}>Fotoğraf</Text>
        </Pressable>
      </View>

      <View style={[styles.preview, mode === "text" ? styles.textPreview : styles.imagePreview]}>
        {mode === "image" && image ? (
          <Image source={{ uri: image.uri }} style={styles.previewImage} resizeMode="cover" />
        ) : mode === "image" ? (
          <View style={styles.emptyImage}>
            <Text style={styles.emptyImageTitle}>Anını fotoğrafla</Text>
            <Text style={styles.emptyImageText}>Dikey veya kare fotoğraflar en iyi görünür.</Text>
            <View style={styles.imageActionRow}>
              {Platform.OS !== "web" ? (
                <Pressable style={styles.imageActionButton} onPress={() => void takePhoto()}>
                  <Text style={styles.imageActionText}>Kamerayı aç</Text>
                </Pressable>
              ) : null}
              <Pressable style={styles.imageActionButton} onPress={() => void chooseImageFromLibrary()}>
                <Text style={styles.imageActionText}>Galeriden seç</Text>
              </Pressable>
            </View>
          </View>
        ) : (
          <Text style={styles.previewText}>{body.trim() || "Anını birkaç kelimeyle anlat..."}</Text>
        )}
      </View>

      <View style={styles.panel}>
        <Text style={styles.label}>{mode === "image" ? "Fotoğrafa not ekle (isteğe bağlı)" : "Anlık yazın"}</Text>
        <TextInput
          value={body}
          onChangeText={setBody}
          placeholder="Bugün harika bir gün..."
          placeholderTextColor={colors.muted}
          multiline
          maxLength={500}
          style={styles.input}
        />
        <Text style={styles.counter}>{body.length}/500</Text>

        <Text style={styles.label}>Kimlerle paylaşılsın?</Text>
        <Text style={styles.audienceHint}>Herkesi seçebilir veya takipçilerinle birden fazla topluluğu birlikte işaretleyebilirsin.</Text>
        {loadingCommunities ? <ActivityIndicator color={colors.accent} /> : (
          <View style={styles.audienceOptions}>
            <Pressable style={[styles.everyoneCard, shareWithEveryone && styles.everyoneCardActive]} onPress={selectEveryone}>
              <View style={styles.audienceCopy}>
                <Text style={[styles.everyoneTitle, shareWithEveryone && styles.communityTextActive]}>Herkes</Text>
                <Text style={[styles.everyoneDescription, shareWithEveryone && styles.everyoneDescriptionActive]}>Takip eden veya etmeyen tüm Bialem üyeleri</Text>
              </View>
              <Text style={[styles.selectionMark, shareWithEveryone && styles.selectionMarkActive]}>{shareWithEveryone ? "✓" : "+"}</Text>
            </Pressable>
            <Text style={styles.orLabel}>VEYA HEDEFLERİ SEÇ</Text>
            <View style={styles.communityRow}>
              <Pressable style={[styles.communityChip, !shareWithEveryone && shareWithFollowers && styles.communityChipActive]} onPress={toggleFollowers}>
                <Text style={[styles.communityText, !shareWithEveryone && shareWithFollowers && styles.communityTextActive]}>
                  {!shareWithEveryone && shareWithFollowers ? "✓ " : ""}Takipçilerim
                </Text>
              </Pressable>
              {communities.map((item) => {
                const selected = !shareWithEveryone && selectedCommunityIds.includes(item.community_id);
                return (
                  <Pressable key={item.community_id} style={[styles.communityChip, selected && styles.communityChipActive]} onPress={() => toggleCommunity(item.community_id)}>
                    <Text style={[styles.communityText, selected && styles.communityTextActive]}>
                      {selected ? "✓ " : ""}{item.communities?.name ?? "Topluluk"}
                    </Text>
                  </Pressable>
                );
              })}
            </View>
          </View>
        )}

        {error ? <Text style={styles.error}>{error}</Text> : null}
        <Pressable style={[styles.shareButton, sharing && styles.disabled]} onPress={() => void shareStory()} disabled={sharing}>
          <Text style={styles.shareButtonText}>{sharing ? "Paylaşılıyor..." : "Anlığı paylaş"}</Text>
        </Pressable>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 22, gap: 18, backgroundColor: colors.page },
  backButton: { alignSelf: "flex-start", paddingHorizontal: 14, paddingVertical: 10, borderRadius: 999, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  backText: { color: colors.ink, fontWeight: "800" },
  hero: { gap: 8 },
  kicker: { color: colors.accent, fontSize: 12, fontWeight: "900", letterSpacing: 1.4 },
  title: { color: colors.ink, fontSize: 31, lineHeight: 37, fontWeight: "900" },
  description: { color: colors.muted, fontSize: 15, lineHeight: 22 },
  modeRow: { flexDirection: "row", padding: 5, gap: 5, borderRadius: 999, backgroundColor: colors.accentSoft },
  modeButton: { flex: 1, paddingVertical: 11, borderRadius: 999 },
  modeButtonActive: { backgroundColor: colors.surface },
  modeText: { textAlign: "center", color: colors.muted, fontWeight: "800" },
  modeTextActive: { color: colors.ink },
  preview: { height: 330, borderRadius: 32, overflow: "hidden", borderWidth: 1, borderColor: colors.border },
  textPreview: { alignItems: "center", justifyContent: "center", padding: 30, backgroundColor: colors.brandInk },
  imagePreview: { backgroundColor: colors.surfaceStrong },
  previewImage: { width: "100%", height: "100%" },
  previewText: { color: colors.onBrand, fontSize: 28, lineHeight: 36, fontWeight: "900", textAlign: "center" },
  emptyImage: { flex: 1, alignItems: "center", justifyContent: "center", padding: 24, gap: 7 },
  emptyImageTitle: { color: colors.ink, fontSize: 19, fontWeight: "900" },
  emptyImageText: { color: colors.muted, fontSize: 13, textAlign: "center" },
  imageActionRow: { flexDirection: "row", flexWrap: "wrap", justifyContent: "center", gap: 9, marginTop: 8 },
  imageActionButton: { minHeight: 44, justifyContent: "center", paddingHorizontal: 16, borderRadius: 999, backgroundColor: colors.action },
  imageActionText: { color: colors.actionText, fontSize: 13, fontWeight: "900" },
  panel: { padding: 18, gap: 11, borderRadius: 26, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  label: { color: colors.ink, fontSize: 14, fontWeight: "900", marginTop: 3 },
  input: { minHeight: 92, padding: 13, borderRadius: 18, backgroundColor: colors.page, borderWidth: 1, borderColor: colors.border, color: colors.ink, fontSize: 15, textAlignVertical: "top" },
  counter: { alignSelf: "flex-end", color: colors.muted, fontSize: 11 },
  audienceHint: { color: colors.muted, fontSize: 12, lineHeight: 18 },
  audienceOptions: { gap: 11 },
  everyoneCard: { minHeight: 76, flexDirection: "row", alignItems: "center", gap: 12, padding: 14, borderRadius: 20, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.page },
  everyoneCardActive: { borderColor: colors.accent, backgroundColor: colors.accent },
  audienceCopy: { flex: 1, gap: 3 },
  everyoneTitle: { color: colors.ink, fontSize: 15, fontWeight: "900" },
  everyoneDescription: { color: colors.muted, fontSize: 11, lineHeight: 16 },
  everyoneDescriptionActive: { color: colors.onBrandMuted },
  selectionMark: { width: 30, height: 30, overflow: "hidden", borderRadius: 15, color: colors.ink, backgroundColor: colors.surfaceStrong, textAlign: "center", textAlignVertical: "center", fontSize: 18, fontWeight: "900" },
  selectionMarkActive: { color: colors.accent, backgroundColor: colors.onBrand },
  orLabel: { color: colors.muted, fontSize: 9, fontWeight: "900", letterSpacing: 1.2 },
  communityRow: { flexDirection: "row", flexWrap: "wrap", gap: 8, paddingVertical: 2 },
  communityChip: { paddingHorizontal: 14, paddingVertical: 10, borderRadius: 999, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.page },
  communityChipActive: { backgroundColor: colors.accent, borderColor: colors.accent },
  communityText: { color: colors.ink, fontSize: 13, fontWeight: "800" },
  communityTextActive: { color: colors.onBrand },
  error: { color: colors.danger, fontSize: 13, lineHeight: 19, fontWeight: "700" },
  shareButton: { marginTop: 4, paddingVertical: 15, borderRadius: 999, backgroundColor: colors.action },
  shareButtonText: { color: colors.actionText, textAlign: "center", fontSize: 16, fontWeight: "900" },
  disabled: { opacity: 0.55 }
});
