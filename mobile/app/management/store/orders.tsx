import { Ionicons } from "@expo/vector-icons";
import { Stack, useRouter } from "expo-router";
import { useCallback, useEffect, useState } from "react";
import { ActivityIndicator, FlatList, Pressable, StyleSheet, Text, View } from "react-native";
import { showAppConfirm, showAppError } from "../../../src/components/AppAlert";
import { storeManagementApi, type StoreManagementOrder } from "../../../src/lib/store-management-api";
import { colors } from "../../../src/theme/colors";

const STATUS_LABELS: Record<string, string> = {
  PENDING: "Bekliyor",
  PENDING_PAYMENT: "Ödeme Bekliyor",
  PAID: "Ödendi",
  WAITING_APPROVAL: "Onay Bekliyor",
  WAITING_ADMIN_APPROVAL: "Yönetici Onayı Bekliyor",
  APPROVED: "Onaylandı",
  PREPARING: "Hazırlanıyor",
  READY_FOR_SHIPPING: "Kargoya Hazır",
  SHIPPED: "Kargoda",
  IN_TRANSIT: "Yolda",
  OUT_FOR_DELIVERY: "Dağıtımda",
  DELIVERED: "Teslim Edildi",
  CANCELLED: "İptal",
  REFUNDED: "İade Edildi",
  RETURN_REQUESTED: "İade Talebi",
  RETURNED: "İade Edildi",
  PAYMENT_FAILED: "Ödeme Başarısız",
};

export default function StoreOrdersManagementScreen() {
  const router = useRouter();
  const [items, setItems] = useState<StoreManagementOrder[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = useCallback(async (p = 0, reset = false) => {
    if (p === 0) setLoading(true);
    try {
      const res = await storeManagementApi.orders(p, 20);
      setItems((prev) => (reset ? res.content : [...prev, ...res.content]));
      setHasMore(!res.last);
      setPage(res.number);
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Siparişler yüklenemedi");
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

  const runAction = async (id: number, action: () => Promise<StoreManagementOrder>, label: string) => {
    try {
      const updated = await action();
      setItems((prev) => prev.map((o) => (o.id === id ? { ...o, ...updated } : o)));
    } catch (e) {
      showAppError(e instanceof Error ? e.message : `${label} işlemi başarısız`);
    }
  };

  const cancelOrder = async (id: number) => {
    const confirmed = await showAppConfirm({
      title: "Siparişi iptal et",
      text: "Bu sipariş iptal edilecek.",
      confirmText: "İptal Et",
      confirmDanger: true
    });
    if (!confirmed) return;
    await runAction(id, () => storeManagementApi.cancelOrder(id, "Yönetici tarafından iptal edildi"), "İptal");
  };

  const formatPrice = (n?: number, currency = "TRY") => {
    if (n === undefined || n === null) return "-";
    return new Intl.NumberFormat("tr-TR", { style: "currency", currency }).format(n);
  };

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ headerShown: true, title: "Sipariş Yönetimi" }} />
      {loading && items.length === 0 ? (
        <ActivityIndicator color={colors.accent} style={{ marginTop: 40 }} />
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
              <Text style={s.emptyText}>Henüz sipariş bulunmuyor.</Text>
            </View>
          }
          renderItem={(info: any) => {
            const item: StoreManagementOrder = info.item;
            return (
              <Pressable style={s.card} onPress={() => router.push(`/management/store/orders/${item.id}` as never)}>
                <View style={s.copy}>
                  <Text style={s.number}>{item.orderNumber}</Text>
                  <Text style={s.meta}>Tutar: {formatPrice(item.totalAmount, item.currency)}</Text>
                  <View style={s.badgeRow}>
                    <View style={[s.badge, { backgroundColor: statusColor(item.orderStatus) }]}>
                      <Text style={s.badgeText}>{STATUS_LABELS[item.orderStatus || "PENDING"] || item.orderStatus}</Text>
                    </View>
                  </View>
                </View>
                <View style={s.actions}>
                  {item.orderStatus === "PENDING" || item.orderStatus === "WAITING_APPROVAL" ? (
                    <Pressable style={s.actionBtn} onPress={() => void runAction(item.id, () => storeManagementApi.approveOrder(item.id), "Onay")}>
                      <Ionicons name="checkmark" size={18} color={colors.success} />
                    </Pressable>
                  ) : null}
                  {item.orderStatus === "APPROVED" ? (
                    <Pressable style={s.actionBtn} onPress={() => void runAction(item.id, () => storeManagementApi.markPreparing(item.id), "Hazırlık")}>
                      <Ionicons name="archive" size={18} color={colors.accent} />
                    </Pressable>
                  ) : null}
                  {item.orderStatus === "PREPARING" ? (
                    <Pressable style={s.actionBtn} onPress={() => void runAction(item.id, () => storeManagementApi.markReadyForShipping(item.id), "Kargoya Hazır")}>
                      <Ionicons name="cube" size={18} color={colors.accent} />
                    </Pressable>
                  ) : null}
                  <Pressable style={s.actionBtn} onPress={() => cancelOrder(item.id)}>
                    <Ionicons name="close" size={18} color={colors.danger} />
                  </Pressable>
                </View>
              </Pressable>
            );
          }}
        />
      )}
    </View>
  );
}

function statusColor(status?: string) {
  switch (status) {
    case "DELIVERED":
      return colors.success;
    case "CANCELLED":
    case "REFUNDED":
      return colors.danger;
    case "SHIPPED":
      return colors.aqua;
    default:
      return colors.warning;
  }
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  list: { padding: 16, paddingBottom: 32, gap: 10 },
  card: { flexDirection: "row", alignItems: "center", gap: 12, padding: 14, borderRadius: 18, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  copy: { flex: 1, gap: 4 },
  number: { fontSize: 15, fontWeight: "900", color: colors.ink },
  meta: { fontSize: 12, color: colors.muted, fontWeight: "700" },
  badgeRow: { flexDirection: "row", gap: 6, marginTop: 2 },
  badge: { paddingHorizontal: 8, paddingVertical: 3, borderRadius: 8 },
  badgeText: { fontSize: 11, fontWeight: "900", color: "#fff" },
  actions: { flexDirection: "row", alignItems: "center", gap: 6 },
  actionBtn: { padding: 8, borderRadius: 10, backgroundColor: colors.surfaceStrong },
  empty: { alignItems: "center", padding: 40 },
  emptyText: { color: colors.muted, fontWeight: "800" },
});
