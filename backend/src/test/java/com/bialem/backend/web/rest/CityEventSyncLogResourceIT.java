package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.CityEventSyncLogAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.CityEventSyncLog;
import com.bialem.backend.repository.CityEventSyncLogRepository;
import com.bialem.backend.service.dto.CityEventSyncLogDTO;
import com.bialem.backend.service.mapper.CityEventSyncLogMapper;
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
 * Integration tests for the {@link CityEventSyncLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CityEventSyncLogResourceIT {

    private static final String DEFAULT_PROVIDER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_PROVIDER_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Integer DEFAULT_IMPORTED_COUNT = 1;
    private static final Integer UPDATED_IMPORTED_COUNT = 2;

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_STARTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FINISHED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FINISHED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/city-event-sync-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CityEventSyncLogRepository cityEventSyncLogRepository;

    @Autowired
    private CityEventSyncLogMapper cityEventSyncLogMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCityEventSyncLogMockMvc;

    private CityEventSyncLog cityEventSyncLog;

    private CityEventSyncLog insertedCityEventSyncLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CityEventSyncLog createEntity() {
        return new CityEventSyncLog()
            .providerCode(DEFAULT_PROVIDER_CODE)
            .status(DEFAULT_STATUS)
            .importedCount(DEFAULT_IMPORTED_COUNT)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .startedAt(DEFAULT_STARTED_AT)
            .finishedAt(DEFAULT_FINISHED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CityEventSyncLog createUpdatedEntity() {
        return new CityEventSyncLog()
            .providerCode(UPDATED_PROVIDER_CODE)
            .status(UPDATED_STATUS)
            .importedCount(UPDATED_IMPORTED_COUNT)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .startedAt(UPDATED_STARTED_AT)
            .finishedAt(UPDATED_FINISHED_AT);
    }

    @BeforeEach
    void initTest() {
        cityEventSyncLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCityEventSyncLog != null) {
            cityEventSyncLogRepository.delete(insertedCityEventSyncLog);
            insertedCityEventSyncLog = null;
        }
    }

    @Test
    @Transactional
    void createCityEventSyncLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CityEventSyncLog
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(cityEventSyncLog);
        var returnedCityEventSyncLogDTO = om.readValue(
            restCityEventSyncLogMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventSyncLogDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CityEventSyncLogDTO.class
        );

        // Validate the CityEventSyncLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCityEventSyncLog = cityEventSyncLogMapper.toEntity(returnedCityEventSyncLogDTO);
        assertCityEventSyncLogUpdatableFieldsEquals(returnedCityEventSyncLog, getPersistedCityEventSyncLog(returnedCityEventSyncLog));

        insertedCityEventSyncLog = returnedCityEventSyncLog;
    }

    @Test
    @Transactional
    void createCityEventSyncLogWithExistingId() throws Exception {
        // Create the CityEventSyncLog with an existing ID
        cityEventSyncLog.setId(1L);
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(cityEventSyncLog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCityEventSyncLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventSyncLogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CityEventSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkProviderCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventSyncLog.setProviderCode(null);

        // Create the CityEventSyncLog, which fails.
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(cityEventSyncLog);

        restCityEventSyncLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventSyncLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventSyncLog.setStatus(null);

        // Create the CityEventSyncLog, which fails.
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(cityEventSyncLog);

        restCityEventSyncLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventSyncLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkImportedCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventSyncLog.setImportedCount(null);

        // Create the CityEventSyncLog, which fails.
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(cityEventSyncLog);

        restCityEventSyncLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventSyncLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventSyncLog.setStartedAt(null);

        // Create the CityEventSyncLog, which fails.
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(cityEventSyncLog);

        restCityEventSyncLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventSyncLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFinishedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventSyncLog.setFinishedAt(null);

        // Create the CityEventSyncLog, which fails.
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(cityEventSyncLog);

        restCityEventSyncLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventSyncLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCityEventSyncLogs() throws Exception {
        // Initialize the database
        insertedCityEventSyncLog = cityEventSyncLogRepository.saveAndFlush(cityEventSyncLog);

        // Get all the cityEventSyncLogList
        restCityEventSyncLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cityEventSyncLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].providerCode").value(hasItem(DEFAULT_PROVIDER_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].importedCount").value(hasItem(DEFAULT_IMPORTED_COUNT)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(DEFAULT_STARTED_AT.toString())))
            .andExpect(jsonPath("$.[*].finishedAt").value(hasItem(DEFAULT_FINISHED_AT.toString())));
    }

    @Test
    @Transactional
    void getCityEventSyncLog() throws Exception {
        // Initialize the database
        insertedCityEventSyncLog = cityEventSyncLogRepository.saveAndFlush(cityEventSyncLog);

        // Get the cityEventSyncLog
        restCityEventSyncLogMockMvc
            .perform(get(ENTITY_API_URL_ID, cityEventSyncLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cityEventSyncLog.getId().intValue()))
            .andExpect(jsonPath("$.providerCode").value(DEFAULT_PROVIDER_CODE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.importedCount").value(DEFAULT_IMPORTED_COUNT))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.startedAt").value(DEFAULT_STARTED_AT.toString()))
            .andExpect(jsonPath("$.finishedAt").value(DEFAULT_FINISHED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCityEventSyncLog() throws Exception {
        // Get the cityEventSyncLog
        restCityEventSyncLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCityEventSyncLog() throws Exception {
        // Initialize the database
        insertedCityEventSyncLog = cityEventSyncLogRepository.saveAndFlush(cityEventSyncLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cityEventSyncLog
        CityEventSyncLog updatedCityEventSyncLog = cityEventSyncLogRepository.findById(cityEventSyncLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCityEventSyncLog are not directly saved in db
        em.detach(updatedCityEventSyncLog);
        updatedCityEventSyncLog
            .providerCode(UPDATED_PROVIDER_CODE)
            .status(UPDATED_STATUS)
            .importedCount(UPDATED_IMPORTED_COUNT)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .startedAt(UPDATED_STARTED_AT)
            .finishedAt(UPDATED_FINISHED_AT);
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(updatedCityEventSyncLog);

        restCityEventSyncLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cityEventSyncLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cityEventSyncLogDTO))
            )
            .andExpect(status().isOk());

        // Validate the CityEventSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCityEventSyncLogToMatchAllProperties(updatedCityEventSyncLog);
    }

    @Test
    @Transactional
    void putNonExistingCityEventSyncLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventSyncLog.setId(longCount.incrementAndGet());

        // Create the CityEventSyncLog
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(cityEventSyncLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCityEventSyncLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cityEventSyncLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cityEventSyncLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEventSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCityEventSyncLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventSyncLog.setId(longCount.incrementAndGet());

        // Create the CityEventSyncLog
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(cityEventSyncLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventSyncLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cityEventSyncLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEventSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCityEventSyncLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventSyncLog.setId(longCount.incrementAndGet());

        // Create the CityEventSyncLog
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(cityEventSyncLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventSyncLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventSyncLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CityEventSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCityEventSyncLogWithPatch() throws Exception {
        // Initialize the database
        insertedCityEventSyncLog = cityEventSyncLogRepository.saveAndFlush(cityEventSyncLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cityEventSyncLog using partial update
        CityEventSyncLog partialUpdatedCityEventSyncLog = new CityEventSyncLog();
        partialUpdatedCityEventSyncLog.setId(cityEventSyncLog.getId());

        partialUpdatedCityEventSyncLog
            .providerCode(UPDATED_PROVIDER_CODE)
            .status(UPDATED_STATUS)
            .importedCount(UPDATED_IMPORTED_COUNT)
            .finishedAt(UPDATED_FINISHED_AT);

        restCityEventSyncLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCityEventSyncLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCityEventSyncLog))
            )
            .andExpect(status().isOk());

        // Validate the CityEventSyncLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityEventSyncLogUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCityEventSyncLog, cityEventSyncLog),
            getPersistedCityEventSyncLog(cityEventSyncLog)
        );
    }

    @Test
    @Transactional
    void fullUpdateCityEventSyncLogWithPatch() throws Exception {
        // Initialize the database
        insertedCityEventSyncLog = cityEventSyncLogRepository.saveAndFlush(cityEventSyncLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cityEventSyncLog using partial update
        CityEventSyncLog partialUpdatedCityEventSyncLog = new CityEventSyncLog();
        partialUpdatedCityEventSyncLog.setId(cityEventSyncLog.getId());

        partialUpdatedCityEventSyncLog
            .providerCode(UPDATED_PROVIDER_CODE)
            .status(UPDATED_STATUS)
            .importedCount(UPDATED_IMPORTED_COUNT)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .startedAt(UPDATED_STARTED_AT)
            .finishedAt(UPDATED_FINISHED_AT);

        restCityEventSyncLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCityEventSyncLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCityEventSyncLog))
            )
            .andExpect(status().isOk());

        // Validate the CityEventSyncLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityEventSyncLogUpdatableFieldsEquals(
            partialUpdatedCityEventSyncLog,
            getPersistedCityEventSyncLog(partialUpdatedCityEventSyncLog)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCityEventSyncLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventSyncLog.setId(longCount.incrementAndGet());

        // Create the CityEventSyncLog
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(cityEventSyncLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCityEventSyncLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cityEventSyncLogDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cityEventSyncLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEventSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCityEventSyncLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventSyncLog.setId(longCount.incrementAndGet());

        // Create the CityEventSyncLog
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(cityEventSyncLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventSyncLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cityEventSyncLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEventSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCityEventSyncLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventSyncLog.setId(longCount.incrementAndGet());

        // Create the CityEventSyncLog
        CityEventSyncLogDTO cityEventSyncLogDTO = cityEventSyncLogMapper.toDto(cityEventSyncLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventSyncLogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cityEventSyncLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CityEventSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCityEventSyncLog() throws Exception {
        // Initialize the database
        insertedCityEventSyncLog = cityEventSyncLogRepository.saveAndFlush(cityEventSyncLog);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cityEventSyncLog
        restCityEventSyncLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, cityEventSyncLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cityEventSyncLogRepository.count();
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

    protected CityEventSyncLog getPersistedCityEventSyncLog(CityEventSyncLog cityEventSyncLog) {
        return cityEventSyncLogRepository.findById(cityEventSyncLog.getId()).orElseThrow();
    }

    protected void assertPersistedCityEventSyncLogToMatchAllProperties(CityEventSyncLog expectedCityEventSyncLog) {
        assertCityEventSyncLogAllPropertiesEquals(expectedCityEventSyncLog, getPersistedCityEventSyncLog(expectedCityEventSyncLog));
    }

    protected void assertPersistedCityEventSyncLogToMatchUpdatableProperties(CityEventSyncLog expectedCityEventSyncLog) {
        assertCityEventSyncLogAllUpdatablePropertiesEquals(
            expectedCityEventSyncLog,
            getPersistedCityEventSyncLog(expectedCityEventSyncLog)
        );
    }
}
