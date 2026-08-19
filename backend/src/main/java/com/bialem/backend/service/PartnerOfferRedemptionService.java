package com.bialem.backend.service;

import com.bialem.backend.domain.PartnerOfferRedemption;
import com.bialem.backend.repository.PartnerOfferRedemptionRepository;
import com.bialem.backend.service.dto.PartnerOfferRedemptionDTO;
import com.bialem.backend.service.mapper.PartnerOfferRedemptionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.PartnerOfferRedemption}.
 */
@Service
@Transactional
public class PartnerOfferRedemptionService {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerOfferRedemptionService.class);

    private final PartnerOfferRedemptionRepository partnerOfferRedemptionRepository;

    private final PartnerOfferRedemptionMapper partnerOfferRedemptionMapper;

    public PartnerOfferRedemptionService(
        PartnerOfferRedemptionRepository partnerOfferRedemptionRepository,
        PartnerOfferRedemptionMapper partnerOfferRedemptionMapper
    ) {
        this.partnerOfferRedemptionRepository = partnerOfferRedemptionRepository;
        this.partnerOfferRedemptionMapper = partnerOfferRedemptionMapper;
    }

    /**
     * Save a partnerOfferRedemption.
     *
     * @param partnerOfferRedemptionDTO the entity to save.
     * @return the persisted entity.
     */
    public PartnerOfferRedemptionDTO save(PartnerOfferRedemptionDTO partnerOfferRedemptionDTO) {
        LOG.debug("Request to save PartnerOfferRedemption : {}", partnerOfferRedemptionDTO);
        PartnerOfferRedemption partnerOfferRedemption = partnerOfferRedemptionMapper.toEntity(partnerOfferRedemptionDTO);
        partnerOfferRedemption = partnerOfferRedemptionRepository.save(partnerOfferRedemption);
        return partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);
    }

    /**
     * Update a partnerOfferRedemption.
     *
     * @param partnerOfferRedemptionDTO the entity to save.
     * @return the persisted entity.
     */
    public PartnerOfferRedemptionDTO update(PartnerOfferRedemptionDTO partnerOfferRedemptionDTO) {
        LOG.debug("Request to update PartnerOfferRedemption : {}", partnerOfferRedemptionDTO);
        PartnerOfferRedemption partnerOfferRedemption = partnerOfferRedemptionMapper.toEntity(partnerOfferRedemptionDTO);
        partnerOfferRedemption = partnerOfferRedemptionRepository.save(partnerOfferRedemption);
        return partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);
    }

    /**
     * Partially update a partnerOfferRedemption.
     *
     * @param partnerOfferRedemptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PartnerOfferRedemptionDTO> partialUpdate(PartnerOfferRedemptionDTO partnerOfferRedemptionDTO) {
        LOG.debug("Request to partially update PartnerOfferRedemption : {}", partnerOfferRedemptionDTO);

        return partnerOfferRedemptionRepository
            .findById(partnerOfferRedemptionDTO.getId())
            .map(existingPartnerOfferRedemption -> {
                partnerOfferRedemptionMapper.partialUpdate(existingPartnerOfferRedemption, partnerOfferRedemptionDTO);

                return existingPartnerOfferRedemption;
            })
            .map(partnerOfferRedemptionRepository::save)
            .map(partnerOfferRedemptionMapper::toDto);
    }

    /**
     * Get all the partnerOfferRedemptions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PartnerOfferRedemptionDTO> findAll() {
        LOG.debug("Request to get all PartnerOfferRedemptions");
        return partnerOfferRedemptionRepository
            .findAll()
            .stream()
            .map(partnerOfferRedemptionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one partnerOfferRedemption by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PartnerOfferRedemptionDTO> findOne(Long id) {
        LOG.debug("Request to get PartnerOfferRedemption : {}", id);
        return partnerOfferRedemptionRepository.findById(id).map(partnerOfferRedemptionMapper::toDto);
    }

    /**
     * Delete the partnerOfferRedemption by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PartnerOfferRedemption : {}", id);
        partnerOfferRedemptionRepository.deleteById(id);
    }
}
