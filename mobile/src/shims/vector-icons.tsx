import type { JSX } from "react";
import { Text, View } from "react-native";
import glyphMap from "react-native-vector-icons/glyphmaps/Ionicons.json";

type Props = { name: string; size?: number; color?: string };

interface IoniconsType {
  (props: Props): JSX.Element;
  glyphMap: Record<string, number>;
}

const Ionicons: IoniconsType = (({ name, size = 20, color = "#111" }: Props) => {
  const glyph = (glyphMap as Record<string, number>)[name] ?? (glyphMap as Record<string, number>)[`${name}-outline`];
  return (
    <View pointerEvents="none" style={{ width: size, height: size, alignItems: "center", justifyContent: "center", overflow: "visible" }}>
      <Text style={{ width: size, height: size, fontFamily: "Ionicons", fontSize: size, lineHeight: size, color, textAlign: "center", includeFontPadding: false }}>
        {glyph ? String.fromCharCode(glyph) : "•"}
      </Text>
    </View>
  );
}) as IoniconsType;

Ionicons.glyphMap = glyphMap;

export { Ionicons };
export default Ionicons;
