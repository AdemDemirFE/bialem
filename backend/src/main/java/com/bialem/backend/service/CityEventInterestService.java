package com.bialem.backend.service;

import com.bialem.backend.domain.CityEventInterest;
import com.bialem.backend.repository.CityEventInterestRepository;
import com.bialem.backend.service.dto.CityEventInterestDTO;
import com.bialem.backend.service.mapper.CityEventInterestMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.CityEventInterest}.
 */
@Service
@Transactional
public class CityEventInterestService {

    private static final Logger LOG = LoggerFactory.getLogger(CityEventInterestService.class);

    private final CityEventInterestRepository cityEventInterestRepository;

    private final CityEventInterestMapper cityEventInterestMapper;

    public CityEventInterestService(
        CityEventInterestRepository cityEventInterestRepository,
        CityEventInterestMapper cityEventInterestMapper
    ) {
        this.cityEventInterestRepository = cityEventInterestRepository;
        this.cityEventInterestMapper = cityEventInterestMapper;
    }

    /**
     * Save a cityEventInterest.
     *
     * @param cityEventInterestDTO the entity to save.
     * @return the persisted entity.
     */
    public CityEventInterestDTO save(CityEventInterestDTO cityEventInterestDTO) {
        LOG.debug("Request to save CityEventInterest : {}", cityEventInterestDTO);
        CityEventInterest cityEventInterest = cityEventInterestMapper.toEntity(cityEventInterestDTO);
        cityEventInterest = cityEventInterestRepository.save(cityEventInterest);
        return cityEventInterestMapper.toDto(cityEventInterest);
    }

    /**
     * Update a cityEventInterest.
     *
     * @param cityEventInterestDTO the entity to save.
     * @return the persisted entity.
     */
    public CityEventInterestDTO update(CityEventInterestDTO cityEventInterestDTO) {
        LOG.debug("Request to update CityEventInterest : {}", cityEventInterestDTO);
        CityEventInterest cityEventInterest = cityEventInterestMapper.toEntity(cityEventInterestDTO);
        cityEventInterest = cityEventInterestRepository.save(cityEventInterest);
        return cityEventInterestMapper.toDto(cityEventInterest);
    }

    /**
     * Partially update a cityEventInterest.
     *
     * @param cityEventInterestDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CityEventInterestDTO> partialUpdate(CityEventInterestDTO cityEventInterestDTO) {
        LOG.debug("Request to partially update CityEventInterest : {}", cityEventInterestDTO);

        return cityEventInterestRepository
            .findById(cityEventInterestDTO.getId())
            .map(existingCityEventInterest -> {
                cityEventInterestMapper.partialUpdate(existingCityEventInterest, cityEventInterestDTO);

                return existingCityEventInterest;
            })
            .map(cityEventInterestRepository::save)
            .map(cityEventInterestMapper::toDto);
    }

    /**
     * Get all the cityEventInterests.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CityEventInterestDTO> findAll() {
        LOG.debug("Request to get all CityEventInterests");
        return cityEventInterestRepository
            .findAll()
            .stream()
            .map(cityEventInterestMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one cityEventInterest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CityEventInterestDTO> findOne(Long id) {
        LOG.debug("Request to get CityEventInterest : {}", id);
        return cityEventInterestRepository.findById(id).map(cityEventInterestMapper::toDto);
    }

    /**
     * Delete the cityEventInterest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CityEventInterest : {}", id);
        cityEventInterestRepository.deleteById(id);
    }
}
