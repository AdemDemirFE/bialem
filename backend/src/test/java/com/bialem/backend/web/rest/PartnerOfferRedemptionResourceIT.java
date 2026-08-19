package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.PartnerOfferRedemptionAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static com.bialem.backend.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.PartnerOfferRedemption;
import com.bialem.backend.domain.enumeration.RedemptionStatus;
import com.bialem.backend.repository.PartnerOfferRedemptionRepository;
import com.bialem.backend.service.dto.PartnerOfferRedemptionDTO;
import com.bialem.backend.service.mapper.PartnerOfferRedemptionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;
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
 * Integration tests for the {@link PartnerOfferRedemptionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PartnerOfferRedemptionResourceIT {

    private static final UUID DEFAULT_TOKEN = UUID.randomUUID();
    private static final UUID UPDATED_TOKEN = UUID.randomUUID();

    private static final String DEFAULT_REDEMPTION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_REDEMPTION_CODE = "BBBBBBBBBB";

    private static final RedemptionStatus DEFAULT_STATUS = RedemptionStatus.ISSUED;
    private static final RedemptionStatus UPDATED_STATUS = RedemptionStatus.REDEMED;

    private static final Instant DEFAULT_ISSUED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ISSUED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_REDEEMED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REDEEMED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_ORDER_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_ORDER_AMOUNT = new BigDecimal(1);

    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_DISCOUNT_AMOUNT = new BigDecimal(1);

    private static final String ENTITY_API_URL = "/api/partner-offer-redemptions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PartnerOfferRedemptionRepository partnerOfferRedemptionRepository;

    @Autowired
    private PartnerOfferRedemptionMapper partnerOfferRedemptionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartnerOfferRedemptionMockMvc;

    private PartnerOfferRedemption partnerOfferRedemption;

    private PartnerOfferRedemption insertedPartnerOfferRedemption;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartnerOfferRedemption createEntity() {
        return new PartnerOfferRedemption()
            .token(DEFAULT_TOKEN)
            .redemptionCode(DEFAULT_REDEMPTION_CODE)
            .status(DEFAULT_STATUS)
            .issuedAt(DEFAULT_ISSUED_AT)
            .expiresAt(DEFAULT_EXPIRES_AT)
            .redeemedAt(DEFAULT_REDEEMED_AT)
            .orderAmount(DEFAULT_ORDER_AMOUNT)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartnerOfferRedemption createUpdatedEntity() {
        return new PartnerOfferRedemption()
            .token(UPDATED_TOKEN)
            .redemptionCode(UPDATED_REDEMPTION_CODE)
            .status(UPDATED_STATUS)
            .issuedAt(UPDATED_ISSUED_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .redeemedAt(UPDATED_REDEEMED_AT)
            .orderAmount(UPDATED_ORDER_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT);
    }

    @BeforeEach
    void initTest() {
        partnerOfferRedemption = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPartnerOfferRedemption != null) {
            partnerOfferRedemptionRepository.delete(insertedPartnerOfferRedemption);
            insertedPartnerOfferRedemption = null;
        }
    }

    @Test
    @Transactional
    void createPartnerOfferRedemption() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PartnerOfferRedemption
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);
        var returnedPartnerOfferRedemptionDTO = om.readValue(
            restPartnerOfferRedemptionMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferRedemptionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PartnerOfferRedemptionDTO.class
        );

        // Validate the PartnerOfferRedemption in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPartnerOfferRedemption = partnerOfferRedemptionMapper.toEntity(returnedPartnerOfferRedemptionDTO);
        assertPartnerOfferRedemptionUpdatableFieldsEquals(
            returnedPartnerOfferRedemption,
            getPersistedPartnerOfferRedemption(returnedPartnerOfferRedemption)
        );

        insertedPartnerOfferRedemption = returnedPartnerOfferRedemption;
    }

    @Test
    @Transactional
    void createPartnerOfferRedemptionWithExistingId() throws Exception {
        // Create the PartnerOfferRedemption with an existing ID
        partnerOfferRedemption.setId(1L);
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartnerOfferRedemptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferRedemptionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PartnerOfferRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTokenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerOfferRedemption.setToken(null);

        // Create the PartnerOfferRedemption, which fails.
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);

        restPartnerOfferRedemptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferRedemptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRedemptionCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerOfferRedemption.setRedemptionCode(null);

        // Create the PartnerOfferRedemption, which fails.
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);

        restPartnerOfferRedemptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferRedemptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerOfferRedemption.setStatus(null);

        // Create the PartnerOfferRedemption, which fails.
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);

        restPartnerOfferRedemptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferRedemptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIssuedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerOfferRedemption.setIssuedAt(null);

        // Create the PartnerOfferRedemption, which fails.
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);

        restPartnerOfferRedemptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferRedemptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExpiresAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerOfferRedemption.setExpiresAt(null);

        // Create the PartnerOfferRedemption, which fails.
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);

        restPartnerOfferRedemptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferRedemptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPartnerOfferRedemptions() throws Exception {
        // Initialize the database
        insertedPartnerOfferRedemption = partnerOfferRedemptionRepository.saveAndFlush(partnerOfferRedemption);

        // Get all the partnerOfferRedemptionList
        restPartnerOfferRedemptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partnerOfferRedemption.getId().intValue())))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN.toString())))
            .andExpect(jsonPath("$.[*].redemptionCode").value(hasItem(DEFAULT_REDEMPTION_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].issuedAt").value(hasItem(DEFAULT_ISSUED_AT.toString())))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].redeemedAt").value(hasItem(DEFAULT_REDEEMED_AT.toString())))
            .andExpect(jsonPath("$.[*].orderAmount").value(hasItem(sameNumber(DEFAULT_ORDER_AMOUNT))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))));
    }

    @Test
    @Transactional
    void getPartnerOfferRedemption() throws Exception {
        // Initialize the database
        insertedPartnerOfferRedemption = partnerOfferRedemptionRepository.saveAndFlush(partnerOfferRedemption);

        // Get the partnerOfferRedemption
        restPartnerOfferRedemptionMockMvc
            .perform(get(ENTITY_API_URL_ID, partnerOfferRedemption.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(partnerOfferRedemption.getId().intValue()))
            .andExpect(jsonPath("$.token").value(DEFAULT_TOKEN.toString()))
            .andExpect(jsonPath("$.redemptionCode").value(DEFAULT_REDEMPTION_CODE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.issuedAt").value(DEFAULT_ISSUED_AT.toString()))
            .andExpect(jsonPath("$.expiresAt").value(DEFAULT_EXPIRES_AT.toString()))
            .andExpect(jsonPath("$.redeemedAt").value(DEFAULT_REDEEMED_AT.toString()))
            .andExpect(jsonPath("$.orderAmount").value(sameNumber(DEFAULT_ORDER_AMOUNT)))
            .andExpect(jsonPath("$.discountAmount").value(sameNumber(DEFAULT_DISCOUNT_AMOUNT)));
    }

    @Test
    @Transactional
    void getNonExistingPartnerOfferRedemption() throws Exception {
        // Get the partnerOfferRedemption
        restPartnerOfferRedemptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPartnerOfferRedemption() throws Exception {
        // Initialize the database
        insertedPartnerOfferRedemption = partnerOfferRedemptionRepository.saveAndFlush(partnerOfferRedemption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partnerOfferRedemption
        PartnerOfferRedemption updatedPartnerOfferRedemption = partnerOfferRedemptionRepository
            .findById(partnerOfferRedemption.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedPartnerOfferRedemption are not directly saved in db
        em.detach(updatedPartnerOfferRedemption);
        updatedPartnerOfferRedemption
            .token(UPDATED_TOKEN)
            .redemptionCode(UPDATED_REDEMPTION_CODE)
            .status(UPDATED_STATUS)
            .issuedAt(UPDATED_ISSUED_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .redeemedAt(UPDATED_REDEEMED_AT)
            .orderAmount(UPDATED_ORDER_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT);
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(updatedPartnerOfferRedemption);

        restPartnerOfferRedemptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partnerOfferRedemptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partnerOfferRedemptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the PartnerOfferRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPartnerOfferRedemptionToMatchAllProperties(updatedPartnerOfferRedemption);
    }

    @Test
    @Transactional
    void putNonExistingPartnerOfferRedemption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerOfferRedemption.setId(longCount.incrementAndGet());

        // Create the PartnerOfferRedemption
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartnerOfferRedemptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partnerOfferRedemptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partnerOfferRedemptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerOfferRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPartnerOfferRedemption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerOfferRedemption.setId(longCount.incrementAndGet());

        // Create the PartnerOfferRedemption
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerOfferRedemptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partnerOfferRedemptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerOfferRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPartnerOfferRedemption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerOfferRedemption.setId(longCount.incrementAndGet());

        // Create the PartnerOfferRedemption
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerOfferRedemptionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerOfferRedemptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartnerOfferRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartnerOfferRedemptionWithPatch() throws Exception {
        // Initialize the database
        insertedPartnerOfferRedemption = partnerOfferRedemptionRepository.saveAndFlush(partnerOfferRedemption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partnerOfferRedemption using partial update
        PartnerOfferRedemption partialUpdatedPartnerOfferRedemption = new PartnerOfferRedemption();
        partialUpdatedPartnerOfferRedemption.setId(partnerOfferRedemption.getId());

        partialUpdatedPartnerOfferRedemption
            .token(UPDATED_TOKEN)
            .redemptionCode(UPDATED_REDEMPTION_CODE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT);

        restPartnerOfferRedemptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartnerOfferRedemption.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartnerOfferRedemption))
            )
            .andExpect(status().isOk());

        // Validate the PartnerOfferRedemption in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartnerOfferRedemptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPartnerOfferRedemption, partnerOfferRedemption),
            getPersistedPartnerOfferRedemption(partnerOfferRedemption)
        );
    }

    @Test
    @Transactional
    void fullUpdatePartnerOfferRedemptionWithPatch() throws Exception {
        // Initialize the database
        insertedPartnerOfferRedemption = partnerOfferRedemptionRepository.saveAndFlush(partnerOfferRedemption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partnerOfferRedemption using partial update
        PartnerOfferRedemption partialUpdatedPartnerOfferRedemption = new PartnerOfferRedemption();
        partialUpdatedPartnerOfferRedemption.setId(partnerOfferRedemption.getId());

        partialUpdatedPartnerOfferRedemption
            .token(UPDATED_TOKEN)
            .redemptionCode(UPDATED_REDEMPTION_CODE)
            .status(UPDATED_STATUS)
            .issuedAt(UPDATED_ISSUED_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .redeemedAt(UPDATED_REDEEMED_AT)
            .orderAmount(UPDATED_ORDER_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT);

        restPartnerOfferRedemptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartnerOfferRedemption.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartnerOfferRedemption))
            )
            .andExpect(status().isOk());

        // Validate the PartnerOfferRedemption in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartnerOfferRedemptionUpdatableFieldsEquals(
            partialUpdatedPartnerOfferRedemption,
            getPersistedPartnerOfferRedemption(partialUpdatedPartnerOfferRedemption)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPartnerOfferRedemption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerOfferRedemption.setId(longCount.incrementAndGet());

        // Create the PartnerOfferRedemption
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartnerOfferRedemptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partnerOfferRedemptionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partnerOfferRedemptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerOfferRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPartnerOfferRedemption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerOfferRedemption.setId(longCount.incrementAndGet());

        // Create the PartnerOfferRedemption
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerOfferRedemptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partnerOfferRedemptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerOfferRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPartnerOfferRedemption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerOfferRedemption.setId(longCount.incrementAndGet());

        // Create the PartnerOfferRedemption
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = partnerOfferRedemptionMapper.toDto(partnerOfferRedemption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerOfferRedemptionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(partnerOfferRedemptionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartnerOfferRedemption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePartnerOfferRedemption() throws Exception {
        // Initialize the database
        insertedPartnerOfferRedemption = partnerOfferRedemptionRepository.saveAndFlush(partnerOfferRedemption);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the partnerOfferRedemption
        restPartnerOfferRedemptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, partnerOfferRedemption.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return partnerOfferRedemptionRepository.count();
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

    protected PartnerOfferRedemption getPersistedPartnerOfferRedemption(PartnerOfferRedemption partnerOfferRedemption) {
        return partnerOfferRedemptionRepository.findById(partnerOfferRedemption.getId()).orElseThrow();
    }

    protected void assertPersistedPartnerOfferRedemptionToMatchAllProperties(PartnerOfferRedemption expectedPartnerOfferRedemption) {
        assertPartnerOfferRedemptionAllPropertiesEquals(
            expectedPartnerOfferRedemption,
            getPersistedPartnerOfferRedemption(expectedPartnerOfferRedemption)
        );
    }

    protected void assertPersistedPartnerOfferRedemptionToMatchUpdatableProperties(PartnerOfferRedemption expectedPartnerOfferRedemption) {
        assertPartnerOfferRedemptionAllUpdatablePropertiesEquals(
            expectedPartnerOfferRedemption,
            getPersistedPartnerOfferRedemption(expectedPartnerOfferRedemption)
        );
    }
}
