import { Ionicons } from "@expo/vector-icons";
import { router, Stack, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, ImageBackground, Pressable, RefreshControl, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { showAppAlert, showAppConfirm, showAppError, showJoinCommunityResult } from "../../src/components/AppAlert";
import { ImagePickerField } from "../../src/components/ImagePickerField";
import { useAuth } from "../../src/lib/auth";
import { removeUploadedImage, uploadCommunityCover, type PickedImage } from "../../src/lib/storage";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";
import { getCommunityCover } from "../../src/theme/communityCovers";

type CommunityRecord = {
  id: string;
  name: string;
  slug: string;
  description: string | null;
  cover_image_url: string | null;
  community_type: "category_hub" | "partner_hub" | "group";
  partner_trust_level: "new" | "verified" | "trusted";
  is_verified_partner: boolean;
};

type GroupRecord = CommunityRecord & { created_by: string; category_id: string | null };
type CategoryRecord = { id: string; name: string; slug: string };
type Membership = { community_id: string; role: "member" | "manager" | "owner"; status: string };
type AssistantPermissions = {
  can_manage_groups: boolean;
  can_review_events: boolean;
  can_manage_participants: boolean;
};

const emptyAssistantPermissions: AssistantPermissions = {
  can_manage_groups: false,
  can_review_events: false,
  can_manage_participants: false
};

type PendingMembership = {
  membership_id: string;
  community_id: string;
  community_name: string;
  user_id: string;
  display_name: string;
  username: string;
  avatar_url: string | null;
  created_at: string;
};

export default function CommunityDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { user } = useAuth();
  const [community, setCommunity] = useState<CommunityRecord | null>(null);
  const [groups, setGroups] = useState<GroupRecord[]>([]);
  const [categories, setCategories] = useState<CategoryRecord[]>([]);
  const [memberships, setMemberships] = useState<Record<string, Membership>>({});
  const [pendingMemberships, setPendingMemberships] = useState<PendingMembership[]>([]);
  const [assistantPermissions, setAssistantPermissions] = useState<AssistantPermissions>(emptyAssistantPermissions);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [busy, setBusy] = useState(false);
  const [reviewingMembershipId, setReviewingMembershipId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [showGroupForm, setShowGroupForm] = useState(false);
  const [groupName, setGroupName] = useState("");
  const [groupSlug, setGroupSlug] = useState("");
  const [groupDescription, setGroupDescription] = useState("");
  const [groupCover, setGroupCover] = useState<PickedImage | null>(null);
  const [groupCategoryId, setGroupCategoryId] = useState("");

  const loadCommunity = async (mode: "initial" | "refresh" = "initial") => {
    if (!id || !user) return;
    if (mode === "initial") setLoading(true);
    else setRefreshing(true);
    setError(null);

    const [communityResult, groupsResult, membershipsResult, categoriesResult, assistantResult, pendingMembershipsResult] = await Promise.all([
      api.from("communities").select("id, name, slug, description, cover_image_url, community_type, partner_trust_level, is_verified_partner").eq("id", id).is("parent_id", null).maybeSingle(),
      api.from("communities").select("id, name, slug, description, cover_image_url, created_by, category_id, community_type, partner_trust_level, is_verified_partner").eq("parent_id", id).order("created_at", { ascending: false }),
      api.from("community_members").select("community_id, role, status").eq("user_id", user.id),
      api.from("communities").select("id, name, slug").eq("community_type", "category_hub").is("parent_id", null).order("name"),
      api.rpc("get_my_community_assistant_permissions", { target_community_id: id }),
      api.rpc("get_pending_managed_community_memberships", { target_root_community_id: id })
    ]);

    if (communityResult.error || membershipsResult.error) {
      setError(communityResult.error?.message || membershipsResult.error?.message || "Topluluk yüklenemedi.");
    } else {
      setCommunity((communityResult.data as CommunityRecord | null) ?? null);
      setGroups(groupsResult.error ? [] : ((groupsResult.data ?? []) as GroupRecord[]));
      const nextCategories = categoriesResult.error ? [] : ((categoriesResult.data ?? []) as CategoryRecord[]);
      setCategories(nextCategories);
      setAssistantPermissions(
        assistantResult.error
          ? emptyAssistantPermissions
          : (((assistantResult.data ?? [])[0] as AssistantPermissions | undefined) ?? emptyAssistantPermissions)
      );
      setPendingMemberships(
        pendingMembershipsResult.error
          ? []
          : ((pendingMembershipsResult.data ?? []) as PendingMembership[])
      );
      setGroupCategoryId((current) => current || nextCategories[0]?.id || "");
      setMemberships(
        ((membershipsResult.data ?? []) as Membership[]).reduce<Record<string, Membership>>((result, membership) => {
          result[membership.community_id] = membership;
          return result;
        }, {})
      );
    }

    setLoading(false);
    setRefreshing(false);
  };

  useEffect(() => {
    void loadCommunity();
  }, [id, user?.id]);

  const communityMembership = id ? memberships[id] : undefined;
  const joined = communityMembership?.status === "approved";
  const requestPending = communityMembership?.status === "pending";
  const requestBlocked = communityMembership?.status === "blocked";
  const isModerator = joined && communityMembership.role !== "member";
  const managesChildGroup = groups.some((group) => {
    const membership = memberships[group.id];
    return membership?.status === "approved" && membership.role !== "member";
  });
  const canReviewMemberships = isModerator || managesChildGroup || pendingMemberships.length > 0;
  const isAssistant = Object.values(assistantPermissions).some(Boolean);
  const canCreateGroups = isModerator || assistantPermissions.can_manage_groups;
  const hasCommunityAccess = joined || isModerator || isAssistant;
  const canLeaveCommunity = joined && communityMembership?.role === "member" && !isModerator && !isAssistant;

  const join = async (targetId: string) => {
    if (busy) return;
    setBusy(true);
    setError(null);
    const { data, error: joinError } = await api.rpc("join_community", { target_community_id: targetId });
    if (joinError) {
      setError(joinError.message);
      await showJoinCommunityResult(data, joinError, targetId === id ? "community" : "group");
    } else {
      await showJoinCommunityResult(data, null, targetId === id ? "community" : "group");
      await loadCommunity("refresh");
    }
    setBusy(false);
  };

  const cancelMembershipRequest = async (targetId: string) => {
    if (busy) return;
    setBusy(true);
    setError(null);
    setNotice(null);
    const { error: cancelError } = await api.rpc("cancel_community_membership_request", {
      target_community_id: targetId
    });
    if (cancelError) {
      setError(cancelError.message);
      await showAppError(cancelError.message);
    } else {
      await showAppAlert({
        title: "İstek geri çekildi",
        text: targetId === id ? "Katılım isteğin iptal edildi." : "Grup katılım isteğin iptal edildi.",
        icon: "success"
      });
      await loadCommunity("refresh");
    }
    setBusy(false);
  };

  const confirmLeaveCommunity = async () => {
    if (!id || busy) return;
    const confirmed = await showAppConfirm({
      title: "Topluluktan ayrıl",
      text: "Ana topluluktan ayrıldığında bağlı olduğun alt gruplardan da çıkarsın. Devam etmek istiyor musun?",
      confirmText: "Ayrıl",
      confirmDanger: true
    });
    if (!confirmed) return;
    setBusy(true);
    setError(null);
    const { error: leaveError } = await api.rpc("leave_community", { target_community_id: id });
    if (leaveError) {
      setError(leaveError.message);
      await showAppError(leaveError.message);
    } else {
      await showAppAlert({ title: "Ayrıldın", text: "Topluluktan ayrıldın.", icon: "success" });
      await loadCommunity("refresh");
    }
    setBusy(false);
  };

  const reviewMembership = async (membershipId: string, status: "approved" | "rejected") => {
    if (reviewingMembershipId) return;
    setReviewingMembershipId(membershipId);
    setError(null);
    setNotice(null);

    const { error: reviewError } = await api.rpc("review_community_membership", {
      target_membership_id: membershipId,
      target_status: status
    });

    if (reviewError) {
      setError(reviewError.message);
    } else {
      setNotice(status === "approved" ? "Katılım isteği onaylandı." : "Katılım isteği reddedildi.");
      await loadCommunity("refresh");
    }
    setReviewingMembershipId(null);
  };

  const createGroup = async () => {
    if (!id || !user || busy || !groupName.trim() || !groupSlug.trim()) {
      setError("Grup adı ve kısa adres zorunludur.");
      return;
    }

    setBusy(true);
    setError(null);
    setNotice(null);
    if (community?.community_type === "partner_hub" && !groupCategoryId) {
      setError("Partner grubunun Keşfet kategorisini seçmelisin.");
      setBusy(false);
      return;
    }

    let uploadedCover: Awaited<ReturnType<typeof uploadCommunityCover>> | null = null;
    try {
      if (groupCover) {
        uploadedCover = await uploadCommunityCover({ userId: user.id, image: groupCover });
      }
    } catch (uploadError) {
      setError(uploadError instanceof Error ? uploadError.message : "Kapak görseli yüklenemedi.");
      setBusy(false);
      return;
    }

    const rpcName = community?.community_type === "partner_hub" ? "create_partner_group" : "create_community_group";
    const rpcParams = community?.community_type === "partner_hub"
      ? {
          target_partner_id: id,
          target_category_id: groupCategoryId,
          target_name: groupName.trim(),
          target_slug: groupSlug.trim(),
          target_description: groupDescription.trim() || null,
          target_cover_image_url: uploadedCover?.storagePath ?? null
        }
      : {
          target_parent_id: id,
          target_name: groupName.trim(),
          target_slug: groupSlug.trim(),
          target_description: groupDescription.trim() || null,
          target_cover_image_url: uploadedCover?.storagePath ?? null
        };
    const { error: createError } = await api.rpc(rpcName, rpcParams);

    if (createError) {
      if (uploadedCover) {
        await removeUploadedImage("community-covers", uploadedCover.bucketPath).catch(() => undefined);
      }
      setError(createError.message);
    }
    else {
      setGroupName("");
      setGroupSlug("");
      setGroupDescription("");
      setGroupCover(null);
      setShowGroupForm(false);
      setNotice("Grup oluşturuldu. Bu grubun moderasyonu sana atandı.");
      await loadCommunity("refresh");
    }
    setBusy(false);
  };

  return (
    <ScrollView
      contentContainerStyle={styles.page}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadCommunity("refresh")} tintColor={colors.accent} />}
    >
      <Stack.Screen options={{ headerShown: true, title: community?.name || "Topluluk" }} />

      {loading ? (
        <View style={styles.loadingBox}><ActivityIndicator color={colors.accent} size="large" /></View>
      ) : !community ? (
        <View style={styles.panel}><Text style={styles.panelTitle}>Topluluk bulunamadı</Text><Text style={styles.body}>{error}</Text></View>
      ) : (
        <>
          <View style={styles.hero}>
            <Text style={styles.kicker}>{community.community_type === "partner_hub" ? "PARTNER TOPLULUK" : "BİALEM İLGİ ALANI"}</Text>
            <Text style={styles.title}>{community.name}</Text>
            <Text style={[styles.body, styles.heroBody]}>{community.description || "Bu topluluk yeni gruplar ve etkinlikler için kuruluyor."}</Text>
            {community.community_type === "partner_hub" ? (
              <View style={styles.partnerBadge}>
                <Ionicons name={community.is_verified_partner ? "shield-checkmark" : "hourglass-outline"} size={16} color={community.is_verified_partner ? colors.aqua : colors.action} />
                <Text style={styles.partnerBadgeText}>{community.is_verified_partner ? "Doğrulanmış partner" : "Yeni partner - etkinlikler ek kontrolden geçer"}</Text>
              </View>
            ) : null}
            <View style={styles.statusRow}>
              <View style={[styles.statusDot, joined && styles.statusDotActive]} />
              <Text style={styles.statusText}>
                {isModerator
                  ? community.community_type === "partner_hub"
                    ? "Bağımsız topluluk yöneticisisin"
                    : "Bialem topluluk moderatörüsün"
                  : isAssistant
                    ? "Moderatör yardımcısısın"
                    : joined
                      ? "Topluluk üyesisin"
                      : requestPending
                        ? "Katılım isteğin moderatör onayında"
                        : requestBlocked
                          ? "Bu topluluğa katılımın engellendi"
                          : "Grupları görmek için katılım isteği gönder"}
              </Text>
            </View>
            {isModerator ? (
              <Pressable style={styles.assistantButton} onPress={() => router.push({ pathname: "/community/[id]/assistants", params: { id: community.id } })}>
                <Ionicons name="people-circle" size={19} color={colors.onBrand} />
                <Text style={styles.assistantButtonText}>Yardımcıları yönet</Text>
              </Pressable>
            ) : null}
            {hasCommunityAccess ? (
              <Pressable
                style={styles.heroOutlineButton}
                onPress={() => router.push({ pathname: "/community/[id]/members", params: { id: community.id, name: community.name } })}
              >
                <Ionicons name="people" size={18} color={colors.onBrand} />
                <Text style={styles.heroOutlineButtonText}>Topluluk üyelerini gör</Text>
              </Pressable>
            ) : null}
            {canLeaveCommunity ? (
              <Pressable style={styles.heroOutlineButton} disabled={busy} onPress={confirmLeaveCommunity}>
                <Ionicons name="exit-outline" size={18} color={colors.onBrand} />
                <Text style={styles.heroOutlineButtonText}>Topluluktan ayrıl</Text>
              </Pressable>
            ) : null}
          </View>

          {error ? <Text style={styles.errorText}>{error}</Text> : null}
          {notice ? <Text style={styles.noticeText}>{notice}</Text> : null}

          {!hasCommunityAccess ? (
            <View style={styles.joinPanel}>
              <Ionicons name="lock-closed" size={29} color={colors.accent} />
              <Text style={styles.panelTitle}>Gruplar üyelere açık</Text>
              <Text style={styles.body}>
                {requestPending
                  ? "Başvurun topluluk moderatörü tarafından inceleniyor. Onaylandığında gruplar ve topluluk etkinlikleri açılacak."
                  : requestBlocked
                    ? "Bu topluluk için yeni katılım isteği gönderemezsin."
                    : "Katılım isteğin moderatör tarafından onaylandığında ilgi gruplarını ve topluluk etkinliklerini görebilirsin."}
              </Text>
              {!requestPending && !requestBlocked ? (
                <Pressable style={styles.primaryButton} disabled={busy} onPress={() => void join(id)}>
                  {busy ? (
                    <ActivityIndicator color={colors.actionText} />
                  ) : (
                    <Text style={styles.primaryButtonText}>
                      {communityMembership?.status === "rejected" ? "Yeniden katılım isteği gönder" : "Katılım isteği gönder"}
                    </Text>
                  )}
                </Pressable>
              ) : null}
              {requestPending ? (
                <Pressable style={styles.outlineButton} disabled={busy} onPress={() => void cancelMembershipRequest(id)}>
                  <Text style={styles.outlineButtonText}>Katılım isteğini geri çek</Text>
                </Pressable>
              ) : null}
            </View>
          ) : (
            <>
              {canReviewMemberships ? (
                <View style={styles.panel}>
                  <View style={styles.membershipHeader}>
                    <View style={styles.sectionCopy}>
                      <Text style={styles.kicker}>ÜYELİK BAŞVURULARI</Text>
                      <Text style={styles.panelTitle}>Onay bekleyenler</Text>
                    </View>
                    <View style={styles.countBadge}>
                      <Text style={styles.countBadgeText}>{pendingMemberships.length}</Text>
                    </View>
                  </View>
                  {pendingMemberships.length === 0 ? (
              <Text style={styles.body}>Şu anda bekleyen katılım isteği yok.</Text>
                  ) : (
                    <View style={styles.membershipList}>
                      {pendingMemberships.map((membership) => (
                        <View key={membership.membership_id} style={styles.membershipCard}>
                          <View style={styles.memberAvatar}>
                            <Text style={styles.memberAvatarText}>{membership.display_name.slice(0, 1).toLocaleUpperCase("tr-TR")}</Text>
                          </View>
                          <View style={styles.memberCopy}>
                            <Text style={styles.memberName}>{membership.display_name}</Text>
                            <Text style={styles.memberUsername}>@{membership.username}</Text>
                            <Text style={styles.memberCommunity}>{membership.community_name}</Text>
                          </View>
                          <View style={styles.membershipActions}>
                            <Pressable
                              accessibilityLabel={`${membership.display_name} başvurusunu onayla`}
                              style={styles.approveMembershipButton}
                              disabled={reviewingMembershipId === membership.membership_id}
                              onPress={() => void reviewMembership(membership.membership_id, "approved")}
                            >
                              <Ionicons name="checkmark" size={20} color={colors.ink} />
                            </Pressable>
                            <Pressable
                              accessibilityLabel={`${membership.display_name} başvurusunu reddet`}
                              style={styles.rejectMembershipButton}
                              disabled={reviewingMembershipId === membership.membership_id}
                              onPress={() => void reviewMembership(membership.membership_id, "rejected")}
                            >
                              <Ionicons name="close" size={20} color={colors.danger} />
                            </Pressable>
                          </View>
                        </View>
                      ))}
                    </View>
                  )}
                </View>
              ) : null}

              <View style={styles.sectionHeader}>
                <View style={styles.sectionCopy}>
                  <Text style={styles.kicker}>ALT GRUPLAR</Text>
            <Text style={styles.sectionTitle}>Kendine uygun alanı seç.</Text>
                </View>
                {canCreateGroups ? (
                  <Pressable style={styles.addButton} onPress={() => setShowGroupForm((value) => !value)}>
                    <Ionicons name={showGroupForm ? "close" : "add"} size={22} color="#ffffff" />
                  </Pressable>
                ) : null}
              </View>

              {canCreateGroups && showGroupForm ? (
                <View style={styles.panel}>
                  <Text style={styles.panelTitle}>Yeni grup oluştur</Text>
                  <Text style={styles.body}>{community.community_type === "partner_hub" ? "Partner yöneticisi grubu yönetir; kategori seçimi yalnızca ortak Keşfet havuzundaki yerini belirler." : "Bu alan yalnızca topluluk moderatörlerine açıktır."}</Text>
                  {community.community_type === "partner_hub" ? (
                    <View style={styles.field}>
                      <Text style={styles.fieldLabel}>Keşfet kategorisi</Text>
                      <View style={styles.categoryChoices}>
                        {categories.map((category) => (
                          <Pressable key={category.id} style={[styles.categoryChip, groupCategoryId === category.id && styles.categoryChipActive]} onPress={() => setGroupCategoryId(category.id)}>
                            <Text style={[styles.categoryChipText, groupCategoryId === category.id && styles.categoryChipTextActive]}>{category.name}</Text>
                          </Pressable>
                        ))}
                      </View>
                    </View>
                  ) : null}
              <Field label="Grup adı" value={groupName} onChangeText={setGroupName} placeholder="Örnek: Hafta Sonu Koşucuları" />
                  <Field label="Kısa adres" value={groupSlug} onChangeText={setGroupSlug} placeholder="hafta-sonu-kosuculari" />
                  <Field label="Açıklama" value={groupDescription} onChangeText={setGroupDescription} placeholder="Grubun amacı ve etkinlik türleri" multiline />
                  <ImagePickerField
                    image={groupCover}
                    onChange={setGroupCover}
                    onError={setError}
                    disabled={busy}
                    label="Grup kapak görseli"
                  />
                  <Pressable style={styles.primaryButton} disabled={busy} onPress={() => void createGroup()}>
                    {busy ? <ActivityIndicator color={colors.actionText} /> : <Text style={styles.primaryButtonText}>Grubu oluştur</Text>}
                  </Pressable>
                </View>
              ) : null}

              {groups.length === 0 ? (
                <View style={styles.emptyBox}><Text style={styles.panelTitle}>Henüz grup yok</Text><Text style={styles.body}>{canCreateGroups ? "İlk grubu + düğmesiyle oluşturabilirsin." : "Topluluk moderatörleri yakında yeni gruplar açacak."}</Text></View>
              ) : (
                <View style={styles.stack}>
                  {groups.map((group, index) => {
                    const groupMembership = memberships[group.id];
                    const groupJoined = groupMembership?.status === "approved";
                    const groupRequestPending = groupMembership?.status === "pending";
                    const groupRequestBlocked = groupMembership?.status === "blocked";
                    const groupModerator = groupJoined && groupMembership.role !== "member";
                    const inheritedPartnerManager = community.community_type === "partner_hub" && isModerator;
                    const assistantGroupManager = assistantPermissions.can_review_events || assistantPermissions.can_manage_participants;
                    const canManageGroup = groupModerator || inheritedPartnerManager || assistantGroupManager;
                    const canOpenGroup = groupJoined || canManageGroup || assistantPermissions.can_manage_groups;
                    const groupCoverSource = getCommunityCover(community.slug, group.cover_image_url);
                    return (
                      <View key={group.id} style={styles.groupCard}>
                        {groupCoverSource ? (
                          <ImageBackground source={groupCoverSource} style={styles.groupCover} imageStyle={styles.groupCoverImage}>
                            <View style={styles.groupShade} />
                            <Text style={styles.groupIndex}>{String(index + 1).padStart(2, "0")}</Text>
                          </ImageBackground>
                        ) : (
                          <View style={[styles.groupCover, styles.fallbackCover]}><Text style={styles.groupIndex}>{String(index + 1).padStart(2, "0")}</Text><Ionicons name="sparkles" size={34} color="#ffffff" /></View>
                        )}
                        <View style={styles.groupBody}>
                          {community.community_type === "partner_hub" && group.category_id ? <Text style={styles.groupCategory}>{categories.find((category) => category.id === group.category_id)?.name || "Keşfet"}</Text> : null}
                          <Text style={styles.groupName}>{group.name}</Text>
                          <Text style={styles.body}>{group.description || "Yeni etkinlik fikirleri için açık grup."}</Text>
                          {canOpenGroup ? (
                            <Pressable style={styles.primaryButton} onPress={() => router.push({ pathname: "/group/[id]", params: { id: group.id } })}>
                              <Text style={styles.primaryButtonText}>{canManageGroup ? "Grubu yönet" : "Gruba gir"}</Text>
                            </Pressable>
                          ) : groupRequestPending ? (
                            <Pressable style={styles.outlineButton} disabled={busy} onPress={() => void cancelMembershipRequest(group.id)}>
                              <Text style={styles.outlineButtonText}>Katılım isteğini geri çek</Text>
                            </Pressable>
                          ) : (
                            <Pressable
                              style={[styles.outlineButton, groupRequestBlocked && styles.disabledButton]}
                              disabled={busy || groupRequestBlocked}
                              onPress={() => void join(group.id)}
                            >
                              <Text style={styles.outlineButtonText}>
                                {groupRequestBlocked
                                    ? "Katılım engellendi"
                                    : groupMembership?.status === "rejected"
                                      ? "Yeniden istek gönder"
                                      : "Gruba katılım isteği gönder"}
                              </Text>
                            </Pressable>
                          )}
                        </View>
                      </View>
                    );
                  })}
                </View>
              )}
            </>
          )}
        </>
      )}
    </ScrollView>
  );
}

function Field({ label, multiline = false, ...props }: { label: string; value: string; onChangeText: (value: string) => void; placeholder: string; multiline?: boolean }) {
  return <View style={styles.field}><Text style={styles.fieldLabel}>{label}</Text><TextInput {...props} multiline={multiline} placeholderTextColor={colors.muted} style={[styles.input, multiline && styles.textArea]} /></View>;
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 16, paddingBottom: 36, gap: 14, backgroundColor: colors.page },
  loadingBox: { minHeight: 360, alignItems: "center", justifyContent: "center" },
  hero: { overflow: "hidden", padding: 18, gap: 8, borderRadius: 20, backgroundColor: colors.brandInk },
  kicker: { color: colors.action, fontSize: 11, fontWeight: "900", letterSpacing: 1.5 },
  title: { color: colors.onBrand, fontSize: 27, lineHeight: 33, fontWeight: "900" },
  body: { color: colors.muted, fontSize: 14, lineHeight: 21 },
  heroBody: { color: colors.onBrandMuted },
  statusRow: { marginTop: 6, flexDirection: "row", alignItems: "center", gap: 8 },
  statusDot: { width: 9, height: 9, borderRadius: 5, backgroundColor: colors.muted },
  statusDotActive: { backgroundColor: colors.aqua },
  assistantButton: { alignSelf: "flex-start", flexDirection: "row", alignItems: "center", gap: 8, marginTop: 4, paddingHorizontal: 13, paddingVertical: 10, borderRadius: 999, backgroundColor: colors.accent },
  assistantButtonText: { color: colors.onBrand, fontSize: 12, fontWeight: "900" },
  heroOutlineButton: { alignSelf: "flex-start", flexDirection: "row", alignItems: "center", gap: 8, marginTop: 4, paddingHorizontal: 13, paddingVertical: 10, borderRadius: 999, borderWidth: 1, borderColor: "rgba(255,255,255,0.55)" },
  heroOutlineButtonText: { color: colors.onBrand, fontSize: 12, fontWeight: "900" },
  statusText: { color: colors.onBrand, fontSize: 13, fontWeight: "700" },
  partnerBadge: { marginTop: 3, alignSelf: "flex-start", flexDirection: "row", alignItems: "center", gap: 7, borderRadius: 999, paddingHorizontal: 11, paddingVertical: 7, backgroundColor: "rgba(255,255,255,0.12)" },
  partnerBadgeText: { color: colors.onBrand, fontSize: 11, fontWeight: "800" },
  errorText: { color: colors.danger, backgroundColor: colors.surfaceStrong, borderRadius: 16, padding: 13, fontSize: 13, lineHeight: 19, fontWeight: "700" },
  noticeText: { color: colors.ink, backgroundColor: colors.accentSoft, borderRadius: 16, padding: 13, fontSize: 13, lineHeight: 19, fontWeight: "700" },
  joinPanel: { alignItems: "center", padding: 26, gap: 12, borderRadius: 28, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  panel: { padding: 15, gap: 11, borderRadius: 18, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  panelTitle: { color: colors.ink, fontSize: 21, fontWeight: "900" },
  membershipHeader: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", gap: 12 },
  countBadge: { minWidth: 34, height: 34, alignItems: "center", justifyContent: "center", borderRadius: 17, backgroundColor: colors.accentSoft },
  countBadgeText: { color: colors.accent, fontSize: 13, fontWeight: "900" },
  membershipList: { gap: 10 },
  membershipCard: { flexDirection: "row", alignItems: "center", gap: 11, padding: 12, borderRadius: 18, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.page },
  memberAvatar: { width: 42, height: 42, alignItems: "center", justifyContent: "center", borderRadius: 21, backgroundColor: colors.accentSoft },
  memberAvatarText: { color: colors.accent, fontSize: 17, fontWeight: "900" },
  memberCopy: { flex: 1, gap: 2 },
  memberName: { color: colors.ink, fontSize: 14, fontWeight: "900" },
  memberUsername: { color: colors.muted, fontSize: 12, fontWeight: "700" },
  memberCommunity: { color: colors.accent, fontSize: 11, fontWeight: "800" },
  membershipActions: { flexDirection: "row", gap: 7 },
  approveMembershipButton: { width: 40, height: 40, alignItems: "center", justifyContent: "center", borderRadius: 14, backgroundColor: colors.action },
  rejectMembershipButton: { width: 40, height: 40, alignItems: "center", justifyContent: "center", borderRadius: 14, borderWidth: 1, borderColor: colors.danger, backgroundColor: colors.surface },
  sectionHeader: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", gap: 12 },
  sectionCopy: { flex: 1, gap: 4 },
  sectionTitle: { color: colors.ink, fontSize: 25, fontWeight: "900" },
  addButton: { width: 46, height: 46, borderRadius: 16, alignItems: "center", justifyContent: "center", backgroundColor: colors.accent },
  emptyBox: { padding: 22, gap: 8, borderRadius: 24, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  stack: { gap: 16 },
  groupCard: { overflow: "hidden", borderRadius: 27, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  groupCover: { height: 130, padding: 15, flexDirection: "row", justifyContent: "space-between", alignItems: "flex-start" },
  groupCoverImage: { borderTopLeftRadius: 26, borderTopRightRadius: 26 },
  groupShade: { ...StyleSheet.absoluteFillObject, backgroundColor: "rgba(8,26,68,0.28)" },
  fallbackCover: { backgroundColor: colors.accent },
  groupIndex: { color: "#ffffff", fontSize: 13, fontWeight: "900", letterSpacing: 1 },
  groupBody: { padding: 17, gap: 11 },
  groupCategory: { alignSelf: "flex-start", color: colors.accent, fontSize: 10, fontWeight: "900", letterSpacing: 1, textTransform: "uppercase" },
  groupName: { color: colors.ink, fontSize: 21, fontWeight: "900" },
  primaryButton: { minHeight: 44, flexDirection: "row", alignItems: "center", justifyContent: "center", borderRadius: 14, paddingHorizontal: 14, backgroundColor: colors.action },
  primaryButtonText: { color: colors.actionText, fontSize: 14, fontWeight: "900" },
  outlineButton: { minHeight: 44, alignItems: "center", justifyContent: "center", borderRadius: 14, borderWidth: 1, borderColor: colors.accent },
  outlineButtonText: { color: colors.accent, fontSize: 14, fontWeight: "900" },
  disabledButton: { opacity: 0.55 },
  field: { gap: 7 },
  fieldLabel: { color: colors.ink, fontSize: 13, fontWeight: "800" },
  categoryChoices: { flexDirection: "row", flexWrap: "wrap", gap: 8 },
  categoryChip: { borderRadius: 999, borderWidth: 1, borderColor: colors.border, paddingHorizontal: 12, paddingVertical: 9, backgroundColor: colors.page },
  categoryChipActive: { borderColor: colors.accent, backgroundColor: colors.accentSoft },
  categoryChipText: { color: colors.muted, fontSize: 12, fontWeight: "800" },
  categoryChipTextActive: { color: colors.accent },
  input: { minHeight: 51, paddingHorizontal: 14, paddingVertical: 12, borderRadius: 16, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.page, color: colors.ink, fontSize: 15 },
  textArea: { minHeight: 100, textAlignVertical: "top" }
});
