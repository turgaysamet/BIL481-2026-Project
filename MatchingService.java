package com.campusbridge.matching;

import com.campusbridge.auth.AccountStatus;
import com.campusbridge.common.BusinessRuleException;
import com.campusbridge.common.NotFoundException;
import com.campusbridge.common.NotificationPublisher;
import com.campusbridge.matching.core.CandidateProfile;
import com.campusbridge.matching.core.MatchFilters;
import com.campusbridge.matching.core.MatchSuggestion;
import com.campusbridge.matching.core.MatchSuggestionEngine;
import com.campusbridge.matching.dto.MatchPreferencesDto;
import com.campusbridge.matching.dto.MatchRequestResponse;
import com.campusbridge.matching.dto.SuggestionResponse;
import com.campusbridge.profile.MatchPreferences;
import com.campusbridge.profile.Profile;
import com.campusbridge.profile.ProfileRepository;
import com.campusbridge.profile.ProfileService;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business logic of UC-3 "Get match suggestions and send a match request". */
@Service
public class MatchingService {

    static final Duration DECLINE_COOLDOWN = Duration.ofDays(30);

    private final ProfileRepository profiles;
    private final MatchRequestRepository requests;
    private final MatchSuggestionEngine engine;
    private final NotificationPublisher notifications;
    private final Clock clock;

    public MatchingService(ProfileRepository profiles, MatchRequestRepository requests,
                           MatchSuggestionEngine engine, NotificationPublisher notifications, Clock clock) {
        this.profiles = profiles;
        this.requests = requests;
        this.engine = engine;
        this.notifications = notifications;
        this.clock = clock;
    }

    /** FR-06 / FR-07. When filters is null, the saved preferences of the user are used. */
    @Transactional(readOnly = true)
    public List<SuggestionResponse> getSuggestions(Long userId, MatchFilters filters) {
        Profile own = requireCompleteProfile(userId);
        MatchFilters effective = filters != null ? filters : fromPreferences(own.getMatchPreferences());

        List<Profile> candidateProfiles = profiles.findMatchCandidates(userId, AccountStatus.ACTIVE);
        Map<Long, Profile> byUserId = candidateProfiles.stream()
                .collect(Collectors.toMap(p -> p.getUser().getId(), Function.identity()));
        List<CandidateProfile> candidates = candidateProfiles.stream().map(CandidateProfileMapper::toCandidate).toList();

        List<MatchSuggestion> ranked = engine.suggest(CandidateProfileMapper.toCandidate(own), candidates,
                excludedUsers(userId), effective, MatchSuggestionEngine.DEFAULT_LIMIT);

        return ranked.stream()
                .map(s -> new SuggestionResponse(ProfileService.toResponse(byUserId.get(s.userId()), false), s.score()))
                .toList();
    }

    @Transactional
    public void savePreferences(Long userId, MatchPreferencesDto dto) {
        Profile own = requireCompleteProfile(userId);
        own.setMatchPreferences(new MatchPreferences(dto.sameGenderOnly(), dto.targetLanguage(),
                dto.interestCategory(), dto.userType()));
    }

    /** FR-08: send a request to a suggested user. */
    @Transactional
    public MatchRequestResponse sendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new BusinessRuleException("INVALID_REQUEST", "You cannot send a request to yourself");
        }
        Profile sender = requireCompleteProfile(senderId);
        Profile receiver = profiles.findByUserId(receiverId)
                .filter(p -> p.isCompleted() && p.getUser().isActive())
                .orElseThrow(() -> new NotFoundException("User not available for matching"));
        if (excludedUsers(senderId).contains(receiverId)) {
            throw new BusinessRuleException("REQUEST_NOT_ALLOWED",
                    "There is already a request or match with this user, or they declined you less than 30 days ago");
        }
        MatchRequest request = requests.save(new MatchRequest(sender.getUser(), receiver.getUser(), clock.instant()));
        notifications.notifyUser(receiverId, "MATCH_REQUEST", sender.getDisplayName() + " wants to connect with you");
        return toResponse(request);
    }

    @Transactional
    public MatchRequestResponse respond(Long requestId, Long userId, boolean accept) {
        MatchRequest request = requests.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found"));
        Instant now = clock.instant();
        if (accept) {
            request.accept(userId, now);
            notifications.notifyUser(request.getSender().getId(), "MATCH_ACCEPTED", "Your match request was accepted");
        } else {
            request.decline(userId, now);
        }
        return toResponse(request);
    }

    @Transactional(readOnly = true)
    public List<MatchRequestResponse> getIncomingRequests(Long userId) {
        return requests.findByReceiverIdAndStatus(userId, MatchRequestStatus.PENDING).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<MatchRequestResponse> getMatches(Long userId) {
        return requests.findByUserAndStatusIn(userId, EnumSet.of(MatchRequestStatus.ACCEPTED)).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public void removeMatch(Long requestId, Long userId) {
        MatchRequest request = requests.findById(requestId).orElseThrow(() -> new NotFoundException("Match not found"));
        request.remove(userId, clock.instant());
    }

    /** Users that must not be suggested or asked again. */
    Set<Long> excludedUsers(Long userId) {
        Set<Long> excluded = new HashSet<>();
        for (MatchRequest r : requests.findByUserAndStatusIn(userId,
                EnumSet.of(MatchRequestStatus.PENDING, MatchRequestStatus.ACCEPTED))) {
            excluded.add(r.otherUserId(userId));
        }
        excluded.addAll(requests.findReceiversWhoDeclinedSince(userId, MatchRequestStatus.DECLINED,
                clock.instant().minus(DECLINE_COOLDOWN)));
        return excluded;
    }

    private Profile requireCompleteProfile(Long userId) {
        Profile profile = profiles.findByUserId(userId)
                .orElseThrow(() -> new BusinessRuleException("PROFILE_INCOMPLETE", "Create your profile first"));
        if (!profile.isCompleted()) {
            throw new BusinessRuleException("PROFILE_INCOMPLETE", "Complete your profile to receive matches");
        }
        return profile;
    }

    private static MatchFilters fromPreferences(MatchPreferences p) {
        return p == null ? MatchFilters.none()
                : new MatchFilters(p.isSameGenderOnly(), p.getTargetLanguage(), p.getInterestCategory(), p.getUserType());
    }

    private MatchRequestResponse toResponse(MatchRequest r) {
        return new MatchRequestResponse(r.getId(), r.getSender().getId(), r.getReceiver().getId(), r.getStatus(),
                r.getCreatedAt(), r.getRespondedAt());
    }
}
