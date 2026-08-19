package com.bialem.backend.web.rest;

import com.bialem.backend.repository.CityEventTicketOfferRepository;
import com.bialem.backend.service.CityEventTicketOfferService;
import com.bialem.backend.service.dto.CityEventTicketOfferDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.CityEventTicketOffer}.
 */
@RestController
@RequestMapping("/api/city-event-ticket-offers")
public class CityEventTicketOfferResource {

    private static final Logger LOG = LoggerFactory.getLogger(CityEventTicketOfferResource.class);

    private static final String ENTITY_NAME = "cityEventTicketOffer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CityEventTicketOfferService cityEventTicketOfferService;

    private final CityEventTicketOfferRepository cityEventTicketOfferRepository;

    public CityEventTicketOfferResource(
        CityEventTicketOfferService cityEventTicketOfferService,
        CityEventTicketOfferRepository cityEventTicketOfferRepository
    ) {
        this.cityEventTicketOfferService = cityEventTicketOfferService;
        this.cityEventTicketOfferRepository = cityEventTicketOfferRepository;
    }

    /**
     * {@code POST  /city-event-ticket-offers} : Create a new cityEventTicketOffer.
     *
     * @param cityEventTicketOfferDTO the cityEventTicketOfferDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cityEventTicketOfferDTO, or with status {@code 400 (Bad Request)} if the cityEventTicketOffer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CityEventTicketOfferDTO> createCityEventTicketOffer(
        @Valid @RequestBody CityEventTicketOfferDTO cityEventTicketOfferDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save CityEventTicketOffer : {}", cityEventTicketOfferDTO);
        if (cityEventTicketOfferDTO.getId() != null) {
            throw new BadRequestAlertException("A new cityEventTicketOffer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cityEventTicketOfferDTO = cityEventTicketOfferService.save(cityEventTicketOfferDTO);
        return ResponseEntity.created(new URI("/api/city-event-ticket-offers/" + cityEventTicketOfferDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, cityEventTicketOfferDTO.getId().toString()))
            .body(cityEventTicketOfferDTO);
    }

    /**
     * {@code PUT  /city-event-ticket-offers/:id} : Updates an existing cityEventTicketOffer.
     *
     * @param id the id of the cityEventTicketOfferDTO to save.
     * @param cityEventTicketOfferDTO the cityEventTicketOfferDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cityEventTicketOfferDTO,
     * or with status {@code 400 (Bad Request)} if the cityEventTicketOfferDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cityEventTicketOfferDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CityEventTicketOfferDTO> updateCityEventTicketOffer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CityEventTicketOfferDTO cityEventTicketOfferDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CityEventTicketOffer : {}, {}", id, cityEventTicketOfferDTO);
        if (cityEventTicketOfferDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cityEventTicketOfferDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cityEventTicketOfferRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cityEventTicketOfferDTO = cityEventTicketOfferService.update(cityEventTicketOfferDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cityEventTicketOfferDTO.getId().toString()))
            .body(cityEventTicketOfferDTO);
    }

    /**
     * {@code PATCH  /city-event-ticket-offers/:id} : Partial updates given fields of an existing cityEventTicketOffer, field will ignore if it is null
     *
     * @param id the id of the cityEventTicketOfferDTO to save.
     * @param cityEventTicketOfferDTO the cityEventTicketOfferDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cityEventTicketOfferDTO,
     * or with status {@code 400 (Bad Request)} if the cityEventTicketOfferDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cityEventTicketOfferDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cityEventTicketOfferDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CityEventTicketOfferDTO> partialUpdateCityEventTicketOffer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CityEventTicketOfferDTO cityEventTicketOfferDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CityEventTicketOffer partially : {}, {}", id, cityEventTicketOfferDTO);
        if (cityEventTicketOfferDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cityEventTicketOfferDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cityEventTicketOfferRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CityEventTicketOfferDTO> result = cityEventTicketOfferService.partialUpdate(cityEventTicketOfferDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cityEventTicketOfferDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /city-event-ticket-offers} : get all the cityEventTicketOffers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cityEventTicketOffers in body.
     */
    @GetMapping("")
    public List<CityEventTicketOfferDTO> getAllCityEventTicketOffers() {
        LOG.debug("REST request to get all CityEventTicketOffers");
        return cityEventTicketOfferService.findAll();
    }

    /**
     * {@code GET  /city-event-ticket-offers/:id} : get the "id" cityEventTicketOffer.
     *
     * @param id the id of the cityEventTicketOfferDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cityEventTicketOfferDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CityEventTicketOfferDTO> getCityEventTicketOffer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CityEventTicketOffer : {}", id);
        Optional<CityEventTicketOfferDTO> cityEventTicketOfferDTO = cityEventTicketOfferService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cityEventTicketOfferDTO);
    }

    /**
     * {@code DELETE  /city-event-ticket-offers/:id} : delete the "id" cityEventTicketOffer.
     *
     * @param id the id of the cityEventTicketOfferDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCityEventTicketOffer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CityEventTicketOffer : {}", id);
        cityEventTicketOfferService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
