package com.campusbridge.auth.dto;

import com.campusbridge.auth.UserType;

public record AuthResponse(String token, Long userId, UserType userType) {
}
