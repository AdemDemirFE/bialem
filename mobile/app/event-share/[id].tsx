import { Ionicons } from "@expo/vector-icons";
import * as Linking from "expo-linking";
import { Link, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import { Image, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { Reveal, Skeleton } from "../../src/animations";
import { eventDeepLink } from "../../src/lib/links";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";
import { imageSources } from "../../src/theme/images";

type PublicEvent = {
  event_id: string;
  title: string;
  description: string | null;
  starts_at: string;
  ends_at: string | null;
  location_name: string | null;
  address_text: string | null;
  cover_image_url: string | null;
  capacity: number | null;
  community_name: string;
  organizer_display_name: string;
  approved_count: number;
};

export default function PublicEventShareScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const [event, setEvent] = useState<PublicEvent | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      if (!id) return;
      const result = await api.rpc("get_public_event_share", { target_event_id: id }).maybeSingle();
      if (result.error || !result.data) setError(result.error?.message || "Bu etkinlik artık yayında değil.");
      else setEvent(result.data as PublicEvent);
      setLoading(false);
    };
    void load();
  }, [id]);

  return (
    <ScrollView contentContainerStyle={styles.page}>
      <View style={styles.shell}>
        <View style={styles.brandRow}>
          <Image source={imageSources.logo} style={styles.logo} resizeMode="contain" />
          <Text style={styles.brand}>BIALEM</Text>
        </View>

        {loading ? (
          <View style={{ gap: 12 }}>
            <Skeleton height={300} borderRadius={30} />
            <Skeleton height={150} borderRadius={26} />
          </View>
        ) : null}
        {error ? <View style={styles.card}><Text style={styles.error}>{error}</Text></View> : null}

        {event ? (
          <>
            <Reveal>
            <View style={styles.hero}>
              {event.cover_image_url ? <Image source={{ uri: event.cover_image_url }} style={styles.cover} /> : <View style={styles.coverFallback}><View style={styles.orbOne} /><View style={styles.orbTwo} /><Ionicons name="sparkles" size={42} color="#ffffff" /></View>}
              <View style={styles.heroCopy}>
                <Text style={styles.community}>{event.community_name}</Text>
                <Text style={styles.title}>{event.title}</Text>
                <Text style={styles.description}>{event.description || "Yeni insanlarla tanış, birlikte deneyimle."}</Text>
              </View>
            </View>
            </Reveal>

            <Reveal index={1}>
            <View style={styles.card}>
              <Info icon="calendar" value={formatDate(event.starts_at)} />
              <Info icon="location" value={[event.location_name, event.address_text].filter(Boolean).join(" - ") || "Mekân yakında"} />
              <Info icon="people" value={`${event.approved_count} onaylı katılımcı${event.capacity ? ` / ${event.capacity} kontenjan` : ""}`} />
              <Info icon="person-circle" value={`Organizatör: ${event.organizer_display_name}`} />
            </View>
            </Reveal>

            <Reveal index={2}>
            <Pressable
              style={({ pressed }) => [styles.primaryButton, pressed && { opacity: 0.92, transform: [{ scale: 0.98 }] }]}
              onPress={() => void Linking.openURL(eventDeepLink(event.event_id))}
            >
              <Text style={styles.primaryText}>Bialem'da aç ve katıl</Text>
              <Ionicons name="arrow-forward" size={20} color={colors.actionText} />
            </Pressable>
            </Reveal>
            <Link href="/" asChild>
              <Pressable style={styles.secondaryButton}><Text style={styles.secondaryText}>Bialem'yı keşfet</Text></Pressable>
            </Link>
          </>
        ) : null}
      </View>
    </ScrollView>
  );
}

function Info({ icon, value }: { icon: keyof typeof Ionicons.glyphMap; value: string }) {
  return <View style={styles.infoRow}><Ionicons name={icon} size={20} color={colors.accent} /><Text style={styles.infoText}>{value}</Text></View>;
}

function formatDate(value: string) {
  return new Date(value).toLocaleString("tr-TR", { weekday: "long", day: "numeric", month: "long", year: "numeric", hour: "2-digit", minute: "2-digit" });
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, backgroundColor: colors.page, padding: 20, paddingTop: 42, alignItems: "center" },
  shell: { width: "100%", maxWidth: 680, gap: 18 },
  brandRow: { flexDirection: "row", alignItems: "center", gap: 10 },
  logo: { width: 48, height: 48, borderRadius: 15, backgroundColor: "#ffffff" },
  brand: { color: colors.ink, fontSize: 17, fontWeight: "900", letterSpacing: 2 },
  hero: { overflow: "hidden", borderRadius: 30, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  cover: { width: "100%", height: 240 },
  coverFallback: { width: "100%", height: 240, alignItems: "center", justifyContent: "center", overflow: "hidden", backgroundColor: colors.brandInk },
  orbOne: { position: "absolute", width: 250, height: 250, borderRadius: 125, right: -70, top: -80, backgroundColor: colors.accent },
  orbTwo: { position: "absolute", width: 190, height: 190, borderRadius: 95, left: -60, bottom: -75, backgroundColor: colors.aqua },
  heroCopy: { padding: 22, gap: 11 },
  community: { color: colors.accent, fontSize: 13, fontWeight: "900", textTransform: "uppercase", letterSpacing: 1 },
  title: { color: colors.ink, fontSize: 35, lineHeight: 40, fontWeight: "900" },
  description: { color: colors.muted, fontSize: 16, lineHeight: 24 },
  card: { padding: 20, gap: 15, borderRadius: 26, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  infoRow: { flexDirection: "row", alignItems: "flex-start", gap: 11 },
  infoText: { flex: 1, color: colors.ink, fontSize: 15, lineHeight: 21, fontWeight: "700" },
  primaryButton: { flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 9, paddingVertical: 17, borderRadius: 999, backgroundColor: colors.action },
  primaryText: { color: colors.actionText, fontSize: 16, fontWeight: "900" },
  secondaryButton: { alignItems: "center", paddingVertical: 15, borderRadius: 999, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  secondaryText: { color: colors.ink, fontSize: 15, fontWeight: "800" },
  error: { color: colors.danger, fontSize: 15, lineHeight: 22, fontWeight: "700" }
});
