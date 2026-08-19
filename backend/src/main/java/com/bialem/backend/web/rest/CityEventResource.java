package com.bialem.backend.web.rest;

import com.bialem.backend.repository.CityEventRepository;
import com.bialem.backend.service.CityEventService;
import com.bialem.backend.service.dto.CityEventDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.bialem.backend.domain.CityEvent}.
 */
@RestController
@RequestMapping("/api/city-events")
public class CityEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(CityEventResource.class);

    private static final String ENTITY_NAME = "cityEvent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CityEventService cityEventService;

    private final CityEventRepository cityEventRepository;

    public CityEventResource(CityEventService cityEventService, CityEventRepository cityEventRepository) {
        this.cityEventService = cityEventService;
        this.cityEventRepository = cityEventRepository;
    }

    /**
     * {@code POST  /city-events} : Create a new cityEvent.
     *
     * @param cityEventDTO the cityEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cityEventDTO, or with status {@code 400 (Bad Request)} if the cityEvent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CityEventDTO> createCityEvent(@Valid @RequestBody CityEventDTO cityEventDTO) throws URISyntaxException {
        LOG.debug("REST request to save CityEvent : {}", cityEventDTO);
        if (cityEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new cityEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cityEventDTO = cityEventService.save(cityEventDTO);
        return ResponseEntity.created(new URI("/api/city-events/" + cityEventDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, cityEventDTO.getId().toString()))
            .body(cityEventDTO);
    }

    /**
     * {@code PUT  /city-events/:id} : Updates an existing cityEvent.
     *
     * @param id the id of the cityEventDTO to save.
     * @param cityEventDTO the cityEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cityEventDTO,
     * or with status {@code 400 (Bad Request)} if the cityEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cityEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CityEventDTO> updateCityEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CityEventDTO cityEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CityEvent : {}, {}", id, cityEventDTO);
        if (cityEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cityEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cityEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cityEventDTO = cityEventService.update(cityEventDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cityEventDTO.getId().toString()))
            .body(cityEventDTO);
    }

    /**
     * {@code PATCH  /city-events/:id} : Partial updates given fields of an existing cityEvent, field will ignore if it is null
     *
     * @param id the id of the cityEventDTO to save.
     * @param cityEventDTO the cityEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cityEventDTO,
     * or with status {@code 400 (Bad Request)} if the cityEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cityEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cityEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CityEventDTO> partialUpdateCityEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CityEventDTO cityEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CityEvent partially : {}, {}", id, cityEventDTO);
        if (cityEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cityEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cityEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CityEventDTO> result = cityEventService.partialUpdate(cityEventDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cityEventDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /city-events} : get all the cityEvents.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cityEvents in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CityEventDTO>> getAllCityEvents(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of CityEvents");
        Page<CityEventDTO> page = cityEventService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /city-events/:id} : get the "id" cityEvent.
     *
     * @param id the id of the cityEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cityEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CityEventDTO> getCityEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CityEvent : {}", id);
        Optional<CityEventDTO> cityEventDTO = cityEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cityEventDTO);
    }

    /**
     * {@code DELETE  /city-events/:id} : delete the "id" cityEvent.
     *
     * @param id the id of the cityEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCityEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CityEvent : {}", id);
        cityEventService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
