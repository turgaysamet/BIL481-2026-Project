package com.campusbridge.event;

import com.campusbridge.common.CurrentUserProvider;
import com.campusbridge.event.dto.CreateEventRequest;
import com.campusbridge.event.dto.EventResponse;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST endpoints of UC-4 "Create and join an event". */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final CurrentUserProvider currentUser;

    public EventController(EventService eventService, CurrentUserProvider currentUser) {
        this.eventService = eventService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse create(@Valid @RequestBody CreateEventRequest request) {
        return eventService.create(currentUser.currentUserId(), request);
    }

    @GetMapping
    public List<EventResponse> search(@RequestParam(required = false) EventCategory category,
                                      @RequestParam(required = false) Instant from,
                                      @RequestParam(required = false) Instant to,
                                      @RequestParam(required = false) String language,
                                      @RequestParam(required = false) String q) {
        return eventService.search(currentUser.currentUserId(), category, from, to, language, q);
    }

    @GetMapping("/{eventId}")
    public EventResponse get(@PathVariable Long eventId) {
        return eventService.get(currentUser.currentUserId(), eventId);
    }

    @PostMapping("/{eventId}/participants")
    public EventResponse join(@PathVariable Long eventId) {
        return eventService.join(currentUser.currentUserId(), eventId);
    }

    @DeleteMapping("/{eventId}/participants/me")
    public EventResponse leave(@PathVariable Long eventId) {
        return eventService.leave(currentUser.currentUserId(), eventId);
    }

    @PostMapping("/{eventId}/cancel")
    public EventResponse cancel(@PathVariable Long eventId) {
        return eventService.cancel(currentUser.currentUserId(), eventId);
    }
}
