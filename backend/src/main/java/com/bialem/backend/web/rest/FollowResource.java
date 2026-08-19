package com.bialem.backend.web.rest;

import com.bialem.backend.repository.FollowRepository;
import com.bialem.backend.service.FollowService;
import com.bialem.backend.service.dto.FollowDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.Follow}.
 */
@RestController
@RequestMapping("/api/follows")
public class FollowResource {

    private static final Logger LOG = LoggerFactory.getLogger(FollowResource.class);

    private static final String ENTITY_NAME = "follow";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FollowService followService;

    private final FollowRepository followRepository;

    public FollowResource(FollowService followService, FollowRepository followRepository) {
        this.followService = followService;
        this.followRepository = followRepository;
    }

    /**
     * {@code POST  /follows} : Create a new follow.
     *
     * @param followDTO the followDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new followDTO, or with status {@code 400 (Bad Request)} if the follow has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FollowDTO> createFollow(@Valid @RequestBody FollowDTO followDTO) throws URISyntaxException {
        LOG.debug("REST request to save Follow : {}", followDTO);
        if (followDTO.getId() != null) {
            throw new BadRequestAlertException("A new follow cannot already have an ID", ENTITY_NAME, "idexists");
        }
        followDTO = followService.save(followDTO);
        return ResponseEntity.created(new URI("/api/follows/" + followDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, followDTO.getId().toString()))
            .body(followDTO);
    }

    /**
     * {@code PUT  /follows/:id} : Updates an existing follow.
     *
     * @param id the id of the followDTO to save.
     * @param followDTO the followDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated followDTO,
     * or with status {@code 400 (Bad Request)} if the followDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the followDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FollowDTO> updateFollow(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FollowDTO followDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Follow : {}, {}", id, followDTO);
        if (followDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, followDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!followRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        followDTO = followService.update(followDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, followDTO.getId().toString()))
            .body(followDTO);
    }

    /**
     * {@code PATCH  /follows/:id} : Partial updates given fields of an existing follow, field will ignore if it is null
     *
     * @param id the id of the followDTO to save.
     * @param followDTO the followDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated followDTO,
     * or with status {@code 400 (Bad Request)} if the followDTO is not valid,
     * or with status {@code 404 (Not Found)} if the followDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the followDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FollowDTO> partialUpdateFollow(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FollowDTO followDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Follow partially : {}, {}", id, followDTO);
        if (followDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, followDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!followRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FollowDTO> result = followService.partialUpdate(followDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, followDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /follows} : get all the follows.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of follows in body.
     */
    @GetMapping("")
    public List<FollowDTO> getAllFollows() {
        LOG.debug("REST request to get all Follows");
        return followService.findAll();
    }

    /**
     * {@code GET  /follows/:id} : get the "id" follow.
     *
     * @param id the id of the followDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the followDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FollowDTO> getFollow(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Follow : {}", id);
        Optional<FollowDTO> followDTO = followService.findOne(id);
        return ResponseUtil.wrapOrNotFound(followDTO);
    }

    /**
     * {@code DELETE  /follows/:id} : delete the "id" follow.
     *
     * @param id the id of the followDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollow(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Follow : {}", id);
        followService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
