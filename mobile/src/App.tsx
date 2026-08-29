import { bindRouter } from "./shims/expo-router";
import { Capacitor } from "@capacitor/core";
import { BrowserRouter, Route, Routes, useLocation, useNavigate } from "react-router-dom";
import { SafeAreaProvider } from "react-native-safe-area-context";
import RootLayout from "../app/_layout";
import TabsLayout from "../app/(tabs)/_layout";
import FeedScreen from "../app/(tabs)/feed";
import CommunitiesScreen from "../app/(tabs)/communities";
import NotificationsScreen from "../app/(tabs)/notifications";
import CalendarScreen from "../app/(tabs)/calendar";
import AssistantScreen from "../app/(tabs)/assistant";
import CartScreen from "../app/(tabs)/cart";
import MessagesScreen from "../app/messages/index";
import DirectChatScreen from "../app/messages/[id]";
import ProfileScreen from "../app/(tabs)/profile";
import ManagementScreen from "../app/(tabs)/management";
import ManagementUsersScreen from "../app/management/users";
import ManagementUserDetailScreen from "../app/management/users/[id]";
import ManagementNewUserScreen from "../app/management/users/new";
import ManagementCommunitiesScreen from "../app/management/communities";
import ManagementCommunityDetailScreen from "../app/management/communities/[id]";
import ManagementEventsScreen from "../app/management/events";
import ManagementEventDetailScreen from "../app/management/events/[id]";
import ManagementNotificationsScreen from "../app/management/notifications";
import ManagementNotificationDetailScreen from "../app/management/notifications/[id]";
import ManagementRolesScreen from "../app/management/roles";
import ManagementModerationScreen from "../app/management/moderation";
import ManagementDataScreen from "../app/management/data";
import StoreManagementScreen from "../app/management/store";
import StoreManagementProductsScreen from "../app/management/store/products";
import StoreManagementCategoriesScreen from "../app/management/store/categories";
import StoreManagementBrandsScreen from "../app/management/store/brands";
import StoreManagementOrdersScreen from "../app/management/store/orders";
import StoreManagementShipmentsScreen from "../app/management/store/shipments";
import StoreManagementAddressesScreen from "../app/management/store/addresses";
import StoreManagementReviewsScreen from "../app/management/store/reviews";
import HomeScreen from "../app/index";
import AccountScreen from "../app/account";
import SettingsScreen from "../app/settings";
import MyPlansScreen from "../app/my-plans";
import CityRadarScreen from "../app/city-radar";
import CityEventScreen from "../app/city-event/[id]";
import CommunityScreen from "../app/community/[id]";
import CommunityMembersScreen from "../app/community/[id]/members";
import CommunityAssistantsScreen from "../app/community/[id]/assistants";
import GroupScreen from "../app/group/[id]";
import EventScreen from "../app/event/[id]";
import EventChatScreen from "../app/event/[id]/chat";
import EventPosterScreen from "../app/event/[id]/poster";
import EventCheckInScreen from "../app/event/[id]/check-in";
import EventShareScreen from "../app/event-share/[id]";
import PostScreen from "../app/post/[id]";
import StoryScreen from "../app/story/[id]";
import StoryCreateScreen from "../app/story/create";
import UserScreen from "../app/user/[id]";
import PeopleScreen from "../app/people/index";
import PeopleRequestsScreen from "../app/people/requests";
import PeopleConnectionsScreen from "../app/people/connections";
import BlockedUsersScreen from "../app/blocked-users";
import ProfileEditScreen from "../app/profile/edit";
import OrganizerRequestScreen from "../app/organizer-request";
import AdvantagesScreen from "../app/advantages/index";
import AdvantageDetailScreen from "../app/advantages/[id]";
import AdvantageRedeemScreen from "../app/advantages/redeem";
import StoreScreen from "../app/store/index";
import StoreCartScreen from "../app/store/cart";
import StoreCheckoutScreen from "../app/store/checkout";
import StorePaymentScreen from "../app/store/payment";
import StoreAddressesScreen from "../app/store/addresses";
import StoreOrdersScreen from "../app/store/orders";
import StoreOrderDetailScreen from "../app/store/orders/[id]";
import StoreProductScreen from "../app/store/product/[slug]";
import StoreCategoryScreen from "../app/store/category/[slug]";
import StoreSearchScreen from "../app/store/search";
import LegalScreen from "../app/legal/[document]";
import ResetPasswordScreen from "../app/reset-password";
import ForgotPasswordScreen from "../app/forgot-password";
import { type ReactNode, useEffect } from "react";
import { RouteErrorBoundary } from "./components/RouteErrorBoundary";

function RouterBinder({ children }: { children: ReactNode }) {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    bindRouter(
      (to, opts) => navigate(to, { replace: opts?.replace }),
      () => navigate(-1)
    );
  }, [navigate]);

  useEffect(() => {
    if (!Capacitor.isNativePlatform()) return;

    let listener: { remove: () => void } | null = null;
    let disposed = false;
    const setup = async () => {
      try {
        const { App: CapacitorApp } = await import("@capacitor/app");
        if (disposed) return;
        listener = await CapacitorApp.addListener("backButton", ({ canGoBack }) => {
          if (canGoBack) {
            navigate(-1);
          } else {
            void CapacitorApp.exitApp();
          }
        });
      } catch {
        // Plugin web ortamında yüklü olmayabilir; sessizce geç.
      }
    };
    void setup();
    return () => {
      disposed = true;
      listener?.remove();
    };
  }, [navigate]);

  return (
    <RouteErrorBoundary resetKey={location.pathname} onReset={() => navigate("/feed", { replace: true })}>
      {children}
    </RouteErrorBoundary>
  );
}

export function App() {
  return (
    <SafeAreaProvider style={{ flex: 1, minHeight: "100%" }}>
      <BrowserRouter>
        <RouterBinder>
          <Routes>
            <Route element={<RootLayout />}>
              <Route path="/" element={<HomeScreen />} />
              <Route element={<TabsLayout />}>
                <Route path="/feed" element={<FeedScreen />} />
                <Route path="/store" element={<StoreScreen />} />
                <Route path="/communities" element={<CommunitiesScreen />} />
                <Route path="/calendar" element={<CalendarScreen />} />
                <Route path="/profile" element={<ProfileScreen />} />
                <Route path="/management" element={<ManagementScreen />} />
              </Route>
              <Route path="/notifications" element={<NotificationsScreen />} />
              <Route path="/account" element={<AccountScreen />} />
              <Route path="/settings" element={<SettingsScreen />} />
              <Route path="/management/users" element={<ManagementUsersScreen />} />
              <Route path="/management/users/new" element={<ManagementNewUserScreen />} />
              <Route path="/management/users/:id" element={<ManagementUserDetailScreen />} />
              <Route path="/management/communities" element={<ManagementCommunitiesScreen />} />
              <Route path="/management/communities/:id" element={<ManagementCommunityDetailScreen />} />
              <Route path="/management/events" element={<ManagementEventsScreen />} />
              <Route path="/management/events/:id" element={<ManagementEventDetailScreen />} />
              <Route path="/management/notifications" element={<ManagementNotificationsScreen />} />
              <Route path="/management/notifications/:id" element={<ManagementNotificationDetailScreen />} />
              <Route path="/management/roles" element={<ManagementRolesScreen />} />
              <Route path="/management/moderation" element={<ManagementModerationScreen />} />
              <Route path="/management/data" element={<ManagementDataScreen />} />
              <Route path="/management/store" element={<StoreManagementScreen />} />
              <Route path="/management/store/products" element={<StoreManagementProductsScreen />} />
              <Route path="/management/store/categories" element={<StoreManagementCategoriesScreen />} />
              <Route path="/management/store/brands" element={<StoreManagementBrandsScreen />} />
              <Route path="/management/store/orders" element={<StoreManagementOrdersScreen />} />
              <Route path="/management/store/shipments" element={<StoreManagementShipmentsScreen />} />
              <Route path="/management/store/addresses" element={<StoreManagementAddressesScreen />} />
              <Route path="/management/store/reviews" element={<StoreManagementReviewsScreen />} />
              <Route path="/my-plans" element={<MyPlansScreen />} />
              <Route path="/city-radar" element={<CityRadarScreen />} />
              <Route path="/city-event/:id" element={<CityEventScreen />} />
              <Route path="/community/:id" element={<CommunityScreen />} />
              <Route path="/community/:id/members" element={<CommunityMembersScreen />} />
              <Route path="/community/:id/assistants" element={<CommunityAssistantsScreen />} />
              <Route path="/group/:id" element={<GroupScreen />} />
              <Route path="/event/:id" element={<EventScreen />} />
              <Route path="/event/:id/chat" element={<EventChatScreen />} />
              <Route path="/event/:id/poster" element={<EventPosterScreen />} />
              <Route path="/event/:id/check-in" element={<EventCheckInScreen />} />
              <Route path="/event-share/:id" element={<EventShareScreen />} />
              <Route path="/post/:id" element={<PostScreen />} />
              <Route path="/story/create" element={<StoryCreateScreen />} />
              <Route path="/story/:id" element={<StoryScreen />} />
              <Route path="/user/:id" element={<UserScreen />} />
              <Route path="/people" element={<PeopleScreen />} />
              <Route path="/messages" element={<MessagesScreen />} />
              <Route path="/messages/:id" element={<DirectChatScreen />} />
              <Route path="/people/requests" element={<PeopleRequestsScreen />} />
              <Route path="/people/connections" element={<PeopleConnectionsScreen />} />
              <Route path="/blocked-users" element={<BlockedUsersScreen />} />
              <Route path="/profile/edit" element={<ProfileEditScreen />} />
              <Route path="/organizer-request" element={<OrganizerRequestScreen />} />
              <Route path="/advantages" element={<AdvantagesScreen />} />
              <Route path="/advantages/redeem" element={<AdvantageRedeemScreen />} />
              <Route path="/advantages/:id" element={<AdvantageDetailScreen />} />
              <Route path="/store/cart" element={<StoreCartScreen />} />
              <Route path="/store/checkout" element={<StoreCheckoutScreen />} />
              <Route path="/store/payment" element={<StorePaymentScreen />} />
              <Route path="/store/addresses" element={<StoreAddressesScreen />} />
              <Route path="/cart" element={<CartScreen />} />
              <Route path="/store/orders" element={<StoreOrdersScreen />} />
              <Route path="/store/orders/:id" element={<StoreOrderDetailScreen />} />
              <Route path="/store/product/:slug" element={<StoreProductScreen />} />
              <Route path="/store/category/:slug" element={<StoreCategoryScreen />} />
              <Route path="/store/search" element={<StoreSearchScreen />} />
              <Route path="/legal/:document" element={<LegalScreen />} />
              <Route path="/forgot-password" element={<ForgotPasswordScreen />} />
              <Route path="/reset-password" element={<ResetPasswordScreen />} />
            </Route>
          </Routes>
        </RouterBinder>
      </BrowserRouter>
    </SafeAreaProvider>
  );
}
