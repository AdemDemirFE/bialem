import { Ionicons } from "@expo/vector-icons";
import { Link, Stack } from "expo-router";
import { useEffect, useMemo, useState } from "react";
import { ActivityIndicator, Image, Pressable, RefreshControl, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import type { CityRadarEvent } from "../src/components/CityDiscovery";
import { Reveal, Skeleton } from "../src/animations";
import { FeedbackState } from "../src/components/ui/FeedbackState";
import { useAuth } from "../src/lib/auth";
import { api } from "../src/lib/api";
import { normalizeImageUrl } from "../src/lib/media-url";
import { colors } from "../src/theme/colors";

type RadarCategory = "Tümü" | "Konser" | "Tiyatro" | "Stand-up" | "Sergi" | "Atölye" | "Spor" | "Diğer";

const categories: RadarCategory[] = ["Tümü", "Konser", "Tiyatro", "Stand-up", "Sergi", "Atölye", "Spor", "Diğer"];

export default function CityRadarScreen() {
  const { profile } = useAuth();
  const city = profile?.city?.trim() || "Ankara";
  const [events, setEvents] = useState<CityRadarEvent[]>([]);
  const [category, setCategory] = useState<RadarCategory>("Tümü");
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [failedImages, setFailedImages] = useState<Set<string>>(new Set());

  const load = async (refresh = false) => {
    refresh ? setRefreshing(true) : setLoading(true);
    setError(null);
    const result = await api.rpc("get_city_radar", { target_city: city });
    if (result.error) setError("Şehir Radarı şu anda güncellenemedi. Lütfen tekrar deneyin.");
    else setEvents((result.data ?? []) as CityRadarEvent[]);
    refresh ? setRefreshing(false) : setLoading(false);
  };

  useEffect(() => {
    void load();
  }, [city]);

  const visibleEvents = useMemo(() => {
    const normalizedQuery = query.trim().toLocaleLowerCase("tr-TR");
    return events.filter((event) => {
      const matchesCategory = category === "Tümü" || normalizeCategory(event.category) === category;
      const searchable = [event.title, event.venue_name, event.category, event.description].filter(Boolean).join(" ").toLocaleLowerCase("tr-TR");
      return matchesCategory && (!normalizedQuery || searchable.includes(normalizedQuery));
    });
  }, [category, events, query]);

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: "Şehir Radarı" }} />
      <ScrollView
        style={styles.screen}
        contentContainerStyle={styles.page}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void load(true)} tintColor={colors.accent} />}
      >
        <Reveal>
        <View style={styles.hero}>
          <View style={styles.radarIcon}><Ionicons name="radio" size={28} color="#fff" /></View>
          <Text style={styles.kicker}>ŞEHRİ KAÇIRMA</Text>
          <Text style={styles.heroTitle}>{city}'da bugün ne var?</Text>
          <Text style={styles.heroText}>Konserden tiyatroya, stand-up'tan atölyeye kadar doğrulanmış kaynaklardan gelen şehir planlarını tek ekranda keşfet.</Text>
          <View style={styles.searchBox}>
            <Ionicons name="search" size={19} color={colors.muted} />
            <TextInput
              value={query}
              onChangeText={setQuery}
              placeholder="Etkinlik veya mekân ara"
              placeholderTextColor={colors.muted}
              style={styles.searchInput}
            />
          </View>
        </View>
        </Reveal>

        <Reveal index={1}>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.filters}>
          {categories.map((item) => {
            const selected = item === category;
            return (
              <Pressable
                key={item}
                onPress={() => setCategory(item)}
                style={({ pressed }) => [styles.filter, selected && styles.filterActive, pressed && { opacity: 0.92 }]}
              >
                <Text style={[styles.filterText, selected && styles.filterTextActive]}>{item}</Text>
              </Pressable>
            );
          })}
        </ScrollView>
        </Reveal>

        <Reveal index={2}>
        <View style={styles.resultHeader}>
          <View>
            <Text style={styles.resultKicker}>{category.toLocaleUpperCase("tr-TR")}</Text>
            <Text style={styles.resultTitle}>Radar sonuçları</Text>
          </View>
          <Text style={styles.resultCount}>{visibleEvents.length} etkinlik</Text>
        </View>
        </Reveal>

        {loading ? (
          <View style={{ gap: 12 }}>
            <Skeleton height={280} borderRadius={19} />
            <Skeleton height={280} borderRadius={19} />
          </View>
        ) : null}
        {error ? (
          <FeedbackState
            kind="error"
            title="Radar güncellenemedi"
            message={error}
            onRetry={() => void load()}
          />
        ) : null}
        {!loading && !error && visibleEvents.length === 0 ? (
          <View style={styles.empty}>
            <Ionicons name="calendar-outline" size={38} color={colors.accent} />
            <Text style={styles.emptyTitle}>Bu filtrede etkinlik bulunamadı.</Text>
            <Text style={styles.emptyText}>Başka bir kategori seçebilir veya arama kelimesini değiştirebilirsin.</Text>
          </View>
        ) : null}

        <View style={styles.list}>
          {visibleEvents.map((event, i) => {
            const normalizedCategory = normalizeCategory(event.category);
            return (
              <Reveal key={event.event_id} index={Math.min(i, 6)}>
              <Link href={{ pathname: "/city-event/[id]", params: { id: event.event_id } }} asChild>
                <Pressable
                  style={({ pressed }) => [styles.card, pressed && { opacity: 0.95, transform: [{ scale: 0.99 }] }]}
                >
                  <View style={[styles.cover, { backgroundColor: categoryColor(normalizedCategory) }]}>
                    <View style={styles.fallback}>
                      <Ionicons name={categoryIcon(normalizedCategory)} size={44} color="rgba(255,255,255,0.86)" />
                    </View>
                    {event.cover_image_url && !failedImages.has(event.event_id) ? (
                      <Image
                        source={{ uri: normalizeImageUrl(event.cover_image_url) }}
                        style={styles.coverImage}
                        resizeMode="cover"
                        onError={() => setFailedImages((current) => new Set(current).add(event.event_id))}
                      />
                    ) : null}
                    <View style={styles.categoryBadge}><Text style={styles.categoryText}>{normalizedCategory}</Text></View>
                    <View style={styles.dateBadge}>
                      <Text style={styles.dateDay}>{new Date(event.starts_at).toLocaleDateString("tr-TR", { day: "2-digit" })}</Text>
                      <Text style={styles.dateMonth}>{new Date(event.starts_at).toLocaleDateString("tr-TR", { month: "short" }).toLocaleUpperCase("tr-TR")}</Text>
                    </View>
                  </View>
                  <View style={styles.cardBody}>
                    <Text style={styles.cardTitle}>{event.title}</Text>
                    <Text style={styles.cardMeta}>{event.venue_name || event.city} · {formatDate(event.starts_at)}</Text>
                    <View style={styles.sourceRow}>
                      <Ionicons name="shield-checkmark" size={15} color={colors.success} />
                      <Text style={styles.source}>Kaynak: {event.source_name}</Text>
                    </View>
                    <View style={styles.cardFooter}>
                      <View>
                        <Text style={styles.price}>{event.price_label || "Fiyat belirtilmedi"}</Text>
                        <Text style={styles.company}>{event.companion_count} kişi birlikte gitmek istiyor</Text>
                      </View>
                      <View style={styles.detailButton}><Ionicons name="arrow-forward" size={20} color={colors.ink} /></View>
                    </View>
                  </View>
                </Pressable>
              </Link>
              </Reveal>
            );
          })}
        </View>
      </ScrollView>
    </>
  );
}

function normalizeCategory(value: string): RadarCategory {
  const normalized = value.toLocaleLowerCase("tr-TR");
  if (/(konser|müzik|music|rock|pop|alternative|jazz)/.test(normalized)) return "Konser";
  if (/(tiyatro|theatre|theater|sahne|play)/.test(normalized)) return "Tiyatro";
  if (/(stand.?up|komedi|comedy)/.test(normalized)) return "Stand-up";
  if (/(sergi|exhibition|gallery|müze|museum|sanat|art)/.test(normalized)) return "Sergi";
  if (/(atölye|workshop|eğitim|seminar)/.test(normalized)) return "Atölye";
  if (/(spor|sport|koşu|fitness|maç)/.test(normalized)) return "Spor";
  return "Diğer";
}

function categoryColor(category: RadarCategory) {
  if (category === "Konser") return "#6f32ff";
  if (category === "Tiyatro") return "#db416f";
  if (category === "Stand-up") return "#f07b21";
  if (category === "Sergi") return "#087cbd";
  if (category === "Atölye") return "#0b9b80";
  if (category === "Spor") return "#1670c5";
  return "#50607f";
}

function categoryIcon(category: RadarCategory): keyof typeof Ionicons.glyphMap {
  if (category === "Konser") return "musical-notes";
  if (category === "Tiyatro") return "ticket";
  if (category === "Stand-up") return "mic";
  if (category === "Sergi") return "color-palette";
  if (category === "Atölye") return "construct";
  if (category === "Spor") return "football";
  return "sparkles";
}

function formatDate(value: string) {
  return new Date(value).toLocaleString("tr-TR", { weekday: "short", day: "2-digit", month: "short", hour: "2-digit", minute: "2-digit" });
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  page: { gap: 14, padding: 16, paddingBottom: 36 },
  hero: { overflow: "hidden", gap: 8, padding: 18, borderRadius: 21, backgroundColor: colors.brandInk },
  radarIcon: { width: 52, height: 52, alignItems: "center", justifyContent: "center", borderRadius: 18, backgroundColor: colors.accent },
  kicker: { color: colors.action, fontSize: 10, fontWeight: "900", letterSpacing: 1.5 },
  heroTitle: { maxWidth: 310, color: "#fff", fontSize: 34, lineHeight: 39, fontWeight: "900", letterSpacing: -1 },
  heroText: { color: "#cbd5ef", fontSize: 14, lineHeight: 21 },
  searchBox: { minHeight: 50, marginTop: 8, flexDirection: "row", alignItems: "center", gap: 9, paddingHorizontal: 14, borderRadius: 17, backgroundColor: colors.surface },
  searchInput: { flex: 1, color: colors.ink, fontSize: 14 },
  filters: { gap: 8 },
  filter: { paddingHorizontal: 15, paddingVertical: 11, borderRadius: 999, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  filterActive: { borderColor: colors.brandInk, backgroundColor: colors.brandInk },
  filterText: { color: colors.ink, fontSize: 12, fontWeight: "800" },
  filterTextActive: { color: "#fff" },
  resultHeader: { flexDirection: "row", alignItems: "flex-end", justifyContent: "space-between", gap: 12 },
  resultKicker: { color: colors.accent, fontSize: 10, fontWeight: "900", letterSpacing: 1.3 },
  resultTitle: { color: colors.ink, fontSize: 25, fontWeight: "900" },
  resultCount: { color: colors.muted, fontSize: 12, fontWeight: "800" },
  list: { gap: 12 },
  card: { overflow: "hidden", borderRadius: 19, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  cover: { height: 190, overflow: "hidden" },
  fallback: { ...StyleSheet.absoluteFillObject, alignItems: "center", justifyContent: "center" },
  coverImage: { width: "100%", height: "100%" },
  categoryBadge: { position: "absolute", top: 14, left: 14, paddingHorizontal: 11, paddingVertical: 7, borderRadius: 999, backgroundColor: colors.action },
  categoryText: { color: colors.ink, fontSize: 10, fontWeight: "900" },
  dateBadge: { position: "absolute", top: 0, right: 15, minWidth: 60, alignItems: "center", paddingVertical: 10, borderBottomLeftRadius: 18, borderBottomRightRadius: 18, backgroundColor: colors.surface },
  dateDay: { color: colors.ink, fontSize: 22, fontWeight: "900" },
  dateMonth: { color: colors.accent, fontSize: 10, fontWeight: "900" },
  cardBody: { gap: 8, padding: 17 },
  cardTitle: { color: colors.ink, fontSize: 21, lineHeight: 26, fontWeight: "900" },
  cardMeta: { color: colors.muted, fontSize: 12, fontWeight: "700" },
  sourceRow: { flexDirection: "row", alignItems: "center", gap: 5 },
  source: { color: colors.success, fontSize: 11, fontWeight: "800" },
  cardFooter: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", gap: 10, paddingTop: 5 },
  price: { color: colors.ink, fontSize: 13, fontWeight: "900" },
  company: { marginTop: 3, color: colors.muted, fontSize: 10, fontWeight: "700" },
  detailButton: { width: 44, height: 44, alignItems: "center", justifyContent: "center", borderRadius: 15, backgroundColor: colors.action },
  error: { padding: 14, borderRadius: 17, color: colors.danger, backgroundColor: "#ffe8ef", fontWeight: "800" },
  empty: { alignItems: "center", gap: 8, padding: 30, borderRadius: 27, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  emptyTitle: { color: colors.ink, textAlign: "center", fontSize: 18, fontWeight: "900" },
  emptyText: { color: colors.muted, textAlign: "center", fontSize: 13, lineHeight: 20 }
});
