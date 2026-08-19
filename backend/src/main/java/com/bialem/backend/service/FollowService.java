package com.bialem.backend.service;

import com.bialem.backend.domain.Follow;
import com.bialem.backend.repository.FollowRepository;
import com.bialem.backend.service.dto.FollowDTO;
import com.bialem.backend.service.mapper.FollowMapper;
import java.util.LinkedList;
import java.util.List;
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

    public FollowService(FollowRepository followRepository, FollowMapper followMapper) {
        this.followRepository = followRepository;
        this.followMapper = followMapper;
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
        return followMapper.toDto(follow);
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
