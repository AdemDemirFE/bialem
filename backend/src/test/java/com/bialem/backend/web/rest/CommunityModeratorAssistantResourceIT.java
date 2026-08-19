package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.CommunityModeratorAssistantAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.CommunityModeratorAssistant;
import com.bialem.backend.repository.CommunityModeratorAssistantRepository;
import com.bialem.backend.service.dto.CommunityModeratorAssistantDTO;
import com.bialem.backend.service.mapper.CommunityModeratorAssistantMapper;
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
 * Integration tests for the {@link CommunityModeratorAssistantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CommunityModeratorAssistantResourceIT {

    private static final Boolean DEFAULT_CAN_MANAGE_GROUPS = false;
    private static final Boolean UPDATED_CAN_MANAGE_GROUPS = true;

    private static final Boolean DEFAULT_CAN_REVIEW_EVENTS = false;
    private static final Boolean UPDATED_CAN_REVIEW_EVENTS = true;

    private static final Boolean DEFAULT_CAN_MANAGE_PARTICIPANTS = false;
    private static final Boolean UPDATED_CAN_MANAGE_PARTICIPANTS = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/community-moderator-assistants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CommunityModeratorAssistantRepository communityModeratorAssistantRepository;

    @Autowired
    private CommunityModeratorAssistantMapper communityModeratorAssistantMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommunityModeratorAssistantMockMvc;

    private CommunityModeratorAssistant communityModeratorAssistant;

    private CommunityModeratorAssistant insertedCommunityModeratorAssistant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommunityModeratorAssistant createEntity() {
        return new CommunityModeratorAssistant()
            .canManageGroups(DEFAULT_CAN_MANAGE_GROUPS)
            .canReviewEvents(DEFAULT_CAN_REVIEW_EVENTS)
            .canManageParticipants(DEFAULT_CAN_MANAGE_PARTICIPANTS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommunityModeratorAssistant createUpdatedEntity() {
        return new CommunityModeratorAssistant()
            .canManageGroups(UPDATED_CAN_MANAGE_GROUPS)
            .canReviewEvents(UPDATED_CAN_REVIEW_EVENTS)
            .canManageParticipants(UPDATED_CAN_MANAGE_PARTICIPANTS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        communityModeratorAssistant = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCommunityModeratorAssistant != null) {
            communityModeratorAssistantRepository.delete(insertedCommunityModeratorAssistant);
            insertedCommunityModeratorAssistant = null;
        }
    }

    @Test
    @Transactional
    void createCommunityModeratorAssistant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CommunityModeratorAssistant
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            communityModeratorAssistant
        );
        var returnedCommunityModeratorAssistantDTO = om.readValue(
            restCommunityModeratorAssistantMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(communityModeratorAssistantDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CommunityModeratorAssistantDTO.class
        );

        // Validate the CommunityModeratorAssistant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCommunityModeratorAssistant = communityModeratorAssistantMapper.toEntity(returnedCommunityModeratorAssistantDTO);
        assertCommunityModeratorAssistantUpdatableFieldsEquals(
            returnedCommunityModeratorAssistant,
            getPersistedCommunityModeratorAssistant(returnedCommunityModeratorAssistant)
        );

        insertedCommunityModeratorAssistant = returnedCommunityModeratorAssistant;
    }

    @Test
    @Transactional
    void createCommunityModeratorAssistantWithExistingId() throws Exception {
        // Create the CommunityModeratorAssistant with an existing ID
        communityModeratorAssistant.setId(1L);
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            communityModeratorAssistant
        );

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommunityModeratorAssistantMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityModeratorAssistantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityModeratorAssistant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCanManageGroupsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        communityModeratorAssistant.setCanManageGroups(null);

        // Create the CommunityModeratorAssistant, which fails.
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            communityModeratorAssistant
        );

        restCommunityModeratorAssistantMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityModeratorAssistantDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCanReviewEventsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        communityModeratorAssistant.setCanReviewEvents(null);

        // Create the CommunityModeratorAssistant, which fails.
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            communityModeratorAssistant
        );

        restCommunityModeratorAssistantMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityModeratorAssistantDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCanManageParticipantsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        communityModeratorAssistant.setCanManageParticipants(null);

        // Create the CommunityModeratorAssistant, which fails.
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            communityModeratorAssistant
        );

        restCommunityModeratorAssistantMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityModeratorAssistantDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        communityModeratorAssistant.setCreatedAt(null);

        // Create the CommunityModeratorAssistant, which fails.
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            communityModeratorAssistant
        );

        restCommunityModeratorAssistantMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityModeratorAssistantDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        communityModeratorAssistant.setUpdatedAt(null);

        // Create the CommunityModeratorAssistant, which fails.
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            communityModeratorAssistant
        );

        restCommunityModeratorAssistantMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityModeratorAssistantDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCommunityModeratorAssistants() throws Exception {
        // Initialize the database
        insertedCommunityModeratorAssistant = communityModeratorAssistantRepository.saveAndFlush(communityModeratorAssistant);

        // Get all the communityModeratorAssistantList
        restCommunityModeratorAssistantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(communityModeratorAssistant.getId().intValue())))
            .andExpect(jsonPath("$.[*].canManageGroups").value(hasItem(DEFAULT_CAN_MANAGE_GROUPS)))
            .andExpect(jsonPath("$.[*].canReviewEvents").value(hasItem(DEFAULT_CAN_REVIEW_EVENTS)))
            .andExpect(jsonPath("$.[*].canManageParticipants").value(hasItem(DEFAULT_CAN_MANAGE_PARTICIPANTS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCommunityModeratorAssistant() throws Exception {
        // Initialize the database
        insertedCommunityModeratorAssistant = communityModeratorAssistantRepository.saveAndFlush(communityModeratorAssistant);

        // Get the communityModeratorAssistant
        restCommunityModeratorAssistantMockMvc
            .perform(get(ENTITY_API_URL_ID, communityModeratorAssistant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(communityModeratorAssistant.getId().intValue()))
            .andExpect(jsonPath("$.canManageGroups").value(DEFAULT_CAN_MANAGE_GROUPS))
            .andExpect(jsonPath("$.canReviewEvents").value(DEFAULT_CAN_REVIEW_EVENTS))
            .andExpect(jsonPath("$.canManageParticipants").value(DEFAULT_CAN_MANAGE_PARTICIPANTS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCommunityModeratorAssistant() throws Exception {
        // Get the communityModeratorAssistant
        restCommunityModeratorAssistantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCommunityModeratorAssistant() throws Exception {
        // Initialize the database
        insertedCommunityModeratorAssistant = communityModeratorAssistantRepository.saveAndFlush(communityModeratorAssistant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the communityModeratorAssistant
        CommunityModeratorAssistant updatedCommunityModeratorAssistant = communityModeratorAssistantRepository
            .findById(communityModeratorAssistant.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedCommunityModeratorAssistant are not directly saved in db
        em.detach(updatedCommunityModeratorAssistant);
        updatedCommunityModeratorAssistant
            .canManageGroups(UPDATED_CAN_MANAGE_GROUPS)
            .canReviewEvents(UPDATED_CAN_REVIEW_EVENTS)
            .canManageParticipants(UPDATED_CAN_MANAGE_PARTICIPANTS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            updatedCommunityModeratorAssistant
        );

        restCommunityModeratorAssistantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, communityModeratorAssistantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(communityModeratorAssistantDTO))
            )
            .andExpect(status().isOk());

        // Validate the CommunityModeratorAssistant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCommunityModeratorAssistantToMatchAllProperties(updatedCommunityModeratorAssistant);
    }

    @Test
    @Transactional
    void putNonExistingCommunityModeratorAssistant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityModeratorAssistant.setId(longCount.incrementAndGet());

        // Create the CommunityModeratorAssistant
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            communityModeratorAssistant
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommunityModeratorAssistantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, communityModeratorAssistantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(communityModeratorAssistantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityModeratorAssistant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCommunityModeratorAssistant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityModeratorAssistant.setId(longCount.incrementAndGet());

        // Create the CommunityModeratorAssistant
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            communityModeratorAssistant
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityModeratorAssistantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(communityModeratorAssistantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityModeratorAssistant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCommunityModeratorAssistant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityModeratorAssistant.setId(longCount.incrementAndGet());

        // Create the CommunityModeratorAssistant
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            communityModeratorAssistant
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityModeratorAssistantMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityModeratorAssistantDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CommunityModeratorAssistant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommunityModeratorAssistantWithPatch() throws Exception {
        // Initialize the database
        insertedCommunityModeratorAssistant = communityModeratorAssistantRepository.saveAndFlush(communityModeratorAssistant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the communityModeratorAssistant using partial update
        CommunityModeratorAssistant partialUpdatedCommunityModeratorAssistant = new CommunityModeratorAssistant();
        partialUpdatedCommunityModeratorAssistant.setId(communityModeratorAssistant.getId());

        partialUpdatedCommunityModeratorAssistant.createdAt(UPDATED_CREATED_AT);

        restCommunityModeratorAssistantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommunityModeratorAssistant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCommunityModeratorAssistant))
            )
            .andExpect(status().isOk());

        // Validate the CommunityModeratorAssistant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommunityModeratorAssistantUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCommunityModeratorAssistant, communityModeratorAssistant),
            getPersistedCommunityModeratorAssistant(communityModeratorAssistant)
        );
    }

    @Test
    @Transactional
    void fullUpdateCommunityModeratorAssistantWithPatch() throws Exception {
        // Initialize the database
        insertedCommunityModeratorAssistant = communityModeratorAssistantRepository.saveAndFlush(communityModeratorAssistant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the communityModeratorAssistant using partial update
        CommunityModeratorAssistant partialUpdatedCommunityModeratorAssistant = new CommunityModeratorAssistant();
        partialUpdatedCommunityModeratorAssistant.setId(communityModeratorAssistant.getId());

        partialUpdatedCommunityModeratorAssistant
            .canManageGroups(UPDATED_CAN_MANAGE_GROUPS)
            .canReviewEvents(UPDATED_CAN_REVIEW_EVENTS)
            .canManageParticipants(UPDATED_CAN_MANAGE_PARTICIPANTS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCommunityModeratorAssistantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommunityModeratorAssistant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCommunityModeratorAssistant))
            )
            .andExpect(status().isOk());

        // Validate the CommunityModeratorAssistant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommunityModeratorAssistantUpdatableFieldsEquals(
            partialUpdatedCommunityModeratorAssistant,
            getPersistedCommunityModeratorAssistant(partialUpdatedCommunityModeratorAssistant)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCommunityModeratorAssistant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityModeratorAssistant.setId(longCount.incrementAndGet());

        // Create the CommunityModeratorAssistant
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            communityModeratorAssistant
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommunityModeratorAssistantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, communityModeratorAssistantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(communityModeratorAssistantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityModeratorAssistant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCommunityModeratorAssistant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityModeratorAssistant.setId(longCount.incrementAndGet());

        // Create the CommunityModeratorAssistant
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            communityModeratorAssistant
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityModeratorAssistantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(communityModeratorAssistantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityModeratorAssistant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCommunityModeratorAssistant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityModeratorAssistant.setId(longCount.incrementAndGet());

        // Create the CommunityModeratorAssistant
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = communityModeratorAssistantMapper.toDto(
            communityModeratorAssistant
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityModeratorAssistantMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(communityModeratorAssistantDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CommunityModeratorAssistant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCommunityModeratorAssistant() throws Exception {
        // Initialize the database
        insertedCommunityModeratorAssistant = communityModeratorAssistantRepository.saveAndFlush(communityModeratorAssistant);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the communityModeratorAssistant
        restCommunityModeratorAssistantMockMvc
            .perform(delete(ENTITY_API_URL_ID, communityModeratorAssistant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return communityModeratorAssistantRepository.count();
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

    protected CommunityModeratorAssistant getPersistedCommunityModeratorAssistant(CommunityModeratorAssistant communityModeratorAssistant) {
        return communityModeratorAssistantRepository.findById(communityModeratorAssistant.getId()).orElseThrow();
    }

    protected void assertPersistedCommunityModeratorAssistantToMatchAllProperties(
        CommunityModeratorAssistant expectedCommunityModeratorAssistant
    ) {
        assertCommunityModeratorAssistantAllPropertiesEquals(
            expectedCommunityModeratorAssistant,
            getPersistedCommunityModeratorAssistant(expectedCommunityModeratorAssistant)
        );
    }

    protected void assertPersistedCommunityModeratorAssistantToMatchUpdatableProperties(
        CommunityModeratorAssistant expectedCommunityModeratorAssistant
    ) {
        assertCommunityModeratorAssistantAllUpdatablePropertiesEquals(
            expectedCommunityModeratorAssistant,
            getPersistedCommunityModeratorAssistant(expectedCommunityModeratorAssistant)
        );
    }
}
