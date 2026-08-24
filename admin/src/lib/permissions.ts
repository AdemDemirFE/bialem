export type AdminPermissions = {
  manageUsers: boolean;
  manageCommunities: boolean;
  manageEvents: boolean;
  moderateContent: boolean;
  manageRoles: boolean;
  manageSystem: boolean;
};

export function hasPlatformAdminAccess(authorities: string[] | undefined) {
  return Boolean(authorities?.some((authority) => authority === "ROLE_ADMIN" || authority === "ROLE_SUPER_ADMIN"));
}
