import { Stack, bindRouter } from "../src/shims/expo-router";
import { StatusBar } from "expo-status-bar";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { AuthProvider } from "../src/lib/auth";
import { colors } from "../src/theme/colors";
import { ThemeProvider, useTheme } from "../src/theme/theme";
import { useSwipeBack } from "../src/lib/swipe-back";
import { FloatingAssistantButton } from "../src/components/FloatingAssistantButton";

export default function RootLayout() {
  return (
    <ThemeProvider>
      <RootNavigator />
    </ThemeProvider>
  );
}

function RootNavigator() {
  const { resolvedTheme } = useTheme();
  const navigate = useNavigate();
  useSwipeBack(true);

  useEffect(() => {
    bindRouter(
      (to, opts) => navigate(to, { replace: opts?.replace }),
      () => navigate(-1)
    );
  }, [navigate]);

  return (
    <AuthProvider>
      <StatusBar style={resolvedTheme === "dark" ? "light" : "dark"} />
      <Stack
        screenOptions={{
          headerShown: false,
          contentStyle: { backgroundColor: colors.page }
        }}
      />
      <FloatingAssistantButton />
    </AuthProvider>
  );
}
