package com.example.bookstep.service;

import com.example.bookstep.dto.BookForm;
import com.example.bookstep.entity.Book;
import com.example.bookstep.entity.BookStatus;
import com.example.bookstep.exception.BookNotFoundException;
import com.example.bookstep.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Book> findRecentBooks() {
        return bookRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public long countAll() {
        return bookRepository.count();
    }

    public long countCompleted() {
        return bookRepository.countByStatus(BookStatus.COMPLETED);
    }

    @Transactional
    public Book create(BookForm form) {
        Book book = new Book();
        applyForm(book, form);
        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long id, BookForm form) {
        Book book = findById(id);
        applyForm(book, form);
        return book;
    }

    @Transactional
    public void delete(Long id) {
        Book book = findById(id);
        bookRepository.delete(book);
    }

    private void applyForm(Book book, BookForm form) {
        book.setTitle(form.getTitle().trim());
        book.setAuthor(normalize(form.getAuthor()));
        book.setCategory(normalize(form.getCategory()));
        book.setStatus(form.getStatus());
        book.setTotalPages(form.getTotalPages());
        book.setStartedDate(form.getStartedDate());
        book.setMemo(normalize(form.getMemo()));

        if (form.getStatus() == BookStatus.COMPLETED && book.getCompletedDate() == null) {
            book.setCompletedDate(LocalDate.now());
        } else if (form.getStatus() != BookStatus.COMPLETED) {
            book.setCompletedDate(null);
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
