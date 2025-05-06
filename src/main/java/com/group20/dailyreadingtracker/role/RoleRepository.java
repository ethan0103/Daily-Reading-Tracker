package com.group20.dailyreadingtracker.role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for Role entities providing:
 * - Standard CRUD operations
 * - Custom query to find roles by name
 * 
 * @author Sofiia Mamonova
 */

public interface RoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByName(String name);
}
