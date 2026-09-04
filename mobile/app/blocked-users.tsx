import { Ionicons } from "@expo/vector-icons";
import { Stack } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Image, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { Reveal, Skeleton } from "../src/animations";
import { FeedbackState } from "../src/components/ui/FeedbackState";
import { api } from "../src/lib/api";
import { colors } from "../src/theme/colors";

type BlockedProfile = {
  user_id: string;
  display_name: string;
  username: string;
  avatar_url: string | null;
  blocked_at: string;
};

export default function BlockedUsersScreen() {
  const [profiles, setProfiles] = useState<BlockedProfile[]>([]);
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    setError(null);
    const { data, error: loadError } = await api.rpc("get_my_blocked_profiles");
    if (loadError) setError("Engellenen kullanıcılar yüklenemedi.");
    else setProfiles((data ?? []) as BlockedProfile[]);
    setLoading(false);
  };

  useEffect(() => {
    void load();
  }, []);

  const unblock = async (userId: string) => {
    if (busyId) return;
    setBusyId(userId);
    setError(null);
    const { error: unblockError } = await api.rpc("set_profile_block", {
      target_user_id: userId,
      target_should_block: false
    });
    if (unblockError) setError("Engel kaldırılamadı. Lütfen tekrar deneyin.");
    else setProfiles((current) => current.filter((profile) => profile.user_id !== userId));
    setBusyId(null);
  };

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: "Engellenen kullanıcılar" }} />
      <ScrollView style={styles.screen} contentContainerStyle={styles.page}>
        <Reveal>
        <View style={styles.hero}>
          <View style={styles.icon}><Ionicons name="shield-outline" size={26} color={colors.actionText} /></View>
          <Text style={styles.title}>Güvenli alanını yönet.</Text>
          <Text style={styles.description}>Engellediğin kişiler profilini göremez, seni takip edemez ve seninle etkileşim kuramaz.</Text>
        </View>
        </Reveal>

        {loading ? (
          <View style={{ gap: 12 }}>
            <Skeleton height={80} borderRadius={22} />
            <Skeleton height={80} borderRadius={22} />
          </View>
        ) : null}
        {error ? (
          <FeedbackState
            kind="error"
            title="Liste yüklenemedi"
            message={error}
            onRetry={() => void load()}
          />
        ) : null}

        {!loading && profiles.length === 0 ? (
          <View style={styles.empty}>
            <Ionicons name="checkmark-circle-outline" size={34} color={colors.success} />
            <Text style={styles.emptyTitle}>Engellenen kullanıcı yok</Text>
            <Text style={styles.emptyText}>Engellediğin bir kullanıcı olduğunda burada görünecek.</Text>
          </View>
        ) : (
          <View style={styles.list}>
            {profiles.map((profile, i) => (
              <Reveal key={profile.user_id} index={Math.min(i, 6)}>
              <View style={styles.row}>
                {profile.avatar_url ? (
                  <Image source={{ uri: profile.avatar_url }} style={styles.avatar} />
                ) : (
                  <View style={styles.avatarFallback}>
                    <Text style={styles.avatarInitial}>{profile.display_name.slice(0, 1).toLocaleUpperCase("tr-TR")}</Text>
                  </View>
                )}
                <View style={styles.copy}>
                  <Text style={styles.name} numberOfLines={1}>{profile.display_name}</Text>
                  <Text style={styles.username} numberOfLines={1}>@{profile.username}</Text>
                </View>
                <Pressable
                  style={({ pressed }) => [styles.button, pressed && { opacity: 0.9, transform: [{ scale: 0.97 }] }]}
                  onPress={() => void unblock(profile.user_id)}
                  disabled={busyId === profile.user_id}
                >
                  {busyId === profile.user_id ? <ActivityIndicator size="small" color={colors.actionText} /> : <Text style={styles.buttonText}>Engeli kaldır</Text>}
                </Pressable>
              </View>
              </Reveal>
            ))}
          </View>
        )}
      </ScrollView>
    </>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  page: { gap: 14, padding: 16, paddingBottom: 36 },
  hero: { gap: 7, padding: 18, borderRadius: 20, backgroundColor: colors.brandInk },
  icon: { width: 52, height: 52, alignItems: "center", justifyContent: "center", borderRadius: 18, backgroundColor: colors.action },
  title: { color: colors.onBrand, fontSize: 24, lineHeight: 29, fontWeight: "900" },
  description: { color: colors.onBrandMuted, fontSize: 14, lineHeight: 21 },
  error: { padding: 14, borderRadius: 16, color: colors.danger, backgroundColor: colors.surface, fontWeight: "800" },
  empty: { alignItems: "center", gap: 8, padding: 22, borderRadius: 18, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  emptyTitle: { color: colors.ink, fontSize: 18, fontWeight: "900" },
  emptyText: { color: colors.muted, textAlign: "center", fontSize: 13, lineHeight: 19 },
  list: { gap: 12 },
  row: { flexDirection: "row", alignItems: "center", gap: 12, padding: 14, borderRadius: 22, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  avatar: { width: 52, height: 52, borderRadius: 18 },
  avatarFallback: { width: 52, height: 52, alignItems: "center", justifyContent: "center", borderRadius: 18, backgroundColor: colors.accentSoft },
  avatarInitial: { color: colors.accent, fontSize: 21, fontWeight: "900" },
  copy: { flex: 1, gap: 3 },
  name: { color: colors.ink, fontSize: 15, fontWeight: "900" },
  username: { color: colors.muted, fontSize: 12, fontWeight: "700" },
  button: { minHeight: 42, justifyContent: "center", paddingHorizontal: 12, borderRadius: 14, backgroundColor: colors.action },
  buttonText: { color: colors.actionText, fontSize: 11, fontWeight: "900" }
});
