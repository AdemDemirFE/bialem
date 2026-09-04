import { Ionicons } from "@expo/vector-icons";
import { Link, Stack, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { Reveal, Skeleton } from "../../src/animations";
import { showAppConfirm, showAppError } from "../../src/components/AppAlert";
import { managementApi, type Community, type CommunityMemberStats } from "../../src/lib/management-api";
import { colors } from "../../src/theme/colors";

export default function ManagementCommunities() {
  const router = useRouter();
  const [items, setItems] = useState<Community[]>([]);
  const [stats, setStats] = useState<Record<number, CommunityMemberStats>>({});
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    try {
      const communities = await managementApi.communities();
      setItems(communities);
      const pairs = await Promise.all(communities.filter(item => item.id).map(async item => [item.id!, await managementApi.communityMemberStats(item.id!)] as const));
      setStats(Object.fromEntries(pairs));
    } catch (error) {
      await showAppError(error instanceof Error ? error.message : "Topluluklar yüklenemedi.");
    } finally { setLoading(false); }
  };
  useEffect(() => { void load(); }, []);

  const deactivate = async (item: Community) => {
    if (!await showAppConfirm({ title: "Topluluk pasife alınsın mı?", text: item.name, confirmText: "Pasife al" })) return;
    try { await managementApi.deactivateCommunity(item); void load(); }
    catch (error) { await showAppError(error instanceof Error ? error.message : "Topluluk pasife alınamadı."); }
  };
  const remove = async (item: Community) => {
    if (!item.id || !await showAppConfirm({ title: "Topluluk kalıcı olarak silinsin mi?", text: item.name, confirmText: "Sil", confirmDanger: true })) return;
    try { await managementApi.deleteCommunity(item.id); setItems(current => current.filter(value => value.id !== item.id)); }
    catch (error) { await showAppError(error instanceof Error ? error.message : "Topluluk silinemedi."); }
  };

  const pendingTotal = Object.values(stats).reduce((sum, value) => sum + value.pending, 0);
  return <>
    <Stack.Screen options={{ headerShown: true, title: "Topluluk Yönetimi" }}/>
    <ScrollView style={styles.screen} contentContainerStyle={styles.page}>
      <Reveal>
      <View style={styles.head}><View><Text style={styles.title}>Topluluklar</Text>{pendingTotal > 0 && <Text style={styles.pendingTotal}>{pendingTotal} yeni istek</Text>}</View><View style={styles.actions}><Link href="/management/communities/new" asChild><Pressable style={styles.add}><Ionicons name="add" size={19} color={colors.actionText}/><Text style={styles.addText}>Yeni</Text></Pressable></Link><Pressable style={styles.refresh} onPress={() => void load()}><Ionicons name="refresh" size={20} color={colors.accent}/></Pressable></View></View>
      </Reveal>
      {loading ? (
        <View style={{ gap: 10 }}>
          <Skeleton height={110} borderRadius={18} />
          <Skeleton height={110} borderRadius={18} />
        </View>
      ) : items.map((item, i) => {
        const count = item.id ? stats[item.id] : undefined;
        return <Reveal key={item.id} index={Math.min(i, 6)}>
        <View style={styles.card}>
          <Pressable style={styles.copy} onPress={() => router.push(`/management/communities/${item.id}` as never)}>
            <Text style={styles.name}>{item.name}</Text><Text style={styles.muted}>{item.slug} · {item.visibility} · {item.communityType}</Text>
            <Text style={styles.muted}>{count?.approved ?? 0} Üye</Text>
            {count?.pending ? <Text style={styles.pending}>{count.pending} Yeni İstek</Text> : null}
            <Text style={[styles.badge, !item.isDiscoverable && styles.passive]}>{item.isDiscoverable ? "Aktif" : "Pasif"}</Text>
          </Pressable>
          <Pressable accessibilityLabel="Pasife al" style={styles.icon} onPress={() => void deactivate(item)}><Ionicons name="pause-circle-outline" size={22} color={colors.muted}/></Pressable>
          <Pressable accessibilityLabel="Sil" style={styles.icon} onPress={() => void remove(item)}><Ionicons name="trash-outline" size={21} color={colors.danger}/></Pressable>
        </View></Reveal>;
      })}
    </ScrollView>
  </>;
}

const styles=StyleSheet.create({screen:{flex:1,backgroundColor:colors.page},page:{padding:16,gap:12},head:{flexDirection:"row",alignItems:"center",justifyContent:"space-between"},title:{fontSize:26,fontWeight:"900",color:colors.ink},pendingTotal:{marginTop:3,fontWeight:"900",color:colors.danger},actions:{flexDirection:"row",gap:8},add:{minHeight:42,flexDirection:"row",alignItems:"center",gap:5,paddingHorizontal:14,borderRadius:99,backgroundColor:colors.action},addText:{fontWeight:"900",color:colors.actionText},refresh:{width:42,height:42,alignItems:"center",justifyContent:"center",borderRadius:21,backgroundColor:colors.surface},card:{flexDirection:"row",alignItems:"center",padding:14,borderWidth:1,borderColor:colors.border,borderRadius:18,backgroundColor:colors.surface},copy:{flex:1,gap:4},name:{fontSize:16,fontWeight:"900",color:colors.ink},muted:{fontSize:12,color:colors.muted},pending:{alignSelf:"flex-start",paddingHorizontal:9,paddingVertical:5,borderRadius:99,backgroundColor:colors.accentSoft,color:colors.danger,fontWeight:"900"},badge:{alignSelf:"flex-start",fontSize:11,fontWeight:"900",color:colors.success},passive:{color:colors.muted},icon:{padding:8}});
