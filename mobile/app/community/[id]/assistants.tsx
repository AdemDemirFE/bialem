import { Ionicons } from "@expo/vector-icons";
import { Stack, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Image, Pressable, ScrollView, StyleSheet, Switch, Text, TextInput, View } from "react-native";
import { api } from "../../../src/lib/api";
import { colors } from "../../../src/theme/colors";

type Assistant = {
  assignment_id: string;
  user_id: string;
  display_name: string;
  email: string;
  avatar_url: string | null;
  can_manage_groups: boolean;
  can_review_events: boolean;
  can_manage_participants: boolean;
  created_at: string;
};

type Permissions = Pick<Assistant, "can_manage_groups" | "can_review_events" | "can_manage_participants">;

const initialPermissions: Permissions = {
  can_manage_groups: false,
  can_review_events: true,
  can_manage_participants: false
};

export default function CommunityAssistantsScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const [communityName, setCommunityName] = useState("Topluluk");
  const [assistants, setAssistants] = useState<Assistant[]>([]);
  const [email, setEmail] = useState("");
  const [permissions, setPermissions] = useState<Permissions>(initialPermissions);
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const loadAssistants = async () => {
    if (!id) return;
    setError(null);
    const [communityResult, assistantsResult] = await Promise.all([
      api.from("communities").select("name").eq("id", id).maybeSingle(),
      api.rpc("get_community_moderator_assistants", { target_community_id: id })
    ]);

    if (communityResult.data?.name) setCommunityName(communityResult.data.name);
    if (assistantsResult.error) setError(formatError(assistantsResult.error.message));
    else setAssistants((assistantsResult.data ?? []) as Assistant[]);
    setLoading(false);
  };

  useEffect(() => {
    void loadAssistants();
  }, [id]);

  const saveAssistant = async (targetEmail: string, targetPermissions: Permissions, assignmentId = "new") => {
    if (!id || !targetEmail.trim()) {
      setError("Yardımcının uygulamada kayıtlı e-posta adresini yazın.");
      return;
    }

    if (!Object.values(targetPermissions).some(Boolean)) {
      setError("En az bir yetki seçmelisiniz.");
      return;
    }

    setBusyId(assignmentId);
    setError(null);
    setNotice(null);
    const { error: saveError } = await api.rpc("set_community_moderator_assistant", {
      target_community_id: id,
      target_user_email: targetEmail.trim(),
      target_can_manage_groups: targetPermissions.can_manage_groups,
      target_can_review_events: targetPermissions.can_review_events,
      target_can_manage_participants: targetPermissions.can_manage_participants
    });

    if (saveError) setError(formatError(saveError.message));
    else {
      setEmail("");
      setPermissions(initialPermissions);
      setNotice(assignmentId === "new" ? "Moderatör yardımcısı atandı." : "Yardımcının yetkileri güncellendi.");
      await loadAssistants();
    }
    setBusyId(null);
  };

  const removeAssistant = async (assignmentId: string) => {
    setBusyId(assignmentId);
    setError(null);
    setNotice(null);
    const { error: removeError } = await api.rpc("remove_community_moderator_assistant", {
      target_assignment_id: assignmentId
    });
    if (removeError) setError(formatError(removeError.message));
    else {
      setNotice("Moderatör yardımcısı kaldırıldı.");
      await loadAssistants();
    }
    setBusyId(null);
  };

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: "Moderatör Yardımcıları" }} />
      <ScrollView contentContainerStyle={styles.page}>
        <View style={styles.hero}>
          <Text style={styles.kicker}>YETKİ PAYLAŞIMI</Text>
          <Text style={styles.title}>{communityName}</Text>
          <Text style={styles.heroText}>En fazla iki yardımcı ata. Her yardımcı yalnızca açtığın yetkileri kullanabilir; ana moderatör yetkisi devredilmez.</Text>
          <View style={styles.counter}><Text style={styles.counterText}>{assistants.length}/2 yardımcı</Text></View>
        </View>

        {error ? <Text style={styles.error}>{error}</Text> : null}
        {notice ? <Text style={styles.notice}>{notice}</Text> : null}

        {loading ? (
          <ActivityIndicator color={colors.accent} size="large" />
        ) : (
          <>
            <View style={styles.panel}>
              <Text style={styles.panelTitle}>Yeni yardımcı ata</Text>
              <Text style={styles.body}>Kişinin önce Bialem hesabını oluşturmuş olması gerekir.</Text>
              <TextInput
                value={email}
                onChangeText={setEmail}
                autoCapitalize="none"
                keyboardType="email-address"
                placeholder="üye@eposta.com"
                placeholderTextColor={colors.muted}
                style={styles.input}
              />
              <PermissionSwitches value={permissions} onChange={setPermissions} />
              <Pressable
                disabled={busyId !== null || assistants.length >= 2}
                style={[styles.primaryButton, (busyId !== null || assistants.length >= 2) && styles.disabled]}
                onPress={() => void saveAssistant(email, permissions)}
              >
                <Text style={styles.primaryButtonText}>{assistants.length >= 2 ? "İki yardımcı sınırına ulaşıldı" : busyId === "new" ? "Atanıyor..." : "Yardımcı ata"}</Text>
              </Pressable>
            </View>

            <View style={styles.list}>
              {assistants.map((assistant) => (
                <AssistantCard
                  key={assistant.assignment_id}
                  assistant={assistant}
                  busy={busyId === assistant.assignment_id}
                  onSave={(nextPermissions) => saveAssistant(assistant.email, nextPermissions, assistant.assignment_id)}
                  onRemove={() => removeAssistant(assistant.assignment_id)}
                />
              ))}
            </View>
          </>
        )}
      </ScrollView>
    </>
  );
}

function AssistantCard({
  assistant,
  busy,
  onSave,
  onRemove
}: {
  assistant: Assistant;
  busy: boolean;
  onSave: (permissions: Permissions) => Promise<void>;
  onRemove: () => Promise<void>;
}) {
  const [permissions, setPermissions] = useState<Permissions>({
    can_manage_groups: assistant.can_manage_groups,
    can_review_events: assistant.can_review_events,
    can_manage_participants: assistant.can_manage_participants
  });

  return (
    <View style={styles.panel}>
      <View style={styles.personRow}>
        <View style={styles.avatar}>
          {assistant.avatar_url ? <Image source={{ uri: assistant.avatar_url }} style={styles.avatarImage} /> : <Text style={styles.avatarText}>{assistant.display_name.slice(0, 1).toUpperCase()}</Text>}
        </View>
        <View style={styles.personCopy}>
          <Text style={styles.personName}>{assistant.display_name}</Text>
          <Text style={styles.personEmail}>{assistant.email}</Text>
        </View>
        <Ionicons name="shield-checkmark" size={22} color={colors.accent} />
      </View>
      <PermissionSwitches value={permissions} onChange={setPermissions} />
      <View style={styles.actionRow}>
        <Pressable disabled={busy} style={styles.saveButton} onPress={() => void onSave(permissions)}>
          <Text style={styles.saveButtonText}>{busy ? "Kaydediliyor..." : "Yetkileri kaydet"}</Text>
        </Pressable>
        <Pressable disabled={busy} style={styles.removeButton} onPress={() => void onRemove()}>
          <Ionicons name="trash-outline" size={18} color={colors.danger} />
        </Pressable>
      </View>
    </View>
  );
}

function PermissionSwitches({ value, onChange }: { value: Permissions; onChange: (value: Permissions) => void }) {
  return (
    <View style={styles.permissionList}>
      <PermissionRow label="Alt grup oluşturabilsin" value={value.can_manage_groups} onChange={(enabled) => onChange({ ...value, can_manage_groups: enabled })} />
      <PermissionRow label="Etkinlik taleplerini inceleyebilsin" value={value.can_review_events} onChange={(enabled) => onChange({ ...value, can_review_events: enabled })} />
      <PermissionRow label="Katılımcıları ve yoklamayi yönetebilsin" value={value.can_manage_participants} onChange={(enabled) => onChange({ ...value, can_manage_participants: enabled })} />
    </View>
  );
}

function PermissionRow({ label, value, onChange }: { label: string; value: boolean; onChange: (value: boolean) => void }) {
  return <View style={styles.permissionRow}><Text style={styles.permissionLabel}>{label}</Text><Switch value={value} onValueChange={onChange} trackColor={{ false: colors.border, true: colors.accentSoft }} thumbColor={value ? colors.accent : colors.muted} /></View>;
}

function formatError(message: string) {
  if (message.includes("at most two assistants")) return "Bir topluluğa en fazla iki moderatör yardımcısı atanabilir.";
  if (message.includes("Registered user not found")) return "Bu e-posta adresiyle kayıtlı bir Bialem üyesi bulunamadı.";
  if (message.includes("lead moderator")) return "Bu işlemi yalnızca ana topluluk moderatörü yapabilir.";
  return message;
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, gap: 16, padding: 20, paddingBottom: 44, backgroundColor: colors.page },
  hero: { gap: 9, padding: 23, borderRadius: 30, backgroundColor: colors.brandInk },
  kicker: { color: colors.action, fontSize: 10, fontWeight: "900", letterSpacing: 1.5 },
  title: { color: colors.onBrand, fontSize: 30, fontWeight: "900" },
  heroText: { color: "#cbd5ef", fontSize: 14, lineHeight: 21 },
  counter: { alignSelf: "flex-start", marginTop: 4, paddingHorizontal: 11, paddingVertical: 7, borderRadius: 999, backgroundColor: colors.accent },
  counterText: { color: colors.onBrand, fontSize: 11, fontWeight: "900" },
  panel: { gap: 13, padding: 19, borderWidth: 1, borderColor: colors.border, borderRadius: 25, backgroundColor: colors.surface },
  panelTitle: { color: colors.ink, fontSize: 20, fontWeight: "900" },
  body: { color: colors.muted, fontSize: 13, lineHeight: 19 },
  input: { minHeight: 52, paddingHorizontal: 14, borderWidth: 1, borderColor: colors.border, borderRadius: 17, color: colors.ink, backgroundColor: colors.surfaceStrong, fontSize: 15 },
  permissionList: { gap: 4 },
  permissionRow: { minHeight: 52, flexDirection: "row", alignItems: "center", justifyContent: "space-between", gap: 12, borderBottomWidth: 1, borderBottomColor: colors.border },
  permissionLabel: { flex: 1, color: colors.ink, fontSize: 13, lineHeight: 18, fontWeight: "700" },
  primaryButton: { alignItems: "center", padding: 15, borderRadius: 999, backgroundColor: colors.accent },
  primaryButtonText: { color: colors.onBrand, fontSize: 13, fontWeight: "900" },
  disabled: { opacity: 0.5 },
  list: { gap: 12 },
  personRow: { flexDirection: "row", alignItems: "center", gap: 11 },
  avatar: { width: 48, height: 48, overflow: "hidden", alignItems: "center", justifyContent: "center", borderRadius: 17, backgroundColor: colors.accentSoft },
  avatarImage: { width: "100%", height: "100%" },
  avatarText: { color: colors.accent, fontSize: 19, fontWeight: "900" },
  personCopy: { flex: 1, gap: 3 },
  personName: { color: colors.ink, fontSize: 16, fontWeight: "900" },
  personEmail: { color: colors.muted, fontSize: 12 },
  actionRow: { flexDirection: "row", gap: 9 },
  saveButton: { flex: 1, alignItems: "center", padding: 13, borderRadius: 999, backgroundColor: colors.accentSoft },
  saveButtonText: { color: colors.accent, fontSize: 12, fontWeight: "900" },
  removeButton: { width: 46, alignItems: "center", justifyContent: "center", borderRadius: 16, backgroundColor: colors.surfaceStrong },
  error: { color: colors.danger, padding: 12, borderRadius: 15, backgroundColor: colors.surface, fontSize: 13, fontWeight: "700" },
  notice: { color: colors.success, padding: 12, borderRadius: 15, backgroundColor: colors.surface, fontSize: 13, fontWeight: "700" }
});
