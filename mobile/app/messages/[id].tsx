import { Ionicons } from "@expo/vector-icons";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useCallback, useEffect, useRef, useState } from "react";
import { ActivityIndicator, FlatList, Image, KeyboardAvoidingView, Platform, Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { BackButton, IconButton } from "../../src/components/IconButton";
import { Reveal } from "../../src/animations";
import { useAuth } from "../../src/lib/auth";
import { getDirectMessages, markConversationRead, sendDirectMessage, type DirectMessage } from "../../src/lib/messagingApi";
import { colors } from "../../src/theme/colors";

export default function DirectChatScreen() {
  const { id, name = "Mesaj", avatar = "" } = useLocalSearchParams<{ id: string; name?: string; avatar?: string }>();
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const { profile } = useAuth();
  const listRef = useRef<typeof FlatList>(null);
  const [messages, setMessages] = useState<DirectMessage[]>([]);
  const [body, setBody] = useState("");
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const conversationId = Number(id);

  const load = useCallback(async (silent = false) => {
    if (!Number.isFinite(conversationId)) return;
    if (!silent) setLoading(true);
    try {
      const data = await getDirectMessages(conversationId);
      setMessages(data);
      await markConversationRead(conversationId);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Mesajlar yüklenemedi");
    } finally {
      setLoading(false);
    }
  }, [conversationId]);

  useEffect(() => { void load(); }, [load]);
  useEffect(() => {
    const timer = setInterval(() => void load(true), 4000);
    return () => clearInterval(timer);
  }, [load]);

  const send = async () => {
    const clean = body.trim();
    if (!clean || sending) return;
    setSending(true);
    try {
      const created = await sendDirectMessage(conversationId, clean);
      setMessages((current) => [...current, created]);
      setBody("");
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Mesaj gönderilemedi");
    } finally {
      setSending(false);
    }
  };

  return (
    <KeyboardAvoidingView style={styles.page} behavior={Platform.OS === "ios" ? "padding" : "height"}>
      <Reveal duration={160}>
      <View style={[styles.header, { paddingTop: insets.top + 10 }]}>
        <BackButton onPress={() => router.back()} />
        {avatar ? <Image source={{ uri: avatar }} style={styles.avatar} /> : <View style={styles.avatarFallback}><Text style={styles.avatarText}>{name.slice(0, 1).toLocaleUpperCase("tr-TR")}</Text></View>}
        <View style={styles.headerCopy}><Text style={styles.name} numberOfLines={1}>{name}</Text><Text style={styles.status}>Bialem mesajları</Text></View>
      </View>
      </Reveal>

      {loading ? <View style={styles.center}><ActivityIndicator color={colors.accent} /><Text style={styles.muted}>Sohbet açılıyor...</Text></View> : (
        <FlatList
          ref={listRef}
          data={messages}
          keyExtractor={(item: DirectMessage) => String(item.id)}
          contentContainerStyle={styles.list}
          keyboardShouldPersistTaps="handled"
          onContentSizeChange={() => listRef.current?.scrollToEnd({ animated: true })}
          ListEmptyComponent={<View style={styles.empty}><Ionicons name="sparkles-outline" size={30} color={colors.accent} /><Text style={styles.emptyTitle}>Sohbeti başlat</Text><Text style={styles.muted}>Samimi bir merhaba her şeyi başlatabilir.</Text></View>}
          renderItem={({ item }: { item: DirectMessage }) => {
            const mine = String(item.senderProfileId) === String(profile?.id);
            return <View style={[styles.bubble, mine && styles.mine]}><Text style={[styles.message, mine && styles.mineText]}>{item.body}</Text><Text style={[styles.time, mine && styles.mineTime]}>{formatTime(item.createdAt)}{mine ? item.readAt ? "  ✓✓" : "  ✓" : ""}</Text></View>;
          }}
        />
      )}

      {error ? <Text style={styles.error}>{error}</Text> : null}
      <View style={[styles.composer, { paddingBottom: Math.max(insets.bottom, 12) }]}>
        <TextInput value={body} onChangeText={setBody} placeholder="Mesajını yaz..." placeholderTextColor={colors.muted} multiline maxLength={2000} style={styles.input} />
        <IconButton icon="send" accessibilityLabel="Mesajı gönder" size={44} backgroundColor={colors.action} borderColor={colors.action} color={colors.actionText} disabled={!body.trim() || sending} loading={sending} onPress={() => void send()} />
      </View>
    </KeyboardAvoidingView>
  );
}

function formatTime(value: string) {
  return new Date(value).toLocaleTimeString("tr-TR", { hour: "2-digit", minute: "2-digit" });
}

const styles = StyleSheet.create({
  page: { flex: 1, backgroundColor: colors.page }, header: { paddingHorizontal: 16, paddingBottom: 13, flexDirection: "row", alignItems: "center", gap: 11, backgroundColor: colors.surface, borderBottomWidth: 1, borderBottomColor: colors.border }, back: { width: 42, height: 42, borderRadius: 21, alignItems: "center", justifyContent: "center", backgroundColor: colors.surfaceStrong },
  avatar: { width: 44, height: 44, borderRadius: 16 }, avatarFallback: { width: 44, height: 44, borderRadius: 16, alignItems: "center", justifyContent: "center", backgroundColor: colors.action }, avatarText: { color: colors.actionText, fontSize: 18, fontWeight: "900" }, headerCopy: { flex: 1 }, name: { color: colors.ink, fontSize: 17, fontWeight: "900" }, status: { color: colors.accent, fontSize: 11, fontWeight: "700" },
  center: { flex: 1, alignItems: "center", justifyContent: "center", gap: 10 }, list: { flexGrow: 1, padding: 18, gap: 9 }, bubble: { maxWidth: "82%", alignSelf: "flex-start", paddingHorizontal: 15, paddingVertical: 11, gap: 5, borderRadius: 20, borderBottomLeftRadius: 6, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border }, mine: { alignSelf: "flex-end", borderBottomLeftRadius: 20, borderBottomRightRadius: 6, backgroundColor: colors.brandInk, borderColor: colors.brandInk }, message: { color: colors.ink, fontSize: 15, lineHeight: 21 }, mineText: { color: colors.onBrand }, time: { color: colors.muted, fontSize: 9, alignSelf: "flex-end" }, mineTime: { color: colors.onBrandMuted },
  empty: { alignItems: "center", gap: 8, marginTop: 56 }, emptyTitle: { color: colors.ink, fontSize: 17, fontWeight: "900" }, muted: { color: colors.muted, textAlign: "center" }, error: { color: colors.danger, textAlign: "center", paddingHorizontal: 16, paddingVertical: 7, fontWeight: "700" }, composer: { flexDirection: "row", alignItems: "flex-end", gap: 8, padding: 10, backgroundColor: colors.surface, borderTopWidth: 1, borderTopColor: colors.border }, input: { flex: 1, maxHeight: 110, minHeight: 44, paddingHorizontal: 13, paddingVertical: 10, borderRadius: 15, color: colors.ink, backgroundColor: colors.page, borderWidth: 1, borderColor: colors.border }, send: { width: 44, height: 44, borderRadius: 15, alignItems: "center", justifyContent: "center", backgroundColor: colors.action }, disabled: { opacity: 0.4 }
});
