package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.StoryCommunityTargetAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.StoryCommunityTarget;
import com.bialem.backend.repository.StoryCommunityTargetRepository;
import com.bialem.backend.service.dto.StoryCommunityTargetDTO;
import com.bialem.backend.service.mapper.StoryCommunityTargetMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StoryCommunityTargetResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StoryCommunityTargetResourceIT {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/story-community-targets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StoryCommunityTargetRepository storyCommunityTargetRepository;

    @Autowired
    private StoryCommunityTargetMapper storyCommunityTargetMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStoryCommunityTargetMockMvc;

    private StoryCommunityTarget storyCommunityTarget;

    private StoryCommunityTarget insertedStoryCommunityTarget;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StoryCommunityTarget createEntity() {
        return new StoryCommunityTarget().createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StoryCommunityTarget createUpdatedEntity() {
        return new StoryCommunityTarget().createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        storyCommunityTarget = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStoryCommunityTarget != null) {
            storyCommunityTargetRepository.delete(insertedStoryCommunityTarget);
            insertedStoryCommunityTarget = null;
        }
    }

    @Test
    @Transactional
    void createStoryCommunityTarget() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StoryCommunityTarget
        StoryCommunityTargetDTO storyCommunityTargetDTO = storyCommunityTargetMapper.toDto(storyCommunityTarget);
        var returnedStoryCommunityTargetDTO = om.readValue(
            restStoryCommunityTargetMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyCommunityTargetDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StoryCommunityTargetDTO.class
        );

        // Validate the StoryCommunityTarget in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStoryCommunityTarget = storyCommunityTargetMapper.toEntity(returnedStoryCommunityTargetDTO);
        assertStoryCommunityTargetUpdatableFieldsEquals(
            returnedStoryCommunityTarget,
            getPersistedStoryCommunityTarget(returnedStoryCommunityTarget)
        );

        insertedStoryCommunityTarget = returnedStoryCommunityTarget;
    }

    @Test
    @Transactional
    void createStoryCommunityTargetWithExistingId() throws Exception {
        // Create the StoryCommunityTarget with an existing ID
        storyCommunityTarget.setId(1L);
        StoryCommunityTargetDTO storyCommunityTargetDTO = storyCommunityTargetMapper.toDto(storyCommunityTarget);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStoryCommunityTargetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyCommunityTargetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StoryCommunityTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        storyCommunityTarget.setCreatedAt(null);

        // Create the StoryCommunityTarget, which fails.
        StoryCommunityTargetDTO storyCommunityTargetDTO = storyCommunityTargetMapper.toDto(storyCommunityTarget);

        restStoryCommunityTargetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyCommunityTargetDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStoryCommunityTargets() throws Exception {
        // Initialize the database
        insertedStoryCommunityTarget = storyCommunityTargetRepository.saveAndFlush(storyCommunityTarget);

        // Get all the storyCommunityTargetList
        restStoryCommunityTargetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(storyCommunityTarget.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getStoryCommunityTarget() throws Exception {
        // Initialize the database
        insertedStoryCommunityTarget = storyCommunityTargetRepository.saveAndFlush(storyCommunityTarget);

        // Get the storyCommunityTarget
        restStoryCommunityTargetMockMvc
            .perform(get(ENTITY_API_URL_ID, storyCommunityTarget.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(storyCommunityTarget.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingStoryCommunityTarget() throws Exception {
        // Get the storyCommunityTarget
        restStoryCommunityTargetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStoryCommunityTarget() throws Exception {
        // Initialize the database
        insertedStoryCommunityTarget = storyCommunityTargetRepository.saveAndFlush(storyCommunityTarget);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the storyCommunityTarget
        StoryCommunityTarget updatedStoryCommunityTarget = storyCommunityTargetRepository
            .findById(storyCommunityTarget.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedStoryCommunityTarget are not directly saved in db
        em.detach(updatedStoryCommunityTarget);
        updatedStoryCommunityTarget.createdAt(UPDATED_CREATED_AT);
        StoryCommunityTargetDTO storyCommunityTargetDTO = storyCommunityTargetMapper.toDto(updatedStoryCommunityTarget);

        restStoryCommunityTargetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, storyCommunityTargetDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(storyCommunityTargetDTO))
            )
            .andExpect(status().isOk());

        // Validate the StoryCommunityTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStoryCommunityTargetToMatchAllProperties(updatedStoryCommunityTarget);
    }

    @Test
    @Transactional
    void putNonExistingStoryCommunityTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storyCommunityTarget.setId(longCount.incrementAndGet());

        // Create the StoryCommunityTarget
        StoryCommunityTargetDTO storyCommunityTargetDTO = storyCommunityTargetMapper.toDto(storyCommunityTarget);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStoryCommunityTargetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, storyCommunityTargetDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(storyCommunityTargetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StoryCommunityTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStoryCommunityTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storyCommunityTarget.setId(longCount.incrementAndGet());

        // Create the StoryCommunityTarget
        StoryCommunityTargetDTO storyCommunityTargetDTO = storyCommunityTargetMapper.toDto(storyCommunityTarget);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoryCommunityTargetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(storyCommunityTargetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StoryCommunityTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStoryCommunityTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storyCommunityTarget.setId(longCount.incrementAndGet());

        // Create the StoryCommunityTarget
        StoryCommunityTargetDTO storyCommunityTargetDTO = storyCommunityTargetMapper.toDto(storyCommunityTarget);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoryCommunityTargetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyCommunityTargetDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StoryCommunityTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStoryCommunityTargetWithPatch() throws Exception {
        // Initialize the database
        insertedStoryCommunityTarget = storyCommunityTargetRepository.saveAndFlush(storyCommunityTarget);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the storyCommunityTarget using partial update
        StoryCommunityTarget partialUpdatedStoryCommunityTarget = new StoryCommunityTarget();
        partialUpdatedStoryCommunityTarget.setId(storyCommunityTarget.getId());

        restStoryCommunityTargetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStoryCommunityTarget.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStoryCommunityTarget))
            )
            .andExpect(status().isOk());

        // Validate the StoryCommunityTarget in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStoryCommunityTargetUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStoryCommunityTarget, storyCommunityTarget),
            getPersistedStoryCommunityTarget(storyCommunityTarget)
        );
    }

    @Test
    @Transactional
    void fullUpdateStoryCommunityTargetWithPatch() throws Exception {
        // Initialize the database
        insertedStoryCommunityTarget = storyCommunityTargetRepository.saveAndFlush(storyCommunityTarget);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the storyCommunityTarget using partial update
        StoryCommunityTarget partialUpdatedStoryCommunityTarget = new StoryCommunityTarget();
        partialUpdatedStoryCommunityTarget.setId(storyCommunityTarget.getId());

        partialUpdatedStoryCommunityTarget.createdAt(UPDATED_CREATED_AT);

        restStoryCommunityTargetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStoryCommunityTarget.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStoryCommunityTarget))
            )
            .andExpect(status().isOk());

        // Validate the StoryCommunityTarget in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStoryCommunityTargetUpdatableFieldsEquals(
            partialUpdatedStoryCommunityTarget,
            getPersistedStoryCommunityTarget(partialUpdatedStoryCommunityTarget)
        );
    }

    @Test
    @Transactional
    void patchNonExistingStoryCommunityTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storyCommunityTarget.setId(longCount.incrementAndGet());

        // Create the StoryCommunityTarget
        StoryCommunityTargetDTO storyCommunityTargetDTO = storyCommunityTargetMapper.toDto(storyCommunityTarget);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStoryCommunityTargetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, storyCommunityTargetDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(storyCommunityTargetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StoryCommunityTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStoryCommunityTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storyCommunityTarget.setId(longCount.incrementAndGet());

        // Create the StoryCommunityTarget
        StoryCommunityTargetDTO storyCommunityTargetDTO = storyCommunityTargetMapper.toDto(storyCommunityTarget);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoryCommunityTargetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(storyCommunityTargetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StoryCommunityTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStoryCommunityTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storyCommunityTarget.setId(longCount.incrementAndGet());

        // Create the StoryCommunityTarget
        StoryCommunityTargetDTO storyCommunityTargetDTO = storyCommunityTargetMapper.toDto(storyCommunityTarget);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoryCommunityTargetMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(storyCommunityTargetDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StoryCommunityTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStoryCommunityTarget() throws Exception {
        // Initialize the database
        insertedStoryCommunityTarget = storyCommunityTargetRepository.saveAndFlush(storyCommunityTarget);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the storyCommunityTarget
        restStoryCommunityTargetMockMvc
            .perform(delete(ENTITY_API_URL_ID, storyCommunityTarget.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return storyCommunityTargetRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected StoryCommunityTarget getPersistedStoryCommunityTarget(StoryCommunityTarget storyCommunityTarget) {
        return storyCommunityTargetRepository.findById(storyCommunityTarget.getId()).orElseThrow();
    }

    protected void assertPersistedStoryCommunityTargetToMatchAllProperties(StoryCommunityTarget expectedStoryCommunityTarget) {
        assertStoryCommunityTargetAllPropertiesEquals(
            expectedStoryCommunityTarget,
            getPersistedStoryCommunityTarget(expectedStoryCommunityTarget)
        );
    }

    protected void assertPersistedStoryCommunityTargetToMatchUpdatableProperties(StoryCommunityTarget expectedStoryCommunityTarget) {
        assertStoryCommunityTargetAllUpdatablePropertiesEquals(
            expectedStoryCommunityTarget,
            getPersistedStoryCommunityTarget(expectedStoryCommunityTarget)
        );
    }
}
