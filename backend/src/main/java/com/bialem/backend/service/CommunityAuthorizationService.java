package com.bialem.backend.service;

import com.bialem.backend.domain.CommunityMember;
import com.bialem.backend.domain.CommunityModeratorAssistant;
import com.bialem.backend.domain.enumeration.CommunityMemberRole;
import com.bialem.backend.domain.enumeration.CommunityMemberStatus;
import com.bialem.backend.repository.CommunityMemberRepository;
import com.bialem.backend.repository.CommunityModeratorAssistantRepository;
import com.bialem.backend.security.AuthoritiesConstants;
import com.bialem.backend.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service("communityAuthorization")
@Transactional(readOnly = true)
public class CommunityAuthorizationService {

    private final CommunityMemberRepository members;
    private final CommunityModeratorAssistantRepository assistants;

    public CommunityAuthorizationService(CommunityMemberRepository members, CommunityModeratorAssistantRepository assistants) {
        this.members = members;
        this.assistants = assistants;
    }

    public boolean isSuperAdmin() {
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SUPER_ADMIN);
    }

    public boolean isAdmin() {
        return SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPER_ADMIN);
    }

    public boolean canManageCommunity(Long communityId) {
        if (isAdmin()) return true;
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (login == null || communityId == null) return false;
        return members.findMembership(communityId, login, CommunityMemberStatus.APPROVED)
            .map(CommunityMember::getRole)
            .filter(role -> role == CommunityMemberRole.MANAGER || role == CommunityMemberRole.OWNER)
            .isPresent();
    }

    public boolean canManageMembers(Long communityId) {
        if (canManageCommunity(communityId)) return true;
        return assistant(communityId).map(CommunityModeratorAssistant::getCanManageParticipants).orElse(false);
    }

    public boolean canReviewEvents(Long communityId) {
        if (canManageCommunity(communityId)) return true;
        return assistant(communityId).map(CommunityModeratorAssistant::getCanReviewEvents).orElse(false);
    }

    public boolean canManageGroups(Long communityId) {
        if (canManageCommunity(communityId)) return true;
        return assistant(communityId).map(CommunityModeratorAssistant::getCanManageGroups).orElse(false);
    }

    public boolean isOwner(Long communityId) {
        if (isSuperAdmin()) return true;
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        return login != null && members.findMembership(communityId, login, CommunityMemberStatus.APPROVED)
            .map(CommunityMember::getRole).filter(CommunityMemberRole.OWNER::equals).isPresent();
    }

    public void requireManageCommunity(Long communityId) { require(canManageCommunity(communityId)); }
    public void requireManageMembers(Long communityId) { require(canManageMembers(communityId)); }
    public void requireReviewEvents(Long communityId) { require(canReviewEvents(communityId)); }
    public void requireOwner(Long communityId) { require(isOwner(communityId)); }

    private java.util.Optional<CommunityModeratorAssistant> assistant(Long communityId) {
        return SecurityUtils.getCurrentUserLogin().flatMap(login -> assistants.findForUser(communityId, login));
    }

    private static void require(boolean allowed) {
        if (!allowed) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu işlem için yetkiniz bulunmuyor.");
    }
}
