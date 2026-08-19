package com.bialem.backend.web.rest;

import com.bialem.backend.repository.PartnerOfferRedemptionRepository;
import com.bialem.backend.service.PartnerOfferRedemptionService;
import com.bialem.backend.service.dto.PartnerOfferRedemptionDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.PartnerOfferRedemption}.
 */
@RestController
@RequestMapping("/api/partner-offer-redemptions")
public class PartnerOfferRedemptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerOfferRedemptionResource.class);

    private static final String ENTITY_NAME = "partnerOfferRedemption";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PartnerOfferRedemptionService partnerOfferRedemptionService;

    private final PartnerOfferRedemptionRepository partnerOfferRedemptionRepository;

    public PartnerOfferRedemptionResource(
        PartnerOfferRedemptionService partnerOfferRedemptionService,
        PartnerOfferRedemptionRepository partnerOfferRedemptionRepository
    ) {
        this.partnerOfferRedemptionService = partnerOfferRedemptionService;
        this.partnerOfferRedemptionRepository = partnerOfferRedemptionRepository;
    }

    /**
     * {@code POST  /partner-offer-redemptions} : Create a new partnerOfferRedemption.
     *
     * @param partnerOfferRedemptionDTO the partnerOfferRedemptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new partnerOfferRedemptionDTO, or with status {@code 400 (Bad Request)} if the partnerOfferRedemption has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PartnerOfferRedemptionDTO> createPartnerOfferRedemption(
        @Valid @RequestBody PartnerOfferRedemptionDTO partnerOfferRedemptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save PartnerOfferRedemption : {}", partnerOfferRedemptionDTO);
        if (partnerOfferRedemptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new partnerOfferRedemption cannot already have an ID", ENTITY_NAME, "idexists");
        }
        partnerOfferRedemptionDTO = partnerOfferRedemptionService.save(partnerOfferRedemptionDTO);
        return ResponseEntity.created(new URI("/api/partner-offer-redemptions/" + partnerOfferRedemptionDTO.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, partnerOfferRedemptionDTO.getId().toString())
            )
            .body(partnerOfferRedemptionDTO);
    }

    /**
     * {@code PUT  /partner-offer-redemptions/:id} : Updates an existing partnerOfferRedemption.
     *
     * @param id the id of the partnerOfferRedemptionDTO to save.
     * @param partnerOfferRedemptionDTO the partnerOfferRedemptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partnerOfferRedemptionDTO,
     * or with status {@code 400 (Bad Request)} if the partnerOfferRedemptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the partnerOfferRedemptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PartnerOfferRedemptionDTO> updatePartnerOfferRedemption(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PartnerOfferRedemptionDTO partnerOfferRedemptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PartnerOfferRedemption : {}, {}", id, partnerOfferRedemptionDTO);
        if (partnerOfferRedemptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partnerOfferRedemptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partnerOfferRedemptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        partnerOfferRedemptionDTO = partnerOfferRedemptionService.update(partnerOfferRedemptionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partnerOfferRedemptionDTO.getId().toString()))
            .body(partnerOfferRedemptionDTO);
    }

    /**
     * {@code PATCH  /partner-offer-redemptions/:id} : Partial updates given fields of an existing partnerOfferRedemption, field will ignore if it is null
     *
     * @param id the id of the partnerOfferRedemptionDTO to save.
     * @param partnerOfferRedemptionDTO the partnerOfferRedemptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partnerOfferRedemptionDTO,
     * or with status {@code 400 (Bad Request)} if the partnerOfferRedemptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the partnerOfferRedemptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the partnerOfferRedemptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PartnerOfferRedemptionDTO> partialUpdatePartnerOfferRedemption(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PartnerOfferRedemptionDTO partnerOfferRedemptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PartnerOfferRedemption partially : {}, {}", id, partnerOfferRedemptionDTO);
        if (partnerOfferRedemptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partnerOfferRedemptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partnerOfferRedemptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PartnerOfferRedemptionDTO> result = partnerOfferRedemptionService.partialUpdate(partnerOfferRedemptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partnerOfferRedemptionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /partner-offer-redemptions} : get all the partnerOfferRedemptions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of partnerOfferRedemptions in body.
     */
    @GetMapping("")
    public List<PartnerOfferRedemptionDTO> getAllPartnerOfferRedemptions() {
        LOG.debug("REST request to get all PartnerOfferRedemptions");
        return partnerOfferRedemptionService.findAll();
    }

    /**
     * {@code GET  /partner-offer-redemptions/:id} : get the "id" partnerOfferRedemption.
     *
     * @param id the id of the partnerOfferRedemptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the partnerOfferRedemptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PartnerOfferRedemptionDTO> getPartnerOfferRedemption(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PartnerOfferRedemption : {}", id);
        Optional<PartnerOfferRedemptionDTO> partnerOfferRedemptionDTO = partnerOfferRedemptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(partnerOfferRedemptionDTO);
    }

    /**
     * {@code DELETE  /partner-offer-redemptions/:id} : delete the "id" partnerOfferRedemption.
     *
     * @param id the id of the partnerOfferRedemptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartnerOfferRedemption(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PartnerOfferRedemption : {}", id);
        partnerOfferRedemptionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
