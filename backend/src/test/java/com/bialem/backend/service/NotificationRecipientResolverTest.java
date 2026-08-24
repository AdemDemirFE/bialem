package com.bialem.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bialem.backend.domain.User;
import com.bialem.backend.domain.enumeration.NotificationTargetType;
import com.bialem.backend.repository.CommunityMemberRepository;
import com.bialem.backend.repository.EventParticipantRepository;
import com.bialem.backend.repository.UserRepository;
import com.bialem.backend.service.dto.AdminNotificationSendRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class NotificationRecipientResolverTest {

    @Mock UserRepository users;
    @Mock CommunityMemberRepository communityMembers;
    @Mock EventParticipantRepository eventParticipants;
    private NotificationRecipientResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new NotificationRecipientResolver(users, communityMembers, eventParticipants);
    }

    @Test
    void allActiveUsersDoesNotRequireProfileOrSpecificUserLookup() {
        User user = activeUser(41L);
        when(users.findAllByActivatedIsTrue()).thenReturn(List.of(user));

        assertThat(resolver.resolve(request(NotificationTargetType.ALL_ACTIVE_USERS, null))).containsExactly(user);
        verify(users, never()).findById(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void specificUserUsesJhiUserIdAndDoesNotResolveProfileId() {
        User user = activeUser(3051L);
        when(users.findById(3051L)).thenReturn(Optional.of(user));

        assertThat(resolver.resolve(request(NotificationTargetType.SPECIFIC_USER, 3051L))).containsExactly(user);
    }

    @Test
    void specificUserIsTheOnlyTargetThatReturnsNotFoundForMissingUser() {
        when(users.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> resolver.resolve(request(NotificationTargetType.SPECIFIC_USER, 999L)))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("404 NOT_FOUND");
    }

    private static User activeUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setActivated(true);
        return user;
    }

    private static AdminNotificationSendRequest request(NotificationTargetType type, Long userId) {
        return new AdminNotificationSendRequest(
            "Başlık", "Mesaj", type, userId, null, null, null, null, "SYSTEM", "/", null, null,
            true, true, null, null, "test-request"
        );
    }
}
