import { Ionicons } from "@expo/vector-icons";
import { CameraView, useCameraPermissions } from "expo-camera";
import { Stack } from "expo-router";
import { useState } from "react";
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from "react-native";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";

type RedemptionResult = {
  venue_name: string;
  offer_title: string;
  member_name: string;
  discount_percent: number;
};

export default function AdvantageRedeemScreen() {
  const [permission, requestPermission] = useCameraPermissions();
  const [scanning, setScanning] = useState(true);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<RedemptionResult | null>(null);
  const [error, setError] = useState<string | null>(null);

  const redeem = async (value: string) => {
    if (!scanning || loading) return;
    setScanning(false);
    setLoading(true);
    setError(null);

    const token = value.includes("token=") ? value.split("token=")[1]?.split(/[&#]/)[0] ?? value : value;
    const response = await api.rpc("redeem_partner_offer", {
      target_token_or_code: token,
      target_order_amount: null
    });

    if (response.error) setError(response.error.message);
    else setResult((response.data?.[0] ?? null) as RedemptionResult | null);
    setLoading(false);
  };

  const reset = () => {
    setResult(null);
    setError(null);
    setScanning(true);
  };

  if (!permission) return <View style={styles.center}><ActivityIndicator color={colors.accent} /></View>;
  if (!permission.granted) {
    return (
      <View style={styles.center}>
        <Ionicons name="camera-outline" size={54} color={colors.accent} />
        <Text style={styles.title}>QR doğrulama için kamera izni gerekli.</Text>
        <Pressable onPress={() => void requestPermission()} style={styles.button}>
          <Text style={styles.buttonText}>Kamera izni ver</Text>
        </Pressable>
      </View>
    );
  }

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: "Avantaj doğrula" }} />
      <View style={styles.page}>
        <View style={styles.cameraFrame}>
          <CameraView
            style={StyleSheet.absoluteFill}
            facing="back"
            barcodeScannerSettings={{ barcodeTypes: ["qr"] }}
            onBarcodeScanned={scanning ? ({ data }) => void redeem(data) : undefined}
          />
          <View style={styles.target} />
        </View>

        <View style={styles.copy}>
          <Text style={styles.kicker}>İŞLETME EKRANI</Text>
          <Text style={styles.title}>Üyenin QR kodunu çerçeveye alın.</Text>
          <Text style={styles.text}>Kod 60 saniye geçerlidir ve yalnızca bir kez kullanılabilir.</Text>
        </View>

        {loading ? <ActivityIndicator size="large" color={colors.accent} /> : null}
        {result ? (
          <View style={styles.success}>
            <Ionicons name="checkmark-circle" size={42} color="#0d9b6b" />
            <Text style={styles.successTitle}>Avantaj doğrulandı</Text>
            <Text style={styles.successText}>{result.member_name} · %{result.discount_percent} indirim</Text>
            <Text style={styles.successText}>{result.venue_name} · {result.offer_title}</Text>
            <Pressable onPress={reset} style={styles.button}><Text style={styles.buttonText}>Yeni kod okut</Text></Pressable>
          </View>
        ) : null}
        {error ? (
          <View style={styles.errorCard}>
            <Ionicons name="close-circle" size={38} color={colors.danger} />
            <Text style={styles.error}>{error}</Text>
            <Pressable onPress={reset} style={styles.retry}><Text style={styles.retryText}>Tekrar dene</Text></Pressable>
          </View>
        ) : null}
      </View>
    </>
  );
}

const styles = StyleSheet.create({
  page: { flex: 1, gap: 14, padding: 16, backgroundColor: colors.page },
  center: { flex: 1, alignItems: "center", justifyContent: "center", gap: 18, padding: 28, backgroundColor: colors.page },
  cameraFrame: { height: 330, overflow: "hidden", alignItems: "center", justifyContent: "center", borderRadius: 30, backgroundColor: colors.brandInk },
  target: { width: 220, height: 220, borderWidth: 4, borderColor: colors.action, borderRadius: 28 },
  copy: { gap: 7 },
  kicker: { color: colors.accent, fontSize: 10, fontWeight: "900", letterSpacing: 1.4 },
  title: { color: colors.ink, textAlign: "center", fontSize: 23, lineHeight: 29, fontWeight: "900" },
  text: { color: colors.muted, textAlign: "center", fontSize: 13, lineHeight: 20 },
  success: { alignItems: "center", gap: 8, padding: 20, borderRadius: 24, backgroundColor: "#e3f8f0" },
  successTitle: { color: colors.ink, fontSize: 20, fontWeight: "900" },
  successText: { color: colors.muted, textAlign: "center", fontSize: 13 },
  errorCard: { alignItems: "center", gap: 10, padding: 20, borderRadius: 24, backgroundColor: "#ffe8ef" },
  error: { color: colors.danger, textAlign: "center", fontWeight: "800" },
  button: { minHeight: 44, marginTop: 6, justifyContent: "center", paddingHorizontal: 16, paddingVertical: 10, borderRadius: 14, backgroundColor: colors.action },
  buttonText: { color: colors.ink, fontWeight: "900" },
  retry: { paddingHorizontal: 18, paddingVertical: 11, borderRadius: 14, backgroundColor: colors.brandInk },
  retryText: { color: "#fff", fontWeight: "900" }
});
