package com.bialem.backend.service;

import com.bialem.backend.domain.StoryCommunityTarget;
import com.bialem.backend.repository.StoryCommunityTargetRepository;
import com.bialem.backend.service.dto.StoryCommunityTargetDTO;
import com.bialem.backend.service.mapper.StoryCommunityTargetMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.StoryCommunityTarget}.
 */
@Service
@Transactional
public class StoryCommunityTargetService {

    private static final Logger LOG = LoggerFactory.getLogger(StoryCommunityTargetService.class);

    private final StoryCommunityTargetRepository storyCommunityTargetRepository;

    private final StoryCommunityTargetMapper storyCommunityTargetMapper;

    public StoryCommunityTargetService(
        StoryCommunityTargetRepository storyCommunityTargetRepository,
        StoryCommunityTargetMapper storyCommunityTargetMapper
    ) {
        this.storyCommunityTargetRepository = storyCommunityTargetRepository;
        this.storyCommunityTargetMapper = storyCommunityTargetMapper;
    }

    /**
     * Save a storyCommunityTarget.
     *
     * @param storyCommunityTargetDTO the entity to save.
     * @return the persisted entity.
     */
    public StoryCommunityTargetDTO save(StoryCommunityTargetDTO storyCommunityTargetDTO) {
        LOG.debug("Request to save StoryCommunityTarget : {}", storyCommunityTargetDTO);
        StoryCommunityTarget storyCommunityTarget = storyCommunityTargetMapper.toEntity(storyCommunityTargetDTO);
        storyCommunityTarget = storyCommunityTargetRepository.save(storyCommunityTarget);
        return storyCommunityTargetMapper.toDto(storyCommunityTarget);
    }

    /**
     * Update a storyCommunityTarget.
     *
     * @param storyCommunityTargetDTO the entity to save.
     * @return the persisted entity.
     */
    public StoryCommunityTargetDTO update(StoryCommunityTargetDTO storyCommunityTargetDTO) {
        LOG.debug("Request to update StoryCommunityTarget : {}", storyCommunityTargetDTO);
        StoryCommunityTarget storyCommunityTarget = storyCommunityTargetMapper.toEntity(storyCommunityTargetDTO);
        storyCommunityTarget = storyCommunityTargetRepository.save(storyCommunityTarget);
        return storyCommunityTargetMapper.toDto(storyCommunityTarget);
    }

    /**
     * Partially update a storyCommunityTarget.
     *
     * @param storyCommunityTargetDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StoryCommunityTargetDTO> partialUpdate(StoryCommunityTargetDTO storyCommunityTargetDTO) {
        LOG.debug("Request to partially update StoryCommunityTarget : {}", storyCommunityTargetDTO);

        return storyCommunityTargetRepository
            .findById(storyCommunityTargetDTO.getId())
            .map(existingStoryCommunityTarget -> {
                storyCommunityTargetMapper.partialUpdate(existingStoryCommunityTarget, storyCommunityTargetDTO);

                return existingStoryCommunityTarget;
            })
            .map(storyCommunityTargetRepository::save)
            .map(storyCommunityTargetMapper::toDto);
    }

    /**
     * Get all the storyCommunityTargets.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StoryCommunityTargetDTO> findAll() {
        LOG.debug("Request to get all StoryCommunityTargets");
        return storyCommunityTargetRepository
            .findAll()
            .stream()
            .map(storyCommunityTargetMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one storyCommunityTarget by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StoryCommunityTargetDTO> findOne(Long id) {
        LOG.debug("Request to get StoryCommunityTarget : {}", id);
        return storyCommunityTargetRepository.findById(id).map(storyCommunityTargetMapper::toDto);
    }

    /**
     * Delete the storyCommunityTarget by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete StoryCommunityTarget : {}", id);
        storyCommunityTargetRepository.deleteById(id);
    }
}
