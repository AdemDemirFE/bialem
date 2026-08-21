package com.bialem.backend.notification;

import com.bialem.backend.domain.NotificationOutbox;
import com.bialem.backend.domain.enumeration.NotificationOutboxStatus;
import com.bialem.backend.repository.NotificationOutboxRepository;
import com.bialem.backend.service.FirebasePushService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NotificationOutboxScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationOutboxScheduler.class);

    private static final int BATCH_SIZE = 100;
    private static final List<Integer> RETRY_DELAYS_MINUTES = List.of(1, 5, 15, 60, 240);

    private final NotificationOutboxRepository outboxRepository;
    private final FirebasePushService firebasePushService;

    public NotificationOutboxScheduler(NotificationOutboxRepository outboxRepository, FirebasePushService firebasePushService) {
        this.outboxRepository = outboxRepository;
        this.firebasePushService = firebasePushService;
    }

    @Scheduled(fixedDelayString = "${bialem.notification.outbox.interval:30000}")
    @Transactional
    public void processPendingOutbox() {
        Instant now = Instant.now();
        Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        Page<NotificationOutbox> page;

        do {
            page = outboxRepository.findPendingReadyForProcessing(NotificationOutboxStatus.PENDING, now, pageable);
            for (NotificationOutbox outbox : page.getContent()) {
                processSingleOutbox(outbox.getId(), now);
            }
            pageable = page.nextPageable();
        } while (page.hasNext());
    }

    @Transactional
    public void processSingleOutbox(Long outboxId, Instant now) {
        NotificationOutbox outbox = outboxRepository.findByIdWithLock(outboxId).orElse(null);
        if (outbox == null || outbox.getStatus() != NotificationOutboxStatus.PENDING) {
            return;
        }

        if (outbox.getScheduledAt() != null && outbox.getScheduledAt().isAfter(now)) {
            return;
        }
        if (outbox.getNextAttemptAt() != null && outbox.getNextAttemptAt().isAfter(now)) {
            return;
        }

        outbox.setStatus(NotificationOutboxStatus.PROCESSING);
        outboxRepository.save(outbox);

        try {
            NotificationOutboxStatus result = firebasePushService.sendOutbox(outbox);
            Instant processedAt = Instant.now();
            if (result == NotificationOutboxStatus.SENT || result == NotificationOutboxStatus.SKIPPED) {
                outboxRepository.updateFinalState(outboxId, result, processedAt, processedAt);
                updateNotificationPushStatus(outbox, result.name(), processedAt);
            } else if (result == NotificationOutboxStatus.PARTIAL) {
                if (outbox.getAttemptCount() + 1 >= outbox.getMaxAttempts()) {
                    outboxRepository.updateFinalState(outboxId, NotificationOutboxStatus.PARTIAL, processedAt, processedAt);
                } else {
                    scheduleRetry(outbox, result);
                }
                updateNotificationPushStatus(outbox, result.name(), processedAt);
            } else {
                if (outbox.getAttemptCount() + 1 >= outbox.getMaxAttempts()) {
                    outboxRepository.updateFinalState(outboxId, NotificationOutboxStatus.FAILED, processedAt, null);
                } else {
                    scheduleRetry(outbox, result);
                }
                updateNotificationPushStatus(outbox, result.name(), null);
            }
        } catch (Exception ex) {
            LOG.error("Unexpected error processing outbox {}", outboxId, ex);
            if (outbox.getAttemptCount() + 1 >= outbox.getMaxAttempts()) {
                outboxRepository.updateFinalState(outboxId, NotificationOutboxStatus.FAILED, Instant.now(), null);
            } else {
                scheduleRetry(outbox, NotificationOutboxStatus.FAILED);
            }
        }
    }

    private void scheduleRetry(NotificationOutbox outbox, NotificationOutboxStatus status) {
        int nextAttempt = outbox.getAttemptCount();
        int delayMinutes = nextAttempt < RETRY_DELAYS_MINUTES.size()
            ? RETRY_DELAYS_MINUTES.get(nextAttempt)
            : RETRY_DELAYS_MINUTES.get(RETRY_DELAYS_MINUTES.size() - 1);
        Instant nextAttemptAt = Instant.now().plus(delayMinutes, ChronoUnit.MINUTES);
        String error = status == NotificationOutboxStatus.FAILED ? "Gönderim başarısız, yeniden denenecek" : "Kısmi gönderim, yeniden denenecek";
        outboxRepository.updateRetryState(outbox.getId(), NotificationOutboxStatus.PENDING, error, nextAttemptAt);
    }

    private void updateNotificationPushStatus(NotificationOutbox outbox, String status, Instant sentAt) {
        if (outbox.getNotification() != null) {
            outbox.getNotification().setPushStatus(status);
            outbox.getNotification().setPushSentAt(sentAt);
            outboxRepository.save(outbox);
        }
    }

    @Scheduled(cron = "${bialem.notification.stale.devices.cron:0 2 * * * *}")
    public void cleanupStaleDevices() {
        Instant cutoff = Instant.now().minus(90, ChronoUnit.DAYS);
        LOG.info("Cleaning up push devices inactive since {}", cutoff);
    }
}
