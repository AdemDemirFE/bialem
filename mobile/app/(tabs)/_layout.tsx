import { Ionicons } from "@expo/vector-icons";
import { Redirect, Tabs } from "expo-router";
import { ActivityIndicator, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { useAuth } from "../../src/lib/auth";
import { canSeeManagement } from "../../src/lib/permissions";
import { colors } from "../../src/theme/colors";
import { useTheme } from "../../src/theme/theme";

function TabIcon({ name, focused, color, size }: { name: string; focused: boolean; color: string; size: number }) {
  return (
    <View style={[styles.iconShell, focused && styles.iconShellActive]}>
      <Ionicons name={name} color={color} size={focused ? size + 1 : size} />
      {focused ? <View style={styles.activeDot} /> : null}
    </View>
  );
}

export default function TabsLayout() {
  const { user, profile, permissions, loading } = useAuth();
  const management = canSeeManagement(permissions);
  const insets = useSafeAreaInsets();
  const { resolvedTheme } = useTheme();

  if (loading) {
    return (
      <View style={styles.loadingPage} accessibilityRole="progressbar">
        <View style={styles.loadingMark}><Text style={styles.loadingMarkText}>B</Text></View>
        <ActivityIndicator color={colors.accent} />
        <Text style={styles.loadingText}>Bialem hazırlanıyor...</Text>
      </View>
    );
  }

  if (!user || !profile?.display_name || !profile?.username) {
    return <Redirect href="/" />;
  }

  return (
    <Tabs
      initialRouteName="feed"
      backBehavior="initialRoute"
      screenOptions={{
        headerStyle: {
          backgroundColor: colors.surface as string
        },
        headerShadowVisible: false,
        headerTintColor: colors.ink as string,
        headerTitleStyle: {
          fontWeight: "800",
          letterSpacing: 0.3
        },
        tabBarStyle: {
          backgroundColor: colors.surface,
          borderTopColor: colors.border,
          borderTopWidth: 1,
          height: 70 + insets.bottom,
          paddingHorizontal: 8,
          paddingBottom: Math.max(insets.bottom, 8),
          paddingTop: 7,
          shadowColor: colors.brandInk,
          shadowOffset: { width: 0, height: -5 },
          shadowOpacity: 0.08,
          shadowRadius: 14,
          elevation: 16
        },
        tabBarActiveTintColor: (resolvedTheme === "dark" ? colors.onBrand : colors.brandInk) as string,
        tabBarInactiveTintColor: colors.muted as string,
        tabBarActiveBackgroundColor: (resolvedTheme === "dark" ? colors.brandInk : colors.accentSoft) as string,
        tabBarLabelStyle: {
          fontWeight: "800",
          fontSize: 10,
          marginTop: 1
        },
        tabBarItemStyle: {
          minWidth: 58,
          minHeight: 52,
          marginHorizontal: 2,
          borderRadius: 17
        },
        tabBarHideOnKeyboard: true,
        sceneStyle: {
          backgroundColor: colors.page
        }
      }}
    >
      <Tabs.Screen
        name="feed"
        options={{
          title: "Keşfet",
          tabBarLabel: "Keşfet",
          tabBarIcon: ({ color, size, focused }) => <TabIcon name={focused ? "compass" : "compass-outline"} focused={focused} color={color} size={size} />
        }}
      />
      <Tabs.Screen
        name="communities"
        options={{
          title: "Topluluklar",
          tabBarLabel: "Topluluk",
          tabBarIcon: ({ color, size, focused }) => <TabIcon name={focused ? "people" : "people-outline"} focused={focused} color={color} size={size} />
        }}
      />
      <Tabs.Screen
        name="calendar"
        options={{
          title: "Takvim",
          tabBarLabel: "Takvim",
          tabBarIcon: ({ color, size, focused }) => <TabIcon name={focused ? "calendar" : "calendar-outline"} focused={focused} color={color} size={size} />
        }}
      />
      <Tabs.Screen
        name="assistant"
        options={{
          title: "Bialem Asistan",
          tabBarLabel: "Asistan",
          tabBarIcon: ({ color, size, focused }) => <TabIcon name={focused ? "sparkles" : "sparkles-outline"} focused={focused} color={color} size={size} />
        }}
      />
      <Tabs.Screen
        name="profile"
        options={{
          href: management ? null : "/profile",
          title: "Profil",
          tabBarLabel: "Profil",
          tabBarIcon: ({ color, size, focused }) => <TabIcon name={focused ? "person-circle" : "person-circle-outline"} focused={focused} color={color} size={size} />
        }}
      />
      <Tabs.Screen
        name="management"
        options={{
          href: management ? "/management" : null,
          title: "Yönetim Merkezi",
          tabBarLabel: "Yönetim",
          tabBarIcon: ({ color, size, focused }) => <TabIcon name={focused ? "shield" : "shield-outline"} focused={focused} color={color} size={size} />
        }}
      />
    </Tabs>
  );
}

const styles = StyleSheet.create({
  loadingPage: { flex: 1, minHeight: "100%", alignItems: "center", justifyContent: "center", gap: 12, backgroundColor: colors.page },
  loadingMark: { width: 48, height: 48, alignItems: "center", justifyContent: "center", borderRadius: 16, backgroundColor: colors.brandInk },
  loadingMarkText: { color: colors.onBrand, fontSize: 20, fontWeight: "900" },
  loadingText: { color: colors.muted, fontSize: 13, fontWeight: "700" },
  iconShell: { width: 30, height: 27, alignItems: "center", justifyContent: "center" },
  iconShellActive: { transform: [{ translateY: -1 }] },
  activeDot: { position: "absolute", bottom: -3, width: 4, height: 4, borderRadius: 2, backgroundColor: colors.action }
});
