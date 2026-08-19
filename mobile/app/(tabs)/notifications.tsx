import { useRouter } from "expo-router";
import { useEffect, useMemo, useState } from "react";
import { ActivityIndicator, Pressable, RefreshControl, ScrollView, StyleSheet, Text, View } from "react-native";
import { useAuth } from "../../src/lib/auth";
import { getNotificationTarget } from "../../src/lib/notifications";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";

type NotificationPayload = {
  event_id?: string;
  post_id?: string;
  user_id?: string;
  community_id?: string;
  follow_request_id?: string;
};

type NotificationItem = {
  id: string;
  type: string;
  title: string;
  body: string | null;
  payload: NotificationPayload;
  is_read: boolean;
  created_at: string;
};

type NotificationFilter = "all" | "unread" | "events" | "communities" | "social" | "advantages";

const notificationFilters: { value: NotificationFilter; label: string }[] = [
  { value: "all", label: "Tümü" },
  { value: "unread", label: "Okunmamış" },
  { value: "events", label: "Etkinlik" },
  { value: "communities", label: "Topluluk" },
  { value: "social", label: "Sosyal" },
  { value: "advantages", label: "Avantaj" }
];

export default function NotificationsScreen() {
  const { user } = useAuth();
  const router = useRouter();
  const [items, setItems] = useState<NotificationItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<NotificationFilter>("all");

  const loadNotifications = async (refresh = false) => {
    if (!user) return;

    refresh ? setRefreshing(true) : setLoading(true);
    setError(null);

    const result = await api
      .from("notifications")
      .select("id, type, title, body, payload, is_read, created_at")
      .eq("user_id", user.id)
      .order("created_at", { ascending: false })
      .limit(50);

    if (result.error) {
      setError(result.error.message);
    } else {
      setItems((result.data ?? []) as NotificationItem[]);
    }

    setLoading(false);
    setRefreshing(false);
  };

  useEffect(() => {
    void loadNotifications();
  }, [user?.id]);

  const markAllAsRead = async () => {
    if (!user || items.every((item) => item.is_read)) return;

    const result = await api.from("notifications").update({ is_read: true }).eq("user_id", user.id).eq("is_read", false);

    if (result.error) {
      setError(result.error.message);
      return;
    }

    setItems((current) => current.map((item) => ({ ...item, is_read: true })));
  };

  const openNotification = async (item: NotificationItem) => {
    if (!item.is_read) {
      await api.from("notifications").update({ is_read: true }).eq("id", item.id);
      setItems((current) => current.map((entry) => (entry.id === item.id ? { ...entry, is_read: true } : entry)));
    }

    const target = getNotificationTarget(item.payload);
    if (target) router.push(target as never);
  };

  const unreadCount = items.filter((item) => !item.is_read).length;
  const visibleItems = useMemo(
    () =>
      items.filter((item) => {
        if (filter === "all") return true;
        if (filter === "unread") return !item.is_read;
        return getNotificationCategory(item.type) === filter;
      }),
    [filter, items]
  );

  return (
    <ScrollView
      contentContainerStyle={styles.page}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadNotifications(true)} tintColor={colors.accent} />}
    >
      <View style={styles.hero}>
        <View style={styles.heroText}>
          <Text style={styles.kicker}>Bildirim Merkezi</Text>
          <Text style={styles.title}>{unreadCount > 0 ? `${unreadCount} yeni bildirimin var` : "Her şey güncel"}</Text>
          <Text style={styles.description}>Etkinlik, yorum ve topluluk hareketlerini tek yerden takip et.</Text>
        </View>
        {unreadCount > 0 ? (
          <Pressable style={styles.readAllButton} onPress={() => void markAllAsRead()}>
            <Text style={styles.readAllText}>Tümünü okundu yap</Text>
          </Pressable>
        ) : null}
      </View>

      <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.filters}>
        {notificationFilters.map((item) => {
          const selected = filter === item.value;
          return (
            <Pressable key={item.value} style={[styles.filter, selected && styles.filterActive]} onPress={() => setFilter(item.value)}>
              <Text style={[styles.filterText, selected && styles.filterTextActive]}>{item.label}</Text>
            </Pressable>
          );
        })}
      </ScrollView>

      {error ? <Text style={styles.error}>{error}</Text> : null}
      {loading ? (
        <View style={styles.stateCard}>
          <ActivityIndicator color={colors.accent} />
          <Text style={styles.stateText}>Bildirimler yükleniyor...</Text>
        </View>
      ) : visibleItems.length === 0 ? (
        <View style={styles.stateCard}>
          <Text style={styles.emptyMark}>B</Text>
          <Text style={styles.emptyTitle}>{items.length === 0 ? "Henüz bildirim yok" : "Bu filtrede bildirim yok"}</Text>
          <Text style={styles.stateText}>{items.length === 0 ? "Yeni bir hareket olduğunda burada göreceksin." : "Başka bir filtre seçerek bildirimlerini inceleyebilirsin."}</Text>
        </View>
      ) : (
        <View style={styles.list}>
          {visibleItems.map((item) => {
            const hasTarget = Boolean(getNotificationTarget(item.payload));
            return (
              <Pressable
                key={item.id}
                style={[styles.card, !item.is_read && styles.unreadCard]}
                onPress={() => void openNotification(item)}
              >
                <View style={[styles.typeMark, !item.is_read && styles.unreadMark]}>
                  <Text style={styles.typeMarkText}>{getTypeMark(item.type)}</Text>
                </View>
                <View style={styles.cardBody}>
                  <View style={styles.cardHeader}>
                    <Text style={styles.cardTitle}>{item.title}</Text>
                    {!item.is_read ? <View style={styles.unreadDot} /> : null}
                  </View>
                  {item.body ? <Text style={styles.cardText}>{item.body}</Text> : null}
                  <Text style={styles.cardMeta}>
                    {formatRelativeDate(item.created_at)}{hasTarget ? "  ·  Detayı aç" : ""}
                  </Text>
                </View>
              </Pressable>
            );
          })}
        </View>
      )}
    </ScrollView>
  );
}

function getNotificationCategory(type: string): Exclude<NotificationFilter, "all" | "unread"> | "system" {
  const normalized = type.toLowerCase();
  if (normalized.includes("advantage") || normalized.includes("offer") || normalized.includes("partner")) return "advantages";
  if (normalized.includes("event") || normalized.includes("participation") || normalized.includes("waitlist")) return "events";
  if (normalized.includes("community") || normalized.includes("group") || normalized.includes("moderator")) return "communities";
  if (normalized.includes("follow") || normalized.includes("comment") || normalized.includes("review") || normalized.includes("post") || normalized.includes("story")) return "social";
  return "system";
}

function getTypeMark(type: string) {
  if (type.includes("event")) return "E";
  if (type.includes("comment")) return "Y";
  if (type.includes("review")) return "P";
  if (type.includes("community")) return "T";
  return "B";
}

function formatRelativeDate(value: string) {
  const elapsed = Date.now() - new Date(value).getTime();
  const minutes = Math.max(1, Math.floor(elapsed / 60000));
  if (minutes < 60) return `${minutes} dk önce`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours} sa önce`;
  const days = Math.floor(hours / 24);
  if (days < 7) return `${days} gün önce`;
  return new Date(value).toLocaleDateString("tr-TR", { day: "2-digit", month: "short", year: "numeric" });
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 22, gap: 18, backgroundColor: colors.page },
  hero: { marginTop: 8, gap: 16 },
  heroText: { gap: 8 },
  kicker: { color: colors.accent, fontSize: 13, fontWeight: "800", letterSpacing: 1.1, textTransform: "uppercase" },
  title: { color: colors.ink, fontSize: 30, lineHeight: 36, fontWeight: "800" },
  description: { color: colors.muted, fontSize: 15, lineHeight: 22 },
  readAllButton: { alignSelf: "flex-start", backgroundColor: colors.action, borderRadius: 999, paddingHorizontal: 16, paddingVertical: 11 },
  readAllText: { color: colors.actionText, fontSize: 13, fontWeight: "800" },
  error: { color: colors.danger, fontSize: 14, fontWeight: "600" },
  filters: { gap: 8, paddingRight: 12 },
  filter: { paddingHorizontal: 14, paddingVertical: 10, borderRadius: 999, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  filterActive: { borderColor: colors.brandInk, backgroundColor: colors.brandInk },
  filterText: { color: colors.ink, fontSize: 12, fontWeight: "800" },
  filterTextActive: { color: "#fff" },
  list: { gap: 11 },
  card: { flexDirection: "row", gap: 13, padding: 16, borderRadius: 22, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  unreadCard: { backgroundColor: colors.accentSoft, borderColor: colors.accent },
  typeMark: { width: 42, height: 42, borderRadius: 15, alignItems: "center", justifyContent: "center", backgroundColor: colors.surfaceStrong },
  unreadMark: { backgroundColor: colors.action },
  typeMarkText: { color: colors.ink, fontSize: 16, fontWeight: "900" },
  cardBody: { flex: 1, gap: 5 },
  cardHeader: { flexDirection: "row", alignItems: "center", gap: 8 },
  cardTitle: { flex: 1, color: colors.ink, fontSize: 16, lineHeight: 21, fontWeight: "800" },
  unreadDot: { width: 8, height: 8, borderRadius: 4, backgroundColor: colors.accent },
  cardText: { color: colors.muted, fontSize: 14, lineHeight: 20 },
  cardMeta: { color: colors.accent, fontSize: 12, fontWeight: "700", marginTop: 3 },
  stateCard: { minHeight: 230, padding: 24, gap: 10, alignItems: "center", justifyContent: "center", borderRadius: 28, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  emptyMark: { width: 58, height: 58, borderRadius: 22, textAlign: "center", textAlignVertical: "center", backgroundColor: colors.action, color: colors.ink, fontSize: 24, fontWeight: "900" },
  emptyTitle: { color: colors.ink, fontSize: 20, fontWeight: "800" },
  stateText: { color: colors.muted, fontSize: 14, lineHeight: 20, textAlign: "center" }
});
