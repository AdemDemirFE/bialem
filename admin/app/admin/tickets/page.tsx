import Link from "next/link";
import { revalidatePath } from "next/cache";
import type { CSSProperties } from "react";
import { getAdminApi } from "../../../src/lib/admin-api";
import { AdminSessionControls } from "../session-controls";

export const dynamic = "force-dynamic";

type EventRecord = {
  id: string;
  title: string;
  starts_at: string;
};

type TicketTypeRecord = {
  id: string;
  name: string;
  price: number;
  currency: string;
  quantity: number;
  sold_quantity: number;
  status: string;
  event_id: string;
};

type OrderRecord = {
  id: string;
  order_number: string;
  total_amount: number;
  currency: string;
  status: string;
  created_at: string;
  user_id: string;
};

type PaymentRecord = {
  id: string;
  provider: string;
  provider_transaction_id: string;
  amount: number;
  currency: string;
  status: string;
  order_id: string;
};

type TicketRecord = {
  id: string;
  ticket_code: string;
  status: string;
  event_id: string;
  user_id: string;
};

async function createTicketType(formData: FormData) {
  "use server";

  const eventId = String(formData.get("event_id") ?? "");
  const name = String(formData.get("name") ?? "").trim();
  const price = Number(formData.get("price") ?? 0);
  const currency = String(formData.get("currency") ?? "TRY").trim();
  const quantity = Number(formData.get("quantity") ?? 0);
  const saleStart = String(formData.get("sale_start_date") ?? "").trim();
  const saleEnd = String(formData.get("sale_end_date") ?? "").trim();

  if (!eventId || !name || price < 0 || quantity <= 0) return;

  const admin = await getAdminApi();
  await admin.from("event_tickets").insert({
    event_id: eventId,
    name,
    description: String(formData.get("description") ?? "").trim() || null,
    price,
    currency,
    quantity,
    sold_quantity: 0,
    sale_start_date: saleStart ? new Date(saleStart).toISOString() : null,
    sale_end_date: saleEnd ? new Date(saleEnd).toISOString() : null,
    status: "active"
  });

  revalidatePath("/admin/tickets");
}

export default async function TicketsAdminPage() {
  const admin = await getAdminApi();
  const [eventsResult, ticketTypesResult, ordersResult, paymentsResult, ticketsResult] = await Promise.all([
    admin.from("events").select("id, title, starts_at").eq("status", "published").order("starts_at", { ascending: false }).limit(50),
    admin.from("event_tickets").select("id, name, price, currency, quantity, sold_quantity, status, event_id").order("created_at", { ascending: false }).limit(50),
    admin.from("ticket_orders").select("id, order_number, total_amount, currency, status, created_at, user_id").order("created_at", { ascending: false }).limit(50),
    admin.from("payments").select("id, provider, provider_transaction_id, amount, currency, status, order_id").order("created_at", { ascending: false }).limit(50),
    admin.from("tickets").select("id, ticket_code, status, event_id, user_id").order("created_at", { ascending: false }).limit(50)
  ]);

  const events = (eventsResult.data ?? []) as EventRecord[];
  const ticketTypes = (ticketTypesResult.data ?? []) as TicketTypeRecord[];
  const orders = (ordersResult.data ?? []) as OrderRecord[];
  const payments = (paymentsResult.data ?? []) as PaymentRecord[];
  const tickets = (ticketsResult.data ?? []) as TicketRecord[];

  return (
    <main style={styles.page}>
      <AdminSessionControls />
      <div style={styles.header}>
        <div>
          <p style={styles.kicker}>Bilet Satışı</p>
          <h1 style={styles.title}>Bilet yönetimi</h1>
          <p style={styles.description}>Etkinlik bilet tiplerini, siparişleri, ödemeleri ve oluşturulan biletleri yönetin.</p>
        </div>
        <Link href="/admin" style={styles.backLink}>Admin ana sayfa</Link>
      </div>

      <section style={styles.panel}>
        <h2 style={styles.panelTitle}>Yeni bilet tipi oluştur</h2>
        <form action={createTicketType} style={styles.formGrid}>
          <select name="event_id" required defaultValue="" style={styles.input}>
            <option value="" disabled>Etkinlik seç</option>
            {events.map((event) => (
              <option key={event.id} value={event.id}>{event.title} - {new Date(event.starts_at).toLocaleString("tr-TR")}</option>
            ))}
          </select>
          <input name="name" required placeholder="Bilet adı (örn. VIP)" style={styles.input} />
          <input name="description" placeholder="Açıklama" style={styles.input} />
          <input name="price" type="number" min="0" step="0.01" required placeholder="Fiyat" style={styles.input} />
          <input name="currency" defaultValue="TRY" required placeholder="Para birimi" style={styles.input} />
          <input name="quantity" type="number" min="1" required placeholder="Kontenjan" style={styles.input} />
          <input name="sale_start_date" type="datetime-local" placeholder="Satış başlangıcı" style={styles.input} />
          <input name="sale_end_date" type="datetime-local" placeholder="Satış bitişi" style={styles.input} />
          <button type="submit" style={styles.primaryButton}>Bilet tipi oluştur</button>
        </form>
      </section>

      <section style={styles.panel}>
        <h2 style={styles.panelTitle}>Bilet tipleri</h2>
        {ticketTypes.length === 0 ? <p style={styles.emptyText}>Henüz bilet tipi oluşturulmamış.</p> : (
          <div style={styles.stack}>
            {ticketTypes.map((ticket) => (
              <article key={ticket.id} style={styles.rowCard}>
                <div>
                  <strong>{ticket.name}</strong>
                  <p style={styles.meta}>{ticket.price} {ticket.currency} · {ticket.sold_quantity}/{ticket.quantity} satıldı</p>
                </div>
                <span style={ticket.status === "active" ? styles.successBadge : styles.pendingBadge}>{ticket.status}</span>
              </article>
            ))}
          </div>
        )}
      </section>

      <section style={styles.twoColumn}>
        <section style={styles.sidePanel}>
          <h2 style={styles.panelTitle}>Son siparişler</h2>
          {orders.length === 0 ? <p style={styles.emptyText}>Henüz sipariş yok.</p> : (
            <div style={styles.stack}>
              {orders.map((order) => (
                <article key={order.id} style={styles.rowCard}>
                  <div>
                    <strong>#{order.order_number}</strong>
                    <p style={styles.meta}>{order.total_amount} {order.currency} · {new Date(order.created_at).toLocaleString("tr-TR")}</p>
                  </div>
                  <span style={order.status === "paid" ? styles.successBadge : styles.pendingBadge}>{order.status}</span>
                </article>
              ))}
            </div>
          )}
        </section>
        <section style={styles.sidePanel}>
          <h2 style={styles.panelTitle}>Son ödemeler</h2>
          {payments.length === 0 ? <p style={styles.emptyText}>Henüz ödeme kaydı yok.</p> : (
            <div style={styles.stack}>
              {payments.map((payment) => (
                <article key={payment.id} style={styles.rowCard}>
                  <div>
                    <strong>{payment.provider}</strong>
                    <p style={styles.meta}>{payment.amount} {payment.currency} · {payment.provider_transaction_id}</p>
                  </div>
                  <span style={payment.status === "completed" ? styles.successBadge : styles.pendingBadge}>{payment.status}</span>
                </article>
              ))}
            </div>
          )}
        </section>
      </section>

      <section style={styles.panel}>
        <h2 style={styles.panelTitle}>Oluşturulan biletler</h2>
        {tickets.length === 0 ? <p style={styles.emptyText}>Henüz bilet oluşturulmamış.</p> : (
          <div style={styles.stack}>
            {tickets.map((ticket) => (
              <article key={ticket.id} style={styles.rowCard}>
                <div>
                  <strong>{ticket.ticket_code}</strong>
                  <p style={styles.meta}>event {ticket.event_id.slice(0, 8)} · user {ticket.user_id.slice(0, 8)}</p>
                </div>
                <span style={ticket.status === "active" ? styles.successBadge : styles.pendingBadge}>{ticket.status}</span>
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}

const styles: Record<string, CSSProperties> = {
  page: { minHeight: "100vh", padding: "48px 32px 64px" },
  header: { maxWidth: 1180, margin: "0 auto 28px", display: "flex", justifyContent: "space-between", gap: 24, alignItems: "flex-start" },
  kicker: { margin: "0 0 8px", color: "#7b35ff", fontWeight: 800, textTransform: "uppercase", letterSpacing: 2 },
  title: { margin: 0, fontSize: "clamp(2.2rem, 6vw, 4.5rem)", lineHeight: 0.95 },
  description: { maxWidth: 680, color: "#53617e", fontSize: "1.05rem", lineHeight: 1.6 },
  backLink: { color: "#081a40", fontWeight: 800, textDecoration: "none", padding: "12px 16px", border: "1px solid #d8deeb", borderRadius: 999, background: "white" },
  panel: { maxWidth: 1180, margin: "24px auto 0", background: "var(--surface)", border: "1px solid var(--line)", borderRadius: 32, padding: 24, boxShadow: "var(--shadow)" },
  sidePanel: { background: "var(--surface)", border: "1px solid var(--line)", borderRadius: 32, padding: 24, boxShadow: "var(--shadow)" },
  twoColumn: { maxWidth: 1180, margin: "24px auto 0", display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(320px, 1fr))", gap: 20 },
  panelTitle: { marginTop: 0, marginBottom: 16, fontSize: "1.5rem" },
  formGrid: { display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))", gap: 12 },
  input: { width: "100%", minHeight: 48, borderRadius: 16, border: "1px solid var(--line)", padding: "12px 14px", background: "#fbfdff", color: "var(--ink)", font: "inherit" },
  primaryButton: { padding: "14px 20px", borderRadius: 999, background: "#ffad1f", color: "#081a40", border: "none", fontWeight: 800, cursor: "pointer" },
  stack: { display: "grid", gap: 12 },
  rowCard: { display: "flex", justifyContent: "space-between", alignItems: "center", gap: 16, padding: 16, border: "1px solid var(--line)", borderRadius: 18, background: "linear-gradient(180deg, #ffffff 0%, #f9fbff 100%)" },
  meta: { margin: "4px 0 0", color: "var(--muted)", fontSize: "0.9rem" },
  emptyText: { color: "var(--muted)", lineHeight: 1.7 },
  successBadge: { background: "#def6ff", color: "var(--success)", borderRadius: 999, padding: "6px 10px", fontWeight: 700, fontSize: "0.8rem" },
  pendingBadge: { background: "#fff1cf", color: "#c47d00", borderRadius: 999, padding: "6px 10px", fontWeight: 700, fontSize: "0.8rem" }
};
