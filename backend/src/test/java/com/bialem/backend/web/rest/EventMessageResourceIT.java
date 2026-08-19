package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.EventMessageAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.EventMessage;
import com.bialem.backend.domain.enumeration.ModerationStatus;
import com.bialem.backend.repository.EventMessageRepository;
import com.bialem.backend.service.dto.EventMessageDTO;
import com.bialem.backend.service.mapper.EventMessageMapper;
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
 * Integration tests for the {@link EventMessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EventMessageResourceIT {

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final ModerationStatus DEFAULT_MODERATION_STATUS = ModerationStatus.VISIBLE;
    private static final ModerationStatus UPDATED_MODERATION_STATUS = ModerationStatus.HIDDEN;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/event-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EventMessageRepository eventMessageRepository;

    @Autowired
    private EventMessageMapper eventMessageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventMessageMockMvc;

    private EventMessage eventMessage;

    private EventMessage insertedEventMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventMessage createEntity() {
        return new EventMessage()
            .body(DEFAULT_BODY)
            .moderationStatus(DEFAULT_MODERATION_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventMessage createUpdatedEntity() {
        return new EventMessage()
            .body(UPDATED_BODY)
            .moderationStatus(UPDATED_MODERATION_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        eventMessage = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEventMessage != null) {
            eventMessageRepository.delete(insertedEventMessage);
            insertedEventMessage = null;
        }
    }

    @Test
    @Transactional
    void createEventMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EventMessage
        EventMessageDTO eventMessageDTO = eventMessageMapper.toDto(eventMessage);
        var returnedEventMessageDTO = om.readValue(
            restEventMessageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventMessageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EventMessageDTO.class
        );

        // Validate the EventMessage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEventMessage = eventMessageMapper.toEntity(returnedEventMessageDTO);
        assertEventMessageUpdatableFieldsEquals(returnedEventMessage, getPersistedEventMessage(returnedEventMessage));

        insertedEventMessage = returnedEventMessage;
    }

    @Test
    @Transactional
    void createEventMessageWithExistingId() throws Exception {
        // Create the EventMessage with an existing ID
        eventMessage.setId(1L);
        EventMessageDTO eventMessageDTO = eventMessageMapper.toDto(eventMessage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventMessageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBodyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventMessage.setBody(null);

        // Create the EventMessage, which fails.
        EventMessageDTO eventMessageDTO = eventMessageMapper.toDto(eventMessage);

        restEventMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkModerationStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventMessage.setModerationStatus(null);

        // Create the EventMessage, which fails.
        EventMessageDTO eventMessageDTO = eventMessageMapper.toDto(eventMessage);

        restEventMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventMessage.setCreatedAt(null);

        // Create the EventMessage, which fails.
        EventMessageDTO eventMessageDTO = eventMessageMapper.toDto(eventMessage);

        restEventMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventMessage.setUpdatedAt(null);

        // Create the EventMessage, which fails.
        EventMessageDTO eventMessageDTO = eventMessageMapper.toDto(eventMessage);

        restEventMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEventMessages() throws Exception {
        // Initialize the database
        insertedEventMessage = eventMessageRepository.saveAndFlush(eventMessage);

        // Get all the eventMessageList
        restEventMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].moderationStatus").value(hasItem(DEFAULT_MODERATION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getEventMessage() throws Exception {
        // Initialize the database
        insertedEventMessage = eventMessageRepository.saveAndFlush(eventMessage);

        // Get the eventMessage
        restEventMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, eventMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(eventMessage.getId().intValue()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY))
            .andExpect(jsonPath("$.moderationStatus").value(DEFAULT_MODERATION_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingEventMessage() throws Exception {
        // Get the eventMessage
        restEventMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEventMessage() throws Exception {
        // Initialize the database
        insertedEventMessage = eventMessageRepository.saveAndFlush(eventMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventMessage
        EventMessage updatedEventMessage = eventMessageRepository.findById(eventMessage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEventMessage are not directly saved in db
        em.detach(updatedEventMessage);
        updatedEventMessage
            .body(UPDATED_BODY)
            .moderationStatus(UPDATED_MODERATION_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        EventMessageDTO eventMessageDTO = eventMessageMapper.toDto(updatedEventMessage);

        restEventMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the EventMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEventMessageToMatchAllProperties(updatedEventMessage);
    }

    @Test
    @Transactional
    void putNonExistingEventMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventMessage.setId(longCount.incrementAndGet());

        // Create the EventMessage
        EventMessageDTO eventMessageDTO = eventMessageMapper.toDto(eventMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEventMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventMessage.setId(longCount.incrementAndGet());

        // Create the EventMessage
        EventMessageDTO eventMessageDTO = eventMessageMapper.toDto(eventMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEventMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventMessage.setId(longCount.incrementAndGet());

        // Create the EventMessage
        EventMessageDTO eventMessageDTO = eventMessageMapper.toDto(eventMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMessageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEventMessageWithPatch() throws Exception {
        // Initialize the database
        insertedEventMessage = eventMessageRepository.saveAndFlush(eventMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventMessage using partial update
        EventMessage partialUpdatedEventMessage = new EventMessage();
        partialUpdatedEventMessage.setId(eventMessage.getId());

        partialUpdatedEventMessage.body(UPDATED_BODY).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restEventMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventMessage))
            )
            .andExpect(status().isOk());

        // Validate the EventMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventMessageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEventMessage, eventMessage),
            getPersistedEventMessage(eventMessage)
        );
    }

    @Test
    @Transactional
    void fullUpdateEventMessageWithPatch() throws Exception {
        // Initialize the database
        insertedEventMessage = eventMessageRepository.saveAndFlush(eventMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventMessage using partial update
        EventMessage partialUpdatedEventMessage = new EventMessage();
        partialUpdatedEventMessage.setId(eventMessage.getId());

        partialUpdatedEventMessage
            .body(UPDATED_BODY)
            .moderationStatus(UPDATED_MODERATION_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restEventMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventMessage))
            )
            .andExpect(status().isOk());

        // Validate the EventMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventMessageUpdatableFieldsEquals(partialUpdatedEventMessage, getPersistedEventMessage(partialUpdatedEventMessage));
    }

    @Test
    @Transactional
    void patchNonExistingEventMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventMessage.setId(longCount.incrementAndGet());

        // Create the EventMessage
        EventMessageDTO eventMessageDTO = eventMessageMapper.toDto(eventMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventMessageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEventMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventMessage.setId(longCount.incrementAndGet());

        // Create the EventMessage
        EventMessageDTO eventMessageDTO = eventMessageMapper.toDto(eventMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEventMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventMessage.setId(longCount.incrementAndGet());

        // Create the EventMessage
        EventMessageDTO eventMessageDTO = eventMessageMapper.toDto(eventMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMessageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(eventMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEventMessage() throws Exception {
        // Initialize the database
        insertedEventMessage = eventMessageRepository.saveAndFlush(eventMessage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the eventMessage
        restEventMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, eventMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return eventMessageRepository.count();
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

    protected EventMessage getPersistedEventMessage(EventMessage eventMessage) {
        return eventMessageRepository.findById(eventMessage.getId()).orElseThrow();
    }

    protected void assertPersistedEventMessageToMatchAllProperties(EventMessage expectedEventMessage) {
        assertEventMessageAllPropertiesEquals(expectedEventMessage, getPersistedEventMessage(expectedEventMessage));
    }

    protected void assertPersistedEventMessageToMatchUpdatableProperties(EventMessage expectedEventMessage) {
        assertEventMessageAllUpdatablePropertiesEquals(expectedEventMessage, getPersistedEventMessage(expectedEventMessage));
    }
}
