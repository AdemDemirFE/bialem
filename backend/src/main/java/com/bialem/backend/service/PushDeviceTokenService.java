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

    public void deactivateCurrentUserDevice(String token, String deviceUuid, String installationId) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();
        User user = userRepository.findOneByLogin(login).orElseThrow();
        if (isBlank(token) && isBlank(deviceUuid) && isBlank(installationId)) {
            LOG.warn("Push device deactivation ignored because no device identifier was supplied for user {}", user.getId());
            return;
        }
        pushDeviceTokenRepository.deactivateCurrentDevice(user.getId(), blankToNull(token), blankToNull(deviceUuid),
            blankToNull(installationId), Instant.now());
    }

    private Optional<PushDeviceToken> findExistingDevice(PushDeviceTokenRequest request) {
        if (request.getFirebaseInstallationId() != null && !request.getFirebaseInstallationId().isBlank()) {
            return pushDeviceTokenRepository.findByFirebaseInstallationId(request.getFirebaseInstallationId());
        }
        if (request.getDeviceUuid() != null && !request.getDeviceUuid().isBlank()) {
            return pushDeviceTokenRepository.findByDeviceUuid(request.getDeviceUuid());
        }
        return pushDeviceTokenRepository.findByToken(request.getToken());
    }

    private boolean isBlank(String value) { return value == null || value.isBlank(); }
    private String blankToNull(String value) { return isBlank(value) ? null : value; }
}
