import { useLocalSearchParams, useRouter, Stack } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, FlatList, Image, Pressable, StyleSheet, Text, View } from "react-native";
import { showAppError } from "../../../src/components/AppAlert";
import { SkeletonList } from "../../../src/components/SkeletonList";
import { storeApi, type StoreProductListItem } from "../../../src/lib/store-api";
import { colors } from "../../../src/theme/colors";

export default function CategoryScreen() {
  const { slug } = useLocalSearchParams<{ slug: string }>();
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [items, setItems] = useState<StoreProductListItem[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  const load = async (p = 0) => {
    try {
      const res = await storeApi.products({ page: p, size: 20 });
      setItems((prev) => (p === 0 ? res.content : [...prev, ...res.content]));
      setHasMore(!res.last);
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Ürünler yüklenemedi");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load(0);
  }, [slug]);

  const loadMore = () => {
    if (hasMore && !loading) {
      setPage(page + 1);
      void load(page + 1);
    }
  };

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ title: "Kategori" }} />
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
                <Text style={s.title}>{item.name}</Text>
                <Text style={s.price}>{new Intl.NumberFormat("tr-TR", { style: "currency", currency: "TRY" }).format(item.discountedPrice || item.price)}</Text>
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
  card: { width: "48%", backgroundColor: colors.surface, borderRadius: 16, overflow: "hidden", borderWidth: 1, borderColor: colors.border },
  imgWrap: { height: 160, backgroundColor: colors.accentSoft },
  img: { width: "100%", height: "100%" },
  title: { fontSize: 13, fontWeight: "800", color: colors.ink, padding: 10, minHeight: 54 },
  price: { fontSize: 14, fontWeight: "900", color: colors.action, paddingHorizontal: 10, paddingBottom: 10 },
});
