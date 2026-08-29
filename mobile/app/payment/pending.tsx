import { Ionicons } from "@expo/vector-icons";
import { useRouter, Stack, useLocalSearchParams } from "expo-router";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { colors } from "../../src/theme/colors";

export default function PaymentPendingScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const { orderNumber, reference } = useLocalSearchParams<{ orderNumber?: string; reference?: string }>();

  return (
    <View style={[s.screen, { paddingBottom: insets.bottom }]}>
      <Stack.Screen options={{ title: "Ödeme Bekliyor" }} />
      <View style={s.iconWrap}>
        <Ionicons name="time" size={72} color={colors.accent} />
      </View>
      <Text style={s.title}>Havale/EFT ödemeniz bekleniyor</Text>
      {reference ? <Text style={s.ref}>Referans: {reference}</Text> : null}
      {orderNumber ? <Text style={s.orderNo}>Sipariş No: {orderNumber}</Text> : null}
      <Text style={s.subtitle}>Dekontunuz incelendikten sonra siparişiniz onaylanacaktır.</Text>
      <Pressable style={s.btn} onPress={() => router.replace("/store/orders" as never)}>
        <Text style={s.btnText}>Siparişlerim</Text>
      </Pressable>
      <Pressable style={s.secondaryBtn} onPress={() => router.replace("/store" as never)}>
        <Text style={s.secondaryBtnText}>Mağazaya Dön</Text>
      </Pressable>
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
