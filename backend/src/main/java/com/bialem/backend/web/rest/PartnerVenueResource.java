package com.bialem.backend.web.rest;

import com.bialem.backend.repository.PartnerVenueRepository;
import com.bialem.backend.service.PartnerVenueQueryService;
import com.bialem.backend.service.PartnerVenueService;
import com.bialem.backend.service.criteria.PartnerVenueCriteria;
import com.bialem.backend.service.dto.PartnerVenueDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.PartnerVenue}.
 */
@RestController
@RequestMapping("/api/partner-venues")
public class PartnerVenueResource {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerVenueResource.class);

    private static final String ENTITY_NAME = "partnerVenue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PartnerVenueService partnerVenueService;

    private final PartnerVenueRepository partnerVenueRepository;

    private final PartnerVenueQueryService partnerVenueQueryService;

    public PartnerVenueResource(
        PartnerVenueService partnerVenueService,
        PartnerVenueRepository partnerVenueRepository,
        PartnerVenueQueryService partnerVenueQueryService
    ) {
        this.partnerVenueService = partnerVenueService;
        this.partnerVenueRepository = partnerVenueRepository;
        this.partnerVenueQueryService = partnerVenueQueryService;
    }

    /**
     * {@code POST  /partner-venues} : Create a new partnerVenue.
     *
     * @param partnerVenueDTO the partnerVenueDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new partnerVenueDTO, or with status {@code 400 (Bad Request)} if the partnerVenue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PartnerVenueDTO> createPartnerVenue(@Valid @RequestBody PartnerVenueDTO partnerVenueDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PartnerVenue : {}", partnerVenueDTO);
        if (partnerVenueDTO.getId() != null) {
            throw new BadRequestAlertException("A new partnerVenue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        partnerVenueDTO = partnerVenueService.save(partnerVenueDTO);
        return ResponseEntity.created(new URI("/api/partner-venues/" + partnerVenueDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, partnerVenueDTO.getId().toString()))
            .body(partnerVenueDTO);
    }

    /**
     * {@code PUT  /partner-venues/:id} : Updates an existing partnerVenue.
     *
     * @param id the id of the partnerVenueDTO to save.
     * @param partnerVenueDTO the partnerVenueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partnerVenueDTO,
     * or with status {@code 400 (Bad Request)} if the partnerVenueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the partnerVenueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PartnerVenueDTO> updatePartnerVenue(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PartnerVenueDTO partnerVenueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PartnerVenue : {}, {}", id, partnerVenueDTO);
        if (partnerVenueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partnerVenueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partnerVenueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        partnerVenueDTO = partnerVenueService.update(partnerVenueDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partnerVenueDTO.getId().toString()))
            .body(partnerVenueDTO);
    }

    /**
     * {@code PATCH  /partner-venues/:id} : Partial updates given fields of an existing partnerVenue, field will ignore if it is null
     *
     * @param id the id of the partnerVenueDTO to save.
     * @param partnerVenueDTO the partnerVenueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partnerVenueDTO,
     * or with status {@code 400 (Bad Request)} if the partnerVenueDTO is not valid,
     * or with status {@code 404 (Not Found)} if the partnerVenueDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the partnerVenueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PartnerVenueDTO> partialUpdatePartnerVenue(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PartnerVenueDTO partnerVenueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PartnerVenue partially : {}, {}", id, partnerVenueDTO);
        if (partnerVenueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partnerVenueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partnerVenueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PartnerVenueDTO> result = partnerVenueService.partialUpdate(partnerVenueDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partnerVenueDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /partner-venues} : get all the partnerVenues.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of partnerVenues in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PartnerVenueDTO>> getAllPartnerVenues(PartnerVenueCriteria criteria) {
        LOG.debug("REST request to get PartnerVenues by criteria: {}", criteria);

        List<PartnerVenueDTO> entityList = partnerVenueQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /partner-venues/count} : count all the partnerVenues.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPartnerVenues(PartnerVenueCriteria criteria) {
        LOG.debug("REST request to count PartnerVenues by criteria: {}", criteria);
        return ResponseEntity.ok().body(partnerVenueQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /partner-venues/:id} : get the "id" partnerVenue.
     *
     * @param id the id of the partnerVenueDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the partnerVenueDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PartnerVenueDTO> getPartnerVenue(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PartnerVenue : {}", id);
        Optional<PartnerVenueDTO> partnerVenueDTO = partnerVenueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(partnerVenueDTO);
    }

    /**
     * {@code DELETE  /partner-venues/:id} : delete the "id" partnerVenue.
     *
     * @param id the id of the partnerVenueDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartnerVenue(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PartnerVenue : {}", id);
        partnerVenueService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
