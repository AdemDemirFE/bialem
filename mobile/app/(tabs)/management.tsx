import { Ionicons } from "@expo/vector-icons";
import { Link, Redirect } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useAuth } from "../../src/lib/auth";
import { api } from "../../src/lib/api";
import { canSeeManagement } from "../../src/lib/permissions";
import { colors } from "../../src/theme/colors";

type Context = { superAdmin: boolean; permissions: string[] };
type Dashboard = {
  users: { total: number; active: number; inactive: number; suspended: number; newToday: number };
  communities: { total: number; active: number; pendingRequests: number };
  events: { total: number; upcoming: number; pendingApproval: number };
  moderation: { openReports: number; flaggedPosts: number; flaggedComments: number };
  communications: { notificationsSent: number };
};

export default function ManagementScreen() {
  const { permissions } = useAuth();
  const [context, setContext] = useState<Context | null>(null);
  const [dashboard, setDashboard] = useState<Dashboard | null>(null);
  const [error, setError] = useState<string | null>(null);
  const allowed = canSeeManagement(permissions);

  const load = async () => {
    setError(null);
    try {
      const [nextContext, nextDashboard] = await Promise.all([
        api.rest.get<Context>("/api/admin/context"), api.rest.get<Dashboard>("/api/admin/dashboard")
      ]);
      setContext(nextContext); setDashboard(nextDashboard);
    } catch (cause) { setError(cause instanceof Error ? cause.message : "Yönetim verileri yüklenemedi."); }
  };
  useEffect(() => { if (allowed) void load(); }, [allowed]);
  if (!allowed) return <Redirect href="/profile" />;

  const stats = dashboard ? [
    ["Toplam kullanıcı", dashboard.users.total, "/management/users"],
    ["Aktif kullanıcı", dashboard.users.active, "/management/users?activated=true"],
    ["Pasif kullanıcı", dashboard.users.inactive, "/management/users?activated=false"],
    ["Askıya alınan", dashboard.users.suspended, "/management/users?status=suspended"],
    ["Topluluk", dashboard.communities.total, "/communities"],
    ["Üyelik talebi", dashboard.communities.pendingRequests, "/communities"],
    ["Yaklaşan etkinlik", dashboard.events.upcoming, "/calendar"],
    ["Onay bekleyen", dashboard.events.pendingApproval, "/calendar"],
    ["Açık rapor", dashboard.moderation.openReports, "/management"],
    ["Bildirim", dashboard.communications.notificationsSent, "/notifications"]
  ] as const : [];

  return <ScrollView style={styles.screen} contentContainerStyle={styles.page}>
    <View style={styles.hero}><Text style={styles.kicker}>{context?.superAdmin ? "SUPER ADMIN" : "ADMIN"}</Text>
      <Text style={styles.title}>Yönetim Merkezi</Text><Text style={styles.subtitle}>Bialem platformunu yönetin</Text></View>
    {!dashboard && !error ? <View style={styles.state}><ActivityIndicator color={colors.accent}/><Text style={styles.muted}>Veriler yükleniyor...</Text></View> : null}
    {error ? <View style={styles.state}><Text style={styles.error}>{error}</Text><Pressable onPress={() => void load()} style={styles.retry}><Text style={styles.retryText}>Tekrar dene</Text></Pressable></View> : null}
    <View style={styles.grid}>{stats.map(([label,value,href]) => <Link key={label} href={href} asChild><Pressable style={styles.stat} accessibilityLabel={`${label}: ${value}`}><Text style={styles.value}>{value}</Text><Text style={styles.label}>{label}</Text></Pressable></Link>)}</View>
    <Text style={styles.section}>YÖNETİM</Text>
    <View style={styles.menu}>
      <Menu href="/management/users" icon="people-outline" title="Kullanıcılar" subtitle="Hesap, durum ve yetkiler" />
      <Menu href="/communities" icon="globe-outline" title="Topluluklar" subtitle="Üyelik ve topluluk işlemleri" />
      <Menu href="/calendar" icon="calendar-outline" title="Etkinlikler" subtitle="Onay ve katılımcılar" />
      <Menu href="/notifications" icon="notifications-outline" title="Bildirimler" subtitle="Bildirim geçmişi" />
      <Menu href="/profile" icon="person-circle-outline" title="Profilim" subtitle="Kendi profilinizi görüntüleyin" />
      <Menu href="/settings" icon="settings-outline" title="Ayarlar" subtitle="Gizlilik ve bildirim tercihleri" />
    </View>
  </ScrollView>;
}

function Menu({href,icon,title,subtitle}:{href:string;icon:keyof typeof Ionicons.glyphMap;title:string;subtitle:string}) {
  return <Link href={href} asChild><Pressable style={styles.row}><View style={styles.icon}><Ionicons name={icon} size={22} color={colors.accent}/></View><View style={{flex:1}}><Text style={styles.rowTitle}>{title}</Text><Text style={styles.muted}>{subtitle}</Text></View><Ionicons name="chevron-forward" size={20} color={colors.muted}/></Pressable></Link>;
}
const styles=StyleSheet.create({screen:{flex:1,backgroundColor:colors.page},page:{padding:16,paddingBottom:36,gap:14},hero:{padding:20,borderRadius:22,backgroundColor:colors.brandInk,gap:5},kicker:{color:colors.action,fontSize:11,fontWeight:"900",letterSpacing:1.3},title:{color:colors.onBrand,fontSize:28,fontWeight:"900"},subtitle:{color:colors.onBrandMuted,fontSize:14},grid:{flexDirection:"row",flexWrap:"wrap",gap:10},stat:{width:"48%",minHeight:104,padding:15,borderRadius:18,borderWidth:1,borderColor:colors.border,backgroundColor:colors.surface},value:{color:colors.ink,fontSize:27,fontWeight:"900"},label:{color:colors.muted,fontSize:12,fontWeight:"800"},section:{marginTop:4,color:colors.muted,fontSize:11,fontWeight:"900",letterSpacing:1.2},menu:{borderRadius:20,borderWidth:1,borderColor:colors.border,backgroundColor:colors.surface,overflow:"hidden"},row:{minHeight:72,flexDirection:"row",alignItems:"center",gap:12,padding:13,borderBottomWidth:1,borderBottomColor:colors.border},icon:{width:44,height:44,alignItems:"center",justifyContent:"center",borderRadius:15,backgroundColor:colors.accentSoft},rowTitle:{color:colors.ink,fontSize:15,fontWeight:"900"},muted:{color:colors.muted,fontSize:12},state:{alignItems:"center",gap:10,padding:24},error:{color:colors.danger,fontWeight:"800"},retry:{paddingHorizontal:18,paddingVertical:11,borderRadius:99,backgroundColor:colors.action},retryText:{color:colors.actionText,fontWeight:"900"}});
