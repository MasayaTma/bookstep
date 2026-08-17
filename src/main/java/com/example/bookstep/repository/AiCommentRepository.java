package com.example.bookstep.repository;

import com.example.bookstep.entity.AiComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiCommentRepository extends JpaRepository<AiComment, Long> {
    Optional<AiComment> findByReadingRecordId(Long readingRecordId);
}
