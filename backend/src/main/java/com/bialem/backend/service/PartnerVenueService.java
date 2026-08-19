package com.bialem.backend.service;

import com.bialem.backend.domain.PartnerVenue;
import com.bialem.backend.repository.PartnerVenueRepository;
import com.bialem.backend.service.dto.PartnerVenueDTO;
import com.bialem.backend.service.mapper.PartnerVenueMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.PartnerVenue}.
 */
@Service
@Transactional
public class PartnerVenueService {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerVenueService.class);

    private final PartnerVenueRepository partnerVenueRepository;

    private final PartnerVenueMapper partnerVenueMapper;

    public PartnerVenueService(PartnerVenueRepository partnerVenueRepository, PartnerVenueMapper partnerVenueMapper) {
        this.partnerVenueRepository = partnerVenueRepository;
        this.partnerVenueMapper = partnerVenueMapper;
    }

    /**
     * Save a partnerVenue.
     *
     * @param partnerVenueDTO the entity to save.
     * @return the persisted entity.
     */
    public PartnerVenueDTO save(PartnerVenueDTO partnerVenueDTO) {
        LOG.debug("Request to save PartnerVenue : {}", partnerVenueDTO);
        PartnerVenue partnerVenue = partnerVenueMapper.toEntity(partnerVenueDTO);
        partnerVenue = partnerVenueRepository.save(partnerVenue);
        return partnerVenueMapper.toDto(partnerVenue);
    }

    /**
     * Update a partnerVenue.
     *
     * @param partnerVenueDTO the entity to save.
     * @return the persisted entity.
     */
    public PartnerVenueDTO update(PartnerVenueDTO partnerVenueDTO) {
        LOG.debug("Request to update PartnerVenue : {}", partnerVenueDTO);
        PartnerVenue partnerVenue = partnerVenueMapper.toEntity(partnerVenueDTO);
        partnerVenue = partnerVenueRepository.save(partnerVenue);
        return partnerVenueMapper.toDto(partnerVenue);
    }

    /**
     * Partially update a partnerVenue.
     *
     * @param partnerVenueDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PartnerVenueDTO> partialUpdate(PartnerVenueDTO partnerVenueDTO) {
        LOG.debug("Request to partially update PartnerVenue : {}", partnerVenueDTO);

        return partnerVenueRepository
            .findById(partnerVenueDTO.getId())
            .map(existingPartnerVenue -> {
                partnerVenueMapper.partialUpdate(existingPartnerVenue, partnerVenueDTO);

                return existingPartnerVenue;
            })
            .map(partnerVenueRepository::save)
            .map(partnerVenueMapper::toDto);
    }

    /**
     * Get one partnerVenue by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PartnerVenueDTO> findOne(Long id) {
        LOG.debug("Request to get PartnerVenue : {}", id);
        return partnerVenueRepository.findById(id).map(partnerVenueMapper::toDto);
    }

    /**
     * Delete the partnerVenue by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PartnerVenue : {}", id);
        partnerVenueRepository.deleteById(id);
    }
}
