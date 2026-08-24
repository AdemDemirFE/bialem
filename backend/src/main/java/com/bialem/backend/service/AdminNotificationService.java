package com.bialem.backend.service;

import com.bialem.backend.domain.User;
import com.bialem.backend.repository.PushDeviceTokenRepository;
import com.bialem.backend.service.dto.AdminNotificationSendRequest;
import com.bialem.backend.service.dto.AdminNotificationSendSummary;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminNotificationService {

    private final NotificationRecipientResolver recipientResolver;
    private final AppNotificationService notifications;
    private final PushDeviceTokenRepository pushTokens;

    public AdminNotificationService(
        NotificationRecipientResolver recipientResolver,
        AppNotificationService notifications,
        PushDeviceTokenRepository pushTokens
    ) {
        this.recipientResolver = recipientResolver;
        this.notifications = notifications;
        this.pushTokens = pushTokens;
    }

    public AdminNotificationSendSummary send(AdminNotificationSendRequest request) {
        List<User> recipients = recipientResolver.resolve(request);
        boolean pushEnabled = !Boolean.FALSE.equals(request.sendPush());
        List<Long> userIds = recipients.stream().map(User::getId).toList();
        long eligible = pushEnabled && !userIds.isEmpty() ? pushTokens.countPushEligibleUsers(userIds) : 0;
        int created = notifications.createAdminNotifications(recipients, request);
        return new AdminNotificationSendSummary(
            created > 0,
            recipients.size(),
            eligible,
            0,
            0,
            pushEnabled ? Math.max(0, recipients.size() - eligible) : recipients.size(),
            created == 0 ? "DUPLICATE" : pushEnabled ? "QUEUED" : "IN_APP_CREATED"
        );
    }
}
