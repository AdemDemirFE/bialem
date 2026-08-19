package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.UserHonorBadgeAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.UserHonorBadge;
import com.bialem.backend.repository.UserHonorBadgeRepository;
import com.bialem.backend.service.dto.UserHonorBadgeDTO;
import com.bialem.backend.service.mapper.UserHonorBadgeMapper;
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
 * Integration tests for the {@link UserHonorBadgeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserHonorBadgeResourceIT {

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final Instant DEFAULT_AWARDED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_AWARDED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/user-honor-badges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserHonorBadgeRepository userHonorBadgeRepository;

    @Autowired
    private UserHonorBadgeMapper userHonorBadgeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserHonorBadgeMockMvc;

    private UserHonorBadge userHonorBadge;

    private UserHonorBadge insertedUserHonorBadge;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserHonorBadge createEntity() {
        return new UserHonorBadge().reason(DEFAULT_REASON).awardedAt(DEFAULT_AWARDED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserHonorBadge createUpdatedEntity() {
        return new UserHonorBadge().reason(UPDATED_REASON).awardedAt(UPDATED_AWARDED_AT);
    }

    @BeforeEach
    void initTest() {
        userHonorBadge = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserHonorBadge != null) {
            userHonorBadgeRepository.delete(insertedUserHonorBadge);
            insertedUserHonorBadge = null;
        }
    }

    @Test
    @Transactional
    void createUserHonorBadge() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserHonorBadge
        UserHonorBadgeDTO userHonorBadgeDTO = userHonorBadgeMapper.toDto(userHonorBadge);
        var returnedUserHonorBadgeDTO = om.readValue(
            restUserHonorBadgeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userHonorBadgeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserHonorBadgeDTO.class
        );

        // Validate the UserHonorBadge in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserHonorBadge = userHonorBadgeMapper.toEntity(returnedUserHonorBadgeDTO);
        assertUserHonorBadgeUpdatableFieldsEquals(returnedUserHonorBadge, getPersistedUserHonorBadge(returnedUserHonorBadge));

        insertedUserHonorBadge = returnedUserHonorBadge;
    }

    @Test
    @Transactional
    void createUserHonorBadgeWithExistingId() throws Exception {
        // Create the UserHonorBadge with an existing ID
        userHonorBadge.setId(1L);
        UserHonorBadgeDTO userHonorBadgeDTO = userHonorBadgeMapper.toDto(userHonorBadge);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserHonorBadgeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userHonorBadgeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserHonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAwardedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userHonorBadge.setAwardedAt(null);

        // Create the UserHonorBadge, which fails.
        UserHonorBadgeDTO userHonorBadgeDTO = userHonorBadgeMapper.toDto(userHonorBadge);

        restUserHonorBadgeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userHonorBadgeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserHonorBadges() throws Exception {
        // Initialize the database
        insertedUserHonorBadge = userHonorBadgeRepository.saveAndFlush(userHonorBadge);

        // Get all the userHonorBadgeList
        restUserHonorBadgeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userHonorBadge.getId().intValue())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].awardedAt").value(hasItem(DEFAULT_AWARDED_AT.toString())));
    }

    @Test
    @Transactional
    void getUserHonorBadge() throws Exception {
        // Initialize the database
        insertedUserHonorBadge = userHonorBadgeRepository.saveAndFlush(userHonorBadge);

        // Get the userHonorBadge
        restUserHonorBadgeMockMvc
            .perform(get(ENTITY_API_URL_ID, userHonorBadge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userHonorBadge.getId().intValue()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.awardedAt").value(DEFAULT_AWARDED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUserHonorBadge() throws Exception {
        // Get the userHonorBadge
        restUserHonorBadgeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserHonorBadge() throws Exception {
        // Initialize the database
        insertedUserHonorBadge = userHonorBadgeRepository.saveAndFlush(userHonorBadge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userHonorBadge
        UserHonorBadge updatedUserHonorBadge = userHonorBadgeRepository.findById(userHonorBadge.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserHonorBadge are not directly saved in db
        em.detach(updatedUserHonorBadge);
        updatedUserHonorBadge.reason(UPDATED_REASON).awardedAt(UPDATED_AWARDED_AT);
        UserHonorBadgeDTO userHonorBadgeDTO = userHonorBadgeMapper.toDto(updatedUserHonorBadge);

        restUserHonorBadgeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userHonorBadgeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userHonorBadgeDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserHonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserHonorBadgeToMatchAllProperties(updatedUserHonorBadge);
    }

    @Test
    @Transactional
    void putNonExistingUserHonorBadge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userHonorBadge.setId(longCount.incrementAndGet());

        // Create the UserHonorBadge
        UserHonorBadgeDTO userHonorBadgeDTO = userHonorBadgeMapper.toDto(userHonorBadge);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserHonorBadgeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userHonorBadgeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userHonorBadgeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserHonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserHonorBadge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userHonorBadge.setId(longCount.incrementAndGet());

        // Create the UserHonorBadge
        UserHonorBadgeDTO userHonorBadgeDTO = userHonorBadgeMapper.toDto(userHonorBadge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserHonorBadgeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userHonorBadgeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserHonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserHonorBadge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userHonorBadge.setId(longCount.incrementAndGet());

        // Create the UserHonorBadge
        UserHonorBadgeDTO userHonorBadgeDTO = userHonorBadgeMapper.toDto(userHonorBadge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserHonorBadgeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userHonorBadgeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserHonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserHonorBadgeWithPatch() throws Exception {
        // Initialize the database
        insertedUserHonorBadge = userHonorBadgeRepository.saveAndFlush(userHonorBadge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userHonorBadge using partial update
        UserHonorBadge partialUpdatedUserHonorBadge = new UserHonorBadge();
        partialUpdatedUserHonorBadge.setId(userHonorBadge.getId());

        partialUpdatedUserHonorBadge.awardedAt(UPDATED_AWARDED_AT);

        restUserHonorBadgeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserHonorBadge.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserHonorBadge))
            )
            .andExpect(status().isOk());

        // Validate the UserHonorBadge in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserHonorBadgeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserHonorBadge, userHonorBadge),
            getPersistedUserHonorBadge(userHonorBadge)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserHonorBadgeWithPatch() throws Exception {
        // Initialize the database
        insertedUserHonorBadge = userHonorBadgeRepository.saveAndFlush(userHonorBadge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userHonorBadge using partial update
        UserHonorBadge partialUpdatedUserHonorBadge = new UserHonorBadge();
        partialUpdatedUserHonorBadge.setId(userHonorBadge.getId());

        partialUpdatedUserHonorBadge.reason(UPDATED_REASON).awardedAt(UPDATED_AWARDED_AT);

        restUserHonorBadgeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserHonorBadge.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserHonorBadge))
            )
            .andExpect(status().isOk());

        // Validate the UserHonorBadge in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserHonorBadgeUpdatableFieldsEquals(partialUpdatedUserHonorBadge, getPersistedUserHonorBadge(partialUpdatedUserHonorBadge));
    }

    @Test
    @Transactional
    void patchNonExistingUserHonorBadge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userHonorBadge.setId(longCount.incrementAndGet());

        // Create the UserHonorBadge
        UserHonorBadgeDTO userHonorBadgeDTO = userHonorBadgeMapper.toDto(userHonorBadge);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserHonorBadgeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userHonorBadgeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userHonorBadgeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserHonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserHonorBadge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userHonorBadge.setId(longCount.incrementAndGet());

        // Create the UserHonorBadge
        UserHonorBadgeDTO userHonorBadgeDTO = userHonorBadgeMapper.toDto(userHonorBadge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserHonorBadgeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userHonorBadgeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserHonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserHonorBadge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userHonorBadge.setId(longCount.incrementAndGet());

        // Create the UserHonorBadge
        UserHonorBadgeDTO userHonorBadgeDTO = userHonorBadgeMapper.toDto(userHonorBadge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserHonorBadgeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userHonorBadgeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserHonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserHonorBadge() throws Exception {
        // Initialize the database
        insertedUserHonorBadge = userHonorBadgeRepository.saveAndFlush(userHonorBadge);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userHonorBadge
        restUserHonorBadgeMockMvc
            .perform(delete(ENTITY_API_URL_ID, userHonorBadge.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userHonorBadgeRepository.count();
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

    protected UserHonorBadge getPersistedUserHonorBadge(UserHonorBadge userHonorBadge) {
        return userHonorBadgeRepository.findById(userHonorBadge.getId()).orElseThrow();
    }

    protected void assertPersistedUserHonorBadgeToMatchAllProperties(UserHonorBadge expectedUserHonorBadge) {
        assertUserHonorBadgeAllPropertiesEquals(expectedUserHonorBadge, getPersistedUserHonorBadge(expectedUserHonorBadge));
    }

    protected void assertPersistedUserHonorBadgeToMatchUpdatableProperties(UserHonorBadge expectedUserHonorBadge) {
        assertUserHonorBadgeAllUpdatablePropertiesEquals(expectedUserHonorBadge, getPersistedUserHonorBadge(expectedUserHonorBadge));
    }
}
