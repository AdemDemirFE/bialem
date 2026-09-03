import { useEffect, useState, useCallback } from "react";
import { getPage, request, type StoreOrderDTO, type StoreOrderDetailDTO } from "../api";

const ORDER_STATUSES = [
  "PENDING_PAYMENT", "PAID", "WAITING_ADMIN_APPROVAL", "APPROVED",
  "PREPARING", "READY_FOR_SHIPPING", "SHIPPED", "IN_TRANSIT",
  "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED", "PAYMENT_FAILED",
  "RETURN_REQUESTED", "RETURNED", "REFUNDED", "PARTIALLY_REFUNDED",
];

const STATUS_LABELS: Record<string, string> = {
  PENDING_PAYMENT: "Ödeme Bekliyor", PAID: "Ödendi", WAITING_ADMIN_APPROVAL: "Onay Bekliyor",
  APPROVED: "Onaylandı", PREPARING: "Hazırlanıyor", READY_FOR_SHIPPING: "Kargoya Hazır",
  SHIPPED: "Kargoya Verildi", IN_TRANSIT: "Yolda", OUT_FOR_DELIVERY: "Dağıtımda",
  DELIVERED: "Teslim Edildi", CANCELLED: "İptal", PAYMENT_FAILED: "Ödeme Başarısız",
  RETURN_REQUESTED: "İade İstendi", RETURNED: "İade Edildi", REFUNDED: "İade Ödemesi Yapıldı",
  PARTIALLY_REFUNDED: "Kısmi İade",
};

const STATUS_BADGE: Record<string, string> = {
  PENDING_PAYMENT: "badge-warning", PAID: "badge-info", WAITING_ADMIN_APPROVAL: "badge-warning",
  APPROVED: "badge-info", PREPARING: "badge-info", READY_FOR_SHIPPING: "badge-info",
  SHIPPED: "badge-success", IN_TRANSIT: "badge-success", OUT_FOR_DELIVERY: "badge-success",
  DELIVERED: "badge-success", CANCELLED: "badge-danger", PAYMENT_FAILED: "badge-danger",
  RETURN_REQUESTED: "badge-warning", RETURNED: "badge-warning", REFUNDED: "badge-info",
  PARTIALLY_REFUNDED: "badge-info",
};

export default function OrdersPage() {
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

  const action = async (id: number, verb: string, label: string) => {
    if (!confirm(`${label} işlemi yapılsın mı?`)) return;
    setError("");
    setSuccess("");
    try {
      await request(`/store/orders/admin/${id}/${verb}`, { method: "POST" });
      setSuccess(label + " başarılı");
      load();
      if (detail?.id === id) openDetail(id);
    } catch (err: any) {
      setError(err.message);
    }
  };

  const badge = (s: string) => (
    <span className={`badge ${STATUS_BADGE[s] || "badge-info"}`}>
      {STATUS_LABELS[s] || s}
    </span>
  );

  const getNextAction = (status: string): { verb: string; label: string } | null => {
    const map: Record<string, { verb: string; label: string }> = {
      PENDING_PAYMENT: { verb: "approve", label: "Onayla" },
      PAID: { verb: "approve", label: "Onayla" },
      WAITING_ADMIN_APPROVAL: { verb: "approve", label: "Onayla" },
      APPROVED: { verb: "preparing", label: "Hazırlanıyor" },
      PREPARING: { verb: "ready-for-shipping", label: "Kargoya Hazır" },
      READY_FOR_SHIPPING: { verb: "ready-for-shipping", label: "Kargoya Ver" },
    };
    return map[status] || null;
  };

  return (
    <div>
      <div className="page-header">
        <h1>Siparişler ({total})</h1>
        <select className="form-select" style={{ width: 200 }} value={statusFilter} onChange={e => { setStatusFilter(e.target.value); setPage(0); }}>
          <option value="">Tüm Durumlar</option>
          {ORDER_STATUSES.map(s => (
            <option key={s} value={s}>{STATUS_LABELS[s] || s}</option>
          ))}
        </select>
      </div>

      {error && <div className="login-error" style={{ marginBottom: 12 }}>{error}</div>}
      {success && <div style={{ background: "rgba(34,197,94,0.1)", border: "1px solid var(--success)", color: "var(--success)", padding: "8px 12px", borderRadius: "var(--radius)", marginBottom: 12 }}>{success}</div>}

      {loading ? <div className="loading">Yükleniyor...</div> : (
        <>
          <table className="data-table">
            <thead><tr>
              <th>Sipariş No</th><th>Tutar</th><th>Ödeme</th><th>Sipariş</th><th>Kargo</th><th>Tarih</th><th>İşlemler</th>
            </tr></thead>
            <tbody>
              {items.map(o => {
                const next = getNextAction(o.orderStatus);
                const canCancel = !["DELIVERED", "CANCELLED"].includes(o.orderStatus);
                return (
                  <tr key={o.id} style={{ cursor: "pointer" }} onClick={() => openDetail(o.id)}>
                    <td style={{ fontWeight: 500 }}>{o.orderNumber}</td>
                    <td>₺{o.totalAmount?.toLocaleString("tr-TR")}</td>
                    <td>{badge(o.paymentStatus)}</td>
                    <td>{badge(o.orderStatus)}</td>
                    <td>{badge(o.shippingStatus)}</td>
                    <td>{new Date(o.createdAt).toLocaleDateString("tr-TR")}</td>
                    <td onClick={e => e.stopPropagation()} style={{ display: "flex", gap: 4, flexWrap: "wrap" }}>
                      {next && (
                        <button className="btn btn-ghost btn-sm" onClick={() => action(o.id, next.verb, next.label)}>
                          {next.label}
                        </button>
                      )}
                      {canCancel && (
                        <button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => action(o.id, "cancel", "İptal")}>
                          İptal
                        </button>
                      )}
                    </td>
                  </tr>
                );
              })}
              {items.length === 0 && <tr><td colSpan={7} className="empty">Kayıt bulunamadı</td></tr>}
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
          <div className="modal" onClick={e => e.stopPropagation()} style={{ width: 700, maxWidth: "95vw" }}>
            {detailLoading ? <div className="loading">Yükleniyor...</div> : detail && (
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

                {/* Status cards */}
                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12, marginBottom: 20 }}>
                  <div className="stat-card"><div className="label">Ödeme Durumu</div><div>{badge(detail.paymentStatus)}</div></div>
                  <div className="stat-card"><div className="label">Sipariş Durumu</div><div>{badge(detail.orderStatus)}</div></div>
                  <div className="stat-card"><div className="label">Kargo Durumu</div><div>{badge(detail.shippingStatus)}</div></div>
                </div>

                {/* Price breakdown */}
                <div style={{ background: "var(--surface2)", borderRadius: 8, padding: 16, marginBottom: 20 }}>
                  <strong>Fiyat Detayları</strong>
                  <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8, marginTop: 8, fontSize: 13 }}>
                    <div>Ara Toplam: <strong>₺{detail.subtotal?.toLocaleString("tr-TR")}</strong></div>
                    <div>İndirim: <strong style={{ color: "var(--danger)" }}>-₺{detail.discountAmount?.toLocaleString("tr-TR") || "0"}</strong></div>
                    <div>Kargo: <strong>₺{detail.shippingAmount?.toLocaleString("tr-TR") || "0"}</strong></div>
                    <div style={{ fontSize: 16, fontWeight: 700 }}>Toplam: <strong>₺{detail.totalAmount?.toLocaleString("tr-TR")}</strong></div>
                  </div>
                  {detail.couponCode && <div style={{ marginTop: 8, fontSize: 12, color: "var(--warning)" }}>Kupon: {detail.couponCode}</div>}
                </div>

                {/* Items */}
                {detail.items?.length > 0 && (
                  <div style={{ marginBottom: 20 }}>
                    <strong>Ürünler ({detail.items.length})</strong>
                    <table className="data-table" style={{ marginTop: 8 }}>
                      <thead><tr><th>Ürün</th><th>SKU</th><th>Varyant</th><th>Adet</th><th>Birim Fiyat</th><th>Toplam</th></tr></thead>
                      <tbody>
                        {detail.items.map(item => (
                          <tr key={item.id}>
                            <td>
                              <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                                {item.productImage && (
                                  <img src={item.productImage} alt="" style={{ width: 36, height: 36, objectFit: "cover", borderRadius: 4 }} onError={e => { (e.target as HTMLImageElement).style.display = "none"; }} />
                                )}
                                <span>{item.productName || `Ürün #${item.productId}`}</span>
                              </div>
                            </td>
                            <td>{item.productSku || "—"}</td>
                            <td>{item.variantName || "—"}</td>
                            <td>{item.quantity}</td>
                            <td>₺{item.unitPrice?.toLocaleString("tr-TR")}</td>
                            <td style={{ fontWeight: 500 }}>₺{item.totalPrice?.toLocaleString("tr-TR")}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}

                {/* Addresses */}
                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16, marginBottom: 20 }}>
                  {detail.shippingAddress && (
                    <div style={{ background: "var(--surface2)", borderRadius: 8, padding: 12 }}>
                      <strong style={{ fontSize: 12, color: "var(--text2)" }}>KARGO ADRESİ</strong>
                      <div style={{ marginTop: 6, fontSize: 13, lineHeight: 1.6 }}>
                        <div>{detail.shippingAddress.firstName} {detail.shippingAddress.lastName}</div>
                        <div>{detail.shippingAddress.addressLine}</div>
                        {detail.shippingAddress.neighborhood && <div>{detail.shippingAddress.neighborhood}</div>}
                        <div>{detail.shippingAddress.district}/{detail.shippingAddress.city}</div>
                        {detail.shippingAddress.postalCode && <div>{detail.shippingAddress.postalCode}</div>}
                        {detail.shippingAddress.phone && <div>📞 {detail.shippingAddress.phone}</div>}
                        {detail.shippingAddress.note && <div style={{ color: "var(--text2)" }}>Not: {detail.shippingAddress.note}</div>}
                      </div>
                    </div>
                  )}
                  {detail.billingAddress && (
                    <div style={{ background: "var(--surface2)", borderRadius: 8, padding: 12 }}>
                      <strong style={{ fontSize: 12, color: "var(--text2)" }}>FATURA ADRESİ</strong>
                      <div style={{ marginTop: 6, fontSize: 13, lineHeight: 1.6 }}>
                        <div>{detail.billingAddress.firstName} {detail.billingAddress.lastName}</div>
                        <div>{detail.billingAddress.addressLine}</div>
                        <div>{detail.billingAddress.district}/{detail.billingAddress.city}</div>
                      </div>
                    </div>
                  )}
                </div>

                {/* Payment */}
                {detail.payment && (
                  <div style={{ background: "var(--surface2)", borderRadius: 8, padding: 12, marginBottom: 20 }}>
                    <strong style={{ fontSize: 12, color: "var(--text2)" }}>ÖDEME BİLGİLERİ</strong>
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8, marginTop: 8, fontSize: 13 }}>
                      <div>Sağlayıcı: {detail.payment.provider}</div>
                      <div>Yöntem: {detail.payment.paymentMethod || "—"}</div>
                      <div>İşlem No: {detail.payment.transactionId || "—"}</div>
                      <div>Durum: {badge(detail.payment.status)}</div>
                      <div>Tutar: ₺{detail.payment.amount?.toLocaleString("tr-TR")}</div>
                      {detail.payment.paidAt && <div>Ödeme Tarihi: {new Date(detail.payment.paidAt).toLocaleString("tr-TR")}</div>}
                      {detail.payment.failureReason && <div style={{ color: "var(--danger)" }}>Hata: {detail.payment.failureReason}</div>}
                    </div>
                  </div>
                )}

                {/* Shipping */}
                {detail.shipping && (
                  <div style={{ background: "var(--surface2)", borderRadius: 8, padding: 12, marginBottom: 20 }}>
                    <strong style={{ fontSize: 12, color: "var(--text2)" }}>KARGO BİLGİLERİ</strong>
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8, marginTop: 8, fontSize: 13 }}>
                      <div>Kurye: {detail.shipping.carrier || "—"}</div>
                      <div>Takip No: {detail.shipping.trackingNumber || "—"}</div>
                      <div>Durum: {detail.shipping.shippingStatus ? badge(detail.shipping.shippingStatus) : "—"}</div>
                      {detail.shipping.shippedAt && <div>Gönderim: {new Date(detail.shipping.shippedAt).toLocaleString("tr-TR")}</div>}
                      {detail.shipping.estimatedDeliveryDate && <div>Tahmini Teslimat: {detail.shipping.estimatedDeliveryDate}</div>}
                      {detail.shipping.deliveredAt && <div>Teslimat: {new Date(detail.shipping.deliveredAt).toLocaleString("tr-TR")}</div>}
                    </div>
                  </div>
                )}

                {/* Customer note */}
                {detail.customerNote && (
                  <div style={{ background: "var(--surface2)", borderRadius: 8, padding: 12, marginBottom: 20 }}>
                    <strong style={{ fontSize: 12, color: "var(--text2)" }}>MÜŞTERİ NOTU</strong>
                    <div style={{ marginTop: 6, fontSize: 13 }}>{detail.customerNote}</div>
                  </div>
                )}

                {/* Status history */}
                {detail.statusHistory?.length > 0 && (
                  <div style={{ marginBottom: 20 }}>
                    <strong>Durum Geçmişi</strong>
                    <div style={{ marginTop: 8 }}>
                      {detail.statusHistory.map(h => (
                        <div key={h.id} style={{ display: "flex", gap: 12, alignItems: "center", padding: "6px 0", borderBottom: "1px solid var(--border)", fontSize: 13 }}>
                          <span style={{ color: "var(--text2)", minWidth: 140 }}>{new Date(h.createdAt).toLocaleString("tr-TR")}</span>
                          {h.oldStatus && <span>{badge(h.oldStatus)}</span>}
                          {h.oldStatus && <span>→</span>}
                          <span>{badge(h.newStatus)}</span>
                          {h.changedBy && <span style={{ color: "var(--text2)" }}>by {h.changedBy}</span>}
                          {h.note && <span style={{ color: "var(--text2)", fontStyle: "italic" }}>({h.note})</span>}
                        </div>
                      ))}
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
    </div>
  );
}
