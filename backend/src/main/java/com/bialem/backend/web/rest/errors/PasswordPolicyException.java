package com.bialem.backend.web.rest.errors;

import com.bialem.backend.security.PasswordPolicy;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

@SuppressWarnings("java:S110")
public class PasswordPolicyException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    public PasswordPolicyException() {
        super(
            HttpStatus.BAD_REQUEST,
            ProblemDetailWithCauseBuilder.instance()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorConstants.INVALID_PASSWORD_TYPE)
                .withTitle(PasswordPolicy.requirementMessage())
                .withProperty("message", "error.passwordpolicy")
                .build(),
            null
        );
    }
}
