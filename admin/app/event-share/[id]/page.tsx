import type { Metadata } from "next";
import { EventShareBridge } from "./event-share-bridge";

export const metadata: Metadata = {
  title: "Etkinliği aç | Bialem",
  description: "Paylaşılan etkinliği Bialem uygulamasında görüntüle."
};

export default async function EventSharePage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  return <EventShareBridge eventId={id} />;
}
