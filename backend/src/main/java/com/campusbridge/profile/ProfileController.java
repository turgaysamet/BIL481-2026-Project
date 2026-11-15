package com.campusbridge.profile;

import com.campusbridge.common.CurrentUserProvider;
import com.campusbridge.profile.dto.InterestDto;
import com.campusbridge.profile.dto.ProfileRequest;
import com.campusbridge.profile.dto.ProfileResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST endpoints of UC-2 "Create and edit profile". */
@RestController
@RequestMapping("/api")
public class ProfileController {

    private final ProfileService profileService;
    private final CurrentUserProvider currentUser;

    public ProfileController(ProfileService profileService, CurrentUserProvider currentUser) {
        this.profileService = profileService;
        this.currentUser = currentUser;
    }

    @GetMapping("/profiles/me")
    public ProfileResponse getOwnProfile() {
        return profileService.getOwnProfile(currentUser.currentUserId());
    }

    @PutMapping("/profiles/me")
    public ProfileResponse saveOwnProfile(@Valid @RequestBody ProfileRequest request) {
        return profileService.saveOwnProfile(currentUser.currentUserId(), request);
    }

    @GetMapping("/profiles/{userId}")
    public ProfileResponse getPublicProfile(@PathVariable Long userId) {
        return profileService.getPublicProfile(userId);
    }

    @GetMapping("/interests")
    public List<InterestDto> getInterestCatalogue() {
        return profileService.getInterestCatalogue();
    }
}
