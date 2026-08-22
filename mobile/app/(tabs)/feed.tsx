import { Ionicons } from "@expo/vector-icons";
import { Link, useFocusEffect } from "expo-router";
import { useCallback, useEffect, useMemo, useState } from "react";
import { ActivityIndicator, Image, Pressable, RefreshControl, ScrollView, StyleSheet, Text, View } from "react-native";
import { CityDiscovery } from "../../src/components/CityDiscovery";
import { useAuth } from "../../src/lib/auth";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";
import { imageSources } from "../../src/theme/images";

type EventItem = {
  id: string;
  created_by: string;
  title: string;
  description: string | null;
  starts_at: string;
  location_name: string | null;
  address_text: string | null;
  cover_image_url: string | null;
  status: string;
  communities: { id?: string; name: string; slug: string } | null;
};

type PostItem = {
  id: string;
  author_id: string;
  body: string | null;
  created_at: string;
  post_media: { id: string; media_type: string; storage_path: string; sort_order: number }[];
  communities: { name: string; slug: string } | null;
};

type StoryItem = {
  story_id: string;
  author_id: string;
  display_name: string;
  avatar_url: string | null;
  community_name: string | null;
  content_type: "text" | "image";
  body: string | null;
  media_url: string | null;
  created_at: string;
  is_viewed: boolean;
};

type DiscoveryMode = "forYou" | "today" | "week" | "city";

export default function FeedScreen() {
  const { user, profile } = useAuth();
  const [events, setEvents] = useState<EventItem[]>([]);
  const [posts, setPosts] = useState<PostItem[]>([]);
  const [stories, setStories] = useState<StoryItem[]>([]);
  const [followedIds, setFollowedIds] = useState<string[]>([]);
  const [joinedCommunityIds, setJoinedCommunityIds] = useState<string[]>([]);
  const [selectedCommunity, setSelectedCommunity] = useState<string | null>(null);
  const [discoveryMode, setDiscoveryMode] = useState<DiscoveryMode>("forYou");
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadFeed = async (mode: "initial" | "refresh" = "initial") => {
    mode === "initial" ? setLoading(true) : setRefreshing(true);
    setError(null);

    const [eventsResult, postsResult, followsResult, storiesResult, membershipsResult] = await Promise.all([
      api
        .from("events")
        .select("id, created_by, title, description, starts_at, location_name, address_text, cover_image_url, status, communities!events_community_id_fkey(id, name, slug)")
        .in("status", ["published", "pending_approval"])
        .order("starts_at", { ascending: true })
        .limit(30),
      api
        .from("posts")
        .select("id, author_id, body, created_at, post_media(id, media_type, storage_path, sort_order), communities(name, slug)")
        .order("created_at", { ascending: false })
        .limit(8),
      user ? api.from("follows").select("followed_id").eq("follower_id", user.id) : Promise.resolve({ data: [], error: null }),
      user ? api.rpc("get_story_feed") : Promise.resolve({ data: [], error: null }),
      user
        ? api.from("community_members").select("community_id").eq("user_id", user.id).eq("status", "approved")
        : Promise.resolve({ data: [], error: null })
    ]);

    const nextFollowedIds = (followsResult.data ?? []).map((item) => item.followed_id);
    const nextJoinedIds = (membershipsResult.data ?? []).map((item) => item.community_id);
    setFollowedIds(nextFollowedIds);
    setJoinedCommunityIds(nextJoinedIds);

    if (eventsResult.error) setError("Etkinlikler şu anda yüklenemedi. Yenilemek için aşağı çekin.");
    else {
      const nextEvents = ((eventsResult.data ?? []) as unknown as EventItem[]).sort((a, b) => {
        const scoreA = (nextFollowedIds.includes(a.created_by) ? 2 : 0) + (a.communities?.id && nextJoinedIds.includes(a.communities.id) ? 1 : 0);
        const scoreB = (nextFollowedIds.includes(b.created_by) ? 2 : 0) + (b.communities?.id && nextJoinedIds.includes(b.communities.id) ? 1 : 0);
        return Number(scoreB) - Number(scoreA) || new Date(a.starts_at).getTime() - new Date(b.starts_at).getTime();
      });
      setEvents(nextEvents);
    }

    if (postsResult.error) setError(postsResult.error.message);
    else setPosts((postsResult.data ?? []) as unknown as PostItem[]);
    if (!storiesResult.error) setStories(Array.isArray(storiesResult.data) ? (storiesResult.data as StoryItem[]) : []);

    mode === "initial" ? setLoading(false) : setRefreshing(false);
  };

  useEffect(() => {
    void loadFeed();
  }, [user?.id]);

  useFocusEffect(
    useCallback(() => {
      if (!user) return;
      void api.rpc("get_story_feed").then((result) => {
        if (!result.error) setStories(Array.isArray(result.data) ? (result.data as StoryItem[]) : []);
      });
    }, [user?.id])
  );

  const city = profile?.city?.trim() || null;
  const communityFilters = useMemo(() => {
    const map = new Map<string, string>();
    events.forEach((event) => {
      if (event.communities?.id) map.set(event.communities.id, event.communities.name);
    });
    return Array.from(map, ([id, name]) => ({ id, name }));
  }, [events]);

  const nowEvents = useMemo(() => {
    const now = Date.now();
    const sixHours = now + 6 * 60 * 60 * 1000;
    return events.filter((event) => {
      const time = new Date(event.starts_at).getTime();
      const location = `${event.location_name || ""} ${event.address_text || ""}`.toLocaleLowerCase("tr-TR");
      return event.status === "published" && time >= now - 60 * 60 * 1000 && time <= sixHours && (!city || location.includes(city.toLocaleLowerCase("tr-TR")));
    }).slice(0, 3);
  }, [city, events]);

  const communityEvents = useMemo(() => {
    const now = new Date();
    const dayEnd = new Date(now); dayEnd.setHours(23, 59, 59, 999);
    const weekEnd = new Date(now); weekEnd.setDate(weekEnd.getDate() + 7);
    return events.filter((event) => {
      if (selectedCommunity && event.communities?.id !== selectedCommunity) return false;
      const date = new Date(event.starts_at);
      if (discoveryMode === "today") return date >= now && date <= dayEnd;
      if (discoveryMode === "week") return date >= now && date <= weekEnd;
      if (discoveryMode === "city") {
        if (!city) return false;
        return `${event.location_name || ""} ${event.address_text || ""}`.toLocaleLowerCase("tr-TR").includes(city.toLocaleLowerCase("tr-TR"));
      }
      return true;
    });
  }, [city, discoveryMode, events, selectedCommunity]);

  return (
    <ScrollView
      contentContainerStyle={styles.page}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadFeed("refresh")} tintColor={colors.accent} />}
    >
      <View style={styles.brandHero}>
        <View style={styles.brandRow}>
          <View style={styles.logoFrame}><Image source={imageSources.logo} style={styles.logo} /></View>
          <View style={styles.brandCopy}>
            <Text style={styles.brandKicker}>BİALEM</Text>
            <Text style={styles.brandTagline}>Birlikte daha fazlası</Text>
          </View>
          <View style={styles.heroActions}>
            <Link href={"/people" as never} asChild>
              <Pressable style={styles.peopleSearchButton}>
                <Ionicons name="search" size={19} color={colors.ink} />
              </Pressable>
            </Link>
            <Link href="/(tabs)/profile" asChild>
              <Pressable style={styles.avatar}>
                {profile?.avatar_url ? <Image source={{ uri: profile.avatar_url }} style={styles.avatarImage} /> : <Text style={styles.avatarInitial}>{(profile?.display_name || "Ü").slice(0, 1).toUpperCase()}</Text>}
              </Pressable>
            </Link>
          </View>
        </View>
        <Text style={styles.heroTitle} numberOfLines={2} adjustsFontSizeToFit minimumFontScale={0.78}>Merhaba, {profile?.display_name?.split(" ")[0] || "üye"}.</Text>
        <Text style={styles.heroDescription}>Bugünün planını bul, topluluğuna katıl ve deneyimi birlikte yaşa.</Text>
      </View>

      <Stories stories={stories} currentUserId={user?.id} />

      <Link href={"/advantages" as never} asChild>
        <Pressable style={styles.advantageCard}>
          <View style={styles.advantageIcon}>
            <Ionicons name="diamond" size={28} color={colors.brandInk} />
          </View>
          <View style={styles.advantageCopy}>
            <Text style={styles.advantageKicker}>BİALEM AVANTAJ</Text>
            <Text style={styles.advantageTitle}>Şehirde ayrıcalıklı ol.</Text>
            <Text style={styles.advantageText}>Anlaşmalı mekânlarda QR ile sana özel indirimler.</Text>
          </View>
          <Ionicons name="arrow-forward-circle" size={33} color={colors.action} />
        </Pressable>
      </Link>

      <View style={styles.nowSection}>
        <View style={styles.sectionHeading}>
          <View style={styles.sectionHeadingCopy}>
            <Text style={styles.kicker}>ŞİMDİ NE YAPABİLİRİM?</Text>
            <Text style={styles.sectionTitle}>Önündeki altı saat.</Text>
            <Text style={styles.sectionSubtitle}>{city ? `${city} yakınındaki hızlı planlar` : "Şehir bilgini ekleyerek yakınındaki planları gör"}</Text>
          </View>
          <View style={styles.nowIcon}><Ionicons name="flash" size={25} color={colors.ink} /></View>
        </View>
        {loading ? <Loading label="Yakındaki planlar aranıyor..." /> : nowEvents.length === 0 ? (
          <View style={styles.emptyBox}><Text style={styles.emptyTitle}>Şu an için hızlı plan bulunamadı.</Text><Text style={styles.emptyText}>Şehir Radarı ve bu haftanın topluluk etkinliklerine göz atabilirsin.</Text></View>
        ) : <View style={styles.stack}>{nowEvents.map((event) => <CompactEventCard key={event.id} event={event} followed={followedIds.includes(event.created_by)} />)}</View>}
      </View>

      <CityDiscovery city={city}>
        <View style={styles.section}>
          <View style={styles.sectionHeading}>
            <View style={styles.sectionHeadingCopy}>
              <Text style={styles.kicker}>Bİ DÜNYA TOPLULUĞUNDAN</Text>
              <Text style={styles.sectionTitle}>Birlikte yapılan etkinlikler.</Text>
              <Text style={styles.sectionSubtitle}>{followedIds.length ? `Takip ettiğin ${followedIds.length} kişinin planları öncelikli.` : "Kişileri takip ettikçe bu alan sana göre şekillenir."}</Text>
            </View>
            <View style={styles.communityIcon}><Ionicons name="people" size={24} color={colors.onBrand} /></View>
          </View>
          <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.filterRow}>
            {([['forYou', 'Sana Özel'], ['today', 'Bugün'], ['week', 'Bu Hafta'], ['city', city ? `${city}'da` : 'Şehrimde']] as [DiscoveryMode, string][]).map(([mode, label]) => (
              <Pressable key={mode} style={[styles.filterChip, discoveryMode === mode && styles.filterChipActive]} onPress={() => setDiscoveryMode(mode)}>
                <Text style={[styles.filterText, discoveryMode === mode && styles.filterTextActive]}>{label}</Text>
              </Pressable>
            ))}
          </ScrollView>
          <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.communityFilters}>
            <Pressable style={[styles.communityChip, !selectedCommunity && styles.communityChipActive]} onPress={() => setSelectedCommunity(null)}><Text style={styles.communityChipText}>Tümü</Text></Pressable>
            {communityFilters.map((item) => (
              <Pressable key={item.id} style={[styles.communityChip, selectedCommunity === item.id && styles.communityChipActive]} onPress={() => setSelectedCommunity(item.id)}>
                <Text style={styles.communityChipText}>{item.name}{joinedCommunityIds.includes(item.id) ? " •" : ""}</Text>
              </Pressable>
            ))}
          </ScrollView>
          {error ? <Text style={styles.error}>{error}</Text> : null}
          {loading ? <Loading label="Topluluk planları yükleniyor..." /> : communityEvents.length === 0 ? <Text style={styles.emptyText}>Bu filtreye uygun etkinlik bulunamadı.</Text> : (
            <View style={styles.stack}>{communityEvents.map((event) => <EventCard key={event.id} event={event} followed={followedIds.includes(event.created_by)} />)}</View>
          )}
        </View>
      </CityDiscovery>

      <View style={styles.section}>
        <View style={styles.sectionHeadingCopy}>
          <Text style={styles.kicker}>TOPLULUK PAYLAŞIMLARI</Text>
          <Text style={styles.sectionTitle}>Deneyim akışı.</Text>
          <Text style={styles.sectionSubtitle}>Fotoğraflar, notlar ve etkinliklerden kalan güzel anlar.</Text>
        </View>
        {loading ? <Loading label="Paylaşımlar yükleniyor..." /> : posts.length === 0 ? <Text style={styles.emptyText}>Henüz paylaşım yok. İlk güzel anı sen paylaşabilirsin.</Text> : (
          <View style={styles.stack}>{posts.map((post) => <PostCard key={post.id} post={post} />)}</View>
        )}
      </View>
    </ScrollView>
  );
}

function Stories({ stories, currentUserId }: { stories: StoryItem[]; currentUserId?: string }) {
  return (
    <View style={styles.storiesSection}>
      <View style={styles.storiesHeading}><View><Text style={styles.kicker}>ANLIKLAR</Text><Text style={styles.storiesTitle}>Şu anda neler oluyor?</Text></View><Text style={styles.storyHint}>24 saat</Text></View>
      <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.storiesRow}>
        <Link href="/story/create" asChild><Pressable style={styles.storyItem}><View style={[styles.storyRing, styles.addRing]}><View style={styles.storyInner}><Ionicons name="add" size={31} color={colors.onBrand} /></View></View><Text style={styles.storyName}>Anını ekle</Text></Pressable></Link>
        {stories.map((story) => (
          <Link key={story.story_id} href={{ pathname: "/story/[id]", params: { id: story.story_id } }} asChild>
            <Pressable style={styles.storyItem}>
              <View style={[styles.storyRing, story.is_viewed && styles.viewedRing]}><View style={styles.storyInner}>
                {story.media_url || story.avatar_url ? <Image source={{ uri: story.media_url || story.avatar_url! }} style={styles.storyImage} /> : <Text style={styles.storyInitial}>{(story.display_name || "?").slice(0, 1)}</Text>}
              </View></View>
              <Text style={styles.storyName} numberOfLines={1}>{story.author_id === currentUserId ? "Sen" : (story.display_name || "").split(" ")[0]}</Text>
            </Pressable>
          </Link>
        ))}
      </ScrollView>
      {!stories.length ? <Text style={styles.storyEmpty}>Takip ettiğin kişilerden yeni anlık yok. İlk anı sen paylaş.</Text> : null}
    </View>
  );
}

function EventCard({ event, followed }: { event: EventItem; followed: boolean }) {
  return (
    <Link href={{ pathname: "/event/[id]", params: { id: event.id } }} asChild>
      <Pressable style={styles.eventCard}>
        {event.cover_image_url ? <Image source={{ uri: event.cover_image_url }} style={styles.eventImage} resizeMode="cover" /> : null}
        <View style={styles.eventBody}>
          <View style={styles.tagRow}>{followed ? <Text style={styles.followTag}>Takip ettiğin kişiden</Text> : null}<Text style={styles.communityTag}>{event.communities?.name || "Topluluk"}</Text></View>
          <Text style={styles.eventTitle}>{event.title}</Text>
          <Text style={styles.eventMeta}>{formatDate(event.starts_at)}{event.location_name ? ` · ${event.location_name}` : ""}</Text>
          <Text style={styles.eventDescription} numberOfLines={2}>{event.description || "Detayları görmek için etkinliği aç."}</Text>
          <View style={styles.openRow}><Text style={styles.openText}>Planı incele</Text><Ionicons name="arrow-forward" size={18} color={colors.accent} /></View>
        </View>
      </Pressable>
    </Link>
  );
}

function CompactEventCard({ event, followed }: { event: EventItem; followed: boolean }) {
  return (
    <Link href={{ pathname: "/event/[id]", params: { id: event.id } }} asChild>
      <Pressable style={styles.compactCard}>
        <View style={styles.timeBlock}><Text style={styles.timeText}>{new Date(event.starts_at).toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' })}</Text><Text style={styles.dayText}>BUGÜN</Text></View>
        <View style={styles.compactCopy}><Text style={styles.compactTitle} numberOfLines={1}>{event.title}</Text><Text style={styles.eventMeta} numberOfLines={1}>{event.location_name || event.communities?.name}{followed ? " · Takipten" : ""}</Text></View>
        <Ionicons name="arrow-forward" size={19} color={colors.accent} />
      </Pressable>
    </Link>
  );
}

function PostCard({ post }: { post: PostItem }) {
  return (
    <Link href={{ pathname: "/post/[id]", params: { id: post.id } }} asChild>
      <Pressable style={styles.postCard}>
        <Text style={styles.postMeta}>{post.communities?.name || "Topluluk"} · {formatDate(post.created_at)}</Text>
        <Text style={styles.postBody}>{post.body || "Yeni bir topluluk anı paylaşıldı."}</Text>
        {post.post_media?.[0] ? <Image source={{ uri: post.post_media[0].storage_path }} style={styles.postImage} resizeMode="cover" /> : null}
        <View style={styles.openRow}><Text style={styles.openText}>Yorumları aç</Text><Ionicons name="chatbubble-ellipses-outline" size={17} color={colors.accent} /></View>
      </Pressable>
    </Link>
  );
}

function Loading({ label }: { label: string }) { return <View style={styles.loading}><ActivityIndicator color={colors.accent} /><Text style={styles.emptyText}>{label}</Text></View>; }
function formatDate(value: string) { return new Date(value).toLocaleString("tr-TR", { day: "2-digit", month: "short", hour: "2-digit", minute: "2-digit" }); }

const styles = StyleSheet.create({
  page: { flexGrow: 1, gap: 14, padding: 16, paddingBottom: 32, backgroundColor: colors.page },
  brandHero: { gap: 9, marginTop: 6, padding: 14, borderRadius: 18, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  brandRow: { flexDirection: "row", alignItems: "center", gap: 10 },
  logoFrame: { width: 54, height: 54, overflow: "hidden", borderRadius: 18, backgroundColor: colors.surfaceStrong },
  logo: { width: "100%", height: "100%" },
  brandCopy: { flex: 1, gap: 2 },
  brandKicker: { color: colors.action, fontSize: 12, fontWeight: "900", letterSpacing: 2 },
  brandTagline: { color: colors.muted, fontSize: 11, fontWeight: "700" },
  heroActions: { flexDirection: "row", alignItems: "center", gap: 8 },
  kicker: { color: colors.accent, fontSize: 10, fontWeight: "900", letterSpacing: 1.45 },
  heroTitle: { color: colors.ink, fontSize: 25, lineHeight: 30, fontWeight: "900", letterSpacing: -0.4 },
  heroDescription: { color: colors.muted, fontSize: 14, lineHeight: 21 },
  avatar: { width: 42, height: 42, overflow: "hidden", alignItems: "center", justifyContent: "center", borderRadius: 21, backgroundColor: colors.accentSoft, borderWidth: 2, borderColor: colors.accent },
  avatarImage: { width: "100%", height: "100%" },
  avatarInitial: { color: colors.accent, fontSize: 20, fontWeight: "900" },
  storiesSection: { gap: 11, paddingVertical: 13, borderRadius: 20, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  advantageCard: { flexDirection: "row", alignItems: "center", gap: 11, padding: 14, borderRadius: 18, backgroundColor: colors.brandInk, borderWidth: 1, borderColor: "#23365a" },
  advantageIcon: { width: 54, height: 54, alignItems: "center", justifyContent: "center", borderRadius: 18, backgroundColor: colors.action },
  advantageCopy: { flex: 1, gap: 3 },
  advantageKicker: { color: colors.aqua, fontSize: 9, fontWeight: "900", letterSpacing: 1.3 },
  advantageTitle: { color: colors.onBrand, fontSize: 18, fontWeight: "900" },
  advantageText: { color: colors.onBrandMuted, fontSize: 11, lineHeight: 16 },
  storiesHeading: { flexDirection: "row", alignItems: "flex-end", justifyContent: "space-between", paddingHorizontal: 17 },
  storiesTitle: { color: colors.ink, fontSize: 19, fontWeight: "900" },
  storyHint: { color: colors.muted, fontSize: 11, fontWeight: "800" },
  storiesRow: { gap: 11, paddingHorizontal: 15 },
  storyItem: { width: 74, alignItems: "center", gap: 5 },
  storyRing: { width: 68, height: 68, padding: 3, borderRadius: 34, backgroundColor: colors.accent },
  addRing: { backgroundColor: colors.action },
  viewedRing: { backgroundColor: colors.border },
  storyInner: { flex: 1, overflow: "hidden", alignItems: "center", justifyContent: "center", borderRadius: 31, borderWidth: 3, borderColor: colors.surface, backgroundColor: colors.brandInk },
  storyImage: { width: "100%", height: "100%" },
  storyInitial: { color: colors.onBrand, fontSize: 22, fontWeight: "900" },
  storyName: { width: "100%", color: colors.ink, textAlign: "center", fontSize: 10, fontWeight: "900" },
  storyEmpty: { color: colors.muted, paddingHorizontal: 17, fontSize: 12 },
  nowSection: { gap: 12, padding: 15, borderRadius: 20, backgroundColor: colors.surfaceStrong, borderWidth: 1, borderColor: colors.warning },
  section: { gap: 12, padding: 15, borderRadius: 20, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  sectionHeading: { flexDirection: "row", alignItems: "flex-start", justifyContent: "space-between", gap: 12 },
  sectionHeadingCopy: { flex: 1, gap: 5 },
  sectionTitle: { color: colors.ink, fontSize: 20, lineHeight: 25, fontWeight: "900" },
  sectionSubtitle: { color: colors.muted, fontSize: 13, lineHeight: 19 },
  nowIcon: { width: 48, height: 48, alignItems: "center", justifyContent: "center", borderRadius: 18, backgroundColor: colors.warning },
  communityIcon: { width: 48, height: 48, alignItems: "center", justifyContent: "center", borderRadius: 18, backgroundColor: colors.accent },
  emptyBox: { gap: 5, padding: 15, borderRadius: 18, backgroundColor: colors.surface },
  emptyTitle: { color: colors.ink, fontSize: 15, fontWeight: "900" },
  emptyText: { color: colors.muted, fontSize: 13, lineHeight: 19 },
  loading: { flexDirection: "row", alignItems: "center", gap: 9 },
  stack: { gap: 11 },
  compactCard: { flexDirection: "row", alignItems: "center", gap: 11, padding: 10, borderRadius: 19, backgroundColor: colors.surface },
  timeBlock: { width: 58, alignItems: "center", gap: 2, paddingVertical: 9, borderRadius: 15, backgroundColor: colors.brandInk },
  timeText: { color: colors.onBrand, fontSize: 14, fontWeight: "900" },
  dayText: { color: colors.action, fontSize: 8, fontWeight: "900" },
  compactCopy: { flex: 1, gap: 4 },
  compactTitle: { color: colors.ink, fontSize: 14, fontWeight: "900" },
  filterRow: { gap: 8 },
  filterChip: { paddingHorizontal: 13, paddingVertical: 9, borderRadius: 999, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.page },
  filterChipActive: { borderColor: colors.brandInk, backgroundColor: colors.brandInk },
  filterText: { color: colors.ink, fontSize: 11, fontWeight: "900" },
  filterTextActive: { color: colors.onBrand },
  communityFilters: { gap: 7 },
  communityChip: { paddingHorizontal: 11, paddingVertical: 7, borderRadius: 13, backgroundColor: colors.surfaceStrong },
  communityChipActive: { backgroundColor: colors.accentSoft },
  communityChipText: { color: colors.ink, fontSize: 10, fontWeight: "900" },
  eventCard: { overflow: "hidden", borderRadius: 23, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  eventImage: { width: "100%", height: 170 },
  eventBody: { gap: 8, padding: 15 },
  tagRow: { flexDirection: "row", flexWrap: "wrap", gap: 6 },
  followTag: { overflow: "hidden", paddingHorizontal: 9, paddingVertical: 5, borderRadius: 999, color: colors.accent, backgroundColor: colors.accentSoft, fontSize: 9, fontWeight: "900" },
  peopleSearchButton: { width: 40, height: 40, borderRadius: 20, alignItems: "center", justifyContent: "center", backgroundColor: colors.surfaceStrong, borderWidth: 1, borderColor: colors.border },
  communityTag: { overflow: "hidden", paddingHorizontal: 9, paddingVertical: 5, borderRadius: 999, color: colors.ink, backgroundColor: colors.warning, fontSize: 9, fontWeight: "900" },
  eventTitle: { color: colors.ink, fontSize: 19, fontWeight: "900" },
  eventMeta: { color: colors.accent, fontSize: 11, fontWeight: "800" },
  eventDescription: { color: colors.muted, fontSize: 13, lineHeight: 19 },
  openRow: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", paddingTop: 3 },
  openText: { color: colors.accent, fontSize: 12, fontWeight: "900" },
  postCard: { gap: 9, padding: 15, borderRadius: 22, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  postMeta: { color: colors.accent, fontSize: 11, fontWeight: "900" },
  postBody: { color: colors.ink, fontSize: 14, lineHeight: 21 },
  postImage: { width: "100%", height: 190, borderRadius: 17, backgroundColor: colors.surfaceStrong },
  error: { color: colors.danger, fontSize: 12, fontWeight: "800" }
});
