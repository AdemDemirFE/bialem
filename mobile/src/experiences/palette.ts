export type ExperiencePalette = {
  page: string;
  surface: string;
  ink: string;
  muted: string;
  accent: string;
  accentGlow: string;
  aqua: string;
  action: string;
  success: string;
  danger: string;
  ringA: string;
  ringB: string;
};

const lightPalette: ExperiencePalette = {
  page: "#f4f6fb",
  surface: "#ffffff",
  ink: "#0a1833",
  muted: "#44516f",
  accent: "#7047d7",
  accentGlow: "#9b7ff0",
  aqua: "#1699b8",
  action: "#f6a51c",
  success: "#168aaf",
  danger: "#c94568",
  ringA: "#7047d7",
  ringB: "#1699b8"
};

const darkPalette: ExperiencePalette = {
  page: "#080d19",
  surface: "#111a2b",
  ink: "#f4f7fc",
  muted: "#b6c0d3",
  accent: "#a98bea",
  accentGlow: "#c9b3f5",
  aqua: "#55c7da",
  action: "#ffb12b",
  success: "#55bdd3",
  danger: "#f07891",
  ringA: "#a98bea",
  ringB: "#55c7da"
};

export function getExperiencePalette(theme: "light" | "dark"): ExperiencePalette {
  return theme === "dark" ? darkPalette : lightPalette;
}