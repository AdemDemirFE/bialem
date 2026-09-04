import { Ionicons } from "@expo/vector-icons";
import { Stack } from "expo-router";
import { StyleSheet, Text, View } from "react-native";
import { Reveal } from "../../../src/animations";
import { colors } from "../../../src/theme/colors";

export default function StoreReviewsManagementScreen() {
  return (
    <View style={s.screen}>
      <Stack.Screen options={{ headerShown: true, title: "Yorum Yönetimi" }} />
      <Reveal style={s.state}>
        <Ionicons name="chatbubble-ellipses-outline" size={48} color={colors.muted} />
        <Text style={s.title}>Yorum Yönetimi</Text>
        <Text style={s.muted}>
          Ürün değerlendirmelerini görüntülemek ve moderasyon yapmak için backend admin endpoint'i hazırlanacak. Şu anda bu ekran listede görülebilir ve yakında aktif hale getirilecektir.
        </Text>
      </Reveal>
    </View>
  );
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  state: { flex: 1, alignItems: "center", justifyContent: "center", gap: 14, padding: 32 },
  title: { fontSize: 20, fontWeight: "900", color: colors.ink },
  muted: { textAlign: "center", color: colors.muted, fontSize: 14, lineHeight: 20 },
});
