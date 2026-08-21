import { useState } from "react";
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
import { useScreenInsets } from "../../src/lib/safeArea";
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
  const insets = useScreenInsets();
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

    const answer = data?.answer || data?.reply;
    if (functionError || !answer) {
      setError(data?.error || functionError?.message || "Asistan yanıt veremedi.");
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
      <ScrollView contentContainerStyle={[styles.page, { paddingBottom: insets.bottom + 24 }]} keyboardShouldPersistTaps="handled">
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
                <Text style={styles.suggestionArrow}>+</Text>
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
        <Pressable style={[styles.sendButton, (!input.trim() || sending) && styles.sendButtonDisabled]} onPress={() => void sendMessage()}>
          <Text style={styles.sendButtonText}>Gönder</Text>
        </Pressable>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.page },
  page: { flexGrow: 1, padding: 20, paddingBottom: 28, gap: 18 },
  hero: { flexDirection: "row", gap: 14, padding: 18, borderRadius: 28, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  logoFrame: { width: 64, height: 64, borderRadius: 22, overflow: "hidden", borderWidth: 1, borderColor: colors.border },
  logo: { width: "100%", height: "100%" },
  heroText: { flex: 1, gap: 6 },
  kicker: { color: colors.accent, fontSize: 11, fontWeight: "900", letterSpacing: 1.3 },
  title: { color: colors.ink, fontSize: 24, lineHeight: 29, fontWeight: "900" },
  description: { color: colors.muted, fontSize: 14, lineHeight: 20 },
  suggestions: { gap: 10 },
  suggestionCard: { minHeight: 64, flexDirection: "row", alignItems: "center", justifyContent: "space-between", gap: 12, paddingHorizontal: 17, paddingVertical: 14, borderRadius: 21, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  suggestionText: { flex: 1, color: colors.ink, fontSize: 15, lineHeight: 21, fontWeight: "800" },
  suggestionArrow: { width: 30, height: 30, borderRadius: 15, textAlign: "center", textAlignVertical: "center", color: colors.actionText, backgroundColor: colors.action, fontSize: 20, fontWeight: "900" },
  chat: { gap: 12 },
  bubble: { maxWidth: "88%", padding: 15, borderRadius: 22, gap: 5 },
  userBubble: { alignSelf: "flex-end", backgroundColor: colors.brandInk, borderBottomRightRadius: 7 },
  assistantBubble: { alignSelf: "flex-start", backgroundColor: colors.accentSoft, borderBottomLeftRadius: 7, borderWidth: 1, borderColor: colors.border },
  bubbleLabel: { color: colors.accent, fontSize: 11, fontWeight: "900", textTransform: "uppercase", letterSpacing: 0.8 },
  bubbleText: { color: colors.ink, fontSize: 15, lineHeight: 22 },
  userBubbleText: { color: colors.onBrand },
  thinkingBubble: { flexDirection: "row", alignItems: "center", gap: 9 },
  thinkingText: { color: colors.muted, fontSize: 14, fontWeight: "700" },
  error: { color: colors.danger, backgroundColor: colors.surfaceStrong, borderRadius: 16, padding: 12, fontSize: 13, lineHeight: 19, fontWeight: "700" },
  composer: { flexDirection: "row", alignItems: "flex-end", gap: 10, paddingHorizontal: 16, paddingTop: 10, paddingBottom: Platform.OS === "ios" ? 24 : 14, borderTopWidth: 1, borderTopColor: colors.border, backgroundColor: colors.surface },
  input: { flex: 1, maxHeight: 110, minHeight: 48, borderRadius: 20, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.page, paddingHorizontal: 14, paddingVertical: 12, color: colors.ink, fontSize: 15 },
  sendButton: { minHeight: 48, justifyContent: "center", backgroundColor: colors.action, borderRadius: 18, paddingHorizontal: 16 },
  sendButtonDisabled: { opacity: 0.45 },
  sendButtonText: { color: colors.actionText, fontSize: 14, fontWeight: "900" }
});
