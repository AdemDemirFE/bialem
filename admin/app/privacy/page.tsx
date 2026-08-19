import type { Metadata } from "next";
import styles from "../public-page.module.css";

export const metadata: Metadata = {
  title: "Gizlilik Politikası | Bialem",
  description: "Bialem uygulaması gizlilik politikası"
};

export default function PrivacyPage() {
  return (
    <main className={styles.page}>
      <div className={styles.shell}>
        <section className={styles.hero}>
          <p className={styles.brand}>BIALEM GÜVEN MERKEZI</p>
          <h1 className={styles.title}>Gizlilik Politikası</h1>
          <p className={styles.lead}>Kişisel verilerin nasıl kullanıldığını, korunduğunu ve nasıl silebileceğinizi burada açıklıyoruz.</p>
        </section>

        <div className={styles.stack}>
          <section className={styles.card}>
            <h2>Toplanan bilgiler</h2>
            <p className={styles.body}>Hesap ve profil bilgileri, topluluk ve etkinlik katılımları, paylaşımlar, yorumlar, puanlar ve güvenlik için gerekli teknik kullanım kayıtları işlenebilir.</p>
          </section>
          <section className={styles.card}>
            <h2>Kullanım amaclari</h2>
            <ul>
              <li>Hesap ve oturum yönetimi</li>
              <li>Topluluk ve etkinlik ozelliklerinin sunulmasi</li>
              <li>Moderasyon, destek ve kötüye kullanım önleme</li>
              <li>Yasal yukumluluklarin yerine getirilmesi</li>
            </ul>
          </section>
          <section className={styles.card}>
            <h2>Hizmet sağlayıcılar</h2>
            <p className={styles.body}>Supabase veritabanı, kimlik doğrulama ve dosya saklama; Expo mobil uygulama dagitimi için kullanılır. Yapay zeka asistanina yazilan mesajlar yanit üretilmesi amaciyla OpenAI hizmetine iletilebilir.</p>
          </section>
          <section className={styles.card}>
            <h2>Veri silme ve haklariniz</h2>
            <p className={styles.body}>Uygulamadaki Hesap ve yasal ekranindan hesabinizi silebilir veya hesap silme sayfamizdan destek talebi olusturabilirsiniz.</p>
            <a className={styles.button} href="/account-deletion">Hesap silme bilgilerini aç</a>
          </section>
        </div>
        <p className={styles.footer}>Taslak metin - Son güncelleme: 13 Temmuz 2026. Yayın öncesinde hukuk uzmanı kontrolü gereklidir.</p>
      </div>
    </main>
  );
}
