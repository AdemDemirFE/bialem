import { revalidatePath } from "next/cache";
import Link from "next/link";
import { getAdminApi } from "../../src/lib/admin-api";
import { CoverImageInput } from "./cover-image-input";
import { AdminSessionControls } from "./session-controls";

export const dynamic = "force-dynamic";

type EventRecord = {
  id: string;
  title: string;
  description: string | null;
  starts_at: string;
  location_name: string | null;
  status: string;
  group_moderation_status: string;
  platform_moderation_status: string;
  rejection_reason: string | null;
  communities: { name: string; slug: string } | null;
  profiles: { display_name: string; email: string } | null;
};

type ReportRecord = {
  id: string;
  reporter_id: string;
  target_type: string;
  target_id: string;
  reason: string;
  details: string | null;
  status: string;
  created_at: string;
};

type ModerationComment = {
  id: string;
  target_type: string;
  target_id: string;
  author_id: string;
  body: string;
  moderation_status: string;
  created_at: string;
};

type ModerationPost = {
  id: string;
  author_id: string;
  community_id: string | null;
  body: string | null;
  moderation_status: string;
  created_at: string;
};

type RiskProfile = {
  id: string;
  email: string;
  display_name: string;
  username: string;
  status: string;
  created_at: string;
};

type MainCommunity = {
  id: string;
  name: string;
  slug: string;
  community_type: "category_hub" | "partner_hub";
  partner_trust_level: "new" | "verified" | "trusted";
  is_verified_partner: boolean;
};

const communityCoverTypes = {
  "image/jpeg": "jpg",
  "image/png": "png",
  "image/webp": "webp"
} as const;

type AdminClient = Awaited<ReturnType<typeof getAdminApi>>;

async function uploadCommunityCover(
  admin: AdminClient,
  file: FormDataEntryValue | null,
  slug: string
) {
  if (!(file instanceof File) || file.size === 0) return null;

  const extension = communityCoverTypes[file.type as keyof typeof communityCoverTypes];
  if (!extension) {
    throw new Error("Kapak görseli JPEG, PNG veya WebP olmalıdır.");
  }
  if (file.size > 5 * 1024 * 1024) {
    throw new Error("Kapak görseli en fazla 5 MB olabilir.");
  }

  const path = `${slug}/${Date.now()}-${crypto.randomUUID()}.${extension}`;
  const { error: uploadError } = await admin.storage
    .from("community-covers")
    .upload(path, await file.arrayBuffer(), {
      contentType: file.type,
      upsert: false
    });

  if (uploadError) {
    throw new Error(`Kapak görseli yüklenemedi: ${uploadError.message}`);
  }

  const { data } = admin.storage.from("community-covers").getPublicUrl(path);
  return { path, publicUrl: data.publicUrl };
}

async function createMainCommunity(formData: FormData) {
  "use server";

  const name = String(formData.get("name") ?? "").trim();
  const slug = String(formData.get("slug") ?? "").trim().toLowerCase().replace(/[^a-z0-9-]/g, "-");
  const description = String(formData.get("description") ?? "").trim();
  const ownerEmail = String(formData.get("owner_email") ?? "").trim().toLowerCase();
  if (!name || !slug || !ownerEmail) return;

  const admin = await getAdminApi();
  const { data: owner } = await admin.from("profiles").select("id").eq("email", ownerEmail).maybeSingle();
  if (!owner) return;

  const cover = await uploadCommunityCover(admin, formData.get("cover_image_file"), slug);
  const { data: community, error: createError } = await admin.from("communities").insert({
    parent_id: null,
    community_type: "category_hub",
    partner_trust_level: "trusted",
    lead_moderator_id: owner.id,
    name,
    slug,
    description: description || null,
    visibility: "public",
    cover_image_url: cover?.publicUrl ?? null,
    created_by: owner.id
  }).select("id").single();

  if (createError) {
    if (cover) await admin.storage.from("community-covers").remove([cover.path]);
    throw new Error(`Topluluk oluşturulamadı: ${createError.message}`);
  }

  if (community) {
    await admin.from("community_members").upsert({
      community_id: community.id,
      user_id: owner.id,
      role: "manager",
      status: "approved"
    }, { onConflict: "community_id,user_id" });
  }

  revalidatePath("/");
}

async function createPartnerCommunity(formData: FormData) {
  "use server";

  const name = String(formData.get("name") ?? "").trim();
  const slug = String(formData.get("slug") ?? "").trim().toLowerCase().replace(/[^a-z0-9-]/g, "-");
  const description = String(formData.get("description") ?? "").trim();
  const ownerEmail = String(formData.get("owner_email") ?? "").trim().toLowerCase();
  if (!name || !slug || !ownerEmail) return;

  const admin = await getAdminApi();
  const { data: owner } = await admin.from("profiles").select("id").eq("email", ownerEmail).maybeSingle();
  if (!owner) return;

  const cover = await uploadCommunityCover(admin, formData.get("cover_image_file"), slug);
  const { data: partner, error: createError } = await admin.from("communities").insert({
    parent_id: null,
    community_type: "partner_hub",
    partner_trust_level: "new",
    is_verified_partner: false,
    lead_moderator_id: owner.id,
    name,
    slug,
    description: description || null,
    visibility: "public",
    cover_image_url: cover?.publicUrl ?? null,
    created_by: owner.id
  }).select("id").single();

  if (createError) {
    if (cover) await admin.storage.from("community-covers").remove([cover.path]);
    throw new Error(`Partner topluluğu oluşturulamadı: ${createError.message}`);
  }

  if (partner) {
    await admin.from("community_members").upsert({
      community_id: partner.id,
      user_id: owner.id,
      role: "owner",
      status: "approved"
    }, { onConflict: "community_id,user_id" });
  }

  revalidatePath("/");
}

async function updatePartnerTrust(formData: FormData) {
  "use server";

  const communityId = String(formData.get("community_id") ?? "");
  const trustLevel = String(formData.get("trust_level") ?? "new");
  if (!communityId || !["new", "verified", "trusted"].includes(trustLevel)) return;

  const admin = await getAdminApi();
  await admin.from("communities").update({
    partner_trust_level: trustLevel,
    is_verified_partner: trustLevel !== "new"
  }).eq("id", communityId).eq("community_type", "partner_hub");

  revalidatePath("/");
}

async function assignCommunityModerator(formData: FormData) {
  "use server";

  const communityId = String(formData.get("community_id") ?? "");
  const userEmail = String(formData.get("user_email") ?? "").trim().toLowerCase();
  if (!communityId || !userEmail) return;

  const admin = await getAdminApi();
  await admin.rpc("set_community_lead_moderator", {
    target_community_id: communityId,
    target_user_email: userEmail
  });

  revalidatePath("/");
}

async function approveEvent(formData: FormData) {
  "use server";

  const id = String(formData.get("id") ?? "");
  if (!id) return;

  const admin = await getAdminApi();
  await admin
    .from("events")
    .update({
      status: "published",
      group_moderation_status: "approved",
      platform_moderation_status: "approved",
      rejection_reason: null,
      published_at: new Date().toISOString()
    })
    .eq("id", id);

  revalidatePath("/");
}

async function rejectEvent(formData: FormData) {
  "use server";

  const id = String(formData.get("id") ?? "");
  const reason = String(formData.get("reason") ?? "").trim();
  if (!id) return;

  const admin = await getAdminApi();
  await admin
    .from("events")
    .update({
      status: "rejected",
      platform_moderation_status: "rejected",
      rejection_reason: reason || "Admin tarafından reddedildi."
    })
    .eq("id", id);

  revalidatePath("/");
}

async function resolveReport(formData: FormData) {
  "use server";

  const id = String(formData.get("id") ?? "");
  const status = String(formData.get("status") ?? "resolved");
  if (!id) return;

  const admin = await getAdminApi();
  await admin
    .from("reports")
    .update({
      status,
      resolved_at: new Date().toISOString()
    })
    .eq("id", id);

  revalidatePath("/");
}

async function updateCommentModeration(formData: FormData) {
  "use server";

  const id = String(formData.get("id") ?? "");
  const moderationStatus = String(formData.get("moderation_status") ?? "hidden");
  if (!id) return;

  const admin = await getAdminApi();
  await admin.from("comments").update({ moderation_status: moderationStatus }).eq("id", id);

  revalidatePath("/");
}

async function updatePostModeration(formData: FormData) {
  "use server";

  const id = String(formData.get("id") ?? "");
  const moderationStatus = String(formData.get("moderation_status") ?? "hidden");
  if (!id) return;

  const admin = await getAdminApi();
  await admin.from("posts").update({ moderation_status: moderationStatus }).eq("id", id);

  revalidatePath("/");
}

async function updateUserStatus(formData: FormData) {
  "use server";

  const id = String(formData.get("id") ?? "");
  const status = String(formData.get("status") ?? "suspended");
  if (!id) return;

  const admin = await getAdminApi();
  await admin.from("profiles").update({ status }).eq("id", id);

  revalidatePath("/");
}

function formatDate(value: string) {
  return new Date(value).toLocaleString("tr-TR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  });
}

function maskUser(userId: string) {
  return `Üye ${userId.slice(0, 6)}`;
}

export default async function AdminHomePage() {
  let stats = [
    { label: "Bekleyen Etkinlik", value: "0" },
    { label: "Aktif Topluluk", value: "0" },
    { label: "Acik Rapor", value: "0" },
    { label: "Riskli Icerik", value: "0" }
  ];
  let pendingEvents: EventRecord[] = [];
  let recentEvents: EventRecord[] = [];
  let openReports: ReportRecord[] = [];
  let flaggedComments: ModerationComment[] = [];
  let flaggedPosts: ModerationPost[] = [];
  let riskProfiles: RiskProfile[] = [];
  let mainCommunities: MainCommunity[] = [];
  let envError: string | null = null;

  try {
    const admin = await getAdminApi();

    const [
      pendingEventsResult,
      communityCountResult,
      openReportsResult,
      recentEventsResult,
      flaggedCommentsResult,
      flaggedPostsResult,
      riskProfilesResult,
      mainCommunitiesResult
    ] = await Promise.all([
      admin
        .from("events")
        .select(
          "id, title, description, starts_at, location_name, status, group_moderation_status, platform_moderation_status, rejection_reason, communities(name, slug), profiles(display_name, email)"
        )
        .eq("status", "pending_approval")
        .order("created_at", { ascending: false }),
      admin.from("communities").select("*", { count: "exact", head: true }),
      admin.from("reports").select("id, reporter_id, target_type, target_id, reason, details, status, created_at").eq("status", "open").order("created_at", { ascending: false }).limit(8),
      admin
        .from("events")
        .select(
          "id, title, description, starts_at, location_name, status, group_moderation_status, platform_moderation_status, rejection_reason, communities(name, slug), profiles(display_name, email)"
        )
        .in("status", ["published", "rejected"])
        .order("updated_at", { ascending: false })
        .limit(5),
      admin
        .from("comments")
        .select("id, target_type, target_id, author_id, body, moderation_status, created_at")
        .in("moderation_status", ["flagged", "hidden"])
        .order("created_at", { ascending: false })
        .limit(6),
      admin
        .from("posts")
        .select("id, author_id, community_id, body, moderation_status, created_at")
        .in("moderation_status", ["flagged", "hidden"])
        .order("created_at", { ascending: false })
        .limit(6),
      admin
        .from("profiles")
        .select("id, email, display_name, username, status, created_at")
        .in("status", ["pending_verification", "suspended"])
        .order("created_at", { ascending: false })
        .limit(6),
      admin
        .from("communities")
        .select("id, name, slug, community_type, partner_trust_level, is_verified_partner")
        .is("parent_id", null)
        .order("created_at", { ascending: false })
    ]);

    pendingEvents = (pendingEventsResult.data ?? []) as unknown as EventRecord[];
    recentEvents = (recentEventsResult.data ?? []) as unknown as EventRecord[];
    openReports = (openReportsResult.data ?? []) as ReportRecord[];
    flaggedComments = (flaggedCommentsResult.data ?? []) as ModerationComment[];
    flaggedPosts = (flaggedPostsResult.data ?? []) as ModerationPost[];
    riskProfiles = (riskProfilesResult.data ?? []) as RiskProfile[];
    mainCommunities = (mainCommunitiesResult.data ?? []) as MainCommunity[];

    stats = [
      { label: "Bekleyen Etkinlik", value: String(pendingEvents.length) },
      { label: "Aktif Topluluk", value: String(communityCountResult.count ?? 0) },
      { label: "Acik Rapor", value: String(openReports.length) },
      { label: "Riskli Icerik", value: String(flaggedComments.length + flaggedPosts.length) }
    ];
  } catch (error) {
    envError = error instanceof Error ? error.message : "Admin panel verileri yüklenemedi.";
  }

  const categoryCommunities = mainCommunities.filter((community) => community.community_type === "category_hub");
  const partnerCommunities = mainCommunities.filter((community) => community.community_type === "partner_hub");

  return (
    <main style={styles.page}>
      <AdminSessionControls />
      <section style={styles.hero}>
        <div>
          <p style={styles.kicker}>Bialem Admin</p>
          <h1 style={styles.title}>Etkinlik taleplerini, raporları ve topluluk güven akışlarını tek panelden yönetin.</h1>
          <p style={styles.description}>
            Bu ekran mobil uygulamadan gelen etkinlik onaylari, raporlar, moderasyon sorunlari ve riskli kullanıcı
            durumlarını yönetmek için canlı Supabase verisini kullanır.
          </p>
          <Link
            href="/admin/team"
            style={{
              display: "inline-block",
              marginTop: 16,
              marginRight: 10,
              padding: "12px 18px",
              borderRadius: 999,
              background: "#ffad1f",
              color: "#081a40",
              textDecoration: "none",
              fontWeight: 800
            }}
          >
            Bialem Ekibi yönetimi
          </Link>
          <Link
            href="/admin/advantages"
            style={{
              display: "inline-block",
              marginTop: 16,
              marginRight: 10,
              padding: "12px 18px",
              borderRadius: 999,
              background: "#7b35ff",
              color: "#fff",
              textDecoration: "none",
              fontWeight: 800
            }}
          >
            Bialem Avantaj yönetimi
          </Link>
          <Link
            href="/admin/store"
            style={{
              display: "inline-block",
              marginTop: 16,
              padding: "12px 18px",
              borderRadius: 999,
              background: "#0ea5e9",
              color: "#fff",
              textDecoration: "none",
              fontWeight: 800
            }}
          >
            Mağaza yönetimi
          </Link>
        </div>
      </section>

      {envError ? <section style={styles.alert}>{envError}</section> : null}

      <section style={styles.grid}>
        {stats.map((item) => (
          <article key={item.label} style={styles.card}>
            <span style={styles.cardLabel}>{item.label}</span>
            <strong style={styles.cardValue}>{item.value}</strong>
          </article>
        ))}
      </section>

      <section style={styles.twoColumn}>
        <section style={styles.sidePanel}>
          <p style={styles.kicker}>Topluluk Mimarisi</p>
          <h2 style={styles.panelTitle}>Yeni ilgi alani oluştur</h2>
          <p style={styles.emptyText}>Ana topluluklar yalnızca bu panelden açılır. Sahip e-postası uygulamada kayıtlı bir hesaba ait olmalıdır.</p>
          <form action={createMainCommunity} style={styles.formStack}>
            <input name="name" required placeholder="Topluluk adı" style={styles.formInput} />
            <input name="slug" required placeholder="kisa-adres" style={styles.formInput} />
            <textarea name="description" placeholder="Topluluğun amacı ve kapsamı" style={styles.formTextArea} />
            <CoverImageInput />
            <input name="owner_email" type="email" required placeholder="Admin hesap e-postası" style={styles.formInput} />
            <button type="submit" style={styles.approveButton}>İlgi alanını oluştur</button>
          </form>
        </section>

        <section style={styles.sidePanel}>
          <p style={styles.kicker}>Yetkilendirme</p>
          <h2 style={styles.panelTitle}>Ana topluluk moderatörü ata</h2>
          <p style={styles.emptyText}>Her toplulukta tek ana moderatör bulunur. Ana moderatör, uygulama içinden en fazla iki yardımcı atar ve yardımcıların grup, etkinlik ve katılımcı yetkilerini ayrı ayrı belirler.</p>
          <form action={assignCommunityModerator} style={styles.formStack}>
            <select name="community_id" required defaultValue="" style={styles.formInput}>
              <option value="" disabled>Ana topluluk seç</option>
              {categoryCommunities.map((community) => <option key={community.id} value={community.id}>{community.name}</option>)}
            </select>
            <input name="user_email" type="email" required placeholder="Ana moderatör e-postası" style={styles.formInput} />
            <button type="submit" style={styles.approveButton}>Ana moderatörü ata</button>
          </form>
        </section>
      </section>

      <section style={styles.twoColumn}>
        <section style={styles.sidePanel}>
          <p style={styles.kicker}>Partner Federasyonu</p>
          <h2 style={styles.panelTitle}>WhatsApp veya yerel topluluk ekle</h2>
          <p style={styles.emptyText}>Topluluğun sahibi uygulamada kayıtlı olmalıdır. Partner kendi gruplarını yönetir; ilgi alanı moderatörleri bu gruplara müdahale edemez.</p>
          <form action={createPartnerCommunity} style={styles.formStack}>
            <input name="name" required placeholder="Ornek: Ankara Kamp Ekibi" style={styles.formInput} />
            <input name="slug" required placeholder="ankara-kamp-ekibi" style={styles.formInput} />
            <textarea name="description" placeholder="Partner topluluğun tanıtımı" style={styles.formTextArea} />
            <CoverImageInput />
            <input name="owner_email" type="email" required placeholder="Partner sahibinin üye e-postası" style={styles.formInput} />
            <button type="submit" style={styles.approveButton}>Partner topluluğu ekle</button>
          </form>
        </section>

        <section style={styles.sidePanel}>
          <p style={styles.kicker}>Güven Seviyesi</p>
          <h2 style={styles.panelTitle}>Partner onay akisini belirle</h2>
          <p style={styles.emptyText}>Yeni partnerlerde grup onayından sonra admin kontrolü gerekir. Doğrulanmış ve güvenilir partnerlerde kendi moderatör onayı yeterlidir.</p>
          {partnerCommunities.length === 0 ? <p style={styles.emptyText}>Henüz partner topluluk eklenmedi.</p> : (
            <form action={updatePartnerTrust} style={styles.formStack}>
              <select name="community_id" required defaultValue="" style={styles.formInput}>
                <option value="" disabled>Partner topluluk seç</option>
                {partnerCommunities.map((community) => <option key={community.id} value={community.id}>{community.name} - {community.partner_trust_level}</option>)}
              </select>
              <select name="trust_level" required defaultValue="new" style={styles.formInput}>
                <option value="new">Yeni - cift onay</option>
                <option value="verified">Doğrulanmış - grup onayı</option>
                <option value="trusted">Güvenilir - grup onayı</option>
              </select>
              <button type="submit" style={styles.approveButton}>Güven seviyesini kaydet</button>
            </form>
          )}
        </section>
      </section>

      <section style={styles.panel}>
        <h2 style={styles.panelTitle}>Bekleyen Etkinlik Talepleri</h2>
        {pendingEvents.length === 0 ? (
          <p style={styles.emptyText}>Şu an onay bekleyen etkinlik talebi yok.</p>
        ) : (
          <div style={styles.stack}>
            {pendingEvents.map((event) => (
              <article key={event.id} style={styles.eventCard}>
                <div style={styles.eventHeader}>
                  <div>
                    <h3 style={styles.eventTitle}>{event.title}</h3>
                    <p style={styles.meta}>
                      {event.communities?.name ?? "Topluluk yok"} - {formatDate(event.starts_at)}
                      {event.location_name ? ` - ${event.location_name}` : ""}
                    </p>
                    <p style={styles.meta}>
                      Talep sahibi: {event.profiles?.display_name ?? "Bilinmiyor"} ({event.profiles?.email ?? "eposta yok"})
                    </p>
                  </div>
                  <span style={styles.pendingBadge}>{event.platform_moderation_status === "pending" ? "Partner Son Kontrolu" : "Grup Onayi Bekliyor"}</span>
                </div>

                <p style={styles.eventText}>{event.description || "Açıklama eklenmemis."}</p>

                <div style={styles.actionRow}>
                  <form action={approveEvent}>
                    <input type="hidden" name="id" value={event.id} />
                    <button type="submit" style={styles.approveButton}>
                      Onayla
                    </button>
                  </form>

                  <form action={rejectEvent} style={styles.rejectForm}>
                    <input type="hidden" name="id" value={event.id} />
                    <input type="text" name="reason" placeholder="Reddetme nedeni" style={styles.rejectInput} />
                    <button type="submit" style={styles.rejectButton}>
                      Reddet
                    </button>
                  </form>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>

      <section style={styles.twoColumn}>
        <section style={styles.sidePanel}>
          <h2 style={styles.panelTitle}>Acik Raporlar</h2>
          {openReports.length === 0 ? (
            <p style={styles.emptyText}>Şu an açık rapor yok.</p>
          ) : (
            <div style={styles.stack}>
              {openReports.map((report) => (
                <article key={report.id} style={styles.moderationCard}>
                  <div style={styles.rowBetween}>
                    <strong style={styles.inlineTitle}>{report.target_type.toUpperCase()}</strong>
                    <span style={styles.reportBadge}>Acik</span>
                  </div>
                  <p style={styles.meta}>Raporlayan: {maskUser(report.reporter_id)}</p>
                  <p style={styles.meta}>Hedef kayıt: {report.target_id.slice(0, 8)}</p>
                  <p style={styles.eventText}>
                    <strong>Neden:</strong> {report.reason}
                  </p>
                  {report.details ? <p style={styles.eventText}>{report.details}</p> : null}
                  <p style={styles.meta}>{formatDate(report.created_at)}</p>
                  <div style={styles.inlineActions}>
                    <form action={resolveReport}>
                      <input type="hidden" name="id" value={report.id} />
                      <input type="hidden" name="status" value="resolved" />
                      <button type="submit" style={styles.smallApproveButton}>
                        Cozuldu
                      </button>
                    </form>
                    <form action={resolveReport}>
                      <input type="hidden" name="id" value={report.id} />
                      <input type="hidden" name="status" value="dismissed" />
                      <button type="submit" style={styles.smallRejectButton}>
                        Reddet
                      </button>
                    </form>
                  </div>
                </article>
              ))}
            </div>
          )}
        </section>

        <section style={styles.sidePanel}>
          <h2 style={styles.panelTitle}>Riskli Kullanıcı Durumlari</h2>
          {riskProfiles.length === 0 ? (
            <p style={styles.emptyText}>Izlenecek kullanıcı durumu yok.</p>
          ) : (
            <div style={styles.stack}>
              {riskProfiles.map((profile) => (
                <article key={profile.id} style={styles.moderationCard}>
                  <div style={styles.rowBetween}>
                    <strong style={styles.inlineTitle}>{profile.display_name}</strong>
                    <span style={profile.status === "suspended" ? styles.rejectBadge : styles.pendingBadge}>
                      {profile.status === "suspended" ? "Askıda" : "Doğrulama"}
                    </span>
                  </div>
                  <p style={styles.meta}>@{profile.username}</p>
                  <p style={styles.meta}>{profile.email}</p>
                  <p style={styles.meta}>{formatDate(profile.created_at)}</p>
                  <div style={styles.inlineActions}>
                    <form action={updateUserStatus}>
                      <input type="hidden" name="id" value={profile.id} />
                      <input type="hidden" name="status" value="active" />
                      <button type="submit" style={styles.smallApproveButton}>
                        Aktif et
                      </button>
                    </form>
                    <form action={updateUserStatus}>
                      <input type="hidden" name="id" value={profile.id} />
                      <input type="hidden" name="status" value="suspended" />
                      <button type="submit" style={styles.smallRejectButton}>
                        Askiya al
                      </button>
                    </form>
                  </div>
                </article>
              ))}
            </div>
          )}
        </section>
      </section>

      <section style={styles.twoColumn}>
        <section style={styles.sidePanel}>
          <h2 style={styles.panelTitle}>Yorum Moderasyonu</h2>
          {flaggedComments.length === 0 ? (
            <p style={styles.emptyText}>Riskli yorum bulunmuyor.</p>
          ) : (
            <div style={styles.stack}>
              {flaggedComments.map((comment) => (
                <article key={comment.id} style={styles.moderationCard}>
                  <div style={styles.rowBetween}>
                    <strong style={styles.inlineTitle}>{comment.target_type} yorumu</strong>
                    <span style={comment.moderation_status === "hidden" ? styles.rejectBadge : styles.pendingBadge}>
                      {comment.moderation_status}
                    </span>
                  </div>
                  <p style={styles.meta}>Yazan: {maskUser(comment.author_id)}</p>
                  <p style={styles.eventText}>{comment.body}</p>
                  <p style={styles.meta}>{formatDate(comment.created_at)}</p>
                  <div style={styles.inlineActions}>
                    <form action={updateCommentModeration}>
                      <input type="hidden" name="id" value={comment.id} />
                      <input type="hidden" name="moderation_status" value="visible" />
                      <button type="submit" style={styles.smallApproveButton}>
                        Gorunur yap
                      </button>
                    </form>
                    <form action={updateCommentModeration}>
                      <input type="hidden" name="id" value={comment.id} />
                      <input type="hidden" name="moderation_status" value="hidden" />
                      <button type="submit" style={styles.smallRejectButton}>
                        Gizle
                      </button>
                    </form>
                  </div>
                </article>
              ))}
            </div>
          )}
        </section>

        <section style={styles.sidePanel}>
          <h2 style={styles.panelTitle}>Paylaşım Moderasyonu</h2>
          {flaggedPosts.length === 0 ? (
            <p style={styles.emptyText}>Riskli paylaşım bulunmuyor.</p>
          ) : (
            <div style={styles.stack}>
              {flaggedPosts.map((post) => (
                <article key={post.id} style={styles.moderationCard}>
                  <div style={styles.rowBetween}>
                    <strong style={styles.inlineTitle}>Topluluk paylaşımı</strong>
                    <span style={post.moderation_status === "hidden" ? styles.rejectBadge : styles.pendingBadge}>
                      {post.moderation_status}
                    </span>
                  </div>
                  <p style={styles.meta}>Yazan: {maskUser(post.author_id)}</p>
                  <p style={styles.meta}>Topluluk: {post.community_id ? post.community_id.slice(0, 8) : "yok"}</p>
                  <p style={styles.eventText}>{post.body || "Açıklama eklenmemis."}</p>
                  <p style={styles.meta}>{formatDate(post.created_at)}</p>
                  <div style={styles.inlineActions}>
                    <form action={updatePostModeration}>
                      <input type="hidden" name="id" value={post.id} />
                      <input type="hidden" name="moderation_status" value="visible" />
                      <button type="submit" style={styles.smallApproveButton}>
                        Gorunur yap
                      </button>
                    </form>
                    <form action={updatePostModeration}>
                      <input type="hidden" name="id" value={post.id} />
                      <input type="hidden" name="moderation_status" value="hidden" />
                      <button type="submit" style={styles.smallRejectButton}>
                        Gizle
                      </button>
                    </form>
                  </div>
                </article>
              ))}
            </div>
          )}
        </section>
      </section>

      <section style={styles.panel}>
        <h2 style={styles.panelTitle}>Son Etkinlik Islemleri</h2>
        {recentEvents.length === 0 ? (
          <p style={styles.emptyText}>Henüz yayınlanan veya reddedilen etkinlik yok.</p>
        ) : (
          <div style={styles.stack}>
            {recentEvents.map((event) => (
              <article key={event.id} style={styles.eventCard}>
                <div style={styles.eventHeader}>
                  <div>
                    <h3 style={styles.eventTitle}>{event.title}</h3>
                    <p style={styles.meta}>
                      {event.communities?.name ?? "Topluluk yok"} - {formatDate(event.starts_at)}
                    </p>
                  </div>
                  <span style={event.status === "published" ? styles.successBadge : styles.rejectBadge}>
                    {event.status === "published" ? "Yayinda" : "Reddedildi"}
                  </span>
                </div>
                <p style={styles.eventText}>{event.description || "Açıklama eklenmemis."}</p>
                {event.rejection_reason ? <p style={styles.rejectionText}>Neden: {event.rejection_reason}</p> : null}
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}

const styles: Record<string, React.CSSProperties> = {
  page: {
    minHeight: "100vh",
    padding: "48px 32px 64px"
  },
  hero: {
    maxWidth: 1180,
    margin: "0 auto 32px",
    padding: "28px 30px",
    borderRadius: 36,
    background:
      "linear-gradient(135deg, rgba(255, 162, 15, 0.14) 0%, rgba(123, 53, 255, 0.1) 48%, rgba(25, 200, 238, 0.14) 100%)",
    border: "1px solid var(--line)",
    boxShadow: "var(--shadow)"
  },
  kicker: {
    margin: 0,
    color: "var(--accent-2)",
    textTransform: "uppercase",
    letterSpacing: 2,
    fontWeight: 700
  },
  title: {
    margin: "12px 0",
    fontSize: "clamp(2.5rem, 6vw, 4.8rem)",
    lineHeight: 0.96,
    maxWidth: 980
  },
  description: {
    maxWidth: 860,
    color: "var(--muted)",
    fontSize: "1.05rem",
    lineHeight: 1.7
  },
  alert: {
    maxWidth: 1180,
    margin: "0 auto 24px",
    background: "#fff0f5",
    color: "var(--danger)",
    border: "1px solid #f6c8d7",
    borderRadius: 20,
    padding: 18,
    fontWeight: 600
  },
  grid: {
    maxWidth: 1180,
    margin: "0 auto",
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
    gap: 16
  },
  card: {
    background: "var(--surface)",
    border: "1px solid var(--line)",
    borderRadius: 28,
    padding: 20,
    display: "flex",
    flexDirection: "column",
    gap: 8,
    boxShadow: "var(--shadow)",
    backdropFilter: "blur(10px)"
  },
  cardLabel: {
    color: "var(--muted)",
    fontSize: "0.95rem"
  },
  cardValue: {
    fontSize: "2.4rem"
  },
  panel: {
    maxWidth: 1180,
    margin: "24px auto 0",
    background: "var(--surface)",
    border: "1px solid var(--line)",
    borderRadius: 32,
    padding: 24,
    boxShadow: "var(--shadow)",
    backdropFilter: "blur(10px)"
  },
  sidePanel: {
    background: "var(--surface)",
    border: "1px solid var(--line)",
    borderRadius: 32,
    padding: 24,
    boxShadow: "var(--shadow)",
    backdropFilter: "blur(10px)"
  },
  twoColumn: {
    maxWidth: 1180,
    margin: "24px auto 0",
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(320px, 1fr))",
    gap: 20
  },
  panelTitle: {
    marginTop: 0,
    marginBottom: 16,
    fontSize: "1.5rem"
  },
  emptyText: {
    margin: 0,
    color: "var(--muted)",
    lineHeight: 1.7
  },
  stack: {
    display: "grid",
    gap: 14
  },
  eventCard: {
    border: "1px solid var(--line)",
    borderRadius: 24,
    padding: 18,
    background: "linear-gradient(180deg, #ffffff 0%, #f9fbff 100%)"
  },
  moderationCard: {
    border: "1px solid var(--line)",
    borderRadius: 24,
    padding: 18,
    background: "linear-gradient(180deg, #ffffff 0%, #f9fbff 100%)"
  },
  eventHeader: {
    display: "flex",
    justifyContent: "space-between",
    gap: 16,
    alignItems: "flex-start"
  },
  rowBetween: {
    display: "flex",
    justifyContent: "space-between",
    gap: 16,
    alignItems: "center"
  },
  eventTitle: {
    margin: "0 0 6px",
    fontSize: "1.2rem"
  },
  inlineTitle: {
    fontSize: "1rem"
  },
  meta: {
    margin: 0,
    color: "var(--muted)",
    lineHeight: 1.6
  },
  eventText: {
    color: "var(--ink)",
    lineHeight: 1.7,
    margin: "14px 0"
  },
  pendingBadge: {
    background: "#fff1cf",
    color: "#c47d00",
    borderRadius: 999,
    padding: "8px 12px",
    fontWeight: 700,
    fontSize: "0.85rem"
  },
  reportBadge: {
    background: "#fff0f5",
    color: "var(--danger)",
    borderRadius: 999,
    padding: "8px 12px",
    fontWeight: 700,
    fontSize: "0.85rem"
  },
  successBadge: {
    background: "#def6ff",
    color: "var(--success)",
    borderRadius: 999,
    padding: "8px 12px",
    fontWeight: 700,
    fontSize: "0.85rem"
  },
  rejectBadge: {
    background: "#fff0f5",
    color: "var(--danger)",
    borderRadius: 999,
    padding: "8px 12px",
    fontWeight: 700,
    fontSize: "0.85rem"
  },
  actionRow: {
    display: "grid",
    gridTemplateColumns: "160px 1fr",
    gap: 12,
    alignItems: "center"
  },
  inlineActions: {
    display: "flex",
    gap: 10,
    flexWrap: "wrap",
    marginTop: 12
  },
  approveButton: {
    width: "100%",
    background: "var(--accent-2)",
    color: "var(--ink)",
    border: "none",
    borderRadius: 999,
    padding: "14px 18px",
    fontWeight: 700,
    cursor: "pointer"
  },
  smallApproveButton: {
    background: "var(--accent-2)",
    color: "var(--ink)",
    border: "none",
    borderRadius: 999,
    padding: "10px 14px",
    fontWeight: 700,
    cursor: "pointer"
  },
  rejectForm: {
    display: "grid",
    gridTemplateColumns: "1fr 140px",
    gap: 10
  },
  rejectInput: {
    borderRadius: 999,
    border: "1px solid var(--line)",
    padding: "12px 14px",
    fontSize: "0.95rem",
    outline: "none",
    background: "#fbfdff",
    color: "var(--ink)"
  },
  rejectButton: {
    background: "#fff0f5",
    color: "var(--danger)",
    border: "1px solid #f6c8d7",
    borderRadius: 999,
    padding: "14px 18px",
    fontWeight: 700,
    cursor: "pointer"
  },
  smallRejectButton: {
    background: "#fff0f5",
    color: "var(--danger)",
    border: "1px solid #f6c8d7",
    borderRadius: 999,
    padding: "10px 14px",
    fontWeight: 700,
    cursor: "pointer"
  },
  rejectionText: {
    margin: "10px 0 0",
    color: "var(--danger)",
    fontWeight: 600
  },
  formStack: {
    display: "grid",
    gap: 11,
    marginTop: 18
  },
  formGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))",
    gap: 11
  },
  formInput: {
    width: "100%",
    minHeight: 48,
    borderRadius: 16,
    border: "1px solid var(--line)",
    padding: "12px 14px",
    background: "#fbfdff",
    color: "var(--ink)",
    font: "inherit"
  },
  formTextArea: {
    width: "100%",
    minHeight: 100,
    resize: "vertical",
    borderRadius: 16,
    border: "1px solid var(--line)",
    padding: "12px 14px",
    background: "#fbfdff",
    color: "var(--ink)",
    font: "inherit"
  }
};
