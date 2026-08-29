package com.bialem.backend.service;

import com.bialem.backend.repository.ProfileRepository;
import com.bialem.backend.repository.UserRoleRepository;
import com.bialem.backend.security.AuthoritiesConstants;
import com.bialem.backend.security.SecurityUtils;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service("managementAuthorization")
public class ManagementAuthorizationService {

    private final ProfileRepository profileRepository;
    private final UserRoleRepository userRoleRepository;

    public ManagementAuthorizationService(ProfileRepository profileRepository, UserRoleRepository userRoleRepository) {
        this.profileRepository = profileRepository;
        this.userRoleRepository = userRoleRepository;
    }
    public enum Permission {
        MANAGEMENT_ACCESS, USER_VIEW, USER_CREATE, USER_EDIT, USER_ACTIVATE, USER_SUSPEND, USER_ROLE_MANAGE,
        COMMUNITY_VIEW, COMMUNITY_CREATE, COMMUNITY_EDIT, COMMUNITY_MEMBER_MANAGE, COMMUNITY_MODERATE,
        EVENT_VIEW, EVENT_CREATE, EVENT_EDIT, EVENT_APPROVE, EVENT_PARTICIPANT_MANAGE,
        NOTIFICATION_SEND, NOTIFICATION_VIEW, MODERATION_VIEW, MODERATION_MANAGE,
        ROLE_VIEW, ROLE_MANAGE, AUDIT_VIEW, SYSTEM_VIEW
    }

    private static final Set<Permission> ADMIN = Collections.unmodifiableSet(EnumSet.of(
        Permission.MANAGEMENT_ACCESS, Permission.USER_VIEW, Permission.USER_CREATE, Permission.USER_EDIT,
        Permission.USER_ACTIVATE, Permission.USER_SUSPEND, Permission.COMMUNITY_VIEW, Permission.COMMUNITY_CREATE,
        Permission.COMMUNITY_EDIT, Permission.COMMUNITY_MEMBER_MANAGE, Permission.COMMUNITY_MODERATE,
        Permission.EVENT_VIEW, Permission.EVENT_CREATE, Permission.EVENT_EDIT, Permission.EVENT_APPROVE,
        Permission.EVENT_PARTICIPANT_MANAGE, Permission.NOTIFICATION_SEND, Permission.NOTIFICATION_VIEW,
        Permission.MODERATION_VIEW, Permission.MODERATION_MANAGE, Permission.ROLE_VIEW
    ));

    public Set<Permission> currentPermissions() {
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SUPER_ADMIN)) return Collections.unmodifiableSet(EnumSet.allOf(Permission.class));
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN) || hasAppAdminRole()) return ADMIN;
        return Set.of();
    }

    public boolean has(Permission permission) { return currentPermissions().contains(permission); }
    public boolean canAccessManagement() { return has(Permission.MANAGEMENT_ACCESS); }

    private boolean hasAppAdminRole() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(profileRepository::findOneByUser_Login)
            .map(profile ->
                userRoleRepository
                    .findByUser(profile)
                    .stream()
                    .anyMatch(userRole -> userRole.getRole() != null && "admin".equalsIgnoreCase(userRole.getRole().getCode()))
            )
            .orElse(false);
    }
}
