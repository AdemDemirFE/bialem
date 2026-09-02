import { Ionicons } from "@expo/vector-icons";
import { Link, useFocusEffect } from "expo-router";
import { useCallback, useEffect, useMemo, useState } from "react";
import { ActivityIndicator, Image, Pressable, RefreshControl, ScrollView, StyleSheet, Text, View } from "react-native";
import { HonorBadges, type HonorBadge } from "../../src/components/HonorBadges";
import { TeamIdentityBadge } from "../../src/components/TeamIdentityBadge";
import { ImageViewerModal } from "../../src/components/ImageViewerModal";
import { NotificationButton } from "../../src/components/NotificationButton";
import { useAuth } from "../../src/lib/auth";
import { pickImageFromLibrary, requestMediaLibraryPermission, uploadProfileAvatar } from "../../src/lib/storage";
import { profileStatusLabel } from "../../src/lib/profile-status";
import { api } from "../../src/lib/api";
import { getPlatformTeamIdentity, type PlatformTeamRole } from "../../src/lib/team-identities";
import { colors } from "../../src/theme/colors";
import { type ThemePreference, useTheme } from "../../src/theme/theme";

type EventRatingRecord = {
  id: string;
  event_id: string;
  user_id: string;
  rating: number;
  review_text: string | null;
  created_at: string;
};

type UserReviewRecord = {
  id: string;
  reviewer_id: string;
  reviewed_user_id: string;
  rating: number;
  review_text: string | null;
  created_at: string;
};

type ActivityStats = {
  communities: number;
  events: number;
  posts: number;
  comments: number;
};

type MediaPostRecord = {
  id: number;
  event?: { id: number; title?: string } | null;
  body?: string | null;
  createdAt?: string;
  media?: {
    id: number;
    mediaType?: string;
    storagePath?: string;
    sortOrder?: number;
  }[] | null;
};

type Reliability = { checked_in_count: number; no_show_count: number; reliability_score: number | null };
type FollowSummary = { follower_count: number; following_count: number; is_following: boolean };
type ProfileTab = "plans" | "posts" | "reviews";

export default function ProfileScreen() {
  const { user, profile, signOut, updateAvatar } = useAuth();
  const { preference, resolvedTheme, setPreference } = useTheme();
  const [stats, setStats] = useState<ActivityStats>({ communities: 0, events: 0, posts: 0, comments: 0 });
  const [ratingsGiven, setRatingsGiven] = useState<EventRatingRecord[]>([]);
  const [reviewsReceived, setReviewsReceived] = useState<UserReviewRecord[]>([]);
  const [mediaPosts, setMediaPosts] = useState<MediaPostRecord[]>([]);
  const [reliability, setReliability] = useState<Reliability | null>(null);
  const [followSummary, setFollowSummary] = useState<FollowSummary>({ follower_count: 0, following_count: 0, is_following: false });
  const [pendingFollowRequestCount, setPendingFollowRequestCount] = useState(0);
  const [honorBadges, setHonorBadges] = useState<HonorBadge[]>([]);
  const [teamRole, setTeamRole] = useState<PlatformTeamRole | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [uploadingAvatar, setUploadingAvatar] = useState(false);
  const [activeTab, setActiveTab] = useState<ProfileTab>("plans");
  const [viewerVisible, setViewerVisible] = useState(false);

  const changeProfilePhoto = async () => {
    if (!user || uploadingAvatar) return;
    setError(null);
    setUploadingAvatar(true);

    try {
      if (!(await requestMediaLibraryPermission())) {
        setError("Profil fotoğrafı seçmek için galeri izni gereklidir.");
        return;
      }

      const image = await pickImageFromLibrary({ square: true });
      if (!image) return;

      const avatarUrl = await uploadProfileAvatar({ userId: user.id, image });
      if (!(await updateAvatar(avatarUrl))) {
        setError("Profil fotoğrafı kaydedilemedi.");
      }
    } catch (avatarError) {
      setError(avatarError instanceof Error ? avatarError.message : "Profil fotoğrafı yüklenemedi.");
    } finally {
      setUploadingAvatar(false);
    }
  };

  const loadProfileData = async (mode: "initial" | "refresh" = "initial") => {
    if (!user) {
      setLoading(false);
      return;
    }

    if (mode === "initial") {
      setLoading(true);
    } else {
      setRefreshing(true);
    }

    setError(null);

    const [
      communitiesCountResult,
      eventsCountResult,
      postsCountResult,
      commentsCountResult,
      ratingsGivenResult,
      reviewsReceivedResult,
      mediaPostsResult,
      reliabilityResult,
      badgesResult,
      followSummaryResult,
      followRequestsCountResult,
      teamIdentityResult
    ] = await Promise.all([
      api.communities.countByCreator(user.id),
      api.events.countByCreator(user.id),
      api.posts.countByAuthor(user.id),
      api.comments.countByAuthor(user.id),
      api.eventRatings.listByUser(user.id),
      api
        .from("user_reviews")
        .select("id, reviewer_id, reviewed_user_id, rating, review_text, created_at")
        .eq("reviewed_user_id", user.id)
        .order("created_at", { ascending: false }),
      api.posts.list({ authorId: user.id, sort: "createdAt,desc", size: 6 }),
      api.rpc("get_user_reliability", { target_user_id: user.id }).maybeSingle(),
      api.rpc("get_user_honor_badges", { target_user_id: user.id }),
      api.rpc("get_public_follow_summary", { target_user_id: user.id }).maybeSingle(),
      api.from("follow_requests").select("*", { count: "exact", head: true }).eq("target_user_id", user.id),
      getPlatformTeamIdentity(user.id)
    ]);

    if (
      communitiesCountResult.error ||
      eventsCountResult.error ||
      postsCountResult.error ||
      commentsCountResult.error ||
      ratingsGivenResult.error ||
      reviewsReceivedResult.error ||
      mediaPostsResult.error ||
      reliabilityResult.error ||
      badgesResult.error ||
      followSummaryResult.error ||
      followRequestsCountResult.error
    ) {
      setError(
        communitiesCountResult.error?.message ||
          eventsCountResult.error?.message ||
          postsCountResult.error?.message ||
          commentsCountResult.error?.message ||
          ratingsGivenResult.error?.message ||
          reviewsReceivedResult.error?.message ||
          mediaPostsResult.error?.message ||
          reliabilityResult.error?.message ||
          badgesResult.error?.message ||
          followSummaryResult.error?.message ||
          followRequestsCountResult.error?.message ||
          "Profil verileri yüklenemedi."
      );
    } else {
      setStats({
        communities: communitiesCountResult.data ?? 0,
        events: eventsCountResult.data ?? 0,
        posts: postsCountResult.data ?? 0,
        comments: commentsCountResult.data ?? 0
      });
      setRatingsGiven((ratingsGivenResult.data ?? []) as EventRatingRecord[]);
      setReviewsReceived((reviewsReceivedResult.data ?? []) as UserReviewRecord[]);
      setMediaPosts((mediaPostsResult.data ?? []) as MediaPostRecord[]);
      setReliability((reliabilityResult.data ?? null) as Reliability | null);
      setHonorBadges((badgesResult.data ?? []) as HonorBadge[]);
      const rawSummary = (followSummaryResult.data ?? { followers: 0, following: 0 }) as { followers?: number; following?: number };
      setFollowSummary({
        follower_count: Number(rawSummary.followers ?? 0),
        following_count: Number(rawSummary.following ?? 0),
        is_following: false
      });
      setPendingFollowRequestCount(followRequestsCountResult.count ?? 0);
      setTeamRole(teamIdentityResult.role);
    }

    if (mode === "initial") {
      setLoading(false);
    } else {
      setRefreshing(false);
    }
  };

  useEffect(() => {
    void loadProfileData();
  }, [user?.id]);

  useFocusEffect(
    useCallback(() => {
      if (!user) return;

      void api
        .from("follow_requests")
        .select("*", { count: "exact", head: true })
        .eq("target_user_id", user.id)
        .then(({ count, error: countError }) => {
          if (!countError) setPendingFollowRequestCount(count ?? 0);
        });
    }, [user?.id])
  );

  const averageReceivedRating = useMemo(() => {
    if (reviewsReceived.length === 0) {
      return null;
    }

    const total = reviewsReceived.reduce((sum, review) => sum + review.rating, 0);
    return (total / reviewsReceived.length).toFixed(1);
  }, [reviewsReceived]);

  return (
    <ScrollView
      contentContainerStyle={styles.page}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadProfileData("refresh")} tintColor={colors.accent} />}
    >
      <ImageViewerModal
        visible={viewerVisible}
        uri={profile?.avatar_url ?? null}
        onClose={() => setViewerVisible(false)}
        onEdit={() => void changeProfilePhoto()}
      />

      <View style={styles.hero}>
        <View style={styles.profileIdentity}>
          <Pressable style={styles.avatarButton} onPress={() => setViewerVisible(true)}>
            {profile?.avatar_url ? (
              <Image source={{ uri: profile.avatar_url }} style={styles.avatarImage} resizeMode="cover" />
            ) : (
              <Text style={styles.avatarInitial}>{(profile?.display_name || "Ü").slice(0, 1).toUpperCase()}</Text>
            )}
            <View style={styles.cameraBadge}>
              {uploadingAvatar ? <ActivityIndicator size="small" color={colors.actionText} /> : <Ionicons name="camera" size={17} color={colors.actionText} />}
            </View>
          </Pressable>
          <View style={styles.identityCopy}>
            <Text style={styles.kicker}>Profil</Text>
            <Text style={styles.title} numberOfLines={2} adjustsFontSizeToFit minimumFontScale={0.76}>{profile?.display_name ?? "Üye"}</Text>
            <TeamIdentityBadge role={teamRole} />
            <Text style={styles.photoHint}>Fotoğrafı değiştirmek için dokun</Text>
          </View>
          <NotificationButton />
        </View>
        <Text style={styles.description}>
          Burada hesabınızın güven sinyallerini, ürettiğiniz içerikleri ve topluluk içindeki hareketinizi görürsünüz.
        </Text>
      </View>

      <View style={styles.socialPanel}>
        <View style={styles.sectionTitleRow}>
          <View style={styles.sectionTitleCopy}>
            <Text style={styles.panelTitle} numberOfLines={1}>Sosyal çevren</Text>
            <Text style={styles.socialHint} numberOfLines={2}>Takip ettiklerinin anlıklarını ve planlarını keşfet.</Text>
          </View>
          <Link href={"/people" as never} asChild>
            <Pressable style={styles.findPeopleButton}>
              <Ionicons name="person-add-outline" size={15} color={colors.actionText} />
              <Text style={styles.findPeopleText} numberOfLines={1}>Kişi bul</Text>
            </Pressable>
          </Link>
        </View>
        <View style={styles.followStatsRow}>
          <Link href={{ pathname: "/people/connections" as never, params: { userId: user?.id || "", tab: "followers" } }} asChild>
            <Pressable style={styles.followStat}>
              <Text style={styles.followStatValue}>{followSummary.follower_count}</Text>
              <Text style={styles.followStatLabel}>Takipçi</Text>
            </Pressable>
          </Link>
          <Link href={{ pathname: "/people/connections" as never, params: { userId: user?.id || "", tab: "following" } }} asChild>
            <Pressable style={styles.followStat}>
              <Text style={styles.followStatValue}>{followSummary.following_count}</Text>
              <Text style={styles.followStatLabel}>Takip edilen</Text>
            </Pressable>
          </Link>
        </View>
        <Link href={"/people/requests" as never} asChild>
          <Pressable style={[styles.followRequestsButton, pendingFollowRequestCount > 0 && styles.followRequestsButtonActive]}>
            <View style={styles.followRequestsIcon}>
              <Ionicons name="person-add-outline" size={21} color={colors.accent} />
            </View>
            <View style={styles.followRequestsCopy}>
              <Text style={styles.followRequestsTitle}>Takip istekleri</Text>
              <Text style={styles.followRequestsHint}>
                {pendingFollowRequestCount > 0
                  ? `${pendingFollowRequestCount} kişi seni takip etmek için onay bekliyor.`
                  : "Yeni takip isteklerini buradan yönet."}
              </Text>
            </View>
            <View style={[styles.followRequestsCount, pendingFollowRequestCount > 0 && styles.followRequestsCountActive]}>
              <Text style={[styles.followRequestsCountText, pendingFollowRequestCount > 0 && styles.followRequestsCountTextActive]}>
                {pendingFollowRequestCount}
              </Text>
            </View>
            <Ionicons name="chevron-forward" size={20} color={colors.muted} />
          </Pressable>
        </Link>
      </View>

      <View style={styles.panel}>
        <View style={styles.sectionTitleRow}>
          <Text style={styles.panelTitle}>Hesap özeti</Text>
          <Link href={"/profile/edit" as never} asChild>
            <Pressable style={styles.editProfileButton}>
              <Ionicons name="create-outline" size={17} color={colors.actionText} />
              <Text style={styles.editProfileText}>Düzenle</Text>
            </Pressable>
          </Link>
        </View>
        <ProfileRow label="E-posta" value={user?.email ?? "-"} />
        <ProfileRow label="Kullanıcı adı" value={profile?.username ?? "-"} />
        <ProfileRow label="Şehir" value={profile?.city || "Henüz eklenmedi"} />
        <ProfileRow label="Durum" value={profileStatusLabel(profile?.status)} />
        <ProfileRow label="Doğrulama" value={profile?.is_verified ? "Doğrulanmış" : "Beklemede"} />
      </View>

      <View style={styles.panel}>
        <View style={styles.appearanceHeader}>
          <View style={styles.appearanceIcon}>
            <Ionicons name={resolvedTheme === "dark" ? "moon" : "sunny"} size={22} color={colors.actionText} />
          </View>
          <View style={styles.appearanceCopy}>
            <Text style={styles.panelTitle}>Görünüm</Text>
            <Text style={styles.emptyText}>Gece modunu seç veya telefonunun temasını otomatik izle.</Text>
          </View>
        </View>
        <View style={styles.themeChoices}>
          {([
            ["light", "Gündüz", "sunny-outline"],
            ["dark", "Gece", "moon-outline"],
            ["system", "Sistem", "phone-portrait-outline"]
          ] as [ThemePreference, string, keyof typeof Ionicons.glyphMap][]).map(([value, label, icon]) => {
            const selected = preference === value;
            return (
              <Pressable key={value} style={[styles.themeChoice, selected && styles.themeChoiceActive]} onPress={() => void setPreference(value)}>
                <Ionicons name={icon} size={19} color={selected ? colors.actionText : colors.muted} />
                <Text style={[styles.themeChoiceText, selected && styles.themeChoiceTextActive]}>{label}</Text>
              </Pressable>
            );
          })}
        </View>
      </View>

      <View style={styles.panel}>
        <View style={styles.appearanceHeader}>
          <View style={[styles.appearanceIcon, { backgroundColor: colors.accentSoft }]}>
            <Ionicons name="notifications-outline" size={22} color={colors.accent} />
          </View>
          <View style={styles.appearanceCopy}>
            <Text style={styles.panelTitle}>Bildirim Ayarları</Text>
            <Text style={styles.emptyText}>Hangi bildirimleri almak istediğini yönet.</Text>
          </View>
        </View>
        <Link href={"/notification-settings" as never} asChild>
          <Pressable style={styles.primaryButton}>
            <Text style={styles.primaryButtonText}>Ayarları aç</Text>
          </Pressable>
        </Link>
      </View>

      <View style={styles.statsGrid}>
        <StatCard label="Topluluk" value={String(stats.communities)} />
        <StatCard label="Etkinlik" value={String(stats.events)} />
        <StatCard label="Paylaşım" value={String(stats.posts)} />
        <StatCard label="Yorum" value={String(stats.comments)} />
      </View>

      <HonorBadges badges={honorBadges} />

      <View style={styles.profileTabs}>
        {([
          ["plans", "Planlarım", "calendar-outline"],
          ["posts", "Paylaşımlar", "images-outline"],
          ["reviews", "Değerlendirmeler", "star-outline"]
        ] as [ProfileTab, string, keyof typeof Ionicons.glyphMap][]).map(([value, label, icon]) => {
          const selected = activeTab === value;
          return (
            <Pressable key={value} style={[styles.profileTab, selected && styles.profileTabActive]} onPress={() => setActiveTab(value)}>
              <Ionicons name={icon} size={18} color={selected ? colors.actionText : colors.muted} />
              <Text style={[styles.profileTabText, selected && styles.profileTabTextActive]}>{label}</Text>
            </Pressable>
          );
        })}
      </View>

      {activeTab === "plans" ? (
        <View style={styles.panel}>
          <View style={styles.tabIntroRow}>
            <View style={styles.tabIntroIcon}>
              <Ionicons name="calendar" size={23} color={colors.actionText} />
            </View>
            <View style={styles.tabIntroCopy}>
              <Text style={styles.panelTitle}>Kişisel etkinlik takvimin</Text>
              <Text style={styles.emptyText}>Yaklaşan, onay bekleyen ve geçmiş planlarını ay ay incele.</Text>
            </View>
          </View>
          <Link href={"/my-plans" as never} asChild>
            <Pressable style={styles.primaryButton}>
              <Text style={styles.primaryButtonText}>Planlarımı aç</Text>
            </Pressable>
          </Link>
        </View>
      ) : null}

      {activeTab === "reviews" ? (
      <>
      <View style={styles.panel}>
        <Text style={styles.panelTitle}>Güven ve puan özeti</Text>
        {loading ? (
          <View style={styles.loadingRow}>
            <ActivityIndicator color={colors.accent} />
            <Text style={styles.loadingText}>Profil verileri yükleniyor...</Text>
          </View>
        ) : (
          <>
            {error ? <Text style={styles.errorText}>{error}</Text> : null}
            <ProfileRow label="Aldığı kullanıcı puanı" value={averageReceivedRating ? `${averageReceivedRating}/5` : "Henüz puan yok"} />
            <ProfileRow label="Katılım güvenilirliği" value={reliability?.reliability_score == null ? "Henüz ölçülmedi" : `%${reliability.reliability_score}`} />
            <ProfileRow label="Doğrulanmış katılım" value={String(reliability?.checked_in_count ?? 0)} />
            <ProfileRow label="Aldığı değerlendirme" value={String(reviewsReceived.length)} />
            <ProfileRow label="Verdiği etkinlik puanı" value={String(ratingsGiven.length)} />
          </>
        )}
      </View>

      <View style={styles.panel}>
        <Text style={styles.panelTitle}>Verdiğin etkinlik puanları</Text>
        {ratingsGiven.length === 0 ? (
          <Text style={styles.emptyText}>Henüz etkinlik puanı vermedin. Katıldığın etkinliklerde bu alan dolacak.</Text>
        ) : (
          <View style={styles.stack}>
            {ratingsGiven.slice(0, 5).map((rating) => (
              <View key={rating.id} style={styles.activityCard}>
                <Text style={styles.activityTitle}>{rating.rating}/5 yıldız</Text>
                <Text style={styles.activityMeta}>{formatDate(rating.created_at)}</Text>
                <Text style={styles.activityText}>{rating.review_text || "Yalnızca puan bırakıldı."}</Text>
              </View>
            ))}
          </View>
        )}
      </View>

      <View style={styles.panel}>
        <Text style={styles.panelTitle}>Aldığın kullanıcı yorumları</Text>
        <Text style={styles.panelHint}>
          Bir sonraki aşamada başka kullanıcıların profillerinden doğrudan size puan ve yorum bırakma akışını bağlayacağız.
        </Text>
        {reviewsReceived.length === 0 ? (
          <Text style={styles.emptyText}>Henüz kullanıcı yorumu yok.</Text>
        ) : (
          <View style={styles.stack}>
            {reviewsReceived.slice(0, 5).map((review) => (
              <View key={review.id} style={styles.activityCard}>
                <Text style={styles.activityTitle}>{review.rating}/5 puan</Text>
                <Text style={styles.activityMeta}>{formatDate(review.created_at)}</Text>
                <Text style={styles.activityText}>{review.review_text || "Yalnızca puan bırakıldı."}</Text>
                <Link href={{ pathname: "/user/[id]", params: { id: review.reviewer_id } }} asChild>
                  <Pressable>
                    <Text style={styles.inlineLinkText}>{maskUser(review.reviewer_id)} profilini aç</Text>
                  </Pressable>
                </Link>
              </View>
            ))}
          </View>
        )}
      </View>
      </>
      ) : null}

      {activeTab === "posts" ? (
      <View style={styles.panel}>
        <Text style={styles.panelTitle}>Paylaştığın güzel anlar</Text>
        {mediaPosts.length === 0 ? (
          <Text style={styles.emptyText}>Henüz görselli bir paylaşımın yok. Topluluk içinde görsel bağlantısı ekleyerek bu alanı doldurabilirsin.</Text>
        ) : (
          <View style={styles.stack}>
            {mediaPosts.map((post) => (
              <Link key={post.id} href={{ pathname: "/post/[id]", params: { id: String(post.id) } }} asChild>
                <Pressable style={styles.activityCard}>
                    {post.media?.length ? (
                    <Image source={{ uri: post.media[0].storagePath }} style={styles.mediaImage} resizeMode="cover" />
                  ) : null}
                  {post.event ? (
                    <View style={styles.memoryBadge}>
                      <Ionicons name="calendar" size={12} color={colors.onBrand} />
                      <Text style={styles.memoryBadgeText} numberOfLines={1}>Etkinlik anısı{post.event.title ? ` · ${post.event.title}` : ""}</Text>
                    </View>
                  ) : null}
                  <Text style={styles.activityMeta}>{formatDate(post.createdAt ?? "")}</Text>
                  <Text style={styles.activityText}>{post.body || "Açıklama eklenmedi."}</Text>
                </Pressable>
              </Link>
            ))}
          </View>
        )}
      </View>
      ) : null}

      <View style={styles.panel}>
        <Text style={styles.panelTitle}>Alışveriş</Text>
        <Link href="/store/addresses" asChild>
          <Pressable style={styles.primaryButton}>
            <Text style={styles.primaryButtonText}>Adreslerim</Text>
          </Pressable>
        </Link>
        <Link href="/store/orders" asChild>
          <Pressable style={styles.secondaryButton}>
            <Text style={styles.secondaryButtonText}>Siparişlerim</Text>
          </Pressable>
        </Link>
        <Link href="/store/cart" asChild>
          <Pressable style={styles.secondaryButton}>
            <Text style={styles.secondaryButtonText}>Sepetim</Text>
          </Pressable>
        </Link>
      </View>

      <View style={styles.panel}>
        <Text style={styles.panelTitle}>Hızlı işlemler</Text>
        <Link href="/organizer-request" asChild>
          <Pressable style={styles.primaryButton}>
            <Text style={styles.primaryButtonText}>Etkinlik talebi oluştur</Text>
          </Pressable>
        </Link>
        <Pressable style={styles.secondaryButton} onPress={() => void signOut()}>
          <Text style={styles.secondaryButtonText}>Çıkış yap</Text>
        </Pressable>
      </View>

      <View style={styles.panel}>
        <Text style={styles.panelTitle}>Hesap ve güvenlik</Text>
        <Text style={styles.emptyText}>Gizlilik, KVKK, topluluk kuralları, destek ve hesap silme işlemlerini buradan yönet.</Text>
        <Link href="/account" asChild>
          <Pressable style={styles.secondaryButton}>
            <Text style={styles.secondaryButtonText}>Hesap ve yasal ayarları aç</Text>
          </Pressable>
        </Link>
      </View>
    </ScrollView>
  );
}

function ProfileRow({ label, value }: { label: string; value: string }) {
  return (
    <View style={styles.profileRow}>
      <Text style={styles.profileLabel}>{label}</Text>
      <Text style={styles.profileValue}>{value}</Text>
    </View>
  );
}

function StatCard({ label, value }: { label: string; value: string }) {
  return (
    <View style={styles.statCard}>
      <Text style={styles.statLabel}>{label}</Text>
      <Text style={styles.statValue}>{value}</Text>
    </View>
  );
}

function formatDate(dateString: string) {
  try {
    return new Date(dateString).toLocaleString("tr-TR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit"
    });
  } catch {
    return dateString;
  }
}

function maskUser(userId: string | null | undefined) {
  if (!userId) return "Üye";
  return `Üye ${String(userId).slice(0, 6)}`;
}

const styles = StyleSheet.create({
  page: {
    flexGrow: 1,
    padding: 16,
    gap: 14,
    backgroundColor: colors.page
  },
  hero: {
    marginTop: 10,
    gap: 10
  },
  profileIdentity: {
    flexDirection: "row",
    alignItems: "center",
    gap: 14
  },
  identityCopy: {
    flex: 1,
    gap: 5
  },
  avatarButton: {
    width: 78,
    height: 78,
    borderRadius: 39,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.accentSoft,
    borderWidth: 4,
    borderColor: colors.accent
  },
  avatarImage: {
    width: "100%",
    height: "100%",
    borderRadius: 39
  },
  avatarInitial: {
    color: colors.accent,
    fontSize: 30,
    fontWeight: "900"
  },
  cameraBadge: {
    position: "absolute",
    right: -2,
    bottom: 2,
    width: 36,
    height: 36,
    borderRadius: 18,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.action,
    borderWidth: 3,
    borderColor: colors.page
  },
  photoHint: {
    color: colors.muted,
    fontSize: 12,
    fontWeight: "700"
  },
  kicker: {
    color: colors.accent,
    fontSize: 14,
    fontWeight: "700",
    textTransform: "uppercase",
    letterSpacing: 1.2
  },
  title: {
    color: colors.ink,
    fontSize: 24,
    lineHeight: 29,
    fontWeight: "900",
    letterSpacing: -0.4
  },
  description: {
    color: colors.muted,
    fontSize: 16,
    lineHeight: 24
  },
  panel: {
    backgroundColor: colors.surface,
    borderRadius: 19,
    borderWidth: 1,
    borderColor: colors.border,
    padding: 15,
    gap: 11
  },
  socialPanel: {
    overflow: "hidden",
    backgroundColor: colors.surface,
    borderRadius: 19,
    borderWidth: 1,
    borderColor: colors.border,
    padding: 15,
    gap: 12
  },
  socialHint: {
    color: colors.muted,
    fontSize: 12,
    lineHeight: 17
  },
  findPeopleButton: {
    flexShrink: 0,
    flexDirection: "row",
    alignItems: "center",
    alignSelf: "flex-start",
    height: 34,
    gap: 5,
    paddingHorizontal: 10,
    borderRadius: 999,
    backgroundColor: colors.action
  },
  findPeopleText: {
    color: colors.actionText,
    fontSize: 12,
    fontWeight: "900"
  },
  followStatsRow: {
    flexDirection: "row",
    gap: 12
  },
  followStat: {
    flex: 1,
    alignItems: "center",
    padding: 16,
    borderRadius: 20,
    backgroundColor: colors.surfaceStrong,
    borderWidth: 1,
    borderColor: colors.border
  },
  followStatValue: {
    color: colors.ink,
    fontSize: 27,
    fontWeight: "900"
  },
  followStatLabel: {
    color: colors.muted,
    fontSize: 12,
    fontWeight: "800"
  },
  followRequestsButton: {
    minHeight: 62,
    flexDirection: "row",
    alignItems: "center",
    gap: 11,
    padding: 13,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surfaceStrong
  },
  followRequestsButtonActive: {
    borderColor: colors.accent
  },
  followRequestsIcon: {
    width: 44,
    height: 44,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 15,
    backgroundColor: colors.accentSoft
  },
  followRequestsCopy: {
    flex: 1,
    gap: 3
  },
  followRequestsTitle: {
    color: colors.ink,
    fontSize: 15,
    fontWeight: "900"
  },
  followRequestsHint: {
    color: colors.muted,
    fontSize: 12,
    lineHeight: 17,
    fontWeight: "600"
  },
  followRequestsCount: {
    minWidth: 30,
    height: 30,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 15,
    backgroundColor: colors.surface
  },
  followRequestsCountActive: {
    backgroundColor: colors.action
  },
  followRequestsCountText: {
    color: colors.muted,
    fontSize: 12,
    fontWeight: "900"
  },
  followRequestsCountTextActive: {
    color: colors.actionText
  },
  panelTitle: {
    color: colors.ink,
    fontSize: 17,
    lineHeight: 24,
    fontWeight: "800"
  },
  sectionTitleRow: {
    flexDirection: "row",
    alignItems: "flex-start",
    justifyContent: "space-between",
    gap: 10
  },
  sectionTitleCopy: {
    flex: 1,
    minWidth: 0,
    paddingRight: 4
  },
  editProfileButton: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    paddingHorizontal: 12,
    paddingVertical: 9,
    borderRadius: 999,
    backgroundColor: colors.action
  },
  editProfileText: {
    color: colors.actionText,
    fontSize: 12,
    fontWeight: "900"
  },
  panelHint: {
    color: colors.accent,
    fontSize: 13,
    lineHeight: 19,
    fontWeight: "600"
  },
  profileRow: {
    gap: 6,
    paddingBottom: 10,
    borderBottomWidth: 1,
    borderBottomColor: colors.border
  },
  profileLabel: {
    color: colors.muted,
    fontSize: 13,
    fontWeight: "700"
  },
  profileValue: {
    color: colors.ink,
    fontSize: 16,
    fontWeight: "700"
  },
  statsGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 12
  },
  statCard: {
    width: "48%",
    backgroundColor: colors.surface,
    borderRadius: 22,
    borderWidth: 1,
    borderColor: colors.border,
    padding: 16,
    gap: 8
  },
  statLabel: {
    color: colors.muted,
    fontSize: 13,
    fontWeight: "700"
  },
  statValue: {
    color: colors.ink,
    fontSize: 26,
    fontWeight: "800"
  },
  loadingRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10
  },
  loadingText: {
    color: colors.muted,
    fontSize: 15
  },
  errorText: {
    color: colors.danger,
    fontSize: 14,
    lineHeight: 20,
    fontWeight: "600"
  },
  emptyText: {
    color: colors.muted,
    fontSize: 15,
    lineHeight: 22
  },
  stack: {
    gap: 12
  },
  activityCard: {
    borderRadius: 20,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surfaceStrong,
    padding: 14,
    gap: 6
  },
  activityTitle: {
    color: colors.ink,
    fontSize: 15,
    fontWeight: "800"
  },
  activityMeta: {
    color: colors.accent,
    fontSize: 12,
    fontWeight: "700"
  },
  activityText: {
    color: colors.muted,
    fontSize: 14,
    lineHeight: 21
  },
  mediaImage: {
    width: "100%",
    height: 210,
    borderRadius: 16,
    backgroundColor: colors.surfaceStrong
  },
  memoryBadge: {
    alignSelf: "flex-start",
    maxWidth: "100%",
    flexDirection: "row",
    alignItems: "center",
    gap: 5,
    paddingHorizontal: 9,
    paddingVertical: 6,
    borderRadius: 999,
    backgroundColor: colors.accentSoft
  },
  memoryBadgeText: {
    flexShrink: 1,
    color: colors.accent,
    fontSize: 10,
    fontWeight: "900"
  },
  inlineLinkText: {
    color: colors.accent,
    fontSize: 13,
    fontWeight: "700"
  },
  primaryButton: {
    backgroundColor: colors.action,
    borderRadius: 999,
    paddingVertical: 15,
    paddingHorizontal: 18
  },
  primaryButtonText: {
    color: colors.actionText,
    textAlign: "center",
    fontSize: 16,
    fontWeight: "800"
  },
  secondaryButton: {
    borderRadius: 999,
    paddingVertical: 15,
    paddingHorizontal: 18,
    borderWidth: 1,
    borderColor: colors.border
  },
  secondaryButtonText: {
    color: colors.ink,
    textAlign: "center",
    fontSize: 16,
    fontWeight: "700"
  },
  profileTabs: {
    flexDirection: "row",
    gap: 7,
    padding: 6,
    borderRadius: 22,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface
  },
  profileTab: {
    flex: 1,
    minHeight: 50,
    alignItems: "center",
    justifyContent: "center",
    gap: 5,
    paddingHorizontal: 5,
    borderRadius: 17
  },
  profileTabActive: {
    backgroundColor: colors.action
  },
  profileTabText: {
    color: colors.muted,
    fontSize: 10,
    fontWeight: "800",
    textAlign: "center"
  },
  profileTabTextActive: {
    color: colors.actionText
  },
  tabIntroRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12
  },
  tabIntroIcon: {
    width: 52,
    height: 52,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 18,
    backgroundColor: colors.action
  },
  tabIntroCopy: {
    flex: 1,
    gap: 4
  },
  appearanceHeader: { flexDirection: "row", alignItems: "center", gap: 12 },
  appearanceIcon: { width: 46, height: 46, alignItems: "center", justifyContent: "center", borderRadius: 16, backgroundColor: colors.action },
  appearanceCopy: { flex: 1, gap: 4 },
  themeChoices: { flexDirection: "row", gap: 8 },
  themeChoice: { flex: 1, minHeight: 54, alignItems: "center", justifyContent: "center", gap: 5, borderRadius: 17, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surfaceStrong },
  themeChoiceActive: { borderColor: colors.action, backgroundColor: colors.action },
  themeChoiceText: { color: colors.muted, fontSize: 11, fontWeight: "800" },
  themeChoiceTextActive: { color: colors.actionText }
});
