package com.example.bookstep.service;

import com.example.bookstep.entity.Book;
import com.example.bookstep.entity.ReadingRecord;
import com.example.bookstep.repository.ReadingRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {
    private final BookService bookService;
    private final ReadingRecordRepository readingRecordRepository;
    private final PointService pointService;

    public DashboardService(BookService bookService, ReadingRecordRepository readingRecordRepository,
                            PointService pointService) {
        this.bookService = bookService;
        this.readingRecordRepository = readingRecordRepository;
        this.pointService = pointService;
    }

    public DashboardSummary getSummary() {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        return new DashboardSummary(bookService.countAll(), bookService.countCompleted(),
                readingRecordRepository.sumAllReadingMinutes(),
                readingRecordRepository.sumReadingMinutesBetween(monthStart, monthStart.plusMonths(1)),
                readingRecordRepository.countDistinctReadingDates(), pointService.calculateTotalPoints(),
                bookService.findRecentBooks(), readingRecordRepository.findTop5ByOrderByCreatedAtDesc());
    }

    public record DashboardSummary(long bookCount, long completedBookCount, long totalMinutes,
                                   long monthlyMinutes, long readingDays, long points,
                                   List<Book> recentBooks, List<ReadingRecord> recentRecords) {}
}
