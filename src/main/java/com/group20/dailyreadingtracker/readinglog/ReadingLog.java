package com.group20.dailyreadingtracker.readinglog;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.violationlog.ViolationLog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a user's reading log entry.
 * 
 * Stores:
 * - Book information (title, author)
 * - Reading session details (date, time spent, pages)
 * - User notes and progress
 * - Version history through previousVersion reference
 * 
 * Relationships:
 * - Many-to-one with User (owner)
 * - Self-referential for version tracking
 * 
 * Features:
 * - Conversion from ViolationLog
 * 
 * Persistence:
 * - Table: reading_logs
 * - Audited for creation time
 * 
 * @author Jianghui Sun
 * @author Yongtai Li
 * @author Qiyu Cao
 */

@Entity
@Table(name = "reading_logs")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    private String title;
    private String author;
    private LocalDate date;
    private int timeSpent;

    private Integer currentPage;
    private Integer totalPages;

    @Column(columnDefinition = "TEXT")
    private String notes; 

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_version_id")
    @JsonIgnore
    private ReadingLog previousVersion;

    private boolean isCurrent = true;


    public ReadingLog(ViolationLog log){
        this.user = log.getUser();
        this.title = log.getTitle();
        this.author = log.getAuthor();
        this.date = log.getDate();
        this.timeSpent = log.getTimeSpent();
        this.currentPage = log.getCurrentPage();
        this.totalPages = log.getTotalPages();
        this.notes = log.getNotes();
        this.createdAt = log.getCreatedAt();
        this.isCurrent = true;
    }
}
