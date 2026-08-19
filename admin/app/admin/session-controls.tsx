"use client";

import { useRouter } from "next/navigation";

export function AdminSessionControls() {
  const router = useRouter();

  async function signOut() {
    await fetch("/api/session", { method: "DELETE" });
    router.replace("/admin/login");
    router.refresh();
  }

  return (
    <button
      type="button"
      onClick={signOut}
      style={{
        position: "fixed",
        top: 18,
        right: 18,
        zIndex: 50,
        padding: "10px 16px",
        border: "1px solid #d8dfed",
        borderRadius: 999,
        background: "#fff",
        color: "#081a40",
        cursor: "pointer",
        fontWeight: 850
      }}
    >
      Güvenli çıkış
    </button>
  );
}
