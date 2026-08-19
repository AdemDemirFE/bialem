package com.bialem.backend.web.rest;

import com.bialem.backend.repository.FollowRequestRepository;
import com.bialem.backend.service.FollowRequestService;
import com.bialem.backend.service.dto.FollowRequestDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.FollowRequest}.
 */
@RestController
@RequestMapping("/api/follow-requests")
public class FollowRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(FollowRequestResource.class);

    private static final String ENTITY_NAME = "followRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FollowRequestService followRequestService;

    private final FollowRequestRepository followRequestRepository;

    public FollowRequestResource(FollowRequestService followRequestService, FollowRequestRepository followRequestRepository) {
        this.followRequestService = followRequestService;
        this.followRequestRepository = followRequestRepository;
    }

    /**
     * {@code POST  /follow-requests} : Create a new followRequest.
     *
     * @param followRequestDTO the followRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new followRequestDTO, or with status {@code 400 (Bad Request)} if the followRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FollowRequestDTO> createFollowRequest(@Valid @RequestBody FollowRequestDTO followRequestDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save FollowRequest : {}", followRequestDTO);
        if (followRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new followRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        followRequestDTO = followRequestService.save(followRequestDTO);
        return ResponseEntity.created(new URI("/api/follow-requests/" + followRequestDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, followRequestDTO.getId().toString()))
            .body(followRequestDTO);
    }

    /**
     * {@code PUT  /follow-requests/:id} : Updates an existing followRequest.
     *
     * @param id the id of the followRequestDTO to save.
     * @param followRequestDTO the followRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated followRequestDTO,
     * or with status {@code 400 (Bad Request)} if the followRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the followRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FollowRequestDTO> updateFollowRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FollowRequestDTO followRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FollowRequest : {}, {}", id, followRequestDTO);
        if (followRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, followRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!followRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        followRequestDTO = followRequestService.update(followRequestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, followRequestDTO.getId().toString()))
            .body(followRequestDTO);
    }

    /**
     * {@code PATCH  /follow-requests/:id} : Partial updates given fields of an existing followRequest, field will ignore if it is null
     *
     * @param id the id of the followRequestDTO to save.
     * @param followRequestDTO the followRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated followRequestDTO,
     * or with status {@code 400 (Bad Request)} if the followRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the followRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the followRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FollowRequestDTO> partialUpdateFollowRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FollowRequestDTO followRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FollowRequest partially : {}, {}", id, followRequestDTO);
        if (followRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, followRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!followRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FollowRequestDTO> result = followRequestService.partialUpdate(followRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, followRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /follow-requests} : get all the followRequests.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of followRequests in body.
     */
    @GetMapping("")
    public List<FollowRequestDTO> getAllFollowRequests() {
        LOG.debug("REST request to get all FollowRequests");
        return followRequestService.findAll();
    }

    /**
     * {@code GET  /follow-requests/:id} : get the "id" followRequest.
     *
     * @param id the id of the followRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the followRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FollowRequestDTO> getFollowRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FollowRequest : {}", id);
        Optional<FollowRequestDTO> followRequestDTO = followRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(followRequestDTO);
    }

    /**
     * {@code DELETE  /follow-requests/:id} : delete the "id" followRequest.
     *
     * @param id the id of the followRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollowRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FollowRequest : {}", id);
        followRequestService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
