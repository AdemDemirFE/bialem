import { Ionicons } from "@expo/vector-icons";
import { Stack } from "expo-router";
import { useEffect, useState } from "react";
import { FlatList, Pressable, StyleSheet, Text, View } from "react-native";
import { Reveal, Skeleton } from "../../../src/animations";
import { showAppError } from "../../../src/components/AppAlert";
import { storeManagementApi, type StoreManagementCategory } from "../../../src/lib/store-management-api";
import { colors } from "../../../src/theme/colors";

export default function StoreCategoriesManagementScreen() {
  const [items, setItems] = useState<StoreManagementCategory[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = async () => {
    try {
      const res = await storeManagementApi.categories();
      setItems(res);
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Kategoriler yüklenemedi");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    void load();
  }, []);

  const renderCategory = (cat: StoreManagementCategory, depth = 0) => {
    return (
      <View key={cat.id} style={[s.catRow, { marginLeft: depth * 16 }]}>
        <Ionicons name={depth === 0 ? "folder-open-outline" : "document-outline"} size={20} color={colors.accent} />
        <View style={s.copy}>
          <Text style={s.name}>{cat.name}</Text>
          <Text style={s.meta}>{cat.slug} · Sıra: {cat.sortOrder ?? 0} · {cat.isActive ? "Aktif" : "Pasif"}</Text>
        </View>
      </View>
    );
  };

  const flatten = (cats: StoreManagementCategory[]): StoreManagementCategory[] => {
    return cats.flatMap((c) => [c, ...(c.children ? flatten(c.children) : [])]);
  };

  const flatItems = flatten(items);

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ headerShown: true, title: "Kategori Yönetimi" }} />
      {loading ? (
        <View style={{ padding: 16, gap: 8 }}>
          <Skeleton height={72} borderRadius={16} />
          <Skeleton height={72} borderRadius={16} />
          <Skeleton height={72} borderRadius={16} />
        </View>
      ) : (
        <FlatList
          data={flatItems}
          keyExtractor={(item: StoreManagementCategory) => String(item.id)}
          contentContainerStyle={s.list}
          refreshing={refreshing}
          onRefresh={() => { setRefreshing(true); void load(); }}
          ListEmptyComponent={
            <View style={s.empty}>
              <Text style={s.emptyText}>Henüz kategori bulunmuyor.</Text>
            </View>
          }
          renderItem={(info: any) => {
            const item: StoreManagementCategory = info.item;
            return (
              <Reveal index={Math.min(info.index ?? 0, 8)}>
                {renderCategory(item, item.parentId ? 1 : 0)}
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
  list: { padding: 16, paddingBottom: 32, gap: 8 },
  catRow: { flexDirection: "row", alignItems: "center", gap: 12, padding: 14, borderRadius: 16, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  copy: { flex: 1, gap: 2 },
  name: { fontSize: 15, fontWeight: "900", color: colors.ink },
  meta: { fontSize: 12, color: colors.muted, fontWeight: "700" },
  empty: { alignItems: "center", padding: 40 },
  emptyText: { color: colors.muted, fontWeight: "800" },
});
