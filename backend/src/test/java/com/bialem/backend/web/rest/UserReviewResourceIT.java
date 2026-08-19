package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.UserReviewAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.UserReview;
import com.bialem.backend.repository.UserReviewRepository;
import com.bialem.backend.service.dto.UserReviewDTO;
import com.bialem.backend.service.mapper.UserReviewMapper;
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
 * Integration tests for the {@link UserReviewResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserReviewResourceIT {

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;

    private static final String DEFAULT_REVIEW_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_REVIEW_TEXT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/user-reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserReviewRepository userReviewRepository;

    @Autowired
    private UserReviewMapper userReviewMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserReviewMockMvc;

    private UserReview userReview;

    private UserReview insertedUserReview;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserReview createEntity() {
        return new UserReview()
            .rating(DEFAULT_RATING)
            .reviewText(DEFAULT_REVIEW_TEXT)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserReview createUpdatedEntity() {
        return new UserReview()
            .rating(UPDATED_RATING)
            .reviewText(UPDATED_REVIEW_TEXT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        userReview = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserReview != null) {
            userReviewRepository.delete(insertedUserReview);
            insertedUserReview = null;
        }
    }

    @Test
    @Transactional
    void createUserReview() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);
        var returnedUserReviewDTO = om.readValue(
            restUserReviewMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userReviewDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserReviewDTO.class
        );

        // Validate the UserReview in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserReview = userReviewMapper.toEntity(returnedUserReviewDTO);
        assertUserReviewUpdatableFieldsEquals(returnedUserReview, getPersistedUserReview(returnedUserReview));

        insertedUserReview = returnedUserReview;
    }

    @Test
    @Transactional
    void createUserReviewWithExistingId() throws Exception {
        // Create the UserReview with an existing ID
        userReview.setId(1L);
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userReviewDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserReview in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRatingIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userReview.setRating(null);

        // Create the UserReview, which fails.
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        restUserReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userReviewDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userReview.setCreatedAt(null);

        // Create the UserReview, which fails.
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        restUserReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userReviewDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userReview.setUpdatedAt(null);

        // Create the UserReview, which fails.
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        restUserReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userReviewDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserReviews() throws Exception {
        // Initialize the database
        insertedUserReview = userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList
        restUserReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userReview.getId().intValue())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].reviewText").value(hasItem(DEFAULT_REVIEW_TEXT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getUserReview() throws Exception {
        // Initialize the database
        insertedUserReview = userReviewRepository.saveAndFlush(userReview);

        // Get the userReview
        restUserReviewMockMvc
            .perform(get(ENTITY_API_URL_ID, userReview.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userReview.getId().intValue()))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.reviewText").value(DEFAULT_REVIEW_TEXT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUserReview() throws Exception {
        // Get the userReview
        restUserReviewMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserReview() throws Exception {
        // Initialize the database
        insertedUserReview = userReviewRepository.saveAndFlush(userReview);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userReview
        UserReview updatedUserReview = userReviewRepository.findById(userReview.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserReview are not directly saved in db
        em.detach(updatedUserReview);
        updatedUserReview
            .rating(UPDATED_RATING)
            .reviewText(UPDATED_REVIEW_TEXT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(updatedUserReview);

        restUserReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userReviewDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userReviewDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserReviewToMatchAllProperties(updatedUserReview);
    }

    @Test
    @Transactional
    void putNonExistingUserReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userReview.setId(longCount.incrementAndGet());

        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userReviewDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userReview.setId(longCount.incrementAndGet());

        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userReview.setId(longCount.incrementAndGet());

        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserReviewMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userReviewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserReviewWithPatch() throws Exception {
        // Initialize the database
        insertedUserReview = userReviewRepository.saveAndFlush(userReview);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userReview using partial update
        UserReview partialUpdatedUserReview = new UserReview();
        partialUpdatedUserReview.setId(userReview.getId());

        restUserReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserReview))
            )
            .andExpect(status().isOk());

        // Validate the UserReview in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserReviewUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserReview, userReview),
            getPersistedUserReview(userReview)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserReviewWithPatch() throws Exception {
        // Initialize the database
        insertedUserReview = userReviewRepository.saveAndFlush(userReview);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userReview using partial update
        UserReview partialUpdatedUserReview = new UserReview();
        partialUpdatedUserReview.setId(userReview.getId());

        partialUpdatedUserReview
            .rating(UPDATED_RATING)
            .reviewText(UPDATED_REVIEW_TEXT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restUserReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserReview))
            )
            .andExpect(status().isOk());

        // Validate the UserReview in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserReviewUpdatableFieldsEquals(partialUpdatedUserReview, getPersistedUserReview(partialUpdatedUserReview));
    }

    @Test
    @Transactional
    void patchNonExistingUserReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userReview.setId(longCount.incrementAndGet());

        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userReviewDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userReview.setId(longCount.incrementAndGet());

        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userReview.setId(longCount.incrementAndGet());

        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserReviewMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userReviewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserReview() throws Exception {
        // Initialize the database
        insertedUserReview = userReviewRepository.saveAndFlush(userReview);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userReview
        restUserReviewMockMvc
            .perform(delete(ENTITY_API_URL_ID, userReview.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userReviewRepository.count();
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

    protected UserReview getPersistedUserReview(UserReview userReview) {
        return userReviewRepository.findById(userReview.getId()).orElseThrow();
    }

    protected void assertPersistedUserReviewToMatchAllProperties(UserReview expectedUserReview) {
        assertUserReviewAllPropertiesEquals(expectedUserReview, getPersistedUserReview(expectedUserReview));
    }

    protected void assertPersistedUserReviewToMatchUpdatableProperties(UserReview expectedUserReview) {
        assertUserReviewAllUpdatablePropertiesEquals(expectedUserReview, getPersistedUserReview(expectedUserReview));
    }
}
