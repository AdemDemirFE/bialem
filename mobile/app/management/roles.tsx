import { ManagementListPage } from "../../src/components/ManagementListPage";
import { managementApi } from "../../src/lib/management-api";
export default function ManagementRoles(){return <ManagementListPage title="Roller ve Yetkiler" load={()=>managementApi.authorities()} label={x=>x.name} detail={()=>"Tek platform rolü"}/>}
