package com.bialem.backend.service;

import com.bialem.backend.domain.HonorBadge;
import com.bialem.backend.repository.HonorBadgeRepository;
import com.bialem.backend.service.dto.HonorBadgeDTO;
import com.bialem.backend.service.mapper.HonorBadgeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.HonorBadge}.
 */
@Service
@Transactional
public class HonorBadgeService {

    private static final Logger LOG = LoggerFactory.getLogger(HonorBadgeService.class);

    private final HonorBadgeRepository honorBadgeRepository;

    private final HonorBadgeMapper honorBadgeMapper;

    public HonorBadgeService(HonorBadgeRepository honorBadgeRepository, HonorBadgeMapper honorBadgeMapper) {
        this.honorBadgeRepository = honorBadgeRepository;
        this.honorBadgeMapper = honorBadgeMapper;
    }

    /**
     * Save a honorBadge.
     *
     * @param honorBadgeDTO the entity to save.
     * @return the persisted entity.
     */
    public HonorBadgeDTO save(HonorBadgeDTO honorBadgeDTO) {
        LOG.debug("Request to save HonorBadge : {}", honorBadgeDTO);
        HonorBadge honorBadge = honorBadgeMapper.toEntity(honorBadgeDTO);
        honorBadge = honorBadgeRepository.save(honorBadge);
        return honorBadgeMapper.toDto(honorBadge);
    }

    /**
     * Update a honorBadge.
     *
     * @param honorBadgeDTO the entity to save.
     * @return the persisted entity.
     */
    public HonorBadgeDTO update(HonorBadgeDTO honorBadgeDTO) {
        LOG.debug("Request to update HonorBadge : {}", honorBadgeDTO);
        HonorBadge honorBadge = honorBadgeMapper.toEntity(honorBadgeDTO);
        honorBadge = honorBadgeRepository.save(honorBadge);
        return honorBadgeMapper.toDto(honorBadge);
    }

    /**
     * Partially update a honorBadge.
     *
     * @param honorBadgeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<HonorBadgeDTO> partialUpdate(HonorBadgeDTO honorBadgeDTO) {
        LOG.debug("Request to partially update HonorBadge : {}", honorBadgeDTO);

        return honorBadgeRepository
            .findById(honorBadgeDTO.getId())
            .map(existingHonorBadge -> {
                honorBadgeMapper.partialUpdate(existingHonorBadge, honorBadgeDTO);

                return existingHonorBadge;
            })
            .map(honorBadgeRepository::save)
            .map(honorBadgeMapper::toDto);
    }

    /**
     * Get all the honorBadges.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<HonorBadgeDTO> findAll() {
        LOG.debug("Request to get all HonorBadges");
        return honorBadgeRepository.findAll().stream().map(honorBadgeMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one honorBadge by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<HonorBadgeDTO> findOne(Long id) {
        LOG.debug("Request to get HonorBadge : {}", id);
        return honorBadgeRepository.findById(id).map(honorBadgeMapper::toDto);
    }

    /**
     * Delete the honorBadge by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete HonorBadge : {}", id);
        honorBadgeRepository.deleteById(id);
    }
}
