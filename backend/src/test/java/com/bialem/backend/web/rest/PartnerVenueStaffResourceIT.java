package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.PartnerVenueStaffAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.PartnerVenueStaff;
import com.bialem.backend.repository.PartnerVenueStaffRepository;
import com.bialem.backend.service.dto.PartnerVenueStaffDTO;
import com.bialem.backend.service.mapper.PartnerVenueStaffMapper;
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
 * Integration tests for the {@link PartnerVenueStaffResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PartnerVenueStaffResourceIT {

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/partner-venue-staffs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PartnerVenueStaffRepository partnerVenueStaffRepository;

    @Autowired
    private PartnerVenueStaffMapper partnerVenueStaffMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartnerVenueStaffMockMvc;

    private PartnerVenueStaff partnerVenueStaff;

    private PartnerVenueStaff insertedPartnerVenueStaff;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartnerVenueStaff createEntity() {
        return new PartnerVenueStaff().isActive(DEFAULT_IS_ACTIVE).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartnerVenueStaff createUpdatedEntity() {
        return new PartnerVenueStaff().isActive(UPDATED_IS_ACTIVE).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        partnerVenueStaff = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPartnerVenueStaff != null) {
            partnerVenueStaffRepository.delete(insertedPartnerVenueStaff);
            insertedPartnerVenueStaff = null;
        }
    }

    @Test
    @Transactional
    void createPartnerVenueStaff() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PartnerVenueStaff
        PartnerVenueStaffDTO partnerVenueStaffDTO = partnerVenueStaffMapper.toDto(partnerVenueStaff);
        var returnedPartnerVenueStaffDTO = om.readValue(
            restPartnerVenueStaffMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueStaffDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PartnerVenueStaffDTO.class
        );

        // Validate the PartnerVenueStaff in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPartnerVenueStaff = partnerVenueStaffMapper.toEntity(returnedPartnerVenueStaffDTO);
        assertPartnerVenueStaffUpdatableFieldsEquals(returnedPartnerVenueStaff, getPersistedPartnerVenueStaff(returnedPartnerVenueStaff));

        insertedPartnerVenueStaff = returnedPartnerVenueStaff;
    }

    @Test
    @Transactional
    void createPartnerVenueStaffWithExistingId() throws Exception {
        // Create the PartnerVenueStaff with an existing ID
        partnerVenueStaff.setId(1L);
        PartnerVenueStaffDTO partnerVenueStaffDTO = partnerVenueStaffMapper.toDto(partnerVenueStaff);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartnerVenueStaffMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueStaffDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PartnerVenueStaff in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerVenueStaff.setIsActive(null);

        // Create the PartnerVenueStaff, which fails.
        PartnerVenueStaffDTO partnerVenueStaffDTO = partnerVenueStaffMapper.toDto(partnerVenueStaff);

        restPartnerVenueStaffMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueStaffDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerVenueStaff.setCreatedAt(null);

        // Create the PartnerVenueStaff, which fails.
        PartnerVenueStaffDTO partnerVenueStaffDTO = partnerVenueStaffMapper.toDto(partnerVenueStaff);

        restPartnerVenueStaffMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueStaffDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPartnerVenueStaffs() throws Exception {
        // Initialize the database
        insertedPartnerVenueStaff = partnerVenueStaffRepository.saveAndFlush(partnerVenueStaff);

        // Get all the partnerVenueStaffList
        restPartnerVenueStaffMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partnerVenueStaff.getId().intValue())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getPartnerVenueStaff() throws Exception {
        // Initialize the database
        insertedPartnerVenueStaff = partnerVenueStaffRepository.saveAndFlush(partnerVenueStaff);

        // Get the partnerVenueStaff
        restPartnerVenueStaffMockMvc
            .perform(get(ENTITY_API_URL_ID, partnerVenueStaff.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(partnerVenueStaff.getId().intValue()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPartnerVenueStaff() throws Exception {
        // Get the partnerVenueStaff
        restPartnerVenueStaffMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPartnerVenueStaff() throws Exception {
        // Initialize the database
        insertedPartnerVenueStaff = partnerVenueStaffRepository.saveAndFlush(partnerVenueStaff);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partnerVenueStaff
        PartnerVenueStaff updatedPartnerVenueStaff = partnerVenueStaffRepository.findById(partnerVenueStaff.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPartnerVenueStaff are not directly saved in db
        em.detach(updatedPartnerVenueStaff);
        updatedPartnerVenueStaff.isActive(UPDATED_IS_ACTIVE).createdAt(UPDATED_CREATED_AT);
        PartnerVenueStaffDTO partnerVenueStaffDTO = partnerVenueStaffMapper.toDto(updatedPartnerVenueStaff);

        restPartnerVenueStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partnerVenueStaffDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partnerVenueStaffDTO))
            )
            .andExpect(status().isOk());

        // Validate the PartnerVenueStaff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPartnerVenueStaffToMatchAllProperties(updatedPartnerVenueStaff);
    }

    @Test
    @Transactional
    void putNonExistingPartnerVenueStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerVenueStaff.setId(longCount.incrementAndGet());

        // Create the PartnerVenueStaff
        PartnerVenueStaffDTO partnerVenueStaffDTO = partnerVenueStaffMapper.toDto(partnerVenueStaff);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartnerVenueStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partnerVenueStaffDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partnerVenueStaffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerVenueStaff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPartnerVenueStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerVenueStaff.setId(longCount.incrementAndGet());

        // Create the PartnerVenueStaff
        PartnerVenueStaffDTO partnerVenueStaffDTO = partnerVenueStaffMapper.toDto(partnerVenueStaff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerVenueStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partnerVenueStaffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerVenueStaff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPartnerVenueStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerVenueStaff.setId(longCount.incrementAndGet());

        // Create the PartnerVenueStaff
        PartnerVenueStaffDTO partnerVenueStaffDTO = partnerVenueStaffMapper.toDto(partnerVenueStaff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerVenueStaffMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueStaffDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartnerVenueStaff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartnerVenueStaffWithPatch() throws Exception {
        // Initialize the database
        insertedPartnerVenueStaff = partnerVenueStaffRepository.saveAndFlush(partnerVenueStaff);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partnerVenueStaff using partial update
        PartnerVenueStaff partialUpdatedPartnerVenueStaff = new PartnerVenueStaff();
        partialUpdatedPartnerVenueStaff.setId(partnerVenueStaff.getId());

        restPartnerVenueStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartnerVenueStaff.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartnerVenueStaff))
            )
            .andExpect(status().isOk());

        // Validate the PartnerVenueStaff in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartnerVenueStaffUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPartnerVenueStaff, partnerVenueStaff),
            getPersistedPartnerVenueStaff(partnerVenueStaff)
        );
    }

    @Test
    @Transactional
    void fullUpdatePartnerVenueStaffWithPatch() throws Exception {
        // Initialize the database
        insertedPartnerVenueStaff = partnerVenueStaffRepository.saveAndFlush(partnerVenueStaff);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partnerVenueStaff using partial update
        PartnerVenueStaff partialUpdatedPartnerVenueStaff = new PartnerVenueStaff();
        partialUpdatedPartnerVenueStaff.setId(partnerVenueStaff.getId());

        partialUpdatedPartnerVenueStaff.isActive(UPDATED_IS_ACTIVE).createdAt(UPDATED_CREATED_AT);

        restPartnerVenueStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartnerVenueStaff.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartnerVenueStaff))
            )
            .andExpect(status().isOk());

        // Validate the PartnerVenueStaff in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartnerVenueStaffUpdatableFieldsEquals(
            partialUpdatedPartnerVenueStaff,
            getPersistedPartnerVenueStaff(partialUpdatedPartnerVenueStaff)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPartnerVenueStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerVenueStaff.setId(longCount.incrementAndGet());

        // Create the PartnerVenueStaff
        PartnerVenueStaffDTO partnerVenueStaffDTO = partnerVenueStaffMapper.toDto(partnerVenueStaff);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartnerVenueStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partnerVenueStaffDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partnerVenueStaffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerVenueStaff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPartnerVenueStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerVenueStaff.setId(longCount.incrementAndGet());

        // Create the PartnerVenueStaff
        PartnerVenueStaffDTO partnerVenueStaffDTO = partnerVenueStaffMapper.toDto(partnerVenueStaff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerVenueStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partnerVenueStaffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerVenueStaff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPartnerVenueStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerVenueStaff.setId(longCount.incrementAndGet());

        // Create the PartnerVenueStaff
        PartnerVenueStaffDTO partnerVenueStaffDTO = partnerVenueStaffMapper.toDto(partnerVenueStaff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerVenueStaffMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(partnerVenueStaffDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartnerVenueStaff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePartnerVenueStaff() throws Exception {
        // Initialize the database
        insertedPartnerVenueStaff = partnerVenueStaffRepository.saveAndFlush(partnerVenueStaff);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the partnerVenueStaff
        restPartnerVenueStaffMockMvc
            .perform(delete(ENTITY_API_URL_ID, partnerVenueStaff.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return partnerVenueStaffRepository.count();
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

    protected PartnerVenueStaff getPersistedPartnerVenueStaff(PartnerVenueStaff partnerVenueStaff) {
        return partnerVenueStaffRepository.findById(partnerVenueStaff.getId()).orElseThrow();
    }

    protected void assertPersistedPartnerVenueStaffToMatchAllProperties(PartnerVenueStaff expectedPartnerVenueStaff) {
        assertPartnerVenueStaffAllPropertiesEquals(expectedPartnerVenueStaff, getPersistedPartnerVenueStaff(expectedPartnerVenueStaff));
    }

    protected void assertPersistedPartnerVenueStaffToMatchUpdatableProperties(PartnerVenueStaff expectedPartnerVenueStaff) {
        assertPartnerVenueStaffAllUpdatablePropertiesEquals(
            expectedPartnerVenueStaff,
            getPersistedPartnerVenueStaff(expectedPartnerVenueStaff)
        );
    }
}
