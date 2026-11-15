package com.campusbridge.matching.core;

import com.campusbridge.auth.UserType;
import com.campusbridge.profile.CefrLevel;
import com.campusbridge.profile.Gender;
import com.campusbridge.profile.InterestCategory;
import java.util.Map;
import java.util.Set;

/**
 * Read-only view of a profile with only the data the matching algorithm needs.
 * The core package has no Spring or JPA dependency, so it can be unit-tested in isolation.
 */
public record CandidateProfile(
        long userId,
        UserType userType,
        Gender gender,
        Set<Long> interestIds,
        Set<InterestCategory> interestCategories,
        Set<String> skills,
        Map<String, CefrLevel> spokenLanguages,
        Set<String> learningLanguages) {
}
