import { Ionicons } from "@expo/vector-icons";
import { Link, Stack } from "expo-router";
import { useEffect, useState } from "react";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { Reveal, Skeleton } from "../../src/animations";
import { showAppError } from "../../src/components/AppAlert";
import { storeManagementApi, type StoreManagementDashboard } from "../../src/lib/store-management-api";
import { colors } from "../../src/theme/colors";

export default function StoreManagementScreen() {
  const [dashboard, setDashboard] = useState<StoreManagementDashboard | null>(null);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    try {
      setDashboard(await storeManagementApi.dashboard());
    } catch (e) {
      showAppError(e instanceof Error ? e.message : "Mağaza verileri yüklenemedi");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load();
  }, []);

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: "Mağaza Yönetimi" }} />
      <ScrollView style={s.screen} contentContainerStyle={s.page}>
        <Reveal>
        <View style={s.hero}>
          <Text style={s.kicker}>MAĞAZA</Text>
          <Text style={s.title}>Yönetim Merkezi</Text>
          <Text style={s.subtitle}>Ürün, sipariş, kargo ve müşteri süreçlerini yönetin.</Text>
        </View>
        </Reveal>

        {loading ? (
          <View style={{ gap: 10 }}>
            <Skeleton height={110} borderRadius={18} />
            <Skeleton height={200} borderRadius={20} />
          </View>
        ) : dashboard ? (
          <Reveal index={1}>
          <View style={s.statsGrid}>
            <Stat value={dashboard.productCount} label="Ürün" icon="cube-outline" href="/management/store/products" />
            <Stat value={dashboard.categoryCount} label="Kategori" icon="list-outline" href="/management/store/categories" />
            <Stat value={dashboard.brandCount} label="Marka" icon="pricetag-outline" href="/management/store/brands" />
            <Stat value={dashboard.orderCount} label="Sipariş" icon="cart-outline" href="/management/store/orders" />
            <Stat value={dashboard.pendingOrderCount} label="Bekleyen" icon="time-outline" href="/management/store/orders" />
            <Stat value={dashboard.shippingCount} label="Kargo" icon="bus-outline" href="/management/store/shipments" />
            <Stat value={dashboard.addressCount} label="Adres" icon="location-outline" href="/management/store/addresses" />
            <Stat value={dashboard.reviewCount} label="Yorum" icon="chatbubble-outline" href="/management/store/reviews" />
          </View>
          </Reveal>
        ) : null}

        <Text style={s.section}>SÜREÇLER</Text>
        <Reveal index={2}>
        <View style={s.menu}>
          <MenuItem href="/management/store/products" icon="cube-outline" title="Ürünler" subtitle="Ürün listesi, ekleme, düzenleme ve silme" />
          <MenuItem href="/management/store/categories" icon="list-outline" title="Kategoriler" subtitle="Kategori hiyerarşisi yönetimi" />
          <MenuItem href="/management/store/brands" icon="pricetag-outline" title="Markalar" subtitle="Mağaza markaları" />
          <MenuItem href="/management/store/orders" icon="cart-outline" title="Siparişler" subtitle="Sipariş durumları ve onay süreçleri" />
          <MenuItem href="/management/store/shipments" icon="bus-outline" title="Kargolar" subtitle="Kargo takibi ve durum güncelleme" />
          <MenuItem href="/management/store/addresses" icon="location-outline" title="Adresler" subtitle="Kayıtlı kullanıcı adresleri" />
          <MenuItem href="/management/store/reviews" icon="chatbubble-outline" title="Yorumlar" subtitle="Ürün değerlendirmeleri moderasyonu" />
        </View>
        </Reveal>
      </ScrollView>
    </>
  );
}

function Stat({ value, label, icon, href }: { value: number; label: string; icon: keyof typeof Ionicons.glyphMap; href: string }) {
  return (
    <Link href={href as never} asChild>
      <Pressable style={s.stat}>
        <Ionicons name={icon} size={22} color={colors.accent} />
        <Text style={s.statValue}>{value}</Text>
        <Text style={s.statLabel}>{label}</Text>
      </Pressable>
    </Link>
  );
}

function MenuItem({ href, icon, title, subtitle }: { href: string; icon: keyof typeof Ionicons.glyphMap; title: string; subtitle: string }) {
  return (
    <Link href={href as never} asChild>
      <Pressable style={s.row}>
        <View style={s.iconShell}>
          <Ionicons name={icon} size={22} color={colors.accent} />
        </View>
        <View style={{ flex: 1 }}>
          <Text style={s.rowTitle}>{title}</Text>
          <Text style={s.muted}>{subtitle}</Text>
        </View>
        <Ionicons name="chevron-forward" size={20} color={colors.muted} />
      </Pressable>
    </Link>
  );
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  page: { padding: 16, paddingBottom: 40, gap: 14 },
  hero: { padding: 20, borderRadius: 22, backgroundColor: colors.brandInk, gap: 5 },
  kicker: { color: colors.action, fontSize: 11, fontWeight: "900", letterSpacing: 1.3 },
  title: { color: colors.onBrand, fontSize: 28, fontWeight: "900" },
  subtitle: { color: colors.onBrandMuted, fontSize: 14 },
  statsGrid: { flexDirection: "row", flexWrap: "wrap", gap: 10 },
  stat: { width: "23%", minWidth: 76, alignItems: "center", gap: 4, padding: 12, borderRadius: 18, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  statValue: { fontSize: 20, fontWeight: "900", color: colors.ink },
  statLabel: { fontSize: 11, fontWeight: "800", color: colors.muted },
  section: { marginTop: 4, color: colors.muted, fontSize: 11, fontWeight: "900", letterSpacing: 1.2 },
  menu: { borderRadius: 20, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, overflow: "hidden" },
  row: { minHeight: 72, flexDirection: "row", alignItems: "center", gap: 12, padding: 13, borderBottomWidth: 1, borderBottomColor: colors.border },
  iconShell: { width: 44, height: 44, alignItems: "center", justifyContent: "center", borderRadius: 15, backgroundColor: colors.accentSoft },
  rowTitle: { color: colors.ink, fontSize: 15, fontWeight: "900" },
  muted: { color: colors.muted, fontSize: 12 },
});
