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

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.PushDeviceToken}.
 */
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

        Optional<PushDeviceToken> existing = pushDeviceTokenRepository.findByToken(request.getToken());
        PushDeviceToken pushDeviceToken = existing.orElseGet(PushDeviceToken::new);
        if (pushDeviceToken.getCreatedAt() == null) {
            pushDeviceToken.setCreatedAt(now);
        }
        pushDeviceToken.setToken(request.getToken());
        pushDeviceToken.setPlatform(platform);
        pushDeviceToken.setUser(user);
        pushDeviceToken.setUpdatedAt(now);

        return pushDeviceTokenMapper.toDto(pushDeviceTokenRepository.save(pushDeviceToken));
    }
}
