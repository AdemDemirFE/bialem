import { Ionicons } from "@expo/vector-icons";
import { Stack } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { showAppAlert, showAppConfirm, showAppError } from "../../src/components/AppAlert";
import { api } from "../../src/lib/api";
import { useAuth } from "../../src/lib/auth";
import { canSeeManagement } from "../../src/lib/permissions";
import { roleLabel } from "../../src/lib/permissions";
import { colors } from "../../src/theme/colors";

type ManagedUser = { id:number; login:string; firstName?:string; lastName?:string; email?:string; activated:boolean; authorities:string[]; createdDate?:string };

export default function ManagementUsersScreen() {
  const { permissions } = useAuth();
  const [users,setUsers]=useState<ManagedUser[]>([]); const [loading,setLoading]=useState(true); const [error,setError]=useState<string|null>(null);
  const load=async()=>{setLoading(true);setError(null);try{setUsers(await api.rest.get<ManagedUser[]>("/api/admin/users?page=0&size=20&sort=createdDate,desc"));}catch(e){setError(e instanceof Error?e.message:"Kullanıcılar yüklenemedi.");}finally{setLoading(false);}};
  useEffect(()=>{if(canSeeManagement(permissions))void load();},[permissions.accessManagement]);
  const toggle=async(user:ManagedUser)=>{const action=user.activated?"pasifleştirmek":"aktifleştirmek";if(!await showAppConfirm({title:`Kullanıcıyı ${action} istiyor musunuz?`,text:user.login,confirmDanger:user.activated,confirmText:user.activated?"Pasifleştir":"Aktifleştir"}))return;try{await api.rest.post(`/api/admin/users/${user.id}/${user.activated?"deactivate":"activate"}`);await showAppAlert({title:`Kullanıcı başarıyla ${user.activated?"pasifleştirildi":"aktifleştirildi"}.`,icon:"success"});await load();}catch(e){await showAppError(e instanceof Error?e.message:"İşlem tamamlanamadı.");}};
  if(!canSeeManagement(permissions)) return <View style={styles.state}><Text style={styles.error}>Bu işlem için yetkiniz bulunmuyor.</Text></View>;
  return <><Stack.Screen options={{headerShown:true,title:"Kullanıcı Yönetimi"}}/><ScrollView style={styles.screen} contentContainerStyle={styles.page}>
    <View style={styles.header}><View><Text style={styles.kicker}>YÖNETİM</Text><Text style={styles.title}>Kullanıcılar</Text></View><Pressable onPress={()=>void load()} accessibilityLabel="Kullanıcıları yenile" style={styles.refresh}><Ionicons name="refresh" size={20} color={colors.accent}/></Pressable></View>
    {loading?<View style={styles.state}><ActivityIndicator color={colors.accent}/><Text style={styles.muted}>Kullanıcılar yükleniyor...</Text></View>:null}
    {error?<View style={styles.state}><Text style={styles.error}>{error}</Text><Pressable onPress={()=>void load()} style={styles.button}><Text style={styles.buttonText}>Tekrar dene</Text></Pressable></View>:null}
    {!loading&&!error&&users.length===0?<View style={styles.state}><Text style={styles.muted}>Gösterilecek kullanıcı bulunamadı.</Text></View>:null}
    {users.map(user=><View key={user.id} style={styles.card}><View style={styles.avatar}><Text style={styles.avatarText}>{user.login.slice(0,1).toUpperCase()}</Text></View><View style={styles.copy}><Text style={styles.name}>{[user.firstName,user.lastName].filter(Boolean).join(" ")||user.login}</Text><Text style={styles.muted}>{user.login} · {user.email}</Text><View style={styles.badges}><Text style={[styles.badge,user.activated?styles.active:styles.inactive]}>{user.activated?"Aktif":"Pasif"}</Text><Text style={styles.role}>{roleLabel(user.authorities)}</Text></View></View><Pressable onPress={()=>void toggle(user)} accessibilityLabel={`${user.login} ${user.activated?"pasifleştir":"aktifleştir"}`} style={styles.action}><Ionicons name={user.activated?"pause-circle-outline":"checkmark-circle-outline"} size={25} color={user.activated?colors.danger:colors.success}/></Pressable></View>)}
  </ScrollView></>;
}
const styles=StyleSheet.create({screen:{flex:1,backgroundColor:colors.page},page:{padding:16,paddingBottom:36,gap:10},header:{flexDirection:"row",alignItems:"center",justifyContent:"space-between",marginBottom:6},kicker:{color:colors.accent,fontSize:11,fontWeight:"900",letterSpacing:1.2},title:{color:colors.ink,fontSize:27,fontWeight:"900"},refresh:{width:44,height:44,alignItems:"center",justifyContent:"center",borderRadius:15,backgroundColor:colors.accentSoft},card:{flexDirection:"row",alignItems:"center",gap:12,padding:13,borderRadius:18,borderWidth:1,borderColor:colors.border,backgroundColor:colors.surface},avatar:{width:46,height:46,alignItems:"center",justifyContent:"center",borderRadius:16,backgroundColor:colors.brandInk},avatarText:{color:colors.onBrand,fontSize:18,fontWeight:"900"},copy:{flex:1,gap:3},name:{color:colors.ink,fontSize:15,fontWeight:"900"},muted:{color:colors.muted,fontSize:12},badges:{flexDirection:"row",gap:6,marginTop:3},badge:{paddingHorizontal:8,paddingVertical:3,borderRadius:99,fontSize:10,fontWeight:"900"},active:{color:colors.success,backgroundColor:colors.accentSoft},inactive:{color:colors.danger,backgroundColor:colors.surfaceStrong},role:{paddingHorizontal:8,paddingVertical:3,borderRadius:99,color:colors.accent,backgroundColor:colors.accentSoft,fontSize:10,fontWeight:"900"},action:{width:44,height:44,alignItems:"center",justifyContent:"center"},state:{alignItems:"center",justifyContent:"center",gap:10,padding:28},error:{color:colors.danger,fontWeight:"800"},button:{paddingHorizontal:18,paddingVertical:11,borderRadius:99,backgroundColor:colors.action},buttonText:{color:colors.actionText,fontWeight:"900"}});
