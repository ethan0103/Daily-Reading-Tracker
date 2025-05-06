package com.group20.dailyreadingtracker.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for User entities providing:
 * - Standard CRUD operations
 * - Custom queries by email/username
 * - Existence checks for registration validation
 * 
 * @author Sofiia Mamonova
 */

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
    
    Optional<User> findById(Long id);

    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
