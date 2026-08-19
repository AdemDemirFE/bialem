package com.bialem.backend.web.rest;

import com.bialem.backend.repository.PostMediaRepository;
import com.bialem.backend.service.PostMediaService;
import com.bialem.backend.service.dto.PostMediaDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.PostMedia}.
 */
@RestController
@RequestMapping("/api/post-medias")
public class PostMediaResource {

    private static final Logger LOG = LoggerFactory.getLogger(PostMediaResource.class);

    private static final String ENTITY_NAME = "postMedia";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostMediaService postMediaService;

    private final PostMediaRepository postMediaRepository;

    public PostMediaResource(PostMediaService postMediaService, PostMediaRepository postMediaRepository) {
        this.postMediaService = postMediaService;
        this.postMediaRepository = postMediaRepository;
    }

    /**
     * {@code POST  /post-medias} : Create a new postMedia.
     *
     * @param postMediaDTO the postMediaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new postMediaDTO, or with status {@code 400 (Bad Request)} if the postMedia has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PostMediaDTO> createPostMedia(@Valid @RequestBody PostMediaDTO postMediaDTO) throws URISyntaxException {
        LOG.debug("REST request to save PostMedia : {}", postMediaDTO);
        if (postMediaDTO.getId() != null) {
            throw new BadRequestAlertException("A new postMedia cannot already have an ID", ENTITY_NAME, "idexists");
        }
        postMediaDTO = postMediaService.save(postMediaDTO);
        return ResponseEntity.created(new URI("/api/post-medias/" + postMediaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, postMediaDTO.getId().toString()))
            .body(postMediaDTO);
    }

    /**
     * {@code PUT  /post-medias/:id} : Updates an existing postMedia.
     *
     * @param id the id of the postMediaDTO to save.
     * @param postMediaDTO the postMediaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postMediaDTO,
     * or with status {@code 400 (Bad Request)} if the postMediaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the postMediaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostMediaDTO> updatePostMedia(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PostMediaDTO postMediaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PostMedia : {}, {}", id, postMediaDTO);
        if (postMediaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postMediaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postMediaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        postMediaDTO = postMediaService.update(postMediaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, postMediaDTO.getId().toString()))
            .body(postMediaDTO);
    }

    /**
     * {@code PATCH  /post-medias/:id} : Partial updates given fields of an existing postMedia, field will ignore if it is null
     *
     * @param id the id of the postMediaDTO to save.
     * @param postMediaDTO the postMediaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postMediaDTO,
     * or with status {@code 400 (Bad Request)} if the postMediaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the postMediaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the postMediaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PostMediaDTO> partialUpdatePostMedia(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PostMediaDTO postMediaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PostMedia partially : {}, {}", id, postMediaDTO);
        if (postMediaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postMediaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postMediaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PostMediaDTO> result = postMediaService.partialUpdate(postMediaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, postMediaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /post-medias} : get all the postMedias.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of postMedias in body.
     */
    @GetMapping("")
    public List<PostMediaDTO> getAllPostMedias() {
        LOG.debug("REST request to get all PostMedias");
        return postMediaService.findAll();
    }

    /**
     * {@code GET  /post-medias/:id} : get the "id" postMedia.
     *
     * @param id the id of the postMediaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the postMediaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostMediaDTO> getPostMedia(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PostMedia : {}", id);
        Optional<PostMediaDTO> postMediaDTO = postMediaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(postMediaDTO);
    }

    /**
     * {@code DELETE  /post-medias/:id} : delete the "id" postMedia.
     *
     * @param id the id of the postMediaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostMedia(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PostMedia : {}", id);
        postMediaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
