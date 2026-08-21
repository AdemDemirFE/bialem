package com.bialem.backend.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class NotificationEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationEventListener.class);

    private final NotificationProcessor notificationProcessor;

    public NotificationEventListener(NotificationProcessor notificationProcessor) {
        this.notificationProcessor = notificationProcessor;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEvent event) {
        LOG.debug("Handling notification event after commit: {}", event.getType());
        try {
            notificationProcessor.process(event);
        } catch (Exception ex) {
            LOG.error("Failed to process notification event: {}", event.getType(), ex);
        }
    }
}
