import { Ionicons } from "@expo/vector-icons";
import { Link } from "expo-router";
import { Image, Pressable, StyleSheet, Text, View } from "react-native";
import { TeamIdentityBadge } from "./TeamIdentityBadge";
import type { PlatformTeamRole } from "../lib/team-identities";
import type { FollowState } from "../lib/follows";
import { colors } from "../theme/colors";

export type PublicPerson = {
  user_id: string;
  display_name: string;
  username: string;
  avatar_url: string | null;
  bio: string | null;
  city: string | null;
  is_verified: boolean;
  follower_count: number;
  following_count: number;
  is_following: boolean;
  follow_state?: FollowState;
  team_role?: PlatformTeamRole | null;
};

type Props = {
  person: PublicPerson;
  currentUserId?: string;
  busy?: boolean;
  onToggleFollow: (person: PublicPerson) => void;
};

export function PeopleListItem({ person, currentUserId, busy = false, onToggleFollow }: Props) {
  const isOwnProfile = person.user_id === currentUserId;
  const followState = person.follow_state ?? (person.is_following ? "following" : "none");

  return (
    <View style={styles.card}>
      <Link href={{ pathname: "/user/[id]", params: { id: person.user_id } }} asChild>
        <Pressable style={styles.profileLink}>
          <View style={styles.avatar}>
            {person.avatar_url ? (
              <Image source={{ uri: person.avatar_url }} style={styles.avatarImage} />
            ) : (
              <Text style={styles.avatarInitial}>{person.display_name.slice(0, 1).toUpperCase()}</Text>
            )}
          </View>
          <View style={styles.copy}>
            <View style={styles.nameRow}>
              <Text style={styles.name} numberOfLines={1}>{person.display_name}</Text>
              <TeamIdentityBadge role={person.team_role} compact />
              {person.is_verified ? <Ionicons name="checkmark-circle" size={16} color={colors.success} /> : null}
            </View>
            <Text style={styles.username} numberOfLines={1}>
              @{person.username}{person.city ? ` · ${person.city}` : ""}
            </Text>
            <Text style={styles.meta}>{person.follower_count} takipçi · {person.following_count} takip</Text>
          </View>
        </Pressable>
      </Link>

      {!isOwnProfile ? (
        <Pressable
          disabled={busy}
          style={[styles.followButton, followState !== "none" && styles.followButtonActive, busy && styles.buttonDisabled]}
          onPress={() => onToggleFollow(person)}
        >
          <Text style={[styles.followButtonText, followState !== "none" && styles.followButtonTextActive]}>
            {busy ? "..." : followState === "following" ? "Takiptesin" : followState === "requested" ? "İstek gönderildi" : "Takip et"}
          </Text>
        </Pressable>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    padding: 11,
    borderRadius: 17,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface
  },
  profileLink: {
    flex: 1,
    minWidth: 0,
    flexDirection: "row",
    alignItems: "center",
    gap: 12
  },
  avatar: {
    width: 48,
    height: 48,
    borderRadius: 24,
    overflow: "hidden",
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.accentSoft,
    borderWidth: 2,
    borderColor: colors.accent
  },
  avatarImage: {
    width: "100%",
    height: "100%"
  },
  avatarInitial: {
    color: colors.accent,
    fontSize: 20,
    fontWeight: "900"
  },
  copy: {
    flex: 1,
    minWidth: 0,
    gap: 3
  },
  nameRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 5
  },
  name: {
    flexShrink: 1,
    color: colors.ink,
    fontSize: 15,
    fontWeight: "900"
  },
  username: {
    color: colors.muted,
    fontSize: 12,
    fontWeight: "700"
  },
  meta: {
    color: colors.accent,
    fontSize: 11,
    fontWeight: "800"
  },
  followButton: {
    minWidth: 76,
    minHeight: 40,
    alignItems: "center",
    paddingHorizontal: 12,
    paddingVertical: 10,
    borderRadius: 13,
    backgroundColor: colors.action,
    borderWidth: 1,
    borderColor: colors.action
  },
  followButtonActive: {
    backgroundColor: colors.surfaceStrong,
    borderColor: colors.accent
  },
  followButtonText: {
    color: colors.actionText,
    fontSize: 11,
    fontWeight: "900"
  },
  followButtonTextActive: {
    color: colors.accent
  },
  buttonDisabled: {
    opacity: 0.55
  }
});
