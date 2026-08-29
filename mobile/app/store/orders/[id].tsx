import { Ionicons } from "@expo/vector-icons";
import { useLocalSearchParams, useRouter, Stack } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { showAppConfirm, showAppError } from "../../../src/components/AppAlert";
import { storeApi, type StoreOrder, type StoreOrderStatusHistory } from "../../../src/lib/store-api";
import { colors } from "../../../src/theme/colors";

const STATUS_ORDER = [
  "PENDING_PAYMENT",
  "PAID",
  "WAITING_ADMIN_APPROVAL",
  "APPROVED",
  "PREPARING",
  "READY_FOR_SHIPPING",
  "SHIPPED",
  "IN_TRANSIT",
  "OUT_FOR_DELIVERY",
  "DELIVERED",
];

export default function OrderDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const [order, setOrder] = useState<StoreOrder | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        setOrder(await storeApi.order(Number(id)));
      } catch (e) {
        showAppError(e instanceof Error ? e.message : "Sipariş yüklenemedi");
      } finally {
        setLoading(false);
      }
    };
    void load();
  }, [id]);

  const cancel = async () => {
    const ok = await showAppConfirm({ title: "Siparişi iptal et", text: "Bu siparişi iptal etmek istediğinize emin misiniz?", confirmDanger: true });
    if (!ok) return;
    try {
      setOrder(await storeApi.cancelOrder(Number(id)));
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "İptal edilemedi");
    }
  };

  if (loading || !order) {
    return (
      <View style={s.screen}>
        <Stack.Screen options={{ title: "Sipariş Detayı" }} />
        <ActivityIndicator color={colors.accent} style={{ marginTop: 60 }} />
      </View>
    );
  }

  const currentIndex = STATUS_ORDER.indexOf(order.orderStatus || "");

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ title: `Sipariş #${order.orderNumber}` }} />
      <ScrollView contentContainerStyle={{ padding: 16, paddingBottom: 100 + insets.bottom }}>
        <View style={s.card}>
          <View style={{ flexDirection: "row", justifyContent: "space-between" }}>
            <Text style={s.orderNo}>{order.orderNumber}</Text>
            <StatusBadge status={order.orderStatus || ""} />
          </View>
          <Text style={s.date}>{new Date(order.createdAt).toLocaleString("tr-TR")}</Text>
        </View>

        <View style={s.card}>
          <Text style={s.sectionTitle}>Sipariş Durumu</Text>
          {STATUS_ORDER.map((status, idx) => {
            const active = idx <= currentIndex;
            return (
              <View key={status} style={s.timelineRow}>
                <Ionicons name={active ? "checkmark-circle" : "ellipse-outline"} size={18} color={active ? colors.action : colors.muted} />
                <Text style={[s.timelineText, active && s.timelineTextActive]}>{statusLabel(status)}</Text>
              </View>
            );
          })}
        </View>

        {order.shipping ? (
          <View style={s.card}>
            <Text style={s.sectionTitle}>Kargo Takibi</Text>
            <Text style={s.text}>Kargo Firması: {order.shipping.carrier}</Text>
            <Text style={s.text}>Takip No: {order.shipping.trackingNumber}</Text>
            <Text style={s.text}>Durum: {statusLabel(order.shipping.shippingStatus || "")}</Text>
          </View>
        ) : null}

        <View style={s.card}>
          <Text style={s.sectionTitle}>Ürünler</Text>
          {order.items.map((item) => (
            <View key={item.id} style={s.itemRow}>
              <View style={{ flex: 1 }}>
                <Text style={s.itemName}>{item.productName}</Text>
                {item.variantName ? <Text style={s.itemVariant}>{item.variantName}</Text> : null}
                <Text style={s.itemQty}>x{item.quantity}</Text>
              </View>
              <Text style={s.itemPrice}>{formatPrice(item.totalPrice)}</Text>
            </View>
          ))}
          <View style={s.totalRow}>
            <Text style={s.totalLabel}>Toplam</Text>
            <Text style={s.totalValue}>{formatPrice(order.totalAmount)}</Text>
          </View>
        </View>

        {order.orderStatus === "DELIVERED" && order.items.some((i) => i.productId) ? (
          <Pressable
            style={s.reviewBtn}
            onPress={() => router.push(`/store/product/${order.items[0].productId}?review=${order.items[0].id}` as never)}
          >
            <Text style={s.reviewBtnText}>Ürünü Değerlendir</Text>
          </Pressable>
        ) : null}

        {(order.orderStatus === "PENDING_PAYMENT" || order.orderStatus === "PAID" || order.orderStatus === "WAITING_ADMIN_APPROVAL" || order.orderStatus === "APPROVED" || order.orderStatus === "PREPARING") ? (
          <Pressable style={s.cancelBtn} onPress={cancel}>
            <Text style={s.cancelBtnText}>Siparişi İptal Et</Text>
          </Pressable>
        ) : null}
      </ScrollView>
    </View>
  );
}

function StatusBadge({ status }: { status: string }) {
  return <View style={s.badge}><Text style={s.badgeText}>{statusLabel(status)}</Text></View>;
}

function statusLabel(status: string) {
  return {
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
}

function formatPrice(n: number) {
  return new Intl.NumberFormat("tr-TR", { style: "currency", currency: "TRY" }).format(n);
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  card: { padding: 16, borderRadius: 16, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, marginBottom: 12 },
  orderNo: { fontSize: 16, fontWeight: "900", color: colors.ink },
  date: { fontSize: 13, color: colors.muted, marginTop: 4 },
  badge: { paddingHorizontal: 8, paddingVertical: 3, borderRadius: 8, backgroundColor: colors.accentSoft },
  badgeText: { fontSize: 11, fontWeight: "900", color: colors.action },
  sectionTitle: { fontSize: 16, fontWeight: "900", color: colors.ink, marginBottom: 10 },
  text: { fontSize: 13, color: colors.muted, marginBottom: 4 },
  timelineRow: { flexDirection: "row", alignItems: "center", gap: 10, paddingVertical: 5 },
  timelineText: { fontSize: 13, color: colors.muted },
  timelineTextActive: { color: colors.ink, fontWeight: "800" },
  itemRow: { flexDirection: "row", justifyContent: "space-between", paddingVertical: 8, borderBottomWidth: 1, borderBottomColor: colors.border },
  itemName: { fontSize: 14, fontWeight: "800", color: colors.ink },
  itemVariant: { fontSize: 12, color: colors.muted },
  itemQty: { fontSize: 12, color: colors.muted },
  itemPrice: { fontSize: 14, fontWeight: "900", color: colors.action },
  totalRow: { flexDirection: "row", justifyContent: "space-between", marginTop: 12 },
  totalLabel: { fontSize: 16, fontWeight: "900", color: colors.ink },
  totalValue: { fontSize: 16, fontWeight: "900", color: colors.action },
  reviewBtn: { height: 50, borderRadius: 99, backgroundColor: colors.action, alignItems: "center", justifyContent: "center", marginBottom: 10 },
  reviewBtnText: { color: "#fff", fontWeight: "900" },
  cancelBtn: { height: 50, borderRadius: 99, backgroundColor: colors.accentSoft, alignItems: "center", justifyContent: "center" },
  cancelBtnText: { color: colors.danger, fontWeight: "900" },
});
