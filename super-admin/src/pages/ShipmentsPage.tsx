import { useEffect, useState, useCallback } from "react";
import {
  getPage, request,
  type StoreOrderDTO, type StoreOrderDetailDTO,
  type StoreShippingDTO, type StoreShippingRequest,
} from "../api";
import { Alert, TableSkeleton } from "../components/Feedback";

const SHIPPING_STATUSES = ["PENDING_PAYMENT", "WAITING_ADMIN_APPROVAL", "APPROVED", "PREPARING", "READY_FOR_SHIPPING", "SHIPPED", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"];
const SHIPPING_LABELS: Record<string, string> = {
  PENDING_PAYMENT: "Ödeme Bekliyor", APPROVED: "Onaylandı", PREPARING: "Hazırlanıyor",
  READY_FOR_SHIPPING: "Kargoya Hazır", SHIPPED: "Kargoya Verildi", IN_TRANSIT: "Yolda",
  OUT_FOR_DELIVERY: "Dağıtımda", DELIVERED: "Teslim Edildi", CANCELLED: "İptal",
  WAITING_ADMIN_APPROVAL: "Onay Bekliyor", PAID: "Ödendi", PROCESSING: "İşleniyor",
};
const SHIPPING_BADGE: Record<string, string> = {
  PENDING_PAYMENT: "badge-warning", APPROVED: "badge-info", PREPARING: "badge-info",
  READY_FOR_SHIPPING: "badge-info", SHIPPED: "badge-success", IN_TRANSIT: "badge-success",
  OUT_FOR_DELIVERY: "badge-success", DELIVERED: "badge-success", CANCELLED: "badge-danger",
  WAITING_ADMIN_APPROVAL: "badge-warning", PAID: "badge-success", PROCESSING: "badge-info",
};

const CARRIERS = ["Yurtiçi Kargo", "MNG Kargo", "Aras Kargo", "PTT Kargo", "Sürat Kargo", "UPS", "FedEx", "DHL"];

export default function ShipmentsPage() {
  const [items, setItems] = useState<StoreOrderDTO[]>([]);
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const pageSize = 20;

  // ── Detail modal ──
  const [detail, setDetail] = useState<StoreOrderDetailDTO | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  // ── Shipping create modal ──
  const [shipModal, setShipModal] = useState<{ orderId: number; orderNumber: string } | null>(null);
  const [shipForm, setShipForm] = useState<StoreShippingRequest>({ carrier: "", trackingNumber: "" });

  // ── Shipping status modal ──
  const [statusModal, setStatusModal] = useState<{ orderId: number; shipping: StoreShippingDTO } | null>(null);
  const [newShipStatus, setNewShipStatus] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const params: Record<string, string | number> = { page, size: pageSize, sort: "id,desc" };
      if (statusFilter) params.status = statusFilter;
      const res = await getPage<StoreOrderDTO>("/store/orders/admin/all", params);
      setItems(res.content);
      setTotal(res.totalElements);
    } catch (err: any) {
      setError(err.message);
      setItems([]);
    }
    setLoading(false);
  }, [page, statusFilter]);

  useEffect(() => { load(); }, [load]);

  const openDetail = async (id: number) => {
    setDetailLoading(true);
    try {
      setDetail(await request<StoreOrderDetailDTO>(`/store/orders/admin/${id}`));
    } catch (err: any) {
      setError(err.message);
    }
    setDetailLoading(false);
  };

  const badge = (s: string) => (
    <span className={`badge ${SHIPPING_BADGE[s] || "badge-info"}`}>{SHIPPING_LABELS[s] || s}</span>
  );

  // ── Create shipping ──
  const openShip = (orderId: number, orderNumber: string) => {
    setShipForm({ carrier: CARRIERS[0], trackingNumber: "" });
    setShipModal({ orderId, orderNumber });
  };

  const handleCreateShipping = async () => {
    if (!shipModal || !shipForm.trackingNumber.trim()) {
      setError("Takip numarası zorunludur");
      return;
    }
    setError("");
    setSuccess("");
    try {
      await request(`/store/orders/${shipModal.orderId}/ship`, {
        method: "POST",
        json: shipForm,
      });
      setSuccess("Kargo oluşturuldu");
      setShipModal(null);
      load();
      if (detail?.id === shipModal.orderId) openDetail(shipModal.orderId);
    } catch (err: any) {
      setError(err.message);
    }
  };

  // ── Update shipping status ──
  const openStatusUpdate = (orderId: number, shipping: StoreShippingDTO) => {
    setNewShipStatus(shipping.shippingStatus || "SHIPPED");
    setStatusModal({ orderId, shipping });
  };

  const handleUpdateStatus = async () => {
    if (!statusModal) return;
    setError("");
    setSuccess("");
    try {
      await request(`/store/orders/${statusModal.orderId}/shipping-status?status=${newShipStatus}`, {
        method: "POST",
      });
      setSuccess("Kargo durumu güncellendi");
      setStatusModal(null);
      load();
      if (detail?.id === statusModal.orderId) openDetail(statusModal.orderId);
    } catch (err: any) {
      setError(err.message);
    }
  };

  // ── Order status action ──
  const orderAction = async (id: number, verb: string, label: string) => {
    if (!confirm(`${label} işlemi yapılsın mı?`)) return;
    setError("");
    setSuccess("");
    try {
      await request(`/store/orders/admin/${id}/${verb}`, { method: "POST" });
      setSuccess(label + " başarılı");
      load();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const getNextOrderAction = (status: string): { verb: string; label: string } | null => {
    const map: Record<string, { verb: string; label: string }> = {
      PENDING_PAYMENT: { verb: "approve", label: "Onayla" },
      WAITING_ADMIN_APPROVAL: { verb: "approve", label: "Onayla" },
      APPROVED: { verb: "preparing", label: "Hazırlanıyor" },
      PREPARING: { verb: "ready-for-shipping", label: "Kargoya Hazır" },
    };
    return map[status] || null;
  };

  return (
    <div>
      <div className="page-header">
        <h1>Kargolar ({total})</h1>
        <select className="form-select" style={{ width: 200 }} value={statusFilter} onChange={e => { setStatusFilter(e.target.value); setPage(0); }}>
          <option value="">Tüm Durumlar</option>
          {SHIPPING_STATUSES.map(s => <option key={s} value={s}>{SHIPPING_LABELS[s]}</option>)}
        </select>
      </div>

      {error && <Alert kind="error">{error}</Alert>}
      {success && <Alert kind="success">{success}</Alert>}

      {loading ? <TableSkeleton rows={6} /> : (
        <>
          <table className="data-table">
            <thead><tr>
              <th>Sipariş No</th><th>Tutar</th><th>Sipariş Durumu</th><th>Kargo Durumu</th><th>Tarih</th><th>İşlemler</th>
            </tr></thead>
            <tbody>
              {items.map(o => {
                const next = getNextOrderAction(o.orderStatus);
                const canShip = ["READY_FOR_SHIPPING", "PREPARING", "APPROVED", "SHIPPED", "IN_TRANSIT", "OUT_FOR_DELIVERY"].includes(o.orderStatus);
                const hasShipping = ["SHIPPED", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED"].includes(o.shippingStatus);
                return (
                  <tr key={o.id} style={{ cursor: "pointer" }} onClick={() => openDetail(o.id)}>
                    <td style={{ fontWeight: 500 }}>{o.orderNumber}</td>
                    <td>₺{o.totalAmount?.toLocaleString("tr-TR")}</td>
                    <td>{badge(o.orderStatus)}</td>
                    <td>{badge(o.shippingStatus)}</td>
                    <td>{new Date(o.createdAt).toLocaleDateString("tr-TR")}</td>
                    <td onClick={e => e.stopPropagation()} style={{ display: "flex", gap: 4, flexWrap: "wrap" }}>
                      {next && <button className="btn btn-ghost btn-sm" onClick={() => orderAction(o.id, next.verb, next.label)}>{next.label}</button>}
                      {canShip && !hasShipping && (
                        <button className="btn btn-ghost btn-sm" style={{ color: "var(--primary)" }} onClick={() => openShip(o.id, o.orderNumber)}>Kargoya Ver</button>
                      )}
                      {hasShipping && (
                        <button className="btn btn-ghost btn-sm" onClick={() => openDetail(o.id)}>Kargo Detay</button>
                      )}
                    </td>
                  </tr>
                );
              })}
              {items.length === 0 && <tr><td colSpan={6} className="empty">Kayıt bulunamadı</td></tr>}
            </tbody>
          </table>
          <div className="pagination">
            <button className="btn btn-ghost btn-sm" disabled={page === 0} onClick={() => setPage(p => p - 1)}>← Önceki</button>
            <span>Sayfa {page + 1} / {Math.ceil(total / pageSize) || 1}</span>
            <button className="btn btn-ghost btn-sm" disabled={(page + 1) * pageSize >= total} onClick={() => setPage(p => p + 1)}>Sonraki →</button>
          </div>
        </>
      )}

      {/* ── Order Detail Modal ── */}
      {(detail || detailLoading) && (
        <div className="modal-overlay" onClick={() => setDetail(null)}>
          <div className="modal" onClick={e => e.stopPropagation()} style={{ width: 680, maxWidth: "95vw" }}>
            {detailLoading ? <TableSkeleton rows={5} /> : detail && (
              <>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "start", marginBottom: 16 }}>
                  <div>
                    <h2 style={{ margin: 0 }}>Sipariş {detail.orderNumber}</h2>
                    <div style={{ color: "var(--text2)", fontSize: 12, marginTop: 4 }}>
                      ID: {detail.id} • {new Date(detail.createdAt).toLocaleString("tr-TR")}
                    </div>
                  </div>
                  <button className="btn btn-ghost btn-sm" onClick={() => setDetail(null)}>✕</button>
                </div>

                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12, marginBottom: 20 }}>
                  <div className="stat-card"><div className="label">Sipariş Durumu</div><div>{badge(detail.orderStatus)}</div></div>
                  <div className="stat-card"><div className="label">Kargo Durumu</div><div>{badge(detail.shippingStatus)}</div></div>
                  <div className="stat-card"><div className="label">Ödeme</div><div>{badge(detail.paymentStatus)}</div></div>
                </div>

                {/* Shipping info */}
                {detail.shipping ? (
                  <div style={{ background: "var(--surface2)", borderRadius: 8, padding: 16, marginBottom: 20 }}>
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 8 }}>
                      <strong>Kargo Bilgileri</strong>
                      <button className="btn btn-ghost btn-sm" onClick={() => openStatusUpdate(detail.id, detail.shipping!)}>Durum Güncelle</button>
                    </div>
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8, fontSize: 13 }}>
                      <div>Kurye: <strong>{detail.shipping.carrier || "—"}</strong></div>
                      <div>Takip No: <strong>{detail.shipping.trackingNumber || "—"}</strong></div>
                      <div>Durum: {detail.shipping.shippingStatus ? badge(detail.shipping.shippingStatus) : "—"}</div>
                      {detail.shipping.shippedAt && <div>Gönderim: {new Date(detail.shipping.shippedAt).toLocaleString("tr-TR")}</div>}
                      {detail.shipping.estimatedDeliveryDate && <div>Tahmini Teslimat: {detail.shipping.estimatedDeliveryDate}</div>}
                      {detail.shipping.deliveredAt && <div>Teslimat: {new Date(detail.shipping.deliveredAt).toLocaleString("tr-TR")}</div>}
                    </div>
                  </div>
                ) : (
                  <div style={{ background: "var(--surface2)", borderRadius: 8, padding: 16, marginBottom: 20, textAlign: "center" }}>
                    <div style={{ color: "var(--text2)", marginBottom: 8 }}>Henüz kargo bilgisi girilmemiş</div>
                    {["READY_FOR_SHIPPING", "PREPARING", "APPROVED"].includes(detail.orderStatus) && (
                      <button className="btn btn-primary btn-sm" onClick={() => openShip(detail.id, detail.orderNumber)}>Kargoya Ver</button>
                    )}
                  </div>
                )}

                {/* Price */}
                <div style={{ background: "var(--surface2)", borderRadius: 8, padding: 16, marginBottom: 20 }}>
                  <strong>Fiyat</strong>
                  <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8, marginTop: 8, fontSize: 13 }}>
                    <div>Ara Toplam: ₺{detail.subtotal?.toLocaleString("tr-TR")}</div>
                    <div>İndirim: -₺{detail.discountAmount?.toLocaleString("tr-TR") || "0"}</div>
                    <div>Kargo: ₺{detail.shippingAmount?.toLocaleString("tr-TR") || "0"}</div>
                    <div style={{ fontWeight: 700, fontSize: 16 }}>Toplam: ₺{detail.totalAmount?.toLocaleString("tr-TR")}</div>
                  </div>
                </div>

                {/* Items */}
                {detail.items?.length > 0 && (
                  <div style={{ marginBottom: 20 }}>
                    <strong>Ürünler</strong>
                    <table className="data-table" style={{ marginTop: 8 }}>
                      <thead><tr><th>Ürün</th><th>Adet</th><th>Birim Fiyat</th><th>Toplam</th></tr></thead>
                      <tbody>
                        {detail.items.map(item => (
                          <tr key={item.id}>
                            <td>{item.productName || `Ürün #${item.productId}`}</td>
                            <td>{item.quantity}</td>
                            <td>₺{item.unitPrice?.toLocaleString("tr-TR")}</td>
                            <td>₺{item.totalPrice?.toLocaleString("tr-TR")}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}

                {/* Shipping Address */}
                {detail.shippingAddress && (
                  <div style={{ background: "var(--surface2)", borderRadius: 8, padding: 12, marginBottom: 20 }}>
                    <strong style={{ fontSize: 12, color: "var(--text2)" }}>KARGO ADRESİ</strong>
                    <div style={{ marginTop: 6, fontSize: 13, lineHeight: 1.6 }}>
                      <div>{detail.shippingAddress.firstName} {detail.shippingAddress.lastName}</div>
                      <div>{detail.shippingAddress.addressLine}</div>
                      <div>{detail.shippingAddress.district}/{detail.shippingAddress.city}</div>
                      {detail.shippingAddress.phone && <div>📞 {detail.shippingAddress.phone}</div>}
                    </div>
                  </div>
                )}

                <div className="modal-actions">
                  <button className="btn btn-ghost" onClick={() => setDetail(null)}>Kapat</button>
                </div>
              </>
            )}
          </div>
        </div>
      )}

      {/* ── Create Shipping Modal ── */}
      {shipModal && (
        <div className="modal-overlay" onClick={() => setShipModal(null)}>
          <div className="modal" onClick={e => e.stopPropagation()} style={{ width: 420 }}>
            <h2>Kargo Oluştur</h2>
            <p style={{ color: "var(--text2)", fontSize: 13, marginBottom: 16 }}>Sipariş: {shipModal.orderNumber}</p>
            <div className="form-group">
              <label>Kurye *</label>
              <select className="form-select" value={shipForm.carrier} onChange={e => setShipForm({ ...shipForm, carrier: e.target.value })}>
                {CARRIERS.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>Takip Numarası *</label>
              <input className="form-input" value={shipForm.trackingNumber} onChange={e => setShipForm({ ...shipForm, trackingNumber: e.target.value })} placeholder="TR123456789" />
            </div>
            <div className="form-group">
              <label>Tahmini Teslimat</label>
              <input className="form-input" type="date" value={shipForm.estimatedDeliveryDate || ""} onChange={e => setShipForm({ ...shipForm, estimatedDeliveryDate: e.target.value || null })} />
            </div>
            <div className="modal-actions">
              <button className="btn btn-ghost" onClick={() => setShipModal(null)}>İptal</button>
              <button className="btn btn-primary" onClick={handleCreateShipping}>Kargoya Ver</button>
            </div>
          </div>
        </div>
      )}

      {/* ── Update Shipping Status Modal ── */}
      {statusModal && (
        <div className="modal-overlay" onClick={() => setStatusModal(null)}>
          <div className="modal" onClick={e => e.stopPropagation()} style={{ width: 400 }}>
            <h2>Kargo Durumu Güncelle</h2>
            <div className="form-group">
              <label>Yeni Durum</label>
              <select className="form-select" value={newShipStatus} onChange={e => setNewShipStatus(e.target.value)}>
                <option value="SHIPPED">Kargoya Verildi</option>
                <option value="IN_TRANSIT">Yolda</option>
                <option value="OUT_FOR_DELIVERY">Dağıtımda</option>
                <option value="DELIVERED">Teslim Edildi</option>
              </select>
            </div>
            <div className="modal-actions">
              <button className="btn btn-ghost" onClick={() => setStatusModal(null)}>İptal</button>
              <button className="btn btn-primary" onClick={handleUpdateStatus}>Güncelle</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
