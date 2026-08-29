import { Ionicons } from "@expo/vector-icons";
import { Redirect, Tabs } from "expo-router";
import { useCallback, useEffect, useState } from "react";
import { ActivityIndicator, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { useAuth } from "../../src/lib/auth";
import { CART_REFRESH_EVENT } from "../../src/lib/cart-events";
import { canSeeManagement } from "../../src/lib/permissions";
import { storeApi } from "../../src/lib/store-api";
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

function CartTabIcon({ color, size, focused }: { color: string; size: number; focused: boolean }) {
  const { user, loading: authLoading } = useAuth();
  const [count, setCount] = useState(0);

  const refresh = useCallback(async () => {
    try {
      const summary = await storeApi.cart();
      setCount(summary.items?.reduce((sum, item) => sum + (item.quantity ?? 1), 0) ?? 0);
    } catch {
      setCount(0);
    }
  }, []);

  useEffect(() => {
    if (authLoading || !user) {
      setCount(0);
      return;
    }
    void refresh();
    const interval = setInterval(() => void refresh(), 8000);
    const onUpdate = () => void refresh();
    if (typeof window !== "undefined") {
      window.addEventListener(CART_REFRESH_EVENT, onUpdate);
    }
    return () => {
      clearInterval(interval);
      if (typeof window !== "undefined") {
        window.removeEventListener(CART_REFRESH_EVENT, onUpdate);
      }
    };
  }, [refresh, authLoading, user]);

  return (
    <View style={[styles.iconShell, focused && styles.iconShellActive]}>
      <Ionicons name={focused ? "cart" : "cart-outline"} color={color} size={focused ? size + 1 : size} />
      {count > 0 ? (
        <View style={styles.badge}>
          <Text style={styles.badgeText}>{count > 99 ? "99+" : count}</Text>
        </View>
      ) : null}
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
        name="store"
        options={{
          title: "Mağaza",
          tabBarLabel: "Mağaza",
          tabBarIcon: ({ color, size, focused }) => <TabIcon name={focused ? "bag" : "bag-outline"} focused={focused} color={color} size={size} />
        }}
      />
      <Tabs.Screen
        name="cart"
        options={{
          title: "Sepetim",
          tabBarLabel: "Sepetim",
          tabBarIcon: ({ color, size, focused }) => <CartTabIcon color={color} size={size} focused={focused} />
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
  activeDot: { position: "absolute", bottom: -3, width: 4, height: 4, borderRadius: 2, backgroundColor: colors.action },
  badge: { position: "absolute", top: -4, right: -4, minWidth: 16, height: 16, borderRadius: 8, backgroundColor: colors.danger, alignItems: "center", justifyContent: "center", paddingHorizontal: 3, borderWidth: 1.5, borderColor: colors.surface },
  badgeText: { color: "#fff", fontSize: 9, fontWeight: "900" }
});
