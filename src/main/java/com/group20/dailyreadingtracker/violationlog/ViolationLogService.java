package com.group20.dailyreadingtracker.violationlog;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.group20.dailyreadingtracker.readinglog.ReadingLog;
import com.group20.dailyreadingtracker.readinglog.ReadingLogRepository;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Service layer for managing violation logs and related operations.
 * 
 * Handles:
 * - Creation and management of violation logs
 * - Restoration of violation logs to reading logs
 * - Violation log retrieval and updates
 * 
 * @author Yongtai Li
 */

@Service 
@RequiredArgsConstructor
public class ViolationLogService {

    private final ReadingLogRepository readingLogRepository;
    private final ViolationLogRepository violationLogRepository;
    private final UserRepository userRepository;

    public ViolationLog createLog(Long userId, ViolationLogDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ViolationLog log = new ViolationLog();
        log.setUser(user);
        log.setTitle(dto.getTitle());
        log.setAuthor(dto.getAuthor());
        log.setNotes(dto.getNotes());

        return violationLogRepository.save(log);
    }

    @Transactional
    public void restoreViolationLog(long userId, Long logId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String email = authentication.getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                "User not found with email: " + email));

        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
                System.out.println("Your role: " + admin.getRoles());
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can restore any violation logs!");
        }

        ViolationLog violog = violationLogRepository.findById(logId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading log not found"));

        User flaggedUser = violog.getUser();

        ReadingLog newLog = new ReadingLog();
        newLog.setTitle(violog.getTitle());     // Original Id cannot be used and can not be set (setter method) since
                                                // it's @GeneratedValue Notation and Database operation will fail after
                                                // using setter method to do it. It may lead to database primary key
                                                // conflict or collide with later generated value (dangerous),
                                                // thus give up id-reuse.
        newLog.setAuthor(violog.getAuthor());
        newLog.setDate(violog.getDate());
        newLog.setTimeSpent(violog.getTimeSpent());
        newLog.setNotes(violog.getNotes());
        newLog.setUser(violog.getUser());
        newLog.setCurrentPage(violog.getCurrentPage());
        newLog.setTotalPages(violog.getTotalPages());
        newLog.setCreatedAt(violog.getCreatedAt());

        flaggedUser.removeFlaggedTimes();

        readingLogRepository.save(newLog);
        violationLogRepository.delete(violog);
    }

    private boolean isAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }

    @Transactional
    public ViolationLog updateLog(Long userId, Long logId, ViolationLogDto dto) {
        ViolationLog log = violationLogRepository.findById(logId)
        .orElseThrow(() -> new IllegalArgumentException("Violation log not found"));
    
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new SecurityException("User not found"));
        
        boolean isAdmin = user.getRoles().stream()
            .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
        
        if (!isAdmin && log.getUser().getId() == userId) {
            throw new SecurityException("Unauthorized to edit this log");
        }

        List<ReadingLog> otherLogs = readingLogRepository
        .findByUserIdAndTitleIgnoreCaseAndAuthorIgnoreCaseAndIdNot(
            userId, dto.getTitle().trim(), dto.getAuthor().trim(), logId);

        if (!otherLogs.isEmpty() && dto.getTotalPages() != null) {
            Integer existingTotalPages = otherLogs.stream()
                .filter(l -> l.getTotalPages() != null)
                .findFirst()
                .map(ReadingLog::getTotalPages)
                .orElse(null);
            
            if (existingTotalPages != null && !existingTotalPages.equals(dto.getTotalPages())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "PAGE_COUNT_MISMATCH:" + existingTotalPages + ":" + dto.getTotalPages());
            }
        }

        log.setTitle(dto.getTitle());
        log.setAuthor(dto.getAuthor());
        log.setDate(dto.getDate());
        log.setTimeSpent(dto.getTimeSpent());
        log.setNotes(dto.getNotes());

        return violationLogRepository.save(log); 
    }

    public ViolationLog getLogById(Long userId, Long logId) {
        ViolationLog log = violationLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Violation log not found"));
                
        if (!isAdmin(userId)) {       
            throw new SecurityException("Access denied");
        }

        return log;
    }
    
}
