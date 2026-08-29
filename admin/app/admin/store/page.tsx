import Link from "next/link";
import { getAdminApi } from "../../../src/lib/admin-api";

export const dynamic = "force-dynamic";

export default async function StoreAdminPage() {
  const api = await getAdminApi();
  const { data: orders } = await api.rest.get<any>("/api/store/orders/admin/all?page=0&size=1");
  const { data: products } = await api.rest.get<any>("/api/store/products?page=0&size=1");

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold">Mağaza Yönetimi</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Link href="/admin/store/products" className="p-6 rounded-xl border bg-white hover:shadow transition">
          <div className="text-sm text-gray-500">Ürünler</div>
          <div className="text-2xl font-bold mt-1">{products?.totalElements ?? 0}</div>
        </Link>
        <Link href="/admin/store/orders" className="p-6 rounded-xl border bg-white hover:shadow transition">
          <div className="text-sm text-gray-500">Siparişler</div>
          <div className="text-2xl font-bold mt-1">{orders?.totalElements ?? 0}</div>
        </Link>
      </div>
    </div>
  );
}
