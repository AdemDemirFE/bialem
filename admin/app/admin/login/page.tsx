"use client";

import { useState, type FormEvent } from "react";
import { useRouter } from "next/navigation";
import styles from "../auth.module.css";

export default function AdminLoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [pending, setPending] = useState(false);

  async function submit(event: FormEvent) {
    event.preventDefault();
    setPending(true);
    setError(null);
    const response = await fetch("/api/session", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: email.trim().toLowerCase(), password })
    });
    if (!response.ok) {
      setError(response.status === 403 ? "Bu hesap admin değil." : "E-posta veya parola doğrulanamadı.");
      setPending(false);
      return;
    }
    router.replace("/admin");
    router.refresh();
  }

  return (
    <main className={styles.page}>
      <section className={styles.card}>
        <p className={styles.kicker}>BİALEM GÜVENLİ YÖNETİM</p>
        <h1 className={styles.title}>Admin girişi</h1>
        <p className={styles.description}>Kendi Spring backend hesabınız ve ROLE_ADMIN yetkisi gerekir.</p>
        <form className={styles.form} onSubmit={submit}>
          <label className={styles.label}>
            E-posta veya kullanıcı adı
            <input className={styles.input} autoComplete="username" required value={email} onChange={(event) => setEmail(event.target.value)} />
          </label>
          <label className={styles.label}>
            Parola
            <input className={styles.input} type="password" autoComplete="current-password" required value={password} onChange={(event) => setPassword(event.target.value)} />
          </label>
          {error ? <p className={styles.error}>{error}</p> : null}
          <p className={styles.note}>Hem ROLE_ADMIN hem ROLE_SUPER_ADMIN yetkileri giriş yapabilir.</p>
          <button className={styles.button} type="submit" disabled={pending}>
            {pending ? "Doğrulanıyor..." : "Giriş yap"}
          </button>
        </form>
      </section>
    </main>
  );
}
