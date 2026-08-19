package com.bialem.backend.service;

import com.bialem.backend.domain.PartnerOffer;
import com.bialem.backend.repository.PartnerOfferRepository;
import com.bialem.backend.service.dto.PartnerOfferDTO;
import com.bialem.backend.service.mapper.PartnerOfferMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.PartnerOffer}.
 */
@Service
@Transactional
public class PartnerOfferService {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerOfferService.class);

    private final PartnerOfferRepository partnerOfferRepository;

    private final PartnerOfferMapper partnerOfferMapper;

    public PartnerOfferService(PartnerOfferRepository partnerOfferRepository, PartnerOfferMapper partnerOfferMapper) {
        this.partnerOfferRepository = partnerOfferRepository;
        this.partnerOfferMapper = partnerOfferMapper;
    }

    /**
     * Save a partnerOffer.
     *
     * @param partnerOfferDTO the entity to save.
     * @return the persisted entity.
     */
    public PartnerOfferDTO save(PartnerOfferDTO partnerOfferDTO) {
        LOG.debug("Request to save PartnerOffer : {}", partnerOfferDTO);
        PartnerOffer partnerOffer = partnerOfferMapper.toEntity(partnerOfferDTO);
        partnerOffer = partnerOfferRepository.save(partnerOffer);
        return partnerOfferMapper.toDto(partnerOffer);
    }

    /**
     * Update a partnerOffer.
     *
     * @param partnerOfferDTO the entity to save.
     * @return the persisted entity.
     */
    public PartnerOfferDTO update(PartnerOfferDTO partnerOfferDTO) {
        LOG.debug("Request to update PartnerOffer : {}", partnerOfferDTO);
        PartnerOffer partnerOffer = partnerOfferMapper.toEntity(partnerOfferDTO);
        partnerOffer = partnerOfferRepository.save(partnerOffer);
        return partnerOfferMapper.toDto(partnerOffer);
    }

    /**
     * Partially update a partnerOffer.
     *
     * @param partnerOfferDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PartnerOfferDTO> partialUpdate(PartnerOfferDTO partnerOfferDTO) {
        LOG.debug("Request to partially update PartnerOffer : {}", partnerOfferDTO);

        return partnerOfferRepository
            .findById(partnerOfferDTO.getId())
            .map(existingPartnerOffer -> {
                partnerOfferMapper.partialUpdate(existingPartnerOffer, partnerOfferDTO);

                return existingPartnerOffer;
            })
            .map(partnerOfferRepository::save)
            .map(partnerOfferMapper::toDto);
    }

    /**
     * Get all the partnerOffers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PartnerOfferDTO> findAll() {
        LOG.debug("Request to get all PartnerOffers");
        return partnerOfferRepository.findAll().stream().map(partnerOfferMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one partnerOffer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PartnerOfferDTO> findOne(Long id) {
        LOG.debug("Request to get PartnerOffer : {}", id);
        return partnerOfferRepository.findById(id).map(partnerOfferMapper::toDto);
    }

    /**
     * Delete the partnerOffer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PartnerOffer : {}", id);
        partnerOfferRepository.deleteById(id);
    }
}
