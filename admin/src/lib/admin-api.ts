import { cookies } from "next/headers";
import { createSpringClient } from "./spring-client";

const TOKEN = "bialem_api_token";

export function apiBase() {
  return (process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080").replace(/\/+$/, "");
}

export async function getAdminApi() {
  const cookieStore = await cookies();
  const token = cookieStore.get(TOKEN)?.value ?? null;
  return createSpringClient({
    getBaseUrl: apiBase,
    getToken: async () => token,
    setToken: async () => undefined
  });
}
