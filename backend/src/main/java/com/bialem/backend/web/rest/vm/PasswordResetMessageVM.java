package com.bialem.backend.web.rest.vm;

/**
 * Uniform response for password-reset init (anti-enumeration).
 */
public class PasswordResetMessageVM {

    public static final String GENERIC_MESSAGE =
        "Eğer bu e-posta adresi sistemimizde kayıtlıysa şifre sıfırlama kodu gönderildi.";

    private final boolean success;
    private final String message;

    public PasswordResetMessageVM(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static PasswordResetMessageVM accepted() {
        return new PasswordResetMessageVM(true, GENERIC_MESSAGE);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
