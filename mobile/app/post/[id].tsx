import { Link, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Image, Pressable, RefreshControl, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { TeamIdentityBadge } from "../../src/components/TeamIdentityBadge";
import { useAuth } from "../../src/lib/auth";
import { api } from "../../src/lib/api";
import { getPlatformTeamIdentityMap, type PlatformTeamRole } from "../../src/lib/team-identities";
import { colors } from "../../src/theme/colors";

type PostRecord = {
  id: string;
  community_id: string | null;
  author_id: string;
  body: string | null;
  visibility: string;
  moderation_status: string;
  created_at: string;
  post_media: {
    id: string;
    media_type: string;
    storage_path: string;
    sort_order: number;
  }[];
};

type CommunityRecord = {
  id: string;
  name: string;
  slug: string;
};

type CommentRecord = {
  id: string;
  author_id: string;
  body: string;
  created_at: string;
};

export default function PostDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { user } = useAuth();
  const [post, setPost] = useState<PostRecord | null>(null);
  const [community, setCommunity] = useState<CommunityRecord | null>(null);
  const [comments, setComments] = useState<CommentRecord[]>([]);
  const [commentTeamRoles, setCommentTeamRoles] = useState<Map<string, PlatformTeamRole>>(new Map());
  const [commentBody, setCommentBody] = useState("");
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [sendingComment, setSendingComment] = useState(false);
  const [reportingTarget, setReportingTarget] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const loadPost = async (mode: "initial" | "refresh" = "initial") => {
    if (!id) {
      setError("Paylaşım bulunamadı.");
      return;
    }

    if (mode === "initial") {
      setLoading(true);
    } else {
      setRefreshing(true);
    }

    setError(null);

    const postResult = await api
      .from("posts")
      .select("id, community_id, author_id, body, visibility, moderation_status, created_at, post_media(id, media_type, storage_path, sort_order)")
      .eq("id", id)
      .maybeSingle<PostRecord>();

    if (postResult.error) {
      setError(postResult.error.message);
      setLoading(false);
      setRefreshing(false);
      return;
    }

    const nextPost = postResult.data ?? null;
    setPost(nextPost);

    if (!nextPost) {
      setLoading(false);
      setRefreshing(false);
      return;
    }

    const [communityResult, commentsResult] = await Promise.all([
      nextPost.community_id
        ? api.from("communities").select("id, name, slug").eq("id", nextPost.community_id).maybeSingle<CommunityRecord>()
        : Promise.resolve({ data: null, error: null }),
      api
        .from("comments")
        .select("id, author_id, body, created_at")
        .eq("target_type", "post")
        .eq("target_id", nextPost.id)
        .order("created_at", { ascending: false })
    ]);

    if (communityResult.error) {
      setError(communityResult.error.message);
    } else {
      setCommunity((communityResult.data as CommunityRecord | null) ?? null);
    }

    if (commentsResult.error) {
      setError(commentsResult.error.message);
    } else {
      const nextComments = (commentsResult.data ?? []) as CommentRecord[];
      setComments(nextComments);
      setCommentTeamRoles(await getPlatformTeamIdentityMap(nextComments.map((comment) => comment.author_id)));
    }

    if (mode === "initial") {
      setLoading(false);
    } else {
      setRefreshing(false);
    }
  };

  useEffect(() => {
    void loadPost();
  }, [id]);

  const handleCommentSubmit = async () => {
    if (!post || !user) {
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
      target_type: "post",
      target_id: post.id,
      author_id: user.id,
      body: commentBody.trim()
    });

    if (commentError) {
      setError(commentError.message);
      setSendingComment(false);
      return;
    }

    setCommentBody("");
    setNotice("Yorumun paylaşıma eklendi.");
    setSendingComment(false);
    await loadPost("refresh");
  };

  const handleReport = async (targetType: "post" | "comment", targetId: string, details: string) => {
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

  const backHref = post?.community_id ? { pathname: "/community/[id]" as const, params: { id: post.community_id } } : "/(tabs)/feed";

  return (
    <ScrollView
      contentContainerStyle={styles.page}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadPost("refresh")} tintColor={colors.accent} />}
    >
      <Link href={backHref} asChild>
        <Pressable style={styles.backButton}>
          <Text style={styles.backButtonText}>Geri dön</Text>
        </Pressable>
      </Link>

      {loading ? (
        <View style={styles.centerBox}>
          <ActivityIndicator color={colors.accent} />
          <Text style={styles.loadingText}>Paylaşım yükleniyor...</Text>
        </View>
      ) : !post ? (
        <View style={styles.panel}>
        <Text style={styles.panelTitle}>Paylaşım bulunamadı</Text>
          <Text style={styles.emptyText}>{error || "Bu paylaşım kaydı şu anda görüntülenemiyor."}</Text>
        </View>
      ) : (
        <>
          <View style={styles.hero}>
            <Text style={styles.kicker}>Paylaşım Detayı</Text>
            <Text style={styles.title}>{community?.name ?? "Topluluk paylaşımı"}</Text>
            <Text style={styles.description}>
              Topluluk akışı içindeki bu notun altına yorumlar bırakabilir, etkinlik sonrası deneyimleri konuşabilirsiniz.
            </Text>
          </View>

          <View style={styles.panel}>
            <Text style={styles.panelTitle}>Paylaşım</Text>
            <Text style={styles.metaText}>
              {maskUser(post.author_id)}
              {community?.name ? ` - ${community.name}` : ""}
              {` - ${formatDate(post.created_at)}`}
            </Text>
            <Text style={styles.postBody}>{post.body || "Icerik eklenmedi."}</Text>
            {post.post_media?.length ? (
              <View style={styles.mediaStack}>
                {post.post_media.map((media) => (
                  <Image key={media.id} source={{ uri: media.storage_path }} style={styles.postImage} resizeMode="cover" />
                ))}
              </View>
            ) : null}
            <Text style={styles.helperText}>
              Görünüm: {post.visibility} - Moderasyon: {post.moderation_status === "visible" ? "görünür" : post.moderation_status}
            </Text>
            <Pressable
              style={[styles.reportButton, reportingTarget === post.id && styles.buttonDisabled]}
              onPress={() => void handleReport("post", post.id, `Paylaşım raporu: ${(post.body || "").slice(0, 80)}`)}
            >
              <Text style={styles.reportButtonText}>
                {reportingTarget === post.id ? "Raporlanıyor..." : "Paylasimi rapor et"}
              </Text>
            </Pressable>
          </View>

          <View style={styles.panel}>
            <Text style={styles.panelTitle}>Yorum yaz</Text>
            {error ? <Text style={styles.errorText}>{error}</Text> : null}
            {notice ? <Text style={styles.noticeText}>{notice}</Text> : null}
            <TextInput
              value={commentBody}
              onChangeText={setCommentBody}
              placeholder="Bu paylaşım hakkındaki düşünceni yaz"
              placeholderTextColor="#7d877d"
              multiline
              style={styles.textArea}
            />
            <Pressable style={[styles.primaryButton, sendingComment && styles.buttonDisabled]} onPress={() => void handleCommentSubmit()}>
              <Text style={styles.primaryButtonText}>{sendingComment ? "Gönderiliyor..." : "Yorum paylaş"}</Text>
            </Pressable>
          </View>

          <View style={styles.panel}>
            <Text style={styles.panelTitle}>Yorum akışı</Text>
            {comments.length === 0 ? (
              <Text style={styles.emptyText}>Henüz yorum yok. İlk yorumu sen bırakabilirsin.</Text>
            ) : (
              <View style={styles.stack}>
                {comments.map((comment) => (
                  <View key={comment.id} style={styles.commentCard}>
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
                      onPress={() => void handleReport("comment", comment.id, `Paylaşım yorumu raporu: ${comment.body.slice(0, 80)}`)}
                    >
                      <Text style={styles.inlineReportText}>
                        {reportingTarget === comment.id ? "Raporlanıyor..." : "Yorumu rapor et"}
                      </Text>
                    </Pressable>
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
  metaText: {
    color: colors.accent,
    fontSize: 13,
    lineHeight: 18,
    fontWeight: "700"
  },
  postBody: {
    color: colors.ink,
    fontSize: 16,
    lineHeight: 24
  },
  mediaStack: {
    gap: 10
  },
  postImage: {
    width: "100%",
    height: 220,
    borderRadius: 18,
    backgroundColor: colors.surfaceStrong
  },
  helperText: {
    color: colors.muted,
    fontSize: 13,
    lineHeight: 19
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
  }
});
