import { Ionicons } from "@expo/vector-icons";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { BackButton } from "../../src/components/IconButton";
import { PeopleListItem, type PublicPerson } from "../../src/components/PeopleListItem";
import { useAuth } from "../../src/lib/auth";
import { getRequestedProfileIds, setProfileFollow } from "../../src/lib/follows";
import { api } from "../../src/lib/api";
import { getPlatformTeamIdentityMap } from "../../src/lib/team-identities";
import { colors } from "../../src/theme/colors";

type ConnectionTab = "followers" | "following";

export default function FollowConnectionsScreen() {
  const router = useRouter();
  const params = useLocalSearchParams<{ userId: string; tab?: string }>();
  const { user, profile } = useAuth();
  const routeUserId = Array.isArray(params.userId) ? params.userId[0] : params.userId;
  const userId = routeUserId || profile?.id || user?.id;
  const initialTab = (Array.isArray(params.tab) ? params.tab[0] : params.tab) === "following" ? "following" : "followers";
  const [tab, setTab] = useState<ConnectionTab>(initialTab);
  const [people, setPeople] = useState<PublicPerson[]>([]);
  const [loading, setLoading] = useState(true);
  const [busyUserId, setBusyUserId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const loadConnections = async () => {
    if (!userId) {
      setError("Profil bulunamadı.");
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);
    const { data, error: connectionsError } = await api.rpc("get_public_follow_connections", {
      target_user_id: userId,
      target_kind: tab,
      result_limit: 100,
      result_offset: 0
    });

    if (connectionsError) setError(connectionsError.message);
    else {
      const nextPeople = ((Array.isArray(data) ? data : []) as PublicPerson[]).map((person) => ({
        ...person,
        follow_state: person.is_following ? "following" : person.follow_state ?? "none"
      }));
      const ids = nextPeople.map((person) => person.user_id);
      const [identities, requestedIds] = await Promise.all([
        getPlatformTeamIdentityMap(ids),
        getRequestedProfileIds(ids)
      ]);
      setPeople(nextPeople.map((person) => ({
        ...person,
        follow_state: person.follow_state === "following" ? "following" : requestedIds.has(person.user_id) ? "requested" : "none",
        team_role: identities.get(person.user_id) ?? null
      })));
    }
    setLoading(false);
  };

  useEffect(() => {
    void loadConnections();
  }, [tab, userId]);

  useEffect(() => {
    setTab(initialTab);
  }, [initialTab]);

  const toggleFollow = async (person: PublicPerson) => {
    if (!user || busyUserId || person.user_id === user.id) return;
    setBusyUserId(person.user_id);
    setError(null);

    const currentState = person.follow_state ?? (person.is_following ? "following" : "none");
    const result = await setProfileFollow(person.user_id, currentState === "none");

    if (result.error) {
      setError(result.error);
    } else if (tab === "following" && userId === user.id && person.is_following) {
      setPeople((current) => current.filter((item) => item.user_id !== person.user_id));
    } else {
      setPeople((current) => current.map((item) => item.user_id === person.user_id
        ? {
            ...item,
            is_following: result.isFollowing ?? item.is_following,
            follow_state: result.state ?? item.follow_state,
            follower_count: Math.max(0, item.follower_count + (result.state === "following" ? 1 : currentState === "following" ? -1 : 0))
          }
        : item));
    }
    setBusyUserId(null);
  };

  return (
    <ScrollView contentContainerStyle={styles.page}>
      <View style={styles.header}>
        <BackButton onPress={() => router.back()} />
        <Text style={styles.title}>Takip bağlantıları</Text>
      </View>

      <View style={styles.tabs}>
        <Pressable style={[styles.tab, tab === "followers" && styles.tabActive]} onPress={() => setTab("followers")}>
          <Text style={[styles.tabText, tab === "followers" && styles.tabTextActive]}>Takipçiler</Text>
        </Pressable>
        <Pressable style={[styles.tab, tab === "following" && styles.tabActive]} onPress={() => setTab("following")}>
          <Text style={[styles.tabText, tab === "following" && styles.tabTextActive]}>Takip edilenler</Text>
        </Pressable>
      </View>

      {error ? <Text style={styles.error}>{error}</Text> : null}
      {loading ? (
        <View style={styles.loading}>
          <ActivityIndicator color={colors.accent} />
          <Text style={styles.muted}>Liste yükleniyor...</Text>
        </View>
      ) : people.length ? (
        <View style={styles.list}>
          {people.map((person) => (
            <PeopleListItem
              key={person.user_id}
              person={person}
              currentUserId={user?.id}
              busy={busyUserId === person.user_id}
              onToggleFollow={(item) => void toggleFollow(item)}
            />
          ))}
        </View>
      ) : (
        <View style={styles.empty}>
          <Ionicons name="people-outline" size={32} color={colors.accent} />
          <Text style={styles.emptyTitle}>{tab === "followers" ? "Henüz takipçi yok" : "Henüz kimse takip edilmiyor"}</Text>
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 16, paddingTop: 22, paddingBottom: 36, gap: 14, backgroundColor: colors.page },
  header: { flexDirection: "row", alignItems: "center", gap: 14 },
  iconButton: { width: 44, height: 44, borderRadius: 22, alignItems: "center", justifyContent: "center", backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  title: { flex: 1, color: colors.ink, fontSize: 23, fontWeight: "900" },
  tabs: { flexDirection: "row", padding: 5, borderRadius: 999, backgroundColor: colors.surfaceStrong, borderWidth: 1, borderColor: colors.border },
  tab: { flex: 1, alignItems: "center", paddingVertical: 12, borderRadius: 999 },
  tabActive: { backgroundColor: colors.action },
  tabText: { color: colors.muted, fontSize: 13, fontWeight: "900" },
  tabTextActive: { color: colors.actionText },
  list: { gap: 10 },
  loading: { alignItems: "center", gap: 10, paddingVertical: 40 },
  empty: { alignItems: "center", gap: 8, padding: 30, borderRadius: 24, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  emptyTitle: { color: colors.ink, fontSize: 17, fontWeight: "900" },
  muted: { color: colors.muted, fontSize: 14 },
  error: { color: colors.danger, fontSize: 13, lineHeight: 19, fontWeight: "700" }
});
