import { redirect } from "next/navigation";
import { getAdminApi } from "./admin-api";

export async function requireAdminMfa() {
  const api = await getAdminApi();
  const { data, error } = await api.rpc("is_admin");
  if (error || !data) redirect("/admin/login");
  return { id: "admin" };
}
