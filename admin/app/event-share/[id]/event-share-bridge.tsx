"use client";

import { useEffect } from "react";
import styles from "../../public-page.module.css";

export function EventShareBridge({ eventId }: { eventId: string }) {
  const appUrl = `bialem://event/${encodeURIComponent(eventId)}`;

  useEffect(() => {
    const timer = window.setTimeout(() => window.location.assign(appUrl), 350);
    return () => window.clearTimeout(timer);
  }, [appUrl]);

  return (
    <main className={styles.page}>
      <div className={styles.shell}>
        <section className={styles.hero}>
          <p className={styles.brand}>BI&apos;ALEM</p>
          <h1 className={styles.title}>Etkinlik Bi&apos;Alem uygulamasında açılıyor.</h1>
          <p className={styles.lead}>
            Uygulama otomatik açılmazsa aşağıdaki düğmeye dokun. Bi&apos;Alem henüz yüklü değilse ana sayfadan uygulama hakkında bilgi alabilirsin.
          </p>
          <div className={styles.actions}>
            <a className={styles.button} href={appUrl}>Uygulamada aç</a>
            <a className={styles.secondaryButton} href="/">Ana sayfaya dön</a>
          </div>
        </section>
      </div>
    </main>
  );
}
