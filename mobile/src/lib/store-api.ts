import { api } from "./api";

export type StoreCategory = {
  id: number;
  name: string;
  slug: string;
  description?: string | null;
  imageUrl?: string | null;
  sortOrder?: number;
  isActive?: boolean;
  parentId?: number | null;
  parentName?: string | null;
  children?: StoreCategory[];
  createdAt?: string;
};

export type StoreBrand = {
  id: number;
  name: string;
  slug: string;
  description?: string | null;
  logoUrl?: string | null;
  isActive?: boolean;
};

export type StoreProductImage = {
  id: number;
  imageUrl: string;
  thumbnailUrl?: string;
  sortOrder?: number;
  isPrimary?: boolean;
  altText?: string;
};

export type StoreProductVariant = {
  id: number;
  variantName: string;
  sku?: string;
  price?: number;
  discountedPrice?: number;
  stockQuantity?: number;
  imageUrl?: string;
  isActive?: boolean;
};

export type StoreProductAttribute = {
  id: number;
  attributeKey: string;
  attributeValue: string;
  sortOrder?: number;
};

export type StoreProduct = {
  id: number;
  name: string;
  slug: string;
  shortDescription?: string;
  description?: string;
  sku?: string;
  barcode?: string;
  price: number;
  discountedPrice?: number;
  currency?: string;
  stockQuantity?: number;
  lowStockThreshold?: number;
  status?: string;
  isFeatured?: boolean;
  isActive?: boolean;
  ratingAverage?: number;
  reviewCount?: number;
  salesCount?: number;
  categoryId?: number;
  categoryName?: string;
  categorySlug?: string;
  brandId?: number;
  brandName?: string;
  images: StoreProductImage[];
  variants: StoreProductVariant[];
  attributes: StoreProductAttribute[];
};

export type StoreProductListItem = {
  id: number;
  name: string;
  slug: string;
  shortDescription?: string;
  price: number;
  discountedPrice?: number;
  currency?: string;
  primaryImageUrl?: string;
  categoryName?: string;
  ratingAverage?: number;
  reviewCount?: number;
  salesCount?: number;
  inStock?: boolean;
};

export type StoreCartItem = {
  id: number;
  quantity: number;
  unitPrice: number;
  discountAmount?: number;
  totalPrice?: number;
  productId: number;
  productName: string;
  productSlug: string;
  productImage?: string;
  variantId?: number;
  variantName?: string;
  stockQuantity?: number;
  inStock?: boolean;
};

export type StoreCartSummary = {
  items: StoreCartItem[];
  subtotal: number;
  discountAmount: number;
  shippingAmount: number;
  totalAmount: number;
  itemCount: number;
};

export type StoreAddress = {
  id?: number;
  title: string;
  firstName: string;
  lastName: string;
  phone?: string;
  country?: string;
  city: string;
  district: string;
  neighborhood?: string;
  addressLine: string;
  postalCode?: string;
  note?: string;
  isDefault?: boolean;
};

export type StoreOrderItem = {
  id: number;
  productId?: number;
  productName: string;
  productSku?: string;
  productImage?: string;
  quantity: number;
  unitPrice: number;
  discount?: number;
  totalPrice: number;
  variantName?: string;
};

export type StorePayment = {
  id: number;
  provider: string;
  transactionId?: string;
  amount: number;
  currency?: string;
  status: string;
  paymentMethod?: string;
  paidAt?: string;
  failureReason?: string;
};

export type StoreShipping = {
  id: number;
  carrier: string;
  trackingNumber: string;
  shippingStatus: string;
  shippedAt?: string;
  estimatedDeliveryDate?: string;
  deliveredAt?: string;
};

export type StoreOrderStatusHistory = {
  id: number;
  oldStatus?: string;
  newStatus: string;
  changedBy?: string;
  note?: string;
  createdAt: string;
};

export type StorePaymentInitiateResponse = {
  orderNumber: string;
  paymentStatus: string;
  redirectUrl?: string;
  htmlContent?: string;
  transactionReference?: string;
  message?: string;
};

export type StoreBankTransfer = {
  id: number;
  referenceCode: string;
  amount: number;
  currency?: string;
  iban?: string;
  accountHolder?: string;
  bankName?: string;
  receiptUrl?: string;
  status: string;
  adminNote?: string;
  createdAt: string;
};

export type StoreOrder = {
  id: number;
  orderNumber: string;
  subtotal: number;
  discountAmount?: number;
  shippingAmount?: number;
  totalAmount: number;
  currency?: string;
  paymentStatus?: string;
  orderStatus?: string;
  shippingStatus?: string;
  customerNote?: string;
  couponCode?: string;
  items: StoreOrderItem[];
  payment?: StorePayment;
  shipping?: StoreShipping;
  statusHistory?: StoreOrderStatusHistory[];
  shippingAddress?: StoreAddress;
  billingAddress?: StoreAddress;
  createdAt: string;
  updatedAt?: string;
};

export type StoreReview = {
  id: number;
  productId?: number;
  userId?: number;
  userName?: string;
  userAvatarUrl?: string;
  rating: number;
  title?: string;
  comment?: string;
  status?: string;
  helpfulCount?: number;
  images?: { id: number; imageUrl: string; thumbnailUrl?: string }[];
  createdAt: string;
};

export type StoreReviewSummary = {
  average: number;
  count: number;
  distribution: Record<number, number>;
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

function buildQuery(params: Record<string, unknown>) {
  const qs = Object.entries(params)
    .filter(([, v]) => v !== undefined && v !== null && v !== "")
    .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`)
    .join("&");
  return qs ? `?${qs}` : "";
}

export const storeApi = {
  categories: () => api.rest.get<StoreCategory[]>("/api/store/categories"),
  categoryTree: () => api.rest.get<StoreCategory[]>("/api/store/categories/tree"),
  categoryBySlug: (slug: string) => api.rest.get<StoreCategory>(`/api/store/categories/${slug}`),
  brands: () => api.rest.get<StoreBrand[]>("/api/store/brands"),

  products: (params: { categoryId?: number; brandId?: number; minPrice?: number; maxPrice?: number; sort?: string; page?: number; size?: number } = {}) =>
    api.rest.get<PageResponse<StoreProductListItem>>(`/api/store/products${buildQuery({ page: 0, size: 20, ...params })}`),
  searchProducts: (query: string, page = 0, size = 20) =>
    api.rest.get<PageResponse<StoreProductListItem>>(`/api/store/products/search${buildQuery({ query, page, size })}`),
  featuredProducts: (limit = 10) => api.rest.get<StoreProductListItem[]>(`/api/store/products/featured?limit=${limit}`),
  newProducts: (limit = 10) => api.rest.get<StoreProductListItem[]>(`/api/store/products/new?limit=${limit}`),
  bestSellers: (limit = 10) => api.rest.get<StoreProductListItem[]>(`/api/store/products/bestsellers?limit=${limit}`),
  discountedProducts: (limit = 10) => api.rest.get<StoreProductListItem[]>(`/api/store/products/discounted?limit=${limit}`),
  productBySlug: (slug: string) => api.rest.get<StoreProduct>(`/api/store/products/${slug}`),

  cart: () => api.rest.get<StoreCartSummary>("/api/store/cart"),
  addToCart: (body: { productId: number; variantId?: number; quantity: number }) =>
    api.rest.post<StoreCartSummary>("/api/store/cart/items", body),
  updateCartItem: (id: number, quantity: number) => api.rest.put<StoreCartSummary>(`/api/store/cart/items/${id}?quantity=${quantity}`, {}),
  removeCartItem: (id: number) => api.rest.delete<StoreCartSummary>(`/api/store/cart/items/${id}`),
  clearCart: () => api.rest.delete<StoreCartSummary>("/api/store/cart"),

  addresses: () => api.rest.get<StoreAddress[]>("/api/store/addresses"),
  address: (id: number) => api.rest.get<StoreAddress>(`/api/store/addresses/${id}`),
  createAddress: (body: StoreAddress) => api.rest.post<StoreAddress>("/api/store/addresses", body),
  updateAddress: (id: number, body: StoreAddress) => api.rest.put<StoreAddress>(`/api/store/addresses/${id}`, body),
  deleteAddress: (id: number) => api.rest.delete(`/api/store/addresses/${id}`),

  checkoutSummary: () => api.rest.get<StoreCartSummary>("/api/store/checkout/summary"),
  checkout: (body: { shippingAddressId: number; billingAddressId?: number; couponCode?: string; customerNote?: string; paymentProvider: string; idempotencyKey: string }) =>
    api.rest.post<StoreOrder>("/api/store/checkout", body),

  paymentOrder: (orderNumber: string) => api.rest.get<StoreOrder>(`/api/store/payments/order/${orderNumber}`),
  initiatePayment: (body: { orderNumber: string; paymentMethod: string; cardHolderName?: string; cardNumber?: string; expireMonth?: string; expireYear?: string; cvc?: string; idempotencyKey: string }) =>
    api.rest.post<StorePaymentInitiateResponse>("/api/store/payments/initiate", body),
  paymentCallback: (orderNumber: string, params: Record<string, string>) =>
    api.rest.post<StoreOrder>(`/api/store/payments/callback/${orderNumber}${buildQuery(params)}`, {}),
  createBankTransfer: (body: { orderNumber: string; receiptUrl: string }) =>
    api.rest.post<StoreBankTransfer>("/api/store/payments/bank-transfer", body),

  orders: (status?: string, page = 0, size = 20) => api.rest.get<PageResponse<StoreOrder>>(`/api/store/orders${buildQuery({ status, page, size })}`),
  order: (id: number) => api.rest.get<StoreOrder>(`/api/store/orders/${id}`),
  cancelOrder: (id: number, reason?: string) => api.rest.post<StoreOrder>(`/api/store/orders/${id}/cancel${reason ? `?reason=${encodeURIComponent(reason)}` : ""}`, {}),
  orderShipping: (orderId: number) => api.rest.get<StoreShipping>(`/api/store/orders/${orderId}/shipping`),

  reviews: (productId: number, params: { rating?: number; page?: number; size?: number } = {}) =>
    api.rest.get<PageResponse<StoreReview>>(`/api/store/products/${productId}/reviews${buildQuery({ page: 0, size: 10, ...params })}`),
  reviewSummary: (productId: number) => api.rest.get<StoreReviewSummary>(`/api/store/products/${productId}/reviews/summary`),
  createReview: (productId: number, body: { rating: number; title?: string; comment?: string; orderItemId: number; imageUrls?: string[] }) =>
    api.rest.post<StoreReview>(`/api/store/products/${productId}/reviews`, body),

  wishlist: (page = 0, size = 20) => api.rest.get<PageResponse<StoreProductListItem>>(`/api/store/wishlist${buildQuery({ page, size })}`),
  addWishlist: (productId: number) => api.rest.post(`/api/store/wishlist/${productId}`, {}),
  removeWishlist: (productId: number) => api.rest.delete(`/api/store/wishlist/${productId}`),
  isWishlisted: (productId: number) => api.rest.get<boolean>(`/api/store/wishlist/${productId}`),
};
