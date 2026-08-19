package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.PlatformTeamMemberAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.PlatformTeamMember;
import com.bialem.backend.domain.enumeration.PlatformTeamRole;
import com.bialem.backend.repository.PlatformTeamMemberRepository;
import com.bialem.backend.service.dto.PlatformTeamMemberDTO;
import com.bialem.backend.service.mapper.PlatformTeamMemberMapper;
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
 * Integration tests for the {@link PlatformTeamMemberResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PlatformTeamMemberResourceIT {

    private static final PlatformTeamRole DEFAULT_ROLE_CODE = PlatformTeamRole.FOUNDER;
    private static final PlatformTeamRole UPDATED_ROLE_CODE = PlatformTeamRole.TEAM;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/platform-team-members";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlatformTeamMemberRepository platformTeamMemberRepository;

    @Autowired
    private PlatformTeamMemberMapper platformTeamMemberMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlatformTeamMemberMockMvc;

    private PlatformTeamMember platformTeamMember;

    private PlatformTeamMember insertedPlatformTeamMember;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlatformTeamMember createEntity() {
        return new PlatformTeamMember().roleCode(DEFAULT_ROLE_CODE).createdAt(DEFAULT_CREATED_AT).updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlatformTeamMember createUpdatedEntity() {
        return new PlatformTeamMember().roleCode(UPDATED_ROLE_CODE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        platformTeamMember = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPlatformTeamMember != null) {
            platformTeamMemberRepository.delete(insertedPlatformTeamMember);
            insertedPlatformTeamMember = null;
        }
    }

    @Test
    @Transactional
    void createPlatformTeamMember() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PlatformTeamMember
        PlatformTeamMemberDTO platformTeamMemberDTO = platformTeamMemberMapper.toDto(platformTeamMember);
        var returnedPlatformTeamMemberDTO = om.readValue(
            restPlatformTeamMemberMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(platformTeamMemberDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PlatformTeamMemberDTO.class
        );

        // Validate the PlatformTeamMember in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlatformTeamMember = platformTeamMemberMapper.toEntity(returnedPlatformTeamMemberDTO);
        assertPlatformTeamMemberUpdatableFieldsEquals(
            returnedPlatformTeamMember,
            getPersistedPlatformTeamMember(returnedPlatformTeamMember)
        );

        insertedPlatformTeamMember = returnedPlatformTeamMember;
    }

    @Test
    @Transactional
    void createPlatformTeamMemberWithExistingId() throws Exception {
        // Create the PlatformTeamMember with an existing ID
        platformTeamMember.setId(1L);
        PlatformTeamMemberDTO platformTeamMemberDTO = platformTeamMemberMapper.toDto(platformTeamMember);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlatformTeamMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(platformTeamMemberDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PlatformTeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRoleCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        platformTeamMember.setRoleCode(null);

        // Create the PlatformTeamMember, which fails.
        PlatformTeamMemberDTO platformTeamMemberDTO = platformTeamMemberMapper.toDto(platformTeamMember);

        restPlatformTeamMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(platformTeamMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        platformTeamMember.setCreatedAt(null);

        // Create the PlatformTeamMember, which fails.
        PlatformTeamMemberDTO platformTeamMemberDTO = platformTeamMemberMapper.toDto(platformTeamMember);

        restPlatformTeamMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(platformTeamMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        platformTeamMember.setUpdatedAt(null);

        // Create the PlatformTeamMember, which fails.
        PlatformTeamMemberDTO platformTeamMemberDTO = platformTeamMemberMapper.toDto(platformTeamMember);

        restPlatformTeamMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(platformTeamMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPlatformTeamMembers() throws Exception {
        // Initialize the database
        insertedPlatformTeamMember = platformTeamMemberRepository.saveAndFlush(platformTeamMember);

        // Get all the platformTeamMemberList
        restPlatformTeamMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(platformTeamMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].roleCode").value(hasItem(DEFAULT_ROLE_CODE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getPlatformTeamMember() throws Exception {
        // Initialize the database
        insertedPlatformTeamMember = platformTeamMemberRepository.saveAndFlush(platformTeamMember);

        // Get the platformTeamMember
        restPlatformTeamMemberMockMvc
            .perform(get(ENTITY_API_URL_ID, platformTeamMember.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(platformTeamMember.getId().intValue()))
            .andExpect(jsonPath("$.roleCode").value(DEFAULT_ROLE_CODE.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPlatformTeamMember() throws Exception {
        // Get the platformTeamMember
        restPlatformTeamMemberMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPlatformTeamMember() throws Exception {
        // Initialize the database
        insertedPlatformTeamMember = platformTeamMemberRepository.saveAndFlush(platformTeamMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the platformTeamMember
        PlatformTeamMember updatedPlatformTeamMember = platformTeamMemberRepository.findById(platformTeamMember.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPlatformTeamMember are not directly saved in db
        em.detach(updatedPlatformTeamMember);
        updatedPlatformTeamMember.roleCode(UPDATED_ROLE_CODE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
        PlatformTeamMemberDTO platformTeamMemberDTO = platformTeamMemberMapper.toDto(updatedPlatformTeamMember);

        restPlatformTeamMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, platformTeamMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(platformTeamMemberDTO))
            )
            .andExpect(status().isOk());

        // Validate the PlatformTeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlatformTeamMemberToMatchAllProperties(updatedPlatformTeamMember);
    }

    @Test
    @Transactional
    void putNonExistingPlatformTeamMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        platformTeamMember.setId(longCount.incrementAndGet());

        // Create the PlatformTeamMember
        PlatformTeamMemberDTO platformTeamMemberDTO = platformTeamMemberMapper.toDto(platformTeamMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlatformTeamMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, platformTeamMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(platformTeamMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlatformTeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlatformTeamMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        platformTeamMember.setId(longCount.incrementAndGet());

        // Create the PlatformTeamMember
        PlatformTeamMemberDTO platformTeamMemberDTO = platformTeamMemberMapper.toDto(platformTeamMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatformTeamMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(platformTeamMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlatformTeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlatformTeamMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        platformTeamMember.setId(longCount.incrementAndGet());

        // Create the PlatformTeamMember
        PlatformTeamMemberDTO platformTeamMemberDTO = platformTeamMemberMapper.toDto(platformTeamMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatformTeamMemberMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(platformTeamMemberDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlatformTeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlatformTeamMemberWithPatch() throws Exception {
        // Initialize the database
        insertedPlatformTeamMember = platformTeamMemberRepository.saveAndFlush(platformTeamMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the platformTeamMember using partial update
        PlatformTeamMember partialUpdatedPlatformTeamMember = new PlatformTeamMember();
        partialUpdatedPlatformTeamMember.setId(platformTeamMember.getId());

        partialUpdatedPlatformTeamMember.roleCode(UPDATED_ROLE_CODE).createdAt(UPDATED_CREATED_AT);

        restPlatformTeamMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlatformTeamMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlatformTeamMember))
            )
            .andExpect(status().isOk());

        // Validate the PlatformTeamMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlatformTeamMemberUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPlatformTeamMember, platformTeamMember),
            getPersistedPlatformTeamMember(platformTeamMember)
        );
    }

    @Test
    @Transactional
    void fullUpdatePlatformTeamMemberWithPatch() throws Exception {
        // Initialize the database
        insertedPlatformTeamMember = platformTeamMemberRepository.saveAndFlush(platformTeamMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the platformTeamMember using partial update
        PlatformTeamMember partialUpdatedPlatformTeamMember = new PlatformTeamMember();
        partialUpdatedPlatformTeamMember.setId(platformTeamMember.getId());

        partialUpdatedPlatformTeamMember.roleCode(UPDATED_ROLE_CODE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restPlatformTeamMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlatformTeamMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlatformTeamMember))
            )
            .andExpect(status().isOk());

        // Validate the PlatformTeamMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlatformTeamMemberUpdatableFieldsEquals(
            partialUpdatedPlatformTeamMember,
            getPersistedPlatformTeamMember(partialUpdatedPlatformTeamMember)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPlatformTeamMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        platformTeamMember.setId(longCount.incrementAndGet());

        // Create the PlatformTeamMember
        PlatformTeamMemberDTO platformTeamMemberDTO = platformTeamMemberMapper.toDto(platformTeamMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlatformTeamMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, platformTeamMemberDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(platformTeamMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlatformTeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlatformTeamMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        platformTeamMember.setId(longCount.incrementAndGet());

        // Create the PlatformTeamMember
        PlatformTeamMemberDTO platformTeamMemberDTO = platformTeamMemberMapper.toDto(platformTeamMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatformTeamMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(platformTeamMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlatformTeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlatformTeamMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        platformTeamMember.setId(longCount.incrementAndGet());

        // Create the PlatformTeamMember
        PlatformTeamMemberDTO platformTeamMemberDTO = platformTeamMemberMapper.toDto(platformTeamMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatformTeamMemberMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(platformTeamMemberDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlatformTeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlatformTeamMember() throws Exception {
        // Initialize the database
        insertedPlatformTeamMember = platformTeamMemberRepository.saveAndFlush(platformTeamMember);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the platformTeamMember
        restPlatformTeamMemberMockMvc
            .perform(delete(ENTITY_API_URL_ID, platformTeamMember.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return platformTeamMemberRepository.count();
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

    protected PlatformTeamMember getPersistedPlatformTeamMember(PlatformTeamMember platformTeamMember) {
        return platformTeamMemberRepository.findById(platformTeamMember.getId()).orElseThrow();
    }

    protected void assertPersistedPlatformTeamMemberToMatchAllProperties(PlatformTeamMember expectedPlatformTeamMember) {
        assertPlatformTeamMemberAllPropertiesEquals(expectedPlatformTeamMember, getPersistedPlatformTeamMember(expectedPlatformTeamMember));
    }

    protected void assertPersistedPlatformTeamMemberToMatchUpdatableProperties(PlatformTeamMember expectedPlatformTeamMember) {
        assertPlatformTeamMemberAllUpdatablePropertiesEquals(
            expectedPlatformTeamMember,
            getPersistedPlatformTeamMember(expectedPlatformTeamMember)
        );
    }
}
