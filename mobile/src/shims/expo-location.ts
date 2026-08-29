export async function requestForegroundPermissionsAsync() {
  return { status: "granted", granted: true };
}

export async function getCurrentPositionAsync(_options?: { accuracy?: number }): Promise<{ coords: { latitude: number; longitude: number } }> {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error("Konum desteklenmiyor"));
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (position) => resolve({ coords: position.coords }),
      reject
    );
  });
}

export const Accuracy = {
  Balanced: 3
} as const;

export type LocationGeocodedAddress = {
  name?: string;
  street?: string;
  district?: string;
  city?: string;
  region?: string;
  country?: string;
};

type GeocodedAddress = LocationGeocodedAddress;

export async function reverseGeocodeAsync({ latitude, longitude }: { latitude: number; longitude: number }): Promise<GeocodedAddress[]> {
  const response = await fetch(`https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${latitude}&lon=${longitude}`, {
    headers: { Accept: "application/json" }
  });
  if (!response.ok) return [];
  const result = await response.json() as { name?: string; display_name?: string; address?: Record<string, string> };
  const address = result.address ?? {};
  return [{
    name: result.name,
    street: address.road ?? address.pedestrian,
    district: address.suburb ?? address.district,
    city: address.city ?? address.town ?? address.village,
    region: address.state,
    country: address.country
  }];
}

export async function geocodeAsync(query: string): Promise<Array<{ latitude: number; longitude: number }>> {
  const response = await fetch(`https://nominatim.openstreetmap.org/search?format=jsonv2&limit=5&q=${encodeURIComponent(query)}`, {
    headers: { Accept: "application/json" }
  });
  if (!response.ok) return [];
  const results = await response.json() as Array<{ lat: string; lon: string }>;
  return results.map((item) => ({ latitude: Number(item.lat), longitude: Number(item.lon) }));
}

export default {
  Accuracy,
  requestForegroundPermissionsAsync,
  getCurrentPositionAsync,
  reverseGeocodeAsync,
  geocodeAsync
};
