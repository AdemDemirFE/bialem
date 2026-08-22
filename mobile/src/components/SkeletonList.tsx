import { useEffect, useRef } from "react";
import { Animated, Easing, StyleSheet, View } from "react-native";
import { colors } from "../theme/colors";

export function SkeletonList({ rows = 4 }: { rows?: number }) {
  const opacity = useRef(new Animated.Value(0.45)).current;

  useEffect(() => {
    const animation = Animated.loop(
      Animated.sequence([
        Animated.timing(opacity, { toValue: 0.9, duration: 700, easing: Easing.inOut(Easing.quad), useNativeDriver: true }),
        Animated.timing(opacity, { toValue: 0.45, duration: 700, easing: Easing.inOut(Easing.quad), useNativeDriver: true })
      ])
    );
    animation.start();
    return () => animation.stop();
  }, [opacity]);

  return (
    <View accessibilityLabel="İçerik yükleniyor" style={styles.list}>
      {Array.from({ length: rows }, (_, index) => (
        <Animated.View key={index} style={[styles.row, { opacity }]}>
          <View style={styles.avatar} />
          <View style={styles.copy}>
            <View style={styles.title} />
            <View style={styles.line} />
          </View>
        </Animated.View>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  list: { gap: 8 },
  row: { minHeight: 68, flexDirection: "row", alignItems: "center", gap: 12, padding: 12, borderRadius: 16, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  avatar: { width: 42, height: 42, borderRadius: 14, backgroundColor: colors.surfaceStrong },
  copy: { flex: 1, gap: 8 },
  title: { width: "52%", height: 10, borderRadius: 5, backgroundColor: colors.surfaceStrong },
  line: { width: "84%", height: 8, borderRadius: 4, backgroundColor: colors.surfaceStrong }
});
