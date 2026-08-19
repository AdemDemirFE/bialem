import { View } from "react-native";

export function CameraView({ children }: { children?: unknown }) {
  return <View>{children as never}</View>;
}

export function useCameraPermissions(): [{ granted: boolean }, () => Promise<{ granted: boolean }>] {
  return [{ granted: false }, async () => ({ granted: false })];
}
