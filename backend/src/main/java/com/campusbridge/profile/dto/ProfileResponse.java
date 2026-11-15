package com.campusbridge.profile.dto;

import com.campusbridge.auth.UserType;
import com.campusbridge.profile.Gender;
import java.util.List;
import java.util.Set;

/**
 * Profile as returned by the API. gender is filled only when the viewer is the owner;
 * the e-mail address is never part of this DTO (FR-05, NFR-03).
 */
public record ProfileResponse(
        Long userId,
        String displayName,
        UserType userType,
        String homeUniversity,
        String homeCountry,
        Gender gender,
        String bio,
        String photoUrl,
        List<SpokenLanguageDto> spokenLanguages,
        Set<String> learningLanguages,
        List<InterestDto> interests,
        Set<String> skills,
        boolean completed) {
}
