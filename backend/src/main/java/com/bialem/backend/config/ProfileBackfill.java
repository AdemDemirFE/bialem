package com.bialem.backend.config;

import com.bialem.backend.repository.UserRepository;
import com.bialem.backend.service.ProfileProvisioningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProfileBackfill {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileBackfill.class);

    private final UserRepository userRepository;
    private final ProfileProvisioningService profileProvisioningService;

    public ProfileBackfill(UserRepository userRepository, ProfileProvisioningService profileProvisioningService) {
        this.userRepository = userRepository;
        this.profileProvisioningService = profileProvisioningService;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void backfillMissingProfiles() {
        userRepository
            .findAll()
            .forEach(user -> {
                profileProvisioningService.createForUser(user, user.getFirstName(), user.getLogin());
                LOG.debug("Ensured profile for {}", user.getLogin());
            });
    }
}
