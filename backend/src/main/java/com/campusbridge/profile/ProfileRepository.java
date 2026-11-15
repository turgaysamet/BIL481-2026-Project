package com.campusbridge.profile;

import com.campusbridge.auth.AccountStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);

    /** Candidate pool for matching: complete profiles of active accounts, except the requester. */
    @Query("select p from Profile p where p.completed = true and p.user.status = :status and p.user.id <> :userId")
    List<Profile> findMatchCandidates(@Param("userId") Long userId, @Param("status") AccountStatus status);
}
