package com.bialem.backend.web.rest;

import com.bialem.backend.repository.EventParticipantRepository;
import com.bialem.backend.service.EventParticipantService;
import com.bialem.backend.service.dto.EventParticipantDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.EventParticipant}.
 */
@RestController
@RequestMapping("/api/event-participants")
public class EventParticipantResource {

    private static final Logger LOG = LoggerFactory.getLogger(EventParticipantResource.class);

    private static final String ENTITY_NAME = "eventParticipant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventParticipantService eventParticipantService;

    private final EventParticipantRepository eventParticipantRepository;

    public EventParticipantResource(
        EventParticipantService eventParticipantService,
        EventParticipantRepository eventParticipantRepository
    ) {
        this.eventParticipantService = eventParticipantService;
        this.eventParticipantRepository = eventParticipantRepository;
    }

    /**
     * {@code POST  /event-participants} : Create a new eventParticipant.
     *
     * @param eventParticipantDTO the eventParticipantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventParticipantDTO, or with status {@code 400 (Bad Request)} if the eventParticipant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EventParticipantDTO> createEventParticipant(@Valid @RequestBody EventParticipantDTO eventParticipantDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save EventParticipant : {}", eventParticipantDTO);
        if (eventParticipantDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventParticipant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        eventParticipantDTO = eventParticipantService.save(eventParticipantDTO);
        return ResponseEntity.created(new URI("/api/event-participants/" + eventParticipantDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, eventParticipantDTO.getId().toString()))
            .body(eventParticipantDTO);
    }

    /**
     * {@code PUT  /event-participants/:id} : Updates an existing eventParticipant.
     *
     * @param id the id of the eventParticipantDTO to save.
     * @param eventParticipantDTO the eventParticipantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventParticipantDTO,
     * or with status {@code 400 (Bad Request)} if the eventParticipantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventParticipantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventParticipantDTO> updateEventParticipant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EventParticipantDTO eventParticipantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EventParticipant : {}, {}", id, eventParticipantDTO);
        if (eventParticipantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventParticipantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventParticipantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        eventParticipantDTO = eventParticipantService.update(eventParticipantDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventParticipantDTO.getId().toString()))
            .body(eventParticipantDTO);
    }

    /**
     * {@code PATCH  /event-participants/:id} : Partial updates given fields of an existing eventParticipant, field will ignore if it is null
     *
     * @param id the id of the eventParticipantDTO to save.
     * @param eventParticipantDTO the eventParticipantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventParticipantDTO,
     * or with status {@code 400 (Bad Request)} if the eventParticipantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the eventParticipantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the eventParticipantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EventParticipantDTO> partialUpdateEventParticipant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EventParticipantDTO eventParticipantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EventParticipant partially : {}, {}", id, eventParticipantDTO);
        if (eventParticipantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventParticipantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventParticipantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EventParticipantDTO> result = eventParticipantService.partialUpdate(eventParticipantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventParticipantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /event-participants} : get all the eventParticipants.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventParticipants in body.
     */
    @GetMapping("")
    public List<EventParticipantDTO> getAllEventParticipants() {
        LOG.debug("REST request to get all EventParticipants");
        return eventParticipantService.findAll();
    }

    /**
     * {@code GET  /event-participants/:id} : get the "id" eventParticipant.
     *
     * @param id the id of the eventParticipantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventParticipantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventParticipantDTO> getEventParticipant(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EventParticipant : {}", id);
        Optional<EventParticipantDTO> eventParticipantDTO = eventParticipantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventParticipantDTO);
    }

    /**
     * {@code DELETE  /event-participants/:id} : delete the "id" eventParticipant.
     *
     * @param id the id of the eventParticipantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventParticipant(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EventParticipant : {}", id);
        eventParticipantService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
