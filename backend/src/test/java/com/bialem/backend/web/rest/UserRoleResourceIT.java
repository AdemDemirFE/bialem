package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.UserRoleAsserts.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.UserRole;
import com.bialem.backend.repository.UserRoleRepository;
import com.bialem.backend.service.dto.UserRoleDTO;
import com.bialem.backend.service.mapper.UserRoleMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
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
class UserRoleResourceIT {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);

    private static final String ENTITY_API_URL = "/api/user-roles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private MockMvc restUserRoleMockMvc;

    private UserRole userRole;

    private UserRole insertedUserRole;

    public static UserRole createEntity() {
        return new UserRole().createdAt(DEFAULT_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        userRole = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserRole != null) {
            userRoleRepository.delete(insertedUserRole);
            insertedUserRole = null;
        }
    }

    @Test
    @Transactional
    void createUserRole() throws Exception {
        long databaseSizeBeforeCreate = userRoleRepository.count();
        UserRoleDTO userRoleDTO = userRoleMapper.toDto(userRole);
        var returnedUserRoleDTO = om.readValue(
            restUserRoleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userRoleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserRoleDTO.class
        );

        assertThat(userRoleRepository.count()).isEqualTo(databaseSizeBeforeCreate + 1);
        var returnedUserRole = userRoleMapper.toEntity(returnedUserRoleDTO);
        assertUserRoleUpdatableFieldsEquals(returnedUserRole, userRoleRepository.findById(returnedUserRole.getId()).orElseThrow());
        insertedUserRole = returnedUserRole;
    }

    @Test
    @Transactional
    void getAllUserRoles() throws Exception {
        insertedUserRole = userRoleRepository.saveAndFlush(userRole);
        restUserRoleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[*].id").value(hasItem(userRole.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getUserRole() throws Exception {
        insertedUserRole = userRoleRepository.saveAndFlush(userRole);
        restUserRoleMockMvc
            .perform(get(ENTITY_API_URL_ID, userRole.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userRole.getId().intValue()));
    }

    @Test
    @Transactional
    void deleteUserRole() throws Exception {
        insertedUserRole = userRoleRepository.saveAndFlush(userRole);
        long databaseSizeBeforeDelete = userRoleRepository.count();
        restUserRoleMockMvc
            .perform(delete(ENTITY_API_URL_ID, userRole.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        assertThat(userRoleRepository.count()).isEqualTo(databaseSizeBeforeDelete - 1);
        insertedUserRole = null;
    }
}
