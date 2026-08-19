import { Ionicons } from "@expo/vector-icons";
import { Link, Stack } from "expo-router";
import { useEffect, useMemo, useState } from "react";
import { ActivityIndicator, Image, Pressable, RefreshControl, ScrollView, StyleSheet, Text, View } from "react-native";
import { api } from "../src/lib/api";
import { colors } from "../src/theme/colors";

type PlanFilter = "upcoming" | "past" | "all";

type ProfilePlan = {
  event_id: string;
  title: string;
  starts_at: string;
  ends_at: string | null;
  location_name: string | null;
  cover_image_url: string | null;
  event_status: string;
  participation_status: string;
  community_name: string;
};

const filters: { value: PlanFilter; label: string }[] = [
  { value: "upcoming", label: "Yaklaşan" },
  { value: "past", label: "Geçmiş" },
  { value: "all", label: "Tümü" }
];

export default function MyPlansScreen() {
  const [plans, setPlans] = useState<ProfilePlan[]>([]);
  const [filter, setFilter] = useState<PlanFilter>("upcoming");
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = async (refresh = false) => {
    refresh ? setRefreshing(true) : setLoading(true);
    setError(null);

    const result = await api.rpc("get_my_profile_plans");
    if (result.error) {
      setError("Planların şu anda yüklenemedi. Lütfen tekrar dene.");
    } else {
      setPlans((result.data ?? []) as ProfilePlan[]);
    }

    setLoading(false);
    setRefreshing(false);
  };

  useEffect(() => {
    void load();
  }, []);

  const visiblePlans = useMemo(() => {
    const now = Date.now();
    return plans
      .filter((plan) => {
        if (filter === "all") return true;
        const startsAt = new Date(plan.starts_at).getTime();
        return filter === "upcoming" ? startsAt >= now : startsAt < now;
      })
      .sort((a, b) => {
        const difference = new Date(a.starts_at).getTime() - new Date(b.starts_at).getTime();
        return filter === "past" ? -difference : difference;
      });
  }, [filter, plans]);

  const groupedPlans = useMemo(() => {
    const groups = new Map<string, ProfilePlan[]>();
    visiblePlans.forEach((plan) => {
      const key = new Date(plan.starts_at).toLocaleDateString("tr-TR", { month: "long", year: "numeric" });
      groups.set(key, [...(groups.get(key) ?? []), plan]);
    });
    return Array.from(groups.entries());
  }, [visiblePlans]);

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: "Planlarım" }} />
      <ScrollView
        contentContainerStyle={styles.page}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void load(true)} tintColor={colors.accent} />}
      >
        <View style={styles.hero}>
          <View style={styles.heroIcon}>
            <Ionicons name="calendar" size={24} color={colors.actionText} />
          </View>
          <Text style={styles.kicker}>Kişisel Takvim</Text>
          <Text style={styles.title}>Katılacağın anları tek yerde gör.</Text>
          <Text style={styles.description}>Onaylanan, bekleyen ve geçmiş etkinlik planlarını ay ay takip et.</Text>
        </View>

        <View style={styles.filters}>
          {filters.map((item) => {
            const selected = filter === item.value;
            return (
              <Pressable key={item.value} style={[styles.filter, selected && styles.filterActive]} onPress={() => setFilter(item.value)}>
                <Text style={[styles.filterText, selected && styles.filterTextActive]}>{item.label}</Text>
              </Pressable>
            );
          })}
        </View>

        {loading ? (
          <View style={styles.stateCard}>
            <ActivityIndicator color={colors.accent} />
            <Text style={styles.stateText}>Planların hazırlanıyor...</Text>
          </View>
        ) : error ? (
          <View style={styles.stateCard}>
            <Text style={styles.errorText}>{error}</Text>
          </View>
        ) : groupedPlans.length === 0 ? (
          <View style={styles.stateCard}>
            <Ionicons name="calendar-outline" size={38} color={colors.accent} />
            <Text style={styles.stateTitle}>Bu bölümde henüz plan yok.</Text>
            <Text style={styles.stateText}>Keşfet ekranından yeni etkinliklere katıldığında takvimin burada oluşacak.</Text>
            <Link href="/(tabs)/feed" asChild>
              <Pressable style={styles.primaryButton}>
                <Text style={styles.primaryButtonText}>Etkinlikleri keşfet</Text>
              </Pressable>
            </Link>
          </View>
        ) : (
          <View style={styles.groups}>
            {groupedPlans.map(([month, monthPlans]) => (
              <View key={month} style={styles.group}>
                <Text style={styles.month}>{month}</Text>
                {monthPlans.map((plan) => (
                  <Link key={plan.event_id} href={{ pathname: "/event/[id]", params: { id: plan.event_id } }} asChild>
                    <Pressable style={styles.planCard}>
                      {plan.cover_image_url ? (
                        <Image source={{ uri: plan.cover_image_url }} style={styles.cover} resizeMode="cover" />
                      ) : (
                        <View style={styles.coverFallback}>
                          <Text style={styles.day}>{new Date(plan.starts_at).getDate()}</Text>
                          <Text style={styles.dayMonth}>{new Date(plan.starts_at).toLocaleDateString("tr-TR", { month: "short" })}</Text>
                        </View>
                      )}
                      <View style={styles.planCopy}>
                        <Text style={styles.community} numberOfLines={1}>{plan.community_name}</Text>
                        <Text style={styles.planTitle} numberOfLines={2}>{plan.title}</Text>
                        <Text style={styles.planMeta}>{formatPlanDate(plan.starts_at)}</Text>
                        <Text style={styles.planMeta} numberOfLines={1}>{plan.location_name || "Mekân bilgisi etkinlikte"}</Text>
                        <View style={styles.statusBadge}>
                          <Text style={styles.statusText}>{participationLabel(plan.participation_status)}</Text>
                        </View>
                      </View>
                      <Ionicons name="chevron-forward" size={20} color={colors.muted} />
                    </Pressable>
                  </Link>
                ))}
              </View>
            ))}
          </View>
        )}
      </ScrollView>
    </>
  );
}

function formatPlanDate(value: string) {
  return new Date(value).toLocaleString("tr-TR", {
    weekday: "short",
    day: "2-digit",
    month: "short",
    hour: "2-digit",
    minute: "2-digit"
  });
}

function participationLabel(status: string) {
  if (status === "approved") return "Katılım onaylandı";
  if (status === "checked_in") return "Katıldın";
  if (status === "waitlisted") return "Bekleme listesinde";
  return "Onay bekliyor";
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, gap: 18, padding: 20, paddingBottom: 48, backgroundColor: colors.page },
  hero: { gap: 9, padding: 22, borderRadius: 30, backgroundColor: colors.brandInk },
  heroIcon: { width: 48, height: 48, alignItems: "center", justifyContent: "center", borderRadius: 17, backgroundColor: colors.action },
  kicker: { color: colors.action, fontSize: 12, fontWeight: "900", letterSpacing: 1.3, textTransform: "uppercase" },
  title: { color: "#fff", fontSize: 30, lineHeight: 35, fontWeight: "900", letterSpacing: -0.8 },
  description: { color: "#cbd6ef", fontSize: 14, lineHeight: 21 },
  filters: { flexDirection: "row", gap: 8 },
  filter: { flex: 1, alignItems: "center", paddingVertical: 11, borderRadius: 999, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  filterActive: { borderColor: colors.brandInk, backgroundColor: colors.brandInk },
  filterText: { color: colors.ink, fontSize: 12, fontWeight: "800" },
  filterTextActive: { color: "#fff" },
  groups: { gap: 22 },
  group: { gap: 11 },
  month: { color: colors.ink, fontSize: 20, fontWeight: "900", textTransform: "capitalize" },
  planCard: { flexDirection: "row", alignItems: "center", gap: 13, padding: 13, borderRadius: 24, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  cover: { width: 82, height: 104, borderRadius: 17, backgroundColor: colors.surfaceStrong },
  coverFallback: { width: 82, height: 104, alignItems: "center", justifyContent: "center", borderRadius: 17, backgroundColor: colors.accentSoft },
  day: { color: colors.ink, fontSize: 28, fontWeight: "900" },
  dayMonth: { color: colors.accent, fontSize: 12, fontWeight: "900", textTransform: "uppercase" },
  planCopy: { flex: 1, gap: 3 },
  community: { color: colors.accent, fontSize: 10, fontWeight: "900", textTransform: "uppercase", letterSpacing: 0.8 },
  planTitle: { color: colors.ink, fontSize: 17, lineHeight: 21, fontWeight: "900" },
  planMeta: { color: colors.muted, fontSize: 12, lineHeight: 17 },
  statusBadge: { alignSelf: "flex-start", marginTop: 4, paddingHorizontal: 9, paddingVertical: 5, borderRadius: 999, backgroundColor: colors.accentSoft },
  statusText: { color: colors.accent, fontSize: 10, fontWeight: "900" },
  stateCard: { minHeight: 240, alignItems: "center", justifyContent: "center", gap: 10, padding: 26, borderRadius: 28, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  stateTitle: { color: colors.ink, fontSize: 19, fontWeight: "900", textAlign: "center" },
  stateText: { color: colors.muted, fontSize: 14, lineHeight: 21, textAlign: "center" },
  errorText: { color: colors.danger, fontSize: 14, lineHeight: 21, fontWeight: "700", textAlign: "center" },
  primaryButton: { marginTop: 7, paddingHorizontal: 18, paddingVertical: 12, borderRadius: 999, backgroundColor: colors.action },
  primaryButtonText: { color: colors.actionText, fontSize: 13, fontWeight: "900" }
});
