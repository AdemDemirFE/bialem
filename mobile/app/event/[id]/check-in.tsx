import { Ionicons } from "@expo/vector-icons";
import { CameraView, useCameraPermissions } from "expo-camera";
import { router, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, RefreshControl, ScrollView, StyleSheet, Text, View } from "react-native";
import { api } from "../../../src/lib/api";
import { colors } from "../../../src/theme/colors";

type RosterItem = {
  participant_id: string;
  user_id: string;
  display_name: string;
  avatar_url: string | null;
  status: string;
  created_at: string;
};

export default function EventCheckInScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const [permission, requestPermission] = useCameraPermissions();
  const [roster, setRoster] = useState<RosterItem[]>([]);
  const [eventTitle, setEventTitle] = useState("Katılımcı yönetimi");
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [scannerOpen, setScannerOpen] = useState(false);
  const [scanLocked, setScanLocked] = useState(false);
  const [busyId, setBusyId] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const loadRoster = async (refresh = false) => {
    if (!id) return;
    refresh ? setRefreshing(true) : setLoading(true);
    const [eventResult, rosterResult] = await Promise.all([
      api.from("events").select("title").eq("id", id).maybeSingle(),
      api.rpc("get_event_participant_roster", { target_event_id: id })
    ]);
    if (eventResult.data?.title) setEventTitle(eventResult.data.title);
    if (rosterResult.error) setError(rosterResult.error.message);
    else {
      setError(null);
      setRoster((rosterResult.data ?? []) as RosterItem[]);
    }
    setLoading(false);
    setRefreshing(false);
  };

  useEffect(() => { void loadRoster(); }, [id]);

  const review = async (participantId: string, status: "approved" | "rejected") => {
    setBusyId(participantId);
    const { error: actionError } = await api.rpc("review_event_participant", { target_participant_id: participantId, target_status: status });
    if (actionError) setError(actionError.message);
    else setNotice(status === "approved" ? "Katılımcı onaylandı." : "Talep reddedildi, sıradaki kullanıcı ilerletildi.");
    setBusyId(null);
    await loadRoster(true);
  };

  const markNoShow = async (userId: string) => {
    if (!id) return;
    setBusyId(userId);
    const { error: actionError } = await api.rpc("mark_event_participant_no_show", { target_event_id: id, target_user_id: userId });
    if (actionError) setError(actionError.message);
    else setNotice("Katılımcı gelmedi olarak işaretlendi.");
    setBusyId(null);
    await loadRoster(true);
  };

  const scanCheckIn = async (data: string) => {
    if (!id || scanLocked) return;
    setScanLocked(true);
    const [prefix, eventId, userId] = data.split(":");
    if (prefix !== "bialem-checkin" || eventId !== id || !userId) {
      setError("Bu QR kod bu etkinliğe ait değil.");
      setTimeout(() => setScanLocked(false), 1600);
      return;
    }
    const { error: checkInError } = await api.rpc("check_in_event_participant", { target_event_id: id, target_user_id: userId });
    if (checkInError) setError(checkInError.message);
    else {
      setNotice("QR doğrulandı, katılımcı giriş yaptı.");
      setScannerOpen(false);
      await loadRoster(true);
    }
    setScanLocked(false);
  };

  const openScanner = async () => {
    if (!permission?.granted) {
      const result = await requestPermission();
      if (!result.granted) {
        setError("QR taramak için kamera izni vermelisiniz.");
        return;
      }
    }
    setError(null);
    setScannerOpen(true);
  };

  return (
    <ScrollView contentContainerStyle={styles.page} refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadRoster(true)} tintColor={colors.accent} />}>
      <View style={styles.header}>
        <Pressable style={styles.iconButton} onPress={() => router.back()}><Ionicons name="arrow-back" size={22} color={colors.ink} /></Pressable>
        <View style={{ flex: 1 }}><Text style={styles.kicker}>ORGANİZATÖR MERKEZİ</Text><Text style={styles.title}>{eventTitle}</Text></View>
      </View>

      <View style={styles.hero}>
        <View style={styles.heroIcon}><Ionicons name="qr-code" size={30} color={colors.actionText} /></View>
        <View style={{ flex: 1 }}><Text style={styles.heroTitle}>Hızlı ve güvenli giriş</Text><Text style={styles.body}>Katılımcının etkinlik ekranındaki QR kodunu okut.</Text></View>
        <Pressable style={styles.scanButton} onPress={() => void openScanner()}><Text style={styles.scanText}>QR Tara</Text></Pressable>
      </View>

      {scannerOpen ? (
        <View style={styles.scannerCard}>
          <CameraView style={styles.camera} barcodeScannerSettings={{ barcodeTypes: ["qr"] }} onBarcodeScanned={({ data }) => void scanCheckIn(data)} />
          <Pressable style={styles.closeScanner} onPress={() => setScannerOpen(false)}><Text style={styles.closeScannerText}>Tarayıcıyı kapat</Text></Pressable>
        </View>
      ) : null}

      {notice ? <Text style={styles.notice}>{notice}</Text> : null}
      {error ? <Text style={styles.error}>{error}</Text> : null}

      {loading ? <ActivityIndicator color={colors.accent} /> : (
        <View style={styles.panel}>
          <Text style={styles.panelTitle}>Katılımcılar</Text>
          <View style={styles.statsRow}>
            <MiniStat label="Bekleyen" value={roster.filter((item) => item.status === "pending").length} />
            <MiniStat label="Sırada" value={roster.filter((item) => item.status === "waitlisted").length} />
            <MiniStat label="Giriş" value={roster.filter((item) => item.status === "checked_in").length} />
          </View>
          {roster.length === 0 ? <Text style={styles.body}>Henüz katılım talebi yok.</Text> : roster.map((item) => (
            <View key={item.participant_id} style={styles.personCard}>
              <View style={styles.avatar}><Text style={styles.avatarText}>{item.display_name.slice(0, 1).toUpperCase()}</Text></View>
              <View style={{ flex: 1 }}><Text style={styles.personName}>{item.display_name}</Text><Text style={styles.status}>{statusLabel(item.status)}</Text></View>
              {item.status === "pending" ? (
                <View style={styles.actionRow}>
                  <Pressable disabled={busyId === item.participant_id} onPress={() => void review(item.participant_id, "approved")}><Ionicons name="checkmark-circle" size={30} color={colors.aqua} /></Pressable>
                  <Pressable disabled={busyId === item.participant_id} onPress={() => void review(item.participant_id, "rejected")}><Ionicons name="close-circle" size={30} color={colors.danger} /></Pressable>
                </View>
              ) : item.status === "approved" ? (
                <Pressable disabled={busyId === item.user_id} style={styles.noShowButton} onPress={() => void markNoShow(item.user_id)}><Text style={styles.noShowText}>Gelmedi</Text></Pressable>
              ) : null}
            </View>
          ))}
        </View>
      )}
    </ScrollView>
  );
}

function MiniStat({ label, value }: { label: string; value: number }) { return <View style={styles.miniStat}><Text style={styles.miniValue}>{value}</Text><Text style={styles.miniLabel}>{label}</Text></View>; }
function statusLabel(status: string) { return ({ pending: "Onay bekliyor", waitlisted: "Bekleme sırasında", approved: "Katılım onaylı", checked_in: "Giriş yaptı", rejected: "Reddedildi", cancelled: "İptal etti", no_show: "Gelmedi" } as Record<string, string>)[status] ?? status; }

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 16, paddingTop: 42, gap: 14, backgroundColor: colors.page },
  header: { flexDirection: "row", alignItems: "center", gap: 12 },
  iconButton: { width: 42, height: 42, borderRadius: 21, alignItems: "center", justifyContent: "center", backgroundColor: colors.surface },
  kicker: { color: colors.accent, fontSize: 10, fontWeight: "900", letterSpacing: 1.2 },
  title: { color: colors.ink, fontSize: 24, fontWeight: "900" },
  hero: { flexDirection: "row", alignItems: "center", gap: 10, padding: 14, borderRadius: 18, backgroundColor: colors.brandInk },
  heroIcon: { width: 50, height: 50, borderRadius: 17, alignItems: "center", justifyContent: "center", backgroundColor: colors.action },
  heroTitle: { color: colors.onBrand, fontSize: 17, fontWeight: "900" },
  body: { color: colors.muted, fontSize: 13, lineHeight: 18 },
  scanButton: { paddingHorizontal: 14, paddingVertical: 10, borderRadius: 999, backgroundColor: colors.action },
  scanText: { color: colors.actionText, fontSize: 12, fontWeight: "900" },
  scannerCard: { overflow: "hidden", borderRadius: 26, backgroundColor: colors.brandInk },
  camera: { height: 330 },
  closeScanner: { padding: 14 },
  closeScannerText: { color: colors.onBrand, textAlign: "center", fontWeight: "800" },
  notice: { color: colors.ink, backgroundColor: colors.accentSoft, borderRadius: 16, padding: 12, fontWeight: "700" },
  error: { color: colors.danger, backgroundColor: colors.surfaceStrong, borderRadius: 16, padding: 12, fontWeight: "700" },
  panel: { padding: 14, gap: 11, borderRadius: 18, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  panelTitle: { color: colors.ink, fontSize: 21, fontWeight: "900" },
  statsRow: { flexDirection: "row", gap: 9 },
  miniStat: { flex: 1, padding: 11, borderRadius: 16, alignItems: "center", backgroundColor: colors.surfaceStrong },
  miniValue: { color: colors.ink, fontSize: 20, fontWeight: "900" },
  miniLabel: { color: colors.muted, fontSize: 10, fontWeight: "800" },
  personCard: { flexDirection: "row", alignItems: "center", gap: 11, paddingVertical: 11, borderTopWidth: 1, borderTopColor: colors.border },
  avatar: { width: 42, height: 42, borderRadius: 15, alignItems: "center", justifyContent: "center", backgroundColor: colors.accentSoft },
  avatarText: { color: colors.ink, fontWeight: "900" },
  personName: { color: colors.ink, fontSize: 14, fontWeight: "900" },
  status: { color: colors.muted, fontSize: 11 },
  actionRow: { flexDirection: "row", gap: 5 },
  noShowButton: { paddingHorizontal: 10, paddingVertical: 8, borderRadius: 999, borderWidth: 1, borderColor: colors.border },
  noShowText: { color: colors.danger, fontSize: 11, fontWeight: "800" }
});
