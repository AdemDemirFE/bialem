package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.EventParticipantAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.EventParticipant;
import com.bialem.backend.domain.enumeration.EventParticipantStatus;
import com.bialem.backend.repository.EventParticipantRepository;
import com.bialem.backend.service.dto.EventParticipantDTO;
import com.bialem.backend.service.mapper.EventParticipantMapper;
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
 * Integration tests for the {@link EventParticipantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EventParticipantResourceIT {

    private static final EventParticipantStatus DEFAULT_STATUS = EventParticipantStatus.PENDING;
    private static final EventParticipantStatus UPDATED_STATUS = EventParticipantStatus.WAITLISTED;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/event-participants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private EventParticipantMapper eventParticipantMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventParticipantMockMvc;

    private EventParticipant eventParticipant;

    private EventParticipant insertedEventParticipant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventParticipant createEntity() {
        return new EventParticipant().status(DEFAULT_STATUS).note(DEFAULT_NOTE).createdAt(DEFAULT_CREATED_AT).updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventParticipant createUpdatedEntity() {
        return new EventParticipant().status(UPDATED_STATUS).note(UPDATED_NOTE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        eventParticipant = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEventParticipant != null) {
            eventParticipantRepository.delete(insertedEventParticipant);
            insertedEventParticipant = null;
        }
    }

    @Test
    @Transactional
    void createEventParticipant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EventParticipant
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);
        var returnedEventParticipantDTO = om.readValue(
            restEventParticipantMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventParticipantDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EventParticipantDTO.class
        );

        // Validate the EventParticipant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEventParticipant = eventParticipantMapper.toEntity(returnedEventParticipantDTO);
        assertEventParticipantUpdatableFieldsEquals(returnedEventParticipant, getPersistedEventParticipant(returnedEventParticipant));

        insertedEventParticipant = returnedEventParticipant;
    }

    @Test
    @Transactional
    void createEventParticipantWithExistingId() throws Exception {
        // Create the EventParticipant with an existing ID
        eventParticipant.setId(1L);
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventParticipantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventParticipantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventParticipant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventParticipant.setStatus(null);

        // Create the EventParticipant, which fails.
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);

        restEventParticipantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventParticipantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventParticipant.setCreatedAt(null);

        // Create the EventParticipant, which fails.
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);

        restEventParticipantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventParticipantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventParticipant.setUpdatedAt(null);

        // Create the EventParticipant, which fails.
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);

        restEventParticipantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventParticipantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEventParticipants() throws Exception {
        // Initialize the database
        insertedEventParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);

        // Get all the eventParticipantList
        restEventParticipantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventParticipant.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getEventParticipant() throws Exception {
        // Initialize the database
        insertedEventParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);

        // Get the eventParticipant
        restEventParticipantMockMvc
            .perform(get(ENTITY_API_URL_ID, eventParticipant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(eventParticipant.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingEventParticipant() throws Exception {
        // Get the eventParticipant
        restEventParticipantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEventParticipant() throws Exception {
        // Initialize the database
        insertedEventParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventParticipant
        EventParticipant updatedEventParticipant = eventParticipantRepository.findById(eventParticipant.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEventParticipant are not directly saved in db
        em.detach(updatedEventParticipant);
        updatedEventParticipant.status(UPDATED_STATUS).note(UPDATED_NOTE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(updatedEventParticipant);

        restEventParticipantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventParticipantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventParticipantDTO))
            )
            .andExpect(status().isOk());

        // Validate the EventParticipant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEventParticipantToMatchAllProperties(updatedEventParticipant);
    }

    @Test
    @Transactional
    void putNonExistingEventParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventParticipant.setId(longCount.incrementAndGet());

        // Create the EventParticipant
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventParticipantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventParticipantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventParticipantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventParticipant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEventParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventParticipant.setId(longCount.incrementAndGet());

        // Create the EventParticipant
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventParticipantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventParticipantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventParticipant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEventParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventParticipant.setId(longCount.incrementAndGet());

        // Create the EventParticipant
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventParticipantMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventParticipantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventParticipant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEventParticipantWithPatch() throws Exception {
        // Initialize the database
        insertedEventParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventParticipant using partial update
        EventParticipant partialUpdatedEventParticipant = new EventParticipant();
        partialUpdatedEventParticipant.setId(eventParticipant.getId());

        partialUpdatedEventParticipant.status(UPDATED_STATUS).note(UPDATED_NOTE).createdAt(UPDATED_CREATED_AT);

        restEventParticipantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventParticipant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventParticipant))
            )
            .andExpect(status().isOk());

        // Validate the EventParticipant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventParticipantUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEventParticipant, eventParticipant),
            getPersistedEventParticipant(eventParticipant)
        );
    }

    @Test
    @Transactional
    void fullUpdateEventParticipantWithPatch() throws Exception {
        // Initialize the database
        insertedEventParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventParticipant using partial update
        EventParticipant partialUpdatedEventParticipant = new EventParticipant();
        partialUpdatedEventParticipant.setId(eventParticipant.getId());

        partialUpdatedEventParticipant
            .status(UPDATED_STATUS)
            .note(UPDATED_NOTE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restEventParticipantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventParticipant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventParticipant))
            )
            .andExpect(status().isOk());

        // Validate the EventParticipant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventParticipantUpdatableFieldsEquals(
            partialUpdatedEventParticipant,
            getPersistedEventParticipant(partialUpdatedEventParticipant)
        );
    }

    @Test
    @Transactional
    void patchNonExistingEventParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventParticipant.setId(longCount.incrementAndGet());

        // Create the EventParticipant
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventParticipantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventParticipantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventParticipantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventParticipant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEventParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventParticipant.setId(longCount.incrementAndGet());

        // Create the EventParticipant
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventParticipantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventParticipantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventParticipant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEventParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventParticipant.setId(longCount.incrementAndGet());

        // Create the EventParticipant
        EventParticipantDTO eventParticipantDTO = eventParticipantMapper.toDto(eventParticipant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventParticipantMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(eventParticipantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventParticipant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEventParticipant() throws Exception {
        // Initialize the database
        insertedEventParticipant = eventParticipantRepository.saveAndFlush(eventParticipant);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the eventParticipant
        restEventParticipantMockMvc
            .perform(delete(ENTITY_API_URL_ID, eventParticipant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return eventParticipantRepository.count();
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

    protected EventParticipant getPersistedEventParticipant(EventParticipant eventParticipant) {
        return eventParticipantRepository.findById(eventParticipant.getId()).orElseThrow();
    }

    protected void assertPersistedEventParticipantToMatchAllProperties(EventParticipant expectedEventParticipant) {
        assertEventParticipantAllPropertiesEquals(expectedEventParticipant, getPersistedEventParticipant(expectedEventParticipant));
    }

    protected void assertPersistedEventParticipantToMatchUpdatableProperties(EventParticipant expectedEventParticipant) {
        assertEventParticipantAllUpdatablePropertiesEquals(
            expectedEventParticipant,
            getPersistedEventParticipant(expectedEventParticipant)
        );
    }
}
