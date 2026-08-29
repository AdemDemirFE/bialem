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
import { AppHeader } from "../components/ui/AppHeader";

function normalizeHref(href: string) {
  return href.replace("/(tabs)", "").replace(/^\/tabs/, "") || "/";
}

function resolveTarget(to: string | { pathname?: string; params?: Record<string, string> }) {
  if (typeof to === "string") return normalizeHref(to);
  let path = normalizeHref(to.pathname || "/");
  const search = new URLSearchParams();
  Object.entries(to.params || {}).forEach(([key, value]) => {
    const placeholder = `[${key}]`;
    if (path.includes(placeholder)) {
      path = path.replace(placeholder, encodeURIComponent(String(value)));
    } else if (value !== undefined && value !== null && String(value).length > 0) {
      search.set(key, String(value));
    }
  });
  const query = search.toString();
  return query ? `${path}${path.includes("?") ? "&" : "?"}${query}` : path;
}

export type Href = string | { pathname?: string; params?: Record<string, string | number | undefined> };

const HeaderContext = createContext<(options: Record<string, unknown>) => void>(() => undefined);

export function useRouter() {
  const navigate = useNavigate();
  return {
    push: (to: Href) => navigate(resolveTarget(to as string | { pathname?: string; params?: Record<string, string> })),
    replace: (to: Href) => navigate(resolveTarget(to as string | { pathname?: string; params?: Record<string, string> }), { replace: true }),
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
  push(to: Href) {
    navigateRef?.(resolveTarget(to as string | { pathname?: string; params?: Record<string, string> }));
  },
  replace(to: Href) {
    navigateRef?.(resolveTarget(to as string | { pathname?: string; params?: Record<string, string> }), { replace: true });
  },
  back() {
    backRef?.();
  }
};

export function useLocalSearchParams<T extends Record<string, string | string[] | undefined>>() {
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
      onPress: () => navigate(to)
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
          opacity: progress.interpolate({ inputRange: [0, 1], outputRange: [0.96, 1] }),
          transform: [{ translateX: progress.interpolate({ inputRange: [0, 1], outputRange: [14, 0] }) }]
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
  const navigate = useNavigate();
  const location = useLocation();
  useEffect(() => {
    if (options?.title) document.title = String(options.title);
    setOptions(options || {});
  }, [options, setOptions]);
  if (options?.headerShown === false) return null;
  if (!options?.title) return null;
  const showBack = options?.headerBackVisible !== false;
  const goBack = () => {
    const historyIndex = typeof window !== "undefined" ? Number(window.history.state?.idx ?? 0) : 0;
    if (historyIndex > 0) {
      navigate(-1);
      return;
    }
    navigate(resolveBackFallback(location.pathname), { replace: true });
  };
  const action = typeof options?.headerRight === "function" ? (options.headerRight as () => ReactNode)() : (options?.headerRight as ReactNode);
  return <AppHeader title={String(options.title)} onBack={showBack ? goBack : undefined} action={action} />;
};

function resolveBackFallback(pathname: string) {
  const segments = pathname.split("/").filter(Boolean);
  if (segments[0] === "event" && segments.length > 2) return `/event/${segments[1]}`;
  if (segments[0] === "community" && segments.length > 2) return `/community/${segments[1]}`;
  if (segments[0] === "advantages" && segments.length > 1) return "/advantages";
  if (segments[0] === "messages" && segments.length > 1) return "/messages";
  if (segments[0] === "people" && segments.length > 1) return "/people";
  if (["settings", "account", "my-plans", "blocked-users", "profile"].includes(segments[0] || "")) return "/profile";
  return "/feed";
}

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
  const screens = Children.toArray(children)
    .filter(isValidElement)
    .filter((screen) => (screen as { props: { options?: { href?: string | null } } }).props.options?.href !== null) as Array<{
      props: { name: string; options?: any };
    }>;
  return (
    <View style={{ flex: 1 }}>
      <View style={{ flex: 1 }}>
        <Outlet />
      </View>
      <View style={[{
        flexDirection: "row", justifyContent: "space-around", alignItems: "center", zIndex: 20,
        boxShadow: "0 -8px 28px rgba(11, 23, 48, 0.08)"
      }, screenOptions?.tabBarStyle]}>
        {screens.map((screen) => {
          const path = screen.props.options?.href || `/${screen.props.name}`;
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
                minHeight: 52,
                alignItems: "center",
                justifyContent: "center",
                gap: 3,
                paddingHorizontal: 6,
                paddingVertical: 4,
                flex: 1,
                marginHorizontal: 2,
                borderRadius: 17,
                backgroundColor: active ? screenOptions?.tabBarActiveBackgroundColor : "transparent",
                opacity: pressed ? 0.8 : 1,
                transform: [{ translateY: active ? -1 : 0 }, { scale: pressed ? 0.95 : 1 }],
                transitionProperty: "background-color, transform, opacity",
                transitionDuration: "180ms"
              })}
            >
              {screen.props.options?.tabBarIcon?.({ color, size: active ? 21 : 20, focused: active })}
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

Tabs.Screen = function TabsScreen(_props: {
  name: string;
  options?: {
    title?: string;
    href?: string | null;
    tabBarLabel?: string;
    tabBarIcon?: (info: { color: string; size: number; focused: boolean }) => ReactNode;
  } & Record<string, unknown>;
}) {
  return null;
};

export default function ExpoRouterOutlet() {
  return <Outlet />;
}
