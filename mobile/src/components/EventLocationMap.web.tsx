import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { useEffect, useRef } from "react";
import { colors } from "../theme/colors";

export type MapCoordinate = {
  latitude: number;
  longitude: number;
};

type EventLocationMapProps = {
  coordinate: MapCoordinate;
  session: number;
  onChange: (coordinate: MapCoordinate) => void;
};

const PIN_HTML = `<svg xmlns="http://www.w3.org/2000/svg" width="42" height="42" viewBox="0 0 42 42">
  <path d="M21 2C12.7 2 6 8.7 6 17c0 10 15 23 15 23s15-13 15-23C36 8.7 29.3 2 21 2z" fill="#7047d7" stroke="#ffffff" stroke-width="2.4"/>
  <circle cx="21" cy="17" r="7" fill="#ffffff"/>
</svg>`;

const pinIcon = L.divIcon({
  className: "bialem-map-pin",
  html: PIN_HTML,
  iconSize: [42, 42],
  iconAnchor: [21, 42],
  popupAnchor: [0, -40]
});

export function EventLocationMap({ coordinate, session, onChange }: EventLocationMapProps) {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const mapRef = useRef<L.Map | null>(null);
  const markerRef = useRef<L.Marker | null>(null);
  const onChangeRef = useRef(onChange);
  onChangeRef.current = onChange;

  useEffect(() => {
    const container = containerRef.current;
    if (!container) return;

    const map = L.map(container, {
      attributionControl: true,
      zoomControl: true
    });

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      maxZoom: 19,
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    const marker = L.marker([coordinate.latitude, coordinate.longitude], {
      draggable: true,
      icon: pinIcon
    }).addTo(map);

    marker.on("dragend", () => {
      const latlng = marker.getLatLng();
      onChangeRef.current({ latitude: latlng.lat, longitude: latlng.lng });
    });

    map.on("click", (event: L.LeafletMouseEvent) => {
      onChangeRef.current({ latitude: event.latlng.lat, longitude: event.latlng.lng });
    });

    map.setView([coordinate.latitude, coordinate.longitude], 15);

    const resize = () => requestAnimationFrame(() => map.invalidateSize());
    const timer = setTimeout(resize, 80);
    const observer = typeof ResizeObserver !== "undefined" ? new ResizeObserver(resize) : null;
    observer?.observe(container);

    mapRef.current = map;
    markerRef.current = marker;

    return () => {
      map.off();
      map.remove();
      observer?.disconnect();
      clearTimeout(timer);
      mapRef.current = null;
      markerRef.current = null;
    };
  }, [session]);

  useEffect(() => {
    const map = mapRef.current;
    const marker = markerRef.current;
    if (!map || !marker) return;
    const next = L.latLng(coordinate.latitude, coordinate.longitude);
    if (!marker.getLatLng().equals(next)) {
      marker.setLatLng(next);
    }
  }, [coordinate.latitude, coordinate.longitude, session]);

  return (
    <div ref={containerRef} style={containerStyle}>
      <button
        type="button"
        onClick={() => {
          const map = mapRef.current;
          if (!map || typeof navigator === "undefined" || !navigator.geolocation) return;
          navigator.geolocation.getCurrentPosition(
            (position) => {
              onChangeRef.current({ latitude: position.coords.latitude, longitude: position.coords.longitude });
              map.setView([position.coords.latitude, position.coords.longitude], 16);
            },
            () => undefined,
            { enableHighAccuracy: true, timeout: 12000 }
          );
        }}
        style={locateButtonStyle}
        aria-label="Konumumu kullan"
        title="Konumumu kullan"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#7047d7" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="12" cy="12" r="3" />
          <path d="M12 2v3M12 19v3M2 12h3M19 12h3" />
        </svg>
      </button>
    </div>
  );
}

const containerStyle: React.CSSProperties = {
  position: "absolute",
  inset: 0
};

const locateButtonStyle: React.CSSProperties = {
  position: "absolute",
  right: 14,
  bottom: 120,
  zIndex: 500,
  width: 46,
  height: 46,
  borderRadius: 23,
  border: "none",
  cursor: "pointer",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  backgroundColor: colors.surface as string,
  boxShadow: "0 2px 8px rgba(10,24,51,0.22)"
};