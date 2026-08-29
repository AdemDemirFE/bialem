import { Ionicons } from "@expo/vector-icons";
import { Link, Stack, useRouter } from "expo-router";
import { useCallback, useEffect, useState } from "react";
import {
  ActivityIndicator,
  Dimensions,
  FlatList,
  Image,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { showAppError } from "../../src/components/AppAlert";
import { SkeletonList } from "../../src/components/SkeletonList";
import { CART_REFRESH_EVENT } from "../../src/lib/cart-events";
import { storeApi, type StoreCategory, type StoreProductListItem } from "../../src/lib/store-api";
import { colors } from "../../src/theme/colors";

const { width } = Dimensions.get("window");
const CARD_WIDTH = (width - 48) / 2;

export default function StoreScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [categories, setCategories] = useState<StoreCategory[]>([]);
  const [featured, setFeatured] = useState<StoreProductListItem[]>([]);
  const [newArrivals, setNewArrivals] = useState<StoreProductListItem[]>([]);
  const [bestSellers, setBestSellers] = useState<StoreProductListItem[]>([]);
  const [discounts, setDiscounts] = useState<StoreProductListItem[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [cartCount, setCartCount] = useState(0);

  const refreshCartCount = useCallback(async () => {
    try {
      const summary = await storeApi.cart();
      setCartCount(summary.items?.reduce((sum, item) => sum + (item.quantity ?? 1), 0) ?? 0);
    } catch {
      setCartCount(0);
    }
  }, []);

  useEffect(() => {
    void refreshCartCount();
    const interval = setInterval(() => void refreshCartCount(), 8000);
    const onUpdate = () => void refreshCartCount();
    if (typeof window !== "undefined") {
      window.addEventListener(CART_REFRESH_EVENT, onUpdate);
    }
    return () => {
      clearInterval(interval);
      if (typeof window !== "undefined") {
        window.removeEventListener(CART_REFRESH_EVENT, onUpdate);
      }
    };
  }, [refreshCartCount]);

  const load = useCallback(async () => {
    try {
      const [catRes, featRes, newRes, bestRes, discRes] = await Promise.all([
        storeApi.categories(),
        storeApi.featuredProducts(6),
        storeApi.newProducts(6),
        storeApi.bestSellers(6),
        storeApi.discountedProducts(6),
      ]);
      setCategories(catRes);
      setFeatured(featRes);
      setNewArrivals(newRes);
      setBestSellers(bestRes);
      setDiscounts(discRes);
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Mağaza yüklenemedi");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  const onSearch = () => {
    if (searchQuery.trim()) {
      router.push(`/store/search?query=${encodeURIComponent(searchQuery.trim())}` as never);
    }
  };

  if (loading) {
    return (
      <View style={s.screen}>
        <Stack.Screen options={{ title: "Mağaza", headerShown: true }} />
        <SkeletonList rows={6} />
      </View>
    );
  }

  return (
    <View style={s.screen}>
      <Stack.Screen
        options={{
          title: "Mağaza",
          headerShown: true,
          headerRight: () => (
            <View style={s.headerActions}>
              <Link href="/store/orders" asChild>
                <Pressable style={s.headerBtn}>
                  <Ionicons name="cube-outline" size={22} color={colors.ink} />
                </Pressable>
              </Link>
              <Link href="/store/cart" asChild>
                <Pressable style={s.headerBtn}>
                  <Ionicons name="cart-outline" size={24} color={colors.ink} />
                  {cartCount > 0 ? (
                    <View style={s.cartBadge}>
                      <Text style={s.cartBadgeText}>{cartCount > 99 ? "99+" : cartCount}</Text>
                    </View>
                  ) : null}
                </Pressable>
              </Link>
            </View>
          ),
        }}
      />
      <ScrollView
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); void load(); }} />}
        contentContainerStyle={[s.page, { paddingBottom: 24 + insets.bottom }]}
      >
        <View style={s.searchBox}>
          <Ionicons name="search" size={18} color={colors.muted} />
          <TextInput
            style={s.searchInput}
            placeholder="Ürün, kategori veya marka ara..."
            placeholderTextColor={colors.muted}
            value={searchQuery}
            onChangeText={setSearchQuery}
            onSubmitEditing={onSearch}
            returnKeyType="search"
          />
        </View>

        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={s.categoryRow}>
          {categories.map((c) => (
            <Link key={c.id} href={`/store/category/${c.slug}` as never} asChild>
              <Pressable style={s.categoryChip}>
                {c.imageUrl ? <Image source={{ uri: c.imageUrl }} style={s.categoryImg} /> : null}
                <Text style={s.categoryText}>{c.name}</Text>
              </Pressable>
            </Link>
          ))}
        </ScrollView>

        <Section title="Öne Çıkanlar" data={featured} />
        <Section title="Çok Satanlar" data={bestSellers} />
        <Section title="Yeni Gelenler" data={newArrivals} />
        <Section title="İndirimli Ürünler" data={discounts} />
      </ScrollView>
    </View>
  );
}

function Section({ title, data }: { title: string; data: StoreProductListItem[] }) {
  if (!data.length) return null;
  return (
    <View style={s.section}>
      <Text style={s.sectionTitle}>{title}</Text>
      <FlatList
        horizontal
        data={data}
        keyExtractor={(item: StoreProductListItem) => String(item.id)}
        showsHorizontalScrollIndicator={false}
        renderItem={(info: any) => {
          const item = info.item as StoreProductListItem;
          return <ProductCard item={item} />;
        }}
        contentContainerStyle={s.productRow}
      />
    </View>
  );
}

function ProductCard({ item }: { item: StoreProductListItem }) {
  const router = useRouter();
  const hasDiscount = item.discountedPrice && item.discountedPrice > 0 && item.discountedPrice < item.price;
  const discountPercent = hasDiscount ? Math.round(((item.price - item.discountedPrice!) / item.price) * 100) : 0;
  return (
    <Pressable style={s.card} onPress={() => router.push(`/store/product/${item.slug}` as never)}>
      <View style={s.imgWrap}>
        {item.primaryImageUrl ? (
          <Image source={{ uri: item.primaryImageUrl }} style={s.img} />
        ) : (
          <View style={s.imgPlaceholder}>
            <Ionicons name="image-outline" size={32} color={colors.muted} />
          </View>
        )}
        {hasDiscount ? <View style={s.badge}><Text style={s.badgeText}>%{discountPercent}</Text></View> : null}
      </View>
      <Text numberOfLines={2} style={s.cardTitle}>{item.name}</Text>
      <View style={s.priceRow}>
        <Text style={s.price}>{formatPrice(hasDiscount ? item.discountedPrice! : item.price)}</Text>
        {hasDiscount ? <Text style={s.oldPrice}>{formatPrice(item.price)}</Text> : null}
      </View>
      {item.ratingAverage ? (
        <View style={s.rating}>
          <Ionicons name="star" size={12} color="#f5a623" />
          <Text style={s.ratingText}>{item.ratingAverage.toFixed(1)} ({item.reviewCount || 0})</Text>
        </View>
      ) : null}
    </Pressable>
  );
}

function formatPrice(n: number) {
  return new Intl.NumberFormat("tr-TR", { style: "currency", currency: "TRY" }).format(n);
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  page: { padding: 16, gap: 18 },
  searchBox: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    paddingHorizontal: 14,
    height: 48,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface,
  },
  searchInput: { flex: 1, color: colors.ink, fontSize: 14 },
  headerActions: { flexDirection: "row", alignItems: "center", gap: 4 },
  headerBtn: { padding: 8 },
  cartBadge: { position: "absolute", top: 2, right: 2, minWidth: 16, height: 16, borderRadius: 8, backgroundColor: colors.danger, alignItems: "center", justifyContent: "center", paddingHorizontal: 3, borderWidth: 1.5, borderColor: colors.surface },
  cartBadgeText: { color: "#fff", fontSize: 9, fontWeight: "900" },
  categoryRow: { gap: 10, paddingRight: 16 },
  categoryChip: {
    width: 86,
    alignItems: "center",
    gap: 6,
  },
  categoryImg: { width: 64, height: 64, borderRadius: 32, backgroundColor: colors.accentSoft },
  categoryText: { fontSize: 11, fontWeight: "800", color: colors.ink, textAlign: "center" },
  section: { gap: 10 },
  sectionTitle: { fontSize: 18, fontWeight: "900", color: colors.ink },
  productRow: { gap: 12, paddingRight: 16 },
  card: { width: CARD_WIDTH, gap: 6 },
  imgWrap: { width: CARD_WIDTH, height: CARD_WIDTH, borderRadius: 16, overflow: "hidden", backgroundColor: colors.surface },
  img: { width: "100%", height: "100%" },
  imgPlaceholder: { flex: 1, alignItems: "center", justifyContent: "center" },
  badge: {
    position: "absolute",
    top: 8,
    left: 8,
    backgroundColor: colors.danger,
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 8,
  },
  badgeText: { color: "#fff", fontSize: 11, fontWeight: "900" },
  cardTitle: { fontSize: 13, fontWeight: "800", color: colors.ink, minHeight: 36 },
  priceRow: { flexDirection: "row", alignItems: "center", gap: 8 },
  price: { fontSize: 14, fontWeight: "900", color: colors.action },
  oldPrice: { fontSize: 12, color: colors.muted, textDecorationLine: "line-through" },
  rating: { flexDirection: "row", alignItems: "center", gap: 4 },
  ratingText: { fontSize: 11, color: colors.muted, fontWeight: "700" },
});
