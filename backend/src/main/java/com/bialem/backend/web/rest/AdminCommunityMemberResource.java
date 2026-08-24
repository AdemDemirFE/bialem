package com.bialem.backend.web.rest;

import com.bialem.backend.domain.CommunityMember;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.enumeration.CommunityMemberStatus;
import com.bialem.backend.repository.CommunityMemberRepository;
import com.bialem.backend.service.CommunityMemberService;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/communities/{communityId}/members")
@PreAuthorize("@communityAuthorization.canManageMembers(#communityId)")
public class AdminCommunityMemberResource {
    private final CommunityMemberRepository members;
    private final CommunityMemberService memberService;

    public AdminCommunityMemberResource(CommunityMemberRepository members, CommunityMemberService memberService) {
        this.members = members;
        this.memberService = memberService;
    }

    @GetMapping
    public Page<MemberView> members(
        @PathVariable Long communityId,
        @RequestParam(defaultValue = "APPROVED") CommunityMemberStatus status,
        @RequestParam(defaultValue = "") String search,
        @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable
    ) {
        return members.findManagementMembers(communityId, status, search.trim(), pageable).map(MemberView::from);
    }

    @GetMapping("/stats")
    public MemberStats stats(@PathVariable Long communityId) {
        return new MemberStats(
            members.countByCommunityIdAndStatus(communityId, CommunityMemberStatus.PENDING),
            members.countByCommunityIdAndStatus(communityId, CommunityMemberStatus.APPROVED),
            members.countByCommunityIdAndStatus(communityId, CommunityMemberStatus.BLOCKED)
        );
    }

    @PostMapping("/{memberId}/approve")
    public MemberView approve(@PathVariable Long communityId, @PathVariable Long memberId) {
        return MemberView.from(memberService.review(communityId, memberId, CommunityMemberStatus.APPROVED));
    }

    @PostMapping("/{memberId}/reject")
    public MemberView reject(@PathVariable Long communityId, @PathVariable Long memberId) {
        return MemberView.from(memberService.review(communityId, memberId, CommunityMemberStatus.REJECTED));
    }

    public record MemberStats(long pending, long approved, long blocked) {}
    public record MemberView(Long id, Long profileId, Long userId, String displayName, String username, String login,
                             String avatarUrl, String city, String bio, String role, String status, Instant createdAt) {
        static MemberView from(CommunityMember member) {
            Profile profile = member.getUser();
            return new MemberView(member.getId(), profile.getId(), profile.getUser().getId(), profile.getDisplayName(),
                profile.getUsername(), profile.getUser().getLogin(), profile.getAvatarUrl(), profile.getCity(), profile.getBio(),
                member.getRole().name(), member.getStatus().name(), member.getCreatedAt());
        }
    }
}
