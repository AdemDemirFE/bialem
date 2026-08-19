"use client";

import { createSpringClient } from "./spring-client";

function apiBase() {
  return (process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080").replace(/\/+$/, "");
}

export function createBrowserApi() {
  return createSpringClient({
    getBaseUrl: apiBase,
    getToken: async () => (typeof window === "undefined" ? null : localStorage.getItem("bialem_api_token")),
    setToken: async (token) => {
      if (typeof window === "undefined") return;
      if (!token) localStorage.removeItem("bialem_api_token");
      else localStorage.setItem("bialem_api_token", token);
    }
  });
}
