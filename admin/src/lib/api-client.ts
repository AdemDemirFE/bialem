import { cookies } from "next/headers";
import { getApiBaseUrl } from "./backend-config";

const TOKEN_COOKIE = "bialem_api_token";

export class ApiError extends Error {
  status: number;
  body: unknown;

  constructor(message: string, status: number, body: unknown) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.body = body;
  }
}

export async function getStoredApiToken(): Promise<string | null> {
  const cookieStore = await cookies();
  return cookieStore.get(TOKEN_COOKIE)?.value ?? null;
}

export async function setStoredApiToken(token: string | null): Promise<void> {
  const cookieStore = await cookies();
  if (!token) {
    cookieStore.delete(TOKEN_COOKIE);
    return;
  }

  cookieStore.set(TOKEN_COOKIE, token, {
    httpOnly: true,
    sameSite: "lax",
    secure: process.env.NODE_ENV === "production",
    path: "/"
  });
}

type ApiRequestOptions = Omit<RequestInit, "body"> & {
  body?: unknown;
  auth?: boolean;
};

export async function apiRequest<T>(path: string, options: ApiRequestOptions = {}): Promise<T> {
  const baseUrl = getApiBaseUrl();
  if (!baseUrl) {
    throw new Error("NEXT_PUBLIC_API_BASE_URL ayarlanmamis.");
  }

  const headers = new Headers(options.headers ?? {});
  if (options.body !== undefined && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  if (options.auth !== false) {
    const token = await getStoredApiToken();
    if (token) {
      headers.set("Authorization", `Bearer ${token}`);
    }
  }

  const response = await fetch(`${baseUrl}${path.startsWith("/") ? path : `/${path}`}`, {
    ...options,
    headers,
    body: options.body === undefined ? undefined : JSON.stringify(options.body),
    cache: "no-store"
  });

  const contentType = response.headers.get("content-type") ?? "";
  const isJson = contentType.includes("application/json");
  const payload = isJson ? await response.json().catch(() => null) : await response.text().catch(() => null);

  if (!response.ok) {
    const message =
      typeof payload === "object" && payload && "detail" in payload && typeof payload.detail === "string"
        ? payload.detail
        : typeof payload === "object" && payload && "message" in payload && typeof payload.message === "string"
          ? payload.message
          : typeof payload === "object" && payload && "title" in payload && typeof payload.title === "string"
            ? payload.title
            : `API istegi basarisiz (${response.status})`;

    throw new ApiError(message, response.status, payload);
  }

  return payload as T;
}
