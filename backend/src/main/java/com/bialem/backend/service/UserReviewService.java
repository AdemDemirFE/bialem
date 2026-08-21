package com.bialem.backend.service;

import com.bialem.backend.domain.UserReview;
import com.bialem.backend.domain.enumeration.NotificationEventType;
import com.bialem.backend.notification.NotificationEvent;
import com.bialem.backend.notification.NotificationEventPublisher;
import com.bialem.backend.repository.UserReviewRepository;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserReviewDTO;
import com.bialem.backend.service.mapper.UserReviewMapper;
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
 * Service Implementation for managing {@link com.bialem.backend.domain.UserReview}.
 */
@Service
@Transactional
public class UserReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(UserReviewService.class);

    private final UserReviewRepository userReviewRepository;

    private final UserReviewMapper userReviewMapper;

    private final NotificationEventPublisher notificationEventPublisher;

    public UserReviewService(
        UserReviewRepository userReviewRepository,
        UserReviewMapper userReviewMapper,
        NotificationEventPublisher notificationEventPublisher
    ) {
        this.userReviewRepository = userReviewRepository;
        this.userReviewMapper = userReviewMapper;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    /**
     * Save a userReview.
     *
     * @param userReviewDTO the entity to save.
     * @return the persisted entity.
     */
    public UserReviewDTO save(UserReviewDTO userReviewDTO) {
        LOG.debug("Request to save UserReview : {}", userReviewDTO);
        UserReview userReview = userReviewMapper.toEntity(userReviewDTO);
        userReview = userReviewRepository.save(userReview);
        publishUserReviewEvent(userReviewDTO);
        return userReviewMapper.toDto(userReview);
    }

    private void publishUserReviewEvent(UserReviewDTO review) {
        if (review.getReviewedUser() == null || review.getReviewer() == null) {
            return;
        }
        Long recipientUserId = userIdOf(review.getReviewedUser());
        if (recipientUserId == null) {
            return;
        }
        String idempotencyKey = "USER_REVIEW:" + review.getId() + ":" + recipientUserId;
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("recipientUserId", recipientUserId);
        variables.put("reviewerId", userIdOf(review.getReviewer()));
        variables.put("reviewerName", displayNameOf(review.getReviewer()));
        variables.put("reviewedUserId", recipientUserId);
        variables.put("rating", review.getRating());
        variables.put("reviewText", review.getReviewText());
        variables.put("reviewId", review.getId());
        notificationEventPublisher.publish(new NotificationEvent(NotificationEventType.USER_REVIEW, idempotencyKey, variables));
    }

    private Long userIdOf(ProfileDTO profile) {
        return profile != null && profile.getUser() != null ? profile.getUser().getId() : null;
    }

    private String displayNameOf(ProfileDTO profile) {
        return profile != null && profile.getDisplayName() != null ? profile.getDisplayName() : "Bir kullanıcı";
    }

    /**
     * Update a userReview.
     *
     * @param userReviewDTO the entity to save.
     * @return the persisted entity.
     */
    public UserReviewDTO update(UserReviewDTO userReviewDTO) {
        LOG.debug("Request to update UserReview : {}", userReviewDTO);
        UserReview userReview = userReviewMapper.toEntity(userReviewDTO);
        userReview = userReviewRepository.save(userReview);
        return userReviewMapper.toDto(userReview);
    }

    /**
     * Partially update a userReview.
     *
     * @param userReviewDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserReviewDTO> partialUpdate(UserReviewDTO userReviewDTO) {
        LOG.debug("Request to partially update UserReview : {}", userReviewDTO);

        return userReviewRepository
            .findById(userReviewDTO.getId())
            .map(existingUserReview -> {
                userReviewMapper.partialUpdate(existingUserReview, userReviewDTO);

                return existingUserReview;
            })
            .map(userReviewRepository::save)
            .map(userReviewMapper::toDto);
    }

    /**
     * Get all the userReviews.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserReviewDTO> findAll() {
        LOG.debug("Request to get all UserReviews");
        return userReviewRepository.findAll().stream().map(userReviewMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one userReview by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserReviewDTO> findOne(Long id) {
        LOG.debug("Request to get UserReview : {}", id);
        return userReviewRepository.findById(id).map(userReviewMapper::toDto);
    }

    /**
     * Delete the userReview by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete UserReview : {}", id);
        userReviewRepository.deleteById(id);
    }
}
