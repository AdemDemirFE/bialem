package com.bialem.backend.web.rest;

import com.bialem.backend.service.AppNotificationService;
import com.bialem.backend.service.dto.AppNotificationDTO;
import com.bialem.backend.service.dto.UnreadCountDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class AppNotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(AppNotificationResource.class);

    private final AppNotificationService appNotificationService;

    public AppNotificationResource(AppNotificationService appNotificationService) {
        this.appNotificationService = appNotificationService;
    }

    @GetMapping("/inbox")
    public List<AppNotificationDTO> getNotifications() {
        LOG.debug("REST request to get current user notifications");
        return appNotificationService.listCurrentUser();
    }

    @PostMapping("/test")
    public ResponseEntity<AppNotificationDTO> sendTestNotification() {
        LOG.debug("REST request to send test notification to current user");
        return ResponseEntity.ok(appNotificationService.sendTestToCurrentUser());
    }

    @GetMapping("/unread-count")
    public UnreadCountDTO getUnreadCount() {
        LOG.debug("REST request to get current user unread notification count");
        return appNotificationService.unreadCountCurrentUser();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        LOG.debug("REST request to mark all current user notifications as read");
        appNotificationService.markAllCurrentUserRead();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/read")
    public AppNotificationDTO markRead(@PathVariable("id") Long id) {
        LOG.debug("REST request to mark notification {} as read", id);
        return appNotificationService.markCurrentUserRead(id);
    }
}
