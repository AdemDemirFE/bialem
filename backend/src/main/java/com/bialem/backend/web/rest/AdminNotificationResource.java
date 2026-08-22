package com.bialem.backend.web.rest;

import com.bialem.backend.domain.NotificationOutbox;
import com.bialem.backend.domain.enumeration.NotificationOutboxStatus;
import com.bialem.backend.domain.enumeration.NotificationPriority;
import com.bialem.backend.notification.NotificationOutboxScheduler;
import com.bialem.backend.repository.NotificationOutboxRepository;
import com.bialem.backend.service.AppNotificationService;
import com.bialem.backend.service.dto.AppNotificationDTO;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/notifications")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminNotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(AdminNotificationResource.class);

    private final AppNotificationService appNotificationService;

    private final NotificationOutboxRepository notificationOutboxRepository;

    private final NotificationOutboxScheduler notificationOutboxScheduler;

    public AdminNotificationResource(
        AppNotificationService appNotificationService,
        NotificationOutboxRepository notificationOutboxRepository,
        NotificationOutboxScheduler notificationOutboxScheduler
    ) {
        this.appNotificationService = appNotificationService;
        this.notificationOutboxRepository = notificationOutboxRepository;
        this.notificationOutboxScheduler = notificationOutboxScheduler;
    }

    @PostMapping("/send")
    public ResponseEntity<AppNotificationDTO> sendManualNotification(@Valid @RequestBody ManualNotificationRequest request) {
        LOG.debug("REST request to send manual notification to {} users", request.userIds().size());
        if (request.userIds().size() == 1) {
            AppNotificationDTO result = appNotificationService.sendManualNotification(
                request.userIds().get(0),
                request.title(),
                request.body(),
                request.route(),
                request.pushEnabled(),
                request.inAppEnabled(),
                request.scheduledAt(),
                request.priority()
            );
            return ResponseEntity.accepted().body(result);
        }
        appNotificationService.sendAdminBroadcast(
            request.userIds(),
            request.title(),
            request.body(),
            request.route(),
            request.pushEnabled(),
            request.inAppEnabled(),
            request.scheduledAt(),
            request.priority()
        );
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/outbox")
    public ResponseEntity<List<NotificationOutbox>> getOutbox(
        @RequestParam(required = false) NotificationOutboxStatus status,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get notification outbox by status {}", status);
        Page<NotificationOutbox> page;
        if (status != null) {
            page = notificationOutboxRepository.findAll((root, query, cb) -> cb.equal(root.get("status"), status), pageable);
        } else {
            page = notificationOutboxRepository.findAll(pageable);
        }
        return ResponseEntity.ok(page.getContent());
    }

    @PostMapping("/outbox/{id}/cancel")
    public ResponseEntity<Void> cancelOutbox(@PathVariable Long id) {
        LOG.debug("REST request to cancel notification outbox {}", id);
        NotificationOutbox outbox = notificationOutboxRepository.findById(id).orElseThrow();
        if (outbox.getStatus() == NotificationOutboxStatus.PENDING) {
            outbox.setStatus(NotificationOutboxStatus.CANCELLED);
            outbox.setProcessedAt(Instant.now());
            notificationOutboxRepository.save(outbox);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/outbox/{id}/process")
    public ResponseEntity<Void> processOutbox(@PathVariable Long id) {
        LOG.debug("REST request to process notification outbox {}", id);
        notificationOutboxScheduler.processSingleOutbox(id, Instant.now());
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        LOG.debug("REST request to get notification stats");
        Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("pending", notificationOutboxRepository.countByStatus(NotificationOutboxStatus.PENDING));
        stats.put("sent", notificationOutboxRepository.countByStatus(NotificationOutboxStatus.SENT));
        stats.put("failed", notificationOutboxRepository.countByStatus(NotificationOutboxStatus.FAILED));
        stats.put("activeDevices", 0L);
        return ResponseEntity.ok(stats);
    }

    public record ManualNotificationRequest(
        List<Long> userIds,
        String title,
        String body,
        String route,
        boolean pushEnabled,
        boolean inAppEnabled,
        Instant scheduledAt,
        NotificationPriority priority
    ) {}
}
