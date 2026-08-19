package com.bialem.backend.service;

import com.bialem.backend.domain.FollowRequest;
import com.bialem.backend.repository.FollowRequestRepository;
import com.bialem.backend.service.dto.FollowRequestDTO;
import com.bialem.backend.service.mapper.FollowRequestMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.FollowRequest}.
 */
@Service
@Transactional
public class FollowRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(FollowRequestService.class);

    private final FollowRequestRepository followRequestRepository;

    private final FollowRequestMapper followRequestMapper;

    public FollowRequestService(FollowRequestRepository followRequestRepository, FollowRequestMapper followRequestMapper) {
        this.followRequestRepository = followRequestRepository;
        this.followRequestMapper = followRequestMapper;
    }

    /**
     * Save a followRequest.
     *
     * @param followRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public FollowRequestDTO save(FollowRequestDTO followRequestDTO) {
        LOG.debug("Request to save FollowRequest : {}", followRequestDTO);
        FollowRequest followRequest = followRequestMapper.toEntity(followRequestDTO);
        followRequest = followRequestRepository.save(followRequest);
        return followRequestMapper.toDto(followRequest);
    }

    /**
     * Update a followRequest.
     *
     * @param followRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public FollowRequestDTO update(FollowRequestDTO followRequestDTO) {
        LOG.debug("Request to update FollowRequest : {}", followRequestDTO);
        FollowRequest followRequest = followRequestMapper.toEntity(followRequestDTO);
        followRequest = followRequestRepository.save(followRequest);
        return followRequestMapper.toDto(followRequest);
    }

    /**
     * Partially update a followRequest.
     *
     * @param followRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FollowRequestDTO> partialUpdate(FollowRequestDTO followRequestDTO) {
        LOG.debug("Request to partially update FollowRequest : {}", followRequestDTO);

        return followRequestRepository
            .findById(followRequestDTO.getId())
            .map(existingFollowRequest -> {
                followRequestMapper.partialUpdate(existingFollowRequest, followRequestDTO);

                return existingFollowRequest;
            })
            .map(followRequestRepository::save)
            .map(followRequestMapper::toDto);
    }

    /**
     * Get all the followRequests.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FollowRequestDTO> findAll() {
        LOG.debug("Request to get all FollowRequests");
        return followRequestRepository.findAll().stream().map(followRequestMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one followRequest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FollowRequestDTO> findOne(Long id) {
        LOG.debug("Request to get FollowRequest : {}", id);
        return followRequestRepository.findById(id).map(followRequestMapper::toDto);
    }

    /**
     * Delete the followRequest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete FollowRequest : {}", id);
        followRequestRepository.deleteById(id);
    }
}
