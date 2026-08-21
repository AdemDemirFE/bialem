import { Ionicons } from "@expo/vector-icons";
import { Redirect, Tabs } from "expo-router";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { useAuth } from "../../src/lib/auth";
import { colors } from "../../src/theme/colors";
import { useTheme } from "../../src/theme/theme";

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
          height: 64 + insets.bottom,
          paddingBottom: Math.max(insets.bottom, 8),
          paddingTop: 8
        },
        tabBarActiveTintColor: colors.action as string,
        tabBarInactiveTintColor: colors.muted as string,
        tabBarLabelStyle: {
          fontWeight: "700",
          fontSize: 10
        },
        tabBarItemStyle: {
          minWidth: 58
        },
        tabBarHideOnKeyboard: true,
        sceneStyle: {
          backgroundColor: colors.page,
          paddingTop: insets.top
        }
      }}
    >
      <Tabs.Screen
        name="feed"
        options={{
          title: "Keşfet",
          tabBarLabel: "Keşfet",
          tabBarIcon: ({ color, size }) => <Ionicons name="compass" color={color} size={size} />
        }}
      />
      <Tabs.Screen
        name="communities"
        options={{
          title: "Topluluklar",
          tabBarLabel: "Topluluk",
          tabBarIcon: ({ color, size }) => <Ionicons name="people" color={color} size={size} />
        }}
      />
      <Tabs.Screen
        name="assistant"
        options={{
          title: "Bialem Asistan",
          tabBarLabel: "Asistan",
          tabBarIcon: ({ color, size }) => <Ionicons name="sparkles" color={color} size={size} />
        }}
      />
      <Tabs.Screen
        name="notifications"
        options={{
          title: "Bildirimler",
          tabBarLabel: "Bildirim",
          tabBarIcon: ({ color, size }) => <Ionicons name="notifications" color={color} size={size} />
        }}
      />
      <Tabs.Screen
        name="profile"
        options={{
          title: "Profil",
          tabBarLabel: "Profil",
          tabBarIcon: ({ color, size }) => <Ionicons name="person-circle" color={color} size={size} />
        }}
      />
    </Tabs>
  );
}
