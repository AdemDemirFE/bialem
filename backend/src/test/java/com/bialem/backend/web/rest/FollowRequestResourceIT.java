package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.FollowRequestAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.FollowRequest;
import com.bialem.backend.repository.FollowRequestRepository;
import com.bialem.backend.service.dto.FollowRequestDTO;
import com.bialem.backend.service.mapper.FollowRequestMapper;
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
 * Integration tests for the {@link FollowRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FollowRequestResourceIT {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/follow-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FollowRequestRepository followRequestRepository;

    @Autowired
    private FollowRequestMapper followRequestMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFollowRequestMockMvc;

    private FollowRequest followRequest;

    private FollowRequest insertedFollowRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FollowRequest createEntity() {
        return new FollowRequest().createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FollowRequest createUpdatedEntity() {
        return new FollowRequest().createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        followRequest = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFollowRequest != null) {
            followRequestRepository.delete(insertedFollowRequest);
            insertedFollowRequest = null;
        }
    }

    @Test
    @Transactional
    void createFollowRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FollowRequest
        FollowRequestDTO followRequestDTO = followRequestMapper.toDto(followRequest);
        var returnedFollowRequestDTO = om.readValue(
            restFollowRequestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FollowRequestDTO.class
        );

        // Validate the FollowRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFollowRequest = followRequestMapper.toEntity(returnedFollowRequestDTO);
        assertFollowRequestUpdatableFieldsEquals(returnedFollowRequest, getPersistedFollowRequest(returnedFollowRequest));

        insertedFollowRequest = returnedFollowRequest;
    }

    @Test
    @Transactional
    void createFollowRequestWithExistingId() throws Exception {
        // Create the FollowRequest with an existing ID
        followRequest.setId(1L);
        FollowRequestDTO followRequestDTO = followRequestMapper.toDto(followRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFollowRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FollowRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        followRequest.setCreatedAt(null);

        // Create the FollowRequest, which fails.
        FollowRequestDTO followRequestDTO = followRequestMapper.toDto(followRequest);

        restFollowRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFollowRequests() throws Exception {
        // Initialize the database
        insertedFollowRequest = followRequestRepository.saveAndFlush(followRequest);

        // Get all the followRequestList
        restFollowRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(followRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getFollowRequest() throws Exception {
        // Initialize the database
        insertedFollowRequest = followRequestRepository.saveAndFlush(followRequest);

        // Get the followRequest
        restFollowRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, followRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(followRequest.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFollowRequest() throws Exception {
        // Get the followRequest
        restFollowRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFollowRequest() throws Exception {
        // Initialize the database
        insertedFollowRequest = followRequestRepository.saveAndFlush(followRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the followRequest
        FollowRequest updatedFollowRequest = followRequestRepository.findById(followRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFollowRequest are not directly saved in db
        em.detach(updatedFollowRequest);
        updatedFollowRequest.createdAt(UPDATED_CREATED_AT);
        FollowRequestDTO followRequestDTO = followRequestMapper.toDto(updatedFollowRequest);

        restFollowRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, followRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(followRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the FollowRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFollowRequestToMatchAllProperties(updatedFollowRequest);
    }

    @Test
    @Transactional
    void putNonExistingFollowRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followRequest.setId(longCount.incrementAndGet());

        // Create the FollowRequest
        FollowRequestDTO followRequestDTO = followRequestMapper.toDto(followRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFollowRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, followRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(followRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FollowRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFollowRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followRequest.setId(longCount.incrementAndGet());

        // Create the FollowRequest
        FollowRequestDTO followRequestDTO = followRequestMapper.toDto(followRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(followRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FollowRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFollowRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followRequest.setId(longCount.incrementAndGet());

        // Create the FollowRequest
        FollowRequestDTO followRequestDTO = followRequestMapper.toDto(followRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FollowRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFollowRequestWithPatch() throws Exception {
        // Initialize the database
        insertedFollowRequest = followRequestRepository.saveAndFlush(followRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the followRequest using partial update
        FollowRequest partialUpdatedFollowRequest = new FollowRequest();
        partialUpdatedFollowRequest.setId(followRequest.getId());

        restFollowRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFollowRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFollowRequest))
            )
            .andExpect(status().isOk());

        // Validate the FollowRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFollowRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFollowRequest, followRequest),
            getPersistedFollowRequest(followRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateFollowRequestWithPatch() throws Exception {
        // Initialize the database
        insertedFollowRequest = followRequestRepository.saveAndFlush(followRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the followRequest using partial update
        FollowRequest partialUpdatedFollowRequest = new FollowRequest();
        partialUpdatedFollowRequest.setId(followRequest.getId());

        partialUpdatedFollowRequest.createdAt(UPDATED_CREATED_AT);

        restFollowRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFollowRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFollowRequest))
            )
            .andExpect(status().isOk());

        // Validate the FollowRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFollowRequestUpdatableFieldsEquals(partialUpdatedFollowRequest, getPersistedFollowRequest(partialUpdatedFollowRequest));
    }

    @Test
    @Transactional
    void patchNonExistingFollowRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followRequest.setId(longCount.incrementAndGet());

        // Create the FollowRequest
        FollowRequestDTO followRequestDTO = followRequestMapper.toDto(followRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFollowRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, followRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(followRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FollowRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFollowRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followRequest.setId(longCount.incrementAndGet());

        // Create the FollowRequest
        FollowRequestDTO followRequestDTO = followRequestMapper.toDto(followRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(followRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FollowRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFollowRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followRequest.setId(longCount.incrementAndGet());

        // Create the FollowRequest
        FollowRequestDTO followRequestDTO = followRequestMapper.toDto(followRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowRequestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(followRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FollowRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFollowRequest() throws Exception {
        // Initialize the database
        insertedFollowRequest = followRequestRepository.saveAndFlush(followRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the followRequest
        restFollowRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, followRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return followRequestRepository.count();
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

    protected FollowRequest getPersistedFollowRequest(FollowRequest followRequest) {
        return followRequestRepository.findById(followRequest.getId()).orElseThrow();
    }

    protected void assertPersistedFollowRequestToMatchAllProperties(FollowRequest expectedFollowRequest) {
        assertFollowRequestAllPropertiesEquals(expectedFollowRequest, getPersistedFollowRequest(expectedFollowRequest));
    }

    protected void assertPersistedFollowRequestToMatchUpdatableProperties(FollowRequest expectedFollowRequest) {
        assertFollowRequestAllUpdatablePropertiesEquals(expectedFollowRequest, getPersistedFollowRequest(expectedFollowRequest));
    }
}
