import { Ionicons } from "@expo/vector-icons";
import { useCallback, useEffect, useRef, useState } from "react";
import { AgendaCalendar, type AgendaPlan } from "../../src/components/AgendaCalendar";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";

type CityEvent = { id:string; title:string; starts_at:string; ends_at:string|null; venue_name:string|null; city:string; category:string|null };
export default function CalendarScreen() {
  const [plans,setPlans]=useState<AgendaPlan[]>([]),[loading,setLoading]=useState(true),[error,setError]=useState<string|null>(null);
  const request=useRef(0), range=useRef("");
  const load=useCallback(async(start:string,end:string)=>{const key=`${start}|${end}`;if(range.current===key)return;range.current=key;const id=++request.current;setLoading(true);setError(null);
    const [bialem,city]=await Promise.all([api.rpc("get_my_profile_plans",{range_start:start,range_end:end}),api.from("city_events").select("id,title,starts_at,ends_at,venue_name,city,category").gte("starts_at",start).lt("starts_at",end).order("starts_at",{ascending:true})]);
    if(id!==request.current)return;if(bialem.error&&city.error){setError("Takvim şu anda yüklenemedi.");setPlans([]);}else{const local=Array.isArray(bialem.data)?bialem.data as AgendaPlan[]:[];const cities=((city.data??[]) as CityEvent[]).map(e=>({event_id:`city-${e.id}`,title:e.title,starts_at:e.starts_at,ends_at:e.ends_at,location_name:e.venue_name||e.city,event_status:"published",participation_status:"city",community_name:e.category||"Şehir etkinliği",event_type:"city",source:"community" as const,route:`/city-event/${e.id}`}));setPlans([...local,...cities]);}setLoading(false);
  },[]);
  useEffect(()=>{const n=new Date();void load(new Date(n.getFullYear(),n.getMonth(),-7).toISOString(),new Date(n.getFullYear(),n.getMonth()+1,8).toISOString());},[load]);
  return <main className="planner-page"><section className="planner-intro"><div className="planner-intro-icon"><Ionicons name="calendar" size={20} color="#fff"/></div><div className="planner-intro-copy"><small className="planner-kicker">BİALEM TAKVİMİ</small><h1 className="planner-heading">Hayatındaki tüm planlar.</h1><p className="planner-description">Bialem buluşmaları, topluluk etkinlikleri, şehir etkinlikleri, doğum günleri ve kişisel planların tek takvimde.</p></div></section>
    {error?<section className="planner-state planner-error-state"><Ionicons name="cloud-offline-outline" size={30} color={colors.danger}/><h2 className="planner-state-title">Takvim yüklenemedi</h2><p className="planner-state-copy">{error}</p></section>:<><AgendaCalendar plans={plans} onRangeChange={load}/>{loading?<div className="planner-refresh-pill"><i className="planner-pulse"/><span>Takvim güncelleniyor</span></div>:null}</>}
  </main>;
}
