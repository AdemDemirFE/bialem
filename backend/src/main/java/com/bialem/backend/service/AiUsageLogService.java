package com.bialem.backend.service;

import com.bialem.backend.domain.AiUsageLog;
import com.bialem.backend.repository.AiUsageLogRepository;
import com.bialem.backend.service.dto.AiUsageLogDTO;
import com.bialem.backend.service.mapper.AiUsageLogMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.AiUsageLog}.
 */
@Service
@Transactional
public class AiUsageLogService {

    private static final Logger LOG = LoggerFactory.getLogger(AiUsageLogService.class);

    private final AiUsageLogRepository aiUsageLogRepository;

    private final AiUsageLogMapper aiUsageLogMapper;

    public AiUsageLogService(AiUsageLogRepository aiUsageLogRepository, AiUsageLogMapper aiUsageLogMapper) {
        this.aiUsageLogRepository = aiUsageLogRepository;
        this.aiUsageLogMapper = aiUsageLogMapper;
    }

    /**
     * Save a aiUsageLog.
     *
     * @param aiUsageLogDTO the entity to save.
     * @return the persisted entity.
     */
    public AiUsageLogDTO save(AiUsageLogDTO aiUsageLogDTO) {
        LOG.debug("Request to save AiUsageLog : {}", aiUsageLogDTO);
        AiUsageLog aiUsageLog = aiUsageLogMapper.toEntity(aiUsageLogDTO);
        aiUsageLog = aiUsageLogRepository.save(aiUsageLog);
        return aiUsageLogMapper.toDto(aiUsageLog);
    }

    /**
     * Update a aiUsageLog.
     *
     * @param aiUsageLogDTO the entity to save.
     * @return the persisted entity.
     */
    public AiUsageLogDTO update(AiUsageLogDTO aiUsageLogDTO) {
        LOG.debug("Request to update AiUsageLog : {}", aiUsageLogDTO);
        AiUsageLog aiUsageLog = aiUsageLogMapper.toEntity(aiUsageLogDTO);
        aiUsageLog = aiUsageLogRepository.save(aiUsageLog);
        return aiUsageLogMapper.toDto(aiUsageLog);
    }

    /**
     * Partially update a aiUsageLog.
     *
     * @param aiUsageLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AiUsageLogDTO> partialUpdate(AiUsageLogDTO aiUsageLogDTO) {
        LOG.debug("Request to partially update AiUsageLog : {}", aiUsageLogDTO);

        return aiUsageLogRepository
            .findById(aiUsageLogDTO.getId())
            .map(existingAiUsageLog -> {
                aiUsageLogMapper.partialUpdate(existingAiUsageLog, aiUsageLogDTO);

                return existingAiUsageLog;
            })
            .map(aiUsageLogRepository::save)
            .map(aiUsageLogMapper::toDto);
    }

    /**
     * Get all the aiUsageLogs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AiUsageLogDTO> findAll() {
        LOG.debug("Request to get all AiUsageLogs");
        return aiUsageLogRepository.findAll().stream().map(aiUsageLogMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one aiUsageLog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AiUsageLogDTO> findOne(Long id) {
        LOG.debug("Request to get AiUsageLog : {}", id);
        return aiUsageLogRepository.findById(id).map(aiUsageLogMapper::toDto);
    }

    /**
     * Delete the aiUsageLog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AiUsageLog : {}", id);
        aiUsageLogRepository.deleteById(id);
    }
}
