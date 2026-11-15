package com.campusbridge.matching;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

    /** Requests in the given states in which the user is sender or receiver. */
    @Query("select r from MatchRequest r where (r.sender.id = :userId or r.receiver.id = :userId) and r.status in :statuses")
    List<MatchRequest> findByUserAndStatusIn(@Param("userId") Long userId,
                                             @Param("statuses") Collection<MatchRequestStatus> statuses);

    List<MatchRequest> findByReceiverIdAndStatus(Long receiverId, MatchRequestStatus status);

    /** FR-08: a declined sender cannot ask the same person again for 30 days. */
    boolean existsBySenderIdAndReceiverIdAndStatusAndRespondedAtAfter(Long senderId, Long receiverId,
                                                                      MatchRequestStatus status, Instant since);

    /** Ids of users who declined the requester after the given instant (excluded from suggestions). */
    @Query("select r.receiver.id from MatchRequest r where r.sender.id = :userId and r.status = :status and r.respondedAt > :since")
    List<Long> findReceiversWhoDeclinedSince(@Param("userId") Long userId,
                                             @Param("status") MatchRequestStatus status,
                                             @Param("since") Instant since);
}
