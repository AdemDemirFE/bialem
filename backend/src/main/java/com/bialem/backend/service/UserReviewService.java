package com.bialem.backend.service;

import com.bialem.backend.domain.UserReview;
import com.bialem.backend.repository.UserReviewRepository;
import com.bialem.backend.service.dto.UserReviewDTO;
import com.bialem.backend.service.mapper.UserReviewMapper;
import java.util.LinkedList;
import java.util.List;
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

    public UserReviewService(UserReviewRepository userReviewRepository, UserReviewMapper userReviewMapper) {
        this.userReviewRepository = userReviewRepository;
        this.userReviewMapper = userReviewMapper;
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
        return userReviewMapper.toDto(userReview);
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
