import Image from "next/image";
import type { Metadata } from "next";
import styles from "./public-page.module.css";

export const metadata: Metadata = {
  title: "Bialem | Şehrinle yeniden tanış",
  description:
    "Şehrindeki etkinlikleri keşfet, güvenli topluluklara katıl ve aynı planı paylaşan insanlarla gerçek hayatta buluş.",
};

const ArrowIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true">
    <path d="M5 12h14M13 6l6 6-6 6" />
  </svg>
);

const CommunityIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true">
    <path d="M16 18a4 4 0 0 0-8 0M12 11a3 3 0 1 0 0-6 3 3 0 0 0 0 6ZM18 8a2.5 2.5 0 0 1 0 5M19 15c1.8.4 3 1.5 3 3M6 8a2.5 2.5 0 0 0 0 5M5 15c-1.8.4-3 1.5-3 3" />
  </svg>
);

const CalendarIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true">
    <path d="M6 3v3M18 3v3M4 9h16M5 5h14a1 1 0 0 1 1 1v14H4V6a1 1 0 0 1 1-1ZM8 13h3v3H8z" />
  </svg>
);

const ShieldIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true">
    <path d="M12 3 4.5 6v5.5c0 4.8 3.2 8.1 7.5 9.5 4.3-1.4 7.5-4.7 7.5-9.5V6L12 3Z" />
    <path d="m8.5 12 2.2 2.2 4.8-5" />
  </svg>
);

const StoreBadge = ({ store }: { store: "App Store" | "Google Play" }) => (
  <div className={styles.storeBadge} aria-label={`${store} bağlantısı yakında`}>
    <span className={styles.storeIcon}>{store === "App Store" ? "A" : "▶"}</span>
    <span>
      <small>YAKINDA</small>
      <strong>{store}</strong>
    </span>
  </div>
);

export default function HomePage() {
  return (
    <main className={styles.page}>
      <nav className={styles.nav} aria-label="Ana menü">
        <a className={styles.logo} href="#top" aria-label="Bialem ana sayfa">
          <Image src="/brand/app-icon.png" alt="" width={46} height={46} priority />
          <span>BİALEM</span>
        </a>
        <div className={styles.navLinks}>
          <a href="#neden">Neden Bialem?</a>
          <a href="#nasil">Nasıl çalışır?</a>
          <a className={styles.navCta} href="#indir">Uygulamayı keşfet</a>
        </div>
      </nav>

      <section className={styles.hero} id="top">
        <div className={styles.heroCopy}>
          <p className={styles.kicker}><span /> ANKARA&apos;DA BAŞLIYOR</p>
          <h1>Şehrinle yeniden <em>tanış.</em></h1>
          <p className={styles.heroText}>
            Gitmek istediğin yerler, tanışmak istediğin insanlar ve parçası olmak
            istediğin topluluklar artık aynı dünyada.
          </p>
          <div className={styles.heroActions}>
            <a className={styles.primaryButton} href="#indir">
              Çok yakında <ArrowIcon />
            </a>
            <span className={styles.heroNote}>iOS ve Android için hazırlanıyor</span>
          </div>
        </div>

        <div className={styles.heroVisual}>
          <Image
            src="/brand/onboarding-worlds.png"
            alt="Doğa, spor ve sanat topluluklarını bir araya getiren Bialem"
            fill
            priority
            sizes="(max-width: 820px) 100vw, 58vw"
          />
          <div className={`${styles.floatingCard} ${styles.floatingTop}`}>
            <span className={styles.liveDot} />
            <div><strong>Şehir hareketli</strong><small>Yeni planlar seni bekliyor</small></div>
          </div>
          <div className={`${styles.floatingCard} ${styles.floatingBottom}`}>
            <div className={styles.avatarStack}><span>K</span><span>M</span><span>+8</span></div>
            <div><strong>Birlikte git</strong><small>Plan yalnız kalmasın</small></div>
          </div>
        </div>
      </section>

      <section className={styles.manifesto} id="neden">
        <p className={styles.sectionLabel}>NEDEN BİALEM?</p>
        <div className={styles.manifestoGrid}>
          <h2>Sosyal medya değil.<br /><span>Sosyal hayat.</span></h2>
          <div>
            <p>
              Bialem, insanları ekranda tutmak için değil; aynı merakı paylaşan
              insanları güvenli topluluklarda buluşturup gerçek hayata taşımak için var.
            </p>
            <p>
              Yeni bir şehir, yeni bir hobi ya da sadece bu akşam için iyi bir plan.
              Başlamak için ortak bir ilgi yeter.
            </p>
          </div>
        </div>
      </section>

      <section className={styles.experience} id="nasil">
        <div className={styles.sectionHeading}>
          <div>
            <p className={styles.sectionLabel}>BİR PLANDAN DAHA FAZLASI</p>
            <h2>Kendine uygun dünyayı bul.</h2>
          </div>
          <p>Şehrin sunduklarını keşfet; kiminle, nerede ve nasıl katılacağını sen seç.</p>
        </div>

        <div className={styles.experienceGrid}>
          <article className={`${styles.featureCard} ${styles.featureOrange}`}>
            <div className={styles.featureIcon}><CalendarIcon /></div>
            <span>01</span>
            <h3>Keşfet</h3>
            <p>Şehir radarındaki etkinlikleri, atölyeleri ve yeni planları tek yerde gör.</p>
          </article>
          <article className={`${styles.featureCard} ${styles.featurePurple}`}>
            <div className={styles.featureIcon}><CommunityIcon /></div>
            <span>02</span>
            <h3>Topluluğunu bul</h3>
            <p>İlgi alanına uygun, moderatörlü topluluklara katıl ve kendi çevreni oluştur.</p>
          </article>
          <article className={`${styles.featureCard} ${styles.featureBlue}`}>
            <div className={styles.featureIcon}><ShieldIcon /></div>
            <span>03</span>
            <h3>Güvenle buluş</h3>
            <p>Onaylı katılım akışları ve topluluk kurallarıyla planına daha rahat katıl.</p>
          </article>
        </div>
      </section>

      <section className={styles.communityShowcase}>
        <div className={styles.showcaseCopy}>
          <p className={styles.sectionLabel}>HER İLGİYE BİR DÜNYA</p>
          <h2>Bir şeyler yapmak isteyen herkes için.</h2>
          <p>
            Masa oyunlarından doğa yürüyüşlerine, kız kıza etkinliklerden kültür ve
            sanata kadar; Bialem&apos;da planlar ortak meraklarla başlar.
          </p>
          <div className={styles.pills}>
            <span>Masa oyunları</span><span>Doğa</span><span>Workshop</span>
            <span>Konser</span><span>Gastronomi</span>
          </div>
        </div>
        <div className={styles.photoStack}>
          <figure className={styles.photoOne}>
            <Image src="/brand/kiz-nesesi.png" alt="Kız Neşesi topluluğu" fill sizes="340px" />
            <figcaption>Kız Neşesi <span>Ankara</span></figcaption>
          </figure>
          <figure className={styles.photoTwo}>
            <Image src="/brand/tabletop-games.jpg" alt="Masa oyunları buluşması" fill sizes="280px" />
            <figcaption>Masa Oyunları</figcaption>
          </figure>
          <figure className={styles.photoThree}>
            <Image src="/brand/nature-outdoor.jpg" alt="Doğa ve açık hava topluluğu" fill sizes="260px" />
            <figcaption>Doğa &amp; Açık Hava</figcaption>
          </figure>
        </div>
      </section>

      <section className={styles.download} id="indir">
        <div className={styles.downloadGlow} />
        <div className={styles.downloadLogo}>
          <Image src="/brand/app-icon.png" alt="Bialem uygulama simgesi" width={104} height={104} />
        </div>
        <p className={styles.sectionLabel}>ÇOK YAKINDA</p>
        <h2>Senin dünyan,<br />şehrin tam ortasında.</h2>
        <p>Bialem iOS ve Android için hazırlanıyor. Mağaza bağlantıları yayınlandığında burada olacak.</p>
        <div className={styles.storeRow}>
          <StoreBadge store="App Store" />
          <StoreBadge store="Google Play" />
        </div>
        <small className={styles.pilotNote}>İlk durak Ankara. Sonra şehir şehir bütün Türkiye.</small>
      </section>

      <footer className={styles.footer}>
        <div className={styles.footerBrand}>
          <a className={styles.logo} href="#top">
            <Image src="/brand/app-icon.png" alt="" width={40} height={40} />
            <span>BİALEM</span>
          </a>
          <p>Birlikte keşfet. Birlikte katıl.</p>
        </div>
        <div className={styles.footerLinks}>
          <a href="/privacy">Gizlilik Politikası</a>
          <a href="/terms">Kullanım Şartları</a>
          <a href="/kvkk">KVKK</a>
          <a href="/community-guidelines">Topluluk Kuralları</a>
          <a href="/account-deletion">Hesap Silme</a>
        </div>
        <p className={styles.copyright}>© 2026 Bialem. Ankara&apos;dan sevgiyle.</p>
      </footer>
    </main>
  );
}
