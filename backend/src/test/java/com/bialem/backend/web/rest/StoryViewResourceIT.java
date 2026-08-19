package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.StoryViewAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.StoryView;
import com.bialem.backend.repository.StoryViewRepository;
import com.bialem.backend.service.dto.StoryViewDTO;
import com.bialem.backend.service.mapper.StoryViewMapper;
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
 * Integration tests for the {@link StoryViewResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StoryViewResourceIT {

    private static final Instant DEFAULT_VIEWED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VIEWED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/story-views";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StoryViewRepository storyViewRepository;

    @Autowired
    private StoryViewMapper storyViewMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStoryViewMockMvc;

    private StoryView storyView;

    private StoryView insertedStoryView;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StoryView createEntity() {
        return new StoryView().viewedAt(DEFAULT_VIEWED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StoryView createUpdatedEntity() {
        return new StoryView().viewedAt(UPDATED_VIEWED_AT);
    }

    @BeforeEach
    void initTest() {
        storyView = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStoryView != null) {
            storyViewRepository.delete(insertedStoryView);
            insertedStoryView = null;
        }
    }

    @Test
    @Transactional
    void createStoryView() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StoryView
        StoryViewDTO storyViewDTO = storyViewMapper.toDto(storyView);
        var returnedStoryViewDTO = om.readValue(
            restStoryViewMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyViewDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StoryViewDTO.class
        );

        // Validate the StoryView in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStoryView = storyViewMapper.toEntity(returnedStoryViewDTO);
        assertStoryViewUpdatableFieldsEquals(returnedStoryView, getPersistedStoryView(returnedStoryView));

        insertedStoryView = returnedStoryView;
    }

    @Test
    @Transactional
    void createStoryViewWithExistingId() throws Exception {
        // Create the StoryView with an existing ID
        storyView.setId(1L);
        StoryViewDTO storyViewDTO = storyViewMapper.toDto(storyView);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStoryViewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyViewDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StoryView in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkViewedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        storyView.setViewedAt(null);

        // Create the StoryView, which fails.
        StoryViewDTO storyViewDTO = storyViewMapper.toDto(storyView);

        restStoryViewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyViewDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStoryViews() throws Exception {
        // Initialize the database
        insertedStoryView = storyViewRepository.saveAndFlush(storyView);

        // Get all the storyViewList
        restStoryViewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(storyView.getId().intValue())))
            .andExpect(jsonPath("$.[*].viewedAt").value(hasItem(DEFAULT_VIEWED_AT.toString())));
    }

    @Test
    @Transactional
    void getStoryView() throws Exception {
        // Initialize the database
        insertedStoryView = storyViewRepository.saveAndFlush(storyView);

        // Get the storyView
        restStoryViewMockMvc
            .perform(get(ENTITY_API_URL_ID, storyView.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(storyView.getId().intValue()))
            .andExpect(jsonPath("$.viewedAt").value(DEFAULT_VIEWED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingStoryView() throws Exception {
        // Get the storyView
        restStoryViewMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStoryView() throws Exception {
        // Initialize the database
        insertedStoryView = storyViewRepository.saveAndFlush(storyView);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the storyView
        StoryView updatedStoryView = storyViewRepository.findById(storyView.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStoryView are not directly saved in db
        em.detach(updatedStoryView);
        updatedStoryView.viewedAt(UPDATED_VIEWED_AT);
        StoryViewDTO storyViewDTO = storyViewMapper.toDto(updatedStoryView);

        restStoryViewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, storyViewDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(storyViewDTO))
            )
            .andExpect(status().isOk());

        // Validate the StoryView in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStoryViewToMatchAllProperties(updatedStoryView);
    }

    @Test
    @Transactional
    void putNonExistingStoryView() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storyView.setId(longCount.incrementAndGet());

        // Create the StoryView
        StoryViewDTO storyViewDTO = storyViewMapper.toDto(storyView);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStoryViewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, storyViewDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(storyViewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StoryView in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStoryView() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storyView.setId(longCount.incrementAndGet());

        // Create the StoryView
        StoryViewDTO storyViewDTO = storyViewMapper.toDto(storyView);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoryViewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(storyViewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StoryView in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStoryView() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storyView.setId(longCount.incrementAndGet());

        // Create the StoryView
        StoryViewDTO storyViewDTO = storyViewMapper.toDto(storyView);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoryViewMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyViewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StoryView in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStoryViewWithPatch() throws Exception {
        // Initialize the database
        insertedStoryView = storyViewRepository.saveAndFlush(storyView);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the storyView using partial update
        StoryView partialUpdatedStoryView = new StoryView();
        partialUpdatedStoryView.setId(storyView.getId());

        partialUpdatedStoryView.viewedAt(UPDATED_VIEWED_AT);

        restStoryViewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStoryView.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStoryView))
            )
            .andExpect(status().isOk());

        // Validate the StoryView in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStoryViewUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStoryView, storyView),
            getPersistedStoryView(storyView)
        );
    }

    @Test
    @Transactional
    void fullUpdateStoryViewWithPatch() throws Exception {
        // Initialize the database
        insertedStoryView = storyViewRepository.saveAndFlush(storyView);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the storyView using partial update
        StoryView partialUpdatedStoryView = new StoryView();
        partialUpdatedStoryView.setId(storyView.getId());

        partialUpdatedStoryView.viewedAt(UPDATED_VIEWED_AT);

        restStoryViewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStoryView.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStoryView))
            )
            .andExpect(status().isOk());

        // Validate the StoryView in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStoryViewUpdatableFieldsEquals(partialUpdatedStoryView, getPersistedStoryView(partialUpdatedStoryView));
    }

    @Test
    @Transactional
    void patchNonExistingStoryView() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storyView.setId(longCount.incrementAndGet());

        // Create the StoryView
        StoryViewDTO storyViewDTO = storyViewMapper.toDto(storyView);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStoryViewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, storyViewDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(storyViewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StoryView in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStoryView() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storyView.setId(longCount.incrementAndGet());

        // Create the StoryView
        StoryViewDTO storyViewDTO = storyViewMapper.toDto(storyView);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoryViewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(storyViewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StoryView in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStoryView() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storyView.setId(longCount.incrementAndGet());

        // Create the StoryView
        StoryViewDTO storyViewDTO = storyViewMapper.toDto(storyView);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoryViewMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(storyViewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StoryView in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStoryView() throws Exception {
        // Initialize the database
        insertedStoryView = storyViewRepository.saveAndFlush(storyView);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the storyView
        restStoryViewMockMvc
            .perform(delete(ENTITY_API_URL_ID, storyView.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return storyViewRepository.count();
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

    protected StoryView getPersistedStoryView(StoryView storyView) {
        return storyViewRepository.findById(storyView.getId()).orElseThrow();
    }

    protected void assertPersistedStoryViewToMatchAllProperties(StoryView expectedStoryView) {
        assertStoryViewAllPropertiesEquals(expectedStoryView, getPersistedStoryView(expectedStoryView));
    }

    protected void assertPersistedStoryViewToMatchUpdatableProperties(StoryView expectedStoryView) {
        assertStoryViewAllUpdatablePropertiesEquals(expectedStoryView, getPersistedStoryView(expectedStoryView));
    }
}
