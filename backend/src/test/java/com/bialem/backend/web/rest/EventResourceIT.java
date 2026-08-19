package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.EventAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static com.bialem.backend.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.Event;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.enumeration.EventStatus;
import com.bialem.backend.domain.enumeration.GroupModerationStatus;
import com.bialem.backend.domain.enumeration.PlatformModerationStatus;
import com.bialem.backend.repository.EventRepository;
import com.bialem.backend.service.dto.EventDTO;
import com.bialem.backend.service.mapper.EventMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link EventResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EventResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_STARTS_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTS_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ENDS_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENDS_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LOCATION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_TEXT = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_LATITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LATITUDE = new BigDecimal(2);
    private static final BigDecimal SMALLER_LATITUDE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_LONGITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LONGITUDE = new BigDecimal(2);
    private static final BigDecimal SMALLER_LONGITUDE = new BigDecimal(1 - 1);

    private static final String DEFAULT_COVER_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_COVER_IMAGE_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_CAPACITY = 1;
    private static final Integer UPDATED_CAPACITY = 2;
    private static final Integer SMALLER_CAPACITY = 1 - 1;

    private static final EventStatus DEFAULT_STATUS = EventStatus.DRAFT;
    private static final EventStatus UPDATED_STATUS = EventStatus.PENDING_APPROVAL;

    private static final String DEFAULT_REJECTION_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REJECTION_REASON = "BBBBBBBBBB";

    private static final Instant DEFAULT_PUBLISHED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLISHED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_PUBLISHED_TO_DISCOVERY = false;
    private static final Boolean UPDATED_PUBLISHED_TO_DISCOVERY = true;

    private static final GroupModerationStatus DEFAULT_GROUP_MODERATION_STATUS = GroupModerationStatus.PENDING;
    private static final GroupModerationStatus UPDATED_GROUP_MODERATION_STATUS = GroupModerationStatus.APPROVED;

    private static final PlatformModerationStatus DEFAULT_PLATFORM_MODERATION_STATUS = PlatformModerationStatus.NOT_REQUIRED;
    private static final PlatformModerationStatus UPDATED_PLATFORM_MODERATION_STATUS = PlatformModerationStatus.PENDING;

    private static final Instant DEFAULT_CANCELLED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CANCELLED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CANCELLATION_REASON = "AAAAAAAAAA";
    private static final String UPDATED_CANCELLATION_REASON = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventMockMvc;

    private Event event;

    private Event insertedEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createEntity() {
        return new Event()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .startsAt(DEFAULT_STARTS_AT)
            .endsAt(DEFAULT_ENDS_AT)
            .locationName(DEFAULT_LOCATION_NAME)
            .addressText(DEFAULT_ADDRESS_TEXT)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .coverImageUrl(DEFAULT_COVER_IMAGE_URL)
            .capacity(DEFAULT_CAPACITY)
            .status(DEFAULT_STATUS)
            .rejectionReason(DEFAULT_REJECTION_REASON)
            .publishedAt(DEFAULT_PUBLISHED_AT)
            .publishedToDiscovery(DEFAULT_PUBLISHED_TO_DISCOVERY)
            .groupModerationStatus(DEFAULT_GROUP_MODERATION_STATUS)
            .platformModerationStatus(DEFAULT_PLATFORM_MODERATION_STATUS)
            .cancelledAt(DEFAULT_CANCELLED_AT)
            .cancellationReason(DEFAULT_CANCELLATION_REASON)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createUpdatedEntity() {
        return new Event()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .startsAt(UPDATED_STARTS_AT)
            .endsAt(UPDATED_ENDS_AT)
            .locationName(UPDATED_LOCATION_NAME)
            .addressText(UPDATED_ADDRESS_TEXT)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .capacity(UPDATED_CAPACITY)
            .status(UPDATED_STATUS)
            .rejectionReason(UPDATED_REJECTION_REASON)
            .publishedAt(UPDATED_PUBLISHED_AT)
            .publishedToDiscovery(UPDATED_PUBLISHED_TO_DISCOVERY)
            .groupModerationStatus(UPDATED_GROUP_MODERATION_STATUS)
            .platformModerationStatus(UPDATED_PLATFORM_MODERATION_STATUS)
            .cancelledAt(UPDATED_CANCELLED_AT)
            .cancellationReason(UPDATED_CANCELLATION_REASON)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        event = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEvent != null) {
            eventRepository.delete(insertedEvent);
            insertedEvent = null;
        }
    }

    @Test
    @Transactional
    void createEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);
        var returnedEventDTO = om.readValue(
            restEventMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EventDTO.class
        );

        // Validate the Event in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEvent = eventMapper.toEntity(returnedEventDTO);
        assertEventUpdatableFieldsEquals(returnedEvent, getPersistedEvent(returnedEvent));

        insertedEvent = returnedEvent;
    }

    @Test
    @Transactional
    void createEventWithExistingId() throws Exception {
        // Create the Event with an existing ID
        event.setId(1L);
        EventDTO eventDTO = eventMapper.toDto(event);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        event.setTitle(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartsAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        event.setStartsAt(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        event.setStatus(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPublishedToDiscoveryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        event.setPublishedToDiscovery(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGroupModerationStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        event.setGroupModerationStatus(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPlatformModerationStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        event.setPlatformModerationStatus(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        event.setCreatedAt(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        event.setUpdatedAt(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEvents() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].startsAt").value(hasItem(DEFAULT_STARTS_AT.toString())))
            .andExpect(jsonPath("$.[*].endsAt").value(hasItem(DEFAULT_ENDS_AT.toString())))
            .andExpect(jsonPath("$.[*].locationName").value(hasItem(DEFAULT_LOCATION_NAME)))
            .andExpect(jsonPath("$.[*].addressText").value(hasItem(DEFAULT_ADDRESS_TEXT)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(sameNumber(DEFAULT_LATITUDE))))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(sameNumber(DEFAULT_LONGITUDE))))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].rejectionReason").value(hasItem(DEFAULT_REJECTION_REASON)))
            .andExpect(jsonPath("$.[*].publishedAt").value(hasItem(DEFAULT_PUBLISHED_AT.toString())))
            .andExpect(jsonPath("$.[*].publishedToDiscovery").value(hasItem(DEFAULT_PUBLISHED_TO_DISCOVERY)))
            .andExpect(jsonPath("$.[*].groupModerationStatus").value(hasItem(DEFAULT_GROUP_MODERATION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].platformModerationStatus").value(hasItem(DEFAULT_PLATFORM_MODERATION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].cancelledAt").value(hasItem(DEFAULT_CANCELLED_AT.toString())))
            .andExpect(jsonPath("$.[*].cancellationReason").value(hasItem(DEFAULT_CANCELLATION_REASON)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getEvent() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get the event
        restEventMockMvc
            .perform(get(ENTITY_API_URL_ID, event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(event.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.startsAt").value(DEFAULT_STARTS_AT.toString()))
            .andExpect(jsonPath("$.endsAt").value(DEFAULT_ENDS_AT.toString()))
            .andExpect(jsonPath("$.locationName").value(DEFAULT_LOCATION_NAME))
            .andExpect(jsonPath("$.addressText").value(DEFAULT_ADDRESS_TEXT))
            .andExpect(jsonPath("$.latitude").value(sameNumber(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.longitude").value(sameNumber(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.coverImageUrl").value(DEFAULT_COVER_IMAGE_URL))
            .andExpect(jsonPath("$.capacity").value(DEFAULT_CAPACITY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.rejectionReason").value(DEFAULT_REJECTION_REASON))
            .andExpect(jsonPath("$.publishedAt").value(DEFAULT_PUBLISHED_AT.toString()))
            .andExpect(jsonPath("$.publishedToDiscovery").value(DEFAULT_PUBLISHED_TO_DISCOVERY))
            .andExpect(jsonPath("$.groupModerationStatus").value(DEFAULT_GROUP_MODERATION_STATUS.toString()))
            .andExpect(jsonPath("$.platformModerationStatus").value(DEFAULT_PLATFORM_MODERATION_STATUS.toString()))
            .andExpect(jsonPath("$.cancelledAt").value(DEFAULT_CANCELLED_AT.toString()))
            .andExpect(jsonPath("$.cancellationReason").value(DEFAULT_CANCELLATION_REASON))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getEventsByIdFiltering() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        Long id = event.getId();

        defaultEventFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEventFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEventFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEventsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where title equals to
        defaultEventFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEventsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where title in
        defaultEventFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEventsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where title is not null
        defaultEventFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where title contains
        defaultEventFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEventsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where title does not contain
        defaultEventFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllEventsByStartsAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where startsAt equals to
        defaultEventFiltering("startsAt.equals=" + DEFAULT_STARTS_AT, "startsAt.equals=" + UPDATED_STARTS_AT);
    }

    @Test
    @Transactional
    void getAllEventsByStartsAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where startsAt in
        defaultEventFiltering("startsAt.in=" + DEFAULT_STARTS_AT + "," + UPDATED_STARTS_AT, "startsAt.in=" + UPDATED_STARTS_AT);
    }

    @Test
    @Transactional
    void getAllEventsByStartsAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where startsAt is not null
        defaultEventFiltering("startsAt.specified=true", "startsAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByEndsAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where endsAt equals to
        defaultEventFiltering("endsAt.equals=" + DEFAULT_ENDS_AT, "endsAt.equals=" + UPDATED_ENDS_AT);
    }

    @Test
    @Transactional
    void getAllEventsByEndsAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where endsAt in
        defaultEventFiltering("endsAt.in=" + DEFAULT_ENDS_AT + "," + UPDATED_ENDS_AT, "endsAt.in=" + UPDATED_ENDS_AT);
    }

    @Test
    @Transactional
    void getAllEventsByEndsAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where endsAt is not null
        defaultEventFiltering("endsAt.specified=true", "endsAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByLocationNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where locationName equals to
        defaultEventFiltering("locationName.equals=" + DEFAULT_LOCATION_NAME, "locationName.equals=" + UPDATED_LOCATION_NAME);
    }

    @Test
    @Transactional
    void getAllEventsByLocationNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where locationName in
        defaultEventFiltering(
            "locationName.in=" + DEFAULT_LOCATION_NAME + "," + UPDATED_LOCATION_NAME,
            "locationName.in=" + UPDATED_LOCATION_NAME
        );
    }

    @Test
    @Transactional
    void getAllEventsByLocationNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where locationName is not null
        defaultEventFiltering("locationName.specified=true", "locationName.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByLocationNameContainsSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where locationName contains
        defaultEventFiltering("locationName.contains=" + DEFAULT_LOCATION_NAME, "locationName.contains=" + UPDATED_LOCATION_NAME);
    }

    @Test
    @Transactional
    void getAllEventsByLocationNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where locationName does not contain
        defaultEventFiltering(
            "locationName.doesNotContain=" + UPDATED_LOCATION_NAME,
            "locationName.doesNotContain=" + DEFAULT_LOCATION_NAME
        );
    }

    @Test
    @Transactional
    void getAllEventsByAddressTextIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where addressText equals to
        defaultEventFiltering("addressText.equals=" + DEFAULT_ADDRESS_TEXT, "addressText.equals=" + UPDATED_ADDRESS_TEXT);
    }

    @Test
    @Transactional
    void getAllEventsByAddressTextIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where addressText in
        defaultEventFiltering(
            "addressText.in=" + DEFAULT_ADDRESS_TEXT + "," + UPDATED_ADDRESS_TEXT,
            "addressText.in=" + UPDATED_ADDRESS_TEXT
        );
    }

    @Test
    @Transactional
    void getAllEventsByAddressTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where addressText is not null
        defaultEventFiltering("addressText.specified=true", "addressText.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByAddressTextContainsSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where addressText contains
        defaultEventFiltering("addressText.contains=" + DEFAULT_ADDRESS_TEXT, "addressText.contains=" + UPDATED_ADDRESS_TEXT);
    }

    @Test
    @Transactional
    void getAllEventsByAddressTextNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where addressText does not contain
        defaultEventFiltering("addressText.doesNotContain=" + UPDATED_ADDRESS_TEXT, "addressText.doesNotContain=" + DEFAULT_ADDRESS_TEXT);
    }

    @Test
    @Transactional
    void getAllEventsByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where latitude equals to
        defaultEventFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllEventsByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where latitude in
        defaultEventFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllEventsByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where latitude is not null
        defaultEventFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where latitude is greater than or equal to
        defaultEventFiltering("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE, "latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllEventsByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where latitude is less than or equal to
        defaultEventFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllEventsByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where latitude is less than
        defaultEventFiltering("latitude.lessThan=" + UPDATED_LATITUDE, "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllEventsByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where latitude is greater than
        defaultEventFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllEventsByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where longitude equals to
        defaultEventFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllEventsByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where longitude in
        defaultEventFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllEventsByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where longitude is not null
        defaultEventFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where longitude is greater than or equal to
        defaultEventFiltering("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllEventsByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where longitude is less than or equal to
        defaultEventFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllEventsByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where longitude is less than
        defaultEventFiltering("longitude.lessThan=" + UPDATED_LONGITUDE, "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllEventsByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where longitude is greater than
        defaultEventFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllEventsByCoverImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where coverImageUrl equals to
        defaultEventFiltering("coverImageUrl.equals=" + DEFAULT_COVER_IMAGE_URL, "coverImageUrl.equals=" + UPDATED_COVER_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllEventsByCoverImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where coverImageUrl in
        defaultEventFiltering(
            "coverImageUrl.in=" + DEFAULT_COVER_IMAGE_URL + "," + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.in=" + UPDATED_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllEventsByCoverImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where coverImageUrl is not null
        defaultEventFiltering("coverImageUrl.specified=true", "coverImageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByCoverImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where coverImageUrl contains
        defaultEventFiltering("coverImageUrl.contains=" + DEFAULT_COVER_IMAGE_URL, "coverImageUrl.contains=" + UPDATED_COVER_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllEventsByCoverImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where coverImageUrl does not contain
        defaultEventFiltering(
            "coverImageUrl.doesNotContain=" + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.doesNotContain=" + DEFAULT_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllEventsByCapacityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where capacity equals to
        defaultEventFiltering("capacity.equals=" + DEFAULT_CAPACITY, "capacity.equals=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllEventsByCapacityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where capacity in
        defaultEventFiltering("capacity.in=" + DEFAULT_CAPACITY + "," + UPDATED_CAPACITY, "capacity.in=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllEventsByCapacityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where capacity is not null
        defaultEventFiltering("capacity.specified=true", "capacity.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByCapacityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where capacity is greater than or equal to
        defaultEventFiltering("capacity.greaterThanOrEqual=" + DEFAULT_CAPACITY, "capacity.greaterThanOrEqual=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllEventsByCapacityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where capacity is less than or equal to
        defaultEventFiltering("capacity.lessThanOrEqual=" + DEFAULT_CAPACITY, "capacity.lessThanOrEqual=" + SMALLER_CAPACITY);
    }

    @Test
    @Transactional
    void getAllEventsByCapacityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where capacity is less than
        defaultEventFiltering("capacity.lessThan=" + UPDATED_CAPACITY, "capacity.lessThan=" + DEFAULT_CAPACITY);
    }

    @Test
    @Transactional
    void getAllEventsByCapacityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where capacity is greater than
        defaultEventFiltering("capacity.greaterThan=" + SMALLER_CAPACITY, "capacity.greaterThan=" + DEFAULT_CAPACITY);
    }

    @Test
    @Transactional
    void getAllEventsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where status equals to
        defaultEventFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEventsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where status in
        defaultEventFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEventsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where status is not null
        defaultEventFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByRejectionReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where rejectionReason equals to
        defaultEventFiltering("rejectionReason.equals=" + DEFAULT_REJECTION_REASON, "rejectionReason.equals=" + UPDATED_REJECTION_REASON);
    }

    @Test
    @Transactional
    void getAllEventsByRejectionReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where rejectionReason in
        defaultEventFiltering(
            "rejectionReason.in=" + DEFAULT_REJECTION_REASON + "," + UPDATED_REJECTION_REASON,
            "rejectionReason.in=" + UPDATED_REJECTION_REASON
        );
    }

    @Test
    @Transactional
    void getAllEventsByRejectionReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where rejectionReason is not null
        defaultEventFiltering("rejectionReason.specified=true", "rejectionReason.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByRejectionReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where rejectionReason contains
        defaultEventFiltering(
            "rejectionReason.contains=" + DEFAULT_REJECTION_REASON,
            "rejectionReason.contains=" + UPDATED_REJECTION_REASON
        );
    }

    @Test
    @Transactional
    void getAllEventsByRejectionReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where rejectionReason does not contain
        defaultEventFiltering(
            "rejectionReason.doesNotContain=" + UPDATED_REJECTION_REASON,
            "rejectionReason.doesNotContain=" + DEFAULT_REJECTION_REASON
        );
    }

    @Test
    @Transactional
    void getAllEventsByPublishedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where publishedAt equals to
        defaultEventFiltering("publishedAt.equals=" + DEFAULT_PUBLISHED_AT, "publishedAt.equals=" + UPDATED_PUBLISHED_AT);
    }

    @Test
    @Transactional
    void getAllEventsByPublishedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where publishedAt in
        defaultEventFiltering(
            "publishedAt.in=" + DEFAULT_PUBLISHED_AT + "," + UPDATED_PUBLISHED_AT,
            "publishedAt.in=" + UPDATED_PUBLISHED_AT
        );
    }

    @Test
    @Transactional
    void getAllEventsByPublishedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where publishedAt is not null
        defaultEventFiltering("publishedAt.specified=true", "publishedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByPublishedToDiscoveryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where publishedToDiscovery equals to
        defaultEventFiltering(
            "publishedToDiscovery.equals=" + DEFAULT_PUBLISHED_TO_DISCOVERY,
            "publishedToDiscovery.equals=" + UPDATED_PUBLISHED_TO_DISCOVERY
        );
    }

    @Test
    @Transactional
    void getAllEventsByPublishedToDiscoveryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where publishedToDiscovery in
        defaultEventFiltering(
            "publishedToDiscovery.in=" + DEFAULT_PUBLISHED_TO_DISCOVERY + "," + UPDATED_PUBLISHED_TO_DISCOVERY,
            "publishedToDiscovery.in=" + UPDATED_PUBLISHED_TO_DISCOVERY
        );
    }

    @Test
    @Transactional
    void getAllEventsByPublishedToDiscoveryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where publishedToDiscovery is not null
        defaultEventFiltering("publishedToDiscovery.specified=true", "publishedToDiscovery.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByGroupModerationStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where groupModerationStatus equals to
        defaultEventFiltering(
            "groupModerationStatus.equals=" + DEFAULT_GROUP_MODERATION_STATUS,
            "groupModerationStatus.equals=" + UPDATED_GROUP_MODERATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllEventsByGroupModerationStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where groupModerationStatus in
        defaultEventFiltering(
            "groupModerationStatus.in=" + DEFAULT_GROUP_MODERATION_STATUS + "," + UPDATED_GROUP_MODERATION_STATUS,
            "groupModerationStatus.in=" + UPDATED_GROUP_MODERATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllEventsByGroupModerationStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where groupModerationStatus is not null
        defaultEventFiltering("groupModerationStatus.specified=true", "groupModerationStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByPlatformModerationStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where platformModerationStatus equals to
        defaultEventFiltering(
            "platformModerationStatus.equals=" + DEFAULT_PLATFORM_MODERATION_STATUS,
            "platformModerationStatus.equals=" + UPDATED_PLATFORM_MODERATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllEventsByPlatformModerationStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where platformModerationStatus in
        defaultEventFiltering(
            "platformModerationStatus.in=" + DEFAULT_PLATFORM_MODERATION_STATUS + "," + UPDATED_PLATFORM_MODERATION_STATUS,
            "platformModerationStatus.in=" + UPDATED_PLATFORM_MODERATION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllEventsByPlatformModerationStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where platformModerationStatus is not null
        defaultEventFiltering("platformModerationStatus.specified=true", "platformModerationStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByCancelledAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where cancelledAt equals to
        defaultEventFiltering("cancelledAt.equals=" + DEFAULT_CANCELLED_AT, "cancelledAt.equals=" + UPDATED_CANCELLED_AT);
    }

    @Test
    @Transactional
    void getAllEventsByCancelledAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where cancelledAt in
        defaultEventFiltering(
            "cancelledAt.in=" + DEFAULT_CANCELLED_AT + "," + UPDATED_CANCELLED_AT,
            "cancelledAt.in=" + UPDATED_CANCELLED_AT
        );
    }

    @Test
    @Transactional
    void getAllEventsByCancelledAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where cancelledAt is not null
        defaultEventFiltering("cancelledAt.specified=true", "cancelledAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByCancellationReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where cancellationReason equals to
        defaultEventFiltering(
            "cancellationReason.equals=" + DEFAULT_CANCELLATION_REASON,
            "cancellationReason.equals=" + UPDATED_CANCELLATION_REASON
        );
    }

    @Test
    @Transactional
    void getAllEventsByCancellationReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where cancellationReason in
        defaultEventFiltering(
            "cancellationReason.in=" + DEFAULT_CANCELLATION_REASON + "," + UPDATED_CANCELLATION_REASON,
            "cancellationReason.in=" + UPDATED_CANCELLATION_REASON
        );
    }

    @Test
    @Transactional
    void getAllEventsByCancellationReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where cancellationReason is not null
        defaultEventFiltering("cancellationReason.specified=true", "cancellationReason.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByCancellationReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where cancellationReason contains
        defaultEventFiltering(
            "cancellationReason.contains=" + DEFAULT_CANCELLATION_REASON,
            "cancellationReason.contains=" + UPDATED_CANCELLATION_REASON
        );
    }

    @Test
    @Transactional
    void getAllEventsByCancellationReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where cancellationReason does not contain
        defaultEventFiltering(
            "cancellationReason.doesNotContain=" + UPDATED_CANCELLATION_REASON,
            "cancellationReason.doesNotContain=" + DEFAULT_CANCELLATION_REASON
        );
    }

    @Test
    @Transactional
    void getAllEventsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where createdAt equals to
        defaultEventFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllEventsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where createdAt in
        defaultEventFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllEventsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where createdAt is not null
        defaultEventFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where updatedAt equals to
        defaultEventFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllEventsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where updatedAt in
        defaultEventFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllEventsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        // Get all the eventList where updatedAt is not null
        defaultEventFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByCommunityIsEqualToSomething() throws Exception {
        Community community;
        if (TestUtil.findAll(em, Community.class).isEmpty()) {
            eventRepository.saveAndFlush(event);
            community = CommunityResourceIT.createEntity();
        } else {
            community = TestUtil.findAll(em, Community.class).get(0);
        }
        em.persist(community);
        em.flush();
        event.setCommunity(community);
        eventRepository.saveAndFlush(event);
        Long communityId = community.getId();
        // Get all the eventList where community equals to communityId
        defaultEventShouldBeFound("communityId.equals=" + communityId);

        // Get all the eventList where community equals to (communityId + 1)
        defaultEventShouldNotBeFound("communityId.equals=" + (communityId + 1));
    }

    @Test
    @Transactional
    void getAllEventsByCategoryIsEqualToSomething() throws Exception {
        Community category;
        if (TestUtil.findAll(em, Community.class).isEmpty()) {
            eventRepository.saveAndFlush(event);
            category = CommunityResourceIT.createEntity();
        } else {
            category = TestUtil.findAll(em, Community.class).get(0);
        }
        em.persist(category);
        em.flush();
        event.setCategory(category);
        eventRepository.saveAndFlush(event);
        Long categoryId = category.getId();
        // Get all the eventList where category equals to categoryId
        defaultEventShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the eventList where category equals to (categoryId + 1)
        defaultEventShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    @Test
    @Transactional
    void getAllEventsByCreatedByIsEqualToSomething() throws Exception {
        Profile createdBy;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            eventRepository.saveAndFlush(event);
            createdBy = ProfileResourceIT.createEntity(em);
        } else {
            createdBy = TestUtil.findAll(em, Profile.class).get(0);
        }
        em.persist(createdBy);
        em.flush();
        event.setCreatedBy(createdBy);
        eventRepository.saveAndFlush(event);
        Long createdById = createdBy.getId();
        // Get all the eventList where createdBy equals to createdById
        defaultEventShouldBeFound("createdById.equals=" + createdById);

        // Get all the eventList where createdBy equals to (createdById + 1)
        defaultEventShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }

    @Test
    @Transactional
    void getAllEventsByCancelledByIsEqualToSomething() throws Exception {
        Profile cancelledBy;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            eventRepository.saveAndFlush(event);
            cancelledBy = ProfileResourceIT.createEntity(em);
        } else {
            cancelledBy = TestUtil.findAll(em, Profile.class).get(0);
        }
        em.persist(cancelledBy);
        em.flush();
        event.setCancelledBy(cancelledBy);
        eventRepository.saveAndFlush(event);
        Long cancelledById = cancelledBy.getId();
        // Get all the eventList where cancelledBy equals to cancelledById
        defaultEventShouldBeFound("cancelledById.equals=" + cancelledById);

        // Get all the eventList where cancelledBy equals to (cancelledById + 1)
        defaultEventShouldNotBeFound("cancelledById.equals=" + (cancelledById + 1));
    }

    private void defaultEventFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEventShouldBeFound(shouldBeFound);
        defaultEventShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEventShouldBeFound(String filter) throws Exception {
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].startsAt").value(hasItem(DEFAULT_STARTS_AT.toString())))
            .andExpect(jsonPath("$.[*].endsAt").value(hasItem(DEFAULT_ENDS_AT.toString())))
            .andExpect(jsonPath("$.[*].locationName").value(hasItem(DEFAULT_LOCATION_NAME)))
            .andExpect(jsonPath("$.[*].addressText").value(hasItem(DEFAULT_ADDRESS_TEXT)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(sameNumber(DEFAULT_LATITUDE))))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(sameNumber(DEFAULT_LONGITUDE))))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].rejectionReason").value(hasItem(DEFAULT_REJECTION_REASON)))
            .andExpect(jsonPath("$.[*].publishedAt").value(hasItem(DEFAULT_PUBLISHED_AT.toString())))
            .andExpect(jsonPath("$.[*].publishedToDiscovery").value(hasItem(DEFAULT_PUBLISHED_TO_DISCOVERY)))
            .andExpect(jsonPath("$.[*].groupModerationStatus").value(hasItem(DEFAULT_GROUP_MODERATION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].platformModerationStatus").value(hasItem(DEFAULT_PLATFORM_MODERATION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].cancelledAt").value(hasItem(DEFAULT_CANCELLED_AT.toString())))
            .andExpect(jsonPath("$.[*].cancellationReason").value(hasItem(DEFAULT_CANCELLATION_REASON)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEventShouldNotBeFound(String filter) throws Exception {
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEvent() throws Exception {
        // Get the event
        restEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEvent() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the event
        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEvent are not directly saved in db
        em.detach(updatedEvent);
        updatedEvent
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .startsAt(UPDATED_STARTS_AT)
            .endsAt(UPDATED_ENDS_AT)
            .locationName(UPDATED_LOCATION_NAME)
            .addressText(UPDATED_ADDRESS_TEXT)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .capacity(UPDATED_CAPACITY)
            .status(UPDATED_STATUS)
            .rejectionReason(UPDATED_REJECTION_REASON)
            .publishedAt(UPDATED_PUBLISHED_AT)
            .publishedToDiscovery(UPDATED_PUBLISHED_TO_DISCOVERY)
            .groupModerationStatus(UPDATED_GROUP_MODERATION_STATUS)
            .platformModerationStatus(UPDATED_PLATFORM_MODERATION_STATUS)
            .cancelledAt(UPDATED_CANCELLED_AT)
            .cancellationReason(UPDATED_CANCELLATION_REASON)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        EventDTO eventDTO = eventMapper.toDto(updatedEvent);

        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventDTO))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEventToMatchAllProperties(updatedEvent);
    }

    @Test
    @Transactional
    void putNonExistingEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        event.setId(longCount.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        event.setId(longCount.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        event.setId(longCount.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEventWithPatch() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the event using partial update
        Event partialUpdatedEvent = new Event();
        partialUpdatedEvent.setId(event.getId());

        partialUpdatedEvent
            .description(UPDATED_DESCRIPTION)
            .locationName(UPDATED_LOCATION_NAME)
            .longitude(UPDATED_LONGITUDE)
            .capacity(UPDATED_CAPACITY)
            .rejectionReason(UPDATED_REJECTION_REASON)
            .publishedAt(UPDATED_PUBLISHED_AT)
            .publishedToDiscovery(UPDATED_PUBLISHED_TO_DISCOVERY)
            .platformModerationStatus(UPDATED_PLATFORM_MODERATION_STATUS)
            .cancelledAt(UPDATED_CANCELLED_AT)
            .createdAt(UPDATED_CREATED_AT);

        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEvent))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEvent, event), getPersistedEvent(event));
    }

    @Test
    @Transactional
    void fullUpdateEventWithPatch() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the event using partial update
        Event partialUpdatedEvent = new Event();
        partialUpdatedEvent.setId(event.getId());

        partialUpdatedEvent
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .startsAt(UPDATED_STARTS_AT)
            .endsAt(UPDATED_ENDS_AT)
            .locationName(UPDATED_LOCATION_NAME)
            .addressText(UPDATED_ADDRESS_TEXT)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .capacity(UPDATED_CAPACITY)
            .status(UPDATED_STATUS)
            .rejectionReason(UPDATED_REJECTION_REASON)
            .publishedAt(UPDATED_PUBLISHED_AT)
            .publishedToDiscovery(UPDATED_PUBLISHED_TO_DISCOVERY)
            .groupModerationStatus(UPDATED_GROUP_MODERATION_STATUS)
            .platformModerationStatus(UPDATED_PLATFORM_MODERATION_STATUS)
            .cancelledAt(UPDATED_CANCELLED_AT)
            .cancellationReason(UPDATED_CANCELLATION_REASON)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEvent))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventUpdatableFieldsEquals(partialUpdatedEvent, getPersistedEvent(partialUpdatedEvent));
    }

    @Test
    @Transactional
    void patchNonExistingEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        event.setId(longCount.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        event.setId(longCount.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        event.setId(longCount.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(eventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Event in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEvent() throws Exception {
        // Initialize the database
        insertedEvent = eventRepository.saveAndFlush(event);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the event
        restEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, event.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return eventRepository.count();
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

    protected Event getPersistedEvent(Event event) {
        return eventRepository.findById(event.getId()).orElseThrow();
    }

    protected void assertPersistedEventToMatchAllProperties(Event expectedEvent) {
        assertEventAllPropertiesEquals(expectedEvent, getPersistedEvent(expectedEvent));
    }

    protected void assertPersistedEventToMatchUpdatableProperties(Event expectedEvent) {
        assertEventAllUpdatablePropertiesEquals(expectedEvent, getPersistedEvent(expectedEvent));
    }
}
