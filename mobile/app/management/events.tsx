import { ManagementListPage } from "../../src/components/ManagementListPage";
import { managementApi } from "../../src/lib/management-api";
export default function ManagementEvents(){return <ManagementListPage title="Etkinlik Yönetimi" load={()=>managementApi.events()} route="/management/events" label={x=>x.title} detail={x=>`${x.status} · ${x.startsAt?new Date(x.startsAt).toLocaleString("tr-TR"):""}`}/>}
