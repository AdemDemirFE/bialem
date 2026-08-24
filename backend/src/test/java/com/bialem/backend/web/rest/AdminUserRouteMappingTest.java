package com.bialem.backend.web.rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bialem.backend.repository.UserRepository;
import com.bialem.backend.service.MailService;
import com.bialem.backend.service.ManagementAuthorizationService;
import com.bialem.backend.service.ManagementDashboardService;
import com.bialem.backend.service.UserService;
import com.bialem.backend.service.dto.AdminUserDTO;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@Timeout(60)
class AdminUserRouteMappingTest {

    private UserService userService;
    private MockMvc mockMvc;

    @BeforeEach
    @Timeout(60)
    void setUp() {
        userService = mock(UserService.class);
        ManagementResource managementResource = new ManagementResource(
            mock(ManagementAuthorizationService.class),
            mock(ManagementDashboardService.class),
            userService
        );
        UserResource userResource = new UserResource(userService, mock(UserRepository.class), mock(MailService.class));
        mockMvc = MockMvcBuilders.standaloneSetup(managementResource, userResource).build();
    }

    @Test
    void numericIdHasExactlyOneHandler() throws Exception {
        AdminUserDTO dto = user(2L, "numeric-user");
        when(userService.getManagedUser(2L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/admin/users/2")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void loginLookupUsesDedicatedPath() throws Exception {
        com.bialem.backend.domain.User entity = new com.bialem.backend.domain.User();
        entity.setId(3L);
        entity.setLogin("ademdemirdev");
        entity.setEmail("adem@example.test");
        entity.setAuthorities(new java.util.HashSet<>());
        when(userService.getUserWithAuthoritiesByLogin("ademdemirdev")).thenReturn(Optional.of(entity));

        mockMvc
            .perform(get("/api/admin/users/by-login/ademdemirdev"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.login").value("ademdemirdev"));
    }

    private AdminUserDTO user(Long id, String login) {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(id);
        dto.setLogin(login);
        dto.setEmail(login + "@example.test");
        dto.setActivated(true);
        dto.setAuthorities(Set.of("ROLE_USER"));
        return dto;
    }
}
