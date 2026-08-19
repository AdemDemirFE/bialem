import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Keyboard, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { PeopleListItem, type PublicPerson } from "../../src/components/PeopleListItem";
import { useAuth } from "../../src/lib/auth";
import { getRequestedProfileIds, setProfileFollow } from "../../src/lib/follows";
import { api } from "../../src/lib/api";
import { getPlatformTeamIdentityMap } from "../../src/lib/team-identities";
import { colors } from "../../src/theme/colors";

export default function PeopleDiscoveryScreen() {
  const router = useRouter();
  const { user } = useAuth();
  const [query, setQuery] = useState("");
  const [people, setPeople] = useState<PublicPerson[]>([]);
  const [loading, setLoading] = useState(true);
  const [busyUserId, setBusyUserId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const searchPeople = async (nextQuery = query) => {
    setLoading(true);
    setError(null);
    Keyboard.dismiss();

    const { data, error: searchError } = await api.rpc("search_public_profiles", {
      target_query: nextQuery.trim() || null,
      result_limit: 40
    });

    if (searchError) {
      setError(searchError.message);
    } else {
      const nextPeople = (data ?? []) as PublicPerson[];
      const ids = nextPeople.map((person) => person.user_id);
      const [identities, requestedIds] = await Promise.all([
        getPlatformTeamIdentityMap(ids),
        getRequestedProfileIds(ids)
      ]);
      setPeople(nextPeople.map((person) => ({
        ...person,
        follow_state: person.is_following ? "following" : requestedIds.has(person.user_id) ? "requested" : "none",
        team_role: identities.get(person.user_id) ?? null
      })));
    }
    setLoading(false);
  };

  useEffect(() => {
    void searchPeople("");
  }, []);

  const toggleFollow = async (person: PublicPerson) => {
    if (!user || busyUserId) return;
    setBusyUserId(person.user_id);
    setError(null);

    const currentState = person.follow_state ?? (person.is_following ? "following" : "none");
    const result = await setProfileFollow(person.user_id, currentState === "none");

    if (result.error) {
      setError(result.error);
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
    <ScrollView contentContainerStyle={styles.page} keyboardShouldPersistTaps="handled">
      <View style={styles.header}>
        <Pressable style={styles.iconButton} onPress={() => router.back()}>
          <Ionicons name="arrow-back" size={22} color={colors.ink} />
        </Pressable>
        <View style={styles.headerCopy}>
          <Text style={styles.kicker}>BİALEM İNSANLARI</Text>
          <Text style={styles.title}>Kişi bul</Text>
        </View>
      </View>

      <Text style={styles.description}>Ad, kullanıcı adı veya şehirle ara; ilham aldığın kişileri takip et.</Text>

      <View style={styles.searchPanel}>
        <View style={styles.inputWrap}>
          <Ionicons name="search" size={20} color={colors.muted} />
          <TextInput
            value={query}
            onChangeText={setQuery}
            onSubmitEditing={() => void searchPeople()}
            placeholder="Örn. ayşe, @kullanıcı veya Ankara"
            placeholderTextColor={colors.muted}
            returnKeyType="search"
            autoCapitalize="none"
            style={styles.input}
          />
          {query ? (
            <Pressable onPress={() => {
              setQuery("");
              void searchPeople("");
            }}>
              <Ionicons name="close-circle" size={20} color={colors.muted} />
            </Pressable>
          ) : null}
        </View>
        <Pressable style={styles.searchButton} onPress={() => void searchPeople()}>
          <Text style={styles.searchButtonText}>Ara</Text>
        </Pressable>
      </View>

      <View style={styles.sectionTitleRow}>
        <Text style={styles.sectionTitle}>{query.trim() ? "Arama sonuçları" : "Keşfedebileceğin kişiler"}</Text>
        <Text style={styles.resultCount}>{people.length}</Text>
      </View>

      {error ? <Text style={styles.error}>{error}</Text> : null}
      {loading ? (
        <View style={styles.loading}>
          <ActivityIndicator color={colors.accent} />
          <Text style={styles.muted}>Kişiler yükleniyor...</Text>
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
          <Text style={styles.emptyTitle}>Eşleşen kişi bulunamadı</Text>
          <Text style={styles.muted}>Farklı bir ad veya kullanıcı adı deneyebilirsin.</Text>
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 22, paddingTop: 34, paddingBottom: 48, gap: 18, backgroundColor: colors.page },
  header: { flexDirection: "row", alignItems: "center", gap: 14 },
  iconButton: { width: 44, height: 44, borderRadius: 22, alignItems: "center", justifyContent: "center", backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  headerCopy: { flex: 1, gap: 3 },
  kicker: { color: colors.accent, fontSize: 11, fontWeight: "900", letterSpacing: 1.3 },
  title: { color: colors.ink, fontSize: 32, fontWeight: "900" },
  description: { color: colors.muted, fontSize: 16, lineHeight: 24 },
  searchPanel: { gap: 10, padding: 14, borderRadius: 24, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  inputWrap: { minHeight: 52, flexDirection: "row", alignItems: "center", gap: 10, paddingHorizontal: 14, borderRadius: 18, backgroundColor: colors.surfaceStrong, borderWidth: 1, borderColor: colors.border },
  input: { flex: 1, color: colors.ink, fontSize: 15 },
  searchButton: { alignItems: "center", padding: 14, borderRadius: 999, backgroundColor: colors.action },
  searchButtonText: { color: colors.actionText, fontSize: 14, fontWeight: "900" },
  sectionTitleRow: { flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  sectionTitle: { color: colors.ink, fontSize: 20, fontWeight: "900" },
  resultCount: { minWidth: 32, textAlign: "center", paddingVertical: 6, borderRadius: 16, overflow: "hidden", color: colors.accent, backgroundColor: colors.accentSoft, fontWeight: "900" },
  list: { gap: 10 },
  loading: { alignItems: "center", gap: 10, paddingVertical: 40 },
  empty: { alignItems: "center", gap: 8, padding: 28, borderRadius: 24, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  emptyTitle: { color: colors.ink, fontSize: 17, fontWeight: "900" },
  muted: { color: colors.muted, fontSize: 14, lineHeight: 20, textAlign: "center" },
  error: { color: colors.danger, fontSize: 13, lineHeight: 19, fontWeight: "700" }
});
