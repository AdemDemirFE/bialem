package com.bialem.backend.web.rest;

import com.bialem.backend.repository.StoryCommunityTargetRepository;
import com.bialem.backend.service.StoryCommunityTargetService;
import com.bialem.backend.service.dto.StoryCommunityTargetDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.StoryCommunityTarget}.
 */
@RestController
@RequestMapping("/api/story-community-targets")
public class StoryCommunityTargetResource {

    private static final Logger LOG = LoggerFactory.getLogger(StoryCommunityTargetResource.class);

    private static final String ENTITY_NAME = "storyCommunityTarget";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StoryCommunityTargetService storyCommunityTargetService;

    private final StoryCommunityTargetRepository storyCommunityTargetRepository;

    public StoryCommunityTargetResource(
        StoryCommunityTargetService storyCommunityTargetService,
        StoryCommunityTargetRepository storyCommunityTargetRepository
    ) {
        this.storyCommunityTargetService = storyCommunityTargetService;
        this.storyCommunityTargetRepository = storyCommunityTargetRepository;
    }

    /**
     * {@code POST  /story-community-targets} : Create a new storyCommunityTarget.
     *
     * @param storyCommunityTargetDTO the storyCommunityTargetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new storyCommunityTargetDTO, or with status {@code 400 (Bad Request)} if the storyCommunityTarget has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StoryCommunityTargetDTO> createStoryCommunityTarget(
        @Valid @RequestBody StoryCommunityTargetDTO storyCommunityTargetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save StoryCommunityTarget : {}", storyCommunityTargetDTO);
        if (storyCommunityTargetDTO.getId() != null) {
            throw new BadRequestAlertException("A new storyCommunityTarget cannot already have an ID", ENTITY_NAME, "idexists");
        }
        storyCommunityTargetDTO = storyCommunityTargetService.save(storyCommunityTargetDTO);
        return ResponseEntity.created(new URI("/api/story-community-targets/" + storyCommunityTargetDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, storyCommunityTargetDTO.getId().toString()))
            .body(storyCommunityTargetDTO);
    }

    /**
     * {@code PUT  /story-community-targets/:id} : Updates an existing storyCommunityTarget.
     *
     * @param id the id of the storyCommunityTargetDTO to save.
     * @param storyCommunityTargetDTO the storyCommunityTargetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storyCommunityTargetDTO,
     * or with status {@code 400 (Bad Request)} if the storyCommunityTargetDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the storyCommunityTargetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StoryCommunityTargetDTO> updateStoryCommunityTarget(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StoryCommunityTargetDTO storyCommunityTargetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StoryCommunityTarget : {}, {}", id, storyCommunityTargetDTO);
        if (storyCommunityTargetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storyCommunityTargetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!storyCommunityTargetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        storyCommunityTargetDTO = storyCommunityTargetService.update(storyCommunityTargetDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, storyCommunityTargetDTO.getId().toString()))
            .body(storyCommunityTargetDTO);
    }

    /**
     * {@code PATCH  /story-community-targets/:id} : Partial updates given fields of an existing storyCommunityTarget, field will ignore if it is null
     *
     * @param id the id of the storyCommunityTargetDTO to save.
     * @param storyCommunityTargetDTO the storyCommunityTargetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storyCommunityTargetDTO,
     * or with status {@code 400 (Bad Request)} if the storyCommunityTargetDTO is not valid,
     * or with status {@code 404 (Not Found)} if the storyCommunityTargetDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the storyCommunityTargetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StoryCommunityTargetDTO> partialUpdateStoryCommunityTarget(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StoryCommunityTargetDTO storyCommunityTargetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StoryCommunityTarget partially : {}, {}", id, storyCommunityTargetDTO);
        if (storyCommunityTargetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storyCommunityTargetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!storyCommunityTargetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StoryCommunityTargetDTO> result = storyCommunityTargetService.partialUpdate(storyCommunityTargetDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, storyCommunityTargetDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /story-community-targets} : get all the storyCommunityTargets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of storyCommunityTargets in body.
     */
    @GetMapping("")
    public List<StoryCommunityTargetDTO> getAllStoryCommunityTargets() {
        LOG.debug("REST request to get all StoryCommunityTargets");
        return storyCommunityTargetService.findAll();
    }

    /**
     * {@code GET  /story-community-targets/:id} : get the "id" storyCommunityTarget.
     *
     * @param id the id of the storyCommunityTargetDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the storyCommunityTargetDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StoryCommunityTargetDTO> getStoryCommunityTarget(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StoryCommunityTarget : {}", id);
        Optional<StoryCommunityTargetDTO> storyCommunityTargetDTO = storyCommunityTargetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(storyCommunityTargetDTO);
    }

    /**
     * {@code DELETE  /story-community-targets/:id} : delete the "id" storyCommunityTarget.
     *
     * @param id the id of the storyCommunityTargetDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStoryCommunityTarget(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StoryCommunityTarget : {}", id);
        storyCommunityTargetService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
