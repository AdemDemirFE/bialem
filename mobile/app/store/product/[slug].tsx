import { Ionicons } from "@expo/vector-icons";
import { useLocalSearchParams, useRouter, Stack } from "expo-router";
import { useCallback, useEffect, useState } from "react";
import {
  Dimensions,
  FlatList,
  Image,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Reveal, Skeleton } from "../../../src/animations";
import { FeedbackState } from "../../../src/components/ui/FeedbackState";
import { showAppAlert, showAppError } from "../../../src/components/AppAlert";
import { notifyCartUpdated } from "../../../src/lib/cart-events";
import { api } from "../../../src/lib/api";
import { storeApi, type StoreProduct, type StoreProductImage, type StoreReview, type StoreReviewSummary } from "../../../src/lib/store-api";
import { colors } from "../../../src/theme/colors";

const { width } = Dimensions.get("window");

export default function ProductDetailScreen() {
  const { slug } = useLocalSearchParams<{ slug: string }>();
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const [product, setProduct] = useState<StoreProduct | null>(null);
  const [loading, setLoading] = useState(true);
  const [selectedVariant, setSelectedVariant] = useState<number | undefined>();
  const [quantity, setQuantity] = useState(1);
  const [reviews, setReviews] = useState<StoreReview[]>([]);
  const [summary, setSummary] = useState<StoreReviewSummary | null>(null);
  const [wishlisted, setWishlisted] = useState(false);
  const [loadError, setLoadError] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setLoadError(false);
    try {
      const p = await storeApi.productBySlug(slug);
      setProduct(p);
      const [rev, summaryResult, wishlistedResult] = await Promise.all([storeApi.reviews(p.id), storeApi.reviewSummary(p.id), storeApi.isWishlisted(p.id)]);
      setReviews(rev.content);
      setSummary(summaryResult);
      setWishlisted(Boolean(wishlistedResult));
    } catch (e) {
      setLoadError(true);
      showAppError(e instanceof Error ? e.message : "Ürün yüklenemedi");
    } finally {
      setLoading(false);
    }
  }, [slug]);

  useEffect(() => {
    void load();
  }, [load]);

  const addToCart = async () => {
    if (!product) return;
    try {
      await storeApi.addToCart({ productId: product.id, variantId: selectedVariant, quantity });
      notifyCartUpdated();
      showAppAlert({ title: "Sepete eklendi", icon: "success" });
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Sepete eklenemedi");
    }
  };

  const toggleWishlist = async () => {
    if (!product) return;
    try {
      if (wishlisted) await storeApi.removeWishlist(product.id);
      else await storeApi.addWishlist(product.id);
      setWishlisted(!wishlisted);
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "İşlem başarısız");
    }
  };

  const shareProduct = async () => {
    if (!product) return;
    const url = `${window.location.origin}/store/product/${product.slug}`;
    if (navigator.share) {
      await navigator.share({ title: product.name, url });
    } else {
      await navigator.clipboard.writeText(url);
      showAppAlert({ title: "Link kopyalandı", icon: "success" });
    }
  };

  if (loading) {
    return (
      <View style={s.screen}>
        <Stack.Screen options={{ title: "Ürün" }} />
        <View style={{ width, height: width, backgroundColor: colors.accentSoft }} />
        <View style={{ padding: 16, gap: 12 }}>
          <Skeleton height={26} width="70%" />
          <Skeleton height={16} width="40%" />
          <Skeleton height={60} width="100%" />
        </View>
      </View>
    );
  }

  if (loadError || !product) {
    return (
      <View style={s.screen}>
        <Stack.Screen options={{ title: "Ürün" }} />
        <View style={{ padding: 16, marginTop: 60 }}>
          <FeedbackState
            kind="error"
            title="Ürün yüklenemedi"
            message="Bağlantını kontrol edip tekrar dene."
            onRetry={() => void load()}
          />
        </View>
      </View>
    );
  }

  const hasDiscount = product.discountedPrice && product.discountedPrice > 0 && product.discountedPrice < product.price;
  const discountPercent = hasDiscount ? Math.round(((product.price - product.discountedPrice!) / product.price) * 100) : 0;
  const summarySource = summary && summary.count > 0
    ? summary
    : product.ratingAverage && product.reviewCount
      ? { average: product.ratingAverage, count: product.reviewCount }
      : null;

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ title: product.name, headerShown: true }} />
      <ScrollView contentContainerStyle={{ paddingBottom: 100 + insets.bottom }}>
        <FlatList
          horizontal
          pagingEnabled
          data={product.images as StoreProductImage[]}
          keyExtractor={(img: StoreProductImage) => String(img.id)}
          showsHorizontalScrollIndicator={false}
          renderItem={(info: any) => {
            const img = info.item as StoreProductImage;
            return <Image source={{ uri: img.imageUrl }} style={{ width, height: width }} resizeMode="cover" />;
          }}
        />

        <View style={s.body}>
          <Reveal index={0}>
          <View style={s.headerRow}>
            <View style={{ flex: 1 }}>
              <Text style={s.name}>{product.name}</Text>
              {product.ratingAverage ? (
                <View style={s.rating}>
                  <Ionicons name="star" size={14} color="#f5a623" />
                  <Text style={s.ratingText}>{product.ratingAverage.toFixed(1)} ({product.reviewCount || 0} değerlendirme)</Text>
                </View>
              ) : null}
            </View>
            <View style={{ flexDirection: "row", gap: 8 }}>
              <Pressable onPress={toggleWishlist} style={s.iconBtn}>
                <Ionicons name={wishlisted ? "heart" : "heart-outline"} size={24} color={wishlisted ? colors.danger : colors.ink} />
              </Pressable>
              <Pressable onPress={shareProduct} style={s.iconBtn}>
                <Ionicons name="share-outline" size={24} color={colors.ink} />
              </Pressable>
            </View>
          </View>

          <View style={s.priceRow}>
            <Text style={s.price}>{formatPrice(hasDiscount ? product.discountedPrice! : product.price)}</Text>
            {hasDiscount ? <Text style={s.oldPrice}>{formatPrice(product.price)}</Text> : null}
            {hasDiscount ? <View style={s.badge}><Text style={s.badgeText}>%{discountPercent}</Text></View> : null}
          </View>
          </Reveal>

          {product.variants.length > 0 ? (
            <Reveal index={1}>
            <View style={s.section}>
              <Text style={s.sectionTitle}>Varyant</Text>
              <View style={s.variantRow}>
                {product.variants.map((v) => (
                  <Pressable
                    key={v.id}
                    onPress={() => setSelectedVariant(v.id)}
                    style={[s.variantChip, selectedVariant === v.id && s.variantChipActive]}
                  >
                    <Text style={[s.variantText, selectedVariant === v.id && s.variantTextActive]}>{v.variantName}</Text>
                  </Pressable>
                ))}
              </View>
            </View>
          </Reveal>
          ) : null}

          <Reveal index={2}>
          <View style={s.section}>
            <Text style={s.sectionTitle}>Adet</Text>
            <View style={s.qtyRow}>
              <Pressable onPress={() => setQuantity(Math.max(1, quantity - 1))} style={s.qtyBtn}><Text style={s.qtyBtnText}>-</Text></Pressable>
              <Text style={s.qtyText}>{quantity}</Text>
              <Pressable onPress={() => setQuantity(quantity + 1)} style={s.qtyBtn}><Text style={s.qtyBtnText}>+</Text></Pressable>
            </View>
          </View>
          </Reveal>

          <Reveal index={3}>
          <View style={s.section}>
            <Text style={s.sectionTitle}>Açıklama</Text>
            <Text style={s.desc}>{product.description || product.shortDescription || "Açıklama bulunmuyor."}</Text>
          </View>
          </Reveal>

          {product.attributes.length > 0 ? (
            <Reveal index={4}>
            <View style={s.section}>
              <Text style={s.sectionTitle}>Teknik Özellikler</Text>
              {product.attributes.map((attr) => (
                <View key={attr.id} style={s.attrRow}>
                  <Text style={s.attrKey}>{attr.attributeKey}</Text>
                  <Text style={s.attrValue}>{attr.attributeValue}</Text>
                </View>
              ))}
            </View>
          </Reveal>
          ) : null}

          <Reveal index={5}>
          <View style={s.section}>
            <Text style={s.sectionTitle}>Değerlendirmeler</Text>
            {summarySource ? (
              <View style={s.summary}>
                <Text style={s.summaryScore}>{summarySource.average.toFixed(1)}</Text>
                <Text style={s.summaryCount}>{summarySource.count} değerlendirme</Text>
              </View>
            ) : null}
            {reviews.length === 0 ? (
              <Text style={s.emptyReviews}>Henüz değerlendirme yok. İlk değerlendirmeyi yapan sen ol.</Text>
            ) : (
              reviews.slice(0, 3).map((r) => (
                <View key={r.id} style={s.reviewCard}>
                  <View style={s.reviewHeader}>
                    <Text style={s.reviewUser}>{r.userName}</Text>
                    <Text style={s.reviewRating}>{"★".repeat(r.rating)}{"☆".repeat(5 - r.rating)}</Text>
                  </View>
                  <Text style={s.reviewComment}>{r.comment}</Text>
                </View>
              ))
            )}
          </View>
          </Reveal>
        </View>
      </ScrollView>

      <View style={[s.footer, { paddingBottom: Math.max(insets.bottom, 12) }]}>
        <Pressable
          style={({ pressed }) => [s.cartBtn, pressed && { opacity: 0.9, transform: [{ scale: 0.98 }] }]}
          onPress={addToCart}
        >
          <Ionicons name="cart" size={20} color="#fff" />
          <Text style={s.cartBtnText}>Sepete Ekle</Text>
        </Pressable>
      </View>
    </View>
  );
}

function formatPrice(n: number) {
  return new Intl.NumberFormat("tr-TR", { style: "currency", currency: "TRY" }).format(n);
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  body: { padding: 16, gap: 18 },
  headerRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "flex-start" },
  name: { fontSize: 22, fontWeight: "900", color: colors.ink, flexShrink: 1 },
  rating: { flexDirection: "row", alignItems: "center", gap: 4, marginTop: 4 },
  ratingText: { fontSize: 13, color: colors.muted, fontWeight: "700" },
  iconBtn: { padding: 8, borderRadius: 12, backgroundColor: colors.accentSoft },
  priceRow: { flexDirection: "row", alignItems: "center", gap: 12, flexWrap: "wrap" },
  price: { fontSize: 24, fontWeight: "900", color: colors.action },
  oldPrice: { fontSize: 16, color: colors.muted, textDecorationLine: "line-through" },
  badge: { backgroundColor: colors.danger, paddingHorizontal: 8, paddingVertical: 3, borderRadius: 8 },
  badgeText: { color: "#fff", fontSize: 12, fontWeight: "900" },
  section: { gap: 8 },
  sectionTitle: { fontSize: 16, fontWeight: "900", color: colors.ink },
  emptyReviews: { color: colors.muted, fontSize: 13, lineHeight: 19, paddingVertical: 4 },
  desc: { fontSize: 14, color: colors.muted, lineHeight: 20 },
  variantRow: { flexDirection: "row", flexWrap: "wrap", gap: 8 },
  variantChip: { paddingHorizontal: 14, paddingVertical: 8, borderRadius: 10, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  variantChipActive: { borderColor: colors.action, backgroundColor: colors.accentSoft },
  variantText: { fontWeight: "700", color: colors.ink },
  variantTextActive: { color: colors.action },
  qtyRow: { flexDirection: "row", alignItems: "center", gap: 12 },
  qtyBtn: { width: 36, height: 36, borderRadius: 10, backgroundColor: colors.accentSoft, alignItems: "center", justifyContent: "center" },
  qtyBtnText: { fontSize: 20, fontWeight: "900", color: colors.ink },
  qtyText: { fontSize: 16, fontWeight: "900", color: colors.ink, minWidth: 24, textAlign: "center" },
  attrRow: { flexDirection: "row", justifyContent: "space-between", paddingVertical: 6, borderBottomWidth: 1, borderBottomColor: colors.border },
  attrKey: { fontSize: 13, color: colors.muted, fontWeight: "700" },
  attrValue: { fontSize: 13, color: colors.ink, fontWeight: "800" },
  summary: { flexDirection: "row", alignItems: "center", gap: 8 },
  summaryScore: { fontSize: 28, fontWeight: "900", color: colors.action },
  summaryCount: { fontSize: 13, color: colors.muted },
  reviewCard: { backgroundColor: colors.surface, borderRadius: 14, padding: 12, gap: 6, borderWidth: 1, borderColor: colors.border },
  reviewHeader: { flexDirection: "row", justifyContent: "space-between" },
  reviewUser: { fontSize: 13, fontWeight: "900", color: colors.ink },
  reviewRating: { fontSize: 12, color: "#f5a623" },
  reviewComment: { fontSize: 13, color: colors.muted },
  footer: {
    position: "absolute",
    bottom: 0,
    left: 0,
    right: 0,
    padding: 16,
    backgroundColor: colors.surface,
    borderTopWidth: 1,
    borderTopColor: colors.border,
  },
  cartBtn: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
    height: 52,
    borderRadius: 99,
    backgroundColor: colors.action,
  },
  cartBtnText: { color: "#fff", fontSize: 16, fontWeight: "900" },
});
