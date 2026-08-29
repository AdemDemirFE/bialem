import { Ionicons } from "@expo/vector-icons";
import { useRouter, Stack, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { showAppAlert, showAppConfirm, showAppError } from "../../src/components/AppAlert";
import { notifyCartUpdated } from "../../src/lib/cart-events";
import { storeApi, type StoreAddress, type StoreCartSummary, type StoreOrder, type StorePaymentInitiateResponse } from "../../src/lib/store-api";
import { colors } from "../../src/theme/colors";

type PaymentMethod = "CREDIT_CARD" | "BANK_TRANSFER";

export default function PaymentScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const { addressId, note, orderNumber } = useLocalSearchParams<{ addressId?: string; note?: string; orderNumber?: string }>();
  const [cart, setCart] = useState<StoreCartSummary | null>(null);
  const [order, setOrder] = useState<StoreOrder | null>(null);
  const [address, setAddress] = useState<StoreAddress | null>(null);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);
  const [method, setMethod] = useState<PaymentMethod>("CREDIT_CARD");
  const [cardName, setCardName] = useState("");
  const [cardNumber, setCardNumber] = useState("");
  const [cardExpiry, setCardExpiry] = useState("");
  const [cardCvc, setCardCvc] = useState("");
  const [receiptUrl, setReceiptUrl] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        if (orderNumber) {
          const o = await storeApi.paymentOrder(orderNumber);
          setOrder(o);
          setCart(null);
          const addresses = await storeApi.addresses();
          const selected = addresses.find((a) => String(a.id) === addressId);
          setAddress(selected || null);
        } else {
          const [c, addresses] = await Promise.all([storeApi.checkoutSummary(), storeApi.addresses()]);
          setCart(c);
          setOrder(null);
          const selected = addresses.find((a) => String(a.id) === addressId);
          setAddress(selected || null);
        }
      } catch (e) {
        showAppError(e instanceof Error ? e.message : "Ödeme bilgileri yüklenemedi");
      } finally {
        setLoading(false);
      }
    };
    void load();
  }, [addressId, orderNumber]);

  const totalAmount = order?.totalAmount ?? cart?.totalAmount ?? 0;

  const pay = async () => {
    const id = Number(addressId);
    if (!id || !address) {
      showAppError("Teslimat adresi bulunamadı");
      return;
    }
    if (!orderNumber && (!cart || cart.items.length === 0)) {
      showAppError("Sepetiniz boş");
      return;
    }

    if (method === "CREDIT_CARD") {
      if (!cardName.trim() || !cardNumber.trim() || !cardExpiry.trim() || !cardCvc.trim()) {
        showAppError("Lütfen kart bilgilerini doldurun");
        return;
      }
    } else {
      if (!receiptUrl.trim()) {
        showAppError("Lütfen dekont linkini girin");
        return;
      }
    }

    const ok = await showAppConfirm({ title: "Ödeme onayı", text: `${formatPrice(totalAmount)} tutarında işlem yapmak istiyor musunuz?` });
    if (!ok) return;
    setProcessing(true);

    try {
      let currentOrder = order;
      if (!currentOrder) {
        currentOrder = await storeApi.checkout({
          shippingAddressId: id,
          customerNote: note || undefined,
          paymentProvider: "MOCK",
          idempotencyKey: `checkout-${Date.now()}-${Math.random().toString(36).slice(2)}`,
        });
      }

      if (method === "BANK_TRANSFER") {
        const transfer = await storeApi.createBankTransfer({ orderNumber: currentOrder.orderNumber, receiptUrl });
        router.replace(`/payment/pending?orderNumber=${currentOrder.orderNumber}&reference=${transfer.referenceCode}` as never);
        return;
      }

      const [month, year] = parseExpiry(cardExpiry);
      const result: StorePaymentInitiateResponse = await storeApi.initiatePayment({
        orderNumber: currentOrder.orderNumber,
        paymentMethod: "CREDIT_CARD",
        cardHolderName: cardName,
        cardNumber: cardNumber.replace(/\s/g, ""),
        expireMonth: month,
        expireYear: year,
        cvc: cardCvc,
        idempotencyKey: `payment-${Date.now()}-${Math.random().toString(36).slice(2)}`,
      });

      if (result.paymentStatus === "FAILED") {
        router.replace(`/payment/failure?orderNumber=${currentOrder.orderNumber}&reason=${encodeURIComponent(result.message || "Ödeme başarısız")}` as never);
        return;
      }

      if (result.redirectUrl) {
        window.location.href = result.redirectUrl;
        return;
      }

      if (result.htmlContent) {
        const form = document.createElement("div");
        form.innerHTML = result.htmlContent;
        document.body.appendChild(form);
        const formElement = form.querySelector("form");
        if (formElement) formElement.submit();
        return;
      }

      // MOCK success path
      notifyCartUpdated();
      showAppAlert({
        title: "Ödeme başarılı!",
        text: `Sipariş No: ${currentOrder.orderNumber}`,
        icon: "success",
        confirmText: "Siparişlerim",
      }).then(() => {
        router.replace(`/store/orders/${currentOrder.id}` as never);
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
      <ScrollView contentContainerStyle={{ padding: 16, paddingBottom: 220 + insets.bottom }}>
        <View style={s.card}>
          <Ionicons name="shield-checkmark-outline" size={28} color={colors.accent} />
          <View style={{ flex: 1 }}>
            <Text style={s.cardTitle}>Güvenli Ödeme</Text>
            <Text style={s.cardMeta}>Kart bilgileriniz sunucularımızda saklanmaz.</Text>
          </View>
        </View>

        <Text style={s.sectionTitle}>Ödeme Yöntemi</Text>
        <View style={s.methodRow}>
          <Pressable style={[s.methodCard, method === "CREDIT_CARD" && s.methodCardActive]} onPress={() => setMethod("CREDIT_CARD")}>
            <Ionicons name="card-outline" size={22} color={method === "CREDIT_CARD" ? colors.action : colors.muted} />
            <Text style={[s.methodText, method === "CREDIT_CARD" && s.methodTextActive]}>Kredi/Banka Kartı</Text>
          </Pressable>
          <Pressable style={[s.methodCard, method === "BANK_TRANSFER" && s.methodCardActive]} onPress={() => setMethod("BANK_TRANSFER")}>
            <Ionicons name="cash-outline" size={22} color={method === "BANK_TRANSFER" ? colors.action : colors.muted} />
            <Text style={[s.methodText, method === "BANK_TRANSFER" && s.methodTextActive]}>Havale/EFT</Text>
          </Pressable>
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

        {method === "CREDIT_CARD" ? (
          <>
            <Text style={s.sectionTitle}>Kart Bilgileri</Text>
            <TextInput style={s.input} placeholder="Kart üzerindeki isim" placeholderTextColor={colors.muted} value={cardName} onChangeText={setCardName} />
            <TextInput
              style={s.input}
              placeholder="Kart numarası"
              placeholderTextColor={colors.muted}
              keyboardType="number-pad"
              maxLength={19}
              value={cardNumber}
              onChangeText={(text) => setCardNumber(formatCardNumber(text))}
            />
            <View style={s.row}>
              <TextInput style={[s.input, { flex: 1 }]} placeholder="AA/YY" placeholderTextColor={colors.muted} maxLength={5} value={cardExpiry} onChangeText={setCardExpiry} />
              <TextInput style={[s.input, { flex: 1 }]} placeholder="CVC" placeholderTextColor={colors.muted} keyboardType="number-pad" maxLength={4} value={cardCvc} onChangeText={setCardCvc} />
            </View>
            <Text style={s.hint}>Test için kart numarası 4 ile başlayınca başarısız, diğerleri başarılı simülasyonu çalışır.</Text>
          </>
        ) : (
          <>
            <Text style={s.sectionTitle}>Havale/EFT Bilgileri</Text>
            <View style={s.addressBox}>
              <Text style={s.addressTitle}>IBAN</Text>
              <Text style={s.addressText}>TR00 1234 0000 0000 0000 0000 00</Text>
              <Text style={s.addressTitle}>Hesap Sahibi</Text>
              <Text style={s.addressText}>Bialem Teknoloji A.Ş.</Text>
              <Text style={s.addressTitle}>Banka</Text>
              <Text style={s.addressText}>Örnek Bank</Text>
            </View>
            <TextInput style={s.input} placeholder="Dekont linki (PDF/JPG/PNG)" placeholderTextColor={colors.muted} value={receiptUrl} onChangeText={setReceiptUrl} />
          </>
        )}

        <Text style={s.sectionTitle}>Sipariş Özeti</Text>
        {(order?.items ?? cart?.items ?? []).map((item: any) => (
          <View key={item.id} style={s.summaryRow}>
            <Text style={s.summaryName}>{item.productName} x{item.quantity}</Text>
            <Text style={s.summaryPrice}>{formatPrice(item.totalPrice || 0)}</Text>
          </View>
        ))}
        <View style={[s.summaryRow, { marginTop: 8 }]}>
          <Text style={s.totalLabel}>Toplam</Text>
          <Text style={s.totalValue}>{formatPrice(totalAmount)}</Text>
        </View>
      </ScrollView>

      <View style={[s.footer, { paddingBottom: Math.max(insets.bottom, 12) }]}>
        <Pressable disabled={processing || !address} style={[s.payBtn, (processing || !address) && s.disabled]} onPress={pay}>
          <Text style={s.payBtnText}>{processing ? "İşleniyor..." : method === "BANK_TRANSFER" ? "Havale Bildirimi Gönder" : `Öde ${formatPrice(totalAmount)}`}</Text>
        </Pressable>
      </View>
    </View>
  );
}

function formatPrice(n: number) {
  return new Intl.NumberFormat("tr-TR", { style: "currency", currency: "TRY" }).format(n);
}

function formatCardNumber(value: string) {
  const v = value.replace(/\s+/g, "").replace(/[^0-9]/g, "");
  const parts = [];
  for (let i = 0; i < v.length; i += 4) {
    parts.push(v.slice(i, i + 4));
  }
  return parts.join(" ").slice(0, 19);
}

function parseExpiry(value: string): [string, string] {
  const [month, year] = value.split("/");
  return [month?.padStart(2, "0") || "", year ? (year.length === 2 ? "20" + year : year) : ""];
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  card: { flexDirection: "row", alignItems: "center", gap: 12, padding: 16, borderRadius: 18, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, marginBottom: 8 },
  cardTitle: { fontSize: 16, fontWeight: "900", color: colors.ink },
  cardMeta: { flex: 1, fontSize: 12, color: colors.muted },
  sectionTitle: { fontSize: 16, fontWeight: "900", color: colors.ink, marginTop: 16, marginBottom: 8 },
  methodRow: { flexDirection: "row", gap: 10 },
  methodCard: { flex: 1, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 8, paddingVertical: 14, borderRadius: 14, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  methodCardActive: { borderColor: colors.action, backgroundColor: colors.accentSoft },
  methodText: { fontSize: 13, fontWeight: "800", color: colors.muted },
  methodTextActive: { color: colors.action },
  addressBox: { padding: 14, borderRadius: 14, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  addressTitle: { fontSize: 14, fontWeight: "900", color: colors.ink },
  addressText: { fontSize: 12, color: colors.muted, marginTop: 2 },
  warning: { color: colors.danger, fontWeight: "800" },
  input: { height: 50, paddingHorizontal: 14, borderRadius: 14, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, color: colors.ink, marginBottom: 10 },
  row: { flexDirection: "row", gap: 10 },
  hint: { fontSize: 12, color: colors.muted, marginBottom: 8 },
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
