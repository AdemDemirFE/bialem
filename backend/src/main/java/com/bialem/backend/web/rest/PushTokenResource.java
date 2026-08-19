package com.bialem.backend.web.rest;

import com.bialem.backend.repository.PushTokenRepository;
import com.bialem.backend.service.PushTokenService;
import com.bialem.backend.service.dto.PushTokenDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.PushToken}.
 */
@RestController
@RequestMapping("/api/push-tokens")
public class PushTokenResource {

    private static final Logger LOG = LoggerFactory.getLogger(PushTokenResource.class);

    private static final String ENTITY_NAME = "pushToken";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PushTokenService pushTokenService;

    private final PushTokenRepository pushTokenRepository;

    public PushTokenResource(PushTokenService pushTokenService, PushTokenRepository pushTokenRepository) {
        this.pushTokenService = pushTokenService;
        this.pushTokenRepository = pushTokenRepository;
    }

    /**
     * {@code POST  /push-tokens} : Create a new pushToken.
     *
     * @param pushTokenDTO the pushTokenDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pushTokenDTO, or with status {@code 400 (Bad Request)} if the pushToken has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PushTokenDTO> createPushToken(@Valid @RequestBody PushTokenDTO pushTokenDTO) throws URISyntaxException {
        LOG.debug("REST request to save PushToken : {}", pushTokenDTO);
        if (pushTokenDTO.getId() != null) {
            throw new BadRequestAlertException("A new pushToken cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pushTokenDTO = pushTokenService.save(pushTokenDTO);
        return ResponseEntity.created(new URI("/api/push-tokens/" + pushTokenDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, pushTokenDTO.getId().toString()))
            .body(pushTokenDTO);
    }

    /**
     * {@code PUT  /push-tokens/:id} : Updates an existing pushToken.
     *
     * @param id the id of the pushTokenDTO to save.
     * @param pushTokenDTO the pushTokenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pushTokenDTO,
     * or with status {@code 400 (Bad Request)} if the pushTokenDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pushTokenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PushTokenDTO> updatePushToken(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PushTokenDTO pushTokenDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PushToken : {}, {}", id, pushTokenDTO);
        if (pushTokenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pushTokenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pushTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pushTokenDTO = pushTokenService.update(pushTokenDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pushTokenDTO.getId().toString()))
            .body(pushTokenDTO);
    }

    /**
     * {@code PATCH  /push-tokens/:id} : Partial updates given fields of an existing pushToken, field will ignore if it is null
     *
     * @param id the id of the pushTokenDTO to save.
     * @param pushTokenDTO the pushTokenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pushTokenDTO,
     * or with status {@code 400 (Bad Request)} if the pushTokenDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pushTokenDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pushTokenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PushTokenDTO> partialUpdatePushToken(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PushTokenDTO pushTokenDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PushToken partially : {}, {}", id, pushTokenDTO);
        if (pushTokenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pushTokenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pushTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PushTokenDTO> result = pushTokenService.partialUpdate(pushTokenDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pushTokenDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /push-tokens} : get all the pushTokens.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pushTokens in body.
     */
    @GetMapping("")
    public List<PushTokenDTO> getAllPushTokens() {
        LOG.debug("REST request to get all PushTokens");
        return pushTokenService.findAll();
    }

    /**
     * {@code GET  /push-tokens/:id} : get the "id" pushToken.
     *
     * @param id the id of the pushTokenDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pushTokenDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PushTokenDTO> getPushToken(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PushToken : {}", id);
        Optional<PushTokenDTO> pushTokenDTO = pushTokenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pushTokenDTO);
    }

    /**
     * {@code DELETE  /push-tokens/:id} : delete the "id" pushToken.
     *
     * @param id the id of the pushTokenDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePushToken(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PushToken : {}", id);
        pushTokenService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
