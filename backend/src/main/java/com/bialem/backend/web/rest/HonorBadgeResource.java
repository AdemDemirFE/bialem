package com.bialem.backend.web.rest;

import com.bialem.backend.repository.HonorBadgeRepository;
import com.bialem.backend.service.HonorBadgeService;
import com.bialem.backend.service.dto.HonorBadgeDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.HonorBadge}.
 */
@RestController
@RequestMapping("/api/honor-badges")
public class HonorBadgeResource {

    private static final Logger LOG = LoggerFactory.getLogger(HonorBadgeResource.class);

    private static final String ENTITY_NAME = "honorBadge";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HonorBadgeService honorBadgeService;

    private final HonorBadgeRepository honorBadgeRepository;

    public HonorBadgeResource(HonorBadgeService honorBadgeService, HonorBadgeRepository honorBadgeRepository) {
        this.honorBadgeService = honorBadgeService;
        this.honorBadgeRepository = honorBadgeRepository;
    }

    /**
     * {@code POST  /honor-badges} : Create a new honorBadge.
     *
     * @param honorBadgeDTO the honorBadgeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new honorBadgeDTO, or with status {@code 400 (Bad Request)} if the honorBadge has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<HonorBadgeDTO> createHonorBadge(@Valid @RequestBody HonorBadgeDTO honorBadgeDTO) throws URISyntaxException {
        LOG.debug("REST request to save HonorBadge : {}", honorBadgeDTO);
        if (honorBadgeDTO.getId() != null) {
            throw new BadRequestAlertException("A new honorBadge cannot already have an ID", ENTITY_NAME, "idexists");
        }
        honorBadgeDTO = honorBadgeService.save(honorBadgeDTO);
        return ResponseEntity.created(new URI("/api/honor-badges/" + honorBadgeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, honorBadgeDTO.getId().toString()))
            .body(honorBadgeDTO);
    }

    /**
     * {@code PUT  /honor-badges/:id} : Updates an existing honorBadge.
     *
     * @param id the id of the honorBadgeDTO to save.
     * @param honorBadgeDTO the honorBadgeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated honorBadgeDTO,
     * or with status {@code 400 (Bad Request)} if the honorBadgeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the honorBadgeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HonorBadgeDTO> updateHonorBadge(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HonorBadgeDTO honorBadgeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update HonorBadge : {}, {}", id, honorBadgeDTO);
        if (honorBadgeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, honorBadgeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!honorBadgeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        honorBadgeDTO = honorBadgeService.update(honorBadgeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, honorBadgeDTO.getId().toString()))
            .body(honorBadgeDTO);
    }

    /**
     * {@code PATCH  /honor-badges/:id} : Partial updates given fields of an existing honorBadge, field will ignore if it is null
     *
     * @param id the id of the honorBadgeDTO to save.
     * @param honorBadgeDTO the honorBadgeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated honorBadgeDTO,
     * or with status {@code 400 (Bad Request)} if the honorBadgeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the honorBadgeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the honorBadgeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HonorBadgeDTO> partialUpdateHonorBadge(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HonorBadgeDTO honorBadgeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update HonorBadge partially : {}, {}", id, honorBadgeDTO);
        if (honorBadgeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, honorBadgeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!honorBadgeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HonorBadgeDTO> result = honorBadgeService.partialUpdate(honorBadgeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, honorBadgeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /honor-badges} : get all the honorBadges.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of honorBadges in body.
     */
    @GetMapping("")
    public List<HonorBadgeDTO> getAllHonorBadges() {
        LOG.debug("REST request to get all HonorBadges");
        return honorBadgeService.findAll();
    }

    /**
     * {@code GET  /honor-badges/:id} : get the "id" honorBadge.
     *
     * @param id the id of the honorBadgeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the honorBadgeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HonorBadgeDTO> getHonorBadge(@PathVariable("id") Long id) {
        LOG.debug("REST request to get HonorBadge : {}", id);
        Optional<HonorBadgeDTO> honorBadgeDTO = honorBadgeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(honorBadgeDTO);
    }

    /**
     * {@code DELETE  /honor-badges/:id} : delete the "id" honorBadge.
     *
     * @param id the id of the honorBadgeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHonorBadge(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete HonorBadge : {}", id);
        honorBadgeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
