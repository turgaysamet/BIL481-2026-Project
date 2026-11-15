package com.campusbridge.profile.dto;

import com.campusbridge.profile.InterestCategory;

public record InterestDto(Long id, String name, InterestCategory category) {
}
