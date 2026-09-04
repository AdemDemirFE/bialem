import { Ionicons } from "@expo/vector-icons";
import { Stack, useRouter } from "expo-router";
import { useCallback, useEffect, useState } from "react";
import { FlatList, Image, Pressable, StyleSheet, Text, View } from "react-native";
import { Reveal, Skeleton } from "../../../src/animations";
import { showAppConfirm, showAppError } from "../../../src/components/AppAlert";
import { storeManagementApi, type StoreManagementProduct } from "../../../src/lib/store-management-api";
import { colors } from "../../../src/theme/colors";

export default function StoreProductsManagementScreen() {
  const router = useRouter();
  const [items, setItems] = useState<StoreManagementProduct[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = useCallback(async (p = 0, reset = false) => {
    if (p === 0) setLoading(true);
    try {
      const res = await storeManagementApi.products(p, 20);
      setItems((prev) => (reset ? res.content : [...prev, ...res.content]));
      setHasMore(!res.last);
      setPage(res.number);
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Ürünler yüklenemedi");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    void load(0, true);
  }, [load]);

  const loadMore = () => {
    if (hasMore && !loading) {
      void load(page + 1);
    }
  };

  const onRefresh = () => {
    setRefreshing(true);
    void load(0, true);
  };

  const deleteProduct = async (id: number, name: string) => {
    const confirmed = await showAppConfirm({
      title: "Ürünü sil?",
      text: `${name} adlı ürün silinecek.`,
      confirmText: "Sil",
      confirmDanger: true
    });
    if (!confirmed) return;
    try {
      await storeManagementApi.deleteProduct(id);
      setItems((prev) => prev.filter((p) => p.id !== id));
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Silinemedi");
    }
  };

  const formatPrice = (n?: number | null, currency = "TRY") => {
    if (n === undefined || n === null) return "-";
    return new Intl.NumberFormat("tr-TR", { style: "currency", currency }).format(n);
  };

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ headerShown: true, title: "Ürün Yönetimi" }} />
      {loading && items.length === 0 ? (
        <View style={{ padding: 16, gap: 10 }}>
          <Skeleton height={92} borderRadius={18} />
          <Skeleton height={92} borderRadius={18} />
          <Skeleton height={92} borderRadius={18} />
        </View>
      ) : (
        <FlatList
          data={items}
          keyExtractor={(item: StoreManagementProduct) => String(item.id)}
          contentContainerStyle={s.list}
          onEndReached={loadMore}
          onEndReachedThreshold={0.5}
          refreshing={refreshing}
          onRefresh={onRefresh}
          ListEmptyComponent={
            <View style={s.empty}>
              <Text style={s.emptyText}>Henüz ürün bulunmuyor.</Text>
            </View>
          }
          renderItem={(info: any) => {
            const item: StoreManagementProduct = info.item;
            return (
            <Reveal index={Math.min(info.index ?? 0, 6)}>
            <Pressable
              style={({ pressed }) => [s.card, pressed && { opacity: 0.94 }]}
              onPress={() => router.push(`/management/store/products/${item.id}` as never)}
            >
              {item.imageUrl ? (
                <Image source={{ uri: item.imageUrl }} style={s.thumb} />
              ) : (
                <View style={s.thumbPlaceholder}>
                  <Ionicons name="cube-outline" size={24} color={colors.muted} />
                </View>
              )}
              <View style={s.copy}>
                <Text style={s.name} numberOfLines={1}>{item.name}</Text>
                <Text style={s.meta}>{item.categoryName || "Kategori yok"} · {item.brandName || "Marka yok"}</Text>
                <Text style={s.price}>{formatPrice(item.discountedPrice || item.price, item.currency)}</Text>
                <Text style={s.stock}>Stok: {item.stockQuantity ?? 0} · {item.status}</Text>
              </View>
              <Pressable style={s.deleteBtn} onPress={() => deleteProduct(item.id, item.name)}>
                <Ionicons name="trash-outline" size={20} color={colors.danger} />
              </Pressable>
            </Pressable>
            </Reveal>
            );
          }}
        />
      )}
    </View>
  );
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  list: { padding: 16, paddingBottom: 32, gap: 10 },
  card: { flexDirection: "row", alignItems: "center", gap: 12, padding: 12, borderRadius: 18, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  thumb: { width: 64, height: 64, borderRadius: 14, backgroundColor: colors.accentSoft },
  thumbPlaceholder: { width: 64, height: 64, borderRadius: 14, backgroundColor: colors.accentSoft, alignItems: "center", justifyContent: "center" },
  copy: { flex: 1, gap: 3 },
  name: { fontSize: 15, fontWeight: "900", color: colors.ink },
  meta: { fontSize: 12, color: colors.muted, fontWeight: "700" },
  price: { fontSize: 14, fontWeight: "900", color: colors.action },
  stock: { fontSize: 12, color: colors.muted, fontWeight: "700" },
  deleteBtn: { padding: 8 },
  empty: { alignItems: "center", padding: 40 },
  emptyText: { color: colors.muted, fontWeight: "800" },
});
