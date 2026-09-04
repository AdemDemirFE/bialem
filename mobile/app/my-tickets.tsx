import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { Pressable, RefreshControl, ScrollView, StyleSheet, Text, View } from "react-native";
import { Reveal, Skeleton } from "../src/animations";
import { FeedbackState } from "../src/components/ui/FeedbackState";
import { api } from "../src/lib/api";
import { colors } from "../src/theme/colors";

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

export default function MyTicketsScreen() {
  const router = useRouter();
  const [tickets, setTickets] = useState<TicketRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadTickets = async (mode: "initial" | "refresh" = "initial") => {
    if (mode === "initial") setLoading(true);
    else setRefreshing(true);
    setError(null);
    const result = await api.rpc("get_my_tickets");
    if (result.error) {
      setError(result.error.message);
    } else {
      setTickets((result.data ?? []) as TicketRecord[]);
    }
    if (mode === "initial") setLoading(false);
    else setRefreshing(false);
  };

  useEffect(() => {
    void loadTickets();
  }, []);

  if (loading) {
    return (
      <View style={styles.center}>
        <View style={{ width: "100%", padding: 24, gap: 12 }}>
          <Skeleton height={150} borderRadius={24} />
          <Skeleton height={150} borderRadius={24} />
        </View>
      </View>
    );
  }

  return (
    <ScrollView
      contentContainerStyle={styles.page}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadTickets("refresh")} tintColor={colors.accent} />}
    >
      <Reveal>
      <Pressable style={styles.backButton} onPress={() => router.back()}>
        <Ionicons name="arrow-back" size={18} color={colors.ink} />
        <Text style={styles.backText}>Geri</Text>
      </Pressable>
      </Reveal>

      <Reveal index={1}>
      <Text style={styles.title}>Biletlerim</Text>
      </Reveal>

      {error ? (
        <FeedbackState
          kind="error"
          title="Biletler yüklenemedi"
          message={error}
          onRetry={() => void loadTickets()}
        />
      ) : null}

      {tickets.length === 0 ? (
        <View style={styles.emptyCard}>
          <Text style={styles.emptyText}>Henüz bir biletiniz yok.</Text>
          <Pressable style={styles.primaryButton} onPress={() => router.replace("/(tabs)/feed")}>
            <Text style={styles.primaryButtonText}>Etkinlikleri keşfet</Text>
          </Pressable>
        </View>
      ) : (
        tickets.map((ticket, i) => (
          <Reveal key={ticket.id} index={Math.min(i, 5)}>
          <Pressable
            style={({ pressed }) => [styles.card, pressed && { opacity: 0.94, transform: [{ scale: 0.99 }] }]}
            onPress={() => router.push(`/ticket/${ticket.id}` as never)}
          >
            <View style={styles.cardHeader}>
              <Text style={styles.eventTitle}>{ticket.event.title}</Text>
              <Text style={[styles.status, (styles as Record<string, any>)[ticket.status]]}>{ticket.status.toUpperCase()}</Text>
            </View>
            <Text style={styles.ticketType}>{ticket.ticket_type.name}</Text>
            <Text style={styles.meta}>{new Date(ticket.event.starts_at).toLocaleString("tr-TR")}</Text>
            {ticket.event.location_name ? <Text style={styles.meta}>{ticket.event.location_name}</Text> : null}
            <View style={styles.codeRow}>
              <Text style={styles.codeLabel}>Bilet Kodu</Text>
              <Text style={styles.code}>{ticket.ticket_code}</Text>
            </View>
          </Pressable>
          </Reveal>
        ))
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 24, gap: 16, backgroundColor: colors.page },
  center: { flex: 1, alignItems: "center", justifyContent: "center", backgroundColor: colors.page },
  backButton: { flexDirection: "row", alignItems: "center", gap: 8, alignSelf: "flex-start" },
  backText: { color: colors.ink, fontWeight: "800" },
  title: { fontSize: 26, fontWeight: "900", color: colors.ink, marginTop: 8 },
  errorText: { color: colors.danger, fontWeight: "700" },
  emptyCard: { padding: 24, borderRadius: 24, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, gap: 16, alignItems: "center" },
  emptyText: { color: colors.muted, fontWeight: "600", textAlign: "center" },
  card: { padding: 18, borderRadius: 24, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, gap: 8 },
  cardHeader: { flexDirection: "row", justifyContent: "space-between", gap: 12, alignItems: "flex-start" },
  eventTitle: { flex: 1, fontSize: 16, fontWeight: "900", color: colors.ink },
  ticketType: { color: colors.accent, fontWeight: "800" },
  meta: { color: colors.muted, fontSize: 13, fontWeight: "600" },
  codeRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginTop: 8, paddingTop: 12, borderTopWidth: 1, borderTopColor: colors.border },
  codeLabel: { color: colors.muted, fontWeight: "700" },
  code: { fontSize: 15, fontWeight: "900", color: colors.ink, letterSpacing: 1 },
  status: { fontSize: 11, fontWeight: "900", paddingHorizontal: 8, paddingVertical: 3, borderRadius: 999 },
  active: { color: "#16a34a", backgroundColor: "#dcfce7" },
  used: { color: colors.muted, backgroundColor: colors.page },
  cancelled: { color: colors.danger, backgroundColor: "#fee2e2" },
  expired: { color: colors.muted, backgroundColor: colors.page },
  primaryButton: { paddingVertical: 14, paddingHorizontal: 24, borderRadius: 999, backgroundColor: colors.action },
  primaryButtonText: { color: colors.actionText, fontWeight: "900", fontSize: 15 }
});
