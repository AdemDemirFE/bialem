import type { Metadata } from "next";
import styles from "../public-page.module.css";

export const metadata: Metadata = {
  title: "Topluluk Kuralları | Bialem",
  description: "Bialem güvenli topluluk kuralları"
};

export default function CommunityGuidelinesPage() {
  return (
    <main className={styles.page}>
      <div className={styles.shell}>
        <section className={styles.hero}>
          <p className={styles.brand}>BIALEM GÜVEN MERKEZI</p>
          <h1 className={styles.title}>Topluluk Kuralları</h1>
          <p className={styles.lead}>Gerçek dünyada güvenli buluşmalar, uygulamadaki saygılı davranışlarla başlar.</p>
        </section>
        <div className={styles.stack}>
          <section className={styles.card}><h2>İzin verilmeyen davranışlar</h2><ul><li>Taciz, tehdit, nefret söylemi ve zorbalık</li><li>İzinsiz kişisel bilgi paylaşımı, spam ve aldatma</li><li>Çocuk güvenliğini ihlal eden veya yasa dışı içerikler</li><li>Etkinlik katılımcılarının güvenliğini tehlikeye atan davranışlar</li></ul></section>
          <section className={styles.card}><h2>Bildirim ve inceleme</h2><p className={styles.body}>Kullanıcılar içerik, mesaj ve etkinlikleri uygulama içinden raporlayabilir. Raporlar moderasyon ekibi tarafından incelenir.</p></section>
          <section className={styles.card}><h2>Acil durumlar</h2><p className={styles.body}>Acil veya fiziksel güvenliği tehdit eden bir durumda önce yerel acil yardım ve yetkili birimlere başvur.</p></section>
        </div>
        <p className={styles.footer}>Son güncelleme: 22 Temmuz 2026</p>
      </div>
    </main>
  );
}
