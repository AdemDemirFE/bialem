import { useState } from "react";
import { Ionicons } from "@expo/vector-icons";
import {
  ActivityIndicator,
  Image,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";
import { api } from "../../src/lib/api";
import { IconButton } from "../../src/components/IconButton";
import { colors } from "../../src/theme/colors";
import { imageSources } from "../../src/theme/images";

type ChatMessage = {
  id: string;
  role: "user" | "assistant";
  content: string;
};

const suggestions = [
  "Bu hafta sonu ne yapabilirim?",
  "Bana uygun bir topluluk bul",
  "Etkinlik fikrimi geliştirelim"
];

export default function AssistantScreen() {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState("");
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const sendMessage = async (text = input) => {
    const cleanText = text.trim();
    if (!cleanText || sending) return;

    const userMessage: ChatMessage = { id: `${Date.now()}-user`, role: "user", content: cleanText };
    const nextMessages = [...messages, userMessage];
    setMessages(nextMessages);
    setInput("");
    setSending(true);
    setError(null);

    const { data, error: functionError } = await api.functions.invoke("bialem-assistant", {
      body: {
        messages: nextMessages.map(({ role, content }) => ({ role, content }))
      }
    });

    const payload = data as { answer?: string; reply?: string; error?: string } | null;
    const answer = payload?.answer || payload?.reply;
    if (functionError || !answer) {
      setError(payload?.error || functionError?.message || "Asistan yanıt veremedi.");
      setSending(false);
      return;
    }

    setMessages((current) => [
      ...current,
      { id: `${Date.now()}-assistant`, role: "assistant", content: answer }
    ]);
    setSending(false);
  };

  return (
    <KeyboardAvoidingView style={styles.container} behavior={Platform.OS === "ios" ? "padding" : undefined} keyboardVerticalOffset={90}>
      <ScrollView contentContainerStyle={styles.page} keyboardShouldPersistTaps="handled">
        <View style={styles.hero}>
          <View style={styles.logoFrame}>
            <Image source={imageSources.logo} style={styles.logo} resizeMode="cover" />
          </View>
          <View style={styles.heroText}>
            <Text style={styles.kicker}>BİALEM ASİSTAN</Text>
            <Text style={styles.title}>Bugün ne keşfetmek istersin?</Text>
            <Text style={styles.description}>Etkinlikleri bulalım, yeni bir fikir geliştirelim veya sana uygun topluluğu seçelim.</Text>
          </View>
        </View>

        {messages.length === 0 ? (
          <View style={styles.suggestions}>
            {suggestions.map((suggestion) => (
              <Pressable key={suggestion} style={styles.suggestionCard} onPress={() => void sendMessage(suggestion)}>
                <Text style={styles.suggestionText}>{suggestion}</Text>
                <View style={styles.suggestionArrow}><Ionicons name="add" size={20} color={colors.actionText} /></View>
              </Pressable>
            ))}
          </View>
        ) : (
          <View style={styles.chat}>
            {messages.map((message) => (
              <View key={message.id} style={[styles.bubble, message.role === "user" ? styles.userBubble : styles.assistantBubble]}>
                <Text style={styles.bubbleLabel}>{message.role === "user" ? "Sen" : "Bialem"}</Text>
                <Text style={[styles.bubbleText, message.role === "user" && styles.userBubbleText]}>{message.content}</Text>
              </View>
            ))}
            {sending ? (
              <View style={[styles.bubble, styles.assistantBubble, styles.thinkingBubble]}>
                <ActivityIndicator size="small" color={colors.accent} />
                <Text style={styles.thinkingText}>Senin için düşünüyor...</Text>
              </View>
            ) : null}
          </View>
        )}

        {error ? <Text style={styles.error}>{error}</Text> : null}
      </ScrollView>

      <View style={styles.composer}>
        <TextInput
          value={input}
          onChangeText={setInput}
          placeholder="Bir etkinlik, topluluk veya fikir sor..."
          placeholderTextColor={colors.muted}
          multiline
          maxLength={2000}
          style={styles.input}
        />
        <IconButton icon="send" accessibilityLabel="Mesajı gönder" size={44} backgroundColor={colors.action as string} borderColor={colors.action as string} color={colors.actionText as string} disabled={!input.trim() || sending} loading={sending} onPress={() => void sendMessage()} />
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.page },
  page: { flexGrow: 1, padding: 16, paddingBottom: 24, gap: 14 },
  hero: { flexDirection: "row", gap: 12, padding: 14, borderRadius: 19, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  logoFrame: { width: 52, height: 52, borderRadius: 17, overflow: "hidden", borderWidth: 1, borderColor: colors.border },
  logo: { width: "100%", height: "100%" },
  heroText: { flex: 1, gap: 6 },
  kicker: { color: colors.accent, fontSize: 11, fontWeight: "900", letterSpacing: 1.3 },
  title: { color: colors.ink, fontSize: 24, lineHeight: 29, fontWeight: "900" },
  description: { color: colors.muted, fontSize: 14, lineHeight: 20 },
  suggestions: { gap: 10 },
  suggestionCard: { minHeight: 52, flexDirection: "row", alignItems: "center", justifyContent: "space-between", gap: 10, paddingHorizontal: 14, paddingVertical: 10, borderRadius: 16, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  suggestionText: { flex: 1, color: colors.ink, fontSize: 15, lineHeight: 21, fontWeight: "800" },
  suggestionArrow: { width: 32, height: 32, borderRadius: 16, alignItems: "center", justifyContent: "center", backgroundColor: colors.action },
  chat: { gap: 12 },
  bubble: { maxWidth: "88%", padding: 12, borderRadius: 17, gap: 5 },
  userBubble: { alignSelf: "flex-end", backgroundColor: colors.brandInk, borderBottomRightRadius: 7 },
  assistantBubble: { alignSelf: "flex-start", backgroundColor: colors.accentSoft, borderBottomLeftRadius: 7, borderWidth: 1, borderColor: colors.border },
  bubbleLabel: { color: colors.accent, fontSize: 11, fontWeight: "900", textTransform: "uppercase", letterSpacing: 0.8 },
  bubbleText: { color: colors.ink, fontSize: 15, lineHeight: 22 },
  userBubbleText: { color: colors.onBrand },
  thinkingBubble: { flexDirection: "row", alignItems: "center", gap: 9 },
  thinkingText: { color: colors.muted, fontSize: 14, fontWeight: "700" },
  error: { color: colors.danger, backgroundColor: colors.surfaceStrong, borderRadius: 16, padding: 12, fontSize: 13, lineHeight: 19, fontWeight: "700" },
  composer: { flexDirection: "row", alignItems: "center", gap: 10, paddingHorizontal: 16, paddingTop: 10, paddingBottom: Platform.OS === "ios" ? 24 : 14, borderTopWidth: 1, borderTopColor: colors.border, backgroundColor: colors.surface },
  input: { flex: 1, maxHeight: 100, minHeight: 44, borderRadius: 15, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.page, paddingHorizontal: 13, paddingVertical: 10, color: colors.ink, fontSize: 14 },
});
