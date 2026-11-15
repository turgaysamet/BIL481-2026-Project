package com.campusbridge.matching.dto;

import com.campusbridge.auth.UserType;
import com.campusbridge.profile.InterestCategory;

public record MatchPreferencesDto(boolean sameGenderOnly, String targetLanguage,
                                  InterestCategory interestCategory, UserType userType) {
}
