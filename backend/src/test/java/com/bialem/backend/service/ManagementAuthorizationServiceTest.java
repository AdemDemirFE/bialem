package com.bialem.backend.service;

import static com.bialem.backend.service.ManagementAuthorizationService.Permission.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.security.AuthoritiesConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class ManagementAuthorizationServiceTest {
    private final ManagementAuthorizationService service = new ManagementAuthorizationService();
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }

    @Test void normalUserHasNoManagementAccess() {
        authenticate(AuthoritiesConstants.USER);
        assertThat(service.canAccessManagement()).isFalse();
        assertThat(service.currentPermissions()).isEmpty();
    }

    @Test void adminCanOperatePlatformButCannotManageSuperAdminRoles() {
        authenticate(AuthoritiesConstants.ADMIN);
        assertThat(service.currentPermissions()).contains(MANAGEMENT_ACCESS, USER_ACTIVATE, COMMUNITY_EDIT, EVENT_APPROVE, NOTIFICATION_SEND);
        assertThat(service.currentPermissions()).doesNotContain(ROLE_MANAGE, AUDIT_VIEW, SYSTEM_VIEW);
    }

    @Test void superAdminHasEveryManagementPermission() {
        authenticate(AuthoritiesConstants.SUPER_ADMIN);
        assertThat(service.currentPermissions()).containsExactlyInAnyOrder(values());
    }

    private static void authenticate(String authority) {
        var authentication = new TestingAuthenticationToken("user", "n/a", authority);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
