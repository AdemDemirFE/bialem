export type SpringClientOptions = {
  getBaseUrl: () => string;
  getToken: () => Promise<string | null>;
  setToken: (token: string | null) => Promise<void>;
};

type Filter = {
  op: string;
  column: string;
  value: unknown;
  negate?: boolean;
};

type AuthUser = { id: string; email: string };
type AuthSession = { access_token: string; user: AuthUser } | null;

function asError(message: string) {
  return { message };
}

export function createSpringClient(options: SpringClientOptions) {
  const authListeners = new Set<(event: string, session: AuthSession) => void>();

  async function request<T>(path: string, init: RequestInit & { auth?: boolean; json?: unknown } = {}): Promise<T> {
    const headers = new Headers(init.headers);
    if (init.json !== undefined && !headers.has("Content-Type")) {
      headers.set("Content-Type", "application/json");
    }
    if (init.auth !== false) {
      const token = await options.getToken();
      if (token) headers.set("Authorization", `Bearer ${token}`);
    }
    const response = await fetch(`${options.getBaseUrl()}${path}`, {
      ...init,
      headers,
      body: init.json === undefined ? init.body : JSON.stringify(init.json)
    });
    const contentType = response.headers.get("content-type") ?? "";
    const payload = contentType.includes("application/json")
      ? await response.json().catch(() => null)
      : await response.text().catch(() => null);
    if (!response.ok) {
      const message =
        payload && typeof payload === "object" && "detail" in payload
          ? String((payload as { detail: string }).detail)
          : payload && typeof payload === "object" && "title" in payload
            ? String((payload as { title: string }).title)
            : payload && typeof payload === "object" && "error" in payload
              ? String((payload as { error: string }).error)
              : `İstek başarısız (${response.status})`;
      throw Object.assign(new Error(message), { status: response.status, body: payload });
    }
    return payload as T;
  }

  class QueryBuilder implements PromiseLike<{ data: any; error: { message: string } | null; count: number | null }> {
    private action = "select";
    private selectClause = "*";
    private filters: Filter[] = [];
    private orderColumn?: string;
    private orderAsc = true;
    private limitValue?: number;
    private singleResult = false;
    private headOnly = false;
    private countExact = false;
    private payload: unknown;
    private onConflict?: string;

    constructor(private table: string) {}

    select(columns = "*", opts?: { count?: "exact"; head?: boolean }) {
      this.selectClause = columns;
      this.countExact = opts?.count === "exact";
      this.headOnly = Boolean(opts?.head);
      return this;
    }

    insert(payload: unknown) {
      this.action = "insert";
      this.payload = payload;
      return this;
    }

    update(payload: unknown) {
      this.action = "update";
      this.payload = payload;
      return this;
    }

    upsert(payload: unknown, opts?: { onConflict?: string }) {
      this.action = "upsert";
      this.payload = payload;
      this.onConflict = opts?.onConflict;
      return this;
    }

    delete() {
      this.action = "delete";
      return this;
    }

    eq(column: string, value: unknown) {
      this.filters.push({ op: "eq", column, value });
      return this;
    }

    neq(column: string, value: unknown) {
      this.filters.push({ op: "neq", column, value });
      return this;
    }

    is(column: string, value: unknown) {
      this.filters.push({ op: "is", column, value });
      return this;
    }

    not(column: string, operator: string, value: unknown) {
      this.filters.push({ op: operator, column, value, negate: true });
      return this;
    }

    in(column: string, value: unknown[]) {
      this.filters.push({ op: "in", column, value });
      return this;
    }

    ilike(column: string, value: string) {
      this.filters.push({ op: "ilike", column, value });
      return this;
    }

    order(column: string, opts?: { ascending?: boolean }) {
      this.orderColumn = column;
      this.orderAsc = opts?.ascending !== false;
      return this;
    }

    limit(value: number) {
      this.limitValue = value;
      return this;
    }

    maybeSingle() {
      this.singleResult = true;
      this.limitValue = 1;
      return this;
    }

    single() {
      return this.maybeSingle();
    }

    then<TResult1 = { data: any; error: { message: string } | null; count: number | null }, TResult2 = never>(
      onfulfilled?: ((value: { data: any; error: { message: string } | null; count: number | null }) => TResult1 | PromiseLike<TResult1>) | null,
      onrejected?: ((reason: any) => TResult2 | PromiseLike<TResult2>) | null
    ) {
      return this.execute().then(onfulfilled, onrejected);
    }

    private async execute() {
      try {
        const result = await request<{ data: any; error?: string; count?: number }>("/api/app/query", {
          method: "POST",
          json: {
            table: this.table,
            action: this.action,
            select: this.selectClause,
            filters: this.filters,
            orderColumn: this.orderColumn,
            orderAsc: this.orderAsc,
            limit: this.limitValue,
            single: this.singleResult,
            head: this.headOnly,
            count: this.countExact,
            payload: this.payload,
            onConflict: this.onConflict
          }
        });
        return {
          data: result.data ?? (this.singleResult ? null : []),
          error: result.error ? asError(result.error) : null,
          count: result.count ?? null
        };
      } catch (error) {
        return {
          data: this.singleResult ? null : [],
          error: asError(error instanceof Error ? error.message : "İstek başarısız"),
          count: null
        };
      }
    }
  }

  async function currentSession(): Promise<AuthSession> {
    const token = await options.getToken();
    if (!token) return null;
    try {
      const profile = await request<{ id: string; email: string }>("/api/app/me", { method: "GET" });
      return { access_token: token, user: { id: String(profile.id), email: profile.email } };
    } catch {
      await options.setToken(null);
      return null;
    }
  }

  return {
    rest: {
      async get<T>(path: string): Promise<T> {
        return request<T>(path, { method: "GET" });
      },
      async post<T>(path: string, json: unknown): Promise<T> {
        return request<T>(path, { method: "POST", json });
      },
      async put<T>(path: string, json: unknown): Promise<T> {
        return request<T>(path, { method: "PUT", json });
      },
      async patch<T>(path: string, json: unknown): Promise<T> {
        return request<T>(path, { method: "PATCH", json });
      },
      async delete<T>(path: string): Promise<T> {
        return request<T>(path, { method: "DELETE" });
      }
    },
    from(table: string) {
      return new QueryBuilder(table);
    },
    rpc(name: string, args: Record<string, unknown> = {}) {
      let single = false;
      const pending = Promise.resolve().then(async () => {
        try {
          const result = await request<{ data: any; error?: string }>(`/api/app/rpc/${name}`, { method: "POST", json: args });
          let data = result.data;
          if (single && Array.isArray(data)) data = data[0] ?? null;
          return { data, error: result.error ? asError(result.error) : null };
        } catch (error) {
          return { data: null, error: asError(error instanceof Error ? error.message : "İşlem başarısız") };
        }
      }) as Promise<{ data: any; error: { message: string } | null }> & {
        maybeSingle: () => Promise<{ data: any; error: { message: string } | null }>;
      };
      pending.maybeSingle = () => {
        single = true;
        return pending;
      };
      return pending;
    },
    functions: {
      async invoke(name: string, init?: { body?: unknown }) {
        try {
          if (name === "delete-account") {
            await request("/api/app/me", { method: "DELETE" });
            await options.setToken(null);
            return { data: { ok: true }, error: null };
          }
          const data = await request("/api/app/ai/chat", { method: "POST", json: init?.body ?? {} });
          return { data, error: null };
        } catch (error) {
          return { data: null, error: asError(error instanceof Error ? error.message : "İşlem başarısız") };
        }
      }
    },
    storage: {
      from(bucket: string) {
        return {
          async upload(path: string, fileData: ArrayBuffer | Blob, fileOptions?: { contentType?: string; upsert?: boolean }) {
            try {
              const form = new FormData();
              form.append("path", path);
              const blob = fileData instanceof Blob ? fileData : new Blob([fileData], { type: fileOptions?.contentType || "application/octet-stream" });
              form.append("file", blob, path.split("/").pop() || "file");
              const uploaded = await request<{ publicUrl: string; path: string }>(`/api/app/media/${bucket}`, {
                method: "POST",
                body: form
              });
              return { data: uploaded, error: null };
            } catch (error) {
              return { data: null, error: asError(error instanceof Error ? error.message : "Yükleme başarısız") };
            }
          },
          getPublicUrl(path: string) {
            return { data: { publicUrl: `${options.getBaseUrl()}/api/app/media/${bucket}/${path}` } };
          },
          async remove(paths: string[]) {
            try {
              await Promise.all(paths.map((path) => request(`/api/app/media/${bucket}?path=${encodeURIComponent(path)}`, { method: "DELETE" })));
              return { error: null };
            } catch (error) {
              return { error: asError(error instanceof Error ? error.message : "Silme başarısız") };
            }
          }
        };
      }
    },
    channel(_name: string) {
      let timer: ReturnType<typeof setInterval> | null = null;
      const handlers: Array<() => void> = [];
      const channel = {
        on(_event: string, _filter: unknown, callback: () => void) {
          handlers.push(callback);
          return channel;
        },
        subscribe() {
          timer = setInterval(() => handlers.forEach((handler) => handler()), 4000);
          return channel;
        }
      };
      (channel as { _cleanup?: () => void })._cleanup = () => {
        if (timer) clearInterval(timer);
      };
      return channel;
    },
    removeChannel(channel: { _cleanup?: () => void }) {
      channel._cleanup?.();
      return Promise.resolve();
    },
    auth: {
      async getSession() {
        return { data: { session: await currentSession() }, error: null };
      },
      async getUser() {
        const session = await currentSession();
        return { data: { user: session?.user ?? null }, error: null };
      },
      onAuthStateChange(callback: (event: string, session: AuthSession) => void) {
        authListeners.add(callback);
        return {
          data: {
            subscription: {
              unsubscribe() {
                authListeners.delete(callback);
              }
            }
          }
        };
      },
      async signInWithPassword({ email, password }: { email: string; password: string }) {
        try {
          const result = await request<{ id_token: string }>("/api/authenticate", {
            method: "POST",
            auth: false,
            json: { username: email.trim(), password, rememberMe: true }
          });
          await options.setToken(result.id_token);
          const session = await currentSession();
          authListeners.forEach((listener) => listener("SIGNED_IN", session));
          return { data: { session }, error: null };
        } catch (error) {
          return { data: { session: null }, error: asError(error instanceof Error ? error.message : "Giriş başarısız") };
        }
      },
      async signUp({
        email,
        password,
        options: signUpOptions
      }: {
        email: string;
        password: string;
        options?: { data?: { display_name?: string; username?: string } };
      }) {
        try {
          await request("/api/register", {
            method: "POST",
            auth: false,
            json: {
              login: signUpOptions?.data?.username || email.split("@")[0],
              email,
              password,
              firstName: signUpOptions?.data?.display_name || signUpOptions?.data?.username,
              langKey: "tr"
            }
          });
          return { data: { session: null }, error: null };
        } catch (error) {
          return { data: { session: null }, error: asError(error instanceof Error ? error.message : "Kayıt başarısız") };
        }
      },
      async signOut() {
        await options.setToken(null);
        authListeners.forEach((listener) => listener("SIGNED_OUT", null));
        return { error: null };
      },
      async resetPasswordForEmail(email: string, _options?: { redirectTo?: string }) {
        try {
          await request("/api/account/reset-password/init", { method: "POST", auth: false, json: email });
          return { error: null };
        } catch (error) {
          return { error: asError(error instanceof Error ? error.message : "Şifre sıfırlama başarısız") };
        }
      },
      async resend() {
        return { error: asError("E-posta tekrar gönderimi bu sürümde e-posta sunucusu üzerinden yapılır.") };
      },
      startAutoRefresh() {},
      stopAutoRefresh() {},
      async setSession() {
        return { data: { session: await currentSession() }, error: null };
      },
      async updateUser({ password, key }: { password: string; key?: string | null }) {
        const resolvedKey =
          key ||
          (typeof window === "undefined"
            ? null
            : new URLSearchParams(window.location.search).get("key") ||
              new URLSearchParams(window.location.hash.replace(/^#/, "")).get("key"));
        if (!resolvedKey) {
          return { error: asError("Şifre yenileme kodu eksik.") };
        }
        try {
          await request("/api/account/reset-password/finish", {
            method: "POST",
            auth: false,
            json: { key: resolvedKey, newPassword: password, confirmPassword: password }
          });
          return { error: null };
        } catch (error) {
          return { error: asError(error instanceof Error ? error.message : "Şifre güncellenemedi") };
        }
      }
    }
  };
}
