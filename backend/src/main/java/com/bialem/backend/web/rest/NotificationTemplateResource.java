package com.bialem.backend.web.rest;

import com.bialem.backend.domain.NotificationTemplate;
import com.bialem.backend.domain.enumeration.NotificationEventType;
import com.bialem.backend.notification.NotificationTemplateService;
import com.bialem.backend.repository.NotificationTemplateRepository;
import com.bialem.backend.security.SecurityUtils;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/notification-templates")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class NotificationTemplateResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationTemplateResource.class);

    private final NotificationTemplateRepository notificationTemplateRepository;

    private final NotificationTemplateService notificationTemplateService;

    public NotificationTemplateResource(
        NotificationTemplateRepository notificationTemplateRepository,
        NotificationTemplateService notificationTemplateService
    ) {
        this.notificationTemplateRepository = notificationTemplateRepository;
        this.notificationTemplateService = notificationTemplateService;
    }

    @GetMapping("")
    public List<NotificationTemplate> getAllTemplates() {
        LOG.debug("REST request to get all notification templates");
        return notificationTemplateRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationTemplate> getTemplate(@PathVariable Long id) {
        LOG.debug("REST request to get notification template {}", id);
        Optional<NotificationTemplate> template = notificationTemplateRepository.findById(id);
        return ResponseEntity.of(template);
    }

    @GetMapping("/event-type/{eventType}")
    public ResponseEntity<NotificationTemplate> getTemplateByEventType(@PathVariable NotificationEventType eventType) {
        LOG.debug("REST request to get notification template by event type {}", eventType);
        Optional<NotificationTemplate> template = notificationTemplateRepository.findByEventType(eventType);
        return ResponseEntity.of(template);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationTemplate> updateTemplate(@PathVariable Long id, @Valid @RequestBody NotificationTemplate template) {
        LOG.debug("REST request to update notification template {}", id);
        if (!notificationTemplateRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        template.setId(id);
        template.setUpdatedAt(Instant.now());
        template.setUpdatedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
        NotificationTemplate result = notificationTemplateRepository.save(template);
        return ResponseEntity.ok(result);
    }

    @PostMapping("")
    public ResponseEntity<NotificationTemplate> createTemplate(@Valid @RequestBody NotificationTemplate template) throws URISyntaxException {
        LOG.debug("REST request to create notification template");
        template.setId(null);
        template.setCreatedAt(Instant.now());
        template.setCreatedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
        NotificationTemplate result = notificationTemplateRepository.save(template);
        return ResponseEntity.created(new URI("/api/admin/notification-templates/" + result.getId())).body(result);
    }

    @PostMapping("/{id}/preview")
    public ResponseEntity<NotificationTemplateService.RenderedNotification> previewTemplate(
        @PathVariable Long id,
        @RequestBody java.util.Map<String, Object> variables
    ) {
        LOG.debug("REST request to preview notification template {}", id);
        Optional<NotificationTemplate> template = notificationTemplateRepository.findById(id);
        if (template.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(notificationTemplateService.render(template.orElseThrow(), variables));
    }
}
