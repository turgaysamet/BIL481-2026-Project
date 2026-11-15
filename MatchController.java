package com.campusbridge.matching;

import com.campusbridge.auth.UserType;
import com.campusbridge.common.CurrentUserProvider;
import com.campusbridge.matching.core.MatchFilters;
import com.campusbridge.matching.dto.MatchPreferencesDto;
import com.campusbridge.matching.dto.MatchRequestCreate;
import com.campusbridge.matching.dto.MatchRequestResponse;
import com.campusbridge.matching.dto.SuggestionResponse;
import com.campusbridge.profile.InterestCategory;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST endpoints of UC-3 "Get match suggestions and send a match request". */
@RestController
@RequestMapping("/api")
public class MatchController {

    private final MatchingService matchingService;
    private final CurrentUserProvider currentUser;

    public MatchController(MatchingService matchingService, CurrentUserProvider currentUser) {
        this.matchingService = matchingService;
        this.currentUser = currentUser;
    }

    /** Without query parameters the saved preferences are used; with "useFilters=true" the given ones. */
    @GetMapping("/matches/suggestions")
    public List<SuggestionResponse> getSuggestions(
            @RequestParam(defaultValue = "false") boolean useFilters,
            @RequestParam(defaultValue = "false") boolean sameGenderOnly,
            @RequestParam(required = false) String targetLanguage,
            @RequestParam(required = false) InterestCategory interestCategory,
            @RequestParam(required = false) UserType userType) {
        MatchFilters filters = useFilters ? new MatchFilters(sameGenderOnly, targetLanguage, interestCategory, userType) : null;
        return matchingService.getSuggestions(currentUser.currentUserId(), filters);
    }

    @PutMapping("/matches/preferences")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void savePreferences(@RequestBody MatchPreferencesDto preferences) {
        matchingService.savePreferences(currentUser.currentUserId(), preferences);
    }

    @GetMapping("/matches")
    public List<MatchRequestResponse> getMatches() {
        return matchingService.getMatches(currentUser.currentUserId());
    }

    @DeleteMapping("/matches/{requestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMatch(@PathVariable Long requestId) {
        matchingService.removeMatch(requestId, currentUser.currentUserId());
    }

    @PostMapping("/match-requests")
    @ResponseStatus(HttpStatus.CREATED)
    public MatchRequestResponse sendRequest(@Valid @RequestBody MatchRequestCreate body) {
        return matchingService.sendRequest(currentUser.currentUserId(), body.receiverId());
    }

    @GetMapping("/match-requests/incoming")
    public List<MatchRequestResponse> getIncomingRequests() {
        return matchingService.getIncomingRequests(currentUser.currentUserId());
    }

    @PostMapping("/match-requests/{requestId}/accept")
    public MatchRequestResponse accept(@PathVariable Long requestId) {
        return matchingService.respond(requestId, currentUser.currentUserId(), true);
    }

    @PostMapping("/match-requests/{requestId}/decline")
    public MatchRequestResponse decline(@PathVariable Long requestId) {
        return matchingService.respond(requestId, currentUser.currentUserId(), false);
    }
}
