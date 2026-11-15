package com.campusbridge.auth;

import com.campusbridge.common.BusinessRuleException;
import com.campusbridge.common.CurrentUserProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Reads the "Authorization: Bearer &lt;token&gt;" header of the current HTTP request
 * and resolves the user id through the {@link TokenService}.
 */
@Component
public class TokenCurrentUserProvider implements CurrentUserProvider {

    private final TokenService tokenService;

    public TokenCurrentUserProvider(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Long currentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String header = attributes == null ? null : attributes.getRequest().getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BusinessRuleException("UNAUTHENTICATED", "Login required");
        }
        return tokenService.resolveUserId(header.substring(7))
                .orElseThrow(() -> new BusinessRuleException("UNAUTHENTICATED", "Session expired"));
    }
}
