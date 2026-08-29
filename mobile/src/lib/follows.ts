import { api } from "./api";

export type FollowState = "none" | "requested" | "following";

const followErrorMessage = (message: string) => {
  if (message.includes("Users cannot follow themselves")) {
    return "Kendi profilini takip edemezsin.";
  }

  if (message.includes("Active profile not found")) {
    return "Bu profil şu anda takip edilemiyor.";
  }

  if (message.includes("Following is not available")) {
    return "Bu kullanıcıyla takip bağlantısı kurulamıyor.";
  }

  if (message.includes("Authentication required")) {
    return "Takip etmek için yeniden giriş yapmalısın.";
  }

  return "Takip işlemi tamamlanamadı. Lütfen tekrar dene.";
};

export const setProfileFollow = async (targetUserId: string, shouldFollow: boolean) => {
  const { data, error } = await api.rpc("set_profile_follow_state", {
    target_user_id: targetUserId,
    target_should_follow: shouldFollow
  });

  if (error) {
    return {
      isFollowing: null,
      state: null,
      error: followErrorMessage(error.message)
    };
  }

  const state = (data === "following" || data === "requested" ? data : "none") as FollowState;

  return {
    isFollowing: state === "following",
    state,
    error: null
  };
};

export const getProfileFollowState = async (targetUserId: string): Promise<FollowState> => {
  const { data, error } = await api.rpc("get_my_follow_relation", {
    target_user_id: targetUserId
  });

  if (error) return "none";
  return data === "following" || data === "requested" ? data : "none";
};

export const getRequestedProfileIds = async (targetUserIds: string[]) => {
  if (!targetUserIds.length) return new Set<string>();

  const { data, error } = await api
    .from("follow_requests")
    .select("target_user_id")
    .in("target_user_id", targetUserIds);

  if (error) return new Set<string>();
  return new Set(((data ?? []) as Array<{ target_user_id: string }>).map((item) => item.target_user_id));
};
