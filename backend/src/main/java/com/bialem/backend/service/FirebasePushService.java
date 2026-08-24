package com.bialem.backend.service;

import com.bialem.backend.config.FirebaseConfig;
import com.bialem.backend.domain.NotificationDeliveryLog;
import com.bialem.backend.domain.NotificationOutbox;
import com.bialem.backend.domain.PushDeviceToken;
import com.bialem.backend.domain.enumeration.NotificationDeliveryStatus;
import com.bialem.backend.domain.enumeration.NotificationOutboxStatus;
import com.bialem.backend.domain.enumeration.NotificationPriority;
import com.bialem.backend.repository.NotificationDeliveryLogRepository;
import com.bialem.backend.repository.NotificationOutboxRepository;
import com.bialem.backend.repository.PushDeviceTokenRepository;
import com.google.firebase.messaging.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FirebasePushService {

    private static final Logger LOG = LoggerFactory.getLogger(FirebasePushService.class);

    private static final int MAX_BATCH_SIZE = 500;

    private final FirebaseConfig firebaseConfig;
    private final PushDeviceTokenRepository pushDeviceTokenRepository;
    private final NotificationDeliveryLogRepository deliveryLogRepository;
    private final NotificationOutboxRepository outboxRepository;

    public FirebasePushService(
        FirebaseConfig firebaseConfig,
        PushDeviceTokenRepository pushDeviceTokenRepository,
        NotificationDeliveryLogRepository deliveryLogRepository,
        NotificationOutboxRepository outboxRepository
    ) {
        this.firebaseConfig = firebaseConfig;
        this.pushDeviceTokenRepository = pushDeviceTokenRepository;
        this.deliveryLogRepository = deliveryLogRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public NotificationOutboxStatus sendOutbox(NotificationOutbox outbox) {
        if (!firebaseConfig.isAvailable()) {
            LOG.debug("Firebase not configured; skipping push for outbox {}", outbox.getId());
            return NotificationOutboxStatus.SKIPPED;
        }

        List<PushDeviceToken> devices = pushDeviceTokenRepository.findByUser_IdAndActiveTrue(outbox.getUser().getId());
        devices = devices.stream().filter(d -> Boolean.TRUE.equals(d.getNotificationsEnabled())).toList();

        if (devices.isEmpty()) {
            LOG.debug("No active devices for user {} outbox {}", outbox.getUser().getId(), outbox.getId());
            return NotificationOutboxStatus.SKIPPED;
        }

        Map<String, String> data = buildPayload(outbox);
        NotificationPriority priority = resolvePriority(outbox);

        int successCount = 0;
        int failureCount = 0;
        String lastError = null;

        for (List<PushDeviceToken> batch : partition(devices, MAX_BATCH_SIZE)) {
            List<String> tokens = batch.stream().map(PushDeviceToken::getToken).toList();
            MulticastMessage message = buildMulticastMessage(tokens, data, priority);
            try {
                BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
                for (int i = 0; i < batch.size(); i++) {
                    PushDeviceToken device = batch.get(i);
                    SendResponse sendResponse = i < response.getResponses().size() ? response.getResponses().get(i) : null;
                    if (sendResponse != null && sendResponse.isSuccessful()) {
                        successCount++;
                        markDeviceSuccess(device);
                        saveDeliveryLog(outbox, device, NotificationDeliveryStatus.SENT, sendResponse.getMessageId(), null, null, outbox.getAttemptCount());
                    } else {
                        failureCount++;
                        String errorCode = null;
                        String errorMessage = null;
                        if (sendResponse != null && sendResponse.getException() != null) {
                            errorCode = String.valueOf(sendResponse.getException().getMessagingErrorCode());
                            errorMessage = maskError(sendResponse.getException().getMessage());
                            lastError = errorMessage;
                            handleDeviceError(device, sendResponse.getException());
                        }
                        saveDeliveryLog(outbox, device, NotificationDeliveryStatus.FAILED, null, errorCode, errorMessage, outbox.getAttemptCount());
                    }
                }
            } catch (FirebaseMessagingException ex) {
                LOG.warn("FCM multicast failed for outbox {}", outbox.getId(), ex);
                lastError = maskError(ex.getMessage());
                failureCount += batch.size();
                for (PushDeviceToken device : batch) {
                    saveDeliveryLog(
                        outbox,
                        device,
                        NotificationDeliveryStatus.FAILED,
                        null,
                        String.valueOf(ex.getMessagingErrorCode()),
                        lastError,
                        outbox.getAttemptCount()
                    );
                }
            } catch (RuntimeException ex) {
                LOG.warn("Unexpected FCM failure for outbox {}", outbox.getId(), ex);
                lastError = "Beklenmeyen gönderim hatası";
                failureCount += batch.size();
                for (PushDeviceToken device : batch) {
                    saveDeliveryLog(outbox, device, NotificationDeliveryStatus.FAILED, null, null, lastError, outbox.getAttemptCount());
                }
            }
        }

        outbox.setLastError(lastError);
        if (failureCount == 0 && successCount > 0) {
            return NotificationOutboxStatus.SENT;
        }
        if (successCount > 0) {
            return NotificationOutboxStatus.PARTIAL;
        }
        return NotificationOutboxStatus.FAILED;
    }

    public boolean isAvailable() {
        return firebaseConfig.isAvailable();
    }

    private MulticastMessage buildMulticastMessage(List<String> tokens, Map<String, String> data, NotificationPriority priority) {
        MulticastMessage.Builder builder = MulticastMessage.builder()
            .addAllTokens(tokens)
            .putAllData(data)
            .setNotification(
                com.google.firebase.messaging.Notification.builder()
                    .setTitle(data.get("title"))
                    .setBody(data.get("body"))
                    .build()
            )
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setPriority(priority == NotificationPriority.HIGH ? AndroidConfig.Priority.HIGH : AndroidConfig.Priority.NORMAL)
                    .setNotification(AndroidNotification.builder().setChannelId("bialem_notifications").setSound("default").build())
                    .build()
            );

        builder.setApnsConfig(
            ApnsConfig.builder()
                .putHeader("apns-priority", priority == NotificationPriority.HIGH ? "10" : "5")
                .setAps(Aps.builder().setContentAvailable(true).setSound("default").build())
                .build()
        );
        return builder.build();
    }

    private Map<String, String> buildPayload(NotificationOutbox outbox) {
        Map<String, String> data = new HashMap<>();
        data.put("notificationId", String.valueOf(outbox.getNotification().getId()));
        data.put("type", outbox.getNotification().getNotificationType());
        data.put("title", outbox.getNotification().getTitle());
        if (outbox.getNotification().getBody() != null) {
            data.put("body", outbox.getNotification().getBody());
        }
        if (outbox.getNotification().getRoute() != null) {
            data.put("route", outbox.getNotification().getRoute());
        }
        if (outbox.getNotification().getReferenceId() != null) {
            data.put("referenceId", outbox.getNotification().getReferenceId());
        }
        if (outbox.getNotification().getReferenceType() != null) {
            data.put("referenceType", outbox.getNotification().getReferenceType());
        }
        return data;
    }

    private NotificationPriority resolvePriority(NotificationOutbox outbox) {
        if (outbox.getNotification().getTemplate() != null && outbox.getNotification().getTemplate().getPriority() != null) {
            return outbox.getNotification().getTemplate().getPriority();
        }
        return NotificationPriority.NORMAL;
    }

    private void markDeviceSuccess(PushDeviceToken device) {
        Instant now = Instant.now();
        device.setLastSuccessAt(now);
        device.setLastSeenAt(now);
        pushDeviceTokenRepository.save(device);
    }

    private void handleDeviceError(PushDeviceToken device, FirebaseMessagingException ex) {
        MessagingErrorCode code = ex.getMessagingErrorCode();
        if (
            code == MessagingErrorCode.UNREGISTERED ||
            code == MessagingErrorCode.INVALID_ARGUMENT ||
            code == MessagingErrorCode.SENDER_ID_MISMATCH
        ) {
            device.setActive(false);
            device.setLastFailureAt(Instant.now());
            pushDeviceTokenRepository.save(device);
            LOG.info("Deactivated invalid FCM token id={}", device.getId());
        } else {
            device.setLastFailureAt(Instant.now());
            pushDeviceTokenRepository.save(device);
        }
    }

    private void saveDeliveryLog(
        NotificationOutbox outbox,
        PushDeviceToken device,
        NotificationDeliveryStatus status,
        String providerMessageId,
        String errorCode,
        String errorMessage,
        Integer attemptNumber
    ) {
        NotificationDeliveryLog log = new NotificationDeliveryLog();
        log.setNotification(outbox.getNotification());
        log.setPushDevice(device);
        log.setProvider("FCM");
        log.setStatus(status);
        log.setProviderMessageId(providerMessageId);
        log.setErrorCode(errorCode);
        log.setErrorMessage(errorMessage);
        log.setAttemptNumber(attemptNumber);
        log.setSentAt(Instant.now());
        log.setCreatedAt(Instant.now());
        deliveryLogRepository.save(log);
    }

    private String maskError(String message) {
        if (message == null) {
            return null;
        }
        return message.replaceAll("[a-zA-Z0-9_-]{20,}", "***");
    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }
}
