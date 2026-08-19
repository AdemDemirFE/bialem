package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.StoryAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.Story;
import com.bialem.backend.domain.enumeration.StoryContentType;
import com.bialem.backend.repository.StoryRepository;
import com.bialem.backend.service.dto.StoryDTO;
import com.bialem.backend.service.mapper.StoryMapper;
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
 * Integration tests for the {@link StoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StoryResourceIT {

    private static final StoryContentType DEFAULT_CONTENT_TYPE = StoryContentType.TEXT;
    private static final StoryContentType UPDATED_CONTENT_TYPE = StoryContentType.IMAGE;

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final String DEFAULT_MEDIA_URL = "AAAAAAAAAA";
    private static final String UPDATED_MEDIA_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_PUBLIC = false;
    private static final Boolean UPDATED_IS_PUBLIC = true;

    private static final Boolean DEFAULT_SHARE_WITH_FOLLOWERS = false;
    private static final Boolean UPDATED_SHARE_WITH_FOLLOWERS = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/stories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private StoryMapper storyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStoryMockMvc;

    private Story story;

    private Story insertedStory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Story createEntity() {
        return new Story()
            .contentType(DEFAULT_CONTENT_TYPE)
            .body(DEFAULT_BODY)
            .mediaUrl(DEFAULT_MEDIA_URL)
            .isPublic(DEFAULT_IS_PUBLIC)
            .shareWithFollowers(DEFAULT_SHARE_WITH_FOLLOWERS)
            .createdAt(DEFAULT_CREATED_AT)
            .expiresAt(DEFAULT_EXPIRES_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Story createUpdatedEntity() {
        return new Story()
            .contentType(UPDATED_CONTENT_TYPE)
            .body(UPDATED_BODY)
            .mediaUrl(UPDATED_MEDIA_URL)
            .isPublic(UPDATED_IS_PUBLIC)
            .shareWithFollowers(UPDATED_SHARE_WITH_FOLLOWERS)
            .createdAt(UPDATED_CREATED_AT)
            .expiresAt(UPDATED_EXPIRES_AT);
    }

    @BeforeEach
    void initTest() {
        story = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStory != null) {
            storyRepository.delete(insertedStory);
            insertedStory = null;
        }
    }

    @Test
    @Transactional
    void createStory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Story
        StoryDTO storyDTO = storyMapper.toDto(story);
        var returnedStoryDTO = om.readValue(
            restStoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StoryDTO.class
        );

        // Validate the Story in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStory = storyMapper.toEntity(returnedStoryDTO);
        assertStoryUpdatableFieldsEquals(returnedStory, getPersistedStory(returnedStory));

        insertedStory = returnedStory;
    }

    @Test
    @Transactional
    void createStoryWithExistingId() throws Exception {
        // Create the Story with an existing ID
        story.setId(1L);
        StoryDTO storyDTO = storyMapper.toDto(story);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Story in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkContentTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        story.setContentType(null);

        // Create the Story, which fails.
        StoryDTO storyDTO = storyMapper.toDto(story);

        restStoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsPublicIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        story.setIsPublic(null);

        // Create the Story, which fails.
        StoryDTO storyDTO = storyMapper.toDto(story);

        restStoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkShareWithFollowersIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        story.setShareWithFollowers(null);

        // Create the Story, which fails.
        StoryDTO storyDTO = storyMapper.toDto(story);

        restStoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        story.setCreatedAt(null);

        // Create the Story, which fails.
        StoryDTO storyDTO = storyMapper.toDto(story);

        restStoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExpiresAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        story.setExpiresAt(null);

        // Create the Story, which fails.
        StoryDTO storyDTO = storyMapper.toDto(story);

        restStoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStories() throws Exception {
        // Initialize the database
        insertedStory = storyRepository.saveAndFlush(story);

        // Get all the storyList
        restStoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(story.getId().intValue())))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].mediaUrl").value(hasItem(DEFAULT_MEDIA_URL)))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC)))
            .andExpect(jsonPath("$.[*].shareWithFollowers").value(hasItem(DEFAULT_SHARE_WITH_FOLLOWERS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())));
    }

    @Test
    @Transactional
    void getStory() throws Exception {
        // Initialize the database
        insertedStory = storyRepository.saveAndFlush(story);

        // Get the story
        restStoryMockMvc
            .perform(get(ENTITY_API_URL_ID, story.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(story.getId().intValue()))
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE.toString()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY))
            .andExpect(jsonPath("$.mediaUrl").value(DEFAULT_MEDIA_URL))
            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC))
            .andExpect(jsonPath("$.shareWithFollowers").value(DEFAULT_SHARE_WITH_FOLLOWERS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.expiresAt").value(DEFAULT_EXPIRES_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingStory() throws Exception {
        // Get the story
        restStoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStory() throws Exception {
        // Initialize the database
        insertedStory = storyRepository.saveAndFlush(story);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the story
        Story updatedStory = storyRepository.findById(story.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStory are not directly saved in db
        em.detach(updatedStory);
        updatedStory
            .contentType(UPDATED_CONTENT_TYPE)
            .body(UPDATED_BODY)
            .mediaUrl(UPDATED_MEDIA_URL)
            .isPublic(UPDATED_IS_PUBLIC)
            .shareWithFollowers(UPDATED_SHARE_WITH_FOLLOWERS)
            .createdAt(UPDATED_CREATED_AT)
            .expiresAt(UPDATED_EXPIRES_AT);
        StoryDTO storyDTO = storyMapper.toDto(updatedStory);

        restStoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, storyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Story in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStoryToMatchAllProperties(updatedStory);
    }

    @Test
    @Transactional
    void putNonExistingStory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        story.setId(longCount.incrementAndGet());

        // Create the Story
        StoryDTO storyDTO = storyMapper.toDto(story);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, storyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Story in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        story.setId(longCount.incrementAndGet());

        // Create the Story
        StoryDTO storyDTO = storyMapper.toDto(story);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(storyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Story in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        story.setId(longCount.incrementAndGet());

        // Create the Story
        StoryDTO storyDTO = storyMapper.toDto(story);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Story in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStoryWithPatch() throws Exception {
        // Initialize the database
        insertedStory = storyRepository.saveAndFlush(story);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the story using partial update
        Story partialUpdatedStory = new Story();
        partialUpdatedStory.setId(story.getId());

        partialUpdatedStory.createdAt(UPDATED_CREATED_AT);

        restStoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStory))
            )
            .andExpect(status().isOk());

        // Validate the Story in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStoryUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedStory, story), getPersistedStory(story));
    }

    @Test
    @Transactional
    void fullUpdateStoryWithPatch() throws Exception {
        // Initialize the database
        insertedStory = storyRepository.saveAndFlush(story);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the story using partial update
        Story partialUpdatedStory = new Story();
        partialUpdatedStory.setId(story.getId());

        partialUpdatedStory
            .contentType(UPDATED_CONTENT_TYPE)
            .body(UPDATED_BODY)
            .mediaUrl(UPDATED_MEDIA_URL)
            .isPublic(UPDATED_IS_PUBLIC)
            .shareWithFollowers(UPDATED_SHARE_WITH_FOLLOWERS)
            .createdAt(UPDATED_CREATED_AT)
            .expiresAt(UPDATED_EXPIRES_AT);

        restStoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStory))
            )
            .andExpect(status().isOk());

        // Validate the Story in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStoryUpdatableFieldsEquals(partialUpdatedStory, getPersistedStory(partialUpdatedStory));
    }

    @Test
    @Transactional
    void patchNonExistingStory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        story.setId(longCount.incrementAndGet());

        // Create the Story
        StoryDTO storyDTO = storyMapper.toDto(story);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, storyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(storyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Story in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        story.setId(longCount.incrementAndGet());

        // Create the Story
        StoryDTO storyDTO = storyMapper.toDto(story);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(storyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Story in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        story.setId(longCount.incrementAndGet());

        // Create the Story
        StoryDTO storyDTO = storyMapper.toDto(story);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(storyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Story in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStory() throws Exception {
        // Initialize the database
        insertedStory = storyRepository.saveAndFlush(story);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the story
        restStoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, story.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return storyRepository.count();
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

    protected Story getPersistedStory(Story story) {
        return storyRepository.findById(story.getId()).orElseThrow();
    }

    protected void assertPersistedStoryToMatchAllProperties(Story expectedStory) {
        assertStoryAllPropertiesEquals(expectedStory, getPersistedStory(expectedStory));
    }

    protected void assertPersistedStoryToMatchUpdatableProperties(Story expectedStory) {
        assertStoryAllUpdatablePropertiesEquals(expectedStory, getPersistedStory(expectedStory));
    }
}
