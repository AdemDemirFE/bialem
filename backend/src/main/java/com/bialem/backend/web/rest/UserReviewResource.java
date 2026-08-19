package com.bialem.backend.web.rest;

import com.bialem.backend.repository.UserReviewRepository;
import com.bialem.backend.service.UserReviewService;
import com.bialem.backend.service.dto.UserReviewDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.UserReview}.
 */
@RestController
@RequestMapping("/api/user-reviews")
public class UserReviewResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserReviewResource.class);

    private static final String ENTITY_NAME = "userReview";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserReviewService userReviewService;

    private final UserReviewRepository userReviewRepository;

    public UserReviewResource(UserReviewService userReviewService, UserReviewRepository userReviewRepository) {
        this.userReviewService = userReviewService;
        this.userReviewRepository = userReviewRepository;
    }

    /**
     * {@code POST  /user-reviews} : Create a new userReview.
     *
     * @param userReviewDTO the userReviewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userReviewDTO, or with status {@code 400 (Bad Request)} if the userReview has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserReviewDTO> createUserReview(@Valid @RequestBody UserReviewDTO userReviewDTO) throws URISyntaxException {
        LOG.debug("REST request to save UserReview : {}", userReviewDTO);
        if (userReviewDTO.getId() != null) {
            throw new BadRequestAlertException("A new userReview cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userReviewDTO = userReviewService.save(userReviewDTO);
        return ResponseEntity.created(new URI("/api/user-reviews/" + userReviewDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, userReviewDTO.getId().toString()))
            .body(userReviewDTO);
    }

    /**
     * {@code PUT  /user-reviews/:id} : Updates an existing userReview.
     *
     * @param id the id of the userReviewDTO to save.
     * @param userReviewDTO the userReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userReviewDTO,
     * or with status {@code 400 (Bad Request)} if the userReviewDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserReviewDTO> updateUserReview(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserReviewDTO userReviewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserReview : {}, {}", id, userReviewDTO);
        if (userReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userReviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userReviewDTO = userReviewService.update(userReviewDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userReviewDTO.getId().toString()))
            .body(userReviewDTO);
    }

    /**
     * {@code PATCH  /user-reviews/:id} : Partial updates given fields of an existing userReview, field will ignore if it is null
     *
     * @param id the id of the userReviewDTO to save.
     * @param userReviewDTO the userReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userReviewDTO,
     * or with status {@code 400 (Bad Request)} if the userReviewDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userReviewDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserReviewDTO> partialUpdateUserReview(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserReviewDTO userReviewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserReview partially : {}, {}", id, userReviewDTO);
        if (userReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userReviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserReviewDTO> result = userReviewService.partialUpdate(userReviewDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userReviewDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-reviews} : get all the userReviews.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userReviews in body.
     */
    @GetMapping("")
    public List<UserReviewDTO> getAllUserReviews() {
        LOG.debug("REST request to get all UserReviews");
        return userReviewService.findAll();
    }

    /**
     * {@code GET  /user-reviews/:id} : get the "id" userReview.
     *
     * @param id the id of the userReviewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userReviewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserReviewDTO> getUserReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserReview : {}", id);
        Optional<UserReviewDTO> userReviewDTO = userReviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userReviewDTO);
    }

    /**
     * {@code DELETE  /user-reviews/:id} : delete the "id" userReview.
     *
     * @param id the id of the userReviewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserReview : {}", id);
        userReviewService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
