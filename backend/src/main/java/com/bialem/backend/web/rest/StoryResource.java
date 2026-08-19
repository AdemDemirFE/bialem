package com.bialem.backend.web.rest;

import com.bialem.backend.repository.StoryRepository;
import com.bialem.backend.service.StoryService;
import com.bialem.backend.service.dto.StoryDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.Story}.
 */
@RestController
@RequestMapping("/api/stories")
public class StoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(StoryResource.class);

    private static final String ENTITY_NAME = "story";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StoryService storyService;

    private final StoryRepository storyRepository;

    public StoryResource(StoryService storyService, StoryRepository storyRepository) {
        this.storyService = storyService;
        this.storyRepository = storyRepository;
    }

    /**
     * {@code POST  /stories} : Create a new story.
     *
     * @param storyDTO the storyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new storyDTO, or with status {@code 400 (Bad Request)} if the story has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StoryDTO> createStory(@Valid @RequestBody StoryDTO storyDTO) throws URISyntaxException {
        LOG.debug("REST request to save Story : {}", storyDTO);
        if (storyDTO.getId() != null) {
            throw new BadRequestAlertException("A new story cannot already have an ID", ENTITY_NAME, "idexists");
        }
        storyDTO = storyService.save(storyDTO);
        return ResponseEntity.created(new URI("/api/stories/" + storyDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, storyDTO.getId().toString()))
            .body(storyDTO);
    }

    /**
     * {@code PUT  /stories/:id} : Updates an existing story.
     *
     * @param id the id of the storyDTO to save.
     * @param storyDTO the storyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storyDTO,
     * or with status {@code 400 (Bad Request)} if the storyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the storyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StoryDTO> updateStory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StoryDTO storyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Story : {}, {}", id, storyDTO);
        if (storyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!storyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        storyDTO = storyService.update(storyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, storyDTO.getId().toString()))
            .body(storyDTO);
    }

    /**
     * {@code PATCH  /stories/:id} : Partial updates given fields of an existing story, field will ignore if it is null
     *
     * @param id the id of the storyDTO to save.
     * @param storyDTO the storyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storyDTO,
     * or with status {@code 400 (Bad Request)} if the storyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the storyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the storyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StoryDTO> partialUpdateStory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StoryDTO storyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Story partially : {}, {}", id, storyDTO);
        if (storyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!storyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StoryDTO> result = storyService.partialUpdate(storyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, storyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stories} : get all the stories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stories in body.
     */
    @GetMapping("")
    public List<StoryDTO> getAllStories() {
        LOG.debug("REST request to get all Stories");
        return storyService.findAll();
    }

    /**
     * {@code GET  /stories/:id} : get the "id" story.
     *
     * @param id the id of the storyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the storyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StoryDTO> getStory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Story : {}", id);
        Optional<StoryDTO> storyDTO = storyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(storyDTO);
    }

    /**
     * {@code DELETE  /stories/:id} : delete the "id" story.
     *
     * @param id the id of the storyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Story : {}", id);
        storyService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
