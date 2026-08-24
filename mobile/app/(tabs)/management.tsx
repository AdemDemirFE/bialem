import { Ionicons } from "@expo/vector-icons";
import { Link, Redirect } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useAuth } from "../../src/lib/auth";
import { managementApi, type ManagementContext as Context } from "../../src/lib/management-api";
import { canSeeManagement } from "../../src/lib/permissions";
import { colors } from "../../src/theme/colors";

export default function ManagementScreen() {
  const { permissions } = useAuth();
  const [context, setContext] = useState<Context | null>(null);
  const [error, setError] = useState<string | null>(null);
  const allowed = canSeeManagement(permissions);

  const load = async () => {
    setError(null);
    try {
      setContext(await managementApi.context());
    } catch (cause) { setError(cause instanceof Error ? cause.message : "Yönetim verileri yüklenemedi."); }
  };
  useEffect(() => { if (allowed) void load(); }, [allowed]);
  if (!allowed) return <Redirect href="/profile" />;

  return <ScrollView style={styles.screen} contentContainerStyle={styles.page}>
    <View style={styles.hero}><Text style={styles.kicker}>{context?.superAdmin ? "SUPER ADMIN" : "ADMIN"}</Text>
      <Text style={styles.title}>Yönetim Merkezi</Text><Text style={styles.subtitle}>Bialem platformunu yönetin</Text></View>
    {!context && !error ? <View style={styles.state}><ActivityIndicator color={colors.accent}/><Text style={styles.muted}>Yönetim yükleniyor...</Text></View> : null}
    {error ? <View style={styles.state}><Text style={styles.error}>{error}</Text><Pressable onPress={() => void load()} style={styles.retry}><Text style={styles.retryText}>Tekrar dene</Text></Pressable></View> : null}
    <Text style={styles.section}>YÖNETİM</Text>
    <View style={styles.menu}>
      <Menu href="/management/users" icon="people-outline" title="Kullanıcılar" subtitle="Hesap, durum ve yetkiler" />
      <Menu href="/management/communities" icon="globe-outline" title="Topluluklar" subtitle="Toplulukları düzenleyin ve oluşturun" />
      <Menu href="/management/events" icon="calendar-outline" title="Etkinlikler" subtitle="Etkinlikleri görüntüleyin ve yönetin" />
      <Menu href="/management/notifications" icon="notifications-outline" title="Bildirimler" subtitle="Bildirim oluşturun ve gönderimleri izleyin" />
      <Menu href="/management/roles" icon="key-outline" title="Roller ve Yetkiler" subtitle="Platform rollerini görüntüleyin" />
      <Menu href="/management/moderation" icon="shield-checkmark-outline" title="Moderasyon" subtitle="Rapor ve içerik yönetimi" />
      <Menu href="/management/data" icon="analytics-outline" title="Veriler" subtitle="Platform istatistikleri" />
    </View>
  </ScrollView>;
}

function Menu({href,icon,title,subtitle}:{href:string;icon:keyof typeof Ionicons.glyphMap;title:string;subtitle:string}) {
  return <Link href={href} asChild><Pressable style={styles.row}><View style={styles.icon}><Ionicons name={icon} size={22} color={colors.accent}/></View><View style={{flex:1}}><Text style={styles.rowTitle}>{title}</Text><Text style={styles.muted}>{subtitle}</Text></View><Ionicons name="chevron-forward" size={20} color={colors.muted}/></Pressable></Link>;
}
const styles=StyleSheet.create({screen:{flex:1,backgroundColor:colors.page},page:{padding:16,paddingBottom:36,gap:14},hero:{padding:20,borderRadius:22,backgroundColor:colors.brandInk,gap:5},kicker:{color:colors.action,fontSize:11,fontWeight:"900",letterSpacing:1.3},title:{color:colors.onBrand,fontSize:28,fontWeight:"900"},subtitle:{color:colors.onBrandMuted,fontSize:14},grid:{flexDirection:"row",flexWrap:"wrap",gap:10},stat:{width:"48%",minHeight:104,padding:15,borderRadius:18,borderWidth:1,borderColor:colors.border,backgroundColor:colors.surface},value:{color:colors.ink,fontSize:27,fontWeight:"900"},label:{color:colors.muted,fontSize:12,fontWeight:"800"},section:{marginTop:4,color:colors.muted,fontSize:11,fontWeight:"900",letterSpacing:1.2},menu:{borderRadius:20,borderWidth:1,borderColor:colors.border,backgroundColor:colors.surface,overflow:"hidden"},row:{minHeight:72,flexDirection:"row",alignItems:"center",gap:12,padding:13,borderBottomWidth:1,borderBottomColor:colors.border},icon:{width:44,height:44,alignItems:"center",justifyContent:"center",borderRadius:15,backgroundColor:colors.accentSoft},rowTitle:{color:colors.ink,fontSize:15,fontWeight:"900"},muted:{color:colors.muted,fontSize:12},state:{alignItems:"center",gap:10,padding:24},error:{color:colors.danger,fontWeight:"800"},retry:{paddingHorizontal:18,paddingVertical:11,borderRadius:99,backgroundColor:colors.action},retryText:{color:colors.actionText,fontWeight:"900"}});
