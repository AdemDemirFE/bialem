package com.bialem.backend.web.rest;

import com.bialem.backend.repository.PlatformTeamMemberRepository;
import com.bialem.backend.service.PlatformTeamMemberService;
import com.bialem.backend.service.dto.PlatformTeamMemberDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.PlatformTeamMember}.
 */
@RestController
@RequestMapping("/api/platform-team-members")
public class PlatformTeamMemberResource {

    private static final Logger LOG = LoggerFactory.getLogger(PlatformTeamMemberResource.class);

    private static final String ENTITY_NAME = "platformTeamMember";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlatformTeamMemberService platformTeamMemberService;

    private final PlatformTeamMemberRepository platformTeamMemberRepository;

    public PlatformTeamMemberResource(
        PlatformTeamMemberService platformTeamMemberService,
        PlatformTeamMemberRepository platformTeamMemberRepository
    ) {
        this.platformTeamMemberService = platformTeamMemberService;
        this.platformTeamMemberRepository = platformTeamMemberRepository;
    }

    /**
     * {@code POST  /platform-team-members} : Create a new platformTeamMember.
     *
     * @param platformTeamMemberDTO the platformTeamMemberDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new platformTeamMemberDTO, or with status {@code 400 (Bad Request)} if the platformTeamMember has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PlatformTeamMemberDTO> createPlatformTeamMember(@Valid @RequestBody PlatformTeamMemberDTO platformTeamMemberDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PlatformTeamMember : {}", platformTeamMemberDTO);
        if (platformTeamMemberDTO.getId() != null) {
            throw new BadRequestAlertException("A new platformTeamMember cannot already have an ID", ENTITY_NAME, "idexists");
        }
        platformTeamMemberDTO = platformTeamMemberService.save(platformTeamMemberDTO);
        return ResponseEntity.created(new URI("/api/platform-team-members/" + platformTeamMemberDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, platformTeamMemberDTO.getId().toString()))
            .body(platformTeamMemberDTO);
    }

    /**
     * {@code PUT  /platform-team-members/:id} : Updates an existing platformTeamMember.
     *
     * @param id the id of the platformTeamMemberDTO to save.
     * @param platformTeamMemberDTO the platformTeamMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated platformTeamMemberDTO,
     * or with status {@code 400 (Bad Request)} if the platformTeamMemberDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the platformTeamMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlatformTeamMemberDTO> updatePlatformTeamMember(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PlatformTeamMemberDTO platformTeamMemberDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PlatformTeamMember : {}, {}", id, platformTeamMemberDTO);
        if (platformTeamMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, platformTeamMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!platformTeamMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        platformTeamMemberDTO = platformTeamMemberService.update(platformTeamMemberDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, platformTeamMemberDTO.getId().toString()))
            .body(platformTeamMemberDTO);
    }

    /**
     * {@code PATCH  /platform-team-members/:id} : Partial updates given fields of an existing platformTeamMember, field will ignore if it is null
     *
     * @param id the id of the platformTeamMemberDTO to save.
     * @param platformTeamMemberDTO the platformTeamMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated platformTeamMemberDTO,
     * or with status {@code 400 (Bad Request)} if the platformTeamMemberDTO is not valid,
     * or with status {@code 404 (Not Found)} if the platformTeamMemberDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the platformTeamMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PlatformTeamMemberDTO> partialUpdatePlatformTeamMember(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PlatformTeamMemberDTO platformTeamMemberDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PlatformTeamMember partially : {}, {}", id, platformTeamMemberDTO);
        if (platformTeamMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, platformTeamMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!platformTeamMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PlatformTeamMemberDTO> result = platformTeamMemberService.partialUpdate(platformTeamMemberDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, platformTeamMemberDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /platform-team-members} : get all the platformTeamMembers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of platformTeamMembers in body.
     */
    @GetMapping("")
    public List<PlatformTeamMemberDTO> getAllPlatformTeamMembers() {
        LOG.debug("REST request to get all PlatformTeamMembers");
        return platformTeamMemberService.findAll();
    }

    /**
     * {@code GET  /platform-team-members/:id} : get the "id" platformTeamMember.
     *
     * @param id the id of the platformTeamMemberDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the platformTeamMemberDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlatformTeamMemberDTO> getPlatformTeamMember(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PlatformTeamMember : {}", id);
        Optional<PlatformTeamMemberDTO> platformTeamMemberDTO = platformTeamMemberService.findOne(id);
        return ResponseUtil.wrapOrNotFound(platformTeamMemberDTO);
    }

    /**
     * {@code DELETE  /platform-team-members/:id} : delete the "id" platformTeamMember.
     *
     * @param id the id of the platformTeamMemberDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlatformTeamMember(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PlatformTeamMember : {}", id);
        platformTeamMemberService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
