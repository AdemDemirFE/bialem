import { Ionicons } from "@expo/vector-icons";
import { Link, Stack } from "expo-router";
import { useEffect, useMemo, useState } from "react";
import { ActivityIndicator, Image, Pressable, RefreshControl, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { useAuth } from "../../src/lib/auth";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";

type Offer = {
  id: string;
  title: string;
  discount_percent: number;
  minimum_spend: number | null;
  valid_until: string | null;
};

type Venue = {
  id: string;
  name: string;
  description: string | null;
  category: string;
  logo_url: string | null;
  cover_image_url: string | null;
  address: string;
  city: string;
  is_featured: boolean;
  partner_offers: Offer[];
};

const categoryLabels: Record<string, string> = {
  cafe: "Kafe",
  restaurant: "Restoran",
  sports: "Spor",
  education: "Eğitim",
  entertainment: "Eğlence",
  beauty: "Güzellik",
  health: "Sağlık",
  shopping: "Alışveriş",
  other: "Diğer"
};

export default function AdvantagesScreen() {
  const { user, profile } = useAuth();
  const [venues, setVenues] = useState<Venue[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [canRedeem, setCanRedeem] = useState(false);
  const [category, setCategory] = useState("all");
  const [search, setSearch] = useState("");
  const [failedImages, setFailedImages] = useState<Set<string>>(new Set());

  const load = async (refresh = false) => {
    refresh ? setRefreshing(true) : setLoading(true);
    setError(null);

    const [venuesResult, staffResult] = await Promise.all([
      api
        .from("partner_venues")
        .select("id, name, description, category, logo_url, cover_image_url, address, city, is_featured, partner_offers!partner_offers_venue_id_fkey(id, title, discount_percent, minimum_spend, valid_until)")
        .eq("is_active", true)
        .order("is_featured", { ascending: false })
        .order("name"),
      user
        ? api.from("partner_venue_staff").select("id").eq("user_id", user.id).eq("is_active", true).limit(1)
        : Promise.resolve({ data: [], error: null })
    ]);

    if (venuesResult.error) setError("Avantajlar şu anda yüklenemedi. Lütfen tekrar deneyin.");
    else setVenues((venuesResult.data ?? []) as unknown as Venue[]);
    setCanRedeem(Boolean(staffResult.data?.length));
    refresh ? setRefreshing(false) : setLoading(false);
  };

  useEffect(() => {
    void load();
  }, [user?.id]);

  const categories = useMemo(
    () => Array.from(new Set(venues.map((venue) => venue.category))),
    [venues]
  );
  const visibleVenues = useMemo(() => {
    const query = search.trim().toLocaleLowerCase("tr-TR");
    return venues.filter((venue) => {
      if (category !== "all" && venue.category !== category) return false;
      if (!query) return true;
      return [venue.name, venue.address, venue.city, venue.description ?? "", categoryLabels[venue.category] ?? venue.category]
        .some((value) => value.toLocaleLowerCase("tr-TR").includes(query));
    });
  }, [category, search, venues]);

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: "Bialem Avantaj" }} />
      <ScrollView
        style={styles.screen}
        contentContainerStyle={styles.page}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void load(true)} tintColor={colors.accent} />}
      >
        <View style={styles.hero}>
          <View style={styles.heroBadge}>
            <Ionicons name="diamond" size={21} color={colors.ink} />
            <Text style={styles.heroBadgeText}>ANKARA PİLOT</Text>
          </View>
          <Text style={styles.heroTitle}>Şehirde ayrıcalıklı ol.</Text>
          <Text style={styles.heroText}>
            Bialem üyesi olduğunu göster, anlaşmalı mekânlarda sana özel indirimi anında kullan.
          </Text>
          <Text style={styles.hello}>Merhaba {profile?.display_name?.split(" ")[0] || "Bialemlı"}, avantajın hazır.</Text>
        </View>

        {canRedeem ? (
          <Link href={"/advantages/redeem" as never} asChild>
            <Pressable style={styles.staffButton}>
              <Ionicons name="scan" size={22} color="#fff" />
              <View style={styles.staffCopy}>
                <Text style={styles.staffTitle}>İşletme doğrulama ekranı</Text>
                <Text style={styles.staffText}>Müşteri QR kodunu okut</Text>
              </View>
              <Ionicons name="chevron-forward" size={21} color="#fff" />
            </Pressable>
          </Link>
        ) : null}

        <View style={styles.searchBox}>
          <Ionicons name="search" size={20} color={colors.muted} />
          <TextInput
            value={search}
            onChangeText={setSearch}
            placeholder="Mekân, semt veya kategori ara"
            placeholderTextColor={colors.muted}
            style={styles.searchInput}
            autoCapitalize="none"
            returnKeyType="search"
          />
          {search ? (
            <Pressable style={styles.clearButton} onPress={() => setSearch("")}>
              <Ionicons name="close" size={18} color={colors.ink} />
            </Pressable>
          ) : null}
        </View>

        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.filters}>
          <Pressable onPress={() => setCategory("all")} style={[styles.filter, category === "all" && styles.filterActive]}>
            <Text style={[styles.filterText, category === "all" && styles.filterTextActive]}>Tümü</Text>
          </Pressable>
          {categories.map((item) => (
            <Pressable key={item} onPress={() => setCategory(item)} style={[styles.filter, category === item && styles.filterActive]}>
              <Text style={[styles.filterText, category === item && styles.filterTextActive]}>{categoryLabels[item] ?? item}</Text>
            </Pressable>
          ))}
        </ScrollView>

        {!loading && !error ? (
          <View style={styles.resultsRow}>
            <Text style={styles.resultsTitle}>{visibleVenues.length} anlaşmalı mekân</Text>
            <Text style={styles.resultsHint}>Güncel kampanya koşulları mekân kartında yer alır.</Text>
          </View>
        ) : null}

        {loading ? <ActivityIndicator color={colors.accent} size="large" /> : null}
        {error ? <Text style={styles.error}>{error}</Text> : null}
        {!loading && !error && visibleVenues.length === 0 ? (
          <View style={styles.empty}>
            <Ionicons name="storefront-outline" size={36} color={colors.accent} />
            <Text style={styles.emptyTitle}>İlk avantajlar hazırlanıyor.</Text>
            <Text style={styles.emptyText}>Ankara’daki anlaşmalı mekânlar çok yakında burada olacak.</Text>
          </View>
        ) : null}

        <View style={styles.list}>
          {visibleVenues.map((venue) => {
            const offer = venue.partner_offers?.[0];
            return (
              <Link
                key={venue.id}
                href={{ pathname: "/advantages/[id]", params: { id: venue.id } } as never}
                asChild
              >
                <Pressable style={styles.card}>
                  {venue.cover_image_url && !failedImages.has(venue.id) ? (
                    <Image
                      source={{ uri: venue.cover_image_url }}
                      style={styles.cover}
                      onError={() => setFailedImages((current) => new Set(current).add(venue.id))}
                    />
                  ) : (
                    <View style={styles.coverFallback}>
                      <Ionicons name="storefront" size={42} color="#fff" />
                    </View>
                  )}
                  <View style={styles.cardBody}>
                    <View style={styles.cardTop}>
                      <View style={styles.identity}>
                        {venue.logo_url ? <Image source={{ uri: venue.logo_url }} style={styles.logo} /> : null}
                        <View style={styles.identityCopy}>
                          <Text style={styles.category}>{categoryLabels[venue.category] ?? venue.category}</Text>
                          <Text style={styles.name}>{venue.name}</Text>
                        </View>
                      </View>
                      {offer ? (
                        <View style={styles.discount}>
                          <Text style={styles.discountNumber}>%{Number(offer.discount_percent).toLocaleString("tr-TR")}</Text>
                          <Text style={styles.discountLabel}>İNDİRİM</Text>
                        </View>
                      ) : null}
                    </View>
                    <Text style={styles.offerTitle}>{offer?.title ?? "Yeni avantaj yakında"}</Text>
                    <View style={styles.location}>
                      <Ionicons name="location" size={15} color={colors.accent} />
                      <Text style={styles.locationText} numberOfLines={1}>{venue.address}</Text>
                    </View>
                    <View style={styles.openRow}>
                      <View style={styles.openCopy}>
                        <Text style={styles.openText}>{offer ? "QR avantajını aç" : "İşletmeyi incele"}</Text>
                        <Text style={styles.openHint}>
                          {offer ? "Üyelik kodunu işletmede oluştur" : "Aktif kampanya henüz bulunmuyor"}
                        </Text>
                      </View>
                      <Ionicons name="arrow-forward" size={18} color={colors.ink} />
                    </View>
                  </View>
                </Pressable>
              </Link>
            );
          })}
        </View>
      </ScrollView>
    </>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  page: { gap: 18, padding: 18, paddingBottom: 42 },
  hero: { overflow: "hidden", gap: 12, padding: 24, borderRadius: 32, backgroundColor: colors.brandInk },
  heroBadge: { alignSelf: "flex-start", flexDirection: "row", alignItems: "center", gap: 7, paddingHorizontal: 11, paddingVertical: 7, borderRadius: 999, backgroundColor: colors.action },
  heroBadgeText: { color: colors.ink, fontSize: 10, fontWeight: "900", letterSpacing: 1.2 },
  heroTitle: { maxWidth: 290, color: "#fff", fontSize: 35, lineHeight: 39, fontWeight: "900", letterSpacing: -1.2 },
  heroText: { color: "#cdd8f5", fontSize: 15, lineHeight: 23 },
  hello: { color: colors.action, fontSize: 12, fontWeight: "900" },
  staffButton: { flexDirection: "row", alignItems: "center", gap: 12, padding: 16, borderRadius: 22, backgroundColor: colors.accent },
  staffCopy: { flex: 1, gap: 2 },
  staffTitle: { color: "#fff", fontSize: 15, fontWeight: "900" },
  staffText: { color: "#e8dcff", fontSize: 12 },
  searchBox: { minHeight: 56, flexDirection: "row", alignItems: "center", gap: 10, paddingHorizontal: 16, borderRadius: 20, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  searchInput: { flex: 1, color: colors.ink, fontSize: 14, fontWeight: "700" },
  clearButton: { width: 32, height: 32, alignItems: "center", justifyContent: "center", borderRadius: 12, backgroundColor: colors.surfaceStrong },
  filters: { gap: 8 },
  filter: { paddingHorizontal: 14, paddingVertical: 10, borderRadius: 999, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  filterActive: { borderColor: colors.brandInk, backgroundColor: colors.brandInk },
  filterText: { color: colors.ink, fontSize: 12, fontWeight: "800" },
  filterTextActive: { color: "#fff" },
  resultsRow: { gap: 3 },
  resultsTitle: { color: colors.ink, fontSize: 17, fontWeight: "900" },
  resultsHint: { color: colors.muted, fontSize: 11, lineHeight: 16 },
  error: { padding: 15, borderRadius: 16, color: colors.danger, backgroundColor: "#ffe8ef", fontWeight: "800" },
  empty: { alignItems: "center", gap: 8, padding: 28, borderRadius: 26, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  emptyTitle: { color: colors.ink, fontSize: 18, fontWeight: "900" },
  emptyText: { color: colors.muted, textAlign: "center", lineHeight: 20 },
  list: { gap: 15 },
  card: { overflow: "hidden", borderRadius: 28, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  cover: { width: "100%", height: 170 },
  coverFallback: { height: 150, alignItems: "center", justifyContent: "center", backgroundColor: colors.accent },
  cardBody: { gap: 11, padding: 17 },
  cardTop: { flexDirection: "row", justifyContent: "space-between", alignItems: "flex-start", gap: 12 },
  identity: { flex: 1, flexDirection: "row", alignItems: "center", gap: 10 },
  logo: { width: 44, height: 44, borderRadius: 14, backgroundColor: colors.surfaceStrong },
  identityCopy: { flex: 1, gap: 2 },
  category: { color: colors.accent, fontSize: 9, fontWeight: "900", letterSpacing: 1.2 },
  name: { color: colors.ink, fontSize: 20, fontWeight: "900" },
  discount: { alignItems: "center", paddingHorizontal: 11, paddingVertical: 8, borderRadius: 15, backgroundColor: colors.action },
  discountNumber: { color: colors.ink, fontSize: 20, fontWeight: "900" },
  discountLabel: { color: colors.ink, fontSize: 7, fontWeight: "900", letterSpacing: 0.8 },
  offerTitle: { color: colors.ink, fontSize: 15, fontWeight: "800" },
  location: { flexDirection: "row", alignItems: "center", gap: 5 },
  locationText: { flex: 1, color: colors.muted, fontSize: 12 },
  openRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", paddingTop: 4 },
  openCopy: { flex: 1, gap: 2 },
  openText: { color: colors.ink, fontSize: 12, fontWeight: "900" },
  openHint: { color: colors.muted, fontSize: 10, lineHeight: 14 }
});
