package com.bialem.backend.web.rest;

import com.bialem.backend.domain.enumeration.RadioContentType;
import com.bialem.backend.repository.RadioContentRepository;
import com.bialem.backend.service.RadioContentService;
import com.bialem.backend.service.dto.RadioContentDTO;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link com.bialem.backend.domain.RadioContent}.
 */
@RestController
@RequestMapping("/api")
public class RadioContentResource {

    private static final Logger LOG = LoggerFactory.getLogger(RadioContentResource.class);

    private static final String ENTITY_NAME = "radioContent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RadioContentService radioContentService;

    private final RadioContentRepository radioContentRepository;

    public RadioContentResource(RadioContentService radioContentService, RadioContentRepository radioContentRepository) {
        this.radioContentService = radioContentService;
        this.radioContentRepository = radioContentRepository;
    }

    /**
     * {@code POST  /radio-contents} : Create a new radioContent.
     */
    @PostMapping("/radio-contents")
    public ResponseEntity<RadioContentDTO> createRadioContent(@Valid @RequestBody RadioContentDTO radioContentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save RadioContent : {}", radioContentDTO);
        if (radioContentDTO.getId() != null) {
            throw new BadRequestAlertException("A new radioContent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        radioContentDTO = radioContentService.save(radioContentDTO);
        return ResponseEntity.created(new URI("/api/radio-contents/" + radioContentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, radioContentDTO.getId().toString()))
            .body(radioContentDTO);
    }

    /**
     * {@code PUT  /radio-contents/:id} : Updates an existing radioContent.
     */
    @PutMapping("/radio-contents/{id}")
    public ResponseEntity<RadioContentDTO> updateRadioContent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RadioContentDTO radioContentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RadioContent : {}, {}", id, radioContentDTO);
        if (radioContentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, radioContentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!radioContentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        radioContentDTO = radioContentService.save(radioContentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, radioContentDTO.getId().toString()))
            .body(radioContentDTO);
    }

    /**
     * {@code PATCH  /radio-contents/:id} : Partial updates given fields of an existing radioContent.
     */
    @PatchMapping(value = "/radio-contents/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RadioContentDTO> partialUpdateRadioContent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RadioContentDTO radioContentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RadioContent partially : {}, {}", id, radioContentDTO);
        if (radioContentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, radioContentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!radioContentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Optional<RadioContentDTO> result = radioContentService.partialUpdate(radioContentDTO);
        return result
            .map(r -> ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, radioContentDTO.getId().toString()))
                .body(r))
            .orElseThrow(() -> new RuntimeException("Could not find RadioContent with id " + id));
    }

    /**
     * {@code GET  /radio-contents} : get all the radioContents.
     */
    @GetMapping("/radio-contents")
    public ResponseEntity<List<RadioContentDTO>> getAllRadioContents() {
        LOG.debug("REST request to get all RadioContents");
        List<RadioContentDTO> list = radioContentService.findAll();
        return ResponseEntity.ok().body(list);
    }

    /**
     * {@code GET  /radio-contents/:id} : get the "id" radioContent.
     */
    @GetMapping("/radio-contents/{id}")
    public ResponseEntity<RadioContentDTO> getRadioContent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RadioContent : {}", id);
        Optional<RadioContentDTO> radioContentDTO = radioContentService.findOne(id);
        return radioContentDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /radio-contents/:id} : delete the "id" radioContent.
     */
    @DeleteMapping("/radio-contents/{id}")
    public ResponseEntity<Void> deleteRadioContent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RadioContent : {}", id);
        radioContentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    // ===== CUSTOM ENDPOINTS =====

    /**
     * {@code GET  /radio-contents/active} : get all active radioContents.
     */
    @GetMapping("/radio-contents/active")
    public ResponseEntity<List<RadioContentDTO>> getActiveRadioContents() {
        return ResponseEntity.ok().body(radioContentService.findAllActive());
    }

    /**
     * {@code GET  /radio-contents/featured} : get all featured active radioContents.
     */
    @GetMapping("/radio-contents/featured")
    public ResponseEntity<List<RadioContentDTO>> getFeaturedRadioContents() {
        return ResponseEntity.ok().body(radioContentService.findAllFeatured());
    }

    /**
     * {@code GET  /radio-contents/by-type/{contentType}} : get active radioContents by content type.
     */
    @GetMapping("/radio-contents/by-type/{contentType}")
    public ResponseEntity<List<RadioContentDTO>> getByContentType(@PathVariable RadioContentType contentType) {
        return ResponseEntity.ok().body(radioContentService.findByContentType(contentType));
    }

    /**
     * {@code GET  /radio-contents/by-category/{category}} : get active radioContents by category.
     */
    @GetMapping("/radio-contents/by-category/{category}")
    public ResponseEntity<List<RadioContentDTO>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok().body(radioContentService.findByCategory(category));
    }

    /**
     * {@code GET  /radio-contents/categories} : get all distinct active categories.
     */
    @GetMapping("/radio-contents/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok().body(radioContentService.findDistinctCategories());
    }

    /**
     * {@code POST  /radio-contents/{id}/play} : increment play count.
     */
    @PostMapping("/radio-contents/{id}/play")
    public ResponseEntity<Void> incrementPlayCount(@PathVariable("id") Long id) {
        radioContentService.incrementPlayCount(id);
        return ResponseEntity.ok().build();
    }
}
