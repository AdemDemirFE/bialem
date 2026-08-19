package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.PartnerVenueAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static com.bialem.backend.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.PartnerVenue;
import com.bialem.backend.domain.enumeration.PartnerVenueCategory;
import com.bialem.backend.repository.PartnerVenueRepository;
import com.bialem.backend.service.dto.PartnerVenueDTO;
import com.bialem.backend.service.mapper.PartnerVenueMapper;
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
 * Integration tests for the {@link PartnerVenueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PartnerVenueResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final PartnerVenueCategory DEFAULT_CATEGORY = PartnerVenueCategory.CAFE;
    private static final PartnerVenueCategory UPDATED_CATEGORY = PartnerVenueCategory.RESTAURANT;

    private static final String DEFAULT_LOGO_URL = "AAAAAAAAAA";
    private static final String UPDATED_LOGO_URL = "BBBBBBBBBB";

    private static final String DEFAULT_COVER_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_COVER_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_LATITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LATITUDE = new BigDecimal(2);
    private static final BigDecimal SMALLER_LATITUDE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_LONGITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LONGITUDE = new BigDecimal(2);
    private static final BigDecimal SMALLER_LONGITUDE = new BigDecimal(1 - 1);

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_WEBSITE_URL = "AAAAAAAAAA";
    private static final String UPDATED_WEBSITE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_INSTAGRAM_URL = "AAAAAAAAAA";
    private static final String UPDATED_INSTAGRAM_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_FEATURED = false;
    private static final Boolean UPDATED_IS_FEATURED = true;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/partner-venues";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PartnerVenueRepository partnerVenueRepository;

    @Autowired
    private PartnerVenueMapper partnerVenueMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartnerVenueMockMvc;

    private PartnerVenue partnerVenue;

    private PartnerVenue insertedPartnerVenue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartnerVenue createEntity() {
        return new PartnerVenue()
            .name(DEFAULT_NAME)
            .slug(DEFAULT_SLUG)
            .description(DEFAULT_DESCRIPTION)
            .category(DEFAULT_CATEGORY)
            .logoUrl(DEFAULT_LOGO_URL)
            .coverImageUrl(DEFAULT_COVER_IMAGE_URL)
            .address(DEFAULT_ADDRESS)
            .city(DEFAULT_CITY)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .phone(DEFAULT_PHONE)
            .websiteUrl(DEFAULT_WEBSITE_URL)
            .instagramUrl(DEFAULT_INSTAGRAM_URL)
            .isFeatured(DEFAULT_IS_FEATURED)
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
    public static PartnerVenue createUpdatedEntity() {
        return new PartnerVenue()
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .category(UPDATED_CATEGORY)
            .logoUrl(UPDATED_LOGO_URL)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .phone(UPDATED_PHONE)
            .websiteUrl(UPDATED_WEBSITE_URL)
            .instagramUrl(UPDATED_INSTAGRAM_URL)
            .isFeatured(UPDATED_IS_FEATURED)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        partnerVenue = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPartnerVenue != null) {
            partnerVenueRepository.delete(insertedPartnerVenue);
            insertedPartnerVenue = null;
        }
    }

    @Test
    @Transactional
    void createPartnerVenue() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PartnerVenue
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);
        var returnedPartnerVenueDTO = om.readValue(
            restPartnerVenueMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PartnerVenueDTO.class
        );

        // Validate the PartnerVenue in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPartnerVenue = partnerVenueMapper.toEntity(returnedPartnerVenueDTO);
        assertPartnerVenueUpdatableFieldsEquals(returnedPartnerVenue, getPersistedPartnerVenue(returnedPartnerVenue));

        insertedPartnerVenue = returnedPartnerVenue;
    }

    @Test
    @Transactional
    void createPartnerVenueWithExistingId() throws Exception {
        // Create the PartnerVenue with an existing ID
        partnerVenue.setId(1L);
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartnerVenueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PartnerVenue in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerVenue.setName(null);

        // Create the PartnerVenue, which fails.
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        restPartnerVenueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerVenue.setSlug(null);

        // Create the PartnerVenue, which fails.
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        restPartnerVenueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategoryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerVenue.setCategory(null);

        // Create the PartnerVenue, which fails.
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        restPartnerVenueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerVenue.setAddress(null);

        // Create the PartnerVenue, which fails.
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        restPartnerVenueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerVenue.setCity(null);

        // Create the PartnerVenue, which fails.
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        restPartnerVenueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsFeaturedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerVenue.setIsFeatured(null);

        // Create the PartnerVenue, which fails.
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        restPartnerVenueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerVenue.setIsActive(null);

        // Create the PartnerVenue, which fails.
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        restPartnerVenueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerVenue.setCreatedAt(null);

        // Create the PartnerVenue, which fails.
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        restPartnerVenueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partnerVenue.setUpdatedAt(null);

        // Create the PartnerVenue, which fails.
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        restPartnerVenueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPartnerVenues() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList
        restPartnerVenueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partnerVenue.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].logoUrl").value(hasItem(DEFAULT_LOGO_URL)))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(sameNumber(DEFAULT_LATITUDE))))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(sameNumber(DEFAULT_LONGITUDE))))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].websiteUrl").value(hasItem(DEFAULT_WEBSITE_URL)))
            .andExpect(jsonPath("$.[*].instagramUrl").value(hasItem(DEFAULT_INSTAGRAM_URL)))
            .andExpect(jsonPath("$.[*].isFeatured").value(hasItem(DEFAULT_IS_FEATURED)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getPartnerVenue() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get the partnerVenue
        restPartnerVenueMockMvc
            .perform(get(ENTITY_API_URL_ID, partnerVenue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(partnerVenue.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.logoUrl").value(DEFAULT_LOGO_URL))
            .andExpect(jsonPath("$.coverImageUrl").value(DEFAULT_COVER_IMAGE_URL))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.latitude").value(sameNumber(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.longitude").value(sameNumber(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.websiteUrl").value(DEFAULT_WEBSITE_URL))
            .andExpect(jsonPath("$.instagramUrl").value(DEFAULT_INSTAGRAM_URL))
            .andExpect(jsonPath("$.isFeatured").value(DEFAULT_IS_FEATURED))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getPartnerVenuesByIdFiltering() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        Long id = partnerVenue.getId();

        defaultPartnerVenueFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPartnerVenueFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPartnerVenueFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where name equals to
        defaultPartnerVenueFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where name in
        defaultPartnerVenueFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where name is not null
        defaultPartnerVenueFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where name contains
        defaultPartnerVenueFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where name does not contain
        defaultPartnerVenueFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where slug equals to
        defaultPartnerVenueFiltering("slug.equals=" + DEFAULT_SLUG, "slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where slug in
        defaultPartnerVenueFiltering("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG, "slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where slug is not null
        defaultPartnerVenueFiltering("slug.specified=true", "slug.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesBySlugContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where slug contains
        defaultPartnerVenueFiltering("slug.contains=" + DEFAULT_SLUG, "slug.contains=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesBySlugNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where slug does not contain
        defaultPartnerVenueFiltering("slug.doesNotContain=" + UPDATED_SLUG, "slug.doesNotContain=" + DEFAULT_SLUG);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where category equals to
        defaultPartnerVenueFiltering("category.equals=" + DEFAULT_CATEGORY, "category.equals=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCategoryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where category in
        defaultPartnerVenueFiltering("category.in=" + DEFAULT_CATEGORY + "," + UPDATED_CATEGORY, "category.in=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCategoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where category is not null
        defaultPartnerVenueFiltering("category.specified=true", "category.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLogoUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where logoUrl equals to
        defaultPartnerVenueFiltering("logoUrl.equals=" + DEFAULT_LOGO_URL, "logoUrl.equals=" + UPDATED_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLogoUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where logoUrl in
        defaultPartnerVenueFiltering("logoUrl.in=" + DEFAULT_LOGO_URL + "," + UPDATED_LOGO_URL, "logoUrl.in=" + UPDATED_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLogoUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where logoUrl is not null
        defaultPartnerVenueFiltering("logoUrl.specified=true", "logoUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLogoUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where logoUrl contains
        defaultPartnerVenueFiltering("logoUrl.contains=" + DEFAULT_LOGO_URL, "logoUrl.contains=" + UPDATED_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLogoUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where logoUrl does not contain
        defaultPartnerVenueFiltering("logoUrl.doesNotContain=" + UPDATED_LOGO_URL, "logoUrl.doesNotContain=" + DEFAULT_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCoverImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where coverImageUrl equals to
        defaultPartnerVenueFiltering("coverImageUrl.equals=" + DEFAULT_COVER_IMAGE_URL, "coverImageUrl.equals=" + UPDATED_COVER_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCoverImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where coverImageUrl in
        defaultPartnerVenueFiltering(
            "coverImageUrl.in=" + DEFAULT_COVER_IMAGE_URL + "," + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.in=" + UPDATED_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCoverImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where coverImageUrl is not null
        defaultPartnerVenueFiltering("coverImageUrl.specified=true", "coverImageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCoverImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where coverImageUrl contains
        defaultPartnerVenueFiltering(
            "coverImageUrl.contains=" + DEFAULT_COVER_IMAGE_URL,
            "coverImageUrl.contains=" + UPDATED_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCoverImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where coverImageUrl does not contain
        defaultPartnerVenueFiltering(
            "coverImageUrl.doesNotContain=" + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.doesNotContain=" + DEFAULT_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where address equals to
        defaultPartnerVenueFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where address in
        defaultPartnerVenueFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where address is not null
        defaultPartnerVenueFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where address contains
        defaultPartnerVenueFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where address does not contain
        defaultPartnerVenueFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where city equals to
        defaultPartnerVenueFiltering("city.equals=" + DEFAULT_CITY, "city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where city in
        defaultPartnerVenueFiltering("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY, "city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where city is not null
        defaultPartnerVenueFiltering("city.specified=true", "city.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCityContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where city contains
        defaultPartnerVenueFiltering("city.contains=" + DEFAULT_CITY, "city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCityNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where city does not contain
        defaultPartnerVenueFiltering("city.doesNotContain=" + UPDATED_CITY, "city.doesNotContain=" + DEFAULT_CITY);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where latitude equals to
        defaultPartnerVenueFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where latitude in
        defaultPartnerVenueFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where latitude is not null
        defaultPartnerVenueFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where latitude is greater than or equal to
        defaultPartnerVenueFiltering("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE, "latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where latitude is less than or equal to
        defaultPartnerVenueFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where latitude is less than
        defaultPartnerVenueFiltering("latitude.lessThan=" + UPDATED_LATITUDE, "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where latitude is greater than
        defaultPartnerVenueFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where longitude equals to
        defaultPartnerVenueFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where longitude in
        defaultPartnerVenueFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where longitude is not null
        defaultPartnerVenueFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where longitude is greater than or equal to
        defaultPartnerVenueFiltering(
            "longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE,
            "longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE
        );
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where longitude is less than or equal to
        defaultPartnerVenueFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where longitude is less than
        defaultPartnerVenueFiltering("longitude.lessThan=" + UPDATED_LONGITUDE, "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where longitude is greater than
        defaultPartnerVenueFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where phone equals to
        defaultPartnerVenueFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where phone in
        defaultPartnerVenueFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where phone is not null
        defaultPartnerVenueFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where phone contains
        defaultPartnerVenueFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where phone does not contain
        defaultPartnerVenueFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByWebsiteUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where websiteUrl equals to
        defaultPartnerVenueFiltering("websiteUrl.equals=" + DEFAULT_WEBSITE_URL, "websiteUrl.equals=" + UPDATED_WEBSITE_URL);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByWebsiteUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where websiteUrl in
        defaultPartnerVenueFiltering(
            "websiteUrl.in=" + DEFAULT_WEBSITE_URL + "," + UPDATED_WEBSITE_URL,
            "websiteUrl.in=" + UPDATED_WEBSITE_URL
        );
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByWebsiteUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where websiteUrl is not null
        defaultPartnerVenueFiltering("websiteUrl.specified=true", "websiteUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByWebsiteUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where websiteUrl contains
        defaultPartnerVenueFiltering("websiteUrl.contains=" + DEFAULT_WEBSITE_URL, "websiteUrl.contains=" + UPDATED_WEBSITE_URL);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByWebsiteUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where websiteUrl does not contain
        defaultPartnerVenueFiltering(
            "websiteUrl.doesNotContain=" + UPDATED_WEBSITE_URL,
            "websiteUrl.doesNotContain=" + DEFAULT_WEBSITE_URL
        );
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByInstagramUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where instagramUrl equals to
        defaultPartnerVenueFiltering("instagramUrl.equals=" + DEFAULT_INSTAGRAM_URL, "instagramUrl.equals=" + UPDATED_INSTAGRAM_URL);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByInstagramUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where instagramUrl in
        defaultPartnerVenueFiltering(
            "instagramUrl.in=" + DEFAULT_INSTAGRAM_URL + "," + UPDATED_INSTAGRAM_URL,
            "instagramUrl.in=" + UPDATED_INSTAGRAM_URL
        );
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByInstagramUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where instagramUrl is not null
        defaultPartnerVenueFiltering("instagramUrl.specified=true", "instagramUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByInstagramUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where instagramUrl contains
        defaultPartnerVenueFiltering("instagramUrl.contains=" + DEFAULT_INSTAGRAM_URL, "instagramUrl.contains=" + UPDATED_INSTAGRAM_URL);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByInstagramUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where instagramUrl does not contain
        defaultPartnerVenueFiltering(
            "instagramUrl.doesNotContain=" + UPDATED_INSTAGRAM_URL,
            "instagramUrl.doesNotContain=" + DEFAULT_INSTAGRAM_URL
        );
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByIsFeaturedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where isFeatured equals to
        defaultPartnerVenueFiltering("isFeatured.equals=" + DEFAULT_IS_FEATURED, "isFeatured.equals=" + UPDATED_IS_FEATURED);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByIsFeaturedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where isFeatured in
        defaultPartnerVenueFiltering(
            "isFeatured.in=" + DEFAULT_IS_FEATURED + "," + UPDATED_IS_FEATURED,
            "isFeatured.in=" + UPDATED_IS_FEATURED
        );
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByIsFeaturedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where isFeatured is not null
        defaultPartnerVenueFiltering("isFeatured.specified=true", "isFeatured.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where isActive equals to
        defaultPartnerVenueFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where isActive in
        defaultPartnerVenueFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where isActive is not null
        defaultPartnerVenueFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where createdAt equals to
        defaultPartnerVenueFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where createdAt in
        defaultPartnerVenueFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where createdAt is not null
        defaultPartnerVenueFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where updatedAt equals to
        defaultPartnerVenueFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where updatedAt in
        defaultPartnerVenueFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllPartnerVenuesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        // Get all the partnerVenueList where updatedAt is not null
        defaultPartnerVenueFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    private void defaultPartnerVenueFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPartnerVenueShouldBeFound(shouldBeFound);
        defaultPartnerVenueShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPartnerVenueShouldBeFound(String filter) throws Exception {
        restPartnerVenueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partnerVenue.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].logoUrl").value(hasItem(DEFAULT_LOGO_URL)))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(sameNumber(DEFAULT_LATITUDE))))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(sameNumber(DEFAULT_LONGITUDE))))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].websiteUrl").value(hasItem(DEFAULT_WEBSITE_URL)))
            .andExpect(jsonPath("$.[*].instagramUrl").value(hasItem(DEFAULT_INSTAGRAM_URL)))
            .andExpect(jsonPath("$.[*].isFeatured").value(hasItem(DEFAULT_IS_FEATURED)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restPartnerVenueMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPartnerVenueShouldNotBeFound(String filter) throws Exception {
        restPartnerVenueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPartnerVenueMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPartnerVenue() throws Exception {
        // Get the partnerVenue
        restPartnerVenueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPartnerVenue() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partnerVenue
        PartnerVenue updatedPartnerVenue = partnerVenueRepository.findById(partnerVenue.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPartnerVenue are not directly saved in db
        em.detach(updatedPartnerVenue);
        updatedPartnerVenue
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .category(UPDATED_CATEGORY)
            .logoUrl(UPDATED_LOGO_URL)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .phone(UPDATED_PHONE)
            .websiteUrl(UPDATED_WEBSITE_URL)
            .instagramUrl(UPDATED_INSTAGRAM_URL)
            .isFeatured(UPDATED_IS_FEATURED)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(updatedPartnerVenue);

        restPartnerVenueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partnerVenueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partnerVenueDTO))
            )
            .andExpect(status().isOk());

        // Validate the PartnerVenue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPartnerVenueToMatchAllProperties(updatedPartnerVenue);
    }

    @Test
    @Transactional
    void putNonExistingPartnerVenue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerVenue.setId(longCount.incrementAndGet());

        // Create the PartnerVenue
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartnerVenueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partnerVenueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partnerVenueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerVenue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPartnerVenue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerVenue.setId(longCount.incrementAndGet());

        // Create the PartnerVenue
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerVenueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partnerVenueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerVenue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPartnerVenue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerVenue.setId(longCount.incrementAndGet());

        // Create the PartnerVenue
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerVenueMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerVenueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartnerVenue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartnerVenueWithPatch() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partnerVenue using partial update
        PartnerVenue partialUpdatedPartnerVenue = new PartnerVenue();
        partialUpdatedPartnerVenue.setId(partnerVenue.getId());

        partialUpdatedPartnerVenue
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .phone(UPDATED_PHONE)
            .instagramUrl(UPDATED_INSTAGRAM_URL);

        restPartnerVenueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartnerVenue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartnerVenue))
            )
            .andExpect(status().isOk());

        // Validate the PartnerVenue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartnerVenueUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPartnerVenue, partnerVenue),
            getPersistedPartnerVenue(partnerVenue)
        );
    }

    @Test
    @Transactional
    void fullUpdatePartnerVenueWithPatch() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partnerVenue using partial update
        PartnerVenue partialUpdatedPartnerVenue = new PartnerVenue();
        partialUpdatedPartnerVenue.setId(partnerVenue.getId());

        partialUpdatedPartnerVenue
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .category(UPDATED_CATEGORY)
            .logoUrl(UPDATED_LOGO_URL)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .phone(UPDATED_PHONE)
            .websiteUrl(UPDATED_WEBSITE_URL)
            .instagramUrl(UPDATED_INSTAGRAM_URL)
            .isFeatured(UPDATED_IS_FEATURED)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restPartnerVenueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartnerVenue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartnerVenue))
            )
            .andExpect(status().isOk());

        // Validate the PartnerVenue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartnerVenueUpdatableFieldsEquals(partialUpdatedPartnerVenue, getPersistedPartnerVenue(partialUpdatedPartnerVenue));
    }

    @Test
    @Transactional
    void patchNonExistingPartnerVenue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerVenue.setId(longCount.incrementAndGet());

        // Create the PartnerVenue
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartnerVenueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partnerVenueDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partnerVenueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerVenue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPartnerVenue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerVenue.setId(longCount.incrementAndGet());

        // Create the PartnerVenue
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerVenueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partnerVenueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartnerVenue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPartnerVenue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partnerVenue.setId(longCount.incrementAndGet());

        // Create the PartnerVenue
        PartnerVenueDTO partnerVenueDTO = partnerVenueMapper.toDto(partnerVenue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerVenueMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(partnerVenueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartnerVenue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePartnerVenue() throws Exception {
        // Initialize the database
        insertedPartnerVenue = partnerVenueRepository.saveAndFlush(partnerVenue);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the partnerVenue
        restPartnerVenueMockMvc
            .perform(delete(ENTITY_API_URL_ID, partnerVenue.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return partnerVenueRepository.count();
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

    protected PartnerVenue getPersistedPartnerVenue(PartnerVenue partnerVenue) {
        return partnerVenueRepository.findById(partnerVenue.getId()).orElseThrow();
    }

    protected void assertPersistedPartnerVenueToMatchAllProperties(PartnerVenue expectedPartnerVenue) {
        assertPartnerVenueAllPropertiesEquals(expectedPartnerVenue, getPersistedPartnerVenue(expectedPartnerVenue));
    }

    protected void assertPersistedPartnerVenueToMatchUpdatableProperties(PartnerVenue expectedPartnerVenue) {
        assertPartnerVenueAllUpdatablePropertiesEquals(expectedPartnerVenue, getPersistedPartnerVenue(expectedPartnerVenue));
    }
}
