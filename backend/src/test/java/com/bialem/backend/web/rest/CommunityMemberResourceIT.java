package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.CommunityMemberAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.CommunityMember;
import com.bialem.backend.domain.enumeration.CommunityMemberRole;
import com.bialem.backend.domain.enumeration.CommunityMemberStatus;
import com.bialem.backend.repository.CommunityMemberRepository;
import com.bialem.backend.service.dto.CommunityMemberDTO;
import com.bialem.backend.service.mapper.CommunityMemberMapper;
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
 * Integration tests for the {@link CommunityMemberResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CommunityMemberResourceIT {

    private static final CommunityMemberRole DEFAULT_ROLE = CommunityMemberRole.MEMBER;
    private static final CommunityMemberRole UPDATED_ROLE = CommunityMemberRole.MANAGER;

    private static final CommunityMemberStatus DEFAULT_STATUS = CommunityMemberStatus.PENDING;
    private static final CommunityMemberStatus UPDATED_STATUS = CommunityMemberStatus.APPROVED;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/community-members";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CommunityMemberRepository communityMemberRepository;

    @Autowired
    private CommunityMemberMapper communityMemberMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommunityMemberMockMvc;

    private CommunityMember communityMember;

    private CommunityMember insertedCommunityMember;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommunityMember createEntity() {
        return new CommunityMember().role(DEFAULT_ROLE).status(DEFAULT_STATUS).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommunityMember createUpdatedEntity() {
        return new CommunityMember().role(UPDATED_ROLE).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        communityMember = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCommunityMember != null) {
            communityMemberRepository.delete(insertedCommunityMember);
            insertedCommunityMember = null;
        }
    }

    @Test
    @Transactional
    void createCommunityMember() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CommunityMember
        CommunityMemberDTO communityMemberDTO = communityMemberMapper.toDto(communityMember);
        var returnedCommunityMemberDTO = om.readValue(
            restCommunityMemberMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityMemberDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CommunityMemberDTO.class
        );

        // Validate the CommunityMember in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCommunityMember = communityMemberMapper.toEntity(returnedCommunityMemberDTO);
        assertCommunityMemberUpdatableFieldsEquals(returnedCommunityMember, getPersistedCommunityMember(returnedCommunityMember));

        insertedCommunityMember = returnedCommunityMember;
    }

    @Test
    @Transactional
    void createCommunityMemberWithExistingId() throws Exception {
        // Create the CommunityMember with an existing ID
        communityMember.setId(1L);
        CommunityMemberDTO communityMemberDTO = communityMemberMapper.toDto(communityMember);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommunityMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityMemberDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CommunityMember in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        communityMember.setRole(null);

        // Create the CommunityMember, which fails.
        CommunityMemberDTO communityMemberDTO = communityMemberMapper.toDto(communityMember);

        restCommunityMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        communityMember.setStatus(null);

        // Create the CommunityMember, which fails.
        CommunityMemberDTO communityMemberDTO = communityMemberMapper.toDto(communityMember);

        restCommunityMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        communityMember.setCreatedAt(null);

        // Create the CommunityMember, which fails.
        CommunityMemberDTO communityMemberDTO = communityMemberMapper.toDto(communityMember);

        restCommunityMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCommunityMembers() throws Exception {
        // Initialize the database
        insertedCommunityMember = communityMemberRepository.saveAndFlush(communityMember);

        // Get all the communityMemberList
        restCommunityMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(communityMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCommunityMember() throws Exception {
        // Initialize the database
        insertedCommunityMember = communityMemberRepository.saveAndFlush(communityMember);

        // Get the communityMember
        restCommunityMemberMockMvc
            .perform(get(ENTITY_API_URL_ID, communityMember.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(communityMember.getId().intValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCommunityMember() throws Exception {
        // Get the communityMember
        restCommunityMemberMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCommunityMember() throws Exception {
        // Initialize the database
        insertedCommunityMember = communityMemberRepository.saveAndFlush(communityMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the communityMember
        CommunityMember updatedCommunityMember = communityMemberRepository.findById(communityMember.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCommunityMember are not directly saved in db
        em.detach(updatedCommunityMember);
        updatedCommunityMember.role(UPDATED_ROLE).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);
        CommunityMemberDTO communityMemberDTO = communityMemberMapper.toDto(updatedCommunityMember);

        restCommunityMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, communityMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(communityMemberDTO))
            )
            .andExpect(status().isOk());

        // Validate the CommunityMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCommunityMemberToMatchAllProperties(updatedCommunityMember);
    }

    @Test
    @Transactional
    void putNonExistingCommunityMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityMember.setId(longCount.incrementAndGet());

        // Create the CommunityMember
        CommunityMemberDTO communityMemberDTO = communityMemberMapper.toDto(communityMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommunityMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, communityMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(communityMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCommunityMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityMember.setId(longCount.incrementAndGet());

        // Create the CommunityMember
        CommunityMemberDTO communityMemberDTO = communityMemberMapper.toDto(communityMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(communityMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCommunityMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityMember.setId(longCount.incrementAndGet());

        // Create the CommunityMember
        CommunityMemberDTO communityMemberDTO = communityMemberMapper.toDto(communityMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityMemberMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityMemberDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CommunityMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommunityMemberWithPatch() throws Exception {
        // Initialize the database
        insertedCommunityMember = communityMemberRepository.saveAndFlush(communityMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the communityMember using partial update
        CommunityMember partialUpdatedCommunityMember = new CommunityMember();
        partialUpdatedCommunityMember.setId(communityMember.getId());

        partialUpdatedCommunityMember.status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);

        restCommunityMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommunityMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCommunityMember))
            )
            .andExpect(status().isOk());

        // Validate the CommunityMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommunityMemberUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCommunityMember, communityMember),
            getPersistedCommunityMember(communityMember)
        );
    }

    @Test
    @Transactional
    void fullUpdateCommunityMemberWithPatch() throws Exception {
        // Initialize the database
        insertedCommunityMember = communityMemberRepository.saveAndFlush(communityMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the communityMember using partial update
        CommunityMember partialUpdatedCommunityMember = new CommunityMember();
        partialUpdatedCommunityMember.setId(communityMember.getId());

        partialUpdatedCommunityMember.role(UPDATED_ROLE).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);

        restCommunityMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommunityMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCommunityMember))
            )
            .andExpect(status().isOk());

        // Validate the CommunityMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommunityMemberUpdatableFieldsEquals(
            partialUpdatedCommunityMember,
            getPersistedCommunityMember(partialUpdatedCommunityMember)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCommunityMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityMember.setId(longCount.incrementAndGet());

        // Create the CommunityMember
        CommunityMemberDTO communityMemberDTO = communityMemberMapper.toDto(communityMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommunityMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, communityMemberDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(communityMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCommunityMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityMember.setId(longCount.incrementAndGet());

        // Create the CommunityMember
        CommunityMemberDTO communityMemberDTO = communityMemberMapper.toDto(communityMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(communityMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommunityMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCommunityMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        communityMember.setId(longCount.incrementAndGet());

        // Create the CommunityMember
        CommunityMemberDTO communityMemberDTO = communityMemberMapper.toDto(communityMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityMemberMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(communityMemberDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CommunityMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCommunityMember() throws Exception {
        // Initialize the database
        insertedCommunityMember = communityMemberRepository.saveAndFlush(communityMember);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the communityMember
        restCommunityMemberMockMvc
            .perform(delete(ENTITY_API_URL_ID, communityMember.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return communityMemberRepository.count();
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

    protected CommunityMember getPersistedCommunityMember(CommunityMember communityMember) {
        return communityMemberRepository.findById(communityMember.getId()).orElseThrow();
    }

    protected void assertPersistedCommunityMemberToMatchAllProperties(CommunityMember expectedCommunityMember) {
        assertCommunityMemberAllPropertiesEquals(expectedCommunityMember, getPersistedCommunityMember(expectedCommunityMember));
    }

    protected void assertPersistedCommunityMemberToMatchUpdatableProperties(CommunityMember expectedCommunityMember) {
        assertCommunityMemberAllUpdatablePropertiesEquals(expectedCommunityMember, getPersistedCommunityMember(expectedCommunityMember));
    }
}
