import { Ionicons } from "@expo/vector-icons";
import { Stack, useLocalSearchParams, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, FlatList, Image, Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { Reveal } from "../../src/animations";
import { showAppError } from "../../src/components/AppAlert";
import { SkeletonList } from "../../src/components/SkeletonList";
import { storeApi, type StoreProductListItem } from "../../src/lib/store-api";
import { colors } from "../../src/theme/colors";

export default function StoreSearchScreen() {
  const { query: initialQuery = "" } = useLocalSearchParams<{ query?: string }>();
  const router = useRouter();
  const [query, setQuery] = useState(initialQuery);
  const [loading, setLoading] = useState(false);
  const [items, setItems] = useState<StoreProductListItem[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(false);

  const search = async (q: string, p = 0) => {
    if (!q.trim()) {
      setItems([]);
      setHasMore(false);
      return;
    }
    if (p === 0) setLoading(true);
    try {
      const res = await storeApi.searchProducts(q.trim(), p, 20);
      setItems((prev) => (p === 0 ? res.content : [...prev, ...res.content]));
      setHasMore(!res.last);
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Arama yapılamadı");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void search(initialQuery, 0);
  }, [initialQuery]);

  const onSubmit = () => {
    setPage(0);
    void search(query, 0);
  };

  const loadMore = () => {
    if (hasMore && !loading) {
      const next = page + 1;
      setPage(next);
      void search(query, next);
    }
  };

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ title: "Ürün Ara" }} />
      <Reveal>
      <View style={s.searchBox}>
        <Ionicons name="search" size={18} color={colors.muted} />
        <TextInput
          style={s.searchInput}
          placeholder="Ürün, kategori veya marka ara..."
          placeholderTextColor={colors.muted}
          value={query}
          onChangeText={setQuery}
          onSubmitEditing={onSubmit}
          returnKeyType="search"
          autoFocus
        />
        {query ? (
          <Pressable onPress={() => { setQuery(""); setItems([]); setHasMore(false); }}>
            <Ionicons name="close-circle" size={20} color={colors.muted} />
          </Pressable>
        ) : null}
      </View>
      </Reveal>
      {loading && items.length === 0 ? (
        <SkeletonList rows={8} />
      ) : (
        <FlatList
          data={items}
          keyExtractor={(item: StoreProductListItem) => String(item.id)}
          numColumns={2}
          columnWrapperStyle={{ justifyContent: "space-between", marginBottom: 12 }}
          contentContainerStyle={{ padding: 16 }}
          onEndReached={loadMore}
          onEndReachedThreshold={0.5}
          ListEmptyComponent={
            <View style={s.empty}>
              <Ionicons name="search-outline" size={48} color={colors.muted} />
              <Text style={s.emptyText}>
                {query.trim() ? "Aramana uygun ürün bulunamadı." : "Aramak istediğin ürünü yaz."}
              </Text>
            </View>
          }
          renderItem={(info: any) => {
            const item = info.item as StoreProductListItem;
            return (
              <Pressable
                style={({ pressed }) => [s.card, pressed && { opacity: 0.92, transform: [{ scale: 0.98 }] }]}
                onPress={() => router.push(`/store/product/${item.slug}` as never)}
              >
                <View style={s.imgWrap}>
                  {item.primaryImageUrl ? <Image source={{ uri: item.primaryImageUrl }} style={s.img} /> : null}
                </View>
                <Text style={s.title} numberOfLines={2}>{item.name}</Text>
                <Text style={s.price}>
                  {new Intl.NumberFormat("tr-TR", { style: "currency", currency: "TRY" }).format(item.discountedPrice || item.price)}
                </Text>
              </Pressable>
            );
          }}
        />
      )}
    </View>
  );
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  searchBox: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    margin: 16,
    paddingHorizontal: 14,
    height: 48,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface,
  },
  searchInput: { flex: 1, color: colors.ink, fontSize: 14 },
  card: { width: "48%", backgroundColor: colors.surface, borderRadius: 16, overflow: "hidden", borderWidth: 1, borderColor: colors.border },
  imgWrap: { height: 160, backgroundColor: colors.accentSoft },
  img: { width: "100%", height: "100%" },
  title: { fontSize: 13, fontWeight: "800", color: colors.ink, padding: 10, minHeight: 54 },
  price: { fontSize: 14, fontWeight: "900", color: colors.action, paddingHorizontal: 10, paddingBottom: 10 },
  empty: { alignItems: "center", justifyContent: "center", paddingVertical: 64, gap: 12 },
  emptyText: { color: colors.muted, fontSize: 14, fontWeight: "700", textAlign: "center", paddingHorizontal: 32 },
});
