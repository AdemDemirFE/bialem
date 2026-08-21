package com.bialem.backend.notification;

import com.bialem.backend.domain.enumeration.NotificationEventType;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Domain event published after a business transaction succeeds.
 */
public class NotificationEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final NotificationEventType type;
    private final String idempotencyKey;
    private final Map<String, Object> variables;

    public NotificationEvent(NotificationEventType type, String idempotencyKey, Map<String, Object> variables) {
        this.type = Objects.requireNonNull(type, "type is required");
        this.idempotencyKey = Objects.requireNonNull(idempotencyKey, "idempotencyKey is required");
        this.variables = variables != null ? new HashMap<>(variables) : new HashMap<>();
    }

    public NotificationEventType getType() {
        return type;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Map<String, Object> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public Object getVariable(String key) {
        return variables.get(key);
    }

    public String getStringVariable(String key) {
        Object value = variables.get(key);
        return value != null ? value.toString() : null;
    }
}
