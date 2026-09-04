/**
 * API client for the Bialem Spring Boot backend.
 *
 * All admin endpoints require ROLE_ADMIN or ROLE_SUPER_ADMIN.
 * The /api/authenticate endpoint is public and returns a JWT.
 */

const API_BASE = "/api";

// ─── Auth ────────────────────────────────────────────────────────────

let token: string | null = localStorage.getItem("sa_token");

export function getToken(): string | null {
  return token;
}

export function setToken(t: string | null) {
  token = t;
  if (t) localStorage.setItem("sa_token", t);
  else localStorage.removeItem("sa_token");
}

export async function authenticate(
  username: string,
  password: string
): Promise<string> {
  const res = await fetch(`${API_BASE}/authenticate`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password, rememberMe: true }),
  });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.detail || body.title || "Giriş başarısız");
  }
  const data = await res.json();
  setToken(data.id_token);
  return data.id_token;
}

export async function getAccount(): Promise<AccountInfo> {
  return request<AccountInfo>("/account");
}

export async function getAdminContext(): Promise<AdminContext> {
  return request<AdminContext>("/admin/context");
}

// ─── Generic request helpers ─────────────────────────────────────────

export async function request<T>(
  path: string,
  init: RequestInit & { json?: unknown } = {}
): Promise<T> {
  const headers = new Headers(init.headers);
  if (init.json !== undefined && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  if (token) headers.set("Authorization", `Bearer ${token}`);

  const res = await fetch(`${API_BASE}${path}`, {
    ...init,
    headers,
    body: init.json !== undefined ? JSON.stringify(init.json) : init.body,
  });

  if (res.status === 204) return undefined as T;

  const ct = res.headers.get("content-type") || "";
  const payload = ct.includes("application/json")
    ? await res.json().catch(() => null)
    : await res.text().catch(() => null);

  if (!res.ok) {
    const msg =
      payload?.detail || payload?.title || payload?.message || `API hatası (${res.status})`;
    throw new ApiError(msg, res.status, payload);
  }

  return payload as T;
}

export class ApiError extends Error {
  status: number;
  body: unknown;
  constructor(message: string, status: number, body: unknown) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.body = body;
  }
}

// ─── Pagination helpers ──────────────────────────────────────────────

export interface PageResult<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export async function getPage<T>(
  path: string,
  params: Record<string, string | number | boolean | undefined> = {}
): Promise<PageResult<T>> {
  const qs = new URLSearchParams();
  for (const [k, v] of Object.entries(params)) {
    if (v !== undefined && v !== "") qs.set(k, String(v));
  }
  const sep = path.includes("?") ? "&" : "?";
  return request<PageResult<T>>(`${path}${sep}${qs.toString()}`);
}

export async function getCount(
  path: string,
  params: Record<string, string | undefined> = {}
): Promise<number> {
  const qs = new URLSearchParams();
  for (const [k, v] of Object.entries(params)) {
    if (v) qs.set(k, v);
  }
  const sep = path.includes("?") ? "&" : "?";
  return request<number>(`${path}${sep}${qs.toString()}`);
}

// ─── Types ───────────────────────────────────────────────────────────

export interface AccountInfo {
  id: number;
  login: string;
  firstName: string;
  lastName: string;
  email: string;
  imageUrl: string | null;
  activated: boolean;
  langKey: string;
  authorities: string[];
}

export interface AdminContext {
  admin: boolean;
  superAdmin: boolean;
  authorities: string[];
  permissions: string[];
}

// Entity DTOs (aligned with backend)
export interface AdminUserDTO {
  id: number;
  login: string;
  firstName: string;
  lastName: string;
  email: string;
  imageUrl: string | null;
  activated: boolean;
  langKey: string;
  createdBy: string;
  createdDate: string;
  lastModifiedBy: string;
  lastModifiedDate: string;
  authorities: string[];
}

export interface ProfileDTO {
  id: number;
  displayName: string;
  username: string;
  avatarUrl: string | null;
  bio: string | null;
  city: string | null;
  status: string;
  isVerified: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface EventDTO {
  id: number;
  title: string;
  description: string | null;
  startsAt: string;
  endsAt: string | null;
  locationName: string | null;
  addressText: string | null;
  latitude: number | null;
  longitude: number | null;
  coverImageUrl: string | null;
  capacity: number | null;
  status: string;
  rejectionReason: string | null;
  publishedAt: string | null;
  publishedToDiscovery: boolean;
  groupModerationStatus: string;
  platformModerationStatus: string;
  cancelledAt: string | null;
  cancellationReason: string | null;
  createdAt: string;
  updatedAt: string | null;
  community: CommunityDTO | null;
  category: CommunityDTO | null;
  createdBy: ProfileDTO | null;
  cancelledBy: ProfileDTO | null;
}

export interface StoreShippingRequest {
  carrier: string;
  trackingNumber: string;
  estimatedDeliveryDate?: string | null;
}

export interface CommunityDTO {
  id: number;
  name: string;
  slug: string;
  description: string | null;
  visibility: string;
  communityType: string;
  partnerTrustLevel: string;
  isVerifiedPartner: boolean;
  isDiscoverable: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface StoreProductListDTO {
  id: number;
  name: string;
  slug: string;
  shortDescription: string | null;
  price: number;
  discountedPrice: number | null;
  currency: string;
  primaryImageUrl: string | null;
  categoryName: string | null;
  ratingAverage: number | null;
  reviewCount: number;
  salesCount: number;
  inStock: boolean;
}

export interface StoreProductImageDTO {
  id: number;
  imageUrl: string;
  thumbnailUrl: string | null;
  sortOrder: number;
  isPrimary: boolean;
  altText: string | null;
}

export interface StoreProductDTO {
  id: number;
  name: string;
  slug: string;
  shortDescription: string | null;
  description: string | null;
  sku: string | null;
  barcode: string | null;
  price: number;
  discountedPrice: number | null;
  currency: string;
  stockQuantity: number;
  lowStockThreshold: number | null;
  status: string;
  isFeatured: boolean;
  isActive: boolean;
  weight: number | null;
  width: number | null;
  height: number | null;
  length: number | null;
  ratingAverage: number | null;
  reviewCount: number;
  salesCount: number;
  categoryId: number | null;
  categoryName: string | null;
  categorySlug: string | null;
  brandId: number | null;
  brandName: string | null;
  sellerId: number | null;
  sellerName: string | null;
  images: StoreProductImageDTO[];
  variants: StoreProductVariantDTO[];
  attributes: StoreProductAttributeDTO[];
  createdAt: string;
  updatedAt: string | null;
}

export interface StoreProductVariantDTO {
  id: number;
  variantName: string;
  sku: string | null;
  price: number | null;
  discountedPrice: number | null;
  stockQuantity: number;
  imageUrl: string | null;
  isActive: boolean;
}

export interface StoreProductAttributeDTO {
  id: number;
  attributeKey: string;
  attributeValue: string;
  sortOrder: number;
}

export interface StoreCategoryDTO {
  id: number;
  name: string;
  slug: string;
  description: string | null;
  imageUrl: string | null;
  sortOrder: number;
  isActive: boolean;
  parentId: number | null;
  parentName: string | null;
  children: StoreCategoryDTO[];
  createdAt: string;
  updatedAt: string | null;
}

export interface StoreBrandDTO {
  id: number;
  name: string;
  slug: string;
  description: string | null;
  logoUrl: string | null;
  isActive: boolean;
  createdAt: string;
  updatedAt: string | null;
}

export interface StoreOrderDTO {
  id: number;
  orderNumber: string;
  subtotal: number;
  discountAmount: number;
  shippingAmount: number;
  totalAmount: number;
  currency: string;
  paymentStatus: string;
  orderStatus: string;
  shippingStatus: string;
  customerNote: string | null;
  couponCode: string | null;
  createdAt: string;
  updatedAt: string | null;
}

export interface StoreOrderDetailDTO extends StoreOrderDTO {
  shippingAddress: StoreAddressDTO | null;
  billingAddress: StoreAddressDTO | null;
  items: StoreOrderItemDTO[];
  payment: StorePaymentDTO | null;
  shipping: StoreShippingDTO | null;
  statusHistory: StoreOrderStatusHistoryDTO[];
}

export interface StoreOrderItemDTO {
  id: number;
  productId: number | null;
  productName: string | null;
  productSku: string | null;
  productImage: string | null;
  quantity: number;
  unitPrice: number;
  discount: number | null;
  totalPrice: number;
  variantName: string | null;
}

export interface StoreAddressDTO {
  id: number;
  title: string | null;
  firstName: string | null;
  lastName: string | null;
  phone: string | null;
  country: string | null;
  city: string | null;
  district: string | null;
  neighborhood: string | null;
  addressLine: string | null;
  postalCode: string | null;
  note: string | null;
}

export interface StorePaymentDTO {
  id: number;
  provider: string | null;
  transactionId: string | null;
  amount: number;
  currency: string | null;
  status: string;
  paymentMethod: string | null;
  paidAt: string | null;
  failureReason: string | null;
  createdAt: string;
}

export interface StoreShippingDTO {
  id: number;
  carrier: string | null;
  trackingNumber: string | null;
  shippingStatus: string | null;
  shippedAt: string | null;
  estimatedDeliveryDate: string | null;
  deliveredAt: string | null;
}

export interface StoreOrderStatusHistoryDTO {
  id: number;
  oldStatus: string | null;
  newStatus: string;
  changedBy: string | null;
  note: string | null;
  createdAt: string;
}

export interface ReportDTO {
  id: number;
  targetType: string;
  targetId: string;
  reason: string;
  details: string | null;
  status: string;
  resolvedAt: string | null;
  createdAt: string;
  updatedAt: string;
  reporterId: number | null;
  resolvedById: number | null;
}

export interface AdminNotificationDTO {
  id: number;
  notificationId: number;
  title: string;
  body: string | null;
  notificationType: string;
  source: string | null;
  trigger: string | null;
  referenceType: string | null;
  referenceId: string | null;
  recipientUserId: number;
  firebaseStatus: string | null;
  firebaseMessageId: string | null;
  pushSuccessful: number;
  pushFailed: number;
  attemptCount: number;
  firebaseErrors: Record<string, number> | null;
  createdAt: string;
  sentAt: string | null;
  lastError: string | null;
}

export interface NotificationTemplateDTO {
  id: number;
  code: string;
  eventType: string;
  name: string;
  titleTemplate: string;
  bodyTemplate: string | null;
  routeTemplate: string | null;
  enabled: boolean;
  inAppEnabled: boolean;
  pushEnabled: boolean;
  priority: string;
  scheduleType: string;
  timezone: string | null;
  createdAt: string;
  updatedAt: string | null;
}

export interface RoleDTO {
  id: number;
  code: string;
  name: string;
  createdAt: string;
}

export interface AuthorityDTO {
  name: string;
}

export interface RadioContentDTO {
  id: number;
  title: string;
  description: string | null;
  contentType: string;
  sourceType: string;
  sourceUrl: string | null;
  audioFile: string | null;
  thumbnail: string | null;
  artist: string | null;
  album: string | null;
  duration: number | null;
  category: string | null;
  programName: string | null;
  presenter: string | null;
  publishDate: string | null;
  startDate: string | null;
  endDate: string | null;
  isActive: boolean;
  isFeatured: boolean;
  sortOrder: number | null;
  playCount: number | null;
  createdAt: string;
  updatedAt: string;
}

export interface RadioConfigDTO {
  id: number;
  radioName: string;
  slogan: string | null;
  logo: string | null;
  cover: string | null;
  liveStreamUrl: string | null;
  isLive: boolean;
  currentProgram: string | null;
  currentTrack: string | null;
  websiteUrl: string | null;
  facebookUrl: string | null;
  twitterUrl: string | null;
  instagramUrl: string | null;
  youtubeUrl: string | null;
  metadataJson: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface DashboardDTO {
  totalUsers: number;
  totalProfiles: number;
  totalEvents: number;
  totalCommunities: number;
  totalPosts: number;
  totalOrders: number;
  totalRevenue: number;
  pendingReports: number;
  activeProducts: number;
}
