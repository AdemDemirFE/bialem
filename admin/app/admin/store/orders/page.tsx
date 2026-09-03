import Link from "next/link";
import { getAdminApi } from "../../../../src/lib/admin-api";

export const dynamic = "force-dynamic";

const STATUS_LABELS: Record<string, string> = {
  PENDING_PAYMENT: "Ödeme Bekliyor",
  PAID: "Ödendi",
  WAITING_ADMIN_APPROVAL: "Onay Bekliyor",
  APPROVED: "Onaylandı",
  PREPARING: "Hazırlanıyor",
  READY_FOR_SHIPPING: "Kargoya Hazır",
  SHIPPED: "Kargoda",
  IN_TRANSIT: "Yolda",
  OUT_FOR_DELIVERY: "Dağıtımda",
  DELIVERED: "Teslim Edildi",
  CANCELLED: "İptal",
};

export default async function StoreOrdersPage({ searchParams }: { searchParams: Promise<{ status?: string }> }) {
  const { status } = await searchParams;
  const api = await getAdminApi();
  const qs = status ? `&status=${encodeURIComponent(status)}` : "";
  const page = await api.rest.get<any>(`/api/store/orders/admin/all?page=0&size=50${qs}`);
  const orders = page?.content ?? [];

  return (
    <div className="p-6 space-y-4">
      <h1 className="text-2xl font-bold">Mağaza Siparişleri</h1>
      <div className="flex gap-2 flex-wrap">
        {["", "WAITING_ADMIN_APPROVAL", "PREPARING", "SHIPPED", "DELIVERED", "CANCELLED"].map((s) => (
          <Link
            key={s || "all"}
            href={`/admin/store/orders${s ? `?status=${s}` : ""}`}
            className={`px-4 py-2 rounded-lg text-sm font-semibold border ${status === s || (!status && !s) ? "bg-black text-white" : "bg-white"}`}
          >
            {s ? STATUS_LABELS[s] : "Tümü"}
          </Link>
        ))}
      </div>
      <div className="bg-white rounded-xl border overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-3 text-left font-semibold">Sipariş No</th>
              <th className="px-4 py-3 text-left font-semibold">Müşteri</th>
              <th className="px-4 py-3 text-left font-semibold">Tutar</th>
              <th className="px-4 py-3 text-left font-semibold">Durum</th>
              <th className="px-4 py-3 text-left font-semibold">Tarih</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order: any) => (
              <tr key={order.id} className="border-t hover:bg-gray-50">
                <td className="px-4 py-3">
                  <Link href={`/admin/store/orders/${order.id}`} className="text-blue-600 hover:underline font-semibold">
                    {order.orderNumber}
                  </Link>
                </td>
                <td className="px-4 py-3">{order.user?.displayName ?? "-"}</td>
                <td className="px-4 py-3">{order.totalAmount} {order.currency}</td>
                <td className="px-4 py-3">{STATUS_LABELS[order.orderStatus] ?? order.orderStatus}</td>
                <td className="px-4 py-3">{new Date(order.createdAt).toLocaleString("tr-TR")}</td>
              </tr>
            ))}
            {orders.length === 0 && (
              <tr>
                <td colSpan={5} className="px-4 py-8 text-center text-gray-500">Henüz sipariş bulunmuyor.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
