const fs = require("node:fs");
const path = require("node:path");
const { withDangerousMod } = require("expo/config-plugins");

const lightColors = {
  bialem_page: "#F4F6FB",
  bialem_surface: "#FFFFFF",
  bialem_surface_strong: "#EEF1F6",
  bialem_ink: "#0A1833",
  bialem_muted: "#44516F",
  bialem_accent: "#7047D7",
  bialem_accent_soft: "#EEE9FA",
  bialem_aqua: "#1699B8",
  bialem_border: "#D7DFEA",
  bialem_action: "#F6A51C",
  bialem_action_text: "#0A1833",
  bialem_success: "#168AAF",
  bialem_warning: "#FBC94D",
  bialem_danger: "#C94568",
  bialem_shadow: "#1A0A1833"
};

const darkColors = {
  bialem_page: "#080D19",
  bialem_surface: "#111A2B",
  bialem_surface_strong: "#182236",
  bialem_ink: "#F4F7FC",
  bialem_muted: "#B6C0D3",
  bialem_accent: "#A98BEA",
  bialem_accent_soft: "#2A2342",
  bialem_aqua: "#55C7DA",
  bialem_border: "#2A3750",
  bialem_action: "#FFB12B",
  bialem_action_text: "#081326",
  bialem_success: "#55BDD3",
  bialem_warning: "#F6CD58",
  bialem_danger: "#F07891",
  bialem_shadow: "#66000000"
};

function toXml(colors) {
  const rows = Object.entries(colors).map(([name, value]) => `  <color name="${name}">${value}</color>`);
  return `<?xml version="1.0" encoding="utf-8"?>\n<resources>\n${rows.join("\n")}\n</resources>\n`;
}

module.exports = function withBialemAndroidTheme(config) {
  return withDangerousMod(config, [
    "android",
    async (androidConfig) => {
      const resourceRoot = path.join(androidConfig.modRequest.platformProjectRoot, "app", "src", "main", "res");
      const lightDirectory = path.join(resourceRoot, "values");
      const darkDirectory = path.join(resourceRoot, "values-night");
      fs.mkdirSync(lightDirectory, { recursive: true });
      fs.mkdirSync(darkDirectory, { recursive: true });
      fs.writeFileSync(path.join(lightDirectory, "bialem_colors.xml"), toXml(lightColors));
      fs.writeFileSync(path.join(darkDirectory, "bialem_colors.xml"), toXml(darkColors));
      return androidConfig;
    }
  ]);
};
