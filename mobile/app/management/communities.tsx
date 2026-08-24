import { ManagementListPage } from "../../src/components/ManagementListPage";
import { managementApi } from "../../src/lib/management-api";
export default function ManagementCommunities(){return <ManagementListPage title="Topluluk Yönetimi" load={()=>managementApi.communities()} route="/management/communities" label={x=>x.name} detail={x=>`${x.slug} · ${x.visibility} · ${x.communityType??""}`}/>}
