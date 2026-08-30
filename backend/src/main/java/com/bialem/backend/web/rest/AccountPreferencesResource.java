package com.bialem.backend.web.rest;

import com.bialem.backend.repository.AccountPreferencesRepository;
import com.bialem.backend.service.AccountPreferencesService;
import com.bialem.backend.service.dto.AccountPreferencesDTO;
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
 * REST controller for managing {@link com.bialem.backend.domain.AccountPreferences}.
 */
@RestController
@RequestMapping("/api/account-preferences")
public class AccountPreferencesResource {

    private static final Logger LOG = LoggerFactory.getLogger(AccountPreferencesResource.class);

    private static final String ENTITY_NAME = "accountPreferences";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AccountPreferencesService accountPreferencesService;

    private final AccountPreferencesRepository accountPreferencesRepository;

    public AccountPreferencesResource(
        AccountPreferencesService accountPreferencesService,
        AccountPreferencesRepository accountPreferencesRepository
    ) {
        this.accountPreferencesService = accountPreferencesService;
        this.accountPreferencesRepository = accountPreferencesRepository;
    }

    /**
     * {@code POST  /account-preferences} : Create a new accountPreferences.
     *
     * @param accountPreferencesDTO the accountPreferencesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new accountPreferencesDTO, or with status {@code 400 (Bad Request)} if the accountPreferences has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AccountPreferencesDTO> createAccountPreferences(@Valid @RequestBody AccountPreferencesDTO accountPreferencesDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AccountPreferences : {}", accountPreferencesDTO);
        if (accountPreferencesDTO.getId() != null) {
            throw new BadRequestAlertException("A new accountPreferences cannot already have an ID", ENTITY_NAME, "idexists");
        }
        accountPreferencesDTO = accountPreferencesService.save(accountPreferencesDTO);
        return ResponseEntity.created(new URI("/api/account-preferences/" + accountPreferencesDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, accountPreferencesDTO.getId().toString()))
            .body(accountPreferencesDTO);
    }

    /**
     * {@code PUT  /account-preferences/:id} : Updates an existing accountPreferences.
     *
     * @param id the id of the accountPreferencesDTO to save.
     * @param accountPreferencesDTO the accountPreferencesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountPreferencesDTO,
     * or with status {@code 400 (Bad Request)} if the accountPreferencesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the accountPreferencesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AccountPreferencesDTO> updateAccountPreferences(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AccountPreferencesDTO accountPreferencesDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AccountPreferences : {}, {}", id, accountPreferencesDTO);
        if (accountPreferencesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountPreferencesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountPreferencesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        accountPreferencesDTO = accountPreferencesService.update(accountPreferencesDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, accountPreferencesDTO.getId().toString()))
            .body(accountPreferencesDTO);
    }

    /**
     * {@code PATCH  /account-preferences/:id} : Partial updates given fields of an existing accountPreferences, field will ignore if it is null
     *
     * @param id the id of the accountPreferencesDTO to save.
     * @param accountPreferencesDTO the accountPreferencesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountPreferencesDTO,
     * or with status {@code 400 (Bad Request)} if the accountPreferencesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the accountPreferencesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the accountPreferencesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AccountPreferencesDTO> partialUpdateAccountPreferences(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AccountPreferencesDTO accountPreferencesDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AccountPreferences partially : {}, {}", id, accountPreferencesDTO);
        if (accountPreferencesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountPreferencesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountPreferencesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AccountPreferencesDTO> result = accountPreferencesService.partialUpdate(accountPreferencesDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, accountPreferencesDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /account-preferences} : get all the accountPreferences.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of accountPreferences in body.
     */
    @GetMapping("")
    public List<AccountPreferencesDTO> getAllAccountPreferences() {
        LOG.debug("REST request to get all AccountPreferences");
        return accountPreferencesService.findAll();
    }

    /**
     * {@code GET  /account-preferences/by-profile/:profileId} : get accountPreferences by profile id.
     *
     * @param profileId the profile id of the accountPreferencesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the accountPreferencesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/by-profile/{profileId}")
    public ResponseEntity<AccountPreferencesDTO> getAccountPreferencesByProfile(@PathVariable("profileId") Long profileId) {
        LOG.debug("REST request to get AccountPreferences by profileId : {}", profileId);
        Optional<AccountPreferencesDTO> accountPreferencesDTO = accountPreferencesService.findByProfileId(profileId);
        return ResponseUtil.wrapOrNotFound(accountPreferencesDTO);
    }

    /**
     * {@code GET  /account-preferences/:id} : get the "id" accountPreferences.
     *
     * @param id the id of the accountPreferencesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the accountPreferencesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountPreferencesDTO> getAccountPreferences(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AccountPreferences : {}", id);
        Optional<AccountPreferencesDTO> accountPreferencesDTO = accountPreferencesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(accountPreferencesDTO);
    }

    /**
     * {@code DELETE  /account-preferences/:id} : delete the "id" accountPreferences.
     *
     * @param id the id of the accountPreferencesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountPreferences(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AccountPreferences : {}", id);
        accountPreferencesService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
