package com.bialem.backend.service;

import com.bialem.backend.domain.PushToken;
import com.bialem.backend.repository.PushTokenRepository;
import com.bialem.backend.service.dto.PushTokenDTO;
import com.bialem.backend.service.mapper.PushTokenMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.PushToken}.
 */
@Service
@Transactional
public class PushTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(PushTokenService.class);

    private final PushTokenRepository pushTokenRepository;

    private final PushTokenMapper pushTokenMapper;

    public PushTokenService(PushTokenRepository pushTokenRepository, PushTokenMapper pushTokenMapper) {
        this.pushTokenRepository = pushTokenRepository;
        this.pushTokenMapper = pushTokenMapper;
    }

    /**
     * Save a pushToken.
     *
     * @param pushTokenDTO the entity to save.
     * @return the persisted entity.
     */
    public PushTokenDTO save(PushTokenDTO pushTokenDTO) {
        LOG.debug("Request to save PushToken : {}", pushTokenDTO);
        PushToken pushToken = pushTokenMapper.toEntity(pushTokenDTO);
        pushToken = pushTokenRepository.save(pushToken);
        return pushTokenMapper.toDto(pushToken);
    }

    /**
     * Update a pushToken.
     *
     * @param pushTokenDTO the entity to save.
     * @return the persisted entity.
     */
    public PushTokenDTO update(PushTokenDTO pushTokenDTO) {
        LOG.debug("Request to update PushToken : {}", pushTokenDTO);
        PushToken pushToken = pushTokenMapper.toEntity(pushTokenDTO);
        pushToken = pushTokenRepository.save(pushToken);
        return pushTokenMapper.toDto(pushToken);
    }

    /**
     * Partially update a pushToken.
     *
     * @param pushTokenDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PushTokenDTO> partialUpdate(PushTokenDTO pushTokenDTO) {
        LOG.debug("Request to partially update PushToken : {}", pushTokenDTO);

        return pushTokenRepository
            .findById(pushTokenDTO.getId())
            .map(existingPushToken -> {
                pushTokenMapper.partialUpdate(existingPushToken, pushTokenDTO);

                return existingPushToken;
            })
            .map(pushTokenRepository::save)
            .map(pushTokenMapper::toDto);
    }

    /**
     * Get all the pushTokens.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PushTokenDTO> findAll() {
        LOG.debug("Request to get all PushTokens");
        return pushTokenRepository.findAll().stream().map(pushTokenMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one pushToken by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PushTokenDTO> findOne(Long id) {
        LOG.debug("Request to get PushToken : {}", id);
        return pushTokenRepository.findById(id).map(pushTokenMapper::toDto);
    }

    /**
     * Delete the pushToken by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PushToken : {}", id);
        pushTokenRepository.deleteById(id);
    }
}
