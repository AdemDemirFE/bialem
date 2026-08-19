package com.bialem.backend.service;

import com.bialem.backend.domain.CommunityModeratorAssistant;
import com.bialem.backend.repository.CommunityModeratorAssistantRepository;
import com.bialem.backend.service.dto.CommunityModeratorAssistantDTO;
import com.bialem.backend.service.mapper.CommunityModeratorAssistantMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.CommunityModeratorAssistant}.
 */
@Service
@Transactional
public class CommunityModeratorAssistantService {

    private static final Logger LOG = LoggerFactory.getLogger(CommunityModeratorAssistantService.class);

    private final CommunityModeratorAssistantRepository communityModeratorAssistantRepository;

    private final CommunityModeratorAssistantMapper communityModeratorAssistantMapper;

    public CommunityModeratorAssistantService(
        CommunityModeratorAssistantRepository communityModeratorAssistantRepository,
        CommunityModeratorAssistantMapper communityModeratorAssistantMapper
    ) {
        this.communityModeratorAssistantRepository = communityModeratorAssistantRepository;
        this.communityModeratorAssistantMapper = communityModeratorAssistantMapper;
    }

    /**
     * Save a communityModeratorAssistant.
     *
     * @param communityModeratorAssistantDTO the entity to save.
     * @return the persisted entity.
     */
    public CommunityModeratorAssistantDTO save(CommunityModeratorAssistantDTO communityModeratorAssistantDTO) {
        LOG.debug("Request to save CommunityModeratorAssistant : {}", communityModeratorAssistantDTO);
        CommunityModeratorAssistant communityModeratorAssistant = communityModeratorAssistantMapper.toEntity(
            communityModeratorAssistantDTO
        );
        communityModeratorAssistant = communityModeratorAssistantRepository.save(communityModeratorAssistant);
        return communityModeratorAssistantMapper.toDto(communityModeratorAssistant);
    }

    /**
     * Update a communityModeratorAssistant.
     *
     * @param communityModeratorAssistantDTO the entity to save.
     * @return the persisted entity.
     */
    public CommunityModeratorAssistantDTO update(CommunityModeratorAssistantDTO communityModeratorAssistantDTO) {
        LOG.debug("Request to update CommunityModeratorAssistant : {}", communityModeratorAssistantDTO);
        CommunityModeratorAssistant communityModeratorAssistant = communityModeratorAssistantMapper.toEntity(
            communityModeratorAssistantDTO
        );
        communityModeratorAssistant = communityModeratorAssistantRepository.save(communityModeratorAssistant);
        return communityModeratorAssistantMapper.toDto(communityModeratorAssistant);
    }

    /**
     * Partially update a communityModeratorAssistant.
     *
     * @param communityModeratorAssistantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CommunityModeratorAssistantDTO> partialUpdate(CommunityModeratorAssistantDTO communityModeratorAssistantDTO) {
        LOG.debug("Request to partially update CommunityModeratorAssistant : {}", communityModeratorAssistantDTO);

        return communityModeratorAssistantRepository
            .findById(communityModeratorAssistantDTO.getId())
            .map(existingCommunityModeratorAssistant -> {
                communityModeratorAssistantMapper.partialUpdate(existingCommunityModeratorAssistant, communityModeratorAssistantDTO);

                return existingCommunityModeratorAssistant;
            })
            .map(communityModeratorAssistantRepository::save)
            .map(communityModeratorAssistantMapper::toDto);
    }

    /**
     * Get all the communityModeratorAssistants.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CommunityModeratorAssistantDTO> findAll() {
        LOG.debug("Request to get all CommunityModeratorAssistants");
        return communityModeratorAssistantRepository
            .findAll()
            .stream()
            .map(communityModeratorAssistantMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one communityModeratorAssistant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CommunityModeratorAssistantDTO> findOne(Long id) {
        LOG.debug("Request to get CommunityModeratorAssistant : {}", id);
        return communityModeratorAssistantRepository.findById(id).map(communityModeratorAssistantMapper::toDto);
    }

    /**
     * Delete the communityModeratorAssistant by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CommunityModeratorAssistant : {}", id);
        communityModeratorAssistantRepository.deleteById(id);
    }
}
