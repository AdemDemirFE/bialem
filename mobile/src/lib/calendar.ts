import { Platform } from "react-native";
import { eventPublicUrl } from "./links";

type CalendarEvent = {
  id: string;
  title: string;
  description: string | null;
  starts_at: string;
  ends_at: string | null;
  location_name: string | null;
  address_text: string | null;
  public_url?: string | null;
};

export async function addEventToCalendar(event: CalendarEvent) {
  const startDate = new Date(event.starts_at);
  const endDate = event.ends_at ? new Date(event.ends_at) : new Date(startDate.getTime() + 2 * 60 * 60 * 1000);
  const location = [event.location_name, event.address_text].filter(Boolean).join(" - ");
  const url = event.public_url || eventPublicUrl(event.id);
  const formatGoogleDate = (date: Date) => date.toISOString().replace(/[-:]/g, "").replace(/\.\d{3}/, "");
  const query = new URLSearchParams({
    action: "TEMPLATE",
    text: event.title,
    dates: `${formatGoogleDate(startDate)}/${formatGoogleDate(endDate)}`,
    details: `${event.description || "Bialem etkinliği"}\n${url}`,
    location
  });
  const href = `https://calendar.google.com/calendar/render?${query.toString()}`;
  if (typeof window !== "undefined") window.open(href, "_blank");
  else if (Platform.OS !== "web") console.info(href);
}
