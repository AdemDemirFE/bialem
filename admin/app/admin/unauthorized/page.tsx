"use client";

import { useRouter } from "next/navigation";
import styles from "../auth.module.css";

export default function UnauthorizedPage() {
  const router = useRouter();

  async function signOut() {
    await fetch("/api/session", { method: "DELETE" });
    router.replace("/admin/login");
    router.refresh();
  }

  return (
    <main className={styles.page}>
      <section className={styles.card}>
        <p className={styles.kicker}>ERİŞİM REDDEDİLDİ</p>
        <h1 className={styles.title}>Bu hesap admin değil</h1>
        <p className={styles.description}>
          Oturum doğrulandı ancak hesabınıza Bialem admin rolü atanmamış.
        </p>
        <button className={styles.button} type="button" onClick={signOut}>
          Güvenli çıkış yap
        </button>
      </section>
    </main>
  );
}
