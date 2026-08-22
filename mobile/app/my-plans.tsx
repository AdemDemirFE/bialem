import { Ionicons } from "@expo/vector-icons";
import { Stack, useRouter } from "expo-router";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { AgendaCalendar, type AgendaPlan } from "../src/components/AgendaCalendar";
import { api } from "../src/lib/api";
import { colors } from "../src/theme/colors";

export default function MyPlansScreen() {
  const router = useRouter();
  const [plans,setPlans] = useState<AgendaPlan[]>([]); const [loading,setLoading] = useState(true); const [initialized,setInitialized] = useState(false); const [error,setError] = useState<string|null>(null);
  const requestId = useRef(0); const lastRange = useRef<{key:string;start:string;end:string}|null>(null);
  const loadRange = useCallback(async (start:string,end:string,force=false) => {
    const key=`${start}|${end}`; if (!force && lastRange.current?.key===key) return; lastRange.current={key,start,end}; const current=++requestId.current;
    setLoading(true); setError(null); const result=await api.rpc("get_my_profile_plans",{range_start:start,range_end:end}); if(current!==requestId.current)return;
    if(result.error){setError("Ajandan şu anda yüklenemedi. Lütfen tekrar dene.");setPlans([]);} else setPlans(Array.isArray(result.data)?result.data as AgendaPlan[]:[]); setLoading(false); setInitialized(true);
  },[]);
  useEffect(()=>{const now=new Date();const start=new Date(Date.UTC(now.getUTCFullYear(),now.getUTCMonth(),-6));const end=new Date(Date.UTC(now.getUTCFullYear(),now.getUTCMonth()+1,8));void loadRange(start.toISOString(),end.toISOString());},[loadRange]);
  const upcoming=useMemo(()=>plans.filter(p=>Date.parse(p.starts_at)>=Date.now()&&p.event_status!=="cancelled").sort((a,b)=>Date.parse(a.starts_at)-Date.parse(b.starts_at)).slice(0,3),[plans]);

  return <><Stack.Screen options={{headerShown:true,title:"Ajandam"}}/><main className="planner-page">
    <section className="planner-intro"><div className="planner-intro-icon"><Ionicons name="calendar" size={20} color="#fff"/></div><div className="planner-intro-copy"><small className="planner-kicker">KİŞİSEL TAKVİM</small><h1 className="planner-heading">Planların, tek bakışta.</h1><p className="planner-description">Katıldığın ve topluluklarında yayınlanan etkinlikleri ay, hafta, gün veya ajanda görünümünde takip et.</p></div></section>
    {loading&&!initialized?<CalendarSkeleton/>:error?<section className="planner-state planner-error-state"><Ionicons name="cloud-offline-outline" size={30} color={colors.danger}/><h2 className="planner-state-title">Takvim yüklenemedi</h2><p className="planner-state-copy">{error}</p><button type="button" className="planner-retry" onClick={()=>{const r=lastRange.current;if(r)void loadRange(r.start,r.end,true);}}>Tekrar dene</button></section>:<>
      <AgendaCalendar plans={plans} onRangeChange={loadRange}/>{loading&&<div className="planner-refresh-pill"><i className="planner-pulse"/><span>Takvim güncelleniyor</span></div>}
      {upcoming.length>0&&<section className="planner-upcoming"><h2 className="planner-section-title">Sıradaki planlar</h2>{upcoming.map(plan=><button type="button" key={plan.event_id} className="planner-upcoming-row" onClick={()=>router.push(`/event/${plan.event_id}` as never)}><span className="planner-date-tile"><strong className="planner-date-day">{new Date(plan.starts_at).getDate()}</strong><small className="planner-date-month">{new Date(plan.starts_at).toLocaleDateString("tr-TR",{month:"short"})}</small></span><span className="planner-upcoming-copy"><small className="planner-upcoming-community">{plan.community_name||"Bialem"}</small><strong className="planner-upcoming-title">{plan.title}</strong><small className="planner-upcoming-meta">{formatDate(plan.starts_at)} · {plan.location_name||"Konum etkinlik detayında"}</small></span><Ionicons name="chevron-forward" size={18} color={colors.muted}/></button>)}</section>}
    </>}
  </main></>;
}
function CalendarSkeleton(){return <section className="planner-skeleton"><div className="planner-skeleton-line"/><div className="planner-skeleton-tabs"/><div className="planner-skeleton-grid">{Array.from({length:35},(_,i)=><i key={i} className="planner-skeleton-cell"/>)}</div></section>}
function formatDate(value:string){return new Date(value).toLocaleString("tr-TR",{weekday:"short",day:"numeric",month:"short",hour:"2-digit",minute:"2-digit"})}
