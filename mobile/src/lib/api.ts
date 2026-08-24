import AsyncStorage from "@react-native-async-storage/async-storage";
import { createSpringClient } from "./spring-client";
import { getApiBaseUrl, getAppEnv } from "./backend-config";

const TOKEN_KEY = "bialem_api_token";
const appEnv = getAppEnv();
const apiBaseUrl = getApiBaseUrl();
const diagnostics = appEnv === "dev" || appEnv === "android-test";

if (diagnostics) {
  console.info(`[Bialem API] env=${appEnv} baseUrl=${apiBaseUrl || "same-origin proxy"}`);
}

export const api = createSpringClient({
  getBaseUrl: () => apiBaseUrl,
  diagnostics,
  getToken: () => AsyncStorage.getItem(TOKEN_KEY),
  setToken: async (token) => {
    if (!token) await AsyncStorage.removeItem(TOKEN_KEY);
    else await AsyncStorage.setItem(TOKEN_KEY, token);
  }
});
