package com.bialem.backend.service;

import com.bialem.backend.domain.User;
import com.bialem.backend.domain.enumeration.CommunityMemberStatus;
import com.bialem.backend.domain.enumeration.EventParticipantStatus;
import com.bialem.backend.repository.CommunityMemberRepository;
import com.bialem.backend.repository.EventParticipantRepository;
import com.bialem.backend.repository.UserRepository;
import com.bialem.backend.service.dto.AdminNotificationSendRequest;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class NotificationRecipientResolver {

    private final UserRepository users;
    private final CommunityMemberRepository communityMembers;
    private final EventParticipantRepository eventParticipants;

    public NotificationRecipientResolver(
        UserRepository users,
        CommunityMemberRepository communityMembers,
        EventParticipantRepository eventParticipants
    ) {
        this.users = users;
        this.communityMembers = communityMembers;
        this.eventParticipants = eventParticipants;
    }

    public List<User> resolve(AdminNotificationSendRequest request) {
        List<User> recipients = switch (request.targetType()) {
            case ALL_ACTIVE_USERS -> users.findAllByActivatedIsTrue();
            case SPECIFIC_USER -> List.of(resolveSpecificUser(required(request.userId(), "userId")));
            case ROLE -> users.findActiveByAuthority(requiredText(request.role(), "role"));
            case COMMUNITY -> communityMembers.findRecipientUsers(
                required(request.communityId(), "communityId"),
                CommunityMemberStatus.APPROVED
            );
            case EVENT_PARTICIPANTS -> eventParticipants.findRecipientUsers(
                required(request.eventId(), "eventId"),
                List.of(EventParticipantStatus.APPROVED, EventParticipantStatus.CHECKED_IN)
            );
            case CITY -> users.findActiveByProfileCity(requiredText(request.city(), "city"));
        };
        LinkedHashMap<Long, User> unique = new LinkedHashMap<>();
        recipients.stream().filter(User::isActivated).forEach(user -> unique.put(user.getId(), user));
        return List.copyOf(unique.values());
    }

    private User resolveSpecificUser(Long userId) {
        return users
            .findById(userId)
            .filter(User::isActivated)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aktif kullanıcı bulunamadı"));
    }

    private static Long required(Long value, String field) {
        if (value == null || value <= 0) throw badRequest(field + " bu hedef tipi için zorunludur");
        return value;
    }

    private static String requiredText(String value, String field) {
        if (value == null || value.isBlank()) throw badRequest(field + " bu hedef tipi için zorunludur");
        return value.trim();
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
