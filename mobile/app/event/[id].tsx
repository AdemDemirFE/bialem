import * as ExpoLinking from "expo-linking";
import { Link, useLocalSearchParams, useRouter } from "expo-router";
import QRCode from "react-native-qrcode-svg";
import { useEffect, useMemo, useState } from "react";
import {
  Pressable,
  RefreshControl,
  Share,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Reveal, Skeleton } from "../../src/animations";
import { FeedbackState } from "../../src/components/ui/FeedbackState";
import { showAppAlert, showAppConfirm, showAppError, showEventJoinResult } from "../../src/components/AppAlert";
import { TeamIdentityBadge } from "../../src/components/TeamIdentityBadge";
import { useAuth } from "../../src/lib/auth";
import { addEventToCalendar } from "../../src/lib/calendar";
import { eventPublicUrl } from "../../src/lib/links";
import { api } from "../../src/lib/api";
import { getPlatformTeamIdentityMap, type PlatformTeamRole } from "../../src/lib/team-identities";
import { colors } from "../../src/theme/colors";

type EventRecord = {
  id: number;
  createdBy?: { id: number } | null;
  title: string;
  description?: string | null;
  startsAt?: string;
  endsAt?: string | null;
  locationName?: string | null;
  addressText?: string | null;
  capacity?: number | null;
  status?: string;
  community?: { id: number } | null;
  groupModerationStatus?: string | null;
  platformModerationStatus?: string | null;
  cancelledAt?: string | null;
  cancellationReason?: string | null;
};

type EventCreationGroup = {
  id: string;
  creation_mode: "direct" | "proposal";
};

type CommunityRecord = {
  id: number;
  name: string;
  slug: string;
};

type CommentRecord = {
  id: string;
  author_id: string;
  body: string;
  created_at: string;
};

type RatingRecord = {
  id: string;
  user_id: string;
  rating: number;
  review_text: string | null;
  created_at: string;
};

type ParticipantRecord = {
  id: string;
  status: string;
};

type ParticipationSummary = {
  pending_count: number;
  approved_count: number;
  waitlisted_count: number;
  checked_in_count: number;
  my_status: string | null;
  my_waitlist_position: number;
  can_manage: boolean;
};

export default function EventDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const insets = useSafeAreaInsets();
  const router = useRouter();
  const { user } = useAuth();
  const [event, setEvent] = useState<EventRecord | null>(null);
  const [community, setCommunity] = useState<CommunityRecord | null>(null);
  const [comments, setComments] = useState<CommentRecord[]>([]);
  const [commentTeamRoles, setCommentTeamRoles] = useState<Map<string, PlatformTeamRole>>(new Map());
  const [ratings, setRatings] = useState<RatingRecord[]>([]);
  const [participant, setParticipant] = useState<ParticipantRecord | null>(null);
  const [participationSummary, setParticipationSummary] = useState<ParticipationSummary | null>(null);
  const [canReviewEvent, setCanReviewEvent] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [moderating, setModerating] = useState<"published" | "rejected" | null>(null);
  const [rejectionReason, setRejectionReason] = useState("");
  const [cancellationReason, setCancellationReason] = useState("");
  const [cancellingEvent, setCancellingEvent] = useState(false);
  const [commentBody, setCommentBody] = useState("");
  const [ratingValue, setRatingValue] = useState(5);
  const [ratingText, setRatingText] = useState("");
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [joining, setJoining] = useState(false);
  const [requiresGroupMembership, setRequiresGroupMembership] = useState(false);
  const [sendingComment, setSendingComment] = useState(false);
  const [sendingRating, setSendingRating] = useState(false);
  const [reportingTarget, setReportingTarget] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const loadEvent = async (mode: "initial" | "refresh" = "initial") => {
    if (!id) {
      setError("Etkinlik bulunamadı.");
      return;
    }

    if (mode === "initial") {
      setLoading(true);
    } else {
      setRefreshing(true);
    }

    setError(null);

    const eventResult = await api.events.getById(id);

    if (eventResult.error) {
      setError(eventResult.error.message);
      setLoading(false);
      setRefreshing(false);
      return;
    }

    const nextEvent = eventResult.data ?? null;
    setEvent(nextEvent);

    if (!nextEvent) {
      setLoading(false);
      setRefreshing(false);
      return;
    }

    const [communityResult, commentsResult, ratingsResult] = await Promise.all([
      nextEvent.community?.id ? api.communities.getById(String(nextEvent.community.id)) : Promise.resolve({ data: null, error: null }),
      api.comments.listByTarget("event", String(nextEvent.id)),
      api.eventRatings.listByEvent(String(nextEvent.id))
    ]);

    if (communityResult.error) {
      setError(communityResult.error.message);
    } else {
      setCommunity(communityResult.data ?? null);
    }

    if (commentsResult.error) {
      setError(commentsResult.error.message);
    } else {
      const nextComments = (commentsResult.data ?? []) as CommentRecord[];
      setComments(nextComments);
      setCommentTeamRoles(await getPlatformTeamIdentityMap(nextComments.map((comment) => comment.author_id)));
    }

    if (ratingsResult.error) {
      setError(ratingsResult.error.message);
    } else {
      setRatings((ratingsResult.data ?? []) as RatingRecord[]);
    }

    if (user) {
      const [participantResult, summaryResult, creationGroupsResult, adminResult] = await Promise.all([
        api.from("event_participants").select("id, status").eq("event_id", nextEvent.id).eq("user_id", user.id).maybeSingle<ParticipantRecord>(),
        api.rpc("get_event_participation_summary", { target_event_id: nextEvent.id }).maybeSingle(),
        api.rpc("get_my_event_creation_groups"),
        api.rpc("is_admin")
      ]);

      if (!participantResult.error) {
        setParticipant(participantResult.data ?? null);
      }
      if (!summaryResult.error) setParticipationSummary((summaryResult.data ?? null) as ParticipationSummary | null);
      const creationGroups = (creationGroupsResult.data ?? []) as EventCreationGroup[];
      setCanReviewEvent(
        !creationGroupsResult.error &&
        creationGroups.some((group) => nextEvent.community?.id && String(nextEvent.community.id) === group.id && group.creation_mode === "direct")
      );
      setIsAdmin(!adminResult.error && adminResult.data === true);
    } else {
      setParticipant(null);
      setParticipationSummary(null);
      setCanReviewEvent(false);
      setIsAdmin(false);
    }

    if (mode === "initial") {
      setLoading(false);
    } else {
      setRefreshing(false);
    }
  };

  useEffect(() => {
    void loadEvent();
  }, [id, user?.id]);

  const averageRating = useMemo(() => {
    if (ratings.length === 0) {
      return null;
    }

    const total = ratings.reduce((sum, item) => sum + item.rating, 0);
    return (total / ratings.length).toFixed(1);
  }, [ratings]);

  const handleJoinRequest = async () => {
    if (!event || !user) {
      setError("Katılım talebi için aktif oturum gerekir.");
      await showAppError("Katılım talebi için aktif oturum gerekir.", "Oturum gerekli");
      return;
    }

    setJoining(true);
    setError(null);
    setNotice(null);
    setRequiresGroupMembership(false);

    const { data: nextStatus, error: joinError } = await api.rpc("request_event_participation", { target_event_id: event.id });

    await showEventJoinResult(nextStatus, joinError);
    if (joinError) {
      if (joinError.message.includes("Join the event group first")) {
        setError("Katılım talebi göndermek için önce etkinliğin grubuna katılmalısın.");
        setRequiresGroupMembership(true);
      } else {
        setError(joinError.message);
      }
      setJoining(false);
      return;
    }

    setJoining(false);
    await loadEvent("refresh");
  };

  const cancelParticipation = async () => {
    if (!event) return;
    setJoining(true);
    setError(null);
    setNotice(null);
    const { error: cancelError } = await api.rpc("cancel_event_participation", { target_event_id: event.id });
    if (cancelError) setError(cancelError.message);
    else setNotice("Katılımın iptal edildi; varsa sıradaki kişiye yer açıldı.");
    setJoining(false);
    await loadEvent("refresh");
  };

  const handleCancelParticipation = async () => {
    if (!participant) return;

    const title = participant.status === "approved" ? "Katılımı iptal et" : "Talebi geri çek";
    const message = participant.status === "approved"
      ? "Onaylanmış katılımın iptal edilecek ve yerin varsa bekleme listesindeki sıradaki kişiye açılacak. Devam edilsin mi?"
      : participant.status === "waitlisted"
        ? "Bekleme listesinden çıkmak istediğine emin misin?"
        : "Katılım talebini geri çekmek istediğine emin misin?";

    const confirmed = await showAppConfirm({
      title,
      text: message,
      confirmText: "Evet, iptal et",
      confirmDanger: true
    });
    if (confirmed) void cancelParticipation();
  };

  const getInvite = () => {
    if (!event) return { url: "", text: "" };
    const url = eventPublicUrl(String(event.id));
    return { url, text: `Bialem'da ${event.title} etkinliğine benimle katıl! ${formatDate(event.startsAt ?? "")} ${event.locationName || ""}` };
  };

  const shareEvent = async () => {
    const invite = getInvite();
    await Share.share({ title: event?.title, message: `${invite.text}\n${invite.url}`, url: invite.url });
  };

  const shareOnWhatsApp = async () => {
    const invite = getInvite();
    await ExpoLinking.openURL(`https://wa.me/?text=${encodeURIComponent(`${invite.text}\n${invite.url}`)}`);
  };

  const shareOnInstagram = async () => {
    const invite = getInvite();
    await Share.share(
      { title: "Instagram'da paylaş", message: `${invite.text}\n${invite.url}`, url: invite.url },
      { dialogTitle: "Instagram veya Instagram Hikâyeleri ile paylaş" }
    );
  };

  const openInstagramPoster = () => {
    if (event) router.push(`/event/${event.id}/poster` as never);
  };

  const handleAddToCalendar = async () => {
    if (!event) return;
    setError(null);
    try {
      await addEventToCalendar({
        id: String(event.id),
        title: event.title,
        description: event.description ?? null,
        starts_at: event.startsAt ?? "",
        ends_at: event.endsAt ?? null,
        location_name: event.locationName ?? null,
        address_text: event.addressText ?? null,
        public_url: eventPublicUrl(String(event.id))
      });
      setNotice("Etkinlik takvim ekranına hazırlandı.");
    } catch (calendarError) {
      setError(calendarError instanceof Error ? calendarError.message : "Takvim açılamadı.");
    }
  };

  const handleCommentSubmit = async () => {
    if (!event || !user) {
      setError("Yorum yazmak için aktif oturum gerekir.");
      return;
    }

    if (!commentBody.trim()) {
      setError("Yorum bos bırakılamaz.");
      return;
    }

    setSendingComment(true);
    setError(null);
    setNotice(null);

    const { error: commentError } = await api.from("comments").insert({
      target_type: "event",
      target_id: event.id,
      author_id: user.id,
      body: commentBody.trim()
    });

    if (commentError) {
      setError(commentError.message);
      setSendingComment(false);
      return;
    }

    setCommentBody("");
    setNotice("Yorumun paylaşıldı.");
    setSendingComment(false);
    await loadEvent("refresh");
  };

  const handleRatingSubmit = async () => {
    if (!event || !user) {
      setError("Puan vermek için aktif oturum gerekir.");
      return;
    }

    setSendingRating(true);
    setError(null);
    setNotice(null);

    const { error: ratingError } = await api.from("event_ratings").upsert({
      event_id: event.id,
      user_id: user.id,
      rating: ratingValue,
      review_text: ratingText.trim() || null
    });

    if (ratingError) {
      setError(
        ratingError.message.includes("eligible to rate")
          ? "Bu etkinliğe puan verebilmek için katılım talebinizin onaylanmış olması gerekir."
          : ratingError.message
      );
      setSendingRating(false);
      return;
    }

    setRatingText("");
    setNotice("Puanin kaydedildi.");
    setSendingRating(false);
    await loadEvent("refresh");
  };

  const handleReport = async (targetType: "event" | "comment", targetId: string, details: string) => {
    if (!user) {
      setError("Rapor gönderebilmek için aktif oturum gerekir.");
      return;
    }

    setReportingTarget(targetId);
    setError(null);
    setNotice(null);

    const { error: reportError } = await api.from("reports").insert({
      reporter_id: user.id,
      target_type: targetType,
      target_id: targetId,
      reason: "Uygunsuz içerik",
      details
    });

    if (reportError) {
      setError(reportError.message);
      setReportingTarget(null);
      return;
    }

    setNotice("Rapor admin ekibine iletildi.");
    setReportingTarget(null);
  };

  const handleModeration = async (targetStatus: "published" | "rejected") => {
    if (!event || moderating) return;

    if (targetStatus === "rejected" && !rejectionReason.trim()) {
      setError("Etkinliği reddetmek için kısa bir gerekçe yazın.");
      return;
    }

    setModerating(targetStatus);
    setError(null);
    setNotice(null);

    const { error: moderationError } = await api.rpc("moderate_group_event", {
      target_event_id: event.id,
      target_status: targetStatus,
      target_rejection_reason: targetStatus === "rejected" ? rejectionReason.trim() : null
    });

    if (moderationError) {
      setError(moderationError.message);
    } else {
      setNotice(targetStatus === "published" ? "Etkinlik onaylandı." : "Etkinlik reddedildi.");
      setRejectionReason("");
      await loadEvent("refresh");
    }
    setModerating(null);
  };

  const cancelEvent = async () => {
    if (!event || cancellingEvent) return;

    setCancellingEvent(true);
    setError(null);
    setNotice(null);

    const { error: cancellationError } = await api.rpc("cancel_event", {
      target_event_id: event.id,
      target_reason: cancellationReason.trim() || null
    });

    if (cancellationError) {
      setError(cancellationError.message);
    } else {
      setNotice("Etkinlik iptal edildi ve katılımcılara bildirildi.");
      setCancellationReason("");
      await loadEvent("refresh");
    }
    setCancellingEvent(false);
  };

  const handleCancelEvent = async () => {
    const confirmed = await showAppConfirm({
      title: "Etkinliği iptal et",
      text: "Etkinlik yayından kaldırılacak, açık katılımlar iptal edilecek ve katılımcılara bildirim gönderilecek. Bu işlemi yapmak istiyor musunuz?",
      confirmText: "Etkinliği iptal et",
      confirmDanger: true
    });
    if (confirmed) void cancelEvent();
  };

  const isEventStaff = canReviewEvent || Boolean(participationSummary?.can_manage);

  return (
    <ScrollView
      contentContainerStyle={[styles.page, { paddingTop: insets.top + 16, paddingBottom: insets.bottom + 28 }]}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadEvent("refresh")} tintColor={colors.accent} />}
    >
      <Link href="/(tabs)/feed" asChild>
        <Pressable style={styles.backButton}>
          <Text style={styles.backButtonText}>Akışa dön</Text>
        </Pressable>
      </Link>

      {loading ? (
        <View style={styles.centerBox}>
          <Skeleton height={30} width="45%" />
          <Skeleton height={150} width="100%" />
          <Skeleton height={220} width="100%" />
        </View>
      ) : !event ? (
        <FeedbackState
          kind="empty"
          title="Etkinlik bulunamadı"
          message={error || "Bu etkinlik kaydı şu anda görüntülenemiyor."}
          onRetry={() => void loadEvent("refresh")}
        />
      ) : (
        <>
          <Reveal>
          <View style={styles.hero}>
            <Text style={styles.kicker}>Etkinlik Detayı</Text>
            <Text style={styles.title}>{event.title}</Text>
          <Text style={styles.description}>{event.description || "Açıklama eklenmemiş."}</Text>
          </View>
          </Reveal>

          <Reveal index={1}>
          <View style={styles.panel}>
            <Text style={styles.panelTitle}>Bilgiler</Text>
            <InfoRow label="Topluluk" value={community?.name ?? "Topluluk yok"} />
            <Link href={{ pathname: "/user/[id]", params: { id: String(event.createdBy?.id ?? "") } }} asChild>
              <Pressable style={styles.organizerButton}>
                <Text style={styles.organizerButtonText}>Organizatör profilini gör ve takip et</Text>
              </Pressable>
            </Link>
          <InfoRow label="Başlangıç" value={formatDate(event.startsAt ?? "")} />
          <InfoRow label="Bitiş" value={event.endsAt ? formatDate(event.endsAt) : "Belirtilmedi"} />
          <InfoRow label="Mekân" value={event.locationName || "Belirtilmedi"} />
          <InfoRow label="Adres" value={event.addressText || "Belirtilmedi"} />
          <InfoRow label="Katılım limiti" value={event.capacity ? String(event.capacity) : "Sınırsız"} />
            <InfoRow label="Durum" value={eventStatusLabel(event.status ?? "")} />
            {event.status === "CANCELLED" ? (
              <View style={styles.panel}>
                <Text style={styles.panelTitle}>Etkinlik iptal edildi</Text>
                <Text style={styles.panelText}>{event.cancellationReason || "Organizatör tarafından iptal edildi."}</Text>
              </View>
            ) : null}
            {canReviewEvent && event.status === "PENDING_APPROVAL" && (event.groupModerationStatus !== "APPROVED" || isAdmin) ? (
              <View style={styles.moderationBox}>
                <Text style={styles.moderationTitle}>Etkinlik moderasyonu</Text>
                <Text style={styles.panelText}>Etkinliği inceleyip yayınlayabilir veya gerekçe belirterek reddedebilirsiniz.</Text>
                <TextInput
                  value={rejectionReason}
                  onChangeText={setRejectionReason}
                  placeholder="Red gerekçesi (reddetme için zorunlu)"
                  placeholderTextColor={colors.muted}
                  multiline
                  style={styles.moderationInput}
                />
                <View style={styles.moderationActions}>
                  <Pressable
                    style={[styles.approveButton, moderating !== null && styles.buttonDisabled]}
                    disabled={moderating !== null}
                    onPress={() => void handleModeration("published")}
                  >
                    <Text style={styles.approveButtonText}>{moderating === "published" ? "Onaylanıyor..." : "Etkinliği onayla"}</Text>
                  </Pressable>
                  <Pressable
                    style={[styles.rejectButton, moderating !== null && styles.buttonDisabled]}
                    disabled={moderating !== null}
                    onPress={() => void handleModeration("rejected")}
                  >
                    <Text style={styles.rejectButtonText}>{moderating === "rejected" ? "Reddediliyor..." : "Reddet"}</Text>
                  </Pressable>
                </View>
              </View>
            ) : null}
            {isEventStaff && ["PENDING_APPROVAL", "PUBLISHED"].includes(event.status ?? "") ? (
              <View style={styles.moderationBox}>
                <Text style={styles.moderationTitle}>Etkinliği iptal et</Text>
                <Text style={styles.panelText}>İptal nedeni katılımcılara bildirim olarak gönderilir.</Text>
                <TextInput
                  value={cancellationReason}
                  onChangeText={setCancellationReason}
                  placeholder="İptal nedeni (isteğe bağlı)"
                  placeholderTextColor={colors.muted}
                  multiline
                  style={styles.moderationInput}
                />
                <Pressable
                  style={[styles.rejectButton, cancellingEvent && styles.buttonDisabled]}
                  disabled={cancellingEvent}
                  onPress={handleCancelEvent}
                >
                  <Text style={styles.rejectButtonText}>{cancellingEvent ? "İptal ediliyor..." : "Etkinliği iptal et"}</Text>
                </Pressable>
              </View>
            ) : null}
            <Pressable
              style={[styles.reportButton, reportingTarget === String(event.id) && styles.buttonDisabled]}
              onPress={() => void handleReport("event", String(event.id), `Etkinlik raporu: ${event.title}`)}
            >
              <Text style={styles.reportButtonText}>
                {reportingTarget === String(event.id) ? "Raporlanıyor..." : "Etkinliği rapor et"}
              </Text>
            </Pressable>
          </View>
          </Reveal>

          {event.status !== "CANCELLED" ? <Reveal index={2}><View style={styles.inviteCard}>
            <View style={styles.inviteCopy}>
              <Text style={styles.inviteKicker}>ARKADAŞINI DA GETİR</Text>
              <Text style={styles.inviteTitle}>Bu deneyim birlikte daha güzel.</Text>
              <Text style={styles.inviteText}>Davet bağlantısı doğrudan bu etkinliği açar.</Text>
            </View>
            <View style={styles.inviteActions}>
              <Pressable style={styles.whatsAppButton} onPress={() => void shareOnWhatsApp()}>
                <Text style={styles.whatsAppText}>WhatsApp</Text>
              </Pressable>
              <Pressable style={styles.instagramButton} onPress={openInstagramPoster}>
                <Text style={styles.instagramText}>Instagram</Text>
              </Pressable>
              <Pressable style={styles.shareButton} onPress={() => void shareEvent()}>
                <Text style={styles.shareText}>Diğer</Text>
              </Pressable>
              <Pressable style={styles.shareButton} onPress={() => void handleAddToCalendar()}>
                <Text style={styles.shareText}>Takvim</Text>
              </Pressable>
            </View>
          </View></Reveal> : null}

          <Reveal index={3}>
          <View style={styles.statsRow}>
            <StatCard label="Yorum" value={String(comments.length)} />
            <StatCard label="Katılıyor" value={String((participationSummary?.approved_count ?? 0) + (participationSummary?.checked_in_count ?? 0))} />
            <StatCard label="Puan" value={averageRating ? `${averageRating}/5` : "-"} />
          </View>
          </Reveal>

          <Reveal index={4}>
          <View style={styles.panel}>
            <Text style={styles.panelTitle}>Katılım ve etkileşim</Text>
            {error ? <Text style={styles.errorText}>{error}</Text> : null}
            {event.status === "CANCELLED" ? (
              <View style={styles.cancelledBanner}>
                <Text style={styles.cancelledTitle}>Katılım kapatıldı</Text>
                <Text style={styles.panelText}>İptal edilen bu etkinlik için yeni katılım alınmıyor.</Text>
              </View>
            ) : requiresGroupMembership && community ? (
              <Link href={{ pathname: "/group/[id]", params: { id: String(community.id) } }} asChild>
                <Pressable style={styles.primaryButton}>
                  <Text style={styles.primaryButtonText}>Etkinlik grubuna git</Text>
                </Pressable>
              </Link>
            ) : null}
            {notice ? <Text style={styles.noticeText}>{notice}</Text> : null}
            <Text style={styles.panelText}>
              Talebin onaylandığında etkinlik sohbetine ve kişisel giriş QR koduna erişebilirsin.
            </Text>
            {!isEventStaff && participant?.status === "waitlisted" ? (
              <View style={styles.waitlistCard}>
                <Text style={styles.waitlistPosition}>Sıran: {participationSummary?.my_waitlist_position || "-"}</Text>
                <Text style={styles.panelText}>Yer açıldığında talebin otomatik olarak değerlendirmeye alınacak.</Text>
              </View>
            ) : null}
            {event.status === "PUBLISHED" && !isEventStaff ? (
              <Pressable style={styles.secondaryActionButton} onPress={() => router.push(`/event/tickets/${event.id}` as never)}>
                <Text style={styles.secondaryActionText}>Bilet al</Text>
              </Pressable>
            ) : null}
            {event.status === "CANCELLED" ? null : isEventStaff ? (
              <View style={styles.statusBanner}>
                <Text style={styles.statusBannerText}>Bu etkinliği yönetici yetkisiyle görüntülüyorsunuz.</Text>
              </View>
            ) : !participant || ["cancelled", "rejected", "no_show"].includes(participant.status) ? (
              <Pressable style={[styles.primaryButton, joining && styles.buttonDisabled]} onPress={() => void handleJoinRequest()}>
                <Text style={styles.primaryButtonText}>{joining ? "Gönderiliyor..." : "Katılım talebi gönder"}</Text>
              </Pressable>
            ) : (
              <>
                <View style={styles.statusBanner}><Text style={styles.statusBannerText}>{participantStatusLabel(participant.status)}</Text></View>
                {participant.status !== "checked_in" ? (
                  <Pressable
                    disabled={joining}
                    style={[styles.cancelButton, joining && styles.buttonDisabled]}
                    onPress={handleCancelParticipation}
                  >
                    <Text style={styles.cancelButtonText}>
                      {joining
                        ? "İptal ediliyor..."
                        : participant.status === "approved"
                          ? "Katılımı iptal et"
                          : participant.status === "waitlisted"
                            ? "Bekleme listesinden çık"
                            : "Katılım talebini geri çek"}
                    </Text>
                  </Pressable>
                ) : null}
              </>
            )}

            {event.status !== "CANCELLED" && !isEventStaff && participant && ["approved", "checked_in"].includes(participant.status) && user ? (
              <View style={styles.memberTools}>
                <View style={styles.qrWrap}>
                  <QRCode value={`bialem-checkin:${event.id}:${user.id}`} size={150} color="#081a44" backgroundColor="#ffffff" />
                  <Text style={styles.qrHint}>{participant.status === "checked_in" ? "Girişin doğrulandı" : "Girişte organizatöre göster"}</Text>
                </View>
                <Link href={`/event/${event.id}/chat` as never} asChild>
                  <Pressable style={styles.chatButton}><Text style={styles.chatButtonText}>Katılımcı sohbetini aç</Text></Pressable>
                </Link>
              </View>
            ) : null}

            {event.status !== "CANCELLED" && participationSummary?.can_manage ? (
              <View style={styles.managerTools}>
                <Link href={{ pathname: "/event/[id]/check-in", params: { id: String(event.id) } }} asChild>
                  <Pressable style={styles.managerButton}><Text style={styles.managerButtonText}>Katılımcıları yönet ve QR tara</Text></Pressable>
                </Link>
                <Link href={`/event/${event.id}/chat` as never} asChild>
                  <Pressable style={styles.cancelButton}><Text style={styles.cancelButtonText}>Etkinlik sohbetini aç</Text></Pressable>
                </Link>
              </View>
            ) : null}
          </View>
          </Reveal>

          <Reveal index={5}>
          <View style={styles.panel}>
            <Text style={styles.panelTitle}>Yorumlar</Text>
            <Text style={styles.panelHint}>
              Yorumlar etkinlik sayfasında herkese açık görünür; nazik ve yapıcı yorumlar topluluk deneyimini güzelleştirir.
            </Text>
            <View style={styles.formGroup}>
              <TextInput
                value={commentBody}
                onChangeText={setCommentBody}
                placeholder="Etkinlik hakkındaki düşünceni yaz"
                placeholderTextColor="#7d877d"
                multiline
                style={styles.textArea}
              />
              <Pressable style={[styles.secondaryActionButton, sendingComment && styles.buttonDisabled]} onPress={() => void handleCommentSubmit()}>
                <Text style={styles.secondaryActionText}>{sendingComment ? "Gönderiliyor..." : "Yorum paylaş"}</Text>
              </Pressable>
            </View>

            {comments.length === 0 ? (
              <Text style={styles.emptyText}>Henüz yorum yok. İlk yorumu sen bırakabilirsin.</Text>
            ) : (
              <View style={styles.stack}>
                {comments.map((comment, i) => (
                  <Reveal key={comment.id} index={Math.min(i, 4)}>
                  <View style={styles.commentCard}>
                    <View style={styles.commentIdentity}>
                      <Text style={styles.commentAuthor}>{maskUser(comment.author_id)}</Text>
                      <TeamIdentityBadge role={commentTeamRoles.get(comment.author_id)} compact />
                    </View>
                    <Text style={styles.commentDate}>{formatDate(comment.created_at)}</Text>
                    <Text style={styles.commentBody}>{comment.body}</Text>
                    <Link href={{ pathname: "/user/[id]", params: { id: comment.author_id } }} asChild>
                      <Pressable>
                        <Text style={styles.inlineLinkText}>Kullanıcı profilini aç</Text>
                      </Pressable>
                    </Link>
                    <Pressable
                      style={styles.inlineReportButton}
                      onPress={() =>
                        void handleReport("comment", comment.id, `Etkinlik yorumu raporu: ${comment.body.slice(0, 80)}`)
                      }
                    >
                      <Text style={styles.inlineReportText}>
                        {reportingTarget === comment.id ? "Raporlanıyor..." : "Yorumu rapor et"}
                      </Text>
                    </Pressable>
                  </View>
                  </Reveal>
                ))}
              </View>
            )}
          </View>
          </Reveal>

          <Reveal index={6}>
          <View style={styles.panel}>
              <Text style={styles.panelTitle}>Yıldız puanı</Text>
            <Text style={styles.panelText}>
              Bu alan etkinliğe katılan üyelerin deneyim paylaşımı için ayrıldı. Katılım onayından önce puan vermeye
              calisirsan sistem seni uyarir.
            </Text>
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
              value={ratingText}
              onChangeText={setRatingText}
              placeholder="İstersen kısa bir değerlendirme yaz"
              placeholderTextColor="#7d877d"
              multiline
              style={styles.textArea}
            />
            <Pressable style={[styles.primaryButton, sendingRating && styles.buttonDisabled]} onPress={() => void handleRatingSubmit()}>
              <Text style={styles.primaryButtonText}>{sendingRating ? "Kaydediliyor..." : "Puanı kaydet"}</Text>
            </Pressable>

            {ratings.length > 0 ? (
              <View style={styles.stack}>
                {ratings.slice(0, 5).map((rating) => (
                  <View key={rating.id} style={styles.commentCard}>
                    <Text style={styles.commentAuthor}>
                      {maskUser(rating.user_id)} - {rating.rating}/5
                    </Text>
                    <Text style={styles.commentDate}>{formatDate(rating.created_at)}</Text>
                    <Text style={styles.commentBody}>{rating.review_text || "Yalnızca puan bırakıldı."}</Text>
                    <Link href={{ pathname: "/user/[id]", params: { id: rating.user_id } }} asChild>
                      <Pressable>
                        <Text style={styles.inlineLinkText}>Kullanıcı profilini aç</Text>
                      </Pressable>
                    </Link>
                  </View>
                ))}
              </View>
            ) : null}
          </View>
          </Reveal>
        </>
      )}
    </ScrollView>
  );
}

function participantStatusLabel(status: string) {
  return ({
    pending: "Talebin moderatör onayında",
    waitlisted: "Bekleme listesindesin",
    approved: "Katılımın onaylandı",
    checked_in: "Etkinliğe girişin doğrulandı",
    cancelled: "Katılımı iptal ettin",
    rejected: "Katılım talebin reddedildi",
    no_show: "Etkinliğe katılmadın"
  } as Record<string, string>)[status] ?? status;
}

function eventStatusLabel(status?: string) {
  if (!status) return "";
  return ({
    DRAFT: "Taslak",
    PENDING_APPROVAL: "Onay bekliyor",
    PUBLISHED: "Yayında",
    REJECTED: "Reddedildi",
    CANCELLED: "İptal edildi",
    COMPLETED: "Tamamlandı"
  } as Record<string, string>)[status] ?? status;
}

function InfoRow({ label, value }: { label: string; value: string }) {
  return (
    <View style={styles.infoRow}>
      <Text style={styles.infoLabel}>{label}</Text>
      <Text style={styles.infoValue}>{value}</Text>
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

function maskUser(userId: string) {
  return `Üye ${userId.slice(0, 6)}`;
}

const styles = StyleSheet.create({
  page: {
    flexGrow: 1,
    paddingHorizontal: 24,
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
  organizerButton: {
    alignSelf: "flex-start",
    backgroundColor: colors.accentSoft,
    borderRadius: 999,
    paddingHorizontal: 15,
    paddingVertical: 11
  },
  organizerButtonText: {
    color: colors.accent,
    fontSize: 13,
    fontWeight: "800"
  },
  panelText: {
    color: colors.muted,
    fontSize: 15,
    lineHeight: 22
  },
  panelHint: {
    color: colors.accent,
    fontSize: 13,
    lineHeight: 19,
    fontWeight: "600"
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
  infoRow: {
    gap: 6,
    paddingBottom: 10,
    borderBottomWidth: 1,
    borderBottomColor: colors.border
  },
  infoLabel: {
    color: colors.muted,
    fontSize: 13,
    fontWeight: "700"
  },
  infoValue: {
    color: colors.ink,
    fontSize: 16,
    fontWeight: "700"
  },
  statsRow: {
    flexDirection: "row",
    gap: 12
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
  secondaryActionButton: {
    alignSelf: "flex-start",
    backgroundColor: colors.surfaceStrong,
    borderRadius: 999,
    paddingVertical: 12,
    paddingHorizontal: 16
  },
  secondaryActionText: {
    color: colors.ink,
    fontSize: 14,
    fontWeight: "800"
  },
  reportButton: {
    marginTop: 4,
    alignSelf: "flex-start",
    backgroundColor: colors.surfaceStrong,
    borderRadius: 999,
    borderWidth: 1,
    borderColor: "#efc6b7",
    paddingVertical: 12,
    paddingHorizontal: 16
  },
  reportButtonText: {
    color: colors.danger,
    fontSize: 14,
    fontWeight: "800"
  },
  inlineReportButton: {
    alignSelf: "flex-start",
    marginTop: 4
  },
  inlineReportText: {
    color: colors.danger,
    fontSize: 12,
    fontWeight: "700"
  },
  inlineLinkText: {
    color: colors.accent,
    fontSize: 12,
    fontWeight: "700"
  },
  buttonDisabled: {
    opacity: 0.7
  },
  formGroup: {
    gap: 10
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
  emptyText: {
    color: colors.muted,
    fontSize: 15,
    lineHeight: 22
  },
  stack: {
    gap: 12
  },
  commentCard: {
    borderRadius: 20,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surfaceStrong,
    padding: 14,
    gap: 6
  },
  commentIdentity: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6
  },
  commentAuthor: {
    color: colors.ink,
    fontSize: 14,
    fontWeight: "800"
  },
  commentDate: {
    color: colors.accent,
    fontSize: 12,
    fontWeight: "700"
  },
  commentBody: {
    color: colors.muted,
    fontSize: 14,
    lineHeight: 21
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
  inviteCard: {
    padding: 18,
    gap: 14,
    borderRadius: 26,
    backgroundColor: colors.brandInk
  },
  inviteCopy: {
    gap: 5
  },
  inviteKicker: {
    color: colors.aqua,
    fontSize: 10,
    fontWeight: "900",
    letterSpacing: 1.2
  },
  inviteTitle: {
    color: colors.onBrand,
    fontSize: 20,
    fontWeight: "900"
  },
  inviteText: {
    color: "#cfd6cf",
    fontSize: 13
  },
  inviteActions: {
    flexDirection: "row",
    gap: 10
  },
  whatsAppButton: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 999,
    alignItems: "center",
    backgroundColor: "#25D366"
  },
  whatsAppText: {
    color: "#0b2a17",
    fontSize: 12,
    fontWeight: "900"
  },
  instagramButton: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 999,
    alignItems: "center",
    backgroundColor: "#d62976"
  },
  instagramText: {
    color: "#ffffff",
    fontSize: 12,
    fontWeight: "900"
  },
  shareButton: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 999,
    alignItems: "center",
    backgroundColor: colors.action
  },
  shareText: {
    color: colors.actionText,
    fontSize: 12,
    fontWeight: "900"
  },
  waitlistCard: {
    padding: 14,
    gap: 5,
    borderRadius: 18,
    backgroundColor: colors.accentSoft
  },
  waitlistPosition: {
    color: colors.ink,
    fontSize: 20,
    fontWeight: "900"
  },
  statusBanner: {
    padding: 13,
    borderRadius: 16,
    backgroundColor: colors.surfaceStrong
  },
  statusBannerText: {
    color: colors.ink,
    textAlign: "center",
    fontWeight: "900"
  },
  cancelButton: {
    paddingVertical: 13,
    paddingHorizontal: 16,
    borderRadius: 999,
    borderWidth: 1,
    borderColor: colors.border
  },
  cancelButtonText: {
    color: colors.ink,
    textAlign: "center",
    fontWeight: "800"
  },
  memberTools: {
    gap: 14,
    paddingTop: 8
  },
  qrWrap: {
    alignItems: "center",
    gap: 10,
    padding: 18,
    borderRadius: 22,
    backgroundColor: colors.surface
  },
  qrHint: {
    color: colors.muted,
    fontSize: 12,
    fontWeight: "700"
  },
  chatButton: {
    paddingVertical: 15,
    borderRadius: 999,
    backgroundColor: colors.brandInk
  },
  chatButtonText: {
    color: colors.onBrand,
    textAlign: "center",
    fontWeight: "900"
  },
  managerTools: {
    gap: 10,
    paddingTop: 8,
    borderTopWidth: 1,
    borderTopColor: colors.border
  },
  managerButton: {
    paddingVertical: 15,
    borderRadius: 999,
    backgroundColor: colors.accent
  },
  managerButtonText: {
    color: colors.onBrand,
    textAlign: "center",
    fontWeight: "900"
  },
  moderationBox: {
    gap: 10,
    padding: 14,
    borderRadius: 18,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surfaceStrong
  },
  cancelledBanner: {
    gap: 6,
    padding: 14,
    borderRadius: 18,
    borderWidth: 1,
    borderColor: colors.danger,
    backgroundColor: colors.surfaceStrong
  },
  cancelledTitle: {
    color: colors.danger,
    fontSize: 17,
    fontWeight: "900"
  },
  moderationTitle: {
    color: colors.ink,
    fontSize: 16,
    fontWeight: "900"
  },
  moderationInput: {
    minHeight: 76,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface,
    color: colors.ink,
    paddingHorizontal: 12,
    paddingVertical: 10,
    textAlignVertical: "top"
  },
  moderationActions: {
    flexDirection: "row",
    gap: 10
  },
  approveButton: {
    flex: 1,
    minHeight: 46,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 999,
    backgroundColor: colors.action
  },
  approveButtonText: {
    color: colors.actionText,
    fontWeight: "900"
  },
  rejectButton: {
    flex: 1,
    minHeight: 46,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 999,
    borderWidth: 1,
    borderColor: colors.danger
  },
  rejectButtonText: {
    color: colors.danger,
    fontWeight: "900"
  }
});
