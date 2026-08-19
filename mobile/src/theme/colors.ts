import { DynamicColorIOS, Platform, PlatformColor, type ColorValue } from "react-native";

export type ThemePalette = {
  page: ColorValue;
  surface: ColorValue;
  surfaceStrong: ColorValue;
  ink: ColorValue;
  muted: ColorValue;
  accent: ColorValue;
  accentSoft: ColorValue;
  aqua: ColorValue;
  border: ColorValue;
  action: ColorValue;
  actionText: ColorValue;
  success: ColorValue;
  warning: ColorValue;
  danger: ColorValue;
  shadow: ColorValue;
};

type BrandPalette = {
  brandInk: ColorValue;
  onBrand: ColorValue;
  onBrandMuted: ColorValue;
};

type ResolvedTheme = "light" | "dark";

type Palette = Record<keyof ThemePalette, string>;

const lightPalette: Palette = {
  page: "#f4f6fb",
  surface: "#ffffff",
  surfaceStrong: "#eef1f6",
  ink: "#0a1833",
  muted: "#44516f",
  accent: "#7047d7",
  accentSoft: "#eee9fa",
  aqua: "#1699b8",
  border: "#d7dfea",
  action: "#f6a51c",
  actionText: "#0a1833",
  success: "#168aaf",
  warning: "#fbc94d",
  danger: "#c94568",
  shadow: "rgba(10, 24, 51, 0.1)"
};

const darkPalette: Palette = {
  page: "#080d19",
  surface: "#111a2b",
  surfaceStrong: "#182236",
  ink: "#f4f7fc",
  muted: "#b6c0d3",
  accent: "#a98bea",
  accentSoft: "#2a2342",
  aqua: "#55c7da",
  border: "#2a3750",
  action: "#ffb12b",
  actionText: "#081326",
  success: "#55bdd3",
  warning: "#f6cd58",
  danger: "#f07891",
  shadow: "rgba(0, 0, 0, 0.4)"
};

type PaletteKey = keyof ThemePalette;

function cssVariable(key: PaletteKey): ColorValue {
  return `var(--bialem-${key})` as ColorValue;
}

function adaptive(key: PaletteKey): ColorValue {
  const light = lightPalette[key];
  const dark = darkPalette[key];

  if (Platform.OS === "ios") return DynamicColorIOS({ light, dark });
  if (Platform.OS === "web") return cssVariable(key);
  return PlatformColor(`@color/bialem_${key.replace(/[A-Z]/g, (letter) => `_${letter.toLowerCase()}`)}`);
}

export const colors: ThemePalette & BrandPalette = {
  page: adaptive("page"),
  surface: adaptive("surface"),
  surfaceStrong: adaptive("surfaceStrong"),
  ink: adaptive("ink"),
  muted: adaptive("muted"),
  accent: adaptive("accent"),
  accentSoft: adaptive("accentSoft"),
  aqua: adaptive("aqua"),
  border: adaptive("border"),
  action: adaptive("action"),
  actionText: adaptive("actionText"),
  success: adaptive("success"),
  warning: adaptive("warning"),
  danger: adaptive("danger"),
  shadow: adaptive("shadow"),
  // Brand surfaces stay dark in both themes; they are not semantic text colors.
  brandInk: "#0b1730",
  onBrand: "#ffffff",
  onBrandMuted: "#c3cee3"
};

export function applyWebTheme(theme: ResolvedTheme) {
  if (Platform.OS !== "web" || typeof document === "undefined") return;

  const palette = theme === "dark" ? darkPalette : lightPalette;
  const root = document.documentElement;
  root.style.colorScheme = theme;

  (Object.keys(palette) as PaletteKey[]).forEach((key) => {
    root.style.setProperty(`--bialem-${key}`, palette[key]);
  });
}
