package com.bialem.backend.web.rest;

import com.bialem.backend.repository.EventMessageRepository;
import com.bialem.backend.service.EventMessageService;
import com.bialem.backend.service.dto.EventMessageDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.EventMessage}.
 */
@RestController
@RequestMapping("/api/event-messages")
public class EventMessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(EventMessageResource.class);

    private static final String ENTITY_NAME = "eventMessage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventMessageService eventMessageService;

    private final EventMessageRepository eventMessageRepository;

    public EventMessageResource(EventMessageService eventMessageService, EventMessageRepository eventMessageRepository) {
        this.eventMessageService = eventMessageService;
        this.eventMessageRepository = eventMessageRepository;
    }

    /**
     * {@code POST  /event-messages} : Create a new eventMessage.
     *
     * @param eventMessageDTO the eventMessageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventMessageDTO, or with status {@code 400 (Bad Request)} if the eventMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EventMessageDTO> createEventMessage(@Valid @RequestBody EventMessageDTO eventMessageDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save EventMessage : {}", eventMessageDTO);
        if (eventMessageDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        eventMessageDTO = eventMessageService.save(eventMessageDTO);
        return ResponseEntity.created(new URI("/api/event-messages/" + eventMessageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, eventMessageDTO.getId().toString()))
            .body(eventMessageDTO);
    }

    /**
     * {@code PUT  /event-messages/:id} : Updates an existing eventMessage.
     *
     * @param id the id of the eventMessageDTO to save.
     * @param eventMessageDTO the eventMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventMessageDTO,
     * or with status {@code 400 (Bad Request)} if the eventMessageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventMessageDTO> updateEventMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EventMessageDTO eventMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EventMessage : {}, {}", id, eventMessageDTO);
        if (eventMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        eventMessageDTO = eventMessageService.update(eventMessageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventMessageDTO.getId().toString()))
            .body(eventMessageDTO);
    }

    /**
     * {@code PATCH  /event-messages/:id} : Partial updates given fields of an existing eventMessage, field will ignore if it is null
     *
     * @param id the id of the eventMessageDTO to save.
     * @param eventMessageDTO the eventMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventMessageDTO,
     * or with status {@code 400 (Bad Request)} if the eventMessageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the eventMessageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the eventMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EventMessageDTO> partialUpdateEventMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EventMessageDTO eventMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EventMessage partially : {}, {}", id, eventMessageDTO);
        if (eventMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EventMessageDTO> result = eventMessageService.partialUpdate(eventMessageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventMessageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /event-messages} : get all the eventMessages.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventMessages in body.
     */
    @GetMapping("")
    public List<EventMessageDTO> getAllEventMessages() {
        LOG.debug("REST request to get all EventMessages");
        return eventMessageService.findAll();
    }

    /**
     * {@code GET  /event-messages/:id} : get the "id" eventMessage.
     *
     * @param id the id of the eventMessageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventMessageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventMessageDTO> getEventMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EventMessage : {}", id);
        Optional<EventMessageDTO> eventMessageDTO = eventMessageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventMessageDTO);
    }

    /**
     * {@code DELETE  /event-messages/:id} : delete the "id" eventMessage.
     *
     * @param id the id of the eventMessageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EventMessage : {}", id);
        eventMessageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
