package com.bialem.backend.web.rest;

import com.bialem.backend.repository.PartnerVenueStaffRepository;
import com.bialem.backend.service.PartnerVenueStaffService;
import com.bialem.backend.service.dto.PartnerVenueStaffDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.PartnerVenueStaff}.
 */
@RestController
@RequestMapping("/api/partner-venue-staffs")
public class PartnerVenueStaffResource {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerVenueStaffResource.class);

    private static final String ENTITY_NAME = "partnerVenueStaff";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PartnerVenueStaffService partnerVenueStaffService;

    private final PartnerVenueStaffRepository partnerVenueStaffRepository;

    public PartnerVenueStaffResource(
        PartnerVenueStaffService partnerVenueStaffService,
        PartnerVenueStaffRepository partnerVenueStaffRepository
    ) {
        this.partnerVenueStaffService = partnerVenueStaffService;
        this.partnerVenueStaffRepository = partnerVenueStaffRepository;
    }

    /**
     * {@code POST  /partner-venue-staffs} : Create a new partnerVenueStaff.
     *
     * @param partnerVenueStaffDTO the partnerVenueStaffDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new partnerVenueStaffDTO, or with status {@code 400 (Bad Request)} if the partnerVenueStaff has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PartnerVenueStaffDTO> createPartnerVenueStaff(@Valid @RequestBody PartnerVenueStaffDTO partnerVenueStaffDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PartnerVenueStaff : {}", partnerVenueStaffDTO);
        if (partnerVenueStaffDTO.getId() != null) {
            throw new BadRequestAlertException("A new partnerVenueStaff cannot already have an ID", ENTITY_NAME, "idexists");
        }
        partnerVenueStaffDTO = partnerVenueStaffService.save(partnerVenueStaffDTO);
        return ResponseEntity.created(new URI("/api/partner-venue-staffs/" + partnerVenueStaffDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, partnerVenueStaffDTO.getId().toString()))
            .body(partnerVenueStaffDTO);
    }

    /**
     * {@code PUT  /partner-venue-staffs/:id} : Updates an existing partnerVenueStaff.
     *
     * @param id the id of the partnerVenueStaffDTO to save.
     * @param partnerVenueStaffDTO the partnerVenueStaffDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partnerVenueStaffDTO,
     * or with status {@code 400 (Bad Request)} if the partnerVenueStaffDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the partnerVenueStaffDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PartnerVenueStaffDTO> updatePartnerVenueStaff(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PartnerVenueStaffDTO partnerVenueStaffDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PartnerVenueStaff : {}, {}", id, partnerVenueStaffDTO);
        if (partnerVenueStaffDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partnerVenueStaffDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partnerVenueStaffRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        partnerVenueStaffDTO = partnerVenueStaffService.update(partnerVenueStaffDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partnerVenueStaffDTO.getId().toString()))
            .body(partnerVenueStaffDTO);
    }

    /**
     * {@code PATCH  /partner-venue-staffs/:id} : Partial updates given fields of an existing partnerVenueStaff, field will ignore if it is null
     *
     * @param id the id of the partnerVenueStaffDTO to save.
     * @param partnerVenueStaffDTO the partnerVenueStaffDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partnerVenueStaffDTO,
     * or with status {@code 400 (Bad Request)} if the partnerVenueStaffDTO is not valid,
     * or with status {@code 404 (Not Found)} if the partnerVenueStaffDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the partnerVenueStaffDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PartnerVenueStaffDTO> partialUpdatePartnerVenueStaff(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PartnerVenueStaffDTO partnerVenueStaffDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PartnerVenueStaff partially : {}, {}", id, partnerVenueStaffDTO);
        if (partnerVenueStaffDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partnerVenueStaffDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partnerVenueStaffRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PartnerVenueStaffDTO> result = partnerVenueStaffService.partialUpdate(partnerVenueStaffDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partnerVenueStaffDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /partner-venue-staffs} : get all the partnerVenueStaffs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of partnerVenueStaffs in body.
     */
    @GetMapping("")
    public List<PartnerVenueStaffDTO> getAllPartnerVenueStaffs() {
        LOG.debug("REST request to get all PartnerVenueStaffs");
        return partnerVenueStaffService.findAll();
    }

    /**
     * {@code GET  /partner-venue-staffs/:id} : get the "id" partnerVenueStaff.
     *
     * @param id the id of the partnerVenueStaffDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the partnerVenueStaffDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PartnerVenueStaffDTO> getPartnerVenueStaff(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PartnerVenueStaff : {}", id);
        Optional<PartnerVenueStaffDTO> partnerVenueStaffDTO = partnerVenueStaffService.findOne(id);
        return ResponseUtil.wrapOrNotFound(partnerVenueStaffDTO);
    }

    /**
     * {@code DELETE  /partner-venue-staffs/:id} : delete the "id" partnerVenueStaff.
     *
     * @param id the id of the partnerVenueStaffDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartnerVenueStaff(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PartnerVenueStaff : {}", id);
        partnerVenueStaffService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
