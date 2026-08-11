package com.example.bookstep.service;

import com.example.bookstep.entity.BookStatus;
import com.example.bookstep.entity.ReadingRecord;
import com.example.bookstep.repository.BookRepository;
import com.example.bookstep.repository.ReadingRecordRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PointServiceTest {
    @Test
    void 保存済みデータからポイントを再計算する() {
        BookRepository books = mock(BookRepository.class);
        ReadingRecordRepository records = mock(ReadingRecordRepository.class);
        ReadingRecord shortRecord = record(20, "短い感想");
        ReadingRecord longRecord = record(45, "あ".repeat(50));
        when(books.countByStatus(BookStatus.COMPLETED)).thenReturn(2L);
        when(records.findAll()).thenReturn(List.of(shortRecord, longRecord));

        assertThat(new PointService(books, records).calculateTotalPoints()).isEqualTo(90);
    }

    private ReadingRecord record(int minutes, String reflection) {
        ReadingRecord record = new ReadingRecord();
        record.setReadingMinutes(minutes);
        record.setReflection(reflection);
        return record;
    }
}
