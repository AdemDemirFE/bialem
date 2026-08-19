package com.bialem.backend.web.rest;

import com.bialem.backend.repository.StoryViewRepository;
import com.bialem.backend.service.StoryViewService;
import com.bialem.backend.service.dto.StoryViewDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.StoryView}.
 */
@RestController
@RequestMapping("/api/story-views")
public class StoryViewResource {

    private static final Logger LOG = LoggerFactory.getLogger(StoryViewResource.class);

    private static final String ENTITY_NAME = "storyView";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StoryViewService storyViewService;

    private final StoryViewRepository storyViewRepository;

    public StoryViewResource(StoryViewService storyViewService, StoryViewRepository storyViewRepository) {
        this.storyViewService = storyViewService;
        this.storyViewRepository = storyViewRepository;
    }

    /**
     * {@code POST  /story-views} : Create a new storyView.
     *
     * @param storyViewDTO the storyViewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new storyViewDTO, or with status {@code 400 (Bad Request)} if the storyView has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StoryViewDTO> createStoryView(@Valid @RequestBody StoryViewDTO storyViewDTO) throws URISyntaxException {
        LOG.debug("REST request to save StoryView : {}", storyViewDTO);
        if (storyViewDTO.getId() != null) {
            throw new BadRequestAlertException("A new storyView cannot already have an ID", ENTITY_NAME, "idexists");
        }
        storyViewDTO = storyViewService.save(storyViewDTO);
        return ResponseEntity.created(new URI("/api/story-views/" + storyViewDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, storyViewDTO.getId().toString()))
            .body(storyViewDTO);
    }

    /**
     * {@code PUT  /story-views/:id} : Updates an existing storyView.
     *
     * @param id the id of the storyViewDTO to save.
     * @param storyViewDTO the storyViewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storyViewDTO,
     * or with status {@code 400 (Bad Request)} if the storyViewDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the storyViewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StoryViewDTO> updateStoryView(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StoryViewDTO storyViewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StoryView : {}, {}", id, storyViewDTO);
        if (storyViewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storyViewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!storyViewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        storyViewDTO = storyViewService.update(storyViewDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, storyViewDTO.getId().toString()))
            .body(storyViewDTO);
    }

    /**
     * {@code PATCH  /story-views/:id} : Partial updates given fields of an existing storyView, field will ignore if it is null
     *
     * @param id the id of the storyViewDTO to save.
     * @param storyViewDTO the storyViewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storyViewDTO,
     * or with status {@code 400 (Bad Request)} if the storyViewDTO is not valid,
     * or with status {@code 404 (Not Found)} if the storyViewDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the storyViewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StoryViewDTO> partialUpdateStoryView(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StoryViewDTO storyViewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StoryView partially : {}, {}", id, storyViewDTO);
        if (storyViewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storyViewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!storyViewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StoryViewDTO> result = storyViewService.partialUpdate(storyViewDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, storyViewDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /story-views} : get all the storyViews.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of storyViews in body.
     */
    @GetMapping("")
    public List<StoryViewDTO> getAllStoryViews() {
        LOG.debug("REST request to get all StoryViews");
        return storyViewService.findAll();
    }

    /**
     * {@code GET  /story-views/:id} : get the "id" storyView.
     *
     * @param id the id of the storyViewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the storyViewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StoryViewDTO> getStoryView(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StoryView : {}", id);
        Optional<StoryViewDTO> storyViewDTO = storyViewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(storyViewDTO);
    }

    /**
     * {@code DELETE  /story-views/:id} : delete the "id" storyView.
     *
     * @param id the id of the storyViewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStoryView(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StoryView : {}", id);
        storyViewService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
