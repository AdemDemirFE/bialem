import { Ionicons } from "@expo/vector-icons";
import { Link, Stack, useLocalSearchParams, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, FlatList, Image, Pressable, RefreshControl, StyleSheet, Text, TextInput, View } from "react-native";
import { api } from "../../../src/lib/api";
import { colors } from "../../../src/theme/colors";

type CommunityMember = {
  user_id: string;
  display_name: string;
  username: string;
  avatar_url: string | null;
  city: string | null;
  is_verified: boolean;
  member_role: "owner" | "manager" | "member";
  joined_at: string;
};

const PAGE_SIZE = 30;

function roleLabel(role: CommunityMember["member_role"]) {
  return role === "owner" || role === "manager" ? "Topluluk moderatörü" : "Topluluk üyesi";
}

export default function CommunityMembersScreen() {
  const params = useLocalSearchParams<{ id: string | string[]; name?: string | string[] }>();
  const router = useRouter();
  const communityId = Array.isArray(params.id) ? params.id[0] : params.id;
  const communityName = Array.isArray(params.name) ? params.name[0] : params.name;
  const [query, setQuery] = useState("");
  const [activeQuery, setActiveQuery] = useState("");
  const [members, setMembers] = useState<CommunityMember[]>([]);
  const [totalCount, setTotalCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [loadingMore, setLoadingMore] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadMembers = async (mode: "initial" | "refresh" | "more" = "initial", nextQuery = activeQuery) => {
    if (!communityId) {
      setError("Topluluk bulunamadı.");
      setLoading(false);
      return;
    }

    if (mode === "initial") setLoading(true);
    if (mode === "refresh") setRefreshing(true);
    if (mode === "more") setLoadingMore(true);
    setError(null);

    const offset = mode === "more" ? members.length : 0;
    const normalizedQuery = nextQuery.trim() || null;
    const [membersResult, countResult] = await Promise.all([
      api.rpc("get_community_member_directory", {
        target_community_id: communityId,
        search_query: normalizedQuery,
        result_limit: PAGE_SIZE,
        result_offset: offset
      }),
      api.rpc("get_community_member_directory_count", {
        target_community_id: communityId,
        search_query: normalizedQuery
      })
    ]);

    if (membersResult.error || countResult.error) {
      setError("Topluluk üyeleri şu anda yüklenemiyor. Lütfen tekrar deneyin.");
    } else {
      const nextMembers = (membersResult.data ?? []) as CommunityMember[];
      setMembers((current) => mode === "more" ? [...current, ...nextMembers] : nextMembers);
      setTotalCount(Number(countResult.data ?? 0));
    }

    setLoading(false);
    setRefreshing(false);
    setLoadingMore(false);
  };

  useEffect(() => {
    void loadMembers("initial", "");
  }, [communityId]);

  const submitSearch = () => {
    const nextQuery = query.trim();
    setActiveQuery(nextQuery);
    void loadMembers("initial", nextQuery);
  };

  const clearSearch = () => {
    setQuery("");
    setActiveQuery("");
    void loadMembers("initial", "");
  };

  return (
    <>
      <Stack.Screen options={{ headerShown: false }} />
      <FlatList
        data={members}
        keyExtractor={(member) => member.user_id}
        contentContainerStyle={styles.page}
        keyboardShouldPersistTaps="handled"
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadMembers("refresh")} tintColor={colors.accent} />}
        ListHeaderComponent={(
          <View style={styles.headerStack}>
            <View style={styles.header}>
              <Pressable accessibilityLabel="Geri dön" style={styles.backButton} onPress={() => router.back()}>
                <Ionicons name="arrow-back" size={23} color={colors.ink} />
              </Pressable>
              <View style={styles.headerCopy}>
                <Text style={styles.kicker}>TOPLULUK ÜYELERİ</Text>
                <Text style={styles.title} numberOfLines={2}>{communityName || "Üyeler"}</Text>
              </View>
              <View style={styles.countBadge}><Text style={styles.countText}>{totalCount}</Text></View>
            </View>

            <View style={styles.searchCard}>
              <View style={styles.searchInputRow}>
                <Ionicons name="search" size={20} color={colors.muted} />
                <TextInput
                  value={query}
                  onChangeText={setQuery}
                  onSubmitEditing={submitSearch}
                  returnKeyType="search"
                  autoCapitalize="none"
                  placeholder="Ad, kullanıcı adı veya şehir"
                  placeholderTextColor={colors.muted}
                  style={styles.searchInput}
                />
                {query ? (
                  <Pressable accessibilityLabel="Aramayı temizle" onPress={clearSearch}>
                    <Ionicons name="close-circle" size={21} color={colors.muted} />
                  </Pressable>
                ) : null}
              </View>
              <Pressable style={styles.searchButton} onPress={submitSearch}>
                <Text style={styles.searchButtonText}>Üyelerde ara</Text>
              </Pressable>
            </View>

            {activeQuery ? <Text style={styles.resultText}>“{activeQuery}” için {totalCount} sonuç</Text> : null}
            {error ? <Text style={styles.error}>{error}</Text> : null}
          </View>
        )}
        renderItem={({ item }) => (
          <Link href={{ pathname: "/user/[id]", params: { id: item.user_id } }} asChild>
            <Pressable style={styles.memberCard}>
              <View style={styles.avatar}>
                {item.avatar_url ? (
                  <Image source={{ uri: item.avatar_url }} style={styles.avatarImage} />
                ) : (
                  <Text style={styles.avatarText}>{item.display_name.slice(0, 1).toLocaleUpperCase("tr-TR")}</Text>
                )}
              </View>
              <View style={styles.memberCopy}>
                <View style={styles.nameRow}>
                  <Text style={styles.memberName} numberOfLines={1}>{item.display_name}</Text>
                  {item.is_verified ? <Ionicons name="checkmark-circle" size={16} color={colors.success} /> : null}
                </View>
                <Text style={styles.memberMeta} numberOfLines={1}>@{item.username}{item.city ? ` · ${item.city}` : ""}</Text>
                <Text style={[styles.role, item.member_role !== "member" && styles.moderatorRole]}>{roleLabel(item.member_role)}</Text>
              </View>
              <Ionicons name="chevron-forward" size={20} color={colors.muted} />
            </Pressable>
          </Link>
        )}
        ItemSeparatorComponent={() => <View style={styles.separator} />}
        ListEmptyComponent={loading ? (
          <View style={styles.stateBox}><ActivityIndicator color={colors.accent} /><Text style={styles.stateText}>Üyeler yükleniyor...</Text></View>
        ) : !error ? (
          <View style={styles.stateBox}><Ionicons name="people-outline" size={34} color={colors.accent} /><Text style={styles.stateTitle}>Üye bulunamadı</Text><Text style={styles.stateText}>Arama ifadesini değiştirip tekrar deneyin.</Text></View>
        ) : null}
        ListFooterComponent={members.length < totalCount ? (
          <Pressable disabled={loadingMore} style={styles.moreButton} onPress={() => void loadMembers("more")}>
            {loadingMore ? <ActivityIndicator color={colors.accent} /> : <Text style={styles.moreButtonText}>Daha fazla üye göster</Text>}
          </Pressable>
        ) : members.length ? <Text style={styles.footerText}>{totalCount} üyenin tamamı gösterildi.</Text> : null}
      />
    </>
  );
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 20, paddingTop: 26, paddingBottom: 52, backgroundColor: colors.page },
  headerStack: { gap: 16, marginBottom: 18 },
  header: { flexDirection: "row", alignItems: "center", gap: 12 },
  backButton: { width: 46, height: 46, alignItems: "center", justifyContent: "center", borderRadius: 23, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  headerCopy: { flex: 1, gap: 3 },
  kicker: { color: colors.accent, fontSize: 10, fontWeight: "900", letterSpacing: 1.4 },
  title: { color: colors.ink, fontSize: 26, lineHeight: 31, fontWeight: "900" },
  countBadge: { minWidth: 43, height: 43, paddingHorizontal: 10, alignItems: "center", justifyContent: "center", borderRadius: 22, backgroundColor: colors.accentSoft },
  countText: { color: colors.accent, fontSize: 14, fontWeight: "900" },
  searchCard: { padding: 13, gap: 10, borderRadius: 24, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  searchInputRow: { minHeight: 50, flexDirection: "row", alignItems: "center", gap: 9, paddingHorizontal: 14, borderRadius: 17, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.page },
  searchInput: { flex: 1, minWidth: 0, color: colors.ink, fontSize: 14, fontWeight: "700" },
  searchButton: { minHeight: 47, alignItems: "center", justifyContent: "center", borderRadius: 999, backgroundColor: colors.action },
  searchButtonText: { color: colors.actionText, fontSize: 13, fontWeight: "900" },
  resultText: { color: colors.muted, fontSize: 12, fontWeight: "700" },
  error: { padding: 13, borderRadius: 16, color: colors.danger, backgroundColor: colors.surfaceStrong, fontSize: 13, lineHeight: 19, fontWeight: "700" },
  memberCard: { minHeight: 82, flexDirection: "row", alignItems: "center", gap: 12, padding: 14, borderRadius: 23, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  avatar: { width: 55, height: 55, overflow: "hidden", alignItems: "center", justifyContent: "center", borderRadius: 28, borderWidth: 2, borderColor: colors.accent, backgroundColor: colors.accentSoft },
  avatarImage: { width: "100%", height: "100%" },
  avatarText: { color: colors.accent, fontSize: 20, fontWeight: "900" },
  memberCopy: { flex: 1, minWidth: 0, gap: 3 },
  nameRow: { flexDirection: "row", alignItems: "center", gap: 5 },
  memberName: { flexShrink: 1, color: colors.ink, fontSize: 15, fontWeight: "900" },
  memberMeta: { color: colors.muted, fontSize: 12, fontWeight: "700" },
  role: { alignSelf: "flex-start", color: colors.muted, fontSize: 10, fontWeight: "900", letterSpacing: 0.3 },
  moderatorRole: { color: colors.accent },
  separator: { height: 10 },
  stateBox: { alignItems: "center", gap: 9, padding: 34, borderRadius: 24, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  stateTitle: { color: colors.ink, fontSize: 18, fontWeight: "900" },
  stateText: { color: colors.muted, fontSize: 13, textAlign: "center" },
  moreButton: { minHeight: 49, alignItems: "center", justifyContent: "center", marginTop: 18, borderRadius: 999, borderWidth: 1, borderColor: colors.accent, backgroundColor: colors.surface },
  moreButtonText: { color: colors.accent, fontSize: 13, fontWeight: "900" },
  footerText: { paddingTop: 20, color: colors.muted, fontSize: 11, fontWeight: "700", textAlign: "center" }
});
