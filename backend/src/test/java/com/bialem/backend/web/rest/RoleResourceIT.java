package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.RoleAsserts.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.Role;
import com.bialem.backend.repository.RoleRepository;
import com.bialem.backend.service.dto.RoleDTO;
import com.bialem.backend.service.mapper.RoleMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RoleResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/roles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MockMvc restRoleMockMvc;

    private Role role;

    private Role insertedRole;

    public static Role createEntity() {
        return new Role().code(DEFAULT_CODE).name(DEFAULT_NAME).createdAt(DEFAULT_CREATED_AT);
    }

    public static Role createUpdatedEntity() {
        return new Role().code(UPDATED_CODE).name(UPDATED_NAME).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        role = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRole != null) {
            roleRepository.delete(insertedRole);
            insertedRole = null;
        }
    }

    @Test
    @Transactional
    void createRole() throws Exception {
        long databaseSizeBeforeCreate = roleRepository.count();
        RoleDTO roleDTO = roleMapper.toDto(role);
        var returnedRoleDTO = om.readValue(
            restRoleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(roleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RoleDTO.class
        );

        assertThat(roleRepository.count()).isEqualTo(databaseSizeBeforeCreate + 1);
        var returnedRole = roleMapper.toEntity(returnedRoleDTO);
        assertRoleUpdatableFieldsEquals(returnedRole, roleRepository.findById(returnedRole.getId()).orElseThrow());
        insertedRole = returnedRole;
    }

    @Test
    @Transactional
    void createRoleWithExistingId() throws Exception {
        role.setId(1L);
        RoleDTO roleDTO = roleMapper.toDto(role);
        long databaseSizeBeforeCreate = roleRepository.count();
        restRoleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(roleDTO)))
            .andExpect(status().isBadRequest());
        assertThat(roleRepository.count()).isEqualTo(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = roleRepository.count();
        role.setCode(null);
        RoleDTO roleDTO = roleMapper.toDto(role);
        restRoleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(roleDTO)))
            .andExpect(status().isBadRequest());
        assertThat(roleRepository.count()).isEqualTo(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRoles() throws Exception {
        insertedRole = roleRepository.saveAndFlush(role);
        restRoleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(role.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getRole() throws Exception {
        insertedRole = roleRepository.saveAndFlush(role);
        restRoleMockMvc
            .perform(get(ENTITY_API_URL_ID, role.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(role.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingRole() throws Exception {
        restRoleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deleteRole() throws Exception {
        insertedRole = roleRepository.saveAndFlush(role);
        long databaseSizeBeforeDelete = roleRepository.count();
        restRoleMockMvc.perform(delete(ENTITY_API_URL_ID, role.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
        assertThat(roleRepository.count()).isEqualTo(databaseSizeBeforeDelete - 1);
        insertedRole = null;
    }
}
