package com.example.bookstep.service;

import com.example.bookstep.dto.BookForm;
import com.example.bookstep.entity.Book;
import com.example.bookstep.entity.BookStatus;
import com.example.bookstep.exception.BookNotFoundException;
import com.example.bookstep.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void 書籍を登録できる() {
        BookForm form = new BookForm();
        form.setTitle("  テスト駆動開発  ");
        form.setAuthor("  Kent Beck  ");
        form.setStatus(BookStatus.READING);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book saved = bookService.create(form);

        assertThat(saved.getTitle()).isEqualTo("テスト駆動開発");
        assertThat(saved.getAuthor()).isEqualTo("Kent Beck");
        assertThat(saved.getStatus()).isEqualTo(BookStatus.READING);
    }

    @Test
    void 存在しない書籍は例外になる() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(999L))
                .isInstanceOf(BookNotFoundException.class);
    }
}
