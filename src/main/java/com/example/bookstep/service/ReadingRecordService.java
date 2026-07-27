package com.example.bookstep.service;

import com.example.bookstep.dto.ReadingRecordForm;
import com.example.bookstep.entity.Book;
import com.example.bookstep.entity.ReadingRecord;
import com.example.bookstep.exception.ReadingRecordNotFoundException;
import com.example.bookstep.repository.ReadingRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReadingRecordService {

    private final ReadingRecordRepository readingRecordRepository;
    private final BookService bookService;

    public ReadingRecordService(ReadingRecordRepository readingRecordRepository, BookService bookService) {
        this.readingRecordRepository = readingRecordRepository;
        this.bookService = bookService;
    }

    public List<ReadingRecord> findAll() {
        return readingRecordRepository.findAllByOrderByReadingDateDescCreatedAtDesc();
    }

    public List<ReadingRecord> findByBookId(Long bookId) {
        bookService.findById(bookId);
        return readingRecordRepository.findByBookIdOrderByReadingDateDescCreatedAtDesc(bookId);
    }

    public ReadingRecord findById(Long id) {
        return readingRecordRepository.findById(id)
                .orElseThrow(() -> new ReadingRecordNotFoundException(id));
    }

    public long getTotalMinutesForBook(Long bookId) {
        return readingRecordRepository.sumReadingMinutesByBookId(bookId);
    }

    @Transactional
    public ReadingRecord create(ReadingRecordForm form) {
        ReadingRecord record = new ReadingRecord();
        applyForm(record, form);
        return readingRecordRepository.save(record);
    }

    @Transactional
    public ReadingRecord update(Long id, ReadingRecordForm form) {
        ReadingRecord record = findById(id);
        applyForm(record, form);
        return record;
    }

    @Transactional
    public Long delete(Long id) {
        ReadingRecord record = findById(id);
        Long bookId = record.getBook().getId();
        readingRecordRepository.delete(record);
        return bookId;
    }

    private void applyForm(ReadingRecord record, ReadingRecordForm form) {
        Book book = bookService.findById(form.getBookId());
        record.setBook(book);
        record.setReadingDate(form.getReadingDate());
        record.setReadingMinutes(form.getReadingMinutes());
        record.setStartPage(form.getStartPage());
        record.setEndPage(form.getEndPage());
        record.setReflection(normalize(form.getReflection()));
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
