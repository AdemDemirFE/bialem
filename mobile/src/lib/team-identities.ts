import { api } from "./api";

type StoredPlatformTeamRole = "founder" | "team" | "support" | "editor";
export type PlatformTeamRole = StoredPlatformTeamRole | "community_moderator" | "external_community_manager";

export const PLATFORM_TEAM_TITLES: Record<PlatformTeamRole, string> = {
  founder: "Bialem Kurucusu",
  team: "Bialem Ekibi",
  support: "Bialem Destek Ekibi",
  editor: "Bialem İçerik Editörü",
  community_moderator: "Bialem Topluluk Moderatörü",
  external_community_manager: "Bağımsız Topluluk Yöneticisi"
};

export async function getPlatformTeamIdentity(userId: string) {
  const identityMap = await getPlatformTeamIdentityMap([userId]);
  return {
    role: identityMap.get(userId) ?? null,
    error: null
  };
}

export async function getPlatformTeamIdentityMap(userIds: string[]) {
  const uniqueIds = [...new Set(userIds.filter(Boolean))];
  const identityMap = new Map<string, PlatformTeamRole>();
  if (uniqueIds.length === 0) return identityMap;

  const [teamResult, communitiesResult] = await Promise.all([
    api
      .from("platform_team_members")
      .select("user_id, role_code")
      .in("user_id", uniqueIds),
    api
      .from("communities")
      .select("lead_moderator_id, community_type")
      .in("lead_moderator_id", uniqueIds)
      .is("parent_id", null)
  ]);

  for (const identity of (teamResult.data ?? []) as { user_id: string; role_code: StoredPlatformTeamRole }[]) {
    identityMap.set(identity.user_id, identity.role_code);
  }

  const moderatedCommunities = (communitiesResult.data ?? []) as {
    lead_moderator_id: string;
    community_type: "category_hub" | "partner_hub";
  }[];

  // Platform roles have priority. A Bialem moderator takes priority over an external manager.
  for (const community of moderatedCommunities) {
    if (
      community.community_type === "category_hub"
      && !identityMap.has(community.lead_moderator_id)
    ) {
      identityMap.set(community.lead_moderator_id, "community_moderator");
    }
  }
  for (const community of moderatedCommunities) {
    if (
      community.community_type === "partner_hub"
      && !identityMap.has(community.lead_moderator_id)
    ) {
      identityMap.set(community.lead_moderator_id, "external_community_manager");
    }
  }

  return identityMap;
}
