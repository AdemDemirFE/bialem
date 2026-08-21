import { useRouter } from "expo-router";
import { useCallback, useEffect, useMemo, useState } from "react";
import {
  ActivityIndicator,
  FlatList,
  Pressable,
  RefreshControl,
  StyleSheet,
  Text,
  View
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useAuth } from "../../src/lib/auth";
import { useScreenInsets } from "../../src/lib/safeArea";
import { getNotificationTarget } from "../../src/lib/notifications";
import { addForegroundNotificationListener } from "../../src/lib/pushNotifications";
import {
  getNotifications,
  markNotificationAsRead,
  markAllNotificationsAsRead,
  sendTestNotification,
  deleteNotification,
  type AppNotification,
  type NotificationFilter
} from "../../src/lib/notificationApi";
import { colors } from "../../src/theme/colors";
import { showAppConfirmDelete, showAppSuccess, showAppError } from "../../src/components/AppAlert";

const PAGE_SIZE = 20;

type FilterValue = "ALL" | "UNREAD" | "READ";

const notificationFilters: { value: FilterValue; label: string }[] = [
  { value: "ALL", label: "Tümü" },
  { value: "UNREAD", label: "Okunmamış" },
  { value: "READ", label: "Okunan" }
];

export default function NotificationsScreen() {
  const insets = useScreenInsets();
  const { user } = useAuth();
  const router = useRouter();
  const [items, setItems] = useState<AppNotification[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [loadingMore, setLoadingMore] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<FilterValue>("ALL");
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  const loadNotifications = useCallback(
    async (refresh = false, nextPage = 0) => {
      if (!user) return;

      if (refresh) {
        setRefreshing(true);
      } else if (nextPage === 0) {
        setLoading(true);
      } else {
        setLoadingMore(true);
      }
      setError(null);

      try {
        const data = await getNotifications(filter, nextPage, PAGE_SIZE);
        if (refresh || nextPage === 0) {
          setItems(data.content);
          setPage(0);
        } else {
          setItems((current) => [...current, ...data.content]);
          setPage(nextPage);
        }
        setHasMore(data.content.length === PAGE_SIZE && nextPage + 1 < data.totalPages);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Bildirimler yüklenemedi");
      }

      setLoading(false);
      setRefreshing(false);
      setLoadingMore(false);
    },
    [filter, user?.id]
  );

  useEffect(() => {
    void loadNotifications(true, 0);
  }, [filter, user?.id]);

  useEffect(() => {
    return addForegroundNotificationListener(() => void loadNotifications(true, 0));
  }, [loadNotifications]);

  const loadMore = () => {
    if (!loadingMore && hasMore) {
      void loadNotifications(false, page + 1);
    }
  };

  const markAllAsRead = async () => {
    if (!user || items.every((item) => item.read)) return;

    try {
      await markAllNotificationsAsRead();
      setItems((current) => current.map((item) => ({ ...item, read: true })));
    } catch (err) {
      setError(err instanceof Error ? err.message : "İşlem başarısız");
    }
  };

  const openNotification = async (item: AppNotification) => {
    if (!item.read) {
      try {
        await markNotificationAsRead(item.id);
        setItems((current) => current.map((entry) => (entry.id === item.id ? { ...entry, read: true } : entry)));
      } catch (err) {
        console.warn("Failed to mark notification as read", err);
      }
    }

    const target = item.route ?? getNotificationTarget({ route: item.route ?? undefined });
    if (target) router.push(target as never);
  };

  const handleTestNotification = async () => {
    try {
      await sendTestNotification();
      await loadNotifications(true, 0);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Test bildirimi gönderilemedi");
    }
  };

  const removeNotification = async (id: number) => {
    const confirmed = await showAppConfirmDelete("Bu bildirimi silmek istediğinize emin misiniz?");
    if (!confirmed) return;

    try {
      await deleteNotification(id);
      setItems((current) => current.filter((item) => item.id !== id));
      void showAppSuccess("Bildirim silindi.");
    } catch (err) {
      const message = err instanceof Error ? err.message : "Bildirim silinemedi";
      setError(message);
      void showAppError(message);
    }
  };

  const unreadCount = useMemo(() => items.filter((item) => !item.read).length, [items]);

  const renderItem = ({ item }: { item: AppNotification }) => {
    const hasTarget = Boolean(item.route);
    return (
      <Pressable
        style={[styles.card, !item.read && styles.unreadCard]}
        onPress={() => void openNotification(item)}
      >
        <View style={[styles.typeMark, !item.read && styles.unreadMark]}>
          <Text style={styles.typeMarkText}>{getTypeMark(item.notificationType ?? "GENERIC")}</Text>
        </View>
        <View style={styles.cardBody}>
          <View style={styles.cardHeader}>
            <Text style={styles.cardTitle}>{item.title}</Text>
            {!item.read ? <View style={styles.unreadDot} /> : null}
          </View>
          {item.body ? <Text style={styles.cardText}>{item.body}</Text> : null}
          <Text style={styles.cardMeta}>
            {formatRelativeDate(item.createdAt)}{hasTarget ? "  ·  Detayı aç" : ""}
          </Text>
        </View>
        <Pressable
          style={styles.deleteButton}
          onPress={() => void removeNotification(item.id)}
          hitSlop={8}
        >
          <Ionicons name="trash-outline" size={16} color={colors.muted} />
        </Pressable>
      </Pressable>
    );
  };

  const ListHeader = () => (
    <>
      <View style={styles.hero}>
        <View style={styles.heroText}>
          <Text style={styles.kicker}>Bildirim Merkezi</Text>
          <Text style={styles.title}>{unreadCount > 0 ? `${unreadCount} yeni bildirimin var` : "Her şey güncel"}</Text>
          <Text style={styles.description}>Etkinlik, yorum ve topluluk hareketlerini tek yerden takip et.</Text>
        </View>
        <View style={styles.heroActions}>
          {unreadCount > 0 ? (
            <Pressable style={styles.readAllButton} onPress={() => void markAllAsRead()}>
              <Text style={styles.readAllText}>Tümünü okundu yap</Text>
            </Pressable>
          ) : null}
          <Pressable style={styles.testButton} onPress={() => void handleTestNotification()}>
            <Text style={styles.testButtonText}>Test bildirimi gönder</Text>
          </Pressable>
        </View>
      </View>

      <View style={styles.filters}>
        {notificationFilters.map((item) => {
          const selected = filter === item.value;
          return (
            <Pressable key={item.value} style={[styles.filter, selected && styles.filterActive]} onPress={() => setFilter(item.value)}>
              <Text style={[styles.filterText, selected && styles.filterTextActive]}>{item.label}</Text>
            </Pressable>
          );
        })}
      </View>

      {error ? <Text style={styles.error}>{error}</Text> : null}
    </>
  );

  return (
    <FlatList
      data={items}
      keyExtractor={(item) => String(item.id)}
      renderItem={renderItem}
      contentContainerStyle={[styles.page, { paddingBottom: insets.bottom + 24 }]}
      ListHeaderComponent={<ListHeader />}
      ListFooterComponent={
        loadingMore ? (
          <View style={styles.footerLoader}>
            <ActivityIndicator color={colors.accent} />
          </View>
        ) : null
      }
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadNotifications(true, 0)} tintColor={colors.accent} />}
      onEndReached={loadMore}
      onEndReachedThreshold={0.5}
      ListEmptyComponent={
        loading ? (
          <View style={styles.stateCard}>
            <ActivityIndicator color={colors.accent} />
            <Text style={styles.stateText}>Bildirimler yükleniyor...</Text>
          </View>
        ) : (
          <View style={styles.stateCard}>
            <Text style={styles.emptyMark}>B</Text>
            <Text style={styles.emptyTitle}>Henüz bildirim yok</Text>
            <Text style={styles.stateText}>Yeni bir hareket olduğunda burada göreceksin.</Text>
          </View>
        )
      }
    />
  );
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
  heroActions: { flexDirection: "row", flexWrap: "wrap", gap: 10 },
  readAllButton: { alignSelf: "flex-start", backgroundColor: colors.action, borderRadius: 999, paddingHorizontal: 16, paddingVertical: 11 },
  readAllText: { color: colors.actionText, fontSize: 13, fontWeight: "800" },
  testButton: { alignSelf: "flex-start", backgroundColor: colors.accentSoft, borderRadius: 999, paddingHorizontal: 16, paddingVertical: 11 },
  testButtonText: { color: colors.accent, fontSize: 13, fontWeight: "800" },
  error: { color: colors.danger, fontSize: 14, fontWeight: "600" },
  filters: { flexDirection: "row", flexWrap: "wrap", alignItems: "center", gap: 8, flexGrow: 0, flexShrink: 0 },
  filter: {
    height: 32,
    paddingHorizontal: 12,
    borderRadius: 999,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface,
    alignItems: "center",
    justifyContent: "center",
    alignSelf: "flex-start"
  },
  filterActive: { borderColor: colors.brandInk, backgroundColor: colors.brandInk },
  filterText: { color: colors.ink, fontSize: 12, fontWeight: "800" },
  filterTextActive: { color: "#fff" },
  card: { flexDirection: "row", gap: 13, padding: 16, borderRadius: 22, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, marginBottom: 11 },
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
  deleteButton: { width: 32, height: 32, borderRadius: 16, alignItems: "center", justifyContent: "center", backgroundColor: colors.surfaceStrong, marginLeft: 8 },
  stateCard: { minHeight: 230, padding: 24, gap: 10, alignItems: "center", justifyContent: "center", borderRadius: 28, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  emptyMark: { width: 58, height: 58, borderRadius: 22, textAlign: "center", textAlignVertical: "center", backgroundColor: colors.action, color: colors.ink, fontSize: 24, fontWeight: "900" },
  emptyTitle: { color: colors.ink, fontSize: 20, fontWeight: "800" },
  stateText: { color: colors.muted, fontSize: 14, lineHeight: 20, textAlign: "center" },
  footerLoader: { paddingVertical: 16, alignItems: "center" }
});
