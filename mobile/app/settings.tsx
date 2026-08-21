import { Ionicons } from "@expo/vector-icons";
import { Link, Stack } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Switch, Text, View } from "react-native";
import { useAuth } from "../src/lib/auth";
import { api } from "../src/lib/api";
import { getAppEnv, getAppVersion, getAppVersionCode } from "../src/lib/backend-config";
import { useScreenInsets } from "../src/lib/safeArea";
import { colors } from "../src/theme/colors";

type Preferences = {
  discoverable: boolean;
  show_city: boolean;
  show_follow_connections: boolean;
  allow_follows: boolean;
  require_follow_approval: boolean;
  allow_messages_from: "everyone" | "following" | "no_one";
  notify_events: boolean;
  notify_communities: boolean;
  notify_social: boolean;
  notify_advantages: boolean;
  notify_system: boolean;
};

const defaults: Preferences = {
  discoverable: true,
  show_city: true,
  show_follow_connections: true,
  allow_follows: true,
  require_follow_approval: false,
  allow_messages_from: "following",
  notify_events: true,
  notify_communities: true,
  notify_social: true,
  notify_advantages: true,
  notify_system: true
};

export default function SettingsScreen() {
  const { user } = useAuth();
  const insets = useScreenInsets();
  const [preferences, setPreferences] = useState(defaults);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!user) return;

    const load = async () => {
      setLoading(true);
      const { data, error: loadError } = await api
        .from("account_preferences")
        .select("discoverable, show_city, show_follow_connections, allow_follows, require_follow_approval, allow_messages_from, notify_events, notify_communities, notify_social, notify_advantages, notify_system")
        .eq("user_id", user.id)
        .maybeSingle<Preferences>();

      if (loadError) setError(loadError.message);
      else if (data) setPreferences(data);
      setLoading(false);
    };

    void load();
  }, [user?.id]);

  const save = async (patch: Partial<Preferences>) => {
    if (!user || saving) return;

    const next = { ...preferences, ...patch };
    setPreferences(next);
    setSaving(true);
    setError(null);

    const { error: saveError } = await api
      .from("account_preferences")
      .upsert({ user_id: user.id, ...next }, { onConflict: "user_id" });

    if (saveError) {
      setPreferences(preferences);
      setError("Ayar kaydedilemedi. Lütfen tekrar deneyin.");
    }
    setSaving(false);
  };

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: "Gizlilik ve bildirimler" }} />
      <ScrollView style={styles.screen} contentContainerStyle={[styles.page, { paddingBottom: insets.bottom + 24 }]}>
        <View style={styles.hero}>
          <View style={styles.heroIcon}>
            <Ionicons name="options" size={27} color={colors.actionText} />
          </View>
          <Text style={styles.kicker}>KONTROL SENDE</Text>
          <Text style={styles.title}>Görünürlüğünü ve bildirimlerini yönet.</Text>
          <Text style={styles.description}>
            E-posta ve telefon numaran hiçbir zaman herkese açık profil bilgisi olarak gösterilmez.
          </Text>
        </View>

        {loading ? (
          <View style={styles.loadingRow}>
            <ActivityIndicator color={colors.accent} />
            <Text style={styles.description}>Ayarların yükleniyor...</Text>
          </View>
        ) : (
          <>
            {error ? <Text style={styles.error}>{error}</Text> : null}

            <View style={styles.panel}>
              <SectionHeader icon="shield-checkmark-outline" title="Gizlilik" subtitle="Diğer üyelerin seni nasıl keşfedeceğini belirle." />
              <SettingSwitch
                title="Aramalarda görün"
                description="Üyeler adın veya kullanıcı adınla seni bulabilir."
                value={preferences.discoverable}
                onValueChange={(value) => void save({ discoverable: value })}
              />
              <SettingSwitch
                title="Şehrimi göster"
                description="Profilinde ve kişi aramasında şehir bilgin görünür."
                value={preferences.show_city}
                onValueChange={(value) => void save({ show_city: value })}
              />
              <SettingSwitch
                title="Takip listelerimi göster"
                description="Takipçi ve takip edilen listelerin profilinden açılabilir."
                value={preferences.show_follow_connections}
                onValueChange={(value) => void save({ show_follow_connections: value })}
              />
              <SettingSwitch
                title="Yeni takipçilere izin ver"
                description="Kapalıyken mevcut takiplerin korunur, yeni takip kurulamaz."
                value={preferences.allow_follows}
                onValueChange={(value) => void save({ allow_follows: value })}
              />
              <SettingSwitch
                title="Gizli hesap"
                description="Açıkken yeni takipçiler ancak sen onayladıktan sonra eklenir."
                value={preferences.require_follow_approval}
                onValueChange={(value) => void save({ require_follow_approval: value })}
                disabled={!preferences.allow_follows}
              />
              <Link href="/people/requests" asChild>
                <Pressable style={styles.requestsButton}>
                  <View style={styles.requestsIcon}><Ionicons name="person-add-outline" size={20} color={colors.accent} /></View>
                  <View style={styles.settingCopy}>
                    <Text style={styles.settingTitle}>Takip istekleri</Text>
                    <Text style={styles.settingDescription}>Bekleyen istekleri incele, kabul et veya reddet.</Text>
                  </View>
                  <Ionicons name="chevron-forward" size={20} color={colors.muted} />
                </Pressable>
              </Link>
            </View>

            <View style={styles.panel}>
              <SectionHeader icon="chatbubble-ellipses-outline" title="Mesaj izinleri" subtitle="Mesajlaşma açıldığında bu tercih uygulanacak." />
              <View style={styles.segmented}>
                {([
                  ["everyone", "Herkes"],
                  ["following", "Takip ettiklerim"],
                  ["no_one", "Hiç kimse"]
                ] as const).map(([value, label]) => {
                  const active = preferences.allow_messages_from === value;
                  return (
                    <Pressable
                      key={value}
                      style={[styles.segment, active && styles.segmentActive]}
                      onPress={() => void save({ allow_messages_from: value })}
                    >
                      <Text style={[styles.segmentText, active && styles.segmentTextActive]}>{label}</Text>
                    </Pressable>
                  );
                })}
              </View>
            </View>

            <View style={styles.panel}>
              <SectionHeader icon="notifications-outline" title="Push bildirimleri" subtitle="Uygulama içi bildirim geçmişin korunur; yalnızca cihaz uyarıları yönetilir." />
              <SettingSwitch title="Etkinlikler ve katılım" description="Onaylar, iptaller, bekleme listesi ve katılım güncellemeleri." value={preferences.notify_events} onValueChange={(value) => void save({ notify_events: value })} />
              <SettingSwitch title="Topluluklar ve gruplar" description="Üyelik talepleri, grup etkinlikleri ve moderasyon gelişmeleri." value={preferences.notify_communities} onValueChange={(value) => void save({ notify_communities: value })} />
              <SettingSwitch title="Sosyal etkileşimler" description="Yeni takipçi, yorum ve profil değerlendirmeleri." value={preferences.notify_social} onValueChange={(value) => void save({ notify_social: value })} />
              <SettingSwitch title="Bialem Avantaj" description="Yeni anlaşmalı kurumlar ve avantaj duyuruları." value={preferences.notify_advantages} onValueChange={(value) => void save({ notify_advantages: value })} />
              <SettingSwitch title="Güvenlik ve sistem" description="Hesabınla ilgili önemli güvenlik ve hizmet duyuruları." value={preferences.notify_system} onValueChange={(value) => void save({ notify_system: value })} last />
            </View>
          </>
        )}

        <Text style={styles.versionLine}>
          BiAlem v{getAppVersion()} ({getAppVersionCode()}) · {getAppEnv()}
        </Text>

        {saving ? (
          <View style={styles.savingBadge}>
            <ActivityIndicator size="small" color={colors.actionText} />
            <Text style={styles.savingText}>Kaydediliyor</Text>
          </View>
        ) : null}
      </ScrollView>
    </>
  );
}

function SectionHeader({ icon, title, subtitle }: { icon: keyof typeof Ionicons.glyphMap; title: string; subtitle: string }) {
  return (
    <View style={styles.sectionHeader}>
      <View style={styles.sectionIcon}><Ionicons name={icon} size={21} color={colors.accent} /></View>
      <View style={styles.sectionCopy}>
        <Text style={styles.panelTitle}>{title}</Text>
        <Text style={styles.sectionSubtitle}>{subtitle}</Text>
      </View>
    </View>
  );
}

function SettingSwitch({
  title,
  description,
  value,
  onValueChange,
  disabled = false,
  last = false
}: {
  title: string;
  description: string;
  value: boolean;
  onValueChange: (value: boolean) => void;
  disabled?: boolean;
  last?: boolean;
}) {
  return (
    <View style={[styles.settingRow, last && styles.settingRowLast, disabled && styles.settingRowDisabled]}>
      <View style={styles.settingCopy}>
        <Text style={styles.settingTitle}>{title}</Text>
        <Text style={styles.settingDescription}>{description}</Text>
      </View>
      <Switch
        accessibilityLabel={title}
        value={value}
        disabled={disabled}
        onValueChange={onValueChange}
        trackColor={{ false: colors.border as string, true: colors.accent as string }}
        thumbColor={colors.surface as string}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  page: { gap: 18, padding: 20, paddingBottom: 48 },
  hero: { gap: 9, padding: 22, borderRadius: 30, backgroundColor: colors.brandInk },
  heroIcon: { width: 52, height: 52, alignItems: "center", justifyContent: "center", borderRadius: 18, backgroundColor: colors.action },
  kicker: { color: colors.action, fontSize: 11, fontWeight: "900", letterSpacing: 1.4 },
  title: { maxWidth: 330, color: colors.onBrand, fontSize: 30, lineHeight: 36, fontWeight: "900" },
  description: { color: colors.onBrandMuted, fontSize: 14, lineHeight: 21 },
  loadingRow: { flexDirection: "row", alignItems: "center", gap: 10, padding: 18 },
  error: { padding: 14, borderRadius: 16, color: colors.danger, backgroundColor: colors.surface, fontWeight: "800" },
  panel: { padding: 18, borderRadius: 27, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  sectionHeader: { flexDirection: "row", alignItems: "center", gap: 12, marginBottom: 8 },
  sectionIcon: { width: 42, height: 42, alignItems: "center", justifyContent: "center", borderRadius: 15, backgroundColor: colors.accentSoft },
  sectionCopy: { flex: 1, gap: 2 },
  panelTitle: { color: colors.ink, fontSize: 20, fontWeight: "900" },
  sectionSubtitle: { color: colors.muted, fontSize: 12, lineHeight: 17 },
  settingRow: { minHeight: 76, flexDirection: "row", alignItems: "center", gap: 12, borderBottomWidth: 1, borderBottomColor: colors.border },
  settingRowLast: { borderBottomWidth: 0 },
  settingRowDisabled: { opacity: 0.48 },
  settingCopy: { flex: 1, gap: 3, paddingVertical: 12 },
  settingTitle: { color: colors.ink, fontSize: 15, fontWeight: "800" },
  settingDescription: { color: colors.muted, fontSize: 12, lineHeight: 17 },
  segmented: { flexDirection: "row", gap: 7, marginTop: 10 },
  segment: { flex: 1, alignItems: "center", justifyContent: "center", minHeight: 48, paddingHorizontal: 7, borderRadius: 16, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surfaceStrong },
  segmentActive: { borderColor: colors.accent, backgroundColor: colors.accentSoft },
  segmentText: { color: colors.muted, textAlign: "center", fontSize: 11, fontWeight: "800" },
  segmentTextActive: { color: colors.accent },
  requestsButton: { minHeight: 76, flexDirection: "row", alignItems: "center", gap: 12, borderTopWidth: 1, borderTopColor: colors.border },
  requestsIcon: { width: 42, height: 42, alignItems: "center", justifyContent: "center", borderRadius: 15, backgroundColor: colors.accentSoft },
  savingBadge: { position: "absolute", right: 22, bottom: 18, flexDirection: "row", alignItems: "center", gap: 8, paddingHorizontal: 14, paddingVertical: 10, borderRadius: 999, backgroundColor: colors.action },
  savingText: { color: colors.actionText, fontSize: 12, fontWeight: "900" },
  versionLine: { marginTop: 8, textAlign: "center", color: colors.muted, fontSize: 12, fontWeight: "700" }
});
