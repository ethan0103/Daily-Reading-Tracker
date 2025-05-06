package com.group20.dailyreadingtracker.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group20.dailyreadingtracker.user.User;

import jakarta.transaction.Transactional;

/**
 * JPA repository for managing PasswordResetToken entities and operations.
 * 
 * Provides both standard CRUD operations and custom queries for:
 * - Token-based authentication flows
 * - Password reset functionality
 * - Token lifecycle management
 * 
 * Key Query Capabilities:
 * - Token lookup by exact value
 * - Token retrieval by associated user
 * - Token search by user email
 * - Secure token invalidation
 * 
 * @author Sofiia Mamonova
 */

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{
    
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);

    Optional<PasswordResetToken> findByUserEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.token = :token")
    void deleteByToken(@Param("token") String token);
}
