package com.bialem.backend.service;

import com.bialem.backend.domain.Community;
import com.bialem.backend.repository.CommunityRepository;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.mapper.CommunityMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.Community}.
 */
@Service
@Transactional
public class CommunityService {

    private static final Logger LOG = LoggerFactory.getLogger(CommunityService.class);

    private final CommunityRepository communityRepository;

    private final CommunityMapper communityMapper;

    public CommunityService(CommunityRepository communityRepository, CommunityMapper communityMapper) {
        this.communityRepository = communityRepository;
        this.communityMapper = communityMapper;
    }

    /**
     * Save a community.
     *
     * @param communityDTO the entity to save.
     * @return the persisted entity.
     */
    public CommunityDTO save(CommunityDTO communityDTO) {
        LOG.debug("Request to save Community : {}", communityDTO);
        Community community = communityMapper.toEntity(communityDTO);
        community = communityRepository.save(community);
        return communityMapper.toDto(community);
    }

    /**
     * Update a community.
     *
     * @param communityDTO the entity to save.
     * @return the persisted entity.
     */
    public CommunityDTO update(CommunityDTO communityDTO) {
        LOG.debug("Request to update Community : {}", communityDTO);
        Community community = communityMapper.toEntity(communityDTO);
        community = communityRepository.save(community);
        return communityMapper.toDto(community);
    }

    /**
     * Partially update a community.
     *
     * @param communityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CommunityDTO> partialUpdate(CommunityDTO communityDTO) {
        LOG.debug("Request to partially update Community : {}", communityDTO);

        return communityRepository
            .findById(communityDTO.getId())
            .map(existingCommunity -> {
                communityMapper.partialUpdate(existingCommunity, communityDTO);

                return existingCommunity;
            })
            .map(communityRepository::save)
            .map(communityMapper::toDto);
    }

    /**
     * Get one community by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CommunityDTO> findOne(Long id) {
        LOG.debug("Request to get Community : {}", id);
        return communityRepository.findById(id).map(communityMapper::toDto);
    }

    /**
     * Delete the community by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Community : {}", id);
        communityRepository.deleteById(id);
    }
}
