import { Ionicons } from "@expo/vector-icons";
import { useRouter, Stack, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { showAppAlert, showAppConfirm, showAppError } from "../../src/components/AppAlert";
import { notifyCartUpdated } from "../../src/lib/cart-events";
import { storeApi, type StoreAddress, type StoreCartSummary } from "../../src/lib/store-api";
import { colors } from "../../src/theme/colors";

export default function PaymentScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const { addressId, note } = useLocalSearchParams<{ addressId?: string; note?: string }>();
  const [cart, setCart] = useState<StoreCartSummary | null>(null);
  const [address, setAddress] = useState<StoreAddress | null>(null);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);
  const [cardName, setCardName] = useState("");
  const [cardNumber, setCardNumber] = useState("");
  const [cardExpiry, setCardExpiry] = useState("");
  const [cardCvc, setCardCvc] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        const [c, addresses] = await Promise.all([storeApi.checkoutSummary(), storeApi.addresses()]);
        setCart(c);
        const selected = addresses.find((a) => String(a.id) === addressId);
        setAddress(selected || null);
      } catch (e) {
        showAppError(e instanceof Error ? e.message : "Ödeme bilgileri yüklenemedi");
      } finally {
        setLoading(false);
      }
    };
    void load();
  }, [addressId]);

  const pay = async () => {
    const id = Number(addressId);
    if (!id || !address) {
      showAppError("Teslimat adresi bulunamadı");
      return;
    }
    if (!cart || cart.items.length === 0) {
      showAppError("Sepetiniz boş");
      return;
    }
    if (!cardName.trim() || !cardNumber.trim() || !cardExpiry.trim() || !cardCvc.trim()) {
      showAppError("Lütfen kart bilgilerini doldurun");
      return;
    }
    const ok = await showAppConfirm({ title: "Ödeme onayı", text: `${formatPrice(cart.totalAmount)} tutarında ödeme yapmak istiyor musunuz?` });
    if (!ok) return;
    setProcessing(true);
    try {
      const order = await storeApi.checkout({
        shippingAddressId: id,
        customerNote: note || undefined,
        paymentProvider: "IYZICO",
        idempotencyKey: `checkout-${Date.now()}-${Math.random().toString(36).slice(2)}`,
      });
      notifyCartUpdated();
      showAppAlert({
        title: "Siparişiniz oluşturuldu!",
        text: `Sipariş No: ${order.orderNumber}\nToplam: ${formatPrice(order.totalAmount)}`,
        icon: "success",
        confirmText: "Siparişlerim",
      }).then(() => {
        router.replace(`/store/orders/${order.id}` as never);
      });
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Ödeme başarısız");
    } finally {
      setProcessing(false);
    }
  };

  if (loading) {
    return (
      <View style={s.screen}>
        <Stack.Screen options={{ title: "Ödeme" }} />
        <ActivityIndicator color={colors.accent} style={{ marginTop: 60 }} />
      </View>
    );
  }

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ title: "Ödeme" }} />
      <ScrollView contentContainerStyle={{ padding: 16, paddingBottom: 160 + insets.bottom }}>
        <View style={s.card}>
          <Ionicons name="card-outline" size={28} color={colors.accent} />
          <Text style={s.cardTitle}>Güvenli Ödeme</Text>
          <Text style={s.cardMeta}>Kart bilgileriniz örnek demo ortamında işlenmektedir.</Text>
        </View>

        <Text style={s.sectionTitle}>Teslimat Adresi</Text>
        {address ? (
          <View style={s.addressBox}>
            <Text style={s.addressTitle}>{address.title}</Text>
            <Text style={s.addressText}>{address.firstName} {address.lastName}</Text>
            <Text style={s.addressText}>{address.city}, {address.district}</Text>
            <Text style={s.addressText}>{address.addressLine}</Text>
          </View>
        ) : (
          <Text style={s.warning}>Adres bulunamadı. Lütfen önce teslimat adresi seçin.</Text>
        )}

        <Text style={s.sectionTitle}>Kart Bilgileri</Text>
        <TextInput style={s.input} placeholder="Kart üzerindeki isim" placeholderTextColor={colors.muted} value={cardName} onChangeText={setCardName} />
        <TextInput style={s.input} placeholder="Kart numarası" placeholderTextColor={colors.muted} keyboardType="number-pad" maxLength={19} value={cardNumber} onChangeText={setCardNumber} />
        <View style={s.row}>
          <TextInput style={[s.input, { flex: 1 }]} placeholder="AA/YY" placeholderTextColor={colors.muted} maxLength={5} value={cardExpiry} onChangeText={setCardExpiry} />
          <TextInput style={[s.input, { flex: 1 }]} placeholder="CVC" placeholderTextColor={colors.muted} keyboardType="number-pad" maxLength={4} value={cardCvc} onChangeText={setCardCvc} />
        </View>

        <Text style={s.sectionTitle}>Sipariş Özeti</Text>
        {cart?.items.map((item) => (
          <View key={item.id} style={s.summaryRow}>
            <Text style={s.summaryName}>{item.productName} x{item.quantity}</Text>
            <Text style={s.summaryPrice}>{formatPrice(item.totalPrice || 0)}</Text>
          </View>
        ))}
        <View style={[s.summaryRow, { marginTop: 8 }]}>
          <Text style={s.totalLabel}>Toplam</Text>
          <Text style={s.totalValue}>{formatPrice(cart?.totalAmount || 0)}</Text>
        </View>
      </ScrollView>

      <View style={[s.footer, { paddingBottom: Math.max(insets.bottom, 12) }]}>
        <Pressable
          disabled={processing || !address}
          style={[s.payBtn, (processing || !address) && s.disabled]}
          onPress={pay}
        >
          <Text style={s.payBtnText}>{processing ? "İşleniyor..." : `Öde ${formatPrice(cart?.totalAmount || 0)}`}</Text>
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
  card: { flexDirection: "row", alignItems: "center", gap: 12, padding: 16, borderRadius: 18, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, marginBottom: 8 },
  cardTitle: { fontSize: 16, fontWeight: "900", color: colors.ink },
  cardMeta: { flex: 1, fontSize: 12, color: colors.muted },
  sectionTitle: { fontSize: 16, fontWeight: "900", color: colors.ink, marginTop: 16, marginBottom: 8 },
  addressBox: { padding: 14, borderRadius: 14, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  addressTitle: { fontSize: 14, fontWeight: "900", color: colors.ink },
  addressText: { fontSize: 12, color: colors.muted, marginTop: 2 },
  warning: { color: colors.danger, fontWeight: "800" },
  input: { height: 50, paddingHorizontal: 14, borderRadius: 14, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, color: colors.ink, marginBottom: 10 },
  row: { flexDirection: "row", gap: 10 },
  summaryRow: { flexDirection: "row", justifyContent: "space-between", paddingVertical: 6 },
  summaryName: { fontSize: 13, color: colors.muted },
  summaryPrice: { fontSize: 13, fontWeight: "800", color: colors.ink },
  totalLabel: { fontSize: 16, fontWeight: "900", color: colors.ink },
  totalValue: { fontSize: 16, fontWeight: "900", color: colors.action },
  footer: { position: "absolute", bottom: 0, left: 0, right: 0, backgroundColor: colors.surface, borderTopWidth: 1, borderTopColor: colors.border, padding: 16 },
  payBtn: { height: 54, borderRadius: 99, backgroundColor: colors.action, alignItems: "center", justifyContent: "center" },
  payBtnText: { color: "#fff", fontSize: 16, fontWeight: "900" },
  disabled: { opacity: 0.6 },
});
