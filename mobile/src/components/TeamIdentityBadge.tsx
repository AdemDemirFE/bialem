import { Ionicons } from "@expo/vector-icons";
import { StyleSheet, Text, View } from "react-native";
import { PLATFORM_TEAM_TITLES, type PlatformTeamRole } from "../lib/team-identities";
import { colors } from "../theme/colors";

const ICONS: Record<PlatformTeamRole, keyof typeof Ionicons.glyphMap> = {
  founder: "star",
  team: "shield-checkmark",
  support: "headset",
  editor: "create",
  community_moderator: "shield-half",
  external_community_manager: "logo-whatsapp"
};

export function TeamIdentityBadge({
  role,
  compact = false
}: {
  role?: PlatformTeamRole | null;
  compact?: boolean;
}) {
  if (!role) return null;

  const title = PLATFORM_TEAM_TITLES[role];
  const external = role === "external_community_manager";
  if (compact) {
    return (
      <View style={[styles.compact, external && styles.external]} accessibilityLabel={title}>
        <Ionicons name={ICONS[role]} size={11} color={colors.actionText} />
      </View>
    );
  }

  return (
    <View style={[styles.badge, external && styles.external]} accessibilityLabel={title}>
      <Ionicons name={ICONS[role]} size={14} color={colors.actionText} />
      <Text style={styles.label}>{title}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  compact: {
    width: 20,
    height: 20,
    borderRadius: 10,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.accent
  },
  external: {
    backgroundColor: "#238b57"
  },
  badge: {
    alignSelf: "flex-start",
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 999,
    backgroundColor: colors.accent
  },
  label: {
    color: colors.actionText,
    fontSize: 12,
    fontWeight: "900"
  }
});
