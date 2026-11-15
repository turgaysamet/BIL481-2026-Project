package com.campusbridge.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class UserTest {

    private static final Instant NOW = Instant.parse("2026-11-15T10:00:00Z");

    private User newUser() {
        User user = new User("Test.Local@etu.edu.tr", "hash", UserType.LOCAL, NOW);
        user.startVerification("token-123", NOW.plusSeconds(24 * 3600));
        return user;
    }

    @Test
    void accountIsActiveOnlyAfterVerificationWithCorrectToken() {
        User user = newUser();

        assertThat(user.getStatus()).isEqualTo(AccountStatus.PENDING_VERIFICATION);
        assertThat(user.verify("wrong", NOW)).isFalse();
        assertThat(user.verify("token-123", NOW.plusSeconds(60))).isTrue();
        assertThat(user.isActive()).isTrue();
        assertThat(user.getEmail()).isEqualTo("test.local@etu.edu.tr");
    }

    @Test
    void verificationLinkExpiresAfter24Hours() {
        User user = newUser();

        assertThat(user.verify("token-123", NOW.plusSeconds(24 * 3600 + 1))).isFalse();
        assertThat(user.isActive()).isFalse();
    }

    @Test
    void fiveFailedLoginsLockTheAccountFor15Minutes() {
        User user = newUser();
        for (int i = 0; i < 4; i++) {
            user.registerFailedLogin(NOW);
        }
        assertThat(user.isLocked(NOW)).isFalse();

        user.registerFailedLogin(NOW); // 5th attempt

        assertThat(user.isLocked(NOW.plusSeconds(14 * 60))).isTrue();
        assertThat(user.isLocked(NOW.plusSeconds(15 * 60 + 1))).isFalse();
    }
}
