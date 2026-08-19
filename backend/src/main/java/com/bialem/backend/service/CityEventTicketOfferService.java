package com.bialem.backend.service;

import com.bialem.backend.domain.CityEventTicketOffer;
import com.bialem.backend.repository.CityEventTicketOfferRepository;
import com.bialem.backend.service.dto.CityEventTicketOfferDTO;
import com.bialem.backend.service.mapper.CityEventTicketOfferMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.CityEventTicketOffer}.
 */
@Service
@Transactional
public class CityEventTicketOfferService {

    private static final Logger LOG = LoggerFactory.getLogger(CityEventTicketOfferService.class);

    private final CityEventTicketOfferRepository cityEventTicketOfferRepository;

    private final CityEventTicketOfferMapper cityEventTicketOfferMapper;

    public CityEventTicketOfferService(
        CityEventTicketOfferRepository cityEventTicketOfferRepository,
        CityEventTicketOfferMapper cityEventTicketOfferMapper
    ) {
        this.cityEventTicketOfferRepository = cityEventTicketOfferRepository;
        this.cityEventTicketOfferMapper = cityEventTicketOfferMapper;
    }

    /**
     * Save a cityEventTicketOffer.
     *
     * @param cityEventTicketOfferDTO the entity to save.
     * @return the persisted entity.
     */
    public CityEventTicketOfferDTO save(CityEventTicketOfferDTO cityEventTicketOfferDTO) {
        LOG.debug("Request to save CityEventTicketOffer : {}", cityEventTicketOfferDTO);
        CityEventTicketOffer cityEventTicketOffer = cityEventTicketOfferMapper.toEntity(cityEventTicketOfferDTO);
        cityEventTicketOffer = cityEventTicketOfferRepository.save(cityEventTicketOffer);
        return cityEventTicketOfferMapper.toDto(cityEventTicketOffer);
    }

    /**
     * Update a cityEventTicketOffer.
     *
     * @param cityEventTicketOfferDTO the entity to save.
     * @return the persisted entity.
     */
    public CityEventTicketOfferDTO update(CityEventTicketOfferDTO cityEventTicketOfferDTO) {
        LOG.debug("Request to update CityEventTicketOffer : {}", cityEventTicketOfferDTO);
        CityEventTicketOffer cityEventTicketOffer = cityEventTicketOfferMapper.toEntity(cityEventTicketOfferDTO);
        cityEventTicketOffer = cityEventTicketOfferRepository.save(cityEventTicketOffer);
        return cityEventTicketOfferMapper.toDto(cityEventTicketOffer);
    }

    /**
     * Partially update a cityEventTicketOffer.
     *
     * @param cityEventTicketOfferDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CityEventTicketOfferDTO> partialUpdate(CityEventTicketOfferDTO cityEventTicketOfferDTO) {
        LOG.debug("Request to partially update CityEventTicketOffer : {}", cityEventTicketOfferDTO);

        return cityEventTicketOfferRepository
            .findById(cityEventTicketOfferDTO.getId())
            .map(existingCityEventTicketOffer -> {
                cityEventTicketOfferMapper.partialUpdate(existingCityEventTicketOffer, cityEventTicketOfferDTO);

                return existingCityEventTicketOffer;
            })
            .map(cityEventTicketOfferRepository::save)
            .map(cityEventTicketOfferMapper::toDto);
    }

    /**
     * Get all the cityEventTicketOffers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CityEventTicketOfferDTO> findAll() {
        LOG.debug("Request to get all CityEventTicketOffers");
        return cityEventTicketOfferRepository
            .findAll()
            .stream()
            .map(cityEventTicketOfferMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one cityEventTicketOffer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CityEventTicketOfferDTO> findOne(Long id) {
        LOG.debug("Request to get CityEventTicketOffer : {}", id);
        return cityEventTicketOfferRepository.findById(id).map(cityEventTicketOfferMapper::toDto);
    }

    /**
     * Delete the cityEventTicketOffer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CityEventTicketOffer : {}", id);
        cityEventTicketOfferRepository.deleteById(id);
    }
}
