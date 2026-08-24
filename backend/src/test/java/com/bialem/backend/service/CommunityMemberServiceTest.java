package com.bialem.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.bialem.backend.domain.CommunityMember;
import com.bialem.backend.domain.enumeration.CommunityMemberRole;
import com.bialem.backend.domain.enumeration.CommunityMemberStatus;
import com.bialem.backend.notification.NotificationEventPublisher;
import com.bialem.backend.repository.CommunityMemberRepository;
import com.bialem.backend.service.mapper.CommunityMemberMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CommunityMemberServiceTest {
    @Mock CommunityMemberRepository repository;
    @Mock CommunityMemberMapper mapper;
    @Mock NotificationEventPublisher publisher;
    private CommunityMemberService service;

    @BeforeEach
    void setUp() {
        service = new CommunityMemberService(repository, mapper, publisher);
    }

    @Test
    void approveMovesPendingMemberToApprovedWithMemberRole() {
        CommunityMember member = new CommunityMember().id(7L).status(CommunityMemberStatus.PENDING).role(CommunityMemberRole.MANAGER);
        when(repository.findForManagementUpdate(3L, 7L)).thenReturn(Optional.of(member));
        when(repository.save(member)).thenReturn(member);

        CommunityMember result = service.review(3L, 7L, CommunityMemberStatus.APPROVED);

        assertThat(result.getStatus()).isEqualTo(CommunityMemberStatus.APPROVED);
        assertThat(result.getRole()).isEqualTo(CommunityMemberRole.MEMBER);
    }

    @Test
    void alreadyReviewedRequestCannotBeReviewedAgain() {
        CommunityMember member = new CommunityMember().id(7L).status(CommunityMemberStatus.APPROVED).role(CommunityMemberRole.MEMBER);
        when(repository.findForManagementUpdate(3L, 7L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> service.review(3L, 7L, CommunityMemberStatus.REJECTED))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("409 CONFLICT");
    }
}
