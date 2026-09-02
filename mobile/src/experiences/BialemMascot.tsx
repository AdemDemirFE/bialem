import { useRef } from "react";
import { StyleSheet, View } from "react-native";
import { useTheme } from "../theme/theme";
import { buildMascotScene, type MascotState } from "./mascot-scene";
import { useBialemExperience } from "./useBialemExperience";
import { isExperienceEnabled } from "./capabilities";

export type { MascotState };

type Props = {
  size?: number;
  state?: MascotState;
  style?: import("react-native").ViewStyle;
  label?: string;
};

export function BialemMascot({ size = 76, state = "idle", style, label }: Props) {
  const { resolvedTheme } = useTheme();
  const containerRef = useRef(null);
  const { ready } = useBialemExperience({
    containerRef,
    theme: resolvedTheme,
    deps: [state, resolvedTheme],
    build: (context) => buildMascotScene(context, state)
  });

  return (
    <View
      ref={containerRef}
      aria-hidden={!label}
      accessibilityLabel={label}
      style={[styles.container, { width: size, height: size }, style]}
    />
  );
}

/** Decorative small mascot for contexts that already provide their own text. */
export function MascotBadge({ size = 40, state = "idle", style }: { size?: number; state?: MascotState; style?: import("react-native").ViewStyle }) {
  return <BialemMascot size={size} state={state} style={style} />;
}

export { isExperienceEnabled };

const styles = StyleSheet.create({
  container: {
    overflow: "hidden",
    pointerEvents: "none"
  }
});