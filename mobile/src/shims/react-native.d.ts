declare module "react-native" {
  import type { ComponentType, ReactNode as RNReactNode, Ref, CSSProperties } from "react";

  export type ReactNode = RNReactNode;
  export type StyleProp<T> = T | T[] | undefined;
  export type ViewStyle = any;
  export type TextStyle = any;
  export type ImageStyle = any;

  export interface ViewProps {
    children?: ReactNode;
    style?: any;
    pointerEvents?: "auto" | "none" | "box-none" | "box-only";
    onLayout?: (event: any) => void;
    onPress?: () => void;
    onStartShouldSetResponder?: (event: any) => boolean;
    onMoveShouldSetResponder?: (event: any) => boolean;
    onResponderGrant?: (event: any) => void;
    onResponderMove?: (event: any) => void;
    onResponderRelease?: (event: any) => void;
    onResponderTerminate?: (event: any) => void;
    testID?: string;
    accessible?: boolean;
    accessibilityLabel?: string;
    accessibilityRole?: string;
    accessibilityState?: { disabled?: boolean; busy?: boolean; checked?: boolean; selected?: boolean };
    role?: string;
    collapsable?: boolean;
    hitSlop?: number | { top?: number; bottom?: number; left?: number; right?: number };
    ref?: Ref<any>;
  }
  export const View: ComponentType<ViewProps>;
  export const KeyboardAvoidingView: ComponentType<ViewProps & { behavior?: "height" | "position" | "padding"; keyboardVerticalOffset?: number }>;
  export const ImageBackground: ComponentType<ImageProps & { children?: ReactNode; imageStyle?: any }>;
  export const RefreshControl: ComponentType<{ refreshing?: boolean; onRefresh?: () => void; tintColor?: string; title?: string }>;

  export interface TextProps {
    children?: ReactNode;
    style?: any;
    numberOfLines?: number;
    onPress?: () => void;
    testID?: string;
    allowFontScaling?: boolean;
    adjustsFontSizeToFit?: boolean;
    minimumFontScale?: number;
  }
  export const Text: ComponentType<TextProps>;

  export interface PressableProps {
    children?: ReactNode | ((state: { pressed: boolean }) => ReactNode);
    style?: ((state: { pressed: boolean }) => any) | Record<string, any> | Array<Record<string, any> | undefined> | undefined;
    onPress?: () => void;
    onLongPress?: () => void;
    onPressIn?: () => void;
    onPressOut?: () => void;
    delayLongPress?: number;
    disabled?: boolean;
    testID?: string;
    accessibilityLabel?: string;
    accessibilityRole?: string;
    accessibilityState?: { disabled?: boolean; busy?: boolean; checked?: boolean; selected?: boolean };
    hitSlop?: number | { top?: number; bottom?: number; left?: number; right?: number };
  }
  export const Pressable: ComponentType<PressableProps>;

  export interface ScrollViewProps {
    children?: ReactNode;
    style?: any;
    contentContainerStyle?: any;
    horizontal?: boolean;
    showsHorizontalScrollIndicator?: boolean;
    showsVerticalScrollIndicator?: boolean;
    refreshControl?: ReactNode;
    keyboardShouldPersistTaps?: "always" | "never" | "handled" | boolean;
    keyboardDismissMode?: "none" | "onDrag" | "interactive" | "on-scroll";
    contentInsetAdjustmentBehavior?: "automatic" | "scrollableAxes" | "never" | "always";
    onScroll?: (event: any) => void;
    scrollEventThrottle?: number;
    ref?: Ref<any>;
  }
  export const ScrollView: ComponentType<ScrollViewProps> & { scrollTo: (options: { x?: number; y?: number; animated?: boolean }) => void; scrollToEnd: (options?: { animated?: boolean }) => void };

  export interface TextInputProps {
    value?: string;
    defaultValue?: string;
    onChangeText?: (text: string) => void;
    placeholder?: string;
    placeholderTextColor?: string;
    secureTextEntry?: boolean;
    multiline?: boolean;
    numberOfLines?: number;
    style?: any;
    keyboardType?: "default" | "email-address" | "numeric" | "phone-pad" | "number-pad" | "url";
    returnKeyType?: "done" | "go" | "next" | "search" | "send";
    autoFocus?: boolean;
    onSubmitEditing?: () => void;
    autoCapitalize?: "none" | "sentences" | "words" | "characters";
    autoCorrect?: boolean;
    editable?: boolean;
    maxLength?: number;
    testID?: string;
    ref?: Ref<any>;
  }
  export const TextInput: ComponentType<TextInputProps> & { focus: () => void; blur: () => void; clear: () => void };

  export interface ImageProps {
    source?: any;
    style?: any;
    resizeMode?: "cover" | "contain" | "stretch" | "center";
    fadeDuration?: number;
    pointerEvents?: "auto" | "none" | "box-none" | "box-only";
    accessibilityLabel?: string;
    onLoad?: () => void;
    onError?: () => void;
  }
  export const Image: ComponentType<ImageProps>;

  export interface FlatListProps<T> {
    data?: T[];
    renderItem?: (info: { item: T; index: number }) => ReactNode;
    keyExtractor?: (item: T, index: number) => string;
    ListEmptyComponent?: ReactNode | ComponentType<any>;
    ListHeaderComponent?: ReactNode | ComponentType<any>;
    ListFooterComponent?: ReactNode | ComponentType<any>;
    ItemSeparatorComponent?: ReactNode | ComponentType<any>;
    refreshControl?: ReactNode;
    refreshing?: boolean;
    onRefresh?: () => void;
    onEndReached?: () => void;
    onEndReachedThreshold?: number;
    horizontal?: boolean;
    showsHorizontalScrollIndicator?: boolean;
    showsVerticalScrollIndicator?: boolean;
    contentContainerStyle?: any;
    style?: any;
    keyboardShouldPersistTaps?: "always" | "never" | "handled" | boolean;
    ref?: Ref<any>;
  }
  export const FlatList: ComponentType<any> & { scrollToEnd: (params?: { animated?: boolean }) => void; scrollToIndex: (params: any) => void; scrollToOffset: (params: any) => void };

  export interface TouchableOpacityProps {
    children?: ReactNode;
    style?: any;
    onPress?: () => void;
    activeOpacity?: number;
    disabled?: boolean;
  }
  export const TouchableOpacity: ComponentType<TouchableOpacityProps>;

  export interface TouchableWithoutFeedbackProps {
    children?: ReactNode;
    onPress?: () => void;
    disabled?: boolean;
  }
  export const TouchableWithoutFeedback: ComponentType<TouchableWithoutFeedbackProps>;

  export interface ActivityIndicatorProps {
    size?: "small" | "large" | number;
    color?: string;
    style?: any;
  }
  export const ActivityIndicator: ComponentType<ActivityIndicatorProps>;

  export interface ModalProps {
    visible?: boolean;
    transparent?: boolean;
    animationType?: "none" | "slide" | "fade";
    onRequestClose?: () => void;
    children?: ReactNode;
  }
  export const Modal: ComponentType<ModalProps>;

  export interface SwitchProps {
    value?: boolean;
    onValueChange?: (value: boolean) => void;
    trackColor?: { false?: string; true?: string };
    thumbColor?: string;
    disabled?: boolean;
    accessibilityLabel?: string;
  }
  export const Switch: ComponentType<SwitchProps>;

  export const Platform: {
    OS: "ios" | "android" | "web" | "windows" | "macos";
    select<T>(spec: { ios?: T; android?: T; web?: T; default?: T }): T | undefined;
  };

  export const StyleSheet: {
    create<T extends Record<string, any>>(styles: T): T;
    flatten(style?: any): any;
    absoluteFill: any;
    absoluteFillObject: any;
    hairlineWidth: number;
  };

  export const Dimensions: {
    get(dim: "window" | "screen"): { width: number; height: number; scale: number; fontScale: number };
    addEventListener: (type: "change", handler: (dims: { window: { width: number; height: number } }) => void) => { remove: () => void };
  };

  export const Animated: {
    View: ComponentType<ViewProps>;
    Text: ComponentType<TextProps>;
    Image: ComponentType<ImageProps>;
    createAnimatedComponent: <P>(component: ComponentType<P>) => ComponentType<P>;
    Value: new (value: number) => any;
    ValueXY: new (value?: { x: number; y: number }) => any;
    event: (argMapping: any[], config?: { listener?: (event: any) => void; useNativeDriver?: boolean }) => (event: any) => void;
    timing: (value: any, config: any) => { start: (callback?: () => void) => void; stop: () => void };
    spring: (value: any, config: any) => { start: (callback?: () => void) => void; stop: () => void };
    loop: (animation: { start: (callback?: () => void) => void; stop?: () => void }) => { start: (callback?: () => void) => void; stop: () => void };
    sequence: (animations: Array<{ start: (callback?: () => void) => void; stop?: () => void }>) => { start: (callback?: () => void) => void; stop: () => void };
    Easing: {
      linear: (value: number) => number;
      ease: (value: number) => number;
      in: (easing: (value: number) => number) => (value: number) => number;
      out: (easing: (value: number) => number) => (value: number) => number;
      inOut: (easing: (value: number) => number) => (value: number) => number;
    };
  };

  export const PanResponder: {
    create: (config: any) => { panHandlers: any };
  };

  export const Alert: {
    alert(title: string, message?: string, buttons?: Array<{ text: string; onPress?: () => void; style?: "default" | "cancel" | "destructive" }>): void;
  };

  export const Linking: {
    openURL(url: string): Promise<void>;
    canOpenURL(url: string): Promise<boolean>;
    getInitialURL(): Promise<string | null>;
    openSettings(): Promise<void>;
  };

  export const Share: {
    share(content: { message?: string; url?: string; title?: string; type?: string }, options?: { dialogTitle?: string; subject?: string; excludedActivityTypes?: string[] }): Promise<{ action: string }>;
  };

  export function useWindowDimensions(): { width: number; height: number; scale: number; fontScale: number };

  export const Keyboard: {
    dismiss(): void;
  };

  export const Easing: {
    linear: (value: number) => number;
    ease: (value: number) => number;
    quad: (value: number) => number;
    cubic: (value: number) => number;
    in: (easing: (value: number) => number) => (value: number) => number;
    out: (easing: (value: number) => number) => (value: number) => number;
    inOut: (easing: (value: number) => number) => (value: number) => number;
  };

  export function DynamicColorIOS(colors: { light: string; dark: string }): string;
  export function PlatformColor(...names: string[]): string;
  export type ColorValue = string;
  export type ImageSourcePropType = any;

  export const Appearance: {
    getColorScheme(): "light" | "dark" | null;
    addChangeListener: (listener: ({ colorScheme }: { colorScheme: "light" | "dark" | null }) => void) => { remove: () => void };
  };
  export function useColorScheme(): "light" | "dark" | null;

  export const AppRegistry: {
    registerComponent(appKey: string, getComponentFn: () => ComponentType<any>): void;
  };

  export interface SafeAreaViewProps {
    children?: ReactNode;
    style?: any;
    edges?: Array<"top" | "bottom" | "left" | "right">;
  }
  export const SafeAreaView: ComponentType<SafeAreaViewProps>;
}
