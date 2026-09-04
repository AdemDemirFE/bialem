import { Ionicons } from "@expo/vector-icons";
import { useRouter, Stack, useLocalSearchParams } from "expo-router";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Reveal } from "../../src/animations";
import { colors } from "../../src/theme/colors";

export default function PaymentPendingScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const { orderNumber, reference } = useLocalSearchParams<{ orderNumber?: string; reference?: string }>();

  return (
    <View style={[s.screen, { paddingBottom: insets.bottom }]}>
      <Stack.Screen options={{ title: "Ödeme Bekliyor" }} />
      <Reveal>
      <View style={s.iconWrap}>
        <Ionicons name="time" size={72} color={colors.accent} />
      </View>
      </Reveal>
      <Reveal index={1}>
      <Text style={s.title}>Havale/EFT ödemeniz bekleniyor</Text>
      </Reveal>
      {reference ? <Text style={s.ref}>Referans: {reference}</Text> : null}
      {orderNumber ? <Text style={s.orderNo}>Sipariş No: {orderNumber}</Text> : null}
      <Text style={s.subtitle}>Dekontunuz incelendikten sonra siparişiniz onaylanacaktır.</Text>
      <Reveal index={2} style={{ width: "100%", gap: 14 }}>
      <Pressable
        style={({ pressed }) => [s.btn, pressed && { opacity: 0.9, transform: [{ scale: 0.98 }] }]}
        onPress={() => router.replace("/store/orders" as never)}
      >
        <Text style={s.btnText}>Siparişlerim</Text>
      </Pressable>
      <Pressable
        style={({ pressed }) => [s.secondaryBtn, pressed && { opacity: 0.9 }]}
        onPress={() => router.replace("/store" as never)}
      >
        <Text style={s.secondaryBtnText}>Mağazaya Dön</Text>
      </Pressable>
      </Reveal>
    </View>
  );
}

const s = StyleSheet.create({
  screen: { flex: 1, alignItems: "center", justifyContent: "center", padding: 24, backgroundColor: colors.page, gap: 14 },
  iconWrap: { width: 120, height: 120, borderRadius: 60, backgroundColor: colors.accentSoft, alignItems: "center", justifyContent: "center", marginBottom: 10 },
  title: { fontSize: 22, fontWeight: "900", color: colors.ink, textAlign: "center" },
  ref: { fontSize: 18, color: colors.action, fontWeight: "900" },
  orderNo: { fontSize: 15, color: colors.muted, fontWeight: "800" },
  subtitle: { fontSize: 15, color: colors.muted, textAlign: "center", lineHeight: 22 },
  btn: { width: "100%", height: 54, borderRadius: 99, backgroundColor: colors.action, alignItems: "center", justifyContent: "center" },
  btnText: { color: "#fff", fontSize: 16, fontWeight: "900" },
  secondaryBtn: { width: "100%", height: 54, borderRadius: 99, borderWidth: 1, borderColor: colors.border, alignItems: "center", justifyContent: "center" },
  secondaryBtnText: { color: colors.ink, fontSize: 16, fontWeight: "800" },
});
