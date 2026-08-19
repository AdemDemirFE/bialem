package com.bialem.backend.web.rest;

import com.bialem.backend.repository.PartnerOfferRepository;
import com.bialem.backend.service.PartnerOfferService;
import com.bialem.backend.service.dto.PartnerOfferDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.PartnerOffer}.
 */
@RestController
@RequestMapping("/api/partner-offers")
public class PartnerOfferResource {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerOfferResource.class);

    private static final String ENTITY_NAME = "partnerOffer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PartnerOfferService partnerOfferService;

    private final PartnerOfferRepository partnerOfferRepository;

    public PartnerOfferResource(PartnerOfferService partnerOfferService, PartnerOfferRepository partnerOfferRepository) {
        this.partnerOfferService = partnerOfferService;
        this.partnerOfferRepository = partnerOfferRepository;
    }

    /**
     * {@code POST  /partner-offers} : Create a new partnerOffer.
     *
     * @param partnerOfferDTO the partnerOfferDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new partnerOfferDTO, or with status {@code 400 (Bad Request)} if the partnerOffer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PartnerOfferDTO> createPartnerOffer(@Valid @RequestBody PartnerOfferDTO partnerOfferDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PartnerOffer : {}", partnerOfferDTO);
        if (partnerOfferDTO.getId() != null) {
            throw new BadRequestAlertException("A new partnerOffer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        partnerOfferDTO = partnerOfferService.save(partnerOfferDTO);
        return ResponseEntity.created(new URI("/api/partner-offers/" + partnerOfferDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, partnerOfferDTO.getId().toString()))
            .body(partnerOfferDTO);
    }

    /**
     * {@code PUT  /partner-offers/:id} : Updates an existing partnerOffer.
     *
     * @param id the id of the partnerOfferDTO to save.
     * @param partnerOfferDTO the partnerOfferDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partnerOfferDTO,
     * or with status {@code 400 (Bad Request)} if the partnerOfferDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the partnerOfferDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PartnerOfferDTO> updatePartnerOffer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PartnerOfferDTO partnerOfferDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PartnerOffer : {}, {}", id, partnerOfferDTO);
        if (partnerOfferDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partnerOfferDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partnerOfferRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        partnerOfferDTO = partnerOfferService.update(partnerOfferDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partnerOfferDTO.getId().toString()))
            .body(partnerOfferDTO);
    }

    /**
     * {@code PATCH  /partner-offers/:id} : Partial updates given fields of an existing partnerOffer, field will ignore if it is null
     *
     * @param id the id of the partnerOfferDTO to save.
     * @param partnerOfferDTO the partnerOfferDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partnerOfferDTO,
     * or with status {@code 400 (Bad Request)} if the partnerOfferDTO is not valid,
     * or with status {@code 404 (Not Found)} if the partnerOfferDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the partnerOfferDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PartnerOfferDTO> partialUpdatePartnerOffer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PartnerOfferDTO partnerOfferDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PartnerOffer partially : {}, {}", id, partnerOfferDTO);
        if (partnerOfferDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partnerOfferDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partnerOfferRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PartnerOfferDTO> result = partnerOfferService.partialUpdate(partnerOfferDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partnerOfferDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /partner-offers} : get all the partnerOffers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of partnerOffers in body.
     */
    @GetMapping("")
    public List<PartnerOfferDTO> getAllPartnerOffers() {
        LOG.debug("REST request to get all PartnerOffers");
        return partnerOfferService.findAll();
    }

    /**
     * {@code GET  /partner-offers/:id} : get the "id" partnerOffer.
     *
     * @param id the id of the partnerOfferDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the partnerOfferDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PartnerOfferDTO> getPartnerOffer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PartnerOffer : {}", id);
        Optional<PartnerOfferDTO> partnerOfferDTO = partnerOfferService.findOne(id);
        return ResponseUtil.wrapOrNotFound(partnerOfferDTO);
    }

    /**
     * {@code DELETE  /partner-offers/:id} : delete the "id" partnerOffer.
     *
     * @param id the id of the partnerOfferDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartnerOffer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PartnerOffer : {}", id);
        partnerOfferService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
