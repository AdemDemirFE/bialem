import Link from "next/link";
import { revalidatePath } from "next/cache";
import type { CSSProperties } from "react";
import { getAdminApi } from "../../../src/lib/admin-api";
import { AdminSessionControls } from "../session-controls";

export const dynamic = "force-dynamic";

type Venue = {
  id: string;
  name: string;
  category: string;
  city: string;
  address: string;
  is_active: boolean;
  is_featured: boolean;
};

type Offer = {
  id: string;
  venue_id: string;
  title: string;
  discount_percent: number;
  valid_until: string | null;
  is_active: boolean;
  partner_venues: { name: string } | null;
};

function slugify(value: string) {
  return value
    .toLocaleLowerCase("tr-TR")
    .replaceAll("ı", "i")
    .replaceAll("ğ", "g")
    .replaceAll("ü", "u")
    .replaceAll("ş", "s")
    .replaceAll("ö", "o")
    .replaceAll("ç", "c")
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "");
}

function optionalNumber(formData: FormData, name: string) {
  const value = String(formData.get(name) ?? "").trim();
  return value ? Number(value) : null;
}

async function createVenue(formData: FormData) {
  "use server";

  const name = String(formData.get("name") ?? "").trim();
  const address = String(formData.get("address") ?? "").trim();
  if (!name || !address) return;

  const admin = await getAdminApi();
  await admin.from("partner_venues").insert({
    name,
    slug: slugify(String(formData.get("slug") ?? "").trim() || name),
    description: String(formData.get("description") ?? "").trim() || null,
    category: String(formData.get("category") ?? "other"),
    logo_url: String(formData.get("logo_url") ?? "").trim() || null,
    cover_image_url: String(formData.get("cover_image_url") ?? "").trim() || null,
    address,
    city: String(formData.get("city") ?? "Ankara").trim() || "Ankara",
    latitude: optionalNumber(formData, "latitude"),
    longitude: optionalNumber(formData, "longitude"),
    phone: String(formData.get("phone") ?? "").trim() || null,
    website_url: String(formData.get("website_url") ?? "").trim() || null,
    instagram_url: String(formData.get("instagram_url") ?? "").trim() || null,
    is_featured: formData.get("is_featured") === "on"
  });

  revalidatePath("/admin/advantages");
}

async function createOffer(formData: FormData) {
  "use server";

  const venueId = String(formData.get("venue_id") ?? "");
  const title = String(formData.get("title") ?? "").trim();
  const discountPercent = Number(formData.get("discount_percent") ?? 0);
  if (!venueId || !title || discountPercent <= 0 || discountPercent > 100) return;

  const validFromInput = String(formData.get("valid_from") ?? "").trim();
  const validUntilInput = String(formData.get("valid_until") ?? "").trim();
  const admin = await getAdminApi();
  await admin.from("partner_offers").insert({
    venue_id: venueId,
    title,
    description: String(formData.get("description") ?? "").trim() || null,
    discount_percent: discountPercent,
    minimum_spend: optionalNumber(formData, "minimum_spend"),
    maximum_discount: optionalNumber(formData, "maximum_discount"),
    valid_from: validFromInput ? new Date(validFromInput).toISOString() : new Date().toISOString(),
    valid_until: validUntilInput ? new Date(validUntilInput).toISOString() : null,
    per_user_limit: optionalNumber(formData, "per_user_limit"),
    terms: String(formData.get("terms") ?? "").trim() || null
  });

  revalidatePath("/admin/advantages");
}

async function assignVenueStaff(formData: FormData) {
  "use server";

  const venueId = String(formData.get("venue_id") ?? "");
  const email = String(formData.get("email") ?? "").trim().toLowerCase();
  if (!venueId || !email) return;

  const admin = await getAdminApi();
  const { data: profile } = await admin.from("profiles").select("id").eq("email", email).maybeSingle();
  if (!profile) return;

  await admin.from("partner_venue_staff").upsert(
    { venue_id: venueId, user_id: profile.id, is_active: true },
    { onConflict: "venue_id,user_id" }
  );
  revalidatePath("/admin/advantages");
}

async function toggleVenue(formData: FormData) {
  "use server";

  const id = String(formData.get("id") ?? "");
  const isActive = String(formData.get("is_active") ?? "") === "true";
  if (!id) return;

  const admin = await getAdminApi();
  await admin.from("partner_venues").update({ is_active: !isActive }).eq("id", id);
  revalidatePath("/admin/advantages");
}

async function toggleOffer(formData: FormData) {
  "use server";

  const id = String(formData.get("id") ?? "");
  const isActive = String(formData.get("is_active") ?? "") === "true";
  if (!id) return;

  const admin = await getAdminApi();
  await admin.from("partner_offers").update({ is_active: !isActive }).eq("id", id);
  revalidatePath("/admin/advantages");
}

async function redeemCode(formData: FormData) {
  "use server";

  const rawCode = String(formData.get("code") ?? "").trim();
  if (!rawCode) return;

  const tokenOrCode = rawCode.includes("token=")
    ? rawCode.split("token=")[1]?.split(/[&#]/)[0] ?? rawCode
    : rawCode;
  const orderAmount = optionalNumber(formData, "order_amount");

  const admin = await getAdminApi();
  await admin.rpc("redeem_partner_offer", {
    target_token_or_code: tokenOrCode,
    target_order_amount: orderAmount
  });
  revalidatePath("/admin/advantages");
}

export default async function AdvantagesAdminPage() {
  let venues: Venue[] = [];
  let offers: Offer[] = [];
  let error: string | null = null;

  try {
    const admin = await getAdminApi();
    const [venuesResult, offersResult] = await Promise.all([
      admin
        .from("partner_venues")
        .select("id, name, category, city, address, is_active, is_featured")
        .order("created_at", { ascending: false }),
      admin
        .from("partner_offers")
        .select("id, venue_id, title, discount_percent, valid_until, is_active, partner_venues(name)")
        .order("created_at", { ascending: false })
    ]);

    if (venuesResult.error) throw venuesResult.error;
    if (offersResult.error) throw offersResult.error;
    venues = (venuesResult.data ?? []) as Venue[];
    offers = (offersResult.data ?? []) as unknown as Offer[];
  } catch (loadError) {
    error = loadError instanceof Error ? loadError.message : "Avantaj verileri yüklenemedi.";
  }

  return (
    <main style={styles.page}>
      <AdminSessionControls />
      <header style={styles.hero}>
        <div>
          <p style={styles.kicker}>BİALEM AVANTAJ</p>
          <h1 style={styles.title}>Anlaşmalı kurum ve indirim yönetimi</h1>
          <p style={styles.lead}>
            Kurumları, kampanyaları ve kasa doğrulama akışını buradan yönetin.
          </p>
        </div>
        <Link href="/admin" style={styles.backLink}>Admin ana sayfa</Link>
      </header>

      {error ? <div style={styles.error}>{error}</div> : null}

      <section style={styles.stats}>
        <article style={styles.stat}><strong>{venues.length}</strong><span>Kurum</span></article>
        <article style={styles.stat}><strong>{offers.length}</strong><span>Kampanya</span></article>
        <article style={styles.stat}><strong>{offers.filter((offer) => offer.is_active).length}</strong><span>Aktif avantaj</span></article>
      </section>

      <section style={styles.twoColumn}>
        <article style={styles.panel}>
          <p style={styles.kicker}>YENİ KURUM</p>
          <h2 style={styles.panelTitle}>Anlaşmalı kurum ekle</h2>
          <form action={createVenue} style={styles.form}>
            <input name="name" required placeholder="Kurum adı" style={styles.input} />
            <input name="slug" placeholder="kisa-adres (boşsa otomatik)" style={styles.input} />
            <select name="category" defaultValue="cafe" style={styles.input}>
              <option value="cafe">Kafe</option>
              <option value="restaurant">Restoran</option>
              <option value="sports">Spor</option>
              <option value="education">Eğitim</option>
              <option value="entertainment">Eğlence</option>
              <option value="beauty">Güzellik</option>
              <option value="health">Sağlık</option>
              <option value="shopping">Alışveriş</option>
              <option value="other">Diğer</option>
            </select>
            <textarea name="description" placeholder="Kısa tanıtım" style={styles.textarea} />
            <input name="address" required placeholder="Açık adres" style={styles.input} />
            <input name="city" defaultValue="Ankara" placeholder="Şehir" style={styles.input} />
            <div style={styles.row}>
              <input name="latitude" type="number" step="any" placeholder="Enlem" style={styles.input} />
              <input name="longitude" type="number" step="any" placeholder="Boylam" style={styles.input} />
            </div>
            <input name="logo_url" type="url" placeholder="Logo URL" style={styles.input} />
            <input name="cover_image_url" type="url" placeholder="Kapak görseli URL" style={styles.input} />
            <input name="phone" placeholder="Telefon" style={styles.input} />
            <input name="website_url" type="url" placeholder="Web sitesi" style={styles.input} />
            <input name="instagram_url" type="url" placeholder="Instagram adresi" style={styles.input} />
            <label style={styles.checkbox}><input name="is_featured" type="checkbox" /> Öne çıkan kurum</label>
            <button type="submit" style={styles.primaryButton}>Kurumu ekle</button>
          </form>
        </article>

        <article style={styles.panel}>
          <p style={styles.kicker}>YENİ KAMPANYA</p>
          <h2 style={styles.panelTitle}>İndirim tanımla</h2>
          <form action={createOffer} style={styles.form}>
            <select name="venue_id" required defaultValue="" style={styles.input}>
              <option value="" disabled>Kurum seç</option>
              {venues.map((venue) => <option key={venue.id} value={venue.id}>{venue.name}</option>)}
            </select>
            <input name="title" required placeholder="Örn: Tüm menüde %15 indirim" style={styles.input} />
            <input name="discount_percent" required type="number" min="1" max="100" step="0.01" placeholder="İndirim yüzdesi" style={styles.input} />
            <div style={styles.row}>
              <input name="minimum_spend" type="number" min="0" step="0.01" placeholder="Alt limit (₺)" style={styles.input} />
              <input name="maximum_discount" type="number" min="0" step="0.01" placeholder="Maks. indirim (₺)" style={styles.input} />
            </div>
            <input name="per_user_limit" type="number" min="1" placeholder="Kişi başı toplam kullanım" style={styles.input} />
            <label style={styles.label}>Başlangıç</label>
            <input name="valid_from" type="datetime-local" style={styles.input} />
            <label style={styles.label}>Bitiş</label>
            <input name="valid_until" type="datetime-local" style={styles.input} />
            <textarea name="description" placeholder="Kampanya açıklaması" style={styles.textarea} />
            <textarea name="terms" placeholder="Koşullar ve istisnalar" style={styles.textarea} />
            <button type="submit" style={styles.primaryButton}>Kampanyayı yayınla</button>
          </form>
        </article>
      </section>

      <section style={styles.twoColumn}>
        <article style={styles.panel}>
          <p style={styles.kicker}>KASA DOĞRULAMA</p>
          <h2 style={styles.panelTitle}>Avantaj kodunu kullan</h2>
          <p style={styles.help}>Üyenin ekranındaki 8 karakterli kodu girin. Kod yalnızca 60 saniye geçerlidir.</p>
          <form action={redeemCode} style={styles.form}>
            <input name="code" required placeholder="Örn: A1B2C3D4" autoCapitalize="characters" style={styles.codeInput} />
            <input name="order_amount" type="number" min="0" step="0.01" placeholder="Sepet tutarı (isteğe bağlı)" style={styles.input} />
            <button type="submit" style={styles.verifyButton}>Kodu doğrula ve kullan</button>
          </form>
        </article>

        <article style={styles.panel}>
          <p style={styles.kicker}>KURUM PERSONELİ</p>
          <h2 style={styles.panelTitle}>Doğrulama yetkisi ver</h2>
          <p style={styles.help}>Personelin önce Bialem uygulamasında kayıtlı olması gerekir.</p>
          <form action={assignVenueStaff} style={styles.form}>
            <select name="venue_id" required defaultValue="" style={styles.input}>
              <option value="" disabled>Kurum seç</option>
              {venues.map((venue) => <option key={venue.id} value={venue.id}>{venue.name}</option>)}
            </select>
            <input name="email" type="email" required placeholder="Personel üye e-postası" style={styles.input} />
            <button type="submit" style={styles.primaryButton}>Personeli yetkilendir</button>
          </form>
        </article>
      </section>

      <section style={styles.panel}>
        <p style={styles.kicker}>KURUMLAR</p>
        <h2 style={styles.panelTitle}>Yayın durumu</h2>
        <div style={styles.list}>
          {venues.length === 0 ? <p style={styles.help}>Henüz kurum eklenmedi.</p> : venues.map((venue) => (
            <article key={venue.id} style={styles.listItem}>
              <div>
                <strong>{venue.name}</strong>
                <p style={styles.help}>{venue.category} · {venue.city} · {venue.address}</p>
              </div>
              <form action={toggleVenue}>
                <input type="hidden" name="id" value={venue.id} />
                <input type="hidden" name="is_active" value={String(venue.is_active)} />
                <button type="submit" style={venue.is_active ? styles.disableButton : styles.enableButton}>
                  {venue.is_active ? "Pasife al" : "Aktifleştir"}
                </button>
              </form>
            </article>
          ))}
        </div>
      </section>

      <section style={styles.panel}>
        <p style={styles.kicker}>KAMPANYALAR</p>
        <h2 style={styles.panelTitle}>İndirimler</h2>
        <div style={styles.list}>
          {offers.length === 0 ? <p style={styles.help}>Henüz kampanya eklenmedi.</p> : offers.map((offer) => (
            <article key={offer.id} style={styles.listItem}>
              <div>
                <strong>%{offer.discount_percent} · {offer.title}</strong>
                <p style={styles.help}>
                  {offer.partner_venues?.name ?? "Kurum"} · {offer.valid_until ? new Date(offer.valid_until).toLocaleDateString("tr-TR") : "Süresiz"}
                </p>
              </div>
              <form action={toggleOffer}>
                <input type="hidden" name="id" value={offer.id} />
                <input type="hidden" name="is_active" value={String(offer.is_active)} />
                <button type="submit" style={offer.is_active ? styles.disableButton : styles.enableButton}>
                  {offer.is_active ? "Durdur" : "Yayınla"}
                </button>
              </form>
            </article>
          ))}
        </div>
      </section>
    </main>
  );
}

const styles: Record<string, CSSProperties> = {
  page: { minHeight: "100vh", padding: "42px clamp(18px, 5vw, 72px) 80px", background: "#f4f7ff", color: "#071b44" },
  hero: { display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: 24, marginBottom: 28 },
  kicker: { margin: "0 0 8px", color: "#7b35ff", fontSize: 12, fontWeight: 900, letterSpacing: "0.16em" },
  title: { maxWidth: 760, margin: 0, fontSize: "clamp(34px, 5vw, 64px)", lineHeight: 0.98, letterSpacing: "-0.04em" },
  lead: { maxWidth: 700, color: "#50608c", fontSize: 17, lineHeight: 1.6 },
  backLink: { padding: "12px 18px", borderRadius: 999, background: "#071b44", color: "#fff", textDecoration: "none", fontWeight: 800 },
  error: { marginBottom: 20, padding: 16, borderRadius: 16, background: "#ffe5ec", color: "#9c2447", fontWeight: 700 },
  stats: { display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(150px, 1fr))", gap: 14, marginBottom: 22 },
  stat: { display: "grid", gap: 4, padding: 22, borderRadius: 22, background: "#071b44", color: "#fff" },
  twoColumn: { display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(min(100%, 390px), 1fr))", gap: 20, marginBottom: 20 },
  panel: { marginBottom: 20, padding: "clamp(20px, 3vw, 34px)", border: "1px solid #d7e2fa", borderRadius: 28, background: "#fff", boxShadow: "0 18px 55px rgba(7,27,68,.06)" },
  panelTitle: { margin: "0 0 18px", fontSize: 27, letterSpacing: "-0.025em" },
  form: { display: "grid", gap: 11 },
  row: { display: "grid", gridTemplateColumns: "repeat(2, minmax(0, 1fr))", gap: 11 },
  input: { width: "100%", minWidth: 0, boxSizing: "border-box", padding: "13px 14px", border: "1px solid #cdd9f2", borderRadius: 14, background: "#f9fbff", color: "#071b44", fontSize: 15 },
  codeInput: { width: "100%", boxSizing: "border-box", padding: "17px 18px", border: "2px solid #7b35ff", borderRadius: 16, color: "#071b44", fontSize: 26, fontWeight: 900, letterSpacing: "0.2em", textTransform: "uppercase" },
  textarea: { width: "100%", minHeight: 90, boxSizing: "border-box", padding: "13px 14px", border: "1px solid #cdd9f2", borderRadius: 14, background: "#f9fbff", color: "#071b44", fontSize: 15, resize: "vertical" },
  label: { color: "#50608c", fontSize: 12, fontWeight: 800 },
  checkbox: { display: "flex", alignItems: "center", gap: 8, color: "#50608c", fontWeight: 700 },
  primaryButton: { padding: "14px 17px", border: 0, borderRadius: 14, background: "#ffa20f", color: "#071b44", fontWeight: 900, cursor: "pointer" },
  verifyButton: { padding: "15px 17px", border: 0, borderRadius: 14, background: "#7b35ff", color: "#fff", fontWeight: 900, cursor: "pointer" },
  disableButton: { padding: "10px 13px", border: 0, borderRadius: 12, background: "#ffe5ec", color: "#a72d50", fontWeight: 800, cursor: "pointer" },
  enableButton: { padding: "10px 13px", border: 0, borderRadius: 12, background: "#def8ee", color: "#087653", fontWeight: 800, cursor: "pointer" },
  help: { margin: "5px 0", color: "#607098", fontSize: 13, lineHeight: 1.5 },
  list: { display: "grid", gap: 10 },
  listItem: { display: "flex", justifyContent: "space-between", alignItems: "center", gap: 18, padding: 16, borderRadius: 16, background: "#f4f7ff" }
};
