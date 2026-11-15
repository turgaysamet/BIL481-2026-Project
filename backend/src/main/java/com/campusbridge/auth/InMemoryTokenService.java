package com.campusbridge.auth;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/** Simple opaque-token implementation used during early development and tests. */
@Component
public class InMemoryTokenService implements TokenService {

    private final Map<String, Long> tokens = new ConcurrentHashMap<>();

    @Override
    public String issueToken(User user) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, user.getId());
        return token;
    }

    @Override
    public Optional<Long> resolveUserId(String token) {
        return Optional.ofNullable(tokens.get(token));
    }
}
