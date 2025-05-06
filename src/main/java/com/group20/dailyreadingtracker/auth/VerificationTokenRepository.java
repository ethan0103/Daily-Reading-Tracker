package com.group20.dailyreadingtracker.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group20.dailyreadingtracker.user.User;

/**
 * JPA repository for managing verification tokens used in email confirmation flows.
 * 
 * Provides operations for:
 * - Token lookup by token value
 * - Token cleanup operations
 * 
 * Primary Use Cases:
 * - Email verification during user registration
 * - Account activation workflows
 * - Token lifecycle management
 * 
 * @author Sofiia Mamonova
 */

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{
    Optional<VerificationToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.user = :user")
    void deleteAllByUser(@Param("user") User user);
}
