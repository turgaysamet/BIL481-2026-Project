package com.campusbridge.matching.core;

import com.campusbridge.auth.UserType;
import com.campusbridge.profile.CefrLevel;
import com.campusbridge.profile.Gender;
import com.campusbridge.profile.InterestCategory;
import java.util.Map;
import java.util.Set;

/** Small builder to create CandidateProfile objects in tests. */
final class TestProfiles {

    private TestProfiles() {
    }

    static CandidateProfile profile(long id, UserType type, Gender gender, Set<Long> interests,
                                    Set<String> skills, Map<String, CefrLevel> spoken, Set<String> learning) {
        return new CandidateProfile(id, type, gender, interests, Set.of(InterestCategory.SPORT), skills, spoken, learning);
    }

    static CandidateProfile withCategories(long id, Set<InterestCategory> categories) {
        return new CandidateProfile(id, UserType.LOCAL, Gender.FEMALE, Set.of(1L), categories, Set.of(),
                Map.of("tr", CefrLevel.C2), Set.of());
    }
}
