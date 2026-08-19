package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.PartnerOfferAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static com.bialem.backend.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.PartnerOffer;
import com.bialem.backend.repository.PartnerOfferRepository;
import com.bialem.backend.service.dto.PartnerOfferDTO;
import com.bialem.backend.service.mapper.PartnerOfferMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
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
 * Integration tests for the {@link PartnerOfferResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PartnerOfferResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_DISCOUNT_PERCENT = new BigDecimal(0);
    private static final BigDecimal UPDATED_DISCOUNT_PERCENT = new BigDecimal(1);

    private static final BigDecimal DEFAULT_MINIMUM_SPEND = new BigDecimal(0);
    private static final BigDecimal UPDATED_MINIMUM_SPEND = new BigDecimal(1);

    private static final BigDecimal DEFAULT_MAXIMUM_DISCOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_MAXIMUM_DISCOUNT = new BigDecimal(1);

    private static final Instant DEFAULT_VALID_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_FROM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_VALID_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_UNTIL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_VALID_DAYS = "1,2,3,4,5";
    private static final String UPDATED_VALID_DAYS = "6,7";

    private static final LocalTime DEFAULT_DAILY_START_TIME = LocalTime.of(8, 0);
    private static final LocalTime UPDATED_DAILY_START_TIME = LocalTime.of(9, 30);

    private static final LocalTime DEFAULT_DAILY_END_TIME = LocalTime.of(18, 0);
    private static final LocalTime UPDATED_DAILY_END_TIME = LocalTime.of(20, 0);

    private static final Integer DEFAULT_PER_USER_LIMIT = 1;
    private static final Integer UPDATED_PER_USER_LIMIT = 2;

    private static final String DEFAULT_TERMS = "AAAAAAAAAA";
    private static final String UPDATED_TERMS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/partner-offers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PartnerOfferRepository partnerOfferRepository;

    @Autowired
    private PartnerOfferMapper partnerOfferMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartnerOfferMockMvc;

    private PartnerOffer partnerOffer;

    private PartnerOffer insertedPartnerOffer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartnerOffer createEntity() {
        return new PartnerOffer()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .discountPercent(DEFAULT_DISCOUNT_PERCENT)
            .minimumSpend(DEFAULT_MINIMUM_SPEND)
            .maximumDiscount(DEFAULT_MAXIMUM_DISCOUNT)
            .validFrom(DEFAULT_VALID_FROM)
            .validUntil(DEFAULT_VALID_UNTIL)
            .validDays(DEFAULT_VALID_DAYS)
            .dailyStartTime(DEFAULT_DAILY_START_TIME)
            .dailyEndTime(DEFAULT_DAILY_END_TIME)
            .perUserLimit(DEFAULT_PER_USER_LIMIT)
            .terms(DEFAULT_TERMS)
            .isActive(DEFAULT_IS_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartnerOffer createUpdatedEntity() {
        return new PartnerOffer()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .discountPercent(UPDATED_DISCOUNT_PERCENT)
            .minimumSpend(UPDATED_MINIMUM_SPEND)
            .maximumDiscount(UPDATED_MAXIMUM_DISCOUNT)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL)
            .validDays(UPDATED_VALID_DAYS)
            .dailyStartTime(UPDATED_DAILY_START_TIME)
            .dailyEndTime(UPDATED_DAILY_END_TIME)
            .perUserLimit(UPDATED_PER_USER_LIMIT)
            .terms(UPDATED_TERMS)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        partnerOffer = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPartnerOffer != null) {
            partnerOfferRepository.delete(insertedPartnerOffer);
            insertedPartnerOffer = null;
        }
    }

    @Test
    @Transactional
    void createPartnerOffer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PartnerOffer
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);
        var returnedPartnerOfferDTO = om.readValue(
            restPartnerOfferMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PartnerOfferDTO.class
        );

        // Validate the PartnerOffer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPartnerOffer = partnerOfferMapper.toEntity(returnedPartnerOfferDTO);
        assertPartnerOfferUpdatableFieldsEquals(returnedPartnerOffer, getPersistedPartnerOffer(returnedPartnerOffer));

        insertedPartnerOffer = returnedPartnerOffer;
    }

    @Test
    @Transactional
    void createPartnerOfferWithExistingId() throws Exception {
        // Create the PartnerOffer with an existing ID
        partnerOffer.setId(1L);
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartnerOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PartnerOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerOffer.setTitle(null);

        // Create the PartnerOffer, which fails.
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);

        restPartnerOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDiscountPercentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerOffer.setDiscountPercent(null);

        // Create the PartnerOffer, which fails.
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);

        restPartnerOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerOffer.setValidFrom(null);

        // Create the PartnerOffer, which fails.
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);

        restPartnerOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerOffer.setIsActive(null);

        // Create the PartnerOffer, which fails.
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);

        restPartnerOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerOffer.setCreatedAt(null);

        // Create the PartnerOffer, which fails.
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);

        restPartnerOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerOffer.setUpdatedAt(null);

        // Create the PartnerOffer, which fails.
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);

        restPartnerOfferMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPartnerOffers() throws Exception {
        // Initialize the database
        insertedPartnerOffer = partnerOfferRepository.saveAndFlush(partnerOffer);

        // Get all the partnerOfferList
        restPartnerOfferMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partnerOffer.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].discountPercent").value(hasItem(sameNumber(DEFAULT_DISCOUNT_PERCENT))))
            .andExpect(jsonPath("$.[*].minimumSpend").value(hasItem(sameNumber(DEFAULT_MINIMUM_SPEND))))
            .andExpect(jsonPath("$.[*].maximumDiscount").value(hasItem(sameNumber(DEFAULT_MAXIMUM_DISCOUNT))))
            .andExpect(jsonPath("$.[*].validFrom").value(hasItem(DEFAULT_VALID_FROM.toString())))
            .andExpect(jsonPath("$.[*].validUntil").value(hasItem(DEFAULT_VALID_UNTIL.toString())))
            .andExpect(jsonPath("$.[*].validDays").value(hasItem(DEFAULT_VALID_DAYS)))
            .andExpect(jsonPath("$.[*].dailyStartTime").value(hasItem(DEFAULT_DAILY_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].dailyEndTime").value(hasItem(DEFAULT_DAILY_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].perUserLimit").value(hasItem(DEFAULT_PER_USER_LIMIT)))
            .andExpect(jsonPath("$.[*].terms").value(hasItem(DEFAULT_TERMS)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getPartnerOffer() throws Exception {
        // Initialize the database
        insertedPartnerOffer = partnerOfferRepository.saveAndFlush(partnerOffer);

        // Get the partnerOffer
        restPartnerOfferMockMvc
            .perform(get(ENTITY_API_URL_ID, partnerOffer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(partnerOffer.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.discountPercent").value(sameNumber(DEFAULT_DISCOUNT_PERCENT)))
            .andExpect(jsonPath("$.minimumSpend").value(sameNumber(DEFAULT_MINIMUM_SPEND)))
            .andExpect(jsonPath("$.maximumDiscount").value(sameNumber(DEFAULT_MAXIMUM_DISCOUNT)))
            .andExpect(jsonPath("$.validFrom").value(DEFAULT_VALID_FROM.toString()))
            .andExpect(jsonPath("$.validUntil").value(DEFAULT_VALID_UNTIL.toString()))
            .andExpect(jsonPath("$.validDays").value(DEFAULT_VALID_DAYS))
            .andExpect(jsonPath("$.dailyStartTime").value(DEFAULT_DAILY_START_TIME.toString()))
            .andExpect(jsonPath("$.dailyEndTime").value(DEFAULT_DAILY_END_TIME.toString()))
            .andExpect(jsonPath("$.perUserLimit").value(DEFAULT_PER_USER_LIMIT))
            .andExpect(jsonPath("$.terms").value(DEFAULT_TERMS))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPartnerOffer() throws Exception {
        // Get the partnerOffer
        restPartnerOfferMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPartnerOffer() throws Exception {
        // Initialize the database
        insertedPartnerOffer = partnerOfferRepository.saveAndFlush(partnerOffer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partnerOffer
        PartnerOffer updatedPartnerOffer = partnerOfferRepository.findById(partnerOffer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPartnerOffer are not directly saved in db
        em.detach(updatedPartnerOffer);
        updatedPartnerOffer
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .discountPercent(UPDATED_DISCOUNT_PERCENT)
            .minimumSpend(UPDATED_MINIMUM_SPEND)
            .maximumDiscount(UPDATED_MAXIMUM_DISCOUNT)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL)
            .validDays(UPDATED_VALID_DAYS)
            .dailyStartTime(UPDATED_DAILY_START_TIME)
            .dailyEndTime(UPDATED_DAILY_END_TIME)
            .perUserLimit(UPDATED_PER_USER_LIMIT)
            .terms(UPDATED_TERMS)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(updatedPartnerOffer);

        restPartnerOfferMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partnerOfferDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partnerOfferDTO))
            )
            .andExpect(status().isOk());

        // Validate the PartnerOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPartnerOfferToMatchAllProperties(updatedPartnerOffer);
    }

    @Test
    @Transactional
    void putNonExistingPartnerOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerOffer.setId(longCount.incrementAndGet());

        // Create the PartnerOffer
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartnerOfferMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partnerOfferDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partnerOfferDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPartnerOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerOffer.setId(longCount.incrementAndGet());

        // Create the PartnerOffer
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerOfferMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partnerOfferDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPartnerOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerOffer.setId(longCount.incrementAndGet());

        // Create the PartnerOffer
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerOfferMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartnerOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartnerOfferWithPatch() throws Exception {
        // Initialize the database
        insertedPartnerOffer = partnerOfferRepository.saveAndFlush(partnerOffer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partnerOffer using partial update
        PartnerOffer partialUpdatedPartnerOffer = new PartnerOffer();
        partialUpdatedPartnerOffer.setId(partnerOffer.getId());

        partialUpdatedPartnerOffer.minimumSpend(UPDATED_MINIMUM_SPEND).validFrom(UPDATED_VALID_FROM).terms(UPDATED_TERMS);

        restPartnerOfferMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartnerOffer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartnerOffer))
            )
            .andExpect(status().isOk());

        // Validate the PartnerOffer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartnerOfferUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPartnerOffer, partnerOffer),
            getPersistedPartnerOffer(partnerOffer)
        );
    }

    @Test
    @Transactional
    void fullUpdatePartnerOfferWithPatch() throws Exception {
        // Initialize the database
        insertedPartnerOffer = partnerOfferRepository.saveAndFlush(partnerOffer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partnerOffer using partial update
        PartnerOffer partialUpdatedPartnerOffer = new PartnerOffer();
        partialUpdatedPartnerOffer.setId(partnerOffer.getId());

        partialUpdatedPartnerOffer
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .discountPercent(UPDATED_DISCOUNT_PERCENT)
            .minimumSpend(UPDATED_MINIMUM_SPEND)
            .maximumDiscount(UPDATED_MAXIMUM_DISCOUNT)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL)
            .validDays(UPDATED_VALID_DAYS)
            .dailyStartTime(UPDATED_DAILY_START_TIME)
            .dailyEndTime(UPDATED_DAILY_END_TIME)
            .perUserLimit(UPDATED_PER_USER_LIMIT)
            .terms(UPDATED_TERMS)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restPartnerOfferMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartnerOffer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartnerOffer))
            )
            .andExpect(status().isOk());

        // Validate the PartnerOffer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartnerOfferUpdatableFieldsEquals(partialUpdatedPartnerOffer, getPersistedPartnerOffer(partialUpdatedPartnerOffer));
    }

    @Test
    @Transactional
    void patchNonExistingPartnerOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerOffer.setId(longCount.incrementAndGet());

        // Create the PartnerOffer
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartnerOfferMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partnerOfferDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partnerOfferDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPartnerOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerOffer.setId(longCount.incrementAndGet());

        // Create the PartnerOffer
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerOfferMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partnerOfferDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPartnerOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerOffer.setId(longCount.incrementAndGet());

        // Create the PartnerOffer
        PartnerOfferDTO partnerOfferDTO = partnerOfferMapper.toDto(partnerOffer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerOfferMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(partnerOfferDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartnerOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePartnerOffer() throws Exception {
        // Initialize the database
        insertedPartnerOffer = partnerOfferRepository.saveAndFlush(partnerOffer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the partnerOffer
        restPartnerOfferMockMvc
            .perform(delete(ENTITY_API_URL_ID, partnerOffer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return partnerOfferRepository.count();
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

    protected PartnerOffer getPersistedPartnerOffer(PartnerOffer partnerOffer) {
        return partnerOfferRepository.findById(partnerOffer.getId()).orElseThrow();
    }

    protected void assertPersistedPartnerOfferToMatchAllProperties(PartnerOffer expectedPartnerOffer) {
        assertPartnerOfferAllPropertiesEquals(expectedPartnerOffer, getPersistedPartnerOffer(expectedPartnerOffer));
    }

    protected void assertPersistedPartnerOfferToMatchUpdatableProperties(PartnerOffer expectedPartnerOffer) {
        assertPartnerOfferAllUpdatablePropertiesEquals(expectedPartnerOffer, getPersistedPartnerOffer(expectedPartnerOffer));
    }
}
