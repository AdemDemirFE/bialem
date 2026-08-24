import { api } from "./api";

export interface CityRadarEventDTO {
  id?: string | number;
  event_id?: string | number;
  title?: string | null;
  description?: string | null;
  category?: string | null;
  city?: string | null;
  venue_name?: string | null;
  address_text?: string | null;
  starts_at?: string | null;
  ends_at?: string | null;
  cover_image_url?: string | null;
  price_label?: string | null;
  source_name?: string | null;
  source_url?: string | null;
  ticket_url?: string | null;
  status?: string | null;
  provider_code?: string | null;
  external_id?: string | null;
  interested_count?: number | null;
  companion_count?: number | null;
  is_interested?: boolean | null;
  is_looking_for_company?: boolean | null;
  raw_payload?: unknown;
}

export interface CityRadarCalendarEvent {
  id: string;
  referenceId: string;
  title: string;
  description: string | null;
  startsAt: string;
  endsAt: string | null;
  city: string | null;
  venueName: string | null;
  addressText: string | null;
  coverImageUrl: string | null;
  category: string | null;
  priceLabel: string | null;
  sourceName: string | null;
  sourceUrl: string | null;
  ticketUrl: string | null;
  providerCode: string | null;
  externalId: string | null;
  interestedCount: number;
  companionCount: number;
  isInterested: boolean;
  isLookingForCompany: boolean;
  sourceType: "CITY_RADAR";
}

export function mapCityRadarEvent(dto: CityRadarEventDTO): CityRadarCalendarEvent | null {
  const referenceId = String(dto.event_id ?? dto.id ?? "").trim();
  const title = String(dto.title ?? "").trim();
  const startsAt = validInstant(dto.starts_at) ?? rawPayloadInstant(dto.raw_payload);
  const status = String(dto.status ?? "PUBLISHED").toUpperCase();
  if (!referenceId || !title || !startsAt || status !== "PUBLISHED") return null;
  return {
    id: `CITY_RADAR:${referenceId}`, referenceId, title, startsAt,
    description: dto.description ?? null, endsAt: validInstant(dto.ends_at), city: dto.city ?? null,
    venueName: dto.venue_name ?? null, addressText: dto.address_text ?? null,
    coverImageUrl: dto.cover_image_url ?? null, category: dto.category ?? null,
    priceLabel: dto.price_label ?? null, sourceName: dto.source_name ?? null,
    sourceUrl: dto.source_url ?? null, ticketUrl: dto.ticket_url ?? null,
    providerCode: dto.provider_code ?? null, externalId: dto.external_id ?? null,
    interestedCount: dto.interested_count ?? 0, companionCount: dto.companion_count ?? 0,
    isInterested: dto.is_interested ?? false, isLookingForCompany: dto.is_looking_for_company ?? false,
    sourceType: "CITY_RADAR"
  };
}

export async function getCityRadar(city?: string | null): Promise<CityRadarCalendarEvent[]> {
  const result = await api.rpc("get_city_radar", { target_city: city?.trim() || null });
  if (result.error) throw new Error(result.error.message);
  const payload = Array.isArray(result.data) ? result.data : [];
  const unique = new Map<string, CityRadarCalendarEvent>();
  for (const dto of payload as CityRadarEventDTO[]) {
    const mapped = mapCityRadarEvent(dto);
    if (mapped) unique.set(mapped.id, mapped);
  }
  return [...unique.values()].sort((a, b) => Date.parse(a.startsAt) - Date.parse(b.startsAt));
}

function validInstant(value?: string | null) {
  if (!value) return null;
  const time = Date.parse(value);
  return Number.isFinite(time) ? new Date(time).toISOString() : null;
}

function rawPayloadInstant(raw: unknown) {
  try {
    const payload = typeof raw === "string" ? JSON.parse(raw) : raw;
    const candidate = (payload as { dates?: { start?: { dateTime?: string } } } | null)?.dates?.start?.dateTime;
    return validInstant(candidate);
  } catch { return null; }
}

export function istanbulDateKey(value: string) {
  const parts = new Intl.DateTimeFormat("en", { timeZone: "Europe/Istanbul", year: "numeric", month: "2-digit", day: "2-digit" }).formatToParts(new Date(value));
  const part = (type: Intl.DateTimeFormatPartTypes) => parts.find(item => item.type === type)?.value ?? "";
  return `${part("year")}-${part("month")}-${part("day")}`;
}

export function istanbulTime(value: string) {
  return new Intl.DateTimeFormat("tr-TR", { timeZone: "Europe/Istanbul", hour: "2-digit", minute: "2-digit", hour12: false }).format(new Date(value));
}
