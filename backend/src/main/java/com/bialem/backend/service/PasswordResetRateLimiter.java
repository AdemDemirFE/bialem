package com.bialem.backend.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * In-memory rate limits for password-reset init requests (email cooldown + IP window).
 */
@Service
public class PasswordResetRateLimiter {

    private static final Duration EMAIL_COOLDOWN = Duration.ofSeconds(60);
    private static final Duration IP_WINDOW = Duration.ofMinutes(15);
    private static final int MAX_REQUESTS_PER_IP = 10;

    private final ConcurrentHashMap<String, Instant> emailLastRequest = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Deque<Instant>> ipRequests = new ConcurrentHashMap<>();

    public void checkAllowed(String email, String clientIp) {
        Instant now = Instant.now();
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        String ip = (clientIp == null || clientIp.isBlank()) ? "unknown" : clientIp.trim();

        if (!normalizedEmail.isEmpty()) {
            Instant last = emailLastRequest.get(normalizedEmail);
            if (last != null && last.plus(EMAIL_COOLDOWN).isAfter(now)) {
                throw new PasswordResetRateLimitException();
            }
        }

        Deque<Instant> timestamps = ipRequests.computeIfAbsent(ip, key -> new ArrayDeque<>());
        synchronized (timestamps) {
            prune(timestamps, now.minus(IP_WINDOW));
            if (timestamps.size() >= MAX_REQUESTS_PER_IP) {
                throw new PasswordResetRateLimitException();
            }
            timestamps.addLast(now);
        }

        if (!normalizedEmail.isEmpty()) {
            emailLastRequest.put(normalizedEmail, now);
        }
    }

    private static void prune(Deque<Instant> timestamps, Instant cutoff) {
        Iterator<Instant> iterator = timestamps.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isBefore(cutoff)) {
                iterator.remove();
            } else {
                break;
            }
        }
    }
}
