import { useEffect, useMemo } from "react";
import { Animated, Easing, type StyleProp, type TextStyle } from "react-native";
import { motion } from "../theme/tokens";
import { usePrefersReducedMotion } from "./usePrefersReducedMotion";

type StaggerTextProps = {
  text: string;
  style?: StyleProp<TextStyle>;
  /** Baz gecikme (ms). */
  delay?: number;
  /** Kelime başına ek gecikme (ms). Varsayılan motion.stagger'ın yarısı. */
  step?: number;
  /** Dikey giriş mesafesi (px). */
  distance?: number;
  /** Kelime başına süre (ms). */
  duration?: number;
};

/** Başlık yazılarını kelime kelime solduran + yükselten giriş animasyonu. */
export function StaggerText({ text, style, delay = 0, step, distance = 10, duration }: StaggerTextProps) {
  const reduce = usePrefersReducedMotion();
  const words = useMemo(() => text.split(" "), [text]);
  const anims = useMemo(
    () => words.map(() => new Animated.Value(0)),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [words.length]
  );

  useEffect(() => {
    if (reduce) {
      anims.forEach((a) => a.setValue(1));
      return;
    }
    const per = step ?? Math.round(motion.stagger / 2);
    const timers = anims.map((a, i) =>
      Animated.timing(a, {
        toValue: 1,
        duration: duration ?? motion.fast,
        delay: delay + i * per,
        easing: Easing.out(Easing.ease),
        useNativeDriver: true,
      })
    );
    timers.forEach((t) => t.start());
    return () => timers.forEach((t) => t.stop());
  }, [reduce, anims, delay, step, duration]);

  if (reduce) {
    return <Animated.Text style={style}>{text}</Animated.Text>;
  }

  return (
    <Animated.Text style={style}>
      {words.map((word, i) => (
        <Animated.Text
          key={`${i}-${word}`}
          style={{
            opacity: anims[i],
            transform: [
              {
                translateY: anims[i].interpolate({
                  inputRange: [0, 1],
                  outputRange: [distance, 0],
                }),
              },
            ],
          }}
        >
          {word}
          {i < words.length - 1 ? " " : ""}
        </Animated.Text>
      ))}
    </Animated.Text>
  );
}
