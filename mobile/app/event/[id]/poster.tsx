import { Ionicons } from "@expo/vector-icons";
import * as Sharing from "expo-sharing";
import { Link, useLocalSearchParams } from "expo-router";
import { useEffect, useRef, useState } from "react";
import { ActivityIndicator, Image, Pressable, ScrollView, Share, StyleSheet, Text, useWindowDimensions, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { BackButton } from "../../../src/components/IconButton";
import QRCode from "react-native-qrcode-svg";
import { captureRef } from "react-native-view-shot";
import { eventPublicUrl } from "../../../src/lib/links";
import { api } from "../../../src/lib/api";
import { colors } from "../../../src/theme/colors";
import { imageSources } from "../../../src/theme/images";

type PosterEvent = {
  id: string;
  title: string;
  description: string | null;
  starts_at: string;
  location_name: string | null;
  community_id: string;
  cover_image_url: string | null;
};

type PosterTheme = {
  label: string;
  icon: keyof typeof Ionicons.glyphMap;
  base: string;
  panel: string;
  accent: string;
  signal: string;
};

const posterThemes: Array<{ words: string[]; theme: PosterTheme }> = [
  { words: ["konser", "müzik", "festival", "dj", "sahne"], theme: { label: "MÜZİK & SAHNE", icon: "musical-notes", base: "#07183f", panel: "#15275c", accent: "#ffb11b", signal: "#913dff" } },
  { words: ["tiyatro", "stand up", "komedi", "gösteri"], theme: { label: "SAHNE & GÖSTERİ", icon: "mic", base: "#24103f", panel: "#4a1760", accent: "#ff9d2e", signal: "#e33d78" } },
  { words: ["atölye", "workshop", "seramik", "resim", "tasarım"], theme: { label: "ATÖLYE & ÜRETİM", icon: "color-palette", base: "#073c45", panel: "#0b6265", accent: "#ffd145", signal: "#12c4b2" } },
  { words: ["doğa", "kamp", "yürüyüş", "koşu", "spor", "yoga"], theme: { label: "DOĞA & HAREKET", icon: "leaf", base: "#133627", panel: "#235d3e", accent: "#ffc33b", signal: "#54bf68" } },
  { words: ["yemek", "kahve", "gurme", "restoran", "mangal"], theme: { label: "GURME & SOFRA", icon: "restaurant", base: "#481d17", panel: "#7a3023", accent: "#ffc037", signal: "#f05a3e" } }
];

const fallbackTheme: PosterTheme = {
  label: "BİRLİKTE DENEYİMLE",
  icon: "sparkles",
  base: "#081a44",
  panel: "#17326b",
  accent: "#ffad16",
  signal: "#7b35ff"
};

export default function EventPosterScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const insets = useSafeAreaInsets();
  const { width: windowWidth } = useWindowDimensions();
  const posterRef = useRef<View>(null);
  const [event, setEvent] = useState<PosterEvent | null>(null);
  const [communityName, setCommunityName] = useState("Bialem Topluluğu");
  const [loading, setLoading] = useState(true);
  const [sharing, setSharing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      if (!id) return;
      const eventResult = await api
        .from("events")
        .select("id, title, description, starts_at, location_name, community_id, cover_image_url")
        .eq("id", id)
        .maybeSingle<PosterEvent>();

      if (eventResult.error || !eventResult.data) {
        setError(eventResult.error?.message || "Etkinlik bulunamadı.");
        setLoading(false);
        return;
      }

      setEvent(eventResult.data);
      const communityResult = await api
        .from("communities")
        .select("name")
        .eq("id", eventResult.data.community_id)
        .maybeSingle<{ name: string }>();
      if (communityResult.data) setCommunityName(communityResult.data.name);
      setLoading(false);
    };

    void load();
  }, [id]);

  const sharePoster = async () => {
    if (!event || !posterRef.current) return;
    setSharing(true);
    setError(null);
    try {
      const imageUri = await captureRef(posterRef, { format: "png", quality: 1, result: "tmpfile" });
      if (await Sharing.isAvailableAsync()) {
        await Sharing.shareAsync(imageUri, {
          mimeType: "image/png",
          dialogTitle: "Instagram Hikâyeleri veya gönderi olarak paylaş"
        });
      } else {
        await Share.share({ message: `${event.title}\n${eventPublicUrl(event.id)}` });
      }
    } catch (shareError) {
      setError(shareError instanceof Error ? shareError.message : "Afiş paylaşılamadı.");
    } finally {
      setSharing(false);
    }
  };

  const posterWidth = Math.min(360, windowWidth - 32);
  const posterHeight = posterWidth * (16 / 9);
  const theme = event ? selectPosterTheme(event, communityName) : fallbackTheme;

  return (
    <ScrollView
      style={styles.screen}
      contentInsetAdjustmentBehavior="automatic"
      contentContainerStyle={[
        styles.page,
        { paddingTop: Math.max(18, insets.top + 8), paddingBottom: Math.max(28, insets.bottom + 22) }
      ]}
    >
      <View style={styles.topBar}>
        <Link href={`/event/${id}` as never} asChild>
          <BackButton />
        </Link>
        <View style={styles.topCopy}>
          <Text style={styles.kicker}>AFİŞ STÜDYOSU</Text>
          <Text style={styles.heading}>Etkinliğine özel paylaşım</Text>
        </View>
      </View>

      {loading ? <ActivityIndicator color={colors.accent} /> : null}
      {error ? <Text style={styles.error}>{error}</Text> : null}

      {event ? (
        <>
          <View
            ref={posterRef}
            collapsable={false}
            style={[styles.poster, { width: posterWidth, height: posterHeight, backgroundColor: theme.base }]}
          >
            {event.cover_image_url ? (
              <Image source={{ uri: event.cover_image_url }} style={styles.coverImage} resizeMode="cover" />
            ) : null}
            <View style={[styles.coverShade, { backgroundColor: event.cover_image_url ? "rgba(5,14,36,.68)" : "transparent" }]} />
            <View style={[styles.signalOrb, { backgroundColor: theme.signal }]} />
            <View style={[styles.accentOrb, { backgroundColor: theme.accent }]} />
            <View style={[styles.posterPanel, { backgroundColor: event.cover_image_url ? "rgba(7,18,47,.88)" : theme.panel }]} />
            <View style={styles.stripeOne} />
            <View style={styles.stripeTwo} />

            <View style={styles.brandRow}>
              <View style={styles.logoFrame}>
                <Image source={imageSources.logo} style={styles.logo} resizeMode="contain" />
              </View>
              <View style={styles.brandCopy}>
                <Text style={styles.brand}>BİALEM</Text>
                <Text style={styles.brandSub}>BİRLİKTE DAHA FAZLASI</Text>
              </View>
              <View style={[styles.themeIcon, { backgroundColor: theme.accent }]}>
                <Ionicons name={theme.icon} size={22} color="#081a44" />
              </View>
            </View>

            <View style={styles.posterCopy}>
              <Text style={[styles.themeLabel, { color: theme.accent }]}>{theme.label}</Text>
              <Text style={styles.posterTitle} numberOfLines={4} adjustsFontSizeToFit minimumFontScale={0.72}>{event.title}</Text>
              <View style={styles.detailRule} />
              <View style={styles.detailRow}>
                <Ionicons name="calendar" size={16} color={theme.accent} />
                <Text style={styles.date}>{formatPosterDate(event.starts_at)}</Text>
              </View>
              <View style={styles.detailRow}>
                <Ionicons name="location" size={16} color={theme.accent} />
                <Text style={styles.location} numberOfLines={2}>{event.location_name || "Mekân etkinlik sayfasında"}</Text>
              </View>
              <Text style={styles.community} numberOfLines={1}>{communityName}</Text>
            </View>

            <View style={styles.posterFooter}>
              <View style={styles.qrCard}>
                <QRCode value={eventPublicUrl(event.id)} size={68} color="#081a44" backgroundColor="#ffffff" />
              </View>
              <View style={styles.footerCopy}>
                <Text style={styles.scan}>Detayları gör, katılımını tamamla.</Text>
                <Text style={[styles.domain, { color: theme.accent }]}>bialem.app</Text>
              </View>
            </View>
          </View>

          <Text style={styles.hint}>Afiş; etkinliğin başlığı, kapağı ve içeriğine göre otomatik hazırlanır. Paylaş menüsünden Instagram'ı seçebilirsin.</Text>
          <Pressable style={[styles.primaryButton, sharing && styles.disabled]} onPress={() => void sharePoster()}>
            <Ionicons name="logo-instagram" size={20} color={colors.actionText} />
            <Text style={styles.primaryText}>{sharing ? "Afiş hazırlanıyor..." : "Instagram'da paylaş"}</Text>
          </Pressable>
        </>
      ) : null}
    </ScrollView>
  );
}

function selectPosterTheme(event: PosterEvent, communityName: string) {
  const source = `${event.title} ${event.description ?? ""} ${event.location_name ?? ""} ${communityName}`.toLocaleLowerCase("tr-TR");
  return posterThemes.find(({ words }) => words.some((word) => source.includes(word)))?.theme ?? fallbackTheme;
}

function formatPosterDate(value: string) {
  return new Date(value).toLocaleString("tr-TR", {
    weekday: "long",
    day: "numeric",
    month: "long",
    hour: "2-digit",
    minute: "2-digit"
  });
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  page: { flexGrow: 1, paddingHorizontal: 16, gap: 18, backgroundColor: colors.page, alignItems: "center" },
  topBar: { width: "100%", maxWidth: 440, flexDirection: "row", alignItems: "center", gap: 12 },
  backButton: { width: 44, height: 44, borderRadius: 22, alignItems: "center", justifyContent: "center", backgroundColor: colors.surface },
  topCopy: { flex: 1 },
  kicker: { color: colors.accent, fontSize: 12, fontWeight: "900", letterSpacing: 1.2 },
  heading: { color: colors.ink, fontSize: 22, lineHeight: 27, fontWeight: "900" },
  poster: { borderRadius: 34, overflow: "hidden", padding: 25, justifyContent: "space-between" },
  coverImage: { ...StyleSheet.absoluteFillObject },
  coverShade: { ...StyleSheet.absoluteFillObject },
  signalOrb: { position: "absolute", width: 330, height: 330, borderRadius: 165, right: -180, top: -112, opacity: 0.82 },
  accentOrb: { position: "absolute", width: 145, height: 145, borderRadius: 73, left: -75, bottom: 74, opacity: 0.95 },
  posterPanel: { position: "absolute", left: 15, right: 15, top: "31%", bottom: 103, borderRadius: 28, opacity: 0.95 },
  stripeOne: { position: "absolute", right: -45, top: 148, width: 220, height: 8, backgroundColor: "rgba(255,255,255,.16)", transform: [{ rotate: "-12deg" }] },
  stripeTwo: { position: "absolute", right: -20, top: 170, width: 155, height: 3, backgroundColor: "rgba(255,255,255,.24)", transform: [{ rotate: "-12deg" }] },
  brandRow: { flexDirection: "row", alignItems: "center", gap: 9 },
  logoFrame: { width: 46, height: 46, padding: 3, borderRadius: 15, backgroundColor: "#ffffff" },
  logo: { width: "100%", height: "100%" },
  brandCopy: { flex: 1, gap: 2 },
  brand: { color: "#ffffff", fontSize: 14, fontWeight: "900", letterSpacing: 2.1 },
  brandSub: { color: "rgba(255,255,255,.74)", fontSize: 7, fontWeight: "800", letterSpacing: 1 },
  themeIcon: { width: 42, height: 42, borderRadius: 15, alignItems: "center", justifyContent: "center" },
  posterCopy: { gap: 9, paddingHorizontal: 3 },
  themeLabel: { fontSize: 11, fontWeight: "900", letterSpacing: 1.7 },
  posterTitle: { color: "#ffffff", fontSize: 38, lineHeight: 41, fontWeight: "900" },
  detailRule: { width: 54, height: 4, borderRadius: 2, backgroundColor: "rgba(255,255,255,.35)", marginVertical: 2 },
  detailRow: { flexDirection: "row", alignItems: "flex-start", gap: 8 },
  date: { flex: 1, color: "#ffffff", fontSize: 14, lineHeight: 19, fontWeight: "800", textTransform: "capitalize" },
  location: { flex: 1, color: "#d9e4ff", fontSize: 13, lineHeight: 18 },
  community: { alignSelf: "flex-start", marginTop: 2, color: "#ffffff", borderWidth: 1, borderColor: "rgba(255,255,255,.32)", borderRadius: 999, paddingHorizontal: 11, paddingVertical: 5, fontSize: 10, fontWeight: "800" },
  posterFooter: { flexDirection: "row", alignItems: "center", gap: 13 },
  qrCard: { padding: 7, backgroundColor: "#ffffff", borderRadius: 15 },
  footerCopy: { flex: 1, gap: 5 },
  scan: { color: "#ffffff", fontSize: 12, lineHeight: 17, fontWeight: "700" },
  domain: { fontSize: 17, fontWeight: "900" },
  hint: { width: "100%", maxWidth: 440, color: colors.muted, textAlign: "center", fontSize: 13, lineHeight: 19 },
  primaryButton: { width: "100%", maxWidth: 440, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 9, paddingVertical: 16, borderRadius: 999, backgroundColor: colors.action },
  primaryText: { color: colors.actionText, fontSize: 16, fontWeight: "900" },
  disabled: { opacity: 0.65 },
  error: { width: "100%", maxWidth: 440, color: colors.danger, fontWeight: "700" }
});
