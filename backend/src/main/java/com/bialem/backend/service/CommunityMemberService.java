package com.bialem.backend.service;

import com.bialem.backend.domain.CommunityMember;
import com.bialem.backend.domain.enumeration.CommunityMemberStatus;
import com.bialem.backend.domain.enumeration.CommunityMemberRole;
import com.bialem.backend.domain.enumeration.NotificationEventType;
import com.bialem.backend.notification.NotificationEvent;
import com.bialem.backend.notification.NotificationEventPublisher;
import com.bialem.backend.repository.CommunityMemberRepository;
import com.bialem.backend.service.dto.CommunityMemberDTO;
import com.bialem.backend.service.mapper.CommunityMemberMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.CommunityMember}.
 */
@Service
@Transactional
public class CommunityMemberService {

    private static final Logger LOG = LoggerFactory.getLogger(CommunityMemberService.class);

    private final CommunityMemberRepository communityMemberRepository;

    private final CommunityMemberMapper communityMemberMapper;

    private final NotificationEventPublisher notificationEventPublisher;

    public CommunityMemberService(
        CommunityMemberRepository communityMemberRepository,
        CommunityMemberMapper communityMemberMapper,
        NotificationEventPublisher notificationEventPublisher
    ) {
        this.communityMemberRepository = communityMemberRepository;
        this.communityMemberMapper = communityMemberMapper;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    /**
     * Save a communityMember.
     *
     * @param communityMemberDTO the entity to save.
     * @return the persisted entity.
     */
    public CommunityMemberDTO save(CommunityMemberDTO communityMemberDTO) {
        LOG.debug("Request to save CommunityMember : {}", communityMemberDTO);
        CommunityMember communityMember = communityMemberMapper.toEntity(communityMemberDTO);
        communityMember = communityMemberRepository.save(communityMember);
        if (communityMember.getStatus() == CommunityMemberStatus.PENDING) {
            publishMembershipRequestEvent(communityMember);
        }
        return communityMemberMapper.toDto(communityMember);
    }

    /**
     * Update a communityMember.
     *
     * @param communityMemberDTO the entity to save.
     * @return the persisted entity.
     */
    public CommunityMemberDTO update(CommunityMemberDTO communityMemberDTO) {
        LOG.debug("Request to update CommunityMember : {}", communityMemberDTO);
        CommunityMember communityMember = communityMemberMapper.toEntity(communityMemberDTO);
        communityMember = communityMemberRepository.save(communityMember);
        publishMembershipStatusEvent(communityMember);
        return communityMemberMapper.toDto(communityMember);
    }

    private void publishMembershipRequestEvent(CommunityMember member) {
        if (member.getCommunity() == null || member.getUser() == null) {
            return;
        }
        Long applicantId = member.getUser().getUser() != null ? member.getUser().getUser().getId() : null;
        if (applicantId == null) {
            return;
        }
        String idempotencyKey = "COMMUNITY_MEMBERSHIP_REQUEST:" + member.getId() + ":" + member.getCommunity().getId();
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("applicantId", applicantId);
        variables.put("applicantName", member.getUser().getDisplayName());
        variables.put("communityId", member.getCommunity().getId());
        variables.put("communityName", member.getCommunity().getName());
        variables.put("membershipId", member.getId());
        notificationEventPublisher.publish(new NotificationEvent(NotificationEventType.COMMUNITY_MEMBERSHIP_REQUEST, idempotencyKey, variables));
    }

    private void publishMembershipStatusEvent(CommunityMember member) {
        if (member.getCommunity() == null || member.getUser() == null) {
            return;
        }
        Long applicantId = member.getUser().getUser() != null ? member.getUser().getUser().getId() : null;
        if (applicantId == null) {
            return;
        }
        NotificationEventType type;
        if (member.getStatus() == CommunityMemberStatus.APPROVED) {
            type = NotificationEventType.COMMUNITY_MEMBERSHIP_APPROVED;
        } else if (member.getStatus() == CommunityMemberStatus.REJECTED) {
            type = NotificationEventType.COMMUNITY_MEMBERSHIP_REJECTED;
        } else {
            return;
        }
        String idempotencyKey = type.name() + ":" + member.getId() + ":" + applicantId;
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("applicantId", applicantId);
        variables.put("communityId", member.getCommunity().getId());
        variables.put("communityName", member.getCommunity().getName());
        variables.put("membershipId", member.getId());
        variables.put("recipientUserId", applicantId);
        notificationEventPublisher.publish(new NotificationEvent(type, idempotencyKey, variables));
    }
    /**
     * Partially update a communityMember.
     *
     * @param communityMemberDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CommunityMemberDTO> partialUpdate(CommunityMemberDTO communityMemberDTO) {
        LOG.debug("Request to partially update CommunityMember : {}", communityMemberDTO);

        return communityMemberRepository
            .findById(communityMemberDTO.getId())
            .map(existingCommunityMember -> {
                communityMemberMapper.partialUpdate(existingCommunityMember, communityMemberDTO);

                return existingCommunityMember;
            })
            .map(communityMemberRepository::save)
            .map(communityMemberMapper::toDto);
    }

    /**
     * Get all the communityMembers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CommunityMemberDTO> findAll() {
        LOG.debug("Request to get all CommunityMembers");
        return communityMemberRepository
            .findAll()
            .stream()
            .map(communityMemberMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one communityMember by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CommunityMemberDTO> findOne(Long id) {
        LOG.debug("Request to get CommunityMember : {}", id);
        return communityMemberRepository.findById(id).map(communityMemberMapper::toDto);
    }

    /**
     * Delete the communityMember by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CommunityMember : {}", id);
        communityMemberRepository.deleteById(id);
    }

    public CommunityMember review(Long communityId, Long memberId, CommunityMemberStatus targetStatus) {
        if (targetStatus != CommunityMemberStatus.APPROVED && targetStatus != CommunityMemberStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz üyelik durumu.");
        }
        CommunityMember member = communityMemberRepository.findForManagementUpdate(communityId, memberId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Katılım isteği bulunamadı."));
        if (member.getStatus() != CommunityMemberStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Katılım isteği daha önce işlenmiş.");
        }
        member.setStatus(targetStatus);
        if (targetStatus == CommunityMemberStatus.APPROVED) member.setRole(CommunityMemberRole.MEMBER);
        CommunityMember saved = communityMemberRepository.save(member);
        publishMembershipStatusEvent(saved);
        return saved;
    }
}
