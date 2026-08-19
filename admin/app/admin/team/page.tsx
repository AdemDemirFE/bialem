import Link from "next/link";
import { revalidatePath } from "next/cache";
import type { CSSProperties } from "react";
import { getAdminApi } from "../../../src/lib/admin-api";
import { AdminSessionControls } from "../session-controls";

export const dynamic = "force-dynamic";

const FOUNDER_EMAILS = new Set(["acbaldirlioglu@gmail.com", "mehmetas58@gmail.com"]);
const MANAGED_ROLES = new Set(["team", "support", "editor"]);

const ROLE_NAMES: Record<string, string> = {
  founder: "Bialem Kurucusu",
  team: "Bialem Ekibi",
  support: "Bialem Destek Ekibi",
  editor: "Bialem İçerik Editörü"
};

type TeamMember = {
  id: string;
  user_id: string;
  role_code: string;
  profiles: { display_name: string; email: string } | null;
};

async function assignTeamMember(formData: FormData) {
  "use server";

  const email = String(formData.get("email") ?? "").trim().toLowerCase();
  const roleCode = String(formData.get("role_code") ?? "");
  if (!email || !MANAGED_ROLES.has(roleCode) || FOUNDER_EMAILS.has(email)) return;

  const admin = await getAdminApi();
  const { data: profile } = await admin.from("profiles").select("id").eq("email", email).maybeSingle();
  if (!profile) return;

  await admin
    .from("platform_team_members")
    .upsert({ user_id: profile.id, role_code: roleCode }, { onConflict: "user_id" });

  revalidatePath("/admin/team");
}

async function removeTeamMember(formData: FormData) {
  "use server";

  const id = String(formData.get("id") ?? "");
  if (!id) return;

  const admin = await getAdminApi();
  const { data: member } = await admin
    .from("platform_team_members")
    .select("id, role_code")
    .eq("id", id)
    .maybeSingle();

  if (!member || member.role_code === "founder") return;
  await admin.from("platform_team_members").delete().eq("id", id);
  revalidatePath("/admin/team");
}

export default async function TeamAdminPage() {
  const admin = await getAdminApi();
  const { data } = await admin
    .from("platform_team_members")
    .select("id, user_id, role_code, profiles(display_name, email)")
    .order("created_at");
  const members = (data ?? []) as unknown as TeamMember[];

  return (
    <main style={styles.page}>
      <AdminSessionControls />
      <div style={styles.header}>
        <div>
          <p style={styles.kicker}>Platform kimlikleri</p>
          <h1 style={styles.title}>Bialem Ekibi</h1>
          <p style={styles.description}>
            Kurucu hesapları sabittir. Diğer resmî ekip rollerini kayıtlı kullanıcıların e-posta adresleriyle yönetin.
          </p>
        </div>
        <Link href="/admin" style={styles.backLink}>Admin paneline dön</Link>
      </div>

      <section style={styles.panel}>
        <h2 style={styles.panelTitle}>Yeni ekip üyesi</h2>
        <form action={assignTeamMember} style={styles.form}>
          <input name="email" type="email" required placeholder="Kayıtlı kullanıcı e-postası" style={styles.input} />
          <select name="role_code" defaultValue="team" style={styles.input}>
            <option value="team">Bialem Ekibi</option>
            <option value="support">Bialem Destek Ekibi</option>
            <option value="editor">Bialem İçerik Editörü</option>
          </select>
          <button type="submit" style={styles.primaryButton}>Rolü ata</button>
        </form>
        <p style={styles.note}>Kurucu rolü güvenlik nedeniyle bu panelden atanamaz.</p>
      </section>

      <section style={styles.panel}>
        <h2 style={styles.panelTitle}>Resmî hesaplar</h2>
        <div style={styles.list}>
          {members.map((member) => (
            <article key={member.id} style={styles.member}>
              <div>
                <strong>{member.profiles?.display_name ?? "Kullanıcı"}</strong>
                <p style={styles.memberMeta}>{member.profiles?.email ?? member.user_id}</p>
                <span style={styles.role}>{ROLE_NAMES[member.role_code] ?? member.role_code}</span>
              </div>
              {member.role_code !== "founder" ? (
                <form action={removeTeamMember}>
                  <input type="hidden" name="id" value={member.id} />
                  <button type="submit" style={styles.removeButton}>Rolü kaldır</button>
                </form>
              ) : (
                <span style={styles.locked}>Sabit kurucu hesabı</span>
              )}
            </article>
          ))}
        </div>
      </section>
    </main>
  );
}

const styles: Record<string, CSSProperties> = {
  page: { minHeight: "100vh", padding: "48px 24px", background: "#f5f7fb", color: "#081a40" },
  header: { maxWidth: 1050, margin: "0 auto 28px", display: "flex", justifyContent: "space-between", gap: 24, alignItems: "flex-start" },
  kicker: { margin: "0 0 8px", color: "#7c3aed", fontWeight: 800, textTransform: "uppercase", letterSpacing: "0.12em" },
  title: { margin: 0, fontSize: "clamp(2.2rem, 6vw, 4.5rem)", lineHeight: 0.95 },
  description: { maxWidth: 680, color: "#53617e", fontSize: "1.05rem", lineHeight: 1.6 },
  backLink: { color: "#081a40", fontWeight: 800, textDecoration: "none", padding: "12px 16px", border: "1px solid #d8deeb", borderRadius: 999, background: "white" },
  panel: { maxWidth: 1050, margin: "0 auto 22px", padding: 26, background: "white", border: "1px solid #d8deeb", borderRadius: 26 },
  panelTitle: { margin: "0 0 18px", fontSize: "1.45rem" },
  form: { display: "grid", gridTemplateColumns: "minmax(220px, 1fr) minmax(210px, 0.65fr) auto", gap: 12 },
  input: { minHeight: 48, border: "1px solid #cfd7e7", borderRadius: 14, padding: "10px 14px", background: "#fbfcff", color: "#081a40", font: "inherit" },
  primaryButton: { border: 0, borderRadius: 14, padding: "12px 20px", background: "#ffad1f", color: "#081a40", fontWeight: 900, cursor: "pointer" },
  note: { color: "#6b7590", marginBottom: 0 },
  list: { display: "grid", gap: 12 },
  member: { display: "flex", justifyContent: "space-between", alignItems: "center", gap: 18, padding: 18, border: "1px solid #e0e5ef", borderRadius: 18 },
  memberMeta: { margin: "5px 0 10px", color: "#68738e" },
  role: { display: "inline-block", padding: "6px 10px", borderRadius: 999, background: "#efe8ff", color: "#6625c9", fontWeight: 800, fontSize: "0.82rem" },
  removeButton: { border: "1px solid #efbfd0", borderRadius: 999, padding: "9px 13px", background: "#fff3f7", color: "#b42355", fontWeight: 800, cursor: "pointer" },
  locked: { color: "#68738e", fontWeight: 700, fontSize: "0.86rem" }
};
