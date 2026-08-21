package com.bialem.backend.web.rest;

import com.bialem.backend.service.AppNotificationService;
import com.bialem.backend.service.dto.AppNotificationDTO;
import com.bialem.backend.service.dto.NotificationPreferenceDTO;
import com.bialem.backend.service.dto.UnreadCountDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/app/notifications")
public class AppNotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(AppNotificationResource.class);

    private final AppNotificationService appNotificationService;

    public AppNotificationResource(AppNotificationService appNotificationService) {
        this.appNotificationService = appNotificationService;
    }

    @GetMapping("")
    public ResponseEntity<List<AppNotificationDTO>> getNotifications(
        @RequestParam(name = "filter", defaultValue = "ALL") AppNotificationService.NotificationFilter filter,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get current user notifications with filter {}", filter);
        Page<AppNotificationDTO> page = appNotificationService.listCurrentUser(filter, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/inbox")
    public List<AppNotificationDTO> getLegacyInbox() {
        LOG.debug("REST request to get legacy current user notifications");
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

    @GetMapping("/preferences")
    public List<NotificationPreferenceDTO> getPreferences() {
        LOG.debug("REST request to get current user notification preferences");
        return appNotificationService.getPreferencesCurrentUser();
    }

    @PutMapping("/preferences")
    public List<NotificationPreferenceDTO> updatePreferences(@RequestBody List<NotificationPreferenceDTO> preferences) {
        LOG.debug("REST request to update current user notification preferences");
        return appNotificationService.updatePreferencesCurrentUser(preferences);
    }
}
