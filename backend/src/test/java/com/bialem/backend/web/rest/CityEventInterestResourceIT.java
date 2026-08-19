package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.CityEventInterestAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.CityEventInterest;
import com.bialem.backend.repository.CityEventInterestRepository;
import com.bialem.backend.service.dto.CityEventInterestDTO;
import com.bialem.backend.service.mapper.CityEventInterestMapper;
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
 * Integration tests for the {@link CityEventInterestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CityEventInterestResourceIT {

    private static final Boolean DEFAULT_LOOKING_FOR_COMPANY = false;
    private static final Boolean UPDATED_LOOKING_FOR_COMPANY = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/city-event-interests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CityEventInterestRepository cityEventInterestRepository;

    @Autowired
    private CityEventInterestMapper cityEventInterestMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCityEventInterestMockMvc;

    private CityEventInterest cityEventInterest;

    private CityEventInterest insertedCityEventInterest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CityEventInterest createEntity() {
        return new CityEventInterest()
            .lookingForCompany(DEFAULT_LOOKING_FOR_COMPANY)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CityEventInterest createUpdatedEntity() {
        return new CityEventInterest()
            .lookingForCompany(UPDATED_LOOKING_FOR_COMPANY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        cityEventInterest = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCityEventInterest != null) {
            cityEventInterestRepository.delete(insertedCityEventInterest);
            insertedCityEventInterest = null;
        }
    }

    @Test
    @Transactional
    void createCityEventInterest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CityEventInterest
        CityEventInterestDTO cityEventInterestDTO = cityEventInterestMapper.toDto(cityEventInterest);
        var returnedCityEventInterestDTO = om.readValue(
            restCityEventInterestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventInterestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CityEventInterestDTO.class
        );

        // Validate the CityEventInterest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCityEventInterest = cityEventInterestMapper.toEntity(returnedCityEventInterestDTO);
        assertCityEventInterestUpdatableFieldsEquals(returnedCityEventInterest, getPersistedCityEventInterest(returnedCityEventInterest));

        insertedCityEventInterest = returnedCityEventInterest;
    }

    @Test
    @Transactional
    void createCityEventInterestWithExistingId() throws Exception {
        // Create the CityEventInterest with an existing ID
        cityEventInterest.setId(1L);
        CityEventInterestDTO cityEventInterestDTO = cityEventInterestMapper.toDto(cityEventInterest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCityEventInterestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventInterestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CityEventInterest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLookingForCompanyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventInterest.setLookingForCompany(null);

        // Create the CityEventInterest, which fails.
        CityEventInterestDTO cityEventInterestDTO = cityEventInterestMapper.toDto(cityEventInterest);

        restCityEventInterestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventInterestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventInterest.setCreatedAt(null);

        // Create the CityEventInterest, which fails.
        CityEventInterestDTO cityEventInterestDTO = cityEventInterestMapper.toDto(cityEventInterest);

        restCityEventInterestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventInterestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventInterest.setUpdatedAt(null);

        // Create the CityEventInterest, which fails.
        CityEventInterestDTO cityEventInterestDTO = cityEventInterestMapper.toDto(cityEventInterest);

        restCityEventInterestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventInterestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCityEventInterests() throws Exception {
        // Initialize the database
        insertedCityEventInterest = cityEventInterestRepository.saveAndFlush(cityEventInterest);

        // Get all the cityEventInterestList
        restCityEventInterestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cityEventInterest.getId().intValue())))
            .andExpect(jsonPath("$.[*].lookingForCompany").value(hasItem(DEFAULT_LOOKING_FOR_COMPANY)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCityEventInterest() throws Exception {
        // Initialize the database
        insertedCityEventInterest = cityEventInterestRepository.saveAndFlush(cityEventInterest);

        // Get the cityEventInterest
        restCityEventInterestMockMvc
            .perform(get(ENTITY_API_URL_ID, cityEventInterest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cityEventInterest.getId().intValue()))
            .andExpect(jsonPath("$.lookingForCompany").value(DEFAULT_LOOKING_FOR_COMPANY))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCityEventInterest() throws Exception {
        // Get the cityEventInterest
        restCityEventInterestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCityEventInterest() throws Exception {
        // Initialize the database
        insertedCityEventInterest = cityEventInterestRepository.saveAndFlush(cityEventInterest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cityEventInterest
        CityEventInterest updatedCityEventInterest = cityEventInterestRepository.findById(cityEventInterest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCityEventInterest are not directly saved in db
        em.detach(updatedCityEventInterest);
        updatedCityEventInterest.lookingForCompany(UPDATED_LOOKING_FOR_COMPANY).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
        CityEventInterestDTO cityEventInterestDTO = cityEventInterestMapper.toDto(updatedCityEventInterest);

        restCityEventInterestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cityEventInterestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cityEventInterestDTO))
            )
            .andExpect(status().isOk());

        // Validate the CityEventInterest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCityEventInterestToMatchAllProperties(updatedCityEventInterest);
    }

    @Test
    @Transactional
    void putNonExistingCityEventInterest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventInterest.setId(longCount.incrementAndGet());

        // Create the CityEventInterest
        CityEventInterestDTO cityEventInterestDTO = cityEventInterestMapper.toDto(cityEventInterest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCityEventInterestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cityEventInterestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cityEventInterestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEventInterest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCityEventInterest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventInterest.setId(longCount.incrementAndGet());

        // Create the CityEventInterest
        CityEventInterestDTO cityEventInterestDTO = cityEventInterestMapper.toDto(cityEventInterest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventInterestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cityEventInterestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEventInterest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCityEventInterest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventInterest.setId(longCount.incrementAndGet());

        // Create the CityEventInterest
        CityEventInterestDTO cityEventInterestDTO = cityEventInterestMapper.toDto(cityEventInterest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventInterestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventInterestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CityEventInterest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCityEventInterestWithPatch() throws Exception {
        // Initialize the database
        insertedCityEventInterest = cityEventInterestRepository.saveAndFlush(cityEventInterest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cityEventInterest using partial update
        CityEventInterest partialUpdatedCityEventInterest = new CityEventInterest();
        partialUpdatedCityEventInterest.setId(cityEventInterest.getId());

        partialUpdatedCityEventInterest.lookingForCompany(UPDATED_LOOKING_FOR_COMPANY);

        restCityEventInterestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCityEventInterest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCityEventInterest))
            )
            .andExpect(status().isOk());

        // Validate the CityEventInterest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityEventInterestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCityEventInterest, cityEventInterest),
            getPersistedCityEventInterest(cityEventInterest)
        );
    }

    @Test
    @Transactional
    void fullUpdateCityEventInterestWithPatch() throws Exception {
        // Initialize the database
        insertedCityEventInterest = cityEventInterestRepository.saveAndFlush(cityEventInterest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cityEventInterest using partial update
        CityEventInterest partialUpdatedCityEventInterest = new CityEventInterest();
        partialUpdatedCityEventInterest.setId(cityEventInterest.getId());

        partialUpdatedCityEventInterest
            .lookingForCompany(UPDATED_LOOKING_FOR_COMPANY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCityEventInterestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCityEventInterest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCityEventInterest))
            )
            .andExpect(status().isOk());

        // Validate the CityEventInterest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityEventInterestUpdatableFieldsEquals(
            partialUpdatedCityEventInterest,
            getPersistedCityEventInterest(partialUpdatedCityEventInterest)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCityEventInterest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventInterest.setId(longCount.incrementAndGet());

        // Create the CityEventInterest
        CityEventInterestDTO cityEventInterestDTO = cityEventInterestMapper.toDto(cityEventInterest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCityEventInterestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cityEventInterestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cityEventInterestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEventInterest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCityEventInterest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventInterest.setId(longCount.incrementAndGet());

        // Create the CityEventInterest
        CityEventInterestDTO cityEventInterestDTO = cityEventInterestMapper.toDto(cityEventInterest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventInterestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cityEventInterestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEventInterest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCityEventInterest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventInterest.setId(longCount.incrementAndGet());

        // Create the CityEventInterest
        CityEventInterestDTO cityEventInterestDTO = cityEventInterestMapper.toDto(cityEventInterest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventInterestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cityEventInterestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CityEventInterest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCityEventInterest() throws Exception {
        // Initialize the database
        insertedCityEventInterest = cityEventInterestRepository.saveAndFlush(cityEventInterest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cityEventInterest
        restCityEventInterestMockMvc
            .perform(delete(ENTITY_API_URL_ID, cityEventInterest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cityEventInterestRepository.count();
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

    protected CityEventInterest getPersistedCityEventInterest(CityEventInterest cityEventInterest) {
        return cityEventInterestRepository.findById(cityEventInterest.getId()).orElseThrow();
    }

    protected void assertPersistedCityEventInterestToMatchAllProperties(CityEventInterest expectedCityEventInterest) {
        assertCityEventInterestAllPropertiesEquals(expectedCityEventInterest, getPersistedCityEventInterest(expectedCityEventInterest));
    }

    protected void assertPersistedCityEventInterestToMatchUpdatableProperties(CityEventInterest expectedCityEventInterest) {
        assertCityEventInterestAllUpdatablePropertiesEquals(
            expectedCityEventInterest,
            getPersistedCityEventInterest(expectedCityEventInterest)
        );
    }
}
