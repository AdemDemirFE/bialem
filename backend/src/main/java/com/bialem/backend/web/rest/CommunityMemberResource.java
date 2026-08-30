package com.bialem.backend.web.rest;

import com.bialem.backend.repository.CommunityMemberRepository;
import com.bialem.backend.service.CommunityMemberService;
import com.bialem.backend.service.dto.CommunityMemberDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.CommunityMember}.
 */
@RestController
@RequestMapping("/api/community-members")
public class CommunityMemberResource {

    private static final Logger LOG = LoggerFactory.getLogger(CommunityMemberResource.class);

    private static final String ENTITY_NAME = "communityMember";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommunityMemberService communityMemberService;

    private final CommunityMemberRepository communityMemberRepository;

    public CommunityMemberResource(CommunityMemberService communityMemberService, CommunityMemberRepository communityMemberRepository) {
        this.communityMemberService = communityMemberService;
        this.communityMemberRepository = communityMemberRepository;
    }

    /**
     * {@code POST  /community-members} : Create a new communityMember.
     *
     * @param communityMemberDTO the communityMemberDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new communityMemberDTO, or with status {@code 400 (Bad Request)} if the communityMember has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CommunityMemberDTO> createCommunityMember(@Valid @RequestBody CommunityMemberDTO communityMemberDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CommunityMember : {}", communityMemberDTO);
        if (communityMemberDTO.getId() != null) {
            throw new BadRequestAlertException("A new communityMember cannot already have an ID", ENTITY_NAME, "idexists");
        }
        communityMemberDTO = communityMemberService.save(communityMemberDTO);
        return ResponseEntity.created(new URI("/api/community-members/" + communityMemberDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, communityMemberDTO.getId().toString()))
            .body(communityMemberDTO);
    }

    /**
     * {@code PUT  /community-members/:id} : Updates an existing communityMember.
     *
     * @param id the id of the communityMemberDTO to save.
     * @param communityMemberDTO the communityMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated communityMemberDTO,
     * or with status {@code 400 (Bad Request)} if the communityMemberDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the communityMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommunityMemberDTO> updateCommunityMember(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CommunityMemberDTO communityMemberDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CommunityMember : {}, {}", id, communityMemberDTO);
        if (communityMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, communityMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!communityMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        communityMemberDTO = communityMemberService.update(communityMemberDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, communityMemberDTO.getId().toString()))
            .body(communityMemberDTO);
    }

    /**
     * {@code PATCH  /community-members/:id} : Partial updates given fields of an existing communityMember, field will ignore if it is null
     *
     * @param id the id of the communityMemberDTO to save.
     * @param communityMemberDTO the communityMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated communityMemberDTO,
     * or with status {@code 400 (Bad Request)} if the communityMemberDTO is not valid,
     * or with status {@code 404 (Not Found)} if the communityMemberDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the communityMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CommunityMemberDTO> partialUpdateCommunityMember(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CommunityMemberDTO communityMemberDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CommunityMember partially : {}, {}", id, communityMemberDTO);
        if (communityMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, communityMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!communityMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CommunityMemberDTO> result = communityMemberService.partialUpdate(communityMemberDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, communityMemberDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /community-members} : get all the communityMembers.
     *
     * @param userId optional filter by user profile id.
     * @param communityId optional filter by community id.
     * @param status optional filter by membership status.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of communityMembers in body.
     */
    @GetMapping("")
    public List<CommunityMemberDTO> getAllCommunityMembers(
        @RequestParam(name = "userId", required = false) Long userId,
        @RequestParam(name = "communityId", required = false) Long communityId,
        @RequestParam(name = "status", required = false) CommunityMemberStatus status
    ) {
        LOG.debug("REST request to get all CommunityMembers by userId {}, communityId {}, status {}", userId, communityId, status);
        return communityMemberService.findAll(userId, communityId, status);
    }

    /**
     * {@code GET  /community-members/:id} : get the "id" communityMember.
     *
     * @param id the id of the communityMemberDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the communityMemberDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommunityMemberDTO> getCommunityMember(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CommunityMember : {}", id);
        Optional<CommunityMemberDTO> communityMemberDTO = communityMemberService.findOne(id);
        return ResponseUtil.wrapOrNotFound(communityMemberDTO);
    }

    /**
     * {@code DELETE  /community-members/:id} : delete the "id" communityMember.
     *
     * @param id the id of the communityMemberDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunityMember(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CommunityMember : {}", id);
        communityMemberService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
