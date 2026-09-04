import { useEffect, useRef, type ReactNode } from "react";
import { Animated, Easing, type StyleProp, type ViewStyle } from "react-native";
import { motion } from "../theme/tokens";
import { usePrefersReducedMotion } from "./usePrefersReducedMotion";

type RevealProps = {
  children: ReactNode;
  /** Baz gecikme (ms). */
  delay?: number;
  /** Liste içi sıra: delay + index * motion.stagger uygulanır. */
  index?: number;
  /** Dikey giriş mesafesi (px). */
  distance?: number;
  /** Süre (ms). Varsayılan motion.normal. */
  duration?: number;
  style?: StyleProp<ViewStyle>;
};

/** Fade + yukarı kayma giriş animasyonu. Reduced-motion'da anında gösterir. */
export function Reveal({ children, delay = 0, index = 0, distance = 14, duration, style }: RevealProps) {
  const reduce = usePrefersReducedMotion();
  const opacity = useRef(new Animated.Value(reduce ? 1 : 0)).current;
  const translate = useRef(new Animated.Value(reduce ? 0 : distance)).current;

  useEffect(() => {
    if (reduce) {
      opacity.setValue(1);
      translate.setValue(0);
      return;
    }
    const at = delay + index * motion.stagger;
    const ease = Easing.out(Easing.ease);
    const fade = Animated.timing(opacity, {
      toValue: 1,
      duration: duration ?? motion.normal,
      delay: at,
      easing: ease,
      useNativeDriver: true,
    });
    const slide = Animated.timing(translate, {
      toValue: 0,
      duration: duration ?? motion.normal,
      delay: at,
      easing: ease,
      useNativeDriver: true,
    });
    fade.start();
    slide.start();
    return () => {
      fade.stop();
      slide.stop();
    };
  }, [reduce, delay, index, distance, duration, opacity, translate]);

  return (
    <Animated.View style={[style, { opacity, transform: [{ translateY: translate }] }]}>
      {children}
    </Animated.View>
  );
}
