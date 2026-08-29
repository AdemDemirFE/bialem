import * as ExpoLinking from "expo-linking";
import { Ionicons } from "@expo/vector-icons";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Alert, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useAuth } from "../../src/lib/auth";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";

type OrderItemRecord = {
  id: string;
  quantity: number;
  unit_price: number;
  total_price: number;
  ticket: {
    id: string;
    name: string;
    event: { id: string; title: string };
  };
};

type OrderRecord = {
  id: string;
  order_number: string;
  total_amount: number;
  currency: string;
  status: string;
  created_at: string;
  items: OrderItemRecord[];
  payment?: { status: string } | null;
};

export default function OrderDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const router = useRouter();
  const { user } = useAuth();
  const [order, setOrder] = useState<OrderRecord | null>(null);
  const [loading, setLoading] = useState(true);
  const [paying, setPaying] = useState(false);
  const [cancelling, setCancelling] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadOrder = async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    const result = await api.rpc("get_order", { target_order_id: id });
    if (result.error || !result.data) {
      setError(result.error?.message || "Sipariş yüklenemedi.");
    } else {
      setOrder(result.data as OrderRecord);
    }
    setLoading(false);
  };

  useEffect(() => {
    void loadOrder();
  }, [id]);

  const startPayment = async () => {
    if (!order || !user) return;
    setPaying(true);
    setError(null);
    const idempotencyKey = `${user.id}-${order.id}-${Date.now()}`;
    const callbackUrl = ExpoLinking.createURL("/payment/callback");
    const result = await api.rpc("initiate_payment", {
      target_order_id: order.id,
      target_provider: "iyzico",
      target_idempotency_key: idempotencyKey,
      target_callback_url: callbackUrl
    });
    setPaying(false);
    if (result.error || !result.data || !(result.data as { success: boolean }).success) {
      setError((result.data as { error?: string })?.error || result.error?.message || "Ödeme başlatılamadı.");
      return;
    }
    const data = result.data as { checkout_url: string; provider_transaction_id: string };
    // In a real integration, open the provider checkout URL and let it deep-link back.
    // For this stub we simulate a successful callback immediately.
    await api.rpc("handle_payment_callback", {
      target_transaction_id: data.provider_transaction_id,
      target_payload: "{}",
      target_provider: "iyzico"
    });
    router.replace(`/payment/callback?transactionId=${data.provider_transaction_id}&status=success` as never);
  };

  const cancelOrder = async () => {
    if (!order) return;
    Alert.alert("Siparişi iptal et", "Bu siparişi iptal etmek istediğinize emin misiniz?", [
      { text: "Vazgeç", style: "cancel" },
      {
        text: "İptal et",
        style: "destructive",
        onPress: async () => {
          setCancelling(true);
          const result = await api.rpc("cancel_order", { target_order_id: order.id });
          setCancelling(false);
          if (result.error) {
            setError(result.error.message);
            return;
          }
          router.replace("/my-tickets" as never);
        }
      }
    ]);
  };

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color={colors.accent} />
      </View>
    );
  }

  if (!order) {
    return (
      <View style={styles.center}>
        <Text style={styles.errorText}>{error || "Sipariş bulunamadı."}</Text>
        <Pressable style={styles.primaryButton} onPress={() => router.back()}>
          <Text style={styles.primaryButtonText}>Geri dön</Text>
        </Pressable>
      </View>
    );
  }

  const isPaid = order.status === "paid";
  const isPending = order.status === "pending";

  return (
    <ScrollView contentContainerStyle={styles.page}>
      <Pressable style={styles.backButton} onPress={() => router.back()}>
        <Ionicons name="arrow-back" size={18} color={colors.ink} />
        <Text style={styles.backText}>Geri</Text>
      </Pressable>

      <Text style={styles.title}>Sipariş Özeti</Text>
      <Text style={styles.orderNumber}>#{order.order_number}</Text>

      {error ? <Text style={styles.errorText}>{error}</Text> : null}

      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Biletler</Text>
        {order.items.map((item) => (
          <View key={item.id} style={styles.itemRow}>
            <View style={{ flex: 1 }}>
              <Text style={styles.itemName}>{item.ticket.name}</Text>
              <Text style={styles.itemEvent}>{item.ticket.event.title}</Text>
            </View>
            <View style={styles.itemRight}>
              <Text style={styles.itemQty}>x{item.quantity}</Text>
              <Text style={styles.itemPrice}>
                {item.total_price} {order.currency}
              </Text>
            </View>
          </View>
        ))}
        <View style={styles.divider} />
        <View style={styles.totalRow}>
          <Text style={styles.totalLabel}>Toplam</Text>
          <Text style={styles.totalValue}>
            {order.total_amount} {order.currency}
          </Text>
        </View>
      </View>

      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Durum</Text>
        <View style={styles.statusRow}>
          <Text style={styles.statusLabel}>Sipariş durumu</Text>
          <Text style={[styles.statusValue, (styles as Record<string, any>)[order.status]]}>{order.status.toUpperCase()}</Text>
        </View>
        {order.payment ? (
          <View style={styles.statusRow}>
            <Text style={styles.statusLabel}>Ödeme durumu</Text>
            <Text style={[styles.statusValue, (styles as Record<string, any>)[order.payment.status]]}>{order.payment.status.toUpperCase()}</Text>
          </View>
        ) : null}
      </View>

      {isPending ? (
        <Pressable style={[styles.primaryButton, paying && styles.disabledButton]} onPress={() => void startPayment()} disabled={paying}>
          <Text style={styles.primaryButtonText}>{paying ? "Yönlendiriliyor..." : "Ödemeyi tamamla"}</Text>
        </Pressable>
      ) : null}

      {isPending ? (
        <Pressable style={[styles.secondaryButton, cancelling && styles.disabledButton]} onPress={() => void cancelOrder()} disabled={cancelling}>
          <Text style={styles.secondaryButtonText}>{cancelling ? "İptal ediliyor..." : "Siparişi iptal et"}</Text>
        </Pressable>
      ) : null}

      {isPaid ? (
        <Pressable style={styles.primaryButton} onPress={() => router.replace("/my-tickets" as never)}>
          <Text style={styles.primaryButtonText}>Biletlerimi gör</Text>
        </Pressable>
      ) : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 24, gap: 16, backgroundColor: colors.page },
  center: { flex: 1, alignItems: "center", justifyContent: "center", gap: 16, padding: 24, backgroundColor: colors.page },
  backButton: { flexDirection: "row", alignItems: "center", gap: 8, alignSelf: "flex-start" },
  backText: { color: colors.ink, fontWeight: "800" },
  title: { fontSize: 26, fontWeight: "900", color: colors.ink, marginTop: 8 },
  orderNumber: { color: colors.muted, fontWeight: "800", marginTop: -10 },
  errorText: { color: colors.danger, fontWeight: "700", textAlign: "center" },
  card: { padding: 18, borderRadius: 24, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, gap: 12 },
  sectionTitle: { fontSize: 16, fontWeight: "900", color: colors.ink },
  itemRow: { flexDirection: "row", justifyContent: "space-between", gap: 12, alignItems: "center" },
  itemName: { fontSize: 15, fontWeight: "800", color: colors.ink },
  itemEvent: { color: colors.muted, fontSize: 12, marginTop: 2 },
  itemRight: { alignItems: "flex-end" },
  itemQty: { color: colors.muted, fontWeight: "700" },
  itemPrice: { fontSize: 15, fontWeight: "900", color: colors.ink },
  divider: { height: 1, backgroundColor: colors.border },
  totalRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  totalLabel: { fontSize: 16, fontWeight: "800", color: colors.ink },
  totalValue: { fontSize: 18, fontWeight: "900", color: colors.accent },
  statusRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  statusLabel: { color: colors.muted, fontWeight: "700" },
  statusValue: { fontWeight: "900" },
  pending: { color: colors.accent },
  paid: { color: "#16a34a" },
  completed: { color: "#16a34a" },
  failed: { color: colors.danger },
  cancelled: { color: colors.muted },
  primaryButton: { paddingVertical: 14, borderRadius: 999, backgroundColor: colors.action, alignItems: "center" },
  secondaryButton: { paddingVertical: 14, borderRadius: 999, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, alignItems: "center" },
  disabledButton: { opacity: 0.5 },
  primaryButtonText: { color: colors.actionText, fontWeight: "900", fontSize: 15 },
  secondaryButtonText: { color: colors.ink, fontWeight: "900", fontSize: 15 }
});
