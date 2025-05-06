package com.group20.dailyreadingtracker.violationlog;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for creating and updating violation log entries.
 * 
 * Usage:
 * - Used as input for violation log creation/update operations
 * - Ensures data integrity before persistence
 * - Simplifies data transfer between layers
 * 
 * @author Yongtai Li
 */

@Data
public class ViolationLogDto {
    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotNull
    private LocalDate date;

    @Min(1)
    private int timeSpent;

    @Min(1)
    private Integer currentPage;

    @Min(1)
    private Integer totalPages;

    @Size(max = 65535, message = "Notes are too long")
    private String notes;

    public ViolationLogDto(String springBoot, String johnDoe, Object o, int i, String test) {
    }

    public ViolationLogDto() {
    }

}