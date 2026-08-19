package com.bialem.backend.web.rest;

import com.bialem.backend.repository.EventRatingRepository;
import com.bialem.backend.service.EventRatingService;
import com.bialem.backend.service.dto.EventRatingDTO;
import com.bialem.backend.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.bialem.backend.domain.EventRating}.
 */
@RestController
@RequestMapping("/api/event-ratings")
public class EventRatingResource {

    private static final Logger LOG = LoggerFactory.getLogger(EventRatingResource.class);

    private static final String ENTITY_NAME = "eventRating";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventRatingService eventRatingService;

    private final EventRatingRepository eventRatingRepository;

    public EventRatingResource(EventRatingService eventRatingService, EventRatingRepository eventRatingRepository) {
        this.eventRatingService = eventRatingService;
        this.eventRatingRepository = eventRatingRepository;
    }

    /**
     * {@code POST  /event-ratings} : Create a new eventRating.
     *
     * @param eventRatingDTO the eventRatingDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventRatingDTO, or with status {@code 400 (Bad Request)} if the eventRating has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EventRatingDTO> createEventRating(@Valid @RequestBody EventRatingDTO eventRatingDTO) throws URISyntaxException {
        LOG.debug("REST request to save EventRating : {}", eventRatingDTO);
        if (eventRatingDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventRating cannot already have an ID", ENTITY_NAME, "idexists");
        }
        eventRatingDTO = eventRatingService.save(eventRatingDTO);
        return ResponseEntity.created(new URI("/api/event-ratings/" + eventRatingDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, eventRatingDTO.getId().toString()))
            .body(eventRatingDTO);
    }

    /**
     * {@code PUT  /event-ratings/:id} : Updates an existing eventRating.
     *
     * @param id the id of the eventRatingDTO to save.
     * @param eventRatingDTO the eventRatingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventRatingDTO,
     * or with status {@code 400 (Bad Request)} if the eventRatingDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventRatingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventRatingDTO> updateEventRating(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EventRatingDTO eventRatingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EventRating : {}, {}", id, eventRatingDTO);
        if (eventRatingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventRatingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventRatingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        eventRatingDTO = eventRatingService.update(eventRatingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventRatingDTO.getId().toString()))
            .body(eventRatingDTO);
    }

    /**
     * {@code PATCH  /event-ratings/:id} : Partial updates given fields of an existing eventRating, field will ignore if it is null
     *
     * @param id the id of the eventRatingDTO to save.
     * @param eventRatingDTO the eventRatingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventRatingDTO,
     * or with status {@code 400 (Bad Request)} if the eventRatingDTO is not valid,
     * or with status {@code 404 (Not Found)} if the eventRatingDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the eventRatingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EventRatingDTO> partialUpdateEventRating(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EventRatingDTO eventRatingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EventRating partially : {}, {}", id, eventRatingDTO);
        if (eventRatingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventRatingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventRatingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EventRatingDTO> result = eventRatingService.partialUpdate(eventRatingDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventRatingDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /event-ratings} : get all the eventRatings.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventRatings in body.
     */
    @GetMapping("")
    public List<EventRatingDTO> getAllEventRatings() {
        LOG.debug("REST request to get all EventRatings");
        return eventRatingService.findAll();
    }

    /**
     * {@code GET  /event-ratings/:id} : get the "id" eventRating.
     *
     * @param id the id of the eventRatingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventRatingDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventRatingDTO> getEventRating(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EventRating : {}", id);
        Optional<EventRatingDTO> eventRatingDTO = eventRatingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventRatingDTO);
    }

    /**
     * {@code DELETE  /event-ratings/:id} : delete the "id" eventRating.
     *
     * @param id the id of the eventRatingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventRating(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EventRating : {}", id);
        eventRatingService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
