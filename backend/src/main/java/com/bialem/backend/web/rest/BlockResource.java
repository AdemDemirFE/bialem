package com.bialem.backend.web.rest;

import com.bialem.backend.repository.BlockRepository;
import com.bialem.backend.service.BlockService;
import com.bialem.backend.service.dto.BlockDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.Block}.
 */
@RestController
@RequestMapping("/api/blocks")
public class BlockResource {

    private static final Logger LOG = LoggerFactory.getLogger(BlockResource.class);

    private static final String ENTITY_NAME = "block";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BlockService blockService;

    private final BlockRepository blockRepository;

    public BlockResource(BlockService blockService, BlockRepository blockRepository) {
        this.blockService = blockService;
        this.blockRepository = blockRepository;
    }

    /**
     * {@code POST  /blocks} : Create a new block.
     *
     * @param blockDTO the blockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new blockDTO, or with status {@code 400 (Bad Request)} if the block has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BlockDTO> createBlock(@Valid @RequestBody BlockDTO blockDTO) throws URISyntaxException {
        LOG.debug("REST request to save Block : {}", blockDTO);
        if (blockDTO.getId() != null) {
            throw new BadRequestAlertException("A new block cannot already have an ID", ENTITY_NAME, "idexists");
        }
        blockDTO = blockService.save(blockDTO);
        return ResponseEntity.created(new URI("/api/blocks/" + blockDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, blockDTO.getId().toString()))
            .body(blockDTO);
    }

    /**
     * {@code PUT  /blocks/:id} : Updates an existing block.
     *
     * @param id the id of the blockDTO to save.
     * @param blockDTO the blockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated blockDTO,
     * or with status {@code 400 (Bad Request)} if the blockDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the blockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BlockDTO> updateBlock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BlockDTO blockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Block : {}, {}", id, blockDTO);
        if (blockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, blockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!blockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        blockDTO = blockService.update(blockDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, blockDTO.getId().toString()))
            .body(blockDTO);
    }

    /**
     * {@code PATCH  /blocks/:id} : Partial updates given fields of an existing block, field will ignore if it is null
     *
     * @param id the id of the blockDTO to save.
     * @param blockDTO the blockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated blockDTO,
     * or with status {@code 400 (Bad Request)} if the blockDTO is not valid,
     * or with status {@code 404 (Not Found)} if the blockDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the blockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BlockDTO> partialUpdateBlock(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BlockDTO blockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Block partially : {}, {}", id, blockDTO);
        if (blockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, blockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!blockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BlockDTO> result = blockService.partialUpdate(blockDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, blockDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /blocks} : get all the blocks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of blocks in body.
     */
    @GetMapping("")
    public List<BlockDTO> getAllBlocks() {
        LOG.debug("REST request to get all Blocks");
        return blockService.findAll();
    }

    /**
     * {@code GET  /blocks/:id} : get the "id" block.
     *
     * @param id the id of the blockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the blockDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BlockDTO> getBlock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Block : {}", id);
        Optional<BlockDTO> blockDTO = blockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(blockDTO);
    }

    /**
     * {@code DELETE  /blocks/:id} : delete the "id" block.
     *
     * @param id the id of the blockDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Block : {}", id);
        blockService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
