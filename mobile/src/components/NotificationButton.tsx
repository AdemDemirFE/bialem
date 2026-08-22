import { useCallback } from "react";
import { useFocusEffect, useRouter } from "expo-router";
import { StyleSheet, Text, View } from "react-native";
import { IconButton } from "./IconButton";
import { refreshUnreadCount, useUnreadNotificationCount } from "../lib/notificationUnreadStore";
import { colors } from "../theme/colors";

export function NotificationButton() {
  const router = useRouter();
  const count = useUnreadNotificationCount();
  useFocusEffect(useCallback(() => { void refreshUnreadCount(); }, []));
  return <View style={styles.wrap}><IconButton icon={count ? "notifications" : "notifications-outline"} accessibilityLabel={count ? `${count} okunmamış bildirim` : "Bildirimler"} size={40} backgroundColor={colors.surface as string} onPress={() => router.push("/notifications" as never)} />
    {count > 0 ? <View pointerEvents="none" style={styles.badge}><Text style={styles.badgeText}>{count > 99 ? "99+" : count}</Text></View> : null}
  </View>;
}
const styles = StyleSheet.create({ wrap:{width:40,height:40,position:"relative"},badge:{position:"absolute",right:-5,top:-5,minWidth:18,height:18,paddingHorizontal:4,alignItems:"center",justifyContent:"center",borderRadius:9,borderWidth:2,borderColor:colors.surface,backgroundColor:colors.danger},badgeText:{color:"#fff",fontSize:9,fontWeight:"900",lineHeight:12} });
