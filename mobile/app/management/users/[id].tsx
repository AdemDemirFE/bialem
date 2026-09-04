import { Stack, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { Reveal, Skeleton } from "../../../src/animations";
import { showAppAlert, showAppConfirm, showAppError } from "../../../src/components/AppAlert";
import { managementApi, type ManagedUser } from "../../../src/lib/management-api";
import { primaryAuthority, roleLabel } from "../../../src/lib/permissions";
import { colors } from "../../../src/theme/colors";

const roles = ["ROLE_USER", "ROLE_COMMUNITY_MANAGER", "ROLE_EVENT_MANAGER", "ROLE_MODERATOR", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];

export default function ManagedUserDetail() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const [user, setUser] = useState<ManagedUser | null>(null);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);

  const load = async () => {
    try { setUser(await managementApi.user(id)); }
    catch (error) { void showAppError(error instanceof Error ? error.message : "Kullanıcı yüklenemedi."); }
    finally { setLoading(false); }
  };

  useEffect(() => { void load(); }, [id]);

  const change = async (role: string) => {
    if (updating || role === primaryAuthority(user?.authorities)) return;
    if (!await showAppConfirm({ title: "Kullanıcı rolü değiştirilsin mi?", text: role, confirmText: "Rolü değiştir" })) return;
    setUpdating(true);
    try {
      setUser(await managementApi.setUserAuthority(id, role));
      await showAppAlert({ title: "Kullanıcı rolü güncellendi.", icon: "success" });
    } catch (error) {
      await showAppError(error instanceof Error ? error.message : "Rol değiştirilemedi.");
    } finally { setUpdating(false); }
  };

  const toggle = async () => {
    if (!user || updating) return;
    const active = !user.activated;
    if (!await showAppConfirm({
      title: `Kullanıcı ${active ? "aktifleştirilsin" : "pasifleştirilsin"} mi?`,
      confirmDanger: !active,
      confirmText: active ? "Aktifleştir" : "Pasifleştir"
    })) return;
    setUpdating(true);
    try {
      await managementApi.setUserActivated(user.id, active);
      setUser(current => current ? { ...current, activated: active } : current);
      await showAppAlert({ title: "Kullanıcı durumu güncellendi.", icon: "success" });
    } catch (error) {
      await showAppError(error instanceof Error ? error.message : "Durum değiştirilemedi.");
    } finally { setUpdating(false); }
  };

  return <>
    <Stack.Screen options={{ headerShown: true, title: "Kullanıcı Düzenle" }} />
    <ScrollView style={styles.screen} contentContainerStyle={styles.page}>
      {loading ? <View style={{ gap: 10 }}><Skeleton height={60} borderRadius={16} /><Skeleton height={54} borderRadius={16} /><Skeleton height={54} borderRadius={16} /></View> : user ? <>
        <Reveal>
        <Text style={styles.title}>{user.firstName} {user.lastName}</Text>
        <Text style={styles.info}>{user.login} · {user.email}</Text>
        </Reveal>
        <Pressable disabled={updating} onPress={() => void toggle()} style={[styles.status, updating && styles.disabled]}>
          <Text style={styles.roleText}>{user.activated ? "Pasifleştir" : "Aktifleştir"}</Text>
        </Pressable>
        <Text style={styles.section}>ROL</Text>
        {roles.map((role, i) => <Reveal key={role} index={Math.min(i, 5)}><Pressable disabled={updating} onPress={() => void change(role)} style={[styles.role, role === primaryAuthority(user.authorities) && styles.selected, updating && styles.disabled]}>
          <Text style={styles.roleText}>{roleLabel([role])}</Text>
          <Text>{role === primaryAuthority(user.authorities) ? "●" : "○"}</Text>
        </Pressable></Reveal>)}
      </> : null}
    </ScrollView>
  </>;
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page }, page: { padding: 16, gap: 10 },
  title: { fontSize: 25, fontWeight: "900", color: colors.ink }, info: { fontSize: 13, color: colors.muted },
  section: { marginTop: 12, fontSize: 11, fontWeight: "900", color: colors.muted },
  status: { minHeight: 48, alignItems: "center", justifyContent: "center", marginTop: 8, borderRadius: 99, backgroundColor: colors.action },
  role: { minHeight: 54, flexDirection: "row", alignItems: "center", justifyContent: "space-between", padding: 14, borderRadius: 16, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  selected: { borderColor: colors.accent, backgroundColor: colors.accentSoft }, disabled: { opacity: 0.55 },
  roleText: { fontWeight: "800", color: colors.ink }
});
