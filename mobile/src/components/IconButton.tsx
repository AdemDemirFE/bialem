import { Ionicons } from "@expo/vector-icons";
import { ActivityIndicator, Pressable, type StyleProp, type ViewStyle } from "react-native";
import { colors } from "../theme/colors";
import { radius, sizes } from "../theme/tokens";

type IconButtonProps = {
  icon: string;
  accessibilityLabel: string;
  onPress?: () => void;
  size?: 36 | 40 | 44;
  iconSize?: number;
  color?: string;
  backgroundColor?: string;
  borderColor?: string;
  disabled?: boolean;
  loading?: boolean;
  style?: StyleProp<ViewStyle>;
};

export function IconButton({ icon, accessibilityLabel, onPress, size = 40,
  iconSize = size === 44 ? 23 : size === 36 ? 19 : 21, color = colors.ink as string,
  backgroundColor = colors.surfaceStrong as string, borderColor = colors.border as string,
  disabled = false, loading = false, style }: IconButtonProps) {
  const inactive = disabled || loading;
  return <Pressable accessibilityRole="button" accessibilityLabel={accessibilityLabel}
    accessibilityState={{ disabled: inactive, busy: loading }} disabled={inactive} hitSlop={5} onPress={onPress}
    style={({ pressed }) => [{ width: size, height: size, minWidth: size, minHeight: size, padding: 0, margin: 0,
      alignItems: "center", justifyContent: "center", borderRadius: size === sizes.touch ? radius.lg : radius.md, borderWidth: 1,
      borderColor, backgroundColor, opacity: inactive ? .48 : pressed ? .72 : 1,
      transform: [{ scale: pressed && !inactive ? .95 : 1 }] }, style]}>
    {loading ? <ActivityIndicator size="small" color={color} /> : <Ionicons name={icon} size={iconSize} color={color} />}
  </Pressable>;
}

export function BackButton(props: Omit<IconButtonProps,"icon"|"accessibilityLabel">) {
  return <IconButton icon="chevron-back" accessibilityLabel="Geri dön" {...props} />;
}
