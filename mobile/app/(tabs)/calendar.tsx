import { Ionicons } from "@expo/vector-icons";
import { useCallback, useState } from "react";
import { useRouter } from "expo-router";
import { AgendaCalendar, type AgendaPlan } from "../../src/components/AgendaCalendar";
import { api } from "../../src/lib/api";
import { getCityRadar, istanbulDateKey, istanbulTime, type CityRadarCalendarEvent } from "../../src/lib/city-radar-api";
import { colors } from "../../src/theme/colors";

type CalendarItem={type:"BIALEM_EVENT"|"CITY_EVENT"|"BIRTHDAY";referenceId:string;title:string;description:string|null;date:string;startsAt:string|null;endsAt:string|null;imageUrl:string|null;location:string|null;category:string|null;priceLabel:string|null;route:string;participantCount:number|null;username:string|null;calculatedAge:number|null};
type CalendarResponse={counts:{bialemEvents:number;cityEvents:number;birthdays:number;total:number};items:CalendarItem[];upcoming:CalendarItem[]};

export default function CalendarScreen() {
  const router=useRouter();
  const [plans,setPlans]=useState<AgendaPlan[]>([]);
  const [counts,setCounts]=useState({bialemEvents:0,cityEvents:0,birthdays:0,total:0});
  const [upcoming,setUpcoming]=useState<CalendarItem[]>([]);
  const [loading,setLoading]=useState(false);
  const [error,setError]=useState<string|null>(null);

  const load=useCallback(async(start:string,end:string)=>{
    setLoading(true);setError(null);
    const startDate=istanbulDateKey(start),endDate=istanbulDateKey(new Date(new Date(end).getTime()-86400000).toISOString());
    const [calendarResult,radarResult]=await Promise.allSettled([
      api.rest.get<CalendarResponse>(`/api/app/calendar?startDate=${startDate}&endDate=${endDate}`),
      getCityRadar()
    ]);
    const calendar=calendarResult.status==="fulfilled"?calendarResult.value:null;
    const rangeStart=Date.parse(start),rangeEnd=Date.parse(end);
    const radar=(radarResult.status==="fulfilled"?radarResult.value:[]).filter(event=>{
      const time=Date.parse(event.startsAt);return time>=rangeStart&&time<rangeEnd;
    });
    if(!calendar&&radarResult.status==="rejected") setError(calendarResult.status==="rejected"&&calendarResult.reason instanceof Error?calendarResult.reason.message:"Takvim yüklenemedi");

    const baseItems=calendar?.items??[];
    const radarIds=new Set(radar.map(event=>event.referenceId));
    const mergedItems=[...baseItems.filter(item=>item.type!=="CITY_EVENT"||!radarIds.has(String(item.referenceId))),...radar.map(toCalendarItem)];
    const uniqueItems=[...new Map(mergedItems.map(item=>[`${item.type}:${item.referenceId}`,item])).values()];
    const sorted=uniqueItems.sort((a,b)=>Date.parse(a.startsAt??`${a.date}T12:00:00`)-Date.parse(b.startsAt??`${b.date}T12:00:00`));
    const now=Date.now();
    setPlans(sorted.map(toAgendaPlan));
    setUpcoming(sorted.filter(item=>item.startsAt&&Date.parse(item.startsAt)>=now).slice(0,10));
    const bialemEvents=sorted.filter(item=>item.type==="BIALEM_EVENT").length,cityEvents=sorted.filter(item=>item.type==="CITY_EVENT").length,birthdays=sorted.filter(item=>item.type==="BIRTHDAY").length;
    setCounts({bialemEvents,cityEvents,birthdays,total:sorted.length});
    setLoading(false);
  },[]);

  return <main className="planner-page"><section className="planner-intro"><div className="planner-intro-icon"><Ionicons name="calendar" size={20} color="#fff"/></div><div className="planner-intro-copy"><small className="planner-kicker">BİALEM GENEL TAKVİM</small><h1 className="planner-heading">Şehrinde neler var?</h1><p className="planner-description">Bialem etkinlikleri, şehir keşifleri ve doğum günleri tek takvimde.</p></div></section>
    <section className="calendar-counts"><span>Bialem Etkinlikleri <b>{counts.bialemEvents}</b></span><span>Şehir Etkinlikleri <b>{counts.cityEvents}</b></span><span>Doğum Günleri <b>{counts.birthdays}</b></span></section>
    {error?<section className="planner-state planner-error-state"><Ionicons name="cloud-offline-outline" size={30} color={colors.danger}/><h2 className="planner-state-title">Takvim yüklenemedi</h2><p className="planner-state-copy">{error}</p></section>:<><AgendaCalendar plans={plans} onRangeChange={load}/>{loading?<div className="planner-refresh-pill"><i className="planner-pulse"/><span>Takvim güncelleniyor</span></div>:null}</>}
    {upcoming.length?<section className="planner-upcoming"><h2 className="planner-section-title">Yaklaşanlar</h2><div className="calendar-upcoming-scroll">{upcoming.map(item=><button type="button" key={`${item.type}:${item.referenceId}`} className="calendar-upcoming-card" onClick={()=>router.push(item.route as never)}><CalendarCover item={item}/><small>{typeLabel(item.type)} · {formatUpcoming(item.startsAt)}</small><strong>{item.title}</strong><span>{item.location||item.category||"Detayları görüntüle"}</span></button>)}</div></section>:null}
  </main>;
}

function toCalendarItem(event:CityRadarCalendarEvent):CalendarItem{return {type:"CITY_EVENT",referenceId:event.referenceId,title:event.title,description:event.description,date:istanbulDateKey(event.startsAt),startsAt:event.startsAt,endsAt:event.endsAt,imageUrl:event.coverImageUrl,location:event.venueName,category:event.category,priceLabel:event.priceLabel,route:`/city-event/${event.referenceId}`,participantCount:event.interestedCount,username:null,calculatedAge:null};}
function toAgendaPlan(item:CalendarItem):AgendaPlan{return {event_id:`${item.type}:${item.referenceId}`,title:item.title,starts_at:item.startsAt||`${item.date}T12:00:00`,ends_at:item.endsAt,location_name:item.location,event_status:"published",participation_status:item.type==="BIRTHDAY"?"birthday":"discovery",community_name:item.category||typeLabel(item.type),event_type:item.type,source:"community",route:item.route,description:item.description,image_url:item.imageUrl,price_label:item.priceLabel,username:item.username,calculated_age:item.calculatedAge};}
function typeLabel(type:CalendarItem["type"]){return type==="CITY_EVENT"?"Şehir Etkinliği":type==="BIRTHDAY"?"Doğum Günü":"Bialem Etkinliği";}
function formatUpcoming(value:string|null){if(!value)return "";return `${new Intl.DateTimeFormat("tr-TR",{timeZone:"Europe/Istanbul",day:"numeric",month:"long"}).format(new Date(value))} · ${istanbulTime(value)}`;}
function CalendarCover({item}:{item:CalendarItem}){const[failed,setFailed]=useState(false);return item.imageUrl&&!failed?<img src={item.imageUrl} alt="" loading="lazy" onError={()=>setFailed(true)}/>:<span className="calendar-upcoming-fallback"><Ionicons name={item.type==="CITY_EVENT"?"location":"calendar"} size={24} color={colors.accent}/></span>;}
