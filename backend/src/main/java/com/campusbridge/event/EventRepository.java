package com.campusbridge.event;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * FR-10: upcoming, not cancelled events in a date range whose title contains the keyword.
     * All parameters are non-null (an empty keyword matches every title); this avoids the
     * "could not determine data type of parameter" problem of PostgreSQL with null parameters.
     * Category and language filters are applied in {@link EventService}.
     */
    @Query("""
            select e from Event e
            where e.status = com.campusbridge.event.EventStatus.SCHEDULED
              and e.startsAt > :now
              and e.startsAt between :fromDate and :toDate
              and lower(e.title) like lower(concat('%', :keyword, '%'))
            order by e.startsAt asc
            """)
    List<Event> findUpcoming(@Param("now") Instant now,
                             @Param("fromDate") Instant fromDate,
                             @Param("toDate") Instant toDate,
                             @Param("keyword") String keyword);
}
