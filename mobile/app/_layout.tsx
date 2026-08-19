import { Stack, bindRouter } from "../src/shims/expo-router";
import { StatusBar } from "expo-status-bar";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { AuthProvider } from "../src/lib/auth";
import { colors } from "../src/theme/colors";
import { ThemeProvider, useTheme } from "../src/theme/theme";

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
    </AuthProvider>
  );
}
