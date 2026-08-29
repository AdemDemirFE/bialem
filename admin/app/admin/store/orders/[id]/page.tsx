import { revalidatePath } from "next/cache";
import { notFound } from "next/navigation";
import { getAdminApi } from "../../../../../src/lib/admin-api";

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

async function approveOrder(formData: FormData) {
  "use server";
  const id = formData.get("id");
  const api = await getAdminApi();
  await api.rest.post(`/api/store/orders/admin/${id}/approve`, {});
  revalidatePath(`/admin/store/orders/${id}`);
}

async function markPreparing(formData: FormData) {
  "use server";
  const id = formData.get("id");
  const api = await getAdminApi();
  await api.rest.post(`/api/store/orders/admin/${id}/preparing`, {});
  revalidatePath(`/admin/store/orders/${id}`);
}

async function shipOrder(formData: FormData) {
  "use server";
  const id = formData.get("id");
  const carrier = String(formData.get("carrier") ?? "");
  const trackingNumber = String(formData.get("trackingNumber") ?? "");
  if (!carrier || !trackingNumber) return;
  const api = await getAdminApi();
  await api.rest.post(`/api/store/orders/${id}/ship`, { carrier, trackingNumber });
  revalidatePath(`/admin/store/orders/${id}`);
}

export default async function StoreOrderDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  const api = await getAdminApi();
  const { data: order } = await api.rest.get<any>(`/api/store/orders/admin/${id}`);
  if (!order) notFound();

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Sipariş {order.orderNumber}</h1>
        <div className="flex gap-2">
          {order.orderStatus === "WAITING_ADMIN_APPROVAL" && (
            <form action={approveOrder}>
              <input type="hidden" name="id" value={id} />
              <button type="submit" className="px-4 py-2 rounded-lg bg-black text-white font-semibold">Onayla</button>
            </form>
          )}
          {order.orderStatus === "APPROVED" && (
            <form action={markPreparing}>
              <input type="hidden" name="id" value={id} />
              <button type="submit" className="px-4 py-2 rounded-lg bg-blue-600 text-white font-semibold">Hazırlanıyor</button>
            </form>
          )}
          {(order.orderStatus === "PREPARING" || order.orderStatus === "READY_FOR_SHIPPING") && (
            <form action={shipOrder} className="flex items-center gap-2">
              <input type="hidden" name="id" value={id} />
              <input name="carrier" placeholder="Kargo firması" className="px-3 py-2 border rounded" required />
              <input name="trackingNumber" placeholder="Takip no" className="px-3 py-2 border rounded" required />
              <button type="submit" className="px-4 py-2 rounded-lg bg-green-600 text-white font-semibold">Kargoya Ver</button>
            </form>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="bg-white rounded-xl border p-4">
          <div className="text-sm text-gray-500">Durum</div>
          <div className="text-lg font-bold">{STATUS_LABELS[order.orderStatus] ?? order.orderStatus}</div>
        </div>
        <div className="bg-white rounded-xl border p-4">
          <div className="text-sm text-gray-500">Ödeme</div>
          <div className="text-lg font-bold">{order.paymentStatus}</div>
        </div>
        <div className="bg-white rounded-xl border p-4">
          <div className="text-sm text-gray-500">Toplam</div>
          <div className="text-lg font-bold">{order.totalAmount} {order.currency}</div>
        </div>
      </div>

      <div className="bg-white rounded-xl border p-4 space-y-2">
        <h2 className="font-bold">Ürünler</h2>
        {order.items.map((item: any) => (
          <div key={item.id} className="flex justify-between py-2 border-b last:border-0">
            <div>
              <div className="font-semibold">{item.productName}</div>
              <div className="text-sm text-gray-500">x{item.quantity}</div>
            </div>
            <div className="font-bold">{item.totalPrice} {order.currency}</div>
          </div>
        ))}
      </div>

      {order.shipping && (
        <div className="bg-white rounded-xl border p-4 space-y-2">
          <h2 className="font-bold">Kargo</h2>
          <div className="text-sm">Firma: {order.shipping.carrier}</div>
          <div className="text-sm">Takip No: {order.shipping.trackingNumber}</div>
          <div className="text-sm">Durum: {STATUS_LABELS[order.shipping.shippingStatus] ?? order.shipping.shippingStatus}</div>
        </div>
      )}
    </div>
  );
}
