package com.bialem.backend.service;

import com.bialem.backend.domain.CityEventSyncLog;
import com.bialem.backend.repository.CityEventSyncLogRepository;
import com.bialem.backend.service.dto.CityEventSyncLogDTO;
import com.bialem.backend.service.mapper.CityEventSyncLogMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.CityEventSyncLog}.
 */
@Service
@Transactional
public class CityEventSyncLogService {

    private static final Logger LOG = LoggerFactory.getLogger(CityEventSyncLogService.class);

    private final CityEventSyncLogRepository cityEventSyncLogRepository;

    private final CityEventSyncLogMapper cityEventSyncLogMapper;

    public CityEventSyncLogService(CityEventSyncLogRepository cityEventSyncLogRepository, CityEventSyncLogMapper cityEventSyncLogMapper) {
        this.cityEventSyncLogRepository = cityEventSyncLogRepository;
        this.cityEventSyncLogMapper = cityEventSyncLogMapper;
    }

    /**
     * Save a cityEventSyncLog.
     *
     * @param cityEventSyncLogDTO the entity to save.
     * @return the persisted entity.
     */
    public CityEventSyncLogDTO save(CityEventSyncLogDTO cityEventSyncLogDTO) {
        LOG.debug("Request to save CityEventSyncLog : {}", cityEventSyncLogDTO);
        CityEventSyncLog cityEventSyncLog = cityEventSyncLogMapper.toEntity(cityEventSyncLogDTO);
        cityEventSyncLog = cityEventSyncLogRepository.save(cityEventSyncLog);
        return cityEventSyncLogMapper.toDto(cityEventSyncLog);
    }

    /**
     * Update a cityEventSyncLog.
     *
     * @param cityEventSyncLogDTO the entity to save.
     * @return the persisted entity.
     */
    public CityEventSyncLogDTO update(CityEventSyncLogDTO cityEventSyncLogDTO) {
        LOG.debug("Request to update CityEventSyncLog : {}", cityEventSyncLogDTO);
        CityEventSyncLog cityEventSyncLog = cityEventSyncLogMapper.toEntity(cityEventSyncLogDTO);
        cityEventSyncLog = cityEventSyncLogRepository.save(cityEventSyncLog);
        return cityEventSyncLogMapper.toDto(cityEventSyncLog);
    }

    /**
     * Partially update a cityEventSyncLog.
     *
     * @param cityEventSyncLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CityEventSyncLogDTO> partialUpdate(CityEventSyncLogDTO cityEventSyncLogDTO) {
        LOG.debug("Request to partially update CityEventSyncLog : {}", cityEventSyncLogDTO);

        return cityEventSyncLogRepository
            .findById(cityEventSyncLogDTO.getId())
            .map(existingCityEventSyncLog -> {
                cityEventSyncLogMapper.partialUpdate(existingCityEventSyncLog, cityEventSyncLogDTO);

                return existingCityEventSyncLog;
            })
            .map(cityEventSyncLogRepository::save)
            .map(cityEventSyncLogMapper::toDto);
    }

    /**
     * Get all the cityEventSyncLogs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CityEventSyncLogDTO> findAll() {
        LOG.debug("Request to get all CityEventSyncLogs");
        return cityEventSyncLogRepository
            .findAll()
            .stream()
            .map(cityEventSyncLogMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one cityEventSyncLog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CityEventSyncLogDTO> findOne(Long id) {
        LOG.debug("Request to get CityEventSyncLog : {}", id);
        return cityEventSyncLogRepository.findById(id).map(cityEventSyncLogMapper::toDto);
    }

    /**
     * Delete the cityEventSyncLog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CityEventSyncLog : {}", id);
        cityEventSyncLogRepository.deleteById(id);
    }
}
