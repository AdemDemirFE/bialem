package com.bialem.backend.service;

import com.bialem.backend.domain.EventParticipant;
import com.bialem.backend.repository.EventParticipantRepository;
import com.bialem.backend.service.dto.EventParticipantDTO;
import com.bialem.backend.service.mapper.EventParticipantMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.EventParticipant}.
 */
@Service
@Transactional
public class EventParticipantService {

    private static final Logger LOG = LoggerFactory.getLogger(EventParticipantService.class);

    private final EventParticipantRepository eventParticipantRepository;

    private final EventParticipantMapper eventParticipantMapper;

    public EventParticipantService(EventParticipantRepository eventParticipantRepository, EventParticipantMapper eventParticipantMapper) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.eventParticipantMapper = eventParticipantMapper;
    }

    /**
     * Save a eventParticipant.
     *
     * @param eventParticipantDTO the entity to save.
     * @return the persisted entity.
     */
    public EventParticipantDTO save(EventParticipantDTO eventParticipantDTO) {
        LOG.debug("Request to save EventParticipant : {}", eventParticipantDTO);
        EventParticipant eventParticipant = eventParticipantMapper.toEntity(eventParticipantDTO);
        eventParticipant = eventParticipantRepository.save(eventParticipant);
        return eventParticipantMapper.toDto(eventParticipant);
    }

    /**
     * Update a eventParticipant.
     *
     * @param eventParticipantDTO the entity to save.
     * @return the persisted entity.
     */
    public EventParticipantDTO update(EventParticipantDTO eventParticipantDTO) {
        LOG.debug("Request to update EventParticipant : {}", eventParticipantDTO);
        EventParticipant eventParticipant = eventParticipantMapper.toEntity(eventParticipantDTO);
        eventParticipant = eventParticipantRepository.save(eventParticipant);
        return eventParticipantMapper.toDto(eventParticipant);
    }

    /**
     * Partially update a eventParticipant.
     *
     * @param eventParticipantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EventParticipantDTO> partialUpdate(EventParticipantDTO eventParticipantDTO) {
        LOG.debug("Request to partially update EventParticipant : {}", eventParticipantDTO);

        return eventParticipantRepository
            .findById(eventParticipantDTO.getId())
            .map(existingEventParticipant -> {
                eventParticipantMapper.partialUpdate(existingEventParticipant, eventParticipantDTO);

                return existingEventParticipant;
            })
            .map(eventParticipantRepository::save)
            .map(eventParticipantMapper::toDto);
    }

    /**
     * Get all the eventParticipants.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EventParticipantDTO> findAll() {
        LOG.debug("Request to get all EventParticipants");
        return eventParticipantRepository
            .findAll()
            .stream()
            .map(eventParticipantMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one eventParticipant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EventParticipantDTO> findOne(Long id) {
        LOG.debug("Request to get EventParticipant : {}", id);
        return eventParticipantRepository.findById(id).map(eventParticipantMapper::toDto);
    }

    /**
     * Delete the eventParticipant by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete EventParticipant : {}", id);
        eventParticipantRepository.deleteById(id);
    }
}
