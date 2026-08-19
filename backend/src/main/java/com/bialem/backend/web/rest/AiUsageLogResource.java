package com.bialem.backend.web.rest;

import com.bialem.backend.repository.AiUsageLogRepository;
import com.bialem.backend.service.AiUsageLogService;
import com.bialem.backend.service.dto.AiUsageLogDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.AiUsageLog}.
 */
@RestController
@RequestMapping("/api/ai-usage-logs")
public class AiUsageLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(AiUsageLogResource.class);

    private static final String ENTITY_NAME = "aiUsageLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AiUsageLogService aiUsageLogService;

    private final AiUsageLogRepository aiUsageLogRepository;

    public AiUsageLogResource(AiUsageLogService aiUsageLogService, AiUsageLogRepository aiUsageLogRepository) {
        this.aiUsageLogService = aiUsageLogService;
        this.aiUsageLogRepository = aiUsageLogRepository;
    }

    /**
     * {@code POST  /ai-usage-logs} : Create a new aiUsageLog.
     *
     * @param aiUsageLogDTO the aiUsageLogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aiUsageLogDTO, or with status {@code 400 (Bad Request)} if the aiUsageLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AiUsageLogDTO> createAiUsageLog(@Valid @RequestBody AiUsageLogDTO aiUsageLogDTO) throws URISyntaxException {
        LOG.debug("REST request to save AiUsageLog : {}", aiUsageLogDTO);
        if (aiUsageLogDTO.getId() != null) {
            throw new BadRequestAlertException("A new aiUsageLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        aiUsageLogDTO = aiUsageLogService.save(aiUsageLogDTO);
        return ResponseEntity.created(new URI("/api/ai-usage-logs/" + aiUsageLogDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, aiUsageLogDTO.getId().toString()))
            .body(aiUsageLogDTO);
    }

    /**
     * {@code PUT  /ai-usage-logs/:id} : Updates an existing aiUsageLog.
     *
     * @param id the id of the aiUsageLogDTO to save.
     * @param aiUsageLogDTO the aiUsageLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aiUsageLogDTO,
     * or with status {@code 400 (Bad Request)} if the aiUsageLogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aiUsageLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AiUsageLogDTO> updateAiUsageLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AiUsageLogDTO aiUsageLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AiUsageLog : {}, {}", id, aiUsageLogDTO);
        if (aiUsageLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aiUsageLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aiUsageLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        aiUsageLogDTO = aiUsageLogService.update(aiUsageLogDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aiUsageLogDTO.getId().toString()))
            .body(aiUsageLogDTO);
    }

    /**
     * {@code PATCH  /ai-usage-logs/:id} : Partial updates given fields of an existing aiUsageLog, field will ignore if it is null
     *
     * @param id the id of the aiUsageLogDTO to save.
     * @param aiUsageLogDTO the aiUsageLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aiUsageLogDTO,
     * or with status {@code 400 (Bad Request)} if the aiUsageLogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the aiUsageLogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the aiUsageLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AiUsageLogDTO> partialUpdateAiUsageLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AiUsageLogDTO aiUsageLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AiUsageLog partially : {}, {}", id, aiUsageLogDTO);
        if (aiUsageLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aiUsageLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aiUsageLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AiUsageLogDTO> result = aiUsageLogService.partialUpdate(aiUsageLogDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aiUsageLogDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ai-usage-logs} : get all the aiUsageLogs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aiUsageLogs in body.
     */
    @GetMapping("")
    public List<AiUsageLogDTO> getAllAiUsageLogs() {
        LOG.debug("REST request to get all AiUsageLogs");
        return aiUsageLogService.findAll();
    }

    /**
     * {@code GET  /ai-usage-logs/:id} : get the "id" aiUsageLog.
     *
     * @param id the id of the aiUsageLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aiUsageLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AiUsageLogDTO> getAiUsageLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AiUsageLog : {}", id);
        Optional<AiUsageLogDTO> aiUsageLogDTO = aiUsageLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(aiUsageLogDTO);
    }

    /**
     * {@code DELETE  /ai-usage-logs/:id} : delete the "id" aiUsageLog.
     *
     * @param id the id of the aiUsageLogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAiUsageLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AiUsageLog : {}", id);
        aiUsageLogService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
