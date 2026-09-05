import { useEffect, useRef } from "react";
import { Animated, Easing, Image, StyleSheet, Text, View } from "react-native";
import { colors } from "../theme/colors";
import { fontFamily } from "../theme/tokens";
import { imageSources } from "../theme/images";

/** Uygulama açılış perdesi: spinner yerine markalı karşılama. */
export function BootSplash({ message = "Hazırlanıyor..." }: { message?: string }) {
  const bar = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    const anim = Animated.loop(
      Animated.sequence([
        Animated.timing(bar, { toValue: 1, duration: 900, easing: Easing.inOut(Easing.quad), useNativeDriver: true }),
        Animated.timing(bar, { toValue: 0, duration: 900, easing: Easing.inOut(Easing.quad), useNativeDriver: true }),
      ])
    );
    anim.start();
    return () => anim.stop();
  }, [bar]);

  return (
    <View style={s.screen} accessibilityRole="progressbar" accessibilityLabel={message}>
      <View style={s.glow} />
      <View style={s.mark}>
        <Image source={imageSources.logo} style={s.logo} resizeMode="cover" />
      </View>
      <Text style={s.name}>BiAlem</Text>
      <Text style={s.tag}>Birlikte daha fazlası</Text>
      <View style={s.barTrack}>
        <Animated.View
          style={[
            s.barFill,
            {
              opacity: bar.interpolate({ inputRange: [0, 1], outputRange: [0.45, 1] }),
              transform: [{ scaleX: bar.interpolate({ inputRange: [0, 1], outputRange: [0.4, 1] }) }],
            },
          ]}
        />
      </View>
      <Text style={s.message}>{message}</Text>
    </View>
  );
}

const s = StyleSheet.create({
  screen: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    gap: 12,
    backgroundColor: "#140d3a",
    padding: 32,
    overflow: "hidden",
  },
  glow: {
    position: "absolute",
    top: -120,
    width: 320,
    height: 320,
    borderRadius: 160,
    backgroundColor: "rgba(112, 71, 215, 0.35)",
  },
  mark: {
    width: 96,
    height: 96,
    borderRadius: 28,
    overflow: "hidden",
    backgroundColor: colors.accentSoft,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.22)",
  },
  logo: { width: "100%", height: "100%" },
  name: {
    color: "#fff",
    fontSize: 32,
    fontWeight: "800",
    letterSpacing: 4,
    fontFamily: fontFamily.base,
  },
  tag: { color: "#cbd5ef", fontSize: 13, fontFamily: fontFamily.base },
  barTrack: {
    width: 148,
    height: 4,
    borderRadius: 99,
    backgroundColor: "rgba(255,255,255,0.16)",
    overflow: "hidden",
    marginTop: 8,
  },
  barFill: {
    width: "100%",
    height: "100%",
    borderRadius: 99,
    backgroundColor: colors.aqua,
  },
  message: { color: "#cbd5ef", fontSize: 12, fontFamily: fontFamily.base },
});
