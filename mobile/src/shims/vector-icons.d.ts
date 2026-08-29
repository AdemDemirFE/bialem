declare module "@expo/vector-icons" {
  import type { JSX } from "react";

  type IconProps = { name: string; size?: number; color?: string };

  export function Ionicons(props: IconProps): JSX.Element;
  export const glyphMap: Record<string, number>;

  const _default: typeof Ionicons & { glyphMap: Record<string, number> };
  export default _default;
}
