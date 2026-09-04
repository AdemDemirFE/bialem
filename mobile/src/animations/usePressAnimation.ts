import { useCallback, useRef } from "react";
import { Animated } from "react-native";

/** Basma geri bildirimi: press-in'de hafif küçül, bırakınca yaylanarak dön. */
export function usePressAnimation(activeScale = 0.97) {
  const scale = useRef(new Animated.Value(1)).current;

  const pressIn = useCallback(() => {
    Animated.spring(scale, { toValue: activeScale, useNativeDriver: true, speed: 48, bounciness: 0 }).start();
  }, [scale, activeScale]);

  const pressOut = useCallback(() => {
    Animated.spring(scale, { toValue: 1, useNativeDriver: true, speed: 32, bounciness: 5 }).start();
  }, [scale]);

  return { scale, pressIn, pressOut };
}
