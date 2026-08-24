package com.bialem.backend.service;

import com.bialem.backend.domain.*;
import com.bialem.backend.domain.enumeration.NotificationEventType;
import com.bialem.backend.domain.enumeration.NotificationOutboxStatus;
import com.bialem.backend.domain.enumeration.NotificationPriority;
import com.bialem.backend.domain.enumeration.NotificationScheduleType;
import com.bialem.backend.notification.NotificationEvent;
import com.bialem.backend.notification.NotificationEventPublisher;
import com.bialem.backend.notification.NotificationTemplateService;
import com.bialem.backend.repository.*;
import com.bialem.backend.security.SecurityUtils;
import com.bialem.backend.service.dto.AppNotificationDTO;
import com.bialem.backend.service.dto.AdminNotificationSendRequest;
import com.bialem.backend.service.dto.NotificationPreferenceDTO;
import com.bialem.backend.service.dto.UnreadCountDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AppNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(AppNotificationService.class);

    private final AppNotificationRepository appNotificationRepository;

    private final NotificationOutboxRepository notificationOutboxRepository;

    private final PushDeviceTokenRepository pushDeviceTokenRepository;

    private final UserRepository userRepository;

    private final ProfileRepository profileRepository;

    private final NotificationTemplateRepository notificationTemplateRepository;

    private final UserNotificationPreferenceRepository preferenceRepository;

    private final NotificationEventPublisher notificationEventPublisher;

    private final ObjectMapper objectMapper;

    public AppNotificationService(
        AppNotificationRepository appNotificationRepository,
        NotificationOutboxRepository notificationOutboxRepository,
        PushDeviceTokenRepository pushDeviceTokenRepository,
        UserRepository userRepository,
        ProfileRepository profileRepository,
        NotificationTemplateRepository notificationTemplateRepository,
        UserNotificationPreferenceRepository preferenceRepository,
        NotificationEventPublisher notificationEventPublisher,
        ObjectMapper objectMapper
    ) {
        this.appNotificationRepository = appNotificationRepository;
        this.notificationOutboxRepository = notificationOutboxRepository;
        this.pushDeviceTokenRepository = pushDeviceTokenRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.notificationTemplateRepository = notificationTemplateRepository;
        this.preferenceRepository = preferenceRepository;
        this.notificationEventPublisher = notificationEventPublisher;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public Page<AppNotificationDTO> listCurrentUser(NotificationFilter filter, Pageable pageable) {
        Long userId = currentUserId();
        return switch (filter) {
            case UNREAD -> appNotificationRepository.findByUser_IdAndIsReadOrderByCreatedAtDesc(userId, false, pageable).map(this::toDto);
            case READ -> appNotificationRepository.findByUser_IdAndIsReadOrderByCreatedAtDesc(userId, true, pageable).map(this::toDto);
            default -> appNotificationRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable).map(this::toDto);
        };
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

    @Transactional(readOnly = true)
    public AppNotificationDTO getCurrentUserNotification(Long id) {
        return appNotificationRepository.findByIdAndUser_Id(id, currentUserId())
            .map(this::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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

        createOutboxForUser(user, notification, now, null);

        return toDto(notification);
    }

    public AppNotificationDTO sendManualNotification(
        Long userId,
        String title,
        String body,
        String route,
        boolean pushEnabled,
        boolean inAppEnabled,
        Instant scheduledAt,
        NotificationPriority priority
    ) {
        User user = resolveUser(userId);
        Instant now = Instant.now();
        String correlationId = UUID.randomUUID().toString();

        AppNotification notification = new AppNotification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setNotificationType(NotificationEventType.ADMIN_BROADCAST.name());
        notification.setRoute(sanitizeRoute(route));
        notification.setCorrelationId(correlationId);
        notification.setIdempotencyKey("MANUAL:" + correlationId + ":" + userId);
        notification.setScheduledAt(scheduledAt);
        notification.setIsRead(!inAppEnabled);
        notification.setCreatedAt(now);
        notification = appNotificationRepository.save(notification);

        if (pushEnabled) {
            createOutboxForUser(user, notification, now, scheduledAt);
        }

        return toDto(notification);
    }

    public void sendAdminBroadcast(
        List<Long> userIds,
        String title,
        String body,
        String route,
        boolean pushEnabled,
        boolean inAppEnabled,
        Instant scheduledAt,
        NotificationPriority priority
    ) {
        String correlationId = UUID.randomUUID().toString();
        for (Long userId : userIds) {
            User user = resolveUser(userId);
            Instant now = Instant.now();
            AppNotification notification = new AppNotification();
            notification.setUser(user);
            notification.setTitle(title);
            notification.setBody(body);
            notification.setNotificationType(NotificationEventType.ADMIN_BROADCAST.name());
            notification.setRoute(sanitizeRoute(route));
            notification.setCorrelationId(correlationId);
            notification.setIdempotencyKey("BROADCAST:" + correlationId + ":" + userId);
            notification.setScheduledAt(scheduledAt);
            notification.setIsRead(!inAppEnabled);
            notification.setCreatedAt(now);
            notification = appNotificationRepository.save(notification);

            if (pushEnabled) {
                createOutboxForUser(user, notification, now, scheduledAt);
            }
        }
    }

    public void sendAdminBroadcastToAllActiveUsers(
        String title,
        String body,
        String route,
        boolean pushEnabled,
        boolean inAppEnabled,
        Instant scheduledAt,
        NotificationPriority priority
    ) {
        sendAdminBroadcast(
            userRepository.findAllByActivatedIsTrue().stream().map(User::getId).toList(),
            title,
            body,
            route,
            pushEnabled,
            inAppEnabled,
            scheduledAt,
            priority
        );
    }

    public int createAdminNotifications(List<User> recipients, AdminNotificationSendRequest request) {
        String correlationId = request.requestId() == null || request.requestId().isBlank()
            ? UUID.randomUUID().toString()
            : request.requestId().trim();
        boolean pushEnabled = !Boolean.FALSE.equals(request.sendPush());
        boolean inAppEnabled = !Boolean.FALSE.equals(request.inAppEnabled());
        int created = 0;
        for (User user : recipients) {
            String idempotencyKey = "ADMIN:" + correlationId + ":" + user.getId();
            if (appNotificationRepository.findByIdempotencyKey(idempotencyKey).isPresent()) continue;

            Instant now = Instant.now();
            AppNotification notification = new AppNotification();
            notification.setUser(user);
            notification.setTitle(request.title().trim());
            notification.setBody(request.body().trim());
            notification.setNotificationType(
                request.notificationType() == null || request.notificationType().isBlank()
                    ? NotificationEventType.ADMIN_BROADCAST.name()
                    : request.notificationType().trim()
            );
            notification.setRoute(sanitizeRoute(request.route()));
            notification.setReferenceType(blankToNull(request.referenceType()));
            notification.setReferenceId(blankToNull(request.referenceId()));
            notification.setCorrelationId(correlationId);
            notification.setIdempotencyKey(idempotencyKey);
            notification.setScheduledAt(request.scheduledAt());
            notification.setIsRead(!inAppEnabled);
            notification.setCreatedAt(now);
            notification = appNotificationRepository.save(notification);
            if (pushEnabled) createOutboxForUser(user, notification, now, request.scheduledAt());
            created++;
        }
        return created;
    }

    @Transactional(readOnly = true)
    public List<NotificationPreferenceDTO> getPreferencesCurrentUser() {
        Long userId = currentUserId();
        List<UserNotificationPreference> prefs = preferenceRepository.findByUser_Id(userId);
        return prefs.stream().map(this::toPreferenceDto).toList();
    }

    public List<NotificationPreferenceDTO> updatePreferencesCurrentUser(List<NotificationPreferenceDTO> preferences) {
        Long userId = currentUserId();
        User user = resolveUser(userId);
        List<NotificationPreferenceDTO> result = new ArrayList<>();
        for (NotificationPreferenceDTO dto : preferences) {
            UserNotificationPreference pref = preferenceRepository
                .findByUser_IdAndNotificationType(userId, dto.getNotificationType())
                .orElseGet(() -> {
                    UserNotificationPreference p = new UserNotificationPreference();
                    p.setUser(user);
                    p.setNotificationType(dto.getNotificationType());
                    p.setCreatedAt(Instant.now());
                    return p;
                });
            if (Boolean.TRUE.equals(pref.getMandatory())) {
                continue;
            }
            pref.setInAppEnabled(dto.getInAppEnabled());
            pref.setPushEnabled(dto.getPushEnabled());
            pref.setEmailEnabled(dto.getEmailEnabled());
            pref.setMutedUntil(dto.getMutedUntil());
            pref.setUpdatedAt(Instant.now());
            result.add(toPreferenceDto(preferenceRepository.save(pref)));
        }
        return result;
    }

    private void createOutboxForUser(User user, AppNotification notification, Instant now, Instant scheduledAt) {
        String idempotencyKey =
            (notification.getIdempotencyKey() != null ? notification.getIdempotencyKey() : UUID.randomUUID().toString()) + ":PUSH";
        if (notificationOutboxRepository.findByIdempotencyKey(idempotencyKey).isPresent()) {
            return;
        }
        NotificationOutbox outbox = new NotificationOutbox();
        outbox.setNotification(notification);
        outbox.setUser(user);
        outbox.setStatus(NotificationOutboxStatus.PENDING);
        outbox.setScheduledAt(scheduledAt);
        outbox.setNextAttemptAt(scheduledAt != null && scheduledAt.isAfter(now) ? scheduledAt : now);
        outbox.setAttemptCount(0);
        outbox.setMaxAttempts(5);
        outbox.setCreatedAt(now);
        outbox.setIdempotencyKey(idempotencyKey);
        notificationOutboxRepository.save(outbox);
    }

    private User resolveUser(Long userId) {
        return userRepository
            .findById(userId)
            .or(() -> profileRepository.findById(userId).map(Profile::getUser))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı veya profil bulunamadı"));
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
        dto.setReferenceType(notification.getReferenceType());
        dto.setRecipientUserId(notification.getUser() != null ? notification.getUser().getId() : null);
        dto.setActorUserId(notification.getActorUserId());
        dto.setMetadata(notification.getPayload());
        dto.setRoute(notification.getRoute());
        dto.setRead(Boolean.TRUE.equals(notification.getIsRead()));
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setReadAt(notification.getReadAt());
        dto.setPushStatus(notification.getPushStatus());
        dto.setPushSentAt(notification.getPushSentAt());
        return dto;
    }

    private NotificationPreferenceDTO toPreferenceDto(UserNotificationPreference preference) {
        NotificationPreferenceDTO dto = new NotificationPreferenceDTO();
        dto.setId(preference.getId());
        dto.setNotificationType(preference.getNotificationType());
        dto.setInAppEnabled(Boolean.TRUE.equals(preference.getInAppEnabled()));
        dto.setPushEnabled(Boolean.TRUE.equals(preference.getPushEnabled()));
        dto.setEmailEnabled(Boolean.TRUE.equals(preference.getEmailEnabled()));
        dto.setMutedUntil(preference.getMutedUntil());
        dto.setMandatory(Boolean.TRUE.equals(preference.getMandatory()));
        return dto;
    }

    private String sanitizeRoute(String route) {
        if (route == null || route.isBlank()) {
            return null;
        }
        String trimmed = route.trim();
        if (trimmed.contains("://") || trimmed.toLowerCase().startsWith("javascript:")) {
            LOG.warn("Rejected unsafe notification route: {}", trimmed);
            return null;
        }
        return trimmed;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public enum NotificationFilter {
        ALL,
        READ,
        UNREAD,
    }
}
