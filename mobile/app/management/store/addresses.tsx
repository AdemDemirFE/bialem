import { Ionicons } from "@expo/vector-icons";
import { Stack } from "expo-router";
import { useCallback, useEffect, useState } from "react";
import { ActivityIndicator, FlatList, Pressable, StyleSheet, Text, View } from "react-native";
import { showAppError } from "../../../src/components/AppAlert";
import { storeManagementApi, type StoreManagementAddress } from "../../../src/lib/store-management-api";
import { colors } from "../../../src/theme/colors";

export default function StoreAddressesManagementScreen() {
  const [items, setItems] = useState<StoreManagementAddress[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setItems(await storeManagementApi.addresses());
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Adresler yüklenemedi");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  const onRefresh = () => {
    setRefreshing(true);
    void load();
  };

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ headerShown: true, title: "Adres Yönetimi" }} />
      {loading && items.length === 0 ? (
        <ActivityIndicator color={colors.accent} style={{ marginTop: 40 }} />
      ) : (
        <FlatList
          data={items}
          keyExtractor={(item: StoreManagementAddress) => String(item.id)}
          contentContainerStyle={s.list}
          refreshing={refreshing}
          onRefresh={onRefresh}
          ListEmptyComponent={
            <View style={s.empty}>
              <Text style={s.emptyText}>Henüz adres bulunmuyor.</Text>
            </View>
          }
          renderItem={(info: any) => {
            const item: StoreManagementAddress = info.item;
            return (
              <View style={s.card}>
                <View style={s.iconShell}>
                  <Ionicons name="location" size={22} color={colors.accent} />
                </View>
                <View style={s.copy}>
                  <Text style={s.title}>{item.title} {item.isDefault ? "(Varsayılan)" : ""}</Text>
                  <Text style={s.name}>{item.firstName} {item.lastName}</Text>
                  <Text style={s.meta}>{item.phone || "Telefon yok"}</Text>
                  <Text style={s.address}>{item.neighborhood ? `${item.neighborhood}, ` : ""}{item.district}/{item.city}</Text>
                  <Text style={s.address} numberOfLines={2}>{item.addressLine}</Text>
                </View>
              </View>
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
  card: { flexDirection: "row", alignItems: "flex-start", gap: 12, padding: 14, borderRadius: 18, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  iconShell: { width: 46, height: 46, alignItems: "center", justifyContent: "center", borderRadius: 15, backgroundColor: colors.accentSoft, marginTop: 2 },
  copy: { flex: 1, gap: 2 },
  title: { fontSize: 15, fontWeight: "900", color: colors.ink },
  name: { fontSize: 13, fontWeight: "800", color: colors.ink },
  meta: { fontSize: 12, color: colors.muted, fontWeight: "700" },
  address: { fontSize: 12, color: colors.muted, fontWeight: "700" },
  empty: { alignItems: "center", padding: 40 },
  emptyText: { color: colors.muted, fontWeight: "800" },
});
