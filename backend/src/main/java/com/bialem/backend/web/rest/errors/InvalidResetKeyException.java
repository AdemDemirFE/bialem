package com.bialem.backend.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

@SuppressWarnings("java:S110")
public class InvalidResetKeyException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    public InvalidResetKeyException() {
        super(
            HttpStatus.BAD_REQUEST,
            ProblemDetailWithCauseBuilder.instance()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorConstants.INVALID_RESET_KEY_TYPE)
                .withTitle("Şifre sıfırlama kodunun süresi dolmuş veya kod geçersiz.")
                .withProperty("message", "error.invalidresetkey")
                .build(),
            null
        );
    }
}
