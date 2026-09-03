package com.bialem.backend.config;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class SecurityJwtConfigurationTest {

    @Test
    void acceptsValidSecret() {
        assertThatCode(() -> SecurityJwtConfiguration.assertValidJwtSecret("ODQ2MThkMjk3ZjJkMzFiYTBjZmU5ZjNmZDAxMTNjYzVjMjg1ZjI2NTRmYjgyNWE2NGU4MzcyOTJlNmUyNmQ2NzU4ZWEwOWU5ZWRkNDNkZGQ1MWYwMmMzZDQ5MDY2NzU3N2ZkMzE4MGM1ZmVjNzU4M2M5M2Y2MDkxZjgwNWVkNjI="))
            .doesNotThrowAnyException();
    }

    @Test
    void rejectsBlankSecret() {
        assertThatThrownBy(() -> SecurityJwtConfiguration.assertValidJwtSecret(null)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> SecurityJwtConfiguration.assertValidJwtSecret("")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> SecurityJwtConfiguration.assertValidJwtSecret("   ")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void rejectsShortSecret() {
        // "aGVsbG8=" decodes to 5 bytes — far below the 32-byte (256-bit) requirement.
        assertThatThrownBy(() -> SecurityJwtConfiguration.assertValidJwtSecret("aGVsbG8=")).isInstanceOf(IllegalStateException.class);
    }
}