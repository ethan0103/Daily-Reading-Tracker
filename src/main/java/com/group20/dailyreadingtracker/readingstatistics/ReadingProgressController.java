package com.group20.dailyreadingtracker.readingstatistics;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * MVC Controller for reading progress visualization.
 * 
 * Handles:
 * - Progress page rendering with reading statistics
 * - Book progress data API endpoint
 * 
 * Key features:
 * - User-specific data isolation
 * - Error handling with fallback statistics
 * - Dual endpoint support (HTML + JSON)
 * - Principal-based authentication
 * 
 * @author Qiyu Cao
 */

@Controller
@RequiredArgsConstructor
public class ReadingProgressController {
    private final ReadingStatisticsService readingStatisticsService;
    private final UserRepository userRepository;

    @GetMapping("/progress")
    public String progressPage(Model model, Principal principal) {

        try {
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Map<String, Object> stats = readingStatisticsService.getReadingStatistics(user.getId());

            model.addAttribute("stats", stats);
        } catch (Exception e) {
            System.err.println("Failed to get statistics: " + e.getMessage());
            model.addAttribute("error", "Failed to load statistics");
            model.addAttribute("stats", Map.of(
                    "bookCount", 0,
                    "totalReadingTime", 0,
                    "avgDailyTime", 0
            ));
        }

        return "readinglog/progress";
    }
    
    @GetMapping("/book-progress")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBookProgress(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("The user does not exist."));

        Map<String, Object> result = readingStatisticsService.getBookProgressStats(user.getId());

        return ResponseEntity.ok(result);
    }
}