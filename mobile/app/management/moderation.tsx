import { ManagementListPage } from "../../src/components/ManagementListPage";
import { managementApi } from "../../src/lib/management-api";
export default function ManagementModeration(){return <ManagementListPage title="Moderasyon" load={()=>managementApi.reports()} label={x=>x.reason??`Rapor #${x.id}`} detail={x=>`${x.status} · ${x.targetType??""}`}/>}
