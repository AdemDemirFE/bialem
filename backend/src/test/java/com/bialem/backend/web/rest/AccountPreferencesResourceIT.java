package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.AccountPreferencesAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.AccountPreferences;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.enumeration.AllowMessagesFrom;
import com.bialem.backend.repository.AccountPreferencesRepository;
import com.bialem.backend.service.dto.AccountPreferencesDTO;
import com.bialem.backend.service.mapper.AccountPreferencesMapper;
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
 * Integration tests for the {@link AccountPreferencesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AccountPreferencesResourceIT {

    private static final Boolean DEFAULT_DISCOVERABLE = false;
    private static final Boolean UPDATED_DISCOVERABLE = true;

    private static final Boolean DEFAULT_SHOW_CITY = false;
    private static final Boolean UPDATED_SHOW_CITY = true;

    private static final Boolean DEFAULT_SHOW_FOLLOW_CONNECTIONS = false;
    private static final Boolean UPDATED_SHOW_FOLLOW_CONNECTIONS = true;

    private static final Boolean DEFAULT_ALLOW_FOLLOWS = false;
    private static final Boolean UPDATED_ALLOW_FOLLOWS = true;

    private static final Boolean DEFAULT_REQUIRE_FOLLOW_APPROVAL = false;
    private static final Boolean UPDATED_REQUIRE_FOLLOW_APPROVAL = true;

    private static final AllowMessagesFrom DEFAULT_ALLOW_MESSAGES_FROM = AllowMessagesFrom.EVERYONE;
    private static final AllowMessagesFrom UPDATED_ALLOW_MESSAGES_FROM = AllowMessagesFrom.FOLLOWING;

    private static final Boolean DEFAULT_NOTIFY_EVENTS = false;
    private static final Boolean UPDATED_NOTIFY_EVENTS = true;

    private static final Boolean DEFAULT_NOTIFY_COMMUNITIES = false;
    private static final Boolean UPDATED_NOTIFY_COMMUNITIES = true;

    private static final Boolean DEFAULT_NOTIFY_SOCIAL = false;
    private static final Boolean UPDATED_NOTIFY_SOCIAL = true;

    private static final Boolean DEFAULT_NOTIFY_ADVANTAGES = false;
    private static final Boolean UPDATED_NOTIFY_ADVANTAGES = true;

    private static final Boolean DEFAULT_NOTIFY_SYSTEM = false;
    private static final Boolean UPDATED_NOTIFY_SYSTEM = true;

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/account-preferences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AccountPreferencesRepository accountPreferencesRepository;

    @Autowired
    private AccountPreferencesMapper accountPreferencesMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAccountPreferencesMockMvc;

    private AccountPreferences accountPreferences;

    private AccountPreferences insertedAccountPreferences;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountPreferences createEntity(EntityManager em) {
        AccountPreferences accountPreferences = new AccountPreferences()
            .discoverable(DEFAULT_DISCOVERABLE)
            .showCity(DEFAULT_SHOW_CITY)
            .showFollowConnections(DEFAULT_SHOW_FOLLOW_CONNECTIONS)
            .allowFollows(DEFAULT_ALLOW_FOLLOWS)
            .requireFollowApproval(DEFAULT_REQUIRE_FOLLOW_APPROVAL)
            .allowMessagesFrom(DEFAULT_ALLOW_MESSAGES_FROM)
            .notifyEvents(DEFAULT_NOTIFY_EVENTS)
            .notifyCommunities(DEFAULT_NOTIFY_COMMUNITIES)
            .notifySocial(DEFAULT_NOTIFY_SOCIAL)
            .notifyAdvantages(DEFAULT_NOTIFY_ADVANTAGES)
            .notifySystem(DEFAULT_NOTIFY_SYSTEM)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        Profile profile;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            profile = ProfileResourceIT.createEntity(em);
            em.persist(profile);
            em.flush();
        } else {
            profile = TestUtil.findAll(em, Profile.class).get(0);
        }
        accountPreferences.setProfile(profile);
        return accountPreferences;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountPreferences createUpdatedEntity(EntityManager em) {
        AccountPreferences updatedAccountPreferences = new AccountPreferences()
            .discoverable(UPDATED_DISCOVERABLE)
            .showCity(UPDATED_SHOW_CITY)
            .showFollowConnections(UPDATED_SHOW_FOLLOW_CONNECTIONS)
            .allowFollows(UPDATED_ALLOW_FOLLOWS)
            .requireFollowApproval(UPDATED_REQUIRE_FOLLOW_APPROVAL)
            .allowMessagesFrom(UPDATED_ALLOW_MESSAGES_FROM)
            .notifyEvents(UPDATED_NOTIFY_EVENTS)
            .notifyCommunities(UPDATED_NOTIFY_COMMUNITIES)
            .notifySocial(UPDATED_NOTIFY_SOCIAL)
            .notifyAdvantages(UPDATED_NOTIFY_ADVANTAGES)
            .notifySystem(UPDATED_NOTIFY_SYSTEM)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        Profile profile;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            profile = ProfileResourceIT.createUpdatedEntity(em);
            em.persist(profile);
            em.flush();
        } else {
            profile = TestUtil.findAll(em, Profile.class).get(0);
        }
        updatedAccountPreferences.setProfile(profile);
        return updatedAccountPreferences;
    }

    @BeforeEach
    void initTest() {
        accountPreferences = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAccountPreferences != null) {
            accountPreferencesRepository.delete(insertedAccountPreferences);
            insertedAccountPreferences = null;
        }
    }

    @Test
    @Transactional
    void createAccountPreferences() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AccountPreferences
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);
        var returnedAccountPreferencesDTO = om.readValue(
            restAccountPreferencesMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AccountPreferencesDTO.class
        );

        // Validate the AccountPreferences in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAccountPreferences = accountPreferencesMapper.toEntity(returnedAccountPreferencesDTO);
        assertAccountPreferencesUpdatableFieldsEquals(
            returnedAccountPreferences,
            getPersistedAccountPreferences(returnedAccountPreferences)
        );

        insertedAccountPreferences = returnedAccountPreferences;
    }

    @Test
    @Transactional
    void createAccountPreferencesWithExistingId() throws Exception {
        // Create the AccountPreferences with an existing ID
        accountPreferences.setId(1L);
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAccountPreferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AccountPreferences in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDiscoverableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accountPreferences.setDiscoverable(null);

        // Create the AccountPreferences, which fails.
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        restAccountPreferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkShowCityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accountPreferences.setShowCity(null);

        // Create the AccountPreferences, which fails.
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        restAccountPreferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkShowFollowConnectionsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accountPreferences.setShowFollowConnections(null);

        // Create the AccountPreferences, which fails.
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        restAccountPreferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAllowFollowsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accountPreferences.setAllowFollows(null);

        // Create the AccountPreferences, which fails.
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        restAccountPreferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRequireFollowApprovalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accountPreferences.setRequireFollowApproval(null);

        // Create the AccountPreferences, which fails.
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        restAccountPreferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAllowMessagesFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accountPreferences.setAllowMessagesFrom(null);

        // Create the AccountPreferences, which fails.
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        restAccountPreferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNotifyEventsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accountPreferences.setNotifyEvents(null);

        // Create the AccountPreferences, which fails.
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        restAccountPreferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNotifyCommunitiesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accountPreferences.setNotifyCommunities(null);

        // Create the AccountPreferences, which fails.
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        restAccountPreferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNotifySocialIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accountPreferences.setNotifySocial(null);

        // Create the AccountPreferences, which fails.
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        restAccountPreferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNotifyAdvantagesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accountPreferences.setNotifyAdvantages(null);

        // Create the AccountPreferences, which fails.
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        restAccountPreferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNotifySystemIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accountPreferences.setNotifySystem(null);

        // Create the AccountPreferences, which fails.
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        restAccountPreferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        accountPreferences.setUpdatedAt(null);

        // Create the AccountPreferences, which fails.
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        restAccountPreferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAccountPreferences() throws Exception {
        // Initialize the database
        insertedAccountPreferences = accountPreferencesRepository.saveAndFlush(accountPreferences);

        // Get all the accountPreferencesList
        restAccountPreferencesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accountPreferences.getId().intValue())))
            .andExpect(jsonPath("$.[*].discoverable").value(hasItem(DEFAULT_DISCOVERABLE)))
            .andExpect(jsonPath("$.[*].showCity").value(hasItem(DEFAULT_SHOW_CITY)))
            .andExpect(jsonPath("$.[*].showFollowConnections").value(hasItem(DEFAULT_SHOW_FOLLOW_CONNECTIONS)))
            .andExpect(jsonPath("$.[*].allowFollows").value(hasItem(DEFAULT_ALLOW_FOLLOWS)))
            .andExpect(jsonPath("$.[*].requireFollowApproval").value(hasItem(DEFAULT_REQUIRE_FOLLOW_APPROVAL)))
            .andExpect(jsonPath("$.[*].allowMessagesFrom").value(hasItem(DEFAULT_ALLOW_MESSAGES_FROM.toString())))
            .andExpect(jsonPath("$.[*].notifyEvents").value(hasItem(DEFAULT_NOTIFY_EVENTS)))
            .andExpect(jsonPath("$.[*].notifyCommunities").value(hasItem(DEFAULT_NOTIFY_COMMUNITIES)))
            .andExpect(jsonPath("$.[*].notifySocial").value(hasItem(DEFAULT_NOTIFY_SOCIAL)))
            .andExpect(jsonPath("$.[*].notifyAdvantages").value(hasItem(DEFAULT_NOTIFY_ADVANTAGES)))
            .andExpect(jsonPath("$.[*].notifySystem").value(hasItem(DEFAULT_NOTIFY_SYSTEM)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getAccountPreferences() throws Exception {
        // Initialize the database
        insertedAccountPreferences = accountPreferencesRepository.saveAndFlush(accountPreferences);

        // Get the accountPreferences
        restAccountPreferencesMockMvc
            .perform(get(ENTITY_API_URL_ID, accountPreferences.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(accountPreferences.getId().intValue()))
            .andExpect(jsonPath("$.discoverable").value(DEFAULT_DISCOVERABLE))
            .andExpect(jsonPath("$.showCity").value(DEFAULT_SHOW_CITY))
            .andExpect(jsonPath("$.showFollowConnections").value(DEFAULT_SHOW_FOLLOW_CONNECTIONS))
            .andExpect(jsonPath("$.allowFollows").value(DEFAULT_ALLOW_FOLLOWS))
            .andExpect(jsonPath("$.requireFollowApproval").value(DEFAULT_REQUIRE_FOLLOW_APPROVAL))
            .andExpect(jsonPath("$.allowMessagesFrom").value(DEFAULT_ALLOW_MESSAGES_FROM.toString()))
            .andExpect(jsonPath("$.notifyEvents").value(DEFAULT_NOTIFY_EVENTS))
            .andExpect(jsonPath("$.notifyCommunities").value(DEFAULT_NOTIFY_COMMUNITIES))
            .andExpect(jsonPath("$.notifySocial").value(DEFAULT_NOTIFY_SOCIAL))
            .andExpect(jsonPath("$.notifyAdvantages").value(DEFAULT_NOTIFY_ADVANTAGES))
            .andExpect(jsonPath("$.notifySystem").value(DEFAULT_NOTIFY_SYSTEM))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAccountPreferences() throws Exception {
        // Get the accountPreferences
        restAccountPreferencesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAccountPreferences() throws Exception {
        // Initialize the database
        insertedAccountPreferences = accountPreferencesRepository.saveAndFlush(accountPreferences);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the accountPreferences
        AccountPreferences updatedAccountPreferences = accountPreferencesRepository.findById(accountPreferences.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAccountPreferences are not directly saved in db
        em.detach(updatedAccountPreferences);
        updatedAccountPreferences
            .discoverable(UPDATED_DISCOVERABLE)
            .showCity(UPDATED_SHOW_CITY)
            .showFollowConnections(UPDATED_SHOW_FOLLOW_CONNECTIONS)
            .allowFollows(UPDATED_ALLOW_FOLLOWS)
            .requireFollowApproval(UPDATED_REQUIRE_FOLLOW_APPROVAL)
            .allowMessagesFrom(UPDATED_ALLOW_MESSAGES_FROM)
            .notifyEvents(UPDATED_NOTIFY_EVENTS)
            .notifyCommunities(UPDATED_NOTIFY_COMMUNITIES)
            .notifySocial(UPDATED_NOTIFY_SOCIAL)
            .notifyAdvantages(UPDATED_NOTIFY_ADVANTAGES)
            .notifySystem(UPDATED_NOTIFY_SYSTEM)
            .updatedAt(UPDATED_UPDATED_AT);
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(updatedAccountPreferences);

        restAccountPreferencesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accountPreferencesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(accountPreferencesDTO))
            )
            .andExpect(status().isOk());

        // Validate the AccountPreferences in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAccountPreferencesToMatchAllProperties(updatedAccountPreferences);
    }

    @Test
    @Transactional
    void putNonExistingAccountPreferences() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accountPreferences.setId(longCount.incrementAndGet());

        // Create the AccountPreferences
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountPreferencesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accountPreferencesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(accountPreferencesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountPreferences in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAccountPreferences() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accountPreferences.setId(longCount.incrementAndGet());

        // Create the AccountPreferences
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountPreferencesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(accountPreferencesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountPreferences in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAccountPreferences() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accountPreferences.setId(longCount.incrementAndGet());

        // Create the AccountPreferences
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountPreferencesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountPreferences in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAccountPreferencesWithPatch() throws Exception {
        // Initialize the database
        insertedAccountPreferences = accountPreferencesRepository.saveAndFlush(accountPreferences);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the accountPreferences using partial update
        AccountPreferences partialUpdatedAccountPreferences = new AccountPreferences();
        partialUpdatedAccountPreferences.setId(accountPreferences.getId());

        partialUpdatedAccountPreferences.notifyEvents(UPDATED_NOTIFY_EVENTS).notifyAdvantages(UPDATED_NOTIFY_ADVANTAGES);

        restAccountPreferencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountPreferences.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAccountPreferences))
            )
            .andExpect(status().isOk());

        // Validate the AccountPreferences in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAccountPreferencesUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAccountPreferences, accountPreferences),
            getPersistedAccountPreferences(accountPreferences)
        );
    }

    @Test
    @Transactional
    void fullUpdateAccountPreferencesWithPatch() throws Exception {
        // Initialize the database
        insertedAccountPreferences = accountPreferencesRepository.saveAndFlush(accountPreferences);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the accountPreferences using partial update
        AccountPreferences partialUpdatedAccountPreferences = new AccountPreferences();
        partialUpdatedAccountPreferences.setId(accountPreferences.getId());

        partialUpdatedAccountPreferences
            .discoverable(UPDATED_DISCOVERABLE)
            .showCity(UPDATED_SHOW_CITY)
            .showFollowConnections(UPDATED_SHOW_FOLLOW_CONNECTIONS)
            .allowFollows(UPDATED_ALLOW_FOLLOWS)
            .requireFollowApproval(UPDATED_REQUIRE_FOLLOW_APPROVAL)
            .allowMessagesFrom(UPDATED_ALLOW_MESSAGES_FROM)
            .notifyEvents(UPDATED_NOTIFY_EVENTS)
            .notifyCommunities(UPDATED_NOTIFY_COMMUNITIES)
            .notifySocial(UPDATED_NOTIFY_SOCIAL)
            .notifyAdvantages(UPDATED_NOTIFY_ADVANTAGES)
            .notifySystem(UPDATED_NOTIFY_SYSTEM)
            .updatedAt(UPDATED_UPDATED_AT);

        restAccountPreferencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountPreferences.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAccountPreferences))
            )
            .andExpect(status().isOk());

        // Validate the AccountPreferences in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAccountPreferencesUpdatableFieldsEquals(
            partialUpdatedAccountPreferences,
            getPersistedAccountPreferences(partialUpdatedAccountPreferences)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAccountPreferences() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accountPreferences.setId(longCount.incrementAndGet());

        // Create the AccountPreferences
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountPreferencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, accountPreferencesDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(accountPreferencesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountPreferences in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAccountPreferences() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accountPreferences.setId(longCount.incrementAndGet());

        // Create the AccountPreferences
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountPreferencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(accountPreferencesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountPreferences in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAccountPreferences() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accountPreferences.setId(longCount.incrementAndGet());

        // Create the AccountPreferences
        AccountPreferencesDTO accountPreferencesDTO = accountPreferencesMapper.toDto(accountPreferences);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountPreferencesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(accountPreferencesDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountPreferences in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAccountPreferences() throws Exception {
        // Initialize the database
        insertedAccountPreferences = accountPreferencesRepository.saveAndFlush(accountPreferences);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the accountPreferences
        restAccountPreferencesMockMvc
            .perform(delete(ENTITY_API_URL_ID, accountPreferences.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return accountPreferencesRepository.count();
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

    protected AccountPreferences getPersistedAccountPreferences(AccountPreferences accountPreferences) {
        return accountPreferencesRepository.findById(accountPreferences.getId()).orElseThrow();
    }

    protected void assertPersistedAccountPreferencesToMatchAllProperties(AccountPreferences expectedAccountPreferences) {
        assertAccountPreferencesAllPropertiesEquals(expectedAccountPreferences, getPersistedAccountPreferences(expectedAccountPreferences));
    }

    protected void assertPersistedAccountPreferencesToMatchUpdatableProperties(AccountPreferences expectedAccountPreferences) {
        assertAccountPreferencesAllUpdatablePropertiesEquals(
            expectedAccountPreferences,
            getPersistedAccountPreferences(expectedAccountPreferences)
        );
    }
}
