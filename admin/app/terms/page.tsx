import type { Metadata } from "next";
import styles from "../public-page.module.css";

export const metadata: Metadata = {
  title: "Kullanım Şartları | Bialem",
  description: "Bialem uygulaması kullanım şartları"
};

export default function TermsPage() {
  return (
    <main className={styles.page}>
      <div className={styles.shell}>
        <section className={styles.hero}>
          <p className={styles.brand}>BIALEM GÜVEN MERKEZI</p>
          <h1 className={styles.title}>Kullanım Şartları</h1>
          <p className={styles.lead}>Bialem hizmetini kullanirken herkes için güvenli ve saygili bir ortam oluşturmayı kabul edersin.</p>
        </section>
        <div className={styles.stack}>
          <section className={styles.card}><h2>Kullanıcı sorumluluklari</h2><ul><li>Doğru hesap bilgileri vermek</li><li>Baskalarinin haklarina ve güvenliğine saygi gostermek</li><li>Hukuka aykiri, tehditkar veya zarar verici içerik paylaşmamak</li><li>Topluluk ve etkinlik kurallarına uymak</li></ul></section>
          <section className={styles.card}><h2>Moderasyon</h2><p className={styles.body}>Kuralları ihlal eden içerikler kaldırılabilir veya sınırlanabilir; gerekli durumlarda hesaplar askıya alınabilir ya da kapatılabilir.</p></section>
          <section className={styles.card}><h2>Etkinlik sorumluluğu</h2><p className={styles.body}>Etkinlik ve topluluk yöneticileri kendi duyuru, içerik ve katılım süreçlerinden sorumludur.</p></section>
        </div>
        <p className={styles.footer}>Taslak metin - Son güncelleme: 22 Temmuz 2026. Hukuk uzmanı kontrolü gereklidir.</p>
      </div>
    </main>
  );
}
