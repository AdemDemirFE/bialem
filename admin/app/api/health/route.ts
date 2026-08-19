import { NextResponse } from "next/server";

export const dynamic = "force-dynamic";

export async function GET() {
  const api = (process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080").replace(/\/+$/, "");
  const startedAt = performance.now();
  try {
    const response = await fetch(`${api}/management/health`, { cache: "no-store" });
    const ok = response.ok;
    return NextResponse.json(
      {
        status: ok ? "ok" : "error",
        checks: { backend: { status: ok ? "ok" : "error", duration_ms: Math.round(performance.now() - startedAt) } },
        timestamp: new Date().toISOString()
      },
      { status: ok ? 200 : 503 }
    );
  } catch {
    return NextResponse.json(
      { status: "error", checks: { backend: { status: "error", duration_ms: Math.round(performance.now() - startedAt) } } },
      { status: 503 }
    );
  }
}
