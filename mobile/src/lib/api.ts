import AsyncStorage from "@react-native-async-storage/async-storage";
import { createSpringClient } from "./spring-client";
import { getApiBaseUrl } from "./backend-config";

const TOKEN_KEY = "bialem_api_token";

export const api = createSpringClient({
  getBaseUrl: getApiBaseUrl,
  getToken: () => AsyncStorage.getItem(TOKEN_KEY),
  setToken: async (token) => {
    if (!token) await AsyncStorage.removeItem(TOKEN_KEY);
    else await AsyncStorage.setItem(TOKEN_KEY, token);
  }
});
