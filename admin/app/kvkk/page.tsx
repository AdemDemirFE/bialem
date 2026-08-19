import type { Metadata } from "next";
import styles from "../public-page.module.css";

export const metadata: Metadata = {
  title: "KVKK Aydınlatma Metni | Bialem",
  description: "Bialem KVKK aydınlatma metni"
};

export default function KvkkPage() {
  const supportEmail = process.env.NEXT_PUBLIC_SUPPORT_EMAIL;

  return (
    <main className={styles.page}>
      <div className={styles.shell}>
        <section className={styles.hero}>
          <p className={styles.brand}>BIALEM GÜVEN MERKEZI</p>
          <h1 className={styles.title}>KVKK Aydınlatma Metni</h1>
          <p className={styles.lead}>Kişisel verilerin hangi kapsamda işlendiğini ve haklarını burada özetliyoruz.</p>
        </section>
        <div className={styles.stack}>
          <section className={styles.card}><h2>İşlenen veri kategorileri</h2><ul><li>Hesap, iletişim ve profil bilgileri</li><li>Topluluk ve etkinlik katılım bilgileri</li><li>Yorum, paylaşım, puan ve değerlendirmeler</li><li>Teknik kullanım ve güvenlik kayıtları</li></ul></section>
          <section className={styles.card}><h2>Isleme amaclari</h2><p className={styles.body}>Veriler hesap yönetimi, hizmetlerin sunulmasi, güvenlik, moderasyon, destek ve hukuki yükümlülükler için işlenebilir.</p></section>
          <section className={styles.card}><h2>Haklarınız</h2><p className={styles.body}>Verileriniz hakkında bilgi isteme, düzeltme, silme veya yok etme ve itiraz haklarınızı kullanabilirsiniz.</p>{supportEmail ? <a className={styles.button} href={`mailto:${supportEmail}?subject=KVKK%20Basvurusu`}>KVKK başvurusu gönder</a> : null}</section>
        </div>
        <p className={styles.footer}>Taslak metin - Son güncelleme: 22 Temmuz 2026. Veri sorumlusu bilgileri ve hukuk uzmanı kontrolü gereklidir.</p>
      </div>
    </main>
  );
}
