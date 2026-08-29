import { View } from "react-native";

type CameraViewProps = {
  children?: unknown;
  style?: any;
  facing?: "front" | "back";
  barcodeScannerSettings?: { barcodeTypes?: string[] };
  onBarcodeScanned?: (event: { data: any }) => void;
};

export function CameraView({ children }: CameraViewProps) {
  return <View>{children as never}</View>;
}

export function useCameraPermissions(): [{ granted: boolean }, () => Promise<{ granted: boolean }>] {
  return [{ granted: false }, async () => ({ granted: false })];
}
