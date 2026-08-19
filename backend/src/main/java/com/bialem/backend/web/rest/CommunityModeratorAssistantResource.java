package com.bialem.backend.web.rest;

import com.bialem.backend.repository.CommunityModeratorAssistantRepository;
import com.bialem.backend.service.CommunityModeratorAssistantService;
import com.bialem.backend.service.dto.CommunityModeratorAssistantDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.CommunityModeratorAssistant}.
 */
@RestController
@RequestMapping("/api/community-moderator-assistants")
public class CommunityModeratorAssistantResource {

    private static final Logger LOG = LoggerFactory.getLogger(CommunityModeratorAssistantResource.class);

    private static final String ENTITY_NAME = "communityModeratorAssistant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommunityModeratorAssistantService communityModeratorAssistantService;

    private final CommunityModeratorAssistantRepository communityModeratorAssistantRepository;

    public CommunityModeratorAssistantResource(
        CommunityModeratorAssistantService communityModeratorAssistantService,
        CommunityModeratorAssistantRepository communityModeratorAssistantRepository
    ) {
        this.communityModeratorAssistantService = communityModeratorAssistantService;
        this.communityModeratorAssistantRepository = communityModeratorAssistantRepository;
    }

    /**
     * {@code POST  /community-moderator-assistants} : Create a new communityModeratorAssistant.
     *
     * @param communityModeratorAssistantDTO the communityModeratorAssistantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new communityModeratorAssistantDTO, or with status {@code 400 (Bad Request)} if the communityModeratorAssistant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CommunityModeratorAssistantDTO> createCommunityModeratorAssistant(
        @Valid @RequestBody CommunityModeratorAssistantDTO communityModeratorAssistantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save CommunityModeratorAssistant : {}", communityModeratorAssistantDTO);
        if (communityModeratorAssistantDTO.getId() != null) {
            throw new BadRequestAlertException("A new communityModeratorAssistant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        communityModeratorAssistantDTO = communityModeratorAssistantService.save(communityModeratorAssistantDTO);
        return ResponseEntity.created(new URI("/api/community-moderator-assistants/" + communityModeratorAssistantDTO.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, communityModeratorAssistantDTO.getId().toString())
            )
            .body(communityModeratorAssistantDTO);
    }

    /**
     * {@code PUT  /community-moderator-assistants/:id} : Updates an existing communityModeratorAssistant.
     *
     * @param id the id of the communityModeratorAssistantDTO to save.
     * @param communityModeratorAssistantDTO the communityModeratorAssistantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated communityModeratorAssistantDTO,
     * or with status {@code 400 (Bad Request)} if the communityModeratorAssistantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the communityModeratorAssistantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommunityModeratorAssistantDTO> updateCommunityModeratorAssistant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CommunityModeratorAssistantDTO communityModeratorAssistantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CommunityModeratorAssistant : {}, {}", id, communityModeratorAssistantDTO);
        if (communityModeratorAssistantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, communityModeratorAssistantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!communityModeratorAssistantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        communityModeratorAssistantDTO = communityModeratorAssistantService.update(communityModeratorAssistantDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, communityModeratorAssistantDTO.getId().toString())
            )
            .body(communityModeratorAssistantDTO);
    }

    /**
     * {@code PATCH  /community-moderator-assistants/:id} : Partial updates given fields of an existing communityModeratorAssistant, field will ignore if it is null
     *
     * @param id the id of the communityModeratorAssistantDTO to save.
     * @param communityModeratorAssistantDTO the communityModeratorAssistantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated communityModeratorAssistantDTO,
     * or with status {@code 400 (Bad Request)} if the communityModeratorAssistantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the communityModeratorAssistantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the communityModeratorAssistantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CommunityModeratorAssistantDTO> partialUpdateCommunityModeratorAssistant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CommunityModeratorAssistantDTO communityModeratorAssistantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CommunityModeratorAssistant partially : {}, {}", id, communityModeratorAssistantDTO);
        if (communityModeratorAssistantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, communityModeratorAssistantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!communityModeratorAssistantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CommunityModeratorAssistantDTO> result = communityModeratorAssistantService.partialUpdate(communityModeratorAssistantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, communityModeratorAssistantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /community-moderator-assistants} : get all the communityModeratorAssistants.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of communityModeratorAssistants in body.
     */
    @GetMapping("")
    public List<CommunityModeratorAssistantDTO> getAllCommunityModeratorAssistants() {
        LOG.debug("REST request to get all CommunityModeratorAssistants");
        return communityModeratorAssistantService.findAll();
    }

    /**
     * {@code GET  /community-moderator-assistants/:id} : get the "id" communityModeratorAssistant.
     *
     * @param id the id of the communityModeratorAssistantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the communityModeratorAssistantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommunityModeratorAssistantDTO> getCommunityModeratorAssistant(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CommunityModeratorAssistant : {}", id);
        Optional<CommunityModeratorAssistantDTO> communityModeratorAssistantDTO = communityModeratorAssistantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(communityModeratorAssistantDTO);
    }

    /**
     * {@code DELETE  /community-moderator-assistants/:id} : delete the "id" communityModeratorAssistant.
     *
     * @param id the id of the communityModeratorAssistantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunityModeratorAssistant(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CommunityModeratorAssistant : {}", id);
        communityModeratorAssistantService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
