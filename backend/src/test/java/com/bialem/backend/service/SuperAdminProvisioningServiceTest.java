package com.bialem.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.bialem.backend.domain.Authority;
import com.bialem.backend.domain.User;
import com.bialem.backend.repository.AuthorityRepository;
import com.bialem.backend.repository.UserRepository;
import com.bialem.backend.security.AuthoritiesConstants;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SuperAdminProvisioningServiceTest {
    @Test
    void protectedUserIsNormalizedToSuperAdminOnly() {
        UserRepository users = mock(UserRepository.class);
        AuthorityRepository authorities = mock(AuthorityRepository.class);
        Authority userRole = new Authority().name(AuthoritiesConstants.USER);
        Authority adminRole = new Authority().name(AuthoritiesConstants.ADMIN);
        Authority superAdminRole = new Authority().name(AuthoritiesConstants.SUPER_ADMIN);
        User user = new User();
        user.setLogin(SuperAdminProvisioningService.PROTECTED_LOGIN);
        user.setAuthorities(new HashSet<>(java.util.Set.of(userRole, adminRole, superAdminRole)));
        when(authorities.findById(AuthoritiesConstants.SUPER_ADMIN)).thenReturn(Optional.of(superAdminRole));

        new SuperAdminProvisioningService(users, authorities).ensureRequiredAuthorities(user);

        assertThat(user.getAuthorities()).containsExactly(superAdminRole);
        verify(users).save(user);
    }
}
