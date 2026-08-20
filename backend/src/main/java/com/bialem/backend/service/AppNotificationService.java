package com.bialem.backend.service;

import com.bialem.backend.domain.AppNotification;
import com.bialem.backend.domain.PushDeviceToken;
import com.bialem.backend.domain.User;
import com.bialem.backend.repository.AppNotificationRepository;
import com.bialem.backend.repository.PushDeviceTokenRepository;
import com.bialem.backend.repository.UserRepository;
import com.bialem.backend.security.SecurityUtils;
import com.bialem.backend.service.dto.AppNotificationDTO;
import com.bialem.backend.service.dto.UnreadCountDTO;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.AppNotification}.
 */
@Service
@Transactional
public class AppNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(AppNotificationService.class);

    private final AppNotificationRepository appNotificationRepository;

    private final PushDeviceTokenRepository pushDeviceTokenRepository;

    private final FirebasePushService firebasePushService;

    private final UserRepository userRepository;

    public AppNotificationService(
        AppNotificationRepository appNotificationRepository,
        PushDeviceTokenRepository pushDeviceTokenRepository,
        FirebasePushService firebasePushService,
        UserRepository userRepository
    ) {
        this.appNotificationRepository = appNotificationRepository;
        this.pushDeviceTokenRepository = pushDeviceTokenRepository;
        this.firebasePushService = firebasePushService;
        this.userRepository = userRepository;
    }

    public AppNotificationDTO sendToUser(
        Long userId,
        String title,
        String body,
        String type,
        String referenceId,
        String route
    ) {
        User user = resolveUser(userId);
        Instant now = Instant.now();

        AppNotification notification = new AppNotification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setNotificationType(type != null ? type : "GENERIC");
        notification.setReferenceId(referenceId);
        notification.setRoute(route);
        notification.setIsRead(false);
        notification.setCreatedAt(now);
        notification = appNotificationRepository.saveAndFlush(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", notification.getNotificationType());
        data.put("referenceId", referenceId != null ? referenceId : "");
        data.put("route", route != null ? route : "");
        data.put("notificationId", String.valueOf(notification.getId()));

        List<PushDeviceToken> tokens = pushDeviceTokenRepository.findByUser_Id(userId);
        for (PushDeviceToken token : tokens) {
            firebasePushService.sendToToken(token.getToken(), title, body, data);
        }

        return toDto(notification);
    }

    @Transactional(readOnly = true)
    public List<AppNotificationDTO> listCurrentUser() {
        Long userId = currentUserId();
        return appNotificationRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UnreadCountDTO unreadCountCurrentUser() {
        Long userId = currentUserId();
        return new UnreadCountDTO(appNotificationRepository.countByUser_IdAndIsReadFalse(userId));
    }

    public AppNotificationDTO markCurrentUserRead(Long id) {
        Long userId = currentUserId();
        AppNotification notification = appNotificationRepository
            .findByIdAndUser_Id(id, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!Boolean.TRUE.equals(notification.getIsRead())) {
            notification.setIsRead(true);
            notification.setReadAt(Instant.now());
            notification = appNotificationRepository.save(notification);
        }
        return toDto(notification);
    }

    public void markAllCurrentUserRead() {
        Long userId = currentUserId();
        appNotificationRepository.markAllReadForUser(userId, Instant.now());
    }

    public AppNotificationDTO sendTestToCurrentUser() {
        Long userId = currentUserId();
        return sendToUser(
            userId,
            "Bialem Test Bildirimi \uD83D\uDD14",
            "Push notification sistemi başarıyla çalışıyor.",
            "TEST",
            "0",
            "/"
        );
    }

    private User resolveUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
    }

    private Long currentUserId() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"))
            .getId();
    }

    private AppNotificationDTO toDto(AppNotification notification) {
        AppNotificationDTO dto = new AppNotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setBody(notification.getBody());
        dto.setNotificationType(notification.getNotificationType());
        dto.setReferenceId(notification.getReferenceId());
        dto.setRoute(notification.getRoute());
        dto.setRead(Boolean.TRUE.equals(notification.getIsRead()));
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
