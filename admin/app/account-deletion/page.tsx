import type { Metadata } from "next";
import styles from "../public-page.module.css";

export const metadata: Metadata = {
  title: "Hesap Silme | Bialem",
  description: "Bialem hesabi ve iliskili veriler için silme talebi"
};

export default function AccountDeletionPage() {
  const supportEmail = process.env.NEXT_PUBLIC_SUPPORT_EMAIL;

  return (
    <main className={styles.page}>
      <div className={styles.shell}>
        <section className={styles.hero}>
          <p className={styles.brand}>BIALEM</p>
          <h1 className={styles.title}>Hesabın ve verilerin senin kontrolünde.</h1>
          <p className={styles.lead}>
            Bialem hesabını uygulama içinden kalıcı olarak silebilir veya uygulamaya erişemiyorsan destek ekibimize silme talebi gönderebilirsin.
          </p>
        </section>

        <div className={styles.stack}>
          <section className={styles.card}>
            <h2>Uygulama içinden silme</h2>
            <ol>
              <li>Bialem hesabina giris yap.</li>
              <li>Profil sekmesini aç.</li>
              <li>Hesap ve yasal ayarlari seç.</li>
              <li>Hesabı kalıcı olarak sil alanindaki onay adimlarini tamamla.</li>
            </ol>
          </section>

          <section className={styles.card}>
            <h2>Uygulamaya erişemiyorsan</h2>
            <p className={styles.body}>
              Kayıtlı e-posta adresinden destek ekibimize &quot;Hesap Silme Talebi&quot; başlıklı bir e-posta gönder. Hesap sahibini doğrulamak için ek bilgi istenebilir.
            </p>
            {supportEmail ? (
              <a className={styles.button} href={`mailto:${supportEmail}?subject=Hesap%20Silme%20Talebi`}>Silme talebi gönder</a>
            ) : (
              <p className={styles.notice}>Resmi destek e-posta adresi yayın öncesinde bu alana eklenecektir.</p>
            )}
          </section>

          <section className={styles.card}>
            <h2>Hangi veriler silinir?</h2>
            <ul>
              <li>Hesap ve profil bilgileri</li>
              <li>Paylasimlar, hikayeler, yorumlar ve puanlar</li>
              <li>Takip, topluluk uyeligi ve bildirim kayıtları</li>
              <li>Hesaba ait yuklenmis medya dosyalari</li>
            </ul>
            <p className={styles.notice}>Yasal olarak saklanmasi zorunlu veriler, ilgili saklama süresi boyunca sinirli bicimde tutulabilir.</p>
          </section>
        </div>
        <p className={styles.footer}>Son güncelleme: 13 Temmuz 2026</p>
      </div>
    </main>
  );
}
