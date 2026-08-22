package com.bialem.backend.notification;

import com.bialem.backend.domain.*;
import com.bialem.backend.domain.enumeration.*;
import com.bialem.backend.notification.NotificationTemplateService.RenderedNotification;
import com.bialem.backend.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationProcessor.class);

    private final NotificationTemplateRepository templateRepository;
    private final NotificationTemplateService templateService;
    private final AppNotificationRepository appNotificationRepository;
    private final NotificationOutboxRepository outboxRepository;
    private final UserNotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final ObjectMapper objectMapper;

    public NotificationProcessor(
        NotificationTemplateRepository templateRepository,
        NotificationTemplateService templateService,
        AppNotificationRepository appNotificationRepository,
        NotificationOutboxRepository outboxRepository,
        UserNotificationPreferenceRepository preferenceRepository,
        UserRepository userRepository,
        CommunityMemberRepository communityMemberRepository,
        ObjectMapper objectMapper
    ) {
        this.templateRepository = templateRepository;
        this.templateService = templateService;
        this.appNotificationRepository = appNotificationRepository;
        this.outboxRepository = outboxRepository;
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
        this.communityMemberRepository = communityMemberRepository;
        this.objectMapper = objectMapper;
    }

    public void process(NotificationEvent event) {
        Optional<NotificationTemplate> templateOptional = templateRepository.findByEventType(event.getType());
        if (templateOptional.isEmpty()) {
            LOG.warn("No notification template found for event type: {}", event.getType());
            return;
        }

        NotificationTemplate template = templateOptional.get();
        if (!Boolean.TRUE.equals(template.getEnabled())) {
            LOG.debug("Notification template {} is disabled", template.getCode());
            return;
        }

        List<Long> recipientIds = resolveRecipients(event, template);
        if (recipientIds.isEmpty()) {
            LOG.debug("No recipients resolved for event type: {}", event.getType());
            return;
        }

        RenderedNotification rendered = templateService.render(template, event.getVariables());
        Instant scheduledAt = resolveScheduledAt(template);

        for (Long recipientId : recipientIds) {
            createNotificationForRecipient(event, template, recipientId, rendered, scheduledAt);
        }
    }

    private void createNotificationForRecipient(
        NotificationEvent event,
        NotificationTemplate template,
        Long recipientId,
        RenderedNotification rendered,
        Instant scheduledAt
    ) {
        Optional<User> userOptional = userRepository.findById(recipientId);
        if (userOptional.isEmpty()) {
            LOG.warn("Recipient user not found: {}", recipientId);
            return;
        }
        User user = userOptional.get();

        Long actorUserId = toLong(event.getVariable("actorUserId"));
        if (actorUserId != null && actorUserId.equals(recipientId)) {
            LOG.debug("Self notification skipped for user {} and event {}", recipientId, event.getType());
            return;
        }

        UserNotificationPreference preference = getOrCreatePreference(user, template);
        boolean muted = preference.getMutedUntil() != null && preference.getMutedUntil().isAfter(Instant.now());
        boolean mandatory = Boolean.TRUE.equals(preference.getMandatory());
        boolean inAppEnabled = mandatory || (Boolean.TRUE.equals(preference.getInAppEnabled()) && !muted);
        boolean pushEnabled = mandatory || (Boolean.TRUE.equals(preference.getPushEnabled()) && !muted);

        if (!inAppEnabled && !pushEnabled) {
            LOG.debug("Notifications disabled for user {} and event type {}", recipientId, template.getEventType());
            return;
        }

        String idempotencyKey = event.getIdempotencyKey() + ":" + recipientId;
        if (appNotificationRepository.findByIdempotencyKey(idempotencyKey).isPresent()) {
            LOG.debug("Duplicate notification skipped for idempotency key: {}", idempotencyKey);
            return;
        }

        Instant now = Instant.now();
        AppNotification notification = new AppNotification();
        notification.setUser(user);
        notification.setTemplate(template);
        notification.setNotificationType(template.getEventType().name());
        notification.setTitle(rendered.title());
        notification.setBody(rendered.body());
        notification.setRoute(sanitizeRoute(rendered.route()));
        notification.setEventId(extractEventId(event));
        notification.setReferenceId(stringValue(event.getVariable("referenceId")));
        notification.setReferenceType(stringValue(event.getVariable("referenceType")));
        notification.setActorUserId(actorUserId);
        notification.setPayload(toJson(event.getVariables()));
        notification.setIdempotencyKey(idempotencyKey);
        notification.setCorrelationId(UUID.randomUUID().toString());
        notification.setScheduledAt(scheduledAt);
        notification.setIsRead(false);
        notification.setCreatedAt(now);

        // Persist the inbox record before creating the Firebase outbox entry. This keeps
        // every push traceable and guarantees that FCM payloads reference a real record.
        notification = appNotificationRepository.save(notification);

        if (pushEnabled && template.getPushEnabled()) {
            createOutboxEntry(notification, user, template, scheduledAt, idempotencyKey + ":PUSH");
        }
    }

    private void createOutboxEntry(
        AppNotification notification,
        User user,
        NotificationTemplate template,
        Instant scheduledAt,
        String idempotencyKey
    ) {
        if (outboxRepository.findByIdempotencyKey(idempotencyKey).isPresent()) {
            LOG.debug("Duplicate outbox entry skipped: {}", idempotencyKey);
            return;
        }

        Instant now = Instant.now();
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
        outboxRepository.save(outbox);
    }

    private UserNotificationPreference getOrCreatePreference(User user, NotificationTemplate template) {
        String type = template.getEventType().name();
        return preferenceRepository
            .findByUser_IdAndNotificationType(user.getId(), type)
            .orElseGet(() -> {
                UserNotificationPreference pref = new UserNotificationPreference();
                pref.setUser(user);
                pref.setNotificationType(type);
                pref.setInAppEnabled(true);
                pref.setPushEnabled(true);
                pref.setEmailEnabled(false);
                pref.setMandatory(false);
                pref.setCreatedAt(Instant.now());
                return preferenceRepository.save(pref);
            });
    }

    private List<Long> resolveRecipients(NotificationEvent event, NotificationTemplate template) {
        NotificationEventType type = event.getType();
        Object recipientId = event.getVariable("recipientUserId");
        if (recipientId instanceof Number number) {
            return List.of(number.longValue());
        }

        Object recipientIds = event.getVariable("recipientUserIds");
        if (recipientIds instanceof Collection<?> collection) {
            return collection.stream().map(this::toLong).filter(Objects::nonNull).distinct().toList();
        }
        if (recipientId instanceof String string) {
            try {
                return List.of(Long.parseLong(string));
            } catch (NumberFormatException ex) {
                LOG.warn("Invalid recipientUserId: {}", recipientId);
            }
        }

        switch (type) {
            case NEW_FOLLOWER:
                Object followedId = event.getVariable("followedUserId");
                if (followedId instanceof Number number) {
                    return List.of(number.longValue());
                }
                break;
            case COMMUNITY_MEMBERSHIP_REQUEST:
                Object communityId = event.getVariable("communityId");
                if (communityId instanceof Number number) {
                    return resolveCommunityAdmins(number.longValue());
                }
                break;
            case COMMUNITY_MEMBERSHIP_APPROVED:
            case COMMUNITY_MEMBERSHIP_REJECTED:
                Object applicantId = event.getVariable("applicantId");
                if (applicantId instanceof Number number) {
                    return List.of(number.longValue());
                }
                break;
            case USER_REVIEW:
                Object reviewedId = event.getVariable("reviewedUserId");
                if (reviewedId instanceof Number number) {
                    return List.of(number.longValue());
                }
                break;
            case HONOR_BADGE_AWARDED:
                Object awardedId = event.getVariable("awardedUserId");
                if (awardedId instanceof Number number) {
                    return List.of(number.longValue());
                }
                break;
            case EVENT_PUBLISHED:
                Object eventOwnerId = event.getVariable("eventOwnerId");
                if (eventOwnerId instanceof Number number) {
                    return List.of(number.longValue());
                }
                break;
            case ADMIN_BROADCAST:
                Object targetUserIds = event.getVariable("targetUserIds");
                if (targetUserIds instanceof Collection<?> collection) {
                    return collection.stream().filter(Objects::nonNull).map(this::toLong).filter(Objects::nonNull).toList();
                }
                return userRepository.findAll().stream().map(User::getId).toList();
            default:
                break;
        }

        return List.of();
    }

    private List<Long> resolveCommunityAdmins(Long communityId) {
        return communityMemberRepository
            .findManagersByCommunityId(
                communityId,
                List.of(CommunityMemberRole.OWNER, CommunityMemberRole.MANAGER),
                CommunityMemberStatus.APPROVED
            )
            .stream()
            .map(CommunityMember::getUser)
            .filter(Objects::nonNull)
            .map(Profile::getUser)
            .filter(Objects::nonNull)
            .map(User::getId)
            .distinct()
            .toList();
    }

    private Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String string) {
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }


    private String stringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private Instant resolveScheduledAt(NotificationTemplate template) {
        Instant now = Instant.now();
        if (template.getScheduleType() == NotificationScheduleType.IMMEDIATE) {
            return now;
        }
        if (template.getScheduleType() == NotificationScheduleType.DELAYED && template.getDelayMinutes() != null) {
            return now.plusSeconds(template.getDelayMinutes() * 60L);
        }
        if (template.getScheduleType() == NotificationScheduleType.FIXED_TIME && template.getPreferredSendTime() != null) {
            try {
                ZoneId zone = ZoneId.of(template.getTimezone());
                ZonedDateTime today = ZonedDateTime.ofInstant(now, zone);
                String[] parts = template.getPreferredSendTime().split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                ZonedDateTime target = today.withHour(hour).withMinute(minute).withSecond(0).withNano(0);
                if (target.isBefore(today)) {
                    target = target.plusDays(1);
                }
                return target.toInstant();
            } catch (Exception ex) {
                LOG.warn("Could not resolve fixed time schedule for template {}", template.getCode(), ex);
            }
        }
        return now;
    }

    private String extractEventId(NotificationEvent event) {
        Object id = event.getVariable("eventId");
        if (id != null) {
            return id.toString();
        }
        Object referenceId = event.getVariable("referenceId");
        return referenceId != null ? referenceId.toString() : null;
    }

    private String toJson(Map<String, Object> variables) {
        try {
            return objectMapper.writeValueAsString(variables);
        } catch (JsonProcessingException ex) {
            LOG.warn("Failed to serialize notification payload", ex);
            return "{}";
        }
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
}
