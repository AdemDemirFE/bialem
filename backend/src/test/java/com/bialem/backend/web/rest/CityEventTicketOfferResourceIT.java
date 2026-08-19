package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.CityEventTicketOfferAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static com.bialem.backend.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.CityEventTicketOffer;
import com.bialem.backend.domain.enumeration.TicketOfferAvailability;
import com.bialem.backend.repository.CityEventTicketOfferRepository;
import com.bialem.backend.service.dto.CityEventTicketOfferDTO;
import com.bialem.backend.service.mapper.CityEventTicketOfferMapper;
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
 * Integration tests for the {@link CityEventTicketOfferResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CityEventTicketOfferResourceIT {

    private static final String DEFAULT_PROVIDER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_PROVIDER_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_EXTERNAL_OFFER_ID = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_OFFER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_SELLER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SELLER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PURCHASE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PURCHASE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY = "AAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBB";

    private static final BigDecimal DEFAULT_MIN_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_MIN_PRICE = new BigDecimal(1);

    private static final BigDecimal DEFAULT_MAX_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_MAX_PRICE = new BigDecimal(1);

    private static final String DEFAULT_PRICE_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_PRICE_LABEL = "BBBBBBBBBB";

    private static final TicketOfferAvailability DEFAULT_AVAILABILITY = TicketOfferAvailability.AVAILABLE;
    private static final TicketOfferAvailability UPDATED_AVAILABILITY = TicketOfferAvailability.LIMITED;

    private static final Boolean DEFAULT_FEES_INCLUDED = false;
    private static final Boolean UPDATED_FEES_INCLUDED = true;

    private static final Boolean DEFAULT_IS_OFFICIAL = false;
    private static final Boolean UPDATED_IS_OFFICIAL = true;

    private static final Instant DEFAULT_LAST_CHECKED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_CHECKED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_RAW_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_RAW_PAYLOAD = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/city-event-ticket-offers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CityEventTicketOfferRepository cityEventTicketOfferRepository;

    @Autowired
    private CityEventTicketOfferMapper cityEventTicketOfferMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCityEventTicketOfferMockMvc;

    private CityEventTicketOffer cityEventTicketOffer;

    private CityEventTicketOffer insertedCityEventTicketOffer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CityEventTicketOffer createEntity() {
        return new CityEventTicketOffer()
            .providerCode(DEFAULT_PROVIDER_CODE)
            .externalOfferId(DEFAULT_EXTERNAL_OFFER_ID)
            .sellerName(DEFAULT_SELLER_NAME)
            .purchaseUrl(DEFAULT_PURCHASE_URL)
            .currency(DEFAULT_CURRENCY)
            .minPrice(DEFAULT_MIN_PRICE)
            .maxPrice(DEFAULT_MAX_PRICE)
            .priceLabel(DEFAULT_PRICE_LABEL)
            .availability(DEFAULT_AVAILABILITY)
            .feesIncluded(DEFAULT_FEES_INCLUDED)
            .isOfficial(DEFAULT_IS_OFFICIAL)
            .lastCheckedAt(DEFAULT_LAST_CHECKED_AT)
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
    public static CityEventTicketOffer createUpdatedEntity() {
        return new CityEventTicketOffer()
            .providerCode(UPDATED_PROVIDER_CODE)
            .externalOfferId(UPDATED_EXTERNAL_OFFER_ID)
            .sellerName(UPDATED_SELLER_NAME)
            .purchaseUrl(UPDATED_PURCHASE_URL)
            .currency(UPDATED_CURRENCY)
            .minPrice(UPDATED_MIN_PRICE)
            .maxPrice(UPDATED_MAX_PRICE)
            .priceLabel(UPDATED_PRICE_LABEL)
            .availability(UPDATED_AVAILABILITY)
            .feesIncluded(UPDATED_FEES_INCLUDED)
            .isOfficial(UPDATED_IS_OFFICIAL)
            .lastCheckedAt(UPDATED_LAST_CHECKED_AT)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        cityEventTicketOffer = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCityEventTicketOffer != null) {
            cityEventTicketOfferRepository.delete(insertedCityEventTicketOffer);
            insertedCityEventTicketOffer = null;
        }
    }

    @Test
    @Transactional
    void createCityEventTicketOffer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CityEventTicketOffer
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);
        var returnedCityEventTicketOfferDTO = om.readValue(
            restCityEventTicketOfferMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventTicketOfferDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CityEventTicketOfferDTO.class
        );

        // Validate the CityEventTicketOffer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCityEventTicketOffer = cityEventTicketOfferMapper.toEntity(returnedCityEventTicketOfferDTO);
        assertCityEventTicketOfferUpdatableFieldsEquals(
            returnedCityEventTicketOffer,
            getPersistedCityEventTicketOffer(returnedCityEventTicketOffer)
        );

        insertedCityEventTicketOffer = returnedCityEventTicketOffer;
    }

    @Test
    @Transactional
    void createCityEventTicketOfferWithExistingId() throws Exception {
        // Create the CityEventTicketOffer with an existing ID
        cityEventTicketOffer.setId(1L);
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCityEventTicketOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventTicketOfferDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CityEventTicketOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkProviderCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventTicketOffer.setProviderCode(null);

        // Create the CityEventTicketOffer, which fails.
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        restCityEventTicketOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventTicketOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExternalOfferIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventTicketOffer.setExternalOfferId(null);

        // Create the CityEventTicketOffer, which fails.
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        restCityEventTicketOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventTicketOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSellerNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventTicketOffer.setSellerName(null);

        // Create the CityEventTicketOffer, which fails.
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        restCityEventTicketOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventTicketOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPurchaseUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventTicketOffer.setPurchaseUrl(null);

        // Create the CityEventTicketOffer, which fails.
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        restCityEventTicketOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventTicketOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAvailabilityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventTicketOffer.setAvailability(null);

        // Create the CityEventTicketOffer, which fails.
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        restCityEventTicketOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventTicketOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsOfficialIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventTicketOffer.setIsOfficial(null);

        // Create the CityEventTicketOffer, which fails.
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        restCityEventTicketOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventTicketOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastCheckedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventTicketOffer.setLastCheckedAt(null);

        // Create the CityEventTicketOffer, which fails.
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        restCityEventTicketOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventTicketOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventTicketOffer.setCreatedAt(null);

        // Create the CityEventTicketOffer, which fails.
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        restCityEventTicketOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventTicketOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cityEventTicketOffer.setUpdatedAt(null);

        // Create the CityEventTicketOffer, which fails.
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        restCityEventTicketOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventTicketOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCityEventTicketOffers() throws Exception {
        // Initialize the database
        insertedCityEventTicketOffer = cityEventTicketOfferRepository.saveAndFlush(cityEventTicketOffer);

        // Get all the cityEventTicketOfferList
        restCityEventTicketOfferMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cityEventTicketOffer.getId().intValue())))
            .andExpect(jsonPath("$.[*].providerCode").value(hasItem(DEFAULT_PROVIDER_CODE)))
            .andExpect(jsonPath("$.[*].externalOfferId").value(hasItem(DEFAULT_EXTERNAL_OFFER_ID)))
            .andExpect(jsonPath("$.[*].sellerName").value(hasItem(DEFAULT_SELLER_NAME)))
            .andExpect(jsonPath("$.[*].purchaseUrl").value(hasItem(DEFAULT_PURCHASE_URL)))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].minPrice").value(hasItem(sameNumber(DEFAULT_MIN_PRICE))))
            .andExpect(jsonPath("$.[*].maxPrice").value(hasItem(sameNumber(DEFAULT_MAX_PRICE))))
            .andExpect(jsonPath("$.[*].priceLabel").value(hasItem(DEFAULT_PRICE_LABEL)))
            .andExpect(jsonPath("$.[*].availability").value(hasItem(DEFAULT_AVAILABILITY.toString())))
            .andExpect(jsonPath("$.[*].feesIncluded").value(hasItem(DEFAULT_FEES_INCLUDED)))
            .andExpect(jsonPath("$.[*].isOfficial").value(hasItem(DEFAULT_IS_OFFICIAL)))
            .andExpect(jsonPath("$.[*].lastCheckedAt").value(hasItem(DEFAULT_LAST_CHECKED_AT.toString())))
            .andExpect(jsonPath("$.[*].rawPayload").value(hasItem(DEFAULT_RAW_PAYLOAD)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCityEventTicketOffer() throws Exception {
        // Initialize the database
        insertedCityEventTicketOffer = cityEventTicketOfferRepository.saveAndFlush(cityEventTicketOffer);

        // Get the cityEventTicketOffer
        restCityEventTicketOfferMockMvc
            .perform(get(ENTITY_API_URL_ID, cityEventTicketOffer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cityEventTicketOffer.getId().intValue()))
            .andExpect(jsonPath("$.providerCode").value(DEFAULT_PROVIDER_CODE))
            .andExpect(jsonPath("$.externalOfferId").value(DEFAULT_EXTERNAL_OFFER_ID))
            .andExpect(jsonPath("$.sellerName").value(DEFAULT_SELLER_NAME))
            .andExpect(jsonPath("$.purchaseUrl").value(DEFAULT_PURCHASE_URL))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.minPrice").value(sameNumber(DEFAULT_MIN_PRICE)))
            .andExpect(jsonPath("$.maxPrice").value(sameNumber(DEFAULT_MAX_PRICE)))
            .andExpect(jsonPath("$.priceLabel").value(DEFAULT_PRICE_LABEL))
            .andExpect(jsonPath("$.availability").value(DEFAULT_AVAILABILITY.toString()))
            .andExpect(jsonPath("$.feesIncluded").value(DEFAULT_FEES_INCLUDED))
            .andExpect(jsonPath("$.isOfficial").value(DEFAULT_IS_OFFICIAL))
            .andExpect(jsonPath("$.lastCheckedAt").value(DEFAULT_LAST_CHECKED_AT.toString()))
            .andExpect(jsonPath("$.rawPayload").value(DEFAULT_RAW_PAYLOAD))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCityEventTicketOffer() throws Exception {
        // Get the cityEventTicketOffer
        restCityEventTicketOfferMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCityEventTicketOffer() throws Exception {
        // Initialize the database
        insertedCityEventTicketOffer = cityEventTicketOfferRepository.saveAndFlush(cityEventTicketOffer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cityEventTicketOffer
        CityEventTicketOffer updatedCityEventTicketOffer = cityEventTicketOfferRepository
            .findById(cityEventTicketOffer.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedCityEventTicketOffer are not directly saved in db
        em.detach(updatedCityEventTicketOffer);
        updatedCityEventTicketOffer
            .providerCode(UPDATED_PROVIDER_CODE)
            .externalOfferId(UPDATED_EXTERNAL_OFFER_ID)
            .sellerName(UPDATED_SELLER_NAME)
            .purchaseUrl(UPDATED_PURCHASE_URL)
            .currency(UPDATED_CURRENCY)
            .minPrice(UPDATED_MIN_PRICE)
            .maxPrice(UPDATED_MAX_PRICE)
            .priceLabel(UPDATED_PRICE_LABEL)
            .availability(UPDATED_AVAILABILITY)
            .feesIncluded(UPDATED_FEES_INCLUDED)
            .isOfficial(UPDATED_IS_OFFICIAL)
            .lastCheckedAt(UPDATED_LAST_CHECKED_AT)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(updatedCityEventTicketOffer);

        restCityEventTicketOfferMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cityEventTicketOfferDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cityEventTicketOfferDTO))
            )
            .andExpect(status().isOk());

        // Validate the CityEventTicketOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCityEventTicketOfferToMatchAllProperties(updatedCityEventTicketOffer);
    }

    @Test
    @Transactional
    void putNonExistingCityEventTicketOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventTicketOffer.setId(longCount.incrementAndGet());

        // Create the CityEventTicketOffer
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCityEventTicketOfferMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cityEventTicketOfferDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cityEventTicketOfferDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEventTicketOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCityEventTicketOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventTicketOffer.setId(longCount.incrementAndGet());

        // Create the CityEventTicketOffer
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventTicketOfferMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cityEventTicketOfferDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEventTicketOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCityEventTicketOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventTicketOffer.setId(longCount.incrementAndGet());

        // Create the CityEventTicketOffer
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventTicketOfferMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityEventTicketOfferDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CityEventTicketOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCityEventTicketOfferWithPatch() throws Exception {
        // Initialize the database
        insertedCityEventTicketOffer = cityEventTicketOfferRepository.saveAndFlush(cityEventTicketOffer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cityEventTicketOffer using partial update
        CityEventTicketOffer partialUpdatedCityEventTicketOffer = new CityEventTicketOffer();
        partialUpdatedCityEventTicketOffer.setId(cityEventTicketOffer.getId());

        partialUpdatedCityEventTicketOffer
            .externalOfferId(UPDATED_EXTERNAL_OFFER_ID)
            .purchaseUrl(UPDATED_PURCHASE_URL)
            .currency(UPDATED_CURRENCY)
            .minPrice(UPDATED_MIN_PRICE)
            .maxPrice(UPDATED_MAX_PRICE)
            .priceLabel(UPDATED_PRICE_LABEL)
            .feesIncluded(UPDATED_FEES_INCLUDED)
            .rawPayload(UPDATED_RAW_PAYLOAD);

        restCityEventTicketOfferMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCityEventTicketOffer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCityEventTicketOffer))
            )
            .andExpect(status().isOk());

        // Validate the CityEventTicketOffer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityEventTicketOfferUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCityEventTicketOffer, cityEventTicketOffer),
            getPersistedCityEventTicketOffer(cityEventTicketOffer)
        );
    }

    @Test
    @Transactional
    void fullUpdateCityEventTicketOfferWithPatch() throws Exception {
        // Initialize the database
        insertedCityEventTicketOffer = cityEventTicketOfferRepository.saveAndFlush(cityEventTicketOffer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cityEventTicketOffer using partial update
        CityEventTicketOffer partialUpdatedCityEventTicketOffer = new CityEventTicketOffer();
        partialUpdatedCityEventTicketOffer.setId(cityEventTicketOffer.getId());

        partialUpdatedCityEventTicketOffer
            .providerCode(UPDATED_PROVIDER_CODE)
            .externalOfferId(UPDATED_EXTERNAL_OFFER_ID)
            .sellerName(UPDATED_SELLER_NAME)
            .purchaseUrl(UPDATED_PURCHASE_URL)
            .currency(UPDATED_CURRENCY)
            .minPrice(UPDATED_MIN_PRICE)
            .maxPrice(UPDATED_MAX_PRICE)
            .priceLabel(UPDATED_PRICE_LABEL)
            .availability(UPDATED_AVAILABILITY)
            .feesIncluded(UPDATED_FEES_INCLUDED)
            .isOfficial(UPDATED_IS_OFFICIAL)
            .lastCheckedAt(UPDATED_LAST_CHECKED_AT)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCityEventTicketOfferMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCityEventTicketOffer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCityEventTicketOffer))
            )
            .andExpect(status().isOk());

        // Validate the CityEventTicketOffer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityEventTicketOfferUpdatableFieldsEquals(
            partialUpdatedCityEventTicketOffer,
            getPersistedCityEventTicketOffer(partialUpdatedCityEventTicketOffer)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCityEventTicketOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventTicketOffer.setId(longCount.incrementAndGet());

        // Create the CityEventTicketOffer
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCityEventTicketOfferMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cityEventTicketOfferDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cityEventTicketOfferDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEventTicketOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCityEventTicketOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventTicketOffer.setId(longCount.incrementAndGet());

        // Create the CityEventTicketOffer
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventTicketOfferMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cityEventTicketOfferDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CityEventTicketOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCityEventTicketOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cityEventTicketOffer.setId(longCount.incrementAndGet());

        // Create the CityEventTicketOffer
        CityEventTicketOfferDTO cityEventTicketOfferDTO = cityEventTicketOfferMapper.toDto(cityEventTicketOffer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityEventTicketOfferMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cityEventTicketOfferDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CityEventTicketOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCityEventTicketOffer() throws Exception {
        // Initialize the database
        insertedCityEventTicketOffer = cityEventTicketOfferRepository.saveAndFlush(cityEventTicketOffer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cityEventTicketOffer
        restCityEventTicketOfferMockMvc
            .perform(delete(ENTITY_API_URL_ID, cityEventTicketOffer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cityEventTicketOfferRepository.count();
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

    protected CityEventTicketOffer getPersistedCityEventTicketOffer(CityEventTicketOffer cityEventTicketOffer) {
        return cityEventTicketOfferRepository.findById(cityEventTicketOffer.getId()).orElseThrow();
    }

    protected void assertPersistedCityEventTicketOfferToMatchAllProperties(CityEventTicketOffer expectedCityEventTicketOffer) {
        assertCityEventTicketOfferAllPropertiesEquals(
            expectedCityEventTicketOffer,
            getPersistedCityEventTicketOffer(expectedCityEventTicketOffer)
        );
    }

    protected void assertPersistedCityEventTicketOfferToMatchUpdatableProperties(CityEventTicketOffer expectedCityEventTicketOffer) {
        assertCityEventTicketOfferAllUpdatablePropertiesEquals(
            expectedCityEventTicketOffer,
            getPersistedCityEventTicketOffer(expectedCityEventTicketOffer)
        );
    }
}
