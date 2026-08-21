import { Stack, useLocalSearchParams } from "expo-router";
import { ScrollView, StyleSheet, Text, View } from "react-native";
import { isLegalDocumentKey, legalDocuments } from "../../src/content/legal";
import { useScreenInsets } from "../../src/lib/safeArea";
import { colors } from "../../src/theme/colors";

export default function LegalDocumentScreen() {
  const insets = useScreenInsets();
  const params = useLocalSearchParams<{ document?: string }>();
  const key = typeof params.document === "string" ? params.document : "";
  const document = isLegalDocumentKey(key) ? legalDocuments[key] : null;

  if (!document) {
    return (
      <View style={styles.centered}>
      <Stack.Screen options={{ headerShown: true, title: "Belge bulunamadı" }} />
      <Text style={styles.title}>Bu hukuki metin bulunamadı.</Text>
      </View>
    );
  }

  return (
      <ScrollView contentContainerStyle={[styles.page, { paddingBottom: insets.bottom + 24 }]}>
      <Stack.Screen options={{ headerShown: true, title: document.title }} />
      <Text style={styles.kicker}>BİALEM GÜVEN MERKEZİ</Text>
      <Text style={styles.title}>{document.title}</Text>
      <Text style={styles.updated}>Son guncelleme: {document.updatedAt}</Text>
      {document.disclaimer ? <Text style={styles.notice}>{document.disclaimer}</Text> : null}

      {document.sections.map((section) => (
        <View key={section.heading} style={styles.section}>
          <Text style={styles.sectionTitle}>{section.heading}</Text>
          {section.paragraphs?.map((paragraph) => <Text key={paragraph} style={styles.body}>{paragraph}</Text>)}
          {section.bullets?.map((bullet) => (
            <View key={bullet} style={styles.bulletRow}>
              <View style={styles.bullet} />
              <Text style={styles.bulletText}>{bullet}</Text>
            </View>
          ))}
        </View>
      ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 24, paddingBottom: 48, gap: 14, backgroundColor: colors.page },
  centered: { flex: 1, padding: 24, alignItems: "center", justifyContent: "center", backgroundColor: colors.page },
  kicker: { color: colors.accent, fontSize: 12, fontWeight: "800", letterSpacing: 1.2 },
  title: { color: colors.ink, fontSize: 32, lineHeight: 39, fontWeight: "900" },
  updated: { color: colors.muted, fontSize: 13 },
  notice: { color: colors.ink, backgroundColor: colors.accentSoft, borderRadius: 18, padding: 14, fontSize: 13, lineHeight: 20 },
  section: { marginTop: 8, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, borderRadius: 24, padding: 20, gap: 10 },
  sectionTitle: { color: colors.ink, fontSize: 20, fontWeight: "800" },
  body: { color: colors.muted, fontSize: 15, lineHeight: 23 },
  bulletRow: { flexDirection: "row", alignItems: "flex-start", gap: 10 },
  bullet: { width: 7, height: 7, borderRadius: 4, marginTop: 7, backgroundColor: colors.action },
  bulletText: { flex: 1, color: colors.muted, fontSize: 15, lineHeight: 22 }
});
