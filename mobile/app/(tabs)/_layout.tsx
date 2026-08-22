import { Ionicons } from "@expo/vector-icons";
import { Redirect, Tabs } from "expo-router";
import { StyleSheet, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { useAuth } from "../../src/lib/auth";
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
  const { user, profile, loading } = useAuth();
  const insets = useSafeAreaInsets();
  useTheme();

  if (loading) {
    return null;
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
        tabBarActiveTintColor: colors.brandInk as string,
        tabBarInactiveTintColor: colors.muted as string,
        tabBarActiveBackgroundColor: colors.accentSoft as string,
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
          title: "Profil",
          tabBarLabel: "Profil",
          tabBarIcon: ({ color, size, focused }) => <TabIcon name={focused ? "person-circle" : "person-circle-outline"} focused={focused} color={color} size={size} />
        }}
      />
    </Tabs>
  );
}

const styles = StyleSheet.create({
  iconShell: { width: 30, height: 27, alignItems: "center", justifyContent: "center" },
  iconShellActive: { transform: [{ translateY: -1 }] },
  activeDot: { position: "absolute", bottom: -3, width: 4, height: 4, borderRadius: 2, backgroundColor: colors.action }
});
