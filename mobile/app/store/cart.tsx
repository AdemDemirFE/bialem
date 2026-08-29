import { Ionicons } from "@expo/vector-icons";
import { useRouter, Stack, useFocusEffect } from "expo-router";
import { useCallback, useState } from "react";
import { ActivityIndicator, FlatList, Image, Pressable, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { showAppError } from "../../src/components/AppAlert";
import { storeApi, type StoreCartItem, type StoreCartSummary } from "../../src/lib/store-api";
import { colors } from "../../src/theme/colors";

export default function CartScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const [cart, setCart] = useState<StoreCartSummary | null>(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    try {
      setCart(await storeApi.cart());
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Sepet yüklenemedi");
    } finally {
      setLoading(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      void load();
    }, [load])
  );

  const updateQty = async (id: number, qty: number) => {
    try {
      setCart(await storeApi.updateCartItem(id, qty));
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Güncellenemedi");
    }
  };

  const remove = async (id: number) => {
    try {
      setCart(await storeApi.removeCartItem(id));
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Silinemedi");
    }
  };

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ title: "Sepetim" }} />
      {loading ? (
        <ActivityIndicator color={colors.accent} style={{ marginTop: 60 }} />
      ) : !cart || cart.items.length === 0 ? (
        <View style={s.empty}>
          <Ionicons name="cart-outline" size={48} color={colors.muted} />
          <Text style={s.emptyText}>Sepetiniz boş</Text>
          <Pressable style={s.shopBtn} onPress={() => router.push("/store")}>
            <Text style={s.shopBtnText}>Alışverişe Başla</Text>
          </Pressable>
        </View>
      ) : (
        <>
          <FlatList
            data={cart.items}
            keyExtractor={(item: StoreCartItem) => String(item.id)}
            contentContainerStyle={{ padding: 16, paddingBottom: 180 }}
            renderItem={(info: any) => {
              const item = info.item as StoreCartItem;
              return (
                <View style={s.item}>
                  {item.productImage ? <Image source={{ uri: item.productImage }} style={s.itemImg} /> : <View style={s.itemImg} />}
                  <View style={s.itemInfo}>
                    <Text style={s.itemName}>{item.productName}</Text>
                    {item.variantName ? <Text style={s.itemVariant}>{item.variantName}</Text> : null}
                    <Text style={s.itemPrice}>{formatPrice(item.unitPrice)}</Text>
                    <View style={s.qtyRow}>
                      <Pressable onPress={() => updateQty(item.id, item.quantity - 1)} style={s.qtyBtn}><Text>-</Text></Pressable>
                      <Text style={s.qtyText}>{item.quantity}</Text>
                      <Pressable onPress={() => updateQty(item.id, item.quantity + 1)} style={s.qtyBtn}><Text>+</Text></Pressable>
                    </View>
                  </View>
                  <Pressable onPress={() => remove(item.id)} style={s.removeBtn}>
                    <Ionicons name="trash-outline" size={20} color={colors.danger} />
                  </Pressable>
                </View>
              );
            }}
          />
          <View style={[s.footer, { paddingBottom: Math.max(insets.bottom, 12) }]}>
            <View style={s.row}>
              <Text style={s.label}>Ara Toplam</Text>
              <Text style={s.value}>{formatPrice(cart.subtotal)}</Text>
            </View>
            <View style={s.row}>
              <Text style={s.label}>Kargo</Text>
              <Text style={s.value}>{cart.shippingAmount > 0 ? formatPrice(cart.shippingAmount) : "Ücretsiz"}</Text>
            </View>
            <View style={[s.row, { marginTop: 8 }]}>
              <Text style={s.totalLabel}>Toplam</Text>
              <Text style={s.totalValue}>{formatPrice(cart.totalAmount)}</Text>
            </View>
            <Pressable style={s.checkoutBtn} onPress={() => router.push("/store/checkout")}>
              <Text style={s.checkoutBtnText}>Ödemeye Geç</Text>
            </Pressable>
          </View>
        </>
      )}
    </View>
  );
}

function formatPrice(n: number) {
  return new Intl.NumberFormat("tr-TR", { style: "currency", currency: "TRY" }).format(n);
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  empty: { flex: 1, alignItems: "center", justifyContent: "center", gap: 16 },
  emptyText: { fontSize: 16, fontWeight: "800", color: colors.muted },
  shopBtn: { paddingHorizontal: 20, paddingVertical: 12, borderRadius: 99, backgroundColor: colors.action },
  shopBtnText: { color: "#fff", fontWeight: "900" },
  item: { flexDirection: "row", gap: 12, padding: 12, backgroundColor: colors.surface, borderRadius: 16, marginBottom: 12, borderWidth: 1, borderColor: colors.border },
  itemImg: { width: 80, height: 80, borderRadius: 12, backgroundColor: colors.accentSoft },
  itemInfo: { flex: 1, gap: 4 },
  itemName: { fontSize: 14, fontWeight: "900", color: colors.ink },
  itemVariant: { fontSize: 12, color: colors.muted },
  itemPrice: { fontSize: 14, fontWeight: "900", color: colors.action },
  qtyRow: { flexDirection: "row", alignItems: "center", gap: 10, marginTop: 4 },
  qtyBtn: { width: 28, height: 28, borderRadius: 8, backgroundColor: colors.accentSoft, alignItems: "center", justifyContent: "center" },
  qtyText: { fontSize: 14, fontWeight: "900", color: colors.ink, minWidth: 20, textAlign: "center" },
  removeBtn: { padding: 8 },
  footer: { position: "absolute", bottom: 0, left: 0, right: 0, backgroundColor: colors.surface, borderTopWidth: 1, borderTopColor: colors.border, padding: 16 },
  row: { flexDirection: "row", justifyContent: "space-between", marginBottom: 4 },
  label: { fontSize: 14, color: colors.muted },
  value: { fontSize: 14, fontWeight: "800", color: colors.ink },
  totalLabel: { fontSize: 18, fontWeight: "900", color: colors.ink },
  totalValue: { fontSize: 18, fontWeight: "900", color: colors.action },
  checkoutBtn: { marginTop: 14, height: 52, borderRadius: 99, backgroundColor: colors.action, alignItems: "center", justifyContent: "center" },
  checkoutBtnText: { color: "#fff", fontSize: 16, fontWeight: "900" },
});
