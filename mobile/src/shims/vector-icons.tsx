import { Text } from "react-native";
import glyphMap from "react-native-vector-icons/glyphmaps/Ionicons.json";

type Props = { name: string; size?: number; color?: string };

export function Ionicons({ name, size = 20, color = "#111" }: Props) {
  const glyph = (glyphMap as Record<string, number>)[name] ?? (glyphMap as Record<string, number>)[`${name}-outline`];
  return (
    <Text style={{ fontFamily: "Ionicons", fontSize: size, color, lineHeight: size }}>
      {glyph ? String.fromCharCode(glyph) : "•"}
    </Text>
  );
}

export default { Ionicons };
