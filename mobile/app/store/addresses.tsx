import { Ionicons } from "@expo/vector-icons";
import { useRouter, Stack, useFocusEffect } from "expo-router";
import { useCallback, useState } from "react";
import { ActivityIndicator, FlatList, Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { showAppError } from "../../src/components/AppAlert";
import { storeApi, type StoreAddress } from "../../src/lib/store-api";
import { colors } from "../../src/theme/colors";

export default function AddressesScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const [addresses, setAddresses] = useState<StoreAddress[]>([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState<StoreAddress>({ title: "", firstName: "", lastName: "", city: "", district: "", addressLine: "" });
  const [saving, setSaving] = useState(false);

  const load = useCallback(async () => {
    try {
      setAddresses(await storeApi.addresses());
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Adresler yüklenemedi");
    } finally {
      setLoading(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      void load();
    }, [load])
  );

  const save = async () => {
    if (!form.title || !form.firstName || !form.lastName || !form.city || !form.district || !form.addressLine) {
      showAppError("Lütfen zorunlu alanları doldurun");
      return;
    }
    setSaving(true);
    try {
      await storeApi.createAddress(form);
      setForm({ title: "", firstName: "", lastName: "", city: "", district: "", addressLine: "" });
      await load();
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Adres kaydedilemedi");
    } finally {
      setSaving(false);
    }
  };

  const remove = async (id: number) => {
    try {
      await storeApi.deleteAddress(id);
      await load();
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Adres silinemedi");
    }
  };

  return (
    <View style={s.screen}>
      <Stack.Screen options={{ title: "Adreslerim" }} />
      {loading ? (
        <ActivityIndicator color={colors.accent} style={{ marginTop: 60 }} />
      ) : (
        <FlatList
          data={addresses as StoreAddress[]}
          keyExtractor={(item: StoreAddress) => String(item.id)}
          contentContainerStyle={{ padding: 16, paddingBottom: 280 + insets.bottom }}
          renderItem={(info: any) => {
            const item = info.item as StoreAddress;
            return (
              <View style={s.card}>
                <View style={{ flexDirection: "row", justifyContent: "space-between" }}>
                  <Text style={s.cardTitle}>{item.title}{item.isDefault ? " (Varsayılan)" : ""}</Text>
                  <Pressable onPress={() => item.id && remove(item.id)}><Ionicons name="trash-outline" size={20} color={colors.danger} /></Pressable>
                </View>
                <Text style={s.cardText}>{item.firstName} {item.lastName}</Text>
                <Text style={s.cardText}>{item.city}, {item.district}</Text>
                <Text style={s.cardText}>{item.addressLine}</Text>
              </View>
            );
          }}
          ListHeaderComponent={
            <View style={{ gap: 10 }}>
              <Text style={s.sectionTitle}>Yeni Adres</Text>
              <TextInput style={s.input} placeholder="Adres başlığı (Ev, İş...)" placeholderTextColor={colors.muted} value={form.title} onChangeText={(t) => setForm({ ...form, title: t })} />
              <View style={{ flexDirection: "row", gap: 10 }}>
                <TextInput style={[s.input, { flex: 1 }]} placeholder="Ad" placeholderTextColor={colors.muted} value={form.firstName} onChangeText={(t) => setForm({ ...form, firstName: t })} />
                <TextInput style={[s.input, { flex: 1 }]} placeholder="Soyad" placeholderTextColor={colors.muted} value={form.lastName} onChangeText={(t) => setForm({ ...form, lastName: t })} />
              </View>
              <View style={{ flexDirection: "row", gap: 10 }}>
                <TextInput style={[s.input, { flex: 1 }]} placeholder="İl" placeholderTextColor={colors.muted} value={form.city} onChangeText={(t) => setForm({ ...form, city: t })} />
                <TextInput style={[s.input, { flex: 1 }]} placeholder="İlçe" placeholderTextColor={colors.muted} value={form.district} onChangeText={(t) => setForm({ ...form, district: t })} />
              </View>
              <TextInput style={s.input} placeholder="Adres" placeholderTextColor={colors.muted} value={form.addressLine} onChangeText={(t) => setForm({ ...form, addressLine: t })} />
              <TextInput style={s.input} placeholder="Telefon" placeholderTextColor={colors.muted} value={form.phone || ""} onChangeText={(t) => setForm({ ...form, phone: t })} />
              <Pressable onPress={() => setForm({ ...form, isDefault: !form.isDefault })} style={{ flexDirection: "row", alignItems: "center", gap: 8 }}>
                <Ionicons name={form.isDefault ? "checkbox" : "square-outline"} size={22} color={colors.action} />
                <Text style={{ color: colors.ink, fontWeight: "800" }}>Varsayılan adres olarak kaydet</Text>
              </Pressable>
              <Pressable disabled={saving} style={[s.saveBtn, saving && s.disabled]} onPress={save}>
                <Text style={s.saveBtnText}>{saving ? "Kaydediliyor..." : "Adresi Kaydet"}</Text>
              </Pressable>
              <Text style={s.sectionTitle}>Kayıtlı Adresler</Text>
            </View>
          }
        />
      )}
    </View>
  );
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  sectionTitle: { fontSize: 16, fontWeight: "900", color: colors.ink, marginTop: 8 },
  input: { height: 48, paddingHorizontal: 12, borderRadius: 12, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, color: colors.ink },
  saveBtn: { height: 50, borderRadius: 99, backgroundColor: colors.action, alignItems: "center", justifyContent: "center", marginTop: 4 },
  saveBtnText: { color: "#fff", fontSize: 15, fontWeight: "900" },
  disabled: { opacity: 0.6 },
  card: { padding: 14, borderRadius: 14, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, marginBottom: 10 },
  cardTitle: { fontSize: 14, fontWeight: "900", color: colors.ink },
  cardText: { fontSize: 13, color: colors.muted },
});
