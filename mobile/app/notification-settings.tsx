import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useEffect, useMemo, useState } from "react";
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Switch, Text, View } from "react-native";
import { BackButton } from "../src/components/IconButton";
import { getNotificationPreferences, updateNotificationPreferences, type NotificationPreference } from "../src/lib/notificationApi";
import { colors } from "../src/theme/colors";

type Item = { type: string; label: string; detail: string; icon: string };
type Group = { title: string; detail: string; icon: string; items: Item[] };
const GROUPS: Group[] = [
  { title: "Takip ve bağlantılar", detail: "Sosyal çevrendeki hareketler", icon: "people-outline", items: [
    { type: "NEW_FOLLOWER", label: "Yeni takipçiler", detail: "Biri seni takip ettiğinde", icon: "person-add-outline" },
    { type: "FOLLOW_REQUEST", label: "Takip istekleri", detail: "Yeni bir takip isteği geldiğinde", icon: "person-circle-outline" },
    { type: "FOLLOW_ACCEPTED", label: "Kabul edilen istekler", detail: "Takip isteğin onaylandığında", icon: "checkmark-circle-outline" },
    { type: "FOLLOW_REJECTED", label: "Reddedilen istekler", detail: "Takip isteğin reddedildiğinde", icon: "close-circle-outline" }
  ]},
  { title: "Mesajlar ve sohbet", detail: "Mesaj ve konuşma hareketleri", icon: "chatbubbles-outline", items: [
    { type: "DIRECT_MESSAGE", label: "Direkt mesajlar", detail: "Bir kişi sana özel mesaj gönderdiğinde", icon: "chatbubble-ellipses-outline" },
    { type: "EVENT_GROUP_MESSAGE", label: "Grup mesajları", detail: "Etkinlik sohbetinde yeni mesaj olduğunda", icon: "people-circle-outline" },
    { type: "MENTION", label: "Bahsetmeler", detail: "Bir mesaj veya içerikte senden bahsedildiğinde", icon: "at-outline" }
  ]},
  { title: "Akış etkileşimleri", detail: "İçeriklerinle ilgili hareketler", icon: "heart-outline", items: [
    { type: "COMMENT", label: "Yorumlar", detail: "İçeriğine yorum geldiğinde", icon: "chatbox-outline" },
    { type: "LIKE", label: "Reaksiyonlar", detail: "İçeriğin beğenildiğinde", icon: "heart-circle-outline" },
    { type: "USER_REVIEW", label: "Değerlendirmeler", detail: "Profiline değerlendirme geldiğinde", icon: "star-outline" }
  ]},
  { title: "Etkinlikler ve buluşmalar", detail: "Davetler, katılım ve değişiklikler", icon: "calendar-outline", items: [
    { type: "EVENT_PUBLISHED", label: "Buluşma davetleri", detail: "Yeni bir etkinlik veya buluşma yayınlandığında", icon: "mail-unread-outline" },
    { type: "EVENT_JOIN_REQUEST", label: "Katılım istekleri", detail: "Yönettiğin etkinliğe katılım isteği geldiğinde", icon: "person-add-outline" },
    { type: "EVENT_JOIN_APPROVED", label: "Katılım onayları", detail: "Katılımın onaylandığında", icon: "ticket-outline" },
    { type: "EVENT_JOIN_REJECTED", label: "Katılım reddi", detail: "Katılım isteğin reddedildiğinde", icon: "ban-outline" },
    { type: "EVENT_UPDATED", label: "Etkinlik güncellemeleri", detail: "Saat, konum veya etkinlik bilgileri değiştiğinde", icon: "sync-outline" },
    { type: "EVENT_CANCELLED", label: "Etkinlik iptalleri", detail: "Katıldığın etkinlik iptal edildiğinde", icon: "calendar-clear-outline" },
    { type: "EVENT_REMINDER", label: "Hatırlatmalar", detail: "Yaklaşan etkinliklerin öncesinde", icon: "alarm-outline" }
  ]},
  { title: "Topluluklar", detail: "Üyelik ve önemli duyurular", icon: "megaphone-outline", items: [
    { type: "COMMUNITY_JOIN_REQUEST", label: "Katılım istekleri", detail: "Topluluğuna istek geldiğinde", icon: "person-add-outline" },
    { type: "COMMUNITY_JOIN_APPROVED", label: "Üyelik onayları", detail: "Katılımın onaylandığında", icon: "shield-checkmark-outline" },
    { type: "COMMUNITY_JOIN_REJECTED", label: "Üyelik reddi", detail: "Katılımın reddedildiğinde", icon: "shield-outline" },
    { type: "COMMUNITY_ANNOUNCEMENT", label: "Önemli duyurular", detail: "Yönetici duyuru yayınladığında", icon: "megaphone-outline" }
  ]},
  { title: "Sistem", detail: "Hesap ve platform haberleri", icon: "settings-outline", items: [
    { type: "HONOR_BADGE_AWARDED", label: "Rozet ve başarılar", detail: "Yeni bir rozet kazandığında", icon: "ribbon-outline" },
    { type: "SYSTEM_NOTIFICATION", label: "Sistem bildirimleri", detail: "Hesabınla ilgili önemli gelişmeler", icon: "information-circle-outline" },
    { type: "SYSTEM_ANNOUNCEMENT", label: "Sistem duyuruları", detail: "Bialem tarafından önemli bir duyuru yayınlandığında", icon: "megaphone-outline" },
    { type: "ADMIN_BROADCAST", label: "Yönetim mesajları", detail: "Platform yönetiminden bilgilendirme geldiğinde", icon: "radio-outline" }
  ]}
];
const ITEMS = GROUPS.flatMap(g => g.items);

export default function NotificationSettingsScreen() {
  const router = useRouter();
  const [prefs, setPrefs] = useState<NotificationPreference[]>([]);
  const [loading, setLoading] = useState(true), [saving, setSaving] = useState(false), [saved, setSaved] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const merge = (existing: NotificationPreference[]) => { const map = new Map(existing.map(p => [p.notificationType, p])); return ITEMS.map(i => map.get(i.type) ?? { notificationType: i.type, inAppEnabled: true, pushEnabled: true, emailEnabled: false, mandatory: false }); };
  useEffect(() => { getNotificationPreferences().then(data => setPrefs(merge(data))).catch(e => setError(e.message)).finally(() => setLoading(false)); }, []);
  const map = useMemo(() => new Map(prefs.map(p => [p.notificationType, p])), [prefs]);
  const change = (type: string, field: "inAppEnabled" | "pushEnabled", value: boolean) => { setSaved(false); setPrefs(old => old.map(p => p.notificationType === type ? { ...p, [field]: value } : p)); };
  const setGroup = (group: Group, value: boolean) => { const types = new Set(group.items.map(i => i.type)); setSaved(false); setPrefs(old => old.map(p => types.has(p.notificationType) && !p.mandatory ? { ...p, inAppEnabled: value, pushEnabled: value } : p)); };
  const save = async () => { setSaving(true); setError(null); try { setPrefs(merge(await updateNotificationPreferences(prefs))); setSaved(true); } catch (e) { setError(e instanceof Error ? e.message : "Tercihler kaydedilemedi"); } finally { setSaving(false); } };
  return <ScrollView contentContainerStyle={s.page}>
    <View style={s.top}><BackButton onPress={() => router.back()} /><View style={s.topCopy}><Text style={s.kicker}>BİLDİRİM YÖNETİMİ</Text><Text style={s.title}>Nelerden haberdar olacağını seç</Text></View></View>
    <View style={s.hero}><View style={s.heroIcon}><Ionicons name="notifications-outline" size={23} color={colors.actionText} /></View><View style={s.copy}><Text style={s.heroTitle}>Kontrol tamamen sende</Text><Text style={s.heroText}>Uygulama içi ve cihaz push bildirimlerini ayrı ayrı yönet.</Text></View></View>
    <View style={s.legend}><Text style={s.legendTitle}>Bildirim türü</Text><Text style={s.channel}>Uygulama</Text><Text style={s.channel}>Push</Text></View>
    {error ? <View style={s.errorBox}><Ionicons name="alert-circle-outline" size={18} color={colors.danger} /><Text style={s.error}>{error}</Text></View> : null}
    {loading ? <View style={s.state}><ActivityIndicator color={colors.accent} /><Text style={s.muted}>Tercihlerin hazırlanıyor...</Text></View> : GROUPS.map(group => { const gp = group.items.map(i => map.get(i.type)).filter(Boolean) as NotificationPreference[]; const enabled = gp.every(p => p.inAppEnabled && p.pushEnabled); return <View key={group.title} style={s.card}>
      <View style={s.groupHead}><View style={s.groupIcon}><Ionicons name={group.icon as keyof typeof Ionicons.glyphMap} size={20} color={colors.accent} /></View><View style={s.copy}><Text style={s.groupTitle}>{group.title}</Text><Text style={s.groupDetail}>{group.detail}</Text></View><Switch value={enabled} onValueChange={v => setGroup(group, v)} trackColor={{ false: colors.border, true: colors.action }} thumbColor={colors.surface} /></View>
      {group.items.map((item, index) => { const p = map.get(item.type); return p ? <View key={item.type} style={[s.row, index === 0 && s.first]}><View style={s.itemIcon}><Ionicons name={item.icon as keyof typeof Ionicons.glyphMap} size={17} color={colors.ink} /></View><View style={s.copy}><Text style={s.itemTitle}>{item.label}</Text><Text style={s.itemDetail}>{item.detail}</Text></View><Switch value={p.inAppEnabled} disabled={p.mandatory} onValueChange={v => change(item.type, "inAppEnabled", v)} trackColor={{ false: colors.border, true: colors.action }} thumbColor={colors.surface} /><Switch value={p.pushEnabled} disabled={p.mandatory} onValueChange={v => change(item.type, "pushEnabled", v)} trackColor={{ false: colors.border, true: colors.action }} thumbColor={colors.surface} /></View> : null; })}
    </View>; })}
    {!loading ? <Pressable disabled={saving} onPress={() => void save()} style={({ pressed }) => [s.save, (pressed || saving) && s.pressed]}>{saving ? <ActivityIndicator color={colors.actionText} /> : <><Ionicons name={saved ? "checkmark-circle" : "save-outline"} size={19} color={colors.actionText} /><Text style={s.saveText}>{saved ? "Tercihler kaydedildi" : "Tercihleri kaydet"}</Text></>}</Pressable> : null}
  </ScrollView>;
}

const s = StyleSheet.create({
  page:{flexGrow:1,padding:16,paddingBottom:40,gap:13,backgroundColor:colors.page},top:{flexDirection:"row",alignItems:"center",gap:12},topCopy:{flex:1,gap:3},kicker:{color:colors.accent,fontSize:10,fontWeight:"900",letterSpacing:1},title:{color:colors.ink,fontSize:21,lineHeight:26,fontWeight:"900"},hero:{flexDirection:"row",alignItems:"center",gap:12,padding:15,borderRadius:19,backgroundColor:colors.brandInk},heroIcon:{width:44,height:44,alignItems:"center",justifyContent:"center",borderRadius:15,backgroundColor:colors.action},copy:{flex:1,gap:2},heroTitle:{color:colors.onBrand,fontSize:15,fontWeight:"900"},heroText:{color:"#cbd6ef",fontSize:12,lineHeight:17},legend:{flexDirection:"row",alignItems:"center",paddingHorizontal:12},legendTitle:{flex:1,color:colors.muted,fontSize:10,fontWeight:"900",letterSpacing:.6,textTransform:"uppercase"},channel:{width:50,color:colors.muted,fontSize:9,fontWeight:"800",textAlign:"center"},card:{borderRadius:19,borderWidth:1,borderColor:colors.border,backgroundColor:colors.surface,overflow:"hidden"},groupHead:{minHeight:65,flexDirection:"row",alignItems:"center",gap:10,padding:12},groupIcon:{width:38,height:38,alignItems:"center",justifyContent:"center",borderRadius:13,backgroundColor:colors.accentSoft},groupTitle:{color:colors.ink,fontSize:14,fontWeight:"900"},groupDetail:{color:colors.muted,fontSize:11},row:{minHeight:68,flexDirection:"row",alignItems:"center",gap:7,marginHorizontal:12,paddingVertical:10,borderTopWidth:1,borderTopColor:colors.border},first:{borderTopWidth:1},itemIcon:{width:30,height:30,alignItems:"center",justifyContent:"center",borderRadius:10,backgroundColor:colors.surfaceStrong},itemTitle:{color:colors.ink,fontSize:13,fontWeight:"800"},itemDetail:{color:colors.muted,fontSize:10,lineHeight:14},state:{minHeight:180,alignItems:"center",justifyContent:"center",gap:10,borderRadius:20,backgroundColor:colors.surface},muted:{color:colors.muted,fontSize:13},errorBox:{flexDirection:"row",alignItems:"center",gap:8,padding:11,borderRadius:13,backgroundColor:colors.dangerSoft},error:{flex:1,color:colors.danger,fontSize:12,fontWeight:"700"},save:{minHeight:48,flexDirection:"row",alignItems:"center",justifyContent:"center",gap:8,borderRadius:15,backgroundColor:colors.action},pressed:{opacity:.72,transform:[{scale:.98}]},saveText:{color:colors.actionText,fontSize:14,fontWeight:"900"}
});
