package com.group20.dailyreadingtracker.searchandfilter;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group20.dailyreadingtracker.readinglog.ReadingLog;
import com.group20.dailyreadingtracker.readinglog.ReadingLogRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service layer for reading log search and filtering operations.
 * 
 * This service provides:
 * - Business logic for search and filter operations
 * - Coordination between controllers and repository
 * - Parameter validation and query construction
 * 
 * Key responsibilities:
 * - Executing search queries with proper user scope
 * - Applying date range filters to reading logs
 * - Filtering by time spent reading
 * - Handling combined filters with null-safe optional parameters
 * 
 * @author Yiting Jiang
 */

@Service
@RequiredArgsConstructor
public class SearchAndFilterService {

    private final ReadingLogRepository readingLogRepository;

    public List<ReadingLog> searchLogs(Long userId, String query) {
        return readingLogRepository.searchByMultiFields(userId, query);
    }

    public List<ReadingLog> filterByDateRange(Long userId, LocalDate starDate, LocalDate endDate){
        return readingLogRepository.findByDateRange(userId, starDate, endDate);
    }

    public List<ReadingLog> filterByTimeRange(Long userId, int minTime, int maxTime) {
        return readingLogRepository.findByTimeSpentRange(userId, minTime, maxTime);
    }

    public List<ReadingLog> filterLogs(Long userId, LocalDate startDate, 
                                       LocalDate endDate, Integer minTime, Integer maxTime) {
        return readingLogRepository.findByFilters(userId, startDate, endDate, minTime, maxTime);
    }
}
