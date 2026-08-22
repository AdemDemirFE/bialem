import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Switch, Text, View } from "react-native";
import {
  getNotificationPreferences,
  updateNotificationPreferences,
  type NotificationPreference
} from "../src/lib/notificationApi";
import { colors } from "../src/theme/colors";

const KNOWN_TYPES = [
  { type: "NEW_FOLLOWER", label: "Yeni takipçiler", icon: "person-add-outline" },
  { type: "COMMUNITY_MEMBERSHIP_REQUEST", label: "Topluluk bildirimleri", icon: "people-outline" },
  { type: "EVENT_PUBLISHED", label: "Etkinlik bildirimleri", icon: "calendar-outline" },
  { type: "USER_REVIEW", label: "Değerlendirmeler", icon: "star-outline" },
  { type: "SYSTEM_ANNOUNCEMENT", label: "Sistem bildirimleri", icon: "information-circle-outline" }
];

export default function NotificationSettingsScreen() {
  const router = useRouter();
  const [preferences, setPreferences] = useState<NotificationPreference[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void loadPreferences();
  }, []);

  const loadPreferences = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getNotificationPreferences();
      const merged = mergeWithDefaults(data);
      setPreferences(merged);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Ayarlar yüklenemedi");
    } finally {
      setLoading(false);
    }
  };

  const mergeWithDefaults = (existing: NotificationPreference[]): NotificationPreference[] => {
    const map = new Map(existing.map((p) => [p.notificationType, p]));
    return KNOWN_TYPES.map((known) => {
      if (map.has(known.type)) {
        return map.get(known.type)!;
      }
      return {
        notificationType: known.type,
        inAppEnabled: true,
        pushEnabled: true,
        emailEnabled: false,
        mandatory: false
      };
    });
  };

  const updatePreference = (index: number, field: "pushEnabled" | "inAppEnabled", value: boolean) => {
    setPreferences((current) => {
      const next = [...current];
      next[index] = { ...next[index], [field]: value };
      return next;
    });
  };

  const savePreferences = async () => {
    setSaving(true);
    setError(null);
    try {
      const updated = await updateNotificationPreferences(preferences);
      setPreferences(mergeWithDefaults(updated));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Ayarlar kaydedilemedi");
    } finally {
      setSaving(false);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.page}>
      <View style={styles.hero}>
        <Pressable style={styles.backButton} onPress={() => router.back()}>
          <Ionicons name="arrow-back" size={22} color={colors.ink} />
        </Pressable>
        <Text style={styles.kicker}>Bildirim Ayarları</Text>
        <Text style={styles.title}>Bildirim tercihlerini yönet</Text>
        <Text style={styles.description}>Hangi olaylar için uygulama içi ve push bildirimi alacağını buradan belirleyebilirsin.</Text>
      </View>

      {error ? <Text style={styles.error}>{error}</Text> : null}

      {loading ? (
        <View style={styles.stateCard}>
          <ActivityIndicator color={colors.accent} />
          <Text style={styles.stateText}>Ayarlar yükleniyor...</Text>
        </View>
      ) : (
        <View style={styles.list}>
          {preferences.map((pref, index) => {
            const known = KNOWN_TYPES.find((k) => k.type === pref.notificationType);
            return (
              <View key={pref.notificationType} style={styles.card}>
                <View style={styles.row}>
                  <View style={styles.icon}>
                    <Ionicons
                      name={(known?.icon as keyof typeof Ionicons.glyphMap) ?? "notifications-outline"}
                      size={20}
                      color={colors.accent}
                    />
                  </View>
                  <Text style={styles.label}>{known?.label ?? pref.notificationType}</Text>
                </View>
                <View style={styles.toggles}>
                  <View style={styles.toggleRow}>
                    <Text style={styles.toggleLabel}>Uygulama içi</Text>
                    <Switch
                      value={pref.inAppEnabled}
                      onValueChange={(value) => updatePreference(index, "inAppEnabled", value)}
                      disabled={pref.mandatory}
                      thumbColor={pref.inAppEnabled ? colors.action : "#f4f3f4"}
                      trackColor={{ false: colors.border, true: colors.action }}
                    />
                  </View>
                  <View style={styles.toggleRow}>
                    <Text style={styles.toggleLabel}>Push bildirimi</Text>
                    <Switch
                      value={pref.pushEnabled}
                      onValueChange={(value) => updatePreference(index, "pushEnabled", value)}
                      disabled={pref.mandatory}
                      thumbColor={pref.pushEnabled ? colors.action : "#f4f3f4"}
                      trackColor={{ false: colors.border, true: colors.action }}
                    />
                  </View>
                </View>
              </View>
            );
          })}
        </View>
      )}

      {!loading ? (
        <Pressable style={[styles.saveButton, saving && styles.saveButtonDisabled]} onPress={() => void savePreferences()} disabled={saving}>
          {saving ? <ActivityIndicator size="small" color={colors.actionText} /> : <Text style={styles.saveButtonText}>Kaydet</Text>}
        </Pressable>
      ) : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 16, gap: 14, backgroundColor: colors.page },
  hero: { marginTop: 8, gap: 10 },
  backButton: { alignSelf: "flex-start", padding: 8, marginLeft: -8 },
  kicker: { color: colors.accent, fontSize: 13, fontWeight: "800", letterSpacing: 1.1, textTransform: "uppercase" },
  title: { color: colors.ink, fontSize: 25, lineHeight: 31, fontWeight: "800" },
  description: { color: colors.muted, fontSize: 15, lineHeight: 22 },
  error: { color: colors.danger, fontSize: 14, fontWeight: "600" },
  stateCard: { minHeight: 160, padding: 24, gap: 10, alignItems: "center", justifyContent: "center", borderRadius: 28, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  stateText: { color: colors.muted, fontSize: 14, lineHeight: 20, textAlign: "center" },
  list: { gap: 12 },
  card: { padding: 13, borderRadius: 17, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, gap: 11 },
  row: { flexDirection: "row", alignItems: "center", gap: 12 },
  icon: { width: 42, height: 42, borderRadius: 15, alignItems: "center", justifyContent: "center", backgroundColor: colors.accentSoft },
  label: { flex: 1, color: colors.ink, fontSize: 16, fontWeight: "800" },
  toggles: { gap: 10 },
  toggleRow: { flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  toggleLabel: { color: colors.muted, fontSize: 14, fontWeight: "700" },
  saveButton: { minHeight: 44, marginTop: 6, backgroundColor: colors.action, borderRadius: 14, paddingVertical: 10, alignItems: "center", justifyContent: "center" },
  saveButtonDisabled: { opacity: 0.7 },
  saveButtonText: { color: colors.actionText, fontSize: 16, fontWeight: "900" }
});
