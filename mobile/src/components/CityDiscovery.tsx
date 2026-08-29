import { Ionicons } from "@expo/vector-icons";
import { Link } from "expo-router";
import { type ReactNode, useEffect, useRef, useState } from "react";
import { ActivityIndicator, Image, Platform, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { api } from "../lib/api";
import { normalizeImageUrl } from "../lib/media-url";
import { colors } from "../theme/colors";

export type CityRadarEvent = {
  event_id: string;
  title: string;
  description: string | null;
  category: string;
  city: string;
  venue_name: string | null;
  address_text: string | null;
  starts_at: string;
  ends_at: string | null;
  cover_image_url: string | null;
  price_label: string | null;
  source_name: string;
  source_url: string | null;
  ticket_url: string | null;
  interested_count: number;
  companion_count: number;
  is_interested: boolean;
  is_looking_for_company: boolean;
};

export function CityDiscovery({ city, children }: { city?: string | null; children: ReactNode }) {
  const [events, setEvents] = useState<CityRadarEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [workingId, setWorkingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [failedImages, setFailedImages] = useState<Set<string>>(new Set());
  const radarScrollRef = useRef<typeof ScrollView>(null);
  const radarScrollOffset = useRef(0);

  const loadEvents = async () => {
    setError(null);
    const { data, error: radarError } = await api.rpc("get_city_radar", {
      target_city: city?.trim() || null
    });

    if (radarError) setError(radarError.message);
    else setEvents((data ?? []) as CityRadarEvent[]);
    setLoading(false);
  };

  useEffect(() => {
    setLoading(true);
    void loadEvents();
  }, [city]);

  const markCompanionIntent = async (event: CityRadarEvent) => {
    setWorkingId(event.event_id);
    setError(null);
    const { error: interestError } = await api.rpc("set_city_event_interest", {
      target_event_id: event.event_id,
      target_looking_for_company: !event.is_looking_for_company
    });
    if (interestError) setError(interestError.message);
    else await loadEvents();
    setWorkingId(null);
  };

  const companionEvents = events.filter((event) => event.companion_count > 0 || event.is_looking_for_company).slice(0, 4);

  const scrollRadar = (direction: -1 | 1) => {
    radarScrollOffset.current = Math.max(0, radarScrollOffset.current + direction * 598);
    radarScrollRef.current?.scrollTo({ x: radarScrollOffset.current, animated: true });
  };

  return (
    <>
      <View style={styles.radarSection}>
        <View style={styles.headingRow}>
          <View style={styles.headingCopy}>
            <Text style={styles.kicker}>ŞEHİR RADARI</Text>
            <Text style={styles.title}>{city ? `${city}'da neler var?` : "Şehrin ritmini yakala"}</Text>
            <Text style={styles.subtitle}>Küratörlü konser, sergi, sahne ve atölyeler. Kaynağı her kartta açıkça görürsün.</Text>
          </View>
          <View style={styles.radarActions}>
            <View style={styles.radarMark}>
              <Ionicons name="radio" size={25} color={colors.onBrand} />
            </View>
            {Platform.OS === "web" && events.length > 1 ? (
              <View style={styles.webNavigation}>
                <Pressable accessibilityLabel="Önceki etkinlikler" style={styles.webNavigationButton} onPress={() => scrollRadar(-1)}>
                  <Ionicons name="chevron-back" size={20} color={colors.onBrand} />
                </Pressable>
                <Pressable accessibilityLabel="Sonraki etkinlikler" style={styles.webNavigationButton} onPress={() => scrollRadar(1)}>
                  <Ionicons name="chevron-forward" size={20} color={colors.onBrand} />
                </Pressable>
              </View>
            ) : null}
          </View>
        </View>

        {error ? <Text style={styles.error}>{error}</Text> : null}
        {loading ? (
          <View style={styles.loadingRow}>
            <ActivityIndicator color={colors.accent} />
            <Text style={styles.muted}>Şehir taranıyor...</Text>
          </View>
        ) : events.length === 0 ? (
          <View style={styles.emptyRadar}>
            <View style={styles.emptyOrbit}>
              <Ionicons name="location" size={28} color={colors.action} />
            </View>
            <Text style={styles.emptyTitle}>Radar hazır, ilk içerik bekleniyor.</Text>
            <Text style={styles.muted}>Resmî etkinlik kaynakları tarandığında burada yeni şehir planları oluşacak.</Text>
            <View style={styles.previewChips}>
              {['Konser', 'Sergi', 'Atölye'].map((label) => <Text key={label} style={styles.previewChip}>{label}</Text>)}
            </View>
          </View>
        ) : (
          <ScrollView
            ref={radarScrollRef}
            horizontal
            showsHorizontalScrollIndicator={Platform.OS === "web"}
            contentContainerStyle={styles.radarRow}
            onScroll={(event) => {
              radarScrollOffset.current = event.nativeEvent.contentOffset.x;
            }}
            scrollEventThrottle={16}
          >
            {events.slice(0, 4).map((event) => (
              <View key={event.event_id} style={styles.radarCard}>
                <Link href={{ pathname: "/city-event/[id]", params: { id: event.event_id } }} asChild>
                  <Pressable>
                    <View style={[styles.cover, { backgroundColor: categoryColor(event.category) }]}>
                      <View style={styles.coverFallback}><Ionicons name="images" size={34} color="rgba(255,255,255,0.82)" /></View>
                      {event.cover_image_url && !failedImages.has(event.event_id) ? (
                        <Image
                          source={{ uri: normalizeImageUrl(event.cover_image_url) }}
                          style={styles.coverImage}
                          resizeMode="cover"
                          fadeDuration={180}
                          onError={() => setFailedImages((current) => new Set(current).add(event.event_id))}
                        />
                      ) : null}
                      <View style={styles.coverShade} />
                      <View style={styles.categoryBadge}><Text style={styles.categoryText}>{event.category}</Text></View>
                      <View style={styles.dateBadge}>
                        <Text style={styles.dateDay}>{new Date(event.starts_at).toLocaleDateString('tr-TR', { day: '2-digit' })}</Text>
                        <Text style={styles.dateMonth}>{new Date(event.starts_at).toLocaleDateString('tr-TR', { month: 'short' }).toUpperCase()}</Text>
                      </View>
                    </View>
                    <View style={styles.cardBody}>
                      <Text style={styles.cardTitle} numberOfLines={2}>{event.title}</Text>
                      <Text style={styles.cardMeta} numberOfLines={1}>{event.venue_name || event.city} · {formatTime(event.starts_at)}</Text>
                      <View style={styles.sourceRow}>
                        <Ionicons name="shield-checkmark" size={14} color={colors.success} />
                        <Text style={styles.sourceText} numberOfLines={1}>Kaynak: {event.source_name}</Text>
                      </View>
                    </View>
                  </Pressable>
                </Link>
                <View style={styles.cardActions}>
                  <View>
                    <Text style={styles.price}>{event.price_label || "Fiyat belirtilmedi"}</Text>
                    <Text style={styles.companionCount}>{event.companion_count} kişi birlikte gitmek istiyor</Text>
                  </View>
                  <Pressable
                    onPress={() => void markCompanionIntent(event)}
                    disabled={workingId === event.event_id}
                    style={[styles.companionButton, event.is_looking_for_company && styles.companionButtonActive]}
                  >
                    <Ionicons name={event.is_looking_for_company ? "people" : "person-add"} size={18} color={event.is_looking_for_company ? colors.onBrand : colors.brandInk} />
                  </Pressable>
                </View>
              </View>
            ))}
          </ScrollView>
        )}
        <Link href={"/city-radar" as never} asChild>
          <Pressable style={styles.openRadarButton}>
            <Text style={styles.openRadarText}>Şehir Radarı'nı tam sayfa aç</Text>
            <Ionicons name="arrow-forward" size={19} color={colors.ink} />
          </Pressable>
        </Link>
      </View>

      {children}

      <View style={styles.companionSection}>
        <View style={styles.headingRow}>
          <View style={styles.headingCopy}>
            <Text style={styles.companionKicker}>BİRLİKTE GİT</Text>
            <Text style={styles.companionHeadingTitle}>Plan var, ekip burada.</Text>
            <Text style={styles.companionSubtitle}>Aynı etkinliğe gitmek isteyen üyeleri sayıyla gör; kişisel bilgiler etkinlikten önce açılmaz.</Text>
          </View>
          <View style={styles.companionMark}><Ionicons name="people" size={24} color={colors.ink} /></View>
        </View>
        {companionEvents.length === 0 ? (
          <View style={styles.companionEmpty}>
            <Text style={styles.emptyTitle}>İlk buluşma işaretini sen yak.</Text>
            <Text style={styles.muted}>Şehir Radarı kartındaki kişi simgesine dokunarak “birlikte gitmek istiyorum” diyebilirsin.</Text>
          </View>
        ) : (
          <View style={styles.companionStack}>
            {companionEvents.map((event) => (
              <Link key={event.event_id} href={{ pathname: "/city-event/[id]", params: { id: event.event_id } }} asChild>
                <Pressable style={styles.companionCard}>
                  <View style={[styles.miniCover, { backgroundColor: categoryColor(event.category) }]}>
                    <Ionicons name="sparkles" size={22} color={colors.onBrand} />
                    {event.cover_image_url && !failedImages.has(event.event_id) ? (
                      <Image
                        source={{ uri: normalizeImageUrl(event.cover_image_url) }}
                        style={styles.coverImageAbsolute}
                        resizeMode="cover"
                        onError={() => setFailedImages((current) => new Set(current).add(event.event_id))}
                      />
                    ) : null}
                  </View>
                  <View style={styles.companionCopy}>
                    <Text style={styles.companionTitle} numberOfLines={1}>{event.title}</Text>
                    <Text style={styles.cardMeta}>{event.city} · {event.companion_count} eşlikçi arıyor</Text>
                  </View>
                  <Ionicons name="arrow-forward" size={20} color={colors.accent} />
                </Pressable>
              </Link>
            ))}
          </View>
        )}
      </View>
    </>
  );
}

function formatTime(value: string) {
  return new Date(value).toLocaleString("tr-TR", { day: "2-digit", month: "short", hour: "2-digit", minute: "2-digit" });
}

function categoryColor(category: string) {
  const normalized = category.toLocaleLowerCase("tr-TR");
  if (normalized.includes("konser") || normalized.includes("sahne")) return "#7b35ff";
  if (normalized.includes("yemek") || normalized.includes("lezzet") || normalized.includes("gastronomi")) return "#ff8a14";
  if (normalized.includes("doğa") || normalized.includes("spor")) return "#08a88a";
  return "#087cbd";
}

const styles = StyleSheet.create({
  radarSection: { gap: 13, paddingVertical: 16, borderRadius: 19, backgroundColor: colors.brandInk, borderWidth: 1, borderColor: "#23365a", overflow: "hidden" },
  companionSection: { gap: 12, padding: 15, borderRadius: 19, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  headingRow: { flexDirection: "row", alignItems: "flex-start", justifyContent: "space-between", gap: 12, paddingHorizontal: 20 },
  headingCopy: { flex: 1, gap: 5 },
  kicker: { color: colors.action, fontSize: 10, fontWeight: "900", letterSpacing: 1.5 },
  title: { color: colors.onBrand, fontSize: 20, lineHeight: 25, fontWeight: "900" },
  subtitle: { color: colors.onBrandMuted, fontSize: 13, lineHeight: 19 },
  companionKicker: { color: colors.action, fontSize: 10, fontWeight: "900", letterSpacing: 1.5 },
  companionHeadingTitle: { color: colors.ink, fontSize: 20, lineHeight: 25, fontWeight: "900" },
  companionSubtitle: { color: colors.muted, fontSize: 13, lineHeight: 19 },
  radarMark: { width: 48, height: 48, borderRadius: 18, alignItems: "center", justifyContent: "center", backgroundColor: colors.accent },
  radarActions: { alignItems: "flex-end", gap: 9 },
  webNavigation: { flexDirection: "row", gap: 7 },
  webNavigationButton: { width: 38, height: 38, alignItems: "center", justifyContent: "center", borderRadius: 13, backgroundColor: colors.accent },
  companionMark: { width: 48, height: 48, borderRadius: 18, alignItems: "center", justifyContent: "center", backgroundColor: colors.warning },
  radarRow: { gap: 13, paddingHorizontal: 16 },
  radarCard: { width: 272, overflow: "hidden", borderRadius: 18, backgroundColor: colors.surface },
  cover: { height: 162, overflow: "hidden" },
  coverFallback: { ...StyleSheet.absoluteFillObject, alignItems: "center", justifyContent: "center" },
  coverImage: { width: "100%", height: "100%" },
  coverImageAbsolute: { ...StyleSheet.absoluteFillObject, width: "100%", height: "100%" },
  coverShade: { ...StyleSheet.absoluteFillObject, backgroundColor: "rgba(8,26,68,0.18)" },
  categoryBadge: { position: "absolute", top: 12, left: 12, borderRadius: 999, paddingHorizontal: 10, paddingVertical: 6, backgroundColor: colors.action },
  categoryText: { color: colors.brandInk, fontSize: 10, fontWeight: "900", textTransform: "uppercase" },
  dateBadge: { position: "absolute", top: 0, right: 13, minWidth: 56, alignItems: "center", paddingVertical: 9, borderBottomLeftRadius: 16, borderBottomRightRadius: 16, backgroundColor: colors.surface },
  dateDay: { color: colors.ink, fontSize: 20, fontWeight: "900" },
  dateMonth: { color: colors.accent, fontSize: 10, fontWeight: "900" },
  cardBody: { gap: 6, padding: 15 },
  cardTitle: { color: colors.ink, fontSize: 19, lineHeight: 24, fontWeight: "900" },
  cardMeta: { color: colors.muted, fontSize: 12, fontWeight: "700" },
  sourceRow: { flexDirection: "row", alignItems: "center", gap: 5 },
  sourceText: { flex: 1, color: colors.success, fontSize: 11, fontWeight: "800" },
  cardActions: { minHeight: 62, flexDirection: "row", alignItems: "center", justifyContent: "space-between", gap: 8, paddingHorizontal: 15, paddingBottom: 14 },
  price: { color: colors.ink, fontSize: 12, fontWeight: "900" },
  companionCount: { marginTop: 3, color: colors.muted, fontSize: 10, fontWeight: "700" },
  companionButton: { width: 42, height: 42, borderRadius: 15, alignItems: "center", justifyContent: "center", backgroundColor: colors.warning },
  companionButtonActive: { backgroundColor: colors.accent },
  openRadarButton: { minHeight: 44, marginHorizontal: 16, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 8, borderRadius: 14, backgroundColor: colors.action },
  openRadarText: { color: colors.brandInk, fontSize: 13, fontWeight: "900" },
  loadingRow: { flexDirection: "row", alignItems: "center", gap: 10, paddingHorizontal: 20 },
  error: { color: "#ffd3df", paddingHorizontal: 20, fontSize: 12, fontWeight: "700" },
  muted: { color: colors.muted, fontSize: 13, lineHeight: 19 },
  emptyRadar: { marginHorizontal: 16, gap: 9, padding: 18, borderRadius: 22, backgroundColor: colors.surface },
  emptyOrbit: { width: 52, height: 52, borderRadius: 26, alignItems: "center", justifyContent: "center", backgroundColor: colors.accentSoft },
  emptyTitle: { color: colors.ink, fontSize: 17, fontWeight: "900" },
  previewChips: { flexDirection: "row", flexWrap: "wrap", gap: 7, marginTop: 3 },
  previewChip: { overflow: "hidden", borderRadius: 999, paddingHorizontal: 10, paddingVertical: 6, color: colors.accent, backgroundColor: colors.accentSoft, fontSize: 10, fontWeight: "900" },
  companionEmpty: { gap: 6, borderRadius: 20, padding: 16, backgroundColor: colors.surfaceStrong },
  companionStack: { gap: 9 },
  companionCard: { flexDirection: "row", alignItems: "center", gap: 11, padding: 10, borderRadius: 19, backgroundColor: colors.surfaceStrong },
  miniCover: { width: 52, height: 52, borderRadius: 16, overflow: "hidden", alignItems: "center", justifyContent: "center" },
  companionCopy: { flex: 1, gap: 4 },
  companionTitle: { color: colors.ink, fontSize: 14, fontWeight: "900" }
});
