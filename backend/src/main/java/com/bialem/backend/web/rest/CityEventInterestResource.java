package com.bialem.backend.web.rest;

import com.bialem.backend.repository.CityEventInterestRepository;
import com.bialem.backend.service.CityEventInterestService;
import com.bialem.backend.service.dto.CityEventInterestDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.CityEventInterest}.
 */
@RestController
@RequestMapping("/api/city-event-interests")
public class CityEventInterestResource {

    private static final Logger LOG = LoggerFactory.getLogger(CityEventInterestResource.class);

    private static final String ENTITY_NAME = "cityEventInterest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CityEventInterestService cityEventInterestService;

    private final CityEventInterestRepository cityEventInterestRepository;

    public CityEventInterestResource(
        CityEventInterestService cityEventInterestService,
        CityEventInterestRepository cityEventInterestRepository
    ) {
        this.cityEventInterestService = cityEventInterestService;
        this.cityEventInterestRepository = cityEventInterestRepository;
    }

    /**
     * {@code POST  /city-event-interests} : Create a new cityEventInterest.
     *
     * @param cityEventInterestDTO the cityEventInterestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cityEventInterestDTO, or with status {@code 400 (Bad Request)} if the cityEventInterest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CityEventInterestDTO> createCityEventInterest(@Valid @RequestBody CityEventInterestDTO cityEventInterestDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CityEventInterest : {}", cityEventInterestDTO);
        if (cityEventInterestDTO.getId() != null) {
            throw new BadRequestAlertException("A new cityEventInterest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cityEventInterestDTO = cityEventInterestService.save(cityEventInterestDTO);
        return ResponseEntity.created(new URI("/api/city-event-interests/" + cityEventInterestDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, cityEventInterestDTO.getId().toString()))
            .body(cityEventInterestDTO);
    }

    /**
     * {@code PUT  /city-event-interests/:id} : Updates an existing cityEventInterest.
     *
     * @param id the id of the cityEventInterestDTO to save.
     * @param cityEventInterestDTO the cityEventInterestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cityEventInterestDTO,
     * or with status {@code 400 (Bad Request)} if the cityEventInterestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cityEventInterestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CityEventInterestDTO> updateCityEventInterest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CityEventInterestDTO cityEventInterestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CityEventInterest : {}, {}", id, cityEventInterestDTO);
        if (cityEventInterestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cityEventInterestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cityEventInterestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cityEventInterestDTO = cityEventInterestService.update(cityEventInterestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cityEventInterestDTO.getId().toString()))
            .body(cityEventInterestDTO);
    }

    /**
     * {@code PATCH  /city-event-interests/:id} : Partial updates given fields of an existing cityEventInterest, field will ignore if it is null
     *
     * @param id the id of the cityEventInterestDTO to save.
     * @param cityEventInterestDTO the cityEventInterestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cityEventInterestDTO,
     * or with status {@code 400 (Bad Request)} if the cityEventInterestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cityEventInterestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cityEventInterestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CityEventInterestDTO> partialUpdateCityEventInterest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CityEventInterestDTO cityEventInterestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CityEventInterest partially : {}, {}", id, cityEventInterestDTO);
        if (cityEventInterestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cityEventInterestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cityEventInterestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CityEventInterestDTO> result = cityEventInterestService.partialUpdate(cityEventInterestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cityEventInterestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /city-event-interests} : get all the cityEventInterests.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cityEventInterests in body.
     */
    @GetMapping("")
    public List<CityEventInterestDTO> getAllCityEventInterests() {
        LOG.debug("REST request to get all CityEventInterests");
        return cityEventInterestService.findAll();
    }

    /**
     * {@code GET  /city-event-interests/:id} : get the "id" cityEventInterest.
     *
     * @param id the id of the cityEventInterestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cityEventInterestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CityEventInterestDTO> getCityEventInterest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CityEventInterest : {}", id);
        Optional<CityEventInterestDTO> cityEventInterestDTO = cityEventInterestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cityEventInterestDTO);
    }

    /**
     * {@code DELETE  /city-event-interests/:id} : delete the "id" cityEventInterest.
     *
     * @param id the id of the cityEventInterestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCityEventInterest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CityEventInterest : {}", id);
        cityEventInterestService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
