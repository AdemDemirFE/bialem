package com.bialem.backend.web.rest;

import com.bialem.backend.repository.CityEventSyncLogRepository;
import com.bialem.backend.service.CityEventSyncLogService;
import com.bialem.backend.service.dto.CityEventSyncLogDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.CityEventSyncLog}.
 */
@RestController
@RequestMapping("/api/city-event-sync-logs")
public class CityEventSyncLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(CityEventSyncLogResource.class);

    private static final String ENTITY_NAME = "cityEventSyncLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CityEventSyncLogService cityEventSyncLogService;

    private final CityEventSyncLogRepository cityEventSyncLogRepository;

    public CityEventSyncLogResource(
        CityEventSyncLogService cityEventSyncLogService,
        CityEventSyncLogRepository cityEventSyncLogRepository
    ) {
        this.cityEventSyncLogService = cityEventSyncLogService;
        this.cityEventSyncLogRepository = cityEventSyncLogRepository;
    }

    /**
     * {@code POST  /city-event-sync-logs} : Create a new cityEventSyncLog.
     *
     * @param cityEventSyncLogDTO the cityEventSyncLogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cityEventSyncLogDTO, or with status {@code 400 (Bad Request)} if the cityEventSyncLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CityEventSyncLogDTO> createCityEventSyncLog(@Valid @RequestBody CityEventSyncLogDTO cityEventSyncLogDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CityEventSyncLog : {}", cityEventSyncLogDTO);
        if (cityEventSyncLogDTO.getId() != null) {
            throw new BadRequestAlertException("A new cityEventSyncLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cityEventSyncLogDTO = cityEventSyncLogService.save(cityEventSyncLogDTO);
        return ResponseEntity.created(new URI("/api/city-event-sync-logs/" + cityEventSyncLogDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, cityEventSyncLogDTO.getId().toString()))
            .body(cityEventSyncLogDTO);
    }

    /**
     * {@code PUT  /city-event-sync-logs/:id} : Updates an existing cityEventSyncLog.
     *
     * @param id the id of the cityEventSyncLogDTO to save.
     * @param cityEventSyncLogDTO the cityEventSyncLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cityEventSyncLogDTO,
     * or with status {@code 400 (Bad Request)} if the cityEventSyncLogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cityEventSyncLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CityEventSyncLogDTO> updateCityEventSyncLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CityEventSyncLogDTO cityEventSyncLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CityEventSyncLog : {}, {}", id, cityEventSyncLogDTO);
        if (cityEventSyncLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cityEventSyncLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cityEventSyncLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cityEventSyncLogDTO = cityEventSyncLogService.update(cityEventSyncLogDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cityEventSyncLogDTO.getId().toString()))
            .body(cityEventSyncLogDTO);
    }

    /**
     * {@code PATCH  /city-event-sync-logs/:id} : Partial updates given fields of an existing cityEventSyncLog, field will ignore if it is null
     *
     * @param id the id of the cityEventSyncLogDTO to save.
     * @param cityEventSyncLogDTO the cityEventSyncLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cityEventSyncLogDTO,
     * or with status {@code 400 (Bad Request)} if the cityEventSyncLogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cityEventSyncLogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cityEventSyncLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CityEventSyncLogDTO> partialUpdateCityEventSyncLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CityEventSyncLogDTO cityEventSyncLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CityEventSyncLog partially : {}, {}", id, cityEventSyncLogDTO);
        if (cityEventSyncLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cityEventSyncLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cityEventSyncLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CityEventSyncLogDTO> result = cityEventSyncLogService.partialUpdate(cityEventSyncLogDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cityEventSyncLogDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /city-event-sync-logs} : get all the cityEventSyncLogs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cityEventSyncLogs in body.
     */
    @GetMapping("")
    public List<CityEventSyncLogDTO> getAllCityEventSyncLogs() {
        LOG.debug("REST request to get all CityEventSyncLogs");
        return cityEventSyncLogService.findAll();
    }

    /**
     * {@code GET  /city-event-sync-logs/:id} : get the "id" cityEventSyncLog.
     *
     * @param id the id of the cityEventSyncLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cityEventSyncLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CityEventSyncLogDTO> getCityEventSyncLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CityEventSyncLog : {}", id);
        Optional<CityEventSyncLogDTO> cityEventSyncLogDTO = cityEventSyncLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cityEventSyncLogDTO);
    }

    /**
     * {@code DELETE  /city-event-sync-logs/:id} : delete the "id" cityEventSyncLog.
     *
     * @param id the id of the cityEventSyncLogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCityEventSyncLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CityEventSyncLog : {}", id);
        cityEventSyncLogService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
