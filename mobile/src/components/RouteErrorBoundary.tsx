import { Component, type ErrorInfo, type ReactNode } from "react";
import { StyleSheet, Text, View } from "react-native";
import { BialemMascot } from "../experiences/BialemMascot";
import { colors } from "../theme/colors";
import { AppButton } from "./ui/AppButton";

type Props = {
  children: ReactNode;
  resetKey: string;
  onReset: () => void;
};

type State = { error: Error | null };

export class RouteErrorBoundary extends Component<Props, State> {
  state: State = { error: null };

  static getDerivedStateFromError(error: Error): State {
    return { error };
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error("Route render failed", error, info.componentStack);
  }

  componentDidUpdate(previousProps: Props) {
    if (this.state.error && previousProps.resetKey !== this.props.resetKey) {
      this.setState({ error: null });
    }
  }

  private reset = () => {
    this.setState({ error: null });
    this.props.onReset();
  };

  render() {
    if (!this.state.error) return this.props.children;

    return (
      <View style={styles.page} accessibilityRole="alert">
        <View style={styles.card}>
          <BialemMascot size={96} state="error" />
          <Text style={styles.eyebrow}>BİALEM</Text>
          <Text style={styles.title}>Bu ekran açılırken bir sorun oluştu.</Text>
          <Text style={styles.message}>Uygulama kabuğu çalışmaya devam ediyor. Ana sayfaya dönüp tekrar deneyebilirsin.</Text>
          <AppButton label="Ana sayfaya dön" onPress={this.reset} />
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  page: { flex: 1, minHeight: "100%", alignItems: "center", justifyContent: "center", padding: 20, backgroundColor: colors.page },
  card: { width: "100%", maxWidth: 480, gap: 12, padding: 24, borderRadius: 24, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  eyebrow: { color: colors.accent, fontSize: 12, fontWeight: "900", letterSpacing: 1.2 },
  title: { color: colors.ink, fontSize: 21, lineHeight: 27, fontWeight: "900" },
  message: { marginBottom: 4, color: colors.muted, fontSize: 14, lineHeight: 21 }
});
