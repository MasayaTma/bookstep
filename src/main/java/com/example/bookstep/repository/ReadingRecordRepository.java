package com.example.bookstep.repository;

import com.example.bookstep.entity.ReadingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    @EntityGraph(attributePaths = "book")
    List<ReadingRecord> findAllByOrderByReadingDateDescCreatedAtDesc();

    @EntityGraph(attributePaths = "book")
    List<ReadingRecord> findByBookIdOrderByReadingDateDescCreatedAtDesc(Long bookId);

    @Query("select coalesce(sum(r.readingMinutes), 0) from ReadingRecord r where r.book.id = :bookId")
    long sumReadingMinutesByBookId(@Param("bookId") Long bookId);
}
