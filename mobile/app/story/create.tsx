import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useEffect, useMemo, useRef, useState } from "react";
import { showAppConfirm } from "../../src/components/AppAlert";
import {
  ActivityIndicator,
  Animated,
  Dimensions,
  Image,
  KeyboardAvoidingView,
  Modal,
  PanResponder,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";
import { useAuth } from "../../src/lib/auth";
import { api } from "../../src/lib/api";
import {
  pickImageFromLibrary,
  requestCameraPermission,
  requestMediaLibraryPermission,
  takePhotoWithCamera,
  type PickedImage,
  uploadStoryMedia
} from "../../src/lib/storage";
import { colors } from "../../src/theme/colors";
import { useSafeAreaInsets } from "react-native-safe-area-context";

const SCREEN = Dimensions.get("window");
const CANVAS_MAX_WIDTH = Math.min(SCREEN.width - 32, 420);
const TOP_BAR_HEIGHT = 56;
const BOTTOM_TOOLBAR_HEIGHT = 72;

const LOCATION_PRESETS = [
  { name: "Ankara", latitude: 39.9334, longitude: 32.8597 },
  { name: "İstanbul", latitude: 41.0082, longitude: 28.9784 },
  { name: "İzmir", latitude: 38.4192, longitude: 27.1287 },
  { name: "Antalya", latitude: 36.8969, longitude: 30.7133 },
  { name: "Bursa", latitude: 40.1828, longitude: 29.0669 }
];

const COLOR_PRESETS = ["#FFFFFF", "#000000", "#FF3B30", "#FF9500", "#FFCC00", "#4CD964", "#5AC8FA", "#007AFF", "#5856D6", "#FF2D55"];
const BG_PRESETS = ["transparent", "rgba(0,0,0,0.4)", "rgba(255,255,255,0.8)", "#000000", "#FFFFFF", "#FF3B30", "#007AFF"];

export type StoryElementType = "TEXT" | "LOCATION" | "HASHTAG" | "COMMUNITY" | "EVENT" | "STICKER";

export type StoryElement = {
  id: string;
  type: StoryElementType;
  content: string;
  x: number;
  y: number;
  scale: number;
  rotation: number;
  color: string;
  backgroundColor: string;
  fontSize: number;
  width?: number;
  height?: number;
  metadata?: Record<string, any>;
};

type AudienceCommunity = {
  id: string;
  name: string;
  avatarUrl?: string | null;
  memberCount?: number | null;
};
type EventOption = { event_id?: string | null; events?: { id?: string; title?: string; starts_at?: string } | null };

type VisibilityMode = "everyone" | "followers" | "community";
type HashtagOption = { hashtag_id: string; name: string; normalized_name: string; usage_count: number };

export default function CreateStoryScreen() {
  const router = useRouter();
  const { user } = useAuth();
  const insets = useSafeAreaInsets();
  const canvasWidth = Math.min(SCREEN.width - 32, CANVAS_MAX_WIDTH);
  const maxCanvasHeight = Math.max(260, SCREEN.height - insets.top - insets.bottom - TOP_BAR_HEIGHT - BOTTOM_TOOLBAR_HEIGHT - 48);
  const canvasHeight = Math.min(maxCanvasHeight, (canvasWidth * 16) / 9);

  const [media, setMedia] = useState<PickedImage | null>(null);
  const [elements, setElements] = useState<StoryElement[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [activeSheet, setActiveSheet] = useState<"text" | "hashtag" | "location" | "community" | "event" | "share" | null>(null);
  const [caption, setCaption] = useState("");
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [error, setError] = useState<string | null>(null);

  const [visibility, setVisibility] = useState<VisibilityMode>("followers");
  const [selectedCommunityId, setSelectedCommunityId] = useState<string | null>(null);
  const [selectedEventId, setSelectedEventId] = useState<string | null>(null);
  const [selectedLocation, setSelectedLocation] = useState<{ name: string; latitude?: number; longitude?: number } | null>(null);

  const [communities, setCommunities] = useState<AudienceCommunity[]>([]);
  const [events, setEvents] = useState<EventOption[]>([]);
  const [hashtagQuery, setHashtagQuery] = useState("");
  const [hashtags, setHashtags] = useState<HashtagOption[]>([]);
  const [loadingCommunities, setLoadingCommunities] = useState(true);
  const [loadingEvents, setLoadingEvents] = useState(true);
  const [loadingHashtags, setLoadingHashtags] = useState(false);

  useEffect(() => {
    if (!user) return;
    const load = async () => {
      const [cResult, eResult] = await Promise.all([
        api.rpc("get_communities_with_my_membership"),
        api.rpc("get_my_events")
      ]);
      if (!cResult.error && Array.isArray(cResult.data)) {
        // Authoritative audience source: only APPROVED memberships, real names,
        // deduplicated by community ID (never render the "Topluluk" fallback twice).
        const byId = new Map<string, AudienceCommunity>();
        for (const row of cResult.data as Array<Record<string, any>>) {
          if (row?.is_member !== true) continue;
          const id = String(row?.id ?? row?.community_id ?? "").trim();
          const name = String(row?.name ?? "").trim();
          if (!id || !name || byId.has(id)) continue;
          byId.set(id, {
            id,
            name,
            avatarUrl: (row?.cover_image_url ?? row?.avatar_url ?? null) as string | null,
            memberCount: typeof row?.member_count === "number" ? row.member_count : Number(row?.member_count ?? 0) || 0
          });
        }
        setCommunities([...byId.values()].sort((a, b) => a.name.localeCompare(b.name, "tr")));
      }
      setLoadingCommunities(false);
      if (!eResult.error && Array.isArray(eResult.data)) {
        const eventRows = (eResult.data as Array<{ event_id?: string; event_id_raw?: string; title?: string; starts_at?: string }>)
          .filter((r) => r.event_id || r.event_id_raw)
          .map((r) => ({
            event_id: r.event_id || r.event_id_raw || "",
            events: { title: r.title || "Etkinlik", starts_at: r.starts_at }
          }));
        setEvents(eventRows);
      }
      setLoadingEvents(false);
    };
    void load();
  }, [user?.id]);

  useEffect(() => {
    if (activeSheet !== "hashtag") return;
    const q = hashtagQuery.trim();
    setLoadingHashtags(true);
    api.rpc("search_hashtags", { target_query: q, result_limit: 20 })
      .then((res) => {
        if (!res.error) setHashtags((res.data ?? []) as HashtagOption[]);
      })
      .finally(() => setLoadingHashtags(false));
  }, [hashtagQuery, activeSheet]);

  const addElement = (partial: Omit<StoryElement, "id" | "x" | "y" | "scale" | "rotation" | "color" | "backgroundColor" | "fontSize"> & Partial<StoryElement>) => {
    const id = `${Date.now()}-${Math.random().toString(36).slice(2, 7)}`;
    const element: StoryElement = {
      id,
      type: partial.type,
      content: partial.content,
      x: 0.5,
      y: 0.45,
      scale: partial.scale ?? 1,
      rotation: partial.rotation ?? 0,
      color: partial.color ?? "#FFFFFF",
      backgroundColor: partial.backgroundColor ?? "transparent",
      fontSize: partial.fontSize ?? 22,
      width: partial.width,
      height: partial.height,
      metadata: partial.metadata
    };
    setElements((current) => [...current, element]);
    setSelectedId(id);
  };

  const updateElement = (id: string, patch: Partial<StoryElement>) => {
    setElements((current) => current.map((el) => (el.id === id ? { ...el, ...patch } : el)));
  };

  const removeElement = (id: string) => {
    setElements((current) => current.filter((el) => el.id !== id));
    if (selectedId === id) setSelectedId(null);
  };

  const pickMedia = async () => {
    if (!(await requestMediaLibraryPermission())) {
      setError("Galeri izni gerekli.");
      return;
    }
    const selected = await pickImageFromLibrary();
    if (selected) setMedia(selected);
  };

  const capturePhoto = async () => {
    const permission = await requestCameraPermission();
    if (!permission.granted) {
      setError("Kamera izni gerekli.");
      return;
    }
    const selected = await takePhotoWithCamera();
    if (selected) setMedia(selected);
  };

  const shareStory = async () => {
    if (!user) return;
    if (!media && !caption.trim() && elements.length === 0) {
      setError("Paylaşmak için en az bir medya, yazı veya etiket ekleyin.");
      return;
    }

    setUploading(true);
    setUploadProgress(10);
    setError(null);

    try {
      let mediaUrl: string | null = null;
      if (media) {
        const uploaded = await uploadStoryMedia({ userId: user.id, image: media });
        mediaUrl = uploaded.storagePath;
        setUploadProgress(60);
      }

      const elementPayload = elements.map((el) => ({
        type: el.type.toLowerCase(),
        content: el.content,
        position_x: el.x,
        position_y: el.y,
        scale: el.scale,
        rotation: el.rotation,
        color: el.color,
        background_color: el.backgroundColor,
        font_size: el.fontSize,
        width: el.width,
        height: el.height,
        metadata: el.metadata
      }));

      const hashtagNames = elements
        .filter((el) => el.type === "HASHTAG")
        .map((el) => el.content.replace(/^#/, ""));

      if (visibility === "community" && !selectedCommunityId) {
        setError("Topluluk Story'si için bir topluluk seç.");
        setUploading(false);
        return;
      }
      const communityIdNumber = selectedCommunityId ? Number(selectedCommunityId) : NaN;
      const payload: Record<string, any> = {
        target_content_type: media ? "image" : "text",
        target_body: caption.trim() || null,
        target_media_url: mediaUrl,
        // Visibility data-model contract:
        // PUBLIC    -> true,  false, []
        // FOLLOWERS -> false, true,  []
        // COMMUNITY -> false, false, [real community ID]
        target_is_public: visibility === "everyone",
        target_share_with_followers: visibility === "followers",
        target_community_ids:
          visibility === "community" && Number.isFinite(communityIdNumber) ? [communityIdNumber] : [],
        target_event_id: selectedEventId,
        target_location: selectedLocation,
        target_hashtags: hashtagNames,
        target_elements: elementPayload
      };

      const { error: rpcError } = await api.rpc("create_story_with_audience", payload);
      if (rpcError) throw new Error(rpcError.message);

      setUploadProgress(100);
      if (typeof window !== "undefined" && (window as any).Swal) {
        (window as any).Swal.fire({ icon: "success", title: "Story paylaşıldı", confirmButtonText: "Tamam" });
      }
      router.replace("/(tabs)/feed");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Story paylaşılırken bir hata oluştu.");
      setUploading(false);
    }
  };

  const confirmBack = async () => {
    if (!media && !caption && elements.length === 0) {
      router.back();
      return;
    }
    const confirmed = await showAppConfirm({
      title: "Story'den çık?",
      text: "Yaptığın değişiklikler kaybolacak.",
      confirmText: "Çık",
      confirmDanger: true
    });
    if (confirmed) router.back();
  };

  const selectedElement = useMemo(() => elements.find((el) => el.id === selectedId) ?? null, [elements, selectedId]);

  return (
    <View style={[styles.root, { paddingBottom: insets.bottom }]}>
      <View style={[styles.topBar, { paddingTop: Math.max(12, insets.top + 8) }]}>
        <Pressable accessibilityLabel="Geri" hitSlop={8} onPress={confirmBack} style={styles.iconButton}>
          <Ionicons name="chevron-back" size={26} color={colors.ink} />
        </Pressable>
        <Text style={styles.topTitle}>Yeni Story</Text>
        <Pressable
          accessibilityLabel="Paylaş"
          hitSlop={8}
          disabled={uploading}
          onPress={() => setActiveSheet("share")}
          style={[styles.shareTopButton, uploading && styles.disabled]}
        >
          {uploading ? <ActivityIndicator size="small" color={colors.actionText} /> : <Text style={styles.shareTopText}>Story paylaş</Text>}
        </Pressable>
      </View>

      <View style={styles.canvasWrap}>
        <View style={[styles.canvas, { width: canvasWidth, height: canvasHeight }]} onStartShouldSetResponder={() => { setSelectedId(null); return true; }}>
          {!media ? (
            <View style={styles.emptyCanvas}>
              <Ionicons name="image-outline" size={48} color={colors.muted} />
              <Text style={styles.emptyCanvasText}>Fotoğraf seç veya çek</Text>
              <View style={styles.emptyActions}>
                <Pressable style={styles.emptyAction} onPress={() => void pickMedia()}>
                  <Ionicons name="images-outline" size={20} color={colors.actionText} />
                  <Text style={styles.emptyActionText}>Galeri</Text>
                </Pressable>
                {Platform.OS !== "web" && (
                  <Pressable style={styles.emptyAction} onPress={() => void capturePhoto()}>
                    <Ionicons name="camera-outline" size={20} color={colors.actionText} />
                    <Text style={styles.emptyActionText}>Kamera</Text>
                  </Pressable>
                )}
              </View>
            </View>
          ) : (
            <Image source={{ uri: media.uri }} style={StyleSheet.absoluteFillObject} resizeMode="cover" />
          )}
          {elements.map((el) => (
            <DraggableElement
              key={el.id}
              element={el}
              canvasWidth={canvasWidth}
              canvasHeight={canvasHeight}
              selected={selectedId === el.id}
              onSelect={() => setSelectedId(el.id)}
              onChange={(patch) => updateElement(el.id, patch)}
            />
          ))}
        </View>
      </View>

      {selectedElement ? (
        <View style={{ paddingBottom: Math.max(12, insets.bottom + 8) }}>
          <ElementToolbar
            element={selectedElement}
            onChange={(patch) => updateElement(selectedElement.id, patch)}
            onDelete={() => removeElement(selectedElement.id)}
          />
        </View>
      ) : (
        <View style={[styles.bottomToolbar, { paddingBottom: Math.max(16, insets.bottom + 12) }]}>
          <ToolButton icon="text" label="Aa" onPress={() => setActiveSheet("text")} />
          <ToolButton icon="pricetag" label="#" onPress={() => setActiveSheet("hashtag")} />
          <ToolButton icon="location" label="Konum" onPress={() => setActiveSheet("location")} />
          <ToolButton icon="people" label="Topluluk" onPress={() => setActiveSheet("community")} />
          <ToolButton icon="ticket" label="Etkinlik" onPress={() => setActiveSheet("event")} />
        </View>
      )}

      {error ? <Text style={styles.errorBanner}>{error}</Text> : null}

      {uploading && (
        <View style={styles.progressOverlay}>
          <ActivityIndicator color={colors.accent} />
          <Text style={styles.progressText}>Story yükleniyor... %{uploadProgress}</Text>
        </View>
      )}

      <TextEditorSheet
        visible={activeSheet === "text"}
        onClose={() => setActiveSheet(null)}
        onSubmit={(text, style) => {
          addElement({ type: "TEXT", content: text, ...style });
          setActiveSheet(null);
        }}
      />

      <HashtagPickerSheet
        visible={activeSheet === "hashtag"}
        query={hashtagQuery}
        onQueryChange={setHashtagQuery}
        hashtags={hashtags}
        loading={loadingHashtags}
        onClose={() => setActiveSheet(null)}
        onSelect={(name) => {
          addElement({ type: "HASHTAG", content: `#${name}`, color: "#FFFFFF", backgroundColor: "rgba(0,0,0,0.4)", fontSize: 20 });
          setActiveSheet(null);
        }}
      />

      <LocationPickerSheet
        visible={activeSheet === "location"}
        presets={LOCATION_PRESETS}
        onClose={() => setActiveSheet(null)}
        onSelect={(loc) => {
          setSelectedLocation(loc);
          addElement({ type: "LOCATION", content: loc.name, color: "#FFFFFF", backgroundColor: "rgba(0,0,0,0.5)", fontSize: 18, metadata: loc });
          setActiveSheet(null);
        }}
      />

      <CommunityPickerSheet
        visible={activeSheet === "community"}
        communities={communities}
        loading={loadingCommunities}
        onClose={() => setActiveSheet(null)}
        onToggle={(id, name) => {
          // Sticker is visual only: it never changes the Story audience.
          // Real audience selection happens in the ShareSheet (visibility + communityId).
          const numericId = Number(id);
          if (!elements.some((el) => el.type === "COMMUNITY" && Number(el.metadata?.community_id) === numericId)) {
            addElement({ type: "COMMUNITY", content: `@${name}`, color: "#FFFFFF", backgroundColor: "rgba(0,0,0,0.4)", fontSize: 18, metadata: { community_id: Number.isFinite(numericId) ? numericId : id } });
          }
        }}
      />

      <EventPickerSheet
        visible={activeSheet === "event"}
        events={events}
        selectedId={selectedEventId}
        loading={loadingEvents}
        onClose={() => setActiveSheet(null)}
        onSelect={(id, title) => {
          setSelectedEventId(id);
          if (!elements.some((el) => el.type === "EVENT" && el.metadata?.event_id === id)) {
            addElement({ type: "EVENT", content: `🎫 ${title}`, color: "#FFFFFF", backgroundColor: "rgba(0,0,0,0.5)", fontSize: 18, metadata: { event_id: id } });
          }
          setActiveSheet(null);
        }}
      />

      <ShareSheet
        visible={activeSheet === "share"}
        onClose={() => setActiveSheet(null)}
        onShare={() => void shareStory()}
        sharing={uploading}
        visibility={visibility}
        selectedCommunityId={selectedCommunityId}
        communities={communities}
        loadingCommunities={loadingCommunities}
        onSelectVisibility={(mode, communityId) => {
          setVisibility(mode);
          setSelectedCommunityId(mode === "community" ? (communityId ?? selectedCommunityId) : null);
        }}
      />
    </View>
  );
}

function ToolButton({ icon, label, onPress }: { icon: any; label: string; onPress: () => void }) {
  return (
    <Pressable accessibilityLabel={label} onPress={onPress} style={styles.toolButton}>
      <Ionicons name={icon} size={22} color={colors.ink} />
      <Text style={styles.toolLabel}>{label}</Text>
    </Pressable>
  );
}

function DraggableElement({
  element,
  canvasWidth,
  canvasHeight,
  selected,
  onSelect,
  onChange
}: {
  element: StoryElement;
  canvasWidth: number;
  canvasHeight: number;
  selected: boolean;
  onSelect: () => void;
  onChange: (patch: Partial<StoryElement>) => void;
}) {
  const pan = useRef(new Animated.ValueXY({ x: element.x * canvasWidth, y: element.y * canvasHeight })).current;
  useEffect(() => {
    pan.setValue({ x: element.x * canvasWidth, y: element.y * canvasHeight });
  }, [canvasWidth, canvasHeight]);

  const panResponder = useRef(
    PanResponder.create({
      onStartShouldSetPanResponder: () => true,
      onMoveShouldSetPanResponder: () => true,
      onPanResponderGrant: () => {
        onSelect();
        pan.setOffset({ x: (pan as any).__getValue().x, y: (pan as any).__getValue().y });
        pan.setValue({ x: 0, y: 0 });
      },
      onPanResponderMove: Animated.event([null, { dx: pan.x, dy: pan.y }], { useNativeDriver: false }),
      onPanResponderRelease: (_evt: any, gestureState: any) => {
        pan.flattenOffset();
        const current = (pan as any).__getValue();
        const nx = Math.max(0, Math.min(1, current.x / canvasWidth));
        const ny = Math.max(0, Math.min(1, current.y / canvasHeight));
        onChange({ x: nx, y: ny });
      }
    })
  ).current;

  const isText = element.type === "TEXT" || element.type === "HASHTAG" || element.type === "LOCATION" || element.type === "COMMUNITY" || element.type === "EVENT";
  const containerStyle = {
    position: "absolute" as const,
    left: 0,
    top: 0,
    transform: [
      { translateX: pan.x },
      { translateY: pan.y },
      { scale: element.scale },
      { rotate: `${element.rotation}deg` }
    ],
    zIndex: selected ? 20 : 10,
    backgroundColor: isText ? element.backgroundColor : "transparent",
    paddingHorizontal: isText ? 10 : 0,
    paddingVertical: isText ? 6 : 0,
    borderRadius: isText ? 10 : 0,
    borderWidth: selected ? 1 : 0,
    borderColor: selected ? colors.accent : "transparent",
    borderStyle: "dashed" as const
  };

  return (
    <Animated.View {...panResponder.panHandlers} style={containerStyle}>
      {element.type === "LOCATION" ? (
        <View style={{ flexDirection: "row", alignItems: "center", gap: 4 }}>
          <Ionicons name="location" size={element.fontSize} color={element.color} />
          <Text style={{ color: element.color, fontSize: element.fontSize, fontWeight: "800" }}>{element.content}</Text>
        </View>
      ) : element.type === "EVENT" ? (
        <View style={{ flexDirection: "row", alignItems: "center", gap: 4 }}>
          <Ionicons name="ticket" size={element.fontSize} color={element.color} />
          <Text style={{ color: element.color, fontSize: element.fontSize, fontWeight: "800" }}>{element.content}</Text>
        </View>
      ) : (
        <Text
          style={{
            color: element.color,
            fontSize: element.fontSize,
            fontWeight: element.type === "TEXT" ? "700" : "800",
            textAlign: "center"
          }}
        >
          {element.content}
        </Text>
      )}
    </Animated.View>
  );
}

function ElementToolbar({
  element,
  onChange,
  onDelete
}: {
  element: StoryElement;
  onChange: (patch: Partial<StoryElement>) => void;
  onDelete: () => void;
}) {
  return (
    <View style={styles.elementToolbar}>
      <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.elementToolbarScroll}>
        <Pressable style={styles.etoolButton} onPress={() => onChange({ scale: Math.max(0.5, element.scale - 0.15) })}>
          <Ionicons name="remove-circle-outline" size={22} color={colors.ink} />
          <Text style={styles.etoolLabel}>Küçült</Text>
        </Pressable>
        <Pressable style={styles.etoolButton} onPress={() => onChange({ scale: Math.min(3, element.scale + 0.15) })}>
          <Ionicons name="add-circle-outline" size={22} color={colors.ink} />
          <Text style={styles.etoolLabel}>Büyüt</Text>
        </Pressable>
        <Pressable style={styles.etoolButton} onPress={() => onChange({ rotation: element.rotation - 15 })}>
          <Ionicons name="refresh-circle-outline" size={22} color={colors.ink} />
          <Text style={styles.etoolLabel}>Sola</Text>
        </Pressable>
        <Pressable style={styles.etoolButton} onPress={() => onChange({ rotation: element.rotation + 15 })}>
          <Ionicons name="refresh-circle" size={22} color={colors.ink} />
          <Text style={styles.etoolLabel}>Sağa</Text>
        </Pressable>
        {element.type === "TEXT" && (
          <>
            <Pressable style={styles.etoolButton} onPress={() => onChange({ fontSize: Math.max(12, element.fontSize - 4) })}>
              <Ionicons name="text-outline" size={18} color={colors.ink} />
              <Text style={styles.etoolLabel}>-</Text>
            </Pressable>
            <Pressable style={styles.etoolButton} onPress={() => onChange({ fontSize: Math.min(72, element.fontSize + 4) })}>
              <Ionicons name="text" size={24} color={colors.ink} />
              <Text style={styles.etoolLabel}>+</Text>
            </Pressable>
          </>
        )}
        <Pressable style={[styles.etoolButton, styles.etoolDelete]} onPress={onDelete}>
          <Ionicons name="trash-outline" size={22} color={colors.danger} />
          <Text style={[styles.etoolLabel, { color: colors.danger }]}>Sil</Text>
        </Pressable>
      </ScrollView>
    </View>
  );
}

function TextEditorSheet({
  visible,
  onClose,
  onSubmit
}: {
  visible: boolean;
  onClose: () => void;
  onSubmit: (text: string, style: Partial<StoryElement>) => void;
}) {
  const [text, setText] = useState("");
  const [color, setColor] = useState("#FFFFFF");
  const [bg, setBg] = useState("transparent");
  const [size, setSize] = useState(28);

  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose}>
      <KeyboardAvoidingView behavior={Platform.OS === "ios" ? "padding" : "height"} style={styles.sheetOverlay}>
        <Pressable style={styles.sheetBackdrop} onPress={onClose} />
        <View style={styles.sheetContent}>
          <View style={styles.sheetHeader}>
            <Text style={styles.sheetTitle}>Yazı ekle</Text>
            <Pressable onPress={onClose}>
              <Ionicons name="close" size={24} color={colors.ink} />
            </Pressable>
          </View>
          <TextInput
            value={text}
            onChangeText={setText}
            placeholder="Bir şeyler yaz..."
            placeholderTextColor={colors.muted}
            multiline
            autoFocus
            style={[styles.sheetInput, { color, fontSize: size }]}
          />
          <View style={styles.sheetRow}>
            <Text style={styles.sheetLabel}>Renk</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false}>
              {COLOR_PRESETS.map((c) => (
                <Pressable key={c} onPress={() => setColor(c)} style={[styles.colorDot, { backgroundColor: c }, color === c && styles.colorDotActive]} />
              ))}
            </ScrollView>
          </View>
          <View style={styles.sheetRow}>
            <Text style={styles.sheetLabel}>Arka plan</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false}>
              {BG_PRESETS.map((b) => (
                <Pressable key={b} onPress={() => setBg(b)} style={[styles.colorDot, { backgroundColor: b === "transparent" ? colors.border : b }, bg === b && styles.colorDotActive]} />
              ))}
            </ScrollView>
          </View>
          <View style={styles.sheetRow}>
            <Text style={styles.sheetLabel}>Boyut</Text>
            <Pressable style={styles.sizeButton} onPress={() => setSize((s) => Math.max(12, s - 4))}>
              <Text style={styles.sizeButtonText}>-</Text>
            </Pressable>
            <Text style={styles.sizeValue}>{size}</Text>
            <Pressable style={styles.sizeButton} onPress={() => setSize((s) => Math.min(72, s + 4))}>
              <Text style={styles.sizeButtonText}>+</Text>
            </Pressable>
          </View>
          <Pressable
            style={[styles.sheetPrimary, (!text.trim()) && styles.disabled]}
            disabled={!text.trim()}
            onPress={() => {
              onSubmit(text.trim(), { color, backgroundColor: bg, fontSize: size });
              setText("");
            }}
          >
            <Text style={styles.sheetPrimaryText}>Ekle</Text>
          </Pressable>
        </View>
      </KeyboardAvoidingView>
    </Modal>
  );
}

function HashtagPickerSheet({
  visible,
  query,
  onQueryChange,
  hashtags,
  loading,
  onClose,
  onSelect
}: {
  visible: boolean;
  query: string;
  onQueryChange: (q: string) => void;
  hashtags: HashtagOption[];
  loading: boolean;
  onClose: () => void;
  onSelect: (name: string) => void;
}) {
  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose}>
      <View style={styles.sheetOverlay}>
        <Pressable style={styles.sheetBackdrop} onPress={onClose} />
        <View style={styles.sheetContent}>
          <View style={styles.sheetHeader}>
            <Text style={styles.sheetTitle}>Hashtag ekle</Text>
            <Pressable onPress={onClose}>
              <Ionicons name="close" size={24} color={colors.ink} />
            </Pressable>
          </View>
          <View style={styles.sheetSearchRow}>
            <Text style={styles.sheetHash}>#</Text>
            <TextInput
              value={query}
              onChangeText={onQueryChange}
              placeholder="hashtag ara"
              placeholderTextColor={colors.muted}
              autoFocus
              style={styles.sheetSearchInput}
            />
          </View>
          {loading ? <ActivityIndicator color={colors.accent} /> : (
            <ScrollView style={{ maxHeight: 260 }}>
              {hashtags.map((h) => (
                <Pressable key={h.hashtag_id} style={styles.sheetRowItem} onPress={() => onSelect(h.normalized_name)}>
                  <Text style={styles.sheetRowTitle}>{h.name}</Text>
                  <Text style={styles.sheetRowMeta}>{h.usage_count} kullanım</Text>
                </Pressable>
              ))}
              {query.trim() && !hashtags.some((h) => h.normalized_name === query.trim().toLowerCase()) && (
                <Pressable style={styles.sheetRowItem} onPress={() => onSelect(query.trim().toLowerCase())}>
                  <Text style={styles.sheetRowTitle}>#{query.trim()}</Text>
                  <Text style={styles.sheetRowMeta}>Yeni oluştur</Text>
                </Pressable>
              )}
            </ScrollView>
          )}
        </View>
      </View>
    </Modal>
  );
}

function LocationPickerSheet({
  visible,
  presets,
  onClose,
  onSelect
}: {
  visible: boolean;
  presets: typeof LOCATION_PRESETS;
  onClose: () => void;
  onSelect: (loc: (typeof LOCATION_PRESETS)[number]) => void;
}) {
  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose}>
      <View style={styles.sheetOverlay}>
        <Pressable style={styles.sheetBackdrop} onPress={onClose} />
        <View style={styles.sheetContent}>
          <View style={styles.sheetHeader}>
            <Text style={styles.sheetTitle}>Konum ekle</Text>
            <Pressable onPress={onClose}>
              <Ionicons name="close" size={24} color={colors.ink} />
            </Pressable>
          </View>
          <ScrollView style={{ maxHeight: 320 }}>
            {presets.map((loc) => (
              <Pressable key={loc.name} style={styles.sheetRowItem} onPress={() => onSelect(loc)}>
                <Ionicons name="location" size={18} color={colors.accent} />
                <Text style={styles.sheetRowTitle}>{loc.name}</Text>
              </Pressable>
            ))}
          </ScrollView>
        </View>
      </View>
    </Modal>
  );
}

function CommunityAvatar({ name, avatarUrl, size = 40 }: { name: string; avatarUrl?: string | null; size?: number }) {
  if (avatarUrl) {
    return <Image source={{ uri: avatarUrl }} style={{ width: size, height: size, borderRadius: size / 2, backgroundColor: colors.surfaceStrong }} />;
  }
  return (
    <View style={{ width: size, height: size, borderRadius: size / 2, backgroundColor: colors.accentSoft, alignItems: "center", justifyContent: "center" }}>
      <Text style={{ fontSize: size * 0.45, fontWeight: "900", color: colors.accent }}>{(name || "?").slice(0, 1).toLocaleUpperCase("tr")}</Text>
    </View>
  );
}

function CommunityPickerSheet({
  visible,
  communities,
  loading,
  onClose,
  onToggle
}: {
  visible: boolean;
  communities: AudienceCommunity[];
  loading: boolean;
  onClose: () => void;
  onToggle: (id: string, name: string) => void;
}) {
  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose}>
      <View style={styles.sheetOverlay}>
        <Pressable style={styles.sheetBackdrop} onPress={onClose} />
        <View style={styles.sheetContent}>
          <View style={styles.sheetHeader}>
            <Text style={styles.sheetTitle}>Topluluk etiketle</Text>
            <Pressable onPress={onClose}>
              <Ionicons name="close" size={24} color={colors.ink} />
            </Pressable>
          </View>
          <Text style={styles.sheetHint}>Etiket yalnızca görseldir; Story'nin kimlerin göreceğini Paylaşım seçenekleri belirler.</Text>
          {loading ? <ActivityIndicator color={colors.accent} /> : communities.length === 0 ? (
            <Text style={styles.emptyState}>Henüz bir topluluğa katılmadın.</Text>
          ) : (
            <ScrollView style={{ maxHeight: 320 }}>
              {communities.map((c) => (
                <Pressable key={c.id} style={styles.sheetRowItem} onPress={() => onToggle(c.id, c.name)}>
                  <View style={styles.communityRow}>
                    <CommunityAvatar name={c.name} avatarUrl={c.avatarUrl} />
                    <View style={styles.communityText}>
                      <Text style={styles.sheetRowTitle} numberOfLines={1}>{c.name}</Text>
                      <Text style={styles.sheetRowMeta}>{c.memberCount ?? 0} üye</Text>
                    </View>
                  </View>
                  <Ionicons name="add-circle-outline" size={22} color={colors.accent} />
                </Pressable>
              ))}
            </ScrollView>
          )}
        </View>
      </View>
    </Modal>
  );
}

function EventPickerSheet({
  visible,
  events,
  selectedId,
  loading,
  onClose,
  onSelect
}: {
  visible: boolean;
  events: EventOption[];
  selectedId: string | null;
  loading: boolean;
  onClose: () => void;
  onSelect: (id: string, title: string) => void;
}) {
  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose}>
      <View style={styles.sheetOverlay}>
        <Pressable style={styles.sheetBackdrop} onPress={onClose} />
        <View style={styles.sheetContent}>
          <View style={styles.sheetHeader}>
            <Text style={styles.sheetTitle}>Etkinlik etiketle</Text>
            <Pressable onPress={onClose}>
              <Ionicons name="close" size={24} color={colors.ink} />
            </Pressable>
          </View>
          {loading ? <ActivityIndicator color={colors.accent} /> : (
            <ScrollView style={{ maxHeight: 320 }}>
              {events.map((e, idx) => {
                const id = e.event_id || (e.events as { id?: string } | null)?.id;
                if (!id) return null;
                const selected = selectedId === id;
                return (
                  <Pressable key={id || idx} style={[styles.sheetRowItem, selected && styles.sheetRowItemActive]} onPress={() => onSelect(id, e.events?.title ?? "Etkinlik")}>
                    <Text style={styles.sheetRowTitle}>{e.events?.title ?? "Etkinlik"}</Text>
                    {selected && <Ionicons name="checkmark" size={20} color={colors.accent} />}
                  </Pressable>
                );
              })}
            </ScrollView>
          )}
        </View>
      </View>
    </Modal>
  );
}

function ShareSheet({
  visible,
  onClose,
  onShare,
  sharing,
  visibility,
  selectedCommunityId,
  communities,
  loadingCommunities,
  onSelectVisibility
}: {
  visible: boolean;
  onClose: () => void;
  onShare: () => void;
  sharing: boolean;
  visibility: VisibilityMode;
  selectedCommunityId: string | null;
  communities: AudienceCommunity[];
  loadingCommunities: boolean;
  onSelectVisibility: (mode: VisibilityMode, communityId?: string | null) => void;
}) {
  const selectedCommunity = communities.find((c) => c.id === selectedCommunityId) ?? null;
  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose}>
      <View style={styles.sheetOverlay}>
        <Pressable style={styles.sheetBackdrop} onPress={onClose} />
        <View style={styles.sheetContent}>
          <View style={styles.sheetHeader}>
            <Text style={styles.sheetTitle}>Paylaşım seçenekleri</Text>
            <Pressable onPress={onClose}>
              <Ionicons name="close" size={24} color={colors.ink} />
            </Pressable>
          </View>
          <ScrollView style={{ maxHeight: 380 }}>
            <Pressable
              style={[styles.sheetRowItem, visibility === "everyone" && styles.sheetRowItemActive]}
              onPress={() => onSelectVisibility("everyone")}
            >
              <View style={styles.communityRow}>
                <Ionicons name="globe-outline" size={22} color={colors.accent} />
                <Text style={styles.sheetRowTitle}>Herkese</Text>
              </View>
              {visibility === "everyone" && <Ionicons name="checkmark-circle" size={22} color={colors.accent} />}
            </Pressable>
            <Pressable
              style={[styles.sheetRowItem, visibility === "followers" && styles.sheetRowItemActive]}
              onPress={() => onSelectVisibility("followers")}
            >
              <View style={styles.communityRow}>
                <Ionicons name="people-outline" size={22} color={colors.accent} />
                <Text style={styles.sheetRowTitle}>Takipçilerim</Text>
              </View>
              {visibility === "followers" && <Ionicons name="checkmark-circle" size={22} color={colors.accent} />}
            </Pressable>
            <Text style={styles.sectionTitle}>Topluluklarım</Text>
            {loadingCommunities ? <ActivityIndicator color={colors.accent} /> : communities.length === 0 ? (
              <Text style={styles.emptyState}>Henüz bir topluluğa katılmadın.</Text>
            ) : (
              communities.map((c) => {
                const selected = visibility === "community" && selectedCommunityId === c.id;
                return (
                  <Pressable
                    key={c.id}
                    style={[styles.sheetRowItem, selected && styles.sheetRowItemActive]}
                    onPress={() => onSelectVisibility("community", c.id)}
                  >
                    <View style={styles.communityRow}>
                      <CommunityAvatar name={c.name} avatarUrl={c.avatarUrl} />
                      <View style={styles.communityText}>
                        <Text style={styles.sheetRowTitle} numberOfLines={1}>{c.name}</Text>
                        <Text style={styles.sheetRowMeta}>{c.memberCount ?? 0} üye</Text>
                      </View>
                    </View>
                    {selected
                      ? <Ionicons name="checkmark-circle" size={22} color={colors.accent} />
                      : <Ionicons name="ellipse-outline" size={22} color={colors.muted} />}
                  </Pressable>
                );
              })
            )}
          </ScrollView>
          {visibility === "community" && selectedCommunity && (
            <Text style={styles.privacyNote}>
              Bu Story yalnızca {selectedCommunity.name} üyeleri tarafından görülebilir.
            </Text>
          )}
          <Pressable style={[styles.sheetPrimary, sharing && styles.disabled]} disabled={sharing} onPress={onShare}>
            {sharing ? <ActivityIndicator size="small" color={colors.actionText} /> : <Text style={styles.sheetPrimaryText}>Story paylaş</Text>}
          </Pressable>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: colors.page },
  topBar: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 14,
    paddingBottom: 12,
    backgroundColor: colors.surface,
    borderBottomWidth: 1,
    borderBottomColor: colors.border,
    shadowColor: colors.shadow,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 1,
    shadowRadius: 12,
    elevation: 4,
    zIndex: 10
  },
  iconButton: {
    width: 42,
    height: 42,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 999,
    backgroundColor: colors.surfaceStrong,
    borderWidth: 1,
    borderColor: colors.border
  },
  topTitle: { fontSize: 18, fontWeight: "900", color: colors.ink, letterSpacing: -0.3 },
  shareTopButton: {
    minWidth: 96,
    height: 40,
    paddingHorizontal: 18,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 999,
    backgroundColor: colors.action,
    shadowColor: colors.action,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.35,
    shadowRadius: 10,
    elevation: 5
  },
  shareTopText: { color: colors.actionText, fontWeight: "900", fontSize: 14 },
  disabled: { opacity: 0.5 },
  canvasWrap: { flex: 1, alignItems: "center", justifyContent: "center", paddingVertical: 16 },
  canvas: { borderRadius: 20, overflow: "hidden", backgroundColor: colors.surfaceStrong, position: "relative", shadowColor: colors.shadow, shadowOffset: { width: 0, height: 8 }, shadowOpacity: 1, shadowRadius: 24, elevation: 6 },
  emptyCanvas: { flex: 1, alignItems: "center", justifyContent: "center", gap: 16, padding: 28 },
  emptyCanvasText: { color: colors.muted, fontSize: 16, fontWeight: "800" },
  emptyActions: { flexDirection: "row", gap: 14 },
  emptyAction: { flexDirection: "row", alignItems: "center", gap: 8, paddingHorizontal: 20, paddingVertical: 12, borderRadius: 999, backgroundColor: colors.action, shadowColor: colors.action, shadowOffset: { width: 0, height: 4 }, shadowOpacity: 0.3, shadowRadius: 10, elevation: 4 },
  emptyActionText: { color: colors.actionText, fontWeight: "900", fontSize: 14 },
  bottomToolbar: { flexDirection: "row", justifyContent: "space-around", alignItems: "center", paddingTop: 12, paddingHorizontal: 8, paddingBottom: 16, borderTopWidth: 1, borderTopColor: colors.border, backgroundColor: colors.surface },
  toolButton: { alignItems: "center", gap: 5, padding: 10 },
  toolLabel: { fontSize: 11, fontWeight: "800", color: colors.ink },
  elementToolbar: { paddingVertical: 8, paddingBottom: 8, borderTopWidth: 1, borderTopColor: colors.border, backgroundColor: colors.surface },
  elementToolbarScroll: { paddingHorizontal: 8, gap: 8, alignItems: "center" },
  etoolButton: { alignItems: "center", gap: 3, paddingHorizontal: 12, paddingVertical: 6 },
  etoolDelete: { marginLeft: 8 },
  etoolLabel: { fontSize: 10, fontWeight: "800", color: colors.ink },
  errorBanner: { marginHorizontal: 16, marginBottom: 8, padding: 10, borderRadius: 10, backgroundColor: colors.dangerSoft, color: colors.danger, fontWeight: "700", textAlign: "center" },
  progressOverlay: { ...StyleSheet.absoluteFillObject, alignItems: "center", justifyContent: "center", gap: 10, backgroundColor: "rgba(0,0,0,0.55)" },
  progressText: { color: "#fff", fontWeight: "900" },
  sheetOverlay: { flex: 1, justifyContent: "flex-end" },
  sheetBackdrop: { ...StyleSheet.absoluteFillObject, backgroundColor: "rgba(0,0,0,0.45)" },
  sheetContent: { padding: 16, paddingBottom: 28, borderTopLeftRadius: 24, borderTopRightRadius: 24, backgroundColor: colors.surface },
  sheetHeader: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", marginBottom: 12 },
  sheetTitle: { fontSize: 18, fontWeight: "900", color: colors.ink },
  sheetInput: { minHeight: 80, padding: 12, borderRadius: 16, backgroundColor: colors.page, borderWidth: 1, borderColor: colors.border, marginBottom: 12 },
  sheetRow: { flexDirection: "row", alignItems: "center", gap: 10, marginBottom: 12 },
  sheetLabel: { width: 70, fontSize: 13, fontWeight: "900", color: colors.muted },
  colorDot: { width: 28, height: 28, borderRadius: 14, marginRight: 8, borderWidth: 1, borderColor: colors.border },
  colorDotActive: { borderWidth: 2, borderColor: colors.accent },
  sizeButton: { width: 32, height: 32, alignItems: "center", justifyContent: "center", borderRadius: 8, backgroundColor: colors.page, borderWidth: 1, borderColor: colors.border },
  sizeButtonText: { fontSize: 18, fontWeight: "900", color: colors.ink },
  sizeValue: { width: 28, textAlign: "center", fontWeight: "900", color: colors.ink },
  sheetPrimary: { marginTop: 4, height: 48, alignItems: "center", justifyContent: "center", borderRadius: 14, backgroundColor: colors.action },
  sheetPrimaryText: { color: colors.actionText, fontWeight: "900", fontSize: 16 },
  sheetSearchRow: { flexDirection: "row", alignItems: "center", gap: 8, paddingHorizontal: 12, paddingVertical: 10, borderRadius: 14, backgroundColor: colors.page, borderWidth: 1, borderColor: colors.border, marginBottom: 8 },
  sheetHash: { fontSize: 20, fontWeight: "900", color: colors.accent },
  sheetSearchInput: { flex: 1, fontSize: 16, color: colors.ink, fontWeight: "700" },
  sheetRowItem: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", paddingVertical: 12, borderBottomWidth: 1, borderBottomColor: colors.border },
  sheetRowItemActive: { backgroundColor: colors.accentSoft },
  sheetRowTitle: { fontSize: 15, fontWeight: "800", color: colors.ink },
  sheetRowMeta: { fontSize: 12, color: colors.muted },
  sheetHint: { fontSize: 12, color: colors.muted, marginBottom: 8 },
  sectionTitle: { fontSize: 13, fontWeight: "900", color: colors.muted, marginTop: 12, marginBottom: 4, textTransform: "uppercase" as const },
  communityRow: { flexDirection: "row", alignItems: "center", gap: 10, flex: 1 },
  communityText: { flex: 1 },
  emptyState: { fontSize: 14, fontWeight: "700", color: colors.muted, textAlign: "center" as const, paddingVertical: 16 },
  privacyNote: { fontSize: 12, fontWeight: "700", color: colors.muted, backgroundColor: colors.accentSoft, borderRadius: 10, padding: 10, marginTop: 8, textAlign: "center" as const }
});
