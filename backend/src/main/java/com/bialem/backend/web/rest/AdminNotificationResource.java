package com.bialem.backend.web.rest;

import com.bialem.backend.domain.NotificationOutbox;
import com.bialem.backend.domain.enumeration.NotificationOutboxStatus;
import com.bialem.backend.domain.enumeration.PushPlatform;
import com.bialem.backend.notification.NotificationOutboxScheduler;
import com.bialem.backend.repository.NotificationOutboxRepository;
import com.bialem.backend.repository.NotificationDeliveryLogRepository;
import com.bialem.backend.repository.PushDeviceTokenRepository;
import com.bialem.backend.service.AdminNotificationService;
import com.bialem.backend.service.FirebasePushService;
import com.bialem.backend.service.dto.AdminNotificationSendRequest;
import com.bialem.backend.service.dto.AdminNotificationSendSummary;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/notifications")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
public class AdminNotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(AdminNotificationResource.class);

    private final AdminNotificationService adminNotificationService;

    private final NotificationOutboxRepository notificationOutboxRepository;

    private final NotificationOutboxScheduler notificationOutboxScheduler;
    private final NotificationDeliveryLogRepository deliveryLogRepository;
    private final PushDeviceTokenRepository pushDeviceTokenRepository;
    private final FirebasePushService firebasePushService;

    public AdminNotificationResource(
        AdminNotificationService adminNotificationService,
        NotificationOutboxRepository notificationOutboxRepository,
        NotificationOutboxScheduler notificationOutboxScheduler,
        NotificationDeliveryLogRepository deliveryLogRepository,
        PushDeviceTokenRepository pushDeviceTokenRepository,
        FirebasePushService firebasePushService
    ) {
        this.adminNotificationService = adminNotificationService;
        this.notificationOutboxRepository = notificationOutboxRepository;
        this.notificationOutboxScheduler = notificationOutboxScheduler;
        this.deliveryLogRepository = deliveryLogRepository;
        this.pushDeviceTokenRepository = pushDeviceTokenRepository;
        this.firebasePushService = firebasePushService;
    }

    @GetMapping("")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ManagementNotificationDTO>> list(@RequestParam(required=false) NotificationOutboxStatus status,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Page<NotificationOutbox> page=status==null?notificationOutboxRepository.findAll(pageable):notificationOutboxRepository.findAll((root,q,cb)->cb.equal(root.get("status"),status),pageable);
        return ResponseEntity.ok(page.stream().map(this::toManagementDto).toList());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ManagementNotificationDTO> detail(@PathVariable Long id) {
        return notificationOutboxRepository.findById(id).map(this::toManagementDto).map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<Void> retry(@PathVariable Long id) {
        NotificationOutbox outbox=notificationOutboxRepository.findById(id).orElseThrow();
        if (outbox.getStatus()==NotificationOutboxStatus.FAILED || outbox.getStatus()==NotificationOutboxStatus.PARTIAL) {
            outbox.setStatus(NotificationOutboxStatus.PENDING); outbox.setNextAttemptAt(Instant.now()); outbox.setLastError(null); notificationOutboxRepository.save(outbox);
        }
        notificationOutboxScheduler.processSingleOutbox(id,Instant.now());
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/integration-status")
    public Map<String,Object> integrationStatus() {
        Map<String, Object> status = new java.util.LinkedHashMap<>();
        status.put("firebaseEnabled", firebasePushService.isEnabled());
        status.put("firebaseInitialized", firebasePushService.isAvailable());
        status.put("firebaseConnected", firebasePushService.isAvailable());
        status.put("fcmReady", firebasePushService.isAvailable());
        status.put("projectId", firebasePushService.getProjectId());
        status.put("activePushTokens", pushDeviceTokenRepository.countByActiveTrue());
        status.put("androidTokens", pushDeviceTokenRepository.countByActiveTrueAndPlatform(PushPlatform.ANDROID));
        status.put("iosTokens", pushDeviceTokenRepository.countByActiveTrueAndPlatform(PushPlatform.IOS));
        status.put("bigQueryConfigured", false);
        status.put("bigQueryMessage", "BigQuery bağlantısı yapılandırılmamış.");
        return status;
    }

    @PostMapping("/send")
    public ResponseEntity<AdminNotificationSendSummary> sendManualNotification(
        @Valid @RequestBody AdminNotificationSendRequest request
    ) {
        LOG.info("Admin notification requested for target type {}", request.targetType());
        return ResponseEntity.accepted().body(adminNotificationService.send(request));
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
        stats.put("partial", notificationOutboxRepository.countByStatus(NotificationOutboxStatus.PARTIAL));
        stats.put("activeDevices", pushDeviceTokenRepository.countByActiveTrue());
        return ResponseEntity.ok(stats);
    }

    private ManagementNotificationDTO toManagementDto(NotificationOutbox outbox) {
        var logs=deliveryLogRepository.findByNotification_Id(outbox.getNotification().getId());
        long success=logs.stream().filter(l->l.getStatus()==com.bialem.backend.domain.enumeration.NotificationDeliveryStatus.SENT||l.getStatus()==com.bialem.backend.domain.enumeration.NotificationDeliveryStatus.DELIVERED).count();
        long failed=logs.stream().filter(l->l.getStatus()==com.bialem.backend.domain.enumeration.NotificationDeliveryStatus.FAILED).count();
        String messageId=logs.stream().map(l->l.getProviderMessageId()).filter(Objects::nonNull).findFirst().orElse(null);
        Map<String,Long> errors=logs.stream().map(l->l.getErrorCode()).filter(Objects::nonNull).collect(java.util.stream.Collectors.groupingBy(v->v,java.util.stream.Collectors.counting()));
        var n=outbox.getNotification();
        return new ManagementNotificationDTO(outbox.getId(),n.getId(),n.getTitle(),n.getBody(),n.getNotificationType(),"BIALEM",n.getPayload(),n.getReferenceType(),n.getReferenceId(),outbox.getUser().getId(),outbox.getStatus().name(),messageId,success,failed,outbox.getAttemptCount(),errors,outbox.getCreatedAt(),outbox.getSentAt(),outbox.getLastError());
    }

    public record ManagementNotificationDTO(Long id,Long notificationId,String title,String body,String notificationType,String source,String trigger,String referenceType,String referenceId,Long recipientUserId,String firebaseStatus,String firebaseMessageId,long pushSuccessful,long pushFailed,Integer attemptCount,Map<String,Long> firebaseErrors,Instant createdAt,Instant sentAt,String lastError) {}

}
