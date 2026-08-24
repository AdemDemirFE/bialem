import { useEffect, useRef, useState } from "react";
import { ActivityIndicator, Image, Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { useRouter } from "expo-router";
import { showAppAlert, showAppConfirm, showAppError } from "./AppAlert";
import { managementApi, type CommunityMemberStats, type CommunityMemberStatus, type CommunityMemberView } from "../lib/management-api";
import { normalizeImageUrl } from "../lib/media-url";
import { colors } from "../theme/colors";

type Segment = "PENDING" | "APPROVED";
const emptyStats: CommunityMemberStats = { pending: 0, approved: 0, blocked: 0 };

export function CommunityMembershipManagement({ communityId }: { communityId: number }) {
  const router = useRouter();
  const initialized = useRef(false);
  const [segment, setSegment] = useState<Segment>("APPROVED");
  const [stats, setStats] = useState(emptyStats);
  const [items, setItems] = useState<CommunityMemberView[]>([]);
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [processing, setProcessing] = useState<number | null>(null);

  const loadStats = async () => {
    const next = await managementApi.communityMemberStats(communityId);
    setStats(next);
    if (!initialized.current) {
      initialized.current = true;
      setSegment(next.pending > 0 ? "PENDING" : "APPROVED");
    }
  };

  const loadMembers = async () => {
    setLoading(true); setError(null);
    try {
      const result = await managementApi.communityMembers(communityId, segment as CommunityMemberStatus, page, 20, search.trim());
      setItems(result.content); setTotalPages(result.totalPages);
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Katılım istekleri yüklenemedi.");
    } finally { setLoading(false); }
  };

  useEffect(() => { void loadStats().catch(() => setError("Üyelik sayıları yüklenemedi.")); }, [communityId]);
  useEffect(() => { void loadMembers(); }, [communityId, segment, page, search]);

  const chooseSegment = (next: Segment) => { setSegment(next); setPage(0); };
  const review = async (member: CommunityMemberView, approve: boolean) => {
    if (processing !== null) return;
    if (!approve && !await showAppConfirm({ title: "Katılım isteği reddedilsin mi?", text: member.displayName, confirmText: "Reddet", confirmDanger: true })) return;
    setProcessing(member.id);
    try {
      await (approve ? managementApi.approveCommunityMember(communityId, member.id) : managementApi.rejectCommunityMember(communityId, member.id));
      setItems(current => current.filter(item => item.id !== member.id));
      setStats(current => ({ ...current, pending: Math.max(0, current.pending - 1), approved: current.approved + (approve ? 1 : 0) }));
      await showAppAlert({ title: approve ? "Katılım isteği onaylandı." : "Katılım isteği reddedildi.", icon: "success" });
    } catch (reason) {
      await showAppError(reason instanceof Error ? reason.message : "Katılım isteği işlenemedi.");
      await Promise.all([loadStats(), loadMembers()]);
    } finally { setProcessing(null); }
  };

  return <View style={styles.section}>
    <View style={styles.heading}>
      <Text style={styles.title}>Üye Yönetimi</Text>
      {stats.pending > 0 && <Text style={styles.pendingBanner}>{stats.pending} bekleyen yeni istek</Text>}
    </View>
    <View style={styles.segments}>
      <SegmentButton label={`Yeni İstekler (${stats.pending})`} active={segment === "PENDING"} onPress={() => chooseSegment("PENDING")} />
      <SegmentButton label={`Topluluk Üyeleri (${stats.approved})`} active={segment === "APPROVED"} onPress={() => chooseSegment("APPROVED")} />
    </View>
    <TextInput value={search} onChangeText={(value: string) => { setSearch(value); setPage(0); }} placeholder="Ad veya kullanıcı adı ara" style={styles.search} />
    {loading ? <View style={styles.state}><ActivityIndicator color={colors.accent}/><Text>Yükleniyor...</Text></View>
      : error ? <View style={styles.state}><Text style={styles.error}>{error}</Text><Pressable onPress={() => void loadMembers()} style={styles.primary}><Text style={styles.primaryText}>Tekrar Dene</Text></Pressable></View>
      : items.length === 0 ? <Text style={styles.empty}>{segment === "PENDING" ? "Bekleyen katılım isteği bulunmuyor." : "Bu toplulukta henüz üye bulunmuyor."}</Text>
      : items.map(member => <View key={member.id} style={styles.card}>
          <View style={styles.person}>
            {member.avatarUrl ? <Image source={{ uri: normalizeImageUrl(member.avatarUrl) }} style={styles.avatar}/> : <View style={styles.avatarFallback}><Text style={styles.avatarLetter}>{member.displayName?.[0]?.toUpperCase() ?? "?"}</Text></View>}
            <View style={styles.copy}><Text style={styles.name}>{member.displayName}</Text><Text style={styles.muted}>@{member.username || member.login}{member.city ? ` · ${member.city}` : ""}</Text><Text style={styles.muted}>{relativeTime(member.createdAt)} · {member.role}</Text>{member.bio ? <Text numberOfLines={2} style={styles.bio}>{member.bio}</Text> : null}</View>
          </View>
          <View style={styles.actions}>
            {segment === "PENDING" && <><Pressable disabled={processing !== null} onPress={() => void review(member, true)} style={styles.primary}><Text style={styles.primaryText}>Onayla</Text></Pressable><Pressable disabled={processing !== null} onPress={() => void review(member, false)} style={styles.reject}><Text style={styles.rejectText}>Reddet</Text></Pressable></>}
            <Pressable onPress={() => router.push(`/management/users/${member.userId}` as never)} style={styles.profile}><Text style={styles.profileText}>Profili Gör</Text></Pressable>
          </View>
        </View>)}
    {totalPages > 1 && <View style={styles.pagination}><Pressable disabled={page === 0} onPress={() => setPage(value => value - 1)}><Text style={styles.profileText}>Önceki</Text></Pressable><Text>{page + 1} / {totalPages}</Text><Pressable disabled={page + 1 >= totalPages} onPress={() => setPage(value => value + 1)}><Text style={styles.profileText}>Sonraki</Text></Pressable></View>}
  </View>;
}

function SegmentButton({ label, active, onPress }: { label: string; active: boolean; onPress: () => void }) {
  return <Pressable onPress={onPress} style={[styles.segment, active && styles.segmentActive]}><Text style={[styles.segmentText, active && styles.segmentTextActive]}>{label}</Text></Pressable>;
}
function relativeTime(value: string) { const seconds=Math.max(0,Math.floor((Date.now()-new Date(value).getTime())/1000));if(seconds<60)return "az önce";if(seconds<3600)return `${Math.floor(seconds/60)} dk önce`;if(seconds<86400)return `${Math.floor(seconds/3600)} saat önce`;return `${Math.floor(seconds/86400)} gün önce`; }

const styles=StyleSheet.create({section:{marginTop:18,gap:12,paddingTop:18,borderTopWidth:1,borderTopColor:colors.border},heading:{gap:6},title:{fontSize:22,fontWeight:"900",color:colors.ink},pendingBanner:{padding:10,borderRadius:12,backgroundColor:colors.accentSoft,color:colors.accent,fontWeight:"900"},segments:{flexDirection:"row",gap:8},segment:{flex:1,minHeight:46,alignItems:"center",justifyContent:"center",paddingHorizontal:8,borderRadius:14,borderWidth:1,borderColor:colors.border,backgroundColor:colors.surface},segmentActive:{borderColor:colors.accent,backgroundColor:colors.accentSoft},segmentText:{fontSize:12,fontWeight:"800",color:colors.muted,textAlign:"center"},segmentTextActive:{color:colors.accent},search:{minHeight:46,paddingHorizontal:13,borderWidth:1,borderColor:colors.border,borderRadius:14,backgroundColor:colors.surface,color:colors.ink},state:{minHeight:130,alignItems:"center",justifyContent:"center",gap:10},error:{color:colors.danger,fontWeight:"800",textAlign:"center"},empty:{padding:22,textAlign:"center",color:colors.muted,backgroundColor:colors.surface,borderRadius:16},card:{gap:12,padding:14,borderWidth:1,borderColor:colors.border,borderRadius:18,backgroundColor:colors.surface},person:{flexDirection:"row",gap:12},avatar:{width:52,height:52,borderRadius:26},avatarFallback:{width:52,height:52,borderRadius:26,alignItems:"center",justifyContent:"center",backgroundColor:colors.accentSoft},avatarLetter:{fontSize:20,fontWeight:"900",color:colors.accent},copy:{flex:1,gap:3},name:{fontSize:16,fontWeight:"900",color:colors.ink},muted:{fontSize:12,color:colors.muted},bio:{fontSize:12,color:colors.ink},actions:{flexDirection:"row",flexWrap:"wrap",gap:8},primary:{minHeight:40,justifyContent:"center",paddingHorizontal:15,borderRadius:99,backgroundColor:colors.action},primaryText:{fontWeight:"900",color:colors.actionText},reject:{minHeight:40,justifyContent:"center",paddingHorizontal:15,borderRadius:99,backgroundColor:colors.danger},rejectText:{fontWeight:"900",color:"#fff"},profile:{minHeight:40,justifyContent:"center",paddingHorizontal:12},profileText:{fontWeight:"800",color:colors.accent},pagination:{flexDirection:"row",alignItems:"center",justifyContent:"space-between",padding:8}});
