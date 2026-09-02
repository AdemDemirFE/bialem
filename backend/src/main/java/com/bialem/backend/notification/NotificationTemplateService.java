package com.bialem.backend.notification;

import com.bialem.backend.domain.NotificationTemplate;
import com.bialem.backend.domain.enumeration.NotificationEventType;
import com.bialem.backend.repository.NotificationTemplateRepository;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NotificationTemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationTemplateService.class);

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([a-zA-Z0-9_]+)\\}\\}");

    private final NotificationTemplateRepository notificationTemplateRepository;

    public NotificationTemplateService(NotificationTemplateRepository notificationTemplateRepository) {
        this.notificationTemplateRepository = notificationTemplateRepository;
    }

    public Optional<NotificationTemplate> findByEventType(NotificationEventType eventType) {
        return notificationTemplateRepository.findByEventType(eventType);
    }

    public Optional<NotificationTemplate> findByCode(String code) {
        return notificationTemplateRepository.findByCode(code);
    }

    public RenderedNotification render(NotificationTemplate template, Map<String, Object> variables) {
        String title = replacePlaceholders(template.getTitleTemplate(), variables);
        String body = replacePlaceholders(template.getBodyTemplate(), variables);
        String route = replacePlaceholders(template.getRouteTemplate(), variables);
        return new RenderedNotification(title, body, route);
    }

    public Set<String> extractPlaceholders(String template) {
        if (template == null) {
            return Set.of();
        }
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        return matcher.results().map(result -> result.group(1)).collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    private String replacePlaceholders(String template, Map<String, Object> variables) {
        if (template == null) {
            return null;
        }
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = variables.get(key);
            String replacement = value != null ? Matcher.quoteReplacement(value.toString()) : matcher.group(0);
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public record RenderedNotification(String title, String body, String route) {}
}
