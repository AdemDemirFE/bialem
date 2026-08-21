package com.bialem.backend.service;

import com.bialem.backend.domain.Follow;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.enumeration.NotificationEventType;
import com.bialem.backend.notification.NotificationEvent;
import com.bialem.backend.notification.NotificationEventPublisher;
import com.bialem.backend.repository.FollowRepository;
import com.bialem.backend.service.dto.FollowDTO;
import com.bialem.backend.service.mapper.FollowMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.Follow}.
 */
@Service
@Transactional
public class FollowService {

    private static final Logger LOG = LoggerFactory.getLogger(FollowService.class);

    private final FollowRepository followRepository;

    private final FollowMapper followMapper;

    private final NotificationEventPublisher notificationEventPublisher;

    public FollowService(
        FollowRepository followRepository,
        FollowMapper followMapper,
        NotificationEventPublisher notificationEventPublisher
    ) {
        this.followRepository = followRepository;
        this.followMapper = followMapper;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    /**
     * Save a follow.
     *
     * @param followDTO the entity to save.
     * @return the persisted entity.
     */
    public FollowDTO save(FollowDTO followDTO) {
        LOG.debug("Request to save Follow : {}", followDTO);
        Follow follow = followMapper.toEntity(followDTO);
        follow = followRepository.save(follow);
        publishNewFollowerEvent(follow);
        return followMapper.toDto(follow);
    }

    private void publishNewFollowerEvent(Follow follow) {
        if (follow.getFollowed() == null || follow.getFollower() == null) {
            return;
        }
        Long recipientUserId = resolveUserId(follow.getFollowed());
        if (recipientUserId == null) {
            return;
        }
        String idempotencyKey = "NEW_FOLLOWER:" + follow.getId() + ":" + recipientUserId;
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("recipientUserId", recipientUserId);
        variables.put("actorUserId", resolveUserId(follow.getFollower()));
        variables.put("actorName", displayNameOf(follow.getFollower()));
        variables.put("followedUserId", recipientUserId);
        variables.put("followId", follow.getId());
        notificationEventPublisher.publish(new NotificationEvent(NotificationEventType.NEW_FOLLOWER, idempotencyKey, variables));
    }

    private Long resolveUserId(Profile profile) {
        return profile != null && profile.getUser() != null ? profile.getUser().getId() : null;
    }

    private String displayNameOf(Profile profile) {
        return profile != null ? profile.getDisplayName() : "Bir kullanıcı";
    }

    /**
     * Update a follow.
     *
     * @param followDTO the entity to save.
     * @return the persisted entity.
     */
    public FollowDTO update(FollowDTO followDTO) {
        LOG.debug("Request to update Follow : {}", followDTO);
        Follow follow = followMapper.toEntity(followDTO);
        follow = followRepository.save(follow);
        return followMapper.toDto(follow);
    }

    /**
     * Partially update a follow.
     *
     * @param followDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FollowDTO> partialUpdate(FollowDTO followDTO) {
        LOG.debug("Request to partially update Follow : {}", followDTO);

        return followRepository
            .findById(followDTO.getId())
            .map(existingFollow -> {
                followMapper.partialUpdate(existingFollow, followDTO);

                return existingFollow;
            })
            .map(followRepository::save)
            .map(followMapper::toDto);
    }

    /**
     * Get all the follows.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FollowDTO> findAll() {
        LOG.debug("Request to get all Follows");
        return followRepository.findAll().stream().map(followMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one follow by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FollowDTO> findOne(Long id) {
        LOG.debug("Request to get Follow : {}", id);
        return followRepository.findById(id).map(followMapper::toDto);
    }

    /**
     * Delete the follow by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Follow : {}", id);
        followRepository.deleteById(id);
    }
}
