import { NextResponse } from "next/server";

const API = (process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080").replace(/\/+$/, "");

export async function POST(request: Request) {
  const body = await request.json();
  const auth = await fetch(`${API}/api/authenticate`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username: String(body.email || "").trim(), password: body.password, rememberMe: true })
  });
  if (!auth.ok) {
    return NextResponse.json({ error: "invalid" }, { status: 401 });
  }
  const { id_token } = await auth.json();
  const account = await fetch(`${API}/api/account`, {
    headers: { Authorization: `Bearer ${id_token}` }
  }).then((res) => res.json());
  if (!account?.authorities?.includes("ROLE_ADMIN")) {
    return NextResponse.json({ error: "forbidden" }, { status: 403 });
  }
  const response = NextResponse.json({ ok: true });
  response.cookies.set("bialem_api_token", id_token, {
    httpOnly: true,
    sameSite: "lax",
    path: "/",
    secure: process.env.NODE_ENV === "production"
  });
  return response;
}

export async function DELETE() {
  const response = NextResponse.json({ ok: true });
  response.cookies.delete("bialem_api_token");
  return response;
}
