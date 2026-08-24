package com.bialem.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.bialem.backend.domain.CommunityMember;
import com.bialem.backend.domain.CommunityModeratorAssistant;
import com.bialem.backend.domain.enumeration.CommunityMemberRole;
import com.bialem.backend.domain.enumeration.CommunityMemberStatus;
import com.bialem.backend.repository.CommunityMemberRepository;
import com.bialem.backend.repository.CommunityModeratorAssistantRepository;
import com.bialem.backend.security.AuthoritiesConstants;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class CommunityAuthorizationServiceTest {
    @Mock CommunityMemberRepository members;
    @Mock CommunityModeratorAssistantRepository assistants;

    @AfterEach void clearSecurityContext() { SecurityContextHolder.clearContext(); }

    @Test
    void adminCanManageAnyCommunityWithoutMembership() {
        authenticate(AuthoritiesConstants.ADMIN);
        assertThat(service().canManageCommunity(99L)).isTrue();
        verifyNoInteractions(members, assistants);
    }

    @Test
    void managerCanManageOnlyAssignedCommunity() {
        authenticate(AuthoritiesConstants.COMMUNITY_MANAGER);
        CommunityMember membership = mock(CommunityMember.class);
        when(membership.getRole()).thenReturn(CommunityMemberRole.MANAGER);
        when(members.findMembership(1L, "manager", CommunityMemberStatus.APPROVED)).thenReturn(Optional.of(membership));
        when(members.findMembership(2L, "manager", CommunityMemberStatus.APPROVED)).thenReturn(Optional.empty());

        assertThat(service().canManageCommunity(1L)).isTrue();
        assertThat(service().canManageCommunity(2L)).isFalse();
    }

    @Test
    void assistantPermissionsAreEnforcedIndividually() {
        authenticate(AuthoritiesConstants.USER);
        when(members.findMembership(1L, "manager", CommunityMemberStatus.APPROVED)).thenReturn(Optional.empty());
        CommunityModeratorAssistant assistant = mock(CommunityModeratorAssistant.class);
        when(assistant.getCanReviewEvents()).thenReturn(true);
        when(assistant.getCanManageParticipants()).thenReturn(false);
        when(assistants.findForUser(1L, "manager")).thenReturn(Optional.of(assistant));

        assertThat(service().canReviewEvents(1L)).isTrue();
        assertThat(service().canManageMembers(1L)).isFalse();
    }

    private CommunityAuthorizationService service() { return new CommunityAuthorizationService(members, assistants); }

    private static void authenticate(String... authorities) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("manager", "n/a", authorities);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
