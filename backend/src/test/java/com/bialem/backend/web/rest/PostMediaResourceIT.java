package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.PostMediaAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.PostMedia;
import com.bialem.backend.domain.enumeration.MediaType;
import com.bialem.backend.repository.PostMediaRepository;
import com.bialem.backend.service.dto.PostMediaDTO;
import com.bialem.backend.service.mapper.PostMediaMapper;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PostMediaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PostMediaResourceIT {

    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.IMAGE;
    private static final MediaType UPDATED_MEDIA_TYPE = MediaType.VIDEO;

    private static final String DEFAULT_STORAGE_PATH = "AAAAAAAAAA";
    private static final String UPDATED_STORAGE_PATH = "BBBBBBBBBB";

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/post-medias";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PostMediaRepository postMediaRepository;

    @Autowired
    private PostMediaMapper postMediaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPostMediaMockMvc;

    private PostMedia postMedia;

    private PostMedia insertedPostMedia;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostMedia createEntity() {
        return new PostMedia()
            .mediaType(DEFAULT_MEDIA_TYPE)
            .storagePath(DEFAULT_STORAGE_PATH)
            .sortOrder(DEFAULT_SORT_ORDER)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostMedia createUpdatedEntity() {
        return new PostMedia()
            .mediaType(UPDATED_MEDIA_TYPE)
            .storagePath(UPDATED_STORAGE_PATH)
            .sortOrder(UPDATED_SORT_ORDER)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        postMedia = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPostMedia != null) {
            postMediaRepository.delete(insertedPostMedia);
            insertedPostMedia = null;
        }
    }

    @Test
    @Transactional
    void createPostMedia() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PostMedia
        PostMediaDTO postMediaDTO = postMediaMapper.toDto(postMedia);
        var returnedPostMediaDTO = om.readValue(
            restPostMediaMockMvc
                .perform(post(ENTITY_API_URL).contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postMediaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PostMediaDTO.class
        );

        // Validate the PostMedia in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPostMedia = postMediaMapper.toEntity(returnedPostMediaDTO);
        assertPostMediaUpdatableFieldsEquals(returnedPostMedia, getPersistedPostMedia(returnedPostMedia));

        insertedPostMedia = returnedPostMedia;
    }

    @Test
    @Transactional
    void createPostMediaWithExistingId() throws Exception {
        // Create the PostMedia with an existing ID
        postMedia.setId(1L);
        PostMediaDTO postMediaDTO = postMediaMapper.toDto(postMedia);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostMediaMockMvc
            .perform(post(ENTITY_API_URL).contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postMediaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PostMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMediaTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        postMedia.setMediaType(null);

        // Create the PostMedia, which fails.
        PostMediaDTO postMediaDTO = postMediaMapper.toDto(postMedia);

        restPostMediaMockMvc
            .perform(post(ENTITY_API_URL).contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postMediaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStoragePathIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        postMedia.setStoragePath(null);

        // Create the PostMedia, which fails.
        PostMediaDTO postMediaDTO = postMediaMapper.toDto(postMedia);

        restPostMediaMockMvc
            .perform(post(ENTITY_API_URL).contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postMediaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSortOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        postMedia.setSortOrder(null);

        // Create the PostMedia, which fails.
        PostMediaDTO postMediaDTO = postMediaMapper.toDto(postMedia);

        restPostMediaMockMvc
            .perform(post(ENTITY_API_URL).contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postMediaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        postMedia.setCreatedAt(null);

        // Create the PostMedia, which fails.
        PostMediaDTO postMediaDTO = postMediaMapper.toDto(postMedia);

        restPostMediaMockMvc
            .perform(post(ENTITY_API_URL).contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postMediaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPostMedias() throws Exception {
        // Initialize the database
        insertedPostMedia = postMediaRepository.saveAndFlush(postMedia);

        // Get all the postMediaList
        restPostMediaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(postMedia.getId().intValue())))
            .andExpect(jsonPath("$.[*].mediaType").value(hasItem(DEFAULT_MEDIA_TYPE.toString())))
            .andExpect(jsonPath("$.[*].storagePath").value(hasItem(DEFAULT_STORAGE_PATH)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getPostMedia() throws Exception {
        // Initialize the database
        insertedPostMedia = postMediaRepository.saveAndFlush(postMedia);

        // Get the postMedia
        restPostMediaMockMvc
            .perform(get(ENTITY_API_URL_ID, postMedia.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(postMedia.getId().intValue()))
            .andExpect(jsonPath("$.mediaType").value(DEFAULT_MEDIA_TYPE.toString()))
            .andExpect(jsonPath("$.storagePath").value(DEFAULT_STORAGE_PATH))
            .andExpect(jsonPath("$.sortOrder").value(DEFAULT_SORT_ORDER))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPostMedia() throws Exception {
        // Get the postMedia
        restPostMediaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPostMedia() throws Exception {
        // Initialize the database
        insertedPostMedia = postMediaRepository.saveAndFlush(postMedia);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the postMedia
        PostMedia updatedPostMedia = postMediaRepository.findById(postMedia.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPostMedia are not directly saved in db
        em.detach(updatedPostMedia);
        updatedPostMedia
            .mediaType(UPDATED_MEDIA_TYPE)
            .storagePath(UPDATED_STORAGE_PATH)
            .sortOrder(UPDATED_SORT_ORDER)
            .createdAt(UPDATED_CREATED_AT);
        PostMediaDTO postMediaDTO = postMediaMapper.toDto(updatedPostMedia);

        restPostMediaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postMediaDTO.getId())
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(postMediaDTO))
            )
            .andExpect(status().isOk());

        // Validate the PostMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPostMediaToMatchAllProperties(updatedPostMedia);
    }

    @Test
    @Transactional
    void putNonExistingPostMedia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        postMedia.setId(longCount.incrementAndGet());

        // Create the PostMedia
        PostMediaDTO postMediaDTO = postMediaMapper.toDto(postMedia);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostMediaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postMediaDTO.getId())
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(postMediaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPostMedia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        postMedia.setId(longCount.incrementAndGet());

        // Create the PostMedia
        PostMediaDTO postMediaDTO = postMediaMapper.toDto(postMedia);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMediaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(postMediaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPostMedia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        postMedia.setId(longCount.incrementAndGet());

        // Create the PostMedia
        PostMediaDTO postMediaDTO = postMediaMapper.toDto(postMedia);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMediaMockMvc
            .perform(put(ENTITY_API_URL).contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postMediaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PostMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePostMediaWithPatch() throws Exception {
        // Initialize the database
        insertedPostMedia = postMediaRepository.saveAndFlush(postMedia);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the postMedia using partial update
        PostMedia partialUpdatedPostMedia = new PostMedia();
        partialUpdatedPostMedia.setId(postMedia.getId());

        partialUpdatedPostMedia.mediaType(UPDATED_MEDIA_TYPE).sortOrder(UPDATED_SORT_ORDER);

        restPostMediaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPostMedia.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPostMedia))
            )
            .andExpect(status().isOk());

        // Validate the PostMedia in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostMediaUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPostMedia, postMedia),
            getPersistedPostMedia(postMedia)
        );
    }

    @Test
    @Transactional
    void fullUpdatePostMediaWithPatch() throws Exception {
        // Initialize the database
        insertedPostMedia = postMediaRepository.saveAndFlush(postMedia);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the postMedia using partial update
        PostMedia partialUpdatedPostMedia = new PostMedia();
        partialUpdatedPostMedia.setId(postMedia.getId());

        partialUpdatedPostMedia
            .mediaType(UPDATED_MEDIA_TYPE)
            .storagePath(UPDATED_STORAGE_PATH)
            .sortOrder(UPDATED_SORT_ORDER)
            .createdAt(UPDATED_CREATED_AT);

        restPostMediaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPostMedia.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPostMedia))
            )
            .andExpect(status().isOk());

        // Validate the PostMedia in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostMediaUpdatableFieldsEquals(partialUpdatedPostMedia, getPersistedPostMedia(partialUpdatedPostMedia));
    }

    @Test
    @Transactional
    void patchNonExistingPostMedia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        postMedia.setId(longCount.incrementAndGet());

        // Create the PostMedia
        PostMediaDTO postMediaDTO = postMediaMapper.toDto(postMedia);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostMediaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, postMediaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(postMediaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPostMedia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        postMedia.setId(longCount.incrementAndGet());

        // Create the PostMedia
        PostMediaDTO postMediaDTO = postMediaMapper.toDto(postMedia);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMediaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(postMediaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPostMedia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        postMedia.setId(longCount.incrementAndGet());

        // Create the PostMedia
        PostMediaDTO postMediaDTO = postMediaMapper.toDto(postMedia);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMediaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(postMediaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PostMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePostMedia() throws Exception {
        // Initialize the database
        insertedPostMedia = postMediaRepository.saveAndFlush(postMedia);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the postMedia
        restPostMediaMockMvc
            .perform(delete(ENTITY_API_URL_ID, postMedia.getId()).accept(org.springframework.http.MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return postMediaRepository.count();
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

    protected PostMedia getPersistedPostMedia(PostMedia postMedia) {
        return postMediaRepository.findById(postMedia.getId()).orElseThrow();
    }

    protected void assertPersistedPostMediaToMatchAllProperties(PostMedia expectedPostMedia) {
        assertPostMediaAllPropertiesEquals(expectedPostMedia, getPersistedPostMedia(expectedPostMedia));
    }

    protected void assertPersistedPostMediaToMatchUpdatableProperties(PostMedia expectedPostMedia) {
        assertPostMediaAllUpdatablePropertiesEquals(expectedPostMedia, getPersistedPostMedia(expectedPostMedia));
    }
}
