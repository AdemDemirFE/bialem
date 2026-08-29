import { Ionicons } from "@expo/vector-icons";
import { useRouter, Stack, useFocusEffect } from "expo-router";
import { useCallback, useState } from "react";
import { ActivityIndicator, FlatList, Pressable, StyleSheet, Text, View } from "react-native";
import { showAppError } from "../../src/components/AppAlert";
import { storeApi, type StoreOrder, type StoreOrderItem } from "../../src/lib/store-api";
import { colors } from "../../src/theme/colors";

const TABS = [
  { key: "", label: "Tümü" },
  { key: "PREPARING", label: "Hazırlanıyor" },
  { key: "SHIPPED", label: "Kargoda" },
  { key: "DELIVERED", label: "Teslim" },
  { key: "CANCELLED", label: "İptal" },
];

export default function OrdersScreen() {
  const router = useRouter();
  const [orders, setOrders] = useState<StoreOrder[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState("");

  const load = useCallback(async () => {
    try {
      const res = await storeApi.orders(activeTab || undefined);
      setOrders(res.content);
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Siparişler yüklenemedi");
    } finally {
      setLoading(false);
    }
  }, [activeTab]);

  useFocusEffect(
    useCallback(() => {
      void load();
    }, [load])
  );

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ title: "Siparişlerim" }} />
      <View style={s.tabs}>
        {TABS.map((t) => (
          <Pressable key={t.key} onPress={() => setActiveTab(t.key)} style={[s.tab, activeTab === t.key && s.tabActive]}>
            <Text style={[s.tabText, activeTab === t.key && s.tabTextActive]}>{t.label}</Text>
          </Pressable>
        ))}
      </View>
      {loading ? (
        <ActivityIndicator color={colors.accent} style={{ marginTop: 60 }} />
      ) : orders.length === 0 ? (
        <View style={s.empty}>
          <Ionicons name="cube-outline" size={48} color={colors.muted} />
          <Text style={s.emptyText}>Henüz siparişiniz yok</Text>
        </View>
      ) : (
        <FlatList
          data={orders as StoreOrder[]}
          keyExtractor={(item: StoreOrder) => String(item.id)}
          contentContainerStyle={{ padding: 16 }}
          renderItem={(info: any) => {
            const item = info.item as StoreOrder;
            return (
              <Pressable style={s.card} onPress={() => router.push(`/store/orders/${item.id}` as never)}>
                <View style={{ flexDirection: "row", justifyContent: "space-between" }}>
                  <Text style={s.orderNo}>{item.orderNumber}</Text>
                  <StatusBadge status={item.orderStatus || ""} />
                </View>
                <Text style={s.date}>{new Date(item.createdAt).toLocaleString("tr-TR")}</Text>
                <Text style={s.total}>{formatPrice(item.totalAmount)}</Text>
                {item.items.slice(0, 1).map((i: StoreOrderItem) => (
                  <Text key={i.id} style={s.itemName}>{i.productName} x{i.quantity}</Text>
                ))}
              </Pressable>
            );
          }}
        />
      )}
    </View>
  );
}

function StatusBadge({ status }: { status: string }) {
  const label = {
    PENDING_PAYMENT: "Ödeme Bekliyor",
    PAID: "Ödendi",
    WAITING_ADMIN_APPROVAL: "Onay Bekliyor",
    APPROVED: "Onaylandı",
    PREPARING: "Hazırlanıyor",
    READY_FOR_SHIPPING: "Kargoya Hazır",
    SHIPPED: "Kargoda",
    IN_TRANSIT: "Yolda",
    OUT_FOR_DELIVERY: "Dağıtımda",
    DELIVERED: "Teslim Edildi",
    CANCELLED: "İptal",
    PAYMENT_FAILED: "Ödeme Başarısız",
  }[status] || status;
  return <View style={s.badge}><Text style={s.badgeText}>{label}</Text></View>;
}

function formatPrice(n: number) {
  return new Intl.NumberFormat("tr-TR", { style: "currency", currency: "TRY" }).format(n);
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  tabs: { flexDirection: "row", gap: 8, padding: 12, borderBottomWidth: 1, borderBottomColor: colors.border },
  tab: { paddingHorizontal: 12, paddingVertical: 6, borderRadius: 99, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  tabActive: { backgroundColor: colors.action, borderColor: colors.action },
  tabText: { fontSize: 12, fontWeight: "800", color: colors.muted },
  tabTextActive: { color: "#fff" },
  empty: { flex: 1, alignItems: "center", justifyContent: "center", gap: 12 },
  emptyText: { fontSize: 15, color: colors.muted, fontWeight: "800" },
  card: { padding: 14, borderRadius: 16, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, marginBottom: 12 },
  orderNo: { fontSize: 14, fontWeight: "900", color: colors.ink },
  date: { fontSize: 12, color: colors.muted, marginTop: 2 },
  total: { fontSize: 16, fontWeight: "900", color: colors.action, marginTop: 8 },
  itemName: { fontSize: 13, color: colors.muted, marginTop: 4 },
  badge: { paddingHorizontal: 8, paddingVertical: 3, borderRadius: 8, backgroundColor: colors.accentSoft },
  badgeText: { fontSize: 11, fontWeight: "900", color: colors.action },
});
