import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { Pressable, StyleSheet } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { colors } from "../theme/colors";

export function FloatingAssistantButton() {
  const router = useRouter();
  const insets = useSafeAreaInsets();

  return (
    <Pressable
      style={[styles.fab, { bottom: 90 + Math.max(insets.bottom, 0) }]}
      onPress={() => router.push("/assistant" as never)}
      accessibilityLabel="Bialem Asistan"
      accessibilityRole="button"
    >
      <Ionicons name="sparkles" size={26} color={colors.onBrand} />
    </Pressable>
  );
}

const styles = StyleSheet.create({
  fab: {
    position: "absolute",
    right: 18,
    width: 58,
    height: 58,
    borderRadius: 29,
    backgroundColor: colors.brandInk,
    alignItems: "center",
    justifyContent: "center",
    shadowColor: colors.brandInk,
    shadowOffset: { width: 0, height: 5 },
    shadowOpacity: 0.25,
    shadowRadius: 12,
    elevation: 8,
    zIndex: 999,
  },
});
