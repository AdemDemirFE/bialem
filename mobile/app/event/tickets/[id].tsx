import { Ionicons } from "@expo/vector-icons";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import {
  ActivityIndicator,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View
} from "react-native";
import { showAppAlert } from "../../../src/components/AppAlert";
import { useAuth } from "../../../src/lib/auth";
import { api } from "../../../src/lib/api";
import { colors } from "../../../src/theme/colors";

type EventTicket = {
  id: string;
  name: string;
  description: string | null;
  price: number;
  currency: string;
  quantity: number;
  available_quantity: number;
  sale_start_date: string | null;
  sale_end_date: string | null;
  status: string;
};

export default function EventTicketSelectionScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const router = useRouter();
  const { user } = useAuth();
  const [eventTitle, setEventTitle] = useState("");
  const [tickets, setTickets] = useState<EventTicket[]>([]);
  const [quantities, setQuantities] = useState<Record<string, number>>({});
  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      if (!id) return;
      setLoading(true);
      const [eventResult, ticketsResult] = await Promise.all([
        api.events.getById(id),
        api.rpc("get_event_tickets", { target_event_id: id })
      ]);
      if (!eventResult.error && eventResult.data) setEventTitle(eventResult.data.title);
      if (ticketsResult.error) {
        setError(ticketsResult.error.message);
      } else {
        const list = (ticketsResult.data ?? []) as EventTicket[];
        setTickets(list);
        const initial: Record<string, number> = {};
        list.forEach((t) => (initial[t.id] = 0));
        setQuantities(initial);
      }
      setLoading(false);
    };
    void load();
  }, [id]);

  const total = tickets.reduce((sum, ticket) => {
    return sum + ticket.price * (quantities[ticket.id] ?? 0);
  }, 0);

  const adjust = (ticketId: string, delta: number) => {
    setQuantities((prev) => {
      const ticket = tickets.find((t) => t.id === ticketId);
      if (!ticket) return prev;
      const next = Math.max(0, Math.min((prev[ticketId] ?? 0) + delta, ticket.available_quantity));
      return { ...prev, [ticketId]: next };
    });
  };

  const selectedItems = tickets
    .filter((t) => (quantities[t.id] ?? 0) > 0)
    .map((t) => ({ ticket_id: t.id, quantity: quantities[t.id] }));

  const createOrder = async () => {
    if (!user) {
      await showAppAlert({ title: "Giriş gerekli", text: "Bilet satın almak için oturum açmalısınız." });
      return;
    }
    if (selectedItems.length === 0) {
      await showAppAlert({ title: "Bilet seçin", text: "Lütfen en az bir bilet tipi seçin." });
      return;
    }
    setCreating(true);
    setError(null);
    const result = await api.rpc("create_ticket_order", { target_items: selectedItems });
    if (result.error || !result.data) {
      setError(result.error?.message || "Sipariş oluşturulamadı.");
      setCreating(false);
      return;
    }
    const orderId = (result.data as { id: string }).id;
    setCreating(false);
    router.push(`/order/${orderId}` as never);
  };

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color={colors.accent} />
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.page}>
      <Pressable style={styles.backButton} onPress={() => router.back()}>
        <Ionicons name="arrow-back" size={18} color={colors.ink} />
        <Text style={styles.backText}>Etkinliğe dön</Text>
      </Pressable>

      <Text style={styles.title}>{eventTitle || "Etkinlik Biletleri"}</Text>
      <Text style={styles.subtitle}>Satın almak istediğiniz bilet tipini ve adedini seçin.</Text>

      {error ? <Text style={styles.errorText}>{error}</Text> : null}

      {tickets.length === 0 ? (
        <View style={styles.emptyCard}>
          <Text style={styles.emptyText}>Bu etkinlik için henüz bilet satışı başlamamış.</Text>
        </View>
      ) : (
        tickets.map((ticket) => (
          <View key={ticket.id} style={styles.card}>
            <View style={styles.cardHeader}>
              <View>
                <Text style={styles.ticketName}>{ticket.name}</Text>
                {ticket.description ? <Text style={styles.ticketDesc}>{ticket.description}</Text> : null}
              </View>
              <Text style={styles.price}>
                {ticket.price} {ticket.currency}
              </Text>
            </View>
            <Text style={styles.availability}>
              {ticket.available_quantity} adet kaldı
            </Text>
            <View style={styles.quantityRow}>
              <Pressable style={styles.qtyButton} onPress={() => adjust(ticket.id, -1)} disabled={(quantities[ticket.id] ?? 0) === 0}>
                <Ionicons name="remove" size={18} color={colors.ink} />
              </Pressable>
              <Text style={styles.qtyValue}>{quantities[ticket.id] ?? 0}</Text>
              <Pressable style={styles.qtyButton} onPress={() => adjust(ticket.id, 1)} disabled={(quantities[ticket.id] ?? 0) >= ticket.available_quantity}>
                <Ionicons name="add" size={18} color={colors.ink} />
              </Pressable>
            </View>
          </View>
        ))
      )}

      {tickets.length > 0 ? (
        <View style={styles.summaryCard}>
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>Toplam</Text>
            <Text style={styles.summaryValue}>
              {total.toFixed(2)} {tickets[0]?.currency}
            </Text>
          </View>
          <Pressable style={[styles.primaryButton, (creating || selectedItems.length === 0) && styles.disabledButton]} onPress={() => void createOrder()} disabled={creating || selectedItems.length === 0}>
            <Text style={styles.primaryButtonText}>{creating ? "Oluşturuluyor..." : "Siparişe geç"}</Text>
          </Pressable>
        </View>
      ) : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, padding: 24, gap: 16, backgroundColor: colors.page },
  center: { flex: 1, alignItems: "center", justifyContent: "center", backgroundColor: colors.page },
  backButton: { flexDirection: "row", alignItems: "center", gap: 8, alignSelf: "flex-start" },
  backText: { color: colors.ink, fontWeight: "800" },
  title: { fontSize: 26, fontWeight: "900", color: colors.ink, marginTop: 8 },
  subtitle: { color: colors.muted, fontSize: 15, lineHeight: 22 },
  errorText: { color: colors.danger, fontWeight: "700" },
  emptyCard: { padding: 24, borderRadius: 24, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  emptyText: { color: colors.muted, textAlign: "center", fontWeight: "600" },
  card: { padding: 18, borderRadius: 24, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, gap: 12 },
  cardHeader: { flexDirection: "row", justifyContent: "space-between", gap: 12, alignItems: "flex-start" },
  ticketName: { fontSize: 17, fontWeight: "900", color: colors.ink },
  ticketDesc: { color: colors.muted, fontSize: 13, marginTop: 4 },
  price: { fontSize: 17, fontWeight: "900", color: colors.accent },
  availability: { color: colors.muted, fontSize: 13, fontWeight: "700" },
  quantityRow: { flexDirection: "row", alignItems: "center", gap: 14, alignSelf: "flex-start" },
  qtyButton: { width: 36, height: 36, borderRadius: 18, alignItems: "center", justifyContent: "center", backgroundColor: colors.page, borderWidth: 1, borderColor: colors.border },
  qtyValue: { fontSize: 17, fontWeight: "900", color: colors.ink, minWidth: 24, textAlign: "center" },
  summaryCard: { padding: 20, borderRadius: 24, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, gap: 16 },
  summaryRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  summaryLabel: { fontSize: 16, fontWeight: "800", color: colors.ink },
  summaryValue: { fontSize: 18, fontWeight: "900", color: colors.accent },
  primaryButton: { paddingVertical: 14, borderRadius: 999, backgroundColor: colors.action, alignItems: "center" },
  disabledButton: { opacity: 0.5 },
  primaryButtonText: { color: colors.actionText, fontWeight: "900", fontSize: 15 }
});
