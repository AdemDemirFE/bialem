"use client";

import { useEffect, useState, type FormEvent } from "react";
import { createBrowserApi } from "../../src/lib/browser-api";
import styles from "../public-page.module.css";

type RecoveryParameters = {
  key: string | null;
  errorDescription: string | null;
};

type Stage = "request" | "choice" | "password" | "success";

const emptyParameters: RecoveryParameters = {
  key: null,
  errorDescription: null
};

function readRecoveryParameters(): RecoveryParameters {
  const query = new URLSearchParams(window.location.search);
  const hash = new URLSearchParams(window.location.hash.replace(/^#/, ""));

  return {
    key: query.get("key") ?? hash.get("key"),
    errorDescription: query.get("error_description") ?? hash.get("error_description")
  };
}

export function ResetPasswordBridge() {
  const [stage, setStage] = useState<Stage>("request");
  const [parameters, setParameters] = useState<RecoveryParameters>(emptyParameters);
  const [appUrl, setAppUrl] = useState("bialem://reset-password");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [passwordConfirmation, setPasswordConfirmation] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [pending, setPending] = useState(false);

  useEffect(() => {
    const recoveryParameters = readRecoveryParameters();
    const hasRecoveryLink = Boolean(recoveryParameters.key || recoveryParameters.errorDescription);

    setParameters(recoveryParameters);
    setAppUrl(`bialem://reset-password${window.location.search}${window.location.hash}`);
    setStage(hasRecoveryLink ? "choice" : "request");
  }, []);

  async function requestReset(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setPending(true);
    setError(null);
    setMessage(null);

    try {
      const client = createBrowserApi();
      const { error: resetError } = await client.auth.resetPasswordForEmail(
        email.trim().toLowerCase(),
        { redirectTo: `${window.location.origin}/reset-password` }
      );

      if (resetError) {
        throw resetError;
      }

      setMessage("E-posta adresi kayıtlıysa şifre yenileme bağlantısı gönderildi.");
    } catch {
      setError("Şifre yenileme isteği şu anda gönderilemedi. Lütfen biraz sonra tekrar deneyin.");
    } finally {
      setPending(false);
    }
  }

  async function continueOnWeb() {
    setError(null);
    if (parameters.errorDescription) {
      setError("Bağlantı geçersiz, süresi dolmuş veya daha önce kullanılmış. Yeni bir bağlantı isteyin.");
      setStage("request");
      return;
    }
    if (!parameters.key) {
      setError("Şifre yenileme anahtarı bulunamadı.");
      setStage("request");
      return;
    }
    setStage("password");
  }

  async function savePassword(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);

    if (password.length < 8) {
      setError("Yeni şifren en az 8 karakter olmalıdır.");
      return;
    }

    if (password !== passwordConfirmation) {
      setError("Şifreler birbiriyle eşleşmiyor.");
      return;
    }

    setPending(true);

    try {
      const client = createBrowserApi();
      const { error: updateError } = await client.auth.updateUser({ password });
      if (updateError) throw updateError;

      await client.auth.signOut();
      window.history.replaceState({}, "", "/reset-password");
      setPassword("");
      setPasswordConfirmation("");
      setStage("success");
    } catch {
      setError("Şifre güncellenemedi. Yeni bir şifre yenileme bağlantısı isteyin.");
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
                Hesabına bağlı e-posta adresini yaz. Güvenli yenileme bağlantısını sana gönderelim.
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
                  {pending ? "Gönderiliyor..." : "Yenileme bağlantısı gönder"}
                </button>
              </form>
              {message ? <p className={styles.successMessage}>{message}</p> : null}
            </>
          ) : null}

          {stage === "choice" ? (
            <>
              <h1 className={styles.title}>Yeni şifreni nerede belirlemek istersin?</h1>
              <p className={styles.lead}>
                İşleme bu tarayıcıda devam edebilir veya Bi&apos;Alem uygulamasını açabilirsin.
              </p>
              <div className={styles.actions}>
                <button className={styles.button} type="button" onClick={continueOnWeb}>
                  Web’de devam et
                </button>
                <a className={styles.secondaryButton} href={appUrl}>Uygulamada devam et</a>
              </div>
            </>
          ) : null}

          {stage === "password" ? (
              <p className={styles.lead}>En az 8 karakterden oluşan güçlü bir şifre kullan.</p>
              <form className={styles.resetForm} onSubmit={savePassword}>
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
            </>
          ) : null}

          {stage === "success" ? (
            <>
              <h1 className={styles.title}>Şifren güncellendi.</h1>
              <p className={styles.lead}>Yeni şifrenle uygulamaya veya yönetim paneline giriş yapabilirsin.</p>
              <div className={styles.actions}>
                <a className={styles.button} href="/admin/login">Admin girişine git</a>
                <a className={styles.secondaryButton} href="/">Ana sayfaya dön</a>
              </div>
            </>
          ) : null}

          {error ? <p className={styles.errorMessage}>{error}</p> : null}
          <p className={styles.notice}>
            Güvenliğin için şifre yenileme bağlantısını başkalarıyla paylaşma.
          </p>
        </section>
      </div>
    </main>
  );
}
