import { Ionicons } from "@expo/vector-icons";
import { useRouter, Stack, useLocalSearchParams } from "expo-router";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Reveal } from "../../src/animations";
import { colors } from "../../src/theme/colors";

export default function PaymentFailureScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const { orderNumber, reason } = useLocalSearchParams<{ orderNumber?: string; reason?: string }>();

  return (
    <View style={[s.screen, { paddingBottom: insets.bottom }]}>
      <Stack.Screen options={{ title: "Ödeme Başarısız" }} />
      <Reveal>
      <View style={s.iconWrap}>
        <Ionicons name="close-circle" size={72} color={colors.danger} />
      </View>
      </Reveal>
      <Reveal index={1}>
      <Text style={s.title}>Ödeme işlemi başarısız oldu</Text>
      </Reveal>
      {reason ? <Text style={s.reason}>{reason}</Text> : null}
      {orderNumber ? <Text style={s.orderNo}>Sipariş No: {orderNumber}</Text> : null}
      <Reveal index={2} style={{ width: "100%", gap: 14 }}>
      <Pressable
        style={({ pressed }) => [s.btn, pressed && { opacity: 0.9, transform: [{ scale: 0.98 }] }]}
        onPress={() => router.replace(`/store/payment?orderNumber=${orderNumber}` as never)}
      >
        <Text style={s.btnText}>Tekrar Dene</Text>
      </Pressable>
      <Pressable
        style={({ pressed }) => [s.secondaryBtn, pressed && { opacity: 0.9 }]}
        onPress={() => router.replace("/store/orders" as never)}
      >
        <Text style={s.secondaryBtnText}>Siparişlerim</Text>
      </Pressable>
      </Reveal>
    </View>
  );
}

const s = StyleSheet.create({
  screen: { flex: 1, alignItems: "center", justifyContent: "center", padding: 24, backgroundColor: colors.page, gap: 14 },
  iconWrap: { width: 120, height: 120, borderRadius: 60, backgroundColor: "#ffe8ef", alignItems: "center", justifyContent: "center", marginBottom: 10 },
  title: { fontSize: 22, fontWeight: "900", color: colors.ink, textAlign: "center" },
  reason: { fontSize: 15, color: colors.danger, fontWeight: "700", textAlign: "center" },
  orderNo: { fontSize: 15, color: colors.muted, fontWeight: "800" },
  btn: { width: "100%", height: 54, borderRadius: 99, backgroundColor: colors.action, alignItems: "center", justifyContent: "center" },
  btnText: { color: "#fff", fontSize: 16, fontWeight: "900" },
  secondaryBtn: { width: "100%", height: 54, borderRadius: 99, borderWidth: 1, borderColor: colors.border, alignItems: "center", justifyContent: "center" },
  secondaryBtnText: { color: colors.ink, fontSize: 16, fontWeight: "800" },
});
