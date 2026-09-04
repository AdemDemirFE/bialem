import { Link, useLocalSearchParams, useRouter } from "expo-router";
import { useEffect, useMemo, useState } from "react";
import { ActivityIndicator, Image, Pressable, RefreshControl, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { HonorBadges, type HonorBadge } from "../../src/components/HonorBadges";
import { TeamIdentityBadge } from "../../src/components/TeamIdentityBadge";
import { Reveal, Skeleton } from "../../src/animations";
import { FeedbackState } from "../../src/components/ui/FeedbackState";
import { showAppConfirm } from "../../src/components/AppAlert";
import { useAuth } from "../../src/lib/auth";
import { getProfileFollowState, setProfileFollow, type FollowState } from "../../src/lib/follows";
import { profileStatusLabel } from "../../src/lib/profile-status";
import { api } from "../../src/lib/api";
import { getPlatformTeamIdentity, getPlatformTeamIdentityMap, type PlatformTeamRole } from "../../src/lib/team-identities";
import { colors } from "../../src/theme/colors";

type PublicProfileCard = {
  id: string;
  display_name: string;
  username: string;
  avatar_url: string | null;
  bio: string | null;
  city: string | null;
  status: string;
  is_verified: boolean;
  created_at: string;
};

type UserReviewRecord = {
  id: string;
  reviewer_id: string;
  reviewed_user_id: string;
  event_id: string | null;
  rating: number;
  review_text: string | null;
  created_at: string;
  profiles: {
    id: string;
    display_name: string;
    username: string;
    avatar_url: string | null;
  } | null;
};

type ReviewableEvent = {
  event_id: string;
  title: string;
  starts_at: string;
};

type Reliability = { checked_in_count: number; no_show_count: number; reliability_score: number | null };
type FollowSummary = { follower_count: number; following_count: number; is_following: boolean };

export default function UserProfileDetailScreen() {
  const params = useLocalSearchParams<{ id: string | string[] }>();
  const id = Array.isArray(params.id) ? params.id[0] : params.id;
  const router = useRouter();
  const { user } = useAuth();
  const [profileCard, setProfileCard] = useState<PublicProfileCard | null>(null);
  const [reviews, setReviews] = useState<UserReviewRecord[]>([]);
  const [reviewableEvents, setReviewableEvents] = useState<ReviewableEvent[]>([]);
  const [selectedEventId, setSelectedEventId] = useState<string>("");
  const [ratingValue, setRatingValue] = useState(5);
  const [reviewText, setReviewText] = useState("");
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [followState, setFollowState] = useState<FollowState>("none");
  const [followersCount, setFollowersCount] = useState(0);
  const [followingCount, setFollowingCount] = useState(0);
  const [followingBusy, setFollowingBusy] = useState(false);
  const [reliability, setReliability] = useState<Reliability | null>(null);
  const [honorBadges, setHonorBadges] = useState<HonorBadge[]>([]);
  const [teamRole, setTeamRole] = useState<PlatformTeamRole | null>(null);
  const [reviewTeamRoles, setReviewTeamRoles] = useState<Map<string, PlatformTeamRole>>(new Map());

  const loadUserProfile = async (mode: "initial" | "refresh" = "initial") => {
    if (!id) {
      setError("Kullanıcı bulunamadı.");
      return;
    }

    if (mode === "initial") {
      setLoading(true);
    } else {
      setRefreshing(true);
    }

    setError(null);

    const [profileResult, reviewsResult, eventsResult, followSummaryResult, reliabilityResult, badgesResult, teamIdentityResult, followStateResult] = await Promise.all([
      api.rpc("get_public_profile_card", { target_user_id: id }),
      api.rpc("get_user_reviews", { target_user_id: id }),
      api.rpc("get_reviewable_events", { target_user_id: id }),
      api.rpc("get_public_follow_summary", { target_user_id: id }).maybeSingle(),
      api.rpc("get_user_reliability", { target_user_id: id }).maybeSingle(),
      api.rpc("get_user_honor_badges", { target_user_id: id }),
      getPlatformTeamIdentity(id),
      getProfileFollowState(id)
    ]);

    setFollowState(followStateResult);
    if (!followSummaryResult.error) {
      const summary = (followSummaryResult.data ?? { follower_count: 0, following_count: 0, is_following: false }) as FollowSummary;
      setFollowState(summary.is_following ? "following" : followStateResult);
      setFollowersCount(summary.follower_count);
      setFollowingCount(summary.following_count);
    }
    if (!reliabilityResult.error) setReliability((reliabilityResult.data ?? null) as Reliability | null);
    if (!badgesResult.error) setHonorBadges((badgesResult.data ?? []) as HonorBadge[]);
    setTeamRole(teamIdentityResult.role);

    if (profileResult.error) {
      setProfileCard(null);
      setError("Kullanıcı profili şu anda yüklenemiyor. Lütfen tekrar dene.");
    } else {
      const nextProfile = Array.isArray(profileResult.data) ? profileResult.data[0] : profileResult.data;
      setProfileCard((nextProfile as PublicProfileCard | null) ?? null);
    }

    if (reviewsResult.error) {
      setReviews([]);
    } else {
      const nextReviews = ((reviewsResult.data ?? []) as Array<UserReviewRecord & { reviewer?: string | { id?: string } }>).map((review) => ({
        ...review,
        reviewer_id: review.reviewer_id ?? (typeof review.reviewer === "string" ? review.reviewer : review.reviewer?.id) ?? ""
      }));
      setReviews(nextReviews);
      setReviewTeamRoles(await getPlatformTeamIdentityMap(nextReviews.map((review) => review.reviewer_id)));
    }

    if (eventsResult.error) {
      setReviewableEvents([]);
      setSelectedEventId("");
    } else {
      const nextEvents = (eventsResult.data ?? []) as ReviewableEvent[];
      setReviewableEvents(nextEvents);
      if (!selectedEventId && nextEvents[0]) {
        setSelectedEventId(nextEvents[0].event_id);
      }
    }

    if (mode === "initial") {
      setLoading(false);
    } else {
      setRefreshing(false);
    }
  };

  useEffect(() => {
    void loadUserProfile();
  }, [id]);

  const averageRating = useMemo(() => {
    if (reviews.length === 0) {
      return null;
    }

    const total = reviews.reduce((sum, review) => sum + review.rating, 0);
    return (total / reviews.length).toFixed(1);
  }, [reviews]);

  const isOwnProfile = user?.id === id;

  const toggleFollow = async () => {
    if (!user || !id || isOwnProfile || followingBusy) return;

    setFollowingBusy(true);
    setError(null);

    const previousState = followState;
    const result = await setProfileFollow(id, previousState === "none");

    if (result.error) {
      setError(result.error);
    } else {
      const nextState = result.state ?? previousState;
      setFollowState(nextState);
      setFollowersCount((value) => Math.max(0, value + (nextState === "following" ? 1 : previousState === "following" ? -1 : 0)));
    }

    setFollowingBusy(false);
  };

  const blockProfile = async () => {
    if (!id || isOwnProfile) return;

    const confirmed = await showAppConfirm({
      title: "Kullanıcıyı engelle",
      text: "Birbirinizi takip edemez, profillerinizi ve içeriklerinizi göremezsiniz. Bu işlemi ayarlardan geri alabilirsiniz.",
      confirmText: "Engelle",
      confirmDanger: true
    });

    if (!confirmed) return;

    setError(null);
    const { error: blockError } = await api.rpc("set_profile_block", {
      target_user_id: id,
      target_blocked: true
    });

    if (blockError) {
      setError("Kullanıcı şu anda engellenemedi. Lütfen tekrar deneyin.");
      return;
    }

    router.replace("/people" as never);
  };

  const handleSubmitReview = async () => {
    if (!user || !id) {
      setError("Değerlendirme gönderebilmek için aktif oturum gerekir.");
      return;
    }

    if (isOwnProfile) {
      setError("Kendi profilinize puan veremezsiniz.");
      return;
    }

    setSubmitting(true);
    setError(null);
    setNotice(null);

    const { error: reviewError } = await api.from("user_reviews").upsert({
      reviewer_id: user.id,
      reviewed_user_id: id,
      event_id: selectedEventId || null,
      rating: ratingValue,
      review_text: reviewText.trim() || null
    });

    if (reviewError) {
      setError(
        reviewError.message.includes("eligible to review")
          ? "Bu kullanıcıya yorum verebilmek için ortak bir onaylı etkinliğe katılmış olmanız gerekir."
          : reviewError.message
      );
      setSubmitting(false);
      return;
    }

    setReviewText("");
    setNotice("Kullanıcı değerlendirmeniz kaydedildi.");
    setSubmitting(false);
    await loadUserProfile("refresh");
  };

  return (
    <ScrollView
      contentContainerStyle={styles.page}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadUserProfile("refresh")} tintColor={colors.accent} />}
    >
      <Link href="/(tabs)/feed" asChild>
        <Pressable style={styles.backButton}>
          <Text style={styles.backButtonText}>Akışa dön</Text>
        </Pressable>
      </Link>

      {loading ? (
        <View style={styles.centerBox}>
          <Skeleton height={200} borderRadius={20} />
          <Skeleton height={130} borderRadius={18} />
        </View>
      ) : !profileCard ? (
        <FeedbackState
          kind="empty"
          title="Kullanıcı bulunamadı"
          message={error || "Bu kullanıcı profili şu anda görüntülenemiyor."}
          onRetry={() => void loadUserProfile("refresh")}
        />
      ) : (
        <>
          <Reveal>
          <View style={styles.hero}>
            <View style={styles.publicAvatar}>
              {profileCard.avatar_url ? (
                <Image source={{ uri: profileCard.avatar_url }} style={styles.publicAvatarImage} resizeMode="cover" />
              ) : (
                <Text style={styles.publicAvatarInitial}>{profileCard.display_name.slice(0, 1).toUpperCase()}</Text>
              )}
            </View>
            <Text style={styles.kicker}>Kullanıcı Profili</Text>
            <Text style={styles.title}>{profileCard.display_name}</Text>
            <TeamIdentityBadge role={teamRole} />
            <Text style={styles.description}>
              @{profileCard.username}
              {profileCard.city ? ` - ${profileCard.city}` : ""}
            </Text>
            {!isOwnProfile ? (
              <View style={styles.profileActions}>
                <Pressable
                  style={({ pressed }) => [styles.followButton, followState !== "none" && styles.followButtonActive, pressed && { opacity: 0.9 }]}
                  onPress={() => void toggleFollow()}
                >
                  <Text style={[styles.followButtonText, followState !== "none" && styles.followButtonTextActive]}>
                    {followingBusy ? "İşleniyor..." : followState === "following" ? "Takibi bırak" : followState === "requested" ? "İsteği geri çek" : "Takip et"}
                  </Text>
                </Pressable>
                <Pressable
                  style={({ pressed }) => [styles.blockButton, pressed && { opacity: 0.9 }]}
                  onPress={blockProfile}
                >
                  <Text style={styles.blockButtonText}>Engelle</Text>
                </Pressable>
              </View>
            ) : null}
          </View>
          </Reveal>

          <Reveal index={1}>
          <View style={styles.panel}>
            <Text style={styles.panelTitle}>Profil özeti</Text>
            <ProfileRow label="Doğrulama" value={profileCard.is_verified ? "Doğrulanmış" : "Doğrulama bekliyor"} />
            <ProfileRow label="Durum" value={statusLabel(profileCard.status, profileCard.is_verified)} />
            <ProfileRow label="Kayıt tarihi" value={formatDate(profileCard.created_at)} />
            <Text style={styles.bioText}>{profileCard.bio || "Bu kullanıcı henüz kısa biyografi eklememiş."}</Text>
          </View>
          </Reveal>

          <Reveal index={2}>
          <View style={styles.followStatsRow}>
            <Link href={{ pathname: "/people/connections" as never, params: { userId: id, tab: "followers" } }} asChild>
              <Pressable style={styles.followStat}>
                <Text style={styles.followStatValue}>{followersCount}</Text>
                <Text style={styles.followStatLabel}>Takipçi</Text>
              </Pressable>
            </Link>
            <Link href={{ pathname: "/people/connections" as never, params: { userId: id, tab: "following" } }} asChild>
              <Pressable style={styles.followStat}>
                <Text style={styles.followStatValue}>{followingCount}</Text>
                <Text style={styles.followStatLabel}>Takip edilen</Text>
              </Pressable>
            </Link>
          </View>
          </Reveal>

          <Reveal index={3}>
          <View style={styles.statsRow}>
            <StatCard label="Ortalama" value={averageRating ? `${averageRating}/5` : "-"} />
            <StatCard label="Güven" value={reliability?.reliability_score == null ? "Yeni" : `%${reliability.reliability_score}`} />
          </View>
          </Reveal>

          <Reveal index={4}>
          <HonorBadges badges={honorBadges} />
          </Reveal>

          <View style={styles.panel}>
            <Text style={styles.panelTitle}>Yildiz ve yorum bırak</Text>
            {error ? <Text style={styles.errorText}>{error}</Text> : null}
            {notice ? <Text style={styles.noticeText}>{notice}</Text> : null}
            {isOwnProfile ? (
              <Text style={styles.emptyText}>Bu sizin kendi profiliniz. Buradan başka kullanıcılara verdiğiniz deneyim güvenini yönetebilirsiniz.</Text>
            ) : (
              <>
                <Text style={styles.panelHint}>
                  Yorum bırakabilmek için bu kullanıcıyla ortak bir onaylı etkinliğe katılmış olmanız gerekir.
                </Text>
                {reviewableEvents.length > 0 ? (
                  <View style={styles.choiceList}>
                    {reviewableEvents.map((event) => (
                      <Pressable
                        key={event.event_id}
                        style={[styles.choiceChip, selectedEventId === event.event_id && styles.choiceChipActive]}
                        onPress={() => setSelectedEventId(event.event_id)}
                      >
                        <Text style={[styles.choiceText, selectedEventId === event.event_id && styles.choiceTextActive]}>
                          {event.title}
                        </Text>
                      </Pressable>
                    ))}
                  </View>
                ) : (
          <Text style={styles.emptyText}>Şu an ortak onaylı etkinlik bulunmuyor.</Text>
                )}
                <View style={styles.ratingPicker}>
                  {[1, 2, 3, 4, 5].map((value) => (
                    <Pressable
                      key={value}
                      style={[styles.ratingChip, ratingValue === value && styles.ratingChipActive]}
                      onPress={() => setRatingValue(value)}
                    >
                      <Text style={[styles.ratingChipText, ratingValue === value && styles.ratingChipTextActive]}>{value} Yildiz</Text>
                    </Pressable>
                  ))}
                </View>
                <TextInput
                  value={reviewText}
                  onChangeText={setReviewText}
                  placeholder="Bu kişiyle ilgili deneyiminizi kısaca yazın"
                  placeholderTextColor="#7d877d"
                  multiline
                  style={styles.textArea}
                />
                <Pressable
                  style={[styles.primaryButton, (submitting || reviewableEvents.length === 0) && styles.buttonDisabled]}
                  onPress={() => void handleSubmitReview()}
                >
                  <Text style={styles.primaryButtonText}>{submitting ? "Kaydediliyor..." : "Değerlendirmeyi gönder"}</Text>
                </Pressable>
              </>
            )}
          </View>

          <View style={styles.panel}>
            <Text style={styles.panelTitle}>Aldığı yorumlar</Text>
            {reviews.length === 0 ? (
              <Text style={styles.emptyText}>Bu kullanıcı için henüz değerlendirme yok.</Text>
            ) : (
              <View style={styles.stack}>
                {reviews.map((review) => (
                  <View key={review.id} style={styles.reviewCard}>
                    <View style={styles.reviewHeader}>
                      {review.profiles?.avatar_url ? (
                        <Image source={{ uri: review.profiles.avatar_url }} style={styles.reviewerAvatar} />
                      ) : (
                        <View style={styles.reviewerAvatarPlaceholder}>
                          <Text style={styles.reviewerAvatarInitial}>{(review.profiles?.display_name ?? "?").slice(0, 1).toUpperCase()}</Text>
                        </View>
                      )}
                      <View style={styles.reviewerInfo}>
                        <Pressable
                          onPress={() => {
                            if (review.profiles?.id) router.push(`/user/${review.profiles.id}` as never);
                          }}
                          disabled={!review.profiles?.id}
                        >
                          <Text style={styles.reviewerName}>{review.profiles?.display_name ?? maskUser(review.reviewer_id)}</Text>
                        </Pressable>
                        {review.profiles?.username ? <Text style={styles.reviewerUsername}>@{review.profiles.username}</Text> : null}
                        <Text style={styles.reviewMeta}>{formatDate(review.created_at)}</Text>
                      </View>
                      <TeamIdentityBadge role={reviewTeamRoles.get(review.reviewer_id)} compact />
                    </View>
                    <Text style={styles.reviewTitle}>{review.rating}/5</Text>
                    <Text style={styles.reviewText}>{review.review_text || "Yalnızca puan bırakıldı."}</Text>
                  </View>
                ))}
              </View>
            )}
          </View>
        </>
      )}
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

function statusLabel(status: string | undefined | null, isVerified: boolean) {
  if (status === "pending_verification") {
    return isVerified ? "Doğrulanmış" : "Doğrulama bekliyor";
  }
  return profileStatusLabel(status);
}

function formatDate(dateString: string | null | undefined) {
  if (!dateString) return "-";
  const date = new Date(dateString);
  if (Number.isNaN(date.getTime())) return "-";
  return date.toLocaleString("tr-TR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  });
}

function maskUser(userId: string | null | undefined) {
  if (!userId) return "Üye";
  return `Üye ${String(userId).slice(0, 6)}`;
}

const styles = StyleSheet.create({
  page: {
    flexGrow: 1,
    padding: 24,
    gap: 20,
    backgroundColor: colors.page
  },
  backButton: {
    alignSelf: "flex-start",
    marginTop: 14,
    paddingVertical: 10,
    paddingHorizontal: 14,
    borderRadius: 999,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface
  },
  backButtonText: {
    color: colors.ink,
    fontWeight: "700"
  },
  centerBox: {
    alignItems: "center",
    gap: 10,
    paddingTop: 80
  },
  loadingText: {
    color: colors.muted,
    fontSize: 15
  },
  hero: {
    gap: 10
  },
  publicAvatar: {
    width: 108,
    height: 108,
    borderRadius: 54,
    alignItems: "center",
    justifyContent: "center",
    overflow: "hidden",
    backgroundColor: colors.accentSoft,
    borderWidth: 4,
    borderColor: colors.accent
  },
  publicAvatarImage: {
    width: "100%",
    height: "100%"
  },
  publicAvatarInitial: {
    color: colors.accent,
    fontSize: 40,
    fontWeight: "900"
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
    fontSize: 32,
    lineHeight: 38,
    fontWeight: "800"
  },
  description: {
    color: colors.muted,
    fontSize: 16,
    lineHeight: 24
  },
  profileActions: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 10
  },
  followButton: {
    alignSelf: "flex-start",
    backgroundColor: colors.action,
    borderRadius: 999,
    paddingHorizontal: 20,
    paddingVertical: 12,
    borderWidth: 1,
    borderColor: colors.action
  },
  followButtonActive: {
    backgroundColor: colors.surface,
    borderColor: colors.accent
  },
  followButtonText: {
    color: colors.actionText,
    fontSize: 14,
    fontWeight: "800"
  },
  followButtonTextActive: {
    color: colors.accent
  },
  blockButton: {
    alignSelf: "flex-start",
    borderRadius: 999,
    paddingHorizontal: 18,
    paddingVertical: 12,
    borderWidth: 1,
    borderColor: colors.danger,
    backgroundColor: colors.surface
  },
  blockButtonText: {
    color: colors.danger,
    fontSize: 14,
    fontWeight: "800"
  },
  panel: {
    backgroundColor: colors.surface,
    borderRadius: 28,
    borderWidth: 1,
    borderColor: colors.border,
    padding: 20,
    gap: 14
  },
  panelTitle: {
    color: colors.ink,
    fontSize: 23,
    fontWeight: "800"
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
  bioText: {
    color: colors.muted,
    fontSize: 15,
    lineHeight: 22
  },
  statsRow: {
    flexDirection: "row",
    gap: 12
  },
  followStatsRow: {
    flexDirection: "row",
    gap: 12
  },
  followStat: {
    flex: 1,
    alignItems: "center",
    padding: 16,
    borderRadius: 22,
    backgroundColor: colors.surface,
    borderWidth: 1,
    borderColor: colors.border
  },
  followStatValue: {
    color: colors.ink,
    fontSize: 26,
    fontWeight: "900"
  },
  followStatLabel: {
    color: colors.muted,
    fontSize: 12,
    fontWeight: "800"
  },
  statCard: {
    flex: 1,
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
    fontSize: 24,
    fontWeight: "800"
  },
  choiceList: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 10
  },
  choiceChip: {
    borderRadius: 999,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surfaceStrong,
    paddingHorizontal: 14,
    paddingVertical: 10
  },
  choiceChipActive: {
    backgroundColor: colors.action,
    borderColor: colors.action
  },
  choiceText: {
    color: colors.ink,
    fontWeight: "700"
  },
  choiceTextActive: {
    color: colors.actionText
  },
  ratingPicker: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 10
  },
  ratingChip: {
    borderRadius: 999,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surfaceStrong,
    paddingHorizontal: 14,
    paddingVertical: 10
  },
  ratingChipActive: {
    backgroundColor: colors.action,
    borderColor: colors.action
  },
  ratingChipText: {
    color: colors.ink,
    fontWeight: "700"
  },
  ratingChipTextActive: {
    color: colors.actionText
  },
  textArea: {
    minHeight: 110,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surfaceStrong,
    paddingHorizontal: 14,
    paddingVertical: 12,
    color: colors.ink,
    fontSize: 16,
    textAlignVertical: "top"
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
  buttonDisabled: {
    opacity: 0.7
  },
  errorText: {
    color: colors.danger,
    fontSize: 14,
    lineHeight: 20,
    fontWeight: "600"
  },
  noticeText: {
    color: colors.accent,
    fontSize: 14,
    lineHeight: 20,
    fontWeight: "600",
    backgroundColor: colors.surfaceStrong,
    borderRadius: 14,
    paddingHorizontal: 12,
    paddingVertical: 10
  },
  emptyText: {
    color: colors.muted,
    fontSize: 15,
    lineHeight: 22
  },
  stack: {
    gap: 12
  },
  reviewCard: {
    borderRadius: 20,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surfaceStrong,
    padding: 14,
    gap: 8
  },
  reviewHeader: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12
  },
  reviewerAvatar: {
    width: 44,
    height: 44,
    borderRadius: 22
  },
  reviewerAvatarPlaceholder: {
    width: 44,
    height: 44,
    borderRadius: 22,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.accentSoft
  },
  reviewerAvatarInitial: {
    color: colors.accent,
    fontSize: 18,
    fontWeight: "900"
  },
  reviewerInfo: {
    flex: 1,
    gap: 2
  },
  reviewerName: {
    color: colors.ink,
    fontSize: 15,
    fontWeight: "800"
  },
  reviewerUsername: {
    color: colors.muted,
    fontSize: 12,
    fontWeight: "700"
  },
  reviewTitle: {
    color: colors.ink,
    fontSize: 15,
    fontWeight: "800"
  },
  reviewMeta: {
    color: colors.accent,
    fontSize: 12,
    fontWeight: "700"
  },
  reviewText: {
    color: colors.muted,
    fontSize: 14,
    lineHeight: 21
  }
});
