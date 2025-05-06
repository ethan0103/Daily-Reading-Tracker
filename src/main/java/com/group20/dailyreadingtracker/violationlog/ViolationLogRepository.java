package com.group20.dailyreadingtracker.violationlog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.QueryHint;
import jakarta.transaction.Transactional;

/**
 * Repository interface for ViolationLog entity operations.
 * 
 * Provides data access methods for violation logs with:
 * - Custom sorted queries
 * - User-specific violation log retrieval
 * - Cache control configuration
 * 
 * Usage Examples:
 * - Admin dashboards showing recent violations
 * - User-specific violation history views
 * - Compliance reporting
 * 
 * @author Yongtai Li
 * @author Sofiia Mamonova
 */

 @Repository
 public interface ViolationLogRepository extends JpaRepository<ViolationLog, Long> {
   @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "false"))
   @Query("SELECT v FROM ViolationLog v ORDER BY v.date DESC")
   List<ViolationLog> findAllOrderByDateDesc();

   @Modifying
   @Transactional
   @Query("UPDATE ViolationLog v SET v.username = :newUsername WHERE v.user.id = :userId")
   void updateUsername(@Param("userId") Long userId, @Param("newUsername") String newUsername);

   List<ViolationLog> findByUserId(Long userId);
 }
