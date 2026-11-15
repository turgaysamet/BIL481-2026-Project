package com.campusbridge.auth;

import java.util.Optional;

/** Issues and resolves access tokens. Replaced by a JWT implementation in Sprint 1 (task 3.3). */
public interface TokenService {

    String issueToken(User user);

    Optional<Long> resolveUserId(String token);
}
