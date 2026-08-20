package com.bialem.backend.service;

public class PasswordResetRateLimitException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PasswordResetRateLimitException() {
        super("Too many password reset requests");
    }
}
