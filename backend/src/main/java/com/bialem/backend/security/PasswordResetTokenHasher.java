package com.bialem.backend.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Generates password-reset secrets and stores only SHA-256 hashes in the database.
 */
public final class PasswordResetTokenHasher {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;
    /** 8-digit numeric code for in-app entry (also usable as email link key). */
    private static final int RESET_CODE_DIGITS = 8;

    private PasswordResetTokenHasher() {}

    public static String generateRawToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Human-enterable reset code (8 digits). Stored hashed; sent clear in email only.
     */
    public static String generateResetCode() {
        int bound = (int) Math.pow(10, RESET_CODE_DIGITS);
        int value = SECURE_RANDOM.nextInt(bound);
        return String.format("%0" + RESET_CODE_DIGITS + "d", value);
    }

    public static String normalizeResetSecret(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().replace(" ", "");
    }

    public static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(normalizeResetSecret(rawToken).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
