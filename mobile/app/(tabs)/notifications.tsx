import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useCallback, useEffect, useMemo, useState } from "react";
import {
  ActivityIndicator,
  FlatList,
  Pressable,
  RefreshControl,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";
import { useAuth } from "../../src/lib/auth";
import { getNotificationTarget } from "../../src/lib/notifications";
import { addForegroundNotificationListener } from "../../src/lib/pushNotifications";
import {
  getNotifications,
  markNotificationAsRead,
  markAllNotificationsAsRead,
  type AppNotification,
  type NotificationFilter
} from "../../src/lib/notificationApi";
import { colors } from "../../src/theme/colors";

const PAGE_SIZE = 20;

type FilterValue = "ALL" | "UNREAD" | "READ";

const notificationFilters: { value: FilterValue; label: string }[] = [
  { value: "ALL", label: "Tümü" },
  { value: "UNREAD", label: "Okunmamış" },
  { value: "READ", label: "Okunan" }
];

export default function NotificationsScreen() {
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
  const [searchQuery, setSearchQuery] = useState("");

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
        const content = Array.isArray(data.content) ? data.content : [];
        if (refresh || nextPage === 0) {
          setItems(content);
          setPage(0);
        } else {
          setItems((current) => [...current, ...content]);
          setPage(nextPage);
        }
        setHasMore(content.length === PAGE_SIZE && nextPage + 1 < data.totalPages);
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

  const unreadCount = useMemo(() => items.filter((item) => !item.read).length, [items]);
  const visibleItems = useMemo(() => {
    const query = searchQuery.trim().toLocaleLowerCase("tr-TR");
    if (!query) return items;
    return items.filter((item) =>
      [item.title, item.body, item.notificationType]
        .filter((value): value is string => Boolean(value))
        .some((value) => value.toLocaleLowerCase("tr-TR").includes(query))
    );
  }, [items, searchQuery]);

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
      </Pressable>
    );
  };

  const ListHeader = () => (
    <>
      <View style={styles.hero}>
        <View style={styles.headingRow}>
          <View style={styles.heroText}>
            <Text style={styles.kicker}>Bildirim Merkezi</Text>
            <Text style={styles.title}>{unreadCount > 0 ? `${unreadCount} yeni bildirim` : "Her şey güncel"}</Text>
          </View>
          <Pressable accessibilityLabel="Mesajlarım" style={styles.messageAction} onPress={() => router.push("/messages" as never)}>
            <Ionicons name="chatbubbles-outline" size={20} color={colors.ink} />
            <Text style={styles.messageActionText}>Mesajlar</Text>
          </Pressable>
        </View>

        <View style={styles.searchInputWrap}>
          <Ionicons name="search-outline" size={18} color={colors.muted} />
          <TextInput value={searchQuery} onChangeText={setSearchQuery} placeholder="Bildirimlerde ara" placeholderTextColor={colors.muted} style={styles.searchInput} returnKeyType="search" />
          {searchQuery ? <Pressable style={styles.clearSearch} onPress={() => setSearchQuery("")}><Ionicons name="close-circle" size={19} color={colors.muted} /></Pressable> : null}
        </View>

        <View style={styles.summaryRow}>
          <Text style={styles.summaryText}>{searchQuery.trim() ? `${visibleItems.length} sonuç` : `${items.length} bildirim`}</Text>
          {unreadCount > 0 ? (
            <Pressable style={styles.readAllButton} onPress={() => void markAllAsRead()}>
              <Ionicons name="checkmark-done" size={15} color={colors.accent} />
              <Text style={styles.readAllText}>Tümünü oku</Text>
            </Pressable>
          ) : null}
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
      data={visibleItems}
      keyExtractor={(item) => String(item.id)}
      renderItem={renderItem}
      contentContainerStyle={styles.page}
      ListHeaderComponent={<ListHeader />}
      ListFooterComponent={
        loadingMore ? (
          <View style={styles.footerLoader}>
            <ActivityIndicator color={colors.accent} />
          </View>
        ) : null
      }
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadNotifications(true, 0)} tintColor={colors.accent} />}
      onEndReached={searchQuery.trim() ? undefined : loadMore}
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
            <Text style={styles.emptyTitle}>{searchQuery.trim() ? "Eşleşen bildirim bulunamadı" : "Henüz bildirim yok"}</Text>
            <Text style={styles.stateText}>{searchQuery.trim() ? "Farklı bir kelimeyle tekrar arayabilirsin." : "Yeni bir hareket olduğunda burada göreceksin."}</Text>
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
  page: { flexGrow: 1, padding: 16, gap: 13, backgroundColor: colors.page },
  hero: { marginTop: 4, gap: 12 },
  headingRow: { flexDirection: "row", alignItems: "center", gap: 12 },
  heroText: { flex: 1, gap: 3 },
  kicker: { color: colors.accent, fontSize: 10, fontWeight: "900", letterSpacing: 1, textTransform: "uppercase" },
  title: { color: colors.ink, fontSize: 24, lineHeight: 29, fontWeight: "900" },
  messageAction: { height: 36, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 6, paddingHorizontal: 11, borderRadius: 12, backgroundColor: colors.action },
  messageActionText: { color: colors.actionText, fontSize: 12, fontWeight: "900" },
  searchInputWrap: { minHeight: 44, flexDirection: "row", alignItems: "center", gap: 8, paddingHorizontal: 12, borderRadius: 14, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  searchInput: { flex: 1, color: colors.ink, fontSize: 14 },
  clearSearch: { padding: 2 },
  summaryRow: { minHeight: 28, flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  summaryText: { color: colors.muted, fontSize: 11, fontWeight: "700" },
  readAllButton: { flexDirection: "row", alignItems: "center", gap: 4, paddingHorizontal: 9, paddingVertical: 6, borderRadius: 10, backgroundColor: colors.accentSoft },
  readAllText: { color: colors.accent, fontSize: 11, fontWeight: "900" },
  error: { color: colors.danger, fontSize: 14, fontWeight: "600" },
  filters: { flexDirection: "row", flexWrap: "wrap", alignItems: "center", gap: 6, flexGrow: 0, flexShrink: 0 },
  filter: {
    height: 30,
    paddingHorizontal: 10,
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
  card: { flexDirection: "row", gap: 11, padding: 13, borderRadius: 17, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, marginBottom: 8 },
  unreadCard: { backgroundColor: colors.accentSoft, borderColor: colors.accent },
  typeMark: { width: 38, height: 38, borderRadius: 13, alignItems: "center", justifyContent: "center", backgroundColor: colors.surfaceStrong },
  unreadMark: { backgroundColor: colors.action },
  typeMarkText: { color: colors.ink, fontSize: 14, fontWeight: "900" },
  cardBody: { flex: 1, gap: 5 },
  cardHeader: { flexDirection: "row", alignItems: "center", gap: 8 },
  cardTitle: { flex: 1, color: colors.ink, fontSize: 15, lineHeight: 19, fontWeight: "800" },
  unreadDot: { width: 8, height: 8, borderRadius: 4, backgroundColor: colors.accent },
  cardText: { color: colors.muted, fontSize: 13, lineHeight: 18 },
  cardMeta: { color: colors.accent, fontSize: 11, fontWeight: "700", marginTop: 2 },
  stateCard: { minHeight: 230, padding: 24, gap: 10, alignItems: "center", justifyContent: "center", borderRadius: 28, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  emptyMark: { width: 58, height: 58, borderRadius: 22, textAlign: "center", textAlignVertical: "center", backgroundColor: colors.action, color: colors.ink, fontSize: 24, fontWeight: "900" },
  emptyTitle: { color: colors.ink, fontSize: 20, fontWeight: "800" },
  stateText: { color: colors.muted, fontSize: 14, lineHeight: 20, textAlign: "center" },
  footerLoader: { paddingVertical: 16, alignItems: "center" }
});
