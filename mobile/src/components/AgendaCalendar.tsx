import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import listPlugin from "@fullcalendar/list";
import interactionPlugin from "@fullcalendar/interaction";
import type { DatesSetArg, EventClickArg, EventContentArg } from "@fullcalendar/core";
import { Ionicons } from "@expo/vector-icons";
import { useMemo, useRef, useState } from "react";
import { router } from "../lib/router";
import { colors } from "../theme/colors";

export type AgendaPlan = {
  event_id: string; title: string; starts_at: string; ends_at: string | null;
  location_name: string | null; event_status: string; participation_status: string;
  community_name: string; event_type?: string; source?: "participating" | "community";
  route?: string;
  description?: string | null; image_url?: string | null; price_label?: string | null; username?: string | null; calculated_age?: number | null;
};

type Props = { plans: AgendaPlan[]; onRangeChange: (start: string, end: string) => void };
const views = [
  { key: "dayGridMonth", label: "Ay" }, { key: "timeGridWeek", label: "Hafta" },
  { key: "timeGridDay", label: "Gün" }, { key: "listMonth", label: "Ajanda" }
] as const;

export function AgendaCalendar({ plans, onRangeChange }: Props) {
  const calendarRef = useRef<FullCalendar | null>(null);
  const touchStart = useRef<number | null>(null);
  const [activeView, setActiveView] = useState<(typeof views)[number]["key"]>("dayGridMonth");
  const [title, setTitle] = useState("");
  const [selectedDate, setSelectedDate] = useState<string | null>(null);
  const [failedImages, setFailedImages] = useState<Set<string>>(new Set());
  const events = useMemo(() => plans.map((plan) => ({ id: plan.event_id, title: plan.title, start: plan.starts_at,
    end: plan.ends_at || undefined, backgroundColor: eventColor(plan), borderColor: eventColor(plan), textColor: "#fff",
    extendedProps: { plan } })), [plans]);
  const selectedPlans = useMemo(() => selectedDate ? plans.filter((p) => localDateKey(p.starts_at) === selectedDate)
    .sort((a, b) => Date.parse(a.starts_at) - Date.parse(b.starts_at)) : [], [plans, selectedDate]);
  const openEvent = (id: string) => { const plan = plans.find(item => item.event_id === id); router.push((plan?.route || `/event/${id}`) as never); };

  return <section className="agenda-shell">
    <header className="agenda-toolbar">
      <div><small className="agenda-eyebrow">AJANDA</small><h2 className="agenda-title">{title}</h2></div>
      <nav className="agenda-nav" aria-label="Takvim gezinme">
        <button type="button" aria-label="Önceki dönem" className="agenda-icon-button" onClick={() => calendarRef.current?.getApi().prev()}><Ionicons name="chevron-back" size={18} color={colors.ink} /></button>
        <button type="button" className="agenda-today" onClick={() => calendarRef.current?.getApi().today()}>Bugün</button>
        <button type="button" aria-label="Sonraki dönem" className="agenda-icon-button" onClick={() => calendarRef.current?.getApi().next()}><Ionicons name="chevron-forward" size={18} color={colors.ink} /></button>
      </nav>
    </header>
    <div className="agenda-view-switcher" role="tablist">
      {views.map((view) => <button type="button" key={view.key} role="tab" aria-selected={activeView === view.key}
        className={`agenda-view-chip ${activeView === view.key ? "is-active" : ""}`}
        onClick={() => { calendarRef.current?.getApi().changeView(view.key); setActiveView(view.key); }}>{view.label}</button>)}
    </div>
    <div className="agenda-calendar" onTouchStart={(e) => { touchStart.current = e.changedTouches[0]?.clientX ?? null; }}
      onTouchEnd={(e) => { if (touchStart.current == null) return; const delta = (e.changedTouches[0]?.clientX ?? touchStart.current) - touchStart.current;
        if (Math.abs(delta) > 60) delta < 0 ? calendarRef.current?.getApi().next() : calendarRef.current?.getApi().prev(); touchStart.current = null; }}>
      <FullCalendar ref={calendarRef} plugins={[dayGridPlugin,timeGridPlugin,listPlugin,interactionPlugin]} initialView="dayGridMonth"
        locale="tr" firstDay={1} headerToolbar={false} height="auto" dayMaxEvents={2} nowIndicator allDayText="Tüm gün"
        noEventsText="Bu aralıkta etkinlik yok" slotMinTime="07:00:00" slotMaxTime="23:00:00" events={events}
        datesSet={(arg: DatesSetArg) => { setTitle(arg.view.title); onRangeChange(arg.start.toISOString(), arg.end.toISOString()); }}
        dateClick={(arg) => setSelectedDate(arg.dateStr.slice(0,10))} eventClick={(arg: EventClickArg) => openEvent(arg.event.id)}
        eventContent={(arg: EventContentArg) => <span className="agenda-event-content"><i>{eventIcon(arg.event.extendedProps.plan as AgendaPlan)}</i>{arg.timeText && <b>{arg.timeText}</b>}<span>{arg.event.title}</span></span>} />
    </div>
    {selectedDate && <div className="agenda-modal-backdrop" role="presentation" onClick={() => setSelectedDate(null)}>
      <section className="agenda-sheet" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()}>
        <div className="agenda-sheet-handle" /><header className="agenda-sheet-header"><div><small className="agenda-eyebrow">SEÇİLEN GÜN</small><h3 className="agenda-sheet-title">{formatSelectedDate(selectedDate)} • Şehrinde Neler Var?</h3></div>
        <button type="button" className="agenda-icon-button" onClick={() => setSelectedDate(null)}><Ionicons name="close" size={19} color={colors.ink} /></button></header>
        {selectedPlans.length === 0 ? <div className="agenda-day-empty"><Ionicons name="calendar-outline" size={28} color={colors.aqua} /><span>Bu güne ait etkinlik bulunmuyor.</span></div>
          : selectedPlans.map((plan) => <button type="button" key={plan.event_id} className="agenda-day-event" onClick={() => openEvent(plan.event_id)}>
            {plan.image_url && !failedImages.has(plan.event_id) ? <img className="agenda-day-image" src={plan.image_url} alt="" loading="lazy" onError={() => setFailedImages(current => new Set(current).add(plan.event_id))} /> : <span className="agenda-day-icon" style={{ backgroundColor: eventColor(plan) }}><Ionicons name={plan.event_type === "BIRTHDAY" ? "gift-outline" : plan.event_type === "CITY_EVENT" ? "location-outline" : "calendar-outline"} size={19} color="#fff" /></span>}
            <span className="agenda-day-copy"><small className="agenda-day-time">{plan.event_type === "BIRTHDAY" ? `@${plan.username || "bialem"} · ${plan.calculated_age || "Yeni"} yaşında` : `${formatTime(plan.starts_at)} · ${plan.community_name || "Bialem"}`}</small>
            <strong className="agenda-day-title">{plan.title}</strong>{plan.description ? <small className="agenda-day-description">{plan.description}</small> : null}<small className="agenda-day-meta">{[plan.location_name,plan.price_label].filter(Boolean).join(" · ") || participationLabel(plan.participation_status)}</small></span><Ionicons name="chevron-forward" size={18} color={colors.muted} /></button>)}
      </section></div>}
  </section>;
}

function eventColor(p: AgendaPlan) { if (p.event_type === "BIRTHDAY") return String(colors.action); if (p.event_type === "CITY_EVENT") return String(colors.aqua); if (p.event_status === "cancelled") return String(colors.danger); if (["approved","checked_in"].includes(p.participation_status)) return String(colors.success); if (["pending","waitlisted"].includes(p.participation_status)) return String(colors.action); return String(colors.ink); }
function eventIcon(p: AgendaPlan) { if (p.event_status === "cancelled") return "×"; if (["approved","checked_in"].includes(p.participation_status)) return "✓"; return p.source === "community" ? "◆" : "•"; }
function localDateKey(value: string) { const parts=new Intl.DateTimeFormat("en",{timeZone:"Europe/Istanbul",year:"numeric",month:"2-digit",day:"2-digit"}).formatToParts(new Date(value));const part=(type:Intl.DateTimeFormatPartTypes)=>parts.find(item=>item.type===type)?.value??"";return `${part("year")}-${part("month")}-${part("day")}`; }
function formatSelectedDate(value: string) { return new Date(`${value}T12:00:00`).toLocaleDateString("tr-TR", { weekday:"long",day:"numeric",month:"long" }); }
function formatTime(value: string) { return new Date(value).toLocaleTimeString("tr-TR", { timeZone:"Europe/Istanbul",hour:"2-digit",minute:"2-digit" }); }
function participationLabel(status: string) { if (status === "approved") return "Katılım onaylandı"; if (status === "checked_in") return "Katıldın"; if (status === "community") return "Topluluk etkinliği"; return "Onay bekliyor"; }
