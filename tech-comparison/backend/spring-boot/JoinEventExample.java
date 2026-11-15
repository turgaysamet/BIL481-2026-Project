// Spring Boot (Java) version of "join an event" — simplified from
// backend/src/main/java/com/campusbridge/event/{EventController,EventService,Event}.java

@RestController
@RequestMapping("/api/events")
class EventController {
    private final EventService eventService;
    private final CurrentUserProvider currentUser;

    EventController(EventService eventService, CurrentUserProvider currentUser) { // constructor injection
        this.eventService = eventService;
        this.currentUser = currentUser;
    }

    @PostMapping("/{eventId}/participants")
    EventResponse join(@PathVariable Long eventId) {          // path variable converted to Long automatically
        return eventService.join(currentUser.currentUserId(), eventId);
    }
}

@Service
class EventService {
    // ...repositories injected by Spring...

    @Transactional                                             // whole method runs in ONE database transaction
    public EventResponse join(Long userId, Long eventId) {
        Event event = events.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        if (participations.existsByEventIdAndUserId(eventId, userId)) {
            throw new BusinessRuleException("ALREADY_JOINED", "You already take part in this event");
        }
        event.addParticipant(clock.instant());                 // business rule lives in the entity
        participations.save(new EventParticipation(event, findUser(userId), clock.instant()));
        return toResponse(event, true);                        // compile-time checked DTO
    }
}

@Entity
class Event {
    @Version private long version;                             // optimistic locking: two users cannot take
                                                               // the last place at the same time
    public void addParticipant(Instant now) {
        if (freePlaces() <= 0) throw new BusinessRuleException("EVENT_FULL", "This event is full");
        participantCount++;
    }
}
// Errors are converted to JSON once, in GlobalExceptionHandler (@RestControllerAdvice).
