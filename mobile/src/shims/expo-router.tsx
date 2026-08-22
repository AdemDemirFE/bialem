import {
  Children,
  cloneElement,
  createContext,
  isValidElement,
  useContext,
  useEffect,
  useRef,
  type CSSProperties,
  type ReactNode
} from "react";
import {
  Link as RouterLink,
  Navigate,
  Outlet,
  useLocation,
  useNavigate,
  useParams,
  useSearchParams
} from "react-router-dom";
import { Animated, Easing, Pressable, Text, View } from "react-native";

function normalizeHref(href: string) {
  return href.replace("/(tabs)", "").replace(/^\/tabs/, "") || "/";
}

function resolveTarget(to: string | { pathname?: string; params?: Record<string, string> }) {
  if (typeof to === "string") return normalizeHref(to);
  let path = normalizeHref(to.pathname || "/");
  Object.entries(to.params || {}).forEach(([key, value]) => {
    path = path.replace(`[${key}]`, encodeURIComponent(value));
  });
  return path;
}

const HeaderContext = createContext<(options: Record<string, unknown>) => void>(() => undefined);

export function useRouter() {
  const navigate = useNavigate();
  return {
    push: (to: never) => navigate(resolveTarget(to as string | { pathname?: string; params?: Record<string, string> })),
    replace: (to: never) => navigate(resolveTarget(to as string | { pathname?: string; params?: Record<string, string> }), { replace: true }),
    back: () => navigate(-1)
  };
}

let navigateRef: ((to: string, opts?: { replace?: boolean }) => void) | null = null;
let backRef: (() => void) | null = null;

export function bindRouter(navigate: (to: string, opts?: { replace?: boolean }) => void, back: () => void) {
  navigateRef = navigate;
  backRef = back;
}

export const router = {
  push(to: never) {
    navigateRef?.(resolveTarget(to as string | { pathname?: string; params?: Record<string, string> }));
  },
  replace(to: never) {
    navigateRef?.(resolveTarget(to as string | { pathname?: string; params?: Record<string, string> }), { replace: true });
  },
  back() {
    backRef?.();
  }
};

export function useLocalSearchParams<T extends Record<string, string>>() {
  const params = useParams();
  const [search] = useSearchParams();
  const merged: Record<string, string> = { ...(params as Record<string, string>) };
  search.forEach((value, key) => {
    merged[key] = value;
  });
  return merged as T;
}

export function useFocusEffect(callback: () => void | (() => void)) {
  const location = useLocation();
  useEffect(() => callback(), [location.pathname]);
}

export function Redirect({ href }: { href: string }) {
  return <Navigate to={normalizeHref(href)} replace />;
}

export function Link({
  href,
  asChild,
  children
}: {
  href: string | { pathname: string; params?: Record<string, string> };
  asChild?: boolean;
  children?: ReactNode;
}) {
  const navigate = useNavigate();
  const to = resolveTarget(href);
  if (asChild && isValidElement(children)) {
    return cloneElement(children as never, {
      onPress: () => navigate(to),
      href: to
    });
  }
  return (
    <RouterLink to={to} style={{ textDecoration: "none" }}>
      {children}
    </RouterLink>
  );
}

export function Stack({ children, screenOptions }: { children?: ReactNode; screenOptions?: Record<string, unknown> }) {
  const location = useLocation();
  const progress = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    progress.setValue(0);
    Animated.timing(progress, {
      toValue: 1,
      duration: 220,
      easing: Easing.out(Easing.cubic),
      useNativeDriver: true
    }).start();
  }, [location.pathname, progress]);

  return (
    <View style={{ flex: 1, backgroundColor: (screenOptions?.contentStyle as { backgroundColor?: string } | undefined)?.backgroundColor }}>
      <Animated.View
        style={{
          flex: 1,
          opacity: progress,
          transform: [{ translateY: progress.interpolate({ inputRange: [0, 1], outputRange: [5, 0] }) }]
        }}
      >
        <Outlet />
      </Animated.View>
      {children}
    </View>
  );
}

Stack.Screen = function StackScreen({ options }: { options?: Record<string, unknown> }) {
  const setOptions = useContext(HeaderContext);
  useEffect(() => {
    if (options?.title) document.title = String(options.title);
    setOptions(options || {});
  }, [options, setOptions]);
  if (options?.headerShown === false) return null;
  if (!options?.title) return null;
  return (
    <View style={{ paddingHorizontal: 16, paddingVertical: 12, borderBottomWidth: 1, borderBottomColor: "#d7e0f5" }}>
      <Text style={{ fontWeight: "800", fontSize: 18 }}>{String(options.title)}</Text>
    </View>
  );
};

export function Tabs({
  children,
  screenOptions
}: {
  children?: ReactNode;
  screenOptions?: Record<string, any>;
  initialRouteName?: string;
  backBehavior?: string;
}) {
  const location = useLocation();
  const navigate = useNavigate();
  const screens = Children.toArray(children).filter(isValidElement) as Array<{ props: { name: string; options?: any } }>;
  return (
    <View style={{ flex: 1 }}>
      <View style={{ flex: 1 }}>
        <Outlet />
      </View>
      <View style={[{ flexDirection: "row", justifyContent: "space-around", alignItems: "center" }, screenOptions?.tabBarStyle]}>
        {screens.map((screen) => {
          const path = `/${screen.props.name}`;
          const active = location.pathname === path || location.pathname.startsWith(`${path}/`);
          const color = active ? screenOptions?.tabBarActiveTintColor : screenOptions?.tabBarInactiveTintColor;
          return (
            <Pressable
              key={screen.props.name}
              accessibilityRole="tab"
              accessibilityState={{ selected: active }}
              hitSlop={4}
              onPress={() => navigate(path)}
              style={({ pressed }) => ({
                minHeight: 44,
                alignItems: "center",
                justifyContent: "center",
                gap: 2,
                paddingHorizontal: 6,
                paddingVertical: 4,
                flex: 1,
                opacity: pressed ? 0.72 : 1,
                transform: [{ scale: pressed ? 0.96 : 1 }]
              })}
            >
              {screen.props.options?.tabBarIcon?.({ color, size: active ? 21 : 20 })}
              <Text style={[{ color, fontSize: 10, fontWeight: "700" }, screenOptions?.tabBarLabelStyle]}>
                {screen.props.options?.tabBarLabel || screen.props.options?.title || screen.props.name}
              </Text>
            </Pressable>
          );
        })}
      </View>
    </View>
  );
}

Tabs.Screen = function TabsScreen(_props: { name: string; options?: Record<string, unknown> }) {
  return null;
};

export default function ExpoRouterOutlet() {
  return <Outlet />;
}
