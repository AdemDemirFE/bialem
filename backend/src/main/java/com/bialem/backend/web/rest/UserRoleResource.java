package com.bialem.backend.web.rest;

import com.bialem.backend.repository.UserRoleRepository;
import com.bialem.backend.service.UserRoleService;
import com.bialem.backend.service.dto.UserRoleDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.UserRole}.
 */
@RestController
@RequestMapping("/api/user-roles")
public class UserRoleResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserRoleResource.class);

    private static final String ENTITY_NAME = "userRole";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserRoleService userRoleService;

    private final UserRoleRepository userRoleRepository;

    public UserRoleResource(UserRoleService userRoleService, UserRoleRepository userRoleRepository) {
        this.userRoleService = userRoleService;
        this.userRoleRepository = userRoleRepository;
    }

    @PostMapping("")
    public ResponseEntity<UserRoleDTO> createUserRole(@Valid @RequestBody UserRoleDTO userRoleDTO) throws URISyntaxException {
        LOG.debug("REST request to save UserRole : {}", userRoleDTO);
        if (userRoleDTO.getId() != null) {
            throw new BadRequestAlertException("A new userRole cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userRoleDTO = userRoleService.save(userRoleDTO);
        return ResponseEntity.created(new URI("/api/user-roles/" + userRoleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, userRoleDTO.getId().toString()))
            .body(userRoleDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserRoleDTO> updateUserRole(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserRoleDTO userRoleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserRole : {}, {}", id, userRoleDTO);
        if (userRoleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userRoleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!userRoleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        userRoleDTO = userRoleService.update(userRoleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userRoleDTO.getId().toString()))
            .body(userRoleDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserRoleDTO> partialUpdateUserRole(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserRoleDTO userRoleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserRole : {}, {}", id, userRoleDTO);
        if (userRoleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userRoleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!userRoleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Optional<UserRoleDTO> result = userRoleService.partialUpdate(userRoleDTO);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userRoleDTO.getId().toString())
        );
    }

    @GetMapping("")
    public List<UserRoleDTO> getAllUserRoles() {
        LOG.debug("REST request to get all UserRoles");
        return userRoleService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRoleDTO> getUserRole(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserRole : {}", id);
        return ResponseUtil.wrapOrNotFound(userRoleService.findOne(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserRole(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserRole : {}", id);
        userRoleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
