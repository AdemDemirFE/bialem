package com.bialem.backend.service;

import com.bialem.backend.domain.PushDeviceToken;
import com.bialem.backend.domain.User;
import com.bialem.backend.domain.enumeration.PushPlatform;
import com.bialem.backend.repository.PushDeviceTokenRepository;
import com.bialem.backend.repository.UserRepository;
import com.bialem.backend.security.SecurityUtils;
import com.bialem.backend.service.dto.PushDeviceTokenDTO;
import com.bialem.backend.service.dto.PushDeviceTokenRequest;
import com.bialem.backend.service.mapper.PushDeviceTokenMapper;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PushDeviceTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(PushDeviceTokenService.class);

    private final PushDeviceTokenRepository pushDeviceTokenRepository;

    private final PushDeviceTokenMapper pushDeviceTokenMapper;

    private final UserRepository userRepository;

    public PushDeviceTokenService(
        PushDeviceTokenRepository pushDeviceTokenRepository,
        PushDeviceTokenMapper pushDeviceTokenMapper,
        UserRepository userRepository
    ) {
        this.pushDeviceTokenRepository = pushDeviceTokenRepository;
        this.pushDeviceTokenMapper = pushDeviceTokenMapper;
        this.userRepository = userRepository;
    }

    public PushDeviceTokenDTO registerCurrentUser(PushDeviceTokenRequest request) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();
        User user = userRepository.findOneByLogin(login).orElseThrow();

        Instant now = Instant.now();
        PushPlatform platform = request.getPlatform() != null ? request.getPlatform() : PushPlatform.ANDROID;

        Optional<PushDeviceToken> existing = findExistingDevice(request);
        PushDeviceToken pushDeviceToken = existing.orElseGet(PushDeviceToken::new);
        if (pushDeviceToken.getCreatedAt() == null) {
            pushDeviceToken.setCreatedAt(now);
        }
        pushDeviceToken.setToken(request.getToken());
        pushDeviceToken.setPlatform(platform);
        pushDeviceToken.setUser(user);
        pushDeviceToken.setUpdatedAt(now);
        pushDeviceToken.setActive(true);
        pushDeviceToken.setLastSeenAt(now);
        if (request.getFirebaseInstallationId() != null) {
            pushDeviceToken.setFirebaseInstallationId(request.getFirebaseInstallationId());
        }
        if (request.getDeviceUuid() != null) {
            pushDeviceToken.setDeviceUuid(request.getDeviceUuid());
        }
        if (request.getAppVersion() != null) {
            pushDeviceToken.setAppVersion(request.getAppVersion());
        }
        if (request.getNotificationsEnabled() != null) {
            pushDeviceToken.setNotificationsEnabled(request.getNotificationsEnabled());
        }

        return pushDeviceTokenMapper.toDto(pushDeviceTokenRepository.save(pushDeviceToken));
    }

    public void deactivateCurrentUserDevice() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();
        User user = userRepository.findOneByLogin(login).orElseThrow();
        pushDeviceTokenRepository
            .findByUser_Id(user.getId())
            .forEach(device -> {
                device.setActive(false);
                device.setUpdatedAt(Instant.now());
                pushDeviceTokenRepository.save(device);
            });
    }

    private Optional<PushDeviceToken> findExistingDevice(PushDeviceTokenRequest request) {
        if (request.getFirebaseInstallationId() != null && !request.getFirebaseInstallationId().isBlank()) {
            return pushDeviceTokenRepository
                .findAll()
                .stream()
                .filter(d -> request.getFirebaseInstallationId().equals(d.getFirebaseInstallationId()))
                .findFirst();
        }
        if (request.getDeviceUuid() != null && !request.getDeviceUuid().isBlank()) {
            return pushDeviceTokenRepository
                .findAll()
                .stream()
                .filter(d -> request.getDeviceUuid().equals(d.getDeviceUuid()))
                .findFirst();
        }
        return pushDeviceTokenRepository.findByToken(request.getToken());
    }
}
