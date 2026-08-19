package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.CityEventAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.CityEvent;
import com.bialem.backend.domain.enumeration.CityEventStatus;
import com.bialem.backend.repository.CityEventRepository;
import com.bialem.backend.service.dto.CityEventDTO;
import com.bialem.backend.service.mapper.CityEventMapper;
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
 * Integration tests for the {@link CityEventResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CityEventResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_VENUE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_VENUE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_TEXT = "BBBBBBBBBB";

    private static final Instant DEFAULT_STARTS_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTS_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ENDS_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENDS_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_COVER_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_COVER_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_PRICE_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_PRICE_LABEL = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE_URL = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_TICKET_URL = "AAAAAAAAAA";
    private static final String UPDATED_TICKET_URL = "BBBBBBBBBB";

    private static final CityEventStatus DEFAULT_STATUS = CityEventStatus.DRAFT;
    private static final CityEventStatus UPDATED_STATUS = CityEventStatus.PUBLISHED;

    private static final String DEFAULT_PROVIDER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_PROVIDER_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_EXTERNAL_ID = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_ID = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_SYNCED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_SYNCED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_RAW_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_RAW_PAYLOAD = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/city-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CityEventRepository cityEventRepository;

    @Autowired
    private CityEventMapper cityEventMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCityEventMockMvc;

    private CityEvent cityEvent;

    private CityEvent insertedCityEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CityEvent createEntity() {
        return new CityEvent()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .category(DEFAULT_CATEGORY)
            .city(DEFAULT_CITY)
            .venueName(DEFAULT_VENUE_NAME)
            .addressText(DEFAULT_ADDRESS_TEXT)
            .startsAt(DEFAULT_STARTS_AT)
            .endsAt(DEFAULT_ENDS_AT)
            .coverImageUrl(DEFAULT_COVER_IMAGE_URL)
            .priceLabel(DEFAULT_PRICE_LABEL)
            .sourceName(DEFAULT_SOURCE_NAME)
            .sourceUrl(DEFAULT_SOURCE_URL)
            .ticketUrl(DEFAULT_TICKET_URL)
            .status(DEFAULT_STATUS)
            .providerCode(DEFAULT_PROVIDER_CODE)
            .externalId(DEFAULT_EXTERNAL_ID)
            .lastSyncedAt(DEFAULT_LAST_SYNCED_AT)
            .rawPayload(DEFAULT_RAW_PAYLOAD)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CityEvent createUpdatedEntity() {
        return new CityEvent()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .category(UPDATED_CATEGORY)
            .city(UPDATED_CITY)
            .venueName(UPDATED_VENUE_NAME)
            .addressText(UPDATED_ADDRESS_TEXT)
            .startsAt(UPDATED_STARTS_AT)
            .endsAt(UPDATED_ENDS_AT)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .priceLabel(UPDATED_PRICE_LABEL)
            .sourceName(UPDATED_SOURCE_NAME)
            .sourceUrl(UPDATED_SOURCE_URL)
            .ticketUrl(UPDATED_TICKET_URL)
            .status(UPDATED_STATUS)
            .providerCode(UPDATED_PROVIDER_CODE)
            .externalId(UPDATED_EXTERNAL_ID)
            .lastSyncedAt(UPDATED_LAST_SYNCED_AT)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        cityEvent = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCityEvent != null) {
            cityEventRepository.delete(insertedCityEvent);
            insertedCityEvent = null;
        }
    }

    @Test
    @Transactional
    void createCityEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CityEvent
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);
        var returnedCityEventDTO = om.readValue(
            restCityEventMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CityEventDTO.class
        );

        // Validate the CityEvent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCityEvent = cityEventMapper.toEntity(returnedCityEventDTO);
        assertCityEventUpdatableFieldsEquals(returnedCityEvent, getPersistedCityEvent(returnedCityEvent));

        insertedCityEvent = returnedCityEvent;
    }

    @Test
    @Transactional
    void createCityEventWithExistingId() throws Exception {
        // Create the CityEvent with an existing ID
        cityEvent.setId(1L);
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCityEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEvent.setTitle(null);

        // Create the CityEvent, which fails.
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        restCityEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategoryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEvent.setCategory(null);

        // Create the CityEvent, which fails.
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        restCityEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEvent.setCity(null);

        // Create the CityEvent, which fails.
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        restCityEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartsAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEvent.setStartsAt(null);

        // Create the CityEvent, which fails.
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        restCityEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSourceNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEvent.setSourceName(null);

        // Create the CityEvent, which fails.
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        restCityEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEvent.setStatus(null);

        // Create the CityEvent, which fails.
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        restCityEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkProviderCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEvent.setProviderCode(null);

        // Create the CityEvent, which fails.
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        restCityEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEvent.setCreatedAt(null);

        // Create the CityEvent, which fails.
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        restCityEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEvent.setUpdatedAt(null);

        // Create the CityEvent, which fails.
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        restCityEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCityEvents() throws Exception {
        // Initialize the database
        insertedCityEvent = cityEventRepository.saveAndFlush(cityEvent);

        // Get all the cityEventList
        restCityEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cityEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].venueName").value(hasItem(DEFAULT_VENUE_NAME)))
            .andExpect(jsonPath("$.[*].addressText").value(hasItem(DEFAULT_ADDRESS_TEXT)))
            .andExpect(jsonPath("$.[*].startsAt").value(hasItem(DEFAULT_STARTS_AT.toString())))
            .andExpect(jsonPath("$.[*].endsAt").value(hasItem(DEFAULT_ENDS_AT.toString())))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].priceLabel").value(hasItem(DEFAULT_PRICE_LABEL)))
            .andExpect(jsonPath("$.[*].sourceName").value(hasItem(DEFAULT_SOURCE_NAME)))
            .andExpect(jsonPath("$.[*].sourceUrl").value(hasItem(DEFAULT_SOURCE_URL)))
            .andExpect(jsonPath("$.[*].ticketUrl").value(hasItem(DEFAULT_TICKET_URL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].providerCode").value(hasItem(DEFAULT_PROVIDER_CODE)))
            .andExpect(jsonPath("$.[*].externalId").value(hasItem(DEFAULT_EXTERNAL_ID)))
            .andExpect(jsonPath("$.[*].lastSyncedAt").value(hasItem(DEFAULT_LAST_SYNCED_AT.toString())))
            .andExpect(jsonPath("$.[*].rawPayload").value(hasItem(DEFAULT_RAW_PAYLOAD)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCityEvent() throws Exception {
        // Initialize the database
        insertedCityEvent = cityEventRepository.saveAndFlush(cityEvent);

        // Get the cityEvent
        restCityEventMockMvc
            .perform(get(ENTITY_API_URL_ID, cityEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cityEvent.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.venueName").value(DEFAULT_VENUE_NAME))
            .andExpect(jsonPath("$.addressText").value(DEFAULT_ADDRESS_TEXT))
            .andExpect(jsonPath("$.startsAt").value(DEFAULT_STARTS_AT.toString()))
            .andExpect(jsonPath("$.endsAt").value(DEFAULT_ENDS_AT.toString()))
            .andExpect(jsonPath("$.coverImageUrl").value(DEFAULT_COVER_IMAGE_URL))
            .andExpect(jsonPath("$.priceLabel").value(DEFAULT_PRICE_LABEL))
            .andExpect(jsonPath("$.sourceName").value(DEFAULT_SOURCE_NAME))
            .andExpect(jsonPath("$.sourceUrl").value(DEFAULT_SOURCE_URL))
            .andExpect(jsonPath("$.ticketUrl").value(DEFAULT_TICKET_URL))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.providerCode").value(DEFAULT_PROVIDER_CODE))
            .andExpect(jsonPath("$.externalId").value(DEFAULT_EXTERNAL_ID))
            .andExpect(jsonPath("$.lastSyncedAt").value(DEFAULT_LAST_SYNCED_AT.toString()))
            .andExpect(jsonPath("$.rawPayload").value(DEFAULT_RAW_PAYLOAD))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCityEvent() throws Exception {
        // Get the cityEvent
        restCityEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCityEvent() throws Exception {
        // Initialize the database
        insertedCityEvent = cityEventRepository.saveAndFlush(cityEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cityEvent
        CityEvent updatedCityEvent = cityEventRepository.findById(cityEvent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCityEvent are not directly saved in db
        em.detach(updatedCityEvent);
        updatedCityEvent
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .category(UPDATED_CATEGORY)
            .city(UPDATED_CITY)
            .venueName(UPDATED_VENUE_NAME)
            .addressText(UPDATED_ADDRESS_TEXT)
            .startsAt(UPDATED_STARTS_AT)
            .endsAt(UPDATED_ENDS_AT)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .priceLabel(UPDATED_PRICE_LABEL)
            .sourceName(UPDATED_SOURCE_NAME)
            .sourceUrl(UPDATED_SOURCE_URL)
            .ticketUrl(UPDATED_TICKET_URL)
            .status(UPDATED_STATUS)
            .providerCode(UPDATED_PROVIDER_CODE)
            .externalId(UPDATED_EXTERNAL_ID)
            .lastSyncedAt(UPDATED_LAST_SYNCED_AT)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        CityEventDTO cityEventDTO = cityEventMapper.toDto(updatedCityEvent);

        restCityEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cityEventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cityEventDTO))
            )
            .andExpect(status().isOk());

        // Validate the CityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCityEventToMatchAllProperties(updatedCityEvent);
    }

    @Test
    @Transactional
    void putNonExistingCityEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEvent.setId(longCount.incrementAndGet());

        // Create the CityEvent
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCityEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cityEventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cityEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCityEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEvent.setId(longCount.incrementAndGet());

        // Create the CityEvent
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cityEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCityEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEvent.setId(longCount.incrementAndGet());

        // Create the CityEvent
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCityEventWithPatch() throws Exception {
        // Initialize the database
        insertedCityEvent = cityEventRepository.saveAndFlush(cityEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cityEvent using partial update
        CityEvent partialUpdatedCityEvent = new CityEvent();
        partialUpdatedCityEvent.setId(cityEvent.getId());

        partialUpdatedCityEvent
            .title(UPDATED_TITLE)
            .category(UPDATED_CATEGORY)
            .city(UPDATED_CITY)
            .addressText(UPDATED_ADDRESS_TEXT)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .priceLabel(UPDATED_PRICE_LABEL)
            .sourceName(UPDATED_SOURCE_NAME)
            .sourceUrl(UPDATED_SOURCE_URL)
            .status(UPDATED_STATUS)
            .externalId(UPDATED_EXTERNAL_ID)
            .lastSyncedAt(UPDATED_LAST_SYNCED_AT)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .createdAt(UPDATED_CREATED_AT);

        restCityEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCityEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCityEvent))
            )
            .andExpect(status().isOk());

        // Validate the CityEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityEventUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCityEvent, cityEvent),
            getPersistedCityEvent(cityEvent)
        );
    }

    @Test
    @Transactional
    void fullUpdateCityEventWithPatch() throws Exception {
        // Initialize the database
        insertedCityEvent = cityEventRepository.saveAndFlush(cityEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cityEvent using partial update
        CityEvent partialUpdatedCityEvent = new CityEvent();
        partialUpdatedCityEvent.setId(cityEvent.getId());

        partialUpdatedCityEvent
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .category(UPDATED_CATEGORY)
            .city(UPDATED_CITY)
            .venueName(UPDATED_VENUE_NAME)
            .addressText(UPDATED_ADDRESS_TEXT)
            .startsAt(UPDATED_STARTS_AT)
            .endsAt(UPDATED_ENDS_AT)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .priceLabel(UPDATED_PRICE_LABEL)
            .sourceName(UPDATED_SOURCE_NAME)
            .sourceUrl(UPDATED_SOURCE_URL)
            .ticketUrl(UPDATED_TICKET_URL)
            .status(UPDATED_STATUS)
            .providerCode(UPDATED_PROVIDER_CODE)
            .externalId(UPDATED_EXTERNAL_ID)
            .lastSyncedAt(UPDATED_LAST_SYNCED_AT)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCityEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCityEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCityEvent))
            )
            .andExpect(status().isOk());

        // Validate the CityEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityEventUpdatableFieldsEquals(partialUpdatedCityEvent, getPersistedCityEvent(partialUpdatedCityEvent));
    }

    @Test
    @Transactional
    void patchNonExistingCityEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEvent.setId(longCount.incrementAndGet());

        // Create the CityEvent
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCityEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cityEventDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cityEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCityEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEvent.setId(longCount.incrementAndGet());

        // Create the CityEvent
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cityEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCityEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEvent.setId(longCount.incrementAndGet());

        // Create the CityEvent
        CityEventDTO cityEventDTO = cityEventMapper.toDto(cityEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cityEventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCityEvent() throws Exception {
        // Initialize the database
        insertedCityEvent = cityEventRepository.saveAndFlush(cityEvent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cityEvent
        restCityEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, cityEvent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cityEventRepository.count();
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

    protected CityEvent getPersistedCityEvent(CityEvent cityEvent) {
        return cityEventRepository.findById(cityEvent.getId()).orElseThrow();
    }

    protected void assertPersistedCityEventToMatchAllProperties(CityEvent expectedCityEvent) {
        assertCityEventAllPropertiesEquals(expectedCityEvent, getPersistedCityEvent(expectedCityEvent));
    }

    protected void assertPersistedCityEventToMatchUpdatableProperties(CityEvent expectedCityEvent) {
        assertCityEventAllUpdatablePropertiesEquals(expectedCityEvent, getPersistedCityEvent(expectedCityEvent));
    }
}
