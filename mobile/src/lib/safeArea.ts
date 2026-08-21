import { useSafeAreaInsets } from "react-native-safe-area-context";

export type ContentInsetOptions = {
  top?: number;
  bottom?: number;
  left?: number;
  right?: number;
};

/**
 * Raw safe-area insets. Prefer the helpers below to avoid duplicated arithmetic.
 */
export function useScreenInsets() {
  return useSafeAreaInsets();
}

/**
 * Returns padding values that combine the device safe-area insets with optional
 * extra spacing. Use this for ScrollView / FlatList contentContainerStyle or
 * fixed header/footer wrappers.
 *
 * Example:
 *   contentContainerStyle={[styles.page, useContentInsets({ top: 16, bottom: 28 })]}
 */
export function useContentInsets(extra: ContentInsetOptions = {}) {
  const { top, bottom, left, right } = useSafeAreaInsets();
  return {
    paddingTop: top + (extra.top ?? 0),
    paddingBottom: bottom + (extra.bottom ?? 0),
    paddingLeft: left + (extra.left ?? 0),
    paddingRight: right + (extra.right ?? 0)
  };
}

/**
 * Returns only top/bottom padding for the common case where horizontal spacing
 * is already handled by the screen's own styles.
 */
export function useVerticalInsets(extra: { top?: number; bottom?: number } = {}) {
  const { top, bottom } = useSafeAreaInsets();
  return {
    paddingTop: top + (extra.top ?? 0),
    paddingBottom: bottom + (extra.bottom ?? 0)
  };
}

/**
 * Convenience wrapper for fixed bottom bars / submit buttons.
 * Adds the home-indicator / gesture-navigation safe-area plus optional extra padding.
 */
export function useBottomInset(extra = 0) {
  const { bottom } = useSafeAreaInsets();
  return bottom + extra;
}

/**
 * Convenience wrapper for fixed headers / custom nav bars.
 * Adds the status-bar / notch safe-area plus optional extra padding.
 */
export function useTopInset(extra = 0) {
  const { top } = useSafeAreaInsets();
  return top + extra;
}
