import { useEffect, useState, useCallback, type FormEvent } from "react";
import {
  getPage, request,
  type StoreProductListDTO, type StoreProductDTO,
  type StoreCategoryDTO, type StoreBrandDTO,
  type StoreProductImageDTO, ApiError,
} from "../api";
import { Alert, TableSkeleton } from "../components/Feedback";

const STATUSES = ["DRAFT", "ACTIVE", "INACTIVE", "ARCHIVED"];

export default function ProductsPage() {
  // ── List state ──
  const [items, setItems] = useState<StoreProductListDTO[]>([]);
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const pageSize = 20;

  // ── Detail drawer ──
  const [detail, setDetail] = useState<StoreProductDTO | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  // ── Form modal ──
  const [modal, setModal] = useState<"create" | "edit" | null>(null);
  const [form, setForm] = useState<Partial<StoreProductDTO>>({});
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  // ── Dropdown data ──
  const [categories, setCategories] = useState<StoreCategoryDTO[]>([]);
  const [brands, setBrands] = useState<StoreBrandDTO[]>([]);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const res = await getPage<StoreProductListDTO>("/store/products", { page, size: pageSize, sort: "id,desc" });
      setItems(res.content);
      setTotal(res.totalElements);
    } catch (err: any) {
      setError(err.message || "Ürünler yüklenemedi");
      setItems([]);
    }
    setLoading(false);
  }, [page]);

  const loadDropdowns = useCallback(async () => {
    try {
      const [cats, brs] = await Promise.all([
        request<StoreCategoryDTO[]>("/store/categories"),
        request<StoreBrandDTO[]>("/store/brands"),
      ]);
      setCategories(cats);
      setBrands(brs);
    } catch { /* dropdown'lar opsiyonel */ }
  }, []);

  useEffect(() => { load(); loadDropdowns(); }, [load, loadDropdowns]);

  // ── Detail ──
  const openDetail = async (slug: string) => {
    setDetailLoading(true);
    try {
      setDetail(await request<StoreProductDTO>(`/store/products/${slug}`));
    } catch (err: any) {
      setError(err.message);
    }
    setDetailLoading(false);
  };

  // ── Create / Edit ──
  const openCreate = () => {
    setForm({ currency: "TRY", status: "DRAFT", isFeatured: false, isActive: true, stockQuantity: 0 });
    setFormErrors({});
    setModal("create");
  };

  const openEdit = async (slug: string) => {
    try {
      const full = await request<StoreProductDTO>(`/store/products/${slug}`);
      setForm(full);
      setFormErrors({});
      setModal("edit");
    } catch (err: any) {
      setError(err.message);
    }
  };

  const validate = (): boolean => {
    const e: Record<string, string> = {};
    if (!form.name?.trim()) e.name = "Ürün adı zorunludur";
    if (!form.slug?.trim()) e.slug = "Slug zorunludur";
    if (!form.price || form.price <= 0) e.price = "Geçerli bir fiyat girin";
    if (form.stockQuantity === undefined || form.stockQuantity < 0) e.stockQuantity = "Stok sıfırdan küçük olamaz";
    setFormErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSave = async (e: FormEvent) => {
    e.preventDefault();
    if (!validate()) return;
    setSaving(true);
    setError("");
    setSuccess("");
    try {
      if (modal === "edit" && form.id) {
        await request(`/store/admin/products/${form.id}`, { method: "PUT", json: form });
        setSuccess("Ürün güncellendi");
      } else {
        await request("/store/admin/products", { method: "POST", json: form });
        setSuccess("Ürün oluşturuldu");
      }
      setModal(null);
      load();
    } catch (err: any) {
      setError(err.message);
    }
    setSaving(false);
  };

  // ── Delete ──
  const handleDelete = async (id: number, name: string) => {
    if (!confirm(`"${name}" ürününü silmek istediğinize emin misiniz?`)) return;
    setError("");
    try {
      await request(`/store/admin/products/${id}`, { method: "DELETE" });
      setSuccess("Ürün silindi");
      load();
    } catch (err: any) {
      setError(err.message);
    }
  };

  // ── Toggle active ──
  const toggleActive = async (p: StoreProductDTO) => {
    try {
      await request(`/store/admin/products/${p.id}`, {
        method: "PUT",
        json: { ...p, isActive: !p.isActive },
      });
      setSuccess(p.isActive ? "Ürün pasifleştirildi" : "Ürün aktifleştirildi");
      load();
      if (detail?.id === p.id) setDetail({ ...detail, isActive: !detail.isActive });
    } catch (err: any) {
      setError(err.message);
    }
  };

  // ── Image preview ──
  const renderImages = (images: StoreProductImageDTO[]) => {
    if (!images?.length) return <span style={{ color: "var(--text2)" }}>Resim yok</span>;
    return (
      <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
        {images.map(img => (
          <div key={img.id} style={{ position: "relative", border: "1px solid var(--border)", borderRadius: 6, overflow: "hidden" }}>
            <img
              src={img.imageUrl}
              alt={img.altText || ""}
              style={{ width: 80, height: 80, objectFit: "cover", display: "block" }}
              onError={(e) => { (e.target as HTMLImageElement).style.display = "none"; }}
            />
            {img.isPrimary && <span className="badge badge-info" style={{ position: "absolute", top: 2, left: 2, fontSize: 9 }}>Ana</span>}
          </div>
        ))}
      </div>
    );
  };

  const renderField = (label: string, key: string) => {
    const err = formErrors[key];
    return (
      <div className="form-group">
        <label>{label} {err && <span style={{ color: "var(--danger)", fontSize: 11 }}>({err})</span>}</label>
        <input
          className="form-input"
          value={(form as any)[key] || ""}
          onChange={e => setForm({ ...form, [key]: e.target.value })}
          style={err ? { borderColor: "var(--danger)" } : undefined}
        />
      </div>
    );
  };

  const renderNumberField = (label: string, key: string, opts?: { step?: string; min?: number }) => {
    const err = formErrors[key];
    return (
      <div className="form-group">
        <label>{label} {err && <span style={{ color: "var(--danger)", fontSize: 11 }}>({err})</span>}</label>
        <input
          className="form-input"
          type="number"
          step={opts?.step || "1"}
          min={opts?.min}
          value={(form as any)[key] ?? ""}
          onChange={e => setForm({ ...form, [key]: e.target.value === "" ? null : Number(e.target.value) })}
          style={err ? { borderColor: "var(--danger)" } : undefined}
        />
      </div>
    );
  };

  return (
    <div>
      <div className="page-header">
        <h1>Ürünler ({total})</h1>
        <button className="btn btn-primary" onClick={openCreate}>+ Yeni Ürün</button>
      </div>

      {error && <Alert kind="error">{error}</Alert>}
      {success && <Alert kind="success">{success}</Alert>}

      {loading ? <TableSkeleton rows={6} /> : (
        <>
          <table className="data-table">
            <thead><tr>
              <th>Görsel</th><th>Ad</th><th>Kategori</th><th>Fiyat</th><th>Stok</th><th>Puan</th><th>Satış</th><th>Durum</th><th>İşlemler</th>
            </tr></thead>
            <tbody>
              {items.map(p => (
                <tr key={p.id} style={{ cursor: "pointer" }} onClick={() => openDetail(p.slug)}>
                  <td>
                    {p.primaryImageUrl ? (
                      <img src={p.primaryImageUrl} alt="" style={{ width: 40, height: 40, objectFit: "cover", borderRadius: 4 }} onError={e => { (e.target as HTMLImageElement).style.display = "none"; }} />
                    ) : <span style={{ color: "var(--text2)" }}>—</span>}
                  </td>
                  <td style={{ fontWeight: 500 }}>{p.name}</td>
                  <td>{p.categoryName || "—"}</td>
                  <td>
                    {p.discountedPrice ? (
                      <><span style={{ textDecoration: "line-through", color: "var(--text2)", marginRight: 4 }}>₺{p.price.toLocaleString("tr-TR")}</span><span style={{ color: "var(--danger)" }}>₺{p.discountedPrice.toLocaleString("tr-TR")}</span></>
                    ) : `₺${p.price.toLocaleString("tr-TR")}`}
                  </td>
                  <td>{p.inStock ? p.salesCount ?? "—" : <span style={{ color: "var(--danger)" }}>Tükendi</span>}</td>
                  <td>{p.ratingAverage ? `⭐ ${p.ratingAverage.toFixed(1)} (${p.reviewCount})` : "—"}</td>
                  <td>{p.salesCount ?? 0}</td>
                  <td><span className={`badge ${p.inStock ? "badge-success" : "badge-danger"}`}>{p.inStock ? "Stokta" : "Tükendi"}</span></td>
                  <td onClick={e => e.stopPropagation()} style={{ display: "flex", gap: 4 }}>
                    <button className="btn btn-ghost btn-sm" onClick={() => openEdit(p.slug)}>Düzenle</button>
                    <button className="btn btn-ghost btn-sm" style={{ color: "var(--danger)" }} onClick={() => handleDelete(p.id, p.name)}>Sil</button>
                  </td>
                </tr>
              ))}
              {items.length === 0 && <tr><td colSpan={9} className="empty">Kayıt bulunamadı</td></tr>}
            </tbody>
          </table>
          <div className="pagination">
            <button className="btn btn-ghost btn-sm" disabled={page === 0} onClick={() => setPage(p => p - 1)}>← Önceki</button>
            <span>Sayfa {page + 1} / {Math.ceil(total / pageSize) || 1}</span>
            <button className="btn btn-ghost btn-sm" disabled={(page + 1) * pageSize >= total} onClick={() => setPage(p => p + 1)}>Sonraki →</button>
          </div>
        </>
      )}

      {/* ── Detail Drawer ── */}
      {(detail || detailLoading) && (
        <div className="modal-overlay" onClick={() => setDetail(null)}>
          <div className="modal" onClick={e => e.stopPropagation()} style={{ width: 640 }}>
            {detailLoading ? <TableSkeleton rows={5} /> : detail && (
              <>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "start", marginBottom: 16 }}>
                  <h2 style={{ margin: 0 }}>{detail.name}</h2>
                  <button className="btn btn-ghost btn-sm" onClick={() => setDetail(null)}>✕</button>
                </div>

                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12, marginBottom: 16 }}>
                  <div><strong>Slug:</strong> {detail.slug}</div>
                  <div><strong>SKU:</strong> {detail.sku || "—"}</div>
                  <div><strong>Barkod:</strong> {detail.barcode || "—"}</div>
                  <div><strong>Durum:</strong> <span className={`badge ${detail.isActive ? "badge-success" : "badge-danger"}`}>{detail.status}</span></div>
                  <div><strong>Fiyat:</strong> ₺{detail.price?.toLocaleString("tr-TR")}</div>
                  {detail.discountedPrice && <div><strong>İndirimli:</strong> <span style={{ color: "var(--danger)" }}>₺{detail.discountedPrice.toLocaleString("tr-TR")}</span></div>}
                  <div><strong>Stok:</strong> {detail.stockQuantity}</div>
                  <div><strong>Düşük Stok Eşiği:</strong> {detail.lowStockThreshold ?? "—"}</div>
                  <div><strong>Kategori:</strong> {detail.categoryName || "—"}</div>
                  <div><strong>Marka:</strong> {detail.brandName || "—"}</div>
                  <div><strong>Satıcı:</strong> {detail.sellerName || "—"}</div>
                  <div><strong>Öne Çıkan:</strong> {detail.isFeatured ? "✅" : "❌"}</div>
                  <div><strong>Puan:</strong> {detail.ratingAverage ? `⭐ ${detail.ratingAverage.toFixed(1)} (${detail.reviewCount})` : "—"}</div>
                  <div><strong>Satış:</strong> {detail.salesCount ?? 0}</div>
                  {detail.weight && <div><strong>Ağırlık:</strong> {detail.weight} kg</div>}
                  {(detail.width || detail.height || detail.length) && (
                    <div><strong>Boyut:</strong> {detail.width}×{detail.height}×{detail.length}</div>
                  )}
                  <div><strong>Oluşturulma:</strong> {new Date(detail.createdAt).toLocaleString("tr-TR")}</div>
                  {detail.updatedAt && <div><strong>Güncellenme:</strong> {new Date(detail.updatedAt).toLocaleString("tr-TR")}</div>}
                </div>

                {detail.shortDescription && <p style={{ color: "var(--text2)", marginBottom: 12 }}>{detail.shortDescription}</p>}
                {detail.description && <div style={{ marginBottom: 16, padding: 12, background: "var(--bg)", borderRadius: 8, fontSize: 13, lineHeight: 1.6 }}>{detail.description}</div>}

                <div style={{ marginBottom: 16 }}>
                  <strong>Resimler</strong>
                  <div style={{ marginTop: 8 }}>{renderImages(detail.images)}</div>
                </div>

                {detail.variants?.length > 0 && (
                  <div style={{ marginBottom: 16 }}>
                    <strong>Varyantlar ({detail.variants.length})</strong>
                    <table className="data-table" style={{ marginTop: 8 }}>
                      <thead><tr><th>Ad</th><th>SKU</th><th>Fiyat</th><th>Stok</th><th>Aktif</th></tr></thead>
                      <tbody>
                        {detail.variants.map(v => (
                          <tr key={v.id}>
                            <td>{v.variantName}</td><td>{v.sku || "—"}</td>
                            <td>{v.price ? `₺${v.price.toLocaleString("tr-TR")}` : "—"}</td>
                            <td>{v.stockQuantity}</td><td>{v.isActive ? "✅" : "❌"}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}

                {detail.attributes?.length > 0 && (
                  <div style={{ marginBottom: 16 }}>
                    <strong>Özellikler</strong>
                    <div style={{ marginTop: 8, display: "flex", gap: 8, flexWrap: "wrap" }}>
                      {detail.attributes.map(a => (
                        <span key={a.id} className="badge badge-info">{a.attributeKey}: {a.attributeValue}</span>
                      ))}
                    </div>
                  </div>
                )}

                <div className="modal-actions">
                  <button className="btn btn-ghost" onClick={() => { setDetail(null); openEdit(detail.slug); }}>Düzenle</button>
                  <button className="btn btn-ghost" onClick={() => toggleActive(detail)}>
                    {detail.isActive ? "Pasifleştir" : "Aktifleştir"}
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}

      {/* ── Create / Edit Modal ── */}
      {modal && (
        <div className="modal-overlay" onClick={() => setModal(null)}>
          <div className="modal" onClick={e => e.stopPropagation()} style={{ width: 600 }}>
            <h2>{modal === "edit" ? "Ürün Düzenle" : "Yeni Ürün"}</h2>
            <form onSubmit={handleSave}>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" }}>
                {renderField("Ürün Adı *", "name")}
                {renderField("Slug *", "slug")}
                {renderNumberField("Fiyat (₺) *", "price", { step: "0.01", min: 0 })}
                {renderNumberField("İndirimli Fiyat", "discountedPrice", { step: "0.01", min: 0 })}
                {renderNumberField("Stok Miktarı *", "stockQuantity", { min: 0 })}
                {renderNumberField("Düşük Stok Eşiği", "lowStockThreshold", { min: 0 })}
                {renderField("SKU", "sku")}
                {renderField("Barkod", "barcode")}
              </div>

              <div className="form-group">
                <label>Kısa Açıklama</label>
                <input className="form-input" value={form.shortDescription || ""} onChange={e => setForm({ ...form, shortDescription: e.target.value })} />
              </div>
              <div className="form-group">
                <label>Detaylı Açıklama</label>
                <textarea className="form-textarea" value={form.description || ""} onChange={e => setForm({ ...form, description: e.target.value })} />
              </div>

              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" }}>
                <div className="form-group">
                  <label>Kategori</label>
                  <select className="form-select" value={form.categoryId || ""} onChange={e => setForm({ ...form, categoryId: e.target.value ? Number(e.target.value) : null })}>
                    <option value="">— Yok —</option>
                    {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label>Marka</label>
                  <select className="form-select" value={form.brandId || ""} onChange={e => setForm({ ...form, brandId: e.target.value ? Number(e.target.value) : null })}>
                    <option value="">— Yok —</option>
                    {brands.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label>Durum</label>
                  <select className="form-select" value={form.status || "DRAFT"} onChange={e => setForm({ ...form, status: e.target.value })}>
                    {STATUSES.map(s => <option key={s} value={s}>{s}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label>Para Birimi</label>
                  <select className="form-select" value={form.currency || "TRY"} onChange={e => setForm({ ...form, currency: e.target.value })}>
                    <option value="TRY">TRY</option><option value="USD">USD</option><option value="EUR">EUR</option>
                  </select>
                </div>
              </div>

              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr 1fr", gap: "0 16px" }}>
                {renderNumberField("Ağırlık (kg)", "weight", { step: "0.001", min: 0 })}
                {renderNumberField("Genişlik", "width", { step: "0.01", min: 0 })}
                {renderNumberField("Yükseklik", "height", { step: "0.01", min: 0 })}
                {renderNumberField("Uzunluk", "length", { step: "0.01", min: 0 })}
              </div>

              <div style={{ display: "flex", gap: 24, marginBottom: 16 }}>
                <label style={{ display: "flex", alignItems: "center", gap: 6, cursor: "pointer" }}>
                  <input type="checkbox" checked={form.isFeatured || false} onChange={e => setForm({ ...form, isFeatured: e.target.checked })} />
                  Öne Çıkan
                </label>
                <label style={{ display: "flex", alignItems: "center", gap: 6, cursor: "pointer" }}>
                  <input type="checkbox" checked={form.isActive !== false} onChange={e => setForm({ ...form, isActive: e.target.checked })} />
                  Aktif
                </label>
              </div>

              {/* Image preview in edit mode */}
              {modal === "edit" && form.images && form.images.length > 0 && (
                <div style={{ marginBottom: 16 }}>
                  <label>Mevcut Resimler</label>
                  <div style={{ marginTop: 8 }}>{renderImages(form.images)}</div>
                </div>
              )}

              <div className="modal-actions">
                <button type="button" className="btn btn-ghost" onClick={() => setModal(null)}>İptal</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? "Kaydediliyor..." : "Kaydet"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
