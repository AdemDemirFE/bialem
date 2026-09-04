import { useLocalSearchParams, useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from "react-native";
import { Reveal } from "../../src/animations";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";

export default function PaymentCallbackScreen() {
  const { transactionId, status } = useLocalSearchParams<{ transactionId?: string; status?: string }>();
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const verify = async () => {
      if (!transactionId) {
        setError("Geçersiz ödeme bilgisi.");
        setLoading(false);
        return;
      }
      if (status === "success") {
        setSuccess(true);
        setLoading(false);
        return;
      }
      const result = await api.rpc("handle_payment_callback", {
        target_transaction_id: transactionId,
        target_payload: "{}",
        target_provider: "iyzico"
      });
      if (result.error) {
        setError(result.error.message);
      } else {
        const data = result.data as { status: string } | null;
        setSuccess(data?.status === "completed");
        if (data?.status !== "completed") {
          setError("Ödeme onaylanamadı.");
        }
      }
      setLoading(false);
    };
    void verify();
  }, [transactionId, status]);

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color={colors.accent} />
        <Text style={styles.text}>Ödeme sonucu kontrol ediliyor...</Text>
      </View>
    );
  }

  return (
    <View style={styles.center}>
      <Reveal>
      <Text style={[styles.icon, success ? styles.successIcon : styles.errorIcon]}>{success ? "✓" : "✕"}</Text>
      </Reveal>
      <Reveal index={1}>
      <Text style={styles.title}>{success ? "Ödeme başarılı!" : error || "Ödeme tamamlanamadı."}</Text>
      </Reveal>
      {success ? <Text style={styles.text}>Biletleriniz oluşturuldu. Biletlerim ekranından görüntüleyebilirsiniz.</Text> : null}
      <Pressable
        style={({ pressed }) => [styles.button, pressed && { opacity: 0.9, transform: [{ scale: 0.98 }] }]}
        onPress={() => router.replace(success ? "/my-tickets" : "/(tabs)/feed")}
      >
        <Text style={styles.buttonText}>{success ? "Biletlerime git" : "Ana sayfaya dön"}</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  center: { flex: 1, alignItems: "center", justifyContent: "center", gap: 16, padding: 24, backgroundColor: colors.page },
  icon: { fontSize: 64, fontWeight: "900" },
  successIcon: { color: "#16a34a" },
  errorIcon: { color: colors.danger },
  title: { fontSize: 22, fontWeight: "900", color: colors.ink, textAlign: "center" },
  text: { color: colors.muted, textAlign: "center", fontWeight: "600" },
  button: { marginTop: 12, paddingHorizontal: 28, paddingVertical: 14, borderRadius: 999, backgroundColor: colors.action },
  buttonText: { color: colors.actionText, fontWeight: "900", fontSize: 15 }
});
