package com.example.bookstep.service;

import com.example.bookstep.entity.BookStatus;
import com.example.bookstep.repository.BookRepository;
import com.example.bookstep.repository.ReadingRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PointService {
    private final BookRepository bookRepository;
    private final ReadingRecordRepository readingRecordRepository;

    public PointService(BookRepository bookRepository, ReadingRecordRepository readingRecordRepository) {
        this.bookRepository = bookRepository;
        this.readingRecordRepository = readingRecordRepository;
    }

    public long calculateTotalPoints() {
        long points = bookRepository.countByStatus(BookStatus.COMPLETED) * 30;
        return points + readingRecordRepository.findAll().stream()
                .mapToLong(record -> 10
                        + (record.getReadingMinutes() >= 30 ? 5 : 0)
                        + (record.getReflection() != null && record.getReflection().length() >= 50 ? 5 : 0))
                .sum();
    }
}
