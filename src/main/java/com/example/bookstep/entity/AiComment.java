package com.example.bookstep.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_comments")
public class AiComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reading_record_id", nullable = false, unique = true)
    private ReadingRecord readingRecord;

    @Lob
    @Column(nullable = false)
    private String comment;

    @Column(length = 30)
    private String provider;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public ReadingRecord getReadingRecord() { return readingRecord; }
    public void setReadingRecord(ReadingRecord readingRecord) { this.readingRecord = readingRecord; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
