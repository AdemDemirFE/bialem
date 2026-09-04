import { Ionicons } from "@expo/vector-icons";
import { router } from "expo-router";
import { useDeferredValue, useEffect, useState } from "react";
import {
  ActivityIndicator,
  ImageBackground,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";
import { showAppAlert, showAppError, showJoinCommunityResult } from "../../src/components/AppAlert";
import { FeedbackState } from "../../src/components/ui/FeedbackState";
import { Reveal } from "../../src/animations";
import { useAuth } from "../../src/lib/auth";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";
import { getCommunityCover } from "../../src/theme/communityCovers";

type Community = {
  id: string;
  name: string;
  slug: string;
  description: string | null;
  cover_image_url: string | null;
  community_type: "category_hub" | "partner_hub";
  partner_trust_level: "new" | "verified" | "trusted";
  is_verified_partner: boolean;
  created_at: string;
  membership_status?: string | null;
  membership_role?: Membership["role"] | null;
  is_member?: boolean;
};

type Membership = {
  community_id: string;
  role: "member" | "manager" | "owner";
  status: string;
};

type CommunityView = "official" | "local" | "mine";
type PartnerFilter = "all" | "verified" | "new";

type CommunityResponse = Partial<Community> & {
  community_id?: string | number | null;
  communityId?: string | number | null;
  coverImageUrl?: string | null;
  communityType?: Community["community_type"];
  partnerTrustLevel?: Community["partner_trust_level"];
  isVerifiedPartner?: boolean;
  createdAt?: string;
  membershipStatus?: string | null;
  membershipRole?: Membership["role"] | null;
  isMember?: boolean;
  data?: CommunityResponse | null;
  community?: CommunityResponse | null;
  communities?: CommunityResponse | null;
};

type CommunityMemberDto = {
  community?: { id: number; name?: string; slug?: string; coverImageUrl?: string | null } | null;
  role?: string | null;
  status?: string | null;
};

const PAGE_SIZE = 6;
const NEW_COMMUNITY_DAYS = 14;
const fallbackColors = ["#7047d7", "#168aaf", "#f6a51c", "#176b87"];

const viewOptions: Array<{ id: CommunityView; label: string; icon: keyof typeof Ionicons.glyphMap }> = [
  { id: "official", label: "Bialem", icon: "sparkles" },
  { id: "local", label: "Yerel & WhatsApp", icon: "location" },
  { id: "mine", label: "Topluluklarım", icon: "bookmark" }
];

const partnerFilters: Array<{ id: PartnerFilter; label: string }> = [
  { id: "all", label: "Tümü" },
  { id: "verified", label: "Doğrulanmış" },
  { id: "new", label: "Yeni partnerler" }
];

function isRecentlyAdded(createdAt: string) {
  const createdTime = new Date(createdAt).getTime();
  return Number.isFinite(createdTime) && Date.now() - createdTime <= NEW_COMMUNITY_DAYS * 24 * 60 * 60 * 1000;
}

function matchesSearch(community: Community, query: string) {
  if (!query) return true;
  const searchableText = `${community.name} ${community.description ?? ""}`.toLocaleLowerCase("tr-TR");
  return searchableText.includes(query);
}

function normalizeCommunity(row: CommunityResponse): Community | null {
  const nested = row.data ?? row.community ?? row.communities ?? {};
  const value = { ...row, ...nested };
  const id = String(value.id ?? value.community_id ?? value.communityId ?? "");
  if (!id || id === "null" || id === "undefined") return null;
  return {
    ...value,
    id,
    name: String(value.name ?? "").trim() || `Topluluk #${id}`,
    slug: String(value.slug ?? id),
    description: value.description ?? null,
    cover_image_url: value.cover_image_url ?? value.coverImageUrl ?? null,
    community_type: value.community_type ?? value.communityType ?? "category_hub",
    partner_trust_level: value.partner_trust_level ?? value.partnerTrustLevel ?? "new",
    is_verified_partner: value.is_verified_partner ?? value.isVerifiedPartner ?? false,
    created_at: value.created_at ?? value.createdAt ?? new Date(0).toISOString(),
    membership_status: row.membership_status ?? row.membershipStatus ?? value.membership_status ?? value.membershipStatus ?? null,
    membership_role: row.membership_role ?? row.membershipRole ?? value.membership_role ?? value.membershipRole ?? null,
    is_member: row.is_member ?? row.isMember ?? value.is_member ?? value.isMember ?? false
  };
}

export default function CommunitiesScreen() {
  const { user } = useAuth();
  const [communities, setCommunities] = useState<Community[]>([]);
  const [memberships, setMemberships] = useState<Record<string, Membership>>({});
  const [assistantCommunityIds, setAssistantCommunityIds] = useState<string[]>([]);
  const [activeView, setActiveView] = useState<CommunityView>("official");
  const [partnerFilter, setPartnerFilter] = useState<PartnerFilter>("all");
  const [searchQuery, setSearchQuery] = useState("");
  const deferredSearchQuery = useDeferredValue(searchQuery.trim().toLocaleLowerCase("tr-TR"));
  const [visibleCount, setVisibleCount] = useState(PAGE_SIZE);
  const [joiningId, setJoiningId] = useState<string | null>(null);
  const [failedCoverIds, setFailedCoverIds] = useState<Set<string>>(new Set());
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadCommunities = async (mode: "initial" | "refresh" = "initial") => {
    if (mode === "initial") setLoading(true);
    else setRefreshing(true);
    setError(null);

    const [communitiesResult, assistantsResult, membershipDetailsResult] = await Promise.all([
      user ? api.rpc("get_communities_with_my_membership") : api.communities.list({ parentId: null, sort: "createdAt,desc" }),
      user
        ? api.from("community_moderator_assistants").select("community_id").eq("user_id", user.id)
        : Promise.resolve({ data: [], error: null }),
      user
        ? api.communityMembers.listByUser(user.id)
        : Promise.resolve({ data: [], error: null })
    ]);

    if (communitiesResult.error) {
      setError(communitiesResult.error.message || "Topluluklar yüklenemedi.");
    } else {
      const rpcCommunities = ((communitiesResult.data ?? []) as CommunityResponse[])
        .map(normalizeCommunity)
        .filter((community): community is Community => community !== null);
      const membershipCommunities = ((membershipDetailsResult.data ?? []) as CommunityMemberDto[])
        .map((membership) => normalizeCommunity({
          ...(membership.community ?? {}),
          id: String(membership.community?.id ?? ""),
          community_id: String(membership.community?.id ?? ""),
          membership_status: membership.status ?? null,
          membership_role: membership.role as Membership["role"] | null ?? null,
          is_member: String(membership.status ?? "").toLowerCase() === "approved"
        }))
        .filter((community): community is Community => community !== null && !community.name.startsWith("Topluluk #"));
      const realMembershipIds = new Set(membershipCommunities.map((community) => community.id));
      const loadedCommunities = [...new Map([
        ...rpcCommunities.filter((community) => !community.name.startsWith("Topluluk #") || realMembershipIds.has(community.id)),
        ...membershipCommunities
      ].map((community) => [community.id, community])).values()];
      setCommunities(loadedCommunities);
      setMemberships(
        loadedCommunities.reduce<Record<string, Membership>>((result, community) => {
          if (!community.membership_status) return result;
          result[community.id] = {
            community_id: community.id,
            role: String(community.membership_role ?? "member").toLowerCase() as Membership["role"],
            status: String(community.membership_status).toLowerCase()
          };
          return result;
        }, {})
      );
      setAssistantCommunityIds(assistantsResult.error ? [] : ((assistantsResult.data ?? []) as Array<{ community_id: string }>).map((assistant) => String(assistant.community_id)));
    }

    setLoading(false);
    setRefreshing(false);
  };

  useEffect(() => {
    void loadCommunities();
  }, [user?.id]);

  useEffect(() => {
    setVisibleCount(PAGE_SIZE);
  }, [activeView, deferredSearchQuery, partnerFilter]);

  const joinCommunity = async (communityId: string) => {
    if (!user || joiningId) return;
    setJoiningId(communityId);
    setError(null);

    const { data, error: joinError } = await api.rpc("join_community", { target_community_id: communityId });
    await showJoinCommunityResult(data, joinError);
    if (joinError) {
      setError(joinError.message);
      setJoiningId(null);
      return;
    }

    setJoiningId(null);
    await loadCommunities("refresh");
  };

  const cancelCommunityRequest = async (communityId: string) => {
    if (!user || joiningId) return;
    setJoiningId(communityId);
    setError(null);

    const { error: cancelError } = await api.rpc("cancel_community_membership_request", {
      target_community_id: communityId
    });
    if (cancelError) {
      setError(cancelError.message);
      await showAppError(cancelError.message);
      setJoiningId(null);
      return;
    }

    await showAppAlert({
      title: "İstek geri çekildi",
      text: "Katılım isteğin iptal edildi.",
      icon: "success"
    });
    setJoiningId(null);
    await loadCommunities("refresh");
  };

  const officialCommunities = communities.filter((community) => community.community_type === "category_hub");
  const localCommunities = communities.filter((community) => community.community_type === "partner_hub");
  const myCommunities = communities.filter((community) => {
    const membership = memberships[community.id];
    return community.is_member === true || community.membership_status === "approved" || community.membership_status === "pending" || membership?.status === "approved" || membership?.status === "pending" || assistantCommunityIds.includes(community.id);
  });

  const currentCommunities = (activeView === "official"
    ? officialCommunities
    : activeView === "mine"
      ? myCommunities
      : localCommunities.filter((community) => {
          if (partnerFilter === "verified" && !community.is_verified_partner) return false;
          if (partnerFilter === "new" && community.partner_trust_level !== "new") return false;
          return true;
        })
  ).filter((community) => matchesSearch(community, deferredSearchQuery));

  const visibleCommunities = currentCommunities.slice(0, visibleCount);
  const hiddenCommunityCount = Math.max(0, currentCommunities.length - visibleCommunities.length);

  const viewCounts: Record<CommunityView, number> = {
    official: officialCommunities.length,
    local: localCommunities.length,
    mine: myCommunities.length
  };

  const sectionCopy = activeView === "official"
    ? {
        kicker: "BİALEM TOPLULUKLARI",
        title: "İlgi alanını seç.",
        description: "Bialem ekibi tarafından yönetilen kalıcı toplulukları keşfet."
      }
    : activeView === "local"
      ? {
          kicker: "YEREL & WHATSAPP",
          title: "Şehrindeki ekipleri bul.",
          description: "Bağımsız topluluklar en yeni katılanlardan başlayarak burada listelenir."
        }
      : {
          kicker: "TOPLULUKLARIM",
          title: "Sana ait alan.",
          description: "Üyesi olduğun ve katılım onayı bekleyen toplulukları tek yerden takip et."
        };

  const renderCommunityCard = (community: Community, index: number) => {
    const membership = memberships[community.id];
    const joined = community.is_member === true || community.membership_status === "approved" || membership?.status === "approved";
    const requestPending = community.membership_status === "pending" || membership?.status === "pending";
    const requestBlocked = community.membership_status === "blocked" || membership?.status === "blocked";
    const isModerator = joined && membership.role !== "member";
    const isAssistant = assistantCommunityIds.includes(community.id);
    const canOpen = joined || isAssistant;
    const canInspect = canOpen || requestPending;
    const isPartner = community.community_type === "partner_hub";
    const recentlyAdded = isRecentlyAdded(community.created_at);
    const remoteCoverFailed = failedCoverIds.has(community.id);
    const coverSource = getCommunityCover(community.slug, remoteCoverFailed ? null : community.cover_image_url);
    const eyebrow = isModerator
      ? isPartner ? "BAĞIMSIZ YÖNETİCİ" : "BİALEM MODERATÖRÜ"
      : isAssistant ? "MODERATÖR YARDIMCISI"
        : joined ? "ÜYESİN"
          : requestPending ? "ONAY BEKLİYOR"
            : isPartner ? "YEREL TOPLULUK"
              : "BİALEM TOPLULUĞU";

    const coverContent = (
      <>
        <View style={styles.coverTopRow}>
          <Text style={styles.coverEyebrow}>{eyebrow}</Text>
          {recentlyAdded ? <Text style={styles.newBadge}>YENİ</Text> : null}
        </View>
        <Text style={styles.coverTitle}>{community.name}</Text>
      </>
    );

    return (
      <Reveal key={community.id} index={Math.min(index, 5)}>
      <View style={styles.card}>
        <Pressable onPress={() => canInspect && router.push({ pathname: "/community/[id]", params: { id: community.id } })}>
          {coverSource ? (
            <ImageBackground
              source={coverSource}
              style={styles.cover}
              imageStyle={styles.coverImage}
              onError={() => {
                if (!community.cover_image_url || remoteCoverFailed) return;
                setFailedCoverIds((current) => new Set(current).add(community.id));
              }}
            >
              <View style={styles.coverShade} />
              <View style={styles.coverContent}>{coverContent}</View>
            </ImageBackground>
          ) : (
            <View style={[styles.cover, { backgroundColor: fallbackColors[index % fallbackColors.length] }]}>
              <View style={styles.orbitOne} />
              <View style={styles.orbitTwo} />
              <View style={styles.coverContent}>{coverContent}</View>
            </View>
          )}
        </Pressable>

        <View style={styles.cardBody}>
          {isPartner ? (
            <View style={styles.partnerMeta}>
              <Ionicons name={community.is_verified_partner ? "shield-checkmark" : "time-outline"} size={16} color={community.is_verified_partner ? colors.aqua : colors.accent} />
              <Text style={styles.partnerMetaText}>{community.is_verified_partner ? "Bialem tarafından doğrulandı" : "Yeni partner · etkinlikler ek kontrolden geçer"}</Text>
            </View>
          ) : null}
          <Text style={styles.cardDescription}>{community.description || "Bu topluluk yeni gruplar ve etkinlikler için seni bekliyor."}</Text>
          {!canOpen && !requestPending && !requestBlocked ? (
            <Text style={styles.approvalNote}>Katılım isteğin topluluk moderatörünün onayına gönderilir.</Text>
          ) : null}
          {canOpen ? (
            <>
              {joined ? <View style={styles.memberNotice}>
                <Ionicons name="checkmark-circle" size={20} color={colors.success} />
                <Text style={styles.memberNoticeText}>{isModerator ? "Bu topluluğun yöneticisisin" : "Bu topluluğun üyesisin"}</Text>
              </View> : null}
              <Pressable style={styles.openButton} onPress={() => router.push({ pathname: "/community/[id]", params: { id: community.id } })}>
                <Text style={styles.openButtonText}>{isModerator ? "Başvuruları ve grupları yönet" : "Topluluğu aç"}</Text>
                <Ionicons name="arrow-forward" size={18} color={colors.actionText} />
              </Pressable>
            </>
          ) : requestPending ? (
            <Pressable
              style={styles.cancelRequestButton}
              disabled={joiningId === community.id}
              onPress={() => void cancelCommunityRequest(community.id)}
            >
              {joiningId === community.id ? (
                <ActivityIndicator color={colors.danger} />
              ) : (
                <>
                  <Ionicons name="close-circle-outline" size={19} color={colors.danger} />
                  <Text style={styles.cancelRequestButtonText}>Katılım isteğini geri çek</Text>
                </>
              )}
            </Pressable>
          ) : (
            <Pressable
              style={[styles.joinButton, requestBlocked && styles.disabledButton]}
              disabled={joiningId === community.id || requestBlocked}
              onPress={() => void joinCommunity(community.id)}
            >
              {joiningId === community.id ? (
                <ActivityIndicator color={colors.actionText} />
              ) : (
                <Text style={styles.joinButtonText}>
                  {requestBlocked
                      ? "Katılım engellendi"
                      : membership?.status === "rejected"
                        ? "Yeniden katılım isteği gönder"
                        : "Katılım isteği gönder"}
                </Text>
              )}
            </Pressable>
          )}
        </View>
      </View>
      </Reveal>
    );
  };

  return (
    <ScrollView
      contentContainerStyle={styles.page}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadCommunities("refresh")} tintColor={colors.accent} />}
      keyboardShouldPersistTaps="handled"
    >
      <Reveal>
      <View style={styles.hero}>
        <View style={styles.heroIcon}><Ionicons name="people" size={25} color={colors.accent} /></View>
        <Text style={styles.kicker}>TOPLULUKLAR</Text>
        <Text style={styles.title}>Topluluğunu bul, birlikte harekete geç.</Text>
        <Text style={styles.description}>Resmi ilgi alanlarını, yerel ekipleri ve üyesi olduğun toplulukları ayrı ayrı keşfet.</Text>
      </View>
      </Reveal>

      <Reveal index={1}>
      <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.viewTabs}>
        {viewOptions.map((option) => {
          const active = activeView === option.id;
          return (
            <Pressable key={option.id} style={[styles.viewTab, active && styles.viewTabActive]} onPress={() => setActiveView(option.id)}>
              <Ionicons name={option.icon} size={17} color={active ? colors.onBrand : colors.muted} />
              <Text style={[styles.viewTabText, active && styles.viewTabTextActive]}>{option.label}</Text>
              <View style={[styles.countBadge, active && styles.countBadgeActive]}>
                <Text style={[styles.countBadgeText, active && styles.countBadgeTextActive]}>{viewCounts[option.id]}</Text>
              </View>
            </Pressable>
          );
        })}
      </ScrollView>
      </Reveal>

      <Reveal index={2}>
      <View style={styles.sectionHeader}>
        <Text style={styles.sectionKicker}>{sectionCopy.kicker}</Text>
        <Text style={styles.sectionTitle}>{sectionCopy.title}</Text>
        <Text style={styles.sectionDescription}>{sectionCopy.description}</Text>
      </View>
      </Reveal>

      {activeView !== "official" ? (
        <View style={styles.searchPanel}>
          <View style={styles.searchField}>
            <Ionicons name="search" size={21} color={colors.muted} />
            <TextInput
              value={searchQuery}
              onChangeText={setSearchQuery}
              placeholder={activeView === "local" ? "Topluluk adı veya ilgi alanı ara" : "Topluluklarımda ara"}
              placeholderTextColor={colors.muted}
              style={styles.searchInput}
              returnKeyType="search"
              autoCorrect={false}
            />
            {searchQuery ? (
              <Pressable accessibilityLabel="Aramayı temizle" onPress={() => setSearchQuery("")}>
                <Ionicons name="close-circle" size={21} color={colors.muted} />
              </Pressable>
            ) : null}
          </View>
          {activeView === "local" ? (
            <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.filters}>
              {partnerFilters.map((filter) => {
                const active = partnerFilter === filter.id;
                return (
                  <Pressable key={filter.id} style={[styles.filterChip, active && styles.filterChipActive]} onPress={() => setPartnerFilter(filter.id)}>
                    <Text style={[styles.filterChipText, active && styles.filterChipTextActive]}>{filter.label}</Text>
                  </Pressable>
                );
              })}
            </ScrollView>
          ) : null}
        </View>
      ) : null}

      {error ? (
        <FeedbackState
          kind="error"
          title="Topluluklar yüklenemedi"
          message={error}
          onRetry={() => void loadCommunities("refresh")}
        />
      ) : null}

      {loading ? (
        <View style={styles.loadingBox}>
          <ActivityIndicator color={colors.accent} size="large" />
          <Text style={styles.loadingText}>Topluluklar hazırlanıyor...</Text>
        </View>
      ) : communities.length === 0 ? (
        <View style={styles.emptyBox}>
          <Ionicons name="planet-outline" size={36} color={colors.accent} />
          <Text style={styles.emptyTitle}>Henüz topluluk açılmadı</Text>
          <Text style={styles.emptyText}>Yeni ana topluluklar yalnızca Bialem yönetimi tarafından oluşturulur.</Text>
        </View>
      ) : visibleCommunities.length === 0 ? (
        <View style={styles.emptyBox}>
          <Ionicons name={activeView === "mine" ? "bookmark-outline" : "search-outline"} size={34} color={colors.accent} />
          <Text style={styles.emptyTitle}>{activeView === "mine" ? "Henüz bir topluluğun yok" : "Eşleşen topluluk bulunamadı"}</Text>
          <Text style={styles.emptyText}>{activeView === "mine" ? "Katılım isteği gönderdiğin topluluklar burada görünecek." : "Arama metnini veya seçtiğin filtreyi değiştirebilirsin."}</Text>
        </View>
      ) : (
        <View style={styles.stack}>
          {visibleCommunities.map(renderCommunityCard)}
          {hiddenCommunityCount > 0 ? (
            <Pressable style={styles.moreButton} onPress={() => setVisibleCount((count) => count + PAGE_SIZE)}>
              <Text style={styles.moreButtonText}>Daha fazla göster</Text>
              <View style={styles.moreCount}><Text style={styles.moreCountText}>+{Math.min(PAGE_SIZE, hiddenCommunityCount)}</Text></View>
            </Pressable>
          ) : null}
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 16, paddingBottom: 34, gap: 15, backgroundColor: colors.page },
  hero: { marginTop: 8, gap: 9 },
  heroIcon: { width: 50, height: 50, borderRadius: 17, alignItems: "center", justifyContent: "center", backgroundColor: colors.accentSoft },
  kicker: { color: colors.accent, fontSize: 12, fontWeight: "900", letterSpacing: 1.5 },
  title: { color: colors.ink, fontSize: 26, lineHeight: 32, fontWeight: "900" },
  description: { color: colors.muted, fontSize: 15, lineHeight: 23 },
  viewTabs: { gap: 9, paddingRight: 4 },
  viewTab: { minHeight: 44, flexDirection: "row", alignItems: "center", gap: 6, paddingHorizontal: 12, borderRadius: 14, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  viewTabActive: { borderColor: colors.brandInk, backgroundColor: colors.brandInk },
  viewTabText: { color: colors.ink, fontSize: 13, fontWeight: "900" },
  viewTabTextActive: { color: colors.onBrand },
  countBadge: { minWidth: 24, height: 24, alignItems: "center", justifyContent: "center", paddingHorizontal: 6, borderRadius: 12, backgroundColor: colors.surfaceStrong },
  countBadgeActive: { backgroundColor: colors.action },
  countBadgeText: { color: colors.muted, fontSize: 11, fontWeight: "900" },
  countBadgeTextActive: { color: colors.actionText },
  sectionHeader: { gap: 5 },
  sectionKicker: { color: colors.accent, fontSize: 11, fontWeight: "900", letterSpacing: 1.4 },
  sectionTitle: { color: colors.ink, fontSize: 21, lineHeight: 26, fontWeight: "900" },
  sectionDescription: { color: colors.muted, fontSize: 14, lineHeight: 21 },
  searchPanel: { gap: 11, padding: 13, borderRadius: 24, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  searchField: { minHeight: 50, flexDirection: "row", alignItems: "center", gap: 10, paddingHorizontal: 14, borderRadius: 17, backgroundColor: colors.surfaceStrong },
  searchInput: { flex: 1, color: colors.ink, fontSize: 14, fontWeight: "700" },
  filters: { gap: 8 },
  filterChip: { paddingHorizontal: 13, paddingVertical: 9, borderRadius: 999, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.page },
  filterChipActive: { borderColor: colors.accent, backgroundColor: colors.accentSoft },
  filterChipText: { color: colors.muted, fontSize: 12, fontWeight: "800" },
  filterChipTextActive: { color: colors.ink },
  approvalNote: { color: colors.accent, fontSize: 13, lineHeight: 19, fontWeight: "800" },
  memberNotice: { minHeight: 42, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 7, paddingHorizontal: 12, borderRadius: 14, backgroundColor: colors.accentSoft },
  memberNoticeText: { color: colors.success, fontSize: 14, fontWeight: "900" },
  errorText: { color: colors.danger, backgroundColor: colors.surfaceStrong, borderRadius: 16, padding: 13, fontSize: 13, lineHeight: 19, fontWeight: "700" },
  loadingBox: { minHeight: 220, alignItems: "center", justifyContent: "center", gap: 12 },
  loadingText: { color: colors.muted, fontSize: 15 },
  emptyBox: { alignItems: "center", padding: 28, gap: 10, borderRadius: 28, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  emptyTitle: { color: colors.ink, textAlign: "center", fontSize: 20, fontWeight: "900" },
  emptyText: { color: colors.muted, textAlign: "center", fontSize: 14, lineHeight: 21 },
  stack: { gap: 12 },
  card: { overflow: "hidden", borderRadius: 20, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, boxShadow: "0 6px 16px var(--bialem-shadow)" },
  cover: { height: 190, overflow: "hidden", justifyContent: "flex-end", padding: 20 },
  coverImage: { borderTopLeftRadius: 29, borderTopRightRadius: 29 },
  coverShade: { ...StyleSheet.absoluteFillObject, backgroundColor: "rgba(8, 26, 68, 0.44)" },
  coverContent: { gap: 7 },
  coverTopRow: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", gap: 10 },
  coverEyebrow: { flexShrink: 1, color: "#ffffff", fontSize: 11, fontWeight: "900", letterSpacing: 1.4 },
  newBadge: { overflow: "hidden", color: colors.actionText, paddingHorizontal: 10, paddingVertical: 6, borderRadius: 999, backgroundColor: colors.action, fontSize: 10, fontWeight: "900", letterSpacing: 0.8 },
  coverTitle: { color: "#ffffff", fontSize: 24, lineHeight: 29, fontWeight: "900" },
  orbitOne: { position: "absolute", width: 180, height: 180, borderRadius: 90, right: -36, top: -42, borderWidth: 32, borderColor: "rgba(255,255,255,0.18)" },
  orbitTwo: { position: "absolute", width: 110, height: 110, borderRadius: 55, left: -32, bottom: -26, backgroundColor: "rgba(246,165,28,0.45)" },
  cardBody: { padding: 18, gap: 15 },
  partnerMeta: { flexDirection: "row", alignItems: "center", gap: 7, alignSelf: "flex-start", borderRadius: 999, paddingHorizontal: 11, paddingVertical: 7, backgroundColor: colors.accentSoft },
  partnerMetaText: { flexShrink: 1, color: colors.ink, fontSize: 11, fontWeight: "800" },
  cardDescription: { color: colors.muted, fontSize: 14, lineHeight: 21 },
  joinButton: { minHeight: 44, alignItems: "center", justifyContent: "center", borderRadius: 14, backgroundColor: colors.action, paddingHorizontal: 14 },
  disabledButton: { opacity: 0.62 },
  joinButtonText: { color: colors.actionText, textAlign: "center", fontSize: 15, fontWeight: "900" },
  cancelRequestButton: { minHeight: 44, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 7, borderRadius: 14, borderWidth: 1, borderColor: colors.danger, backgroundColor: colors.surface, paddingHorizontal: 14 },
  cancelRequestButtonText: { color: colors.danger, textAlign: "center", fontSize: 15, fontWeight: "900" },
  openButton: { minHeight: 44, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 8, borderRadius: 14, backgroundColor: colors.action, paddingHorizontal: 14 },
  openButtonText: { color: colors.actionText, textAlign: "center", fontSize: 15, fontWeight: "900" },
  moreButton: { minHeight: 54, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 10, borderRadius: 20, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  moreButtonText: { color: colors.ink, fontSize: 14, fontWeight: "900" },
  moreCount: { minWidth: 30, height: 26, alignItems: "center", justifyContent: "center", paddingHorizontal: 7, borderRadius: 13, backgroundColor: colors.accentSoft },
  moreCountText: { color: colors.accent, fontSize: 11, fontWeight: "900" }
});
