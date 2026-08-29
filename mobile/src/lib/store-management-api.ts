import { api } from "./api";

export type StoreManagementProduct = {
  id: number;
  name: string;
  slug: string;
  shortDescription?: string | null;
  sku?: string | null;
  barcode?: string | null;
  price: number;
  discountedPrice?: number | null;
  currency: string;
  stockQuantity?: number;
  status: string;
  isFeatured?: boolean;
  isActive?: boolean;
  categoryName?: string | null;
  brandName?: string | null;
  sellerName?: string | null;
  imageUrl?: string;
  createdAt?: string;
};

export type StoreManagementCategory = {
  id: number;
  name: string;
  slug: string;
  description?: string | null;
  imageUrl?: string | null;
  sortOrder?: number;
  isActive?: boolean;
  parentId?: number | null;
  parentName?: string | null;
  children?: StoreManagementCategory[];
  createdAt?: string;
};

export type StoreManagementBrand = {
  id: number;
  name: string;
  slug: string;
  description?: string | null;
  logoUrl?: string | null;
  isActive?: boolean;
  createdAt?: string;
};

export type StoreManagementOrder = {
  id: number;
  orderNumber: string;
  subtotal: number;
  discountAmount?: number;
  shippingAmount?: number;
  totalAmount: number;
  currency: string;
  paymentStatus?: string;
  orderStatus?: string;
  shippingStatus?: string;
  customerNote?: string | null;
  couponCode?: string | null;
  createdAt?: string;
  updatedAt?: string;
};

export type StoreManagementOrderDetail = StoreManagementOrder & {
  items?: Array<{
    id: number;
    productId?: number;
    productNameSnapshot?: string;
    productSkuSnapshot?: string;
    quantity: number;
    unitPrice: number;
    totalPrice: number;
  }>;
  shipping?: StoreManagementShipping | null;
  statusHistory?: Array<{ oldStatus?: string; newStatus: string; changedBy?: string; note?: string; createdAt?: string }>;
};

export type StoreManagementShipping = {
  id: number;
  orderId: number;
  carrier: string;
  trackingNumber: string;
  shippingStatus: string;
  shippedAt?: string | null;
  estimatedDeliveryDate?: string | null;
  deliveredAt?: string | null;
  createdAt?: string;
};

export type StoreManagementAddress = {
  id: number;
  title: string;
  firstName: string;
  lastName: string;
  phone?: string | null;
  country?: string | null;
  city: string;
  district: string;
  neighborhood?: string | null;
  addressLine: string;
  postalCode?: string | null;
  note?: string | null;
  isDefault?: boolean;
  createdAt?: string;
};

export type StoreManagementReview = {
  id: number;
  productId?: number;
  userId?: number;
  rating: number;
  title?: string | null;
  comment?: string | null;
  status: string;
  helpfulCount?: number;
  createdAt?: string;
};

export type StoreManagementDashboard = {
  productCount: number;
  categoryCount: number;
  brandCount: number;
  orderCount: number;
  pendingOrderCount: number;
  shippingCount: number;
  addressCount: number;
  reviewCount: number;
};

export type PageResponse<T> = {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};

const buildQuery = (params: Record<string, string | number | boolean | undefined | null>) => {
  const qs = Object.entries(params)
    .filter(([, v]) => v !== undefined && v !== null && String(v).length > 0)
    .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`)
    .join("&");
  return qs ? `?${qs}` : "";
};

export const storeManagementApi = {
  dashboard: async (): Promise<StoreManagementDashboard> => {
    const [products, categories, brands, orders, addresses] = await Promise.all([
      storeManagementApi.products(0, 1),
      storeManagementApi.categories(),
      storeManagementApi.brands(),
      storeManagementApi.orders(0, 1),
      storeManagementApi.addresses(),
    ]);
    return {
      productCount: products.totalElements,
      categoryCount: categories.length,
      brandCount: brands.length,
      orderCount: orders.totalElements,
      pendingOrderCount: orders.content.filter((o) => o.orderStatus === "PENDING" || o.orderStatus === "WAITING_APPROVAL").length,
      shippingCount: 0,
      addressCount: addresses.length,
      reviewCount: 0,
    };
  },

  products: (page = 0, size = 20, sort = "createdAt,desc") =>
    api.rest.get<PageResponse<StoreManagementProduct>>(`/api/store/products${buildQuery({ page, size, sort })}`),

  deleteProduct: (id: number) => api.rest.delete<void>(`/api/store/admin/products/${id}`),

  categories: () => api.rest.get<StoreManagementCategory[]>("/api/store/categories/tree"),

  createCategory: (body: Partial<StoreManagementCategory>) =>
    api.rest.post<StoreManagementCategory>("/api/store/admin/categories", body),

  updateCategory: (id: number, body: Partial<StoreManagementCategory>) =>
    api.rest.put<StoreManagementCategory>(`/api/store/admin/categories/${id}`, body),

  brands: () => api.rest.get<StoreManagementBrand[]>("/api/store/brands"),

  createBrand: (body: Partial<StoreManagementBrand>) =>
    api.rest.post<StoreManagementBrand>("/api/store/admin/brands", body),

  updateBrand: (id: number, body: Partial<StoreManagementBrand>) =>
    api.rest.put<StoreManagementBrand>(`/api/store/admin/brands/${id}`, body),

  orders: (page = 0, size = 20, status?: string, sort = "createdAt,desc") =>
    api.rest.get<PageResponse<StoreManagementOrder>>(`/api/store/orders/admin/all${buildQuery({ page, size, status, sort })}`),

  orderDetail: (id: number) => api.rest.get<StoreManagementOrderDetail>(`/api/store/orders/admin/${id}`),

  approveOrder: (id: number) => api.rest.post<StoreManagementOrderDetail>(`/api/store/orders/admin/${id}/approve`, {}),
  markPreparing: (id: number) => api.rest.post<StoreManagementOrderDetail>(`/api/store/orders/admin/${id}/preparing`, {}),
  markReadyForShipping: (id: number) => api.rest.post<StoreManagementOrderDetail>(`/api/store/orders/admin/${id}/ready-for-shipping`, {}),
  cancelOrder: (id: number, reason?: string) =>
    api.rest.post<StoreManagementOrderDetail>(`/api/store/orders/admin/${id}/cancel${reason ? `?reason=${encodeURIComponent(reason)}` : ""}`, {}),

  createShipping: (orderId: number, body: { carrier: string; trackingNumber: string }) =>
    api.rest.post<StoreManagementOrderDetail>(`/api/store/orders/${orderId}/ship`, body),

  updateShippingStatus: (orderId: number, status: string) =>
    api.rest.post<StoreManagementOrderDetail>(`/api/store/orders/${orderId}/shipping-status?status=${encodeURIComponent(status)}`, {}),

  shippings: (page = 0, size = 20, sort = "createdAt,desc") =>
    api.rest.get<PageResponse<StoreManagementShipping>>(`/api/store/orders/admin/all${buildQuery({ page, size, sort })}`).then((orders) => ({
      ...orders,
      content: orders.content
        .filter((o) => (o as any).shippingStatus && (o as any).shippingStatus !== "NOT_SHIPPED")
        .map((o) => ({
          id: o.id,
          orderId: o.id,
          carrier: (o as any).carrier || "-",
          trackingNumber: (o as any).trackingNumber || "-",
          shippingStatus: (o as any).shippingStatus || "PENDING",
          createdAt: o.createdAt,
        })) as StoreManagementShipping[],
    })),

  addresses: () => api.rest.get<StoreManagementAddress[]>("/api/store/addresses"),

  reviews: (page = 0, size = 20, sort = "createdAt,desc") =>
    api.rest.get<PageResponse<StoreManagementReview>>(`/api/store/products/0/reviews${buildQuery({ page, size, sort })}`),

  moderateReview: (productId: number, reviewId: number, status: string) =>
    api.rest.post<StoreManagementReview>(`/api/store/products/${productId}/reviews/${reviewId}/moderate?status=${encodeURIComponent(status)}`, {}),
};
