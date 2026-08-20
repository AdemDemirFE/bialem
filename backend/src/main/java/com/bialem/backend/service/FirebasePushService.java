package com.bialem.backend.service;

import com.bialem.backend.config.FirebaseConfig;
import com.bialem.backend.repository.PushDeviceTokenRepository;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FirebasePushService {

    private static final Logger LOG = LoggerFactory.getLogger(FirebasePushService.class);

    private final FirebaseConfig firebaseConfig;
    private final PushDeviceTokenRepository pushDeviceTokenRepository;

    public FirebasePushService(FirebaseConfig firebaseConfig, PushDeviceTokenRepository pushDeviceTokenRepository) {
        this.firebaseConfig = firebaseConfig;
        this.pushDeviceTokenRepository = pushDeviceTokenRepository;
    }

    public void sendToToken(String token, String title, String body, Map<String, String> data) {
        if (!firebaseConfig.isAvailable()) {
            LOG.debug("Skipping FCM send; Firebase is not configured");
            return;
        }
        if (token == null || token.isBlank()) {
            return;
        }
        Map<String, String> payload = new HashMap<>();
        if (data != null) {
            data.forEach((key, value) -> {
                if (key != null && value != null) {
                    payload.put(key, value);
                }
            });
        }
        Message message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            .putAllData(payload)
            .setAndroidConfig(AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build())
            .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException ex) {
            LOG.warn("FCM send failed: {}", ex.getMessagingErrorCode());
            removeIfInvalid(token, ex.getMessagingErrorCode());
        } catch (RuntimeException ex) {
            LOG.warn("FCM send failed", ex);
        }
    }

    private void removeIfInvalid(String token, MessagingErrorCode code) {
        if (
            code == MessagingErrorCode.UNREGISTERED ||
            code == MessagingErrorCode.INVALID_ARGUMENT ||
            code == MessagingErrorCode.SENDER_ID_MISMATCH
        ) {
            pushDeviceTokenRepository.findByToken(token).ifPresent(existing -> {
                pushDeviceTokenRepository.delete(existing);
                LOG.info("Removed invalid FCM token id={}", existing.getId());
            });
        }
    }
}
