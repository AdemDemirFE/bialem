import { Ionicons } from "@expo/vector-icons";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useEffect, useRef, useState } from "react";
import { ActivityIndicator, Image, Pressable, StyleSheet, Text, View } from "react-native";
import { useAuth } from "../../src/lib/auth";
import { removeStoryImage } from "../../src/lib/storage";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";
import { showAppConfirmDelete, showAppSuccess, showAppError } from "../../src/components/AppAlert";

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

const STORY_MS = 6000;

export default function StoryViewerScreen() {
  const router = useRouter();
  const { id } = useLocalSearchParams<{ id: string }>();
  const { user } = useAuth();
  const [queue, setQueue] = useState<StoryDetail[]>([]);
  const [index, setIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [deleting, setDeleting] = useState(false);
  const [paused, setPaused] = useState(false);
  const [progress, setProgress] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const pausedRef = useRef(false);
  const holdPausedRef = useRef(false);
  const buttonPausedRef = useRef(false);
  const elapsedRef = useRef(0);
  const lastTickRef = useRef<number | null>(null);
  const goNextRef = useRef<() => void>(() => undefined);
  const story = queue[index] ?? null;

  useEffect(() => {
    pausedRef.current = paused;
  }, [paused]);

  useEffect(() => {
    const loadStories = async () => {
      if (!id) return;
      setLoading(true);
      setError(null);
      const feed = await api.rpc("get_story_feed");
      const items = Array.isArray(feed.data) ? (feed.data as StoryDetail[]) : [];
      let nextQueue = items.filter((item) => item.story_id);
      if (!nextQueue.some((item) => item.story_id === id)) {
        const detail = await api.rpc("get_story_detail", { target_story_id: id });
        if (detail.data?.[0]) nextQueue = [detail.data[0] as StoryDetail, ...nextQueue];
      }
      const start = Math.max(0, nextQueue.findIndex((item) => item.story_id === id));
      if (!nextQueue.length) {
        setError("Bu anlık artık görüntülenemiyor.");
        setQueue([]);
      } else {
        setQueue(nextQueue);
        setIndex(start < 0 ? 0 : start);
      }
      setLoading(false);
    };
    void loadStories();
  }, [id]);

  useEffect(() => {
    if (!story?.story_id) return;
    void api.rpc("mark_story_viewed", { target_story_id: story.story_id });
  }, [story?.story_id]);

  const closeViewer = () => {
    if (typeof window !== "undefined" && window.history.length > 1) router.back();
    else router.replace("/(tabs)/feed");
  };

  const goPrev = () => {
    if (index <= 0) {
      closeViewer();
      return;
    }
    setIndex((current) => current - 1);
  };

  const goNext = () => {
    if (index >= queue.length - 1) {
      closeViewer();
      return;
    }
    setIndex((current) => current + 1);
  };

  goNextRef.current = goNext;

  useEffect(() => {
    if (!story || loading) return;
    elapsedRef.current = 0;
    lastTickRef.current = null;
    setProgress(0);
    let frame = 0;
    const tick = (now: number) => {
      if (lastTickRef.current == null) lastTickRef.current = now;
      if (!pausedRef.current) elapsedRef.current += now - lastTickRef.current;
      lastTickRef.current = now;
      const ratio = Math.min(1, elapsedRef.current / STORY_MS);
      setProgress(ratio);
      if (ratio >= 1) {
        goNextRef.current();
        return;
      }
      frame = requestAnimationFrame(tick);
    };
    frame = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(frame);
  }, [story?.story_id, loading, deleting]);

  const syncPaused = () => {
    const next = holdPausedRef.current || buttonPausedRef.current;
    setPaused(next);
  };

  const onHoldStart = () => {
    holdPausedRef.current = true;
    syncPaused();
  };

  const onHoldEnd = () => {
    holdPausedRef.current = false;
    syncPaused();
  };

  const togglePause = () => {
    buttonPausedRef.current = !buttonPausedRef.current;
    syncPaused();
  };

  const requestDelete = async () => {
    if (!story || !user || story.author_id !== user.id || deleting) return;

    buttonPausedRef.current = true;
    syncPaused();

    const confirmed = await showAppConfirmDelete("Bu anıyı kaldırmak istediğine emin misin? Artık hiç kimse tarafından görüntülenemeyecek.");
    if (!confirmed) {
      buttonPausedRef.current = false;
      syncPaused();
      return;
    }

    setDeleting(true);
    setError(null);
    const result = await api
      .from("stories")
      .delete()
      .eq("id", story.story_id)
      .eq("author_id", user.id)
      .select("id");

    if (result.error || !result.data?.length) {
      const message = result.error?.message || "Anı silinemedi. Lütfen tekrar dene.";
      setError(message);
      void showAppError(message);
      setDeleting(false);
      buttonPausedRef.current = false;
      syncPaused();
      return;
    }

    if (story.media_url) {
      try {
        await removeStoryImage(story.media_url);
      } catch {
        // Story is already gone; leftover media can be cleaned later.
      }
    }

    void showAppSuccess("Anı kaldırıldı.");
    const remaining = queue.filter((item) => item.story_id !== story.story_id);
    if (!remaining.length) {
      router.replace("/(tabs)/feed");
      return;
    }
    setQueue(remaining);
    setIndex((current) => Math.min(current, remaining.length - 1));
    buttonPausedRef.current = false;
    setDeleting(false);
    syncPaused();
  };

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color={colors.action} />
      </View>
    );
  }

  if (!story) {
    return (
      <View style={styles.center}>
        <Text style={styles.error}>{error}</Text>
        <Pressable style={styles.closeButton} onPress={closeViewer}>
          <Text style={styles.closeText}>Keşfet'e dön</Text>
        </Pressable>
      </View>
    );
  }

  return (
    <View style={styles.page}>
      {story.content_type === "image" && story.media_url ? (
        <Image source={{ uri: story.media_url }} style={styles.backgroundImage} resizeMode="cover" pointerEvents="none" />
      ) : (
        <View style={styles.textBackground} pointerEvents="none" />
      )}
      <View style={styles.scrim} pointerEvents="none" />

      <View style={styles.tapRow}>
        <Pressable
          style={styles.tapZone}
          onPress={goPrev}
          onLongPress={onHoldStart}
          delayLongPress={180}
          onPressOut={onHoldEnd}
          accessibilityLabel="Önceki anlık"
        />
        <Pressable
          style={[styles.tapZone, styles.tapZoneNext]}
          onPress={goNext}
          onLongPress={onHoldStart}
          delayLongPress={180}
          onPressOut={onHoldEnd}
          accessibilityLabel="Sonraki anlık"
        />
      </View>

      <View style={styles.progressRow} pointerEvents="none">
        {queue.map((item, itemIndex) => (
          <View key={item.story_id} style={styles.progressTrack}>
            <View
              style={[
                styles.progressFill,
                {
                  width:
                    itemIndex < index ? "100%" : itemIndex === index ? `${Math.round(progress * 100)}%` : "0%"
                }
              ]}
            />
          </View>
        ))}
      </View>

      <View style={styles.header} pointerEvents="box-none">
        <View style={styles.avatar}>
          {story.avatar_url ? (
            <Image source={{ uri: story.avatar_url }} style={styles.avatarImage} />
          ) : (
            <Text style={styles.avatarText}>{story.display_name.slice(0, 1).toUpperCase()}</Text>
          )}
        </View>
        <View style={styles.authorCopy} pointerEvents="none">
          <Text style={styles.author}>{story.display_name}</Text>
          <Text style={styles.meta}>
            {story.community_name || "Takipçileriyle paylaştı"} · {formatAge(story.created_at)}
          </Text>
        </View>
        <View style={styles.headerActions}>
          <Pressable style={styles.iconButton} onPress={togglePause} accessibilityLabel={paused ? "Başlat" : "Durdur"}>
            <Ionicons name={paused ? "play" : "pause"} size={18} color={colors.onBrand} />
          </Pressable>
          {story.author_id === user?.id ? (
            <Pressable style={styles.deleteButton} onPress={requestDelete} disabled={deleting} accessibilityLabel="Sil">
              {deleting ? <ActivityIndicator size="small" color={colors.onBrand} /> : <Ionicons name="trash-outline" size={16} color={colors.onBrand} />}
            </Pressable>
          ) : null}
          <Pressable style={styles.iconButton} onPress={closeViewer} accessibilityLabel="Kapat">
            <Ionicons name="close" size={22} color={colors.onBrand} />
          </Pressable>
        </View>
      </View>

      <View style={styles.content} pointerEvents="none">
        {story.body ? (
          <Text style={[styles.body, story.content_type === "text" && styles.textStoryBody]}>{story.body}</Text>
        ) : null}
        {error ? <Text style={styles.inlineError}>{error}</Text> : null}
      </View>

      <View style={styles.sideHints} pointerEvents="none">
        <Text style={styles.sideHint}>{index > 0 ? "‹" : ""}</Text>
        <Text style={styles.sideHint}>{index < queue.length - 1 ? "›" : ""}</Text>
      </View>
      <Text style={styles.expiry} pointerEvents="none">
        {paused ? "Duraklatıldı" : "Bu anlık 24 saat sonra kaybolur."}
      </Text>

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
  tapRow: { position: "absolute", top: 118, left: 0, right: 0, bottom: 0, zIndex: 1, flexDirection: "row" },
  tapZone: { flex: 1 },
  tapZoneNext: { flex: 2 },
  progressRow: { position: "absolute", top: 52, left: 14, right: 14, zIndex: 4, flexDirection: "row", gap: 4 },
  progressTrack: { flex: 1, height: 3, borderRadius: 2, overflow: "hidden", backgroundColor: "rgba(255,255,255,0.35)" },
  progressFill: { height: "100%", borderRadius: 2, backgroundColor: colors.surface },
  header: { position: "absolute", top: 66, left: 14, right: 14, zIndex: 5, flexDirection: "row", alignItems: "center", gap: 10 },
  avatar: { width: 42, height: 42, borderRadius: 21, alignItems: "center", justifyContent: "center", overflow: "hidden", backgroundColor: colors.action, borderWidth: 2, borderColor: colors.surface },
  avatarImage: { width: "100%", height: "100%" },
  avatarText: { color: colors.actionText, fontSize: 17, fontWeight: "900" },
  authorCopy: { flex: 1, gap: 2 },
  author: { color: colors.onBrand, fontSize: 15, fontWeight: "900" },
  meta: { color: "rgba(255,255,255,0.78)", fontSize: 11, fontWeight: "700" },
  headerActions: { flexDirection: "row", alignItems: "center", gap: 8 },
  iconButton: { width: 38, height: 38, borderRadius: 19, alignItems: "center", justifyContent: "center", backgroundColor: "rgba(0,0,0,0.45)" },
  deleteButton: { width: 38, height: 38, borderRadius: 19, alignItems: "center", justifyContent: "center", backgroundColor: "rgba(196,45,74,0.88)" },
  content: { flex: 1, alignItems: "center", justifyContent: "center", paddingHorizontal: 30, paddingTop: 130, paddingBottom: 90, zIndex: 0 },
  body: { color: colors.onBrand, fontSize: 18, lineHeight: 25, fontWeight: "800", textAlign: "center", padding: 14, borderRadius: 18, backgroundColor: "rgba(3,12,35,0.48)" },
  textStoryBody: { fontSize: 30, lineHeight: 38, backgroundColor: "transparent" },
  inlineError: { marginTop: 14, color: colors.onBrand, textAlign: "center", fontSize: 13, fontWeight: "800", paddingHorizontal: 14, paddingVertical: 10, borderRadius: 14, backgroundColor: "rgba(196,45,74,0.88)" },
  sideHints: { position: "absolute", left: 10, right: 10, top: "48%", zIndex: 2, flexDirection: "row", justifyContent: "space-between" },
  sideHint: { color: "rgba(255,255,255,0.35)", fontSize: 36, fontWeight: "200" },
  expiry: { position: "absolute", bottom: 34, left: 20, right: 20, zIndex: 2, color: "rgba(255,255,255,0.7)", fontSize: 11, textAlign: "center", fontWeight: "700" },
  error: { color: colors.onBrand, textAlign: "center", fontSize: 16, lineHeight: 23 },
  closeButton: { paddingHorizontal: 18, paddingVertical: 12, borderRadius: 999, backgroundColor: colors.action },
  closeText: { color: colors.actionText, fontWeight: "900" },
});
