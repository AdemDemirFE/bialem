package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.EventRatingAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.EventRating;
import com.bialem.backend.repository.EventRatingRepository;
import com.bialem.backend.service.dto.EventRatingDTO;
import com.bialem.backend.service.mapper.EventRatingMapper;
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
 * Integration tests for the {@link EventRatingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EventRatingResourceIT {

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;

    private static final String DEFAULT_REVIEW_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_REVIEW_TEXT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/event-ratings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EventRatingRepository eventRatingRepository;

    @Autowired
    private EventRatingMapper eventRatingMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventRatingMockMvc;

    private EventRating eventRating;

    private EventRating insertedEventRating;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventRating createEntity() {
        return new EventRating()
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
    public static EventRating createUpdatedEntity() {
        return new EventRating()
            .rating(UPDATED_RATING)
            .reviewText(UPDATED_REVIEW_TEXT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        eventRating = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEventRating != null) {
            eventRatingRepository.delete(insertedEventRating);
            insertedEventRating = null;
        }
    }

    @Test
    @Transactional
    void createEventRating() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EventRating
        EventRatingDTO eventRatingDTO = eventRatingMapper.toDto(eventRating);
        var returnedEventRatingDTO = om.readValue(
            restEventRatingMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventRatingDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EventRatingDTO.class
        );

        // Validate the EventRating in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEventRating = eventRatingMapper.toEntity(returnedEventRatingDTO);
        assertEventRatingUpdatableFieldsEquals(returnedEventRating, getPersistedEventRating(returnedEventRating));

        insertedEventRating = returnedEventRating;
    }

    @Test
    @Transactional
    void createEventRatingWithExistingId() throws Exception {
        // Create the EventRating with an existing ID
        eventRating.setId(1L);
        EventRatingDTO eventRatingDTO = eventRatingMapper.toDto(eventRating);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventRatingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventRatingDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventRating in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRatingIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventRating.setRating(null);

        // Create the EventRating, which fails.
        EventRatingDTO eventRatingDTO = eventRatingMapper.toDto(eventRating);

        restEventRatingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventRatingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventRating.setCreatedAt(null);

        // Create the EventRating, which fails.
        EventRatingDTO eventRatingDTO = eventRatingMapper.toDto(eventRating);

        restEventRatingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventRatingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventRating.setUpdatedAt(null);

        // Create the EventRating, which fails.
        EventRatingDTO eventRatingDTO = eventRatingMapper.toDto(eventRating);

        restEventRatingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventRatingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEventRatings() throws Exception {
        // Initialize the database
        insertedEventRating = eventRatingRepository.saveAndFlush(eventRating);

        // Get all the eventRatingList
        restEventRatingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventRating.getId().intValue())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].reviewText").value(hasItem(DEFAULT_REVIEW_TEXT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getEventRating() throws Exception {
        // Initialize the database
        insertedEventRating = eventRatingRepository.saveAndFlush(eventRating);

        // Get the eventRating
        restEventRatingMockMvc
            .perform(get(ENTITY_API_URL_ID, eventRating.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(eventRating.getId().intValue()))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.reviewText").value(DEFAULT_REVIEW_TEXT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingEventRating() throws Exception {
        // Get the eventRating
        restEventRatingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEventRating() throws Exception {
        // Initialize the database
        insertedEventRating = eventRatingRepository.saveAndFlush(eventRating);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventRating
        EventRating updatedEventRating = eventRatingRepository.findById(eventRating.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEventRating are not directly saved in db
        em.detach(updatedEventRating);
        updatedEventRating
            .rating(UPDATED_RATING)
            .reviewText(UPDATED_REVIEW_TEXT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        EventRatingDTO eventRatingDTO = eventRatingMapper.toDto(updatedEventRating);

        restEventRatingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventRatingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventRatingDTO))
            )
            .andExpect(status().isOk());

        // Validate the EventRating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEventRatingToMatchAllProperties(updatedEventRating);
    }

    @Test
    @Transactional
    void putNonExistingEventRating() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventRating.setId(longCount.incrementAndGet());

        // Create the EventRating
        EventRatingDTO eventRatingDTO = eventRatingMapper.toDto(eventRating);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventRatingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventRatingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventRatingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventRating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEventRating() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventRating.setId(longCount.incrementAndGet());

        // Create the EventRating
        EventRatingDTO eventRatingDTO = eventRatingMapper.toDto(eventRating);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventRatingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventRatingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventRating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEventRating() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventRating.setId(longCount.incrementAndGet());

        // Create the EventRating
        EventRatingDTO eventRatingDTO = eventRatingMapper.toDto(eventRating);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventRatingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventRatingDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventRating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEventRatingWithPatch() throws Exception {
        // Initialize the database
        insertedEventRating = eventRatingRepository.saveAndFlush(eventRating);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventRating using partial update
        EventRating partialUpdatedEventRating = new EventRating();
        partialUpdatedEventRating.setId(eventRating.getId());

        partialUpdatedEventRating
            .rating(UPDATED_RATING)
            .reviewText(UPDATED_REVIEW_TEXT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restEventRatingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventRating.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventRating))
            )
            .andExpect(status().isOk());

        // Validate the EventRating in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventRatingUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEventRating, eventRating),
            getPersistedEventRating(eventRating)
        );
    }

    @Test
    @Transactional
    void fullUpdateEventRatingWithPatch() throws Exception {
        // Initialize the database
        insertedEventRating = eventRatingRepository.saveAndFlush(eventRating);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventRating using partial update
        EventRating partialUpdatedEventRating = new EventRating();
        partialUpdatedEventRating.setId(eventRating.getId());

        partialUpdatedEventRating
            .rating(UPDATED_RATING)
            .reviewText(UPDATED_REVIEW_TEXT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restEventRatingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventRating.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventRating))
            )
            .andExpect(status().isOk());

        // Validate the EventRating in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventRatingUpdatableFieldsEquals(partialUpdatedEventRating, getPersistedEventRating(partialUpdatedEventRating));
    }

    @Test
    @Transactional
    void patchNonExistingEventRating() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventRating.setId(longCount.incrementAndGet());

        // Create the EventRating
        EventRatingDTO eventRatingDTO = eventRatingMapper.toDto(eventRating);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventRatingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventRatingDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventRatingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventRating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEventRating() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventRating.setId(longCount.incrementAndGet());

        // Create the EventRating
        EventRatingDTO eventRatingDTO = eventRatingMapper.toDto(eventRating);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventRatingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventRatingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventRating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEventRating() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventRating.setId(longCount.incrementAndGet());

        // Create the EventRating
        EventRatingDTO eventRatingDTO = eventRatingMapper.toDto(eventRating);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventRatingMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(eventRatingDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventRating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEventRating() throws Exception {
        // Initialize the database
        insertedEventRating = eventRatingRepository.saveAndFlush(eventRating);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the eventRating
        restEventRatingMockMvc
            .perform(delete(ENTITY_API_URL_ID, eventRating.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return eventRatingRepository.count();
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

    protected EventRating getPersistedEventRating(EventRating eventRating) {
        return eventRatingRepository.findById(eventRating.getId()).orElseThrow();
    }

    protected void assertPersistedEventRatingToMatchAllProperties(EventRating expectedEventRating) {
        assertEventRatingAllPropertiesEquals(expectedEventRating, getPersistedEventRating(expectedEventRating));
    }

    protected void assertPersistedEventRatingToMatchUpdatableProperties(EventRating expectedEventRating) {
        assertEventRatingAllUpdatablePropertiesEquals(expectedEventRating, getPersistedEventRating(expectedEventRating));
    }
}
