package com.bialem.backend.service;

import com.bialem.backend.domain.EventMessage;
import com.bialem.backend.repository.EventMessageRepository;
import com.bialem.backend.service.dto.EventMessageDTO;
import com.bialem.backend.service.mapper.EventMessageMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.EventMessage}.
 */
@Service
@Transactional
public class EventMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(EventMessageService.class);

    private final EventMessageRepository eventMessageRepository;

    private final EventMessageMapper eventMessageMapper;

    public EventMessageService(EventMessageRepository eventMessageRepository, EventMessageMapper eventMessageMapper) {
        this.eventMessageRepository = eventMessageRepository;
        this.eventMessageMapper = eventMessageMapper;
    }

    /**
     * Save a eventMessage.
     *
     * @param eventMessageDTO the entity to save.
     * @return the persisted entity.
     */
    public EventMessageDTO save(EventMessageDTO eventMessageDTO) {
        LOG.debug("Request to save EventMessage : {}", eventMessageDTO);
        EventMessage eventMessage = eventMessageMapper.toEntity(eventMessageDTO);
        eventMessage = eventMessageRepository.save(eventMessage);
        return eventMessageMapper.toDto(eventMessage);
    }

    /**
     * Update a eventMessage.
     *
     * @param eventMessageDTO the entity to save.
     * @return the persisted entity.
     */
    public EventMessageDTO update(EventMessageDTO eventMessageDTO) {
        LOG.debug("Request to update EventMessage : {}", eventMessageDTO);
        EventMessage eventMessage = eventMessageMapper.toEntity(eventMessageDTO);
        eventMessage = eventMessageRepository.save(eventMessage);
        return eventMessageMapper.toDto(eventMessage);
    }

    /**
     * Partially update a eventMessage.
     *
     * @param eventMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EventMessageDTO> partialUpdate(EventMessageDTO eventMessageDTO) {
        LOG.debug("Request to partially update EventMessage : {}", eventMessageDTO);

        return eventMessageRepository
            .findById(eventMessageDTO.getId())
            .map(existingEventMessage -> {
                eventMessageMapper.partialUpdate(existingEventMessage, eventMessageDTO);

                return existingEventMessage;
            })
            .map(eventMessageRepository::save)
            .map(eventMessageMapper::toDto);
    }

    /**
     * Get all the eventMessages.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EventMessageDTO> findAll() {
        LOG.debug("Request to get all EventMessages");
        return eventMessageRepository.findAll().stream().map(eventMessageMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one eventMessage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EventMessageDTO> findOne(Long id) {
        LOG.debug("Request to get EventMessage : {}", id);
        return eventMessageRepository.findById(id).map(eventMessageMapper::toDto);
    }

    /**
     * Delete the eventMessage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete EventMessage : {}", id);
        eventMessageRepository.deleteById(id);
    }
}
