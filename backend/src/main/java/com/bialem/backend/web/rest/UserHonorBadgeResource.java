package com.bialem.backend.web.rest;

import com.bialem.backend.repository.UserHonorBadgeRepository;
import com.bialem.backend.service.UserHonorBadgeService;
import com.bialem.backend.service.dto.UserHonorBadgeDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.UserHonorBadge}.
 */
@RestController
@RequestMapping("/api/user-honor-badges")
public class UserHonorBadgeResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserHonorBadgeResource.class);

    private static final String ENTITY_NAME = "userHonorBadge";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserHonorBadgeService userHonorBadgeService;

    private final UserHonorBadgeRepository userHonorBadgeRepository;

    public UserHonorBadgeResource(UserHonorBadgeService userHonorBadgeService, UserHonorBadgeRepository userHonorBadgeRepository) {
        this.userHonorBadgeService = userHonorBadgeService;
        this.userHonorBadgeRepository = userHonorBadgeRepository;
    }

    /**
     * {@code POST  /user-honor-badges} : Create a new userHonorBadge.
     *
     * @param userHonorBadgeDTO the userHonorBadgeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userHonorBadgeDTO, or with status {@code 400 (Bad Request)} if the userHonorBadge has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserHonorBadgeDTO> createUserHonorBadge(@Valid @RequestBody UserHonorBadgeDTO userHonorBadgeDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save UserHonorBadge : {}", userHonorBadgeDTO);
        if (userHonorBadgeDTO.getId() != null) {
            throw new BadRequestAlertException("A new userHonorBadge cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userHonorBadgeDTO = userHonorBadgeService.save(userHonorBadgeDTO);
        return ResponseEntity.created(new URI("/api/user-honor-badges/" + userHonorBadgeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, userHonorBadgeDTO.getId().toString()))
            .body(userHonorBadgeDTO);
    }

    /**
     * {@code PUT  /user-honor-badges/:id} : Updates an existing userHonorBadge.
     *
     * @param id the id of the userHonorBadgeDTO to save.
     * @param userHonorBadgeDTO the userHonorBadgeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userHonorBadgeDTO,
     * or with status {@code 400 (Bad Request)} if the userHonorBadgeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userHonorBadgeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserHonorBadgeDTO> updateUserHonorBadge(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserHonorBadgeDTO userHonorBadgeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserHonorBadge : {}, {}", id, userHonorBadgeDTO);
        if (userHonorBadgeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userHonorBadgeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userHonorBadgeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userHonorBadgeDTO = userHonorBadgeService.update(userHonorBadgeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userHonorBadgeDTO.getId().toString()))
            .body(userHonorBadgeDTO);
    }

    /**
     * {@code PATCH  /user-honor-badges/:id} : Partial updates given fields of an existing userHonorBadge, field will ignore if it is null
     *
     * @param id the id of the userHonorBadgeDTO to save.
     * @param userHonorBadgeDTO the userHonorBadgeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userHonorBadgeDTO,
     * or with status {@code 400 (Bad Request)} if the userHonorBadgeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userHonorBadgeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userHonorBadgeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserHonorBadgeDTO> partialUpdateUserHonorBadge(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserHonorBadgeDTO userHonorBadgeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserHonorBadge partially : {}, {}", id, userHonorBadgeDTO);
        if (userHonorBadgeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userHonorBadgeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userHonorBadgeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserHonorBadgeDTO> result = userHonorBadgeService.partialUpdate(userHonorBadgeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userHonorBadgeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-honor-badges} : get all the userHonorBadges.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userHonorBadges in body.
     */
    @GetMapping("")
    public List<UserHonorBadgeDTO> getAllUserHonorBadges() {
        LOG.debug("REST request to get all UserHonorBadges");
        return userHonorBadgeService.findAll();
    }

    /**
     * {@code GET  /user-honor-badges/:id} : get the "id" userHonorBadge.
     *
     * @param id the id of the userHonorBadgeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userHonorBadgeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserHonorBadgeDTO> getUserHonorBadge(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserHonorBadge : {}", id);
        Optional<UserHonorBadgeDTO> userHonorBadgeDTO = userHonorBadgeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userHonorBadgeDTO);
    }

    /**
     * {@code DELETE  /user-honor-badges/:id} : delete the "id" userHonorBadge.
     *
     * @param id the id of the userHonorBadgeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserHonorBadge(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserHonorBadge : {}", id);
        userHonorBadgeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
