import { Ionicons } from "@expo/vector-icons";
import { useRouter, Stack } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { showAppError } from "../../src/components/AppAlert";
import { storeApi, type StoreAddress, type StoreCartSummary } from "../../src/lib/store-api";
import { colors } from "../../src/theme/colors";

export default function CheckoutScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const [cart, setCart] = useState<StoreCartSummary | null>(null);
  const [addresses, setAddresses] = useState<StoreAddress[]>([]);
  const [selectedAddress, setSelectedAddress] = useState<number | null>(null);
  const [note, setNote] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const [c, a] = await Promise.all([storeApi.checkoutSummary(), storeApi.addresses()]);
        setCart(c);
        setAddresses(a);
        const def = a.find((x) => x.isDefault);
        if (def) setSelectedAddress(def.id!);
        else if (a.length) setSelectedAddress(a[0].id!);
      } catch (e) {
        showAppError(e instanceof Error ? e.message : "Ödeme sayfası yüklenemedi");
      } finally {
        setLoading(false);
      }
    };
    void load();
  }, []);

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
        <Text style={s.sectionTitle}>Teslimat Adresi</Text>
        {addresses.map((a) => (
          <Pressable key={a.id} onPress={() => setSelectedAddress(a.id!)} style={[s.addressCard, selectedAddress === a.id && s.addressCardActive]}>
            <View style={{ flexDirection: "row", alignItems: "center", gap: 10 }}>
              <Ionicons name={selectedAddress === a.id ? "radio-button-on" : "radio-button-off"} size={22} color={colors.action} />
              <View style={{ flex: 1 }}>
                <Text style={s.addressTitle}>{a.title}</Text>
                <Text style={s.addressText}>{a.firstName} {a.lastName}</Text>
                <Text style={s.addressText}>{a.city}, {a.district}</Text>
                <Text style={s.addressText}>{a.addressLine}</Text>
              </View>
            </View>
          </Pressable>
        ))}
        <Pressable style={s.addAddress} onPress={() => router.push("/store/addresses")}>
          <Ionicons name="add" size={20} color={colors.action} />
          <Text style={s.addAddressText}>Yeni Adres Ekle / Yönet</Text>
        </Pressable>

        <Text style={s.sectionTitle}>Sipariş Notu</Text>
        <TextInput
          style={s.noteInput}
          multiline
          placeholder="Satıcıya notunuz..."
          placeholderTextColor={colors.muted}
          value={note}
          onChangeText={setNote}
        />

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
          disabled={!selectedAddress || !cart || cart.items.length === 0}
          style={[s.payBtn, (!selectedAddress || !cart || cart.items.length === 0) && s.disabled]}
          onPress={() => router.push(`/store/payment?addressId=${selectedAddress}&note=${encodeURIComponent(note)}` as never)}
        >
          <Text style={s.payBtnText}>Ödemeye Geç</Text>
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
  sectionTitle: { fontSize: 16, fontWeight: "900", color: colors.ink, marginTop: 16, marginBottom: 8 },
  addressCard: { padding: 14, borderRadius: 14, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, marginBottom: 8 },
  addressCardActive: { borderColor: colors.action, backgroundColor: colors.accentSoft },
  addressTitle: { fontSize: 14, fontWeight: "900", color: colors.ink },
  addressText: { fontSize: 12, color: colors.muted },
  addAddress: { flexDirection: "row", alignItems: "center", gap: 6, marginTop: 4 },
  addAddressText: { color: colors.action, fontWeight: "900" },
  noteInput: { minHeight: 90, padding: 12, borderRadius: 14, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, color: colors.ink, textAlignVertical: "top" },
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
