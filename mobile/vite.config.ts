import { createRequire } from "node:module";
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath, pathToFileURL } from "node:url";
import { defineConfig, type Plugin } from "vite";
import react from "@vitejs/plugin-react";

const root = path.dirname(fileURLToPath(import.meta.url));
const require = createRequire(import.meta.url);
const bialemEnvPath = path.resolve(root, "../scripts/bialem-env.mjs");

async function loadEnvForMode(mode: string) {
  try {
    const mod = await import(pathToFileURL(bialemEnvPath).href);
    return mod.resolveEnvironment(mode) as {
      name: string;
      version: string;
      versionCode: number;
      apiBaseUrl: string;
      publicWebUrl: string;
    };
  } catch {
    // Docker web image may not include scripts/bialem-env.mjs; honor build ARG/ENV.
    const name = mode === "production" ? "prod" : mode === "android-test" ? "android-test" : "dev";
    return {
      name,
      version: process.env.VITE_APP_VERSION || "0.0.0",
      versionCode: Number(process.env.VITE_APP_VERSION_CODE || 1),
      apiBaseUrl: (process.env.VITE_API_BASE_URL || "http://localhost:8080").replace(/\/+$/, ""),
      publicWebUrl: (process.env.VITE_PUBLIC_WEB_URL || "https://bialem.app").replace(/\/+$/, "")
    };
  }
}

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

function bialemCapacitorAndroidTestFlag(mode: string): Plugin {
  const flag = path.resolve(root, ".capacitor-android-test");
  return {
    name: "bialem-capacitor-android-test-flag",
    closeBundle() {
      if (mode === "android-test") {
        fs.writeFileSync(flag, "android-test\n");
        console.log("[bialem] wrote .capacitor-android-test (cap sync → http://localhost)");
      } else if (fs.existsSync(flag)) {
        fs.unlinkSync(flag);
        console.log("[bialem] removed .capacitor-android-test (cap sync → https://localhost)");
      }
    }
  };
}

function bialemBrandAssets(): Plugin {
  const resources = path.resolve(root, "resources");
  const routes: Record<string, string> = {
    "/favicon.ico": "icon.png",
    "/favicon.png": "icon.png",
    "/apple-touch-icon.png": "icon.png",
    "/brand/icon.png": "icon.png"
  };

  return {
    name: "bialem-brand-assets",
    configureServer(server) {
      server.middlewares.use((req, res, next) => {
        const url = req.url?.split("?")[0] ?? "";
        const file = routes[url];
        if (!file) {
          next();
          return;
        }
        const full = path.join(resources, file);
        if (!fs.existsSync(full)) {
          next();
          return;
        }
        res.setHeader("Content-Type", "image/png");
        res.setHeader("Cache-Control", "no-cache");
        fs.createReadStream(full).pipe(res);
      });
    },
    closeBundle() {
      const outDir = path.resolve(root, "dist");
      for (const [url, file] of Object.entries(routes)) {
        const dest = path.join(outDir, url.replace(/^\//, ""));
        fs.mkdirSync(path.dirname(dest), { recursive: true });
        fs.copyFileSync(path.join(resources, file), dest);
      }
    }
  };
}

export default defineConfig(async ({ mode, command }) => {
  const env = await loadEnvForMode(mode);
  const isProdLike = mode === "production" || mode === "android-test";
  // Default: browser calls the real API URL (e.g. http://191.215.36.29:8184).
  // Optional: BIALEM_DEV_PROXY=1 → same-origin /api proxy (avoids CORS until VPS allows localhost:5173).
  const useDevProxy = command === "serve" && process.env.BIALEM_DEV_PROXY === "1";

  // Inject into process.env so Vite's import.meta.env.VITE_* replacement picks them up.
  process.env.VITE_API_BASE_URL = useDevProxy ? "" : env.apiBaseUrl;
  process.env.VITE_DEV_API_PROXY = useDevProxy ? "true" : "false";
  process.env.VITE_PUBLIC_WEB_URL = env.publicWebUrl;
  process.env.VITE_APP_VERSION = env.version;
  process.env.VITE_APP_ENV = env.name;
  process.env.VITE_APP_VERSION_CODE = String(env.versionCode);

  console.log(
    `[bialem] env=${env.name} api=${useDevProxy ? `proxy→${env.apiBaseUrl}` : env.apiBaseUrl} version=${env.version} (${env.versionCode})`
  );

  return {
    plugins: [react(), bialemBrandAssets(), bialemCapacitorAndroidTestFlag(mode)],
    define: {
      global: "window",
      __DEV__: JSON.stringify(!isProdLike),
      "process.env.NODE_ENV": JSON.stringify(isProdLike ? "production" : "development")
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
      },
      proxy: useDevProxy
        ? {
            // Strip browser Origin so Spring CorsFilter does not 403 the proxied request.
            "/api": {
              target: env.apiBaseUrl,
              changeOrigin: true,
              configure(proxy) {
                proxy.on("proxyReq", (proxyReq) => {
                  proxyReq.removeHeader("origin");
                  proxyReq.removeHeader("referer");
                });
              }
            },
            "/management": {
              target: env.apiBaseUrl,
              changeOrigin: true,
              configure(proxy) {
                proxy.on("proxyReq", (proxyReq) => {
                  proxyReq.removeHeader("origin");
                  proxyReq.removeHeader("referer");
                });
              }
            }
          }
        : undefined
    }
  };
});
