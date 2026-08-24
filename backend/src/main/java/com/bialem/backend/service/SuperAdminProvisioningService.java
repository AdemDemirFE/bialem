package com.bialem.backend.service;

import com.bialem.backend.domain.Authority;
import com.bialem.backend.domain.User;
import com.bialem.backend.repository.AuthorityRepository;
import com.bialem.backend.repository.UserRepository;
import com.bialem.backend.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuperAdminProvisioningService {
    public static final String PROTECTED_LOGIN = "ademdemirdev";
    private static final Logger LOG = LoggerFactory.getLogger(SuperAdminProvisioningService.class);
    private final UserRepository users;
    private final AuthorityRepository authorities;

    public SuperAdminProvisioningService(UserRepository users, AuthorityRepository authorities) {
        this.users = users;
        this.authorities = authorities;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void provisionAfterStartup() {
        users.findOneWithAuthoritiesByLogin(PROTECTED_LOGIN).ifPresent(this::ensureRequiredAuthorities);
    }

    public boolean isProtected(User user) {
        return user != null && PROTECTED_LOGIN.equalsIgnoreCase(user.getLogin());
    }

    @Transactional
    public void ensureRequiredAuthorities(User user) {
        if (!isProtected(user)) return;
        Authority authority = authorities.findById(AuthoritiesConstants.SUPER_ADMIN)
            .orElseGet(() -> authorities.save(new Authority().name(AuthoritiesConstants.SUPER_ADMIN)));
        boolean changed = user.getAuthorities().size() != 1 || !user.getAuthorities().contains(authority);
        if (changed) {
            user.getAuthorities().clear();
            user.getAuthorities().add(authority);
            users.save(user);
            LOG.info("Restored protected single platform authority for user {}", PROTECTED_LOGIN);
        }
    }
}
