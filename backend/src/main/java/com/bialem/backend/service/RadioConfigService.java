package com.bialem.backend.service;

import com.bialem.backend.domain.RadioConfig;
import com.bialem.backend.repository.RadioConfigRepository;
import com.bialem.backend.service.dto.RadioConfigDTO;
import com.bialem.backend.service.mapper.RadioConfigMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RadioConfig}.
 */
@Service
@Transactional
public class RadioConfigService {

    private final Logger log = LoggerFactory.getLogger(RadioConfigService.class);

    private final RadioConfigRepository radioConfigRepository;

    private final RadioConfigMapper radioConfigMapper;

    public RadioConfigService(RadioConfigRepository radioConfigRepository, RadioConfigMapper radioConfigMapper) {
        this.radioConfigRepository = radioConfigRepository;
        this.radioConfigMapper = radioConfigMapper;
    }

    /**
     * Save a radioConfig.
     *
     * @param radioConfigDTO the entity to save.
     * @return the persisted entity.
     */
    public RadioConfigDTO save(RadioConfigDTO radioConfigDTO) {
        log.debug("Request to save RadioConfig : {}", radioConfigDTO);
        RadioConfig radioConfig = radioConfigMapper.toEntity(radioConfigDTO);
        if (radioConfig.getCreatedAt() == null) {
            radioConfig.setCreatedAt(Instant.now());
        }
        radioConfig.setUpdatedAt(Instant.now());
        radioConfig = radioConfigRepository.save(radioConfig);
        return radioConfigMapper.toDto(radioConfig);
    }

    /**
     * Partially update a radioConfig.
     *
     * @param radioConfigDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RadioConfigDTO> partialUpdate(RadioConfigDTO radioConfigDTO) {
        log.debug("Request to partially update RadioConfig : {}", radioConfigDTO);

        return radioConfigRepository
            .findById(radioConfigDTO.getId())
            .map(existingRadioConfig -> {
                radioConfigMapper.partialUpdate(existingRadioConfig, radioConfigDTO);
                existingRadioConfig.setUpdatedAt(Instant.now());
                return existingRadioConfig;
            })
            .map(radioConfigRepository::save)
            .map(radioConfigMapper::toDto);
    }

    /**
     * Get all the radioConfigs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RadioConfigDTO> findAll() {
        log.debug("Request to get all RadioConfigs");
        return radioConfigMapper.toDto(radioConfigRepository.findAll());
    }

    /**
     * Get one radioConfig by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RadioConfigDTO> findOne(Long id) {
        log.debug("Request to get RadioConfig : {}", id);
        return radioConfigRepository.findById(id).map(radioConfigMapper::toDto);
    }

    /**
     * Get the latest (most recently created) radio config.
     * For a radio station, there's typically only one config record.
     */
    @Transactional(readOnly = true)
    public Optional<RadioConfigDTO> findLatest() {
        return radioConfigRepository.findFirstByOrderByCreatedAtDesc().map(radioConfigMapper::toDto);
    }

    /**
     * Delete the radioConfig by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RadioConfig : {}", id);
        radioConfigRepository.deleteById(id);
    }
}
