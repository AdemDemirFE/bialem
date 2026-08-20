"use client";

import { useEffect, useState, type FormEvent } from "react";
import { createBrowserApi } from "../../src/lib/browser-api";
import styles from "../public-page.module.css";

type Stage = "request" | "password" | "success";

export function ResetPasswordBridge() {
  const [stage, setStage] = useState<Stage>("request");
  const [email, setEmail] = useState("");
  const [code, setCode] = useState("");
  const [password, setPassword] = useState("");
  const [passwordConfirmation, setPasswordConfirmation] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [pending, setPending] = useState(false);

  useEffect(() => {
    const query = new URLSearchParams(window.location.search);
    const hash = new URLSearchParams(window.location.hash.replace(/^#/, ""));
    const initialCode = query.get("key") ?? query.get("code") ?? hash.get("key") ?? hash.get("code");
    if (initialCode) {
      setCode(initialCode.replace(/\s/g, ""));
      setStage("password");
    }
  }, []);

  async function requestReset(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setPending(true);
    setError(null);
    setMessage(null);

    try {
      const client = createBrowserApi();
      const { error: resetError } = await client.auth.resetPasswordForEmail(email.trim().toLowerCase(), {
        redirectTo: `${window.location.origin}/reset-password`
      });

      if (resetError) {
        throw resetError;
      }

      setMessage("Eğer bu e-posta adresi sistemimizde kayıtlıysa şifre sıfırlama kodu gönderildi.");
      setStage("password");
    } catch {
      setError("Şifre yenileme isteği şu anda gönderilemedi. Lütfen biraz sonra tekrar deneyin.");
    } finally {
      setPending(false);
    }
  }

  async function savePassword(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);

    const normalizedCode = code.replace(/\s/g, "").trim();
    if (!normalizedCode || normalizedCode.length < 6) {
      setError("E-postanıza gelen 8 haneli sıfırlama kodunu girin.");
      return;
    }

    if (password.length < 8 || !/[A-Z]/.test(password) || !/[a-z]/.test(password) || !/\d/.test(password)) {
      setError("Yeni şifren en az 8 karakter olmalı ve en az bir büyük harf, bir küçük harf ve bir rakam içermelidir.");
      return;
    }

    if (password !== passwordConfirmation) {
      setError("Şifreler birbiriyle eşleşmiyor.");
      return;
    }

    setPending(true);

    try {
      const client = createBrowserApi();
      const { error: updateError } = await client.auth.updateUser({ password, key: normalizedCode });
      if (updateError) throw updateError;

      await client.auth.signOut();
      window.history.replaceState({}, "", "/reset-password");
      setPassword("");
      setPasswordConfirmation("");
      setCode("");
      setStage("success");
    } catch {
      setError("Şifre güncellenemedi. Kod hatalı veya süresi dolmuş olabilir. Yeni bir kod isteyin.");
    } finally {
      setPending(false);
    }
  }

  return (
    <main className={styles.page}>
      <div className={styles.shell}>
        <section className={styles.hero}>
          <p className={styles.brand}>Bİ&apos;ALEM</p>

          {stage === "request" ? (
            <>
              <h1 className={styles.title}>Şifreni yenile.</h1>
              <p className={styles.lead}>
                Hesabına bağlı e-posta adresini yaz. Sana 8 haneli bir sıfırlama kodu gönderelim.
              </p>
              <form className={styles.resetForm} onSubmit={requestReset}>
                <label className={styles.resetLabel}>
                  E-posta
                  <input
                    className={styles.resetInput}
                    type="email"
                    autoComplete="email"
                    required
                    value={email}
                    onChange={(event) => setEmail(event.target.value)}
                    placeholder="ornek@eposta.com"
                  />
                </label>
                <button className={styles.button} type="submit" disabled={pending}>
                  {pending ? "Gönderiliyor..." : "Sıfırlama kodu gönder"}
                </button>
              </form>
              {message ? <p className={styles.successMessage}>{message}</p> : null}
            </>
          ) : null}

          {stage === "password" ? (
            <>
              <h1 className={styles.title}>Kodu gir, yeni şifreni belirle.</h1>
              <p className={styles.lead}>
                E-postadaki 8 haneli kodu yaz. Şifre en az 8 karakter, bir büyük harf, bir küçük harf ve bir rakam içermeli.
              </p>
              {message ? <p className={styles.successMessage}>{message}</p> : null}
              <form className={styles.resetForm} onSubmit={savePassword}>
                <label className={styles.resetLabel}>
                  Sıfırlama kodu
                  <input
                    className={styles.resetInput}
                    type="text"
                    inputMode="numeric"
                    autoComplete="one-time-code"
                    required
                    maxLength={12}
                    value={code}
                    onChange={(event) => setCode(event.target.value.replace(/[^\d\s]/g, ""))}
                    placeholder="12345678"
                  />
                </label>
                <label className={styles.resetLabel}>
                  Yeni şifre
                  <input
                    className={styles.resetInput}
                    type="password"
                    autoComplete="new-password"
                    minLength={8}
                    required
                    value={password}
                    onChange={(event) => setPassword(event.target.value)}
                  />
                </label>
                <label className={styles.resetLabel}>
                  Yeni şifre tekrar
                  <input
                    className={styles.resetInput}
                    type="password"
                    autoComplete="new-password"
                    minLength={8}
                    required
                    value={passwordConfirmation}
                    onChange={(event) => setPasswordConfirmation(event.target.value)}
                  />
                </label>
                <button className={styles.button} type="submit" disabled={pending}>
                  {pending ? "Kaydediliyor..." : "Şifreyi güncelle"}
                </button>
              </form>
              <button className={styles.secondaryButton} type="button" onClick={() => setStage("request")}>
                Yeni kod gönder
              </button>
            </>
          ) : null}

          {stage === "success" ? (
            <>
              <h1 className={styles.title}>Şifren güncellendi.</h1>
              <p className={styles.lead}>Yeni şifrenle uygulamaya veya yönetim paneline giriş yapabilirsin.</p>
              <div className={styles.actions}>
                <a className={styles.button} href="/admin/login">
                  Admin girişine git
                </a>
                <a className={styles.secondaryButton} href="/">
                  Ana sayfaya dön
                </a>
              </div>
            </>
          ) : null}

          {error ? <p className={styles.errorMessage}>{error}</p> : null}
          <p className={styles.notice}>Güvenliğin için sıfırlama kodunu başkalarıyla paylaşma.</p>
        </section>
      </div>
    </main>
  );
}
