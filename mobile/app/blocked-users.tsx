import { Ionicons } from "@expo/vector-icons";
import { Stack } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Image, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { api } from "../src/lib/api";
import { colors } from "../src/theme/colors";
import { showAppConfirm, showAppSuccess, showAppError } from "../src/components/AppAlert";

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

  const unblock = async (profile: BlockedProfile) => {
    if (busyId) return;

    const confirmed = await showAppConfirm({
      title: "Engeli kaldır",
      text: `${profile.display_name} adlı kullanıcının engelini kaldırmak istediğine emin misin?`,
      confirmText: "Engeli kaldır",
      cancelText: "Vazgeç"
    });
    if (!confirmed) return;

    setBusyId(profile.user_id);
    setError(null);
    const { error: unblockError } = await api.rpc("set_profile_block", {
      target_user_id: profile.user_id,
      target_should_block: false
    });
    if (unblockError) {
      setError("Engel kaldırılamadı. Lütfen tekrar deneyin.");
      void showAppError(unblockError.message || "Engel kaldırılamadı.");
    } else {
      setProfiles((current) => current.filter((item) => item.user_id !== profile.user_id));
      void showAppSuccess(`${profile.display_name} adlı kullanıcının engeli kaldırıldı.`);
    }
    setBusyId(null);
  };

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: "Engellenen kullanıcılar" }} />
      <ScrollView style={styles.screen} contentContainerStyle={styles.page}>
        <View style={styles.hero}>
          <View style={styles.icon}><Ionicons name="shield-outline" size={26} color={colors.actionText} /></View>
          <Text style={styles.title}>Güvenli alanını yönet.</Text>
          <Text style={styles.description}>Engellediğin kişiler profilini göremez, seni takip edemez ve seninle etkileşim kuramaz.</Text>
        </View>

        {loading ? <ActivityIndicator color={colors.accent} /> : null}
        {error ? <Text style={styles.error}>{error}</Text> : null}

        {!loading && profiles.length === 0 ? (
          <View style={styles.empty}>
            <Ionicons name="checkmark-circle-outline" size={34} color={colors.success} />
            <Text style={styles.emptyTitle}>Engellenen kullanıcı yok</Text>
            <Text style={styles.emptyText}>Engellediğin bir kullanıcı olduğunda burada görünecek.</Text>
          </View>
        ) : (
          <View style={styles.list}>
            {profiles.map((profile) => (
              <View key={profile.user_id} style={styles.row}>
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
                <Pressable style={styles.button} onPress={() => void unblock(profile)} disabled={busyId === profile.user_id}>
                  {busyId === profile.user_id ? <ActivityIndicator size="small" color={colors.actionText} /> : <Text style={styles.buttonText}>Engeli kaldır</Text>}
                </Pressable>
              </View>
            ))}
          </View>
        )}
      </ScrollView>
    </>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  page: { gap: 18, padding: 20, paddingBottom: 48 },
  hero: { gap: 9, padding: 22, borderRadius: 30, backgroundColor: colors.brandInk },
  icon: { width: 52, height: 52, alignItems: "center", justifyContent: "center", borderRadius: 18, backgroundColor: colors.action },
  title: { color: colors.onBrand, fontSize: 29, lineHeight: 35, fontWeight: "900" },
  description: { color: colors.onBrandMuted, fontSize: 14, lineHeight: 21 },
  error: { padding: 14, borderRadius: 16, color: colors.danger, backgroundColor: colors.surface, fontWeight: "800" },
  empty: { alignItems: "center", gap: 8, padding: 28, borderRadius: 26, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
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
