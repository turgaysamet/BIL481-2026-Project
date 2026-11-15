package com.campusbridge.profile;

import com.campusbridge.auth.User;
import com.campusbridge.auth.UserRepository;
import com.campusbridge.common.BusinessRuleException;
import com.campusbridge.common.NotFoundException;
import com.campusbridge.profile.dto.InterestDto;
import com.campusbridge.profile.dto.ProfileRequest;
import com.campusbridge.profile.dto.ProfileResponse;
import com.campusbridge.profile.dto.SpokenLanguageDto;
import java.time.Clock;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business logic of UC-2 "Create and edit profile". */
@Service
public class ProfileService {

    private final ProfileRepository profiles;
    private final InterestRepository interests;
    private final UserRepository users;
    private final Clock clock;

    public ProfileService(ProfileRepository profiles, InterestRepository interests, UserRepository users, Clock clock) {
        this.profiles = profiles;
        this.interests = interests;
        this.users = users;
        this.clock = clock;
    }

    @Transactional
    public ProfileResponse saveOwnProfile(Long userId, ProfileRequest request) {
        Set<Interest> selected = new HashSet<>(interests.findAllById(request.interestIds()));
        if (selected.size() < Profile.MIN_INTERESTS) {
            throw new BusinessRuleException("PROFILE_INVALID", "Select at least 3 interests from the catalogue");
        }
        Set<SpokenLanguage> spoken = request.spokenLanguages().stream()
                .map(l -> new SpokenLanguage(l.languageCode(), l.level()))
                .collect(Collectors.toSet());

        Profile profile = profiles.findByUserId(userId).orElseGet(() -> new Profile(findUser(userId)));
        profile.update(request.displayName(), request.homeUniversity(), request.homeCountry(), request.gender(),
                request.bio(), selected, nullSafe(request.skills()), spoken,
                nullSafe(request.learningLanguages()), clock.instant());
        return toResponse(profiles.save(profile), true);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getOwnProfile(Long userId) {
        return toResponse(findProfile(userId), true);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getPublicProfile(Long userId) {
        return toResponse(findProfile(userId), false);
    }

    @Transactional(readOnly = true)
    public List<InterestDto> getInterestCatalogue() {
        return interests.findAll().stream()
                .sorted(Comparator.comparing(Interest::getName))
                .map(i -> new InterestDto(i.getId(), i.getName(), i.getCategory()))
                .toList();
    }

    Profile findProfile(Long userId) {
        return profiles.findByUserId(userId).orElseThrow(() -> new NotFoundException("Profile not found"));
    }

    private User findUser(Long userId) {
        return users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private static Set<String> nullSafe(Set<String> values) {
        return values == null ? Set.of() : values;
    }

    /** Maps an entity to the API DTO; owner=false hides private fields. */
    public static ProfileResponse toResponse(Profile p, boolean owner) {
        return new ProfileResponse(
                p.getUser().getId(), p.getDisplayName(), p.getUser().getUserType(),
                p.getHomeUniversity(), p.getHomeCountry(), owner ? p.getGender() : null,
                p.getBio(), p.getPhotoUrl(),
                p.getSpokenLanguages().stream().map(l -> new SpokenLanguageDto(l.getLanguageCode(), l.getLevel())).toList(),
                p.getLearningLanguages(),
                p.getInterests().stream().map(i -> new InterestDto(i.getId(), i.getName(), i.getCategory())).toList(),
                p.getSkills(), p.isCompleted());
    }
}
