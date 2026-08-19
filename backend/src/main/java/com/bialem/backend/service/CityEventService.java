package com.bialem.backend.service;

import com.bialem.backend.domain.CityEvent;
import com.bialem.backend.repository.CityEventRepository;
import com.bialem.backend.service.dto.CityEventDTO;
import com.bialem.backend.service.mapper.CityEventMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.CityEvent}.
 */
@Service
@Transactional
public class CityEventService {

    private static final Logger LOG = LoggerFactory.getLogger(CityEventService.class);

    private final CityEventRepository cityEventRepository;

    private final CityEventMapper cityEventMapper;

    public CityEventService(CityEventRepository cityEventRepository, CityEventMapper cityEventMapper) {
        this.cityEventRepository = cityEventRepository;
        this.cityEventMapper = cityEventMapper;
    }

    /**
     * Save a cityEvent.
     *
     * @param cityEventDTO the entity to save.
     * @return the persisted entity.
     */
    public CityEventDTO save(CityEventDTO cityEventDTO) {
        LOG.debug("Request to save CityEvent : {}", cityEventDTO);
        CityEvent cityEvent = cityEventMapper.toEntity(cityEventDTO);
        cityEvent = cityEventRepository.save(cityEvent);
        return cityEventMapper.toDto(cityEvent);
    }

    /**
     * Update a cityEvent.
     *
     * @param cityEventDTO the entity to save.
     * @return the persisted entity.
     */
    public CityEventDTO update(CityEventDTO cityEventDTO) {
        LOG.debug("Request to update CityEvent : {}", cityEventDTO);
        CityEvent cityEvent = cityEventMapper.toEntity(cityEventDTO);
        cityEvent = cityEventRepository.save(cityEvent);
        return cityEventMapper.toDto(cityEvent);
    }

    /**
     * Partially update a cityEvent.
     *
     * @param cityEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CityEventDTO> partialUpdate(CityEventDTO cityEventDTO) {
        LOG.debug("Request to partially update CityEvent : {}", cityEventDTO);

        return cityEventRepository
            .findById(cityEventDTO.getId())
            .map(existingCityEvent -> {
                cityEventMapper.partialUpdate(existingCityEvent, cityEventDTO);

                return existingCityEvent;
            })
            .map(cityEventRepository::save)
            .map(cityEventMapper::toDto);
    }

    /**
     * Get all the cityEvents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CityEventDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CityEvents");
        return cityEventRepository.findAll(pageable).map(cityEventMapper::toDto);
    }

    /**
     * Get one cityEvent by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CityEventDTO> findOne(Long id) {
        LOG.debug("Request to get CityEvent : {}", id);
        return cityEventRepository.findById(id).map(cityEventMapper::toDto);
    }

    /**
     * Delete the cityEvent by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CityEvent : {}", id);
        cityEventRepository.deleteById(id);
    }
}
