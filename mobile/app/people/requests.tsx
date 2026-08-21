import { Ionicons } from "@expo/vector-icons";
import { Link, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Image, Pressable, RefreshControl, ScrollView, StyleSheet, Text, View } from "react-native";
import { useAuth } from "../../src/lib/auth";
import { api } from "../../src/lib/api";
import { useScreenInsets } from "../../src/lib/safeArea";
import { colors } from "../../src/theme/colors";

type FollowRequest = {
  request_id: string;
  user_id: string;
  display_name: string;
  username: string;
  avatar_url: string | null;
  bio: string | null;
  city: string | null;
  is_verified: boolean;
  follower_count: number;
  following_count: number;
  requested_at: string;
};

export default function FollowRequestsScreen() {
  const router = useRouter();
  const { user } = useAuth();
  const insets = useScreenInsets();
  const [requests, setRequests] = useState<FollowRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [busyRequestId, setBusyRequestId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const loadRequests = async (refresh = false) => {
    refresh ? setRefreshing(true) : setLoading(true);
    setError(null);

    const { data, error: loadError } = await api.rpc("get_my_follow_requests");
    if (loadError) {
      setError("Takip istekleri yüklenemedi. Lütfen tekrar dene.");
    } else {
      setRequests((data ?? []) as FollowRequest[]);
    }

    setLoading(false);
    setRefreshing(false);
  };

  useEffect(() => {
    if (user) void loadRequests();
  }, [user?.id]);

  const reviewRequest = async (requestId: string, accept: boolean) => {
    if (busyRequestId) return;
    setBusyRequestId(requestId);
    setError(null);

    const { error: reviewError } = await api.rpc("review_follow_request", {
      target_request_id: requestId,
      target_decision: accept ? "approved" : "rejected"
    });

    if (reviewError) {
      setError("Takip isteği sonuçlandırılamadı. Lütfen tekrar dene.");
    } else {
      setRequests((current) => current.filter((request) => request.request_id !== requestId));
    }
    setBusyRequestId(null);
  };

  return (
    <ScrollView
      contentContainerStyle={[styles.page, { paddingTop: insets.top + 16, paddingBottom: insets.bottom + 24 }]}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadRequests(true)} tintColor={colors.accent as string} />}
    >
      <View style={styles.header}>
        <Pressable style={styles.iconButton} onPress={() => router.back()}>
          <Ionicons name="arrow-back" size={22} color={colors.ink} />
        </Pressable>
        <View style={styles.headerCopy}>
          <Text style={styles.kicker}>GİZLİ HESAP</Text>
          <Text style={styles.title}>Takip istekleri</Text>
        </View>
        <Text style={styles.count}>{requests.length}</Text>
      </View>

      <Text style={styles.description}>Profilini kimlerin takip edebileceğine sen karar verirsin.</Text>
      {error ? <Text style={styles.error}>{error}</Text> : null}

      {loading ? (
        <View style={styles.centerBox}>
          <ActivityIndicator color={colors.accent} />
          <Text style={styles.muted}>İstekler yükleniyor...</Text>
        </View>
      ) : requests.length ? (
        <View style={styles.list}>
          {requests.map((request) => {
            const busy = busyRequestId === request.request_id;
            return (
              <View key={request.request_id} style={styles.card}>
                <Link href={{ pathname: "/user/[id]", params: { id: request.user_id } }} asChild>
                  <Pressable style={styles.personRow}>
                    <View style={styles.avatar}>
                      {request.avatar_url ? (
                        <Image source={{ uri: request.avatar_url }} style={styles.avatarImage} />
                      ) : (
                        <Text style={styles.avatarInitial}>{request.display_name.slice(0, 1).toUpperCase()}</Text>
                      )}
                    </View>
                    <View style={styles.personCopy}>
                      <View style={styles.nameRow}>
                        <Text style={styles.name} numberOfLines={1}>{request.display_name}</Text>
                        {request.is_verified ? <Ionicons name="checkmark-circle" size={17} color={colors.aqua} /> : null}
                      </View>
                      <Text style={styles.meta} numberOfLines={1}>@{request.username}{request.city ? ` · ${request.city}` : ""}</Text>
                      <Text style={styles.stats}>{request.follower_count} takipçi · {request.following_count} takip</Text>
                    </View>
                    <Ionicons name="chevron-forward" size={20} color={colors.muted} />
                  </Pressable>
                </Link>

                <View style={styles.actions}>
                  <Pressable disabled={busy} style={[styles.secondaryButton, busy && styles.buttonDisabled]} onPress={() => void reviewRequest(request.request_id, false)}>
                    <Text style={styles.secondaryButtonText}>Reddet</Text>
                  </Pressable>
                  <Pressable disabled={busy} style={[styles.primaryButton, busy && styles.buttonDisabled]} onPress={() => void reviewRequest(request.request_id, true)}>
                    <Text style={styles.primaryButtonText}>{busy ? "İşleniyor..." : "Onayla"}</Text>
                  </Pressable>
                </View>
              </View>
            );
          })}
        </View>
      ) : (
        <View style={styles.empty}>
          <View style={styles.emptyIcon}><Ionicons name="person-add-outline" size={30} color={colors.accent} /></View>
          <Text style={styles.emptyTitle}>Bekleyen istek yok</Text>
          <Text style={styles.muted}>Yeni bir takip isteği geldiğinde burada göreceksin.</Text>
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, gap: 18, padding: 22, paddingTop: 34, paddingBottom: 52, backgroundColor: colors.page },
  header: { flexDirection: "row", alignItems: "center", gap: 14 },
  iconButton: { width: 44, height: 44, alignItems: "center", justifyContent: "center", borderRadius: 22, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  headerCopy: { flex: 1, gap: 3 },
  kicker: { color: colors.accent, fontSize: 11, fontWeight: "900", letterSpacing: 1.3 },
  title: { color: colors.ink, fontSize: 29, fontWeight: "900" },
  count: { minWidth: 34, paddingVertical: 7, textAlign: "center", overflow: "hidden", borderRadius: 17, color: colors.accent, backgroundColor: colors.accentSoft, fontWeight: "900" },
  description: { color: colors.muted, fontSize: 16, lineHeight: 24 },
  error: { padding: 14, borderRadius: 16, color: colors.danger, backgroundColor: colors.surface, fontSize: 13, fontWeight: "800" },
  centerBox: { alignItems: "center", gap: 10, paddingVertical: 48 },
  list: { gap: 12 },
  card: { gap: 14, padding: 16, borderRadius: 24, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  personRow: { flexDirection: "row", alignItems: "center", gap: 12 },
  avatar: { width: 58, height: 58, alignItems: "center", justifyContent: "center", overflow: "hidden", borderRadius: 29, borderWidth: 2, borderColor: colors.accent, backgroundColor: colors.accentSoft },
  avatarImage: { width: "100%", height: "100%" },
  avatarInitial: { color: colors.accent, fontSize: 22, fontWeight: "900" },
  personCopy: { flex: 1, gap: 3 },
  nameRow: { flexDirection: "row", alignItems: "center", gap: 5 },
  name: { flexShrink: 1, color: colors.ink, fontSize: 17, fontWeight: "900" },
  meta: { color: colors.muted, fontSize: 13, fontWeight: "700" },
  stats: { color: colors.accent, fontSize: 12, fontWeight: "800" },
  actions: { flexDirection: "row", gap: 10 },
  secondaryButton: { flex: 1, alignItems: "center", justifyContent: "center", minHeight: 46, borderRadius: 999, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surfaceStrong },
  secondaryButtonText: { color: colors.ink, fontSize: 14, fontWeight: "900" },
  primaryButton: { flex: 1.35, alignItems: "center", justifyContent: "center", minHeight: 46, borderRadius: 999, backgroundColor: colors.action },
  primaryButtonText: { color: colors.actionText, fontSize: 14, fontWeight: "900" },
  buttonDisabled: { opacity: 0.55 },
  empty: { alignItems: "center", gap: 9, padding: 30, borderRadius: 26, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  emptyIcon: { width: 58, height: 58, alignItems: "center", justifyContent: "center", borderRadius: 20, backgroundColor: colors.accentSoft },
  emptyTitle: { color: colors.ink, fontSize: 20, fontWeight: "900" },
  muted: { color: colors.muted, textAlign: "center", fontSize: 14, lineHeight: 20 }
});
