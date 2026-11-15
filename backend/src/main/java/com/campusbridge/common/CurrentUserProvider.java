package com.campusbridge.common;

/**
 * Returns the id of the authenticated user of the current request.
 * Implemented by the security layer (token filter); mocked in tests.
 */
public interface CurrentUserProvider {

    Long currentUserId();
}
