package com.example.bookstep.service;

import com.example.bookstep.dto.ReadingRecordForm;
import com.example.bookstep.entity.Book;
import com.example.bookstep.entity.ReadingRecord;
import com.example.bookstep.repository.ReadingRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadingRecordServiceTest {

    @Mock ReadingRecordRepository repository;
    @Mock BookService bookService;
    @InjectMocks ReadingRecordService service;

    @Test
    void 読書記録を登録できる() {
        Book book = new Book();
        ReadingRecordForm form = new ReadingRecordForm();
        form.setBookId(1L);
        form.setReadingDate(LocalDate.of(2026, 7, 27));
        form.setReadingMinutes(45);
        form.setReflection("  学びがあった  ");
        when(bookService.findById(1L)).thenReturn(book);
        when(repository.save(any(ReadingRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReadingRecord saved = service.create(form);

        assertThat(saved.getBook()).isSameAs(book);
        assertThat(saved.getReadingMinutes()).isEqualTo(45);
        assertThat(saved.getReflection()).isEqualTo("学びがあった");
    }
}
