package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.CommunityAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.enumeration.CommunityType;
import com.bialem.backend.domain.enumeration.CommunityVisibility;
import com.bialem.backend.domain.enumeration.PartnerTrustLevel;
import com.bialem.backend.repository.CommunityRepository;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.mapper.CommunityMapper;
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
 * Integration tests for the {@link CommunityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CommunityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final CommunityVisibility DEFAULT_VISIBILITY = CommunityVisibility.PUBLIC;
    private static final CommunityVisibility UPDATED_VISIBILITY = CommunityVisibility.PRIVATE;

    private static final String DEFAULT_COVER_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_COVER_IMAGE_URL = "BBBBBBBBBB";

    private static final CommunityType DEFAULT_COMMUNITY_TYPE = CommunityType.CATEGORY_HUB;
    private static final CommunityType UPDATED_COMMUNITY_TYPE = CommunityType.PARTNER_HUB;

    private static final PartnerTrustLevel DEFAULT_PARTNER_TRUST_LEVEL = PartnerTrustLevel.NEW;
    private static final PartnerTrustLevel UPDATED_PARTNER_TRUST_LEVEL = PartnerTrustLevel.VERIFIED;

    private static final Boolean DEFAULT_IS_VERIFIED_PARTNER = false;
    private static final Boolean UPDATED_IS_VERIFIED_PARTNER = true;

    private static final Boolean DEFAULT_IS_DISCOVERABLE = false;
    private static final Boolean UPDATED_IS_DISCOVERABLE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/communities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommunityMockMvc;

    private Community community;

    private Community insertedCommunity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Community createEntity() {
        return new Community()
            .name(DEFAULT_NAME)
            .slug(DEFAULT_SLUG)
            .description(DEFAULT_DESCRIPTION)
            .visibility(DEFAULT_VISIBILITY)
            .coverImageUrl(DEFAULT_COVER_IMAGE_URL)
            .communityType(DEFAULT_COMMUNITY_TYPE)
            .partnerTrustLevel(DEFAULT_PARTNER_TRUST_LEVEL)
            .isVerifiedPartner(DEFAULT_IS_VERIFIED_PARTNER)
            .isDiscoverable(DEFAULT_IS_DISCOVERABLE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Community createUpdatedEntity() {
        return new Community()
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .visibility(UPDATED_VISIBILITY)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .communityType(UPDATED_COMMUNITY_TYPE)
            .partnerTrustLevel(UPDATED_PARTNER_TRUST_LEVEL)
            .isVerifiedPartner(UPDATED_IS_VERIFIED_PARTNER)
            .isDiscoverable(UPDATED_IS_DISCOVERABLE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        community = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCommunity != null) {
            communityRepository.delete(insertedCommunity);
            insertedCommunity = null;
        }
    }

    @Test
    @Transactional
    void createCommunity() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Community
        CommunityDTO communityDTO = communityMapper.toDto(community);
        var returnedCommunityDTO = om.readValue(
            restCommunityMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CommunityDTO.class
        );

        // Validate the Community in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCommunity = communityMapper.toEntity(returnedCommunityDTO);
        assertCommunityUpdatableFieldsEquals(returnedCommunity, getPersistedCommunity(returnedCommunity));

        insertedCommunity = returnedCommunity;
    }

    @Test
    @Transactional
    void createCommunityWithExistingId() throws Exception {
        // Create the Community with an existing ID
        community.setId(1L);
        CommunityDTO communityDTO = communityMapper.toDto(community);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommunityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Community in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        community.setName(null);

        // Create the Community, which fails.
        CommunityDTO communityDTO = communityMapper.toDto(community);

        restCommunityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        community.setSlug(null);

        // Create the Community, which fails.
        CommunityDTO communityDTO = communityMapper.toDto(community);

        restCommunityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVisibilityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        community.setVisibility(null);

        // Create the Community, which fails.
        CommunityDTO communityDTO = communityMapper.toDto(community);

        restCommunityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCommunityTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        community.setCommunityType(null);

        // Create the Community, which fails.
        CommunityDTO communityDTO = communityMapper.toDto(community);

        restCommunityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPartnerTrustLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        community.setPartnerTrustLevel(null);

        // Create the Community, which fails.
        CommunityDTO communityDTO = communityMapper.toDto(community);

        restCommunityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsVerifiedPartnerIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        community.setIsVerifiedPartner(null);

        // Create the Community, which fails.
        CommunityDTO communityDTO = communityMapper.toDto(community);

        restCommunityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsDiscoverableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        community.setIsDiscoverable(null);

        // Create the Community, which fails.
        CommunityDTO communityDTO = communityMapper.toDto(community);

        restCommunityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        community.setCreatedAt(null);

        // Create the Community, which fails.
        CommunityDTO communityDTO = communityMapper.toDto(community);

        restCommunityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        community.setUpdatedAt(null);

        // Create the Community, which fails.
        CommunityDTO communityDTO = communityMapper.toDto(community);

        restCommunityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCommunities() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList
        restCommunityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(community.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].communityType").value(hasItem(DEFAULT_COMMUNITY_TYPE.toString())))
            .andExpect(jsonPath("$.[*].partnerTrustLevel").value(hasItem(DEFAULT_PARTNER_TRUST_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].isVerifiedPartner").value(hasItem(DEFAULT_IS_VERIFIED_PARTNER)))
            .andExpect(jsonPath("$.[*].isDiscoverable").value(hasItem(DEFAULT_IS_DISCOVERABLE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCommunity() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get the community
        restCommunityMockMvc
            .perform(get(ENTITY_API_URL_ID, community.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(community.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.visibility").value(DEFAULT_VISIBILITY.toString()))
            .andExpect(jsonPath("$.coverImageUrl").value(DEFAULT_COVER_IMAGE_URL))
            .andExpect(jsonPath("$.communityType").value(DEFAULT_COMMUNITY_TYPE.toString()))
            .andExpect(jsonPath("$.partnerTrustLevel").value(DEFAULT_PARTNER_TRUST_LEVEL.toString()))
            .andExpect(jsonPath("$.isVerifiedPartner").value(DEFAULT_IS_VERIFIED_PARTNER))
            .andExpect(jsonPath("$.isDiscoverable").value(DEFAULT_IS_DISCOVERABLE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getCommunitiesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        Long id = community.getId();

        defaultCommunityFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCommunityFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCommunityFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCommunitiesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where name equals to
        defaultCommunityFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCommunitiesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where name in
        defaultCommunityFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCommunitiesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where name is not null
        defaultCommunityFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllCommunitiesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where name contains
        defaultCommunityFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCommunitiesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where name does not contain
        defaultCommunityFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllCommunitiesBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where slug equals to
        defaultCommunityFiltering("slug.equals=" + DEFAULT_SLUG, "slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllCommunitiesBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where slug in
        defaultCommunityFiltering("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG, "slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllCommunitiesBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where slug is not null
        defaultCommunityFiltering("slug.specified=true", "slug.specified=false");
    }

    @Test
    @Transactional
    void getAllCommunitiesBySlugContainsSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where slug contains
        defaultCommunityFiltering("slug.contains=" + DEFAULT_SLUG, "slug.contains=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllCommunitiesBySlugNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where slug does not contain
        defaultCommunityFiltering("slug.doesNotContain=" + UPDATED_SLUG, "slug.doesNotContain=" + DEFAULT_SLUG);
    }

    @Test
    @Transactional
    void getAllCommunitiesByVisibilityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where visibility equals to
        defaultCommunityFiltering("visibility.equals=" + DEFAULT_VISIBILITY, "visibility.equals=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    void getAllCommunitiesByVisibilityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where visibility in
        defaultCommunityFiltering("visibility.in=" + DEFAULT_VISIBILITY + "," + UPDATED_VISIBILITY, "visibility.in=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    void getAllCommunitiesByVisibilityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where visibility is not null
        defaultCommunityFiltering("visibility.specified=true", "visibility.specified=false");
    }

    @Test
    @Transactional
    void getAllCommunitiesByCoverImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where coverImageUrl equals to
        defaultCommunityFiltering("coverImageUrl.equals=" + DEFAULT_COVER_IMAGE_URL, "coverImageUrl.equals=" + UPDATED_COVER_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllCommunitiesByCoverImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where coverImageUrl in
        defaultCommunityFiltering(
            "coverImageUrl.in=" + DEFAULT_COVER_IMAGE_URL + "," + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.in=" + UPDATED_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllCommunitiesByCoverImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where coverImageUrl is not null
        defaultCommunityFiltering("coverImageUrl.specified=true", "coverImageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllCommunitiesByCoverImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where coverImageUrl contains
        defaultCommunityFiltering("coverImageUrl.contains=" + DEFAULT_COVER_IMAGE_URL, "coverImageUrl.contains=" + UPDATED_COVER_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllCommunitiesByCoverImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where coverImageUrl does not contain
        defaultCommunityFiltering(
            "coverImageUrl.doesNotContain=" + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.doesNotContain=" + DEFAULT_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllCommunitiesByCommunityTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where communityType equals to
        defaultCommunityFiltering("communityType.equals=" + DEFAULT_COMMUNITY_TYPE, "communityType.equals=" + UPDATED_COMMUNITY_TYPE);
    }

    @Test
    @Transactional
    void getAllCommunitiesByCommunityTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where communityType in
        defaultCommunityFiltering(
            "communityType.in=" + DEFAULT_COMMUNITY_TYPE + "," + UPDATED_COMMUNITY_TYPE,
            "communityType.in=" + UPDATED_COMMUNITY_TYPE
        );
    }

    @Test
    @Transactional
    void getAllCommunitiesByCommunityTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where communityType is not null
        defaultCommunityFiltering("communityType.specified=true", "communityType.specified=false");
    }

    @Test
    @Transactional
    void getAllCommunitiesByPartnerTrustLevelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where partnerTrustLevel equals to
        defaultCommunityFiltering(
            "partnerTrustLevel.equals=" + DEFAULT_PARTNER_TRUST_LEVEL,
            "partnerTrustLevel.equals=" + UPDATED_PARTNER_TRUST_LEVEL
        );
    }

    @Test
    @Transactional
    void getAllCommunitiesByPartnerTrustLevelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where partnerTrustLevel in
        defaultCommunityFiltering(
            "partnerTrustLevel.in=" + DEFAULT_PARTNER_TRUST_LEVEL + "," + UPDATED_PARTNER_TRUST_LEVEL,
            "partnerTrustLevel.in=" + UPDATED_PARTNER_TRUST_LEVEL
        );
    }

    @Test
    @Transactional
    void getAllCommunitiesByPartnerTrustLevelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where partnerTrustLevel is not null
        defaultCommunityFiltering("partnerTrustLevel.specified=true", "partnerTrustLevel.specified=false");
    }

    @Test
    @Transactional
    void getAllCommunitiesByIsVerifiedPartnerIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where isVerifiedPartner equals to
        defaultCommunityFiltering(
            "isVerifiedPartner.equals=" + DEFAULT_IS_VERIFIED_PARTNER,
            "isVerifiedPartner.equals=" + UPDATED_IS_VERIFIED_PARTNER
        );
    }

    @Test
    @Transactional
    void getAllCommunitiesByIsVerifiedPartnerIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where isVerifiedPartner in
        defaultCommunityFiltering(
            "isVerifiedPartner.in=" + DEFAULT_IS_VERIFIED_PARTNER + "," + UPDATED_IS_VERIFIED_PARTNER,
            "isVerifiedPartner.in=" + UPDATED_IS_VERIFIED_PARTNER
        );
    }

    @Test
    @Transactional
    void getAllCommunitiesByIsVerifiedPartnerIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where isVerifiedPartner is not null
        defaultCommunityFiltering("isVerifiedPartner.specified=true", "isVerifiedPartner.specified=false");
    }

    @Test
    @Transactional
    void getAllCommunitiesByIsDiscoverableIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where isDiscoverable equals to
        defaultCommunityFiltering("isDiscoverable.equals=" + DEFAULT_IS_DISCOVERABLE, "isDiscoverable.equals=" + UPDATED_IS_DISCOVERABLE);
    }

    @Test
    @Transactional
    void getAllCommunitiesByIsDiscoverableIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where isDiscoverable in
        defaultCommunityFiltering(
            "isDiscoverable.in=" + DEFAULT_IS_DISCOVERABLE + "," + UPDATED_IS_DISCOVERABLE,
            "isDiscoverable.in=" + UPDATED_IS_DISCOVERABLE
        );
    }

    @Test
    @Transactional
    void getAllCommunitiesByIsDiscoverableIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where isDiscoverable is not null
        defaultCommunityFiltering("isDiscoverable.specified=true", "isDiscoverable.specified=false");
    }

    @Test
    @Transactional
    void getAllCommunitiesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where createdAt equals to
        defaultCommunityFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCommunitiesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where createdAt in
        defaultCommunityFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCommunitiesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where createdAt is not null
        defaultCommunityFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCommunitiesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where updatedAt equals to
        defaultCommunityFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCommunitiesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where updatedAt in
        defaultCommunityFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCommunitiesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        // Get all the communityList where updatedAt is not null
        defaultCommunityFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCommunitiesByParentIsEqualToSomething() throws Exception {
        Community parent;
        if (TestUtil.findAll(em, Community.class).isEmpty()) {
            communityRepository.saveAndFlush(community);
            parent = CommunityResourceIT.createEntity();
        } else {
            parent = TestUtil.findAll(em, Community.class).get(0);
        }
        em.persist(parent);
        em.flush();
        community.setParent(parent);
        communityRepository.saveAndFlush(community);
        Long parentId = parent.getId();
        // Get all the communityList where parent equals to parentId
        defaultCommunityShouldBeFound("parentId.equals=" + parentId);

        // Get all the communityList where parent equals to (parentId + 1)
        defaultCommunityShouldNotBeFound("parentId.equals=" + (parentId + 1));
    }

    @Test
    @Transactional
    void getAllCommunitiesByCategoryHubIsEqualToSomething() throws Exception {
        Community categoryHub;
        if (TestUtil.findAll(em, Community.class).isEmpty()) {
            communityRepository.saveAndFlush(community);
            categoryHub = CommunityResourceIT.createEntity();
        } else {
            categoryHub = TestUtil.findAll(em, Community.class).get(0);
        }
        em.persist(categoryHub);
        em.flush();
        community.setCategoryHub(categoryHub);
        communityRepository.saveAndFlush(community);
        Long categoryHubId = categoryHub.getId();
        // Get all the communityList where categoryHub equals to categoryHubId
        defaultCommunityShouldBeFound("categoryHubId.equals=" + categoryHubId);

        // Get all the communityList where categoryHub equals to (categoryHubId + 1)
        defaultCommunityShouldNotBeFound("categoryHubId.equals=" + (categoryHubId + 1));
    }

    @Test
    @Transactional
    void getAllCommunitiesByCreatedByIsEqualToSomething() throws Exception {
        Profile createdBy;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            communityRepository.saveAndFlush(community);
            createdBy = ProfileResourceIT.createEntity(em);
        } else {
            createdBy = TestUtil.findAll(em, Profile.class).get(0);
        }
        em.persist(createdBy);
        em.flush();
        community.setCreatedBy(createdBy);
        communityRepository.saveAndFlush(community);
        Long createdById = createdBy.getId();
        // Get all the communityList where createdBy equals to createdById
        defaultCommunityShouldBeFound("createdById.equals=" + createdById);

        // Get all the communityList where createdBy equals to (createdById + 1)
        defaultCommunityShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }

    @Test
    @Transactional
    void getAllCommunitiesByLeadModeratorIsEqualToSomething() throws Exception {
        Profile leadModerator;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            communityRepository.saveAndFlush(community);
            leadModerator = ProfileResourceIT.createEntity(em);
        } else {
            leadModerator = TestUtil.findAll(em, Profile.class).get(0);
        }
        em.persist(leadModerator);
        em.flush();
        community.setLeadModerator(leadModerator);
        communityRepository.saveAndFlush(community);
        Long leadModeratorId = leadModerator.getId();
        // Get all the communityList where leadModerator equals to leadModeratorId
        defaultCommunityShouldBeFound("leadModeratorId.equals=" + leadModeratorId);

        // Get all the communityList where leadModerator equals to (leadModeratorId + 1)
        defaultCommunityShouldNotBeFound("leadModeratorId.equals=" + (leadModeratorId + 1));
    }

    private void defaultCommunityFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCommunityShouldBeFound(shouldBeFound);
        defaultCommunityShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCommunityShouldBeFound(String filter) throws Exception {
        restCommunityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(community.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].communityType").value(hasItem(DEFAULT_COMMUNITY_TYPE.toString())))
            .andExpect(jsonPath("$.[*].partnerTrustLevel").value(hasItem(DEFAULT_PARTNER_TRUST_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].isVerifiedPartner").value(hasItem(DEFAULT_IS_VERIFIED_PARTNER)))
            .andExpect(jsonPath("$.[*].isDiscoverable").value(hasItem(DEFAULT_IS_DISCOVERABLE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restCommunityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCommunityShouldNotBeFound(String filter) throws Exception {
        restCommunityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCommunityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCommunity() throws Exception {
        // Get the community
        restCommunityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCommunity() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the community
        Community updatedCommunity = communityRepository.findById(community.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCommunity are not directly saved in db
        em.detach(updatedCommunity);
        updatedCommunity
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .visibility(UPDATED_VISIBILITY)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .communityType(UPDATED_COMMUNITY_TYPE)
            .partnerTrustLevel(UPDATED_PARTNER_TRUST_LEVEL)
            .isVerifiedPartner(UPDATED_IS_VERIFIED_PARTNER)
            .isDiscoverable(UPDATED_IS_DISCOVERABLE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        CommunityDTO communityDTO = communityMapper.toDto(updatedCommunity);

        restCommunityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, communityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(communityDTO))
            )
            .andExpect(status().isOk());

        // Validate the Community in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCommunityToMatchAllProperties(updatedCommunity);
    }

    @Test
    @Transactional
    void putNonExistingCommunity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        community.setId(longCount.incrementAndGet());

        // Create the Community
        CommunityDTO communityDTO = communityMapper.toDto(community);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommunityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, communityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(communityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Community in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCommunity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        community.setId(longCount.incrementAndGet());

        // Create the Community
        CommunityDTO communityDTO = communityMapper.toDto(community);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(communityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Community in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCommunity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        community.setId(longCount.incrementAndGet());

        // Create the Community
        CommunityDTO communityDTO = communityMapper.toDto(community);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(communityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Community in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommunityWithPatch() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the community using partial update
        Community partialUpdatedCommunity = new Community();
        partialUpdatedCommunity.setId(community.getId());

        partialUpdatedCommunity
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .visibility(UPDATED_VISIBILITY)
            .isDiscoverable(UPDATED_IS_DISCOVERABLE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCommunityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommunity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCommunity))
            )
            .andExpect(status().isOk());

        // Validate the Community in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommunityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCommunity, community),
            getPersistedCommunity(community)
        );
    }

    @Test
    @Transactional
    void fullUpdateCommunityWithPatch() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the community using partial update
        Community partialUpdatedCommunity = new Community();
        partialUpdatedCommunity.setId(community.getId());

        partialUpdatedCommunity
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .visibility(UPDATED_VISIBILITY)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .communityType(UPDATED_COMMUNITY_TYPE)
            .partnerTrustLevel(UPDATED_PARTNER_TRUST_LEVEL)
            .isVerifiedPartner(UPDATED_IS_VERIFIED_PARTNER)
            .isDiscoverable(UPDATED_IS_DISCOVERABLE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCommunityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommunity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCommunity))
            )
            .andExpect(status().isOk());

        // Validate the Community in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommunityUpdatableFieldsEquals(partialUpdatedCommunity, getPersistedCommunity(partialUpdatedCommunity));
    }

    @Test
    @Transactional
    void patchNonExistingCommunity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        community.setId(longCount.incrementAndGet());

        // Create the Community
        CommunityDTO communityDTO = communityMapper.toDto(community);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommunityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, communityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(communityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Community in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCommunity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        community.setId(longCount.incrementAndGet());

        // Create the Community
        CommunityDTO communityDTO = communityMapper.toDto(community);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(communityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Community in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCommunity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        community.setId(longCount.incrementAndGet());

        // Create the Community
        CommunityDTO communityDTO = communityMapper.toDto(community);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommunityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(communityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Community in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCommunity() throws Exception {
        // Initialize the database
        insertedCommunity = communityRepository.saveAndFlush(community);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the community
        restCommunityMockMvc
            .perform(delete(ENTITY_API_URL_ID, community.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return communityRepository.count();
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

    protected Community getPersistedCommunity(Community community) {
        return communityRepository.findById(community.getId()).orElseThrow();
    }

    protected void assertPersistedCommunityToMatchAllProperties(Community expectedCommunity) {
        assertCommunityAllPropertiesEquals(expectedCommunity, getPersistedCommunity(expectedCommunity));
    }

    protected void assertPersistedCommunityToMatchUpdatableProperties(Community expectedCommunity) {
        assertCommunityAllUpdatablePropertiesEquals(expectedCommunity, getPersistedCommunity(expectedCommunity));
    }
}
