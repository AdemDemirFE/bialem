package com.bialem.backend.web.rest;

import com.bialem.backend.repository.RadioConfigRepository;
import com.bialem.backend.service.RadioConfigService;
import com.bialem.backend.service.dto.RadioConfigDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.RadioConfig}.
 */
@RestController
@RequestMapping("/api")
public class RadioConfigResource {

    private static final Logger LOG = LoggerFactory.getLogger(RadioConfigResource.class);

    private static final String ENTITY_NAME = "radioConfig";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RadioConfigService radioConfigService;

    private final RadioConfigRepository radioConfigRepository;

    public RadioConfigResource(RadioConfigService radioConfigService, RadioConfigRepository radioConfigRepository) {
        this.radioConfigService = radioConfigService;
        this.radioConfigRepository = radioConfigRepository;
    }

    /**
     * {@code POST  /radio-configs} : Create a new radioConfig.
     */
    @PostMapping("/radio-configs")
    public ResponseEntity<RadioConfigDTO> createRadioConfig(@Valid @RequestBody RadioConfigDTO radioConfigDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save RadioConfig : {}", radioConfigDTO);
        if (radioConfigDTO.getId() != null) {
            throw new BadRequestAlertException("A new radioConfig cannot already have an ID", ENTITY_NAME, "idexists");
        }
        radioConfigDTO = radioConfigService.save(radioConfigDTO);
        return ResponseEntity.created(new URI("/api/radio-configs/" + radioConfigDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, radioConfigDTO.getId().toString()))
            .body(radioConfigDTO);
    }

    /**
     * {@code PUT  /radio-configs/:id} : Updates an existing radioConfig.
     */
    @PutMapping("/radio-configs/{id}")
    public ResponseEntity<RadioConfigDTO> updateRadioConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RadioConfigDTO radioConfigDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RadioConfig : {}, {}", id, radioConfigDTO);
        if (radioConfigDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, radioConfigDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!radioConfigRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        radioConfigDTO = radioConfigService.save(radioConfigDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, radioConfigDTO.getId().toString()))
            .body(radioConfigDTO);
    }

    /**
     * {@code GET  /radio-configs} : get all radioConfigs.
     */
    @GetMapping("/radio-configs")
    public ResponseEntity<List<RadioConfigDTO>> getAllRadioConfigs() {
        LOG.debug("REST request to get all RadioConfigs");
        return ResponseEntity.ok().body(radioConfigService.findAll());
    }

    /**
     * {@code GET  /radio-configs/:id} : get the "id" radioConfig.
     */
    @GetMapping("/radio-configs/{id}")
    public ResponseEntity<RadioConfigDTO> getRadioConfig(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RadioConfig : {}", id);
        Optional<RadioConfigDTO> radioConfigDTO = radioConfigService.findOne(id);
        return radioConfigDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /radio-configs/:id} : delete the "id" radioConfig.
     */
    @DeleteMapping("/radio-configs/{id}")
    public ResponseEntity<Void> deleteRadioConfig(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RadioConfig : {}", id);
        radioConfigService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    // ===== CUSTOM ENDPOINTS =====

    /**
     * {@code GET  /radio-configs/latest} : get the latest radio config (station info).
     * This is a public endpoint for any client (mobile, web) that needs station metadata.
     */
    @GetMapping("/radio-configs/latest")
    public ResponseEntity<RadioConfigDTO> getLatestRadioConfig() {
        Optional<RadioConfigDTO> config = radioConfigService.findLatest();
        return config.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
