package com.campusbridge.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Data access layer for {@link User}. Queries are derived by Spring Data from method names. */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByVerificationToken(String verificationToken);

    boolean existsByEmailIgnoreCase(String email);
}
