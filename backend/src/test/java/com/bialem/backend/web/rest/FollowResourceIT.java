package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.FollowAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.Follow;
import com.bialem.backend.repository.FollowRepository;
import com.bialem.backend.service.dto.FollowDTO;
import com.bialem.backend.service.mapper.FollowMapper;
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
 * Integration tests for the {@link FollowResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FollowResourceIT {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/follows";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFollowMockMvc;

    private Follow follow;

    private Follow insertedFollow;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Follow createEntity() {
        return new Follow().createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Follow createUpdatedEntity() {
        return new Follow().createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        follow = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFollow != null) {
            followRepository.delete(insertedFollow);
            insertedFollow = null;
        }
    }

    @Test
    @Transactional
    void createFollow() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);
        var returnedFollowDTO = om.readValue(
            restFollowMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FollowDTO.class
        );

        // Validate the Follow in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFollow = followMapper.toEntity(returnedFollowDTO);
        assertFollowUpdatableFieldsEquals(returnedFollow, getPersistedFollow(returnedFollow));

        insertedFollow = returnedFollow;
    }

    @Test
    @Transactional
    void createFollowWithExistingId() throws Exception {
        // Create the Follow with an existing ID
        follow.setId(1L);
        FollowDTO followDTO = followMapper.toDto(follow);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFollowMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Follow in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        follow.setCreatedAt(null);

        // Create the Follow, which fails.
        FollowDTO followDTO = followMapper.toDto(follow);

        restFollowMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFollows() throws Exception {
        // Initialize the database
        insertedFollow = followRepository.saveAndFlush(follow);

        // Get all the followList
        restFollowMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(follow.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getFollow() throws Exception {
        // Initialize the database
        insertedFollow = followRepository.saveAndFlush(follow);

        // Get the follow
        restFollowMockMvc
            .perform(get(ENTITY_API_URL_ID, follow.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(follow.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFollow() throws Exception {
        // Get the follow
        restFollowMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFollow() throws Exception {
        // Initialize the database
        insertedFollow = followRepository.saveAndFlush(follow);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the follow
        Follow updatedFollow = followRepository.findById(follow.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFollow are not directly saved in db
        em.detach(updatedFollow);
        updatedFollow.createdAt(UPDATED_CREATED_AT);
        FollowDTO followDTO = followMapper.toDto(updatedFollow);

        restFollowMockMvc
            .perform(
                put(ENTITY_API_URL_ID, followDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followDTO))
            )
            .andExpect(status().isOk());

        // Validate the Follow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFollowToMatchAllProperties(updatedFollow);
    }

    @Test
    @Transactional
    void putNonExistingFollow() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        follow.setId(longCount.incrementAndGet());

        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFollowMockMvc
            .perform(
                put(ENTITY_API_URL_ID, followDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Follow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFollow() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        follow.setId(longCount.incrementAndGet());

        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(followDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Follow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFollow() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        follow.setId(longCount.incrementAndGet());

        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Follow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFollowWithPatch() throws Exception {
        // Initialize the database
        insertedFollow = followRepository.saveAndFlush(follow);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the follow using partial update
        Follow partialUpdatedFollow = new Follow();
        partialUpdatedFollow.setId(follow.getId());

        restFollowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFollow.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFollow))
            )
            .andExpect(status().isOk());

        // Validate the Follow in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFollowUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFollow, follow), getPersistedFollow(follow));
    }

    @Test
    @Transactional
    void fullUpdateFollowWithPatch() throws Exception {
        // Initialize the database
        insertedFollow = followRepository.saveAndFlush(follow);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the follow using partial update
        Follow partialUpdatedFollow = new Follow();
        partialUpdatedFollow.setId(follow.getId());

        partialUpdatedFollow.createdAt(UPDATED_CREATED_AT);

        restFollowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFollow.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFollow))
            )
            .andExpect(status().isOk());

        // Validate the Follow in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFollowUpdatableFieldsEquals(partialUpdatedFollow, getPersistedFollow(partialUpdatedFollow));
    }

    @Test
    @Transactional
    void patchNonExistingFollow() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        follow.setId(longCount.incrementAndGet());

        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFollowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, followDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(followDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Follow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFollow() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        follow.setId(longCount.incrementAndGet());

        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(followDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Follow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFollow() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        follow.setId(longCount.incrementAndGet());

        // Create the Follow
        FollowDTO followDTO = followMapper.toDto(follow);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(followDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Follow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFollow() throws Exception {
        // Initialize the database
        insertedFollow = followRepository.saveAndFlush(follow);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the follow
        restFollowMockMvc
            .perform(delete(ENTITY_API_URL_ID, follow.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return followRepository.count();
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

    protected Follow getPersistedFollow(Follow follow) {
        return followRepository.findById(follow.getId()).orElseThrow();
    }

    protected void assertPersistedFollowToMatchAllProperties(Follow expectedFollow) {
        assertFollowAllPropertiesEquals(expectedFollow, getPersistedFollow(expectedFollow));
    }

    protected void assertPersistedFollowToMatchUpdatableProperties(Follow expectedFollow) {
        assertFollowAllUpdatablePropertiesEquals(expectedFollow, getPersistedFollow(expectedFollow));
    }
}
