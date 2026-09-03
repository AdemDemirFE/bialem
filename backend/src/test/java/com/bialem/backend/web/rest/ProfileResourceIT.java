package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.ProfileAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.domain.enumeration.ProfileStatus;
import com.bialem.backend.repository.ProfileRepository;
import com.bialem.backend.repository.UserRepository;
import com.bialem.backend.service.ProfileService;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.mapper.ProfileMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProfileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(authorities = { "ROLE_ADMIN" })
class ProfileResourceIT {

    private static final String DEFAULT_DISPLAY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DISPLAY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_AVATAR_URL = "AAAAAAAAAA";
    private static final String UPDATED_AVATAR_URL = "BBBBBBBBBB";

    private static final String DEFAULT_BIO = "AAAAAAAAAA";
    private static final String UPDATED_BIO = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final ProfileStatus DEFAULT_STATUS = ProfileStatus.ACTIVE;
    private static final ProfileStatus UPDATED_STATUS = ProfileStatus.PENDING_VERIFICATION;

    private static final Boolean DEFAULT_IS_VERIFIED = false;
    private static final Boolean UPDATED_IS_VERIFIED = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepositoryMock;

    @Autowired
    private ProfileMapper profileMapper;

    @Mock
    private ProfileService profileServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProfileMockMvc;

    private Profile profile;

    private Profile insertedProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profile createEntity(EntityManager em) {
        Profile profile = new Profile()
            .displayName(DEFAULT_DISPLAY_NAME)
            .username(DEFAULT_USERNAME)
            .avatarUrl(DEFAULT_AVATAR_URL)
            .bio(DEFAULT_BIO)
            .city(DEFAULT_CITY)
            .status(DEFAULT_STATUS)
            .isVerified(DEFAULT_IS_VERIFIED)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        profile.setUser(user);
        return profile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profile createUpdatedEntity(EntityManager em) {
        Profile updatedProfile = new Profile()
            .displayName(UPDATED_DISPLAY_NAME)
            .username(UPDATED_USERNAME)
            .avatarUrl(UPDATED_AVATAR_URL)
            .bio(UPDATED_BIO)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS)
            .isVerified(UPDATED_IS_VERIFIED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedProfile.setUser(user);
        return updatedProfile;
    }

    @BeforeEach
    void initTest() {
        profile = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedProfile != null) {
            profileRepository.delete(insertedProfile);
            insertedProfile = null;
        }
    }

    @Test
    @Transactional
    void createProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);
        var returnedProfileDTO = om.readValue(
            restProfileMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProfileDTO.class
        );

        // Validate the Profile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProfile = profileMapper.toEntity(returnedProfileDTO);
        assertProfileUpdatableFieldsEquals(returnedProfile, getPersistedProfile(returnedProfile));

        insertedProfile = returnedProfile;
    }

    @Test
    @Transactional
    void createProfileWithExistingId() throws Exception {
        // Create the Profile with an existing ID
        profile.setId(1L);
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDisplayNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        profile.setDisplayName(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUsernameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        profile.setUsername(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        profile.setStatus(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsVerifiedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        profile.setIsVerified(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        profile.setCreatedAt(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        profile.setUpdatedAt(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProfiles() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profile.getId().intValue())))
            .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME)))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].avatarUrl").value(hasItem(DEFAULT_AVATAR_URL)))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isVerified").value(hasItem(DEFAULT_IS_VERIFIED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProfilesWithEagerRelationshipsIsEnabled() throws Exception {
        when(profileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(profileServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProfilesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(profileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(profileRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProfile() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get the profile
        restProfileMockMvc
            .perform(get(ENTITY_API_URL_ID, profile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(profile.getId().intValue()))
            .andExpect(jsonPath("$.displayName").value(DEFAULT_DISPLAY_NAME))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.avatarUrl").value(DEFAULT_AVATAR_URL))
            .andExpect(jsonPath("$.bio").value(DEFAULT_BIO))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.isVerified").value(DEFAULT_IS_VERIFIED))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getProfilesByIdFiltering() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        Long id = profile.getId();

        defaultProfileFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProfileFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProfileFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProfilesByDisplayNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where displayName equals to
        defaultProfileFiltering("displayName.equals=" + DEFAULT_DISPLAY_NAME, "displayName.equals=" + UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllProfilesByDisplayNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where displayName in
        defaultProfileFiltering(
            "displayName.in=" + DEFAULT_DISPLAY_NAME + "," + UPDATED_DISPLAY_NAME,
            "displayName.in=" + UPDATED_DISPLAY_NAME
        );
    }

    @Test
    @Transactional
    void getAllProfilesByDisplayNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where displayName is not null
        defaultProfileFiltering("displayName.specified=true", "displayName.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByDisplayNameContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where displayName contains
        defaultProfileFiltering("displayName.contains=" + DEFAULT_DISPLAY_NAME, "displayName.contains=" + UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllProfilesByDisplayNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where displayName does not contain
        defaultProfileFiltering("displayName.doesNotContain=" + UPDATED_DISPLAY_NAME, "displayName.doesNotContain=" + DEFAULT_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllProfilesByUsernameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where username equals to
        defaultProfileFiltering("username.equals=" + DEFAULT_USERNAME, "username.equals=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    void getAllProfilesByUsernameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where username in
        defaultProfileFiltering("username.in=" + DEFAULT_USERNAME + "," + UPDATED_USERNAME, "username.in=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    void getAllProfilesByUsernameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where username is not null
        defaultProfileFiltering("username.specified=true", "username.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByUsernameContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where username contains
        defaultProfileFiltering("username.contains=" + DEFAULT_USERNAME, "username.contains=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    void getAllProfilesByUsernameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where username does not contain
        defaultProfileFiltering("username.doesNotContain=" + UPDATED_USERNAME, "username.doesNotContain=" + DEFAULT_USERNAME);
    }

    @Test
    @Transactional
    void getAllProfilesByAvatarUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where avatarUrl equals to
        defaultProfileFiltering("avatarUrl.equals=" + DEFAULT_AVATAR_URL, "avatarUrl.equals=" + UPDATED_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllProfilesByAvatarUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where avatarUrl in
        defaultProfileFiltering("avatarUrl.in=" + DEFAULT_AVATAR_URL + "," + UPDATED_AVATAR_URL, "avatarUrl.in=" + UPDATED_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllProfilesByAvatarUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where avatarUrl is not null
        defaultProfileFiltering("avatarUrl.specified=true", "avatarUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByAvatarUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where avatarUrl contains
        defaultProfileFiltering("avatarUrl.contains=" + DEFAULT_AVATAR_URL, "avatarUrl.contains=" + UPDATED_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllProfilesByAvatarUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where avatarUrl does not contain
        defaultProfileFiltering("avatarUrl.doesNotContain=" + UPDATED_AVATAR_URL, "avatarUrl.doesNotContain=" + DEFAULT_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllProfilesByBioIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where bio equals to
        defaultProfileFiltering("bio.equals=" + DEFAULT_BIO, "bio.equals=" + UPDATED_BIO);
    }

    @Test
    @Transactional
    void getAllProfilesByBioIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where bio in
        defaultProfileFiltering("bio.in=" + DEFAULT_BIO + "," + UPDATED_BIO, "bio.in=" + UPDATED_BIO);
    }

    @Test
    @Transactional
    void getAllProfilesByBioIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where bio is not null
        defaultProfileFiltering("bio.specified=true", "bio.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByBioContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where bio contains
        defaultProfileFiltering("bio.contains=" + DEFAULT_BIO, "bio.contains=" + UPDATED_BIO);
    }

    @Test
    @Transactional
    void getAllProfilesByBioNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where bio does not contain
        defaultProfileFiltering("bio.doesNotContain=" + UPDATED_BIO, "bio.doesNotContain=" + DEFAULT_BIO);
    }

    @Test
    @Transactional
    void getAllProfilesByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where city equals to
        defaultProfileFiltering("city.equals=" + DEFAULT_CITY, "city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllProfilesByCityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where city in
        defaultProfileFiltering("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY, "city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllProfilesByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where city is not null
        defaultProfileFiltering("city.specified=true", "city.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByCityContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where city contains
        defaultProfileFiltering("city.contains=" + DEFAULT_CITY, "city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllProfilesByCityNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where city does not contain
        defaultProfileFiltering("city.doesNotContain=" + UPDATED_CITY, "city.doesNotContain=" + DEFAULT_CITY);
    }

    @Test
    @Transactional
    void getAllProfilesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where status equals to
        defaultProfileFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllProfilesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where status in
        defaultProfileFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllProfilesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where status is not null
        defaultProfileFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByIsVerifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where isVerified equals to
        defaultProfileFiltering("isVerified.equals=" + DEFAULT_IS_VERIFIED, "isVerified.equals=" + UPDATED_IS_VERIFIED);
    }

    @Test
    @Transactional
    void getAllProfilesByIsVerifiedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where isVerified in
        defaultProfileFiltering("isVerified.in=" + DEFAULT_IS_VERIFIED + "," + UPDATED_IS_VERIFIED, "isVerified.in=" + UPDATED_IS_VERIFIED);
    }

    @Test
    @Transactional
    void getAllProfilesByIsVerifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where isVerified is not null
        defaultProfileFiltering("isVerified.specified=true", "isVerified.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where createdAt equals to
        defaultProfileFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProfilesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where createdAt in
        defaultProfileFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProfilesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where createdAt is not null
        defaultProfileFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where updatedAt equals to
        defaultProfileFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllProfilesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where updatedAt in
        defaultProfileFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllProfilesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where updatedAt is not null
        defaultProfileFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = profile.getUser();
        profileRepository.saveAndFlush(profile);
        Long userId = user.getId();
        // Get all the profileList where user equals to userId
        defaultProfileShouldBeFound("userId.equals=" + userId);

        // Get all the profileList where user equals to (userId + 1)
        defaultProfileShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultProfileFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProfileShouldBeFound(shouldBeFound);
        defaultProfileShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProfileShouldBeFound(String filter) throws Exception {
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profile.getId().intValue())))
            .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME)))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].avatarUrl").value(hasItem(DEFAULT_AVATAR_URL)))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isVerified").value(hasItem(DEFAULT_IS_VERIFIED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProfileShouldNotBeFound(String filter) throws Exception {
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProfile() throws Exception {
        // Get the profile
        restProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProfile() throws Exception {
        // Initialize the database
        profile.getUser().setLogin("user");
        userRepository.saveAndFlush(profile.getUser());
        insertedProfile = profileRepository.saveAndFlush(profile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profile
        Profile updatedProfile = profileRepository.findById(profile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProfile are not directly saved in db
        em.detach(updatedProfile);
        updatedProfile
            .displayName(UPDATED_DISPLAY_NAME)
            .username(UPDATED_USERNAME)
            .avatarUrl(UPDATED_AVATAR_URL)
            .bio(UPDATED_BIO)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS)
            .isVerified(UPDATED_IS_VERIFIED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        ProfileDTO profileDTO = profileMapper.toDto(updatedProfile);

        restProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO))
            )
            .andExpect(status().isOk());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProfileToMatchAllProperties(updatedProfile);
    }

    @Test
    @Transactional
    void putExistingProfileAsOtherUser() throws Exception {
        // Initialize the database with a different owner
        profile.getUser().setLogin("otheruser");
        userRepository.saveAndFlush(profile.getUser());
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Update the profile
        Profile updatedProfile = profileRepository.findById(profile.getId()).orElseThrow();
        em.detach(updatedProfile);
        updatedProfile.displayName(UPDATED_DISPLAY_NAME);
        ProfileDTO profileDTO = profileMapper.toDto(updatedProfile);

        // Authenticated user is "user" while the profile belongs to "otheruser"
        restProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void putNonExistingProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProfileWithPatch() throws Exception {
        // Initialize the database
        profile.getUser().setLogin("user");
        userRepository.saveAndFlush(profile.getUser());
        insertedProfile = profileRepository.saveAndFlush(profile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profile using partial update
        Profile partialUpdatedProfile = new Profile();
        partialUpdatedProfile.setId(profile.getId());

        partialUpdatedProfile.displayName(UPDATED_DISPLAY_NAME).username(UPDATED_USERNAME).bio(UPDATED_BIO).updatedAt(UPDATED_UPDATED_AT);

        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfile))
            )
            .andExpect(status().isOk());

        // Validate the Profile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProfile, profile), getPersistedProfile(profile));
    }

    @Test
    @Transactional
    void fullUpdateProfileWithPatch() throws Exception {
        // Initialize the database
        profile.getUser().setLogin("user");
        userRepository.saveAndFlush(profile.getUser());
        insertedProfile = profileRepository.saveAndFlush(profile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profile using partial update
        Profile partialUpdatedProfile = new Profile();
        partialUpdatedProfile.setId(profile.getId());

        partialUpdatedProfile
            .displayName(UPDATED_DISPLAY_NAME)
            .username(UPDATED_USERNAME)
            .avatarUrl(UPDATED_AVATAR_URL)
            .bio(UPDATED_BIO)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS)
            .isVerified(UPDATED_IS_VERIFIED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfile))
            )
            .andExpect(status().isOk());

        // Validate the Profile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileUpdatableFieldsEquals(partialUpdatedProfile, getPersistedProfile(partialUpdatedProfile));
    }

    @Test
    @Transactional
    void patchNonExistingProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, profileDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(profileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProfile() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the profile
        restProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, profile.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return profileRepository.count();
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

    protected Profile getPersistedProfile(Profile profile) {
        return profileRepository.findById(profile.getId()).orElseThrow();
    }

    protected void assertPersistedProfileToMatchAllProperties(Profile expectedProfile) {
        assertProfileAllPropertiesEquals(expectedProfile, getPersistedProfile(expectedProfile));
    }

    protected void assertPersistedProfileToMatchUpdatableProperties(Profile expectedProfile) {
        assertProfileAllUpdatablePropertiesEquals(expectedProfile, getPersistedProfile(expectedProfile));
    }
}
