package com.campusbridge.matching;

import com.campusbridge.matching.core.CandidateProfile;
import com.campusbridge.profile.CefrLevel;
import com.campusbridge.profile.Interest;
import com.campusbridge.profile.Profile;
import com.campusbridge.profile.SpokenLanguage;
import java.util.Map;
import java.util.stream.Collectors;

/** Converts the JPA {@link Profile} entity into the framework-free {@link CandidateProfile}. */
final class CandidateProfileMapper {

    private CandidateProfileMapper() {
    }

    static CandidateProfile toCandidate(Profile p) {
        Map<String, CefrLevel> spoken = p.getSpokenLanguages().stream()
                .collect(Collectors.toMap(SpokenLanguage::getLanguageCode, SpokenLanguage::getLevel, (x, y) -> x));
        return new CandidateProfile(
                p.getUser().getId(),
                p.getUser().getUserType(),
                p.getGender(),
                p.getInterests().stream().map(Interest::getId).collect(Collectors.toSet()),
                p.getInterests().stream().map(Interest::getCategory).collect(Collectors.toSet()),
                p.getSkills(),
                spoken,
                p.getLearningLanguages());
    }
}
