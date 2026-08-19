package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.PushTokenAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.PushToken;
import com.bialem.backend.domain.enumeration.PushPlatform;
import com.bialem.backend.repository.PushTokenRepository;
import com.bialem.backend.service.dto.PushTokenDTO;
import com.bialem.backend.service.mapper.PushTokenMapper;
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
 * Integration tests for the {@link PushTokenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PushTokenResourceIT {

    private static final String DEFAULT_DEVICE_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_DEVICE_TOKEN = "BBBBBBBBBB";

    private static final PushPlatform DEFAULT_PLATFORM = PushPlatform.IOS;
    private static final PushPlatform UPDATED_PLATFORM = PushPlatform.ANDROID;

    private static final String DEFAULT_DEVICE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DEVICE_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_LAST_SEEN_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_SEEN_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/push-tokens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PushTokenRepository pushTokenRepository;

    @Autowired
    private PushTokenMapper pushTokenMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPushTokenMockMvc;

    private PushToken pushToken;

    private PushToken insertedPushToken;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PushToken createEntity() {
        return new PushToken()
            .deviceToken(DEFAULT_DEVICE_TOKEN)
            .platform(DEFAULT_PLATFORM)
            .deviceName(DEFAULT_DEVICE_NAME)
            .isActive(DEFAULT_IS_ACTIVE)
            .lastSeenAt(DEFAULT_LAST_SEEN_AT)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PushToken createUpdatedEntity() {
        return new PushToken()
            .deviceToken(UPDATED_DEVICE_TOKEN)
            .platform(UPDATED_PLATFORM)
            .deviceName(UPDATED_DEVICE_NAME)
            .isActive(UPDATED_IS_ACTIVE)
            .lastSeenAt(UPDATED_LAST_SEEN_AT)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        pushToken = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPushToken != null) {
            pushTokenRepository.delete(insertedPushToken);
            insertedPushToken = null;
        }
    }

    @Test
    @Transactional
    void createPushToken() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PushToken
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(pushToken);
        var returnedPushTokenDTO = om.readValue(
            restPushTokenMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pushTokenDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PushTokenDTO.class
        );

        // Validate the PushToken in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPushToken = pushTokenMapper.toEntity(returnedPushTokenDTO);
        assertPushTokenUpdatableFieldsEquals(returnedPushToken, getPersistedPushToken(returnedPushToken));

        insertedPushToken = returnedPushToken;
    }

    @Test
    @Transactional
    void createPushTokenWithExistingId() throws Exception {
        // Create the PushToken with an existing ID
        pushToken.setId(1L);
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(pushToken);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPushTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pushTokenDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PushToken in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDeviceTokenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pushToken.setDeviceToken(null);

        // Create the PushToken, which fails.
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(pushToken);

        restPushTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pushTokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPlatformIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pushToken.setPlatform(null);

        // Create the PushToken, which fails.
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(pushToken);

        restPushTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pushTokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pushToken.setIsActive(null);

        // Create the PushToken, which fails.
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(pushToken);

        restPushTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pushTokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastSeenAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pushToken.setLastSeenAt(null);

        // Create the PushToken, which fails.
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(pushToken);

        restPushTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pushTokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pushToken.setCreatedAt(null);

        // Create the PushToken, which fails.
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(pushToken);

        restPushTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pushTokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPushTokens() throws Exception {
        // Initialize the database
        insertedPushToken = pushTokenRepository.saveAndFlush(pushToken);

        // Get all the pushTokenList
        restPushTokenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pushToken.getId().intValue())))
            .andExpect(jsonPath("$.[*].deviceToken").value(hasItem(DEFAULT_DEVICE_TOKEN)))
            .andExpect(jsonPath("$.[*].platform").value(hasItem(DEFAULT_PLATFORM.toString())))
            .andExpect(jsonPath("$.[*].deviceName").value(hasItem(DEFAULT_DEVICE_NAME)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].lastSeenAt").value(hasItem(DEFAULT_LAST_SEEN_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getPushToken() throws Exception {
        // Initialize the database
        insertedPushToken = pushTokenRepository.saveAndFlush(pushToken);

        // Get the pushToken
        restPushTokenMockMvc
            .perform(get(ENTITY_API_URL_ID, pushToken.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pushToken.getId().intValue()))
            .andExpect(jsonPath("$.deviceToken").value(DEFAULT_DEVICE_TOKEN))
            .andExpect(jsonPath("$.platform").value(DEFAULT_PLATFORM.toString()))
            .andExpect(jsonPath("$.deviceName").value(DEFAULT_DEVICE_NAME))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.lastSeenAt").value(DEFAULT_LAST_SEEN_AT.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPushToken() throws Exception {
        // Get the pushToken
        restPushTokenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPushToken() throws Exception {
        // Initialize the database
        insertedPushToken = pushTokenRepository.saveAndFlush(pushToken);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pushToken
        PushToken updatedPushToken = pushTokenRepository.findById(pushToken.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPushToken are not directly saved in db
        em.detach(updatedPushToken);
        updatedPushToken
            .deviceToken(UPDATED_DEVICE_TOKEN)
            .platform(UPDATED_PLATFORM)
            .deviceName(UPDATED_DEVICE_NAME)
            .isActive(UPDATED_IS_ACTIVE)
            .lastSeenAt(UPDATED_LAST_SEEN_AT)
            .createdAt(UPDATED_CREATED_AT);
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(updatedPushToken);

        restPushTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pushTokenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pushTokenDTO))
            )
            .andExpect(status().isOk());

        // Validate the PushToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPushTokenToMatchAllProperties(updatedPushToken);
    }

    @Test
    @Transactional
    void putNonExistingPushToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pushToken.setId(longCount.incrementAndGet());

        // Create the PushToken
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(pushToken);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPushTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pushTokenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pushTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PushToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPushToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pushToken.setId(longCount.incrementAndGet());

        // Create the PushToken
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(pushToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPushTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pushTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PushToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPushToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pushToken.setId(longCount.incrementAndGet());

        // Create the PushToken
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(pushToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPushTokenMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pushTokenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PushToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePushTokenWithPatch() throws Exception {
        // Initialize the database
        insertedPushToken = pushTokenRepository.saveAndFlush(pushToken);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pushToken using partial update
        PushToken partialUpdatedPushToken = new PushToken();
        partialUpdatedPushToken.setId(pushToken.getId());

        partialUpdatedPushToken.deviceToken(UPDATED_DEVICE_TOKEN).platform(UPDATED_PLATFORM);

        restPushTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPushToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPushToken))
            )
            .andExpect(status().isOk());

        // Validate the PushToken in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPushTokenUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPushToken, pushToken),
            getPersistedPushToken(pushToken)
        );
    }

    @Test
    @Transactional
    void fullUpdatePushTokenWithPatch() throws Exception {
        // Initialize the database
        insertedPushToken = pushTokenRepository.saveAndFlush(pushToken);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pushToken using partial update
        PushToken partialUpdatedPushToken = new PushToken();
        partialUpdatedPushToken.setId(pushToken.getId());

        partialUpdatedPushToken
            .deviceToken(UPDATED_DEVICE_TOKEN)
            .platform(UPDATED_PLATFORM)
            .deviceName(UPDATED_DEVICE_NAME)
            .isActive(UPDATED_IS_ACTIVE)
            .lastSeenAt(UPDATED_LAST_SEEN_AT)
            .createdAt(UPDATED_CREATED_AT);

        restPushTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPushToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPushToken))
            )
            .andExpect(status().isOk());

        // Validate the PushToken in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPushTokenUpdatableFieldsEquals(partialUpdatedPushToken, getPersistedPushToken(partialUpdatedPushToken));
    }

    @Test
    @Transactional
    void patchNonExistingPushToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pushToken.setId(longCount.incrementAndGet());

        // Create the PushToken
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(pushToken);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPushTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pushTokenDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pushTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PushToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPushToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pushToken.setId(longCount.incrementAndGet());

        // Create the PushToken
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(pushToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPushTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pushTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PushToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPushToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pushToken.setId(longCount.incrementAndGet());

        // Create the PushToken
        PushTokenDTO pushTokenDTO = pushTokenMapper.toDto(pushToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPushTokenMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pushTokenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PushToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePushToken() throws Exception {
        // Initialize the database
        insertedPushToken = pushTokenRepository.saveAndFlush(pushToken);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pushToken
        restPushTokenMockMvc
            .perform(delete(ENTITY_API_URL_ID, pushToken.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pushTokenRepository.count();
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

    protected PushToken getPersistedPushToken(PushToken pushToken) {
        return pushTokenRepository.findById(pushToken.getId()).orElseThrow();
    }

    protected void assertPersistedPushTokenToMatchAllProperties(PushToken expectedPushToken) {
        assertPushTokenAllPropertiesEquals(expectedPushToken, getPersistedPushToken(expectedPushToken));
    }

    protected void assertPersistedPushTokenToMatchUpdatableProperties(PushToken expectedPushToken) {
        assertPushTokenAllUpdatablePropertiesEquals(expectedPushToken, getPersistedPushToken(expectedPushToken));
    }
}
