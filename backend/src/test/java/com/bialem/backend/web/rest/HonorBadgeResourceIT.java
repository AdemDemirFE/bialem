package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.HonorBadgeAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.HonorBadge;
import com.bialem.backend.domain.enumeration.HonorBadgeType;
import com.bialem.backend.repository.HonorBadgeRepository;
import com.bialem.backend.service.dto.HonorBadgeDTO;
import com.bialem.backend.service.mapper.HonorBadgeMapper;
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
 * Integration tests for the {@link HonorBadgeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HonorBadgeResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME_TEMPLATE = "AAAAAAAAAA";
    private static final String UPDATED_NAME_TEMPLATE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final HonorBadgeType DEFAULT_BADGE_TYPE = HonorBadgeType.COMMUNITY;
    private static final HonorBadgeType UPDATED_BADGE_TYPE = HonorBadgeType.CITY;

    private static final Integer DEFAULT_MINIMUM_CHECK_INS = 1;
    private static final Integer UPDATED_MINIMUM_CHECK_INS = 2;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/honor-badges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HonorBadgeRepository honorBadgeRepository;

    @Autowired
    private HonorBadgeMapper honorBadgeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHonorBadgeMockMvc;

    private HonorBadge honorBadge;

    private HonorBadge insertedHonorBadge;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HonorBadge createEntity() {
        return new HonorBadge()
            .code(DEFAULT_CODE)
            .nameTemplate(DEFAULT_NAME_TEMPLATE)
            .description(DEFAULT_DESCRIPTION)
            .badgeType(DEFAULT_BADGE_TYPE)
            .minimumCheckIns(DEFAULT_MINIMUM_CHECK_INS)
            .isActive(DEFAULT_IS_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HonorBadge createUpdatedEntity() {
        return new HonorBadge()
            .code(UPDATED_CODE)
            .nameTemplate(UPDATED_NAME_TEMPLATE)
            .description(UPDATED_DESCRIPTION)
            .badgeType(UPDATED_BADGE_TYPE)
            .minimumCheckIns(UPDATED_MINIMUM_CHECK_INS)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        honorBadge = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedHonorBadge != null) {
            honorBadgeRepository.delete(insertedHonorBadge);
            insertedHonorBadge = null;
        }
    }

    @Test
    @Transactional
    void createHonorBadge() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the HonorBadge
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);
        var returnedHonorBadgeDTO = om.readValue(
            restHonorBadgeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(honorBadgeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HonorBadgeDTO.class
        );

        // Validate the HonorBadge in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedHonorBadge = honorBadgeMapper.toEntity(returnedHonorBadgeDTO);
        assertHonorBadgeUpdatableFieldsEquals(returnedHonorBadge, getPersistedHonorBadge(returnedHonorBadge));

        insertedHonorBadge = returnedHonorBadge;
    }

    @Test
    @Transactional
    void createHonorBadgeWithExistingId() throws Exception {
        // Create the HonorBadge with an existing ID
        honorBadge.setId(1L);
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHonorBadgeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(honorBadgeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the HonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        honorBadge.setCode(null);

        // Create the HonorBadge, which fails.
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        restHonorBadgeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(honorBadgeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameTemplateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        honorBadge.setNameTemplate(null);

        // Create the HonorBadge, which fails.
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        restHonorBadgeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(honorBadgeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        honorBadge.setDescription(null);

        // Create the HonorBadge, which fails.
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        restHonorBadgeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(honorBadgeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBadgeTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        honorBadge.setBadgeType(null);

        // Create the HonorBadge, which fails.
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        restHonorBadgeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(honorBadgeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMinimumCheckInsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        honorBadge.setMinimumCheckIns(null);

        // Create the HonorBadge, which fails.
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        restHonorBadgeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(honorBadgeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        honorBadge.setIsActive(null);

        // Create the HonorBadge, which fails.
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        restHonorBadgeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(honorBadgeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        honorBadge.setCreatedAt(null);

        // Create the HonorBadge, which fails.
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        restHonorBadgeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(honorBadgeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHonorBadges() throws Exception {
        // Initialize the database
        insertedHonorBadge = honorBadgeRepository.saveAndFlush(honorBadge);

        // Get all the honorBadgeList
        restHonorBadgeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(honorBadge.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].nameTemplate").value(hasItem(DEFAULT_NAME_TEMPLATE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].badgeType").value(hasItem(DEFAULT_BADGE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].minimumCheckIns").value(hasItem(DEFAULT_MINIMUM_CHECK_INS)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getHonorBadge() throws Exception {
        // Initialize the database
        insertedHonorBadge = honorBadgeRepository.saveAndFlush(honorBadge);

        // Get the honorBadge
        restHonorBadgeMockMvc
            .perform(get(ENTITY_API_URL_ID, honorBadge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(honorBadge.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.nameTemplate").value(DEFAULT_NAME_TEMPLATE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.badgeType").value(DEFAULT_BADGE_TYPE.toString()))
            .andExpect(jsonPath("$.minimumCheckIns").value(DEFAULT_MINIMUM_CHECK_INS))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingHonorBadge() throws Exception {
        // Get the honorBadge
        restHonorBadgeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHonorBadge() throws Exception {
        // Initialize the database
        insertedHonorBadge = honorBadgeRepository.saveAndFlush(honorBadge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the honorBadge
        HonorBadge updatedHonorBadge = honorBadgeRepository.findById(honorBadge.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHonorBadge are not directly saved in db
        em.detach(updatedHonorBadge);
        updatedHonorBadge
            .code(UPDATED_CODE)
            .nameTemplate(UPDATED_NAME_TEMPLATE)
            .description(UPDATED_DESCRIPTION)
            .badgeType(UPDATED_BADGE_TYPE)
            .minimumCheckIns(UPDATED_MINIMUM_CHECK_INS)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(updatedHonorBadge);

        restHonorBadgeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, honorBadgeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(honorBadgeDTO))
            )
            .andExpect(status().isOk());

        // Validate the HonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHonorBadgeToMatchAllProperties(updatedHonorBadge);
    }

    @Test
    @Transactional
    void putNonExistingHonorBadge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        honorBadge.setId(longCount.incrementAndGet());

        // Create the HonorBadge
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHonorBadgeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, honorBadgeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(honorBadgeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHonorBadge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        honorBadge.setId(longCount.incrementAndGet());

        // Create the HonorBadge
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHonorBadgeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(honorBadgeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHonorBadge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        honorBadge.setId(longCount.incrementAndGet());

        // Create the HonorBadge
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHonorBadgeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(honorBadgeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHonorBadgeWithPatch() throws Exception {
        // Initialize the database
        insertedHonorBadge = honorBadgeRepository.saveAndFlush(honorBadge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the honorBadge using partial update
        HonorBadge partialUpdatedHonorBadge = new HonorBadge();
        partialUpdatedHonorBadge.setId(honorBadge.getId());

        partialUpdatedHonorBadge
            .nameTemplate(UPDATED_NAME_TEMPLATE)
            .description(UPDATED_DESCRIPTION)
            .badgeType(UPDATED_BADGE_TYPE)
            .minimumCheckIns(UPDATED_MINIMUM_CHECK_INS);

        restHonorBadgeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHonorBadge.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHonorBadge))
            )
            .andExpect(status().isOk());

        // Validate the HonorBadge in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHonorBadgeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedHonorBadge, honorBadge),
            getPersistedHonorBadge(honorBadge)
        );
    }

    @Test
    @Transactional
    void fullUpdateHonorBadgeWithPatch() throws Exception {
        // Initialize the database
        insertedHonorBadge = honorBadgeRepository.saveAndFlush(honorBadge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the honorBadge using partial update
        HonorBadge partialUpdatedHonorBadge = new HonorBadge();
        partialUpdatedHonorBadge.setId(honorBadge.getId());

        partialUpdatedHonorBadge
            .code(UPDATED_CODE)
            .nameTemplate(UPDATED_NAME_TEMPLATE)
            .description(UPDATED_DESCRIPTION)
            .badgeType(UPDATED_BADGE_TYPE)
            .minimumCheckIns(UPDATED_MINIMUM_CHECK_INS)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT);

        restHonorBadgeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHonorBadge.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHonorBadge))
            )
            .andExpect(status().isOk());

        // Validate the HonorBadge in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHonorBadgeUpdatableFieldsEquals(partialUpdatedHonorBadge, getPersistedHonorBadge(partialUpdatedHonorBadge));
    }

    @Test
    @Transactional
    void patchNonExistingHonorBadge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        honorBadge.setId(longCount.incrementAndGet());

        // Create the HonorBadge
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHonorBadgeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, honorBadgeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(honorBadgeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHonorBadge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        honorBadge.setId(longCount.incrementAndGet());

        // Create the HonorBadge
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHonorBadgeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(honorBadgeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHonorBadge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        honorBadge.setId(longCount.incrementAndGet());

        // Create the HonorBadge
        HonorBadgeDTO honorBadgeDTO = honorBadgeMapper.toDto(honorBadge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHonorBadgeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(honorBadgeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HonorBadge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHonorBadge() throws Exception {
        // Initialize the database
        insertedHonorBadge = honorBadgeRepository.saveAndFlush(honorBadge);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the honorBadge
        restHonorBadgeMockMvc
            .perform(delete(ENTITY_API_URL_ID, honorBadge.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return honorBadgeRepository.count();
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

    protected HonorBadge getPersistedHonorBadge(HonorBadge honorBadge) {
        return honorBadgeRepository.findById(honorBadge.getId()).orElseThrow();
    }

    protected void assertPersistedHonorBadgeToMatchAllProperties(HonorBadge expectedHonorBadge) {
        assertHonorBadgeAllPropertiesEquals(expectedHonorBadge, getPersistedHonorBadge(expectedHonorBadge));
    }

    protected void assertPersistedHonorBadgeToMatchUpdatableProperties(HonorBadge expectedHonorBadge) {
        assertHonorBadgeAllUpdatablePropertiesEquals(expectedHonorBadge, getPersistedHonorBadge(expectedHonorBadge));
    }
}
