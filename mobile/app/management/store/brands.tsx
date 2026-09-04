import { Ionicons } from "@expo/vector-icons";
import { Stack } from "expo-router";
import { useEffect, useState } from "react";
import { FlatList, Pressable, StyleSheet, Text, View } from "react-native";
import { Reveal, Skeleton } from "../../../src/animations";
import { showAppError } from "../../../src/components/AppAlert";
import { storeManagementApi, type StoreManagementBrand } from "../../../src/lib/store-management-api";
import { colors } from "../../../src/theme/colors";

export default function StoreBrandsManagementScreen() {
  const [items, setItems] = useState<StoreManagementBrand[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = async () => {
    try {
      setItems(await storeManagementApi.brands());
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Markalar yüklenemedi");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    void load();
  }, []);

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ headerShown: true, title: "Marka Yönetimi" }} />
      {loading ? (
        <View style={{ padding: 16, gap: 10 }}>
          <Skeleton height={76} borderRadius={18} />
          <Skeleton height={76} borderRadius={18} />
          <Skeleton height={76} borderRadius={18} />
        </View>
      ) : (
        <FlatList
          data={items}
          keyExtractor={(item: StoreManagementBrand) => String(item.id)}
          contentContainerStyle={s.list}
          refreshing={refreshing}
          onRefresh={() => { setRefreshing(true); void load(); }}
          ListEmptyComponent={
            <View style={s.empty}>
              <Text style={s.emptyText}>Henüz marka bulunmuyor.</Text>
            </View>
          }
          renderItem={(info: any) => {
            const item: StoreManagementBrand = info.item;
            return (
              <Reveal index={Math.min(info.index ?? 0, 8)}>
              <Pressable
                style={({ pressed }) => [s.card, pressed && { opacity: 0.94 }]}
              >
                <View style={s.iconShell}>
                  <Ionicons name="pricetag" size={22} color={colors.accent} />
                </View>
                <View style={s.copy}>
                  <Text style={s.name}>{item.name}</Text>
                  <Text style={s.meta}>{item.slug} · {item.isActive ? "Aktif" : "Pasif"}</Text>
                </View>
              </Pressable>
              </Reveal>
            );
          }}
        />
      )}
    </View>
  );
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  list: { padding: 16, paddingBottom: 32, gap: 10 },
  card: { flexDirection: "row", alignItems: "center", gap: 12, padding: 14, borderRadius: 18, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  iconShell: { width: 46, height: 46, alignItems: "center", justifyContent: "center", borderRadius: 15, backgroundColor: colors.accentSoft },
  copy: { flex: 1, gap: 2 },
  name: { fontSize: 15, fontWeight: "900", color: colors.ink },
  meta: { fontSize: 12, color: colors.muted, fontWeight: "700" },
  empty: { alignItems: "center", padding: 40 },
  emptyText: { color: colors.muted, fontWeight: "800" },
});
