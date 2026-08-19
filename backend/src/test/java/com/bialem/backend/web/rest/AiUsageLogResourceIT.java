package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.AiUsageLogAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.AiUsageLog;
import com.bialem.backend.repository.AiUsageLogRepository;
import com.bialem.backend.service.dto.AiUsageLogDTO;
import com.bialem.backend.service.mapper.AiUsageLogMapper;
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
 * Integration tests for the {@link AiUsageLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AiUsageLogResourceIT {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/ai-usage-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AiUsageLogRepository aiUsageLogRepository;

    @Autowired
    private AiUsageLogMapper aiUsageLogMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAiUsageLogMockMvc;

    private AiUsageLog aiUsageLog;

    private AiUsageLog insertedAiUsageLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AiUsageLog createEntity() {
        return new AiUsageLog().createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AiUsageLog createUpdatedEntity() {
        return new AiUsageLog().createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        aiUsageLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAiUsageLog != null) {
            aiUsageLogRepository.delete(insertedAiUsageLog);
            insertedAiUsageLog = null;
        }
    }

    @Test
    @Transactional
    void createAiUsageLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AiUsageLog
        AiUsageLogDTO aiUsageLogDTO = aiUsageLogMapper.toDto(aiUsageLog);
        var returnedAiUsageLogDTO = om.readValue(
            restAiUsageLogMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiUsageLogDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AiUsageLogDTO.class
        );

        // Validate the AiUsageLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAiUsageLog = aiUsageLogMapper.toEntity(returnedAiUsageLogDTO);
        assertAiUsageLogUpdatableFieldsEquals(returnedAiUsageLog, getPersistedAiUsageLog(returnedAiUsageLog));

        insertedAiUsageLog = returnedAiUsageLog;
    }

    @Test
    @Transactional
    void createAiUsageLogWithExistingId() throws Exception {
        // Create the AiUsageLog with an existing ID
        aiUsageLog.setId(1L);
        AiUsageLogDTO aiUsageLogDTO = aiUsageLogMapper.toDto(aiUsageLog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAiUsageLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiUsageLogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AiUsageLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aiUsageLog.setCreatedAt(null);

        // Create the AiUsageLog, which fails.
        AiUsageLogDTO aiUsageLogDTO = aiUsageLogMapper.toDto(aiUsageLog);

        restAiUsageLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiUsageLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAiUsageLogs() throws Exception {
        // Initialize the database
        insertedAiUsageLog = aiUsageLogRepository.saveAndFlush(aiUsageLog);

        // Get all the aiUsageLogList
        restAiUsageLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aiUsageLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getAiUsageLog() throws Exception {
        // Initialize the database
        insertedAiUsageLog = aiUsageLogRepository.saveAndFlush(aiUsageLog);

        // Get the aiUsageLog
        restAiUsageLogMockMvc
            .perform(get(ENTITY_API_URL_ID, aiUsageLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aiUsageLog.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAiUsageLog() throws Exception {
        // Get the aiUsageLog
        restAiUsageLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAiUsageLog() throws Exception {
        // Initialize the database
        insertedAiUsageLog = aiUsageLogRepository.saveAndFlush(aiUsageLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aiUsageLog
        AiUsageLog updatedAiUsageLog = aiUsageLogRepository.findById(aiUsageLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAiUsageLog are not directly saved in db
        em.detach(updatedAiUsageLog);
        updatedAiUsageLog.createdAt(UPDATED_CREATED_AT);
        AiUsageLogDTO aiUsageLogDTO = aiUsageLogMapper.toDto(updatedAiUsageLog);

        restAiUsageLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aiUsageLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aiUsageLogDTO))
            )
            .andExpect(status().isOk());

        // Validate the AiUsageLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAiUsageLogToMatchAllProperties(updatedAiUsageLog);
    }

    @Test
    @Transactional
    void putNonExistingAiUsageLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiUsageLog.setId(longCount.incrementAndGet());

        // Create the AiUsageLog
        AiUsageLogDTO aiUsageLogDTO = aiUsageLogMapper.toDto(aiUsageLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAiUsageLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aiUsageLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aiUsageLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiUsageLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAiUsageLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiUsageLog.setId(longCount.incrementAndGet());

        // Create the AiUsageLog
        AiUsageLogDTO aiUsageLogDTO = aiUsageLogMapper.toDto(aiUsageLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiUsageLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aiUsageLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiUsageLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAiUsageLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiUsageLog.setId(longCount.incrementAndGet());

        // Create the AiUsageLog
        AiUsageLogDTO aiUsageLogDTO = aiUsageLogMapper.toDto(aiUsageLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiUsageLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aiUsageLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AiUsageLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAiUsageLogWithPatch() throws Exception {
        // Initialize the database
        insertedAiUsageLog = aiUsageLogRepository.saveAndFlush(aiUsageLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aiUsageLog using partial update
        AiUsageLog partialUpdatedAiUsageLog = new AiUsageLog();
        partialUpdatedAiUsageLog.setId(aiUsageLog.getId());

        restAiUsageLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAiUsageLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAiUsageLog))
            )
            .andExpect(status().isOk());

        // Validate the AiUsageLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAiUsageLogUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAiUsageLog, aiUsageLog),
            getPersistedAiUsageLog(aiUsageLog)
        );
    }

    @Test
    @Transactional
    void fullUpdateAiUsageLogWithPatch() throws Exception {
        // Initialize the database
        insertedAiUsageLog = aiUsageLogRepository.saveAndFlush(aiUsageLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aiUsageLog using partial update
        AiUsageLog partialUpdatedAiUsageLog = new AiUsageLog();
        partialUpdatedAiUsageLog.setId(aiUsageLog.getId());

        partialUpdatedAiUsageLog.createdAt(UPDATED_CREATED_AT);

        restAiUsageLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAiUsageLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAiUsageLog))
            )
            .andExpect(status().isOk());

        // Validate the AiUsageLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAiUsageLogUpdatableFieldsEquals(partialUpdatedAiUsageLog, getPersistedAiUsageLog(partialUpdatedAiUsageLog));
    }

    @Test
    @Transactional
    void patchNonExistingAiUsageLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiUsageLog.setId(longCount.incrementAndGet());

        // Create the AiUsageLog
        AiUsageLogDTO aiUsageLogDTO = aiUsageLogMapper.toDto(aiUsageLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAiUsageLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aiUsageLogDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(aiUsageLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiUsageLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAiUsageLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiUsageLog.setId(longCount.incrementAndGet());

        // Create the AiUsageLog
        AiUsageLogDTO aiUsageLogDTO = aiUsageLogMapper.toDto(aiUsageLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiUsageLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(aiUsageLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AiUsageLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAiUsageLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aiUsageLog.setId(longCount.incrementAndGet());

        // Create the AiUsageLog
        AiUsageLogDTO aiUsageLogDTO = aiUsageLogMapper.toDto(aiUsageLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAiUsageLogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(aiUsageLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AiUsageLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAiUsageLog() throws Exception {
        // Initialize the database
        insertedAiUsageLog = aiUsageLogRepository.saveAndFlush(aiUsageLog);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the aiUsageLog
        restAiUsageLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, aiUsageLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return aiUsageLogRepository.count();
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

    protected AiUsageLog getPersistedAiUsageLog(AiUsageLog aiUsageLog) {
        return aiUsageLogRepository.findById(aiUsageLog.getId()).orElseThrow();
    }

    protected void assertPersistedAiUsageLogToMatchAllProperties(AiUsageLog expectedAiUsageLog) {
        assertAiUsageLogAllPropertiesEquals(expectedAiUsageLog, getPersistedAiUsageLog(expectedAiUsageLog));
    }

    protected void assertPersistedAiUsageLogToMatchUpdatableProperties(AiUsageLog expectedAiUsageLog) {
        assertAiUsageLogAllUpdatablePropertiesEquals(expectedAiUsageLog, getPersistedAiUsageLog(expectedAiUsageLog));
    }
}
