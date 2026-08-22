import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useCallback, useEffect, useState } from "react";
import { ActivityIndicator, Image, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import {
  getConversations,
  searchMessageRecipients,
  startConversation,
  type DirectConversation,
  type MessageRecipient
} from "../../src/lib/messagingApi";
import { colors } from "../../src/theme/colors";

type Filter = "ALL" | "UNREAD";

export default function MessagesScreen() {
  const router = useRouter();
  const [filter, setFilter] = useState<Filter>("ALL");
  const [query, setQuery] = useState("");
  const [conversations, setConversations] = useState<DirectConversation[]>([]);
  const [people, setPeople] = useState<MessageRecipient[]>([]);
  const [composeMode, setComposeMode] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      if (composeMode) setPeople(await searchMessageRecipients(query));
      else setConversations(await getConversations(filter, query));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Mesajlar yüklenemedi");
    } finally {
      setLoading(false);
    }
  }, [composeMode, filter, query]);

  useEffect(() => {
    const timer = setTimeout(() => void load(), 250);
    return () => clearTimeout(timer);
  }, [load]);

  const openPerson = async (person: MessageRecipient) => {
    try {
      const conversation = await startConversation(person.profileId);
      router.push(`/messages/${conversation.id}?name=${encodeURIComponent(person.displayName)}&avatar=${encodeURIComponent(person.avatarUrl ?? "")}` as never);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Konuşma başlatılamadı");
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.page} keyboardShouldPersistTaps="handled">
      <View style={styles.header}>
        <Pressable style={styles.iconButton} onPress={() => router.back()}><Ionicons name="arrow-back" size={22} color={colors.ink} /></Pressable>
        <View style={styles.headerCopy}><Text style={styles.kicker}>BAĞLANTILARIN</Text><Text style={styles.title}>Mesajlarım</Text></View>
        <Pressable style={styles.composeButton} onPress={() => { setComposeMode((value) => !value); setQuery(""); }}>
          <Ionicons name={composeMode ? "close" : "create-outline"} size={22} color={colors.actionText} />
        </Pressable>
      </View>

      <View style={styles.searchBox}>
        <Ionicons name="search" size={20} color={colors.muted} />
        <TextInput value={query} onChangeText={setQuery} placeholder={composeMode ? "Mesaj göndereceğin kişiyi ara" : "Kişi veya mesaj ara"} placeholderTextColor={colors.muted} style={styles.searchInput} autoCapitalize="none" />
        {query ? <Pressable onPress={() => setQuery("")}><Ionicons name="close-circle" size={20} color={colors.muted} /></Pressable> : null}
      </View>

      {!composeMode ? (
        <View style={styles.filters}>
          {(["ALL", "UNREAD"] as Filter[]).map((value) => (
            <Pressable key={value} style={[styles.filter, filter === value && styles.filterActive]} onPress={() => setFilter(value)}>
              <Text style={[styles.filterText, filter === value && styles.filterTextActive]}>{value === "ALL" ? "Tümü" : "Okunmamış"}</Text>
            </Pressable>
          ))}
        </View>
      ) : <Text style={styles.sectionTitle}>Yeni konuşma</Text>}

      {error ? <Text style={styles.error}>{error}</Text> : null}
      {loading ? <View style={styles.center}><ActivityIndicator color={colors.accent} /><Text style={styles.muted}>Yükleniyor...</Text></View> : composeMode ? (
        people.length ? <View style={styles.list}>{people.map((person) => (
          <Pressable key={person.profileId} style={styles.personCard} onPress={() => void openPerson(person)}>
            <Avatar uri={person.avatarUrl} name={person.displayName} />
            <View style={styles.cardCopy}><Text style={styles.name}>{person.displayName}</Text><Text style={styles.username}>@{person.username}</Text></View>
            <Ionicons name="chatbubble-ellipses-outline" size={22} color={colors.accent} />
          </Pressable>
        ))}</View> : <Empty icon="person-add-outline" title="Kişi bulunamadı" text="Farklı bir ad veya kullanıcı adı deneyebilirsin." />
      ) : conversations.length ? <View style={styles.list}>{conversations.map((item) => (
        <Pressable key={item.id} style={[styles.conversationCard, item.unreadCount > 0 && styles.unreadCard]} onPress={() => router.push(`/messages/${item.id}?name=${encodeURIComponent(item.displayName)}&avatar=${encodeURIComponent(item.avatarUrl ?? "")}` as never)}>
          <Avatar uri={item.avatarUrl} name={item.displayName} />
          <View style={styles.cardCopy}>
            <View style={styles.cardTop}><Text style={styles.name} numberOfLines={1}>{item.displayName}</Text><Text style={styles.time}>{relativeTime(item.lastMessageAt)}</Text></View>
            <View style={styles.previewRow}><Text style={[styles.preview, item.unreadCount > 0 && styles.previewUnread]} numberOfLines={1}>{item.lastMessage ?? "Yeni konuşma"}</Text>{item.unreadCount > 0 ? <Text style={styles.badge}>{item.unreadCount}</Text> : null}</View>
          </View>
        </Pressable>
      ))}</View> : <Empty icon="chatbubbles-outline" title="Henüz mesajın yok" text="Sağ üstteki kalem simgesinden bir kişi seçip sohbete başlayabilirsin." />}
    </ScrollView>
  );
}

function Avatar({ uri, name }: { uri: string | null; name: string }) {
  return uri ? <Image source={{ uri }} style={styles.avatar} /> : <View style={styles.avatarFallback}><Text style={styles.avatarText}>{name.slice(0, 1).toLocaleUpperCase("tr-TR")}</Text></View>;
}

function Empty({ icon, title, text }: { icon: keyof typeof Ionicons.glyphMap; title: string; text: string }) {
  return <View style={styles.empty}><Ionicons name={icon} size={34} color={colors.accent} /><Text style={styles.emptyTitle}>{title}</Text><Text style={styles.muted}>{text}</Text></View>;
}

function relativeTime(value: string) {
  const seconds = Math.max(1, Math.floor((Date.now() - new Date(value).getTime()) / 1000));
  if (seconds < 60) return "şimdi";
  if (seconds < 3600) return `${Math.floor(seconds / 60)} dk`;
  if (seconds < 86400) return `${Math.floor(seconds / 3600)} sa`;
  if (seconds < 604800) return `${Math.floor(seconds / 86400)} gün`;
  return new Date(value).toLocaleDateString("tr-TR", { day: "2-digit", month: "short" });
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 22, paddingTop: 30, paddingBottom: 50, gap: 18, backgroundColor: colors.page },
  header: { flexDirection: "row", alignItems: "center", gap: 13 }, iconButton: { width: 44, height: 44, borderRadius: 22, alignItems: "center", justifyContent: "center", backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  headerCopy: { flex: 1 }, kicker: { color: colors.accent, fontSize: 10, fontWeight: "900", letterSpacing: 1.2 }, title: { color: colors.ink, fontSize: 31, fontWeight: "900" },
  composeButton: { width: 48, height: 48, borderRadius: 18, alignItems: "center", justifyContent: "center", backgroundColor: colors.action },
  searchBox: { minHeight: 54, flexDirection: "row", alignItems: "center", gap: 10, paddingHorizontal: 16, borderRadius: 20, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border }, searchInput: { flex: 1, color: colors.ink, fontSize: 15 },
  filters: { flexDirection: "row", gap: 8 }, filter: { paddingHorizontal: 18, paddingVertical: 10, borderRadius: 999, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border }, filterActive: { backgroundColor: colors.brandInk, borderColor: colors.brandInk }, filterText: { color: colors.ink, fontWeight: "800" }, filterTextActive: { color: colors.onBrand }, sectionTitle: { color: colors.ink, fontSize: 18, fontWeight: "900" },
  list: { gap: 10 }, conversationCard: { flexDirection: "row", alignItems: "center", gap: 13, padding: 14, borderRadius: 22, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border }, unreadCard: { backgroundColor: colors.accentSoft, borderColor: colors.accent }, personCard: { flexDirection: "row", alignItems: "center", gap: 13, padding: 14, borderRadius: 20, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  avatar: { width: 54, height: 54, borderRadius: 20, backgroundColor: colors.surfaceStrong }, avatarFallback: { width: 54, height: 54, borderRadius: 20, alignItems: "center", justifyContent: "center", backgroundColor: colors.action }, avatarText: { color: colors.actionText, fontSize: 20, fontWeight: "900" }, cardCopy: { flex: 1, gap: 5 }, cardTop: { flexDirection: "row", alignItems: "center", gap: 8 }, name: { flex: 1, color: colors.ink, fontSize: 16, fontWeight: "900" }, username: { color: colors.muted, fontSize: 13 }, time: { color: colors.muted, fontSize: 11, fontWeight: "700" }, previewRow: { flexDirection: "row", alignItems: "center", gap: 8 }, preview: { flex: 1, color: colors.muted, fontSize: 13 }, previewUnread: { color: colors.ink, fontWeight: "800" }, badge: { minWidth: 22, paddingHorizontal: 6, paddingVertical: 3, textAlign: "center", overflow: "hidden", borderRadius: 11, backgroundColor: colors.accent, color: "#fff", fontSize: 11, fontWeight: "900" },
  center: { alignItems: "center", gap: 10, paddingVertical: 50 }, empty: { alignItems: "center", gap: 9, padding: 30, borderRadius: 26, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border }, emptyTitle: { color: colors.ink, fontSize: 18, fontWeight: "900" }, muted: { color: colors.muted, textAlign: "center", lineHeight: 20 }, error: { color: colors.danger, fontWeight: "700" }
});
