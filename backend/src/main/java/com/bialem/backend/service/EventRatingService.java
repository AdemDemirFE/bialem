package com.bialem.backend.service;

import com.bialem.backend.domain.EventRating;
import com.bialem.backend.repository.EventRatingRepository;
import com.bialem.backend.service.dto.EventRatingDTO;
import com.bialem.backend.service.mapper.EventRatingMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.EventRating}.
 */
@Service
@Transactional
public class EventRatingService {

    private static final Logger LOG = LoggerFactory.getLogger(EventRatingService.class);

    private final EventRatingRepository eventRatingRepository;

    private final EventRatingMapper eventRatingMapper;

    public EventRatingService(EventRatingRepository eventRatingRepository, EventRatingMapper eventRatingMapper) {
        this.eventRatingRepository = eventRatingRepository;
        this.eventRatingMapper = eventRatingMapper;
    }

    /**
     * Save a eventRating.
     *
     * @param eventRatingDTO the entity to save.
     * @return the persisted entity.
     */
    public EventRatingDTO save(EventRatingDTO eventRatingDTO) {
        LOG.debug("Request to save EventRating : {}", eventRatingDTO);
        EventRating eventRating = eventRatingMapper.toEntity(eventRatingDTO);
        eventRating = eventRatingRepository.save(eventRating);
        return eventRatingMapper.toDto(eventRating);
    }

    /**
     * Update a eventRating.
     *
     * @param eventRatingDTO the entity to save.
     * @return the persisted entity.
     */
    public EventRatingDTO update(EventRatingDTO eventRatingDTO) {
        LOG.debug("Request to update EventRating : {}", eventRatingDTO);
        EventRating eventRating = eventRatingMapper.toEntity(eventRatingDTO);
        eventRating = eventRatingRepository.save(eventRating);
        return eventRatingMapper.toDto(eventRating);
    }

    /**
     * Partially update a eventRating.
     *
     * @param eventRatingDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EventRatingDTO> partialUpdate(EventRatingDTO eventRatingDTO) {
        LOG.debug("Request to partially update EventRating : {}", eventRatingDTO);

        return eventRatingRepository
            .findById(eventRatingDTO.getId())
            .map(existingEventRating -> {
                eventRatingMapper.partialUpdate(existingEventRating, eventRatingDTO);

                return existingEventRating;
            })
            .map(eventRatingRepository::save)
            .map(eventRatingMapper::toDto);
    }

    /**
     * Get all the eventRatings.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EventRatingDTO> findAll() {
        LOG.debug("Request to get all EventRatings");
        return eventRatingRepository.findAll().stream().map(eventRatingMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get event ratings by event id.
     *
     * @param eventId the event id.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EventRatingDTO> findByEventId(Long eventId) {
        LOG.debug("Request to get EventRatings by eventId {}", eventId);
        return eventRatingRepository.findByEvent_IdOrderByCreatedAtDesc(eventId)
            .stream()
            .map(eventRatingMapper::toDto)
            .toList();
    }

    /**
     * Get event ratings by user id.
     *
     * @param userId the user profile id.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EventRatingDTO> findByUserId(Long userId) {
        LOG.debug("Request to get EventRatings by userId {}", userId);
        return eventRatingRepository.findByUser_IdOrderByCreatedAtDesc(userId)
            .stream()
            .map(eventRatingMapper::toDto)
            .toList();
    }

    /**
     * Get one eventRating by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EventRatingDTO> findOne(Long id) {
        LOG.debug("Request to get EventRating : {}", id);
        return eventRatingRepository.findById(id).map(eventRatingMapper::toDto);
    }

    /**
     * Delete the eventRating by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete EventRating : {}", id);
        eventRatingRepository.deleteById(id);
    }
}
