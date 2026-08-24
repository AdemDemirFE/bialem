export type AccountPermissions = {
  accessManagement: boolean;
  manageUsers: boolean;
  manageCommunities: boolean;
  manageEvents: boolean;
  moderateContent: boolean;
  manageRoles: boolean;
  manageSystem: boolean;
};

export const noPermissions: AccountPermissions = {
  accessManagement: false,
  manageUsers: false,
  manageCommunities: false,
  manageEvents: false,
  moderateContent: false,
  manageRoles: false,
  manageSystem: false
};

export function canSeeManagement(permissions: AccountPermissions) {
  return permissions.accessManagement;
}

export function primaryAuthority(authorities: string[] | undefined) {
  return authorities?.[0] ?? "ROLE_USER";
}

export function roleLabel(authorities: string[] | undefined) {
  const labels: Record<string, string> = {
    ROLE_USER: "Kullanıcı",
    ROLE_COMMUNITY_MANAGER: "Topluluk Yöneticisi",
    ROLE_EVENT_MANAGER: "Etkinlik Yöneticisi",
    ROLE_MODERATOR: "Moderatör",
    ROLE_ADMIN: "Admin",
    ROLE_SUPER_ADMIN: "Super Admin"
  };
  const role = primaryAuthority(authorities);
  return labels[role] ?? role;
}
