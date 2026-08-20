package com.bialem.backend.service;

import com.bialem.backend.domain.Notification;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.PushToken;
import com.bialem.backend.repository.NotificationRepository;
import com.bialem.backend.repository.ProfileRepository;
import com.bialem.backend.repository.PushTokenRepository;
import com.bialem.backend.service.dto.AppNotificationDTO;
import com.bialem.backend.service.dto.NotificationDTO;
import com.bialem.backend.service.dto.UnreadCountDTO;
import com.bialem.backend.service.mapper.NotificationMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.Notification}.
 */
@Service
@Transactional
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final ProfileRepository profileRepository;

    private final PushTokenRepository pushTokenRepository;

    private final FirebasePushService firebasePushService;

    private final AppSupport appSupport;

    public NotificationService(
        NotificationRepository notificationRepository,
        NotificationMapper notificationMapper,
        ProfileRepository profileRepository,
        PushTokenRepository pushTokenRepository,
        FirebasePushService firebasePushService,
        AppSupport appSupport
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.profileRepository = profileRepository;
        this.pushTokenRepository = pushTokenRepository;
        this.firebasePushService = firebasePushService;
        this.appSupport = appSupport;
    }

    public NotificationDTO save(NotificationDTO notificationDTO) {
        LOG.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    public NotificationDTO update(NotificationDTO notificationDTO) {
        LOG.debug("Request to update Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    public Optional<NotificationDTO> partialUpdate(NotificationDTO notificationDTO) {
        LOG.debug("Request to partially update Notification : {}", notificationDTO);

        return notificationRepository
            .findById(notificationDTO.getId())
            .map(existingNotification -> {
                notificationMapper.partialUpdate(existingNotification, notificationDTO);

                return existingNotification;
            })
            .map(notificationRepository::save)
            .map(notificationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<NotificationDTO> findOne(Long id) {
        LOG.debug("Request to get Notification : {}", id);
        return notificationRepository.findById(id).map(notificationMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Notification : {}", id);
        notificationRepository.deleteById(id);
    }

    public AppNotificationDTO sendToUser(
        Long userId,
        String title,
        String body,
        String type,
        String referenceId,
        String route
    ) {
        Profile profile = resolveProfile(userId);
        Instant now = Instant.now();
        Notification notification = new Notification();
        notification.setUser(profile);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setType(type != null ? type : "GENERIC");
        notification.setNotificationType(type != null ? type : "GENERIC");
        notification.setReferenceId(referenceId);
        notification.setRoute(route);
        notification.setIsRead(false);
        notification.setCreatedAt(now);
        notification = notificationRepository.saveAndFlush(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", notification.getType());
        data.put("referenceId", referenceId != null ? referenceId : "");
        data.put("route", route != null ? route : "");
        data.put("notificationId", String.valueOf(notification.getId()));

        List<PushToken> tokens = pushTokenRepository.findByUser_IdAndIsActiveTrue(profile.getId());
        for (PushToken pushToken : tokens) {
            firebasePushService.sendToToken(pushToken.getDeviceToken(), title, body, data);
        }
        return toInboxDto(notification);
    }

    @Transactional(readOnly = true)
    public List<AppNotificationDTO> listCurrentUser() {
        Long profileId = appSupport.currentProfile().getId();
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(profileId).stream().map(this::toInboxDto).toList();
    }

    @Transactional(readOnly = true)
    public UnreadCountDTO unreadCountCurrentUser() {
        Long profileId = appSupport.currentProfile().getId();
        return new UnreadCountDTO(notificationRepository.countByUser_IdAndIsReadFalse(profileId));
    }

    public AppNotificationDTO markCurrentUserRead(Long id) {
        Long profileId = appSupport.currentProfile().getId();
        Notification notification = notificationRepository
            .findByIdAndUser_Id(id, profileId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!Boolean.TRUE.equals(notification.getIsRead())) {
            notification.setIsRead(true);
            notification.setReadAt(Instant.now());
            notification = notificationRepository.save(notification);
        }
        return toInboxDto(notification);
    }

    public void markAllCurrentUserRead() {
        Long profileId = appSupport.currentProfile().getId();
        notificationRepository.markAllReadForUser(profileId, Instant.now());
    }

    public AppNotificationDTO sendTestToCurrentUser() {
        Long profileId = appSupport.currentProfile().getId();
        return sendToUser(
            profileId,
            "Bialem Test Bildirimi 🔔",
            "Push notification sistemi başarıyla çalışıyor.",
            "TEST",
            "0",
            "/"
        );
    }

    private Profile resolveProfile(Long userId) {
        return profileRepository
            .findById(userId)
            .or(() -> profileRepository.findOneByUser_Id(userId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil bulunamadı"));
    }

    private AppNotificationDTO toInboxDto(Notification notification) {
        AppNotificationDTO dto = new AppNotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setBody(notification.getBody());
        dto.setNotificationType(
            notification.getNotificationType() != null ? notification.getNotificationType() : notification.getType()
        );
        dto.setReferenceId(notification.getReferenceId());
        dto.setRoute(notification.getRoute());
        dto.setRead(Boolean.TRUE.equals(notification.getIsRead()));
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
