package com.bialem.backend.service;

import com.bialem.backend.domain.UserHonorBadge;
import com.bialem.backend.repository.UserHonorBadgeRepository;
import com.bialem.backend.service.dto.UserHonorBadgeDTO;
import com.bialem.backend.service.mapper.UserHonorBadgeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.UserHonorBadge}.
 */
@Service
@Transactional
public class UserHonorBadgeService {

    private static final Logger LOG = LoggerFactory.getLogger(UserHonorBadgeService.class);

    private final UserHonorBadgeRepository userHonorBadgeRepository;

    private final UserHonorBadgeMapper userHonorBadgeMapper;

    public UserHonorBadgeService(UserHonorBadgeRepository userHonorBadgeRepository, UserHonorBadgeMapper userHonorBadgeMapper) {
        this.userHonorBadgeRepository = userHonorBadgeRepository;
        this.userHonorBadgeMapper = userHonorBadgeMapper;
    }

    /**
     * Save a userHonorBadge.
     *
     * @param userHonorBadgeDTO the entity to save.
     * @return the persisted entity.
     */
    public UserHonorBadgeDTO save(UserHonorBadgeDTO userHonorBadgeDTO) {
        LOG.debug("Request to save UserHonorBadge : {}", userHonorBadgeDTO);
        UserHonorBadge userHonorBadge = userHonorBadgeMapper.toEntity(userHonorBadgeDTO);
        userHonorBadge = userHonorBadgeRepository.save(userHonorBadge);
        return userHonorBadgeMapper.toDto(userHonorBadge);
    }

    /**
     * Update a userHonorBadge.
     *
     * @param userHonorBadgeDTO the entity to save.
     * @return the persisted entity.
     */
    public UserHonorBadgeDTO update(UserHonorBadgeDTO userHonorBadgeDTO) {
        LOG.debug("Request to update UserHonorBadge : {}", userHonorBadgeDTO);
        UserHonorBadge userHonorBadge = userHonorBadgeMapper.toEntity(userHonorBadgeDTO);
        userHonorBadge = userHonorBadgeRepository.save(userHonorBadge);
        return userHonorBadgeMapper.toDto(userHonorBadge);
    }

    /**
     * Partially update a userHonorBadge.
     *
     * @param userHonorBadgeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserHonorBadgeDTO> partialUpdate(UserHonorBadgeDTO userHonorBadgeDTO) {
        LOG.debug("Request to partially update UserHonorBadge : {}", userHonorBadgeDTO);

        return userHonorBadgeRepository
            .findById(userHonorBadgeDTO.getId())
            .map(existingUserHonorBadge -> {
                userHonorBadgeMapper.partialUpdate(existingUserHonorBadge, userHonorBadgeDTO);

                return existingUserHonorBadge;
            })
            .map(userHonorBadgeRepository::save)
            .map(userHonorBadgeMapper::toDto);
    }

    /**
     * Get all the userHonorBadges.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserHonorBadgeDTO> findAll() {
        LOG.debug("Request to get all UserHonorBadges");
        return userHonorBadgeRepository
            .findAll()
            .stream()
            .map(userHonorBadgeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one userHonorBadge by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserHonorBadgeDTO> findOne(Long id) {
        LOG.debug("Request to get UserHonorBadge : {}", id);
        return userHonorBadgeRepository.findById(id).map(userHonorBadgeMapper::toDto);
    }

    /**
     * Delete the userHonorBadge by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete UserHonorBadge : {}", id);
        userHonorBadgeRepository.deleteById(id);
    }
}
