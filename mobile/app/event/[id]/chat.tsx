import { Ionicons } from "@expo/vector-icons";
import { router, useLocalSearchParams } from "expo-router";
import { useEffect, useRef, useState } from "react";
import { ActivityIndicator, FlatList, KeyboardAvoidingView, Platform, Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { Reveal } from "../../../src/animations";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { BackButton, IconButton } from "../../../src/components/IconButton";
import { TeamIdentityBadge } from "../../../src/components/TeamIdentityBadge";
import { useAuth } from "../../../src/lib/auth";
import { api } from "../../../src/lib/api";
import { getPlatformTeamIdentityMap, type PlatformTeamRole } from "../../../src/lib/team-identities";
import { colors } from "../../../src/theme/colors";

type ChatMessage = {
  message_id: string;
  author_id: string;
  display_name: string;
  avatar_url: string | null;
  body: string;
  created_at: string;
};

export default function EventChatScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { user } = useAuth();
  const insets = useSafeAreaInsets();
  const listRef = useRef<typeof FlatList>(null);
  const [eventTitle, setEventTitle] = useState("Etkinlik sohbeti");
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [messageTeamRoles, setMessageTeamRoles] = useState<Map<string, PlatformTeamRole>>(new Map());
  const [body, setBody] = useState("");
  const [loading, setLoading] = useState(true);
  const [canAccess, setCanAccess] = useState(false);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadMessages = async () => {
    if (!id) {
      setCanAccess(false);
      setLoading(false);
      return;
    }
    const [eventResult, messagesResult] = await Promise.all([
      api.events.getById(id),
      api.rpc("get_event_chat_messages", { target_event_id: id })
    ]);

    if (eventResult.data?.title) setEventTitle(eventResult.data.title);
    if (messagesResult.error) {
      setCanAccess(false);
      setMessages([]);
      setMessageTeamRoles(new Map());
      setError("Bu sohbete yalnızca onaylı katılımcılar ile etkinlik moderatörleri erişebilir.");
    } else {
      setCanAccess(true);
      setError(null);
      const nextMessages = (messagesResult.data ?? []) as ChatMessage[];
      setMessages(nextMessages);
      setMessageTeamRoles(await getPlatformTeamIdentityMap(nextMessages.map((message) => message.author_id)));
    }
    setLoading(false);
  };

  useEffect(() => {
    void loadMessages();
  }, [id, user?.id]);

  useEffect(() => {
    if (!id || !user || !canAccess) return;

    const channel = api
      .channel(`event-chat-${id}`)
      .on("postgres_changes", { event: "*", schema: "public", table: "event_messages", filter: `event_id=eq.${id}` }, () => {
        void loadMessages();
      })
      .on("postgres_changes", { event: "*", schema: "public", table: "event_participants", filter: `event_id=eq.${id}` }, () => {
        void loadMessages();
      })
      .subscribe();

    return () => {
      void api.removeChannel(channel);
    };
  }, [canAccess, id, user?.id]);

  const sendMessage = async () => {
    if (!id || !user || !canAccess || !body.trim() || sending) return;
    setSending(true);
    setError(null);
    const { error: sendError } = await api.from("event_messages").insert({
      event_id: id,
      author_id: user.id,
      body: body.trim()
    });
    if (sendError) {
      await loadMessages();
    } else {
      setBody("");
      await loadMessages();
    }
    setSending(false);
  };

  const reportMessage = async (message: ChatMessage) => {
    if (!user) return;
    const { error: reportError } = await api.from("reports").insert({
      reporter_id: user.id,
      target_type: "event_message",
      target_id: message.message_id,
      reason: "Uygunsuz sohbet mesajı",
      details: message.body.slice(0, 180)
    });
    setError(reportError ? reportError.message : "Mesaj moderasyon ekibine bildirildi.");
  };

  return (
    <KeyboardAvoidingView style={styles.page} behavior={Platform.OS === "ios" ? "padding" : "height"}>
      <Reveal duration={160}>
      <View style={[styles.header, { paddingTop: insets.top + 12 }]}>
        <BackButton onPress={() => router.back()} />
        <View style={styles.headerCopy}>
          <Text style={styles.kicker}>KATILIMCILARA ÖZEL</Text>
          <Text style={styles.title} numberOfLines={1}>{eventTitle}</Text>
        </View>
        <View style={styles.liveDot} />
      </View>
      </Reveal>

      {loading ? (
        <View style={styles.center}><ActivityIndicator color={colors.accent} /><Text style={styles.muted}>Sohbet açılıyor...</Text></View>
      ) : error && messages.length === 0 ? (
        <View style={styles.center}><Ionicons name="lock-closed" size={30} color={colors.accent} /><Text style={styles.error}>{error}</Text></View>
      ) : (
        <FlatList
          ref={listRef}
          data={messages}
          keyExtractor={(item: ChatMessage) => item.message_id}
          contentContainerStyle={styles.list}
          keyboardShouldPersistTaps="handled"
          onContentSizeChange={() => listRef.current?.scrollToEnd({ animated: true })}
          ListEmptyComponent={<Text style={styles.empty}>İlk mesajı göndererek buluşmayı başlat.</Text>}
          renderItem={({ item }: { item: ChatMessage }) => {
            const mine = item.author_id === user?.id;
            return (
              <View style={[styles.message, mine && styles.messageMine]}>
                <View style={styles.messageHeader}>
                  <View style={styles.authorIdentity}>
                    <Text style={styles.author}>{mine ? "Sen" : item.display_name}</Text>
                    <TeamIdentityBadge role={messageTeamRoles.get(item.author_id)} compact />
                  </View>
                  <Text style={styles.time}>{formatTime(item.created_at)}</Text>
                </View>
                <Text style={styles.messageBody}>{item.body}</Text>
                {!mine ? <Pressable onPress={() => void reportMessage(item)}><Text style={styles.report}>Bildir</Text></Pressable> : null}
              </View>
            );
          }}
        />
      )}

      {canAccess && error && messages.length > 0 ? <Text style={styles.inlineNotice}>{error}</Text> : null}
      {canAccess ? (
        <View style={[styles.composer, { paddingBottom: Math.max(insets.bottom, 14) }]}>
          <TextInput value={body} onChangeText={setBody} placeholder="Katılımcılara mesaj yaz..." placeholderTextColor={colors.muted} style={styles.input} multiline maxLength={1000} />
          <IconButton icon="send" accessibilityLabel="Mesajı gönder" size={44} backgroundColor={colors.action} borderColor={colors.action} color={colors.actionText} disabled={!body.trim() || sending} loading={sending} onPress={() => void sendMessage()} />
        </View>
      ) : null}
    </KeyboardAvoidingView>
  );
}

function formatTime(value: string) {
  return new Date(value).toLocaleString("tr-TR", { day: "2-digit", month: "short", hour: "2-digit", minute: "2-digit" });
}

const styles = StyleSheet.create({
  page: { flex: 1, backgroundColor: colors.page },
  header: { paddingHorizontal: 18, paddingBottom: 16, flexDirection: "row", alignItems: "center", gap: 12, backgroundColor: colors.surface, borderBottomWidth: 1, borderBottomColor: colors.border },
  iconButton: { width: 42, height: 42, borderRadius: 21, alignItems: "center", justifyContent: "center", backgroundColor: colors.surfaceStrong },
  headerCopy: { flex: 1 },
  kicker: { color: colors.accent, fontSize: 10, fontWeight: "900", letterSpacing: 1.2 },
  title: { color: colors.ink, fontSize: 19, fontWeight: "900" },
  liveDot: { width: 10, height: 10, borderRadius: 5, backgroundColor: colors.aqua },
  center: { flex: 1, alignItems: "center", justifyContent: "center", padding: 28, gap: 12 },
  muted: { color: colors.muted },
  error: { color: colors.danger, textAlign: "center", lineHeight: 21 },
  list: { padding: 14, gap: 8, flexGrow: 1 },
  empty: { color: colors.muted, textAlign: "center", marginTop: 40 },
  message: { maxWidth: "84%", alignSelf: "flex-start", backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, borderRadius: 20, borderBottomLeftRadius: 6, padding: 13, gap: 6 },
  messageMine: { alignSelf: "flex-end", backgroundColor: colors.accentSoft, borderBottomLeftRadius: 20, borderBottomRightRadius: 6 },
  messageHeader: { flexDirection: "row", justifyContent: "space-between", gap: 14 },
  authorIdentity: { flexDirection: "row", alignItems: "center", gap: 5 },
  author: { color: colors.ink, fontSize: 12, fontWeight: "900" },
  time: { color: colors.muted, fontSize: 10 },
  messageBody: { color: colors.ink, fontSize: 15, lineHeight: 21 },
  report: { color: colors.danger, fontSize: 10, fontWeight: "800", alignSelf: "flex-end" },
  inlineNotice: { color: colors.accent, fontSize: 12, textAlign: "center", paddingHorizontal: 16 },
  composer: { flexDirection: "row", alignItems: "flex-end", gap: 8, padding: 10, backgroundColor: colors.surface, borderTopWidth: 1, borderTopColor: colors.border },
  input: { flex: 1, maxHeight: 100, minHeight: 44, borderRadius: 15, borderWidth: 1, borderColor: colors.border, paddingHorizontal: 13, paddingVertical: 10, color: colors.ink, backgroundColor: colors.page },
  sendButton: { width: 44, height: 44, borderRadius: 15, alignItems: "center", justifyContent: "center", backgroundColor: colors.action },
  disabled: { opacity: 0.45 }
});
