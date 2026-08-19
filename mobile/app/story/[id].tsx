import { Link, useLocalSearchParams, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Alert, Image, Pressable, StyleSheet, Text, View } from "react-native";
import { useAuth } from "../../src/lib/auth";
import { removeStoryImage } from "../../src/lib/storage";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";

type StoryDetail = {
  story_id: string;
  author_id: string;
  display_name: string;
  avatar_url: string | null;
  community_name: string | null;
  content_type: "text" | "image";
  body: string | null;
  media_url: string | null;
  created_at: string;
};

export default function StoryViewerScreen() {
  const router = useRouter();
  const { id } = useLocalSearchParams<{ id: string }>();
  const { user } = useAuth();
  const [story, setStory] = useState<StoryDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadStory = async () => {
      if (!id) return;
      const result = await api.rpc("get_story_detail", { target_story_id: id });

      if (result.error || !result.data?.[0]) {
        setError(result.error?.message || "Bu anlık artık görüntülenemiyor.");
      } else {
        setStory(result.data[0] as StoryDetail);
        await api.rpc("mark_story_viewed", { target_story_id: id });
      }
      setLoading(false);
    };

    void loadStory();
  }, [id]);

  const deleteStory = () => {
    if (!story || !user || story.author_id !== user.id || deleting) return;

    Alert.alert(
      "Anını kaldır",
      "Bu anı hemen kaldırılacak ve artık hiç kimse tarafından görüntülenemeyecek.",
      [
        { text: "Vazgeç", style: "cancel" },
        {
          text: "Anıyı sil",
          style: "destructive",
          onPress: () => void confirmDeleteStory()
        }
      ]
    );
  };

  const confirmDeleteStory = async () => {
    if (!story || !user || story.author_id !== user.id) return;

    setDeleting(true);
    setError(null);

    const result = await api
      .from("stories")
      .delete()
      .eq("id", story.story_id)
      .eq("author_id", user.id)
      .select("id");

    if (result.error || !result.data?.length) {
      setError(result.error?.message || "Anı silinemedi. Lütfen tekrar dene.");
      setDeleting(false);
      return;
    }

    if (story.media_url) {
      try {
        await removeStoryImage(story.media_url);
      } catch {
        // The story is already inaccessible; an orphaned file can be cleaned up later.
      }
    }

    router.replace("/(tabs)/feed");
  };

  if (loading) {
    return <View style={styles.center}><ActivityIndicator size="large" color={colors.action} /></View>;
  }

  if (!story) {
    return (
      <View style={styles.center}>
        <Text style={styles.error}>{error}</Text>
        <Link href="/(tabs)/feed" asChild><Pressable style={styles.closeButton}><Text style={styles.closeText}>Keşfet'e dön</Text></Pressable></Link>
      </View>
    );
  }

  return (
    <View style={styles.page}>
      {story.content_type === "image" && story.media_url ? (
        <Image source={{ uri: story.media_url }} style={styles.backgroundImage} resizeMode="cover" />
      ) : <View style={styles.textBackground} />}
      <View style={styles.scrim} />

      <View style={styles.progress}><View style={styles.progressFill} /></View>
      <View style={styles.header}>
        <View style={styles.avatar}>
          {story.avatar_url ? <Image source={{ uri: story.avatar_url }} style={styles.avatarImage} /> : <Text style={styles.avatarText}>{story.display_name.slice(0, 1).toUpperCase()}</Text>}
        </View>
        <View style={styles.authorCopy}>
          <Text style={styles.author}>{story.display_name}</Text>
          <Text style={styles.meta}>{story.community_name || "Takipçileriyle paylaştı"} · {formatAge(story.created_at)}</Text>
        </View>
        <View style={styles.headerActions}>
          {story.author_id === user?.id ? (
            <Pressable style={styles.deleteButton} onPress={deleteStory} disabled={deleting}>
              {deleting ? <ActivityIndicator size="small" color={colors.onBrand} /> : <Text style={styles.deleteText}>Sil</Text>}
            </Pressable>
          ) : null}
          <Link href="/(tabs)/feed" asChild><Pressable style={styles.close}><Text style={styles.closeSymbol}>×</Text></Pressable></Link>
        </View>
      </View>

      <View style={styles.content}>
        {story.body ? <Text style={[styles.body, story.content_type === "text" && styles.textStoryBody]}>{story.body}</Text> : null}
        {error ? <Text style={styles.inlineError}>{error}</Text> : null}
      </View>
      <Text style={styles.expiry}>Bu anlık 24 saat sonra kaybolur.</Text>
    </View>
  );
}

function formatAge(value: string) {
  const minutes = Math.max(1, Math.floor((Date.now() - new Date(value).getTime()) / 60000));
  if (minutes < 60) return `${minutes} dk`;
  return `${Math.floor(minutes / 60)} sa`;
}

const styles = StyleSheet.create({
  page: { flex: 1, backgroundColor: colors.brandInk },
  center: { flex: 1, alignItems: "center", justifyContent: "center", gap: 18, padding: 24, backgroundColor: colors.brandInk },
  backgroundImage: { ...StyleSheet.absoluteFillObject },
  textBackground: { ...StyleSheet.absoluteFillObject, backgroundColor: colors.accent },
  scrim: { ...StyleSheet.absoluteFillObject, backgroundColor: "rgba(3,12,35,0.35)" },
  progress: { position: "absolute", top: 52, left: 18, right: 18, height: 3, borderRadius: 2, backgroundColor: "rgba(255,255,255,0.35)" },
  progressFill: { width: "100%", height: "100%", borderRadius: 2, backgroundColor: colors.surface },
  header: { position: "absolute", top: 66, left: 18, right: 18, flexDirection: "row", alignItems: "center", gap: 10 },
  avatar: { width: 42, height: 42, borderRadius: 21, alignItems: "center", justifyContent: "center", overflow: "hidden", backgroundColor: colors.action, borderWidth: 2, borderColor: colors.surface },
  avatarImage: { width: "100%", height: "100%" },
  avatarText: { color: colors.actionText, fontSize: 17, fontWeight: "900" },
  authorCopy: { flex: 1, gap: 2 },
  author: { color: colors.onBrand, fontSize: 15, fontWeight: "900" },
  meta: { color: "rgba(255,255,255,0.78)", fontSize: 11, fontWeight: "700" },
  headerActions: { flexDirection: "row", alignItems: "center", gap: 8 },
  deleteButton: { minWidth: 48, height: 38, paddingHorizontal: 13, borderRadius: 19, alignItems: "center", justifyContent: "center", backgroundColor: "rgba(196,45,74,0.88)" },
  deleteText: { color: colors.onBrand, fontSize: 12, fontWeight: "900" },
  close: { width: 38, height: 38, borderRadius: 19, alignItems: "center", justifyContent: "center", backgroundColor: "rgba(0,0,0,0.25)" },
  closeSymbol: { color: colors.onBrand, fontSize: 28, lineHeight: 31 },
  content: { flex: 1, alignItems: "center", justifyContent: "center", paddingHorizontal: 30, paddingTop: 130, paddingBottom: 90 },
  body: { color: colors.onBrand, fontSize: 18, lineHeight: 25, fontWeight: "800", textAlign: "center", padding: 14, borderRadius: 18, backgroundColor: "rgba(3,12,35,0.48)" },
  textStoryBody: { fontSize: 30, lineHeight: 38, backgroundColor: "transparent" },
  inlineError: { marginTop: 14, color: colors.onBrand, textAlign: "center", fontSize: 13, fontWeight: "800", paddingHorizontal: 14, paddingVertical: 10, borderRadius: 14, backgroundColor: "rgba(196,45,74,0.88)" },
  expiry: { position: "absolute", bottom: 34, left: 20, right: 20, color: "rgba(255,255,255,0.7)", fontSize: 11, textAlign: "center", fontWeight: "700" },
  error: { color: colors.onBrand, textAlign: "center", fontSize: 16, lineHeight: 23 },
  closeButton: { paddingHorizontal: 18, paddingVertical: 12, borderRadius: 999, backgroundColor: colors.action },
  closeText: { color: colors.actionText, fontWeight: "900" }
});
