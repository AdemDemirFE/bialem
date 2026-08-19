package com.bialem.backend.service;

import com.bialem.backend.domain.AccountPreferences;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.domain.enumeration.AllowMessagesFrom;
import com.bialem.backend.domain.enumeration.ProfileStatus;
import com.bialem.backend.repository.AccountPreferencesRepository;
import com.bialem.backend.repository.ProfileRepository;
import java.time.Instant;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileProvisioningService {

    private final ProfileRepository profileRepository;
    private final AccountPreferencesRepository accountPreferencesRepository;

    public ProfileProvisioningService(
        ProfileRepository profileRepository,
        AccountPreferencesRepository accountPreferencesRepository
    ) {
        this.profileRepository = profileRepository;
        this.accountPreferencesRepository = accountPreferencesRepository;
    }

    @Transactional
    public Profile createForUser(User user, String displayName, String username) {
        return profileRepository.findOneByUser_Id(user.getId()).orElseGet(() -> persist(user, displayName, username));
    }

    @Transactional
    public void deleteForUser(User user) {
        profileRepository
            .findOneByUser_Id(user.getId())
            .ifPresent(profile -> {
                accountPreferencesRepository.findOneByProfile_Id(profile.getId()).ifPresent(accountPreferencesRepository::delete);
                profileRepository.delete(profile);
            });
    }

    private Profile persist(User user, String displayName, String username) {
        Instant now = Instant.now();
        Profile profile = new Profile();
        profile.setDisplayName(displayName == null || displayName.isBlank() ? user.getLogin() : displayName);
        profile.setUsername(uniqueUsername(username == null || username.isBlank() ? user.getLogin() : username));
        profile.setStatus(ProfileStatus.ACTIVE);
        profile.setIsVerified(false);
        profile.setCreatedAt(now);
        profile.setUpdatedAt(now);
        profile.setUser(user);
        profile = profileRepository.save(profile);

        AccountPreferences preferences = new AccountPreferences();
        preferences.setDiscoverable(true);
        preferences.setShowCity(true);
        preferences.setShowFollowConnections(true);
        preferences.setAllowFollows(true);
        preferences.setRequireFollowApproval(false);
        preferences.setAllowMessagesFrom(AllowMessagesFrom.EVERYONE);
        preferences.setNotifyEvents(true);
        preferences.setNotifyCommunities(true);
        preferences.setNotifySocial(true);
        preferences.setNotifyAdvantages(true);
        preferences.setNotifySystem(true);
        preferences.setUpdatedAt(now);
        preferences.setProfile(profile);
        accountPreferencesRepository.save(preferences);
        return profile;
    }

    private String uniqueUsername(String raw) {
        String base = raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "");
        if (base.length() < 3) {
            base = "user";
        }
        String candidate = base;
        int suffix = 1;
        while (profileRepository.existsByUsernameIgnoreCase(candidate)) {
            candidate = base + suffix++;
        }
        return candidate;
    }
}
