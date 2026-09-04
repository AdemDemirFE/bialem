import { Ionicons } from "@expo/vector-icons";
import { Stack } from "expo-router";
import { useCallback, useEffect, useState } from "react";
import { FlatList, Pressable, StyleSheet, Text, View } from "react-native";
import { Reveal, Skeleton } from "../../../src/animations";
import { showAppConfirm, showAppError, showAppSelectAlert } from "../../../src/components/AppAlert";
import { storeManagementApi, type StoreManagementOrder } from "../../../src/lib/store-management-api";
import { colors } from "../../../src/theme/colors";

const SHIPPING_STATUSES = ["SHIPPED", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED"];

export default function StoreShipmentsManagementScreen() {
  const [items, setItems] = useState<StoreManagementOrder[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = useCallback(async (p = 0, reset = false) => {
    if (p === 0) setLoading(true);
    try {
      const res = await storeManagementApi.orders(p, 20, undefined, "createdAt,desc");
      const shipped = res.content.filter((o) => o.shippingStatus && o.shippingStatus !== "NOT_SHIPPED" && o.orderStatus !== "CANCELLED");
      setItems((prev) => (reset ? shipped : [...prev, ...shipped]));
      setHasMore(!res.last);
      setPage(res.number);
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Kargolar yüklenemedi");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    void load(0, true);
  }, [load]);

  const loadMore = () => {
    if (hasMore && !loading) void load(page + 1);
  };

  const onRefresh = () => {
    setRefreshing(true);
    void load(0, true);
  };

  const createShipping = async (orderId: number) => {
    const confirmed = await showAppConfirm({
      title: "Kargo oluştur",
      text: "Siparişe varsayılan kargo kaydı ekleniyor. Detayları sonradan düzenleyebilirsiniz.",
      confirmText: "Oluştur"
    });
    if (!confirmed) return;
    try {
      await storeManagementApi.createShipping(orderId, { carrier: "Kargo Firması", trackingNumber: "-" });
      void load(0, true);
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Kargo oluşturulamadı");
    }
  };

  const updateStatus = async (orderId: number) => {
    const status = await showAppSelectAlert({
      title: "Kargo Durumu",
      text: "Yeni durum seçin",
      options: SHIPPING_STATUSES.map((s) => ({ value: s, label: s }))
    });
    if (!status) return;
    try {
      await storeManagementApi.updateShippingStatus(orderId, status);
      void load(0, true);
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Durum güncellenemedi");
    }
  };

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ headerShown: true, title: "Kargo Yönetimi" }} />
      {loading && items.length === 0 ? (
        <View style={{ padding: 16, gap: 10 }}>
          <Skeleton height={80} borderRadius={18} />
          <Skeleton height={80} borderRadius={18} />
          <Skeleton height={80} borderRadius={18} />
        </View>
      ) : (
        <FlatList
          data={items}
          keyExtractor={(item: StoreManagementOrder) => String(item.id)}
          contentContainerStyle={s.list}
          onEndReached={loadMore}
          onEndReachedThreshold={0.5}
          refreshing={refreshing}
          onRefresh={onRefresh}
          ListEmptyComponent={
            <View style={s.empty}>
              <Text style={s.emptyText}>Henüz kargo kaydı bulunmuyor.</Text>
            </View>
          }
          renderItem={(info: any) => {
            const item: StoreManagementOrder = info.item;
            return (
              <Reveal index={Math.min(info.index ?? 0, 6)}>
              <View style={s.card}>
                <View style={s.copy}>
                  <Text style={s.number}>{item.orderNumber}</Text>
                  <Text style={s.meta}>Durum: {item.shippingStatus}</Text>
                </View>
                <View style={s.actions}>
                  {item.shippingStatus === "NOT_SHIPPED" || !item.shippingStatus ? (
                    <Pressable
                      style={({ pressed }) => [s.actionBtn, pressed && { opacity: 0.85 }]}
                      onPress={() => createShipping(item.id)}
                    >
                      <Ionicons name="add" size={18} color={colors.success} />
                    </Pressable>
                  ) : (
                    <Pressable
                      style={({ pressed }) => [s.actionBtn, pressed && { opacity: 0.85 }]}
                      onPress={() => updateStatus(item.id)}
                    >
                      <Ionicons name="refresh" size={18} color={colors.accent} />
                    </Pressable>
                  )}
                </View>
              </View>
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
  card: { flexDirection: "row", alignItems: "center", gap: 12, padding: 14, borderRadius: 18, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  copy: { flex: 1, gap: 4 },
  number: { fontSize: 15, fontWeight: "900", color: colors.ink },
  meta: { fontSize: 12, color: colors.muted, fontWeight: "700" },
  actions: { flexDirection: "row", alignItems: "center", gap: 6 },
  actionBtn: { padding: 8, borderRadius: 10, backgroundColor: colors.surfaceStrong },
  empty: { alignItems: "center", padding: 40 },
  emptyText: { color: colors.muted, fontWeight: "800" },
});
