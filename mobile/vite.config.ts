import { createRequire } from "node:module";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

const root = path.dirname(fileURLToPath(import.meta.url));
const require = createRequire(import.meta.url);

function resolvePackage(id: string) {
  try {
    return require.resolve(id);
  } catch {
    return path.resolve(root, "..", "node_modules", id);
  }
}

const reactNativeWeb = resolvePackage("react-native-web");
const assetsRegistry = path.resolve(root, "src/shims/assets-registry.ts");

const aliases = [
  { find: /^react-native$/, replacement: reactNativeWeb },
  { find: "@react-native/assets-registry/registry", replacement: assetsRegistry },
  { find: "@react-native/assets-registry", replacement: assetsRegistry },
  { find: "expo-router", replacement: path.resolve(root, "src/shims/expo-router.tsx") },
  { find: "expo-status-bar", replacement: path.resolve(root, "src/shims/expo-status-bar.ts") },
  { find: "expo-constants", replacement: path.resolve(root, "src/shims/expo-constants.ts") },
  { find: "expo-linking", replacement: path.resolve(root, "src/shims/expo-linking.ts") },
  { find: "expo-sharing", replacement: path.resolve(root, "src/shims/expo-sharing.ts") },
  { find: "expo-location", replacement: path.resolve(root, "src/shims/expo-location.ts") },
  { find: "expo-camera", replacement: path.resolve(root, "src/shims/expo-camera.tsx") },
  { find: "expo-image-picker", replacement: path.resolve(root, "src/shims/expo-image-picker.ts") },
  { find: "expo-haptics", replacement: path.resolve(root, "src/shims/expo-haptics.ts") },
  { find: "@expo/vector-icons", replacement: path.resolve(root, "src/shims/vector-icons.tsx") },
  { find: "@react-native-community/datetimepicker", replacement: path.resolve(root, "src/shims/datetimepicker.tsx") },
  { find: "react-native-view-shot", replacement: path.resolve(root, "src/shims/view-shot.ts") },
  { find: "react-native-qrcode-svg", replacement: path.resolve(root, "src/shims/qrcode-svg.tsx") }
];

export default defineConfig({
  plugins: [react()],
  define: {
    global: "window",
    __DEV__: JSON.stringify(true),
    "process.env.NODE_ENV": JSON.stringify(process.env.NODE_ENV || "development")
  },
  resolve: {
    alias: aliases,
    dedupe: ["react", "react-dom", "react-native-web"],
    extensions: [".web.tsx", ".web.ts", ".tsx", ".ts", ".web.jsx", ".web.js", ".jsx", ".js"]
  },
  optimizeDeps: {
    include: ["react-native-vector-icons"],
    exclude: [
      "react-native-web",
      "react-native-svg",
      "react-native-qrcode-svg",
      "react-native-safe-area-context"
    ],
    esbuildOptions: {
      resolveExtensions: [".web.js", ".web.ts", ".js", ".ts", ".tsx", ".jsx"],
      alias: {
        "@react-native/assets-registry/registry": assetsRegistry,
        "@react-native/assets-registry": assetsRegistry
      },
      loader: { ".js": "jsx" }
    }
  },
  server: {
    port: 5173,
    host: true,
    fs: {
      allow: [".."]
    }
  }
});
