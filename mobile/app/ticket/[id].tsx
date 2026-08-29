import QRCode from "react-native-qrcode-svg";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";

type TicketRecord = {
  id: string;
  ticket_code: string;
  qr_code: string;
  status: string;
  used_at: string | null;
  event: {
    id: string;
    title: string;
    starts_at: string;
    location_name: string | null;
  };
  ticket_type: {
    id: string;
    name: string;
  };
};

export default function TicketDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const router = useRouter();
  const [ticket, setTicket] = useState<TicketRecord | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      if (!id) return;
      setLoading(true);
      const result = await api.from("tickets").select("id, ticket_code, qr_code, status, used_at").eq("id", id).maybeSingle<TicketRecord>();
      if (result.error || !result.data) {
        setError(result.error?.message || "Bilet bulunamadı.");
      } else {
        // Fetch enriched data via RPC fallback if relations not loaded
        setTicket(result.data);
      }
      setLoading(false);
    };
    void load();
  }, [id]);

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color={colors.accent} />
      </View>
    );
  }

  if (!ticket) {
    return (
      <View style={styles.center}>
        <Text style={styles.errorText}>{error || "Bilet bulunamadı."}</Text>
        <Pressable style={styles.button} onPress={() => router.back()}>
          <Text style={styles.buttonText}>Geri dön</Text>
        </Pressable>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.page}>
      <Text style={styles.title}>Bilet Detayı</Text>
      <View style={styles.card}>
        <Text style={styles.eventTitle}>{ticket.event?.title || "Etkinlik"}</Text>
        <Text style={styles.ticketType}>{ticket.ticket_type?.name || "Bilet"}</Text>
        {ticket.event?.starts_at ? <Text style={styles.meta}>{new Date(ticket.event.starts_at).toLocaleString("tr-TR")}</Text> : null}
        {ticket.event?.location_name ? <Text style={styles.meta}>{ticket.event.location_name}</Text> : null}
      </View>

      <View style={styles.qrCard}>
        <QRCode value={ticket.qr_code || `bialem://ticket/${ticket.ticket_code}`} size={200} color="#081a44" backgroundColor="#ffffff" />
        <Text style={styles.code}>{ticket.ticket_code}</Text>
        <Text style={[styles.status, (styles as Record<string, any>)[ticket.status]]}>{ticket.status.toUpperCase()}</Text>
        {ticket.used_at ? <Text style={styles.meta}>Kullanıldı: {new Date(ticket.used_at).toLocaleString("tr-TR")}</Text> : null}
      </View>

      <Pressable style={styles.button} onPress={() => router.replace("/my-tickets" as never)}>
        <Text style={styles.buttonText}>Tüm biletlerim</Text>
      </Pressable>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 24, gap: 16, alignItems: "center", backgroundColor: colors.page },
  center: { flex: 1, alignItems: "center", justifyContent: "center", gap: 16, padding: 24, backgroundColor: colors.page },
  title: { fontSize: 26, fontWeight: "900", color: colors.ink, marginTop: 24, alignSelf: "flex-start" },
  card: { width: "100%", padding: 20, borderRadius: 24, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, gap: 6 },
  eventTitle: { fontSize: 18, fontWeight: "900", color: colors.ink },
  ticketType: { color: colors.accent, fontWeight: "800" },
  meta: { color: colors.muted, fontWeight: "600" },
  qrCard: { width: "100%", padding: 24, borderRadius: 24, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, alignItems: "center", gap: 14 },
  code: { fontSize: 18, fontWeight: "900", color: colors.ink, letterSpacing: 1.5 },
  status: { fontSize: 12, fontWeight: "900", paddingHorizontal: 12, paddingVertical: 4, borderRadius: 999 },
  active: { color: "#16a34a", backgroundColor: "#dcfce7" },
  used: { color: colors.muted, backgroundColor: colors.page },
  cancelled: { color: colors.danger, backgroundColor: "#fee2e2" },
  expired: { color: colors.muted, backgroundColor: colors.page },
  errorText: { color: colors.danger, fontWeight: "700", textAlign: "center" },
  button: { paddingVertical: 14, paddingHorizontal: 28, borderRadius: 999, backgroundColor: colors.action },
  buttonText: { color: colors.actionText, fontWeight: "900", fontSize: 15 }
});
