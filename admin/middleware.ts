import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { hasPlatformAdminAccess } from "./src/lib/permissions";

const API = (process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080").replace(/\/+$/, "");
const TOKEN = "bialem_api_token";

export async function middleware(request: NextRequest) {
  if (!request.nextUrl.pathname.startsWith("/admin")) return NextResponse.next();

  const token = request.cookies.get(TOKEN)?.value;
  const pathname = request.nextUrl.pathname;
  const isLogin = pathname === "/admin/login";

  if (!token) {
    if (isLogin) return NextResponse.next();
    return NextResponse.redirect(new URL("/admin/login", request.url));
  }

  const account = await fetch(`${API}/api/account`, {
    headers: { Authorization: `Bearer ${token}` },
    cache: "no-store"
  }).then((res) => (res.ok ? res.json() : null)).catch(() => null);

  const isAdmin = hasPlatformAdminAccess(account?.authorities);
  if (!isAdmin) {
    if (pathname === "/admin/unauthorized") return NextResponse.next();
    return NextResponse.redirect(new URL("/admin/unauthorized", request.url));
  }

  if (pathname === "/admin/mfa") return NextResponse.redirect(new URL("/admin", request.url));
  if (isLogin) return NextResponse.redirect(new URL("/admin", request.url));
  return NextResponse.next();
}

export const config = {
  matcher: ["/admin/:path*"]
};
