import { Ionicons } from "@expo/vector-icons";
import { StyleSheet, Text, View } from "react-native";
import { colors } from "../theme/colors";

export type HonorBadge = {
  badge_code: string;
  badge_name: string;
  description: string;
  reason: string | null;
  awarded_at: string;
};

export function HonorBadges({ badges }: { badges: HonorBadge[] }) {
  return (
    <View style={styles.panel}>
      <View style={styles.headingRow}>
        <View style={styles.headingIcon}>
          <Ionicons name="ribbon" size={20} color={colors.actionText} />
        </View>
        <View style={styles.headingCopy}>
          <Text style={styles.title}>Onur Madalyaları</Text>
          <Text style={styles.subtitle}>Doğrulanmış katılım ve topluluk katkısıyla kazanılır.</Text>
        </View>
      </View>

      {badges.length === 0 ? (
        <Text style={styles.empty}>İlk madalyan için etkinliklere katıl ve girişini doğrulat.</Text>
      ) : (
        <View style={styles.grid}>
          {badges.map((badge) => (
              <View key={badge.badge_code} style={styles.badge}>
                <View style={styles.medal}>
                  <Ionicons name="star" size={19} color={colors.actionText} />
                </View>
                <Text style={styles.badgeName}>{badge.badge_name}</Text>
                <Text style={styles.badgeDescription}>{badge.description}</Text>
                {badge.reason ? <Text style={styles.reason}>{badge.reason}</Text> : null}
              </View>
          ))}
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  panel: {
    backgroundColor: colors.surface,
    borderRadius: 19,
    borderWidth: 1,
    borderColor: colors.border,
    padding: 15,
    gap: 12
  },
  headingRow: { flexDirection: "row", alignItems: "center", gap: 12 },
  headingIcon: {
    width: 42,
    height: 42,
    borderRadius: 21,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.action
  },
  headingCopy: { flex: 1, gap: 3 },
  title: { color: colors.ink, fontSize: 18, fontWeight: "800" },
  subtitle: { color: colors.muted, fontSize: 13, lineHeight: 18 },
  empty: { color: colors.muted, fontSize: 15, lineHeight: 22 },
  grid: { flexDirection: "row", flexWrap: "wrap", gap: 12 },
  badge: {
    minWidth: 150,
    flexGrow: 1,
    flexBasis: "45%",
    borderRadius: 16,
    padding: 12,
    gap: 7,
    backgroundColor: colors.accentSoft,
    borderWidth: 1,
    borderColor: colors.border
  },
  medal: {
    width: 38,
    height: 38,
    borderRadius: 19,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.action
  },
  badgeName: { color: colors.ink, fontSize: 16, fontWeight: "900" },
  badgeDescription: { color: colors.muted, fontSize: 12, lineHeight: 17 },
  reason: { color: colors.accent, fontSize: 11, lineHeight: 15, fontWeight: "700" }
});
