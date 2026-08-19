const baseUrl = (process.env.PRODUCTION_URL ?? "https://bialem.app").replace(/\/$/, "");
const failures = [];

async function request(path, init = {}) {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 8_000);

  try {
    return await fetch(`${baseUrl}${path}`, {
      ...init,
      redirect: init.redirect ?? "error",
      cache: "no-store",
      signal: controller.signal
    });
  } catch (error) {
    failures.push(`${path}: istek basarisiz (${error instanceof Error ? error.message : "bilinmeyen hata"})`);
    return null;
  } finally {
    clearTimeout(timeout);
  }
}

function expect(condition, message) {
  if (!condition) failures.push(message);
}

const healthResponse = await request("/api/health");
if (healthResponse) {
  expect(healthResponse.status === 200, `/api/health: 200 yerine ${healthResponse.status}`);
  const health = await healthResponse.json().catch(() => null);
  expect(health?.status === "ok", "/api/health: genel durum ok degil");
  expect(health?.checks?.auth?.status === "ok", "/api/health: auth kontrolu ok degil");
  expect(health?.checks?.database?.status === "ok", "/api/health: database kontrolu ok degil");
  expect(healthResponse.headers.get("cache-control")?.includes("no-store"), "/api/health: no-store eksik");
}

for (const path of [
  "/",
  "/privacy",
  "/terms",
  "/kvkk",
  "/community-guidelines",
  "/account-deletion",
  "/reset-password"
]) {
  const response = await request(path);
  if (!response) continue;
  expect(response.status === 200, `${path}: 200 yerine ${response.status}`);

  if (path === "/") {
    expect(response.headers.get("strict-transport-security") !== null, "/: HSTS basligi eksik");
    expect(response.headers.get("x-content-type-options") === "nosniff", "/: nosniff basligi eksik");
    expect(response.headers.get("x-frame-options") === "DENY", "/: frame korumasi eksik");
  }
}

const adminResponse = await request("/admin", { redirect: "manual" });
if (adminResponse) {
  expect([307, 308].includes(adminResponse.status), `/admin: giris yonlendirmesi yerine ${adminResponse.status}`);
  const location = adminResponse.headers.get("location");
  expect(location !== null && new URL(location, baseUrl).pathname === "/admin/login", "/admin: /admin/login yonlendirmesi eksik");
}

const adminLoginResponse = await request("/admin/login");
if (adminLoginResponse) {
  expect(adminLoginResponse.status === 200, `/admin/login: 200 yerine ${adminLoginResponse.status}`);
}

const assetLinksResponse = await request("/.well-known/assetlinks.json");
if (assetLinksResponse) {
  expect(assetLinksResponse.status === 200, `/assetlinks: 200 yerine ${assetLinksResponse.status}`);
  expect(assetLinksResponse.headers.get("content-type")?.includes("application/json"), "/assetlinks: JSON content type eksik");
  const assetLinks = await assetLinksResponse.json().catch(() => null);
  const target = Array.isArray(assetLinks) ? assetLinks[0]?.target : null;
  expect(target?.namespace === "android_app", "/assetlinks: android_app namespace eksik");
  expect(target?.package_name === "com.bialem.app", "/assetlinks: Android paket adi hatali");
  expect(target?.sha256_cert_fingerprints?.length > 0, "/assetlinks: imza parmak izi eksik");
}

if (failures.length) {
  for (const failure of failures) console.error(`HATA: ${failure}`);
  process.exit(1);
}

console.log(`Production smoke kontrolleri basarili: ${baseUrl}`);
