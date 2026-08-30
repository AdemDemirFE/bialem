import { Ionicons } from "@expo/vector-icons";
import { router, Stack, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, RefreshControl, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { showAppConfirm } from "../../src/components/AppAlert";
import { useAuth } from "../../src/lib/auth";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";

type GroupRecord = {
  id: string;
  parent_id: string;
  name: string;
  description: string | null;
};

type EventRecord = {
  id: string;
  created_by: string;
  title: string;
  description: string | null;
  starts_at: string;
  location_name: string | null;
  capacity: number | null;
  status: string;
  group_moderation_status: "pending" | "approved" | "rejected";
  platform_moderation_status: "not_required" | "pending" | "approved" | "rejected";
};

type Membership = { community_id: string; role: "member" | "manager" | "owner"; status: string; community?: { id: number } | null };
type AssistantPermissions = { can_manage_groups: boolean; can_review_events: boolean; can_manage_participants: boolean };
type ManagedMember = {
  membership_id: string;
  user_id: string;
  display_name: string;
  username: string;
  avatar_url: string | null;
  created_at: string;
};

export default function GroupDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { user } = useAuth();
  const [group, setGroup] = useState<GroupRecord | null>(null);
  const [communityName, setCommunityName] = useState("");
  const [membership, setMembership] = useState<Membership | null>(null);
  const [parentMembership, setParentMembership] = useState<Membership | null>(null);
  const [parentType, setParentType] = useState<"category_hub" | "partner_hub" | null>(null);
  const [assistantPermissions, setAssistantPermissions] = useState<AssistantPermissions>({ can_manage_groups: false, can_review_events: false, can_manage_participants: false });
  const [creationMode, setCreationMode] = useState<"direct" | "proposal" | null>(null);
  const [events, setEvents] = useState<EventRecord[]>([]);
  const [managedMembers, setManagedMembers] = useState<ManagedMember[]>([]);
  const [rejectReasons, setRejectReasons] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [busyId, setBusyId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const loadGroup = async (mode: "initial" | "refresh" = "initial") => {
    if (!id || !user) return;
    if (mode === "initial") setLoading(true);
    else setRefreshing(true);
    setError(null);

    const [groupResult, membershipResult, eventsResult, creationGroupsResult, managedMembersResult] = await Promise.all([
      api.from("communities").select("id, parent_id, name, description").eq("id", id).not("parent_id", "is", null).maybeSingle(),
      api.communityMembers.listByUser(user.id),
      api.from("events").select("id, created_by, title, description, starts_at, location_name, capacity, status, group_moderation_status, platform_moderation_status").eq("community_id", id).order("starts_at", { ascending: true }),
      api.rpc("get_my_event_creation_groups"),
      api.rpc("get_managed_community_members", { target_community_id: id })
    ]);

    if (groupResult.error || membershipResult.error || eventsResult.error || creationGroupsResult.error) {
      setError(groupResult.error?.message || membershipResult.error?.message || eventsResult.error?.message || creationGroupsResult.error?.message || "Grup yüklenemedi.");
    } else {
      const nextGroup = (groupResult.data as GroupRecord | null) ?? null;
      setGroup(nextGroup);
      const nextMemberships = (membershipResult.data ?? []) as Membership[];
      setMembership(nextMemberships.find((item) => (item.community_id || String(item.community?.id ?? "")) === id) ?? null);
      setParentMembership(nextGroup?.parent_id ? nextMemberships.find((item) => (item.community_id || String(item.community?.id ?? "")) === nextGroup.parent_id) ?? null : null);
      setEvents((eventsResult.data ?? []) as EventRecord[]);
      setManagedMembers(managedMembersResult.error ? [] : (managedMembersResult.data ?? []) as ManagedMember[]);
      const eventCreationGroup = (creationGroupsResult.data ?? []).find(
        (item: { id: string; creation_mode: "direct" | "proposal" }) => item.id === id
      );
      setCreationMode((eventCreationGroup?.creation_mode as "direct" | "proposal" | undefined) ?? null);

      if (nextGroup?.parent_id) {
        const [{ data }, { data: permissionData }] = await Promise.all([
          api.from("communities").select("name, community_type").eq("id", nextGroup.parent_id).maybeSingle(),
          api.rpc("get_my_community_assistant_permissions", { target_community_id: id })
        ]);
        setCommunityName(data?.name ?? "Ana topluluk");
        setParentType((data?.community_type as "category_hub" | "partner_hub" | undefined) ?? null);
        setAssistantPermissions(
          ((permissionData ?? [])[0] as AssistantPermissions | undefined)
          ?? { can_manage_groups: false, can_review_events: false, can_manage_participants: false }
        );
      }
    }

    setLoading(false);
    setRefreshing(false);
  };

  useEffect(() => {
    void loadGroup();
  }, [id, user?.id]);

  const directModerator = membership?.status === "approved" && membership.role !== "member";
  const partnerManager = parentType === "partner_hub" && parentMembership?.status === "approved" && parentMembership.role !== "member";
  const isModerator = directModerator || partnerManager;
  const canReviewEvents = isModerator || assistantPermissions.can_review_events;
  const isAssistant = Object.values(assistantPermissions).some(Boolean);
  const canCreateDirectly = creationMode === "direct";
  const joined = membership?.status === "approved" || isModerator || isAssistant || creationMode !== null;
  const requestPending = membership?.status === "pending";
  const canLeaveGroup = membership?.status === "approved" && membership.role === "member";
  const pendingEvents = events.filter((event) => event.status === "pending_approval" && event.group_moderation_status === "pending");
  const platformReviewEvents = events.filter((event) => event.status === "pending_approval" && event.group_moderation_status === "approved" && event.platform_moderation_status === "pending");
  const publishedEvents = events.filter((event) => event.status === "published");

  const moderateEvent = async (eventId: string, status: "published" | "rejected") => {
    if (busyId) return;
    setBusyId(eventId);
    setError(null);
    setNotice(null);

    const { error: moderationError } = await api.rpc("moderate_group_event", {
      target_event_id: eventId,
      target_status: status,
      target_rejection_reason: status === "rejected" ? rejectReasons[eventId]?.trim() || "Grup kurallarına uygun bulunmadi." : null
    });

    if (moderationError) setError(moderationError.message);
    else {
      setNotice(status === "published" ? "Grup onayı tamamlandı. Güven seviyesine göre etkinlik yayınlandı veya Bialem son kontrolüne gönderildi." : "Etkinlik reddedildi; oluşturan üyeye bildirim gönderildi.");
      await loadGroup("refresh");
    }
    setBusyId(null);
  };

  const confirmLeaveGroup = async () => {
    if (!group || busyId) return;
    const confirmed = await showAppConfirm({
      title: "Gruptan ayrıl",
      text: `${group.name} grubundan ayrılmak istediğine emin misin? Grup içeriklerine erişimin sona erecek.`,
      confirmText: "Gruptan ayrıl",
      confirmDanger: true
    });
    if (!confirmed) return;
    setBusyId("leave-group");
    setError(null);
    const { error: leaveError } = await api.rpc("leave_community", { target_community_id: group.id });
    setBusyId(null);
    if (leaveError) setError(leaveError.message);
    else router.replace({ pathname: "/community/[id]", params: { id: group.parent_id } });
  };

  const cancelMembershipRequest = async () => {
    if (!group || busyId) return;
    setBusyId("cancel-membership");
    setError(null);
    setNotice(null);
    const { error: cancelError } = await api.rpc("cancel_community_membership_request", {
      target_community_id: group.id
    });
    if (cancelError) setError(cancelError.message);
    else {
      setNotice("Grup katılım isteğin geri çekildi.");
      await loadGroup("refresh");
    }
    setBusyId(null);
  };

  const confirmRemoveMember = async (member: ManagedMember) => {
    if (busyId) return;
    const confirmed = await showAppConfirm({
      title: "Üyeyi gruptan çıkar",
      text: `${member.display_name} adlı üyeyi bu gruptan çıkarmak istediğine emin misin?`,
      confirmText: "Üyeyi çıkar",
      confirmDanger: true
    });
    if (!confirmed) return;
    setBusyId(member.membership_id);
    setError(null);
    const { error: removeError } = await api.rpc("remove_community_member", { target_membership_id: member.membership_id });
    if (removeError) setError(removeError.message);
    else {
      setNotice(`${member.display_name} gruptan çıkarıldı.`);
      await loadGroup("refresh");
    }
    setBusyId(null);
  };

  return (
    <ScrollView
      contentContainerStyle={styles.page}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadGroup("refresh")} tintColor={colors.accent} />}
    >
      <Stack.Screen options={{ headerShown: true, title: group?.name || "Grup" }} />

      {loading ? (
        <View style={styles.loadingBox}><ActivityIndicator color={colors.accent} size="large" /></View>
      ) : !group ? (
        <View style={styles.panel}><Text style={styles.panelTitle}>Grup bulunamadı</Text><Text style={styles.body}>{error}</Text></View>
      ) : !joined ? (
        <View style={styles.panel}>
          <Ionicons name={requestPending ? "time-outline" : "lock-closed"} size={30} color={colors.accent} />
          <Text style={styles.panelTitle}>{requestPending ? "Katılım isteğin onay bekliyor" : "Bu grup üyelere açık"}</Text>
          <Text style={styles.body}>
            {requestPending
              ? "Grup moderatörü isteğini inceledikten sonra bu grubun etkinliklerini görebileceksin."
              : "Önce ana topluluk sayfasından bu gruba katılmalısın."}
          </Text>
          {error ? <Text style={styles.errorText}>{error}</Text> : null}
          {notice ? <Text style={styles.noticeText}>{notice}</Text> : null}
          {requestPending ? (
            <Pressable style={styles.cancelRequestButton} disabled={busyId === "cancel-membership"} onPress={() => void cancelMembershipRequest()}>
              {busyId === "cancel-membership"
                ? <ActivityIndicator size="small" color={colors.accent} />
                : <Text style={styles.cancelRequestButtonText}>Katılım isteğini geri çek</Text>}
            </Pressable>
          ) : null}
        </View>
      ) : (
        <>
          <View style={styles.hero}>
            <Text style={styles.kicker}>{communityName.toLocaleUpperCase("tr-TR")}</Text>
            <Text style={styles.title}>{group.name}</Text>
            <Text style={styles.heroBody}>{group.description || "Bu grup yeni fikirleri birlikte etkinliğe dönüştürmek için kuruldu."}</Text>
            <View style={styles.roleChip}>
              <Ionicons name={canReviewEvents ? "shield-checkmark" : "person"} size={15} color={colors.ink} />
              <Text style={styles.roleText}>
                {isModerator
                  ? parentType === "partner_hub"
                    ? "Bağımsız topluluk yöneticisi"
                    : "Bialem grup moderatörü"
                  : isAssistant
                    ? "Moderatör yardımcısı"
                    : "Grup üyesi"}
              </Text>
            </View>
            {canLeaveGroup ? (
              <Pressable style={styles.heroOutlineButton} disabled={busyId === "leave-group"} onPress={confirmLeaveGroup}>
                <Ionicons name="exit-outline" size={17} color="#ffffff" />
                <Text style={styles.heroOutlineButtonText}>Gruptan ayrıl</Text>
              </Pressable>
            ) : null}
          </View>

          {error ? <Text style={styles.errorText}>{error}</Text> : null}
          {notice ? <Text style={styles.noticeText}>{notice}</Text> : null}

          <View style={styles.actionPanel}>
            <View style={styles.actionIcon}><Ionicons name="calendar" size={24} color={colors.accent} /></View>
            <View style={styles.actionCopy}>
              <Text style={styles.panelTitle}>{canCreateDirectly ? "Yeni bir etkinlik oluştur" : "Bir etkinlik fikrin mi var?"}</Text>
              <Text style={styles.body}>
                {canCreateDirectly
                  ? "Yetkin sayesinde grup onayı beklemeden etkinlik oluşturabilirsin."
                  : "Önerin grup moderatörüne gider; onaylandığında tüm grup üyelerine açılır."}
              </Text>
            </View>
            <Pressable style={styles.primaryButton} onPress={() => router.push({ pathname: "/organizer-request", params: { groupId: group.id } })}>
              <Text style={styles.primaryButtonText}>{canCreateDirectly ? "Etkinlik oluştur" : "Etkinlik öner"}</Text>
              <Ionicons name="arrow-forward" size={17} color={colors.actionText} />
            </Pressable>
          </View>

          {canReviewEvents ? (
            <View style={styles.panel}>
              <View style={styles.sectionHeader}>
                <Text style={styles.panelTitle}>Onay bekleyen etkinlikler</Text>
                <View style={styles.countBadge}><Text style={styles.countText}>{pendingEvents.length}</Text></View>
              </View>
              {pendingEvents.length === 0 ? (
                <Text style={styles.body}>Şu anda inceleme bekleyen etkinlik yok.</Text>
              ) : pendingEvents.map((event) => (
                <View key={event.id} style={styles.pendingCard}>
                  <Text style={styles.eventTitle}>{event.title}</Text>
                  <Text style={styles.eventMeta}>{formatDate(event.starts_at)}{event.location_name ? ` - ${event.location_name}` : ""}</Text>
                  <Text style={styles.body}>{event.description || "Açıklama eklenmemiş."}</Text>
                  <TextInput
                    value={rejectReasons[event.id] ?? ""}
                    onChangeText={(value) => setRejectReasons((current) => ({ ...current, [event.id]: value }))}
                    placeholder="Reddetme nedeni (reddedilecekse)"
                    placeholderTextColor={colors.muted}
                    style={styles.input}
                  />
                  <View style={styles.moderationRow}>
                    <Pressable style={styles.approveButton} disabled={busyId === event.id} onPress={() => void moderateEvent(event.id, "published")}>
                      <Text style={styles.approveText}>Onayla</Text>
                    </Pressable>
                    <Pressable style={styles.rejectButton} disabled={busyId === event.id} onPress={() => void moderateEvent(event.id, "rejected")}>
                      <Text style={styles.rejectText}>Reddet</Text>
                    </Pressable>
                  </View>
                </View>
              ))}
              {platformReviewEvents.length ? (
                <View style={styles.reviewQueue}>
                  <Text style={styles.reviewQueueTitle}>Bialem son kontrolünde</Text>
                  <Text style={styles.body}>Grup onayını verdiğin bu etkinlikler yeni partner güven kontrolünden sonra ortak Keşfet havuzunda yayınlanacak.</Text>
                  {platformReviewEvents.map((event) => <Text key={event.id} style={styles.reviewEvent}>• {event.title}</Text>)}
                </View>
              ) : null}
            </View>
          ) : null}

          {isModerator ? (
            <View style={styles.panel}>
              <View style={styles.sectionHeader}>
                <View style={styles.memberHeading}>
                  <Text style={styles.panelTitle}>Grup üyeleri</Text>
                  <Text style={styles.body}>Yalnızca standart üyeleri buradan yönetebilirsin.</Text>
                </View>
                <View style={styles.countBadge}><Text style={styles.countText}>{managedMembers.length}</Text></View>
              </View>
              {managedMembers.length === 0 ? (
                <Text style={styles.body}>Bu grupta henüz standart üye yok.</Text>
              ) : (
                <View style={styles.memberList}>
                  {managedMembers.map((member) => (
                    <View key={member.membership_id} style={styles.memberCard}>
                      <View style={styles.memberAvatar}>
                        <Text style={styles.memberAvatarText}>{member.display_name.trim().charAt(0).toLocaleUpperCase("tr-TR") || "Ü"}</Text>
                      </View>
                      <View style={styles.memberCopy}>
                        <Text style={styles.memberName} numberOfLines={1}>{member.display_name}</Text>
                        <Text style={styles.memberUsername} numberOfLines={1}>@{member.username}</Text>
                      </View>
                      <Pressable
                        accessibilityRole="button"
                        accessibilityLabel={`${member.display_name} adlı üyeyi gruptan çıkar`}
                        style={styles.removeMemberButton}
                        disabled={busyId === member.membership_id}
                        onPress={() => confirmRemoveMember(member)}
                      >
                        {busyId === member.membership_id
                          ? <ActivityIndicator size="small" color={colors.danger} />
                          : <Ionicons name="person-remove-outline" size={19} color={colors.danger} />}
                      </Pressable>
                    </View>
                  ))}
                </View>
              )}
            </View>
          ) : null}

          <View style={styles.sectionHeader}>
            <View><Text style={styles.kicker}>AÇIK ETKİNLİKLER</Text><Text style={styles.sectionTitle}>Birlikte yapacaklarını seç.</Text></View>
            <Text style={styles.totalText}>{publishedEvents.length} etkinlik</Text>
          </View>

          {publishedEvents.length === 0 ? (
            <View style={styles.emptyBox}><Ionicons name="calendar-outline" size={30} color={colors.accent} /><Text style={styles.panelTitle}>Henüz açık etkinlik yok</Text><Text style={styles.body}>İlk etkinlik fikrini sen önerebilirsin.</Text></View>
          ) : (
            <View style={styles.stack}>
              {publishedEvents.map((event) => (
                <Pressable key={event.id} style={styles.eventCard} onPress={() => router.push({ pathname: "/event/[id]", params: { id: event.id } })}>
                  <View style={styles.dateBox}><Text style={styles.dateDay}>{new Date(event.starts_at).getDate()}</Text><Text style={styles.dateMonth}>{new Date(event.starts_at).toLocaleDateString("tr-TR", { month: "short" }).toLocaleUpperCase("tr-TR")}</Text></View>
                  <View style={styles.eventCopy}>
                    <Text style={styles.eventTitle}>{event.title}</Text>
                    <Text style={styles.eventMeta}>{event.location_name || "Konum daha sonra açıklanacak"}</Text>
                    <Text style={styles.eventJoin}>Detayı aç ve katılım isteği gönder</Text>
                  </View>
                  <Ionicons name="chevron-forward" size={21} color={colors.accent} />
                </Pressable>
              ))}
            </View>
          )}
        </>
      )}
    </ScrollView>
  );
}

function formatDate(value: string) {
  return new Date(value).toLocaleString("tr-TR", { day: "2-digit", month: "long", year: "numeric", hour: "2-digit", minute: "2-digit" });
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 16, paddingBottom: 36, gap: 14, backgroundColor: colors.page },
  loadingBox: { minHeight: 360, alignItems: "center", justifyContent: "center" },
  hero: { padding: 18, gap: 8, borderRadius: 20, backgroundColor: colors.accent },
  kicker: { color: colors.action, fontSize: 11, fontWeight: "900", letterSpacing: 1.4 },
  title: { color: "#ffffff", fontSize: 27, lineHeight: 33, fontWeight: "900" },
  heroBody: { color: "rgba(255,255,255,0.86)", fontSize: 15, lineHeight: 22 },
  roleChip: { alignSelf: "flex-start", flexDirection: "row", alignItems: "center", gap: 6, marginTop: 4, paddingHorizontal: 11, paddingVertical: 7, borderRadius: 999, backgroundColor: colors.action },
  roleText: { color: colors.ink, fontSize: 12, fontWeight: "900" },
  heroOutlineButton: { alignSelf: "flex-start", flexDirection: "row", alignItems: "center", gap: 7, marginTop: 4, paddingHorizontal: 12, paddingVertical: 9, borderRadius: 999, borderWidth: 1, borderColor: "rgba(255,255,255,0.62)" },
  heroOutlineButtonText: { color: "#ffffff", fontSize: 12, fontWeight: "900" },
  body: { color: colors.muted, fontSize: 14, lineHeight: 21 },
  errorText: { color: colors.danger, backgroundColor: colors.surfaceStrong, borderRadius: 16, padding: 13, fontSize: 13, lineHeight: 19, fontWeight: "700" },
  noticeText: { color: colors.ink, backgroundColor: colors.accentSoft, borderRadius: 16, padding: 13, fontSize: 13, lineHeight: 19, fontWeight: "700" },
  panel: { padding: 15, gap: 11, borderRadius: 18, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  panelTitle: { color: colors.ink, fontSize: 20, fontWeight: "900" },
  actionPanel: { padding: 19, gap: 12, borderRadius: 27, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  actionIcon: { width: 48, height: 48, borderRadius: 16, alignItems: "center", justifyContent: "center", backgroundColor: colors.accentSoft },
  actionCopy: { gap: 5 },
  primaryButton: { minHeight: 44, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 8, borderRadius: 14, backgroundColor: colors.action },
  primaryButtonText: { color: colors.actionText, fontSize: 14, fontWeight: "900" },
  cancelRequestButton: { minHeight: 49, alignItems: "center", justifyContent: "center", borderRadius: 999, borderWidth: 1, borderColor: colors.accent, backgroundColor: colors.surface },
  cancelRequestButtonText: { color: colors.accent, fontSize: 14, fontWeight: "900" },
  sectionHeader: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", gap: 12 },
  sectionTitle: { marginTop: 4, color: colors.ink, fontSize: 24, lineHeight: 29, fontWeight: "900" },
  countBadge: { minWidth: 32, height: 32, alignItems: "center", justifyContent: "center", borderRadius: 16, backgroundColor: colors.accentSoft },
  countText: { color: colors.accent, fontSize: 13, fontWeight: "900" },
  memberHeading: { flex: 1, gap: 3 },
  memberList: { gap: 9 },
  memberCard: { flexDirection: "row", alignItems: "center", gap: 11, padding: 11, borderRadius: 18, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.page },
  memberAvatar: { width: 42, height: 42, alignItems: "center", justifyContent: "center", borderRadius: 21, backgroundColor: colors.accentSoft },
  memberAvatarText: { color: colors.accent, fontSize: 17, fontWeight: "900" },
  memberCopy: { flex: 1, gap: 2 },
  memberName: { color: colors.ink, fontSize: 14, fontWeight: "900" },
  memberUsername: { color: colors.muted, fontSize: 12, fontWeight: "700" },
  removeMemberButton: { width: 42, height: 42, alignItems: "center", justifyContent: "center", borderRadius: 14, borderWidth: 1, borderColor: colors.danger, backgroundColor: colors.surface },
  pendingCard: { padding: 15, gap: 9, borderRadius: 20, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.page },
  reviewQueue: { gap: 7, padding: 14, borderRadius: 18, backgroundColor: colors.accentSoft },
  reviewQueueTitle: { color: colors.accent, fontSize: 14, fontWeight: "900" },
  reviewEvent: { color: colors.ink, fontSize: 13, fontWeight: "800" },
  eventTitle: { color: colors.ink, fontSize: 17, fontWeight: "900" },
  eventMeta: { color: colors.muted, fontSize: 12, lineHeight: 18, fontWeight: "700" },
  input: { minHeight: 47, paddingHorizontal: 13, borderRadius: 15, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, color: colors.ink },
  moderationRow: { flexDirection: "row", gap: 9 },
  approveButton: { flex: 1, alignItems: "center", padding: 12, borderRadius: 999, backgroundColor: colors.action },
  approveText: { color: colors.actionText, fontWeight: "900" },
  rejectButton: { flex: 1, alignItems: "center", padding: 12, borderRadius: 999, borderWidth: 1, borderColor: colors.danger },
  rejectText: { color: colors.danger, fontWeight: "900" },
  totalText: { color: colors.muted, fontSize: 12, fontWeight: "800" },
  emptyBox: { alignItems: "center", padding: 25, gap: 8, borderRadius: 25, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  stack: { gap: 12 },
  eventCard: { flexDirection: "row", alignItems: "center", gap: 13, padding: 14, borderRadius: 23, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  dateBox: { width: 55, height: 61, alignItems: "center", justifyContent: "center", borderRadius: 17, backgroundColor: colors.brandInk },
  dateDay: { color: "#ffffff", fontSize: 22, lineHeight: 24, fontWeight: "900" },
  dateMonth: { color: colors.action, fontSize: 9, fontWeight: "900" },
  eventCopy: { flex: 1, gap: 4 },
  eventJoin: { color: colors.accent, fontSize: 11, fontWeight: "800" }
});
