import AsyncStorage from "@react-native-async-storage/async-storage";
import { Appearance, Platform, useColorScheme } from "react-native";
import { createContext, type PropsWithChildren, useContext, useEffect, useState } from "react";
import { applyWebTheme } from "./colors";

export type ThemePreference = "light" | "dark" | "system";
type ResolvedTheme = "light" | "dark";

type ThemeContextValue = {
  preference: ThemePreference;
  resolvedTheme: ResolvedTheme;
  setPreference: (preference: ThemePreference) => Promise<void>;
  ready: boolean;
};

const STORAGE_KEY = "bialem.theme.preference";
const ThemeContext = createContext<ThemeContextValue | null>(null);

function waitForNativeTheme() {
  return new Promise<void>((resolve) => setTimeout(resolve, 250));
}

function applyNativePreference(preference: ThemePreference) {
  if (Platform.OS === "web") return;

  const setColorScheme = (Appearance as typeof Appearance & {
    setColorScheme?: (scheme: "light" | "dark" | null) => void;
  }).setColorScheme;

  if (typeof setColorScheme === "function") {
    const nativePreference = preference === "system" ? null : preference;
    setColorScheme.call(Appearance, nativePreference);
  }
}

export function ThemeProvider({ children }: PropsWithChildren) {
  const systemTheme = useColorScheme();
  const [preference, setPreferenceState] = useState<ThemePreference>("system");
  const [ready, setReady] = useState(false);

  useEffect(() => {
    let active = true;

    void AsyncStorage.getItem(STORAGE_KEY).then((storedPreference) => {
      if (!active) return;
      const storedTheme: ThemePreference = storedPreference === "light" || storedPreference === "dark" ? storedPreference : "system";
      const nextPreference: ThemePreference = storedTheme;
      setPreferenceState(nextPreference);
      applyNativePreference(nextPreference);
      setReady(true);
    });

    return () => {
      active = false;
    };
  }, []);

  const setPreference = async (nextPreference: ThemePreference) => {
    await AsyncStorage.setItem(STORAGE_KEY, nextPreference);
    applyNativePreference(nextPreference);
    setPreferenceState(nextPreference);
  };

  const resolvedTheme: ResolvedTheme = preference === "system"
    ? (systemTheme === "dark" ? "dark" : "light")
    : preference;

  useEffect(() => {
    applyWebTheme(resolvedTheme);
  }, [resolvedTheme]);

  return <ThemeContext.Provider value={{ preference, resolvedTheme, setPreference, ready }}>{children}</ThemeContext.Provider>;
}

export function useTheme() {
  const context = useContext(ThemeContext);
  if (!context) throw new Error("useTheme must be used inside ThemeProvider");
  return context;
}
